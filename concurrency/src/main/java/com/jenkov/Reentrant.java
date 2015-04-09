package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 */
public class Reentrant {

    //如果一个java线程进入了代码中的synchronized同步块，
    //并因此获得了该同步块使用的同步对象对应的管程上的锁，
    //那么这个线程可以进入由同一个管程对象所同步的另一个java代码块

    //如果一个线程已经拥有了一个管程对象上的锁，那么它就有权访问被这个管程对象同步的所有代码块
    //这就是可重入。线程可以进入任何一个它已经拥有的锁所同步着的代码块
    public synchronized void outer(){
        inner();
    }

    public synchronized void inner(){
        //do something
    }

    //synchronized的实例方法相当于在this上加锁
    //如果某个线程调用了outer()，outer()中的inner()调用是没问题的，
    //因为两个方法都是在同一个管程对象(即this)上同步的
    public void outerSync(){
        synchronized (this){
            innerSync();
        }
    }

    public void innerSync(){
        synchronized (this){
            //do sth
        }
    }
}
