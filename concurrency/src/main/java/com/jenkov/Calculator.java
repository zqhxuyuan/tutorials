package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-7.
 *
 * 即使一个对象是线程安全的不可变对象，指向这个对象的引用也可能不是线程安全的
 */
public class Calculator {

    //这是一个线程安全的不可变对象. Calculator类持有一个指向ImmutableValue实例的引用
    private ImmutableValue currentValue = null;

    public ImmutableValue getValue(){
        return currentValue;
    }

    //通过setValue()方法和add()方法可能会改变这个引用。
    //因此即使Calculator类内部使用了一个不可变对象，但Calculator类本身还是可变的，
    //因此Calculator类不是线程安全的。换句话说：ImmutableValue类是线程安全的，但使用它的类不是
    public void setValue(ImmutableValue newValue){
        this.currentValue = newValue;
    }

    public void add(int newValue){
        this.currentValue = this.currentValue.add(newValue);
    }

}
