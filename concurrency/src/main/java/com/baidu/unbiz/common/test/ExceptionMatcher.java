/**
 * 
 */
package com.baidu.unbiz.common.test;

import static org.hamcrest.Matchers.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * 用来检查一个异常的类型和message内容。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午4:35:47
 * @param <T>
 */
public class ExceptionMatcher<T extends Throwable> extends BaseMatcher<T> {
    private final Matcher<?> exceptionMatcher;
    private final Matcher<?> causeExceptionMatcher;
    private final Matcher<?> messageMatcher;
    private final Class<? extends Throwable> cause;

    public ExceptionMatcher(String...snippets) {
        this(null, snippets);
    }

    public ExceptionMatcher(Class<? extends Throwable> cause, String...snippets) {
        // exception matcher
        List<Matcher<?>> matchers = new LinkedList<Matcher<?>>();

        matchers.add(notNullValue());
        matchers.add(instanceOf(Throwable.class));

        exceptionMatcher = allOf(matchers);

        // cause exception matcher
        if (cause != null) {
            matchers = new LinkedList<Matcher<?>>();

            matchers.add(notNullValue());
            matchers.add(instanceOf(cause));

            causeExceptionMatcher = allOf(matchers);
        } else {
            causeExceptionMatcher = null;
        }

        this.cause = cause;

        // message exception matcher
        if (snippets != null && snippets.length > 0) {
            matchers = new LinkedList<Matcher<?>>();

            for (String snippet : snippets) {
                matchers.add(containsString(snippet));
            }

            messageMatcher = allOf(matchers);
        } else {
            messageMatcher = null;
        }
    }

    public boolean matches(Object item) {
        if (!exceptionMatcher.matches(item)) {
            return false;
        }
        Throwable top = (Throwable) item;
        Throwable t = top;

        if (causeExceptionMatcher != null) {
            Set<Throwable> visited = new HashSet<Throwable>();

            for (; t != null && !cause.isInstance(t) && !visited.contains(t); t = t.getCause()) {
                visited.add(t);
            }

            if (!causeExceptionMatcher.matches(t)) {
                return false;
            }
        }

        if (messageMatcher == null) {
            return true;
        }
        String message = t.getMessage();

        if (t != top) {
            message += "\n" + top.getMessage();
        }

        return messageMatcher.matches(message);
    }

    public void describeTo(Description description) {
        description.appendText("An exception that is ").appendDescriptionOf(exceptionMatcher);

        if (causeExceptionMatcher != null) {
            description.appendText("\n  and its cause exception is ").appendDescriptionOf(causeExceptionMatcher);
        }

        if (messageMatcher != null) {
            description.appendText("\n  and its message is ").appendDescriptionOf(messageMatcher);
        }
    }
}
