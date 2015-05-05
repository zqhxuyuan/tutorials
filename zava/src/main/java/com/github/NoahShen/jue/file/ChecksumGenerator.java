package com.github.NoahShen.jue.file;

import java.util.zip.Checksum;

/**
 * 校验码生成器
 * @author noah
 *
 */
public interface ChecksumGenerator {
	
	/**
	 * 生成校验码算法
	 * @return
	 */
	Checksum createChecksum();
}
