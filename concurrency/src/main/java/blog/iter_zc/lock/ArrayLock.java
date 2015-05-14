package blog.iter_zc.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 有界队列锁，使用一个volatile数组来组织线程
 * 缺点是得预先知道线程的规模n，所有线程获取同一个锁的次数不能超过n(否则会发生状态覆盖)
 * 假设L把锁，那么锁的空间复杂度为O(Ln)
 */
public class ArrayLock implements Lock{
    // 使用volatile数组来存放锁标志， flags[i] = true表示可以获得锁
    // 原先所有线程对一个共享变量进行自旋,现在用数组,将自旋的变量从一个分散到多个，减少缓存一致性流量
    // 因为原先一个共享变量时,所有线程操作对一个变量自旋. 现在有多个变量,每个线程分散到其中的一个变量进行自旋
    private volatile boolean[] flags;

    // 指向新加入的节点的后一个位置
    private AtomicInteger tail;

    // 总容量
    private final int capacity;

    // ThreadLocal每个线程都有自己的线程本地变量
    private ThreadLocal<Integer> mySlotIndex = new ThreadLocal<Integer>(){
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     *
     * @param capacity 线程的数量, 预先设置.
     */
    public ArrayLock(int capacity){
        this.capacity = capacity;
        flags = new boolean[capacity];
        tail = new AtomicInteger(0);
        // 默认第一个位置可获得锁
        //初始值为true,这样第一个线程调用lock加锁时,!flags[slot]=false,不满足,不进行忙循环,成功获取到锁
        flags[0] = true;
    }

    @Override
    public void lock() {
        //不同的线程获取到的slot的值不一样,flags数组的容量大小为初始的线程数
        //flags数组的每个元素对应了一个线程对这个元素的加锁.
        //当同时加锁的线程数等于数组大小时,如果再有线程进来,则重新回到数组第一个元素
        //由于这个元素已经有线程锁住了:flags[slot]=true,则当前线程进行忙循环.
        int slot = tail.getAndIncrement() % capacity;
        //设置线程局部变量.每个线程都有自己的ThreadLocal对象,表示自己锁住了数组的哪个位置
        //这样在释放的时候,就可以从ThreadLocal中获取到这个位置,设置flags中这个位置的值为false
        mySlotIndex.set(slot);
        // flags[slot] == true 表示获得了锁， volatile变量保证锁释放及时通知
        while(!flags[slot]){

        }
    }

    /**
     * 上面的lock实现中,如果flags[slot]=true才可以成功加锁,否则进行忙循环
     *
     */
    @Override
    public void unlock() {
        int slot = mySlotIndex.get();
        //当前槽位:不可获得锁
        flags[slot] = false;
        //下一个槽位:可获得锁
        flags[(slot + 1) % capacity] = true;
    }

    public String toString(){
        return "ArrayLock";
    }
}