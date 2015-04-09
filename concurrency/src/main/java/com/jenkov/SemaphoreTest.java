package com.jenkov;

/**
 * Created by zqhxuyuan on 15-4-8.
 */
public class SemaphoreTest {

    public static void main(String[] args) {
        new SemaphoreTest().testSemaphore();
    }

    /**
     * 当信号量的数量上限是1时，Semaphore可以被当做锁来使用。通过take和release方法来保护关键区域
     *
     * 在锁这个例子中，take和release方法将被同一线程调用，因为只允许一个线程来获取信号（允许进入关键区域的信号），
     * 其它调用take方法获取信号的线程将被阻塞，直到第一个调用take方法的线程调用release方法来释放信号。
     * 对release方法的调用永远不会被阻塞，这是因为任何一个线程都是先调用take方法，然后再调用release。
     */
    public void testSemaphoreAsLock() throws Exception{
        SemaphoreBounded bounded = new SemaphoreBounded(1);
        //lock
        bounded.take();
        try {
            //critical section
        }finally {
            //unlock
            bounded.release();
        }
    }

    public void testSemaphore(){
        Semaphore semaphore = new Semaphore();
        SendingThread sender = new SendingThread(semaphore);
        ReceivingThread receiver = new ReceivingThread(semaphore);

        receiver.start();
        sender.start();
    }

    class SendingThread extends Thread{
        Semaphore semaphore = null;

        public SendingThread(Semaphore semaphore){
            this.semaphore = semaphore;
        }

        @Override
        public void run(){
            while(true){
                //do something, then signal
                this.semaphore.take();
            }
        }

    }

    class ReceivingThread extends Thread{
        Semaphore semaphore = null;

        public ReceivingThread(Semaphore semaphore){
            this.semaphore = semaphore;
        }

        @Override
        public void run(){
            while(true){
                try {
                    this.semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //receive signal, then do something...
            }
        }
    }
}
