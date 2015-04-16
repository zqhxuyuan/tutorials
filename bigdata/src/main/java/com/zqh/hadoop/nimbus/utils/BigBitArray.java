package com.zqh.hadoop.nimbus.utils;

import java.security.InvalidParameterException;

/**
 * A big bit array is internally represented as a byte array, but has functions
 * to flip bits inside the byte array. The BigBitArray must be initializes to a
 * multiple of eight, as there are eight bits in a byte array.
 */
public class BigBitArray {

	private static final long NUM_BITS = 8;
	private static final byte[] mask = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
			0x40, (byte) 0x80 };

	private long arrayLength = -1;
	private long trueSize = -1;
	private byte[] bytes = null;

	/**
	 * Initializes a new instance of a BigBitArray based on the given size.
	 * 
	 * @param approxNumRecords
	 *            The number of records that are most likely going to be used to
	 *            train this filter.
	 * @param desiredFalsePosRate
	 *            The desired false positive rate.
	 */
	public BigBitArray(long size) {
		setSize(size);
		bytes = new byte[(int) arrayLength];
	}

	/**
	 * Initializes a Bloom filter based on the given array. The given array is
	 * NOT cloned to cut down on memory usage.
	 * 
	 * @param array
	 *            An array to use for this Bloom filter.
	 */
	public BigBitArray(byte[] array) {
		bytes = array;
		arrayLength = bytes.length;
		trueSize = bytes.length * NUM_BITS;
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
	public void reset(int size) {
		setSize(size);
		bytes = new byte[(int) arrayLength];
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
			if (isBitOn(i)) {
				builder.append('1');
			} else {
				builder.append('0');
			}
		}

		return builder.toString();
	}

	/**
	 * Turns a bit between 0 and {@link BigBitArray#size()} - 1 to on.
	 * 
	 * @param trueIndex
	 *            The bit to turn on.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the bit is not between 0 and the true size.
	 */
	public void turnBitOn(long trueIndex) throws ArrayIndexOutOfBoundsException {
		if (trueIndex >= trueSize || trueIndex < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}

		int TBOarrayIndex = (int) (trueIndex / NUM_BITS);
		int TBObitIndex = (int) (trueIndex % NUM_BITS);
		bytes[TBOarrayIndex] = (byte) (bytes[TBOarrayIndex] | mask[TBObitIndex]);
	}

	/**
	 * Turns a bit between 0 and {@link BigBitArray#size()} - 1 to off.
	 * 
	 * @param trueIndex
	 *            The bit to turn off.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the bit is not between 0 and the true size.
	 */
	public void turnBitOff(long trueIndex)
			throws ArrayIndexOutOfBoundsException {
		if (trueIndex >= trueSize || trueIndex < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		if (isBitOn(trueIndex)) {
			int TBOarrayIndex = (int) (trueIndex / NUM_BITS);
			int TBObitIndex = (int) (trueIndex % NUM_BITS);
			bytes[TBOarrayIndex] = (byte) (bytes[TBOarrayIndex] ^ mask[TBObitIndex]);
		}
	}

	/**
	 * Returns a boolean value if the given bit is set.
	 * 
	 * @param i
	 *            The bit to test.
	 * @return Whether or not the bit is on.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the bit is not between 0 and {@link BigBitArray#size()} -
	 *             1.
	 */
	public boolean isBitOn(long i) throws ArrayIndexOutOfBoundsException {
		if (i >= trueSize || i < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}

		return (bytes[(int) (i / NUM_BITS)] & (1 << (i % NUM_BITS))) != 0;
	}

	/**
	 * Clones the given array and sets it to the internal byte array. Sizes
	 * itself appropriately off of the given array.
	 * 
	 * @param bytes
	 *            The array to clone.
	 */
	public void setBytes(byte[] bytes) {
		this.bytes = bytes.clone();
		arrayLength = bytes.length;
		trueSize = bytes.length * NUM_BITS;
	}

	/**
	 * Gets a clone of the internal byte array.
	 * 
	 * @return The bytes.
	 */
	public byte[] getBytes() {
		return bytes.clone();
	}

	/**
	 * Rounds the given number up so it is a multiple of eight. If it is already
	 * a multiple of eight, the parameter is simply returned.
	 * 
	 * @param num
	 *            The number to round.
	 * @return The given number rounded up.
	 */
	public static long makeMultipleOfEight(long num) {
		return num % NUM_BITS == 0 ? num : num + (NUM_BITS - (num % NUM_BITS));
	}

	/**
	 * Helper function to set member variables based on the given parameters.
	 * 
	 * @param approxNumRecords
	 * @param desiredFalsePosRate
	 */
	private void setSize(long size) {
		if (size % 8L != 0) {
			throw new InvalidParameterException("Size must be a multiple of "
					+ NUM_BITS);
		}

		arrayLength = size / NUM_BITS;
		trueSize = size;
		;
	}

	public void set(int i, boolean on) {
		if (on) {
			this.turnBitOn(i);
		} else {
			this.turnBitOff(i);
		}
	}
}
