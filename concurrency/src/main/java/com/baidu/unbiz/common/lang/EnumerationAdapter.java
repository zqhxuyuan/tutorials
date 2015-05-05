/**
 * 
 */
package com.baidu.unbiz.common.lang;

import java.util.Enumeration;
import java.util.Iterator;

import com.baidu.unbiz.common.able.Adaptable;

/**
 * 很多遗留的使用<code>Enumeration</code>迭代的老系统已经不再维护了并且不支持泛型，将其升级到<code>Iterator</code>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 上午2:37:58
 */
public class EnumerationAdapter<E> implements Iterator<E>, Adaptable<Enumeration<?>, Iterator<E>> {

    private Enumeration<?> enumeration;

    public EnumerationAdapter() {

    }

    public EnumerationAdapter(Enumeration<?> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public E next() {
        @SuppressWarnings("unchecked")
        E next = (E) enumeration.nextElement();
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> forNew(Enumeration<?> old) {
        return new EnumerationAdapter<E>(old);
    }

}
