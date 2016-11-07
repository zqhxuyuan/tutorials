package com.zqh.hadoop.nimbus.test.java.nimbus.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.UUID;

import com.zqh.hadoop.nimbus.client.CacheletsUnavailableException;
import com.zqh.hadoop.nimbus.client.StaticSetClient;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.master.NimbusMaster;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Test;

import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.master.CacheExistsException;
import com.zqh.hadoop.nimbus.utils.StaticSetIngestor;

import junit.framework.TestSuite;

public class IngestorTestSuite extends TestSuite {

	private static FileSystem fs = null;
	private static final String CACHE_NAME = "ingestor-test";
	private static int APPROX_NUM_RECORDS = 5;
	private static final int TEST_BAD_RECORDS = 1000000;
	private static final float FALSE_POSITIVE_RATE = .05f;
	private static StaticSetClient client = null;

	static {
		try {
			fs = FileSystem.get(NimbusConf.getConf());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void ingestTest() throws IOException, CacheDoesNotExistException,
			CacheExistsException, InterruptedException {
		Path src = new Path(System.getProperty("user.dir")
				+ "/src/test/data/data-512B-12c-39");
		Path dest = new Path("/user/" + System.getProperty("user.name")
				+ "/ingestor-test");
		APPROX_NUM_RECORDS = Integer.parseInt(src.getName().split("-")[src
				.getName().split("-").length - 1]);

		putInHDFS(src, dest);
		System.out.print("Ingesting... ");
		long start, finish;
		start = System.currentTimeMillis();
		StaticSetIngestor.ingest(true, CACHE_NAME, dest, APPROX_NUM_RECORDS,
				FALSE_POSITIVE_RATE);
		finish = System.currentTimeMillis();
		System.out.println("... Done. Took " + (finish - start) + " ms.");
		Thread.sleep(5000);
		client = new StaticSetClient(CACHE_NAME);

		BufferedReader rdr = new BufferedReader(new InputStreamReader(
				fs.open(dest)));

		String testline;
		HashSet<String> set = new HashSet<String>();
		float accumtime = 0.0f;
		boolean value;
		while ((testline = rdr.readLine()) != null) {
			set.add(testline);
			start = System.currentTimeMillis();
			value = client.contains(testline);
			finish = System.currentTimeMillis();
			assertTrue(value);
			accumtime += finish - start;
		}
		System.out.println("MS per record if in set: "
				+ (accumtime / (float) set.size()));

		rdr.close();
		accumtime = 0;
		for (int i = 0; i < TEST_BAD_RECORDS; ++i) {
			testline = generateRandomString();
			if (!set.contains(testline)) {
				start = System.currentTimeMillis();
				try {
					value = client.contains(testline);
				} catch (CacheletsUnavailableException e) {
					--i;
					continue;
				}
				finish = System.currentTimeMillis();
				assertFalse(value);
				accumtime += finish - start;
			} else {
				--i;
			}
		}
		System.out.println("MS per record if not in set: "
				+ (accumtime / TEST_BAD_RECORDS));
		removeFromHDFS(dest);
	}

	@After
	public void cleanup() throws IOException {
		NimbusMaster master = NimbusMaster.getInstance();

		if (master.exists(CACHE_NAME)) {
			master.destroy(CACHE_NAME);
		}
	}

	/**
	 * Generates a random string. Looks a lot like a UUID because it is a random
	 * UUID.
	 * 
	 * @return A random string.
	 */
	private String generateRandomString() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Takes a file from the local file system (src) and puts it in HDFS (dest)
	 * 
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	private void putInHDFS(Path src, Path dest) throws IOException {
		if (fs.exists(dest)) {
			removeFromHDFS(dest);
		}
		fs.copyFromLocalFile(src, dest);
	}

	private void removeFromHDFS(Path p) throws IOException {
		fs.delete(p, false);
	}
}