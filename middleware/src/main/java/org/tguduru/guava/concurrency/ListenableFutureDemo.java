package org.tguduru.guava.concurrency;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates the use of {@link com.google.common.util.concurrent.ListenableFuture}
 * @author Guduru, Thirupathi Reddy
 * @modified 11/24/15
 */
public class ListenableFutureDemo {
    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        final AtomicInteger atomicInteger = new AtomicInteger();
        final ExecutorService executorService = Executors.newCachedThreadPool();
        final Future<Integer> integerFuture = executorService.submit(atomicInteger::incrementAndGet);
        // this blocks the call and we need to call get() to compute the thread and return the result.
        System.out.println(integerFuture.get());

        // To avoid the above issue there is a class in Guava;s library as ListenableFuture which register a listener
        // and calls once the computation is one.

        // Here we are wrapping the executor service for callback functions.
        final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

        // getting a ListenableFuture from the ListeningExecutorService.
        final ListenableFuture<Integer> integerListenableFuture = listeningExecutorService.submit(atomicInteger::incrementAndGet);

        // registering a callback using Runnable. In this case we are not actually calling the Future.get() to block the
        // call. This is good for callbacks like they don't need to worry about the success or failure of the
        // computation. If we want to call different methods based on computation there is another interface called
        // FutureCallback will be able to help with that.
        integerListenableFuture.addListener(() -> System.out.println(atomicInteger.get()), listeningExecutorService);
        executorService.shutdown();
    }
}
