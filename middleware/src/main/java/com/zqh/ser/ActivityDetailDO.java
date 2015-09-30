/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package com.zqh.ser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kok 2014年9月12日 上午10:15:04
 */
public class ActivityDetailDO implements Serializable {

    private static final long             serialVersionUID = 3942128251898371315L;
    private static final Logger           logger           = LoggerFactory.getLogger(ActivityDetailDO.class);
    private String                        sequenceId;
    private Map<String, String>           eventResultMap   = new HashMap<String, String>();                  // 事件结果信息
    private Map<String, String>           activityMap      = new HashMap<String, String>();                  // 原activity表数据
    private Map<String, String>           geoMap           = new HashMap<String, String>();                  // 原activity_geo表数据
    private Map<String, String>           browserMap       = new HashMap<String, String>();                  // 原activity_browser表数据
    private Map<String, String>           deviceMap        = new HashMap<String, String>();                  // 原activity_device或activity_sdk表数据
    private Map<String, String>           policyMap        = new HashMap<String, String>();                  // 策略信息
    private long                          timestamp;                                                         // 时间戳
    @SuppressWarnings("unused")
    private String                        gmtCreate;                                                         // 展示时间

    private static final SimpleDateFormat dateTimeFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getGmtCreate() {
        String result = "";
        try {
            result = dateTimeFormat.format(new Date(timestamp));
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return result;
        }
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Map<String, String> getEventResultMap() {
        return eventResultMap;
    }

    public void setEventResultMap(Map<String, String> eventResultMap) {
        this.eventResultMap = eventResultMap;
    }

    public Map<String, String> getActivityMap() {
        return activityMap;
    }

    public void setActivityMap(Map<String, String> activityMap) {
        this.activityMap = activityMap;
    }

    public Map<String, String> getGeoMap() {
        return geoMap;
    }

    public void setGeoMap(Map<String, String> geoMap) {
        this.geoMap = geoMap;
    }

    public Map<String, String> getBrowserMap() {
        return browserMap;
    }

    public void setBrowserMap(Map<String, String> browserMap) {
        this.browserMap = browserMap;
    }

    public Map<String, String> getDeviceMap() {
        return deviceMap;
    }

    public void setDeviceMap(Map<String, String> deviceMap) {
        this.deviceMap = deviceMap;
    }

    public Map<String, String> getPolicyMap() {
        return policyMap;
    }

    public void setPolicyMap(Map<String, String> policyMap) {
        this.policyMap = policyMap;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activityMap == null) ? 0 : activityMap.hashCode());
        result = prime * result + ((browserMap == null) ? 0 : browserMap.hashCode());
        result = prime * result + ((deviceMap == null) ? 0 : deviceMap.hashCode());
        result = prime * result + ((geoMap == null) ? 0 : geoMap.hashCode());
        result = prime * result + ((policyMap == null) ? 0 : policyMap.hashCode());
        result = prime * result + ((sequenceId == null) ? 0 : sequenceId.hashCode());
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ActivityDetailDO other = (ActivityDetailDO) obj;
        if (activityMap == null) {
            if (other.activityMap != null) return false;
        } else if (!activityMap.equals(other.activityMap)) return false;
        if (browserMap == null) {
            if (other.browserMap != null) return false;
        } else if (!browserMap.equals(other.browserMap)) return false;
        if (deviceMap == null) {
            if (other.deviceMap != null) return false;
        } else if (!deviceMap.equals(other.deviceMap)) return false;
        if (geoMap == null) {
            if (other.geoMap != null) return false;
        } else if (!geoMap.equals(other.geoMap)) return false;
        if (policyMap == null) {
            if (other.policyMap != null) return false;
        } else if (!policyMap.equals(other.policyMap)) return false;
        if (sequenceId == null) {
            if (other.sequenceId != null) return false;
        } else if (!sequenceId.equals(other.sequenceId)) return false;
        if (timestamp != other.timestamp) return false;
        return true;
    }

}
