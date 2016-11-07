package com.paperbook.batchimport2;


public class Literature {
	public String rowkey;
	public String title;
	public String authors;
	public int year;
	public String publication;
	public String user;
	public long timestamp;
	
	public Literature(String rowkey, String title, String authors, int year,
			String publication, String user, long timestamp) {
		this.rowkey = rowkey;
		this.title = title;
		this.authors = authors;
		this.year = year;
		this.publication = publication;
		this.user = user;
		this.timestamp = timestamp;
	}	
}
