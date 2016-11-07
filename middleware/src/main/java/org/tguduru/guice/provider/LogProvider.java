package org.tguduru.guice.provider;

import com.google.inject.Provider;

import org.tguduru.guice.service.Log;
import org.tguduru.guice.service.impl.LogService;

/**
 * Provider for {@link org.tguduru.guice.service.Log}. A {@link com.google.inject.Provider} is a lazy creation of
 * instance for injecting while guice is asking for it. Some use cases need not be instantiated all instances in advance
 * as only when needed and/or sometimes we may need to create multiple instances of a given class.
 * This will help the guice container startup very quick and helps prevent from errors when instantiating some classes
 * which are not needed for normal execution flows.
 *
 * During injection we will inject the provider instead of the actual implementation.
 * <p>
 * This is kind of factory pattern for guice injection.
 * </p>
 * @author Guduru, Thirupathi Reddy
 */

public class LogProvider implements Provider<Log> {
    /**
     * Provides an instance of {@code Log}. Must never return {@code null}.
     * @throws com.google.inject.OutOfScopeException when an attempt is made to access a scoped object while the scope
     *             in question is not currently active
     * @throws com.google.inject.ProvisionException if an instance cannot be provided. Such exceptions include messages
     *             and throwables to describe why provision failed.
     */
    @Override
    public Log get() {
        return new LogService();
    }
}
