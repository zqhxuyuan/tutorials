package org.tguduru.guice.bind.props;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Guduru, Thirupathi Reddy
 */
public class ConfigInjectDemo {
    public static void main(final String[] args) {
        final Injector injector = Guice.createInjector(new ConfigurationModule());
        final ConfigInjection configInjection = injector.getInstance(ConfigInjection.class);
        configInjection.doWork();
    }
}
