package org.tguduru.guava.collections;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Demonstrates the usage and {@link FluentIterable} and compares between java 8 {@link java.util.stream.Stream}.
 * @author Guduru, Thirupathi Reddy
 */
public class FluentIterableDemo {
    public static void main(String[] args) {
        List<Integer> integers = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        // FluentFilter
        System.out.println("**** guava filter ****");
        FluentIterable.from(integers).filter(new Predicate<Integer>() {
            @Override
            public boolean apply(final Integer input) {
                return input % 2 == 0;
            }
        }).forEach(System.out::println);

        // java 8
        System.out.println("**** java 8 filter ****");
        integers.stream().filter(a -> a % 2 == 0).forEach(System.out::println);

        // guava transform iterator
        System.out.println("**** guava transform ****");
        FluentIterable.from(integers).transform(new Function<Integer, Integer>() {
            @Override
            public Integer apply(final Integer input) {
                return input * 2;
            }
        }).forEach(System.out::println);

        // java 8 transform (map) for iterators.
        System.out.println("**** Java 8 transformations ****");
        integers.stream().map(a -> a * 2).forEach(System.out::println);

        // like these there are so many useful method in the FluentIterable class, and those same functions are
        // available in Java 8 as well, so if you are using Java 8 then you don't need guava for Iterable processing.

    }
}
