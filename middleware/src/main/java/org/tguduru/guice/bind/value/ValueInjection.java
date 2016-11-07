package org.tguduru.guice.bind.value;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Demonstrates the value injection using Guice.
 * @author Guduru, Thirupathi Reddy
 */
public class ValueInjection {
    private String value;

    @Inject
    public ValueInjection(@Named("value") final String value) {
        this.value = value;
    }

    public void doWork(){
        System.out.println(value);
    }

}
