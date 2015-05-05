/**
 *
 */
package com.baidu.unbiz.common.able;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午8:16:07
 */
public interface Processable extends Closure {

    /**
     * info about execute results
     *
     * @return execute results
     */
    String getInfo();

    public void execute(String... input);

    public void execute(String input);

}
