/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.ObjectUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午3:29:10
 */
public class ObjectUtilTest {

    @Test
    public void defaultIfNull() {
        int[] ia = new int[0];
        Class<?>[] ic = new Class[0];
        String[] is = new String[0];
        String s = "";

        assertSame(ia, ObjectUtil.defaultIfNull(null, ia));
        assertSame(ic, ObjectUtil.defaultIfNull(null, ic));
        assertSame(is, ObjectUtil.defaultIfNull(null, is));
        assertSame(s, ObjectUtil.defaultIfNull(null, s));
        assertSame(s, ObjectUtil.defaultIfNull((Object) null, s));

        assertEquals("123", ObjectUtil.defaultIfNull("123", s));
    }

    @Test
    public void isSameType() {
        assertTrue(ObjectUtil.isSameType(null, null));
        assertTrue(ObjectUtil.isSameType(null, Boolean.TRUE));
        assertTrue(ObjectUtil.isSameType(Boolean.TRUE, null));
        assertTrue(ObjectUtil.isSameType(Boolean.TRUE, Boolean.FALSE));
        assertFalse(ObjectUtil.isSameType(Boolean.TRUE, new Integer(0)));
    }

    @Test
    public void isEmpty() {
        // object or null
        assertTrue(ObjectUtil.isEmpty(null));
        assertFalse(ObjectUtil.isEmpty(0));

        // String
        assertTrue(ObjectUtil.isEmpty(""));
        assertFalse(ObjectUtil.isEmpty(" "));
        assertFalse(ObjectUtil.isEmpty("bob"));
        assertFalse(ObjectUtil.isEmpty("  bob  "));
        assertFalse(ObjectUtil.isEmpty("\u3000")); // unicode blank
        assertFalse(ObjectUtil.isEmpty("\r\n")); // blank

        // array
        assertTrue(ObjectUtil.isEmpty(new Object[0]));
        assertTrue(ObjectUtil.isEmpty(new int[0]));
        assertFalse(ObjectUtil.isEmpty(new int[] { 1, 2 }));
    }

    @Test
    public void testNull() {
        // all null
        Object[] NULL = null;
        assertTrue(ObjectUtil.isAllNull(NULL));
        assertTrue(ObjectUtil.isAllNull(new Object[] { null, null, null, null, null }));
        assertFalse(ObjectUtil.isEmpty(new Object[] { null, null, 0, null, null }));

        // any null
        assertTrue(ObjectUtil.isAnyNull(NULL));
        assertTrue(ObjectUtil.isAnyNull(new Object[] { null, null, null, null, null }));
        assertTrue(ObjectUtil.isAnyNull(new Object[] { null, null, 0, null, null }));
        assertTrue(ObjectUtil.isAnyNull(new Object[] { null, "null", 0, null, null }));
        assertFalse(ObjectUtil
                .isAnyNull(new Object[] { "", "null", 0, new int[] {}, CollectionUtil.createArrayList() }));
    }

}
