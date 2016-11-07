package org.tguduru.guice;

import com.google.inject.AbstractModule;

import org.tguduru.guice.service.Hello;
import org.tguduru.guice.service.Log;
import org.tguduru.guice.service.impl.HelloService;
import org.tguduru.guice.service.impl.LogService;

/**
 * Guice module
 * @author Guduru, Thirupathi Reddy
 * Modified on : Jul 27, 2014 10:00:24 PM
 */
public class Module extends AbstractModule {

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(Hello.class).to(HelloService.class);
        bind(Log.class).to(LogService.class);
    }

}
