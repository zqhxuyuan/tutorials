package blog.iter_zc.lock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 回退自旋锁，在测试-测试-设置自旋锁的基础上增加了线程回退，降低锁的争用
 * 优点是在锁高争用的情况下减少了锁的争用，提高了执行的性能
 * 缺点是回退的时间难以控制，需要不断测试才能找到合适的值，而且依赖底层硬件的性能，扩展性差
 */
public class BackoffLock implements Lock{

    private final int MIN_DELAY, MAX_DELAY;

    public BackoffLock(int min, int max){
        MIN_DELAY = min;
        MAX_DELAY = max;
    }

    private AtomicBoolean mutex = new AtomicBoolean(false);

    @Override
    public void lock() {
        // 增加回退对象
        Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
        while(true){
            // 第一步使用读操作，尝试获取锁，当mutex为false时退出循环，表示可以获取锁
            while(mutex.get()){}
            // 第二部使用getAndSet方法来尝试获取锁
            if(!mutex.getAndSet(true)){
                //成功获取锁
                return;
            }else{
                //回退
                try {
                    backoff.backoff();
                } catch (InterruptedException e) {
                }
            }

        }
    }

    @Override
    public void unlock() {
        mutex.set(false);
    }

    public String toString(){
        return "TTASLock";
    }
}