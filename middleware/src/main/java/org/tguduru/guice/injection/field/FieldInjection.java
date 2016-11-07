package org.tguduru.guice.injection.field;

import com.google.inject.Inject;

import org.tguduru.guice.service.Log;

/**
 * Demonstrates the Field Injection using guice
 * @author Guduru, Thirupathi Reddy
 */
public class FieldInjection {

    // this is field injection. This will be injected whenever FieldInjection instantiated. In order work with guice you
    // need to bind the implementation of Log in the Guice Module. If you don't then you will see an error saying no
    // implementation found in the Guice container.
    // Look at FieldInjectionModule for Module implementation.
    @Inject
    private Log log;

    public void doSomeWork() {
        log.log();
    }
}
