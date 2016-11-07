package org.tguduru.guice.provider;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Demonstrates the use of {@link com.google.inject.Provider}
 * @author Guduru, Thirupathi Reddy
 */
public class ProviderDemo {

    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new ProviderModule());
        final UseProvider useProvider = injector.getInstance(UseProvider.class);
        useProvider.doProviderWork();
    }
}
