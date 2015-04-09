package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-7.
 */
public class ImmutableValue {

    private int value = 0;

    public ImmutableValue(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    //如果你需要对ImmutableValue类的实例进行操作，可以通过得到value变量后创建一个新的实例来实现
    //注意add()方法以加法操作的结果作为一个新的ImmutableValue类实例返回，而不是直接对它自己的value变量进行操作
    public ImmutableValue add(int valueToAdd){
        return new ImmutableValue(this.value + valueToAdd);
    }

}
