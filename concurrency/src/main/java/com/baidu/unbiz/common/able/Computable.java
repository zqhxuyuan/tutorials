/**
 *
 */
package com.baidu.unbiz.common.able;

import java.util.concurrent.Callable;

/**
 * 计算
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月22日 下午11:16:27
 */
public interface Computable<K, V> {

    /**
     * 通过关键字来计算
     *
     * @param key      查找关键字
     * @param callable # @see Callable
     *
     * @return 计算结果
     */
    V get(K key, Callable<V> callable);

}
