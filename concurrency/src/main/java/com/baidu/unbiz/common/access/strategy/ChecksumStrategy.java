/**
 * 
 */
package com.baidu.unbiz.common.access.strategy;

import com.baidu.unbiz.common.access.Checksum;
import com.baidu.unbiz.common.access.AccessStrategy;

/**
 * 通过<code>checksum</code>来关联
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午4:40:57
 */
public class ChecksumStrategy implements AccessStrategy {

    private Checksum acs;

    public ChecksumStrategy(Checksum acs) {
        this.acs = acs;
    }

    @Override
    public String find(long id) {
        return id + "_" + acs.checksum();
    }

}
