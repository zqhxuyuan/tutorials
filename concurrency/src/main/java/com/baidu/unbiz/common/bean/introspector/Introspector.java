/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午7:59:57
 */
public interface Introspector {

    ClassDescriptor lookup(Class<?> type);

    ClassDescriptor register(Class<?> type);

    void reset();

}
