/**
 * 
 */
package com.baidu.unbiz.common.access.strategy;

import java.io.File;

import com.baidu.unbiz.common.access.AccessStrategy;
import com.baidu.unbiz.common.collection.Stack;

/**
 * 将id反复除以<code>base</code>(默认1000)，直到商为0
 * 
 * <pre>
 * 设id为123456789，则其对应的路径为：
 * 123/123456/123456789；
 * 设id为1234567890，则其对应的路径为：
 * 1/1234/1234567/1234567890；
 * </pre>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午4:40:26
 */
public class DivideThousand implements AccessStrategy {

    private static final int BASE = 1000;

    private int base = BASE;

    public DivideThousand() {

    }

    public DivideThousand(int base) {
        this.base = base;
    }

    @Override
    public String find(long id) {
        Stack<Long> stack = new Stack<Long>();

        for (; id * base >= base; id = id / base) {
            stack.push(id);
        }

        StringBuilder builder = new StringBuilder();

        for (; !stack.isEmpty();) {
            builder.append(stack.pop()).append(File.separator);
        }

        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    public void setBase(int base) {
        this.base = base;
    }
}
