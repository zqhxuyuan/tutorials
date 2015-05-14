package blog.wenshao;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by zqhxuyuan on 15-5-13.
 */
public class PerformaceTest {

    private int threadCount;
    private CyclicBarrier barrier;
    private int loopCount = 10;

    public PerformaceTest(int threadCount) {
        this.threadCount = threadCount;
        barrier = new CyclicBarrier(threadCount, new Runnable() {
            public void run() {
                //在都通过屏障之后, 收集结果
                collectTestResult();
            }
        });
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread("test-thread " + i) {
                public void run() {
                    for (int j = 0; j < loopCount; ++j) {
                        doTest();
                        try {
                            //使用Barrier来实现并发性能测试的聚合点
                            barrier.await();
                        } catch (InterruptedException e) {
                            return;
                        } catch (BrokenBarrierException e) {
                            return;
                        }
                    }
                }
            };
            thread.start();
        }
    }

    private void doTest() { /* do xxx */ }
    private void collectTestResult() { /* do xxx */ }

}
