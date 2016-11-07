package org.tguduru.guava.collections;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Demonstrates the usage of {@link com.google.common.collect.BiMap}, BiMap guarantees the uniqueness of the key & value
 * as well,usually Maps do not have this property.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/19/15
 */
public class BiMapDemo {
    public static void main(final String[] args) {
        final BiMap<Integer, String> states = HashBiMap.create();
        states.put(1, "KS");
        states.put(2, "KS"); // this will thrown an error saying value already exists.

        // There are other implementation as well for BiMap interface, use appropriate one for given use case.
    }
}
