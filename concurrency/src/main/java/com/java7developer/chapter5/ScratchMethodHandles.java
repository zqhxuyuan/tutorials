package com.java7developer.chapter5;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Comparator;
import java.util.Objects;

public class ScratchMethodHandles {

  private static ScratchMethodHandles inst = null;

  // Constructor
  private ScratchMethodHandles() {
    super();
  }

  /*
   * This is where your actual code will go
   */
  private void run() {
    MethodType mtToString = MethodType.methodType(String.class);
    MethodType mtSetter = MethodType.methodType(void.class, Object.class);
    MethodType mtStringComparator = MethodType.methodType(int.class,
        String.class, String.class);

    /*
     * MethodHandle mh = getToStringMH(); try {
     * System.out.println((String)mh.invokeExact(this)); } catch (Throwable e) {
     * // TODO Auto-generated catch block e.printStackTrace(); }
     */

    /*
     * MethodHandle mh = getStringCompMH(); MyStringComp comp = new
     * MyStringComp(); try { System.out.println(mh.invoke(comp, "a", "bb"));
     * System.out.println(mh.invoke(comp, "ccc", "bb")); } catch (Throwable e) {
     * // TODO Auto-generated catch block e.printStackTrace(); }
     */

    MethodHandle mh = getIntCompMH();
    MyIntComp comp = new MyIntComp();
    try {
      System.out.println(mh.invoke(comp, 1, 2));
    } catch (Throwable e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static class MyStringComp implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
      if (o1 == null) {
        if (o2 == null)
          return 0;
        return -1;
      }
      // o1 !null
      if (o2 == null)
        return 1;

      if (o1.length() == o2.length())
        return 0;

      return o1.length() > o2.length() ? 1 : -1;
    }
  }

  private static class MyIntComp implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
      if (o1 == null) {
        if (o2 == null)
          return 0;
        return -1;
      }
      // o1 !null
      if (o2 == null)
        return 1;

      if (o1.equals(o2))
        return 0;

      return o1 > o2 ? 1 : -1;
    }
  }

  public MethodHandle getIntCompMH() {
    MethodHandle mh;
    MethodType mtIntComparator = MethodType.methodType(int.class,
        Integer.class, Integer.class);
    MethodHandles.Lookup lk = MethodHandles.lookup();

    try {
      mh = lk.findVirtual(MyIntComp.class, "compare", mtIntComparator);
    } catch (NoSuchMethodException | IllegalAccessException mhx) {
      throw (AssertionError) new AssertionError().initCause(mhx);
    }

    return mh;
  }

  public MethodHandle getStringCompMH() {
    MethodHandle mh;
    MethodType mtStringComparator = MethodType.methodType(int.class,
        String.class, String.class);
    MethodHandles.Lookup lk = MethodHandles.lookup();

    try {
      mh = lk.findVirtual(MyStringComp.class, "compare", mtStringComparator);
    } catch (NoSuchMethodException | IllegalAccessException mhx) {
      throw (AssertionError) new AssertionError().initCause(mhx);
    }

    return mh;
  }

  public MethodHandle getToStringMH() {
    MethodHandle mh;
    MethodType mtToString = MethodType.methodType(String.class);
    MethodHandles.Lookup lk = MethodHandles.lookup();

    try {
      mh = lk.findVirtual(getClass(), "toString", mtToString);
    } catch (NoSuchMethodException | IllegalAccessException mhx) {
      throw (AssertionError) new AssertionError().initCause(mhx);
    }

    return mh;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    inst = new ScratchMethodHandles();
    inst.run();
  }

}
