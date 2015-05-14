package com.zqh.hadoop.lmb.recommendations;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.zqh.hadoop.lmb.mapreduce.Constant;

public class RecommendationsGenerator {
	
	public static void batchWriteToFile() throws Exception {
		RepeatBuyRecommender repeatBuyRecommender = new RepeatBuyRecommender();
		HashMap<Long, String> buyRecords = repeatBuyRecommender.getBuyRecords();
		UserBasedRecommender userBasedRecommender = new UserBasedRecommender();
		ArrayList<String> recommendations = userBasedRecommender.generateRecommendations();
		PrintWriter writer = new PrintWriter(Constant.FILEPATH_RECOMMENDATIONS_OUTPUT, "utf-8");
		for (int i = 0; i < recommendations.size(); i++) {
			String item = recommendations.get(i);
			String[] temp = item.split("\t"); // temp[0] is userid, temp[1] is itemids
			long userid = Long.valueOf(temp[0]);
			String buyItem = buyRecords.get(userid);
			if (temp.length > 1 && temp[1].length() > 1) {
				if (buyItem != null) {
					writer.println(item + "," + buyItem);
				} else {
					writer.println(item);
				}
			}
		}
		writer.close();
	}
	
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			batchWriteToFile();
			long end = System.currentTimeMillis();
			System.out.println("Job done with time " + (end - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
