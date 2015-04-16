package com.zqh.hadoop.nimbus.master;

import java.io.IOException;

/**
 * An exception to represent that a Cache was not created.
 */
public class FailedToCreateCacheException extends IOException {

	private static final long serialVersionUID = -4428561702630686666L;
	private String msg = null;

	public FailedToCreateCacheException(String name) {
		msg = "Failed to create Cache " + name
				+ ".  Check the Nimbus master log.";
	}

	public String getMessage() {
		return msg;
	}
}
