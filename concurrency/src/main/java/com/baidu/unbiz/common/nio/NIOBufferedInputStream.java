/**
 * 
 */
package com.baidu.unbiz.common.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 上午1:48:30
 */
public class NIOBufferedInputStream extends InputStream {

    // FIXME
    private final static int BUFFER_SIZE = 8192;

    private SocketChannel sc = null;

    private ByteBuffer bb = null;

    private Selector rs = null;

    public NIOBufferedInputStream(ReadableByteChannel channel, int size) throws ClosedChannelException, IOException {

        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }

        this.bb = ByteBuffer.allocateDirect(size);
        this.sc = (SocketChannel) channel;

        this.sc.configureBlocking(false);

        this.rs = Selector.open();

        sc.register(rs, SelectionKey.OP_READ);

        bb.position(0);
        bb.limit(0);
    }

    public NIOBufferedInputStream(ReadableByteChannel channel) throws ClosedChannelException, IOException {
        this(channel, BUFFER_SIZE);
    }

    public int available() throws IOException {
        if (!rs.isOpen())
            throw new IOException("Input Stream Closed");

        return bb.remaining();
    }

    public void close() throws IOException {
        if (rs.isOpen()) {
            rs.close();

            if (sc.isOpen()) {
                sc.socket().shutdownInput();
                sc.socket().close();
            }

            bb = null;
            sc = null;
        }
    }

    public int read() throws IOException {
        if (!rs.isOpen())
            throw new IOException("Input Stream Closed");

        if (!bb.hasRemaining()) {
            try {
                fill(1);
            } catch (ClosedChannelException e) {
                close();
                return -1;
            }
        }

        return (bb.get() & 0xFF);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int bytesCopied = -1;

        if (!rs.isOpen())
            throw new IOException("Input Stream Closed");

        while (bytesCopied == -1) {
            if (bb.hasRemaining()) {
                bytesCopied = (len < bb.remaining() ? len : bb.remaining());
                bb.get(b, off, bytesCopied);
            } else {
                try {
                    fill(1);
                } catch (ClosedChannelException e) {
                    close();
                    return -1;
                }
            }
        }

        return bytesCopied;
    }

    public long skip(long n) throws IOException {
        long skiped = 0;

        if (!rs.isOpen())
            throw new IOException("Input Stream Closed");

        while (n > 0) {
            if (n <= bb.remaining()) {
                skiped += n;
                bb.position(bb.position() + (int) n);
                n = 0;
            } else {
                skiped += bb.remaining();
                n -= bb.remaining();

                bb.position(bb.limit());

                try {
                    fill((int) n);
                } catch (ClosedChannelException e) {
                    close();
                    return skiped;
                }
            }
        }

        return skiped;
    }

    private void fill(int n) throws IOException, ClosedChannelException {
        int bytesRead = -1;

        if ((n <= 0) || (n <= bb.remaining()))
            return;

        bb.compact();

        n = (bb.remaining() < n ? bb.remaining() : n);

        for (;;) {
            bytesRead = sc.read(bb);

            if (bytesRead == -1)
                throw new ClosedChannelException();

            n -= bytesRead;

            if (n <= 0)
                break;

            rs.select(0);
            rs.selectedKeys().clear();
        }

        bb.flip();
    }
}
