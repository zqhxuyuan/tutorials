package com.zqh.guava;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhengqh on 15/11/18.
 */
public class MultiHashMap {

    public static void main(String[] args) {
        Multimap<String, String> multimap = HashMultimap.create();

        multimap.put("a", "a");
        multimap.put("a", "b");

        Collection<String> values = multimap.get("a");
        for(String str : values){
            System.out.println(str);
        }
    }
}
