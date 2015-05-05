package com.baidu.unbiz.common.able;

/**
 * 将一个对象转换成另一个对象的接口.
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 上午1:22:18
 */
public interface Transformer<FROM, TO> {
    TO transform(FROM from);
}
