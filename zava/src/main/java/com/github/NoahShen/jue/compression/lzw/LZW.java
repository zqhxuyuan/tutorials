package com.github.NoahShen.jue.compression.lzw;

/*
 * LZW.java
 *
 * Created on 01 Dec 2005
 *
 * Implementation of LZW compression/decompression algorithm
 */
import java.io.*;

/**
 * 
 * 
 * @author Moshe Fresko
 * @course Algorithmic Programming 1
 * @exercise 3
 */
public class LZW implements Compression {
	boolean stopped = false;
	Dict dict;
	// The bits that should be written for each code
	int numOfBits;
	// The previous string that we should remember
	// in order to insert into the dictionary
	final ByteArray emptyBA = new ByteArray();
	ByteArray w = emptyBA;

	// Constructor gets the number of bits to be written for each code
	public LZW() {
		numOfBits = 12;
		// Create a new Limited Dictionary
		// For maximum of 2^bits entries
		dict = new LimitedDict(1 << numOfBits);
		// Add all ascii characters to the dictionary
		for (int i = 0; i < 256; ++i)
			dict.add(new ByteArray((byte) i));
	}

	// Encodes the next character.
	// If there is a code generated returns it.
	// If not returns -1.
	int encodeOneChar(int n) {
		byte c = (byte) n;
		ByteArray nw = w.conc(c);
		int code = dict.numFromStr(nw);
		// if it exists then we continue to search for a longer string
		if (code != -1) {
			w = nw;
			return -1;
		} else {
			dict.add(nw);
			nw = w;
			w = new ByteArray(c);
			return dict.numFromStr(nw);
		}
	}

	// If there is something left in w, returns its code
	int encodeLast() {
		ByteArray nw = w;
		w = emptyBA;
		return dict.numFromStr(nw);
	}

	// Write the code in bits into output stream
	void writeCode(OutputStream os, int code) throws IOException {
		for (int i = 0; i < numOfBits; ++i) {
			os.write(code & 1);
			code /= 2;
		}
	}

	int readCode(InputStream is) throws IOException {
		int num = 0;
		for (int i = 0; i < numOfBits; ++i) {
			int next = is.read();
			if (next < 0)
				return -1;
			num += next << i;
		}
		return num;
	}

	// We need to call the close() method of BitOutputStream,
	// but without closing the encompassing OutputStream
	private class UnClosedOutputStream extends FilterOutputStream {
		public UnClosedOutputStream(OutputStream os) {
			super(os);
		}

		public void write(byte b[], int off, int len) throws IOException {
			out.write(b, off, len);
		}

		// Does not close anything
		public void close() throws IOException {
		}
	}

	public void compress(InputStream is, OutputStream os) throws IOException {
		os = new BitOutputStream(new UnClosedOutputStream(os));
		int next; // next input character
		int code; // next code generated
		while ((next = is.read()) >= 0) {
			if (stopped)
				break;
			code = encodeOneChar(next);
			if (code >= 0)
				writeCode(os, code);
		}
		code = encodeLast();
		if (code >= 0)
			writeCode(os, code);
		os.close();
	}

	ByteArray decodeOne(int code) {
		// Either "ABA" or null, w="AB"
		ByteArray str = dict.strFromNum(code);
		if (str == null) {
			str = w.conc(w.getAt(0));
			dict.add(str);
		} else if (!w.isEmpty())
			dict.add(w.conc(str.getAt(0)));
		w = str;
		return w;
	}

	public void decompress(InputStream is, OutputStream os) throws IOException {
		is = new BitInputStream(is);
		ByteArray str; // Next entry
		int code; // Next code to be read
		while ((code = readCode(is)) >= 0) {
			if (stopped)
				break;
			str = decodeOne(code);
			os.write(str.getBytes());
		}
	}

	public void stop() {
		stopped = true;
	}

	public static void main(String args[]) {
		LZW lzw = new LZW();
		try {
			lzw.compress(new FileInputStream("D:/des_test/src/LZW/TEST.PNG"),
					new FileOutputStream("D:/des_test/src/LZW/test.lzw"));
			// lzw.decompress(new FileInputStream("D:/des_test/src/LZW/test.lzw"),new FileOutputStream("D:/des_test/src/LZW/TEST1.PNG"));
			// lzw.compress(new FileInputStream("LZW.JAVA"),new FileOutputStream("lzw.lzw"));
			// lzw.decompress(new FileInputStream("lzw.lzw"),new FileOutputStream("lzw1.java"));
		} catch (Exception e) {
		}
	}
}
