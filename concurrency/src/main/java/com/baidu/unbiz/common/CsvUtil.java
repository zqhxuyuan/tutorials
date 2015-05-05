/**
 * 
 */
package com.baidu.unbiz.common;

import java.util.List;

/**
 * <code>CSV</code>格式工具类 See: http://en.wikipedia.org/wiki/Comma-separated_values
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 上午4:01:52
 */
public abstract class CsvUtil {

    protected static final char FIELD_SEPARATOR = ',';
    protected static final char FIELD_QUOTE = '"';
    protected static final String DOUBLE_QUOTE = "\"\"";
    protected static final String SPECIAL_CHARS = "\r\n";

    /** 转换成<code>CSV</code>字面格式 */
    public static String toCsvString(Object...elements) {
        if (ArrayUtil.isEmpty(elements)) {
            return null;
        }
        StringBuilder line = new StringBuilder();
        for (int i = 0, last = elements.length - 1; i <= last; i++) {
            if (elements[i] == null && i != last) {
                line.append(FIELD_SEPARATOR);
                continue;
            }
            // FIXME
            if (elements[i] == null) {
                continue;
            }
            String field = elements[i].toString();

            // check for special cases
            int ndx = field.indexOf(FIELD_SEPARATOR);
            ndx = (ndx == -1) ? field.indexOf(FIELD_QUOTE) : ndx;

            if (ndx == -1 && (field.startsWith(StringPool.Symbol.SPACE) || field.endsWith(StringPool.Symbol.SPACE))) {
                ndx = 1;
            }
            ndx = (ndx == -1) ? StringUtil.indexOfChars(field, SPECIAL_CHARS) : ndx;

            // add field
            if (ndx != -1) {
                line.append(FIELD_QUOTE);
            }
            line.append(StringUtil.replace(field, StringPool.Symbol.DOUBLE_QUOTE, DOUBLE_QUOTE));
            if (ndx != -1) {
                line.append(FIELD_QUOTE);
            }

            // last
            if (i != last) {
                line.append(FIELD_SEPARATOR);
            }
        }
        return line.toString();
    }

    /** 转换成<code>CSV</code>字面格式 */
    public static <T> String toCsvString(List<T> elements) {
        if (CollectionUtil.isEmpty(elements)) {
            return null;
        }

        return toCsvString(elements.toArray(new Object[0]));
    }

    /**
     * 将<code>CSV</code>行转换成字符串数组。
     */
    public static String[] toStringArray(String line) {
        List<String> row = CollectionUtil.createArrayList();

        boolean inQuotedField = false;
        int fieldStart = 0;

        final int len = line.length();
        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);
            if (c == FIELD_SEPARATOR) {
                if (!inQuotedField) { // ignore we are quoting
                    addField(row, line, fieldStart, i, inQuotedField);
                    fieldStart = i + 1;
                }
                continue;
            }
            if (c == FIELD_QUOTE) {
                if (inQuotedField) {
                    // we are already quoting - peek to see if this is the end
                    // of the field
                    if (i + 1 == len || line.charAt(i + 1) == FIELD_SEPARATOR) {
                        addField(row, line, fieldStart, i, inQuotedField);
                        fieldStart = i + 2;
                        i++; // and skip the comma
                        inQuotedField = false;
                    }
                } else if (fieldStart == i) {
                    inQuotedField = true; // this is a beginning of a quote
                    fieldStart++; // move field start
                }
            }
        }
        // add last field - but only if string was not empty
        if (len > 0 && fieldStart <= len) {
            addField(row, line, fieldStart, len, inQuotedField);
        }
        return row.toArray(new String[row.size()]);
    }

    private static void addField(List<String> row, String line, int startIndex, int endIndex, boolean inQuoted) {
        String field = line.substring(startIndex, endIndex);
        if (inQuoted) {
            field = StringUtil.replace(field, DOUBLE_QUOTE, "\"");
        }

        row.add(field);
    }

}
