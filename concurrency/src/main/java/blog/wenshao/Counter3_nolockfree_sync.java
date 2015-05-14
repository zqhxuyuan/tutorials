package blog.wenshao;

/**
 * Created by zqhxuyuan on 15-5-13.
 */
public class Counter3_nolockfree_sync {

    private volatile int max = 0;

    //若要线程安全,需要加锁
    public synchronized void set(int value) {
        if (value > max) {
            max = value;
        }
    }

    public int getMax() {
        return max;
    }
}
