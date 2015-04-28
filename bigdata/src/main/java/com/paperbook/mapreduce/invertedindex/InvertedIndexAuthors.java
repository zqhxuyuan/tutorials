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

public class InvertedIndexAuthors {

	public static class IndexMapper extends
			TableMapper<ImmutableBytesWritable, Put> {

		@Override
		protected void map(ImmutableBytesWritable key, Result rs,
				Context context) throws IOException, InterruptedException {
			byte[] byteAuthors = rs.getValue(
					Bytes.toBytes(GlobalConfig.TBL_COLUMN_FAMILY),
					Bytes.toBytes(GlobalConfig.TBL_COLUMN_AUTHORS));
			byte[] byteTitle = rs.getValue(
					Bytes.toBytes(GlobalConfig.TBL_COLUMN_FAMILY),
					Bytes.toBytes(GlobalConfig.TBL_COLUMN_TITLE));
			String authors = Bytes.toString(byteAuthors);
			String title = Bytes.toString(byteTitle);
			String[] words = authors.split(GlobalConfig.STRING_DELIMITER);
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
		Job job = new Job(conf, GlobalConfig.TBL_INDEX_AUTHORS);
		job.setJarByClass(InvertedIndexAuthors.class);
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes(GlobalConfig.TBL_COLUMN_FAMILY),
				Bytes.toBytes(GlobalConfig.TBL_COLUMN_AUTHORS));
		scan.addColumn(Bytes.toBytes(GlobalConfig.TBL_COLUMN_FAMILY),
				Bytes.toBytes(GlobalConfig.TBL_COLUMN_TITLE));
		scan.setCaching(500);
		scan.setCacheBlocks(false);
		TableMapReduceUtil.initTableMapperJob(GlobalConfig.TBL_LITERATURES,
				scan, IndexMapper.class, ImmutableBytesWritable.class,
				Put.class, job);
		TableMapReduceUtil.initTableReducerJob(GlobalConfig.TBL_INDEX_AUTHORS,
				null, job);
		job.setNumReduceTasks(0);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
