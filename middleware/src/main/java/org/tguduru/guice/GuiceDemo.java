/**
 *
 */
package org.tguduru.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.google.inject.Stage;
import org.tguduru.guice.Module;
import org.tguduru.guice.service.ProcessingService;

/**
 * @author Guduru, Thirupathi Reddy
 * Modified on : Jul 27, 2014
 */
public class GuiceDemo {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,new Module());
        final ProcessingService service = injector.getInstance(ProcessingService.class);
        service.processMessage();
    }

}
