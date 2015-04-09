package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 */
public class CounterLock {

    //用Lock代替synchronized
    private Lock lock = new Lock();
    private int count = 0;

    //lock()方法会对Lock实例对象(lock)进行加锁，
    //因此所有对该lock对象调用lock()方法的线程都会被阻塞，直到该Lock对象的unlock()方法被调用
    public int inc() throws Exception{
        lock.lock();

        int newCount = ++count;

        lock.unlock();

        return newCount;
    }
}
