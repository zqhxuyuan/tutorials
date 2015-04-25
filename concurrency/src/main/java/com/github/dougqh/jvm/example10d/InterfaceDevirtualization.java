package com.github.dougqh.jvm.example10d;

import com.github.dougqh.jvm.support.AlsoSquare;
import com.github.dougqh.jvm.support.RunWith;
import com.github.dougqh.jvm.support.Output;
import com.github.dougqh.jvm.support.AnotherSquare;
import com.github.dougqh.jvm.support.IFunc;
import com.github.dougqh.jvm.support.Sqrt;
import com.github.dougqh.jvm.support.Square;
import com.github.dougqh.jvm.support.YetAnotherSquare;

@RunWith({
  "-XX:-TieredCompilation", "-XX:+PrintCompilation",
  // "-XX:+PrintSafepointStatistics", "-XX:PrintSafepointStatisticsCount=1"
})
@Output(highlight={
  "InterfaceDevirtualization::apply\\d",
  "made not entrant"
})
public class InterfaceDevirtualization {
  public static void main(String[] args) throws InterruptedException {
    System.out.println("Using Square...");
    IFunc func = new Square();
    for ( int i = 0; i < 20_000; ++i ) {
      apply1(func, i);
      apply2(func, i);
    }
    
    Thread.sleep(5_000);
    
    System.out.printf("Loading %s - no CHA for interfaces!%n", Sqrt.class);

    System.out.println("Keep using Square in apply1...");
    func = new Square();
    for ( int i = 0; i < 20_000; ++i ) {
      apply1(func, i);
    }

    Thread.sleep(5_000);

    System.out.println("Use AlsoSquare in apply1...");
    func = new AlsoSquare();
    for ( int i = 0; i < 20_000; ++i ) {
      apply1(func, i);
    }

    Thread.sleep(5_000);

    System.out.println("Use AnotherSquare in apply1...");
    func = new AnotherSquare();
    for ( int i = 0; i < 20_000; ++i ) {
      apply1(func, i);
    }
    
    Thread.sleep(5_000);

    System.out.println("Use YetAnotherSquare in apply1...");
    func = new YetAnotherSquare();
    for ( int i = 0; i < 20_000; ++i ) {
      apply1(func, i);
    }
    
    Thread.sleep(5_000);
  }
  
  static double apply1(IFunc func, int x) {
    return func.apply(x);
  }
  
  static double apply2(IFunc func, int x) {
    return func.apply(x);
  }
}