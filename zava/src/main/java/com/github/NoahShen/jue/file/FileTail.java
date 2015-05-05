/**
 * 
 */
package com.github.NoahShen.jue.file;

/**
 * 文件尾信息
 * @author noah
 *
 */
public class FileTail implements ADrop {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5378605477255397823L;
	
	/**
	 * FileTail的长度，24个字节
	 */
	public static final int TAIL_LENGHT = 28;
	
	/**
	 * 文件版本号
	 */
	private int revision;
	
	/**
	 * Key的B+树的根节点地址
	 */
	private long rootNode;
	
	/**
	 * 主键key的平均长度
	 */
    private int avgKeyLen;
    
    /**
     * 值Value的平均长度
     */
    private int avgValueLen;
    
    /**
     * K-V 对的数量
     */
    private long entryCount;

	public FileTail() {
		super();
	}

	public FileTail(int revision, long rootNode, int avgKeyLen, int avgValueLen, long entryCount) {
		super();
		this.revision = revision;
		this.rootNode = rootNode;
		this.avgKeyLen = avgKeyLen;
		this.avgValueLen = avgValueLen;
		this.entryCount = entryCount;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public long getRootNode() {
		return rootNode;
	}

	public void setRootNode(long rootNode) {
		this.rootNode = rootNode;
	}

	public int getAvgKeyLen() {
		return avgKeyLen;
	}

	public void setAvgKeyLen(int avgKeyLen) {
		this.avgKeyLen = avgKeyLen;
	}

	public int getAvgValueLen() {
		return avgValueLen;
	}

	public void setAvgValueLen(int avgValueLen) {
		this.avgValueLen = avgValueLen;
	}

	public long getEntryCount() {
		return entryCount;
	}

	public void setEntryCount(long entryCount) {
		this.entryCount = entryCount;
	}

	@Override
	public String toString() {
		return "FileTail [revision=" + revision + ", rootNode=" + rootNode
				+ ", avgKeyLen=" + avgKeyLen + ", avgValueLen=" + avgValueLen
				+ ", entryCount=" + entryCount + "]";
	}
    
    
}
