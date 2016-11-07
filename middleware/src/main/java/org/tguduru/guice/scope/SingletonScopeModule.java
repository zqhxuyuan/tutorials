package org.tguduru.guice.scope;

import com.google.inject.AbstractModule;

import org.tguduru.guice.service.Log;

/**
 * Module defines the scope of the instance created using the guice framework.
 * @author Guduru, Thirupathi Reddy
 */
public class SingletonScopeModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(Log.class).to(SingletonLogService.class);
    }
}
