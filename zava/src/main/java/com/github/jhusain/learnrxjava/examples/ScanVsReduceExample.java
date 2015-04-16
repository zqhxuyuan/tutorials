package com.github.jhusain.learnrxjava.examples;

import java.util.ArrayList;

import rx.Observable;

public class ScanVsReduceExample {

    public static void main(String... args) {
        Observable.range(0, 10)
        //reduce是对range里的每2个元素处理. 初始值提供一个ArrayList, 用来存放reduce的结果
        //reduce需要2个元素, 第一个元素是前面处理过的所有元素, 用list保存, 第二个元素是接下来的下一个元素
        .reduce(new ArrayList<>(), (list, i) -> {
            list.add(i);
            //返回list, 这样reduce的结果就是一个list
            return list;
        }).forEach(System.out::println); //foreach循环的是就是reduce的list结果

        System.out.println("... vs ...");

        Observable.range(0, 10)
        .scan(new ArrayList<>(), (list, i) -> {
            list.add(i);
            return list;
        }).forEach(System.out::println);
    }
}
