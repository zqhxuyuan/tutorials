/**
 * 
 */
package com.baidu.unbiz.common.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Test;

import com.baidu.unbiz.common.io.ByteArray;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午8:35:34
 */
public class ByteArrayTest {

    private byte[] data = "0123456789".getBytes();

    @Test(expected = IllegalArgumentException.class)
    public void constructor_wrong0() {
        new ByteArray(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_wrong1() {
        new ByteArray(null, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_wrong2() {
        new ByteArray(data, -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_wrong3() {
        new ByteArray(data, 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_wrong4() {
        new ByteArray(data, 0, 11);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_wrong5() {
        new ByteArray(data, 1, 10);
    }

    @Test
    public void toBytes() throws Exception {
        ByteArray ba;

        ba = new ByteArray(data);

        assertEquals(0, ba.getOffset());
        assertEquals(10, ba.getLength());
        assertSame(data, ba.getRawBytes());
        assertNotSame(data, ba.toByteArray());
        assertArrayEquals(data, ba.toByteArray());
        assertArrayEquals(data, readStream(ba.toInputStream()));

        ba = new ByteArray(data, 1, Integer.MIN_VALUE);

        assertEquals(1, ba.getOffset());
        assertEquals(9, ba.getLength());
        assertSame(data, ba.getRawBytes());
        assertNotSame(data, ba.toByteArray());
        assertArrayEquals("123456789".getBytes(), ba.toByteArray());
        assertArrayEquals("123456789".getBytes(), readStream(ba.toInputStream()));

        ba = new ByteArray(data, 0, 10);

        assertEquals(0, ba.getOffset());
        assertEquals(10, ba.getLength());
        assertSame(data, ba.getRawBytes());
        assertNotSame(data, ba.toByteArray());
        assertArrayEquals(data, ba.toByteArray());
        assertArrayEquals(data, readStream(ba.toInputStream()));

        ba = new ByteArray(data, 1, 9);

        assertEquals(1, ba.getOffset());
        assertEquals(9, ba.getLength());
        assertSame(data, ba.getRawBytes());
        assertNotSame(data, ba.toByteArray());
        assertArrayEquals("123456789".getBytes(), ba.toByteArray());
        assertArrayEquals("123456789".getBytes(), readStream(ba.toInputStream()));

        ba = new ByteArray(data, 1, 0);

        assertEquals(1, ba.getOffset());
        assertEquals(0, ba.getLength());
        assertSame(data, ba.getRawBytes());
        assertNotSame(data, ba.toByteArray());
        assertArrayEquals("".getBytes(), ba.toByteArray());
        assertArrayEquals("".getBytes(), readStream(ba.toInputStream()));
    }

    @Test
    public void writeTo() throws Exception {
        ByteArray ba;

        ba = new ByteArray(data);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ba.writeTo(baos);

        assertArrayEquals(data, baos.toByteArray());
    }

    @Test
    public void _toString() {
        ByteArray ba;

        ba = new ByteArray(data);

        assertEquals("byte[10]", ba.toString());

        ba = new ByteArray(data, 1, Integer.MIN_VALUE);

        assertEquals("byte[9]", ba.toString());

        ba = new ByteArray(data, 0, 10);

        assertEquals("byte[10]", ba.toString());

        ba = new ByteArray(data, 1, 9);

        assertEquals("byte[9]", ba.toString());

        ba = new ByteArray(data, 1, 0);

        assertEquals("byte[0]", ba.toString());
    }

    private byte[] readStream(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;

        while ((i = is.read()) >= 0) {
            baos.write(i);
        }

        return baos.toByteArray();
    }
}
