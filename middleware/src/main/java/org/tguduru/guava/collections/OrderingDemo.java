package org.tguduru.guava.collections;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;

/**
 * Demonstrates the usage of {@link com.google.common.collect.Ordering}. It will help construct the Collections using
 * provided order or {@link java.util.Comparator}.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/19/15
 */
public class OrderingDemo {
    public static void main(final String[] args) {
        final Ordering<String> naturalOrdering = Ordering.natural();
        final List<String> states = Lists.newArrayList("KS", "MO", "OH", "NY", "SC");
        System.out.println(naturalOrdering.immutableSortedCopy(states)); // this prints the same order as the order is
                                                                         // natural.
        final Ordering<Object> arbitraryOrdering = Ordering.arbitrary();
        System.out.println(arbitraryOrdering.immutableSortedCopy(states));// thia one order arbitrarily.

        final Ordering<String> customOrdering = Ordering.from(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.hashCode() > o2.hashCode() == true ? 1 : 0;
            }
        });
        System.out.println(customOrdering.immutableSortedCopy(states)); // this prints the order by using customOrdering.
    }
}
