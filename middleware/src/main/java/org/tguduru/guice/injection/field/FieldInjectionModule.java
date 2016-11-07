package org.tguduru.guice.injection.field;

import com.google.inject.AbstractModule;

import org.tguduru.guice.service.Log;
import org.tguduru.guice.service.impl.LogService;

/**
 * Module for field injection in guice.
 * @author Guduru, Thirupathi Reddy
 */
public class FieldInjectionModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(Log.class).to(LogService.class); // this is for interface binding to implementation.
        // if you want only implementation as field injection, you just bind the implementation as below.
        // bind(LogService.class);
    }
}
