package com.github.NoahShen.jue.compression.lzw;

// ByteArray is used instead of String,
// so that we will have no conversion from/to char,
// Conversion to (char) from int is not recognized for some specific characters
public class ByteArray {
	// The single member variable is a byte array kept inside, it is immutable
	final byte[] arr;

	// Constructor with a byte arrays will clone it, since we dont want access
	// from outside
	ByteArray(byte[] b) {
		arr = (byte[]) b.clone();
	}

	// Default Constructor, 0 length array
	ByteArray() {
		arr = new byte[0];
	}

	// Constructor with a single byte
	ByteArray(byte b) {
		arr = new byte[] { b };
	}

	// For the hash-table we need this
	public boolean equals(Object o) {
		ByteArray ba = (ByteArray) o;
		return java.util.Arrays.equals(arr, ba.arr);
	}

	// For the hash-table we need to give a hash code. (Change must be done for
	// a better hash code)
	public int hashCode() {
		int code = 0;
		for (int i = 0; i < arr.length; ++i)
			code = code * 2 + arr[i];
		return code;
	}

	// returns the size of the byte array
	public int size() {
		return arr.length;
	}

	// returns the byte in a given position
	byte getAt(int i) {
		return arr[i];
	}

	// concatenates another byte array into this one,
	// and returns the concatenation in another newly created one. (ByteArray is
	// immutable)
	public ByteArray conc(ByteArray b2) {
		int sz = size() + b2.size();
		byte[] b = new byte[sz];
		for (int i = 0; i < size(); ++i)
			b[i] = getAt(i);
		for (int i = 0; i < b2.size(); ++i)
			b[i + size()] = b2.getAt(i);
		return new ByteArray(b);
	}

	// Concatenates a byte into this ByteArray.
	// The result is returned in a new ByteArray. (ByteArray is immutable)
	public ByteArray conc(byte b2) {
		return conc(new ByteArray(b2));
	}

	// Returns a byte array of the copy of the inner byte arrays
	public byte[] getBytes() {
		return (byte[]) arr.clone();
	}

	// Checks if it is zero length
	public boolean isEmpty() {
		return size() == 0;
	}

	// Drops the last character and returns it
	public byte getLast() {
		return arr[size() - 1];
	}

	public ByteArray dropLast() {
		byte[] newarr = new byte[size() - 1];
		for (int i = 0; i < newarr.length; ++i)
			newarr[i] = arr[i];
		return new ByteArray(newarr);
	}

	public String toString() {
		return new String(arr);
	}
}
