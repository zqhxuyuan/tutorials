/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月6日 下午10:18:32
 */
public interface Processor<P, R> {

    R processAndGet(P processing);

}
