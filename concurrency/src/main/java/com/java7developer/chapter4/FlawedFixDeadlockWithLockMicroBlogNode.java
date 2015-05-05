package com.java7developer.chapter4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlawedFixDeadlockWithLockMicroBlogNode implements
    SimpleMicroBlogNode {

  private static Update getUpdate(String s) {
    Update.Builder b = new Update.Builder();
    b.updateText(s).author(new Author("Ben"));

    return b.build();
  }

  private final String ident;

  private final Lock lock = new ReentrantLock();

  public FlawedFixDeadlockWithLockMicroBlogNode(String ident_) {
    ident = ident_;
  }

  public String getIdent() {
    return ident;
  }

  @Override
  public void propagateUpdate(Update upd_, SimpleMicroBlogNode backup_) {
    boolean acquired = false;

    while (!acquired) {
      try {
        int wait = (int) (Math.random() * 10);
        acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);
        if (acquired) {
          System.out.println(ident + ": recvd: " + upd_.getUpdateText()
              + " ; backup: " + backup_.getIdent());
          backup_.confirmUpdate(this, upd_);
        } else {
          Thread.sleep(wait);
        }
      } catch (InterruptedException e) {
      } finally {
        if (acquired)
          lock.unlock();
      }
    }
  }

  @Override
  public void confirmUpdate(SimpleMicroBlogNode other_, Update upd_) {
    boolean acquired = false;

    while (!acquired) {
      try {
        int wait = (int) (Math.random() * 10);
        acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);
        if (acquired) {
          System.out.println(ident + ": recvd confirm: " + upd_.getUpdateText()
              + " from " + other_.getIdent());
        } else {
          Thread.sleep(wait);
        }
      } catch (InterruptedException e) {
      } finally {
        if (acquired)
          lock.unlock();
      }
    }
  }

  public static void main(String[] a) {
    final FlawedFixDeadlockWithLockMicroBlogNode local = new FlawedFixDeadlockWithLockMicroBlogNode(
        "localhost:8888");
    final FlawedFixDeadlockWithLockMicroBlogNode other = new FlawedFixDeadlockWithLockMicroBlogNode(
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