package com.zqh.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CompletionServiceTest {

    private static final Log LOG = LogFactory.getLog(CompletionServiceTest.class);
    private final CompletionService<Result> completionService;
    private final ExecutorService executorService;

    public CompletionServiceTest(int nThreads) {
        executorService = Executors.newFixedThreadPool(nThreads);
        completionService = new ExecutorCompletionService<Result>(executorService);
        addShutdownHook();
    }

    public CompletionServiceTest(ExecutorService executorService) {
        this.executorService = executorService;
        completionService = new ExecutorCompletionService<Result>(executorService);
        addShutdownHook();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executorService.shutdown();
                LOG.info("Shutdown executor service!");
            }
        });
    }

    public Future<Result> submit(Callable<Result> task) {
        return completionService.submit(task);
    }

    public Future<Result> submit(Runnable task, Result result) {
        return completionService.submit(task, result);
    }

    public Future<Result> poll() {
        return completionService.poll();
    }

    static abstract class Worker {

        protected String name;
        protected Random r;

        public Worker(String name) {
            this.name = name;
            r = new Random();
        }

        public int sleep() {
            int sleep = 1000 * r.nextInt(10);
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sleep;
        }
    }

    static class Caller extends Worker implements Callable<Result> {

        public Caller(String name) {
            super(name);
        }

        public Result call() throws Exception {
            Result result = new Result();
            result.setStatus(Result.OK);
            int sleep = sleep();
            LOG.info("Caller[" + name + "] do something: " + sleep);
            return result;
        }

    }

    static class Runner extends Worker implements Runnable {

        public Runner(String name) {
            super(name);
        }

        public void run() {
            int sleep = sleep();
            LOG.info("Runner[" + name + "] do something: " + sleep);
        }

    }

    static class Result {

        public static final String OK = "OK";
        public static final String ERROR = "ERROR";
        String status;
        final List<Exception> exceptions = new ArrayList<Exception>(0);

        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public List<Exception> getExceptions() {
            return exceptions;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CompletionServiceTest test = new CompletionServiceTest(2);
        for (int i = 0; i < 5; i++) {
            test.submit(new Caller("caller-" + i) );
        }
        Future<Result> f = null;
        while((f = test.poll()) != null) {
            LOG.info(f.get().getStatus());
        }
        LOG.info("FINISHED.");
    }

}