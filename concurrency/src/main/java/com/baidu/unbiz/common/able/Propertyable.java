package com.baidu.unbiz.common.able;

import java.util.Map;

/**
 * <code>Property</code>接口
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月19日 上午2:31:58
 */
public interface Propertyable {

    Object getProperty(String key);

    Map<String, Object> getProperties();

    void setProperty(String key, Object value);

    void setProperties(Map<String, Object> properties);
}
