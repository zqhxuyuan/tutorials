package com.baidu.unbiz.common.able;

import java.util.concurrent.TimeUnit;

/**
 * 通信接口一般用于异步或远程通信，不具备广播特性，为单点通信，故具备同步等待对等实体返回的可能
 * <p/>
 * 若需要广播特性，可以实现<code>Broadcaster</code>接口。
 *
 * @author <a href="mailto:xuc@iphonele.com">saga67</a>
 * @version create on 2012-3-16 下午1:22:46
 */
public interface Communicable {

    /**
     * 发送接口，一般用于测试对等实体
     */
    void send();

    /**
     * 发送信息给对等实体
     *
     * @param bean 消息对象
     */
    void send(Object bean);

    /**
     * 发送信息集给对等实体
     *
     * @param beans 消息对象集
     */
    void send(Object... beans);

    /**
     * 发送后执行异步回调,一般用于测试对等实体
     *
     * @param callback 回调接口
     */
    <T> void send(ResponseClosure<T> callback);

    /**
     * 发送消息实体后执行异步回调
     *
     * @param object   消息对象
     * @param callback 回调接口
     */
    <T> void send(Object object, ResponseClosure<T> callback);

    /**
     * 发送消息实体集后执行异步回调
     *
     * @param callback 消息对象
     * @param objects  回调接口
     */
    <T> void send(ResponseClosure<T> callback, Object... objects);

    /**
     * 发送后等待响应，一般用于测试对等实体，需有默认超时时间
     *
     * @return 对等实体返回结果
     */
    <T> T sendAndWait();

    /**
     * 发送消息实体后等待响应，需有默认超时时间
     *
     * @param bean 消息对象
     *
     * @return 对等实体返回结果
     */
    <T> T sendAndWait(Object bean);

    /**
     * 发送消息实体集后等待响应，需有默认超时时间
     *
     * @param beans 消息对象集
     *
     * @return 对等实体返回结果
     */
    <T> T sendAndWait(Object... beans);

    /**
     * 发送后等待响应，一般用于测试对等实体
     *
     * @param duration 等待时间
     * @param unit     时间单位
     *
     * @return 对等实体返回结果
     */
    <T> T sendAndWait(long duration, TimeUnit unit);

    /**
     * 发送消息实体后等待响应
     *
     * @param bean     消息对象
     * @param duration 等待时间
     * @param unit     时间单位
     *
     * @return 对等实体返回结果
     */
    <T> T sendAndWait(Object bean, long duration, TimeUnit unit);

    /**
     * 发送消息实体集后等待响应
     *
     * @param beans    消息对象集
     * @param duration 等待时间
     * @param unit     时间单位
     *
     * @return 对等实体返回结果
     */
    <T> T sendAndWait(Object[] beans, long duration, TimeUnit unit);
}
