package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-7.
 *
 * 启动了两个线程，都调用Counter类同一个实例的add方法。
 * 因为同步在该方法所属的实例上，所以同时只能有一个线程访问该方法
 */
public class SyncExample {

    public static void main(String[] args){
        new SyncExample().testSync();
    }

    public void testSync(){
        //创建了两个线程。他们的构造器引用同一个Counter实例。
        //Counter.add方法是同步在实例上，是因为add方法是实例方法并且被标记上synchronized关键字。
        //因此每次只允许一个线程调用该方法。另外一个线程必须要等到第一个线程退出add()方法时，才能继续执行方法
        Counter counter = new Counter();
        Thread  threadA = new CounterThread(counter);
        Thread  threadB = new CounterThread(counter);

        threadA.start();
        threadB.start();

        //如果两个线程引用了两个不同的Counter实例，那么他们可以同时调用add()方法。
        //这些方法调用了不同的对象，因此这些方法也就同步在不同的对象上。这些方法调用将不会被阻塞
        Counter counterA = new Counter();
        Counter counterB = new Counter();
        Thread  threadA2 = new CounterThread(counterA);
        Thread  threadB2 = new CounterThread(counterB);

        //这两个线程，threadA和threadB，不再引用同一个counter实例。
        //CounterA和counterB的add方法同步在他们所属的对象上。
        //调用counterA的add方法将不会阻塞调用counterB的add方法
        threadA2.start();
        threadB2.start();
    }

    class Counter{
        long count = 0;

        public synchronized void add(long value){
            this.count += value;
        }
    }

    class CounterThread extends Thread{

        protected Counter counter = null;

        public CounterThread(Counter counter){
            this.counter = counter;
        }

        public void run() {
            for(int i=0; i<10; i++){
                counter.add(i);
            }
        }
    }
}