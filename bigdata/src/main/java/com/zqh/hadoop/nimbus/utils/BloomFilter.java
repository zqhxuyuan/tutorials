package com.zqh.hadoop.nimbus.utils;

import java.io.IOException;

import com.zqh.hadoop.nimbus.main.NimbusConf;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;


/**
 * From Wikipedia<br>
 * A Bloom filter is a space-efficient probabilistic data structure that is used
 * to test whether an element is a member of a set. False positives are
 * possible, but false negatives are not; i.e. a query returns either "inside
 * set (may be wrong)" or "definitely not in set". Elements can be added to the
 * set, but not removed (though this can be addressed with a counting filter).
 * The more elements that are added to the set, the larger the probability of
 * false positives.<br>
 * <br>
 * 
 * This implementation of a Bloom filter uses 11 different hash functions to
 * turn bits of a byte array on when training. It requires the approximate
 * number of records and the desired false positive rate during initialization.
 * The Bloom filter can also be given a {@link Path} to a serialized Bloom
 * filter in HDFS, or a byte array to clone.<br>
 * <br>
 * 
 * The theoretical largest size of a Bloom filter is about 17.18 billion bits,
 * which uses about 2 GB of raw memory. A Bloom filter of this size is optimal
 * for a set that contains about 1.196 billion members.
 */
public class BloomFilter {

	private static final Logger LOG = Logger.getLogger(BloomFilter.class);
	private static final long NUM_BITS = 8;
	private static final byte[] mask = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
			0x40, (byte) 0x80 };
	private static final int NUM_HASHES = 11;
	private static HashFunctionLibrary LIBRARY = null;

	private long arrayLength = -1;
	private long trueSize = -1;
	private byte[] bytes = null;

	/**
	 * Initializes a new instance of a Bloom filter based on the given size and
	 * desired false positive rate.
	 * 
	 * @param approxNumRecords
	 *            The number of records that are most likely going to be used to
	 *            train this filter.
	 * @param desiredFalsePosRate
	 *            The desired false positive rate.
	 */
	public BloomFilter(int approxNumRecords, float desiredFalsePosRate) {
		LIBRARY = new HashFunctionLibrary();
		setSize(approxNumRecords, desiredFalsePosRate);
		bytes = new byte[(int) arrayLength];
	}

	/**
	 * Initializes a Bloom filter based on the given array. The given array is
	 * NOT cloned to cut down on memory usage.
	 * 
	 * @param array
	 *            An array to use for this Bloom filter.
	 */
	public BloomFilter(byte[] array) {
		LIBRARY = new HashFunctionLibrary();
		bytes = array;
		arrayLength = bytes.length;
		trueSize = bytes.length * NUM_BITS;
	}

	/**
	 * Initializes a new instance of a Bloom filter off of a given file that had
	 * this Bloom filter serialize data to. The file in the Path is opened and
	 * the Bloom filter deserializes the filter inside.
	 * 
	 * @param p
	 *            A path to a file that contains a serialized Bloom filter
	 * @throws IOException
	 *             If an error occurs, most likely the file is not found.
	 */
	public BloomFilter(Path p) throws IOException {
		LIBRARY = new HashFunctionLibrary();
		deserialize(p);
	}

	/**
	 * Returns the "true" size of this Bloom filter, i.e. the length of the
	 * internal byte array times 8.
	 * 
	 * @return "True" size of the filter.
	 */
	public long size() {
		return trueSize;
	}

	/**
	 * Trains the given key for the filter. Iterates over all the hash
	 * algorithms and turns the appropriate bit on in the byte array.
	 * 
	 * @param key
	 *            The key to train.
	 */
	public void train(String key) {
		for (int i = 0; i < NUM_HASHES; ++i) {
			turnBitOn(hash(key, i));
		}
	}

	/**
	 * Returns a boolean value as to whether or not the key is a possible member
	 * of the set. A value of false means this member is definitely not in the
	 * set, whereas a value of true means this member may possibly be in the
	 * set.
	 * 
	 * @param key
	 *            The key to test
	 * @return Whether or not the key is a possible member of the set.
	 */
	public boolean membershipTest(String key) {
		long hash;
		for (int i = 0; i < NUM_HASHES; ++i) {
			hash = hash(key, i);
			try {
				if (!isBitSet(hash)) {
					return false;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				LOG.error(e.getMessage() + "  Hash Func Index: " + i
						+ "  Hash:" + hash);
				return true;
			}
		}
		return true;
	}

	/**
	 * Sets all the bits in the byte array to zero.
	 */
	public void reset() {
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = 0;
		}
	}

	/**
	 * Sets the filter's internal byte array to null.
	 */
	public void clear() {
		bytes = null;
	}

	/**
	 * Creates a new Bloom filter based on the approximate number of records and
	 * desired false positive rate
	 * 
	 * @param approxNumRecords
	 *            The number of records that are most likely going to be used to
	 *            train this filter.
	 * @param desiredFalsePosRate
	 *            The desired false positive rate.
	 */
	public void reset(int approxNumRecords, float desiredFalsePosRate) {
		setSize(approxNumRecords, desiredFalsePosRate);
		bytes = null;
		bytes = new byte[(int) arrayLength];
	}

	/**
	 * Serializes this filter to the given file in HDFS. The file will be
	 * overwritten.
	 * 
	 * @param p
	 * @throws IOException
	 */
	public void serialize(Path p) throws IOException {
		FSDataOutputStream wrtr = FileSystem.get(NimbusConf.getConf())
				.create(p);
		wrtr.writeLong(arrayLength);
		wrtr.write(bytes);
		wrtr.flush();
		wrtr.close();
		LOG.info("Serialized " + arrayLength + " to " + p);

	}

	/**
	 * Deserializes a Bloom filter from a given file.
	 * 
	 * @param p
	 *            A Path to a filter that contains a serializes filter.
	 * @throws IOException
	 *             If an error occurs, most likely the file is not found.
	 */
	public void deserialize(Path p) throws IOException {
		FSDataInputStream rdr = FileSystem.get(NimbusConf.getConf()).open(p);
		arrayLength = rdr.readLong();
		trueSize = arrayLength * NUM_BITS;
		bytes = null;
		bytes = new byte[(int) arrayLength];
		int c = 0;
		int numrecords = 0;
		for (int i = 0; i < arrayLength; ++i) {
			c = rdr.read();
			if (c == -1) {
				break;
			}

			bytes[i] = (byte) c;
			++numrecords;
		}
		rdr.close();
		LOG.info("Deserialized " + numrecords + " bytes from " + p
				+ " into an array of size " + bytes.length);
	}

	/**
	 * Returns each bit of the Bloom filter. Should only really be used for
	 * debugging purposes with small Bloom filters, since the String will most
	 * likely overflow.
	 * 
	 * @return The String representation of this Bloom filter.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(arrayLength + "\t");
		builder.append(trueSize + "\t");
		for (long i = trueSize - 1; i >= 0; --i) {
			if (isBitSet(i)) {
				builder.append('1');
			} else {
				builder.append('0');
			}
		}

		return builder.toString();
	}

	/**
	 * Helper function to execute a particular hash function.
	 * 
	 * @param key
	 *            The key to hash.
	 * @param func
	 *            A hash function value
	 * @return The hashed value between 0 and {@link BloomFilter#size} - 1.
	 */
	private long hash(String key, int func) {
		long hash = 0;
		switch (func) {
		case 0:
			hash = LIBRARY.APHash(key);
			break;
		case 1:
			hash = LIBRARY.BKDRHash(key);
			break;
		case 2:
			hash = LIBRARY.BPHash(key);
			break;
		case 3:
			hash = LIBRARY.DEKHash(key);
			break;
		case 4:
			hash = LIBRARY.DJBHash(key);
			break;
		case 5:
			hash = LIBRARY.ELFHash(key);
			break;
		case 6:
			hash = LIBRARY.FNVHash(key);
			break;
		case 7:
			hash = LIBRARY.JSHash(key);
			break;
		case 8:
			hash = LIBRARY.PJWHash(key);
			break;
		case 9:
			hash = LIBRARY.RSHash(key);
			break;
		case 10:
			hash = LIBRARY.SDBMHash(key);
			break;
		}

		return Math.abs(hash) % trueSize;
	}

	/**
	 * Helper function to set member variables based on the given parameters.
	 * 
	 * @param approxNumRecords
	 * @param desiredFalsePosRate
	 */
	private void setSize(int approxNumRecords, float desiredFalsePosRate) {
		long size = (long) (-approxNumRecords * Math.log(desiredFalsePosRate) / (Math
				.pow(Math.log(2), 2)));
		size = (size + size % NUM_BITS) / NUM_BITS;
		arrayLength = size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size;
		trueSize = arrayLength * NUM_BITS;
		LOG.info("Setting Bloom filter size for " + approxNumRecords
				+ " records w/ " + desiredFalsePosRate + " false pos rate to "
				+ trueSize);
		LOG.info("Byte array size is " + arrayLength + " bytes.");
	}

	/**
	 * Turns a bit between 0 and {@link BloomFilter#size()} - 1 to on.
	 * 
	 * @param trueIndex
	 *            The bit to turn on.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the bit is not between 0 and {@link BloomFilter#size()} -
	 *             1.
	 */
	// These variables are here to prevent them from being declared inside the
	// method, seeing as how this is going to be called many, many of times
	private int TBOarrayIndex, TBObitIndex;

	private void turnBitOn(long trueIndex)
			throws ArrayIndexOutOfBoundsException {
		if (trueIndex >= trueSize || trueIndex < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}

		TBOarrayIndex = (int) (trueIndex / NUM_BITS);
		TBObitIndex = (int) (trueIndex % NUM_BITS);
		bytes[TBOarrayIndex] = (byte) (bytes[TBOarrayIndex] | mask[TBObitIndex]);
	}

	/**
	 * Returns a boolean value if the given bit is set.
	 * 
	 * @param i
	 *            The bit to test.
	 * @return Whether or not the bit is on.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the bit is not between 0 and {@link BloomFilter#size()} -
	 *             1.
	 */
	private boolean isBitSet(long i) throws ArrayIndexOutOfBoundsException {
		if (i >= trueSize || i < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}

		return (bytes[(int) (i / NUM_BITS)] & (1 << (i % NUM_BITS))) != 0;
	}

	/*
	 * *************************************************************************
	 * *
	 * General Purpose Hash Function Algorithms Library * * Author: Arash Partow
	 * - 2002 * URL: http://www.partow.net * URL:
	 * http://www.partow.net/programming/hashfunctions/index.html * * Copyright
	 * notice: * Free use of the General Purpose Hash Function Algorithms
	 * Library is * permitted under the guidelines and in accordance with the
	 * most current * version of the Common Public License. *
	 * http://www.opensource.org/licenses/cpl1.0.php * *
	 * *************************************************************************
	 */

	private class HashFunctionLibrary {
		public long RSHash(String str) {
			int b = 378551;
			int a = 63689;
			long hash = 0;

			for (int i = 0; i < str.length(); i++) {
				hash = hash * a + str.charAt(i);
				a = a * b;
			}

			return hash;
		}

		public long JSHash(String str) {
			long hash = 1315423911;

			for (int i = 0; i < str.length(); i++) {
				hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
			}

			return hash;
		}

		public long PJWHash(String str) {
			long BitsInUnsignedInt = (long) (4 * 8);
			long ThreeQuarters = (long) ((BitsInUnsignedInt * 3) / 4);
			long OneEighth = (long) (BitsInUnsignedInt / 8);
			long HighBits = (long) (0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
			long hash = 0;
			long test = 0;

			for (int i = 0; i < str.length(); i++) {
				hash = (hash << OneEighth) + str.charAt(i);

				if ((test = hash & HighBits) != 0) {
					hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
				}
			}

			return hash;
		}

		public long ELFHash(String str) {
			long hash = 0;
			long x = 0;

			for (int i = 0; i < str.length(); i++) {
				hash = (hash << 4) + str.charAt(i);

				if ((x = hash & 0xF0000000L) != 0) {
					hash ^= (x >> 24);
				}
				hash &= ~x;
			}

			return hash;
		}

		public long BKDRHash(String str) {
			long seed = 131; // 31 131 1313 13131 131313 etc..
			long hash = 0;

			for (int i = 0; i < str.length(); i++) {
				hash = (hash * seed) + str.charAt(i);
			}

			return hash;
		}

		public long SDBMHash(String str) {
			long hash = 0;

			for (int i = 0; i < str.length(); i++) {
				hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
			}

			return hash;
		}

		public long DJBHash(String str) {
			long hash = 5381;

			for (int i = 0; i < str.length(); i++) {
				hash = ((hash << 5) + hash) + str.charAt(i);
			}

			return hash;
		}

		public long DEKHash(String str) {
			long hash = str.length();

			for (int i = 0; i < str.length(); i++) {
				hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
			}

			return hash;
		}

		public long BPHash(String str) {
			long hash = 0;

			for (int i = 0; i < str.length(); i++) {
				hash = hash << 7 ^ str.charAt(i);
			}

			return hash;
		}

		public long FNVHash(String str) {
			long fnv_prime = 0x811C9DC5;
			long hash = 0;

			for (int i = 0; i < str.length(); i++) {
				hash *= fnv_prime;
				hash ^= str.charAt(i);
			}

			return hash;
		}

		public long APHash(String str) {
			long hash = 0xAAAAAAAA;

			for (int i = 0; i < str.length(); i++) {
				if ((i & 1) == 0) {
					hash ^= ((hash << 7) ^ str.charAt(i) * (hash >> 3));
				} else {
					hash ^= (~((hash << 11) + str.charAt(i) ^ (hash >> 5)));
				}
			}

			return hash;
		}
	}
}
