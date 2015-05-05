package com.baidu.unbiz.common.convert.object;

import com.baidu.unbiz.common.ClassLoaderUtil;
import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.convert.ConvertException;
import com.baidu.unbiz.common.convert.TypeConverter;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 下午2:56:42
 */
public class ClassConverter extends ObjectConverter<Class<?>> implements TypeConverter<Class<?>> {

    public ClassConverter() {
        typeConverters.put(Class.class, this);
    }

    @Override
    public Class<?> toConvert(String value) {
        try {
            return ClassLoaderUtil.loadClass(value);
        } catch (ClassNotFoundException e) {
            throw new ConvertException(value, e);
        }
    }

    @Override
    public String fromConvert(Class<?> value) {
        return String.valueOf(value);
    }

    public Class<?> toConvert(Object value) {
        if (value.getClass() == Class.class) {
            return (Class<?>) value;
        }
        try {
            return ClassLoaderUtil.loadClass(value.toString().trim());
        } catch (ClassNotFoundException e) {
            throw new ConvertException(value, e);
        }
    }

}