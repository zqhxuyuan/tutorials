/**
 * 
 */
package com.github.NoahShen.jue.compression.gzip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.github.NoahShen.jue.compression.DataCompress;

/**
 * @author noah
 *
 */
public class GZipDataCompress implements DataCompress {

	@Override
	public byte[] compress(byte[] data) throws Exception {
		GZIPOutputStream gzipos = null;
		try {
			ByteArrayOutputStream byteos = new ByteArrayOutputStream(data.length / 2);
			gzipos = new GZIPOutputStream(byteos);
			gzipos.write(data);
			gzipos.finish();
			gzipos.flush();
			return byteos.toByteArray();
		} finally {
			try {
				if (gzipos != null) {
					gzipos.close();
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public byte[] decompress(byte[] data) throws Exception {
		GZIPInputStream gzipis = null;
		ByteArrayOutputStream byteos = null;
		try {
			gzipis = new GZIPInputStream(new ByteArrayInputStream(data));
			byte[] b = new byte[data.length * 2];
			byteos = new ByteArrayOutputStream(data.length * 2);
			int i = 0;
			while ((i = gzipis.read(b)) != -1) {
				byteos.write(b, 0, i);
			}
			return byteos.toByteArray();
		} finally {
			try {
				if (gzipis != null) {
					gzipis.close();
				}
				if (byteos != null) {
					byteos.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
