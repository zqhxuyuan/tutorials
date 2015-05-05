/**
 * 
 */
package com.github.NoahShen.jue.file;

/**
 * 文件头信息
 * @author noah
 *
 */
public class FileHeader implements ADrop {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2193314360648043655L;
	
	/**
	 * FileHeader的长度，不足部分用0填充
	 */
	public static final int HEADER_SIZE = 4096;
	
	/**
	 * 文件尾的位置
	 */
	private long fileTail = -1;
	
	/**
	 * key的B+树的最小关键字数
	 */
	private int keyTreeMin;
	
	/**
	 * value的版本树的最小关键字数
	 */
	private int valueRevTreeMin;
	
	/**
	 * Value是否压缩
	 */
	private byte valueCompressed;
	
	/**
	 * 采用的压缩编码
	 */
	private byte compressionCodec;
	
	/**
	 * 块大小
	 */
	private int blockSize;

	public FileHeader() {
		super();
	}

	public FileHeader(int keyTreeMin, int valueRevTreeMin, byte valueCompressed, byte compressionCodec, int blockSize) {
		super();
		this.keyTreeMin = keyTreeMin;
		this.valueRevTreeMin = valueRevTreeMin;
		this.valueCompressed = valueCompressed;
		this.compressionCodec = compressionCodec;
		this.blockSize = blockSize;
	}

	public long getFileTail() {
		return fileTail;
	}

	public void setFileTail(long fileTail) {
		this.fileTail = fileTail;
	}

	public int getKeyTreeMin() {
		return keyTreeMin;
	}

	public void setKeyTreeMin(int keyTreeMin) {
		this.keyTreeMin = keyTreeMin;
	}

	public int getValueRevTreeMin() {
		return valueRevTreeMin;
	}

	public void setValueRevTreeMin(int valueRevTreeMin) {
		this.valueRevTreeMin = valueRevTreeMin;
	}

	public byte getValueCompressed() {
		return valueCompressed;
	}

	public void setValueCompressed(byte valueCompressed) {
		this.valueCompressed = valueCompressed;
	}

	public byte getCompressionCodec() {
		return compressionCodec;
	}

	public void setCompressionCodec(byte compressionCodec) {
		this.compressionCodec = compressionCodec;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	@Override
	public String toString() {
		return "FileHeader [fileTail=" + fileTail + ", keyTreeMin="
				+ keyTreeMin + ", valueRevTreeMin=" + valueRevTreeMin
				+ ", valueCompressed=" + valueCompressed
				+ ", compressionCodec=" + compressionCodec + ", blockSize="
				+ blockSize + "]";
	}


	
}
