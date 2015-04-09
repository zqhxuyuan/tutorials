package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 可计数的Semaphore:
 * 计算通过调用take方法所产生信号的数量
 */
public class SemaphoreCounting {

    private int signals = 0;

    public synchronized void take() {
        this.signals++;
        this.notify();
    }

    public synchronized void release() throws InterruptedException{
        while(this.signals == 0) wait();
        this.signals--;
    }

}
