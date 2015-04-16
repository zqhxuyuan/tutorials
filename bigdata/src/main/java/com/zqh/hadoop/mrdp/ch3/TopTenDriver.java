package com.zqh.hadoop.mrdp.ch3;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.zqh.hadoop.mrdp.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * Chapter 3 Filter Patterns : Top Ten
 *
 * Problem: Given a list of user information, output the information of the top ten users based on reputation.
 */
public class TopTenDriver {

	public static class SOTopTenMapper extends
			Mapper<Object, Text, NullWritable, Text> {

        // TreeMap是有序的.为什么要有序, 因为Top N的top含义指的是最高/最低的几个值.
		// Our output key and value Writables
		private TreeMap<Integer, Text> repToRecordMap = new TreeMap<Integer, Text>();

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			// Parse the input string into a nice map
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());
			if (parsed == null) return;

			String userId = parsed.get("Id");
			String reputation = parsed.get("Reputation");

			// Get will return null if the key is not there
			if (userId == null || reputation == null) return;

            // 放进Map里的key是要进行top值比较的reputation字段.value是整个Comment
            // 因为我们要按照reputation字段排序(实际上没有排序,只是取最高的10个), 而输出结果是最高的10条Comment
			repToRecordMap.put(Integer.parseInt(reputation), new Text(value));

			if (repToRecordMap.size() > 10) {
                // the first is the smallest reputation in all 10 elements
				repToRecordMap.remove(repToRecordMap.firstKey());
			}
		}

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
            // 在清理阶段输出最高的10个Comment. 因为每个Map的InputSplit都完成后才可以进行整个操作
            // value是在map阶段放进的Comment记录. key为空. 因为我们只需要一个Reduce!
            // 多个Map的key都是Null, 这样所有的Map都会输出到同一个Reduce. 在Reduce端再进行一次topN操作.
			for (Text t : repToRecordMap.values()) {
				context.write(NullWritable.get(), t);
			}
		}
	}

	public static class SOTopTenReducer extends Reducer<NullWritable, Text, NullWritable, Text> {

		private TreeMap<Integer, Text> repToRecordMap = new TreeMap<Integer, Text>();

		@Override
		public void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for (Text value : values) {
				Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());

                // 下面两断和Map端的一样. 都是先把值放进Map, 然后判断是否>10, 如果超过10个, 则删除第一个key
				repToRecordMap.put(Integer.parseInt(parsed.get("Reputation")), new Text(value));

				if (repToRecordMap.size() > 10) {
					repToRecordMap.remove(repToRecordMap.firstKey());
				}
			}

            // 这个过程和Map的cleanup阶段类似. 当然也可以把下面这段也放在Reduce的cleanup里
			for (Text t : repToRecordMap.descendingMap().values()) {
				context.write(NullWritable.get(), t);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: TopTenDriver <in> <out>");
			System.exit(2);
		}

		Job job = new Job(conf, "Top Ten Users by Reputation");
		job.setJarByClass(TopTenDriver.class);
		job.setMapperClass(SOTopTenMapper.class);
		job.setReducerClass(SOTopTenReducer.class);
		job.setNumReduceTasks(1);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
