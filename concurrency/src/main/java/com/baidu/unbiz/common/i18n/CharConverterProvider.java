/**
 * 
 */
package com.baidu.unbiz.common.i18n;

/**
 * 代表一个<code>CharConverter</code>方案的提供者。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月24日 上午3:43:02
 */
public interface CharConverterProvider {
    /** 创建一个新的converter。 */
    CharConverter createCharConverter();
}
