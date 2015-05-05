/**
 * 
 */
package com.baidu.unbiz.common.access;

import com.baidu.unbiz.common.io.ByteArray;

/**
 * 访问介质
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午2:49:27
 */
public interface Resource extends Checksum {

    /**
     * 获取唯一<code>id</code>
     * 
     * @return 唯一<code>id</code>
     */
    long getId();

    /**
     * 获取二进制内容
     * 
     * @return 二进制内容
     */
    ByteArray getBody();

    /**
     * 获取头信息
     * 
     * @return 头信息 @see ResourceHeader
     */
    ResourceHeader getHeader();

    /**
     * 获取大小
     * 
     * @return how long is resource
     */
    long getSize();

}
