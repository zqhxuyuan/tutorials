package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-7.
 */
public class NotThreadSafe {

    //对象成员存储在堆上
    StringBuilder builder = new StringBuilder();

    public void add(String text){
        this.builder.append(text);
    }


    public static void main(String[] args) {
        NotThreadSafe sharedInstance = new NotThreadSafe();

        //如果两个线程同时更新同一个对象的同一个成员，那这个代码就不是线程安全的
        //两个MyRunnable共享了同一个NotThreadSafe对象。因此当它们调用add()方法时会造成竞态条件
        new Thread(new MyRunnable(sharedInstance)).start();
        new Thread(new MyRunnable(sharedInstance)).start();

        System.out.println(sharedInstance.builder.toString());

        //线程安全的做法
        //两个线程都有自己单独的NotThreadSafe对象，调用add()方法时就会互不干扰，再也不会有竞态条件问题
        new Thread(new MyRunnable(new NotThreadSafe())).start();
        new Thread(new MyRunnable(new NotThreadSafe())).start();
    }

    static class MyRunnable implements Runnable{
        NotThreadSafe instance = null;

        public MyRunnable(NotThreadSafe instance){
            this.instance = instance;
        }

        public void run(){
            this.instance.add("some text");
        }
    }

}
