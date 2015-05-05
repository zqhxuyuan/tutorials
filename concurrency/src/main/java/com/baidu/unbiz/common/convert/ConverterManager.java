package com.baidu.unbiz.common.convert;

/**
 * FIXME removed
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 下午8:00:13
 */
public abstract class ConverterManager {

    private static final ConverterManagerBean CONVERTER_MANAGER_BEAN = ConverterManagerBean.getInstance();

    public static void unregister(Class<?> type) {
        CONVERTER_MANAGER_BEAN.unregister(type);
    }

    public static TypeConverter<?> lookup(Class<?> type) {
        return CONVERTER_MANAGER_BEAN.lookup(type);
    }

    public static <T> T convertType(Object value, Class<T> destinationType) {
        return CONVERTER_MANAGER_BEAN.convertType(value, destinationType);
    }

}