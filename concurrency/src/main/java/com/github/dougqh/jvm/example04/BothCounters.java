package com.github.dougqh.jvm.example04;

import com.github.dougqh.jvm.support.RunWith;
import com.github.dougqh.jvm.support.Output;

@RunWith({"-XX:-TieredCompilation", "-XX:+PrintCompilation"})
@Output(highlight={"BothCounters::outerMethod", "BothCounters::innerMethod"})
public class BothCounters {
  public static void main(final String[] args)
    throws InterruptedException
  {
    for ( int i = 0; i < 2; ++i ) {
      outerMethod();
    }
  
    System.out.println("Waiting for compiler...");
    Thread.sleep(5000);
  }
  
  static void outerMethod() {
    for ( int i = 0; i < 10_000; ++i ) {
      innerMethod();
    }
  }
  
  static void innerMethod() {}
}