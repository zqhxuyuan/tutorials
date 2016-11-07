package org.tguduru.guice.injection.method;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import org.tguduru.guice.service.Hello;
import org.tguduru.guice.service.Log;
import org.tguduru.guice.service.impl.HelloService;
import org.tguduru.guice.service.impl.LogService;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class MethodInjectionDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, module -> {
            module.bind(Log.class).to(LogService.class);
            module.bind(Hello.class).to(HelloService.class);
        });
        // as soon as getting an instance the method which is annotated with @Inject will gets called. so method injection is for use
        // cases like object construction with additional external resource when you want to use inside other additional
        // methods in the instantiated class. Do not use method injections for heavy business logic methods.
        final MethodInjection methodInjection = injector.getInstance(MethodInjection.class);
        methodInjection.doSomeWork();
    }
}
