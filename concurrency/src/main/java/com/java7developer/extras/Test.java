package com.java7developer.extras;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
*/
public class Test {
  public static void main(String args[]) {
    AutoUnlockable resource1 = new AutoUnlockable();
    AutoUnlockable resource2 = new AutoUnlockable();

    /*
     * try (SyncLocker.lock(resource1, resource2) ) { // When this is done,
     * release the resources System.out.println("Love try with resources!"); }
     */
  }
}
