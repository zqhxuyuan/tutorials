package com.baidu.unbiz.common.able;

/**
 * 定义回调响应
 *
 * @author <a href="mailto:xuc@iphonele.com">saga67</a>
 * @version create on 2012-3-16 下午1:23:12
 */
public interface ResponseClosure<T> {

    /**
     * 一般用于异步或远程通信
     *
     * @param resp 附带对象
     */
    void onResponse(T resp);
}
