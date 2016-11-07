package org.tguduru.guice.bind.multi;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tguduru.guice.service.Log;

/**
 * Constructor injection with {@link org.tguduru.guice.service.impl.LogService} implementation
 * @author Guduru, Thirupathi Reddy
 */
public class LoggingInjection {
    private Log log;

    @Inject
    public LoggingInjection(@Named("logging")final Log log) {
        this.log = log;
    }

    public void doWork(){
        log.log();
    }
}
