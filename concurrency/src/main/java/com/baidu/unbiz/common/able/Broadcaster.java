/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * 广播接口
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月19日 上午2:39:17
 */
public interface Broadcaster {

    /**
     * 将消息对象广播给所有对等实体
     *
     * @param bean 消息对象
     */
    void notifyAll(Object bean);

    /**
     * 将消息发送给任一对等实体
     *
     * @param bean 消息对象
     */
    void notifyAny(Object bean);

}
