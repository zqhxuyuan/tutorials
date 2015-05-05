/**
 * 
 */
package com.baidu.unbiz.common.convert.object;

import com.baidu.unbiz.common.ArrayUtil;
import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.convert.TypeConverter;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月28日 下午11:15:07
 */
public class StringConverter extends ObjectConverter<String> implements TypeConverter<String> {

    public StringConverter() {
        register(String.class);
    }

    @Override
    public String toConvert(String value) {
        return value;
    }

    @Override
    public String fromConvert(String value) {
        return value;
    }

    public String toConvert(Object value) {
        if (value instanceof CharSequence) { // for speed
            return value.toString();
        }
        Class<?> type = value.getClass();
        if (type == Class.class) {
            return ((Class<?>) value).getName();
        }

        return ArrayUtil.toString(value);
    }

}
