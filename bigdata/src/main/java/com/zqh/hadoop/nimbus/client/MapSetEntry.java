package com.zqh.hadoop.nimbus.client;

import java.util.Map.Entry;

public class MapSetEntry implements Entry<String, String> {

	private String key, value;

	public MapSetEntry(String key, String value) {
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

	@Override
	public String setValue(String value) {
		throw new UnsupportedOperationException(
				"MapSetEntry::setValue is not supported");
	}
}
