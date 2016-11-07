package org.tguduru.guice.injection.constructor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.tguduru.guice.service.Log;
import org.tguduru.guice.service.impl.LogService;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class ConstructorInjectionDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(module -> {
            module.bind(Log.class).to(LogService.class);
        });
        final ConstructorInjection constructorInjection = injector.getInstance(ConstructorInjection.class);
        constructorInjection.doSomeWork();
    }
}
