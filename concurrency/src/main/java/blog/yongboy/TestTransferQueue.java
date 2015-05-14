package blog.yongboy;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;

public class TestTransferQueue {
    final static TransferQueue<String> queue = new LinkedTransferQueue<String>();

    @Test
    public void testTansferQueue() throws InterruptedException {
        final TransferQueue<String> transferQueue = new LinkedTransferQueue<String>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    // 此处阻塞，等待take()，poll()的发生。
                    transferQueue.transfer("test");
                    System.out.println("子线程完成传递.");
                } catch (InterruptedException e) {}
            }
        });

        // 此处阻塞，等待trankser(当然可以是别的插入元素的方法)的发生
        String test = transferQueue.take();
        System.out.printf("主线程完成获取 %s.\n", test);
        Thread.sleep(1000);
    }

    // 模拟transfer等待消费者线程，若消费者线程不出现的话，则阻塞
    @Test
    public void testTransfer() {
        try {
            // 优先启动一个线程，做消费者线程，去取队列数据
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        // 取完元素，transfer不再阻塞
                        String result = queue.take();
                        System.out.println("result : " + result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            queue.transfer("hello0");
            System.out.println("the data had been taked");
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        Assert.assertEquals(queue.size(), 0);
    }

    // 模拟若消费者线程等待获取元素，元素不存在情况下，进入阻塞状态
    @Test
    public void testTake() {
        try {
            // 优先启动一个线程，做消费者线程，去取队列数据
            new Thread() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        // 取完元素，transfer不再阻塞
                        String result = queue.take();
                        System.out.println("result : " + result
                                + " use time : "
                                + (System.currentTimeMillis() - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            Thread.sleep(2000L);

            queue.transfer("hello-0");
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        Assert.assertEquals(queue.size(), 0);
    }

    // 模拟获取等待消费者线程的个数
    @Test
    public void testWaitingConsumerCount() {
        int consumerCount = -1;
        int threadNum = 5;
        try {
            for (int i = 0; i < threadNum; i++) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            long start = System.currentTimeMillis();
                            String result = queue.take();
                            System.out.println("result : " + result
                                    + " use time : "
                                    + (System.currentTimeMillis() - start));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            Thread.sleep(2000L);
            consumerCount = queue.getWaitingConsumerCount();

            for (int i = 0; i < consumerCount; i++) {
                queue.transfer("hello-0-" + i);
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        Assert.assertEquals(consumerCount, threadNum);
    }

    @Test
    public void testTryTransfer() {
        queue.tryTransfer("hello");

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(queue.size(), 0);
    }

    @Test
    public void testTryTimedTransfer() {
        try {
            queue.tryTransfer("hello2", 100L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(queue.size(), 0);
    }

    @Test
    public void testAdd() {
        queue.add("hello3");

        Assert.assertEquals(queue.size(), 1);
    }

    @Test
    public void testGet() {
        String result = queue.poll();

        Assert.assertEquals(result, "hello3");
    }

    // 如何模拟，在队列有数据的情况下，测试transfer
    @Test
    public void testTransferWithOldElement() {
        final int COUNT = 1001;
        final Phaser barrier = new Phaser(COUNT);
        final StringBuilder sb = new StringBuilder();

        try {
            for (int i = 0; i < 999; i++) {
                queue.add("hello_0_" + i);
            }

            for (int i = 0; i < 1000; i++) {
                // 优先启动一个线程，做消费者线程，去取队列数据
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            barrier.arriveAndAwaitAdvance();
                            // 取完元素，transfer不再阻塞
                            String result = queue.take();
                            if (result.startsWith("_hello_")) {
                                System.out.println("thread name : "
                                        + Thread.currentThread().getName()
                                        + " ; queue size : " + queue.size());
                                sb.append(result);
                                // 模拟主线程等待时间
                                Thread.sleep(1000L);
                                // 终止，主线程得以继续
                                barrier.forceTermination();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            barrier.arriveAndAwaitAdvance();
            queue.transfer("_hello_");
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        long start = System.currentTimeMillis();
        barrier.arriveAndAwaitAdvance();
        System.out.println("wait use time : "
                + (System.currentTimeMillis() - start));

        Assert.assertEquals(sb.toString(), "_hello_");
    }
}