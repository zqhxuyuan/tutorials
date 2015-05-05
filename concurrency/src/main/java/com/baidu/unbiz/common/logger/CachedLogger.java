/**
 * 
 */
package com.baidu.unbiz.common.logger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月15日 下午7:14:16
 */
public class CachedLogger implements LoggerProvider {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Logger getLogger() {
        return logger;
    }

}
