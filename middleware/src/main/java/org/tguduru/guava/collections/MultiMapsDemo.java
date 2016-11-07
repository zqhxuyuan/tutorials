package org.tguduru.guava.collections;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Demonstrates the usage of {@link com.google.common.collect.Multimap}.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/19/15
 */
public class MultiMapsDemo {
    public static void main(final String[] args) {
        final Multimap<Integer, String> multimap = HashMultimap.create();
        multimap.put(1, "KS");
        multimap.put(1, "MO"); // this will add the value to the same key and forms a 1 -> [KS,MO]
        System.out.println(multimap);
        System.out.println(multimap.size());// but size is 2 since HashMultiMap allows duplicates.

        System.out.println(multimap.get(1)); // will return all values of key 1.

        // There are lot of implementation of MultiMap for various use cases.

    }
}
