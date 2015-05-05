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
 * @version create on 2014年7月28日 下午11:12:30
 */
public class DoubleConverter extends ObjectConverter<Double> implements TypeConverter<Double> {

    public DoubleConverter() {
        register(Double.class);
        register(double.class);
    }

    @Override
    public Double toConvert(String value) {
        return convert(value);
    }

    @Override
    public String fromConvert(Double value) {
        return String.valueOf(value);
    }

    public Double toConvert(Object value) {
        if (value.getClass() == Double.class) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return Double.valueOf(((Number) value).doubleValue());
        }

        return convert(value.toString());
    }

    private Double convert(String value) {
        try {
            String stringValue = value.trim();
            if (StringUtil.startsWithChar(stringValue, '+')) {
                stringValue = stringValue.substring(1);
            }
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new ConvertException(value, e);
        }
    }

}
