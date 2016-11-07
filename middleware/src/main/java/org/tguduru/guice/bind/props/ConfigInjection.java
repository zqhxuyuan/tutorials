package org.tguduru.guice.bind.props;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Injecting properties as {@link Named}
 * @author Guduru, Thirupathi Reddy
 */
public class ConfigInjection {
    @Inject
    @Named("city")
    private String city;
    @Inject
    @Named("state")
    private String state;
    @Inject
    @Named("country")
    private String country;

    public void doWork() {
        System.out.println("Config Values: "+ city +", " + state+", " + country);
    }
}
