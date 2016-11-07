package com.zqh.midd.netty.callback;

/**
 * Created by hadoop on 15-2-9.
 */
public class Worker {

    public void doWork() {
        Fetcher fetcher = new MyFetcher(new Data(2, 1));
        fetcher.fetchData(new FetcherCallback() {

            @Override
            public void onError(Exception cause) {
                System.out.println("An error accour: " + cause.getMessage());
            }

            @Override
            public void onData(Data data) {
                System.out.println("Data received: " + data);
            }
        });
    }

    public static void main(String[] args) {
        Worker w = new Worker();
        w.doWork();
    }

}