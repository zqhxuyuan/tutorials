package com.paperbook.mapreduce.stat.literatures;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.paperbook.mapreduce.invertedindex.InvertedIndexTitle.IndexMapper;
import com.paperbook.mapreduce.stat.comments.CommentsCount.CountReducer;

/**
 * Count repeat literatures
 * @author lgrcyanny
 *
 */
public class LiteratureRepeatCount {
	
	
	public static class CountMapper extends TableMapper<Text, IntWritable> {

		@Override
		protected void map(ImmutableBytesWritable key, Result rs,
				Context context) throws IOException, InterruptedException {
			String literature = Bytes.toString(rs.getValue(Bytes.toBytes("info"), Bytes.toBytes("title")));
			context.write(new Text(literature), new IntWritable(1));
		}
		
	}
	
	
	public static class CountCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<IntWritable> items,
				Context context)
				throws IOException, InterruptedException {
			int count = 0;
			Iterator<IntWritable> iterator = items.iterator();
			while (iterator.hasNext()) {
				count = count + iterator.next().get();
			}		
			context.write(key, new IntWritable(count));
		}		
	}
	
	public static class CountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<IntWritable> items,
				Context context)
				throws IOException, InterruptedException {
			int count = 0;
			Iterator<IntWritable> iterator = items.iterator();
			while (iterator.hasNext()) {
				count = count + iterator.next().get();
			}	
			if (count > 1) {
				context.write(key, new IntWritable(count));
			}			
		}		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = HBaseConfiguration.create();
		Job job = new Job(conf, "pb_mr_literatures_repeat_count");
		job.setJarByClass(LiteratureRepeatCount.class);
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes("info"),
				Bytes.toBytes("title"));
		scan.setCaching(5000); // Default is 1, set 5000 improve performance
		scan.setCacheBlocks(false); // Close block cache for MR job
		TableMapReduceUtil.initTableMapperJob("pb_mr_literatures_repeat",
				scan, CountMapper.class, Text.class,
				IntWritable.class, job);
		job.setCombinerClass(CountCombiner.class);
		job.setReducerClass(CountReducer.class);
		job.setNumReduceTasks(1);  // At least one reducer, adjust as required
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileOutputFormat.setOutputPath(job, new Path("literatures-repeat-res"));
		
		long start = System.currentTimeMillis();
		boolean res = job.waitForCompletion(true);
		long end = System.currentTimeMillis();
		if (res) {
			System.out.println("Job done with time " + (end - start));
		} else {
			throw new IOException("Job exit with error.");
		}
	}

}
