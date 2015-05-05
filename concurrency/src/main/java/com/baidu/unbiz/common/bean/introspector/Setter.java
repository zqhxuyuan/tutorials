/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 上午1:06:15
 */
public interface Setter {

    void invokeSetter(Object target, Object argument) throws IllegalAccessException, InvocationTargetException;

    Class<?> getSetterRawType();

    Class<?> getSetterRawComponentType();

}