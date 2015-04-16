package com.github.jhusain.learnrxjava.examples;
import java.util.concurrent.TimeUnit;

import rx.Observable;

//每隔一段时间,操作Zip中的一对元素
public class ZipInterval {

    public static void main(String... args) {
        Observable<String> data = Observable.just("one", "two", "three", "four", "five");

        //间隔一秒/2秒, 返回值都是从0开始自增1
        //Zip的前2个参数是2个数据源
        Observable.zip(data, Observable.interval(2, TimeUnit.SECONDS), (d, t) -> {
            return d + " " + t;
        }).toBlocking().forEach(System.out::println);
        
    }
}
