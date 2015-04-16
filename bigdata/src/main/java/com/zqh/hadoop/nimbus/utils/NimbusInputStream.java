package com.zqh.hadoop.nimbus.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NimbusInputStream extends InputStream {

	public static final int EOF = -1;
	private DataInputStream strm = null;

	public NimbusInputStream(InputStream in) {
		strm = new DataInputStream(new BufferedInputStream(in));
	}

	public int skipToEOF() throws IOException {
		int bytesRead = 0;
		while (strm.read() != EOF) {
			++bytesRead;
		}
		return bytesRead;
	}

	public int readCmd() throws IOException {
		char cmdToken = strm.readChar();

		if (cmdToken != NimbusOutputStream.CMD_TOKEN) {
			throw new IOException("Token is not the cmd token");
		}

		return strm.readInt();
	}

	public long readNumArgs() throws IOException {
		char argsToken = strm.readChar();

		if (argsToken != NimbusOutputStream.ARGS_TOKEN) {
			throw new IOException("Token is not the args token: " + argsToken);
		}

		return strm.readLong();
	}

	public byte[] readArg() throws IOException {
		char bytesToken = strm.readChar();

		if (bytesToken != NimbusOutputStream.BYTES_TOKEN) {
			throw new IOException("Token is not the bytes token: " + bytesToken);
		}

		int numBytes = strm.readInt();
		byte[] bytes = new byte[numBytes];
		for (int i = 0; i < numBytes; ++i) {
			bytes[i] = strm.readByte();
		}

		return bytes;
	}

	public void verifyEndOfMessage() throws IOException {
		char token = strm.readChar();

		if (token != NimbusOutputStream.END_CMD_TOKEN) {
			throw new IOException("Token is not the end command token: "
					+ token);
		}
	}

	@Override
	@Deprecated
	public int read() throws IOException {
		throw new UnsupportedOperationException("NimbusOutputStream::write");
	}

	@Override
	@Deprecated
	public int read(byte[] b) throws IOException {
		throw new UnsupportedOperationException("NimbusOutputStream::write");
	}

	@Override
	@Deprecated
	public int read(byte[] b, int off, int len) throws IOException {
		throw new UnsupportedOperationException("NimbusOutputStream::write");
	}
}
