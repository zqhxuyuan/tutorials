package com.java7developer.chapter4;

import com.java7developer.chapter4.pets.WorkUnit;

import java.util.concurrent.*;

/**
 * Created by zqhxuyuan on 15-5-12.
 *
 */
public class STPEExample extends Thread{

    //线程池
    private ScheduledExecutorService service;
    //异步任务的执行结果
    private static ScheduledFuture<?> future;
    //任务存放在阻塞队列里
    private static BlockingQueue<WorkUnit<String>> queue = new LinkedBlockingQueue<>();

    public STPEExample(){
        //这个线程池有2个线程. 但是对于任务而言,不需要直到它要在哪个线程上运行. 由线程池负责调度
        service = Executors.newScheduledThreadPool(2);
    }

    //msgReader对象被安排poll()一个队列,从队列中的WorkUnit对象里取得工作项,然后输出
    class MsgReader implements Runnable{
        @Override
        public void run() {
            //从阻塞队列中取出任务执行
            String nextMsg = queue.poll().getWorkUnit();
            if(nextMsg != null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(nextMsg);
            }
        }
    }

    @Override
    public void run() {
        //每隔10毫秒就唤醒一个线程,让它尝试poll()一个队列
        //service是线程池,msgReader是一个可以运行的任务.线程池会调用其中的线程来运行任务
        future = service.scheduleAtFixedRate(new MsgReader(), 10, 10, TimeUnit.MILLISECONDS);
    }

    public void cancel(){
        ScheduledFuture<?> scheduledFuture = future;

        service.schedule(new Runnable() {
            @Override
            public void run() {
                scheduledFuture.cancel(true);
            }
        }, 10, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        queue.add(new WorkUnit<>("Hello"));
        queue.add(new WorkUnit<>("Concurrent"));

        STPEExample thread = new STPEExample();
        thread.start();
        //thread.cancel();

        //shouldn't do like this!
        //because main thread exit will cause new sub-thread exit too
        //System.out.println("end...");
        //System.exit(0);
    }
}
