package com.jenkov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * Fair Lock implementation with slipped conditions problem
 */
public class FairLockSlippedCondition {

    private boolean isLocked = false;
    private Thread lockingThread = null;
    private List<QueueObject> waitingThreads = new ArrayList<>();

    public void lock() throws InterruptedException{
        QueueObject queueObject = new QueueObject();

        synchronized(this){
            waitingThreads.add(queueObject);
        }

        boolean mustWait = true;
        while(mustWait){
            //同步块1: 检查内部变量的值
            synchronized(this){
                mustWait = isLocked || waitingThreads.get(0) != queueObject;
            }

            //同步块2: 检查线程是否需要等待
            synchronized(queueObject){
                if(mustWait){
                    try{
                        queueObject.wait();
                    }catch(InterruptedException e){
                        waitingThreads.remove(queueObject);
                        throw e;
                    }
                }
            }
        }

        //同步块3: 只会在mustWait为false的时候执行。它将isLocked重新设回true，然后离开lock()方法
        synchronized(this){
            waitingThreads.remove(queueObject);
            isLocked = true;
            lockingThread = Thread.currentThread();
        }
    }

    public synchronized void unlock(){
        if(this.lockingThread != Thread.currentThread()){
            throw new IllegalMonitorStateException("Calling thread has not locked this lock");
        }
        isLocked = false;
        lockingThread = null;
        if(waitingThreads.size() > 0){
            QueueObject queueObject = waitingThreads.get(0);
            synchronized(queueObject){
                queueObject.notify();
            }
        }
    }
}
