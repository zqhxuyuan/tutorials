package org.tguduru.guice.scope;

import com.google.inject.Singleton;

import org.tguduru.guice.service.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton implementation of {@link Log} which will evaluate the {@link com.google.inject.Singleton} of guice.
 * @author Guduru, Thirupathi Reddy
 */
@Singleton
// this annotation makes this class as singleton and will be instantiated only once.
public class SingletonLogService implements Log {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public SingletonLogService() {
        atomicInteger.incrementAndGet();
    }

    @Override
    public void log() {
        System.out.println("atomic value: " + atomicInteger.get()); // this should print only 1 as this class will be
                                                                    // instantiated only once.
    }
}
