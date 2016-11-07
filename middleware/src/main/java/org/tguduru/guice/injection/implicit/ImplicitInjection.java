package org.tguduru.guice.injection.implicit;

import com.google.inject.Inject;

import org.tguduru.guice.service.Log;

/**
 * Demonstrates implicit injection.
 * @author Guduru, Thirupathi Reddy
 */
public class ImplicitInjection {
    @Inject
    // this is implicitly injected with LogService as that implemented as the default injection in Log interface.
    private Log log;

    public void doWork() {
        log.log();
    }
}
