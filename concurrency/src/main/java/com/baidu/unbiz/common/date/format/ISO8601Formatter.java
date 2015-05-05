package com.baidu.unbiz.common.date.format;

import java.util.TimeZone;

import com.baidu.unbiz.common.date.DateTimeStamp;
import com.baidu.unbiz.common.date.DatetimeObject;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:19:43
 */
public class ISO8601Formatter extends AbstractFormatter {

    public ISO8601Formatter() {
        preparePatterns(new String[] { "YYYY", // 0 + year
                "MM", // 1 + month
                "DD", // 2 + day of month
                "D", // 3 - day of week
                "MML", // 4 - month long name
                "MMS", // 5 - month short name
                "DL", // 6 - day of week long name
                "DS", // 7 - day of week short name
                "hh", // 8 + hour
                "mm", // 9 + minute
                "ss", // 10 + seconds
                "mss", // 11 + milliseconds
                "DDD", // 12 - day of year
                "WW", // 13 - week of year
                "WWW", // 14 - week of year with 'W' prefix
                "W", // 15 - week of month
                "E", // 16 - era
                "TZL", // 17 - timezone long name
                "TZS", // 18 - timezone short name
        });
    }

    @Override
    protected String convertPattern(int patternIndex, DatetimeObject jdt) {
        // FIXME
        LocaledDateFormat dfs = new LocaledDateFormat(jdt.getLocale());
        switch (patternIndex) {
            case 0:
                return printThousands(jdt.getYear());
            case 1:
                return printTens(jdt.getMonth());
            case 2:
                return printTens(jdt.getDay());
            case 3:
                return Integer.toString(jdt.getDayOfWeek());
            case 4:
                return dfs.getMonth(jdt.getMonth() - 1);
            case 5:
                return dfs.getShortMonth(jdt.getMonth() - 1);
            case 6:
                return dfs.getWeekday((jdt.getDayOfWeek() % 7) + 1);
            case 7:
                return dfs.getShortWeekday((jdt.getDayOfWeek() % 7) + 1);
            case 8:
                return printTens(jdt.getHour());
            case 9:
                return printTens(jdt.getMinute());
            case 10:
                return printTens(jdt.getSecond());
            case 11:
                return printHundreds(jdt.getMillisecond());
            case 12:
                return printHundreds(jdt.getDayOfYear());
            case 13:
                return printTens(jdt.getWeekOfYear());
            case 14:
                return 'W' + printTens(jdt.getWeekOfYear());
            case 15:
                return Integer.toString(jdt.getWeekOfMonth());
            case 16:
                return jdt.getEra() == 1 ? dfs.getAdEra() : dfs.getBcEra();
            case 17:
                return jdt.getTimeZone().getDisplayName(jdt.isInDaylightTime(), TimeZone.LONG, jdt.getLocale());
            case 18:
                return jdt.getTimeZone().getDisplayName(jdt.isInDaylightTime(), TimeZone.SHORT, jdt.getLocale());
            default:
                return new String(patterns[patternIndex]);
        }
    }

    @Override
    protected void parseValue(int patternIndex, String value, DateTimeStamp destination) {
        int v = Integer.parseInt(value);
        switch (patternIndex) {
            case 0:
                destination.year = v;
                break;
            case 1:
                destination.month = v;
                break;
            case 2:
                destination.day = v;
                break;
            case 8:
                destination.hour = v;
                break;
            case 9:
                destination.minute = v;
                break;
            case 10:
                destination.second = v;
                break;
            case 11:
                destination.millisecond = v;
                break;
            default:
                throw new IllegalArgumentException("Parsing template failed: " + new String(patterns[patternIndex]));
        }
    }
}
