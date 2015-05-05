/**
 * 
 */
package com.baidu.unbiz.common.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 简单点，就放在<code>ElementType.FIELD</code>上
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月27日 上午7:42:24
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ProcessedField {

    /** 在文件列中的位置 */
    int index();

    // /** 转换后的类型 */
    // Class<?> type() default String.class;

    // /** 转换后的格式 */
    // String format() default "";

    /** 转换后的列名称 */
    String title() default "";
}
