package com.zqh.midd.netty.callback;

/**
 * Created by hadoop on 15-2-9.
 */
public interface FetcherCallback {
    void onData(Data data);

    void onError(Exception e);
}
