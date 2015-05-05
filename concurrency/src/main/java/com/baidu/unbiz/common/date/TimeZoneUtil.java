/**
 * 
 */
package com.baidu.unbiz.common.date;

import java.util.TimeZone;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:17:47
 */
public abstract class TimeZoneUtil {

    public static int getRawOffsetDifference(TimeZone from, TimeZone to) {
        int offsetBefore = from.getRawOffset();
        int offsetAfter = to.getRawOffset();
        return offsetAfter - offsetBefore;
    }

    public static int getOffsetDifference(long now, TimeZone from, TimeZone to) {
        int offsetBefore = from.getOffset(now);
        int offsetAfter = to.getOffset(now);
        return offsetAfter - offsetBefore;
    }

    public static int getOffset(DatetimeObject jdt, TimeZone tz) {
        return tz.getOffset(jdt.getEra(), jdt.getYear(), jdt.getMonth() - 1, jdt.getDay(),
                TimeUtil.toCalendarDayOfWeek(jdt.getDayOfWeek()), jdt.getMillisOfDay());
    }

    public static int getOffsetDifference(DatetimeObject jdt, TimeZone from, TimeZone to) {
        int offsetBefore = getOffset(jdt, from);
        int offsetAfter = getOffset(jdt, to);
        return offsetAfter - offsetBefore;
    }
}
