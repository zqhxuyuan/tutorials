package com.zqh.hadoop.nimbus.test.java.nimbus.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.zqh.hadoop.nimbus.utils.BigBitArray;
import junit.framework.TestSuite;

import org.junit.Test;

public class BigBitArrayTestSuite extends TestSuite {
	private static BigBitArray array = null;

	private static final long ARRAY_SIZE = 1024;
	
	@Test
	public void test() {

		array = new BigBitArray(ARRAY_SIZE);
		
		for (int i = 0; i < ARRAY_SIZE; ++i) {
			array.turnBitOn(i);
			for (int j = 0; j <= i; ++j) {
				assertTrue(array.isBitOn(j));
			}
			for (int j = i+1; j < ARRAY_SIZE; ++j) {
				assertFalse(array.isBitOn(j));
			}
		}
		
		for (int i = 0; i < ARRAY_SIZE; ++i) {
			array.turnBitOff(i);
			for (int j = 0; j <= i; ++j) {
				assertFalse(array.isBitOn(j));
			}
			for (int j = i+1; j < ARRAY_SIZE; ++j) {
				assertTrue(array.isBitOn(j));
			}
		}
	}
}
