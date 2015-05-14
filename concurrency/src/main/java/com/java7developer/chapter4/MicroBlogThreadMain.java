package com.java7developer.chapter4;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MicroBlogThreadMain {

  public static void main(String[] a) {
    //只需要一个构建器实例
    final Update.Builder ub = new Update.Builder();
    //阻塞队列, 生产者和消费者共用这个阻塞队列
    final BlockingQueue<Update> lbq = new LinkedBlockingQueue<>(100);

    //生产者线程
    MicroBlogThread t1 = new MicroBlogThread(lbq, 10) {
      public void doAction() {
        text = text + "X";
        //每个Update对象的产生都是通过构建器实例的链式调用, 最终调用build方法得到的
        Update u = ub.author(new Author("Tallulah")).updateText(text).build();
        boolean handed = false;
        try {
          //往队列中生产任务
          handed = updates.offer(u, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        //如果队里满了,消费者来不及消费,生产者往队列中继续offer元素,会被阻塞
        if (!handed)
          System.out.println("Unable to handoff Update to Queue due to timeout");
      }
    };

    //消费者线程
    MicroBlogThread t2 = new MicroBlogThread(lbq, 1000) {
      public void doAction() {
        Update u = null;
        try {
          //从队列中获取任务并执行任务
          u = updates.take();
        } catch (InterruptedException e) {
          return;
        }
      }
    };
    //分别启动生产者线程和消费者线程
    t1.start();
    t2.start();
  }

}
