package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * Hot it comes?
 *
 * 假设你的程序中涉及到对一些共享资源的读和写操作，且写操作没有读操作那么频繁。
 * 在没有写操作的时候，两个线程同时读一个资源没有任何问题，所以应该允许多个线程能在同时读取共享资源。
 * 但是如果有一个线程想去写这些共享资源，就不应该再有其它线程对该资源进行读或写.这就需要一个读/写锁来解决这个问题
 * （也就是说：读-读能共存，读-写不能共存，写-写不能共存）
 *
 * 这个类不是可重入锁:
 * 为了让ReadWriteLock的读锁可重入，我们要先为读锁重入建立规则：
 * 要保证某个线程中的读锁可重入，要么满足获取读锁的条件（没有写或写请求），要么已经持有读锁（不管是否有写请求）
 * 1. 没有写或写请求, 因为可以多次读, 也就是读锁可重入
 * 2. 已经持有读锁了, 可以再次获得读锁.
 */
public class ReadWriteLock {

    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;

    public synchronized void lockRead() throws InterruptedException{
        while(writers > 0 || writeRequests > 0){
            wait();
        }
        //只要没有线程拥有写锁（writers==0），且没有线程在请求写锁（writeRequests ==0），所有想获得读锁的线程都能成功获取
        readers++;
    }

    public synchronized void unlockRead(){
        readers--;
        notifyAll();
    }

    //当一个线程想获得写锁的时候，首先会把写锁请求数加1（writeRequests++），然后再去判断是否能够真能获得写锁，
    //当没有线程持有读锁（readers==0）,且没有线程持有写锁（writers==0）时就能获得写锁
    public synchronized void lockWrite() throws InterruptedException{
        writeRequests++;

        while(readers > 0 || writers > 0){
            wait();
        }
        writeRequests--;
        writers++;
    }

    public synchronized void unlockWrite() throws InterruptedException{
        writers--;
        //如果有多个读线程在等待读锁且没有线程在等待写锁时，调用unlockWrite()后，
        //所有等待读锁的线程都能立马成功获取读锁 —— 而不是一次只允许一个
        notifyAll();
    }
}
