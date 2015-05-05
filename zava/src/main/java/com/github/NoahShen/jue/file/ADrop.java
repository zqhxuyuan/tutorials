/**
 * 
 */
package com.github.NoahShen.jue.file;

import java.io.Serializable;

/**
 * 文件中的存储对象的统一借口
 * @author noah
 *
 */
public interface ADrop extends Serializable {

	/**
	 * 代表true值
	 */
	public static final byte TRUE_BYTE = 0x1;
	
	/**
	 * 代表false值
	 */
	public static final byte FALSE_BYTE = 0x0;
}
