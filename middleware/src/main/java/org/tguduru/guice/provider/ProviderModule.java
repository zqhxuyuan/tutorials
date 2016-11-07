package org.tguduru.guice.provider;

import com.google.inject.AbstractModule;

import org.tguduru.guice.service.Log;

/**
 * A module injects {@link com.google.inject.Provider}
 * @author Guduru, Thirupathi Reddy
 */
public class ProviderModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(Log.class).toProvider(LogProvider.class);
        bind(UseProvider.class);
    }
}
