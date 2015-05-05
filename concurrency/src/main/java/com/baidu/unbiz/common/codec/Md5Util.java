/**
 * 
 */
package com.baidu.unbiz.common.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.baidu.unbiz.common.ExceptionUtil;
import com.baidu.unbiz.common.logger.Logger;
import com.baidu.unbiz.common.logger.LoggerFactory;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月14日 上午8:11:18
 */
public abstract class Md5Util {

    private static final Logger logger = LoggerFactory.getLogger(Md5Util.class);

    private static final MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO 处理异常（不应该发生）
            logger.error("MD5 digest failed", e);
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    public static String bufferToHex(String value) {
        if (value == null) {
            return null;
        }

        return bufferToHex(md5.digest(value.getBytes()));
    }

    public static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    public static String bufferToHex(byte bytes[], int m, int n) {
        StringBuilder builder = new StringBuilder(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], builder);
        }
        return builder.toString();
    }

    protected static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    private static void appendHexPair(byte bt, StringBuilder builder) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        builder.append(c0);
        builder.append(c1);
    }

}
