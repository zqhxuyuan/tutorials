package com.zqh.hadoop.nimbus.mapreduce.lib.output;

import java.io.IOException;

import com.zqh.hadoop.nimbus.client.MapSetClient;
import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.server.CacheType;
import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.log4j.Logger;

public class NimbusMapSetOutputFormat extends OutputFormat<Text, Text> {

	private static final Logger LOG = Logger
			.getLogger(NimbusMapSetOutputFormat.class);
	public static final String NIMBUS_MAPSET_OUTPUT_FORMAT_CACHE_NAME = "nimbus.mapset.output.format.cache.name";
	public static final String NIMBUS_MAPSET_OUTPUT_FORMAT_CREATE_CACHE = "nimbus.mapset.output.format.create.cache";

	public static void setCacheName(Job job, String name) {
		job.getConfiguration()
				.set(NIMBUS_MAPSET_OUTPUT_FORMAT_CACHE_NAME, name);
	}

	public static void setCreateCacheIfNotExist(Job job, boolean val) {
		job.getConfiguration().setBoolean(
				NIMBUS_MAPSET_OUTPUT_FORMAT_CREATE_CACHE, val);
	}

	@Override
	public void checkOutputSpecs(JobContext context) throws IOException,
			InterruptedException {

		String cacheName = context.getConfiguration().get(
				NIMBUS_MAPSET_OUTPUT_FORMAT_CACHE_NAME);
		if (cacheName == null) {
			throw new IOException(NIMBUS_MAPSET_OUTPUT_FORMAT_CACHE_NAME
					+ " is not set");
		}

		if (context.getConfiguration().getBoolean(
				NIMBUS_MAPSET_OUTPUT_FORMAT_CREATE_CACHE, true)) {
			if (!NimbusMaster.getInstance().exists(cacheName)) {
				LOG.info("Cache " + cacheName + " does not exist.  Creating");
				NimbusMaster.getInstance().create(cacheName,
						CacheType.DYNAMIC_SET);
			} else {
				LOG.info("Cache exists");
			}
		} else if (!NimbusMaster.getInstance().exists(cacheName)) {
			throw new IOException(NIMBUS_MAPSET_OUTPUT_FORMAT_CREATE_CACHE
					+ " is false and cache does not exist");
		}
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		return new NullOutputFormat<Text, Text>().getOutputCommitter(context);
	}

	@Override
	public RecordWriter<Text, Text> getRecordWriter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		return new NimbusMapSetRecordWriter(context.getConfiguration().get(
				NIMBUS_MAPSET_OUTPUT_FORMAT_CACHE_NAME));
	}

	public static class NimbusMapSetRecordWriter extends
			RecordWriter<Text, Text> {

		private MapSetClient client = null;

		public NimbusMapSetRecordWriter(String cacheName) throws IOException {
			try {
				client = new MapSetClient(cacheName);
			} catch (CacheDoesNotExistException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		@Override
		public void close(TaskAttemptContext context) throws IOException,
				InterruptedException {
			client.disconnect();
		}

		@Override
		public void write(Text key, Text value) throws IOException,
				InterruptedException {
			client.add(key.toString(), value.toString());
		}
	}
}