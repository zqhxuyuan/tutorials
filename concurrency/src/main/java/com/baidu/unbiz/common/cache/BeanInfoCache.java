/**
 * 
 */
package com.baidu.unbiz.common.cache;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.ExceptionUtil;
import com.baidu.unbiz.common.able.Computable;
import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午6:40:00
 */
public class BeanInfoCache extends CachedLogger {

    private static final BeanInfoCache instance = new BeanInfoCache();

    private final Computable<String, Map<String, PropertyDescriptor>> propertyCached =
            new ConcurrentCache<String, Map<String, PropertyDescriptor>>();

    private final Computable<String, Map<String, MethodDescriptor>> methodCached =
            new ConcurrentCache<String, Map<String, MethodDescriptor>>();

    private BeanInfoCache() {

    }

    public static BeanInfoCache getInstance() {
        return instance;
    }

    public BeanInfo getBeanInfo(Class<?> beanClass) {
        try {
            return Introspector.getBeanInfo(beanClass, Object.class);
        } catch (IntrospectionException e) {
            logger.error("Introspector.getBeanInfo {} error", e, beanClass);
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    public BeanInfo getBeanInfo(Class<?> beanClass, Class<?> stopClass) {
        try {
            return Introspector.getBeanInfo(beanClass, stopClass);
        } catch (IntrospectionException e) {
            logger.error("Introspector.getBeanInfo {} error", e, beanClass);
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    public BeanDescriptor getBeanDescriptor(Class<?> beanClass) {
        BeanInfo beanInfo = getBeanInfo(beanClass);

        return beanInfo.getBeanDescriptor();
    }

    private Map<String, PropertyDescriptor> getPropertyMap(final Class<?> beanClass) {
        return propertyCached.get(beanClass.getName(), new Callable<Map<String, PropertyDescriptor>>() {
            @Override
            public Map<String, PropertyDescriptor> call() throws Exception {
                BeanInfo beanInfo = getBeanInfo(beanClass);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                Map<String, PropertyDescriptor> map = CollectionUtil.createHashMap(propertyDescriptors.length);
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    map.put(propertyDescriptor.getName(), propertyDescriptor);
                }

                return map;
            }
        });
    }

    public Map<String, PropertyDescriptor> getPropertyDescriptor(final Class<?> beanClass) {
        Map<String, PropertyDescriptor> map = getPropertyMap(beanClass);

        return Collections.unmodifiableMap(map);
    }

    public PropertyDescriptor getPropertyDescriptor(final Class<?> beanClass, String propertyName) {
        Map<String, PropertyDescriptor> map = getPropertyMap(beanClass);
        return map.get(propertyName);
    }

    private Map<String, MethodDescriptor> getMethodMap(final Class<?> beanClass) {
        return methodCached.get(beanClass.getName(), new Callable<Map<String, MethodDescriptor>>() {
            @Override
            public Map<String, MethodDescriptor> call() throws Exception {
                BeanInfo beanInfo = getBeanInfo(beanClass);
                MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
                Map<String, MethodDescriptor> map = CollectionUtil.createHashMap(methodDescriptors.length);
                for (MethodDescriptor methodDescriptor : methodDescriptors) {
                    map.put(methodDescriptor.getName(), methodDescriptor);
                }

                return map;
            }
        });
    }

    public Map<String, MethodDescriptor> getMethodDescriptor(final Class<?> beanClass) {
        Map<String, MethodDescriptor> map = getMethodMap(beanClass);

        return Collections.unmodifiableMap(map);
    }

    public MethodDescriptor getMethodDescriptor(final Class<?> beanClass, String methodName) {
        Map<String, MethodDescriptor> map = getMethodMap(beanClass);

        return map.get(methodName);
    }

}
