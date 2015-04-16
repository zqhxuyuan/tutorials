package com.zqh.hadoop.nimbus.mapreduce.lib.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.mapreduce.NimbusSplit;
import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.client.MapSetCacheletConnection;
import com.zqh.hadoop.nimbus.client.MapSetCacheletConnection.MapSetCacheletIterator;
import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class NimbusMapSetInputFormat extends InputFormat<Text, Text> {

	public static String NIMBUS_MAPSET_INPUT_FORMAT_CACHE_NAME = "nimbus.mapset.output.format.cache.name";

	public static void setCacheName(Job job, String name) {
		job.getConfiguration().set(NIMBUS_MAPSET_INPUT_FORMAT_CACHE_NAME, name);
	}

	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit split,
			TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new NimbusMapSetRecordReader();
	}

	@Override
	public List<InputSplit> getSplits(JobContext context) throws IOException,
			InterruptedException {

		String cacheName = context.getConfiguration().get(
				NIMBUS_MAPSET_INPUT_FORMAT_CACHE_NAME);

		NimbusMaster master = NimbusMaster.getInstance();
		if (cacheName == null) {
			throw new IOException(NIMBUS_MAPSET_INPUT_FORMAT_CACHE_NAME
					+ " is not set");
		} else if (!master.exists(cacheName)) {
			throw new IOException(cacheName + "  does not exist");
		}

		List<InputSplit> splits = new ArrayList<InputSplit>();
		String[] hosts = NimbusConf.getConf().getNimbusCacheletAddresses()
				.split(",");

		for (String host : hosts) {
			splits.add(new NimbusSplit(host));
		}

		return splits;
	}

	public static class NimbusMapSetRecordReader extends
			RecordReader<Text, Text> {

		private MapSetCacheletConnection client = null;
		private Text currentKey = new Text(), currentValue = new Text();
		private Entry<String, String> currEntry = null;
		private MapSetCacheletIterator iter = null;

		@Override
		public void initialize(InputSplit split, TaskAttemptContext context)
				throws IOException, InterruptedException {
			String host = split.getLocations()[0];
			try {
				client = new MapSetCacheletConnection(context
						.getConfiguration().get(
								NIMBUS_MAPSET_INPUT_FORMAT_CACHE_NAME), host);

				iter = (MapSetCacheletIterator) client.iterator();
			} catch (CacheDoesNotExistException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		@Override
		public void close() throws IOException {
			client.disconnect();
		}

		@Override
		public Text getCurrentKey() throws IOException, InterruptedException {
			return currentKey;
		}

		@Override
		public Text getCurrentValue() throws IOException, InterruptedException {
			return currentValue;
		}

		@Override
		public float getProgress() throws IOException, InterruptedException {
			return iter.getProgress();
		}

		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			if (iter.hasNext()) {
				currEntry = iter.next();
				currentKey.set(currEntry.getKey());
				currentValue.set(currEntry.getValue());
				return true;
			} else {
				return false;
			}
		}
	}
}