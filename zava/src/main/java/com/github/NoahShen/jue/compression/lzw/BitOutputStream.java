package com.github.NoahShen.jue.compression.lzw;

/*
 * BitOutputStream.java
 *
 * Created on 01 Dec 2003
 */
import java.io.*;

/**
 * 
 * @author Moshe Fresko
 * @course Algorithmic Programming 1
 * @exercise 2
 */
public class BitOutputStream extends FilterOutputStream {
	class BitManager {
		int buf = 0;
		int cnt = 0;

		// Returns -1 if there is nothing yet to be written
		int writeOne(int next) {
			int ret = -1;
			buf = buf * 2 + next;
			cnt++;
			if (cnt == 7) {
				cnt = 0;
				ret = buf;
				buf = 0;
			} else {
				ret = -1;
			}
			return ret;
		}

		//
		int writeLast() {
			int x = 0;
			for (int i = 0; i < 7 - cnt; ++i)
				x = x * 2 + 1;
			for (int i = 7 - cnt; i < 8; ++i)
				x = x * 2;
			return buf | x;
		}
	}

	BitManager bitManager = new BitManager();

	/**
	 * Constructor creates a new instance of BitOutputStream, A decarotor to
	 * OutputStream, via FilterOutputStream
	 */
	public BitOutputStream(OutputStream os) {
		super(os);
	}

	/**
	 * Writes a single bit into the included stream. Although the input is a
	 * single bit, it is given as an int. If it is non-zero, it is threated as
	 * 1.
	 */
	public void write(int i) throws IOException {
		int x = bitManager.writeOne(i >= 1 ? 1 : 0);
		if (x >= 0)
			out.write(x);
	}

	/** Writes a list of bits given in the byte array as 0's and 1's */
	public void write(byte[] arr) throws IOException {
		write(arr, 0, arr.length);
	}

	public void write(byte[] arr, int off, int len) throws IOException {
		int clen = 0;
		for (int i = 0; i < len; ++i) {
			int x = bitManager.writeOne(arr[off + i]);
			if (x >= 0)
				arr[off + (clen++)] = (byte) x;
		}
		out.write(arr, off, clen);
	}

	/**
	 * Closes the included stream. Before closing flushes the necessary buffer.
	 * Flush writes the partial byte kept in the internal buffer.
	 */
	public void close() throws IOException {
		out.write(bitManager.writeLast());
		out.close();
	}

	// "Main" reads a file in the form of characters of '0's and '1's
	// and prints them as bits into another file as a BitStream
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java BitOutputStream FromFile ToFile");
			System.out
					.println("where 'FromFile' includes characters of '0' and '1'");
			System.out.println("and they are written as bits into 'ToFile'");
			System.exit(1);
		}
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(
					args[0]));
			OutputStream os = new BitOutputStream(new BufferedOutputStream(
					new FileOutputStream(args[1])));
			int next;
			while ((next = is.read()) >= 0) {
				char ch = (char) next;
				if (ch == '0' || ch == '1')
					os.write((int) (ch - '0'));
			}
			is.close();
			os.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println(args[0] + " file not found");
			System.exit(1);
		} catch (IOException ioe) {
			System.out.println("Error in reading file " + args[0]
					+ " or writing file " + args[1]);
			System.exit(1);
		}
	}
}
