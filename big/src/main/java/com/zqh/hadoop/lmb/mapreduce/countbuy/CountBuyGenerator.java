package com.zqh.hadoop.lmb.mapreduce.countbuy;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.zqh.hadoop.lmb.mapreduce.Constant;

/**
 * Generate rating for each （userid, itemid）pair
 * First, we use map to get key, value pair (userid + itemid, type)
 * type: 0 click, 1 buy, 2 collect, 3 shopping cart
 * @author lgrcyanny
 *
 */
public class CountBuyGenerator {
	
	public CountBuyGenerator() {
		System.out.println("CountBuyGenerator");
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(CountBuyGenerator.class);
		job.setMapperClass(CountMapper.class);
		job.setReducerClass(CountReducer.class);
		job.setCombinerClass(CountCombiner.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class); // It's the map output
		
		FileInputFormat.addInputPath(job, new Path(Constant.FILEPATH_DATA_SOURCE));
		FileOutputFormat.setOutputPath(job, new Path(Constant.FILEPATH_COUNTBUY));
		
		long start = System.currentTimeMillis();
		boolean b = job.waitForCompletion(true);
		long end = System.currentTimeMillis();
		if (b) {
			System.out.println("Job done with time " + (end - start));
		} else {
			throw new IOException("Job exit with error.");
		}
		
	}

}
