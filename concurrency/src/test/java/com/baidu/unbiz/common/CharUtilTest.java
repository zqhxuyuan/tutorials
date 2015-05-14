/**
 * 
 */
package com.baidu.unbiz.common;

import org.junit.Test;

import com.baidu.unbiz.common.logger.CachedLogger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午2:24:26
 */
public class CharUtilTest extends CachedLogger {

    @Test
    public void toAscii() {
        assertEquals(0x3F, CharUtil.toAscii('你'));
        assertEquals(0x3F, CharUtil.toAscii('我'));

        assertEquals(48, CharUtil.toAscii('0'));

        assertEquals(9, CharUtil.toAscii('9') - CharUtil.toAscii('0'));
    }

    @Test
    public void isWhitespace() {
        assertTrue(CharUtil.isWhitespace(' '));
        assertTrue(CharUtil.isWhitespace('\t'));
        assertTrue(CharUtil.isWhitespace('\r'));
        assertTrue(CharUtil.isWhitespace('\n'));

        assertFalse(CharUtil.isWhitespace('a'));
        assertFalse(CharUtil.isWhitespace('0'));
        assertFalse(CharUtil.isWhitespace('我'));
    }

}
