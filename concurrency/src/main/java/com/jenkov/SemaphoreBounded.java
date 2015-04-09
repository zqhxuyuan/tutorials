package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 有上限的Semaphore:
 * 限制信号的数量. 当已经产生的信号数量达到了上限，take方法将阻塞新的信号产生请求，
 * 直到某个线程调用release方法后，被阻塞于take方法的线程才能传递自己的信号
 */
public class SemaphoreBounded {

    private int signals = 0;
    private int bound   = 0;

    public SemaphoreBounded(int upperBound){
        this.bound = upperBound;
    }

    public synchronized void take() throws InterruptedException{
        while(this.signals == bound) {
            wait();
        }
        this.signals++;
        this.notify();
    }

    public synchronized void release() throws InterruptedException{
        while(this.signals == 0) {
            wait();
        }
        this.signals--;
        this.notify();
    }
}
