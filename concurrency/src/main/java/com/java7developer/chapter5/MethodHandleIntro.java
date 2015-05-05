package com.java7developer.chapter5;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandleIntro {

  private static MethodHandleIntro inst = null;

  // Constructor
  private MethodHandleIntro() {
    super();
  }

  public String toString() {
    return "My toString()";
  }

  /*
   * This is where your actual code will go
   */
  private void run() throws Throwable {
    MethodHandle mh = getToStringMH();
    String s = (String) mh.invoke(this);
    System.out.println(s);
  }

  public MethodHandle getToStringMH() {
    MethodHandle mh;
    MethodType mt = MethodType.methodType(String.class);
    MethodHandles.Lookup lk = MethodHandles.lookup();

    try {
      mh = lk.findVirtual(getClass(), "toString", mt);
    } catch (NoSuchMethodException | IllegalAccessException mhx) {
      throw (AssertionError) new AssertionError().initCause(mhx);
    }

    return mh;
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Throwable {
    inst = new MethodHandleIntro();
    inst.run();
  }

}
