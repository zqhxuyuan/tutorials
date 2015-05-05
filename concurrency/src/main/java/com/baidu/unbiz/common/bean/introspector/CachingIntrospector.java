/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

import java.util.Map;

import com.baidu.unbiz.common.CollectionUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午7:58:25
 */
public class CachingIntrospector implements Introspector {

    protected final Map<Class<?>, ClassDescriptor> cache = CollectionUtil.createHashMap();
    protected final boolean scanAccessible;
    protected final boolean enhancedProperties;
    protected final boolean includeFieldsAsProperties;
    protected final String propertyFieldPrefix;

    public CachingIntrospector() {
        this(true, true, true, null);
    }

    public CachingIntrospector(boolean scanAccessible, boolean enhancedProperties, boolean includeFieldsAsProperties,
            String propertyFieldPrefix) {
        this.scanAccessible = scanAccessible;
        this.enhancedProperties = enhancedProperties;
        this.includeFieldsAsProperties = includeFieldsAsProperties;
        this.propertyFieldPrefix = propertyFieldPrefix;
    }

    public ClassDescriptor lookup(Class<?> type) {
        ClassDescriptor cd = cache.get(type);
        if (cd != null) {
            cd.increaseUsageCount();
            return cd;
        }
        cd = describeClass(type);
        cache.put(type, cd);
        return cd;
    }

    public ClassDescriptor register(Class<?> type) {
        ClassDescriptor cd = describeClass(type);
        cache.put(type, cd);
        return cd;
    }

    protected ClassDescriptor describeClass(Class<?> type) {
        return new ClassDescriptor(type, scanAccessible, enhancedProperties, includeFieldsAsProperties,
                propertyFieldPrefix);
    }

    public void reset() {
        cache.clear();
    }

}