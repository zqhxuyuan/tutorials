/**
 * 
 */
package com.baidu.unbiz.common;

import static com.baidu.unbiz.common.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 处理异常的工具类。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午2:24:07
 */
public abstract class ExceptionUtil {

    /**
     * 检查异常是否由指定类型的异常引起。
     * 
     * @param throwable 受检异常
     * @param causeType 指定类型
     * @return 如果是则返还<code>true</code>
     */
    public static boolean causedBy(Throwable throwable, Class<? extends Throwable> causeType) {
        assertNotNull(causeType, "causeType");

        Set<Throwable> causes = CollectionUtil.createHashSet();

        for (; throwable != null && !causeType.isInstance(throwable) && !causes.contains(throwable); throwable =
                throwable.getCause()) {
            causes.add(throwable);
        }

        return throwable != null && causeType.isInstance(throwable);
    }

    /**
     * 取得最根本的异常。
     * 
     * @param throwable 受检异常
     * @return 最根本的异常。
     */
    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> causes = getCauses(throwable, true);

        return causes.isEmpty() ? null : causes.get(0);
    }

    /**
     * 取得包括当前异常在内的所有的causes异常，按出现的顺序排列。
     * 
     * @param throwable 受检异常
     * @return 包括当前异常在内的所有的causes异常，按出现的顺序排列。
     */
    public static List<Throwable> getCauses(Throwable throwable) {
        return getCauses(throwable, false);
    }

    /**
     * 取得包括当前异常在内的所有的causes异常，按出现的顺序排列
     * 
     * @param throwable 受检异常
     * @param reversed 是否反向
     * @return 包括当前异常在内的所有的causes异常，按出现的顺序排列。
     */
    public static List<Throwable> getCauses(Throwable throwable, boolean reversed) {
        LinkedList<Throwable> causes = CollectionUtil.createLinkedList();

        for (; throwable != null && !causes.contains(throwable); throwable = throwable.getCause()) {
            if (reversed) {
                causes.addFirst(throwable);
            } else {
                causes.addLast(throwable);
            }
        }

        return causes;
    }

    /**
     * 将异常转换成<code>RuntimeException</code>。
     * 
     * @param exception 受检异常
     * @return to <code>RuntimeException</code
     */
    public static RuntimeException toRuntimeException(Exception exception) {
        return toRuntimeException(exception, null);
    }

    /**
     * 将异常转换成<code>RuntimeException</code>。
     * 
     * @param exception 受检异常
     * @param runtimeExceptionClass 转换异常的类型
     * @return to <code>RuntimeException</code
     */
    public static RuntimeException toRuntimeException(Exception exception,
            Class<? extends RuntimeException> runtimeExceptionClass) {
        if (exception == null) {
            return null;
        }

        if (exception instanceof RuntimeException) {
            return (RuntimeException) exception;
        }
        if (runtimeExceptionClass == null) {
            return new RuntimeException(exception);
        }

        RuntimeException runtimeException;

        try {
            runtimeException = runtimeExceptionClass.newInstance();
        } catch (Exception ee) {
            return new RuntimeException(exception);
        }

        runtimeException.initCause(exception);
        return runtimeException;

    }

    /**
     * 抛出Throwable，但不需要声明<code>throws Throwable</code>，区分<code>Exception</code> 或</code>Error</code>。
     * 
     * @param throwable 受检异常
     * @throws Exception
     */
    public static void throwExceptionOrError(Throwable throwable) throws Exception {
        if (throwable instanceof Exception) {
            throw (Exception) throwable;
        }

        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        throw new RuntimeException(throwable); // unreachable code
    }

    /**
     * 抛出Throwable，但不需要声明<code>throws Throwable</code>，区分 <code>RuntimeException</code>、<code>Exception</code>
     * 或</code>Error</code>。
     * 
     * @param throwable 受检异常
     */
    public static void throwRuntimeExceptionOrError(Throwable throwable) {
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }

        throw new RuntimeException(throwable);
    }

    /**
     * 取得异常的stacktrace字符串。
     * 
     * @param throwable 受检异常
     * @return stacktrace字符串
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);

        throwable.printStackTrace(out);
        out.flush();

        return buffer.toString();
    }

}
