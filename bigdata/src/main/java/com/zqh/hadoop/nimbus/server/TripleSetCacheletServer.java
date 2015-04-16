package com.zqh.hadoop.nimbus.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.master.CacheInfo;
import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.nativestructs.Triple;
import com.zqh.hadoop.nimbus.nativestructs.TripleSet;
import com.zqh.hadoop.nimbus.utils.ICacheletHash;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

public class TripleSetCacheletServer extends ICacheletServer {

	private TripleSet set = new TripleSet();
	private static final Logger LOG = Logger
			.getLogger(TripleSetCacheletServer.class);

	public TripleSetCacheletServer(String cacheName, String cacheletName,
			int port, CacheType type) {
		super(cacheName, cacheletName, port, type);
	}

	@Override
	protected ICacheletWorker getNewWorker() {
		return new TripleSetCacheletWorker(this);
	}
	
	@Override
	protected void startStatusThread() {
	}
	
	@Override
	public void run() {
		openServer();

		CacheInfo info = NimbusMaster.getInstance().getCacheInfo(cacheName);
		if (info.getFilename() != null) {
			LOG.info("Re-ingesting " + info.getFilename() + "...");
			this.distributedLoadFromHDFS(new Path(info.getFilename()),
					NimbusMaster.getInstance().getCacheletID(cacheletName));
		} else { // leave watch on node for when it does change.
			Nimbus.getZooKeeper().getDataVariable(Nimbus.CACHE_ZNODE,
					new CacheletDataWatcher(this), null);
		}

		acceptConnections();
	}

	public void clear() {
		set.clear();
	}

	public Iterator<Triple> iterator() {
		return set.iterator();
	}

	public Iterator<Triple> iterator(String s1) {
		return set.iterator(s1);
	}

	public Iterator<Triple> iterator(String s1, String s2) {
		return set.iterator(s1, s2);
	}

	public boolean add(String s1, String s2, String s3) {
		return set.add(s1, s2, s3);
	}

	public boolean contains(Triple element) {
		return set.contains(element);
	}

	public boolean contains(String s1, String s2, String s3) {
		return set.contains(s1, s2, s3);
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public boolean distributedLoadFromHDFS(Path file, int cacheletID) {
		int numCachelets = NimbusConf.getConf().getNumNimbusCachelets();
		int replication = NimbusConf.getConf().getReplicationFactor();

		try {
			NimbusMaster.getInstance().setCacheletAvailability(cacheName,
					cacheletName, false);
			FileSystem fs = FileSystem.get(NimbusConf.getConf());
			long start = System.currentTimeMillis();
			LOG.info("Reading from file " + file.makeQualified(fs)
					+ ".  My Cachelet ID is " + cacheletID);

			ICacheletHash hashalgo = ICacheletHash.getInstance();
			if (hashalgo == null) {
				throw new RuntimeException("Hash algorithm is null");
			}

			// open the file for read.
			BufferedReader rdr = new BufferedReader(new InputStreamReader(
					fs.open(file)));
			int numrecords = 0, added = 0;
			String s;
			while ((s = rdr.readLine()) != null) {
				++numrecords;
				if (hashalgo.isValidCachelet(cacheletID, s, numCachelets,
						replication)) {
					set.add(s.split("\\s"));
					++added;
				}
			}

			rdr.close();

			LOG.info("Num records: " + numrecords + "  Added: " + added
					+ "  Took " + (System.currentTimeMillis() - start) + "ms.");

			System.gc();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			NimbusMaster.getInstance().setCacheletAvailability(cacheName,
					cacheletName, true);
		}
	}

	private class CacheletDataWatcher implements Watcher {

		private TripleSetCacheletServer server = null;

		public CacheletDataWatcher(TripleSetCacheletServer server) {
			this.server = server;
		}

		@Override
		public void process(WatchedEvent event) {
			if (event.getType().equals(EventType.NodeDataChanged)) {
				CacheInfo info = NimbusMaster.getInstance().getCacheInfo(
						cacheName);
				if (info.getFilename() != null) {
					LOG.info("Data Watch Trigger.  Ingesting file "
							+ info.getFilename() + "...");
					server.distributedLoadFromHDFS(
							new Path(info.getFilename()), NimbusMaster
									.getInstance().getCacheletID(cacheletName));
				}
			}
		}
	}
	
	public long size() {
		return set.size();
	}
	
	public long sizeOf(String s1) {
		return set.sizeOf(s1);
	}
	
	public long sizeOf(String s1, String s2) {
		return set.sizeOf(s1, s2);
	}
}