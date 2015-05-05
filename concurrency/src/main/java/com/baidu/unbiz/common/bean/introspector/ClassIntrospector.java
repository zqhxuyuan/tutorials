/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午8:22:01
 */
public class ClassIntrospector {

    private static final Introspector introspector = new CachingIntrospector();

    public static ClassDescriptor lookup(Class<?> type) {
        return introspector.lookup(type);
    }

    public static ClassDescriptor register(Class<?> type) {
        return introspector.register(type);
    }

    public static void reset() {
        introspector.reset();
    }

}