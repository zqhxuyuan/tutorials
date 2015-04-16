package com.zqh.hadoop.nimbus.client;

import java.io.IOException;

public class CacheletsUnavailableException extends IOException {

	private static final long serialVersionUID = -1964767893018564264L;
	private static final String msg = "All Cachelets that contain this element are currently unavailable.";
	
	public String getMessage() {
		return msg;
	}
}
