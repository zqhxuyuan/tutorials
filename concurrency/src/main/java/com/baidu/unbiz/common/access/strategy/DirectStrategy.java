/**
 * 
 */
package com.baidu.unbiz.common.access.strategy;

import com.baidu.unbiz.common.access.AccessStrategy;

/**
 * 直接通过<code>id</code>对应
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午4:41:28
 */
public class DirectStrategy implements AccessStrategy {

    @Override
    public String find(long id) {
        return String.valueOf(id);
    }

}
