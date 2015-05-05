/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

import java.lang.reflect.Constructor;

import com.baidu.unbiz.common.ReflectionUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午8:24:16
 */
public class ConstructorDescriptor extends Descriptor {

    protected final Constructor<?> constructor;
    protected final Class<?>[] parameters;

    public ConstructorDescriptor(ClassDescriptor classDescriptor, Constructor<?> constructor) {
        super(classDescriptor, ReflectionUtil.isPublic(constructor));
        this.constructor = constructor;
        this.parameters = constructor.getParameterTypes();

        ReflectionUtil.forceAccess(constructor);
    }

    @Override
    public String getName() {
        return constructor.getName();
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }

    public boolean isDefault() {
        return parameters.length == 0;
    }

}