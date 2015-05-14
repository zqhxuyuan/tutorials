package com.ibm.jtp;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zqhxuyuan on 15-5-5.
 *
 * 使用 CAS 的非阻塞算法
 */
public class CounterNonblocking {

    //原子变量: 提供了对数字和对象引用的细粒度的原子更新
    private AtomicInteger value;

    public int getValue() {
        return value.get();
    }

    public int increment() {
        int v;
        do {
            v = value.get();
        } while (!value.compareAndSet(v, v + 1)) ;

        return v + 1;
    }

    public int increment2(){
        for(;;){
            int v = value.get();

            if(value.compareAndSet(v, v + 1)){
                return v + 1;
            }
        }
    }
}
