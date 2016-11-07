package org.tguduru.guava.collections;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Demonstrates the methods of {@link com.google.common.collect.Sets}
 * @author Guduru, Thirupathi Reddy
 * @modified 11/19/15
 */
public class SetsDemo {
    public static void main(String[] args) {
        Set<String> set1 = Sets.newHashSet("KS","MO");
        Set<String> set2 = Sets.newHashSet("KS","NY");

        //find out sets differences from first one to second one.
        System.out.println(Sets.difference(set1,set2));

        //symmetric difference means find out elements not part of both sets
        System.out.println(Sets.symmetricDifference(set1,set2));

        //intersection
        System.out.println(Sets.intersection(set1,set2));

        //union
        System.out.println(Sets.union(set1,set2));
    }
}
