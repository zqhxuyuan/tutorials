/**
 * 
 */
package com.baidu.unbiz.common.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午1:26:38
 */
public class DefaultMapEntryTest {
    private Map.Entry<String, Object> entry1;
    private Map.Entry<String, Object> entry2;

    @Before
    public void init() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put(null, null);
        entry1 = map.entrySet().iterator().next();

        map.clear();
        map.put("hello", "baobao");
        entry2 = map.entrySet().iterator().next();
    }

    /** 测试equals方法. */
    @Test
    public void equals_() {
        DefaultMapEntry<String, Object> e1 = new DefaultMapEntry<String, Object>(null, null);
        DefaultMapEntry<String, Object> e2 = new DefaultMapEntry<String, Object>("hello", "baobao");

        assertTrue(e1.equals(entry1));
        assertTrue(e2.equals(entry2));

        assertTrue(e1.equals(e1));
        assertFalse(e1.equals(null));
        assertFalse(e1.equals(""));
    }

    /** 测试hashCode方法. */
    @Test
    public void hashCode_() {
        assertEquals(entry1.hashCode(), new DefaultMapEntry<String, Object>(null, null).hashCode());

        assertEquals(entry2.hashCode(), new DefaultMapEntry<String, Object>("hello", "baobao").hashCode());
    }

    /** 测试toString方法. */
    @Test
    public void toString_() {
        assertEquals(entry1.toString(), new DefaultMapEntry<String, Object>(null, null).toString());

        assertEquals(entry2.toString(), new DefaultMapEntry<String, Object>("hello", "baobao").toString());
    }

    /** 测试setValue方法. */
    @Test
    public void setValue() {
        DefaultMapEntry<String, Object> entry = new DefaultMapEntry<String, Object>(null, null);

        assertEquals(null, entry.setValue("hello"));
        assertEquals("hello", entry.getValue());
    }
}
