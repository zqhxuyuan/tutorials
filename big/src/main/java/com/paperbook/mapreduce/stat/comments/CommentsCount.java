package com.paperbook.mapreduce.stat.comments;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;

import com.paperbook.mapreduce.invertedindex.InvertedIndexTitle.IndexMapper;

/**
 * Count comments for each literatures
 * @author lgrcyanny
 *
 */
public class CommentsCount {
	
	
	public static class CountMapper extends TableMapper<Text, Text> {

		@Override
		protected void map(ImmutableBytesWritable key, Result rs,
				Context context) throws IOException, InterruptedException {
			String literature = Bytes.toString(rs.getValue(Bytes.toBytes("info"), Bytes.toBytes("literature")));
			long timestamp = rs.getColumnLatestCell(Bytes.toBytes("info"), Bytes.toBytes("literature")).getTimestamp();
			context.write(new Text(literature), new Text(1 + "," + timestamp));
		}
		
	}
	
	public static class CountCombiner extends Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text literature, Iterable<Text> countItems,
				Context context)
				throws IOException, InterruptedException {
			int count = 0;
			long timestamp = 0;
			Iterator<Text> iterator = countItems.iterator();
			while (iterator.hasNext()) {
				Text item = iterator.next();
				String[] str = item.toString().split(",");
				int itemCount = Integer.valueOf(str[0]);
				long itemTimeStamp = Long.valueOf(str[1]);
				count = count + itemCount;
				timestamp = timestamp + itemCount * itemTimeStamp;
			}
			timestamp = (long) timestamp / count;
			context.write(literature, new Text(count + "," + timestamp));
		}		
	}
	
	public static class CountReducer extends TableReducer<Text, Text, ImmutableBytesWritable> {

		@Override
		protected void reduce(Text literature, Iterable<Text> countItems,
				Context context)
				throws IOException, InterruptedException {
			int count = 0;
			long timestamp = 0;
			Iterator<Text> iterator = countItems.iterator();
			while (iterator.hasNext()) {
				Text item = iterator.next();
				String[] str = item.toString().split(",");
				int itemCount = Integer.valueOf(str[0]);
				long itemTimeStamp = Long.valueOf(str[1]);
				count = count + itemCount;
				timestamp = timestamp + itemCount * itemTimeStamp;
			}
			timestamp = (long) timestamp / count;
			
			Put put = new Put(Bytes.toBytes(literature.toString()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("count"), Bytes.toBytes(String.valueOf(count)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("avgts"), Bytes.toBytes(String.valueOf(timestamp)));
			
			context.write(null, put);			
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = HBaseConfiguration.create();
		Job job = new Job(conf, "pb_stat_comments_count");
		job.setJarByClass(CommentsCount.class);
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes("info"),
				Bytes.toBytes("literature"));
		scan.setCaching(5000); // Default is 1, set 5000 improve performance
		scan.setCacheBlocks(false); // Close block cache for MR job
		TableMapReduceUtil.initTableMapperJob("pb_mr_comments",
				scan, CountMapper.class, Text.class,
				Text.class, job);
		job.setCombinerClass(CountCombiner.class);
		TableMapReduceUtil.initTableReducerJob("pb_stat_comments_count",
				CountReducer.class, job);
		job.setNumReduceTasks(1);  // At least one reducer, adjust as required
		
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
