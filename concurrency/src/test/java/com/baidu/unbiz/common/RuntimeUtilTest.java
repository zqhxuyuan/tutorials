/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午5:16:10
 */
public class RuntimeUtilTest extends CachedLogger {

    @Test
    public void currentClassMethod() {
        assertEquals("com.baidu.unbiz.common.RuntimeUtilTest.currentClassMethod", RuntimeUtil.currentClassMethod());
    }

    @Test
    public void currentMethodName() {
        assertEquals("currentMethodName", RuntimeUtil.currentMethodName());
    }

    @Test
    public void currentClassName() {
        assertEquals("com.baidu.unbiz.common.RuntimeUtilTest", RuntimeUtil.currentClassName());
    }

    @Test
    public void currentNamespace() {
        assertEquals("com.baidu.unbiz.common.RuntimeUtilTest.currentNamespace", RuntimeUtil.currentNamespace());

    }

    @Test
    public void classLocation() {
        File file = new File(RuntimeUtil.classLocation());
        String classLocation = file.getAbsolutePath();

        file = new File(ClassLoaderUtil.getClasspath());
        String classpath = file.getAbsolutePath();

        String target = StringUtil.substringBeforeLast(classpath, File.separator);

        assertEquals(target + File.separator + "classes", classLocation);

        assertNull(RuntimeUtil.classLocation(null));

        assertEquals("java.lang.String", RuntimeUtil.classLocation(String.class));

        // logger.info(RuntimeUtil.classLocation(String.class));

        logger.info(RuntimeUtil.classLocation(StringUtil.class));
    }

}
