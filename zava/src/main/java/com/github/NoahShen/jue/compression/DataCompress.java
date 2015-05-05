/**
 * 
 */
package com.github.NoahShen.jue.compression;

import java.io.IOException;

/**
 * 数据压缩接口
 * @author noah
 *
 */
public interface DataCompress {
	/**
	 * 压缩
	 * @param data
	 * @return
	 * @throws Exception
	 */
	byte[] compress(byte[] data) throws Exception;
	
	/**
	 * 解压缩
	 * @param data
	 * @return
	 * @throws IOException 
	 */
	byte[] decompress(byte[] data) throws Exception;
}
