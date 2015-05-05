package com.java7developer.extras;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class SyncLocker {

  private static final ReentrantLock globalLock = new ReentrantLock(true);
  private static final Condition access = globalLock.newCondition();

  private SyncLocker() {
  }

  public static NonExceptionAutoCloseable lock(final ReentrantLock... locks) {
    lockImpl(locks);
    return new NonExceptionAutoCloseable() {

      public void close() {
        SyncLocker.unlock(locks);
      }
    };
  }

  public static void lockImpl(ReentrantLock... locks) {
    boolean obtainable = true;
    try {
      globalLock.lock();
      while (true) {
        // Check to see if all the locks are available
        for (ReentrantLock lock : locks) {
          if (lock.isLocked()) {
            obtainable = false;
            try {
              access.await();
            } catch (InterruptedException ie) {
              // This needs to be thrown, perhaps a different
              // method, that way the programmer can decide if they
              // want the silent exception handling that this current
              // implementation affords.
            }
            break;
          }
        }

        // If all the locks are available
        if (obtainable) {
          for (ReentrantLock lock : locks) {
            lock.lock();
          }
          return;
          // Otherwise we need to try again
        } else
          obtainable = true;
      }
    } finally {
      globalLock.unlock();
    }
  }

  public static void unlock(ReentrantLock... locks) {
    globalLock.lock();
    try {
      for (ReentrantLock lock : locks) {
        lock.unlock();
      }

      access.signal();
    } finally {
      globalLock.unlock();
    }
  }

  public static NonExceptionAutoCloseable lock(final Lockable lockables) {
    lockImpl(lockables);
    return new NonExceptionAutoCloseable() {
      public void close() {
        SyncLocker.unlock(lockables);
      }
    };
  }

  public static void lockImpl(Lockable... lockables) {
    boolean obtainable = true;
    try {
      globalLock.lock();
      while (true) {
        // Check to see if all the locks are available
        for (Lockable lockable : lockables) {
          if (lockable.getLock().isLocked()) {
            obtainable = false;
            try {
              access.await();
            } catch (InterruptedException ie) {
              // This needs to be thrown, perhaps a different
              // method, that way the programmer can decide if they
              // want the silent exception handling that this current
              // implementation affords.
            }
            break;
          }
        }

        // If all the locks are available
        if (obtainable) {
          for (Lockable lockable : lockables) {
            lockable.getLock().lock();
          }
          return;
          // Otherwise we need to try again
        } else
          obtainable = true;
      }
    } finally {
      globalLock.unlock();
    }
  }

  public static void unlock(Lockable... lockables) {
    globalLock.lock();
    try {
      for (Lockable lockable : lockables) {
        lockable.getLock().unlock();
      }

      access.signal();
    } finally {
      globalLock.unlock();
    }
  }

  public static ReentrantLock getLock() {
    return globalLock;
  }
}
