/**
 * 
 */
package com.baidu.unbiz.common.regex;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.unbiz.common.ArrayUtil;
import com.baidu.unbiz.common.Assert;
import com.baidu.unbiz.common.apache.ToStringBuilder;
import com.baidu.unbiz.common.apache.ToStringStyle;

/**
 * 用<code>MatchResult</code>来替换字符串中的变量。通常变量以<code>'$'</code>开始，例如： <code>$1</code>，<code>$2</code>等，但
 * <code>MatchResultSubstitution</code>类支持对多个 <code>MatchResult</code>变量进行替换，分别对应不同的前缀。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 上午2:35:19
 */
public class MatchResultSubstitution extends Substitution {
    /** 代表一个成功但无内容的匹配结果。 */
    public static final MatchResult EMPTY_MATCH_RESULT = createEmptyMatchResult();

    private final MatchResult[] results;

    private static MatchResult createEmptyMatchResult() {
        Matcher matcher = Pattern.compile("^$").matcher("");

        Assert.assertTrue(matcher.find());

        return matcher.toMatchResult();
    }

    /** 创建一个替换。替换所有<code>$num</code>所代表的变量。 */
    public MatchResultSubstitution() {
        this("$", EMPTY_MATCH_RESULT);
    }

    /** 创建一个替换。替换所有<code>$num</code>所代表的变量。 */
    public MatchResultSubstitution(MatchResult result) {
        this("$", result);
    }

    /** 设置新匹配。 */
    public void setMatchResult(MatchResult result) {
        if (results.length != 1) {
            new IllegalArgumentException("expected " + this.results.length + " MatchResults");
        }

        results[0] = result;
    }

    /** 设置新匹配。 */
    public void setMatchResults(MatchResult...results) {
        Assert.assertTrue(ArrayUtil.isNotEmpty(results), "results");

        if (this.results.length != results.length) {
            throw new IllegalArgumentException("expected " + this.results.length + " MatchResults");
        }

        for (int i = 0; i < results.length; i++) {
            this.results[i] = results[i];
        }
    }

    /** 创建一个替换。将所有指定前缀所代表的变量替换成相应<code>MatchResult.group(num)</code>的值。 */
    public MatchResultSubstitution(String replacementPrefixes, MatchResult...results) {
        super(replacementPrefixes);
        this.results = new MatchResult[this.replacementPrefixes.length()];

        setMatchResults(results);
    }

    /** 取得匹配。 */
    public MatchResult getMatch() {
        return getMatch(0);
    }

    /** 取得匹配。 */
    public MatchResult getMatch(int index) {
        return results[index];
    }

    @Override
    protected String group(int index, int groupNumber) {
        MatchResult result = getMatch(index);

        if (0 <= groupNumber && groupNumber <= result.groupCount()) {
            return result.group(groupNumber);
        }

        return null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
