package blog.xylz;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.junit.Test;

import static org.junit.Assert.*;

public class AtomicIntegerTest {

    @Test
    public void testAll() throws InterruptedException{
        final AtomicInteger value = new AtomicInteger(10);
        //expect=1 <> current=10, return false, did't set to 2
        assertEquals(value.compareAndSet(1, 2), false);
        assertEquals(value.get(), 10);
        //expect=current, set current to 3
        assertTrue(value.compareAndSet(10, 3));
        //current is 3
        assertEquals(value.get(), 3);
        //set to 0
        value.set(0);

        //first op is not get, so return new value = 1
        assertEquals(value.incrementAndGet(), 1);
        //first op is get, return old value = 1, add current value by 2, which is 3
        assertEquals(value.getAndAdd(2),1);
        //first op is get, return old value =3, set current value to 5
        assertEquals(value.getAndSet(5),3);
        //current value is 5
        assertEquals(value.get(),5);

        //multi thread
        final int threadSize = 10;
        Thread[] ts = new Thread[threadSize];
        for (int i = 0; i < threadSize; i++) {
            ts[i] = new Thread() {
                public void run() {
                    //atomic increment, the result: value.get() return new value
                    value.incrementAndGet();
                }
            };
        }

        //start multi thread
        for(Thread t:ts) {
            t.start();
        }
        //wait all thread finish, then execute value.get()
        for(Thread t:ts) {
            t.join();
        }
        //initial value=5
        assertEquals(value.get(), 5+threadSize);
    }

    public void testBasicAndArray(){
        AtomicInteger ai = new AtomicInteger(1);

        System.out.println(ai.getAndIncrement());
        System.out.println(ai.get());


        int[] value = new int[] { 1, 2 };
        AtomicIntegerArray aia = new AtomicIntegerArray(value);

        aia.getAndSet(0, 3);
        System.out.println(aia.get(0));
        System.out.println(value[0]);
    }

}