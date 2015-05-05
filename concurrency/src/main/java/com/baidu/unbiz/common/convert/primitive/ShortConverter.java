/**
 * 
 */
package com.baidu.unbiz.common.convert.primitive;

import com.baidu.unbiz.common.StringUtil;
import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.convert.ConvertException;
import com.baidu.unbiz.common.convert.TypeConverter;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月28日 下午11:13:39
 */
public class ShortConverter extends ObjectConverter<Short> implements TypeConverter<Short> {

    public ShortConverter() {
        register(Short.class);
        register(short.class);
    }

    @Override
    public Short toConvert(String value) {
        return convert(value);
    }

    @Override
    public String fromConvert(Short value) {
        return String.valueOf(value);
    }

    public Short toConvert(Object value) {
        if (value.getClass() == Short.class) {
            return (Short) value;
        }
        if (value instanceof Number) {
            return Short.valueOf(((Number) value).shortValue());
        }

        return convert(value.toString());
    }

    private Short convert(String value) {
        try {
            String stringValue = value.trim();
            if (StringUtil.startsWithChar(stringValue, '+')) {
                stringValue = stringValue.substring(1);
            }
            return Short.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new ConvertException(value, e);
        }
    }

}
