/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import com.baidu.unbiz.common.ReflectionUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午8:39:13
 */
public class FieldDescriptor extends Descriptor implements Getter, Setter {

    protected final Field field;
    protected final Type type;
    protected final Class<?> rawType;
    protected final Class<?> rawComponentType;
    protected final Class<?> rawKeyComponentType;

    public FieldDescriptor(ClassDescriptor classDescriptor, Field field) {
        super(classDescriptor, ReflectionUtil.isPublic(field));
        this.field = field;
        this.type = field.getGenericType();
        this.rawType = ReflectionUtil.getRawType(type, classDescriptor.getType());

        Class<?>[] componentTypes = ReflectionUtil.getComponentTypes(type, classDescriptor.getType());
        if (componentTypes != null) {
            this.rawComponentType = componentTypes[componentTypes.length - 1];
            this.rawKeyComponentType = componentTypes[0];
        } else {
            this.rawComponentType = null;
            this.rawKeyComponentType = null;
        }

        ReflectionUtil.forceAccess(field);
    }

    @Override
    public String getName() {
        return field.getName();
    }

    public Field getField() {
        return field;
    }

    public Class<?> getRawType() {
        return rawType;
    }

    public Class<?> getRawComponentType() {
        return rawComponentType;
    }

    public Class<?> getRawKeyComponentType() {
        return rawKeyComponentType;
    }

    public Class<?>[] resolveRawComponentTypes() {
        return ReflectionUtil.getComponentTypes(type, classDescriptor.getType());
    }

    public Object invokeGetter(Object target) throws InvocationTargetException, IllegalAccessException {
        return field.get(target);
    }

    public Class<?> getGetterRawType() {
        return getRawType();
    }

    public Class<?> getGetterRawComponentType() {
        return getRawComponentType();
    }

    public Class<?> getGetterRawKeyComponentType() {
        return getRawKeyComponentType();
    }

    public void invokeSetter(Object target, Object argument) throws IllegalAccessException {
        field.set(target, argument);
    }

    public Class<?> getSetterRawType() {
        return getRawType();
    }

    public Class<?> getSetterRawComponentType() {
        return getRawComponentType();
    }

    @Override
    public String toString() {
        return classDescriptor.getType().getSimpleName() + '#' + field.getName();
    }

}