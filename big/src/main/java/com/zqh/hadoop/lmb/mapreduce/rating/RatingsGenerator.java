package com.zqh.hadoop.lmb.mapreduce.rating;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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
public class RatingsGenerator {
	
	public RatingsGenerator() {
		System.out.println("RatingsGenerator");
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(RatingsGenerator.class);
		job.setMapperClass(RatingMapper.class);
		job.setReducerClass(RatingReducer.class);
		job.setCombinerClass(RatingCombiner.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(Constant.FILEPATH_DATA_SOURCE));
		FileOutputFormat.setOutputPath(job, new Path(Constant.FILEPATH_RATING));
		
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
