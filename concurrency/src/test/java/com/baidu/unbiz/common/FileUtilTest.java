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
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 下午7:21:04
 */
public class FileUtilTest extends CachedLogger {

    @Test
    public void toFile() {
        assertNull(FileUtil.toFile(null));

        File file = new File(ClassLoaderUtil.getClasspath());
        try {
            assertEquals(file, FileUtil.toFile(file.toURI().toURL()));
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Test
    public void exist() {
        assertFalse(FileUtil.exist((String) null));
        assertFalse(FileUtil.exist("notexist"));
        assertFalse(FileUtil.exist("xxx/yyy/zzz"));

        assertTrue(FileUtil.exist(ClassLoaderUtil.getClasspath()));
        assertTrue(FileUtil.exist(ClassLoaderUtil.getClasspath() + "META-INF/log4j.properties"));
    }

    @Test
    public void listDirSuffixFiles() {
        assertNull(FileUtil.listDirSuffixFiles((String) null, null));
        assertNull(FileUtil.listDirSuffixFiles((File) null, null));
        assertNull(FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath() + "notexist", null));
        assertNull(FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath() + "xxx/yyy/zzz", null));
        assertNull(FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath() + "log4j.properties", null));

        assertNull(FileUtil.listDirSuffixFiles((String) null, ".properties"));
        assertNull(FileUtil.listDirSuffixFiles((File) null, ".properties"));
        assertNull(FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath() + "notexist", ".properties"));
        assertNull(FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath() + "xxx/yyy/zzz", ".properties"));
        assertNull(FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath() + "log4j.properties", ".properties"));

        assertArrayEquals(new File[] {}, FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath(), ".nothissuffix"));

        int expected = new File(ClassLoaderUtil.getClasspath()).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.getName().endsWith(".properties"));
            }
        }).length;
        assertEquals(expected, FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath(), ".properties").length);

        expected = new File(ClassLoaderUtil.getClasspath()).listFiles().length;
        assertEquals(expected, FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath(), null).length);
        assertEquals(expected, FileUtil.listDirSuffixFiles(ClassLoaderUtil.getClasspath(), "").length);
    }

    @Test
    public void listDirAllConditionFiles() {
        assertNull(FileUtil.listDirAllConditionFiles((String) null, null));
        assertNull(FileUtil.listDirAllConditionFiles((File) null, null));
        assertNull(FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath() + "notexist", null));
        assertNull(FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath() + "xxx/yyy/zzz", null));
        assertNull(FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath() + "log4j.properties", null));

        int expected = new File(ClassLoaderUtil.getClasspath()).listFiles().length;
        assertEquals(expected, FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath(), null).length);
        assertEquals(expected,
                FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath(), new boolean[] {}).length);

        assertArrayEquals(
                new File[] {},
                FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath(), new boolean[] { true, true, false,
                        true, true }));

        assertArrayEquals(new File[] {},
                FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath(), true, true, false, true, true));

        assertEquals(
                expected,
                FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath(), new boolean[] { true, true, true,
                        true, true }).length);
        assertEquals(expected,
                FileUtil.listDirAllConditionFiles(ClassLoaderUtil.getClasspath(), true, true, true, true, true).length);
    }

    @Test
    public void listDirAnyConditionFiles() {
        assertNull(FileUtil.listDirAnyConditionFiles((String) null, null));
        assertNull(FileUtil.listDirAnyConditionFiles((File) null, null));
        assertNull(FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath() + "notexist", null));
        assertNull(FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath() + "xxx/yyy/zzz", null));
        assertNull(FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath() + "log4j.properties", null));

        int expected = new File(ClassLoaderUtil.getClasspath()).listFiles().length;
        assertEquals(expected, FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath(), null).length);
        assertEquals(expected,
                FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath(), new boolean[] {}).length);

        assertArrayEquals(
                new File[] {},
                FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath(), new boolean[] { false, false, false,
                        false, false }));

        assertArrayEquals(new File[] {},
                FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath(), false, false, false, false, false));

        assertEquals(
                expected,
                FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath(), new boolean[] { false, true, false,
                        false, false }).length);
        assertEquals(
                expected,
                FileUtil.listDirAnyConditionFiles(ClassLoaderUtil.getClasspath(), false, true, false, false, false).length);
    }

    @Test
    public void file() {
        assertNull(FileUtil.file(null));
        assertNull(FileUtil.file(null, null));
        assertNull(FileUtil.file(new File(ClassLoaderUtil.getClasspath()), null));

        assertNotNull(FileUtil.file(""));
        assertNotNull(FileUtil.file(null, ""));
        assertNotNull(FileUtil.file(new File(ClassLoaderUtil.getClasspath()), ""));
    }

    @Test
    public void readBytes() {
        try {
            assertNull(FileUtil.readBytes((String) null));
            assertNull(FileUtil.readBytes((File) null));

            assertNull(FileUtil.readBytes("notexist"));
            assertNull(FileUtil.readBytes(new File("notexist")));

            assertNull(FileUtil.readBytes(ClassLoaderUtil.getClasspath()));

            String log4jPath = ClassLoaderUtil.getClasspath() + File.separator + "META-INF/log4j.properties";

            long fileSize = new File(log4jPath).length();

            assertEquals(fileSize, FileUtil.readBytes(log4jPath).length);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createFile() {

    }

    @Test
    public void createDir() {

    }

    @Test
    public void createParentDir() {

    }

    @Test
    public void delete() {

    }

    @Test
    public void deleteDir() {

    }

}
