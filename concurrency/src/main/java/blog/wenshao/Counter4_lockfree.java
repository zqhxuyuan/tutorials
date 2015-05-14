package blog.wenshao;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zqhxuyuan on 15-5-13.
 *
 * LockFree算法,不需要加锁。通常都是三个部分组成:
 * 1 循环
 * 2 CAS (CompareAndSet)
 * 3 回退
 */
public class Counter4_lockfree {

    private AtomicInteger max = new AtomicInteger();

    public void set(int value) {
        for (;;) {
            int current = max.get();
            if (value > current) {
                if (max.compareAndSet(current, value)) {
                    break;
                } else {
                    continue;
                }
            } else { break; }
        }
    }

    //把for(;;)换成了do...while
    public void set2(int value) {
        int current;
        do {
            current = max.get();
            if (value <= current) {
                break;
            }
        } while (!max.compareAndSet(current, value));
    }

    public int getMax() {
        return max.get();
    }
}
