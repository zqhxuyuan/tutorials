package org.tguduru.guice.bind.multi;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class MultiImplementationDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new MultiImplementationModule());
        final LoggingInjection logging = injector.getInstance(LoggingInjection.class);
        final ConsoleInjection consoleInjection = injector.getInstance(ConsoleInjection.class);
        logging.doWork();
        consoleInjection.doWork();
    }
}
