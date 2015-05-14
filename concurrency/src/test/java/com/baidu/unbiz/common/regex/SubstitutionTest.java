/**
 * 
 */
package com.baidu.unbiz.common.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.test.TestUtil;

public class SubstitutionTest {
    private MatchResult r1;
    private MatchResult r2;

    @Before
    public void init() {
        r1 = getMatchResult("(aa)bb", "aabb");
        r2 = getMatchResult("(cc)dd", "ccdd");
    }

    private MatchResult getMatchResult(String pattern, String input) {
        Matcher matcher = Pattern.compile(pattern).matcher(input);

        assertTrue(matcher.find());

        return matcher.toMatchResult();
    }

    @Test
    public void emptyResult() {
        assertEquals(0, MatchResultSubstitution.EMPTY_MATCH_RESULT.groupCount());
        assertEquals("", MatchResultSubstitution.EMPTY_MATCH_RESULT.group(0));
    }

    @Test
    public void illegal_replacementPrefixes() {
        try {
            new MatchResultSubstitution("");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("replacementPrefixes"));
        }
    }

    @Test
    public void illegal_results() {
        try {
            new MatchResultSubstitution("$");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("results"));
        }
    }

    @Test
    public void illegal_results_count_not_match() {
        try {
            new MatchResultSubstitution("$%", r1);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("expected 2 MatchResults"));
        }
    }

    @Test
    public void subst_null() {
        Substitution subs = new MatchResultSubstitution(r1);
        assertEquals(null, subs.substitute(null));
    }

    @Test
    public void setMatchResult() {
        MatchResultSubstitution subs = new MatchResultSubstitution();
        subs.setMatchResult(r1);

        assertEquals("xxaayy$2zz", subs.substitute("xx$1yy$2zz"));

        try {
            subs.setMatchResults(r1, r2);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("expected 1 MatchResults"));
        }
    }

    @Test
    public void matchNormal() {
        Substitution subs = new MatchResultSubstitution(r1);
        assertEquals("xxaayy$2zz", subs.substitute("xx$1yy$2zz"));
    }

    @Test
    public void matchOverflowNumber() {
        Substitution subs = new MatchResultSubstitution(r1);
        // Number $12345678901234567890 exceeds the integer range, which causes
        // a negative number
        assertEquals("xx$12345678901234567890yy$2zz", subs.substitute("xx$12345678901234567890yy$2zz"));
    }

    @Test
    public void matchMulti() {
        Substitution subs = new MatchResultSubstitution("$%", r1, r2);
        assertEquals("xxaayycczz$2%2", subs.substitute("xx$1yy%1zz$2%2"));

        r1 = getMatchResult("\\.(\\w+)\\.com/(.*)", "www.saga67.com/test.htm");
        r2 = getMatchResult("a=(\\d+)&b=(\\d+)", "a=1&b=2&c=3");

        assertEquals("\\saga67, $1, $x, .saga67.com/test.htm, saga67, test.htm, $3, %1, %x, a=1&b=2, 1, 2, %3, \\",
                new MatchResultSubstitution("$%", r1, r2)
                        .substitute("\\\\$1, \\$1, $x, $0, $1, $2, $3, \\%1, %x, %0, %1, %2, %3, \\"));
    }

}
