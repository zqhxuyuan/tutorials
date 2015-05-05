package com.java7developer.extras;

import java.util.concurrent.locks.ReentrantLock;

class AutoUnlockable implements Lockable {
  ReentrantLock lock = new ReentrantLock();

  public ReentrantLock getLock() {
    return lock;
  }

}
