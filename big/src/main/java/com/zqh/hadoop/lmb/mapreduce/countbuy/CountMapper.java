package com.zqh.hadoop.lmb.mapreduce.countbuy;

import java.io.IOException;

import com.zqh.hadoop.lmb.mapreduce.Constant;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Input: [line offset, line of text]
 * Output: [userid + "," + "itemid", 1], when the type is Buy
 * @author lgrcyanny
 *
 */
public class CountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	private static final IntWritable ONE = new IntWritable(1);
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] items = value.toString().split(",");
		String outputKey = items[0] + "," + items[1];
		int type = Integer.valueOf(items[2]);
		if (type == Constant.BUY) {
			context.write(new Text(outputKey), new IntWritable(1));
		}		
	}
}