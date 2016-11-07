package com.zqh.hadoop.nimbus.test.java.nimbus.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.utils.CacheletHashType;
import com.zqh.hadoop.nimbus.utils.ICacheletHash;
import junit.framework.TestSuite;

import org.junit.BeforeClass;
import org.junit.Test;


public class ServerHashTestSuite extends TestSuite {

	private static HashSet<String> set = new HashSet<String>();
	private HashMap<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();

	private static int NUM_SERVERS = 3;
	private static int REPLICATION = 2;
	private static int SET_SIZE = 1000000;
	
	@BeforeClass
	public static void initset() {
		System.out.print("Initializing set... ");

		String tmp = null;
		set.clear();
		for (int i = 0; i < SET_SIZE; ++i) {
			tmp = generateRandomString();
			if (!set.contains(tmp)) {
				set.add(tmp);
			} else {
				--i;
			}
		}
		
		System.out.println("Done.");
	}

	@Test
	public void testCRC16() {
		ICacheletHash.destroyInstance();
		NimbusConf.getConf().set(NimbusConf.SERVER_HASH_TYPE,
				CacheletHashType.CRC16.toString());

		runtest();
	}

	@Test
	public void testHashCode() {
		ICacheletHash.destroyInstance();
		NimbusConf.getConf().set(NimbusConf.SERVER_HASH_TYPE,
				CacheletHashType.HASHCODE.toString());
		
		runtest();
	}

	
	@Test
	public void testMurMur() {
		ICacheletHash.destroyInstance();
		NimbusConf.getConf().set(NimbusConf.SERVER_HASH_TYPE,
				CacheletHashType.MURMUR.toString());

		runtest();
	}

	/**
	 * Generates a random string. Looks a lot like a UUID because it is a random
	 * UUID.
	 * 
	 * @return A random string.
	 */
	private static String generateRandomString() {
		return UUID.randomUUID().toString();
	}
	
	
	private void runtest() {
		System.out.println(ICacheletHash.getInstance().getClass().getName());
		HashMap<Integer, List<String>> keys = new HashMap<Integer, List<String>>();

		for (int i = 0; i < NUM_SERVERS; ++i) {
			keys.put(i, new ArrayList<String>());
		}

		long start = 0, accumtime = 0, validtime = 0;
		for (String key : set) {
			HashSet<Integer> set = new HashSet<Integer>();
			start = System.currentTimeMillis();
			ICacheletHash.getInstance().getCacheletsFromKey(key, set,
					NUM_SERVERS, REPLICATION);
			accumtime += System.currentTimeMillis() - start;
			map.put(key, set);
			assertTrue(set.size() == REPLICATION);

			for (Integer i : set) {
				keys.get(i).add(key);
			}
		}

		boolean[] check = new boolean[NUM_SERVERS];
		int numvalidates = 0;
		for (Entry<String, Set<Integer>> e : map.entrySet()) {
			Arrays.fill(check, 0, check.length - 1, false);
			for (Integer i : e.getValue()) {
				start = System.currentTimeMillis();
				assertTrue(ICacheletHash.getInstance().isValidCachelet(i,
						e.getKey(), NUM_SERVERS, REPLICATION));
				validtime += System.currentTimeMillis() - start;
				++numvalidates;
				check[i] = true;
			}

			for (int i = 0; i < check.length; ++i) {
				if (!check[i]) {
					start = System.currentTimeMillis();
					assertFalse(ICacheletHash.getInstance().isValidCachelet(i,
							e.getKey(), NUM_SERVERS, REPLICATION));
					validtime += System.currentTimeMillis() - start;
					++numvalidates;
				}
			}
		}

		int total = 0;
		for (int i = 0; i < keys.size(); ++i) {
			System.out.println("i: " + i + "  Count: " + keys.get(i).size());
			total += keys.get(i).size();
		}

		for (int i = 0; i < keys.size(); ++i) {
			List<String> a = keys.get(i);
			if (a.size() == 0) {
				continue;
			}
			HERE: for (int j = 0; j < keys.size(); ++j) {
				if (i == j) {
					break;
				}
				List<String> b = keys.get(j);
				if (b.size() == 0) {
					continue;
				}

				for (String s : a) {
					for (String s1 : b) {
						if (!s1.equals(s)) {
							break HERE;
						}
					}
				}

				System.out.println("I: " + i + "  J: " + j);
				Collections.sort(a);
				Collections.sort(b);
				for (int k = 0; k < a.size() && k < b.size(); ++k) {
					System.out.println(a.get(k) + "\t" + b.get(k));
				}
				assertFalse(true);
			}
		}

		assertTrue(total == SET_SIZE * REPLICATION);

		System.out.println("Total: " + total);
		System.out.println("Accum Time (ms per record): " + (float) accumtime
				/ (float) SET_SIZE);
		System.out.println("Valid Server Time (ms per record): "
				+ (float) validtime / ((float) numvalidates));
	}
}
