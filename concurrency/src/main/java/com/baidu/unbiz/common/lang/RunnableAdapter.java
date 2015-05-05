/**
 * 
 */
package com.baidu.unbiz.common.lang;

import java.util.concurrent.Callable;

import com.baidu.unbiz.common.able.Adaptable;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 上午12:26:20
 */
public class RunnableAdapter<T> implements Adaptable<Runnable, Callable<T>>, Callable<T> {

    final Runnable task;
    final T result;

    public RunnableAdapter(Runnable task, T result) {
        this.task = task;
        this.result = result;
    }

    public T call() {
        task.run();
        return result;
    }

    @Override
    public Callable<T> forNew(final Runnable old) {
        return new RunnableAdapter<T>(old, result);
    }

}
