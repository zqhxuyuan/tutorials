/**
 * 
 */
package com.baidu.unbiz.common.exception;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 上午3:26:11
 */
public class UncheckedException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5325929756409272324L;

    /**
     * 构造一个空的异常.
     */
    public UncheckedException() {
        super();
    }

    /**
     * 构造一个异常, 指明异常的详细信息.
     * 
     * @param message 详细信息
     */
    public UncheckedException(String message) {
        super(message);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param cause 异常的起因
     */
    public UncheckedException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param message 详细信息
     * @param cause 异常的起因
     */
    public UncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

}
