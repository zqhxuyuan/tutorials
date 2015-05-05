package com.java7developer.chapter6;

public class TimerDrift {

  private static void runWithSpin(String[] args) {
    long nowNanos = 0, startNanos = 0;
    long startMillis = System.currentTimeMillis();
    long nowMillis = startMillis;

    while (startMillis == nowMillis) {
      startNanos = System.nanoTime();
      nowMillis = System.currentTimeMillis();
    }

    startMillis = nowMillis;
    double maxDrift = 0;
    long lastMillis;

    while (true) {
      lastMillis = nowMillis;
      while (nowMillis - lastMillis < 1000) {
        nowNanos = System.nanoTime();
        nowMillis = System.currentTimeMillis();
      }

      long durationMillis = nowMillis - startMillis;
      double driftNanos = 1000000 * (((double) (nowNanos - startNanos)) / 1000000 - durationMillis);
      if (Math.abs(driftNanos) > maxDrift) {
        System.out.println("Now - Start = " + durationMillis + " driftNanos = "
            + driftNanos);
        maxDrift = Math.abs(driftNanos);
      }
    }
  }

  public static void main(String[] args) {
    runWithSpin(args);
  }

}
