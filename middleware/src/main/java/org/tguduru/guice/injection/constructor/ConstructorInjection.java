package org.tguduru.guice.injection.constructor;

import com.google.inject.Inject;

import org.tguduru.guice.service.Log;

/**
 * Demonstrate the Constructor Injection of the guice.
 * @author Guduru, Thirupathi Reddy
 */
public class ConstructorInjection {
    private Log log;

    @Inject
    public ConstructorInjection(final Log log) {
        this.log = log;
    }

    public void doSomeWork() {
        log.log();
    }
}
