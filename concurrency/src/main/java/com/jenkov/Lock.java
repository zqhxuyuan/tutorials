package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 */
public class Lock{

    private boolean isLocked      = false;
    private Thread lockingThread = null;

    /**
     * 如果存在多线程并发访问lock()，这些线程将阻塞在对lock()方法的访问上
     *
     * 当isLocked为true时，调用lock()的线程在wait()调用上阻塞等待。
     * 为防止该线程没有收到notify()调用也从wait()中返回（也称作虚假唤醒），
     * 这个线程会重新去检查isLocked条件以决定当前是否可以安全地继续执行还是需要重新保持等待，
     * 而不是认为线程被唤醒了就可以安全地继续执行了。
     *
     * 如果isLocked为false，当前线程会退出while(isLocked)循环，并将isLocked设回true，
     * 让其它正在调用lock()方法的线程能够在Lock实例上加锁
     *
     * 一个线程是否被允许退出lock()方法是由while循环（自旋锁）中的条件决定的。
     * 当前的判断条件是只有当isLocked为false时lock操作才被允许，而没有考虑是哪个线程锁住了它
     * 可重入的锁: LockReentrant
     * @throws InterruptedException
     */
    public synchronized void lock() throws InterruptedException{
        //如果锁已经锁上（isLocked=true），这些线程将阻塞在while(isLocked)循环的wait()调用里面
        while(isLocked){  //while(isLocked)循环，又被叫做“自旋锁”
            //当线程正在等待进入lock()时，可以调用wait()释放其锁实例对应的同步锁，
            //使得其他多个线程可以进入lock()方法，并调用wait()方法
            wait();
        }
        isLocked = true;
        lockingThread = Thread.currentThread();
    }

    //同步块不会对等待进入的多个线程谁能获得访问做任何保障，同样当调用notify()时，wait()也不会做保障一定能唤醒线程
    //因此这个版本的Lock类和doSynchronized()那个版本就保障公平性而言，没有任何区别
    public synchronized void unlock(){
        if(this.lockingThread != Thread.currentThread()){
            throw new IllegalMonitorStateException("Calling thread has not locked this lock");
        }
        //当线程完成了临界区（位于lock()和unlock()之间）中的代码，就会调用unlock()。
        //执行unlock()会重新将isLocked设置为false，
        //并且通知（唤醒）其中一个（若有的话）在lock()方法中调用了wait()函数而处于等待状态的线程
        isLocked = false;
        lockingThread = null;
        notify();
    }
}
