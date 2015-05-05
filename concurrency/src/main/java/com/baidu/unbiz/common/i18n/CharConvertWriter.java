/**
 * 
 */
package com.baidu.unbiz.common.i18n;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 边写数据边转换的Writer。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月24日 上午3:43:36
 */
public class CharConvertWriter extends FilterWriter {
    private CharConverter converter;

    public CharConvertWriter(Writer out, String converterName) {
        this(out, CharConverter.getInstance(converterName));
    }

    public CharConvertWriter(Writer out, CharConverter converter) {
        super(out);
        this.converter = converter;

        if (converter == null) {
            throw new NullPointerException("converter is null");
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] newbuf = new char[len];

        System.arraycopy(cbuf, off, newbuf, 0, len);

        converter.convert(newbuf, 0, len);

        super.write(newbuf, 0, len);
    }

    @Override
    public void write(int c) throws IOException {
        super.write(converter.convert((char) c));
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        super.write(converter.convert(str, off, len), 0, len);
    }
}
