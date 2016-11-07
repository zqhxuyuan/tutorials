package com.zqh.hadoop.lmb.mapreduce.rating;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class RatingReducer extends Reducer<Text, Text, Text, Text> {

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
		double A = countTypes[1] + 0.3 * countTypes[3];
		double B = 0.611 * A + 0.124 * countTypes[0];
		double C = countTypes[0];
		double D = 0.263 * countTypes[0] + 0.399 * countTypes[2]; 
		double Y = 0.268 * A + 0.49 * B + 0.064 * C + 0.56 * D;
		String outputValue = key.toString() + "," + Y;
		context.write(null, new Text(outputValue));		
	}		
}
