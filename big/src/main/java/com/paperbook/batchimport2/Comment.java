package com.paperbook.batchimport2;

public class Comment {
	public String rowkey;
	public String user;
	public String content;
	public long timestamp;
	
	public Comment(String rowkey, String user, String content, long timestamp) {
		this.rowkey = rowkey;
		this.user = user;
		this.content = content;
		this.timestamp = timestamp;
	}	

}
