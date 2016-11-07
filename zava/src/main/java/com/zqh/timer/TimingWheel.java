package com.zqh.timer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A timing-wheel optimized for approximated I/O timeout scheduling.<br>
 * {@link TimingWheel} creates a new thread whenever it is instantiated and started, so don't create many instances.
 * <p>
 * <b>The classic usage as follows:</b><br>
 * <li>using timing-wheel manage any object timeout</li>
 * <pre>
 *    // Create a timing-wheel with 60 ticks, and every tick is 1 second.
 *    private static final TimingWheel<CometChannel> TIMING_WHEEL = new TimingWheel<CometChannel>(1, 60, TimeUnit.SECONDS);
 *
 *    // Add expiration listener and start the timing-wheel.
 *    static {
 *      TIMING_WHEEL.addExpirationListener(new YourExpirationListener());
 *      TIMING_WHEEL.start();
 *    }
 *
 *    // Add one element to be timeout approximated after 60 seconds
 *    TIMING_WHEEL.add(e);
 *
 *    // Anytime you can cancel count down timer for element e like this
 *    TIMING_WHEEL.remove(e);
 * </pre>
 *
 * After expiration occurs, the {@link ExpirationListener} interface will be invoked and the expired object will be
 * the argument for callback method {@link ExpirationListener#expired(Object)}
 * <p>
 * {@link TimingWheel} is based on <a href="http://cseweb.ucsd.edu/users/varghese/">George Varghese</a> and Tony Lauck's paper,
 * <a href="http://cseweb.ucsd.edu/users/varghese/PAPERS/twheel.ps.Z">'Hashed and Hierarchical Timing Wheels: data structures
 * to efficiently implement a timer facility'</a>.  More comprehensive slides are located <a href="http://www.cse.wustl.edu/~cdgill/courses/cs6874/TimingWheels.ppt">here</a>.
 *
 * @author mindwind
 * @version 1.0, Sep 20, 2012
 *
 * @ref: http://blog.csdn.net/mindfloating/article/details/8033340
 *
定时轮是一种数据结构，其主体是一个循环列表（circular buffer），每个列表中包含一个称之为槽（slot）的结构

定时轮的工作原理可以类比于时钟，如上图箭头（指针）按某一个方向按固定频率轮动，每一次跳动称为一个 tick。
这样可以看出定时轮由个3个重要的属性参数，ticksPerWheel（一轮的tick数），tickDuration（一个tick的持续时间）
以及 timeUnit（时间单位），例如 当ticksPerWheel=60，tickDuration=1，timeUnit=秒，这就和现实中的时钟的秒针走动完全类似了。

这里给出一种简单的实现方式，指针按 tickDuration 的设置进行固定频率的转动，其中的必要约定如下：

1. 新加入的对象总是保存在当前指针转动方向上一个位置
2. 相等的对象仅存在于一个 slot 中
3. 指针转动到当前位置对应的 slot 中保存的对象就意味着 timeout 了


 */
public class TimingWheel<E> {

    private final long tickDuration;
    private final int ticksPerWheel;
    private volatile int currentTickIndex = 0;

    private final CopyOnWriteArrayList<ExpirationListener<E>> expirationListeners = new CopyOnWriteArrayList<ExpirationListener<E>>();
    private final ArrayList<Slot<E>> wheel;
    private final Map<E, Slot<E>> indicator = new ConcurrentHashMap<E, Slot<E>>();

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Thread workerThread;

    // ~ -------------------------------------------------------------------------------------------------------------

    /**
     * Construct a timing wheel.
     *
     * @param tickDuration
     *            tick duration with specified time unit.
     * @param ticksPerWheel
     * @param timeUnit
     */
    public TimingWheel(int tickDuration, int ticksPerWheel, TimeUnit timeUnit) {
        if (timeUnit == null) {
            throw new NullPointerException("unit");
        }
        if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }

        this.wheel = new ArrayList<Slot<E>>();
        this.tickDuration = TimeUnit.MILLISECONDS.convert(tickDuration, timeUnit);
        this.ticksPerWheel = ticksPerWheel + 1;

        for (int i = 0; i < this.ticksPerWheel; i++) {
            wheel.add(new Slot<E>(i));
        }
        wheel.trimToSize();

        workerThread = new Thread(new TickWorker(), "Timing-Wheel");
    }

    // ~ -------------------------------------------------------------------------------------------------------------

    public void start() {
        if (shutdown.get()) {
            throw new IllegalStateException("Cannot be started once stopped");
        }

        if (!workerThread.isAlive()) {
            workerThread.start();
        }
    }

    public boolean stop() {
        if (!shutdown.compareAndSet(false, true)) {
            return false;
        }

        boolean interrupted = false;
        while (workerThread.isAlive()) {
            workerThread.interrupt();
            try {
                workerThread.join(100);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return true;
    }

    public void addExpirationListener(ExpirationListener<E> listener) {
        expirationListeners.add(listener);
    }

    public void removeExpirationListener(ExpirationListener<E> listener) {
        expirationListeners.remove(listener);
    }

    /**
     * Add a element to {@link TimingWheel} and start to count down its life-time.
     *
     * @param e
     * @return remain time to be expired in millisecond.
     */
    public long add(E e) {
        synchronized(e) {
            checkAdd(e);

            int previousTickIndex = getPreviousTickIndex();
            Slot<E> slot = wheel.get(previousTickIndex);
            slot.add(e);
            indicator.put(e, slot);

            return (ticksPerWheel - 1) * tickDuration;
        }
    }

    private void checkAdd(E e) {
        Slot<E> slot = indicator.get(e);
        if (slot != null) {
            slot.remove(e);
        }
    }

    private int getPreviousTickIndex() {
        lock.readLock().lock();
        try {
            int cti = currentTickIndex;
            if (cti == 0) {
                return ticksPerWheel - 1;
            }

            return cti - 1;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Removes the specified element from timing wheel.
     *
     * @param e
     * @return <tt>true</tt> if this timing wheel contained the specified
     *         element
     */
    public boolean remove(E e) {
        synchronized (e) {
            Slot<E> slot = indicator.get(e);
            if (slot == null) {
                return false;
            }

            indicator.remove(e);
            return slot.remove(e) != null;
        }
    }

    private void notifyExpired(int idx) {
        Slot<E> slot = wheel.get(idx);
        Set<E> elements = slot.elements();
        for (E e : elements) {
            slot.remove(e);
            synchronized (e) {
                Slot<E> latestSlot = indicator.get(e);
                if (latestSlot.equals(slot)) {
                    indicator.remove(e);
                }
            }
            for (ExpirationListener<E> listener : expirationListeners) {
                listener.expired(e);
            }
        }
    }

    // ~ -------------------------------------------------------------------------------------------------------------

    private class TickWorker implements Runnable {

        private long startTime;
        private long tick;

        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            tick = 1;

            for (int i = 0; !shutdown.get(); i++) {
                if (i == wheel.size()) {
                    i = 0;
                }
                lock.writeLock().lock();
                try {
                    currentTickIndex = i;
                } finally {
                    lock.writeLock().unlock();
                }
                notifyExpired(currentTickIndex);
                waitForNextTick();
            }
        }

        private void waitForNextTick() {
            for (;;) {
                long currentTime = System.currentTimeMillis();
                long sleepTime = tickDuration * tick - (currentTime - startTime);

                if (sleepTime <= 0) {
                    break;
                }

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    return;
                }
            }

            tick++;
        }
    }

    private static class Slot<E> {

        private int id;
        private Map<E, E> elements = new ConcurrentHashMap<E, E>();

        public Slot(int id) {
            this.id = id;
        }

        public void add(E e) {
            elements.put(e, e);
        }

        public E remove(E e) {
            return elements.remove(e);
        }

        public Set<E> elements() {
            return elements.keySet();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            @SuppressWarnings("rawtypes")
            Slot other = (Slot) obj;
            if (id != other.id)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Slot [id=" + id + ", elements=" + elements + "]";
        }

    }

}