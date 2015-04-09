package com.jenkov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * Fair Lock implementation with nested monitor lockout problem
 */
public class FairLockDeadMonitor {

    private boolean isLocked = false;
    private Thread lockingThread = null;
    private List<QueueObject> waitingThreads = new ArrayList<>();

    /**
     * 在方法内部有两个synchronized块，一个锁定this，一个嵌在上一个synchronized块内部，它锁定的是局部变量queueObject。
     * 当一个线程调用queueObject.wait()方法的时候，它仅仅释放的是在queueObject对象实例的锁，并没有释放”this”上面的锁
     * @throws InterruptedException
     */
    public void lock() throws InterruptedException{
        QueueObject queueObject = new QueueObject();

        //同步块1
        synchronized(this){
            waitingThreads.add(queueObject);

            while(isLocked || waitingThreads.get(0) != queueObject){
                //同步块2
                synchronized(queueObject){
                    try{
                        queueObject.wait();
                    }catch(InterruptedException e){
                        waitingThreads.remove(queueObject);
                        throw e;
                    }
                }
            }
            waitingThreads.remove(queueObject);
            isLocked = true;
            lockingThread = Thread.currentThread();
        }
    }

    /**
     * unlock方法被声明成了synchronized，这就相当于一个synchronized(this)块
     *
     * 如果一个线程在lock()中等待，该线程将持有与this关联的管程对象。所有调用unlock()的线程将会一直保持阻塞，
     * 等待着前面那个已经获得this锁的线程释放this锁，但这永远也发生不了，
     * 因为只有某个线程成功地给lock()中等待的线程发送了信号，this上的锁才会释放，但只有执行unlock()方法才会发送这个信号
     */
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
