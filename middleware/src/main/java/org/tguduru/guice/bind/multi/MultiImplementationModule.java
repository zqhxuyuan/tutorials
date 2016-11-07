package org.tguduru.guice.bind.multi;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.tguduru.guice.service.Log;
import org.tguduru.guice.service.impl.LogService;
import org.tguduru.guice.service.impl.ConsoleLogService;

/**
 * Demonstrates the multi implementation of the given interface injection using {@link com.google.inject.name.Named}.
 * @author Guduru, Thirupathi Reddy
 */
public class MultiImplementationModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(Log.class).annotatedWith(Names.named("logging")).to(LogService.class);
        bind(Log.class).annotatedWith(Names.named("console")).to(ConsoleLogService.class);
    }
}
