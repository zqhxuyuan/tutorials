package com.ibm.jtp;

/**
 * Created by zqhxuyuan on 15-5-5.
 *
 * 使用同步的线程安全的计数器
 *
 * 在多个线程同时请求同一个锁时，会有一个线程获胜并得到锁，而其他线程被阻塞。
 * JVM 实现阻塞的方式通常是挂起阻塞的线程，过一会儿再重新调度它。
 * 由此造成的上下文切换相对于锁保护的少数几条指令来说，会造成相当大的延迟
 */
public class CounterSync {

    private long value = 0;

    //getValue 方法上也需要同步，以保证调用 getValue 的线程看到的是最新的值
    public synchronized long getValue() {
        return value;
    }

    //虽然增加看起来是单一操作，但实际是三个独立操作的简化：检索值，给值加 1，再写回值
    public synchronized long increment() {
        return ++value;
    }

}
