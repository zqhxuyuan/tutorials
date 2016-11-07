package com.paperbook.mapreduce.invertedindex;

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
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class InvertedIndexTitle {

	public static class IndexMapper extends
			TableMapper<ImmutableBytesWritable, Put> {

		@Override
		protected void map(ImmutableBytesWritable key, Result rs,
				Context context) throws IOException, InterruptedException {
			byte[] b = rs.getValue(
					Bytes.toBytes(GlobalConfig.TBL_COLUMN_FAMILY),
					Bytes.toBytes(GlobalConfig.TBL_COLUMN_TITLE));
			String title = Bytes.toString(b);
			String[] words = title.split(GlobalConfig.STRING_DELIMITER);
			for (String item : words) {
				Put put = new Put(Bytes.toBytes(item));
				// Column Family: info, Column qualifier: literatureRowKey,
				// Value: literature title
				put.add(Bytes.toBytes(GlobalConfig.TBL_COLUMN_FAMILY),
						rs.getRow(), Bytes.toBytes(title));
				context.write(new ImmutableBytesWritable(put.getRow()), put);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		Configuration conf = HBaseConfiguration.create();
		Job job = new Job(conf, GlobalConfig.TBL_INDEX_TITLE);
		job.setJarByClass(InvertedIndexTitle.class);
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes(GlobalConfig.TBL_COLUMN_FAMILY),
				Bytes.toBytes(GlobalConfig.TBL_COLUMN_TITLE));
		scan.setCaching(500); // Default is 1, set 500 improve performance
		scan.setCacheBlocks(false); // Close block cache for MR job
		TableMapReduceUtil.initTableMapperJob(GlobalConfig.TBL_LITERATURES,
				scan, IndexMapper.class, ImmutableBytesWritable.class,
				Put.class, job);
		TableMapReduceUtil.initTableReducerJob(GlobalConfig.TBL_INDEX_TITLE,
				null, job);
		job.setNumReduceTasks(0); // There is no need of Reducer, HBase is the
									// reduer
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
