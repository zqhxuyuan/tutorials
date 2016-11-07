package org.tguduru.guice.injection.staticinject;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Module defines configuration for static injection.
 * @author Guduru, Thirupathi Reddy
 */
public class StaticInjectionModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("value")).to("Static Value");
        requestStaticInjection(Utils.class); // without this guice module won't prepare the static classes for injection
                                             // so if we remove this line you will see a null value for Named parameter.
    }
}
