/**
 * 
 */
package com.baidu.unbiz.common.file.csv;

import static com.baidu.unbiz.common.StringPool.Charset.GBK;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;

import com.baidu.unbiz.common.Assert;
import com.baidu.unbiz.common.ClassLoaderUtil;
import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.CsvUtil;
import com.baidu.unbiz.common.ObjectUtil;
import com.baidu.unbiz.common.ReflectionUtil;
import com.baidu.unbiz.common.able.Computable;
import com.baidu.unbiz.common.cache.ConcurrentCache;
import com.baidu.unbiz.common.collection.ListMap;
import com.baidu.unbiz.common.file.FieldDesc;
import com.baidu.unbiz.common.file.FileBeanInfo;
import com.baidu.unbiz.common.file.FileProcessor;
import com.baidu.unbiz.common.file.ProcessedCache;
import com.baidu.unbiz.common.io.ByteArray;
import com.baidu.unbiz.common.io.ByteArrayOutputStream;
import com.baidu.unbiz.common.io.ReaderUtil;
import com.baidu.unbiz.common.io.WriterUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月27日 上午9:58:38
 */
public class CsvProcessor implements FileProcessor {

    private final ProcessedCache processeCache = ProcessedCache.getInstance();

    private final Computable<String, Field[]> fieldCache = new ConcurrentCache<String, Field[]>();

    @Override
    public void importBean(Object bean, String filePath) throws IOException {
        Class<?> clazz = bean.getClass();

        List<FieldDesc> list = processeCache.getFieldDescs(clazz);
        Assert.assertTrue(CollectionUtil.isNotEmpty(list), "FieldDesc is not empty");

        String data = toCsvLine(list, bean);
        List<String> titles = processeCache.getTitles(clazz);

        if (CollectionUtil.isEmpty(titles)) {
            WriterUtil.writeLinesAndClose(filePath, data, false, GBK);
            return;
        }

        String title = CsvUtil.toCsvString(titles);
        WriterUtil.writeLinesAndClose(filePath, false, GBK, new Object[] { title, data });
    }

    @Override
    public void importBeans(Class<?> clazz, Object[] beans, String filePath) throws IOException {
        List<FieldDesc> list = processeCache.getFieldDescs(clazz);
        Assert.assertTrue(CollectionUtil.isNotEmpty(list), "FieldDesc is not empty");

        List<String> datas = CollectionUtil.createArrayList(beans.length + 1);
        List<String> titles = processeCache.getTitles(clazz);
        if (CollectionUtil.isNotEmpty(titles)) {
            datas.add(CsvUtil.toCsvString(titles));
        }

        for (Object bean : beans) {
            datas.add(toCsvLine(list, bean));
        }

        WriterUtil.writeLinesAndClose(filePath, datas, GBK);

    }

    @Override
    public void importBeans(String[] contents, String filePath) throws IOException {
        List<String> datas = CollectionUtil.createArrayList(contents.length);

        for (String content : contents) {
            datas.add(content);
        }

        WriterUtil.writeLinesAndClose(filePath, datas, GBK);
    }

    @Override
    public void importBeans(List<String> contents, String filePath) throws IOException {
        List<String> datas = CollectionUtil.createArrayList(contents.size());

        for (String content : contents) {
            datas.add(content);
        }

        WriterUtil.writeLinesAndClose(filePath, datas, GBK);

    }

    @Override
    public <T> T exportBean(Class<T> clazz, String filePath) throws IOException {
        List<FieldDesc> list = processeCache.getFieldDescs(clazz);
        Assert.assertTrue(CollectionUtil.isNotEmpty(list), "FieldDesc is not empty");

        List<String> lines = ReaderUtil.readLinesAndClose(filePath, Charset.forName(GBK));
        List<String> titles = processeCache.getTitles(clazz);
        int size = lines.size();

        if (CollectionUtil.isEmpty(titles)) {
            // FIXME
            Assert.assertTrue(size == 1, "record size wrong");
        } else {
            Assert.assertTrue(size == 2, "record size wrong");
        }

        String[] strings = CsvUtil.toStringArray(lines.get(size - 1));
        Assert.assertTrue(list.size() == strings.length, "read record error");

        return stringsToBean(clazz, list, strings);
    }

    @Override
    public <T> List<T> exportBeans(Class<T> clazz, String filePath) throws IOException {
        List<FieldDesc> list = processeCache.getFieldDescs(clazz);
        Assert.assertTrue(CollectionUtil.isNotEmpty(list), "FieldDesc is not empty");

        List<String> lines = ReaderUtil.readLinesAndClose(filePath, Charset.forName(GBK));
        List<String> titles = processeCache.getTitles(clazz);

        if (CollectionUtil.isEmpty(titles)) {
            return linesToList(clazz, list, lines);
        }
        // FIXME 此时应安全
        lines.remove(0);
        return linesToList(clazz, list, lines);
    }

    private String toCsvLine(List<FieldDesc> list, Object bean) {
        List<String> strings = CollectionUtil.createArrayList();
        for (FieldDesc desc : list) {
            Object value = ReflectionUtil.readField(desc.field(), bean);
            if (value == null) {
                // FIXME
                strings.add(null);
                continue;
            }

            String result = CONVERTER.fromConvert(value);
            strings.add((result != null) ? result : String.valueOf(value));
        }

        return CsvUtil.toCsvString(strings);
    }

    private <T> List<T> linesToList(Class<T> clazz, List<FieldDesc> list, List<String> lines) {
        List<T> result = CollectionUtil.createArrayList(lines.size());

        for (String line : lines) {
            String[] strings = CsvUtil.toStringArray(line);
            T bean = stringsToBean(clazz, list, strings);

            result.add(bean);
        }

        return result;
    }

    private <T> T stringsToBean(Class<T> clazz, List<FieldDesc> list, String[] strings) {
        T bean = ClassLoaderUtil.newInstance(clazz);

        for (int i = 0, size = strings.length; i < size; i++) {
            FieldDesc fieldDesc = list.get(i);
            Field field = fieldDesc.field();
            String value = strings[i];
            if (value != null) {
                Object result = CONVERTER.toConvert(value, fieldDesc.type());

                ReflectionUtil.writeField(field, bean, (result != null) ? result : value);
            }

        }

        return bean;

    }

    @Override
    public void importBeans(String[] headers, Class<?> clazz, Object[] beans, String filePath) throws IOException {
        List<FieldDesc> list = processeCache.getFieldDescs(clazz);
        Assert.assertTrue(CollectionUtil.isNotEmpty(list), "FieldDesc is not empty");

        List<String> datas = CollectionUtil.createArrayList(headers.length + beans.length);

        for (String header : headers) {
            datas.add(header);
        }

        for (Object bean : beans) {
            datas.add(toCsvLine(list, bean));
        }

        WriterUtil.writeLinesAndClose(filePath, datas, GBK);

    }

    @Override
    public void importBeans(ListMap<String, String> headers, Class<?> clazz, Object[] beans, String filePath)
            throws IOException {
        List<FieldDesc> list = processeCache.getFieldDescs(clazz);
        Assert.assertTrue(CollectionUtil.isNotEmpty(list), "FieldDesc is not empty");

        List<String> datas = CollectionUtil.createArrayList(headers.size() + beans.length);

        for (int index = 0, size = headers.size(); index < size; index++) {
            datas.add(CsvUtil.toCsvString(headers.getKey(index), headers.get(index)));
        }

        for (Object bean : beans) {
            datas.add(toCsvLine(list, bean));
        }

        WriterUtil.writeLinesAndClose(filePath, datas, GBK);

    }

    // FIXME 这个为北斗报表而生的东西
    @Override
    public List<String> collectData(FileBeanInfo beanInfo) throws IOException {
        ListMap<String, String> headers = beanInfo.getHeaders();
        int headerSize = CollectionUtil.isNotEmpty(headers) ? headers.size() : 0;
        boolean hasTitle = CollectionUtil.isNotEmpty(beanInfo.getTitles());
        Object[] beans = beanInfo.getBeans();
        List<String> datas = CollectionUtil.createArrayList(headerSize + ((hasTitle) ? 1 : 0) + beans.length);

        if (headerSize > 0) {
            for (int index = 0, size = headers.size(); index < size; index++) {
                datas.add(CsvUtil.toCsvString(headers.getKey(index), headers.get(index)));
            }
        }
        if (hasTitle) {
            datas.add(CsvUtil.toCsvString(beanInfo.getTitles()));
        }
        if (ObjectUtil.isEmpty(beans)) {
            return datas;
        }
        Field[] fields = getField(beanInfo);

        for (Object bean : beans) {
            datas.add(toCsvLine(bean, fields));
        }

        return datas;
    }

    private Field[] getField(final FileBeanInfo beanInfo) {
        // FIXME
        final Object object = beanInfo.getBeans()[0];

        return fieldCache.get(object.getClass().getName(), new Callable<Field[]>() {
            @Override
            public Field[] call() throws Exception {
                List<Field> fields = CollectionUtil.createArrayList(beanInfo.getFields().size());
                // FIXME
                for (String field : beanInfo.getFields()) {
                    fields.add(ReflectionUtil.getField(object, field));
                }
                return fields.toArray(new Field[0]);
            }
        });
    }

    @Override
    public void importBeans(FileBeanInfo beanInfo, String filePath) throws IOException {
        List<String> datas = collectData(beanInfo);
        if (CollectionUtil.isEmpty(datas)) {
            return;
        }

        WriterUtil.writeLinesAndClose(filePath, datas, GBK);

    }

    private String toCsvLine(Object bean, Field[] fields) {
        List<String> strings = CollectionUtil.createArrayList();

        for (Field field : fields) {
            Object value = ReflectionUtil.readField(field, bean);
            if (value == null) {
                // FIXME
                strings.add(null);
                continue;
            }

            String result = CONVERTER.fromConvert(value);
            strings.add((result != null) ? result : String.valueOf(value));
        }

        return CsvUtil.toCsvString(strings);
    }

    @Override
    public ByteArray toByteArray(FileBeanInfo beanInfo) throws IOException {
        List<String> data = collectData(beanInfo);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        WriterUtil.writeLinesAndClose(data, out, GBK);

        return out.toByteArray();
    }

    @Override
    public ByteArray toByteArray(List<String> contents) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        WriterUtil.writeLinesAndClose(contents, out, GBK);

        return out.toByteArray();
    }

}
