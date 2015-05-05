package com.java7developer.chapter5;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.*;

public class ThreadPoolManager {

  private final ScheduledExecutorService stpe = Executors
      .newScheduledThreadPool(2);
  private final BlockingQueue<WorkUnit<String>> lbq;

  public ThreadPoolManager(BlockingQueue<WorkUnit<String>> lbq_) {
    lbq = lbq_;
  }

  public ScheduledFuture<?> run(QueueReaderTask msgReader) {
    msgReader.setQueue(lbq);
    return stpe.scheduleAtFixedRate(msgReader, 10, 10, TimeUnit.MILLISECONDS);
  }

  private void cancel(final ScheduledFuture<?> hndl) {
    stpe.schedule(new Runnable() {
      public void run() {
        hndl.cancel(true);
      }
    }, 10, TimeUnit.MILLISECONDS);
  }

  public Method makeReflective() {
    Method meth = null;

    try {
      Class<?>[] argTypes = new Class[] { ScheduledFuture.class };
      meth = ThreadPoolManager.class.getDeclaredMethod("cancel", argTypes);
      meth.setAccessible(true);
    } catch (IllegalArgumentException | NoSuchMethodException
        | SecurityException e) {
      e.printStackTrace();
    }

    return meth;
  }

  public static class CancelProxy {
    private CancelProxy() {
    }

    public void invoke(ThreadPoolManager mae_, ScheduledFuture<?> hndl_) {
      mae_.cancel(hndl_);
    }
  }

  public CancelProxy makeProxy() {
    return new CancelProxy();
  }

  public MethodHandle makeMh() {
    MethodHandle mh;
    MethodType desc = MethodType.methodType(void.class, ScheduledFuture.class);

    try {
      mh = MethodHandles.lookup().findVirtual(ThreadPoolManager.class,
          "cancel", desc);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw (AssertionError) new AssertionError().initCause(e);
    }

    return mh;
  }
}