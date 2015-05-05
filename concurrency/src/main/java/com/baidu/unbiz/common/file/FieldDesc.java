/**
 * 
 */
package com.baidu.unbiz.common.file;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月27日 上午7:57:26
 */
public class FieldDesc {

    private int order;

    private String title;

    private Field field;

    private Class<?> type;

    public int order() {
        return order;
    }

    public FieldDesc order(int order) {
        this.order = order;
        return this;
    }

    public String title() {
        return title;
    }

    public FieldDesc title(String title) {
        this.title = title;
        return this;
    }

    public Field field() {
        return field;
    }

    public FieldDesc field(Field field) {
        this.field = field;
        return this;
    }

    public Class<?> type() {
        return type;
    }

    public FieldDesc type(Class<?> type) {
        this.type = type;
        return this;
    }

    public static final Comparator<FieldDesc> comparator = new Comparator<FieldDesc>() {
        @Override
        public int compare(FieldDesc o1, FieldDesc o2) {
            int result = o1.order - o2.order;
            if (0 == result) {
                throw new RuntimeException("field1:" + o1.field + "/field2:" + o2.field
                        + " has the same index value, internal error.");
            }

            return result;
        }
    };

}
