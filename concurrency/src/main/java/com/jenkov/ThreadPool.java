package com.jenkov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 线程池（Thread Pool）对于限制应用程序中同一时刻运行的线程数很有用。
 * 因为每启动一个新线程都会有相应的性能开销，每个线程都需要给栈分配一些内存等
 *
 * 我们可以把并发执行的任务传递给一个线程池，来替代为每个并发执行的任务都启动一个新的线程。
 * 只要池里有空闲的线程，任务就会分配给一个线程执行。在线程池的内部，任务被插入一个阻塞队列（Blocking Queue ），
 * 线程池里的线程会去取这个队列里的任务。当一个新任务插入队列时，一个空闲线程就会成功的从队列中取出任务并且执行它。
 *
 * 类ThreadPool是线程池的公开接口，而类PoolThread用来实现执行任务的子线程
 */
public class ThreadPool {

    private BlockingQueue       taskQueue   = null;
    private List<PoolThread>    threads     = new ArrayList<PoolThread>();
    private boolean             isStopped   = false;

    public ThreadPool(int noOfThreads, int maxNoOfTasks) {
        taskQueue = new BlockingQueue(maxNoOfTasks);

        for (int i=0; i<noOfThreads; i++) {
            threads.add(new PoolThread(taskQueue));
        }
        for (PoolThread thread : threads) {
            thread.start();
        }
    }

    /**
     * 为了执行一个任务，方法 ThreadPool.execute(Runnable r) 用 Runnable 的实现作为调用参数。
     * Runnable 对象被放入 阻塞队列 (Blocking Queue)，等待着被子线程取出队列
     * @param task
     * @throws Exception
     */
    public synchronized void execute(Runnable task) throws Exception{
        if(this.isStopped) throw new IllegalStateException("ThreadPool is stopped");
        this.taskQueue.enqueue(task);
    }

    public synchronized boolean stop() {
        boolean isStopped = true;
        for (PoolThread thread : threads) {
            thread.stop();
        }
        return isStopped;
    }
}
