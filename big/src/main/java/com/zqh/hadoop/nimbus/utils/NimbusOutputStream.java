package com.zqh.hadoop.nimbus.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class NimbusOutputStream extends OutputStream {

	public static final char CMD_TOKEN = '*';
	public static final char END_CMD_TOKEN = '%';
	public static final char ARGS_TOKEN = '&';
	public static final char BYTES_TOKEN = '$';

	private DataOutputStream strm = null;

	public NimbusOutputStream() {
	}

	public NimbusOutputStream(OutputStream out) {
		strm = new DataOutputStream(new BufferedOutputStream(out));
	}
	
	public void setOutputStream(OutputStream out) {
		strm = new DataOutputStream(new BufferedOutputStream(out));		
	}

	public void prepStreamingWrite(int cmd, long numArgs) throws IOException {
		strm.writeChar(CMD_TOKEN);
		strm.writeInt(cmd);

		strm.writeChar(ARGS_TOKEN);
		strm.writeLong(numArgs);
	}

	public void streamingWrite(byte[] arg) throws IOException {
		strm.writeChar(BYTES_TOKEN);
		strm.writeInt(arg.length);
		strm.write(arg);
	}

	public void streamingWrite(String arg) throws IOException {

		byte[] bytes = BytesUtil.toBytes(arg);
		strm.writeChar(BYTES_TOKEN);
		strm.writeInt(bytes.length);
		strm.write(bytes);
	}

	public void streamingWrite(String... args) throws IOException {

		byte[] bytes = null;
		for (String arg : args) {
			bytes = BytesUtil.toBytes(arg);
			strm.writeChar(BYTES_TOKEN);
			strm.writeInt(bytes.length);
			strm.write(bytes);
		}
	}

	public void endStreamingWrite() throws IOException {
		strm.writeChar(END_CMD_TOKEN);
		strm.flush();
	}

	public void write(int cmd, byte[]... args) throws IOException {

		strm.writeChar(CMD_TOKEN);
		strm.writeInt(cmd);

		strm.writeChar(ARGS_TOKEN);
		strm.writeLong(args.length);

		for (byte[] arg : args) {
			strm.writeChar(BYTES_TOKEN);
			strm.writeInt(arg.length);
			strm.write(arg);
		}

		strm.writeChar(END_CMD_TOKEN);
		strm.flush();
	}

	public void write(int cmd, Collection<? extends String> args)
			throws IOException {
		strm.writeChar(CMD_TOKEN);
		strm.writeInt(cmd);

		strm.writeChar(ARGS_TOKEN);
		strm.writeLong(args.size());

		byte[] bytes = null;
		for (String arg : args) {
			bytes = BytesUtil.toBytes(arg);
			strm.writeChar(BYTES_TOKEN);
			strm.writeInt(bytes.length);

			strm.write(bytes);
		}

		strm.writeChar(END_CMD_TOKEN);
		strm.flush();
	}

	public void write(int cmd, Map<? extends String, ? extends String> values)
			throws IOException {
		strm.writeChar(CMD_TOKEN);
		strm.writeInt(cmd);

		strm.writeChar(ARGS_TOKEN);
		strm.writeLong(values.size());

		byte[] bytes = null;
		for (Entry<? extends String, ? extends String> entry : values
				.entrySet()) {
			bytes = BytesUtil.toBytes(entry.getKey());
			strm.writeChar(BYTES_TOKEN);
			strm.writeInt(bytes.length);
			strm.write(bytes);

			bytes = BytesUtil.toBytes(entry.getValue());
			strm.writeChar(BYTES_TOKEN);
			strm.writeInt(bytes.length);
			strm.write(bytes);
		}

		strm.writeChar(END_CMD_TOKEN);
		strm.flush();
	}

	@Override
	public void write(int cmd) throws IOException {
		strm.writeChar(CMD_TOKEN);
		strm.writeInt(cmd);

		strm.writeChar(ARGS_TOKEN);
		strm.writeLong(0L);

		strm.writeChar(END_CMD_TOKEN);
		strm.flush();
	}

	public void write(int cmd, String... args) throws IOException {
		strm.writeChar(CMD_TOKEN);
		strm.writeInt(cmd);

		strm.writeChar(ARGS_TOKEN);
		strm.writeLong(args.length);

		byte[] bytes = null;
		for (String arg : args) {
			bytes = BytesUtil.toBytes(arg);
			strm.writeChar(BYTES_TOKEN);
			strm.writeInt(bytes.length);
			strm.write(bytes);
		}

		strm.writeChar(END_CMD_TOKEN);
		strm.flush();
	}

	@Override
	public void flush() throws IOException {
		strm.flush();
	}
}
