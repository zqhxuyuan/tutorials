package com.jenkov;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 在enqueue和dequeue方法内部，只有队列的大小等于上限（limit）或者下限（0）时，
 * 才调用notifyAll方法。如果队列的大小既不等于上限，也不等于下限，
 * 任何线程调用enqueue或者dequeue方法时，都不会阻塞，都能够正常的往队列中添加或者移除元素
 */
public class BlockingQueue<T> {

    private List queue = new LinkedList();
    private int limit = 10;

    public BlockingQueue(int limit) {
        this.limit = limit;
    }

    public synchronized void enqueue(Object item) throws InterruptedException {
        //当队列是满时，往队列里添加元素的操作会被阻塞
        //试图往已满的阻塞队列中添加新元素的线程同样也会被阻塞，直到其他的线程使队列重新变得空闲起来
        while (this.queue.size() == this.limit) {
            wait();
        }
        if (this.queue.size() == 0) {
            notifyAll();
        }
        this.queue.add(item);
    }

    public synchronized Object dequeue() throws InterruptedException {
        //当队列是空的时，从队列中获取元素的操作将会被阻塞
        //试图从空的阻塞队列中获取元素的线程将会被阻塞，直到其他的线程往空的队列插入新的元素
        while (this.queue.size() == 0) {
            wait();
        }
        if (this.queue.size() == this.limit) {
            notifyAll();
        }
        return this.queue.remove(0);
    }

}