package com.jenkov;

//worker线程
public class PoolThread extends Thread {

    private BlockingQueue<Runnable> taskQueue = null;
    private boolean                 isStopped = false;

    public PoolThread(BlockingQueue<Runnable> queue) {
        taskQueue = queue;
    }

    //一个空闲的 PoolThread 线程会把 Runnable 对象从队列中取出并执行
    public void run() {
        //执行完毕后，PoolThread 进入循环并且尝试从队列中再取出一个任务，直到线程终止
        while (!isStopped()) {
            try {
                Runnable runnable = (Runnable)taskQueue.dequeue();
                runnable.run();
            } catch(Exception e) {
                // 写日志或者报告异常, 但保持线程池运行.
            }
        }
    }

    //终止子线程
    public synchronized void toStop() {
        isStopped = true;
        this.interrupt(); // 打断池中线程的 dequeue() 调用.
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }
}