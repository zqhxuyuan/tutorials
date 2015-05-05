/**
 * 
 */
package com.baidu.unbiz.common.file;

import java.io.IOException;
import java.util.List;

import com.baidu.unbiz.common.collection.ListMap;
import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.io.ByteArray;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月27日 上午10:02:10
 */
public interface FileProcessor {

    void importBean(Object bean, String filePath) throws IOException;

    void importBeans(Class<?> clazz, Object[] beans, String filePath) throws IOException;

    void importBeans(String[] contents, String filePath) throws IOException;

    void importBeans(List<String> contents, String filePath) throws IOException;

    // FIXME
    void importBeans(String[] headers, Class<?> clazz, Object[] beans, String filePath) throws IOException;

    void importBeans(ListMap<String, String> headers, Class<?> clazz, Object[] beans, String filePath)
            throws IOException;

    List<String> collectData(FileBeanInfo beanInfo) throws IOException;

    void importBeans(FileBeanInfo beanInfo, String filePath) throws IOException;

    <T> T exportBean(Class<T> clazz, String filePath) throws IOException;

    <T> List<T> exportBeans(Class<T> clazz, String filePath) throws IOException;

    ByteArray toByteArray(FileBeanInfo beanInfo) throws IOException;

    ByteArray toByteArray(List<String> contents) throws IOException;

    ObjectConverter<Object> CONVERTER = ObjectConverter.getInstance();

}
