package org.tguduru.guice.injection.implicit;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Demonstrates the default injection of {@link org.tguduru.guice.service.Log} implementation. See the
 * {@link org.tguduru.guice.service.Log} and {@link ImplicitInjection} for detailed explanation.
 * @author Guduru, Thirupathi Reddy
 */
public class ImplicitInjectionDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector();
        // We don't have any guice module configured for the ImplicitInjection as the interface injected is defined with
        // default injection type with @ImplementedBy annotation of LogService. So whenever the guice creates an
        // injection container it will inject LogService for Log interface as its defined as default.
        final ImplicitInjection implicitInjection = injector.getInstance(ImplicitInjection.class);
        implicitInjection.doWork();
    }
}
