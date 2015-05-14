/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.Ignore;
import org.junit.Test;

import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * FIXME
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月16日 下午5:09:01
 */
public class JVMUtilTest extends CachedLogger {

    private static final String classpath = ClassLoaderUtil.getClasspath();

    @Test
    public void appendToClassPath() {

        assertTrue(JVMUtil.appendToClassPath(classpath + File.separator + "lib/test1.jar"));

        assertFalse(JVMUtil.appendToClassPath(null));
        assertFalse(JVMUtil.appendToClassPath(""));
        assertFalse(JVMUtil.appendToClassPath("impossible exist! "));
        assertFalse(JVMUtil.appendToClassPath(classpath + File.separator + "lib/notexist.jar"));

    }

    @Test
    public void addAllJarsToClassPath() {

        String libPath = classpath + File.separator + "lib";
        File[] jarSuffixs = FileUtil.listDirSuffixFiles(libPath, ".jar");
        String[] jars = JVMUtil.addAllJarsToClassPath(libPath);

        assertTrue(ArrayUtil.isNotEmpty(jars));
        assertEquals(jarSuffixs.length, jars.length);

        for (int i = 0, size = jars.length; i < size; i++) {
            assertEquals(jarSuffixs[i].getAbsolutePath(), jars[i]);
        }

        assertNull(JVMUtil.addAllJarsToClassPath(null));

        assertTrue(ArrayUtil.isEmpty(JVMUtil.addAllJarsToClassPath("")));
        assertTrue(ArrayUtil.isEmpty(JVMUtil.addAllJarsToClassPath("notexist")));
    }

    @Ignore
    @Test
    public void getManifest() {
        assertNull(JVMUtil.getManifest(null));
        assertNull(JVMUtil.getManifest(new File("")));
        assertNull(JVMUtil.getManifest(new File("notexist")));
        assertNull(JVMUtil.getManifest(new File(ClassLoaderUtil.getClasspath())));
        try {
            assertNull(JVMUtil.getManifest(new File(ClassLoaderUtil.getClasspath(), "log4j.properties")));
        } catch (Exception e) {
            assertEquals("JVMUtil.getManifestFromJar Error", e.getMessage());
            assertEquals(RuntimeException.class, e.getClass());
            assertEquals("error in opening zip file", e.getCause().getMessage());
            assertEquals(java.util.zip.ZipException.class, e.getCause().getClass());
        }

        Manifest manifest = JVMUtil.getManifest(new File(System.getProperty("user.dir")));
        assertNotNull(manifest);

        Attributes main = manifest.getMainAttributes();
        // Assert.assertEquals("1.6.0_43", main.getValue("Build-Jdk"));
        assertEquals("work", main.getValue("Built-By"));
        assertEquals("1.0", main.getValue("Manifest-Version"));

    }

    @Test
    public void getClasspath() {

        try {

            assertTrue(JVMUtil.getClasspath().length > 0);
            assertTrue(JVMUtil.getClasspath(ClassLoader.getSystemClassLoader()).length > 0);
            assertArrayEquals(JVMUtil.getClasspath(), JVMUtil.getClasspath(ClassLoader.getSystemClassLoader()));

            assertEquals(1, JVMUtil.getClasspath(null).length);

        } catch (Exception e) {
            assertEquals("JVMUtil.getManifestFromJar Error", e.getMessage());
            assertEquals(RuntimeException.class, e.getClass());
            assertEquals("error in opening zip file", e.getCause().getMessage());
            assertEquals(java.util.zip.ZipException.class, e.getCause().getClass());
        }
    }

    @Test
    public void getJavaRuntimeInfo() {
        String arch = JVMUtil.getJavaRuntimeInfo().getSunArchDataModel();
        assertTrue("32".equals(arch) || "64".equals(arch));

        String boot = JVMUtil.getJavaRuntimeInfo().getSunBootClassPath();

        try {

            File[] classpathes = JVMUtil.getClasspath();
            boolean flag = false;
            for (File classpath : classpathes) {
                if (classpath.getAbsolutePath().equals(boot)) {
                    flag = true;
                    break;
                }
            }
            assertTrue(flag);

        } catch (Exception e) {
            assertEquals("JVMUtil.getManifestFromJar Error", e.getMessage());
            assertEquals(RuntimeException.class, e.getClass());
            assertEquals("error in opening zip file", e.getCause().getMessage());
            assertEquals(java.util.zip.ZipException.class, e.getCause().getClass());
        }

    }
}
