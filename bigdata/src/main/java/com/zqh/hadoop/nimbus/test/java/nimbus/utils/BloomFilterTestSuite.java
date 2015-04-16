package com.zqh.hadoop.nimbus.test.java.nimbus.utils;

import java.util.HashSet;
import java.util.UUID;

import com.zqh.hadoop.nimbus.utils.BloomFilter;
import junit.framework.TestSuite;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BloomFilterTestSuite extends TestSuite {

	private static final int NUM_BITS = 8;

	private static int NUM_RECORDS = 1000;
	private static int NUM_BADRECORDS = NUM_RECORDS * 100;
	private static float FALSE_POSITIVE_RATE = .01f;
	private static HashSet<String> set = new HashSet<String>();

	private static BloomFilter bfilter = null;

	@Test
	public void test() {

		bfilter = new BloomFilter(NUM_RECORDS, FALSE_POSITIVE_RATE);

		for (int i = 0; i < NUM_RECORDS; ++i) {
			String tmp = generateRandomString();
			if (!set.contains(tmp)) {
				set.add(tmp);
				bfilter.train(tmp);
			} else {
				--i;
			}
		}

		assertTrue(set.size() == NUM_RECORDS);

		float count = 0;
		for (String s : set) {
			++count;
			assertTrue(bfilter.membershipTest(s));
		}

		float falsePositive = 0;
		for (int i = 0; i < NUM_BADRECORDS; ++i) {
			String tmp = generateRandomString();
			boolean yes = bfilter.membershipTest(tmp);
			++count;
			if (yes) {
				if (!set.contains(tmp)) {
					++falsePositive;
				}
			}
		}

		System.out.println("False Positive Rate: " + falsePositive / count);
	}

	private String generateRandomString() {
		return UUID.randomUUID().toString();
	}

	public String printbits(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (int i = (bytes.length * NUM_BITS) - 1; i >= 0; --i) {
			if (isBitOn(bytes, i)) {
				builder.append('1');
			} else {
				builder.append('0');
			}
		}

		return builder.toString();
	}

	public String printbyte(byte bytes) {
		StringBuilder builder = new StringBuilder();
		for (int i = NUM_BITS - 1; i >= 0; --i) {
			if (isBitOn(bytes, i)) {
				builder.append('1');
			} else {
				builder.append('0');
			}
		}

		return builder.toString();
	}

	private boolean isBitOn(byte[] bytes, int trueIndex) {
		return (bytes[trueIndex / NUM_BITS] & (1 << (trueIndex % NUM_BITS))) != 0;
	}

	private boolean isBitOn(byte bytes, int index) {
		return (bytes & (1 << (index % NUM_BITS))) != 0;
	}
}
