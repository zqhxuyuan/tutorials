/**
 * 
 */
package com.baidu.unbiz.common.exception;

/**
 * 代表<code>META-INF/services/</code>中的文件未找到或读文件失败的异常。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午2:17:28
 */
public class ServiceNotFoundException extends ClassNotFoundException {
    /**
	 * 
	 */
    private static final long serialVersionUID = -6525751787646334079L;

    /**
     * 构造一个空的异常.
     */
    public ServiceNotFoundException() {
        super();
    }

    /**
     * 构造一个异常, 指明异常的详细信息.
     * 
     * @param message 详细信息
     */
    public ServiceNotFoundException(String message) {
        super(message);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param cause 异常的起因
     */
    public ServiceNotFoundException(Throwable cause) {
        super(null, cause);

    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param message 详细信息
     * @param cause 异常的起因
     */
    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
