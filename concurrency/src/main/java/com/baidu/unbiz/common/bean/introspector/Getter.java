/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午8:39:19
 */
public interface Getter {

    Object invokeGetter(Object target) throws InvocationTargetException, IllegalAccessException;

    Class<?> getGetterRawType();

    Class<?> getGetterRawComponentType();

    Class<?> getGetterRawKeyComponentType();

}