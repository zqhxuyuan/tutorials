/**
 * 
 */
package com.baidu.unbiz.common.wrapper;

import java.util.Map;

import com.baidu.unbiz.common.CollectionUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 上午2:18:44
 */
public class MapWrapper<K, V> {

    private Map<K, V> map;

    public MapWrapper() {
        map = CollectionUtil.createHashMap();
    }

    public MapWrapper(Map<K, V> map) {
        this.map = map;
    }

    public MapWrapper<K, V> set(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapWrapper<K, V> remove(K key) {
        map.remove(key);
        return this;
    }

    public MapWrapper<K, V> clear() {
        map.clear();
        return this;
    }

    public Map<K, V> getMap() {
        return map;
    }

}
