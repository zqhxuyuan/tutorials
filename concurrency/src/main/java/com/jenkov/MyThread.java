package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-7.
 */
public class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("Running in a new thread....");
    }

    public static void main(String[] args) {
        //Thread子类
        MyThread thread = new MyThread();
        thread.start();
        System.out.println("Main Thread...");

        //匿名子类
        Thread thread2 = new Thread(){
            public void run(){
                System.out.println("Thread Running");
            }
        };
        thread2.start();
        System.out.println("Main Thread...");

        //Runnable实现类
        Thread thread3 = new Thread(new MyRunnable());
        thread3.start();

        //线程名: 尽管启动线程的顺序是有序的，但是执行的顺序并非是有序的
        System.out.println(Thread.currentThread().getName());
        for(int i=0; i<10; i++){
            new Thread("" + i){
                public void run(){
                    System.out.println("Thread: " + getName() + "running");
                }
            }.start();
        }

    }
}
