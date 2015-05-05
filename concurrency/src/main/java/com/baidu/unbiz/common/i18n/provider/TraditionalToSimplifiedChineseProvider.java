/**
 * 
 */
package com.baidu.unbiz.common.i18n.provider;

/**
 * 繁体转简体
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月24日 上午3:49:10
 */
public class TraditionalToSimplifiedChineseProvider extends ChineseCharConverterProvider {
    @Override
    protected String getCodeTableName() {
        return "TraditionalToSimplifiedChinese";
    }
}
