package com.zqh.hadoop.nimbus.main;

import java.util.List;

import com.zqh.hadoop.nimbus.master.NimbusSafetyNet;
import com.zqh.hadoop.nimbus.server.CacheType;

import org.apache.log4j.Logger;

/**
 * This class handles graceful shutdown of Nimbus. Its mainly deletes this
 * machine's ZNode from ZooKeeper, as well as deleting the Cache ZNode if no
 * more children exist.<br>
 * <br>
 * 
 * Forceful killing of a Nimbus process will leave this machine's ZNode in
 * Nimbus, and will therefore corrupt ZooKeeper.
 */
public class NimbusShutdownHook extends Thread {

	private static final Logger LOG = Logger
			.getLogger(NimbusShutdownHook.class);

	private static NimbusShutdownHook s_instance = null;
	private boolean clean = false;
	private CacheType type = null;

	public static void createInstance(CacheType type) {
		if (s_instance == null) {
			s_instance = new NimbusShutdownHook(type);
			Runtime.getRuntime().addShutdownHook(s_instance);
		}
	}

	public static NimbusShutdownHook getInstance() {
		return s_instance;
	}

	private NimbusShutdownHook(CacheType type) {
		this.type = type;
	}

	public void cleanShutdown() {
		this.clean = true;
	}

	/**
	 * This method is executed when Nimbus is shutdown to cleanup ZNodes in
	 * ZooKeeper.
	 */
	@Override
	public void run() {
		LOG.info("Shutting down server.  You stay classy Sandy Eggo.");

		if (type.equals(CacheType.MASTER)) {
			LOG.info("Stopping safety net...");
			NimbusSafetyNet.getInstance().stop();
		}

		if (clean) {
			LOG.info("Clean shutdown of this cache");
			Nimbus.getZooKeeper().deletePaths(Nimbus.CACHELET_ZNODE, true);

			List<String> children = Nimbus.getZooKeeper().getChildren(
					Nimbus.CACHE_ZNODE);
			if (children.size() == 0) {
				LOG.info("No more children left.  Deleting Cache node: "
						+ Nimbus.CACHE_ZNODE);
				Nimbus.getZooKeeper().deletePaths(Nimbus.CACHE_ZNODE, true);
			}
		} else {
			LOG.info("Not a clean shutdown.  Leaving ZNodes");
		}

		Nimbus.getZooKeeper().close();
	}
}
