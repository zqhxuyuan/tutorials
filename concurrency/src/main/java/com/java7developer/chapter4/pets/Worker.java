package com.java7developer.chapter4.pets;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by zqhxuyuan on 15-5-12.
 */
public class Worker extends Thread {

    protected final BlockingQueue<WorkUnit<Pet>> queue;
    private volatile boolean shutdown = false;

    public Worker(BlockingQueue<WorkUnit<Pet>> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!shutdown) {
            try {
                System.out.println("take a task...");
                WorkUnit<Pet> ap = queue.take();
                Pet patient = ap.getWorkUnit();

                System.out.printf("run the task...");
                patient.examine();
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                shutdown = true;
            }
        }
        System.out.println("worker down...");
    }
}
