package org.tguduru.guava.collections;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Demonstrates the usage of {@link com.google.common.collect.Lists}
 * @author Guduru, Thirupathi Reddy
 */
public class ListsDemo {
    public static void main(String[] args) {
        List<Integer> integers = Lists.newArrayList(1, 2, 3, 4, 5, 6);

        // list transformations
        Lists.transform(integers, new Function<Integer, Integer>() {
            @Override
            public Integer apply(final Integer input) {
                return input * 2;
            }
        }).forEach(System.out::println);

        // list partition, this one divides the list into the specified number of element size lists
        List<List<Integer>> lists = Lists.partition(integers, 2);
        lists.forEach(a -> a.forEach(System.out::print));
    }
}
