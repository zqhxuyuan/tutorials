package org.tguduru.guice.bind.annotation;

import com.google.inject.Inject;

import org.tguduru.guice.service.Log;

/**
 * Demonstrates the usage custom annotation injection instead of @Named, here using multiple implementation of
 * {@link Log} using custom annotations.
 * @author Guduru, Thirupathi Reddy
 */
public class CustomAnnotationInjection {
    @LogServiceInject
    @Inject
    private Log log;

    @ConsoleServiceInject
    @Inject
    private Log sysoutLog;

    public void doWork() {
        log.log();
        sysoutLog.log();
    }
}
