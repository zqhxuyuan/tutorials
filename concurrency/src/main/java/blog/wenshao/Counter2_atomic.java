package blog.wenshao;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zqhxuyuan on 15-5-13.
 *
 * 使用AtomicInteger之后,不需要加锁,也可以实现线程安全。
 * 这是由硬件提供原子操作指令实现的。在非激烈竞争的情况下,开销更小,速度更快。
 * java.util.concurrent中实现的原子操作类包括: AtomicBoolean、AtomicInteger、AtomicLong、AtomicReference
 */
public class Counter2_atomic {
    private AtomicInteger count = new AtomicInteger();

    public void increment() {
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}