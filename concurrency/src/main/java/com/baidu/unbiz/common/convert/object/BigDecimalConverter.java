/**
 * 
 */
package com.baidu.unbiz.common.convert.object;

import java.math.BigDecimal;

import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.convert.ConvertException;
import com.baidu.unbiz.common.convert.TypeConverter;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月7日 下午11:58:26
 */
public class BigDecimalConverter extends ObjectConverter<BigDecimal> implements TypeConverter<BigDecimal> {

    public BigDecimalConverter() {
        register(BigDecimal.class);
    }

    @Override
    public BigDecimal toConvert(String value) {
        return new BigDecimal(value.trim());
    }

    @Override
    public String fromConvert(BigDecimal value) {
        return String.valueOf(value);
    }

    public BigDecimal toConvert(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        try {
            return new BigDecimal(value.toString().trim());
        } catch (NumberFormatException e) {
            throw new ConvertException(value, e);
        }
    }

}