/**
 * 
 */
package com.baidu.unbiz.common.cache;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.StringUtil;
import com.baidu.unbiz.common.logger.LoggerFactory;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 上午11:43:12
 */
public class ConcurrentCacheTest {

    private ConcurrentCache<String, Object> cache;

    @Before
    public void setUp() throws Exception {
        cache = new ConcurrentCache<String, Object>();
    }

    @After
    public void tearDown() throws Exception {
        cache = null;
    }

    @Test
    public void testString() {
        final String key = "baidu_beidou";

        Object result = cache.get(key, new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return StringUtil.toCamelCase(key);
            }
        });

        assertEquals("baiduBeidou", result);
    }

    @Test
    public void testObject() {
        final String key = "object";

        Object result1 = cache.get(key, new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return new Object();
            }
        });

        Object result2 = cache.get(key, new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return new Object();
            }
        });

        assertEquals(result1, result2);
    }

    @Test
    public void testLogger() {
        Object result = cache.get("logger", new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return LoggerFactory.getLogger(ConcurrentCacheTest.class);
            }
        });

        assertEquals(LoggerFactory.getLogger(ConcurrentCacheTest.class), result);
        assertTrue(LoggerFactory.getLogger(ConcurrentCacheTest.class) == result);
    }

}
