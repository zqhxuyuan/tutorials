package org.tguduru.guice.scope;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.tguduru.guice.service.Log;

/**
 * Demonstrates the usage of {@link com.google.inject.Singleton} in guice.
 * @author Guduru, Thirupathi Reddy
 */
public class SingletonDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new SingletonScopeModule());
        final Log log1 = injector.getInstance(SingletonLogService.class);
        log1.log();
        final Log log2 = injector.getInstance(SingletonLogService.class);
        log2.log();
    }
}
