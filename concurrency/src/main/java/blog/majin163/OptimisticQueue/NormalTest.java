package blog.majin163.OptimisticQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class NormalTest {


	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<Long> queue = new ArrayBlockingQueue<Long>(1024);
		AtomicLong count = new AtomicLong(0);
		MyProducer[] producer = new MyProducer[50];
		for (int i = 0; i < 50; i++) {
			producer[i] = new MyProducer(queue);
			producer[i].start();
		}
		MyConsumer[] consumer = new MyConsumer[50];
		for (int i = 0; i < 10; i++) {
			consumer[i] = new MyConsumer(queue, count);
			consumer[i].start();
		}
		while (true) {
			long pre = count.get();
			Thread.sleep(5000);
			long tps = (count.get() - pre) / 5;
			System.out.println("5s, tps : " + tps);
		}
	}
	
	private static class MyProducer extends Thread {
		
		private BlockingQueue<Long> queue = null;
		
		public MyProducer(BlockingQueue<Long> queue) {
			this.queue = queue;
		}
		
		public void run() {
			while (true) {
				long value = 2000;
				long sum = 0;
				for (int i = 0; i < value; i++) {
					sum += i;
				}
				queue.offer(value);
			}
		}
	}

	private static class MyConsumer extends Thread implements BarrierHolder {
		
		private Object barrier = new Object();
		private BlockingQueue<Long> queue = null;
		private AtomicLong count = null;
		
		public MyConsumer(BlockingQueue<Long> queue, AtomicLong count) {
			this.queue = queue;
			this.count = count;
		}

		public Object getBarrier() {
			return barrier;
		}
		
		public void run() {
			while (true) {
				Long value = null;
				try {
					value = queue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long sum = 0;
				for (int i = 0; i < value; i++) {
					sum += i;
				}
				count.incrementAndGet();
			}
		}
	}
}
