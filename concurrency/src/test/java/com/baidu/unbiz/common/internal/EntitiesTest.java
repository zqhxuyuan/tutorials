package com.baidu.unbiz.common.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年10月8日 上午11:11:49
 */
public class EntitiesTest {
    private Entities entities;

    @Before
    public void init() {
        entities = new Entities();
        entities.addEntity("foo", 161);
        entities.addEntity("bar", 162);
    }

    @Test
    public void addEntities() throws Exception {
        String[][] array = { { "foo", "100" }, { "bar", "101" } };

        Entities e = new Entities();

        e.addEntities(array);

        assertEquals("foo", e.getEntityName(100));
        assertEquals("bar", e.getEntityName(101));

        assertEquals(100, e.getEntityValue("foo"));
        assertEquals(101, e.getEntityValue("bar"));
    }

    @Test
    public void xmlEntities() throws Exception {
        assertEquals("gt", Entities.XML.getEntityName('>'));
        assertEquals('>', Entities.XML.getEntityValue("gt"));
        assertEquals(-1, Entities.XML.getEntityValue("xyzzy"));
    }

    @Test
    public void html40Entities() throws Exception {
        assertEquals("nbsp", Entities.HTML40.getEntityName('\u00A0'));
        assertEquals('\u00A0', Entities.HTML40.getEntityValue("nbsp"));
        assertEquals(-1, Entities.XML.getEntityValue("xyzzy"));
        assertEquals(null, Entities.XML.getEntityName(123));
    }

    @Test
    public void entityMap() {
        Entities map = new Entities();

        map.addEntity("foo", 1);
        assertEquals(1, map.getEntityValue("foo"));
        assertEquals("foo", map.getEntityName(1));

        // 查找表将被更新
        map.addEntity("bar", 2);
        map.addEntity("baz", 3);
        assertEquals(3, map.getEntityValue("baz"));
        assertEquals("baz", map.getEntityName(3));
    }
}
