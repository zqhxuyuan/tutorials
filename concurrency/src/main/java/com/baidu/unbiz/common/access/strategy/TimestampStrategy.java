/**
 * 
 */
package com.baidu.unbiz.common.access.strategy;

import com.baidu.unbiz.common.access.AccessStrategy;

/**
 * 通过时间戳
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午4:47:10
 */
public class TimestampStrategy implements AccessStrategy {

    @Override
    public String find(long id) {
        return id + "_" + System.currentTimeMillis();
    }

}
