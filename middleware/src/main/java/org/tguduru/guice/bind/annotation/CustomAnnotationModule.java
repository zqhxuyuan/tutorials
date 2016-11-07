package org.tguduru.guice.bind.annotation;

import com.google.inject.AbstractModule;

import org.tguduru.guice.service.Log;
import org.tguduru.guice.service.impl.LogService;
import org.tguduru.guice.service.impl.ConsoleLogService;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class CustomAnnotationModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        //here using the custom annotation instead of @Named.
        bind(Log.class).annotatedWith(LogServiceInject.class).to(LogService.class);
        bind(Log.class).annotatedWith(ConsoleServiceInject.class).to(ConsoleLogService.class);
    }
}
