/**
 * 
 */
package com.github.NoahShen.jue.file;

import java.util.zip.CRC32;
import java.util.zip.Checksum;


/**
 * CRC32 算法的校验码生成器
 * @see <a>java.util.zip.CRC32</a>
 * @author noah
 *
 */
public class CRC32ChecksumGenerator implements ChecksumGenerator {
	
	@Override
	public Checksum createChecksum() {
		return new CRC32();
	}

}
