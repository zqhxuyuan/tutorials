/**
 * 
 */
package com.baidu.unbiz.common;

import static com.baidu.unbiz.common.test.TestUtil.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.baidu.unbiz.common.Assert;
import com.baidu.unbiz.common.exception.UnexpectedFailureException;
import com.baidu.unbiz.common.exception.UnreachableCodeException;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午4:38:57
 */
public class AssertTest {
    private static final String DEFAULT_HEADER = "[Assertion";
    private static final String MESSAGE = "test message %d";
    private static final String MESSAGE_1 = "test message 1";
    private static final Object OBJECT = new Object();

    @Test
    public void assertNotNull() {
        // form 1
        Assert.assertNotNull(OBJECT);

        try {
            Assert.assertNotNull(null);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, DEFAULT_HEADER, "must not be null"));
        }

        // form 2
        Assert.assertNotNull(OBJECT, MESSAGE);

        try {
            Assert.assertNotNull(null, MESSAGE);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, MESSAGE));
        }

        try {
            Assert.assertNotNull(null, MESSAGE, 1);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, MESSAGE_1));
        }

        // form 3
        Assert.assertNotNull(OBJECT, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE);

        try {
            Assert.assertNotNull(null, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE);
        } catch (IllegalStateException e) {
            assertThat(e, exception(IllegalStateException.class, MESSAGE));
        }

        try {
            Assert.assertNotNull(null, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE, 1);
        } catch (IllegalStateException e) {
            assertThat(e, exception(IllegalStateException.class, MESSAGE_1));
        }
    }

    @Test
    public void assertNull() {
        // form 1
        Assert.assertNull(null);

        try {
            Assert.assertNull(OBJECT);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, DEFAULT_HEADER, "must be null"));
        }

        // form 2
        Assert.assertNull(null, MESSAGE);

        try {
            Assert.assertNull(OBJECT, MESSAGE);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, MESSAGE));
        }

        try {
            Assert.assertNull(OBJECT, MESSAGE, 1);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, MESSAGE_1));
        }

        // form 3
        Assert.assertNull(null, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE);

        try {
            Assert.assertNull(OBJECT, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE);
        } catch (IllegalStateException e) {
            assertThat(e, exception(IllegalStateException.class, MESSAGE));
        }

        try {
            Assert.assertNull(OBJECT, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE, 1);
        } catch (IllegalStateException e) {
            assertThat(e, exception(IllegalStateException.class, MESSAGE_1));
        }
    }

    @Test
    public void assertTrue() {
        // form 1
        Assert.assertTrue(true);

        try {
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, DEFAULT_HEADER, "must be true"));
        }

        // form 2
        Assert.assertTrue(true, MESSAGE);

        try {
            Assert.assertTrue(false, MESSAGE);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, MESSAGE));
        }

        try {
            Assert.assertTrue(false, MESSAGE, 1);
        } catch (IllegalArgumentException e) {
            assertThat(e, exception(IllegalArgumentException.class, MESSAGE_1));
        }

        // form 3
        Assert.assertTrue(true, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE);

        try {
            Assert.assertTrue(false, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE);
        } catch (IllegalStateException e) {
            assertThat(e, exception(IllegalStateException.class, MESSAGE));
        }

        try {
            Assert.assertTrue(false, Assert.ExceptionType.ILLEGAL_STATE, MESSAGE, 1);
        } catch (IllegalStateException e) {
            assertThat(e, exception(IllegalStateException.class, MESSAGE_1));
        }
    }

    @Test
    public void unreachableCode() {
        try {
            Assert.unreachableCode();
        } catch (UnreachableCodeException e) {
            assertThat(e, exception(UnreachableCodeException.class, DEFAULT_HEADER, "unreachable"));
        }

        try {
            Assert.unreachableCode(MESSAGE);
        } catch (UnreachableCodeException e) {
            assertThat(e, exception(UnreachableCodeException.class, MESSAGE));
        }

        try {
            Assert.unreachableCode(MESSAGE, 1);
        } catch (UnreachableCodeException e) {
            assertThat(e, exception(UnreachableCodeException.class, MESSAGE_1));
        }
    }

    @Test
    public void unexpectedException() {
        final Throwable e = new Throwable();

        try {
            Assert.unexpectedException(e);
        } catch (UnexpectedFailureException ee) {
            assertThat(ee, exception(UnexpectedFailureException.class, DEFAULT_HEADER, "unexpected"));
        }

        try {
            Assert.unexpectedException(e, MESSAGE);
        } catch (UnexpectedFailureException ee) {
            assertThat(ee, exception(UnexpectedFailureException.class, MESSAGE));
        }

        try {
            Assert.unexpectedException(e, MESSAGE, 1);
        } catch (UnexpectedFailureException ee) {
            assertThat(ee, exception(UnexpectedFailureException.class, MESSAGE_1));
        }

        try {
            Assert.unexpectedException(e);
            fail();
        } catch (UnexpectedFailureException ee) {
            assertSame(e, ee.getCause());
        }

        try {
            Assert.unexpectedException(e, MESSAGE);
            fail();
        } catch (UnexpectedFailureException ee) {
            assertSame(e, ee.getCause());
        }
    }

    @Test
    public void fail() {
        try {
            Assert.fail();
        } catch (UnexpectedFailureException e) {
            assertThat(e, exception(UnexpectedFailureException.class, DEFAULT_HEADER, "unexpected"));
        }

        try {
            Assert.fail(MESSAGE);
        } catch (UnexpectedFailureException e) {
            assertThat(e, exception(UnexpectedFailureException.class, MESSAGE));
        }

        try {
            Assert.fail(MESSAGE, 1);
        } catch (UnexpectedFailureException e) {
            assertThat(e, exception(UnexpectedFailureException.class, MESSAGE_1));
        }
    }

    @Test
    public void unsupportedOperation() {
        try {
            Assert.unsupportedOperation();
        } catch (UnsupportedOperationException e) {
            assertThat(e, exception(UnsupportedOperationException.class, DEFAULT_HEADER, "unsupported"));
        }

        try {
            Assert.unsupportedOperation(MESSAGE);
        } catch (UnsupportedOperationException e) {
            assertThat(e, exception(UnsupportedOperationException.class, MESSAGE));
        }

        try {
            Assert.unsupportedOperation(MESSAGE, 1);
        } catch (UnsupportedOperationException e) {
            assertThat(e, exception(UnsupportedOperationException.class, MESSAGE_1));
        }
    }
}
