package com.baidu.unbiz.common.convert;

import java.lang.reflect.Array;

import com.baidu.unbiz.common.ClassUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 下午8:00:41
 */
public class ConverterManagerBean {

    private static ConverterManagerBean instance = new ConverterManagerBean();

    protected static ObjectConverter<?> objectConverter = ObjectConverter.getInstance();

    private ConverterManagerBean() {

    }

    public static ConverterManagerBean getInstance() {
        return instance;
    }

    public void register(Class<?> type, TypeConverter<?> converter) {
        ObjectConverter.typeConverters.put(type, converter);
    }

    public void unregister(Class<?> type) {
        ObjectConverter.typeConverters.remove(type);
    }

    public TypeConverter<?> lookup(Class<?> type) {
        return ObjectConverter.typeConverters.get(type);
    }

    public <T> T convertType(Object value, Class<T> destinationType) {
        TypeConverter<?> converter = lookup(destinationType);

        if (converter != null) {
            try {
                @SuppressWarnings("unchecked")
                T result = (T) converter.toConvert(value);
                return result;
            } catch (ConvertException tcex) {
                throw new ClassCastException("Unable to convert to type: " + destinationType.getName() + '\n'
                        + tcex.toString());
            }
        }

        if (value == null) {
            return null;
        }

        if (ClassUtil.isInstance(destinationType, value)) {
            @SuppressWarnings("unchecked")
            T result = (T) value;
            return result;
        }

        if (destinationType.isArray()) {
            return convertArray(value, destinationType);
        }

        if (destinationType.isEnum()) {
            Object[] enums = destinationType.getEnumConstants();
            String valStr = value.toString();
            for (Object object : enums) {
                if (object.toString().equals(valStr)) {
                    @SuppressWarnings("unchecked")
                    T result = (T) object;
                    return result;
                }
            }
        }

        throw new ClassCastException("Unable to cast to type: " + destinationType.getName());
    }

    @SuppressWarnings({ "unchecked" })
    private <T> T convertArray(Object value, Class<T> destinationType) {
        Class<?> componentType = destinationType.getComponentType();

        if (!value.getClass().isArray()) {
            T[] result = (T[]) Array.newInstance(componentType, 1);
            result[0] = (T) convertType(value, componentType);
            return (T) result;
        }

        // value is an array
        Object[] array = (Object[]) value;
        T[] result = (T[]) Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = (T) convertType(array[i], componentType);
        }
        return (T) result;
    }

}