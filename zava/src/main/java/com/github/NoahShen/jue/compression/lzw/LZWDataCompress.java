/**
 * 
 */
package com.github.NoahShen.jue.compression.lzw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.github.NoahShen.jue.compression.DataCompress;

/**
 * @author noah
 *
 */
public class LZWDataCompress implements DataCompress {

	private final LZW lzw;
	
	public LZWDataCompress() {
		lzw = new LZW();
	}

	@Override
	public byte[] compress(byte[] data) throws IOException {
		ByteArrayInputStream byteis = new ByteArrayInputStream(data);
		ByteArrayOutputStream byteos = new ByteArrayOutputStream(data.length / 2);
		lzw.compress(byteis, byteos);
		return byteos.toByteArray();
	}

	@Override
	public byte[] decompress(byte[] data) throws IOException {
		ByteArrayInputStream byteis = new ByteArrayInputStream(data);
		ByteArrayOutputStream byteos = new ByteArrayOutputStream(data.length * 2);
		lzw.decompress(byteis, byteos);
		return byteos.toByteArray();
	}

}
