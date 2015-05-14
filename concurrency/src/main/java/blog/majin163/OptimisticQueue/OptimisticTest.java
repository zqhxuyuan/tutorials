package blog.majin163.OptimisticQueue;

import java.util.concurrent.atomic.AtomicLong;

public class OptimisticTest {

	public static void main(String[] args) throws InterruptedException {
		OptimisticQueue<Long> queue = new OptimisticQueue<Long>(10);
		AtomicLong count = new AtomicLong(0);
		MyProducer[] producer = new MyProducer[50];
		for (int i = 0; i < 50; i++) {
			producer[i] = new MyProducer(queue);
			producer[i].start();
		}
		MyConsumer[] consumer = new MyConsumer[50];
		for (int i = 0; i <10; i++) {
			consumer[i] = new MyConsumer(queue, count);
			consumer[i].start();
		}
		while (true) {
			long pre = count.get();
			Thread.sleep(5000);
			long tps = (count.get() - pre) / 5;
			System.out.println("5s : " + (count.get() - pre) + " tps : " + tps);
		}
	}
	
	private static class MyProducer extends Thread implements BarrierHolder {
		
		private OptimisticQueue<Long> queue = null;
		private Object barrier = new Object();
		
		public MyProducer(OptimisticQueue<Long> queue) {
			this.queue = queue;
		}
		
		public void run() {
			while (true) {
				long value = 2000;
				long sum = 0;
				for (int i = 0; i < value; i++) {
					sum += i;
				}
				try {
					queue.offer(this, value);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public Object getBarrier() {
			// TODO Auto-generated method stub
			return barrier;
		}
	}

	private static class MyConsumer extends Thread implements BarrierHolder {
		
		private Object barrier = new Object();
		private OptimisticQueue<Long> queue = null;
		private AtomicLong count = null;
		
		public MyConsumer(OptimisticQueue<Long> queue, AtomicLong count) {
			this.queue = queue;
			this.count = count;
		}

		public Object getBarrier() {
			return barrier;
		}
		
		public void run() {
			while (true) {
				long value = 2000;
				try {
					value = queue.take(this);
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
