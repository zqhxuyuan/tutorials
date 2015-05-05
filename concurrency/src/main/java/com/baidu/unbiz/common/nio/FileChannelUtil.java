/**
 * 
 */
package com.baidu.unbiz.common.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import com.baidu.unbiz.common.ClassUtil;

/**
 * 使用通道调用操作系统命令进行拷贝
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 上午1:43:30
 */
public final class FileChannelUtil {

    private FileChannelUtil() {

    }

    private static void copy(ReadableByteChannel srcChannel, WritableByteChannel destChannel) throws IOException {
        if (ClassUtil.isInstance(FileChannel.class, srcChannel)) {
            FileChannel channel = (FileChannel) srcChannel;

            channel.transferTo(0, channel.size(), destChannel);
        } else if (ClassUtil.isInstance(FileChannel.class, destChannel)) {
            FileChannel channel = (FileChannel) destChannel;

            channel.transferFrom(srcChannel, 0, Long.MAX_VALUE);
        } else {
            throw new IOException("file Channel not support");
        }
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {
        ReadableByteChannel srcChannel = Channels.newChannel(input);

        WritableByteChannel destChannel = Channels.newChannel(output);

        copy(srcChannel, destChannel);

    }

    public static void copyAndClose(InputStream input, OutputStream output) throws IOException {
        ReadableByteChannel srcChannel = Channels.newChannel(input);

        WritableByteChannel destChannel = Channels.newChannel(output);
        try {
            copy(srcChannel, destChannel);
        }

        finally {
            srcChannel.close();
            destChannel.close();
        }

    }

    public static void copy(String src, String dest) throws IOException {
        FileChannel srcChannel = (FileChannel) Channels.newChannel(new FileInputStream(src));

        WritableByteChannel destChannel = Channels.newChannel(new FileOutputStream(dest));

        srcChannel.transferTo(0, srcChannel.size(), destChannel);
    }

    public static void copyAndClose(String src, String dest) throws IOException {
        FileChannel srcChannel = (FileChannel) Channels.newChannel(new FileInputStream(src));

        WritableByteChannel destChannel = Channels.newChannel(new FileOutputStream(dest));
        try {
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } finally {
            destChannel.close();
            srcChannel.close();
        }

    }

    public static void copy(URL url, String dest) throws IOException {

        ReadableByteChannel srcChannel = Channels.newChannel(url.openStream());

        FileChannel destChannel = (FileChannel) Channels.newChannel(new FileOutputStream(dest));

        destChannel.transferFrom(srcChannel, 0, Long.MAX_VALUE);
    }

    public static void copyAndClose(URL url, String dest) throws IOException {

        ReadableByteChannel srcChannel = Channels.newChannel(url.openStream());

        FileChannel destChannel = (FileChannel) Channels.newChannel(new FileOutputStream(dest));
        try {
            destChannel.transferFrom(srcChannel, 0, Long.MAX_VALUE);
        } finally {
            srcChannel.close();
            destChannel.close();
        }

    }

    public static void copy(Socket socket, String dest) throws IOException {

        ReadableByteChannel srcChannel = Channels.newChannel(socket.getInputStream());

        FileChannel destChannel = (FileChannel) Channels.newChannel(new FileOutputStream(dest));

        destChannel.transferFrom(srcChannel, 0, Long.MAX_VALUE);

    }

    public static void copyAndClose(Socket socket, String dest) throws IOException {

        ReadableByteChannel srcChannel = Channels.newChannel(socket.getInputStream());

        FileChannel destChannel = (FileChannel) Channels.newChannel(new FileOutputStream(dest));
        try {
            destChannel.transferFrom(srcChannel, 0, Long.MAX_VALUE);
        } finally {
            srcChannel.close();
            destChannel.close();
        }
    }

    public static void copy(String src, Socket socket) throws IOException {

        FileChannel srcChannel = (FileChannel) Channels.newChannel(new FileOutputStream(src));

        WritableByteChannel destChannel = Channels.newChannel(socket.getOutputStream());

        srcChannel.transferTo(0, srcChannel.size(), destChannel);
    }

    public static void copyAndClose(String src, Socket socket) throws IOException {

        FileChannel srcChannel = (FileChannel) Channels.newChannel(new FileOutputStream(src));

        WritableByteChannel destChannel = Channels.newChannel(socket.getOutputStream());
        try {
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } finally {
            srcChannel.close();
            destChannel.close();
        }

    }

}
