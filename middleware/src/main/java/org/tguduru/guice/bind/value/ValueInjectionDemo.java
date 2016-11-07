package org.tguduru.guice.bind.value;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class ValueInjectionDemo {
    public static void main(final String[] args) {
         final Injector injector = Guice.createInjector(new ValueInjectionModule());
         final ValueInjection valueInjection = injector.getInstance(ValueInjection.class);
        valueInjection.doWork();
    }
}
