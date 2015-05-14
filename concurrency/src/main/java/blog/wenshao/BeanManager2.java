package blog.wenshao;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zqhxuyuan on 15-5-13.
 *
 * ConcurrentHashMap并没有实现Lock-Free,只是使用了分离锁的办法使得能够支持多个Writer并发。
 * ConcurrentHashMap需要使用更多的内存
 */
public class BeanManager2 {
    //使用ConcurrentMap,避免直 接使用锁,锁由数据结构来管理。
    private ConcurrentMap<String, Object> map = new ConcurrentHashMap<String, Object>();

    public Object getBean(String key) {
        Object bean = map.get(key);
        if (bean == null) {
            //直接使用ConcurrentHashMap提供的原子操作: 如果不存在则新建并放入Map中
            map.putIfAbsent(key, createBean());
            bean = map.get(key);
        }
        return bean;
    }

    private Object createBean() {
        return new Object();
    }

}