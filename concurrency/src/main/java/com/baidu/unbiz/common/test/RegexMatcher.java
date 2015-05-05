/**
 * 
 */
package com.baidu.unbiz.common.test;

import java.util.regex.Pattern;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * 正则匹配
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午4:36:54
 */
public class RegexMatcher extends BaseMatcher<String> {
    private Pattern regex;

    public RegexMatcher(String regex) {
        this.regex = Pattern.compile(regex);
    }

    public boolean matches(Object item) {
        String s = (String) item;

        return regex.matcher(s).find();
    }

    public void describeTo(Description description) {
        description.appendText("A string matches regex ").appendText(regex.toString());
    }
}
