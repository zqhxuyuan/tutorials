package com.baidu.unbiz.common.division;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 上午3:58:38
 */
public class DivisionException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = -693227965893118092L;

    public DivisionException() {
        super();
    }

    public DivisionException(String msg) {
        super(msg);
    }

    public DivisionException(Throwable cause) {
        super(cause);
    }

    public DivisionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
