package org.tguduru.guice.bind.annotation;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class CustomAnnotationDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new CustomAnnotationModule());
        final CustomAnnotationInjection customAnnotationInjection = injector
                .getInstance(CustomAnnotationInjection.class);
        customAnnotationInjection.doWork();
    }
}
