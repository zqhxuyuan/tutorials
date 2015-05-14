package com.ibm.jtp;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zqhxuyuan on 15-5-5.
 *
 * 使用 Treiber 算法的非阻塞堆栈
 */
public class ConcurrentStack<E> {

    AtomicReference<Node<E>> head = new AtomicReference<Node<E>>();

    /**
     * push() 方法观察当前最顶的节点，构建一个新节点放在堆栈上，
     * 如果最顶端的节点在初始观察之后没有变化，那么就安装新节点。
     * 如果 CAS 失败，意味着另一个线程已经修改了堆栈，那么过程就会重新开始
     */
    public void push(E item) {
        Node<E> newHead = new Node<E>(item); //新节点
        Node<E> oldHead;
        do {
            oldHead = head.get(); //栈顶节点
            newHead.next = oldHead; //新节点的下一个节点是旧的栈顶节点
        } while (!head.compareAndSet(oldHead, newHead)); //新节点是栈顶节点
    }

    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = head.get();
            if (oldHead == null)
                return null;
            newHead = oldHead.next;
        } while (!head.compareAndSet(oldHead,newHead));
        return oldHead.item;
    }

    static class Node<E> {
        final E item;
        Node<E> next;
        public Node(E item) { this.item = item; }
    }
}