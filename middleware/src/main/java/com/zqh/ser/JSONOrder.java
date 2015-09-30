package com.zqh.ser;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengqh on 15/9/28.
 */
public class JSONOrder {

    public static void main(String[] args) {
        ActivityDetailDO activityDO = new ActivityDetailDO();

        Map<String, String> eventResultMap = activityDO.getEventResultMap();
        Map<String, String> activityMap = activityDO.getActivityMap();
        Map<String, String> deviceMap = activityDO.getDeviceMap();
        Map<String, String> browserMap = activityDO.getBrowserMap();
        Map<String, String> geoMap = activityDO.getGeoMap();
        Map<String, String> policyMap = activityDO.getPolicyMap();

        ActivityDetailDO activity = new ActivityDetailDO();

        activity.setSequenceId("1234567890");
        activity.setTimestamp(1234567890L);

        eventResultMap.put("event", "event");
        geoMap.put("geo", "geo");
        policyMap.put("policy", "policy");
        activityMap.put("activity", "activity");
        browserMap.put("browser", "browser");

        activity.setEventResultMap(eventResultMap);
        activity.setGeoMap(geoMap);
        activity.setPolicyMap(policyMap);
        activity.setActivityMap(activityMap);
        activity.setBrowserMap(browserMap);

        String jsonStr = JSON.toJSONString(activity);
        String newStr = jsonStr.replaceAll("[\\x00-\\x1f\\x7f\\x80-\\x9f\\xa0]", "");

        System.out.println(newStr);
        System.exit(0);

    }
}
