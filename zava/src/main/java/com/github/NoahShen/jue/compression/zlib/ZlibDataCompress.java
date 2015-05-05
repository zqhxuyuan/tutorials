/**
 * 
 */
package com.github.NoahShen.jue.compression.zlib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.github.NoahShen.jue.compression.DataCompress;

/**
 * @author noah
 *
 */
public class ZlibDataCompress implements DataCompress {

	@Override
	public byte[] compress(byte[] data) throws Exception {
		DeflaterOutputStream zos = null;
		try {
			ByteArrayOutputStream byteos = new ByteArrayOutputStream(data.length / 2);
			zos = new DeflaterOutputStream(byteos);
			zos.write(data);
			zos.finish();
			zos.flush();
			return byteos.toByteArray();
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/* (non-Javadoc)
	 * @see compression.StringCompress#decompress(byte[])
	 */
	@Override
	public byte[] decompress(byte[] data) throws Exception {
		InflaterInputStream zlibis = null;
		ByteArrayOutputStream byteos = null;
		try {
			zlibis = new InflaterInputStream(new ByteArrayInputStream(data));
			byte[] b = new byte[data.length * 2];
			byteos = new ByteArrayOutputStream(data.length * 2);
			int i = 0;
			while ((i = zlibis.read(b)) != -1) {
				byteos.write(b, 0, i);
			}
			return byteos.toByteArray();
		} finally {
			try {
				if (zlibis != null) {
					zlibis.close();
				}
				if (byteos != null) {
					byteos.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
