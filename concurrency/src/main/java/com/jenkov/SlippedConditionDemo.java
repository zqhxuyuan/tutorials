package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 */
public class SlippedConditionDemo {

    private boolean isLocked = true;

    /**
     * 第一个同步块执行wait操作直到isLocked变为false才退出，
     * 第二个同步块将isLocked置为true，以此来锁住这个Lock实例避免其它线程通过lock()方法
     *
     * 假如在某个时刻isLocked为false， 这个时候，有两个线程同时访问lock方法。
     * 如果第一个线程先进入第一个同步块，这个时候它会发现isLocked为false，
     * 若此时允许第二个线程执行，它也进入第一个同步块，同样发现isLocked是false。
     * 现在两个线程都检查了这个条件为false，然后它们都会继续进入第二个同步块中并设置isLocked为true
     */
    public void lockHasSlippedConditions(){
        synchronized(this){
            //已经被锁住了,则等待
            while(isLocked){
                try{
                    this.wait();
                } catch(InterruptedException e){
                    //do nothing, keep waiting
                }
            }
        }

        //没有被锁住,设置信号=true, 其他线程在上一个同步块里,会进入阻塞状态
        synchronized(this){
            isLocked = true;
        }
    }

    //为避免slipped conditions，条件的检查与设置必须是原子的，
    //也就是说，在第一个线程检查和设置条件期间，不会有其它线程检查这个条件
    public void lockWithoutSlippedConditions(){
        synchronized(this){
            //已经被锁住了,则等待
            while(isLocked){
                try{
                    this.wait();
                } catch(InterruptedException e){
                    //do nothing, keep waiting
                }
            }
            isLocked = true;
        }
    }

    public synchronized void unlock(){
        isLocked = false;
        this.notify();
    }
}
