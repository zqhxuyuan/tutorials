package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * QueueObject实际是一个semaphore。doWait()和doNotify()方法在QueueObject中保存着信号
 */
public class QueueObject {

    //doWait()和doNotify()方法在QueueObject中保存着信号
    private boolean isNotified = false;

    public synchronized void doWait() throws InterruptedException {
        while(!isNotified){
            this.wait();
        }
        this.isNotified = false;
    }

    public synchronized void doNotify() {
        this.isNotified = true;
        this.notify();
    }

    public boolean equals(Object o) {
        return this == o;
    }

}
