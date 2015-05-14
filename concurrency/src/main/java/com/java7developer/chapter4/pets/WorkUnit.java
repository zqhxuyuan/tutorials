package com.java7developer.chapter4.pets;

/**
 * Created by zqhxuyuan on 15-5-12.
 */
public class WorkUnit<T> {

    private final T workUnit;

    public T getWorkUnit(){
        return workUnit;
    }

    public WorkUnit(T workUnit){
        this.workUnit = workUnit;
    }
}
