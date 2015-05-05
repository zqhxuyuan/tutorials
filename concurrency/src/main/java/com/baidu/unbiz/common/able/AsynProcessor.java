/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 上午12:13:45
 */
public interface AsynProcessor<P, R> {

    void process(P processing);

    R get();

}
