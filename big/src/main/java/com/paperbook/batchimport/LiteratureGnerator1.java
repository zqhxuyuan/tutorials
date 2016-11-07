package com.paperbook.batchimport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;


public class LiteratureGnerator1 {
	public static ArrayList<Word> dictionary = new ArrayList<Word>();
	public static ArrayList<String> publications = new ArrayList<String>();
	public static ArrayList<String> users = new ArrayList<String>();
	public ArrayList<Put> putlist = new ArrayList<Put>();
	public int dicStartIndex = 0;
	private int runTimeIndex = 0;
	
	public static class Word {
		public String value;
		public int count;
		public Word(String word) {
			this.value = word;
			this.count = 0;
		}		
	}
	
	public LiteratureGnerator1() {
		this.initDictionary();
		this.initPublications();
		this.initUsers();
	}
	
	public ArrayList<Put> createLiteratures(int number) {
		for (int i = 0; i < number; i++) {
			String title = getRandomTitle();
			String authors = getRandomAuthors();
			int year = getRandomYear();
			String publication = getRandomPublication();
			String userid = getRandomUser();
			Put put = new Put(Bytes.toBytes(md5(title)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(title));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("authors"), Bytes.toBytes(authors));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("year"), Bytes.toBytes(String.valueOf(year)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("publication"), Bytes.toBytes(publication));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("Category"), Bytes.toBytes("Journal"));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("user_id"), Bytes.toBytes(userid));
			putlist.add(put);
		}	
		return putlist;
	}
	
	public static int random(int m, int n) {
		return (int)(Math.random() * (n - m + 1) + m);
	}
	
	public static String md5(String str) {
		MessageDigest md;
		StringBuilder res = new StringBuilder();
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] data = md.digest();
			for (int i = 0; i < data.length; i++) {
				String hex = Integer.toHexString(data[i] & 0xff);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				res.append(hex);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res.toString();
	}
	
	public void testGetTitle() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			System.out.println(getRandomTitle());
		}
		long endTime = System.currentTimeMillis();
		System.out.println("The running time is = " + (endTime - startTime));
		int count = 0;;
		for (int i = 0; i < dictionary.size(); i++) {
			if (dictionary.get(i).count != 50) {
				System.out.println(dictionary.get(i).value + "=" + dictionary.get(i).count);
				count++;
			}
		}
		System.out.println("count = " + count);
	}
	
	private String getRandomPublication() {
		int index = random(0, 49);
		return publications.get(index);
	}
	
	private String getRandomAuthors() {
		int count = random(2, 3);
		StringBuilder res = new StringBuilder();
		while (count > 0) {
			int index = random(0, 1999);
			Word word = dictionary.get(index);
			res.append(word.value);
			if (count > 1) {
				res.append(" ");
			}
			count--;
		}
		return res.toString();
	}
	
	private int getRandomYear() {
		return random(1900, 2014);
	}
	
	private String getRandomTitle () {
		StringBuilder res = new StringBuilder();
		int count = random(2, 10);
		while (count > 0) {
			if (runTimeIndex >= dictionary.size()) {
				dictionary.add(dictionary.remove(dicStartIndex));
				runTimeIndex = dicStartIndex;
			}
			//System.out.println("dicStartIndex = "+ dicStartIndex);
//			if (dictionary.get(runTimeIndex).count >= 50) {
//				runTimeIndex++;
//			} else {
				res.append(dictionary.get(runTimeIndex).value + " ");
				dictionary.get(runTimeIndex).count++;
				count--;
				runTimeIndex++;
//			}			
		}
		
		return res.toString().trim();
	}
	
	private String getRandomUser() {
		int index = random(0, users.size() - 1);
		return users.get(index);
	}	
	
	private void initDictionary() {
		try {
			BufferedReader buffer = new BufferedReader(new FileReader("resources/words.txt"));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				dictionary.add(new Word(line));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initPublications() {
		try {
			BufferedReader buffer = new BufferedReader(new FileReader("resources/publications.txt"));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				publications.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initUsers() {
		users.add("5454ab5e37b70e000555ef593b707c3");
		users.add("6d2734d50bcaa510ebb06497b7c96c61");
		users.add("798b9cb0913098f6bb9c317e8c439c86");
		users.add("a561894f76fd1090b5aa94281d6d01f2");
		users.add("e3e389aeb31ddf6881c3f95d03f8fe37");
		users.add("fa4ec0d6e8371bee67327c67413c9983");
	}
	
	public static void main(String[] args) throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HTableInterface literaturesTable = new HTable(conf, "pb_literatures");
		LiteratureGnerator1 generator = new LiteratureGnerator1();
		long startTime = System.currentTimeMillis();
		ArrayList<Put> putList = generator.createLiteratures(20000);
		literaturesTable.put(putList);
		long endTime = System.currentTimeMillis();
		literaturesTable.close();
		System.out.println("Running time is " + (endTime - startTime));	
//		new LiteratureGnerator().testGetTitle();
		
		//System.out.println(new Integer(3).hashCode());
	}
}
