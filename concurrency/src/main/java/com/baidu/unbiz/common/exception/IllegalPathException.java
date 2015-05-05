/**
 * 
 */
package com.baidu.unbiz.common.exception;

/**
 * 代表非法的路径。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午1:50:42
 */
public class IllegalPathException extends IllegalArgumentException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1434004725746713564L;

    public IllegalPathException() {
        super();
    }

    public IllegalPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPathException(String s) {
        super(s);
    }

    public IllegalPathException(Throwable cause) {
        super(cause);
    }

}
