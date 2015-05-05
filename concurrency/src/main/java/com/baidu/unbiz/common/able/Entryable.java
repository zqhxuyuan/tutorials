/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * 定义<code>Entry</code>，一般代表键值对
 *
 * @param <K> key
 * @param <V> value
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月19日 上午2:28:40
 */
public interface Entryable<K, V> {

    K getKey();

    V getValue();

}
