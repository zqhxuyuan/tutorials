package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 使用锁方式替代同步块
 * 为了提高等待线程的公平性，我们使用锁方式来替代同步块
 */
public class Synchronizer{

    Lock lock = new Lock();
    FairLock fairLock = new FairLock();

    //同步方法
    public synchronized void doSynchronized1() throws InterruptedException{
        //critical section, do a lot of work which takes a long time
    }

    //锁
    public void doSynchronizedWithLock() throws InterruptedException{
        this.lock.lock();
        //critical section, do a lot of work which takes a long time
        this.lock.unlock();
    }

    //公平锁
    public void doSynchronizedWithFailLock() throws InterruptedException{
        this.fairLock.lock();
        //critical section, do a lot of work which takes a long time
        this.fairLock.unlock();
    }
}
