/**
 * 
 */
package com.baidu.unbiz.common.lang;

import com.baidu.unbiz.common.able.Adaptable;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 上午12:22:19
 */
public class AdapterFactory<O, N> {

    public static <O, N> N create(Adaptable<O, N> adaptable, O old) {
        return adaptable.forNew(old);
    }

}
