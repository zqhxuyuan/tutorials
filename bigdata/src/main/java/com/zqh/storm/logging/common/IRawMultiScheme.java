package com.zqh.storm.logging.common;

import backtype.storm.tuple.Fields;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface IRawMultiScheme extends Serializable {
	public Iterable<List<Object>> deserialize(byte[] ser) throws UnsupportedEncodingException;
	public Fields getOutputFields();
}
