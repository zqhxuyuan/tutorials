package org.tguduru.guava.validation;

import com.google.common.base.Preconditions;

/**
 * Demonstrates the usage of guava's {@link com.google.common.base.Preconditions} for common data validation.
 * @author Guduru, Thirupathi Reddy
 */
public class PreconditionsDemo {
    public static void main(final String[] args) {
        final int value = -1; //
        try {
            Preconditions.checkArgument(value > 0, "value is negative");// this one validates the given value should be
            // positive.
        } catch (final IllegalArgumentException e) {
            System.out.println(e);
        }
    }
}
