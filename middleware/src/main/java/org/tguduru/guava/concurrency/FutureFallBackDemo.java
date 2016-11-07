package org.tguduru.guava.concurrency;

import com.google.common.util.concurrent.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Demonstrates the usage of {@link com.google.common.util.concurrent.FutureFallback}, this interface is useful when you
 * want to have a fallback logic for a {@link java.util.concurrent.Future} execution. If anything goes wrong while
 * executing the {@link java.util.concurrent.Future} this fallback logic will be executed.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/30/15
 */
public class FutureFallBackDemo {
    public static void main(final String[] args) throws ExecutionException, InterruptedException {

        final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors
                .newFixedThreadPool(5));
        // this fall back logic returns the default value (1) if atomicInteger if anything goes wrong while computing
        // it. I could write a lambda but wrote an anonymous class to understand what exactly its doing.
        final FutureFallback<Integer> futureFallback = new FutureFallback<Integer>() {
            @Override
            public ListenableFuture<Integer> create(final Throwable t) throws Exception {
                System.err.println("Exception thrown : " + t);
                return executorService.submit(() -> 1);
            }
        };

        final ListenableFuture<Integer> failFuture = executorService.submit(() -> {
            throw new RuntimeException("Failed");
        });

        final ListenableFuture<Integer> listenableFallbackFuture = Futures.withFallback(failFuture, futureFallback,
                executorService);
        System.out.println("Returns default value (1) : " + listenableFallbackFuture.get());
        executorService.shutdown();
    }
}
