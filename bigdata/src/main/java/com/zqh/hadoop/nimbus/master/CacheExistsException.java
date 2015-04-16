package com.zqh.hadoop.nimbus.master;

/**
 * An exception to represent that a Cache already exists.
 */
public class CacheExistsException extends Exception {

	private static final long serialVersionUID = 6305774187977276336L;
	private String msg = null;

	/**
	 * Initializes a new instance of the CacheExistsException class.
	 * 
	 * @param name
	 *            The name of the Cache.
	 */
	public CacheExistsException(String name) {
		msg = "Cache " + name + " already exists.";
	}

	@Override
	public String getMessage() {
		return msg;
	}
}
