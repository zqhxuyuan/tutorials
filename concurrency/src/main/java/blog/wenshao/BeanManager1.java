package blog.wenshao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqhxuyuan on 15-5-13.
 */
public class BeanManager1 {
    private Map<String, Object> map = new HashMap<String, Object>();

    public Object getBean(String key) {
        //同步Map
        synchronized (map) {
            Object bean = map.get(key);
            if (bean == null) {
                //如果不存在, 新建并放入Map中
                map.put(key, createBean());
                bean = map.get(key);
            }
            return bean;
        }
    }

    private Object createBean(){
        return new Object();
    }
}