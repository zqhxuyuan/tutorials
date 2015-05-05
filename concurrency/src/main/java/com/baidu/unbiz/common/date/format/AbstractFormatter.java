package com.baidu.unbiz.common.date.format;

import com.baidu.unbiz.common.date.DateTimeStamp;
import com.baidu.unbiz.common.date.DatetimeObject;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:18:26
 */
public abstract class AbstractFormatter implements Formatter {

    protected char[][] patterns;

    protected char escapeChar = '\'';

    protected void preparePatterns(String[] spat) {
        patterns = new char[spat.length][];
        for (int i = 0; i < spat.length; i++) {
            patterns[i] = spat[i].toCharArray();
        }
    }

    protected int findPattern(char[] format, int i) {
        int frmtc_len = format.length;
        boolean match;
        int n, lastn = -1;
        int maxLen = 0;
        for (n = 0; n < patterns.length; n++) {
            char[] curr = patterns[n]; // current pattern from the pattern list
            if (i > frmtc_len - curr.length) {
                continue;
            }
            match = true;
            int delta = 0;
            while (delta < curr.length) { // match given pattern
                if (curr[delta] != format[i + delta]) {
                    match = false; // no match, go to next
                    break;
                }
                delta++;
            }
            if (match == true) { // match
                if (patterns[n].length > maxLen) { // find longest match
                    lastn = n;
                    maxLen = patterns[n].length;
                }
            }
        }
        return lastn;
    }

    protected abstract String convertPattern(int patternIndex, DatetimeObject jdt);

    public String convert(DatetimeObject jdt, String format) {
        char[] fmtc = format.toCharArray();
        int fmtc_len = fmtc.length;
        StringBuilder result = new StringBuilder(fmtc_len);

        int i = 0;
        while (i < fmtc_len) {
            if (fmtc[i] == escapeChar) { // quote founded
                int end = i + 1;
                while (end < fmtc_len) {
                    if (fmtc[end] == escapeChar) { // second quote founded
                        if (end + 1 < fmtc_len) {
                            end++;
                            if (fmtc[end] == escapeChar) { // skip double quotes
                                result.append(escapeChar); // and continue
                            } else {
                                break;
                            }
                        }
                    } else {
                        result.append(fmtc[end]);
                    }
                    end++;
                }
                i = end;
                continue; // end of quoted string, continue the main loop
            }

            int n = findPattern(fmtc, i);
            if (n != -1) { // pattern founded
                result.append(convertPattern(n, jdt));
                i += patterns[n].length;
            } else {
                result.append(fmtc[i]);
                i++;
            }
        }
        return result.toString();
    }

    protected abstract void parseValue(int patternIndex, String value, DateTimeStamp destination);

    public DateTimeStamp parse(String value, String format) {
        char[] sc = value.toCharArray();
        char[] fc = format.toCharArray();

        int i = 0, j = 0;
        int slen = value.length();
        int tlen = format.length();

        DateTimeStamp time = new DateTimeStamp();
        StringBuilder w = new StringBuilder();
        while (true) {
            int n = findPattern(fc, i);
            if (n != -1) { // pattern founded
                i += patterns[n].length;
                w.setLength(0);
                char next = 0xFFFF;
                if (i < tlen) {
                    next = fc[i]; // next = delimiter
                }
                while ((j < slen) && (sc[j] != next)) {
                    char scj = sc[j];
                    if ((scj != ' ') && (scj != '\t')) { // ignore surrounding
                                                         // whitespaces
                        w.append(sc[j]);
                    }
                    j++;
                }
                parseValue(n, w.toString(), time);
            } else {
                if (fc[i] == sc[j]) {
                    j++;
                }
                i++;
            }
            if ((i == tlen) || (j == slen)) {
                break;
            }
        }
        return time;
    }

    protected String printTens(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be positive: " + value);
        }
        if (value < 10) {
            return '0' + Integer.toString(value);
        }
        if (value < 100) {
            return Integer.toString(value);
        }
        throw new IllegalArgumentException("Value too big: " + value);
    }

    protected String printHundreds(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be positive: " + value);
        }
        if (value < 10) {
            return "00" + Integer.toString(value);
        }
        if (value < 100) {
            return '0' + Integer.toString(value);
        }
        if (value < 1000) {
            return Integer.toString(value);
        }
        throw new IllegalArgumentException("Value too big: " + value);
    }

    protected String printThousands(int value) {
        char[] result = new char[4];
        int count = 0;

        if (value < 0) {
            result[count++] = '-';
            value = -value;
        }

        String str = Integer.toString(value);

        if (value < 10) {
            result[count++] = '0';
            result[count++] = '0';
            result[count++] = '0';
            result[count++] = str.charAt(0);
        } else if (value < 100) {
            result[count++] = '0';
            result[count++] = '0';
            result[count++] = str.charAt(0);
            result[count++] = str.charAt(1);
        } else if (value < 1000) {
            result[count++] = '0';
            result[count++] = str.charAt(0);
            result[count++] = str.charAt(1);
            result[count++] = str.charAt(2);
        } else {
            if (count > 0) {
                return '-' + str;
            }
            return str;
        }
        return new String(result, 0, count);
    }
}
