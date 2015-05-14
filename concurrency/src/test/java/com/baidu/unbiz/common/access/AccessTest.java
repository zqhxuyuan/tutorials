/**
 * 
 */
package com.baidu.unbiz.common.access;

//import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.unbiz.common.ClassLoaderUtil;
import com.baidu.unbiz.common.access.fs.FileResource;
import com.baidu.unbiz.common.access.fs.LocalFSAccess;
import com.baidu.unbiz.common.access.strategy.DivideThousand;
import com.baidu.unbiz.common.io.ByteArray;
import com.baidu.unbiz.common.io.StreamUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 上午2:48:25
 */
public class AccessTest {

    private Access access;

    private int id;

    String log4j = ClassLoaderUtil.getClasspath() + File.separator + "META-INF/log4j.properties";

    @Before
    public void setUp() throws Exception {
        access = new LocalFSAccess(new DivideThousand(), ClassLoaderUtil.getClasspath());

        id = 1234567890;
    }

    @Test
    public void store() {

        File file = new File(log4j);

        try {
            ByteArray byteArray = StreamUtil.readBytes(file, true);
            Resource resource = new FileResource(id, byteArray);
            resource.getHeader().ext("properties");
            assertEquals(4114700253L, resource.checksum());
            // 4114700253
            access.store(resource);
            byte[] raw = StreamUtil.readBytes(new File(log4j), true).getRawBytes();
            assertArrayEquals(raw, resource.getBody().getRawBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void retrieve() {

        store();

        try {
            Resource resource = access.retrieve(id, "properties");
            byte[] raw = StreamUtil.readBytes(new File(log4j), true).getRawBytes();

            assertArrayEquals(raw, resource.getBody().getRawBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void remove() {

        store();
        try {
            assertTrue(access.remove(id, "properties"));
        } catch (AccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void name() {

    }

    @After
    public void tearDown() throws Exception {
        access = null;
    }

    // LocalFSAccess

}
