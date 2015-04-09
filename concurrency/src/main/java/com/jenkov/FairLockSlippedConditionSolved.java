package com.jenkov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * Fair Lock implementation without nested monitor lockout problem,
 * but with missed signals problem.
 */
public class FairLockSlippedConditionSolved {

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
            //对局部变量mustWait的检查与赋值是在同一个同步块中完成的
            synchronized(this){
                mustWait = isLocked || waitingThreads.get(0) != queueObject;
                if(!mustWait){
                    waitingThreads.remove(queueObject);
                    isLocked = true;
                    lockingThread = Thread.currentThread();
                    //如果一个线程肯定不会等待（即mustWait为false），那么就没必要让它进入到
                    //synchronized(queueObject)同步块中和执行if(mustWait)子句了
                    return;
                }
            }

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
