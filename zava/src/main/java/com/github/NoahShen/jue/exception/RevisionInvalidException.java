/**
 * 
 */
package com.github.NoahShen.jue.exception;

/**
 * 版本验证错误异常
 * @author noah
 *
 */
public class RevisionInvalidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2736649292576386777L;

	public RevisionInvalidException() {
		super();
	}

	public RevisionInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public RevisionInvalidException(String message) {
		super(message);
	}

	public RevisionInvalidException(Throwable cause) {
		super(cause);
	}

}
