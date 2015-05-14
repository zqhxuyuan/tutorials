/**
 * 
 */
package com.baidu.unbiz.common.i18n;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

import org.junit.Test;

import com.baidu.unbiz.common.i18n.LocaleInfo;
import com.baidu.unbiz.common.i18n.LocaleUtil;
import com.baidu.unbiz.common.test.TestUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午8:33:32
 */
public class LocaleInfoTest {
    private LocaleInfo localeInfo;

    @Test
    public void createSystemLocaleInfo() {
        localeInfo = new LocaleInfo();

        assertNotNull(localeInfo.getLocale());
        assertNotNull(localeInfo.getCharset());
    }

    @Test
    public void create_noLocale_noCharset() {
        try {
            LocaleUtil.setDefault(Locale.CHINA, "GB18030");

            localeInfo = new LocaleInfo(null);

            assertEquals(Locale.CHINA, localeInfo.getLocale());
            assertEquals("GB18030", localeInfo.getCharset().name());
        } finally {
            LocaleUtil.resetDefault();
        }
    }

    @Test
    public void create_withLocale_noCharset() {
        try {
            LocaleUtil.setDefault(Locale.CHINA, "GB18030");

            localeInfo = new LocaleInfo(Locale.US);

            assertEquals(Locale.US, localeInfo.getLocale());
            assertEquals("UTF-8", localeInfo.getCharset().name()); // 全能charset
        } finally {
            LocaleUtil.resetDefault();
        }
    }

    @Test
    public void create_withLocale_withCharset() {
        try {
            LocaleUtil.setDefault(Locale.CHINA, "GB18030");

            localeInfo = new LocaleInfo(Locale.US, "8859_1");

            assertEquals(Locale.US, localeInfo.getLocale());
            assertEquals("ISO-8859-1", localeInfo.getCharset().name());

            assertTrue(localeInfo.isCharsetSupported());
            assertSame(localeInfo, localeInfo.assertCharsetSupported());
        } finally {
            LocaleUtil.resetDefault();
        }
    }

    @Test
    public void create_withLocale_unknwonCharset() {
        try {
            LocaleUtil.setDefault(Locale.CHINA, "GB18030");

            localeInfo = new LocaleInfo(Locale.US, "unknown");

            assertEquals(Locale.US, localeInfo.getLocale());
            assertEquals("unknown", localeInfo.getCharset().name());
            assertFalse(localeInfo.isCharsetSupported());

            try {
                localeInfo.assertCharsetSupported();
                fail();
            } catch (UnsupportedCharsetException e) {
                assertEquals("unknown", e.getCharsetName());
            }
        } finally {
            LocaleUtil.resetDefault();
        }
    }

    @Test
    public void create_noFallback() {
        try {
            new LocaleInfo(null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("fallbackLocaleInfo"));
        }
    }

    @Test
    public void equalsHashCode() {
        LocaleInfo l1 = new LocaleInfo(Locale.CHINA, "GB18030");
        LocaleInfo l2 = new LocaleInfo(Locale.CHINA, "GB18030");
        LocaleInfo l3 = new LocaleInfo(Locale.US, "8859_1");

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());

        assertThat(l1, not(equalTo(l3)));
        assertThat(l1.hashCode(), not(equalTo(l3.hashCode())));

        assertTrue(l1.equals(l1));
        assertFalse(l1.equals(null));
        assertFalse(l1.equals("not a locale"));
    }

    @Test
    public void clone_() {
        localeInfo = new LocaleInfo(Locale.US, "8859_1");

        assertNotSame(localeInfo, localeInfo.clone());
        assertEquals(localeInfo, localeInfo.clone());
    }

    @Test
    public void toString_() {
        localeInfo = new LocaleInfo(Locale.US, "8859_1");
        assertEquals("en_US:ISO-8859-1", localeInfo.toString());
    }

    @Test
    public void parse() {
        try {
            localeInfo = LocaleInfo.parse(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("no locale name"));
        }

        try {
            localeInfo = LocaleInfo.parse("  ");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("no locale name"));
        }

        assertEquals("en_US:UTF-8", LocaleInfo.parse(" en_US ").toString());
        assertEquals("en_US:UTF-8", LocaleInfo.parse(" en_US : ").toString());
        assertEquals("en_US:ISO-8859-1", LocaleInfo.parse(" en_US : 8859_1").toString());
    }

    @Test
    public void parse_unknown() {
        assertEquals("en_US:unknown", LocaleInfo.parse(" en_US : unknown").toString());
        assertFalse(LocaleInfo.parse(" en_US : unknown").isCharsetSupported());

        try {
            LocaleInfo.parse(" en_US : unknown").assertCharsetSupported();
            fail();
        } catch (UnsupportedCharsetException e) {
            assertEquals("unknown", e.getCharsetName());
        }
    }

    @Test
    public void serialize() throws Exception {
        localeInfo = new LocaleInfo(Locale.US, "8859_1");

        // write
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(localeInfo);
        oos.close();

        // read
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        LocaleInfo copy = (LocaleInfo) ois.readObject();

        assertNotSame(localeInfo, copy);
        assertEquals(localeInfo, copy);
    }
}
