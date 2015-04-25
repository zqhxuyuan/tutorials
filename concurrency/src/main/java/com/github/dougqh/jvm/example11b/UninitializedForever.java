package com.github.dougqh.jvm.example11b;

import com.github.dougqh.jvm.support.RunWith;
import com.github.dougqh.jvm.support.Output;

@RunWith({"-XX:-TieredCompilation", "-XX:+PrintCompilation"})
@Output(highlight={
  "UninitializedForever::main",
  "made not entrant",
  "made zombie"
})
public class UninitializedForever {
  static class Uninitialized {
    static {
      if ( true ) throw new RuntimeException();
    }
  }
  
  public static void main(String[] args) {
    for ( int i = 0; i < 100_000; ++i ) {
      try {
        new Uninitialized();
      } catch ( Throwable t ) {
        // ignore
      }
    }
  }
}
