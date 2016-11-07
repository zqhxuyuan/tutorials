package com.zqh.hadoop.nimbus.zk;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ConnectWatcher implements Watcher {

	private ConnectListener listener = null;

	public ConnectWatcher(ConnectListener listener) {
		this.listener = listener;
	}

	@Override
	public void process(WatchedEvent event) {
		try {
			if (event.getType() == Event.EventType.None) {
				switch (event.getState()) {
				case SyncConnected:
					listener.connected();
					break;
				case Expired:
					listener.closing();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
