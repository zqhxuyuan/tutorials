package com.java7developer.chapter5;

import com.java7developer.chapter5.ThreadPoolManager.CancelProxy;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;

public class ThreadPoolMain {

  private ThreadPoolManager manager;

  private void cancelUsingReflection(ScheduledFuture<?> hndl) {
    Method meth = manager.makeReflective();

    try {
      System.out.println("With Reflection");
      meth.invoke(hndl);
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  private void cancelUsingProxy(ScheduledFuture<?> hndl) {
    CancelProxy proxy = manager.makeProxy();

    System.out.println("With Proxy");
    proxy.invoke(manager, hndl);
  }

  private void cancelUsingMH(ScheduledFuture<?> hndl) {
    MethodHandle mh = manager.makeMh();

    try {
      System.out.println("With Method Handle");
      mh.invokeExact(manager, hndl);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void run() {
    BlockingQueue<WorkUnit<String>> lbq = new LinkedBlockingQueue<>();
    manager = new ThreadPoolManager(lbq);

    final QueueReaderTask msgReader = new QueueReaderTask(100) {
      @Override
      public void doAction(String msg_) {
        if (msg_ != null)
          System.out.println("Msg recvd: " + msg_);
      }
    };
    ScheduledFuture<?> hndl = manager.run(msgReader);
    cancelUsingMH(hndl);
    // cancelUsingProxy(hndl);
    // cancelUsingReflection(hndl);
  }

  public static void main(String[] args) {
    ThreadPoolMain main = new ThreadPoolMain();
    main.run();
  }
}
