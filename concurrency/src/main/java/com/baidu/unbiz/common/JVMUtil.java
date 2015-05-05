/**
 * 
 */
package com.baidu.unbiz.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.baidu.unbiz.common.io.StreamUtil;

/**
 * 有关 <code>JVM</code> 处理的工具类。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午3:57:41
 */
public abstract class JVMUtil {

    private static final String[] MANIFESTS = { "Manifest.mf", "manifest.mf", "MANIFEST.MF" };

    private static final JavaRuntimeInfo JAVA_RUNTIME_INFO = new JavaRuntimeInfo();

    /**
     * 适用于 JDK 1.6 from JDK DOC "java.lang.instrument Interface Instrumentation" ... The system class loader supports
     * adding a JAR file to be searched if it implements a method named appendToClassPathForInstrumentation which takes
     * a single parameter of type java.lang.String. The method is not required to have public access. The name of the
     * JAR file is obtained by invoking the getName() method on the jarfile and this is provided as the parameter to the
     * appendtoClassPathForInstrumentation method.
     * 
     * 将指定文件加载到<code>classpath</code>中
     * 
     * @param name 指定路径
     * @return 如果加载成功返回<code>true</code>
     */
    public static boolean appendToClassPath(String name) {
        if (!FileUtil.exist(name)) {
            return false;
        }

        try {
            ClassLoader clsLoader = ClassLoader.getSystemClassLoader();
            Method appendToClassPathMethod =
                    clsLoader.getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);

            if (null != appendToClassPathMethod) {
                appendToClassPathMethod.setAccessible(true);
                appendToClassPathMethod.invoke(clsLoader, name);
            }

            return true;
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    /**
     * 将指定路径下的<code>jar</code>文件加载到<code>classpath</code>中
     * 
     * @param dirName 指定路径
     * @return jar文件路径数组
     */
    public static String[] addAllJarsToClassPath(String dirName) {
        if (!FileUtil.isDirectory(dirName)) {
            return null;
        }

        File dir = new File(dirName);
        List<String> ret = CollectionUtil.createArrayList();

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                ret.addAll(Arrays.asList(addAllJarsToClassPath(file.getAbsolutePath())));
                continue;
            }

            if (file.getName().toLowerCase().endsWith(".jar") && appendToClassPath(file.getAbsolutePath())) {
                ret.add(file.getAbsolutePath());
            }
        }

        return ret.toArray(new String[0]);
    }

    private static Manifest getManifestFromFile(File classpathItem) {

        File metaDir = new File(classpathItem, "META-INF");
        File manifestFile = null;
        if (metaDir.isDirectory()) {
            for (String m : MANIFESTS) {
                File mFile = new File(metaDir, m);
                if (mFile.isFile()) {
                    manifestFile = mFile;
                    break;
                }
            }
        }

        if (manifestFile == null) {
            return null;
        }

        return getAndClose(manifestFile);
    }

    // FIXME ExceptionUtil.toRuntimeException(e) And method name
    private static Manifest getAndClose(File manifestFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(manifestFile);
            return new Manifest(fis);
        } catch (IOException e) {
            throw ExceptionUtil.toRuntimeException(e);
        } finally {
            StreamUtil.close(fis);
        }
    }

    private static Manifest getManifestFromJar(File classpathItem) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(classpathItem);
            return new JarFile(classpathItem).getManifest();
        } catch (IOException e) {
            throw new RuntimeException("JVMUtil.getManifestFromJar Error", e);
        } finally {
            StreamUtil.close(fis);
        }

    }

    /**
     * 获取<code>classpath</code>下的<code>Manifest</code>，如果文件不存在，返还 <code>null</code>
     * 
     * @param classpathItem 文件项路径，如果为<code>null</code>，则返还<code>null</code>
     * @return ## @see Manifest
     */
    public static Manifest getManifest(File classpathItem) {
        if (classpathItem == null) {
            return null;
        }

        if (classpathItem.isFile()) {
            return getManifestFromJar(classpathItem);
        }

        return getManifestFromFile(classpathItem);
    }

    private static String getClasspathItemBaseDir(File classpathItem) {

        if (classpathItem.isFile()) {
            return classpathItem.getParent();
        }

        return classpathItem.toString();
    }

    /**
     * 使用默认的classloader获取classpath下所有资源的路径文件 @see {@link #getClasspath(ClassLoader)}
     * 
     * @return classpath下所有资源的路径文件
     */
    public static File[] getClasspath() {
        return getClasspath(ClassLoaderUtil.getContextClassLoader());
    }

    /**
     * 通过<code>ClassLoader</code>的继承关系，递归获取classpath下所有资源的路径文件,根据以下顺序收集：
     * <p>
     * <li>通过<code>URLClassLoader</code>收集 <code>URL</code>，忽略其他协议</li>
     * <li>如果存在manifest文件，获取manifest文件中的定义的资源</li>
     * <li>获取bootstrap classpath下的资源</li>
     * 
     * @param classLoader ## @see ClassLoader
     * @return classpath下所有资源的路径文件
     */
    public static File[] getClasspath(ClassLoader classLoader) {
        Set<File> classpaths = CollectionUtil.createHashSet();

        while (classLoader != null) {
            if (classLoader instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader) classLoader).getURLs();
                for (URL u : urls) {
                    File f = FileUtil.toFile(u);
                    if (f != null) {
                        try {
                            f = f.getCanonicalFile();
                            classpaths.add(f);
                            addInnerClasspathItems(classpaths, f);
                        } catch (IOException e) {
                            throw ExceptionUtil.toRuntimeException(e);
                        }
                    }
                }
            }
            classLoader = classLoader.getParent();
        }

        String bootstrap = JAVA_RUNTIME_INFO.SUN_BOOT_CLASS_PATH;
        if (bootstrap != null) {
            classpaths.add(new File(bootstrap));
        }

        return classpaths.toArray(new File[classpaths.size()]);
    }

    private static void addInnerClasspathItems(Set<File> classpaths, File item) throws IOException {

        Manifest manifest = getManifest(item);
        if (manifest == null) {
            return;
        }

        Attributes attributes = manifest.getMainAttributes();
        if (attributes == null) {
            return;
        }

        String classPaths = attributes.getValue(Attributes.Name.CLASS_PATH);
        if (classPaths == null) {
            return;
        }

        String base = getClasspathItemBaseDir(item);

        String[] tokens = StringUtil.split(classPaths, ' ');
        for (String t : tokens) {
            File file = new File(base, t);
            file = file.getCanonicalFile();

            if (file.exists()) {
                classpaths.add(file);
            }
        }
    }

    /**
     * 取得当前运行的JRE的信息。
     * 
     * @return <code>JreInfo</code>对象
     */
    public static final JavaRuntimeInfo getJavaRuntimeInfo() {
        return JAVA_RUNTIME_INFO;
    }

    /**
     * 代表当前运行的JRE的信息。
     */
    public static final class JavaRuntimeInfo {

        private final String SUN_BOOT_CLASS_PATH = getSystemProperty("sun.boot.class.path", false);

        private final String SUN_ARCH_DATA_MODEL = getSystemProperty("sun.arch.data.model", false);

        private final String JAVA_VENDOR_URL = getSystemProperty("java.vendor.url", false);

        /**
         * 防止从外界创建此对象。
         */
        private JavaRuntimeInfo() {
        }

        /**
         * Bootstrap classes are the classes that implement the Java 2 Platform.
         * <p>
         * Bootstrap classes are in the rt.jar and several other jar files in
         * <p>
         * the jre/lib directory. These archives are specified by the value of
         * <p>
         * the bootstrap class path which is stored in the sun.boot.class.path
         * <p>
         * system property. This system property is for reference only, and
         * <p>
         * should not be directly modified. It is very unlikely that you will
         * <p>
         * need to redefine the bootstrap class path. The nonstandard option,
         * <p>
         * -Xbootclasspath, allows you to do so in those rare cicrcumstances in
         * <p>
         * which it is necessary to use a different set of core classes
         * 
         * @return bootstrap class path
         */
        public final String getSunBootClassPath() {
            return SUN_BOOT_CLASS_PATH;
        }

        /**
         * There's no public API that allows you to distinguish between 32 and
         * <p>
         * 64-bit operation. Think of 64-bit as just another platform in the
         * <p>
         * write once, run anywhere tradition. However, if you'd like to write
         * <p>
         * code which is platform specific (shame on you), the system property
         * <p>
         * sun.arch.data.model has the value "32", "64", or "unknown".
         * 
         * @return <code>32</code> or <code>64</code>-bit operation,if not support the property, return
         *         <code>unknown</code>
         */
        public final String getSunArchDataModel() {
            return SUN_ARCH_DATA_MODEL;
        }

        public final String getVendorURL() {
            return JAVA_VENDOR_URL;
        }

    }

    /**
     * 取得系统属性，如果因为Java安全的限制而失败，则将错误打在<code>System.err</code>中，然后返回 <code>null</code>。
     * 
     * @param name 属性名
     * @param quiet 安静模式，不将出错信息打在<code>System.err</code>中
     * 
     * @return 属性值或<code>null</code>
     */
    private static String getSystemProperty(String name, boolean quiet) {
        try {
            return System.getProperty(name);
        } catch (SecurityException e) {
            if (!quiet) {
                System.err.println("Caught a SecurityException reading the system property '" + name
                        + "'; the SystemUtil property value will default to null.");
            }

            return null;
        }
    }

}
