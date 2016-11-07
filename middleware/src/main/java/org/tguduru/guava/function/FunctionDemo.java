package org.tguduru.guava.function;

import com.google.common.base.Function;

/**
 * Demonstrates the usage of {@link Function}. Usually {@link Function} are useful when you want to do data
 * transformations from one state to another.
 * @author Guduru, Thirupathi Reddy
 */
public class FunctionDemo {
    public static void main(final String[] args) {
        final String s = "string for length";
        System.out.println(new StringLengthFunction().apply(s));

        // here is the another function with anonymous class
        final Function<String, String> combineFunction = new Function<String, String>() {
            @Override
            public String apply(final String input) {
                return input + " - " + input;
            }
        };
        System.out.println(combineFunction.apply(s));

        // Java 8 has Function as well, so we can pretty much achieve same thing without guava. see below
        System.out.println("***  Java 8 Function ****");
        final java.util.function.Function<String, Long> stringLengthFunction = str -> Long.valueOf(str.length());
        System.out.println(stringLengthFunction.apply(s));

    }
}
