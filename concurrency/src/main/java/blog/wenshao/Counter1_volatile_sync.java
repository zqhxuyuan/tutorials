package blog.wenshao;

/**
 * Created by zqhxuyuan on 15-5-13.
 */
public class Counter1_volatile_sync {

    private volatile int count = 0;

    //若要线程安全执行执行count++,需要加锁
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
