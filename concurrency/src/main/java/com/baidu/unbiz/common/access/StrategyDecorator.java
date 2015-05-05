/**
 * 
 */
package com.baidu.unbiz.common.access;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月7日 上午2:05:07
 */
public class StrategyDecorator implements AccessStrategy {

    private AccessStrategy strategy;

    public StrategyDecorator(AccessStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String find(long id) {
        return strategy.find(id);
    }

}
