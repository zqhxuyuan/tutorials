/**
 * 
 */
package com.baidu.unbiz.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.baidu.unbiz.common.able.Closure;

/**
 * 并发相关的一些常用工具类
 * 
 * <p>
 * 这个类中的每个方法都可以“安全”地处理 <code>null</code> ，而不会抛出 <code>NullPointerException</code>。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月15日 下午4:07:30
 */
public abstract class ConcurrentUtil {

    private static final int defaultTimeout = 60;

    /**
     * 在1分钟内关闭<code>executorService</code>，如果不能正常结束，将强制关闭。
     * <p>
     * 如果<code>executorService</code>不存在或已关闭，将不做任何处理。
     * 
     * @param executorService ## @see ExecutorService
     */
    public static final void shutdownAndAwaitTermination(ExecutorService executorService) {

        shutdownAndAwaitTermination(executorService, defaultTimeout, TimeUnit.SECONDS);

    }

    /**
     * 如果<code>executorService</code>不存在，也当做“已关闭”对待
     * 
     * @param executorService ## @see ExecutorService
     * @return 如果<code>executorService</code>“已关闭”，则返回<code>true</code>
     */
    public static boolean isShutDown(ExecutorService executorService) {
        return (executorService == null || executorService.isShutdown());
    }

    /**
     * 关闭<code>executorService</code>，如果<code>executorService</code> 不存在或已关闭，将不做任何处理。在限定时间内如无法正常结束，将强制关闭。
     * 
     * @param executorService ## @see ExecutorService
     * @param timeout 限定时间
     * @param timeUnit 单位 @see TimeUnit
     */
    public static final void shutdownAndAwaitTermination(ExecutorService executorService, long timeout,
            TimeUnit timeUnit) {
        if (isShutDown(executorService)) {
            return;
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout, timeUnit)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        executorService.shutdownNow();

    }

    public static void execute(boolean asyn, Runnable runnable) {
        if (asyn) {
            asynExecute(runnable);
            return;
        }

        if (runnable == null) {
            return;
        }
        runnable.run();
    }

    public static void asynExecute(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        new Thread(runnable).start();
    }

    public static void execute(boolean asyn, final Object object, final String methodName, final Object...inputs) {
        if (asyn) {
            asynExecute(object, methodName, inputs);
            return;
        }

        if (object == null || StringUtil.isBlank(methodName)) {
            return;
        }
        doExecute(object, methodName, inputs);
    }

    public static void asynExecute(final Object object, final String methodName, final Object...inputs) {
        if (object == null || StringUtil.isBlank(methodName)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                doExecute(object, methodName, inputs);
            }

        }).start();
    }

    private static void doExecute(final Object object, final String methodName, final Object...inputs) {
        Class<?>[] parameterTypes = null;
        if (ArrayUtil.isEmpty(inputs)) {
            parameterTypes = new Class<?>[0];
        } else {
            int size = inputs.length;
            parameterTypes = new Class<?>[size];
            for (int i = 0; i < size; i++) {
                parameterTypes[i] = inputs[i].getClass();
            }
        }

        ReflectionUtil.invokeMethod(object, methodName, inputs, parameterTypes);
    }

    public static void execute(boolean asyn, final Closure closure, final Object...inputs) {
        if (asyn) {
            asynExecute(closure, inputs);
        }

        if (closure == null) {
            return;
        }
        closure.execute(inputs);
    }

    public static void asynExecute(final Closure closure, final Object...inputs) {
        if (closure == null) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                closure.execute(inputs);
            }

        }).start();
    }

}
