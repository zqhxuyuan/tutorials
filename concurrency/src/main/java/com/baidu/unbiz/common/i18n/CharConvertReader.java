/**
 * 
 */
package com.baidu.unbiz.common.i18n;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * 边读数据边转换的Reader。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月24日 上午3:43:18
 */
public class CharConvertReader extends FilterReader {
    private CharConverter converter;

    public CharConvertReader(Reader in, String converterName) {
        this(in, CharConverter.getInstance(converterName));
    }

    public CharConvertReader(Reader in, CharConverter converter) {
        super(in);
        this.converter = converter;

        if (converter == null) {
            throw new NullPointerException("converter is null");
        }
    }

    @Override
    public int read() throws IOException {
        int ch = super.read();

        if (ch < 0) {
            return ch;
        }

        return converter.convert((char) ch);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int count = super.read(cbuf, off, len);

        if (count > 0) {
            converter.convert(cbuf, off, count);
        }

        return count;
    }
}
