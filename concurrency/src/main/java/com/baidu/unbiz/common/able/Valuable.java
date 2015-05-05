/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * 定义返回值接口，一般用于不能继承的对象，如枚举
 *
 * @param <T>
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月17日 下午5:31:06
 */
public interface Valuable<T> {
    T value();
}
