package blog.wenshao;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by zqhxuyuan on 15-5-13.
 *
 * 分别需要对notEmpty和notFull加锁
 */
public class BlockingQ2 {

    //两个对象锁, 分别用于控制队列空时,和队列满时
    private Object notEmpty = new Object();
    private Object notFull = new Object();
    private Queue<Object> linkedList = new LinkedList<Object>();
    private int maxLength = 10;

    public Object take() throws InterruptedException {
        synchronized (notEmpty) {
            if (linkedList.size() == 0) {
                //消费队列空时, 在notEmpty对象锁上等待, 并通过offer()操作得到通知:生产元素后队列就不为空了(queue not empty)
                notEmpty.wait();
            }
            synchronized (notFull) {
                if (linkedList.size() == maxLength) {
                    notFull.notifyAll();
                }
                return linkedList.poll();
            }
        }
    }

    public void offer(Object object) throws InterruptedException {
        synchronized (notEmpty) {
            if (linkedList.size() == 0) {
                notEmpty.notifyAll();
            }
            synchronized (notFull) {
                if (linkedList.size() == maxLength) {
                    //生产队列满时,在notFull对象锁上等待, 并通过take()进行通知
                    notFull.wait();
                }
                linkedList.add(object);
            }
        }
    }
}
