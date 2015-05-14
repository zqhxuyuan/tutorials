package blog.wenshao;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 阻塞队列的一个特点是: 队列为空时, 消费者阻塞, 队列满时,生产者阻塞
 * 下面的实现由于使用链表, 所以是一个无界队列.
 * 由于无界, 所以生产者总是能够生产数据, 不用怕队列满了
 *
 * 而我们要控制的是队列为空时,消费者获取线程应该进行等待并释放锁
 * 那么什么时候触发通知呢?
 * 在生产者生产数据的时候,如果队列为空,就进行通知.
 *
 * 注意下面的wait,notify都加上了锁. 未取得锁就直接执行wait、notfiy、notifyAll会抛异常
 */
class BlockingQ1 {
    private Object notEmpty = new Object();
    private Queue<Object> linkedList = new LinkedList<Object>();

    public Object take() throws InterruptedException {
        //在并发环境下,要对整个队列的操作过程进行同步
        synchronized (notEmpty) {
            if (linkedList.size() == 0) {
                /**
                 * 要执行wait操作,必须先取得该对象的锁。
                 * 执行wait操作之后,锁会释放。
                 * 被唤醒之前,需要先获得锁。
                 */
                notEmpty.wait();
            }
            return linkedList.poll();
        }
    }

    public void offer(Object object) {
        synchronized (notEmpty) {
            if (linkedList.size() == 0) {
                /**
                 * 要执行notify和notifyAll操作,都必须先取得该对象的锁。
                 */
                notEmpty.notifyAll();
            }
            linkedList.add(object);
        }
    }
}