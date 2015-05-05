/**
 * 
 */
package com.baidu.unbiz.common.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月28日 下午11:08:07
 */
public interface TypeConverter<T> {

    T toConvert(String value);

    String fromConvert(T value);

    T toConvert(Object value);

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Convert {

    }

}
