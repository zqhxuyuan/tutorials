package com.baidu.unbiz.common.lang;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月6日 上午4:05:53
 * @param <V>
 */
public class FutureResponse<V> extends FutureTask<V> {

    public FutureResponse() {
        super(new Callable<V>() {
            public V call() throws Exception {
                return null;
            }
        });
    }

    // public void bind(Object key) {
    //
    // }

    public void set(V v) {
        super.set(v);
    }
}
