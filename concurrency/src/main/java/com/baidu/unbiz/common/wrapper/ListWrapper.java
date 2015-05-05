/**
 * 
 */
package com.baidu.unbiz.common.wrapper;

import java.util.List;

import com.baidu.unbiz.common.CollectionUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 上午2:17:49
 */
public class ListWrapper<T> {

    private List<T> list;

    public ListWrapper() {
        list = CollectionUtil.createArrayList();
    }

    public ListWrapper(List<T> list) {
        this.list = list;
    }

    public ListWrapper<T> set(T value) {
        list.add(value);
        return this;
    }

    public ListWrapper<T> set(T value, int index) {
        list.add(index, value);
        return this;
    }

    public ListWrapper<T> remove(T value) {
        list.remove(value);
        return this;
    }

    public ListWrapper<T> remove(int index) {
        list.remove(index);
        return this;
    }

    public ListWrapper<T> clear() {
        list.clear();
        return this;
    }

    public List<T> getList() {
        return list;
    }

}
