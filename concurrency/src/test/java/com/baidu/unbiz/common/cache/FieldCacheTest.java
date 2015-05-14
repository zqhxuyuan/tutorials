/**
 * 
 */
package com.baidu.unbiz.common.cache;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.ReflectionUtil;
import com.baidu.unbiz.common.sample.AnnotationClass;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午12:32:05
 */
public class FieldCacheTest {

    private FieldCache cache;

    private int fieldSize;

    private int instanceFieldSize;

    @Before
    public void setUp() throws Exception {
        cache = FieldCache.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        cache = null;
    }

    @Test
    public void getFields() {
        Field[] result = cache.getFields(String.class);
        Field[] fields = ReflectionUtil.getAllFieldsOfClass(String.class);

        assertArrayEquals(result, fields);
        fieldSize = result.length;

        result = cache.getFields(AnnotationClass.class, AnnotationClass.Test.class);
        fields = ReflectionUtil.getAnnotationFields(AnnotationClass.class, AnnotationClass.Test.class);

        assertArrayEquals(result, fields);
    }

    @Test
    public void getInstanceFields() {
        Field[] result = cache.getInstanceFields(String.class);
        Field[] fields = ReflectionUtil.getAllInstanceFields(String.class);

        assertArrayEquals(result, fields);
        instanceFieldSize = result.length;

        result = cache.getInstanceFields(AnnotationClass.class);
        fields = ReflectionUtil.getAllInstanceFields(AnnotationClass.class);
        assertArrayEquals(result, fields);
        getFields();
        assertTrue(fieldSize > 0);
        assertTrue(instanceFieldSize > 0);
        assertTrue(fieldSize > instanceFieldSize);
    }

}
