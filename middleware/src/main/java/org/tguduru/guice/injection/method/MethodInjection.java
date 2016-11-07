package org.tguduru.guice.injection.method;

import com.google.inject.Inject;

import org.tguduru.guice.service.Hello;
import org.tguduru.guice.service.Log;

/**
 * Demonstrates the method injection in Guice
 * @author Guduru, Thirupathi Reddy
 */
public class MethodInjection {
    private Log log;
    private Hello hello;

    @Inject
    public void inject(final Log log) {
        this.log = log;
    }

    @Inject
    public void injectHello(final Hello hello){
        this.hello = hello;
    }

    public void doSomeWork() {
        log.log();
        hello.sayHello();
    }
}
