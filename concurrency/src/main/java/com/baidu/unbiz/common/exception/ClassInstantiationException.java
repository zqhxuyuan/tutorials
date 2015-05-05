/**
 * 
 */
package com.baidu.unbiz.common.exception;

/**
 * 代表实例化类时失败的异常。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午2:17:05
 */
public class ClassInstantiationException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2178789451989790744L;

    private Class<?> clazz;

    /**
     * 构造一个空的异常.
     */
    public ClassInstantiationException() {
        super();
    }

    /**
     * 构造一个异常, 指明异常的详细信息.
     * 
     * @param message 详细信息
     */
    public ClassInstantiationException(String message) {
        super(message);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param cause 异常的起因
     */
    public ClassInstantiationException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param message 详细信息
     * @param cause 异常的起因
     */
    public ClassInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param clazz 目标类
     * @param message 详细信息
     */
    public ClassInstantiationException(Class<?> clazz, String message) {
        super(message);
        this.clazz = clazz;
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param clazz 目标类
     * @param message 详细信息
     * @param cause 异常的起因
     */
    public ClassInstantiationException(Class<?> clazz, String message, Throwable cause) {
        super(message, cause);
        this.clazz = clazz;
    }

    /**
     * 获取目标类
     */
    public Class<?> getClazz() {
        return clazz;
    }
}
