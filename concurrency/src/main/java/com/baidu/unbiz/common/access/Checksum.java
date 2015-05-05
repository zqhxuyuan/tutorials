/**
 * 
 */
package com.baidu.unbiz.common.access;

/**
 * 提供检验码
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午4:53:43
 */
public interface Checksum {

    /**
     * 获取校验码
     * 
     * @return 校验码
     */
    long checksum();

}
