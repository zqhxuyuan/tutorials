/**
 * 
 */
package com.baidu.unbiz.common.wrapper;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 上午2:19:08
 */
public class ArrayWrapper {

    private Object[] array;

    private int index;

    public ArrayWrapper(Object[] array) {
        this.array = array;
        index = 0;
    }

    public ArrayWrapper set(Object value) {
        array[index++] = value;
        return this;
    }

    public ArrayWrapper set(Object value, int index) {
        array[index++] = value;
        // FIXME
        this.index = index;
        return this;
    }

    public ArrayWrapper clear() {
        int length = array.length;
        this.array = new Object[length];
        return this;
    }

    public Object[] getArray() {
        return array;
    }

}
