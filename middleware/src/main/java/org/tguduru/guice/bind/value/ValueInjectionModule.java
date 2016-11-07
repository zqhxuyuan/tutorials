package org.tguduru.guice.bind.value;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * A guice module defines injection for value objects.
 * @author Guduru, Thirupathi Reddy
 */
public class ValueInjectionModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        //toInstance method injects the value of the requested injection, this helps for classes which has default constructor only.
        bind(String.class).annotatedWith(Names.named("value")).toInstance("Value Injection");
    }
}
