package com.github.NoahShen.jue;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.github.NoahShen.jue.util.ConcurrentLRUCache;


public class Performance {
	
    public static final int readCount = 10000;
    
    public static final int writeCount = 10000;

    public static final int readThreadCount = 10;

    public static final int writeThreadCount = 10;
    
    static ExecutorService readExec;
    
    static ExecutorService writeExec;
    
    static AtomicLong totalReadTime;
    
    static AtomicLong totalWriteTime;
    
    static AtomicLong totalReadCount;
    
    static AtomicLong totalWriteCount;
    
    static final long waitSecond = 20;
    
    static List<Long> timePerReadThread;
    
    static List<Long> timePerWriteThread;
    
    static ConcurrentLRUCache<String, String> cache;
    
    public static void main(String[] args) throws InterruptedException {
        init();
        start();
    }

    private static void init() {
//    	cache = new ConcurrentHashMap<String, String>(1000);
//    	cache = Collections.synchronizedMap(new HashMap<String, String>(1000));
    	cache = new ConcurrentLRUCache<String, String>(1000);
//    	cache = new ConcurrentLRUCache<String, String>(1000, 0.75f, 32);
//    	cache = new LRUCache<String, String>(1000);
//    	cache = new ConcurrentLRUHashMap<String, String>(100);
    	readExec = Executors.newCachedThreadPool();
    	writeExec = Executors.newCachedThreadPool();
    	totalReadTime = new AtomicLong();
    	totalWriteTime = new AtomicLong();
    	
    	totalReadCount = new AtomicLong();
    	totalWriteCount = new AtomicLong();
    	
    	timePerReadThread = new CopyOnWriteArrayList<Long>();
    	timePerWriteThread = new CopyOnWriteArrayList<Long>();
	}

	private static void start() throws InterruptedException {
		loops();
        printResult();
    }
	
	private static void printResult() throws InterruptedException {
		readExec.shutdown();
		writeExec.shutdown();
		
		System.out.println("readThreadCount:" + readThreadCount);
		System.out.println("writeThreadCount:" + writeThreadCount);
		System.out.println();
		
		readExec.awaitTermination(waitSecond, TimeUnit.SECONDS);
        if (readExec.isShutdown()) {
            System.out.println("readCount:" + readCount);
        } else {
        	System.out.println("readExec not shutDown");
        }
        System.out.println("totalReadTime:" + totalReadTime);
//        for (int i = 0; i < timePerReadThread.size(); ++i) {
//        	System.out.println(timePerReadThread.get(i));
//        }
        double avgTimePerReadThread = (double) totalReadTime.longValue() / readThreadCount;
        System.out.println("avgTimePerReadThread:" + avgTimePerReadThread);
        System.out.println("readPerSecond:" + readCount / (avgTimePerReadThread / 1000));
        System.out.println("==============================");
        
        writeExec.awaitTermination(waitSecond, TimeUnit.SECONDS);
        if (writeExec.isShutdown()) {
            System.out.println("writeCount:" + writeCount);
        } else {
        	System.out.println("writeExec not shutDown");
        }
        System.out.println("totalWriteTime:" + totalWriteTime);
//        for (int i = 0; i < timePerWriteThread.size(); ++i) {
//        	System.out.println(timePerWriteThread.get(i));
//        }
        double avgTimePerWriteThread = (double) totalWriteTime.longValue() / writeThreadCount;
        System.out.println("avgTimePerWriteThread:" + avgTimePerWriteThread);
        System.out.println("writePerSecond:" + writeCount / (avgTimePerWriteThread / 1000));
        System.out.println("==============================");
        
        System.out.println("cache size:" + cache.size());
//        System.out.println("cache:\n" + cache);
	}

    private static void loops() {
        for (int i = 0; i < readThreadCount; i++) {
        	readExec.execute(new Runnable() {
                @Override
                public void run() {
                    final long start = System.currentTimeMillis();
                    Random r = new Random();
                    int c = 0;
                    for (int i = 0; i < readCount; ++i) {
                    	int n = r.nextInt();
                    	cache.get(String.valueOf(n));
                    	++c;
                    }
                    
                    totalReadCount.addAndGet(c);
                    long escaped = System.currentTimeMillis() - start;
                    totalReadTime.addAndGet(escaped);
                    timePerReadThread.add(escaped);
                }
            });
        }
        
        for (int i = 0; i < writeThreadCount; i++) {
        	writeExec.execute(new Runnable() {
                @Override
                public void run() {
                    final long start = System.currentTimeMillis();
                    Random r = new Random();
                    int c = 0;
                    for (int i = 0; i < writeCount; ++i) {
                    	int n = r.nextInt();
                    	String s = String.valueOf(n);
                    	cache.put(s, s);
                    	++c;
                    }
                    totalWriteCount.addAndGet(c);
                    long escaped = System.currentTimeMillis() - start;
                    totalWriteTime.addAndGet(escaped);
                    timePerWriteThread.add(escaped);
                }
            });
        }
    }
}