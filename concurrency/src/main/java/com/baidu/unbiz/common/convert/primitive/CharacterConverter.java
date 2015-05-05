/**
 * 
 */
package com.baidu.unbiz.common.convert.primitive;

import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.convert.TypeConverter;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月28日 下午11:11:42
 */
public class CharacterConverter extends ObjectConverter<Character> implements TypeConverter<Character> {

    public CharacterConverter() {
        register(Character.class);
        register(char.class);
    }

    @Override
    public Character toConvert(String value) {
        return value.charAt(0);
    }

    @Override
    public String fromConvert(Character value) {
        return String.valueOf(value);
    }

    public Character toConvert(Object value) {
        if (value.getClass() == Character.class) {
            return (Character) value;
        }
        if (value instanceof Number) {
            char c = (char) ((Number) value).intValue();
            return Character.valueOf(c);
        }

        return Character.valueOf(value.toString().charAt(0));
    }

}
