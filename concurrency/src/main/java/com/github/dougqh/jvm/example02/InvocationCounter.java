package com.github.dougqh.jvm.example02;

import com.github.dougqh.jvm.support.RunWith;
import com.github.dougqh.jvm.support.Output;

@RunWith({"-XX:-TieredCompilation", "-XX:+PrintCompilation"})
@Output(highlight="InvocationCounter::hotMethod")
public class InvocationCounter {
  public static void main(final String[] args)
    throws InterruptedException
  {
    for ( int i = 0; i < 20_000; ++i ) {
      hotMethod();
    }
    
    System.out.println("Waiting for compiler...");
    Thread.sleep(5_000);
  }
  
  static void hotMethod() {}
}