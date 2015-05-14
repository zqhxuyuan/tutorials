package blog.iter_zc.lock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 测试-测试-设置自旋锁，使用AtomicBoolean原子变量保存状态, 分为两步来获取锁
 * 1. 先采用读变量自旋的方式尝试获取锁
 * 2. 当有可能获取锁时，再使用getAndSet原子操作来尝试获取锁
 * 优点是第一步使用读变量的方式来获取锁，在处理器内部高速缓存操作，不会产生缓存一致性流量
 * 缺点是当锁争用激烈的时候，第一步一直获取不到锁，getAndSet底层使用CAS来实现，一直在修改共享变量的值，会引发缓存一致性流量风暴
 */
public class TTASLock implements Lock{

    private AtomicBoolean mutex = new AtomicBoolean(false);

    @Override
    public void lock() {
        while(true){
            // 第一步使用读操作，尝试获取锁，当mutex为false时退出循环，表示可以获取锁
            while(mutex.get()){}
            // 第二步使用getAndSet方法来尝试获取锁
            if(!mutex.getAndSet(true)){
                return;
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