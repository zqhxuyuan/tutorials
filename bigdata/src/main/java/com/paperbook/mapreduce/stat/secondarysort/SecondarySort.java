package com.paperbook.mapreduce.stat.secondarysort;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SecondarySort {

	/**
	 * It must be declared static, in case of reflection error
	 * 
	 * @author lgrcyanny
	 * 
	 */
	public static class SortMapper extends TableMapper<SortKeyPair, Text> {

		@Override
		protected void map(ImmutableBytesWritable key, Result rs,
				Context context) throws IOException, InterruptedException {
			String literature = Bytes.toString(rs.getRow());
			int count = Integer.valueOf(Bytes.toString(rs.getValue(
					Bytes.toBytes("info"), Bytes.toBytes("count"))));
			long avgts = Long.valueOf(Bytes.toString(rs.getValue(
					Bytes.toBytes("info"), Bytes.toBytes("avgts"))));
			context.write(new SortKeyPair(count, avgts), new Text(literature
					+ "," + count + "," + avgts));
		}

	}

	public static class SortReducer extends
			Reducer<SortKeyPair, Text, Text, Text> {

		/**
		 * Now the key is max SortKeyPair in the list, we just dump the ordered
		 * items
		 */
		@Override
		protected void reduce(SortKeyPair key, Iterable<Text> items,
				Context context) throws IOException, InterruptedException {
			Iterator<Text> iterator = items.iterator();
			while (iterator.hasNext()) {
				context.write(null, iterator.next());
			}
		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		Configuration conf = HBaseConfiguration.create();
		Job job = new Job(conf, "Secondarysort");
		job.setJarByClass(SecondarySort.class);
		Scan scan = new Scan();
		scan.addFamily(Bytes.toBytes("info"));
		scan.setCaching(5000); // Default is 1, set 500 improve performance
		scan.setCacheBlocks(false); // Close block cache for MR job
		TableMapReduceUtil.initTableMapperJob("pb_stat_comments_count", scan,
				SortMapper.class, SortKeyPair.class, Text.class, job);
		job.setReducerClass(SortReducer.class);

		// For secondary sort
		job.setSortComparatorClass(CompositeKeyComparator.class);
		job.setPartitionerClass(NaturalKeyPartitioner.class);
		job.setGroupingComparatorClass(NaturalKeyGroupComparator.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileOutputFormat.setOutputPath(job, new Path("secondary-sort-res"));

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
