/**
 * 
 */
package com.baidu.unbiz.common.io;

import static com.baidu.unbiz.common.Assert.assertNotNull;
import static com.baidu.unbiz.common.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 代表一个byte数组。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月21日 上午2:03:48
 */
public class ByteArray {
    private final byte[] bytes;
    private final int offset;
    private final int length;

    public ByteArray(byte[] bytes) {
        this(bytes, 0, Integer.MIN_VALUE);
    }

    public ByteArray(byte[] bytes, int offset, int length) {
        assertNotNull(bytes, "bytes");
        assertTrue(offset >= 0, "offset", offset);

        if (length == Integer.MIN_VALUE) {
            length = bytes.length - offset;
        }

        assertTrue(length >= 0 && length <= bytes.length - offset, "length");

        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    public byte[] getRawBytes() {
        return bytes;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public byte[] toByteArray() {
        byte[] copy = new byte[length];

        System.arraycopy(bytes, offset, copy, 0, length);

        return copy;
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(bytes, offset, length);
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(bytes, offset, length);
    }

    @Override
    public String toString() {
        return "byte[" + length + "]";
    }
}
