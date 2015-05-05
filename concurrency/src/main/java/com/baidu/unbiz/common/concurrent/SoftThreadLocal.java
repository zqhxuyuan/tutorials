/**
 * 
 */
package com.baidu.unbiz.common.concurrent;

import java.lang.ref.SoftReference;

/**
 * 软引用的<code>ThreadLocal</code>,自动释放内存
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午2:03:02
 * @param <T>
 */
public class SoftThreadLocal<T> extends ThreadLocal<T> {

    /**
     * 基于<code>SoftReference</code>的<code>ThreadLocal</cdoe>
     */
    private final ThreadLocal<SoftReference<T>> local = new ThreadLocal<SoftReference<T>>();

    @Override
    public T get() {
        SoftReference<T> ref = local.get();
        T result = null;
        if (null != ref) {
            result = ref.get();
        }
        if (null == result) {
            result = initialValue();
            ref = new SoftReference<T>(result);
            local.set(ref);
        }
        return result;
    }

    @Override
    public void set(T value) {
        if (null == value) {
            remove();
        } else {
            local.set(new SoftReference<T>(value));
        }
    }

    @Override
    public void remove() {
        local.remove();
    }

}
