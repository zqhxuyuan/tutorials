package com.zqh.timer;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhengqh on 16/3/28.
 */
public class TimerTest {

    private static final TimingWheel<CometChannel> TIMING_WHEEL = new TimingWheel<CometChannel>(1, 60, TimeUnit.SECONDS);

    public static void main(String[] args) {
        TIMING_WHEEL.addExpirationListener(new ExpirationListener(){

            @Override
            public void expired(Object expiredObject) {
                System.out.println("expired...");
            }
        });

        TIMING_WHEEL.start();

        CometChannel c1 = new CometChannel();

        TIMING_WHEEL.add(c1);


    }
}
