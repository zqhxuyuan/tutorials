package org.tguduru.guice.injection.staticinject;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * A simple utility class provides all static methods, some of the methods can be injected with parameters.
 * @author Guduru, Thirupathi Reddy
 */
public class Utils {
    private static String val;

    // Here demonstrating the injection in static methods, since this is static injection this method gets called as
    // soon as guice created the injector container. In this example you can see the sout for twice when explicitly
    // calling doSomeWork() once and static injection once.
    @Inject
    public static void doUtility(@Named("value") final String value) {
        val = value;
        System.out.println(val);
    }

    public static void doSomeWork() {
        System.out.println(val);
    }
}
