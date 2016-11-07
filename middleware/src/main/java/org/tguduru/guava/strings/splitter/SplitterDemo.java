package org.tguduru.guava.strings.splitter;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Demonstrates the use of guava's {@link com.google.common.base.Splitter}. Splitter allows to split the strings based
 * on given separator, the same job can be done by Java's {@link java.util.StringTokenizer} as well but it has some
 * limitations like allowing empty strings, remove spaces etc...
 * @author Guduru, Thirupathi Reddy
 */
public class SplitterDemo {
    public static void main(final String[] args) {
        final String s = "im,going, to, split,, this, string, using, comma,";
        Splitter.on(',').split(s).forEach(System.out::println); // this prints the split values, doesn't
        // omit empty strings
        System.out.println("Splitter with omit empty strings");
        Splitter.on(',').omitEmptyStrings().split(s).forEach(System.out::println);

        // remove trailing/leading spaces.
        Splitter.on(',').trimResults().split(s).forEach(System.out::println);

        /*
         * match any separator char given in Splitter and then split the string, this CharMatcher.anyOf() can be used
         * for any alphabets as well.
         * CharMatcher has lot of other methods as well for different use cases.
         */
        final String multiSeparatorString = "this, string; contains. multiple: separators/ to' split";
        Splitter.on(CharMatcher.anyOf(",.';:/")).trimResults().split(multiSeparatorString).forEach(System.out::println);

        // splits only a given length and then return the entire string as a last split.
        System.out.println("****   split up to given length   ****");
        final String string = "producing,only , a , few , strings, from, here";
        Splitter.on(',').trimResults().limit(3).split(string).forEach(System.out::println);

        //Splitter also has a MapSplitter which splits a given string into a map
        final String stringMap = "Apple=iPhone;Google=Nexus;Samsung=Galaxy";
        final Map<String,String> map = Splitter.on(';').withKeyValueSeparator('=').split(stringMap);
        map.entrySet().forEach(System.out::println);

    }
}
