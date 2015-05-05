package com.baidu.unbiz.common.date;

import java.io.Serializable;

import com.baidu.unbiz.common.HashCode;
import com.baidu.unbiz.common.able.CloneableObject;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:09:21
 */
public class DateTimeStamp implements Comparable<DateTimeStamp>, Serializable, CloneableObject<DateTimeStamp> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1586526670204046957L;

    public DateTimeStamp() {

    }

    public DateTimeStamp(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
    }

    public DateTimeStamp(int year, int month, int day) {
        this(year, month, day, 0, 0, 0, 0);
    }

    /**
     * Year.
     */
    public int year;

    /**
     * Month, range: [1 - 12]
     */
    public int month = 1;

    /**
     * Day, range: [1 - 31]
     */
    public int day = 1;

    /**
     * Hour, range: [0 - 23]
     */
    public int hour;

    /**
     * Minute, range [0 - 59]
     */
    public int minute;

    /**
     * Second, range: [0 - 59]
     */
    public int second;

    /**
     * Millisecond, range: [0 - 1000]
     */
    public int millisecond;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }

    public int compareTo(DateTimeStamp another) {
        int thisDate = year * 10000 + month * 100 + day;
        int anotherDate = another.year * 10000 + another.month * 100 + another.day;

        if (thisDate < anotherDate) {
            return -1;
        }
        if (thisDate > anotherDate) {
            return 1;
        }

        thisDate = (hour * 10000000) + (minute * 100000) + (second * 1000) + millisecond;
        anotherDate =
                (another.hour * 10000000) + (another.minute * 100000) + (another.second * 1000) + another.millisecond;

        if (thisDate < anotherDate) {
            return -1;
        }
        if (thisDate > anotherDate) {
            return 1;
        }
        return 0;
    }

    public int compareDateTo(Object o) {
        DateTimeStamp dts = (DateTimeStamp) o;

        int date1 = year * 10000 + month * 100 + day;
        int date2 = dts.year * 10000 + dts.month * 100 + dts.day;

        if (date1 < date2) {
            return -1;
        }
        if (date1 > date2) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(25);
        sb.append(year).append('-').append(month).append('-').append(day).append(' ');
        sb.append(hour).append(':').append(minute).append(':').append(second).append('.').append(millisecond);
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DateTimeStamp)) {
            return false;
        }
        DateTimeStamp stamp = (DateTimeStamp) object;
        return (stamp.year == this.year) && (stamp.month == this.month) && (stamp.day == this.day)
                && (stamp.hour == this.hour) && (stamp.minute == this.minute) && (stamp.second == this.second)
                && (stamp.millisecond == this.millisecond);
    }

    @Override
    public int hashCode() {
        int result = HashCode.SEED;
        result = HashCode.hash(result, year);
        result = HashCode.hash(result, month);
        result = HashCode.hash(result, day);
        result = HashCode.hash(result, hour);
        result = HashCode.hash(result, minute);
        result = HashCode.hash(result, second);
        result = HashCode.hash(result, millisecond);
        return result;
    }

    @Override
    public DateTimeStamp clone() {
        DateTimeStamp dts = new DateTimeStamp();
        dts.year = this.year;
        dts.month = this.month;
        dts.day = this.day;
        dts.hour = this.hour;
        dts.minute = this.minute;
        dts.second = this.second;
        dts.millisecond = this.millisecond;
        return dts;
    }

    public boolean isEqualDate(DateTimeStamp date) {
        return date.day == this.day && date.month == this.month && date.year == this.year;
    }

    public boolean isEqualTime(DateTimeStamp time) {
        return time.hour == this.hour && time.minute == this.minute && time.second == this.second
                && time.millisecond == this.millisecond;
    }

}
