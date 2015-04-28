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

public class CommentsStat {
	
	public static class StatMapper extends TableMapper<Text, LongWritable> {

		@Override
		protected void map(ImmutableBytesWritable key, Result rs,
				Context context) throws IOException, InterruptedException {
			String user = Bytes.toString(rs.getValue(Bytes.toBytes("info"), Bytes.toBytes("user")));
			long timestamp = rs.getColumnLatestCell(Bytes.toBytes("info"), Bytes.toBytes("user")).getTimestamp();
			context.write(new Text(user), new LongWritable(timestamp));
		}
		
	}
	
	public static class StatReducer extends TableReducer<Text, LongWritable, ImmutableBytesWritable> {

		@Override
		protected void reduce(Text user, Iterable<LongWritable> timestamps,
				Context context)
				throws IOException, InterruptedException {
			int countWeek = 0;
			int countMonth = 0;
			int countHalfYear = 0;
			int countYear = 0;
			int countTotal = 0;
			long tsWeek = System.currentTimeMillis() - (long) 3600 * 1000 * 24 * 7;
			long tsMonth = System.currentTimeMillis() - (long) 3600 * 1000 * 24 * 30;
			long tsYear = System.currentTimeMillis() - (long) 3600 * 1000 * 24 * 365;
			long tsHalftYear = System.currentTimeMillis() - (long) 3600 * 1000 * 24 * 182;
			
			Iterator<LongWritable> iterator = timestamps.iterator();
			while (iterator.hasNext()) {
				long time = iterator.next().get();
				countTotal++;
				if (time > tsYear) {
					countYear++;
				}
				if (time > tsHalftYear) {
					countHalfYear++;
				}
				if (time > tsMonth) {
					countMonth++;
				}
				if (time > tsWeek) {
					countWeek++;
				}
			}
			
			Put put = new Put(Bytes.toBytes(user.toString()));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("week"), Bytes.toBytes(String.valueOf(countWeek)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("month"), Bytes.toBytes(String.valueOf(countMonth)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("year"), Bytes.toBytes(String.valueOf(countYear)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("halfyear"), Bytes.toBytes(String.valueOf(countHalfYear)));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("total"), Bytes.toBytes(String.valueOf(countTotal)));
			
			context.write(null, put);
		
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = HBaseConfiguration.create();
		Job job = new Job(conf, "pb_mr_comments");
		job.setJarByClass(CommentsStat.class);
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes("info"),
				Bytes.toBytes("user"));
		scan.setCaching(5000); // Default is 1, set 500 improve performance
		scan.setCacheBlocks(false); // Close block cache for MR job
		TableMapReduceUtil.initTableMapperJob("pb_mr_comments",
				scan, StatMapper.class, Text.class,
				LongWritable.class, job);
		TableMapReduceUtil.initTableReducerJob("pb_stat_comments",
				StatReducer.class, job);
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
