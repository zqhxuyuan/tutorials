/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 上午1:10:06
 */
public interface ClosureResult<T> extends Closure {

    T getResult();

}
