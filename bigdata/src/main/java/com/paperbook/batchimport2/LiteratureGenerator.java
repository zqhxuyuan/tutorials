package com.paperbook.batchimport2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;



public class LiteratureGenerator {
	public ArrayList<Word> dictionary = new ArrayList<Word>();
	public ArrayList<String> publications = new ArrayList<String>();
	public ArrayList<User> userlist = new ArrayList<User>();
	public ArrayList<Literature> literatures = new ArrayList<Literature>(100000);
	private int dicRunTimeIndex = 0; // Index for random title, record the run time index
	private int userlistIndex = 0; // Index for random user
	
	
	public LiteratureGenerator() {
		this.initDictionary();
		this.initPublications();
		this.initUserList();
	}
	
	public void createLiteratures(int number) {
		for (int i = 0; i < number; i++) {
			String title = getRandomTitle();
			String authors = getRandomAuthors();
			int year = getRandomYear();
			String publication = getRandomPublication();
			String user = getRandomUser();
			long timestamp = Utils.getRandomTimeStamp(year);
			String rowkey = Utils.md5(title + System.currentTimeMillis());
			Literature literature = new Literature(rowkey, title, authors, year, publication, user, timestamp);
			literatures.add(literature);
		}	
	}
	
	public ArrayList<Put> createPutList () {
		ArrayList<Put> putlist = new ArrayList<Put>(100000);
		for (int i = 0; i < literatures.size(); i++) {
			Literature item = literatures.get(i);
			Put put = new Put(Bytes.toBytes(item.rowkey));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("title"), item.timestamp, Bytes.toBytes(item.title));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("authors"), item.timestamp, Bytes.toBytes(item.authors));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("year"), item.timestamp, Bytes.toBytes(String.valueOf(item.year)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("publication"), item.timestamp, Bytes.toBytes(item.publication));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("user"), item.timestamp, Bytes.toBytes(item.user));
			putlist.add(put);
		}	
		return putlist;
	}
	
	public ArrayList<Put> createRandomRepeatPutList () {
		ArrayList<Put> res = new ArrayList<Put>(300);
		int i = Utils.random(0, literatures.size());
		int totalcount = 100;
		while (totalcount > 0) {
			if (i == literatures.size()) {
				i = 0;
			}
			Literature item = literatures.get(i);
			int count = Utils.random(2, 5);
			for (int j = 0; j < count; j++) {
				long timestamp = Utils.getRandomTimeStamp(item.year);
				Put put = new Put(Bytes.toBytes(Utils.md5(item.title + System.currentTimeMillis() + timestamp)));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("title"), timestamp, Bytes.toBytes(item.title));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("authors"), timestamp, Bytes.toBytes(item.authors));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("year"), timestamp, Bytes.toBytes(String.valueOf(item.year)));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("publication"), timestamp, Bytes.toBytes(item.publication));
				put.add(Bytes.toBytes("info"), Bytes.toBytes("user"), timestamp, Bytes.toBytes(item.user));
				res.add(put);
			}
			i++;
			totalcount--;
		}
		return res;
	}
	
	
	public void testGetTitle() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			System.out.println(getRandomTitle());
		}
		long endTime = System.currentTimeMillis();
		
		int count = 0;;
		for (int i = 0; i < dictionary.size(); i++) {
			if (dictionary.get(i).count != 50) {
				System.out.println(dictionary.get(i).value + "=" + dictionary.get(i).count);
				count++;
			}
		}
		System.out.println("count = " + count);
		System.out.println("The running time is = " + (endTime - startTime));
	}
	
	private String getRandomPublication() {
		int index = Utils.random(0, 49);
		return publications.get(index);
	}
	
	private String getRandomAuthors() {
		int count = Utils.random(2, 3);
		StringBuilder res = new StringBuilder();
		while (count > 0) {
			int index = Utils.random(0, 1999);
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
		return Utils.random(1970, 2014);
	}
	
	private String getRandomTitle() {
		StringBuilder res = new StringBuilder();
		int count = Utils.random(2, 10);
		while (count > 0) {
			if (dicRunTimeIndex >= dictionary.size()) {
				dictionary.add(dictionary.remove(0)); // Add the first element to last, avoid title repetition
				dicRunTimeIndex = 0;
			}
			res.append(dictionary.get(dicRunTimeIndex).value + " ");
			dictionary.get(dicRunTimeIndex).count++;
			count--;
			dicRunTimeIndex++;
		}

		return res.toString().trim();
	}
	
	private String getRandomUser() {
		String res = null;
		while (res == null) {
			if (userlistIndex >= userlist.size()) {
				userlistIndex = 0;
				for (User user: userlist) {
					user.count = 0;
				}
			}
			if (userlist.get(userlistIndex).count <1000) {
				res = userlist.get(userlistIndex).name;
				userlist.get(userlistIndex).count++;
			} else {
				userlistIndex++;
			}
		}
		return res;		
	}	
	
	
	private void initDictionary() {
		try {
			BufferedReader buffer = new BufferedReader(new FileReader("resources/words.txt"));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				dictionary.add(new Word(line));
			}
		} catch (Exception e) {
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
	
	private void initUserList() {
		try {
			BufferedReader buffer = new BufferedReader(new FileReader("resources/names.txt"));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				userlist.add(new User(line.trim()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		GregorianCalendar gc = new GregorianCalendar(1989, 11, 30);
		System.out.println(gc.getTimeInMillis());
	}
	
}
