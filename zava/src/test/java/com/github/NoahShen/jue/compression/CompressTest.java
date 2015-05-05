/**
 * 
 */
package com.github.NoahShen.jue.compression;

import org.junit.Before;
import org.junit.Test;

import com.github.NoahShen.jue.compression.gzip.GZipDataCompress;
import com.github.NoahShen.jue.compression.lzw.LZWDataCompress;
import com.github.NoahShen.jue.compression.quicklz.QuickLZDataCompress;
import com.github.NoahShen.jue.compression.zlib.ZlibDataCompress;

/**
 * @author noah
 *
 */
public class CompressTest {

	private static final String COMPRESS_MSG_FORMAT = "compress method: %s\ntime esaped %d\ncompressed size: %d";
	
	private static final String DECOMPRESS_MSG_FORMAT = "decompress: time esaped %d\n=================";
	
	private int times;
	
	private byte[] testData;
	
	@Before
	public void creatTestData() throws Exception {
		int len = 1 << 16; // 65536
		String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; ++i) {
			sb.append(s.charAt(i % s.length()));
		}
		testData = sb.toString().getBytes("UTF-8");
		times = 100;
	}
	
	@Test
	public void testLZW() throws Exception {
		LZWDataCompress lzw = new LZWDataCompress();
		CompressResult result = doCompress(lzw, testData, times);
		System.out.println(String.format(COMPRESS_MSG_FORMAT, "LZW", result.escaped, result.compressedSize));
		long escaped = doDecompress(lzw, testData, times);
		System.out.println(String.format(DECOMPRESS_MSG_FORMAT, escaped, times));
	}
	
	@Test
	public void testGzip() throws Exception {
		GZipDataCompress gzip = new GZipDataCompress();
		CompressResult result = doCompress(gzip, testData, times);
		System.out.println(String.format(COMPRESS_MSG_FORMAT, "Gzip", result.escaped, result.compressedSize));
		long escaped = doDecompress(gzip, testData, times);
		System.out.println(String.format(DECOMPRESS_MSG_FORMAT, escaped, times));
	}
	
	@Test
	public void testZlib() throws Exception {
		ZlibDataCompress zip = new ZlibDataCompress();
		CompressResult result = doCompress(zip, testData, times);
		System.out.println(String.format(COMPRESS_MSG_FORMAT, "Zlib", result.escaped, result.compressedSize));
		long escaped = doDecompress(zip, testData, times);
		System.out.println(String.format(DECOMPRESS_MSG_FORMAT, escaped, times));
	}

	@Test
	public void testQuickLZ() throws Exception {
		QuickLZDataCompress quickLZ = new QuickLZDataCompress();
		CompressResult result = doCompress(quickLZ, testData, times);
		System.out.println(String.format(COMPRESS_MSG_FORMAT, "QuickLZ", result.escaped, result.compressedSize));
		long escaped = doDecompress(quickLZ, testData, times);
		System.out.println(String.format(DECOMPRESS_MSG_FORMAT, escaped, times));
	}

	
	private long doDecompress(DataCompress compress, byte[] data, int times) throws Exception {
		byte[] b = compress.compress(data);
		// 预热
		for (int i = 0; i < times; ++i) {
			compress.decompress(b);
		}
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < times; ++i) {
			compress.decompress(b);
		}
		long escaped = System.currentTimeMillis() - startTime;
		return escaped;
	}


	private CompressResult doCompress(DataCompress compress, byte[] data, int times) throws Exception {
		// 预热
		for (int i = 0; i < times; ++i) {
			compress.compress(data);
		}
		long startTime = System.currentTimeMillis();
		byte[] b = null;
		for (int i = 0; i < times; ++i) {
			b = compress.compress(data);
		}
		long escaped = System.currentTimeMillis() - startTime;
		CompressResult result = new CompressResult(escaped, b.length);
		return result;
	}
	
	private class CompressResult {
		public long escaped;
		public long compressedSize;
		
		public CompressResult(long escaped, long compressedSize) {
			this.escaped = escaped;
			this.compressedSize = compressedSize;
		}
	}
}
