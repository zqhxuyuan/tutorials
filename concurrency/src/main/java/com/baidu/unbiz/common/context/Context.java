/**
 * 
 */
package com.baidu.unbiz.common.context;

import java.util.Collection;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月31日 下午10:39:40
 */
public interface Context<K, V> {

    V get(K key);

    Context<K, V> set(K key, V value);

    Collection<V> values();

}
