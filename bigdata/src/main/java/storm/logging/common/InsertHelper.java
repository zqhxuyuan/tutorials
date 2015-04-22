package storm.logging.common;

import java.util.concurrent.CountDownLatch;

public class InsertHelper implements Runnable {

	public InsertHelper(
			String dbHost,
			int dbPort,
			String dbName,
			String collectionName,
			CountDownLatch latch) {
		this.dbHost = dbHost;
		this.dbPort = dbPort;
		this.collectionName = collectionName;
		this.latch = latch;
	}
	
	@Override
	public void run() {
		
		/*try {
			//maybe u can insert something before storm ready
			DBCollection coll = MongoDBHelper.getDBCollection(
					this.dbHost,
					this.dbPort,
					this.dbName,
					this.collectionName);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}*/
		//wait until storm main thread ready
		while(this.latch.getCount() != 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	private String dbHost;
	private int dbPort;
	private String dbName;
	private String collectionName;
	private CountDownLatch latch;

}
