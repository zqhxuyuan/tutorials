package com.zqh.hadoop.lmb.mapreduce.rating;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Input: [line offset, line of text]
 * Output: [userid + "," + "itemid", "type" + "," + "1"]
 * @author lgrcyanny
 *
 */
public class RatingMapper extends Mapper<LongWritable, Text, Text, Text> {

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] items = value.toString().split(",");
		String outputKey = items[0] + "," + items[1];
		String outputValue = items[2] + "," + "1";
		context.write(new Text(outputKey), new Text(outputValue));
	}
}