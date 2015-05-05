/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * 消息发送
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午2:30:44
 */
public interface Sender<T> {

    /**
     * 发送消息，一般用于异步或远程通信
     *
     * @param msg 发送信息
     */
    void sendMessage(T msg);

}
