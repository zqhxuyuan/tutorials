package com.zqh.midd.netty.callback;

/**
 * Created by hadoop on 15-2-9.
 */
public interface Fetcher {
    void fetchData(FetcherCallback callback);
}
