/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ResourceBundle;

import org.junit.Test;

import com.baidu.unbiz.common.MessageUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午5:08:36
 */
public class MessageUtilTest {
    @Test
    public void formatMessage() {
        assertNull(MessageUtil.formatMessage(null, (Object[]) null));

        String message = "message {0}, {1}, {2}, {3}, {4}";

        assertSame(message, MessageUtil.formatMessage(message, (Object[]) null));
        assertSame(message, MessageUtil.formatMessage(message, new Object[0]));

        assertEquals("message aa, {1}, {2}, {3}, {4}", MessageUtil.formatMessage(message, "aa"));
        assertEquals("message aa, bb, {2}, {3}, {4}", MessageUtil.formatMessage(message, "aa", "bb"));
        assertEquals("message aa, bb, cc, {3}, {4}", MessageUtil.formatMessage(message, "aa", "bb", "cc"));
        assertEquals("message aa, bb, cc, dd, {4}", MessageUtil.formatMessage(message, "aa", "bb", "cc", "dd"));
        assertEquals("message aa, bb, cc, dd, ee", MessageUtil.formatMessage(message, "aa", "bb", "cc", "dd", "ee"));
    }

    @Test
    public void getMessage() {
        ResourceBundle bundle = ResourceBundle.getBundle(MessageUtilTest.class.getName());
        String key = "key";
        String notFoundKey = "notFound";

        assertNull(MessageUtil.getMessage(null, null, (Object[]) null));
        assertNull(MessageUtil.getMessage(bundle, null, (Object[]) null));
        assertSame(key, MessageUtil.getMessage(null, key, (Object[]) null));
        assertSame(notFoundKey, MessageUtil.getMessage(bundle, notFoundKey, (Object[]) null));

        assertEquals("message aa, {1}, {2}, {3}, {4}", MessageUtil.getMessage(bundle, key, "aa"));
        assertEquals("message aa, bb, {2}, {3}, {4}", MessageUtil.getMessage(bundle, key, "aa", "bb"));
        assertEquals("message aa, bb, cc, {3}, {4}", MessageUtil.getMessage(bundle, key, "aa", "bb", "cc"));
        assertEquals("message aa, bb, cc, dd, {4}", MessageUtil.getMessage(bundle, key, "aa", "bb", "cc", "dd"));
        assertEquals("message aa, bb, cc, dd, ee", MessageUtil.getMessage(bundle, key, "aa", "bb", "cc", "dd", "ee"));
    }
}
