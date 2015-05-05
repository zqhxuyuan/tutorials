/**
 *
 */
package com.baidu.unbiz.common;

import java.security.ProtectionDomain;

/**
 * 用来获取当前运行信息的快照
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月20日 上午6:01:26
 */
public abstract class RuntimeUtil {

    /**
     * 获取当前执行的类和方法名
     * 
     * @return 当前执行的类和方法名
     */
    public static String currentClassMethod() {
        StackTraceElement[] ste = new Exception().getStackTrace();
        int ndx = (ste.length > 1) ? 1 : 0;
        StackTraceElement element = new Exception().getStackTrace()[ndx];

        return element.getClassName() + "." + element.getMethodName();
    }

    /**
     * 获取当前执行的方法名
     * 
     * @return 当前执行的方法名
     */
    public static String currentMethodName() {
        StackTraceElement[] ste = new Exception().getStackTrace();
        int ndx = (ste.length > 1) ? 1 : 0;
        return new Exception().getStackTrace()[ndx].getMethodName();
    }

    /**
     * 获取当前执行的类名
     * 
     * @return 当前执行的类名
     */
    public static String currentClassName() {
        StackTraceElement[] ste = new Exception().getStackTrace();
        int ndx = (ste.length > 1) ? 1 : 0;
        return new Exception().getStackTrace()[ndx].getClassName();
    }

    /**
     * 获取当前的命名空间
     * 
     * @return 当前的命名空间
     */
    public static String currentNamespace() {
        StackTraceElement[] ste = new Exception().getStackTrace();
        int ndx = (ste.length > 1) ? 1 : 0;
        StackTraceElement current = new Exception().getStackTrace()[ndx];
        return current.getClassName() + "." + current.getMethodName();
    }

    /**
     * 获取<code>clazz</code>的所在路径
     * 
     * @param clazz 要获取的类
     * @return <code>clazz</code>的所在路径
     */
    public static String classLocation(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        ProtectionDomain domain = clazz.getProtectionDomain();

        return (domain.getClassLoader() != null) ? domain.getCodeSource().getLocation().getPath() : clazz.getName();
    }

    /**
     * 获取当前类的路径
     * 
     * @return 当前类的路径
     */
    public static String classLocation() {
        return classLocation(RuntimeUtil.class);
    }

}
