package com.zqh.hadoop.nimbus.test.java.nimbus.master;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.master.NimbusSafetyNet;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.master.ISafetyNetListener;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SafetyNetTestSuite implements ISafetyNetListener {
	private boolean notifiedcacheletadded = false,
			notifiedcacheletremoved = false;
	private boolean notifiedcacheadded = false, notifiedcacheremoved = false;
	private boolean notifiedcachestale = false;

	@Before
	public void setup() throws IOException, KeeperException,
			InterruptedException {
		System.out.println("setupcalled");

		Nimbus.getZooKeeper().deletePaths(Nimbus.ROOT_ZNODE, true);

		Thread.sleep(1000);

		Nimbus.getZooKeeper().ensurePaths(Nimbus.ROOT_ZNODE);

		Thread t = new Thread(NimbusSafetyNet.getInstance());
		t.start();
		NimbusSafetyNet.getInstance().addListener(this);
	}

	@Test
	public void test() throws KeeperException, InterruptedException {
		ZooKeeper zk = Nimbus.getZooKeeper().getZooKeeper();

		notifiedcacheadded = false;
		zk.create(Nimbus.ROOT_ZNODE + "/testcache", BytesUtil.EMPTY_BYTES,
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		Thread.sleep(100);
		assertTrue(notifiedcacheadded);

		notifiedcacheletadded = false;
		zk.create(Nimbus.ROOT_ZNODE + "/testcache/machine1", BytesUtil.EMPTY_BYTES,
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		Thread.sleep(100);
		assertTrue(notifiedcacheletadded);

		notifiedcacheletadded = false;
		zk.create(Nimbus.ROOT_ZNODE + "/testcache/machine2", BytesUtil.EMPTY_BYTES,
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		Thread.sleep(100);
		assertTrue(notifiedcacheletadded);

		notifiedcacheletremoved = false;
		zk.delete(Nimbus.ROOT_ZNODE + "/testcache/machine1", -1);
		Thread.sleep(100);
		assertTrue(notifiedcacheletremoved);

		notifiedcacheletremoved = false;
		zk.delete(Nimbus.ROOT_ZNODE + "/testcache/machine2", -1);
		Thread.sleep(100);
		assertTrue(notifiedcacheletremoved);

		notifiedcacheremoved = false;
		zk.delete(Nimbus.ROOT_ZNODE + "/testcache", -1);
		Thread.sleep(100);
		assertTrue(notifiedcacheremoved);
	}

	@Test
	public void testHeartbeat() throws KeeperException, InterruptedException,
			IOException {
		ZooKeeper zk = Nimbus.getZooKeeper().getZooKeeper();

		notifiedcacheadded = false;
		zk.create(Nimbus.ROOT_ZNODE + "/testcache", BytesUtil.EMPTY_BYTES,
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		Thread.sleep(100);
		assertTrue(notifiedcacheadded);

		notifiedcacheletadded = false;
		zk.create(Nimbus.ROOT_ZNODE + "/testcache/machine1", BytesUtil.EMPTY_BYTES,
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		Thread.sleep(100);
		assertTrue(notifiedcacheletadded);

		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) <= NimbusConf.getConf()
				.getSafetyNetTimeout() * 2) {
			zk.setData(Nimbus.ROOT_ZNODE + "/testcache/machine1",
					BytesUtil.EMPTY_BYTES, -1);
			Thread.sleep(100);
		}

		Thread.sleep((long) ((float) NimbusConf.getConf().getSafetyNetTimeout() * 1.5f));
		assertTrue(notifiedcachestale);
	}

	@After
	public void cleanup() throws IOException, KeeperException,
			InterruptedException {
	}

	@Override
	public void onCacheAdded(String name) {
		System.out.println("Notified cache added " + name);
		notifiedcacheadded = true;
	}

	@Override
	public void onCacheRemoved(String name) {
		System.out.println("Notified cache removed " + name);
		notifiedcacheremoved = true;
	}

	@Override
	public void onCacheletAdded(String cacheName, String cacheletName) {
		System.out.println("Notified cachelet added " + cacheletName
				+ " on cache " + cacheName);
		notifiedcacheletadded = true;
	}

	@Override
	public void onCacheletRemoved(String cacheName, String cacheletName) {
		System.out.println("Notified cachelet removed " + cacheletName
				+ " on cache " + cacheName);
		notifiedcacheletremoved = true;
	}

	@Override
	public void onCacheletStale(String cacheName, String cacheletName) {
		System.out.println("Notified cachelet stale " + cacheletName
				+ " on cache " + cacheName);
		notifiedcachestale = true;
	}
}
