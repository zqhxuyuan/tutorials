package blog.iter_zc.lock;

public class Main {
    //private static TimeCost timeCost = new TimeCost(new TASLock());
    //private static TimeCost timeCost = new TimeCost(new TTASLock());

    private static Lock timeCost = new ArrayLock(5);
    //private static Lock timeCost = new CLHLock();

    private static volatile int value = 0;

    /**
     * 使用50个线程对一个volatile变量++操作，由于volatile变量++操作不是原子的，
     * 在不加锁的情况下，可能同时有多个线程同时对volatile变量++, 最终的结果是无法预测的
     * 比如注释掉lock和unlock,结果会乱序输出, 并且会出现多个线程操作同一个变量的情况
     */
    public static void methodWithoutLock(){
        //int a = 10;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("value:" + ++value);
    }

    /**
     * 然后使用这两种锁，先获取锁再volatile变量++，由于volatile变量会防止重排序，并能保证可见性，
     * 我们可以确定如果锁是正确获取的，也就是说同一时刻只有一个线程对volatile变量++,那么结果肯定是顺序的1到50
     *
     * ** 同一时刻只有一个线程对volatile变量++ **
     *
     *
     */
    public static void methodWithLock(){
        timeCost.lock();
        System.out.println("value:" + ++value);
        timeCost.unlock();
    }

    public static void main(String[] args) {
        for(int i = 0; i < 50; i ++){
            Thread t = new Thread(new Runnable(){

                @Override
                public void run() {
                    //methodWithoutLock();
                    methodWithLock();
                }

            });
            t.start();
        }
    }

}