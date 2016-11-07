package com.zqh.hadoop.lmb.recommendations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import com.zqh.hadoop.lmb.mapreduce.Constant;

public class RepeatBuyRecommender {
	private HashMap<Long, String> buyRecords = new HashMap<Long, String>();
	private final static String FILEPATH = Constant.FILEPATH_COUNTBUY + "/"
			+ Constant.MR_OUTPUT_FILENAME;

	public RepeatBuyRecommender() {

		this.loadDataFromFile();

	}

	public HashMap<Long, String> getBuyRecords() {
		return buyRecords;
	}

	private void loadDataFromFile() {
		System.out.println("Start load buy records form file");
		long start = System.currentTimeMillis();
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(FILEPATH));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				String[] items = line.split(",");
				long userid = Long.valueOf(items[0]);
				String itemid = items[1];
				// Get the current items in HashMap
				String currentItems = buyRecords.get(userid);
				if (currentItems != null) {
					buyRecords.put(userid, currentItems + "," + itemid);
				} else {
					buyRecords.put(userid, itemid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("Loaded buy records from file, number = " + buyRecords.size() + " with time = " + (end - start));
	}

	public static void main(String[] args) {
		new RepeatBuyRecommender();
	}

}
