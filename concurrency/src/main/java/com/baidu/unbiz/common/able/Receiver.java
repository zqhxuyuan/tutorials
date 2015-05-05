package com.baidu.unbiz.common.able;

/**
 * 消息接收
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午6:47:15
 */
public interface Receiver<T> {

    /**
     * 接受消息，一般用于异步或远程通信
     *
     * @param msg 接收信息附带对象
     */
    void messageReceived(T msg);
}
