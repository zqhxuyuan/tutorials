package com.zqh.time;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

/**
 * Created by zhengqh on 15/12/8.
 */
public class JodaTimeTest {

    public static void main(String[] args) {
        //当前时间
        DateTime time = new DateTime();

        //修改时间
        MutableDateTime mutableDateTime = time.toMutableDateTime();
        //设置时分秒都为0, 只保留到天
        mutableDateTime.setMillisOfDay(0);
        DateTime dateTime =  mutableDateTime.toDateTime();

        System.out.println(time);       //2015-12-08T18:02:24.562+08:00
        System.out.println(dateTime);   //2015-12-08T00:00:00.000+08:00
    }
}
