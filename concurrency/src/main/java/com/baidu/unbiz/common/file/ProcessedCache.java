/**
 * 
 */
package com.baidu.unbiz.common.file;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.baidu.unbiz.common.ArrayUtil;
import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.ReflectionUtil;
import com.baidu.unbiz.common.StringUtil;
import com.baidu.unbiz.common.able.Computable;
import com.baidu.unbiz.common.cache.ConcurrentCache;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月27日 上午7:56:13
 */
public class ProcessedCache {

    private Computable<Class<?>, List<FieldDesc>> fieldCache = new ConcurrentCache<Class<?>, List<FieldDesc>>();

    private Map<Class<?>, List<String>> titleCache = CollectionUtil.createHashMap();

    private ProcessedCache() {

    }

    public static final ProcessedCache getInstance() {
        return Singleton.instance;
    }

    /** 纯粹找个地方初始化 */
    interface Singleton {
        ProcessedCache instance = new ProcessedCache();
    }

    public List<FieldDesc> getFieldDescs(final Class<?> clazz) {
        return fieldCache.get(clazz, new Callable<List<FieldDesc>>() {

            @Override
            public List<FieldDesc> call() throws Exception {
                Field[] fields = ReflectionUtil.getAnnotationFields(clazz, ProcessedField.class);
                if (ArrayUtil.isEmpty(fields)) {
                    return null;
                }

                List<FieldDesc> result = CollectionUtil.createArrayList(fields.length);
                for (Field field : fields) {
                    FieldDesc object = toFieldObject(field);

                    result.add(object);
                }

                Collections.sort(result, FieldDesc.comparator);

                ProcessedType type = clazz.getAnnotation(ProcessedType.class);
                if (type != null && type.title()) {
                    List<String> titles = getTitles(result);
                    titleCache.put(clazz, titles);
                }

                return result;
            }
        });

    }

    public List<String> getTitles(final Class<?> clazz) {
        List<String> result = titleCache.get(clazz);
        if (result != null) {
            return result;
        }
        // 唤起阻塞
        getFieldDescs(clazz);
        return titleCache.get(clazz);
    }

    private static FieldDesc toFieldObject(Field field) {
        ProcessedField convert = field.getAnnotation(ProcessedField.class);

        FieldDesc object = new FieldDesc();

        String title = convert.title();
        object.order(convert.index()).title(title.equals("") ? StringUtil.toLowerCaseWithUnderscores(title) : title);
        object.type(field.getType()).field(field);

        return object;
    }

    private List<String> getTitles(final List<FieldDesc> list) {
        List<String> result = CollectionUtil.createArrayList();

        for (FieldDesc object : list) {
            result.add(object.title());
        }

        return result;
    }

}
