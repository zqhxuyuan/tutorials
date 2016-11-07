package com.zqh.hadoop.nimbus.server;

public class MasterCacheletServer extends ICacheletServer {

	public MasterCacheletServer(String cacheName, String cacheletName,
			int port, CacheType type) {
		super(cacheName, cacheletName, port, type);
	}

	@Override
	protected ICacheletWorker getNewWorker() {
		return new MasterCacheletWorker();
	}
	
	@Override
	protected void startStatusThread() {
	}
}
