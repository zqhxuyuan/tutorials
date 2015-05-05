/**
 * 
 */
package com.baidu.unbiz.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * 用来获取包中的相关信息。
 * 
 * <p>
 * 这个类中的每个方法都可以“安全”地处理 <code>null</code> ，而不会抛出 <code>NullPointerException</code>。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午6:50:28
 */
public abstract class PackageUtil {

    /**
     * 空的列表
     */
    private static final List<String> EMPTY_LIST = Collections.emptyList();

    /**
     * 获取包中所有资源，如果字符串为<code>“空”</code>则返回<code>null</code>。
     * 
     * @param packageName 包名
     * @return 包中所有资源文件，如果字符串为<code>“空”</code>则返回<code>null</code>。
     * @throws IOException
     */
    public static List<String> getResourceInPackage(String packageName) throws IOException {
        if (StringUtil.isBlank(packageName)) {
            return null;
        }

        boolean recursive = packageName.endsWith(".*");
        String packagePath = getPackagePath(packageName);
        List<String> resources = CollectionUtil.createArrayList();
        String packageDirName = packagePath.replace('.', '/');

        URL[] dirs = ClassLoaderUtil.getResources(packageDirName);
        for (URL url : dirs) {
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                findResourceInDirPackage(packagePath, URLDecoder.decode(url.getFile(), "UTF-8"), resources);
            } else if ("jar".equals(protocol)) {
                findResourceInJarPackage(url, packageName, packageDirName, recursive, resources);
            }

        }
        return resources;
    }

    /**
     * 获取包中的所有类，如果字符串为<code>“空”</code>则返回<code>null</code>。
     * 
     * @param packageName 包名
     * @return 包中所有类，如果字符串为<code>“空”</code>则返回<code>null</code>。
     * @throws IOException
     */
    public static List<String> getClassesInPackage(String packageName) throws IOException {
        return getClassesInPackage(packageName, null, null);
    }

    /**
     * 过滤后，获取包中的类，如果字符串为<code>“空”</code>则返回<code>null</code>。
     * 
     * @param packageName 包名
     * @param included 包含的类列表
     * @param excluded 排除的类列表
     * @return 包中满足条件的类，如果字符串为<code>“空”</code>则返回<code>null</code>。
     * @throws IOException
     */
    public static List<String> getClassesInPackage(String packageName, List<String> included, List<String> excluded)
            throws IOException {
        if (StringUtil.isBlank(packageName)) {
            return null;
        }

        boolean recursive = packageName.endsWith(".*");
        String packagePath = getPackagePath(packageName);
        List<String> classes = CollectionUtil.createArrayList();
        String packageDirName = packagePath.replace('.', '/');

        URL[] dirs = ClassLoaderUtil.getResources(packageDirName);
        for (URL url : dirs) {
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                findClassesInDirPackage(packagePath, included, excluded, URLDecoder.decode(url.getFile(), "UTF-8"),
                        recursive, classes);
            } else if ("jar".equals(protocol)) {
                findClassesInJarPackage(url, packageName, included, excluded, packageDirName, recursive, classes);
            }
        }
        return classes;
    }

    /**
     * 查找<code>jar</code>中的资源
     * 
     * @param url 资源<code>URL</code>
     * @param packageName 包名
     * @param included 包含的类列表
     * @param excluded 排除的类列表
     * @param packageDirName 包的目录名
     * @param recursive 是否递归
     * @param classes 存储类的资源列表
     * @throws IOException
     */
    private static void findClassesInJarPackage(URL url, String packageName, List<String> included,
            List<String> excluded, String packageDirName, final boolean recursive, List<String> classes)
            throws IOException {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }

            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }

                if ((idx != -1) || recursive) {
                    // it's not inside a deeper dir
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        filterClass(packageName, className, included, excluded, classes);
                    }
                }
            }
        }
    }

    /**
     * 查找包中的类
     * 
     * @param packageName 包名
     * @param included 包含的类列表
     * @param excluded 排除的类列表
     * @param packagePath 包路径
     * @param recursive 是否递归
     * @param classes 存储类的资源列表
     */
    private static void findClassesInDirPackage(String packageName, List<String> included, List<String> excluded,
            String packagePath, final boolean recursive, List<String> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findClassesInDirPackage(packageName + "." + file.getName(), included, excluded, file.getAbsolutePath(),
                        recursive, classes);
                continue;
            }

            filterClass(packageName, file.getName().substring(0, file.getName().length() - 6), included, excluded,
                    classes);

        }
    }

    /**
     * 过滤包中的类
     * 
     * @param packageName 包名
     * @param className 类名
     * @param included 包含的类列表
     * @param excluded 排除的类列表
     * @param classes 存储类的列表
     */
    private static void filterClass(String packageName, String className, List<String> included, List<String> excluded,
            List<String> classes) {
        if (isIncluded(className, included, excluded)) {
            classes.add(packageName + '.' + className);
        }
    }

    /**
     * 查找<code>jar</code>中的资源
     * 
     * @param url 资源<code>URL</code>
     * @param packageName 包名
     * @param packageDirName 包目录名
     * @param recursive 是否递归
     * @param resources 存储资源的列表
     * @throws IOException
     */
    private static void findResourceInJarPackage(URL url, String packageName, String packageDirName, boolean recursive,
            List<String> resources) throws IOException {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }

            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }

                if ((idx != -1) || recursive) {
                    // it's not inside a deeper dir
                    if (!entry.isDirectory()) {
                        resources.add(packageName + "." + name.substring(packageName.length() + 1));
                    }

                }
            }
        }
    }

    /**
     * 查找包中的资源
     * 
     * @param packageName 包名
     * @param packagePath 包的路径
     * @param resources 存储资源的列表
     */
    private static void findResourceInDirPackage(String packageName, String packagePath, List<String> resources) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles();
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findResourceInDirPackage(packageName + "." + file.getName(), file.getAbsolutePath(), resources);
                continue;
            }
            resources.add(packageName + "." + file.getName());
        }
    }

    /**
     * 获取包路径
     * 
     * @param packageName 包名
     * @return 包路径
     */
    private static String getPackagePath(String packageName) {
        if (packageName.endsWith(".*")) {
            packageName = packageName.substring(0, packageName.lastIndexOf(".*"));
        }

        if (packageName.endsWith("/")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        return packageName;
    }

    /**
     * 是否包含名称，如果<code>included</code>和<code>excluded</code>列表均为“空”，返回 <code>true</code>
     * 
     * @param name 需要包含的名称
     * @param included 需要验证的包含的正则表达式列表
     * @param excluded 不需要验证的排除的正则表达式列表
     * @return 如果包含则返回<code>true</code>，否则返回<code>false</code>
     */
    private static boolean isIncluded(String name, List<String> included, List<String> excluded) {
        if (CollectionUtil.isEmpty(included) && CollectionUtil.isEmpty(excluded)) {
            return true;
        }

        included = (null == included) ? EMPTY_LIST : included;
        excluded = (null == excluded) ? EMPTY_LIST : excluded;
        boolean isIncluded = PackageUtil.isMatched(name, included);
        boolean isExcluded = PackageUtil.isMatched(name, excluded);

        if (isIncluded && !isExcluded) {
            return true;
        }

        if (isExcluded) {
            return false;
        }

        return included.size() == 0;
    }

    /**
     * 列表中是否匹配存在匹配的名称
     * 
     * @param name 需要匹配的名称
     * @param list 名称列表
     * @return 如果匹配则返回<code>true</code>，否则返回<code>false</code>
     */
    private static boolean isMatched(String name, List<String> list) {
        for (String regexpStr : list) {
            if (Pattern.matches(regexpStr, name)) {
                return true;
            }

        }
        return false;
    }

    /** 获取<code>clazz</code>所在的包 */
    public static Package getPackage(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return clazz.getPackage();
    }

    /**
     * 取得指定对象所属的类的package名。
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     * 
     * @param object 要查看的对象
     * @return package名，如果对象为 <code>null</code> ，则返回<code>""</code>
     */
    public static String getPackageNameForObject(Object object) {
        if (object == null) {
            return Emptys.EMPTY_STRING;
        }

        return getPackageName(object.getClass().getName());
    }

    /**
     * 取得指定类的package名。
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     * 
     * @param clazz 要查看的类
     * @return package名，如果类为 <code>null</code> ，则返回<code>""</code>
     */
    public static String getPackageName(Class<?> clazz) {
        if (clazz == null) {
            return Emptys.EMPTY_STRING;
        }

        return getPackageName(clazz.getName());
    }

    /**
     * 取得指定类名的package名。
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     * 
     * @param javaClassName 要查看的类名
     * @return package名，如果类名为空，则返回 <code>null</code>
     */
    public static String getPackageName(String javaClassName) {
        String friendlyClassName = ClassUtil.toFriendlyClassName(javaClassName, false, null);

        if (friendlyClassName == null) {
            return Emptys.EMPTY_STRING;
        }

        int i = friendlyClassName.lastIndexOf('.');

        if (i == -1) {
            return Emptys.EMPTY_STRING;
        }

        return friendlyClassName.substring(0, i);
    }

    // ==========================================================================
    // 取得类名和package名的resource名的方法。
    //
    // 和类名、package名不同的是，resource名符合文件名命名规范，例如：
    // java/lang/String.class
    // com/baidu/unbiz/common
    // etc.
    // ==========================================================================

    /**
     * 取得对象所属的类的资源名。
     * <p>
     * 例如：
     * </p>
     * <p/>
     * 
     * <pre>
     * ClassUtil.getResourceNameForObjectClass(&quot;This is a string&quot;) = &quot;java/lang/String.class&quot;
     * </pre>
     * 
     * @param object 要显示类名的对象
     * @return 指定对象所属类的资源名，如果对象为空，则返回<code>null</code>
     */
    public static String getResourceNameForObjectClass(Object object) {
        if (object == null) {
            return null;
        }

        return object.getClass().getName().replace('.', '/') + ".class";
    }

    /**
     * 取得指定类的资源名。
     * <p>
     * 例如：
     * </p>
     * <p/>
     * 
     * <pre>
     * ClassUtil.getResourceNameForClass(String.class) = &quot;java/lang/String.class&quot;
     * </pre>
     * 
     * @param clazz 要显示类名的类
     * @return 指定类的资源名，如果指定类为空，则返回<code>null</code>
     */
    public static String getResourceNameForClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return clazz.getName().replace('.', '/') + ".class";
    }

    /**
     * 取得指定类的资源名。
     * <p>
     * 例如：
     * </p>
     * <p/>
     * 
     * <pre>
     * ClassUtil.getResourceNameForClass(&quot;java.lang.String&quot;) = &quot;java/lang/String.class&quot;
     * </pre>
     * 
     * @param className 要显示的类名
     * @return 指定类名对应的资源名，如果指定类名为空，则返回<code>null</code>
     */
    public static String getResourceNameForClass(String className) {
        if (className == null) {
            return null;
        }

        return className.replace('.', '/') + ".class";
    }

    /**
     * 取得指定对象所属的类的package名的资源名。
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     * 
     * @param object 要查看的对象
     * @return package名，如果对象为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getResourceNameForObjectPackage(Object object) {
        if (object == null) {
            return null;
        }

        return getPackageNameForObject(object).replace('.', '/');
    }

    /**
     * 取得指定类的package名的资源名。
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     * 
     * @param clazz 要查看的类
     * @return package名，如果类为 <code>null</code> ，则返回 <code>null</code>
     */
    public static String getResourceNameForPackage(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getPackageName(clazz).replace('.', '/');
    }

    /**
     * 取得指定类名的package名的资源名。
     * <p>
     * 对于数组，此方法返回的是数组元素类型的package名。
     * </p>
     * 
     * @param className 要查看的类名
     * @return package名，如果类名为空，则返回 <code>null</code>
     */
    public static String getResourceNameForPackage(String className) {
        if (className == null) {
            return null;
        }

        return getPackageName(className).replace('.', '/');
    }

}
