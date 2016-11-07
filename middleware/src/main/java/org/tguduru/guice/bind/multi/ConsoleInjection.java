package org.tguduru.guice.bind.multi;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tguduru.guice.service.Log;

/**
 * Constructor injection with {@link ConsoleInjection}
 * @author Guduru, Thirupathi Reddy
 */
public class ConsoleInjection {
    private Log log;

    @Inject
    public ConsoleInjection(@Named("console") final Log log) {
        this.log = log;
    }

    public void doWork(){
        log.log();
    }
}
