package com.zqh.hadoop.lmb.mapreduce.rating;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class RatingCombiner extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Context context)
			throws IOException, InterruptedException {
		// countTypes[0] for click, countTypes[1] for buy, 
		// countTypes[2] for collect, countTypes[3] for cart 
		int[] countTypes = new int[4]; 
		Iterator<Text> iterator = values.iterator();
		while (iterator.hasNext()) {
			String[] items = iterator.next().toString().split(",");
			int type = Integer.valueOf(items[0]);
			int count = Integer.valueOf(items[1]);
			countTypes[type] = countTypes[type] + count;
		}
		for (int i = 0; i < countTypes.length; i++) {
			context.write(key, new Text(i + "," + countTypes[i]));
		}
	}		
}
