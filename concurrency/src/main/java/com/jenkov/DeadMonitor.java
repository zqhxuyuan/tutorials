package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * http://ifeve.com/nested-monitor-lockout/
 *
 * 嵌套管程锁死
 * lock implementation with nested monitor lockout problem
 *
 * 发生嵌套管程锁死时锁获取的顺序是一致的。
 * 线程1获得A和B，然后释放B，等待线程2的信号。
 * 线程2需要同时获得A和B，才能向线程1发送信号。
 * 所以，一个线程(1)在等待唤醒，另一个线程(2)在等待想要的锁被释放
 */
public class DeadMonitor {

    //管程对象
    protected MonitorObject monitorObject = new MonitorObject();
    //信号
    protected boolean isLocked = false;

    public void lock() throws InterruptedException{
        //lock()方法首先在”this”上同步，然后在monitorObject上同步。
        synchronized(this){
            //如果isLocked等于false，因为线程不会继续调用monitorObject.wait()，那么一切都没有问题 。
            //但是如果isLocked等于true，调用lock()方法的线程会在monitorObject.wait()上阻塞

            //这里的问题在于，调用monitorObject.wait()方法只释放了monitorObject上的管程对象，
            //而与”this“关联的管程对象并没有释放。换句话说，这个刚被阻塞的线程仍然持有”this”上的锁

            //当一个已经持有这种Lock(持有this锁)的线程想调用unlock(),就会在unlock()方法进入synchronized(this)块时阻塞。
            //这会一直阻塞到在lock()方法中等待的线程离开synchronized(this)块。
            //但是，在unlock中isLocked变为false，monitorObject.notify()被执行之后，lock()中等待的线程才会离开synchronized(this)块
            while(isLocked){
                synchronized(this.monitorObject){
                    this.monitorObject.wait();
                }
            }
            isLocked = true;
        }
    }

    public void unlock(){
        //lock只释放了monitorObject上的管程对象,但仍然持有this上的锁
        //导致这里synchronized(this)被阻塞. 只有调用lock方法的线程离开了synchronized(this)锁,这里才会执行
        //但是只有调用unlock的monitorObject.notify才会释放this锁. 而unlock已经被synchronized(this)阻塞了. 所以导致了死锁!
        synchronized(this){
            this.isLocked = false;
            synchronized(this.monitorObject){
                this.monitorObject.notify();
            }
        }
    }

}
