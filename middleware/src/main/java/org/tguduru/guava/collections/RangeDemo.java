package org.tguduru.guava.collections;

import com.google.common.collect.Range;

/**
 * Demonstrates the usage of {@link com.google.common.collect.Range}.
 * @author Guduru, Thirupathi Reddy
 * @modified 11/19/15
 */
public class RangeDemo {
    public static void main(final String[] args) {
        final Range<Integer> inclusiveRange = Range.closed(1, 5); // which is a inclusive range includes end values as
                                                                  // well like
        // 1 & 5.
        System.out.println(inclusiveRange);

        final Range<Integer> exclusiveRange = Range.open(1, 5);// doesn't include 1 & 5.
        System.out.println(exclusiveRange);
    }
}
