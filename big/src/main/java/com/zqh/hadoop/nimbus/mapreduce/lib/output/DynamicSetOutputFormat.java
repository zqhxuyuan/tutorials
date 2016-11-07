package com.zqh.hadoop.nimbus.mapreduce.lib.output;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.HashSet;
import java.util.Set;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import com.zqh.hadoop.nimbus.client.DynamicSetCacheletConnection;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.server.CacheType;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.log4j.Logger;

public class DynamicSetOutputFormat extends OutputFormat<Text, Object> {

	private static final Logger LOG = Logger
			.getLogger(DynamicSetOutputFormat.class);
	public static final String NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CACHE_NAME = "nimbus.dynamic.set.output.format.cache.name";
	public static final String NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CREATE_CACHE = "nimbus.dynamic.set.output.format.create.cache";

	public static void setCacheName(Job job, String name) {
		job.getConfiguration().set(NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CACHE_NAME,
				name);
	}

	public static void setCreateCacheIfNotExist(Job job, boolean val) {
		job.getConfiguration().setBoolean(
				NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CREATE_CACHE, val);
	}

	@Override
	public void checkOutputSpecs(JobContext context) throws IOException,
			InterruptedException {

		String cacheName = context.getConfiguration().get(
				NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CACHE_NAME);
		if (cacheName == null) {
			throw new IOException(NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CACHE_NAME
					+ " is not set");
		}

		if (context.getConfiguration().getBoolean(
				NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CREATE_CACHE, true)) {
			if (!NimbusMaster.getInstance().exists(cacheName)) {
				LOG.info("Cache " + cacheName + " does not exist.  Creating");
				NimbusMaster.getInstance().create(cacheName,
						CacheType.DYNAMIC_SET);
			} else {
				LOG.info("Cache exists");
			}
		} else if (!NimbusMaster.getInstance().exists(cacheName)) {
			throw new IOException(NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CREATE_CACHE
					+ " is false and cache does not exist");
		}

		context.getConfiguration().setInt("mapred.reduce.tasks",
				NimbusConf.getConf().getNumNimbusCachelets());
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		return new NullOutputFormat<Text, Text>().getOutputCommitter(context);
	}

	@Override
	public RecordWriter<Text, Object> getRecordWriter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		return new NimbusDynamicSetRecordWriter(context.getConfiguration().get(
				NIMBUS_DYNAMIC_SET_OUTPUT_FORMAT_CACHE_NAME), context);
	}

	public static class NimbusDynamicSetRecordWriter extends
			RecordWriter<Text, Object> implements NotificationListener {

		private DynamicSetCacheletConnection client = null;
		private Set<String> bufferedElements = new HashSet<String>();
		Boolean flush = false;

		public NimbusDynamicSetRecordWriter(String cacheName,
				TaskAttemptContext context) throws IOException {

			int id = context.getTaskAttemptID().getTaskID().getId();
			try {
				String[] addresses = NimbusConf.getConf()
						.getNimbusCacheletAddresses().split(",");

				client = new DynamicSetCacheletConnection(cacheName,
						addresses[id]);
			} catch (CacheDoesNotExistException e) {
				e.printStackTrace();
				throw new IOException(e);
			}

			// heuristic to find the tenured pool (largest heap) as seen on
			// http://www.javaspecialists.eu/archive/Issue092.html
			MemoryPoolMXBean tenuredGenPool = null;
			for (MemoryPoolMXBean pool : ManagementFactory
					.getMemoryPoolMXBeans()) {
				if (pool.getType() == MemoryType.HEAP
						&& pool.isUsageThresholdSupported()) {
					tenuredGenPool = pool;
				}
			}

			// we do something when we reached 80% of memory usage
			tenuredGenPool.setCollectionUsageThreshold((int) Math
					.floor(tenuredGenPool.getUsage().getMax() * 0.8));

			// set a listener
			MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			NotificationEmitter emitter = (NotificationEmitter) mbean;
			emitter.addNotificationListener(this, null, null);
		}

		@Override
		public void close(TaskAttemptContext context) throws IOException,
				InterruptedException {
			flush();
			client.disconnect();
		}

		@Override
		public void write(Text key, Object value) throws IOException,
				InterruptedException {

			synchronized (bufferedElements) {
				bufferedElements.add(key.toString());
			}

			synchronized (flush) {
				if (flush) {
					flush();
					flush = false;
				}
			}
		}

		@Override
		public void handleNotification(Notification notification,
				Object handback) {
			synchronized (flush) {
				flush = true;
			}
		}

		private void flush() throws IOException, InterruptedException {

			synchronized (bufferedElements) {
				client.addAll(bufferedElements);
				bufferedElements.clear();
				System.gc();
			}
		}
	}
}