package com.baidu.unbiz.common.diagnostic;

import java.io.File;
import java.net.URL;

import com.baidu.unbiz.common.ClassLoaderUtil;
import com.baidu.unbiz.common.StringUtil;
import com.baidu.unbiz.common.SystemUtil;

/**
 * <code>JWhich</code>是一个类似UNIX的<code>which</code> 命令的小工具，所不同的是，它可以查找指定的java类或资源是从什么地方装载的。
 * 
 * <p>
 * 另外，<code>JWhich</code>还可以检查系统的classpath是否有错，例如有路径不存在。
 * </p>
 * 
 * <p>
 * 该工具可在命令行上直接运行。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午2:47:39
 */
public class JWhich {
    private static final String[] CLASSPATH = SystemUtil.getJavaRuntimeInfo().getClassPathArray();

    /**
     * 在当前线程的context class loader下查找指定的类或资源，并全部列出来。
     * 
     * @param classOrResourceName 类名或资源名
     */
    public static void which(String classOrResourceName) {
        URL[] classURLs = ClassLoaderUtil.whichClasses(classOrResourceName);
        URL[] resourceURLs = ClassLoaderUtil.getResources(classOrResourceName);

        if ((classURLs.length == 0) && (resourceURLs.length == 0)) {
            System.out.println("\nClass or resource '" + classOrResourceName + "' not found.");
        } else {
            if (classURLs.length > 0) {
                System.out.println("\nClass '" + classOrResourceName + "' found in: \n");

                for (int i = 0; i < classURLs.length; i++) {
                    System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + classURLs[i]);
                }
            }

            if (resourceURLs.length > 0) {
                System.out.println("\nResource '" + classOrResourceName + "' found in: \n");

                for (int i = 0; i < resourceURLs.length; i++) {
                    System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + resourceURLs[i]);
                }
            }
        }
    }

    /**
     * 打印系统classpath。
     */
    public static void printClasspath() {
        System.out.println("\nSystem Classpath:");

        for (int i = 0; i < CLASSPATH.length; i++) {
            String path = CLASSPATH[i];
            File file = new File(path);
            String errmsg = null;

            if (!file.exists()) {
                errmsg = "classpath element does not exist.";
            } else if ((!file.isDirectory()) && (!path.toLowerCase().endsWith(".jar"))
                    && (!path.toLowerCase().endsWith(".zip"))) {
                errmsg = "classpath element is not a directory, .jar file, or .zip file.";
            }

            if (errmsg == null) {
                System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + path);
            } else {
                System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + path);
                System.out.println("wrong! " + errmsg);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("\nUsage:");
            System.out.println("    java " + JWhich.class.getName() + " MyClass");
            System.out.println("    java " + JWhich.class.getName() + " my.package.MyClass");
            System.out.println("    java " + JWhich.class.getName() + " META-INF/MANIFEST.MF");
            System.exit(-1);
        }

        for (int i = 0; i < args.length; i++) {
            which(args[i]);
        }

        printClasspath();
    }
}
