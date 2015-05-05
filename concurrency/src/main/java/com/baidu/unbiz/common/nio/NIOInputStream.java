/**
 * 
 */
package com.baidu.unbiz.common.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 上午1:47:48
 */
public class NIOInputStream extends InputStream {

    protected int count;
    protected int position;
    private final ByteBuffer in;

    public NIOInputStream(ByteBuffer in) {
        this.in = in;
    }

    public int read() throws IOException {
        try {
            int rc = in.get() & 0xff;
            return rc;
        } catch (BufferUnderflowException e) {
            return -1;
        }
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (in.hasRemaining()) {
            int rc = Math.min(len, in.remaining());
            in.get(b, off, rc);
            return rc;
        }

        return len == 0 ? 0 : -1;
    }

    public long skip(long n) throws IOException {
        int rc = Math.min((int) n, in.remaining());
        in.position(in.position() + rc);
        return rc;
    }

    public int available() throws IOException {
        return in.remaining();
    }

    public boolean markSupported() {
        return false;
    }

    // ignore
    public void close() throws IOException {
    }
}
