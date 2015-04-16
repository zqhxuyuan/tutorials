package com.zqh.hadoop.nimbus.master;

/**
 * An exception to represent that a Cache does not exist.
 */
public class CacheDoesNotExistException extends Exception {

	private static final long serialVersionUID = 5567615222954888496L;
	private String name = null;

	/**
	 * Initializes a new instance of the CacheDoesNotExistException class.
	 * 
	 * @param name
	 *            The name of the Cache.
	 */
	public CacheDoesNotExistException(String name) {
		this.name = name;
	}

	@Override
	public String getMessage() {
		return "Cache \"" + name + "\" not exist";
	}
}
