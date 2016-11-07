package org.tguduru.guice.injection.field;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class FieldInjectionDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new FieldInjectionModule());
        final FieldInjection fieldInjection = injector.getInstance(FieldInjection.class);
        fieldInjection.doSomeWork();
    }
}
