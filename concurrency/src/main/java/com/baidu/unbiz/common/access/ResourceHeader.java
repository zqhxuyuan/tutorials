/**
 * 
 */
package com.baidu.unbiz.common.access;

import java.util.Map;
import java.util.Set;

/**
 * <code>Access</code>头信息
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午3:17:19
 */
public interface ResourceHeader {

    /**
     * 获取编码方式
     * 
     * @return 编码方式
     */
    String encode();

    /**
     * 可选的扩展名
     * 
     * @return 扩展名
     */
    String ext();

    /**
     * 获取属性
     * 
     * @param name 属性名
     * @return 属性值
     */
    String getAttribute(String name);

    /**
     * 设置属性
     * 
     * @param name 属性名
     * @param value 属性值
     * @return 自身，方便快速设置
     */
    ResourceHeader setAttribute(String name, String value);

    /**
     * 获取可迭代的属性信息
     * 
     * @return 头文件信息
     */
    Set<Map.Entry<String, String>> getInfo();

    ResourceHeader encode(String encode);

    ResourceHeader ext(String ext);

}
