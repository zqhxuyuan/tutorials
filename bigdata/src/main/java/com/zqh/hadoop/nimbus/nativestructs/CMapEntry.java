package com.zqh.hadoop.nimbus.nativestructs;

import java.util.Map.Entry;

public class CMapEntry implements Entry<String, String> {

	private String key = null;
	private String value = null;

	public CMapEntry() {

	}

	public CMapEntry(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	public String setKey(String key) {
		String oldKey = this.key;
		this.key = key;
		return oldKey;
	}

	@Override
	public String setValue(String value) {
		String oldValue = this.value;
		this.value = value;
		return oldValue;
	}
	
	@Override
	public String toString() {
		return key + "\t" + value;
	}
}
