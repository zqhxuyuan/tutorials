/**
 * 
 */
package com.baidu.unbiz.common.date;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.baidu.unbiz.common.HashCode;
import com.baidu.unbiz.common.date.format.Format;
import com.baidu.unbiz.common.date.format.Formatter;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:25:06
 */
public class DatetimeObject implements Comparable<Object>, Cloneable {

    public static final String DEFAULT_FORMAT = "YYYY-MM-DD hh:mm:ss.mss";

    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    public static final int SUNDAY = 7;

    public static final int JANUARY = 1;
    public static final int FEBRUARY = 2;
    public static final int MARCH = 3;
    public static final int APRIL = 4;
    public static final int MAY = 5;
    public static final int JUNE = 6;
    public static final int JULY = 7;
    public static final int AUGUST = 8;
    public static final int SEPTEMBER = 9;
    public static final int OCTOBER = 10;
    public static final int NOVEMBER = 11;
    public static final int DECEMBER = 12;

    protected DateTimeStamp time = new DateTimeStamp();

    protected int dayofweek;

    protected int dayofyear;

    protected boolean leap;

    protected int weekofyear;

    protected int weekofmonth;

    protected JulianDateStamp jdate;

    public static final JulianDateStamp JD_1970 = new JulianDateStamp(2440587, 0.5);

    public static final JulianDateStamp JD_2001 = new JulianDateStamp(2451910, 0.5);

    public DateTimeStamp getDateTimeStamp() {
        return time;
    }

    public void setDateTimeStamp(DateTimeStamp dts) {
        set(dts.year, dts.month, dts.day, dts.hour, dts.minute, dts.second, dts.millisecond);
    }

    public void setJulianDate(JulianDateStamp jds) {
        setJdOnly(jds.clone());
        calculateAdditionalData();
    }

    public JulianDateStamp getJulianDate() {
        return jdate;
    }

    public int getJulianDayNumber() {
        return jdate.getJulianDayNumber();
    }

    private void calculateAdditionalData() {
        this.leap = TimeUtil.isLeapYear(time.year);
        this.dayofweek = calcDayOfWeek();
        this.dayofyear = calcDayOfYear();
        this.weekofyear = calcWeekOfYear(firstDayOfWeek, mustHaveDayOfFirstWeek);
        this.weekofmonth = calcWeekNumber(time.day, this.dayofweek);
    }

    private void setJdOnly(JulianDateStamp jds) {
        jdate = jds;
        time = TimeUtil.fromJulianDate(jds);
    }

    public void set(int year, int month, int day, int hour, int minute, int second, int millisecond) {

        jdate = TimeUtil.toJulianDate(year, month, day, hour, minute, second, millisecond);

        if (TimeUtil.isValidDateTime(year, month, day, hour, minute, second, millisecond)) {
            time.year = year;
            time.month = month;
            time.day = day;
            time.hour = hour;
            time.minute = minute;
            time.second = second;
            time.millisecond = millisecond;
            calculateAdditionalData();
        } else {
            setJulianDate(jdate);
        }

    }

    private void setJdOnly(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        setJdOnly(TimeUtil.toJulianDate(year, month, day, hour, minute, second, millisecond));
    }

    private int calcDayOfWeek() {
        int jd = (int) (jdate.doubleValue() + 0.5);
        return (jd % 7) + 1;
    }

    private static final int NUM_DAYS[] = { -1, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 }; // 1-based
    private static final int LEAP_NUM_DAYS[] = { -1, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 }; // 1-based

    private int calcDayOfYear() {
        if (leap == true) {
            return LEAP_NUM_DAYS[time.month] + time.day;
        }
        return NUM_DAYS[time.month] + time.day;
    }

    private int calcWeekOfYear(int start, int must) {

        int delta = 0;
        if (start <= this.dayofweek) {
            if (must < start) {
                delta = 7;
            }
        } else {
            if (must >= start) {
                delta = -7;
            }
        }

        int jd = (int) (jdate.doubleValue() + 0.5) + delta;
        int WeekDay = (jd % 7) + 1;

        int time_year = time.year;
        int DayOfYearNumber = this.dayofyear + delta;
        if (DayOfYearNumber < 1) {
            time_year--;
            DayOfYearNumber = TimeUtil.isLeapYear(time_year) ? 366 + DayOfYearNumber : 365 + DayOfYearNumber;
        } else if (DayOfYearNumber > (this.leap ? 366 : 365)) {
            DayOfYearNumber = this.leap ? DayOfYearNumber - 366 : DayOfYearNumber - 365;
            time_year++;
        }

        // modification, if required, is finished. proceed to the calculation.

        int firstDay = jd - DayOfYearNumber + 1;
        int Jan1WeekDay = (firstDay % 7) + 1;

        // find if the date falls in YearNumber Y - 1 set WeekNumber to 52 or 53
        int YearNumber = time_year;
        int WeekNumber = 52;
        if ((DayOfYearNumber <= (8 - Jan1WeekDay)) && (Jan1WeekDay > must)) {
            YearNumber--;
            if ((Jan1WeekDay == must + 1) || ((Jan1WeekDay == must + 2) && (TimeUtil.isLeapYear(YearNumber)))) {
                WeekNumber = 53;
            }
        }

        // set WeekNumber to 1 to 53 if date falls in YearNumber
        int m = 365;
        if (YearNumber == time_year) {
            if (TimeUtil.isLeapYear(time_year) == true) {
                m = 366;
            }
            if ((m - DayOfYearNumber) < (must - WeekDay)) {
                YearNumber = time_year + 1;
                WeekNumber = 1;
            }
        }

        if (YearNumber == time_year) {
            int n = DayOfYearNumber + (7 - WeekDay) + (Jan1WeekDay - 1);
            WeekNumber = n / 7;
            if (Jan1WeekDay > must) {
                WeekNumber -= 1;
            }
        }
        return WeekNumber;
    }

    private int calcWeekNumber(int dayOfPeriod, int dayOfWeek) {

        int periodStartDayOfWeek = (dayOfWeek - firstDayOfWeek - dayOfPeriod + 1) % 7;
        if (periodStartDayOfWeek < 0) {
            periodStartDayOfWeek += 7;
        }

        int weekNo = (dayOfPeriod + periodStartDayOfWeek - 1) / 7;

        if ((7 - periodStartDayOfWeek) >= minDaysInFirstWeek) {
            ++weekNo;
        }

        return weekNo;
    }

    public void add(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean monthFix) {
        int difference = 0;
        if (trackDST) {
            difference = TimeZoneUtil.getOffset(this, timezone);
        }
        addNoDST(year, month, day, hour, minute, second, millisecond, monthFix);
        if (trackDST) {
            difference = TimeZoneUtil.getOffset(this, timezone) - difference;
            if (difference != 0) {
                addNoDST(0, 0, 0, 0, 0, 0, difference, false);
            }
        }
    }

    protected void addNoDST(int year, int month, int day, int hour, int minute, int second, int millisecond,
            boolean monthFix) {
        millisecond += time.millisecond;
        second += time.second;
        minute += time.minute;
        hour += time.hour;
        day += time.day;
        if (monthFix == false) {
            month += time.month;
            year += time.year;
            set(year, month, day, hour, minute, second, millisecond);
        } else {

            setJdOnly(time.year, time.month, day, hour, minute, second, millisecond);
            int from = time.day;
            month += time.month + (year * 12); // delta years to add are
                                               // converted to delta months
            setJdOnly(time.year, month, time.day, time.hour, time.minute, time.second, time.millisecond);
            if (time.day < from) {
                set(time.year, time.month, 0, time.hour, time.minute, time.second, time.millisecond);
            } else {
                calculateAdditionalData();
            }

        }
    }

    public void sub(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean monthFix) {
        add(-year, -month, -day, -hour, -minute, -second, -millisecond, monthFix);
    }

    public void add(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        add(year, month, day, hour, minute, second, millisecond, monthFix);
    }

    public void sub(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        add(-year, -month, -day, -hour, -minute, -second, millisecond, monthFix);
    }

    public void add(int year, int month, int day, boolean monthFix) {
        add(year, month, day, 0, 0, 0, 0, monthFix);
    }

    public void sub(int year, int month, int day, boolean monthFix) {
        add(-year, -month, -day, 0, 0, 0, 0, monthFix);
    }

    public void add(int year, int month, int day) {
        add(year, month, day, monthFix);
    }

    public void sub(int year, int month, int day) {
        add(-year, -month, -day, monthFix);
    }

    public void addTime(int hour, int minute, int second, int millisecond, boolean monthFix) {
        add(0, 0, 0, hour, minute, second, millisecond, monthFix);
    }

    public void subTime(int hour, int minute, int second, int millisecond, boolean monthFix) {
        add(0, 0, 0, -hour, -minute, -second, -millisecond, monthFix);
    }

    public void addTime(int hour, int minute, int second, boolean monthFix) {
        add(0, 0, 0, hour, minute, second, 0, monthFix);
    }

    public void subTime(int hour, int minute, int second, boolean monthFix) {
        add(0, 0, 0, -hour, -minute, -second, 0, monthFix);
    }

    public void addTime(int hour, int minute, int second, int millisecond) {
        addTime(hour, minute, second, millisecond, monthFix);
    }

    public void subTime(int hour, int minute, int second, int millisecond) {
        addTime(-hour, -minute, -second, -millisecond, monthFix);
    }

    public void addTime(int hour, int minute, int second) {
        addTime(hour, minute, second, 0, monthFix);
    }

    public void subTime(int hour, int minute, int second) {
        addTime(-hour, -minute, -second, 0, monthFix);
    }

    public void addYear(int y, boolean monthFix) {
        add(y, 0, 0, monthFix);
    }

    public void subYear(int y, boolean monthFix) {
        add(-y, 0, 0, monthFix);
    }

    public void addYear(int y) {
        addYear(y, monthFix);
    }

    public void subYear(int y) {
        addYear(-y, monthFix);
    }

    public void addMonth(int m, boolean monthFix) {
        add(0, m, 0, monthFix);
    }

    public void subMonth(int m, boolean monthFix) {
        add(0, -m, 0, monthFix);
    }

    public void addMonth(int m) {
        addMonth(m, monthFix);
    }

    public void subMonth(int m) {
        addMonth(-m, monthFix);
    }

    public void addDay(int d, boolean monthFix) {
        add(0, 0, d, monthFix);
    }

    public void subDay(int d, boolean monthFix) {
        add(0, 0, -d, monthFix);
    }

    public void addDay(int d) {
        addDay(d, monthFix);
    }

    public void subDay(int d) {
        addDay(-d, monthFix);
    }

    public void addHour(int h, boolean monthFix) {
        addTime(h, 0, 0, 0, monthFix);
    }

    public void subHour(int h, boolean monthFix) {
        addTime(-h, 0, 0, 0, monthFix);
    }

    public void addHour(int h) {
        addHour(h, monthFix);
    }

    public void subHour(int h) {
        addHour(-h, monthFix);
    }

    public void addMinute(int m, boolean monthFix) {
        addTime(0, m, 0, 0, monthFix);
    }

    public void subMinute(int m, boolean monthFix) {
        addTime(0, -m, 0, 0, monthFix);
    }

    public void addMinute(int m) {
        addMinute(m, monthFix);
    }

    public void subMinute(int m) {
        addMinute(-m, monthFix);
    }

    public void addSecond(int s, boolean monthFix) {
        addTime(0, 0, s, 0, monthFix);
    }

    public void subSecond(int s, boolean monthFix) {
        addTime(0, 0, -s, 0, monthFix);
    }

    public void addSecond(int s) {
        addSecond(s, monthFix);
    }

    public void subSecond(int s) {
        addSecond(-s, monthFix);
    }

    public void addMillisecond(int ms, boolean monthFix) {
        addTime(0, 0, 0, ms, monthFix);
    }

    public void subMillisecond(int ms, boolean monthFix) {
        addTime(0, 0, 0, -ms, monthFix);
    }

    public void addMillisecond(int ms) {
        addMillisecond(ms, monthFix);
    }

    public void subMillisecond(int ms) {
        addMillisecond(-ms, monthFix);
    }

    public DatetimeObject(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this.set(year, month, day, hour, minute, second, millisecond);
    }

    public void set(int year, int month, int day) {
        set(year, month, day, 0, 0, 0, 0);
    }

    public DatetimeObject(int year, int month, int day) {
        this.set(year, month, day);
    }

    public void setTime(int hour, int minute, int second, int millisecond) {
        set(time.year, time.month, time.day, hour, minute, second, millisecond);
    }

    public void setDate(int year, int month, int day) {
        set(year, month, day, time.hour, time.minute, time.second, time.millisecond);
    }

    public DatetimeObject(long millis) {
        setTimeInMillis(millis);
    }

    public void setTimeInMillis(long millis) {
        millis += timezone.getOffset(millis);
        int integer = (int) (millis / TimeUtil.MILLIS_IN_DAY);
        double fraction = (double) (millis % TimeUtil.MILLIS_IN_DAY) / TimeUtil.MILLIS_IN_DAY;
        integer += JD_1970.integer;
        fraction += JD_1970.fraction;
        setJulianDate(new JulianDateStamp(integer, fraction));
    }

    public long getTimeInMillis() {
        double then = (jdate.fraction - JD_1970.fraction) * TimeUtil.MILLIS_IN_DAY;
        then += (jdate.integer - JD_1970.integer) * TimeUtil.MILLIS_IN_DAY;
        then -= timezone.getOffset((long) then);
        then += then > 0 ? 1.0e-6 : -1.0e-6;
        return (long) then;
    }

    public void setYear(int y) {
        setDate(y, time.month, time.day);
    }

    public void setMonth(int m) {
        setDate(time.year, m, time.day);
    }

    public void setDay(int d) {
        setDate(time.year, time.month, d);
    }

    public void setHour(int h) {
        setTime(h, time.minute, time.second, time.millisecond);
    }

    public void setMinute(int m) {
        setTime(time.hour, m, time.second, time.millisecond);

    }

    public void setSecond(int s) {
        setTime(time.hour, time.minute, s, time.millisecond);
    }

    public void setSecond(int s, int m) {
        setTime(time.hour, time.minute, s, m);
    }

    public void setMillisecond(int m) {
        setTime(time.hour, time.minute, time.second, m);
    }

    public int getYear() {
        return time.year;
    }

    public int getMonth() {
        return time.month;
    }

    public int getDay() {
        return time.day;
    }

    public int getDayOfMonth() {
        return time.day;
    }

    public int getHour() {
        return time.hour;
    }

    public int getMinute() {
        return time.minute;
    }

    public int getSecond() {
        return time.second;
    }

    public int getMillisecond() {
        return time.millisecond;
    }

    public int getDayOfWeek() {
        return dayofweek;
    }

    public int getDayOfYear() {
        return dayofyear;
    }

    public boolean isLeapYear() {
        return leap;
    }

    public int getWeekOfYear() {
        return weekofyear;
    }

    public int getWeekOfMonth() {
        return weekofmonth;
    }

    private static final int MONTH_LENGTH[] = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public int getMonthLength(int m) {
        if ((m < 1) || (m > 12)) {
            throw new IllegalArgumentException("Invalid month: " + m);
        }
        if (m == 2) {
            return this.leap ? 29 : 28;
        }
        if ((time.year == 1582) && (time.month == 10)) {
            return 21;
        }
        return MONTH_LENGTH[m];
    }

    public int getMonthLength() {
        return getMonthLength(time.month);
    }

    public int getEra() {
        return time.year > 0 ? 1 : 0;
    }

    public int getMillisOfDay() {
        return ((((time.hour * 60) + time.minute) * 60) + time.second) * 1000 + time.millisecond;
    }

    public void setCurrentTime() {
        setTimeInMillis(System.currentTimeMillis());
    }

    public DatetimeObject() {
        this.setCurrentTime();
    }

    public DatetimeObject(Calendar calendar) {
        setDateTime(calendar);
    }

    public void setDateTime(Calendar calendar) {
        setTimeInMillis(calendar.getTimeInMillis());
        changeTimeZone(calendar.getTimeZone());
    }

    public DatetimeObject(Date date) {
        setDateTime(date);
    }

    public void setDateTime(Date date) {
        setTimeInMillis(date.getTime());
    }

    public Date convertToDate() {
        return new Date(getTimeInMillis());
    }

    public Calendar convertToCalendar() {
        Calendar calendar = Calendar.getInstance(getTimeZone());
        calendar.setTimeInMillis(getTimeInMillis());
        return calendar;
    }

    public java.sql.Date convertToSqlDate() {
        return new java.sql.Date(getTimeInMillis());
    }

    public Time convertToSqlTime() {
        return new Time(getTimeInMillis());
    }

    public Timestamp convertToSqlTimestamp() {
        return new Timestamp(getTimeInMillis());
    }

    public DatetimeObject(DateTimeStamp dts) {
        setDateTimeStamp(dts);
    }

    public DatetimeObject(JulianDateStamp jds) {
        setJulianDate(jds);
    }

    public DatetimeObject(double jd) {
        setJulianDate(new JulianDateStamp(jd));
    }

    public DatetimeObject(String src) {
        parse(src);
    }

    public DatetimeObject(String src, String template) {
        parse(src, template);
    }

    public DatetimeObject(String src, Format jdtFormat) {
        parse(src, jdtFormat);
    }

    protected boolean trackDST = DefaultDatetime.trackDST;

    public boolean isTrackDST() {
        return trackDST;
    }

    public void setTrackDST(boolean trackDST) {
        this.trackDST = trackDST;
    }

    protected boolean monthFix = DefaultDatetime.monthFix;

    public boolean isMonthFix() {
        return monthFix;
    }

    public void setMonthFix(boolean monthFix) {
        this.monthFix = monthFix;
    }

    protected TimeZone timezone = DefaultDatetime.timeZone == null ? TimeZone.getDefault() : DefaultDatetime.timeZone;

    public void changeTimeZone(TimeZone timezone) {
        long now = getTimeInMillis();
        int difference = TimeZoneUtil.getOffsetDifference(now, this.timezone, timezone);
        this.timezone = timezone;
        if (difference != 0) {
            addMillisecond(difference);
        }
    }

    public void setTimeZone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public TimeZone getTimeZone() {
        return timezone;
    }

    public boolean isInDaylightTime() {
        long now = getTimeInMillis();
        int offset = timezone.getOffset(now);
        int rawOffset = timezone.getRawOffset();
        return (offset != rawOffset);
    }

    protected Locale locale = DefaultDatetime.locale == null ? Locale.getDefault() : DefaultDatetime.locale;

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    protected String format = DefaultDatetime.format;

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    protected Formatter jdtFormatter = DefaultDatetime.formatter;

    public void setJdtFormatter(Formatter jdtFormatter) {
        this.jdtFormatter = jdtFormatter;
    }

    public Formatter getJdtFormatter() {
        return jdtFormatter;
    }

    public void setJdtFormat(Format jdtFormat) {
        this.format = jdtFormat.getFormat();
        this.jdtFormatter = jdtFormat.getFormatter();
    }

    public String toString(String format) {
        return jdtFormatter.convert(this, format);
    }

    @Override
    public String toString() {
        return jdtFormatter.convert(this, format);
    }

    public String toString(Format jdtFormat) {
        return jdtFormat.convert(this);
    }

    public void parse(String src, String format) {
        setDateTimeStamp(jdtFormatter.parse(src, format));
    }

    public void parse(String src) {
        setDateTimeStamp(jdtFormatter.parse(src, format));
    }

    public void parse(String src, Format jdtFormat) {
        setDateTimeStamp(jdtFormat.parse(src));
    }

    public boolean isValid(String s) {
        return isValid(s, format);
    }

    public boolean isValid(String s, String template) {
        DateTimeStamp dtsOriginal;
        try {
            dtsOriginal = jdtFormatter.parse(s, template);
        } catch (Exception ignore) {
            return false;
        }
        if (dtsOriginal == null) {
            return false;
        }
        return TimeUtil.isValidDateTime(dtsOriginal);
    }

    protected int firstDayOfWeek = DefaultDatetime.firstDayOfWeek;
    protected int mustHaveDayOfFirstWeek = DefaultDatetime.mustHaveDayOfFirstWeek;

    public void setWeekDefinition(int start, int must) {
        if ((start >= 1) && (start <= 7)) {
            firstDayOfWeek = start;
        }
        if ((must >= 1) && (must <= 7)) {
            mustHaveDayOfFirstWeek = must;
            minDaysInFirstWeek = convertMin2Must(firstDayOfWeek, must);
        }
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public int getMustHaveDayOfFirstWeek() {
        return mustHaveDayOfFirstWeek;
    }

    protected int minDaysInFirstWeek = DefaultDatetime.minDaysInFirstWeek;

    public int getMinDaysInFirstWeek() {
        return minDaysInFirstWeek;
    }

    public void setWeekDefinitionAlt(int start, int min) {
        if ((start >= 1) && (start <= 7)) {
            firstDayOfWeek = start;
        }
        if ((min >= 1) && (min <= 7)) {
            mustHaveDayOfFirstWeek = convertMin2Must(firstDayOfWeek, min);
            minDaysInFirstWeek = min;
        }
    }

    private static int convertMin2Must(int start, int min) {
        int must = 8 - min + (start - 1);
        if (must > 7) {
            must -= 7;
        }
        return must;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DatetimeObject)) {
            return false;
        }
        DatetimeObject jdt = (DatetimeObject) obj;
        return (this.monthFix == jdt.monthFix) && (this.firstDayOfWeek == jdt.firstDayOfWeek)
                && (this.mustHaveDayOfFirstWeek == jdt.mustHaveDayOfFirstWeek) && (this.jdate.equals(jdt.jdate))
                && (this.timezone.equals(jdt.timezone));
    }

    @Override
    public int hashCode() {
        int result = HashCode.SEED;
        result = HashCode.hash(result, jdate);
        result = HashCode.hash(result, timezone);
        result = HashCode.hash(result, monthFix);
        result = HashCode.hash(result, firstDayOfWeek);
        result = HashCode.hash(result, mustHaveDayOfFirstWeek);
        return result;
    }

    @Override
    public DatetimeObject clone() {
        DatetimeObject jdt = new DatetimeObject(this.jdate);
        jdt.monthFix = this.monthFix;
        jdt.timezone = this.timezone;
        jdt.locale = this.locale;
        jdt.format = this.format;
        jdt.jdtFormatter = this.jdtFormatter;
        jdt.firstDayOfWeek = this.firstDayOfWeek;
        jdt.mustHaveDayOfFirstWeek = this.mustHaveDayOfFirstWeek;
        jdt.trackDST = this.trackDST;
        return jdt;
    }

    public int compareTo(Object o) {
        return time.compareTo(((DatetimeObject) o).getDateTimeStamp());
    }

    public int compareTo(DatetimeObject jd) {
        return time.compareTo(jd.getDateTimeStamp());
    }

    public int compareDateTo(DatetimeObject jd) {
        return time.compareDateTo(jd.getDateTimeStamp());
    }

    public boolean isAfter(DatetimeObject then) {
        return time.compareTo((then).getDateTimeStamp()) > 0;
    }

    public boolean isBefore(DatetimeObject then) {
        return time.compareTo((then).getDateTimeStamp()) < 0;
    }

    public boolean isAfterDate(DatetimeObject then) {
        return time.compareDateTo((then).getDateTimeStamp()) > 0;
    }

    public boolean isBeforeDate(DatetimeObject then) {
        return time.compareDateTo((then).getDateTimeStamp()) < 0;
    }

    public int daysBetween(DatetimeObject then) {
        return this.jdate.daysBetween(then.jdate);
    }

    public int daysBetween(JulianDateStamp then) {
        return this.jdate.daysBetween(then);
    }

    public double getJulianDateDouble() {
        return jdate.doubleValue();
    }

    public void setJulianDate(double jd) {
        setJulianDate(new JulianDateStamp(jd));
    }

    public JulianDateStamp getReducedJulianDate() {
        return jdate.getReducedJulianDate();
    }

    public double getReducedJulianDateDouble() {
        return jdate.getReducedJulianDate().doubleValue();
    }

    public void setReducedJulianDate(double rjd) {
        jdate.setReducedJulianDate(rjd);
    }

    public JulianDateStamp getModifiedJulianDate() {
        return jdate.getModifiedJulianDate();
    }

    public double getModifiedJulianDateDouble() {
        return jdate.getModifiedJulianDate().doubleValue();
    }

    public void setModifiedJulianDate(double mjd) {
        jdate.setModifiedJulianDate(mjd);
    }

    public JulianDateStamp getTruncatedJulianDate() {
        return jdate.getTruncatedJulianDate();
    }

    public double getTruncatedJulianDateDouble() {
        return jdate.getTruncatedJulianDate().doubleValue();
    }

    public void setTruncatedJulianDate(double tjd) {
        jdate.setTruncatedJulianDate(tjd);
    }

    public boolean equalsDate(int year, int month, int day) {
        return (time.year == year) && (time.month == month) && (time.day == day);
    }

    public boolean equalsDate(DatetimeObject date) {
        return time.isEqualDate(date.time);
    }

    public boolean equalsTime(DatetimeObject date) {
        return time.isEqualTime(date.time);
    }

}
