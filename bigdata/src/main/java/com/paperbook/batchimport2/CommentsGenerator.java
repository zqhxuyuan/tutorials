package com.paperbook.batchimport2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;


public class CommentsGenerator {
	public ArrayList<Word> dictionary = null;
	public ArrayList<User> userlist = null;
	public ArrayList<Literature> literatures = null;
		
	public CommentsGenerator(ArrayList<Word> dictionary,
			ArrayList<User> userlist, ArrayList<Literature> literatures) {
		this.dictionary = dictionary;
		this.userlist = userlist;
		this.literatures = literatures;
	}

	public ArrayList<Put> createComments() {
		ArrayList<Put> res = new ArrayList<Put>(300000);
		for (int i = 0; i < literatures.size(); i++) {
			Literature literature = literatures.get(i);
			int count = getRandomCount();
			for (int j = 0; j < count; j++) {
				String user = getRandomUser();				
				long timestamp = getRandomTimeStamp(literature.timestamp);
				String rowkey = Utils.md5(literature.rowkey + System.currentTimeMillis() + user);
				Put put = new Put(Bytes.toBytes(rowkey));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("user"), timestamp, Bytes.toBytes(user));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("literature"), timestamp, Bytes.toBytes(literature.rowkey));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("content"), timestamp, Bytes.toBytes("Comment by " + user));
				res.add(put);
			}			
		}
		return res;
	}
	
	private long getRandomTimeStamp(long timestamp) {
		return (long) (Math.random() * (System.currentTimeMillis() - timestamp)) + timestamp;
	}
		
	public String getRandomUser() {
		int index = Utils.random(0, userlist.size() - 1);
		return userlist.get(index).name;
	}

	public int getRandomCount () {
		return Utils.random(2, 5);
	}
		
}
