package org.tguduru.guava.strings.joiner;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Demonstrates the usage of {@link com.google.common.base.Joiner} in guava. And this also has the implementation
 * comparison between Java 8 & Guava which explains in which cases we need guava.
 * @author Guduru, Thirupathi Reddy
 */
public class JoinerDemo {
    public static void main(final String[] args) {
        final Joiner joiner = Joiner.on(",").skipNulls(); // this adds a comma after every value and skips the null
                                                          // values.
        final List<String> strings = Lists.newArrayList("ks", "mo", "oh", "ny");
        System.out.println(joiner.join(strings));
        // Java 8 implementation, so in java 8 we don't need guava to do joiners
        System.out.println(strings.stream().collect(Collectors.joining(",")));

        // adds default value for nulls using Joiner, for this use case unfortunately JDK 8 doesn't have any functions
        // so we need to use guava.
        final Joiner defaultJoiner = Joiner.on(",").useForNull("[no value]");
        System.out.println(defaultJoiner.join("NY", "OH", null, "SC"));

        // joins the given string and append into StringBuilder
        final StringBuilder stringBuilder = new StringBuilder();
        joiner.appendTo(stringBuilder, "ks", "mo", "oh", "ny");
        System.out.println(stringBuilder.toString());

        final Map<String, String> stringMap = Maps.newHashMap();
        stringMap.put("Apple", "iPhone");
        stringMap.put("Google", "Nexus");
        stringMap.put("Samsung", "Galaxy");
        // prints a string separated by # with key=value from the stringMap, JDK8 doesn't have this so we need guava in
        // these use cases.
        System.out.println(Joiner.on("#").withKeyValueSeparator("=").join(stringMap));
    }
}
