package org.tguduru.guice.service;

import com.google.inject.Inject;

/**
 * @author Guduru, Thirupathi Reddy
 * Modified on : Jul 27, 2014 10:35:29 PM
 */
public class ProcessingService {
    Hello hello;
    Log log;

    /**
	 * Constructor Injection
	 */
    @Inject
    public ProcessingService(final Hello hello, final Log log) {
        this.hello = hello;
        this.log = log;
    }

    public void processMessage() {
        hello.sayHello();
        log.log();
    }
}
