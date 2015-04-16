package com.github.jhusain.learnrxjava.examples;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class HelloWorld {

    public static void main(String[] args) {
        // expand to show full classes 最原始的版本, 采用匿名内部类
        // publisher and subscriber. subscriber subscribe specify events,
        // when publisher send msg to a event, then the event's subscribers will get the msg.
        // To the RxJava world. we hide the publisher.
        // when create Observable, we means create a observer on the event 在事件上创建一个观察者
        // the create method's parameter is OnSubscribe means subscribe on the event 订阅某个事件
        // so the call method parameter is Subscribe object means we have an subscriber on this event
        // but where is the subscriber? there are in subscribe method which is a anonymous inner class
        // so now, we have a Subscriber, and an event, the the onSubscribe calling method
        Observable.create(new OnSubscribe<String>() {
            //可以认为call是publish,会往subscribe发送消息
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello World!");
                subscriber.onCompleted();
            }
        }).subscribe(new Subscriber<String>() {  // Subscriber
            @Override
            public void onCompleted() {
                System.out.println("Done");
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onNext(String t) {
                System.out.println(t);
            }
        });

        // Hello World. function style, lambda version
        Observable.create(subscriber -> {
            //回调
            subscriber.onNext("Hello World!");
            subscriber.onCompleted();
        }).subscribe(System.out::println); //订阅到消息后的处理方式

        // shorten by using helper method
        Observable.just("Hello", "World!") //回调中只有一个onNext方法时,可以直接省略
                .subscribe(System.out::println);  //只有一个参数时,是onNext

        // add onError and onComplete listeners
        Observable.just("Hello World!")
                .subscribe(System.out::println,   //onNext
                        Throwable::printStackTrace, //onError
                        () -> System.out.println("Done")); //onComplete

        // add error propagation 添加抛出异常的处理逻辑
        Observable.create(subscriber -> {
            try {
                subscriber.onNext("Hello World!");
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribe(System.out::println);

        // add concurrency (manually) 手动加入并发/多线程, 实际上是参见一个新的线程
        Observable.create(subscriber -> {
            //因为Runnable接口的run方法没有参数, 所以参数用()表示, 对应的Action是Action0
            new Thread(() -> {
                try {
                    subscriber.onNext(getData());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }).start();
        }).subscribe(System.out::println);

        // add concurrency (using a Scheduler) 使用调度器引入并发/多线程
        Observable.create(subscriber -> {
            try {
                subscriber.onNext(getData());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())  // 并发!
                .subscribe(System.out::println);

        // add operator
        Observable.create(subscriber -> {
            try {
                subscriber.onNext(getData());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                // 添加一个操作算子:Map映射
                // 参数data是回调方法里的getData()的返回值, map的计算结果会传给subscribe中的sout
                .map(data -> data + " --> at " + System.currentTimeMillis())
                .subscribe(System.out::println);

        // add error handling
        Observable.create(subscriber -> {
            try {
                subscriber.onNext(getData());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .map(data -> data + " --> at " + System.currentTimeMillis())
                .onErrorResumeNext(e -> Observable.just("Fallback Data"))
                .subscribe(System.out::println);

        // infinite 无限数据, 但只有在调用的时候才会取数据, 避免了内存溢出: 延迟执行!
        Observable.create(subscriber -> {
            int i = 0;
            while (!subscriber.isUnsubscribed()) {
                subscriber.onNext(i++);
            }
        }).take(10).subscribe(System.out::println);

        //Hello World
        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).onErrorResumeNext(throwable -> {
            return Observable.just("fallback value");
        }).subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).onErrorResumeNext(Observable.just("fallback value"))
                .subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).onErrorReturn(throwable -> {
            return "fallback value";
        }).subscribe(System.out::println);

        Observable.create(subscriber -> {
            throw new RuntimeException("failed!");
        }).retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (throwable, i) -> i)
                    .flatMap(i -> {
                        System.out.println("delay retry by " + i + " second(s)");
                        return Observable.timer(i, TimeUnit.SECONDS);
                    }).concatWith(Observable.error(new RuntimeException("Exceeded 3 retries")));
        }).subscribe(System.out::println, t -> t.printStackTrace());

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private static String getData() {
        return "Got Data!";
    }

}
