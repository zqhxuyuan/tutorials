/**
 * 
 */
package com.baidu.unbiz.common;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 和<code>ResourceBundle</code>及消息字符串有关的工具类。
 * 
 * <p>
 * 这个类中的每个方法都可以“安全”地处理<code>null</code>，而不会抛出<code>NullPointerException</code>。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月20日 上午6:00:56
 */
public class MessageUtil {
    /**
     * 从<code>ResourceBundle</code>中取得字符串，并使用<code>MessageFormat</code>格式化字符串.
     * 
     * @param bundle resource bundle
     * @param key 要查找的键
     * @param params 参数表
     * @return key对应的字符串，如果key为<code>null</code>或resource bundle为 <code>null</code>，或resource key未找到，则返回<code>key</code>
     */
    public static String getMessage(ResourceBundle bundle, String key, Object...params) {
        if (bundle == null || key == null) {
            return key;
        }

        try {
            return formatMessage(bundle.getString(key), params);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * 使用<code>MessageFormat</code>格式化字符串.
     * 
     * @param message 要格式化的字符串
     * @param params 参数表
     * @return 格式化的字符串，如果message为<code>null</code>，则返回<code>null</code>
     */
    public static String formatMessage(String message, Object...params) {
        if (message == null || params == null || params.length == 0) {
            return message;
        }

        return MessageFormat.format(message, params);
    }
}
