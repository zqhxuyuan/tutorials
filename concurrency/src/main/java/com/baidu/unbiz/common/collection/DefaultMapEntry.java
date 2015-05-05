/**
 * 
 */
package com.baidu.unbiz.common.collection;

import java.util.Map;

import com.baidu.unbiz.common.ObjectUtil;

/**
 * <p>
 * <code>Map.Entry</code>的默认实现. 具有如下特征:
 * </p>
 * <ul>
 * <li>支持值为<code>null</code>的key</li>
 * <li>可以和任意<code>Map.Entry</code>的实现进行<code>equals</code>比较</li>
 * <li>如果两个<code>Map.Entry</code>相同(<code>e1.equals(e2) == true</code>), 则它们的 <code>hashCode()</code>也相等</li>
 * </ul>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月24日 上午3:34:14
 * @param <K>
 * @param <V>
 */
public class DefaultMapEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    /**
     * 创建一个<code>Map.Entry</code>.
     * 
     * @param key <code>Map.Entry</code>的key
     * @param value <code>Map.Entry</code>的value
     */
    public DefaultMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 取得key.
     * 
     * @return <code>Map.Entry</code>的key
     */
    public K getKey() {
        return key;
    }

    /**
     * 取得value.
     * 
     * @return <code>Map.Entry</code>的value
     */
    public V getValue() {
        return value;
    }

    /**
     * 设置value的值.
     * 
     * @param value 新的value值
     * @return 老的value值
     */
    public V setValue(V value) {
        V oldValue = this.value;

        this.value = value;

        return oldValue;
    }

    /**
     * 判断两个对象是否相同.
     * 
     * @param o 要比较的对象
     * @return 如果相同, 则返回<code>true</code>
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof Map.Entry)) {
            return false;
        }

        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

        return ObjectUtil.isEquals(key, e.getKey()) && ObjectUtil.isEquals(value, e.getValue());
    }

    /**
     * 取得<code>Map.Entry</code>的hash值. 如果两个<code>Map.Entry</code>相同, 则它们的hash值也相同.
     * 
     * @return hash值
     */
    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    /**
     * 将<code>Map.Entry</code>转换成字符串.
     * 
     * @return 字符串形式的<code>Map.Entry</code>
     */
    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
