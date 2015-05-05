/**
 * 
 */
package com.github.NoahShen.jue.file;

/**
 * 校验错误异常
 * @author noah
 *
 */
public class ChecksumException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1432423965408066414L;

	public ChecksumException() {
		super();
	}

	public ChecksumException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChecksumException(String message) {
		super(message);
	}

	public ChecksumException(Throwable cause) {
		super(cause);
	}

}
