package com.zqh.hadoop.nimbus.client;

import java.io.IOException;

public class CacheletNotConnectedException extends IOException {
	
	private static final long serialVersionUID = -4964573321449493981L;
	
	private Exception parent = null;
	private String id;

	public CacheletNotConnectedException(String host) {
		this(host, null);
	}

	public CacheletNotConnectedException(String host, Exception e) {
		this.id = host;
		parent = e;
	}
	
	public CacheletNotConnectedException(int id) {
		this(id, null);
	}

	public CacheletNotConnectedException(int id, Exception e) {
		this.id = Integer.toString(id);
		parent = e;
	}

	@Override
	public String getMessage() {
		return "Cachelet " + id + " is not connected" + (parent != null ? parent.getMessage() : "");
	}
}
