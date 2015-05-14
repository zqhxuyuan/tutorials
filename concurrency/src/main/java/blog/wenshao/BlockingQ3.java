package blog.wenshao;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zqhxuyuan on 15-5-13.
 *
 * BlockingQ2采用对象锁, 这里采用Lock的条件锁
 */
public class BlockingQ3 {

    private Lock lock = new ReentrantLock();
    //一个锁可以创建多个Condition
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();
    private Queue<Object> linkedList = new LinkedList<Object>();
    private int maxLength = 10;

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            if (linkedList.size() == 0) {
                //要执行await操作,必须先取得该Condition的锁。 执行await操作之后,锁会释放。 被唤醒之前,需要先获得锁。
                notEmpty.await();
            }
            if (linkedList.size() == maxLength) {
                notFull.signalAll();
            }
            return linkedList.poll();
        } finally {
            lock.unlock();
        }
    }

    public void offer(Object object) throws InterruptedException {
        lock.lock();
        try {
            if (linkedList.size() == 0) {
                //要执行signal和signalAll操作,都必须先取得该对象的锁
                notEmpty.signalAll();
            }
            if (linkedList.size() == maxLength) {
                notFull.await();
            }
            linkedList.add(object);
        } finally {
            lock.unlock();
        }
    }
}
