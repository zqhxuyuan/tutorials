package com.java7developer.extras;

import java.util.concurrent.locks.ReentrantLock;

interface Lockable {
  public ReentrantLock getLock();
}
