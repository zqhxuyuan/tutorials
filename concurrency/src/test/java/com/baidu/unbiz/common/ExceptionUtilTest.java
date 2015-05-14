/**
 * 
 */
package com.baidu.unbiz.common;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.baidu.unbiz.common.ExceptionUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午5:07:11
 */
public class ExceptionUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void causedBy_causeTypeIsNull() {
        ExceptionUtil.causedBy(new Exception(), null);
    }

    @Test
    public void causedBy() {
        // null
        assertFalse(ExceptionUtil.causedBy(null, Exception.class));

        // 1 exception
        assertTrue(ExceptionUtil.causedBy(new IOException(), IOException.class));
        assertTrue(ExceptionUtil.causedBy(new IOException(), Exception.class));

        // 3 exceptions
        assertTrue(ExceptionUtil.causedBy(getException(), IOException.class));
        assertTrue(ExceptionUtil.causedBy(getException(), IllegalArgumentException.class));
        assertTrue(ExceptionUtil.causedBy(getException(), IllegalStateException.class));
        assertTrue(ExceptionUtil.causedBy(getException(), RuntimeException.class));
        assertTrue(ExceptionUtil.causedBy(getException(), Exception.class));

        assertFalse(ExceptionUtil.causedBy(getException(), IllegalAccessException.class));
    }

    @Test
    public void getRootCause() {
        // null
        assertNull(ExceptionUtil.getRootCause(null));

        // 1 exception
        Throwable t = new IOException();
        assertSame(t, ExceptionUtil.getRootCause(t));

        // 3 exceptions
        assertThat(ExceptionUtil.getRootCause(getException()), instanceOf(IllegalStateException.class));
    }

    @Test
    public void getCauses() {
        // null
        assertTrue(ExceptionUtil.getCauses(null).isEmpty());

        // 1 exception
        List<Throwable> es = ExceptionUtil.getCauses(new IOException());

        assertEquals(1, es.size());

        Iterator<Throwable> i = es.iterator();

        assertThat(i.next(), instanceOf(IOException.class));

        // 3 exceptions
        es = ExceptionUtil.getCauses(getException());

        assertEquals(3, es.size());

        i = es.iterator();

        assertThat(i.next(), instanceOf(IOException.class));
        assertThat(i.next(), instanceOf(IllegalArgumentException.class));
        assertThat(i.next(), instanceOf(IllegalStateException.class));

        // 3 exceptions
        es = ExceptionUtil.getCauses(getException(), false);

        assertEquals(3, es.size());

        i = es.iterator();

        assertThat(i.next(), instanceOf(IOException.class));
        assertThat(i.next(), instanceOf(IllegalArgumentException.class));
        assertThat(i.next(), instanceOf(IllegalStateException.class));
    }

    @Test
    public void getCauses_reversed() {
        // 3 exceptions, reversed
        List<Throwable> es = ExceptionUtil.getCauses(getException(), true);

        assertEquals(3, es.size());

        Iterator<Throwable> i = es.iterator();

        assertThat(i.next(), instanceOf(IllegalStateException.class));
        assertThat(i.next(), instanceOf(IllegalArgumentException.class));
        assertThat(i.next(), instanceOf(IOException.class));
    }

    private IOException getException() {
        IllegalStateException e1 = new IllegalStateException();
        IllegalArgumentException e2 = new IllegalArgumentException();
        IOException e3 = new IOException();

        e1.initCause(e3);
        e2.initCause(e1);
        e3.initCause(e2);

        return e3;
    }

    @Test
    public void toRuntimeException() {
        // null
        assertNull(ExceptionUtil.toRuntimeException(null));

        // wrong class
        assertEquals(RuntimeException.class,
                ExceptionUtil.toRuntimeException(new Exception(), PrivateRuntimeException.class).getClass());

        IllegalArgumentException iae = new IllegalArgumentException();
        IOException ioe = new IOException();

        assertSame(iae, ExceptionUtil.toRuntimeException(iae));
        assertTrue(ExceptionUtil.toRuntimeException(ioe) instanceof RuntimeException);
        assertSame(ioe, ExceptionUtil.toRuntimeException(ioe).getCause());
    }

    private class PrivateRuntimeException extends RuntimeException {
        private static final long serialVersionUID = -7903623389794106652L;

        private PrivateRuntimeException() {
        }
    }

    @Test
    public void toRuntimeException2() {
        IllegalArgumentException iae = new IllegalArgumentException();
        IOException ioe = new IOException();

        assertSame(iae, ExceptionUtil.toRuntimeException(iae, IllegalStateException.class));
        assertTrue(ExceptionUtil.toRuntimeException(ioe, IllegalStateException.class) instanceof IllegalStateException);
        assertSame(ioe, ExceptionUtil.toRuntimeException(ioe, IllegalStateException.class).getCause());
    }

    @Test
    public void throwExceptionOrError() {
        try {
            ExceptionUtil.throwExceptionOrError(new Exception("test"));
            fail();
        } catch (Exception e) {
            assertEquals("test", e.getMessage());
        }

        try {
            ExceptionUtil.throwExceptionOrError(new RuntimeException("test"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("test", e.getMessage());
        } catch (Exception e) {
            fail();
        }

        try {
            ExceptionUtil.throwExceptionOrError(new Error("test"));
            fail();
        } catch (Error e) {
            assertEquals("test", e.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void throwRuntimeExceptionOrError() {
        try {
            ExceptionUtil.throwRuntimeExceptionOrError(new Exception("test"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("test", e.getCause().getMessage());
        }

        try {
            ExceptionUtil.throwRuntimeExceptionOrError(new RuntimeException("test"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("test", e.getMessage());
        }

        try {
            ExceptionUtil.throwRuntimeExceptionOrError(new Error("test"));
            fail();
        } catch (Error e) {
            assertEquals("test", e.getMessage());
        }
    }

    @Test
    public void getStackTrace() {
        Throwable e = new Throwable();
        String stacktrace = ExceptionUtil.getStackTrace(e);

        assertTrue(stacktrace.contains(Throwable.class.getName()));
        assertTrue(stacktrace.contains(ExceptionUtilTest.class.getName() + ".getStackTrace"));
    }

}
