/**
 * 
 */
package com.baidu.unbiz.common.date.format;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午6:00:39
 */
public class LocaledDateFormat {

    protected final String[] months;
    protected final String[] shortMonths;
    protected final String[] weekdays;
    protected final String[] shortWeekdays;
    protected final String[] eras;
    protected final String[] ampms;

    public LocaledDateFormat(Locale locale) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);

        months = dateFormatSymbols.getMonths();
        shortMonths = dateFormatSymbols.getShortMonths();
        weekdays = dateFormatSymbols.getWeekdays();
        shortWeekdays = dateFormatSymbols.getShortWeekdays();
        eras = dateFormatSymbols.getEras();
        ampms = dateFormatSymbols.getAmPmStrings();
    }

    public String getMonth(int i) {
        return this.months[i];
    }

    public String getShortMonth(int i) {
        return this.shortMonths[i];
    }

    public String getWeekday(int i) {
        return this.weekdays[i];
    }

    public String getShortWeekday(int i) {
        return this.shortWeekdays[i];
    }

    public String getBcEra() {
        return this.eras[0];
    }

    public String getAdEra() {
        return this.eras[1];
    }

    public String getAM() {
        return this.ampms[0];
    }

    public String getPM() {
        return this.ampms[1];
    }

}