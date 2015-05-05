package com.github.NoahShen.jue.compression.lzw;

import java.util.*;

// The dictionary
public class Dict {
	// mp keeps : Word => Index
	// ls keeps : Index => Word
	Map<ByteArray, Integer> mp = new HashMap<ByteArray, Integer>();
	List<ByteArray> ls = new ArrayList<ByteArray>();

	// Adds an element into the dictionary
	public void add(ByteArray str) {
		mp.put(str, new Integer(ls.size()));
		ls.add(str);
	}

	// Gets the number for the given string.
	// If it does not exist, returns -1
	public final int numFromStr(ByteArray str) {
		return (mp.containsKey(str) ? ((Integer) mp.get(str)).intValue() : -1);
	}

	// Gets the string for the given number
	// If the number does not exist, return null
	public final ByteArray strFromNum(int i) {
		return (i < ls.size() ? (ByteArray) ls.get(i) : null);
	}

	public final int size() {
		return ls.size();
	}
};
