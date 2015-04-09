package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 */
public class LockReentrant {

    private boolean isLocked      = false;
    private Thread lockingThread = null;
    int lockedCount = 0;  //记录同一个线程重复对一个锁对象加锁的次数

    public synchronized void lock() throws InterruptedException{
        Thread callingThread = Thread.currentThread();
        while(isLocked && lockingThread != callingThread){
            wait();
        }

        isLocked = true;
        lockedCount++;
        lockingThread = Thread.currentThread();
    }

    public synchronized void unlock(){
        if(Thread.currentThread() == this.lockingThread){
            lockedCount--;

            //在unlock()调用没有达到对应lock()调用的次数之前，我们不希望锁被解除
            //否则，一次unblock()调用就会解除整个锁，即使当前锁已经被加锁过多次
            if(lockedCount == 0){
                isLocked = false;
                notify();
            }
        }
    }
}
