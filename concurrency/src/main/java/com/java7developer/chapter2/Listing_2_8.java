package com.java7developer.chapter2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Code for Listing 2_8 - You'll need to change the hardcoded path.
 */
public class Listing_2_8 {

  public static void main(String[] args) {
    try {
      Path file = Paths.get("/usr/karianna/foobar.txt");

      AsynchronousFileChannel channel = AsynchronousFileChannel.open(file);

      ByteBuffer buffer = ByteBuffer.allocate(100_000);
      Future<Integer> result = channel.read(buffer, 0);

      while (!result.isDone()) {
        ProfitCalculator.calculateTax();
      }

      Integer bytesRead = result.get();
      System.out.println("Bytes read [" + bytesRead + "]");
    } catch (IOException | ExecutionException | InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }

  private static class ProfitCalculator {

    public ProfitCalculator() {
    }

    public static void calculateTax() {
    }
  }
}
