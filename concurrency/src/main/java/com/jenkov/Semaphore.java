package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 简单的Semaphore实现
 * Semaphore（信号量） 是一个线程同步结构，用于在线程间传递信号，
 * 以避免出现信号丢失，或者像锁一样用于保护一个关键区域
 *
 * Semaphore被用来在多个线程之间传递信号，这种情况下，take和release分别被不同的线程调用
 */
public class Semaphore {

    //信号量标志位
    private boolean signal = false;

    public synchronized void take() {
        //take the signal, set it to true
        this.signal = true;
        this.notify();
    }

    public synchronized void release() throws InterruptedException{
        while(!this.signal) {
            wait();
        }

        this.signal = false;
    }
}
