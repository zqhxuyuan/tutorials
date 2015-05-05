/**
 * 
 */
package com.baidu.unbiz.common.exception;

/**
 * 代表未预期的失败。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午2:09:40
 */
public class UnexpectedFailureException extends RuntimeException {
    private static final long serialVersionUID = -8227335536836081391L;

    /**
     * 构造一个空的异常.
     */
    public UnexpectedFailureException() {
        super();
    }

    /**
     * 构造一个异常, 指明异常的详细信息.
     * 
     * @param message 详细信息
     */
    public UnexpectedFailureException(String message) {
        super(message);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param cause 异常的起因
     */
    public UnexpectedFailureException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param message 详细信息
     * @param cause 异常的起因
     */
    public UnexpectedFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
