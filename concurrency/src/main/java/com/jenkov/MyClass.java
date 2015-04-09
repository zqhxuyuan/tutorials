package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-7.
 */
public class MyClass {

    //实例方法
    public synchronized void log1(String msg1, String msg2){
        method1();
        method2();
    }

    //实例方法中的同步块
    public void log2(String msg1, String msg2){
        synchronized(this){
            method1();
            method2();
        }
    }

    //静态方法
    public static synchronized void log3(String msg1, String msg2){
        method3();
        method4();
    }

    //静态方法中的同步块
    public static void log4(String msg1, String msg2){
        synchronized(MyClass.class){
            method3();
            method4();
        }
    }

    public void method1(){}
    public void method2(){}
    public static void method3(){}
    public static void method4(){}
}
