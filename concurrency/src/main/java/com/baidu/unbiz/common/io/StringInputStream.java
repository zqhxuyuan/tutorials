/**
 * 
 */
package com.baidu.unbiz.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import com.baidu.unbiz.common.CharUtil;

/**
 * 定义了三种字符处理方式
 * <p>
 * <code>ALL</code>低高位字节均会处理
 * <p>
 * <code>STRIP</code>只处理低位
 * <p>
 * <code>ASCII</code>只处理低位，且上限为<code>0x3F</code>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月22日 上午2:31:23
 */
public class StringInputStream extends InputStream implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7989260165741751591L;
    protected final String string;
    protected final Mode mode;
    protected int index;
    protected int charOffset;
    protected int available;

    public enum Mode {
        /**
         * Both lower and higher byte of string characters are processed.
         */
        ALL,
        /**
         * High bytes are simply cut off.
         */
        STRIP,
        /**
         * Returns only low bytes, marking overloaded chars with 0x3F.
         */
        ASCII
    }

    public StringInputStream(String string, Mode mode) {
        this.string = string;
        this.mode = mode;
        available = string.length();

        if (mode == Mode.ALL) {
            available <<= 1;
        }
    }

    @Override
    public int read() throws IOException {
        if (available == 0) {
            return -1;
        }
        available--;
        char c = string.charAt(index);

        switch (mode) {
            case ALL:
                if (charOffset == 0) {
                    charOffset = 1;
                    return (c & 0x0000ff00) >> 8;
                }
                charOffset = 0;
                index++;
                return c & 0x000000ff;
            case STRIP:
                index++;
                return c & 0x000000ff;
            case ASCII:
                index++;
                return CharUtil.toAscii(c);
        }
        return -1;
    }

    @Override
    public int available() throws IOException {
        return available;
    }

}
