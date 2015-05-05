package com.java7developer.chapter4;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MicroBlogTimeline {
  private final CopyOnWriteArrayList<Update> updates;
  private final Lock lock;
  private final String name;
  private Iterator<Update> it;

  public MicroBlogTimeline(String name_) {
    name = name_;
    updates = new CopyOnWriteArrayList<>();
    lock = new ReentrantLock();
  }

  MicroBlogTimeline(String name_, CopyOnWriteArrayList<Update> l_, Lock lock_) {
    name = name_;
    updates = l_;
    lock = lock_;
  }

  public void addUpdate(Update update_) {
    updates.add(update_);
  }

  public void prep() {
    it = updates.iterator();
  }

  public void printTimeline() {
    lock.lock();
    try {
      if (it != null) {
        System.out.print(name + ": ");
        while (it.hasNext()) {
          Update s = it.next();
          System.out.print(s + ", ");
        }
        System.out.println();
      }
    } finally {
      lock.unlock();
    }
  }
}