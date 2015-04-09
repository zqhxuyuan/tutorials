package com.jenkov;

import java.util.HashMap;
import java.util.Map;

/**
 * 读锁重入
 */
public class ReadWriteLockReadReentrant {

    //存储已经持有读锁的线程以及对应线程获取读锁的次数
    private Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();

    private int writers = 0;
    private int writeRequests = 0;

    public synchronized void lockRead() throws InterruptedException{
        Thread callingThread = Thread.currentThread();
        //如果不能重入锁, 则等待
        while(!canGrantReadAccess(callingThread)){
            wait();
        }
        //是重入锁, 添加当前线程获取读锁的次数
        readingThreads.put(callingThread, (getAccessCount(callingThread) + 1));
    }

    public synchronized void unlockRead(){
        Thread callingThread = Thread.currentThread();
        int accessCount = getAccessCount(callingThread);
        if(accessCount == 1) {
            readingThreads.remove(callingThread);
        } else {
            readingThreads.put(callingThread, (accessCount -1));
        }
        notifyAll();
    }

    /**
     * 是否可以重入读锁
     * @param callingThread 调用者线程
     * @return true:表示可重入, false:表示不可重入
     */
    private boolean canGrantReadAccess(Thread callingThread){
        //有线程持有写锁, 不能读
        if(writers > 0) return false;
        //持有读锁, 可以重入
        if(isReader(callingThread)) return true;
        //有线程请求写, 且当前线程没有持有读锁
        if(writeRequests > 0) return false;
        //其他情况表示: 没有线程持有写锁, 也没有线程请求写, 都是读的情况. 因为允许多次读, 所以是可重入的
        return true;
    }

    private int getAccessCount(Thread callingThread){
        Integer accessCount = readingThreads.get(callingThread);
        if(accessCount == null) return 0;
        return accessCount.intValue();
    }

    private boolean isReader(Thread callingThread){
        return readingThreads.get(callingThread) != null;
    }
}
