package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * 一个简单的锁
 */
public class CounterSync {

    private int count = 0;

    public int inc(){
        synchronized(this){
            return ++count;
        }
    }

}
