package org.tguduru.guice.injection.staticinject;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author Guduru, Thirupathi Reddy
 */
public class StaticInjectionDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new StaticInjectionModule());
        Utils.doSomeWork();
    }
}
