package com.baidu.unbiz.common.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午2:13:33
 */
public class CompositeFastPathLockTest {
    private final static int THREADS = 2;
    private final static int COUNT = 32 * 1024;
    private final static int PER_THREAD = COUNT / THREADS;

    private Thread[] thread;
    private int counter;

    private CompositeFastPathLock instance;

    @Before
    public void setUp() throws Exception {
        instance = new CompositeFastPathLock();

        thread = new Thread[THREADS];
    }

    @After
    public void tearDown() throws Exception {
        instance = null;
        thread = null;
    }

    @Test
    public void testParallel() throws Exception {
        for (int i = 0; i < THREADS; i++) {
            thread[i] = new MyThread();
        }
        for (int i = 0; i < THREADS; i++) {
            thread[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            thread[i].join();
        }
        System.out.printf("Fast path taken: %d/%d\n", instance.fastPathTaken, COUNT);
        assertEquals(COUNT, counter);
    }

    class MyThread extends Thread {
        public void run() {
            for (int i = 0; i < PER_THREAD; i++) {
                instance.lock();
                try {
                    counter = counter + 1;
                } finally {
                    instance.unlock();
                }
            }
        }
    }
}
