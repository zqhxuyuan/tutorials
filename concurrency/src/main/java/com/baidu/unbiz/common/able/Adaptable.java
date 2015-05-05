/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * 适配器接口
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 上午2:41:25
 */
public interface Adaptable<O, N> {

    N forNew(O old);

}
