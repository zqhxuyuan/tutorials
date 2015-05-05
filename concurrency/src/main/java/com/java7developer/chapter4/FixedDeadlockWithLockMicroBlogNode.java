package com.java7developer.chapter4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// This class needs to implement a different interface, as it needs to 
// have awareness of the confirmation / locking strategy of its backup node
public class FixedDeadlockWithLockMicroBlogNode implements
    ConfirmingMicroBlogNode {

  private static Update getUpdate(String s) {
    Update.Builder b = new Update.Builder();
    b.updateText(s).author(new Author("Ben"));

    return b.build();
  }

  private final String ident;

  private final Lock lock = new ReentrantLock();

  public FixedDeadlockWithLockMicroBlogNode(String ident_) {
    ident = ident_;
  }

  @Override
  public String getIdent() {
    return ident;
  }

  @Override
  public void propagateUpdate(Update upd_, ConfirmingMicroBlogNode backup_) {
    boolean acquired = false;
    boolean done = false;

    while (!done) {
      int wait = (int) (Math.random() * 10);
      try {
        acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);
        if (acquired) {
          System.out.println(ident + ": recvd: " + upd_.getUpdateText()
              + " ; backup: " + backup_.getIdent());
          done = backup_.tryConfirmUpdate(this, upd_);
        }
      } catch (InterruptedException e) {
      } finally {
        if (acquired)
          lock.unlock();
      }
      if (!done)
        try {
          Thread.sleep(wait);
        } catch (InterruptedException e) {
        }
    }
  }

  @Override
  public boolean tryConfirmUpdate(ConfirmingMicroBlogNode other_, Update upd_) {
    long startTime = System.currentTimeMillis();
    boolean acquired = false;
    try {
      int wait = (int) (Math.random() * 10);
      acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);

      if (acquired) {
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(ident + ": recvd confirm: " + upd_.getUpdateText()
            + " from " + other_.getIdent() + " - took " + elapsed + " millis");
        return true;
      }
    } catch (InterruptedException e) {
    } finally {
      if (acquired)
        lock.unlock();
    }
    return false;
  }

  public static void main(String[] a) {
    final FixedDeadlockWithLockMicroBlogNode local = new FixedDeadlockWithLockMicroBlogNode(
        "localhost:8888");
    final FixedDeadlockWithLockMicroBlogNode other = new FixedDeadlockWithLockMicroBlogNode(
        "localhost:8988");
    final Update first = getUpdate("1");
    final Update second = getUpdate("2");

    new Thread(new Runnable() {
      public void run() {
        local.propagateUpdate(first, other);
      }
    }).start();

    new Thread(new Runnable() {
      public void run() {
        other.propagateUpdate(second, local);
      }
    }).start();
  }

}