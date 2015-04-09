package com.jenkov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 公平锁
 *
 * 方法:
 * 如果每个线程在不同的对象上调用wait()，那么只有一个线程会在该对象上调用wait()，
 * Lock类可以决定哪个对象能对其调用notify()，因此能做到有效的选择唤醒哪个线程
 *
 * 细节:
 * 每一个调用lock()的线程都会进入一个队列，当解锁后，
 * 只有队列里的第一个线程被允许锁住FairLock实例，
 * 所有其它的线程都将处于等待状态，直到他们处于队列头部
 *
 * 通过这种方式，在同一时间仅有一个等待线程获得唤醒，而不是所有的等待线程。这也是实现FairLock公平性的核心所在
 */
public class FairLock {
    private boolean           isLocked       = false;
    private Thread            lockingThread  = null;
    private List<QueueObject> waitingThreads = new ArrayList<QueueObject>();

    public void lock() throws InterruptedException{
        //新创建了一个QueueObject的实例，并对每个调用lock()的线程进行入队列
        QueueObject queueObject           = new QueueObject();
        boolean     isLockedForThisThread = true;
        synchronized(this){
            waitingThreads.add(queueObject);
        }

        while(isLockedForThisThread){
            synchronized(this){
                isLockedForThisThread = isLocked || waitingThreads.get(0) != queueObject;
                if(!isLockedForThisThread){
                    isLocked = true;
                    waitingThreads.remove(queueObject);
                    lockingThread = Thread.currentThread();
                    return;
                }
            }
            //避免被monitor嵌套锁死，所以另外的线程可以解锁，只要当没有线程在lock方法的synchronized(this)块中执行即可
            try{
                queueObject.doWait();
            }catch(InterruptedException e){
                //在InterruptedException抛出的情况下，线程得以离开lock()，并需让它从队列中移除
                synchronized(this) {
                    waitingThreads.remove(queueObject);
                }
                throw e;
            }
        }
    }

    public synchronized void unlock(){
        if(this.lockingThread != Thread.currentThread()){
            throw new IllegalMonitorStateException("Calling thread has not locked this lock");
        }
        isLocked      = false;
        lockingThread = null;
        if(waitingThreads.size() > 0){
            //调用unlock()的线程将从队列头部获取QueueObject，并对其调用doNotify()，以唤醒在该对象上等待的线程
            waitingThreads.get(0).doNotify();
        }
    }
}