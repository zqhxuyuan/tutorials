package com.baidu.unbiz.common.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Composite Abortable Lock, now with Fast Path!
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午2:06:57
 */
public class CompositeFastPathLock extends CompositeLock {
    private static final int FASTPATH = 1 << 30;
    public int fastPathTaken;

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (fastPathLock()) {
            fastPathTaken++;
            return true;
        }
        if (super.tryLock(time, unit)) {
            fastPathWait();
            return true;
        }
        return false;
    }

    public void unlock() {
        if (!fastPathUnlock()) {
            super.unlock();
        }
        ;
    }

    private boolean fastPathLock() {
        int oldStamp, newStamp;
        int stamp[] = { 0 };
        QNode qnode;
        qnode = tail.get(stamp);
        oldStamp = stamp[0];
        if (qnode != null) {
            return false;
        }
        if ((oldStamp & FASTPATH) != 0) {
            return false;
        }
        newStamp = (oldStamp + 1) | FASTPATH; // set flag
        return tail.compareAndSet(qnode, null, oldStamp, newStamp);
    }

    private boolean fastPathUnlock() {
        int oldStamp, newStamp;
        oldStamp = tail.getStamp();
        if ((oldStamp & FASTPATH) == 0) {
            return false;
        }
        int[] stamp = { 0 };
        QNode qnode;
        do {
            qnode = tail.get(stamp);
            oldStamp = stamp[0];
            newStamp = oldStamp & (~FASTPATH); // unset flag
        } while (!tail.compareAndSet(qnode, qnode, oldStamp, newStamp));
        return true;
    }

    private void fastPathWait() {
        while ((tail.getStamp() & FASTPATH) != 0) {
        } // spin while flag is set
    }

}
