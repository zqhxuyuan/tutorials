package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 */
public class Reentrant2{

    //不可重入锁
    Lock lock = new Lock();
    //可重入锁
    LockReentrant reentrantLock = new LockReentrant();

    public void outer() throws Exception{
        lock.lock();
        inner();
        lock.unlock();
    }

    //调用outer()的线程首先会锁住Lock实例，然后继续调用inner()。
    //inner()方法中该线程将再一次尝试锁住Lock实例，结果该动作会失败（也就是说该线程会被阻塞），
    //因为这个Lock实例已经在outer()方法中被锁住了
    public synchronized void inner() throws Exception{
        lock.lock();
        //do something
        lock.unlock();
    }


    public void outerLock() throws Exception{
        reentrantLock.lock();
        inner();
        reentrantLock.unlock();
    }

    public synchronized void innerReentrant() throws Exception{
        reentrantLock.lock();
        //do something
        reentrantLock.unlock();
    }

}