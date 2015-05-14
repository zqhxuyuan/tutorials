/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午7:07:59
 */
public class CsvUtilTest {
    @Test
    public void testToCsv() {
        assertEquals("a", CsvUtil.toCsvString("a"));
        assertEquals("a,b", CsvUtil.toCsvString("a", "b"));
        assertEquals("a,b,", CsvUtil.toCsvString("a", "b", ""));
        assertEquals("a,\" b \"", CsvUtil.toCsvString("a", " b "));
        assertEquals("a,b,\"jo,e\"", CsvUtil.toCsvString("a", "b", "jo,e"));
        assertEquals("a,b,\"\"\"some\"\"r\"", CsvUtil.toCsvString("a", "b", "\"some\"r"));
        assertEquals("1997,Ford,E350,\"Super, luxurious truck\"",
                CsvUtil.toCsvString("1997", "Ford", "E350", "Super, luxurious truck"));
        assertEquals("1997,Ford,E350,\"Super \"\"luxurious\"\" truck\"",
                CsvUtil.toCsvString("1997", "Ford", "E350", "Super \"luxurious\" truck"));
        assertEquals("1,,2", CsvUtil.toCsvString(Integer.valueOf(1), null, Integer.valueOf(2)));
        // FIXME
        assertEquals("\"a\nb\"", CsvUtil.toCsvString("a\nb"));
    }

    @Test
    public void testFromCsv() {
        assertStringArray(CsvUtil.toStringArray("a"), "a");
        assertStringArray(CsvUtil.toStringArray("a,b"), "a", "b");
        assertStringArray(CsvUtil.toStringArray("a, b "), "a", " b ");
        assertStringArray(CsvUtil.toStringArray("a,\" b \""), "a", " b ");
        assertStringArray(CsvUtil.toStringArray("a,b,"), "a", "b", "");
        assertStringArray(CsvUtil.toStringArray("a,b,\"jo,e\""), "a", "b", "jo,e");
        assertStringArray(CsvUtil.toStringArray("a,b,\"\"\"some\"\"r\""), "a", "b", "\"some\"r");
        assertStringArray(CsvUtil.toStringArray("1997,Ford,E350,\"Super, luxurious truck\""), "1997", "Ford", "E350",
                "Super, luxurious truck");
        assertStringArray(CsvUtil.toStringArray("1997,Ford,E350,\"Super \"\"luxurious\"\" truck\""), "1997", "Ford",
                "E350", "Super \"luxurious\" truck");
        assertStringArray(CsvUtil.toStringArray("\"a\nb\""), "a\nb");
        assertStringArray(CsvUtil.toStringArray("a,,b"), "a", "", "b");
    }

    void assertStringArray(String[] result, String...expected) {
        assertEquals(expected.length, result.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }
    }

}
