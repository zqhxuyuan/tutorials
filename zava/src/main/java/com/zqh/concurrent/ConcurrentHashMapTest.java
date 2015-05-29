package com.zqh.concurrent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zqhxuyuan on 15-5-25.
 */
public class ConcurrentHashMapTest {

    public static void main(String[] args) {
        ConcurrentHashMap map = new ConcurrentHashMap();

        Set<String> set1 = new HashSet<>();
        set1.add("id");
        set1.add("gender");

        Set<String> set2 = new HashSet<>();
        set2.add("id");
        set2.add("age");

        set1.retainAll(set2);

        for(String s : set1){
            System.out.println(s);
        }

        System.out.println(1/3);
        System.out.println(4%3);
    }
}
