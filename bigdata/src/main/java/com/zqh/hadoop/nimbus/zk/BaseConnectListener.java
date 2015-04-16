package com.zqh.hadoop.nimbus.zk;

import java.io.IOException;

import org.apache.log4j.Logger;

public class BaseConnectListener implements ConnectListener {

	private boolean connected = false;
	private Logger LOG = Logger.getLogger(BaseConnectListener.class);

	@Override
	public void connected() throws IOException {
		connected = true;
		LOG.info("Connected to ZooKeeper.");
	}

	@Override
	public void closing() throws IOException {
		connected = false;
		LOG.info("ZooKeeper session closed.");
	}

	public void waitFor() {
		while (!connected) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isConnected() {
		return connected;
	}
}
