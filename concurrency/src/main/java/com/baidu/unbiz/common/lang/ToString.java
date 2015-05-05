/**
 * 
 */
package com.baidu.unbiz.common.lang;

import java.io.Serializable;

import com.baidu.unbiz.common.able.ToStringable;
import com.baidu.unbiz.common.apache.ToStringBuilder;
import com.baidu.unbiz.common.apache.ToStringStyle;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 下午11:56:52
 */
public class ToString implements ToStringable, Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -7273706793161702222L;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
