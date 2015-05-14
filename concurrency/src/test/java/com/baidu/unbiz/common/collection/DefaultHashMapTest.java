/**
 * 
 */
package com.baidu.unbiz.common.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.collection.DefaultHashMapTest.Constructors;
import com.baidu.unbiz.common.collection.DefaultHashMapTest.MapBasic;
import com.baidu.unbiz.common.collection.DefaultHashMapTest.MapEntrySet;
import com.baidu.unbiz.common.collection.DefaultHashMapTest.MapKeySet;
import com.baidu.unbiz.common.collection.DefaultHashMapTest.MapValues;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午1:26:14
 */
@RunWith(Suite.class)
@SuiteClasses({ Constructors.class, MapBasic.class, MapEntrySet.class, MapKeySet.class, MapValues.class })
public class DefaultHashMapTest {
    private static int getThreshold(DefaultHashMap<Object, Object> map) {
        return map.getThreshold();
    }

    private static int getCapacity(DefaultHashMap<Object, Object> map) {
        return map.getCapacity();
    }

    public static class MapBasic extends AbstractMapTest {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Map<Object, Object> cloneMap(Map<Object, Object> map) {
            return (Map<Object, Object>) ((DefaultHashMap<Object, Object>) map).clone();
        }

        @Override
        protected int getThreshold(Map<Object, Object> map) {
            return DefaultHashMapTest.getThreshold((DefaultHashMap<Object, Object>) map);
        }

        @Override
        protected int getCapacity(Map<Object, Object> map) {
            return DefaultHashMapTest.getCapacity((DefaultHashMap<Object, Object>) map);
        }
    }

    public static class MapEntrySet extends AbstractMapViewTest {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        protected Collection<?> getView(Map<Object, Object> map) {
            return map.entrySet();
        }

        @Override
        protected Collection<?> createCollectionToCompareWith() {
            return CollectionUtil.createHashSet();
        }

        @Override
        protected Object createItem(Object key, Object value) {
            return new DefaultMapEntry<Object, Object>(key, value);
        }
    }

    public static class MapKeySet extends AbstractMapViewTest {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        protected Collection<?> getView(Map<Object, Object> map) {
            return map.keySet();
        }

        @Override
        protected Collection<?> createCollectionToCompareWith() {
            return CollectionUtil.createHashSet();
        }

        @Override
        protected Object createItem(Object key, Object value) {
            return key;
        }
    }

    public static class MapValues extends AbstractMapViewTest {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        protected Collection<?> getView(Map<Object, Object> map) {
            return map.values();
        }

        @Override
        protected Collection<?> createCollectionToCompareWith() {
            return null;
        }

        @Override
        protected Object createItem(Object key, Object value) {
            return value;
        }
    }

    public static class Constructors {
        private DefaultHashMap<Object, Object> map;

        /** 测试构造函数(initialCapacity, loadFactor). */
        @Test
        public void constructor1() {
            assertConstructorException(-1, .75f);
            assertConstructorException(16, 0);
            assertConstructorException(16, -1);

            map = new DefaultHashMap<Object, Object>(0, .75f);
            assertParameters(0, 1);

            map = new DefaultHashMap<Object, Object>(16, 1);
            assertParameters(16, 16);

            map = new DefaultHashMap<Object, Object>(16, 1.000001f);
            assertParameters(16, 16);

            map = new DefaultHashMap<Object, Object>(16, 2);
            assertParameters(32, 16);
        }

        /** 测试构造函数(initialCapacity). */
        @Test
        public void constructor2() {
            assertConstructorException(-1);

            map = new DefaultHashMap<Object, Object>(0);
            assertParameters(0, 1);
        }

        /** 测试构造函数(无参数). */
        @Test
        public void constructor3() {
            map = new DefaultHashMap<Object, Object>();
            assertParameters(12, 16);
        }

        /** 测试构造函数(Map). */
        @Test
        public void constructor4() {
            Map<Object, Object> anotherMap = new java.util.HashMap<Object, Object>();

            anotherMap.put("aaa", "111");
            anotherMap.put("bbb", "222");
            anotherMap.put("ccc", "333");

            map = new DefaultHashMap<Object, Object>(anotherMap);
            assertParameters(12, 16);
            assertEquals(3, map.size());
            assertEquals("111", map.get("aaa"));
            assertEquals("222", map.get("bbb"));
            assertEquals("333", map.get("ccc"));
        }

        /**
         * 测试内部参数: threshold和capacity.
         * 
         * @param expectedThreshold 期望的阈值
         * @param expectedCapacity 期望的容量
         */
        private void assertParameters(int expectedThreshold, int expectedCapacity) {
            assertEquals(expectedThreshold, getThreshold(map));
            assertEquals(expectedCapacity, getCapacity(map));
        }

        /**
         * 测试并试图得到Exception.
         * 
         * @param initialCapacity 初始容量
         * @param loadFactor 负载系数
         */
        private void assertConstructorException(int initialCapacity, float loadFactor) {
            try {
                new DefaultHashMap<Object, Object>(initialCapacity, loadFactor);
                fail("should throw an IllegalArgumentException");
            } catch (IllegalArgumentException e) {
            }
        }

        /**
         * 测试并试图得到Exception.
         * 
         * @param initialCapacity 初始容量
         */
        private void assertConstructorException(int initialCapacity) {
            try {
                new DefaultHashMap<Object, Object>(initialCapacity);
                fail("should throw an IllegalArgumentException");
            } catch (IllegalArgumentException e) {
            }
        }
    }
}
