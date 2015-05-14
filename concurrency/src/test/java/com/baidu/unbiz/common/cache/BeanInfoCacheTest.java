/**
 * 
 */
package com.baidu.unbiz.common.cache;

import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Map;

import org.junit.Test;

import com.baidu.unbiz.common.bean.sample.Abean;
import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年10月16日 下午7:17:46
 */
public class BeanInfoCacheTest extends CachedLogger {

    @Test
    public void getPropertyDescriptor() {
        Map<String, PropertyDescriptor> propertyMap = BeanInfoCache.getInstance().getPropertyDescriptor(Abean.class);
        for (Map.Entry<String, PropertyDescriptor> entry : propertyMap.entrySet()) {
            logger.info(entry.getKey());
            logger.info(entry.getValue().getName());
        }
    }

    @Test
    public void getMethodDescriptor() {
        Map<String, MethodDescriptor> propertyMap = BeanInfoCache.getInstance().getMethodDescriptor(Abean.class);
        for (Map.Entry<String, MethodDescriptor> entry : propertyMap.entrySet()) {
            logger.info(entry.getKey());
            logger.info(entry.getValue().getName());
        }
    }

}
