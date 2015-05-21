package com.zqh.concurrent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScheduledExecutorServiceTest {

    private static final Log LOG = LogFactory.getLog(ScheduledExecutorServiceTest.class);
    private final ScheduledExecutorService executorService;

    public ScheduledExecutorServiceTest(int corePoolSize) {
        executorService = Executors.newScheduledThreadPool(corePoolSize);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executorService.shutdown();
                LOG.info("Shutdown executor service!");
            }
        });
    }

    static class MyTimerTask implements Runnable {

        private static volatile int index = 0;
        private String name;
        private static final String format = "yyyy-MM-dd HH:mm:ss.SSS";

        public MyTimerTask(String name) {
            this.name = name;
        }

        public void run() {
            final DateFormat DF = new SimpleDateFormat(format);
            String datetime = DF.format(new Date());
            LOG.info("Start my timer task: " +
                    "[id=" + (++index) + ", name=" + name + ", datetime=" + datetime + "]");
            try {
                if(index % 2 == 0) {
                    throw new RuntimeException("Simulate to throw a exception.");
                }
            } catch (Exception e) {
                LOG.info("A exception is caught by me, lol!");
            }
        }

    }

    public void scheduleWithFixedDelay(Runnable task) {
        executorService.scheduleWithFixedDelay(task, 1, 10, TimeUnit.SECONDS);
    }

    public void scheduleAtFixedRate(Runnable task) {
        executorService.scheduleAtFixedRate(task, 5 * 1000, 2 * 1000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        ScheduledExecutorServiceTest scheduler = new ScheduledExecutorServiceTest(1);
        scheduler.scheduleWithFixedDelay(new MyTimerTask("bot-1"));

        scheduler.scheduleAtFixedRate(new MyTimerTask("bot-2"));
    }
}