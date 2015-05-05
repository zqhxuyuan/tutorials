/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.baidu.unbiz.common.ReflectionUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 上午1:47:15
 */
public class MethodDescriptor extends Descriptor implements Getter, Setter {

    protected final Method method;
    protected final Type returnType;
    protected final Class<?> rawReturnType;
    protected final Class<?> rawReturnComponentType;
    protected final Class<?> rawReturnKeyComponentType;
    protected final Class<?>[] rawParameterTypes;
    protected final Class<?>[] rawParameterComponentTypes;

    public MethodDescriptor(ClassDescriptor classDescriptor, Method method) {
        super(classDescriptor, ReflectionUtil.isPublic(method));
        this.method = method;
        this.returnType = method.getGenericReturnType();
        this.rawReturnType = ReflectionUtil.getRawType(returnType, classDescriptor.getType());

        Class<?>[] componentTypes = ReflectionUtil.getComponentTypes(returnType, classDescriptor.getType());
        if (componentTypes != null) {
            this.rawReturnComponentType = componentTypes[componentTypes.length - 1];
            this.rawReturnKeyComponentType = componentTypes[0];
        } else {
            this.rawReturnComponentType = null;
            this.rawReturnKeyComponentType = null;
        }

        ReflectionUtil.forceAccess(method);

        Type[] params = method.getGenericParameterTypes();
        Type[] genericParams = method.getGenericParameterTypes();

        rawParameterTypes = new Class[params.length];
        rawParameterComponentTypes = genericParams.length == 0 ? null : new Class[params.length];

        for (int i = 0; i < params.length; i++) {
            Type type = params[i];
            rawParameterTypes[i] = ReflectionUtil.getRawType(type, classDescriptor.getType());
            if (rawParameterComponentTypes != null) {
                rawParameterComponentTypes[i] =
                        ReflectionUtil.getComponentType(genericParams[i], classDescriptor.getType());
            }
        }
    }

    public String getName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getRawReturnType() {
        return rawReturnType;
    }

    public Class<?> getRawReturnComponentType() {
        return rawReturnComponentType;
    }

    public Class<?> getRawReturnKeyComponentType() {
        return rawReturnKeyComponentType;
    }

    public Class<?>[] resolveRawReturnComponentTypes() {
        return ReflectionUtil.getComponentTypes(returnType, classDescriptor.getType());
    }

    public Class<?>[] getRawParameterTypes() {
        return rawParameterTypes;
    }

    public Class<?>[] getRawParameterComponentTypes() {
        return rawParameterComponentTypes;
    }

    public Object invokeGetter(Object target) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target);
    }

    public Class<?> getGetterRawType() {
        return getRawReturnType();
    }

    public Class<?> getGetterRawComponentType() {
        return getRawReturnComponentType();
    }

    public Class<?> getGetterRawKeyComponentType() {
        return getRawReturnKeyComponentType();
    }

    public void invokeSetter(Object target, Object argument) throws IllegalAccessException, InvocationTargetException {
        method.invoke(target, argument);
    }

    public Class<?> getSetterRawType() {
        return getRawParameterTypes()[0];
    }

    public Class<?> getSetterRawComponentType() {
        Class<?>[] ts = getRawParameterComponentTypes();
        if (ts == null) {
            return null;
        }
        return ts[0];
    }

    @Override
    public String toString() {
        return classDescriptor.getType().getSimpleName() + '#' + method.getName() + "()";
    }

}