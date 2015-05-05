/**
 * 
 */
package com.baidu.unbiz.common.date.format;

import com.baidu.unbiz.common.date.DateTimeStamp;
import com.baidu.unbiz.common.date.DatetimeObject;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:23:41
 */
public class Format {

    protected final String format;
    protected final Formatter formatter;

    public Format(Formatter formatter, String format) {
        this.format = format;
        this.formatter = formatter;
    }

    public String getFormat() {
        return format;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public String convert(DatetimeObject jdt) {
        return formatter.convert(jdt, format);
    }

    public DateTimeStamp parse(String value) {
        return formatter.parse(value, format);
    }
}
