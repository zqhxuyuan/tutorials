/**
 * 
 */
package com.baidu.unbiz.common.convert;

import com.baidu.unbiz.common.exception.UncheckedException;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 上午3:34:57
 */
public class ConvertException extends UncheckedException {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4896121228312641852L;

    public ConvertException(Throwable t) {
        super(t);
    }

    public ConvertException() {
        super();
    }

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String message, Throwable t) {
        super(message, t);
    }

    public ConvertException(Object value) {
        this("Unable to convert value: " + value);
    }

    public ConvertException(Object value, Throwable t) {
        this("Unable to convert value: " + value, t);
    }
}