/**
 * 
 */
package com.baidu.unbiz.common.i18n.provider;

/**
 * 简体转繁体
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月24日 上午3:48:49
 */
public class SimplifiedToTraditionalChineseProvider extends ChineseCharConverterProvider {
    @Override
    protected String getCodeTableName() {
        return "SimplifiedToTraditionalChinese";
    }
}
