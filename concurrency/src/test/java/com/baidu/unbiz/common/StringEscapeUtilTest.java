/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.util.Formatter;
import java.util.Locale;

import org.junit.Test;

import com.baidu.unbiz.common.StringEscapeUtil;
import com.baidu.unbiz.common.i18n.LocaleUtil;
import com.baidu.unbiz.common.test.TestUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午8:29:11
 */
public class StringEscapeUtilTest {

    /* ==================================================================== */
    /* URL/URI encoding/decoding。 */
    /* 根据RFC2396：http://www.ietf.org/rfc/rfc2396.txt */
    /* ==================================================================== */
    @Test
    public void escapeURLStrict() throws Exception {
        String unreserved = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'()";

        assertSame(unreserved, StringEscapeUtil.escapeURL(unreserved));

        // 测试所有ISO-8859-1字符
        StringBuilder buffer = new StringBuilder(256);
        StringBuilder expectedBuffer = new StringBuilder(256 * 3);

        for (int i = 0; i < 256; i++) {
            buffer.append((char) i);

            if (i == ' ') {
                expectedBuffer.append('+');
            } else if (unreserved.indexOf(i) == -1) {
                new Formatter(expectedBuffer).format("%%%02X", i);
            } else {
                expectedBuffer.append((char) i);
            }
        }

        String str = buffer.toString();
        String expectedStr = expectedBuffer.toString();

        assertEquals(expectedStr, StringEscapeUtil.escapeURL(str, "8859_1"));

        // 测试writer
        StringWriter writer = new StringWriter();

        StringEscapeUtil.escapeURL(str, "8859_1", writer);
        assertEquals(expectedStr, writer.toString());

        // 测试中文
        assertEquals("%D6%D0%BB%AA%C8%CB%C3%F1%B9%B2%BA%CD%B9%FA", StringEscapeUtil.escapeURL("中华人民共和国", "GBK"));

        // 中文writer
        writer = new StringWriter();
        StringEscapeUtil.escapeURL("中华人民共和国", "GBK", writer);
        assertEquals("%D6%D0%BB%AA%C8%CB%C3%F1%B9%B2%BA%CD%B9%FA", writer.toString());
    }

    @Test
    public void escapeURLLoose() throws Exception {
        String reserved = ";/?:@&=+$,";

        assertEquals("%3B%2F%3F%3A%40%26%3D%2B%24%2C", StringEscapeUtil.escapeURL(reserved, "8859_1", false));

        // 测试所有ISO-8859-1字符
        StringBuilder buffer = new StringBuilder(256);
        StringBuilder expectedBuffer = new StringBuilder(256 * 3);

        for (int i = 0; i < 256; i++) {
            buffer.append((char) i);

            if (i == ' ') {
                expectedBuffer.append('+');
            } else if (reserved.indexOf(i) == -1 && i > 32) {
                expectedBuffer.append((char) i);
            } else {
                new Formatter(expectedBuffer).format("%%%02X", i);
            }
        }

        String str = buffer.toString();
        String expectedStr = expectedBuffer.toString();

        assertEquals(expectedStr, StringEscapeUtil.escapeURL(str, "8859_1", false));

        // 测试writer
        StringWriter writer = new StringWriter();

        StringEscapeUtil.escapeURL(str, "8859_1", writer, false);
        assertEquals(expectedStr, writer.toString());

        // 测试中文和全角空格
        str = "中华人民共和国";
        assertSame(str, StringEscapeUtil.escapeURL(str, "GBK", false));

        str = "中华人民共和国\u3000";
        assertEquals("中华人民共和国%A1%A1", StringEscapeUtil.escapeURL(str, "GBK", false));

        // 中文writer
        writer = new StringWriter();
        StringEscapeUtil.escapeURL("中华人民共和国", "GBK", writer, false);
        assertEquals("中华人民共和国", writer.toString());
    }

    @Test
    public void escapeURLEncoding() {
        LocaleUtil.setContext(Locale.CHINA, "GBK");
        assertEquals("%D6%D0%BB%AA%C8%CB%C3%F1%B9%B2%BA%CD%B9%FA", StringEscapeUtil.escapeURL("中华人民共和国"));

        LocaleUtil.setContext(Locale.US, "8859_1");
        assertEquals("%3F%3F%3F%3F%3F%3F%3F", StringEscapeUtil.escapeURL("中华人民共和国"));

        LocaleUtil.resetContext();
    }

    @Test
    public void unescapeURL() throws Exception {
        assertEquals(null, StringEscapeUtil.unescapeURL(null));

        try {
            StringEscapeUtil.unescapeURL("test", null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("The Appendable must not be null"));
        }
    }

    @Test
    public void unescapeURLStrict() throws Exception {
        String unreserved = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'()";
        String escaped = StringEscapeUtil.escapeURL(unreserved);

        assertSame(unreserved, StringEscapeUtil.unescapeURL(escaped));

        // 测试所有ISO-8859-1字符
        StringBuilder buffer = new StringBuilder(256);
        StringBuilder expectedBuffer = new StringBuilder(256 * 3);

        for (int i = 0; i < 256; i++) {
            buffer.append((char) i);

            if (i == ' ') {
                expectedBuffer.append('+');
            } else if (unreserved.indexOf(i) == -1) {
                new Formatter(expectedBuffer).format("%%%02X", i);
            } else {
                expectedBuffer.append((char) i);
            }
        }

        String str = buffer.toString();
        String expectedStr = expectedBuffer.toString();

        escaped = StringEscapeUtil.escapeURL(str, "8859_1");
        assertEquals(expectedStr, escaped);
        assertEquals(str, StringEscapeUtil.unescapeURL(escaped, "8859_1"));

        // 测试writer
        StringWriter writer = new StringWriter();

        StringEscapeUtil.unescapeURL(escaped, "8859_1", writer);
        assertEquals(str, writer.toString());

        // 测试中文
        escaped = StringEscapeUtil.escapeURL("中华人民共和国", "GBK");
        assertEquals("中华人民共和国", StringEscapeUtil.unescapeURL(escaped, "GBK"));

        // 中文writer
        writer = new StringWriter();
        StringEscapeUtil.unescapeURL(escaped, "GBK", writer);
        assertEquals("中华人民共和国", writer.toString());

        // 错误的编码
        str = "abc%xx%20%1";
        assertEquals("abc%xx %1", StringEscapeUtil.unescapeURL(str));

        str = "abc%xx%1%";
        assertSame(str, StringEscapeUtil.unescapeURL(str));
    }

    @Test
    public void unescapeURLLoose() throws Exception {
        String reserved = ";/?:@&=+$,";
        String escaped = StringEscapeUtil.escapeURL(reserved, "8859_1", false);

        assertEquals(reserved, StringEscapeUtil.unescapeURL(escaped, "8859_1"));

        // 测试所有ISO-8859-1字符
        StringBuilder buffer = new StringBuilder(256);
        StringBuilder expectedBuffer = new StringBuilder(256 * 3);

        for (int i = 0; i < 256; i++) {
            buffer.append((char) i);

            if (i == ' ') {
                expectedBuffer.append('+');
            } else if (reserved.indexOf(i) == -1 && i > 32) {
                expectedBuffer.append((char) i);
            } else {
                new Formatter(expectedBuffer).format("%%%02X", i);
            }
        }

        String str = buffer.toString();
        String expectedStr = expectedBuffer.toString();

        escaped = StringEscapeUtil.escapeURL(str, "8859_1", false);

        assertEquals(expectedStr, escaped);
        assertEquals(str, StringEscapeUtil.unescapeURL(escaped, "8859_1"));

        // 测试writer
        StringWriter writer = new StringWriter();

        escaped = StringEscapeUtil.escapeURL(str, "8859_1", false);
        StringEscapeUtil.unescapeURL(escaped, "8859_1", writer);

        assertEquals(str, writer.toString());

        // 测试中文和全角空格
        str = "中华人民共和国";
        assertSame(str, StringEscapeUtil.unescapeURL("中华人民共和国", "GBK"));

        str = "中华人民共和国\u3000";
        assertEquals(str, StringEscapeUtil.unescapeURL("中华人民共和国%A1%A1", "GBK"));

        // 中文writer
        writer = new StringWriter();
        StringEscapeUtil.unescapeURL("中华人民共和国", "GBK", writer);
        assertEquals("中华人民共和国", writer.toString());
    }

    @Test
    public void unescapeURLEncoding() {
        LocaleUtil.setContext(Locale.CHINA, "GBK");
        assertEquals("中华人民共和国", StringEscapeUtil.unescapeURL("%D6%D0%BB%AA%C8%CB%C3%F1%B9%B2%BA%CD%B9%FA"));

        LocaleUtil.setContext(Locale.US, "8859_1");
        assertEquals("\u00D6\u00D0\u00BB\u00AA\u00C8\u00CB\u00C3\u00F1\u00B9\u00B2\u00BA\u00CD\u00B9\u00FA",
                StringEscapeUtil.unescapeURL("%D6%D0%BB%AA%C8%CB%C3%F1%B9%B2%BA%CD%B9%FA"));

        LocaleUtil.resetContext();
    }

    @Test
    public void unescapeURL_rawBytes() throws Exception {
        LocaleUtil.setContext(Locale.CHINA, "GBK");
        String escaped = StringEscapeUtil.escapeURL("Justin\u00b7Bieber");

        assertEquals("Justin\u00b7Bieber", StringEscapeUtil.unescapeURL(escaped));
        assertEquals("中华民国100年", StringEscapeUtil.unescapeURL(reencode("中华民国100年", "GBK"), "GBK"));

        // 混合中/英文、编码/未编码
        assertEquals("中华abc 人民共和国abc 人", StringEscapeUtil.unescapeURL("中华abc+\310%CB民共和国abc+\310%CB", "GBK"));
        assertSame("abc中华", StringEscapeUtil.unescapeURL("abc中华", "GBK"));
        assertSame("ab中华c", StringEscapeUtil.unescapeURL("ab中华c", "GBK"));
        assertSame("中华abc", StringEscapeUtil.unescapeURL("中华abc", "GBK"));
        assertSame("中abc华", StringEscapeUtil.unescapeURL("中abc华", "GBK"));
        assertEquals("abc中华", StringEscapeUtil.unescapeURL(reencode("abc中", "GBK") + "华", "GBK"));
        assertEquals("中华abc", StringEscapeUtil.unescapeURL(reencode("中", "GBK") + "华abc", "GBK"));

        LocaleUtil.resetContext();
    }

    private String reencode(String str, String charset) throws Exception {
        return new String(str.getBytes(charset), "8859_1");
    }

}
