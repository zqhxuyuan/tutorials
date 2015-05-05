/**
 * 
 */
package com.baidu.unbiz.common.collection;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.LinkedList;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.able.CloneableObject;

/**
 * 从<code>LinkedList</code>继承的stack，避免<code>java.util.Stack</code> 中synchronized的代价。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午5:11:45
 */
public class Stack<T> implements Serializable, CloneableObject<Stack<T>> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2884212111417967679L;

    private LinkedList<T> list = CollectionUtil.createLinkedList();

    public T peek() {
        if (list.isEmpty()) {
            throw new EmptyStackException();
        }

        return list.getLast();
    }

    public void push(T object) {
        list.addLast(object);
    }

    public T pop() {
        if (list.isEmpty()) {
            throw new EmptyStackException();
        }

        return list.removeLast();
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public String toString() {
        return list.toString();
    }

    public Stack<T> clone() throws CloneNotSupportedException {
        Stack<T> stack = new Stack<T>();
        @SuppressWarnings("unchecked")
        LinkedList<T> newList = (LinkedList<T>) list.clone();

        stack.list = newList;
        return stack;
    }

}