package org.tguduru.guice.bind.props;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A guice module to read the properties/configuration and injects them as {@link com.google.inject.name.Named}.
 * @author Guduru, Thirupathi Reddy
 */
public class ConfigurationModule extends AbstractModule {
    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        // This one binds the properties as named injections. This one injects using the key of the property, so you can
        // inject the key of the property as @Named. This injection can be usable when you want to inject configuration.
        Names.bindProperties(binder(), getConfiguration());
    }

    private Properties getConfiguration() {
        final Properties properties = new Properties();
        try {
            final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("configuration.properties");
            properties.load(inputStream);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
