package com.java7developer.chapter4.pets;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by zqhxuyuan on 15-5-12.
 */
public class WorkerTest {

    public static void main(String[] args) {
        BlockingQueue<WorkUnit<Pet>> queue = new LinkedBlockingDeque<>(10);
        queue.add(new WorkUnit(new Cat("Cat")));
        queue.add(new WorkUnit(new Dog("Dog")));

        Worker worker = new Worker(queue);
        worker.start();
    }
}
