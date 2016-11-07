package org.tguduru.guava.concurrency;

import com.google.common.util.concurrent.*;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates the usage of {@link com.google.common.util.concurrent.FutureCallback}. This class will be used for call
 * back function when something happened with the {@link java.util.concurrent.Future}.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/30/15
 */
public class FutureCallBackDemo {
    public static void main(final String[] args) {
        final AtomicInteger atomicInteger = new AtomicInteger();
        final FutureCallback<Integer> futureCallback = new FutureCallback<Integer>() {
            @Override
            public void onSuccess(final Integer result) {
                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " - Result : " + result);
            }

            @Override
            public void onFailure(final Throwable t) {
                System.err.println(Thread.currentThread().getName() + " - Failed Processing : " + t);
            }
        };

        final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
                .newCachedThreadPool());
        final ListenableFuture<Integer> listenableFuture = executorService
                .submit(() -> atomicInteger.incrementAndGet());
        final ListenableFuture<Integer> throwsExceptionFuture = executorService.submit(() -> {
            throw new RuntimeException("Exception happened");
        });
        Futures.addCallback(listenableFuture, futureCallback, executorService); // this one registers the call back.

        // Failure case
        Futures.addCallback(throwsExceptionFuture, futureCallback, executorService);
        System.out.println("main thread - Done"); // since its doing its processing asynchronously this statement executes first
                                    // before all Futures... Those futures will execute in different threads

        System.out.println(Thread.currentThread().getState());
        executorService.shutdown();
    }
}
