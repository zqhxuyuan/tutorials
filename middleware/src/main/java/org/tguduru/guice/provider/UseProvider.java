package org.tguduru.guice.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.tguduru.guice.service.Log;

/**
 * Demonstrates the injection of {@link Provider}
 * @author Guduru, Thirupathi Reddy
 */
public class UseProvider {
    @Inject // Here injecting provider instead of the actual implementation.
    private Provider<Log> logProvider;

    public void doProviderWork() {
        logProvider.get().log();
    }
}
