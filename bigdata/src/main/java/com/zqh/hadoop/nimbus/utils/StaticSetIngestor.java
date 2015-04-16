package com.zqh.hadoop.nimbus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.zqh.hadoop.nimbus.client.StaticSetClient;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.master.FailedToCreateCacheException;
import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.server.CacheType;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.master.CacheExistsException;

/**
 * A utility class that can be used via command line or Java code to create and
 * ingest a file into a Distributed Set.
 * 
 */
public class StaticSetIngestor {

	private static final Logger LOG = Logger.getLogger(StaticSetIngestor.class);

	public static void main(String args[]) throws NumberFormatException,
			CacheExistsException, CacheDoesNotExistException, IOException {
		if (args.length != 5 && args.length != 6) {
			System.err
					.println("Usage: java -classpath $NIMBUS_HOME/bin/nimbus.jar nimbus.utils.DSetIngestor <destroy> <cachename> <ingest_file> <approxNumRecords> <falsePositiveRate> [verify]");
			System.exit(-1);
		}

		boolean destroy = Boolean.parseBoolean(args[0]);
		String cacheName = args[1];
		Path p = new Path(args[2]);
		int approxNumRecords = Integer.parseInt(args[3]);
		float falsePosRate = Float.parseFloat(args[4]);
		boolean successful = ingest(destroy, cacheName, p, approxNumRecords,
				falsePosRate);

		if (successful) {
			System.out.println("Ingest complete.");
			if (args.length == 6) {
				System.out.println("Verifying...");
				if (verify(cacheName, p)) {
					System.out
							.println("Verification complete.  All records accounted for.");
				} else {
					System.out
							.println("Verification failed.  All records not accounted for.");
				}
			}
		} else {
			System.out.println("Ingest failed.");
		}

		System.exit(0);
	}

	/**
	 * Ingests the given file into a new Cache. If the Cache already exists and
	 * the <b>destroy</b> parameter is not true, then a CacheExistsException
	 * will be thrown.
	 * 
	 * @param destroy
	 *            Whether or not to destroy the Cache if it already exists. If
	 *            the Cache does already exist, then a CacheExistsException is
	 *            thrown.
	 * @param cacheName
	 *            The name of the Cache to create.
	 * @param file
	 *            The file to ingest.
	 * @param approxNumRecords
	 *            The approximate number of records inside the file.
	 * @param falsePositiveRate
	 *            The desired false positive rate for the Bloom filter.
	 * @return If the file was successfully ingested.
	 * @throws CacheExistsException
	 * @throws IOException
	 *             If <b>file</b> is not a file.
	 */
	public static boolean ingest(boolean destroy, String cacheName, Path file,
			int approxNumRecords, float falsePositiveRate)
			throws CacheExistsException, IOException {
		if (FileSystem.get(NimbusConf.getConf()).getFileStatus(file).isDir()) {
			throw new IOException("Path " + file + " is not a file.");
		}
		try {
			NimbusMaster master = NimbusMaster.getInstance();
			if (destroy) {
				if (master.exists(cacheName)) {
					if (destroy) {
						master.destroy(cacheName);
					} else {
						throw new CacheExistsException(cacheName);
					}
				} else {
					master.create(cacheName, CacheType.STATIC_SET);

					while (!master.exists(cacheName)) {
						LOG.info("Cache does not exist yet... Sleeping");
						Thread.sleep(1000);
					}
				}
			}
			Thread.sleep(1000);
			StaticSetClient set = new StaticSetClient(cacheName, false);
			set.read(file, approxNumRecords, falsePositiveRate, false);
			set.disconnect();
		} catch (FailedToCreateCacheException e) {
			e.printStackTrace();
			return false;
		} catch (CacheDoesNotExistException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Verifies that each element in the given file is present in the Cache.
	 * This is a serial process and can take quite some time, depending on the
	 * size of the Cache. This was mainly used for testing purposes. All the
	 * elements are there. Promise.
	 * 
	 * @param cacheName
	 *            The Cache to verify.
	 * @param file
	 *            The file to check against the Cache.
	 * @return Whether or not every member in the file is in the Cache.
	 * @throws CacheDoesNotExistException
	 *             If the Cache does not exist.
	 * @throws IOException
	 *             If an error occurs when reading the file.
	 */
	public static boolean verify(String cacheName, Path file)
			throws CacheDoesNotExistException, IOException {

		StaticSetClient set = new StaticSetClient(cacheName);
		BufferedReader rdr = new BufferedReader(new InputStreamReader(
				FileSystem.get(NimbusConf.getConf()).open(file)));

		boolean goodtogo = true;
		String s;
		float records = 0;
		long start = System.currentTimeMillis();
		float begin = 0.0f, end = 0.0f, accumtime = 0.0f;
		while ((s = rdr.readLine()) != null && goodtogo) {
			++records;
			begin = System.currentTimeMillis();
			goodtogo = set.contains(s);
			end = System.currentTimeMillis();
			accumtime += end - begin;
		}

		System.out.println("Checked " + (int) records + " records in "
				+ (System.currentTimeMillis() - start) + " ms.  Average "
				+ accumtime / records + " ms for contains().");

		rdr.close();
		set.disconnect();
		return goodtogo;
	}
}
