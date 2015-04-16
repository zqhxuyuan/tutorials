package com.zqh.hadoop.mrdp.ch6;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * Parallel job chaining
 *
 * Problem: Given the previous example’s output of binned users, [JobChainingDriver]
 * run parallel jobs over both bins to calculate the average reputation of each user.
 */
public class ParallelJobs {

    /**
     * The mapper splits the input value into a string array. The third column of
     this index is the reputation of the particular user. This reputation is output with a unique
     key. This key is shared across all map tasks in order to group all the reputations together
     for the average calculation. NullWritable can be used to group all the records together,
     but we want the key to have a meaningful value.
     */
	public static class AverageReputationMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

		private static final Text GROUP_ALL_KEY = new Text("Average Reputation:");
		private DoubleWritable outvalue = new DoubleWritable();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			try {
				// Split the line into tokens
                // JobChainingDriver的最终结果key:userId, value:PostsCount\tReputation
                // 由于写到HDFS,形成文件形式. 文件的每一行的内容就是userId\tPostsCount\tReputation
                // 用户Id 用户创建的帖子数量   Reputation(用户的声望值)
				String[] tokens = value.toString().split("\t");

				// Get the reputation from the third column
				double reputation = Double.parseDouble(tokens[2]);

				// Set the output value and write to context
				outvalue.set(reputation);
                // 要计算所有用户的平均声望值, 必须只有一个Reduce, 所以这里的key设置为一个常量字符串
                // 每个用户的声望值都加起来就是总的声望值. 然后除于数量=平均声望值
				context.write(GROUP_ALL_KEY, outvalue);
			} catch (NumberFormatException e) {
				// Skip record
			}
		}
	}

    /**
     * The reducer simply iterates through the reputation values, summing the
     numbers and keeping a count. The average is then calculated and output with the input key.

     用户会根据posts数量和平均值进行比较被分成2类. 一类是比平均值高的,一类是比平均值低的.
     要对这两类数据分别统计各自的Reputation的平均值. 这是2个Job, 相互之间互相不影响. 所以可以并行执行.
     */
	public static class AverageReputationReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

		private DoubleWritable outvalue = new DoubleWritable();

		@Override
		protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
			double sum = 0.0;
			double count = 0;
			for (DoubleWritable dw : values) {
				sum += dw.get();
				++count;
			}

			outvalue.set(sum / count);
			context.write(key, outvalue);
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		if (otherArgs.length != 4) {
			System.err.println("Usage: ParallelJobs <below-avg-in> <below-avg-out> <below-avg-out> <above-avg-out>");
			System.exit(2);
		}

		Path belowAvgInputDir = new Path(otherArgs[0]);
		Path aboveAvgInputDir = new Path(otherArgs[1]);

		Path belowAvgOutputDir = new Path(otherArgs[2]);
		Path aboveAvgOutputDir = new Path(otherArgs[3]);

        // 并行的实现是创建多个Job, 提交并运行. 当所有Job都完成时表示整个作业完成
		Job belowAvgJob = submitJob(conf, belowAvgInputDir, belowAvgOutputDir);
		Job aboveAvgJob = submitJob(conf, aboveAvgInputDir, aboveAvgOutputDir);

		// While both jobs are not finished, sleep
		while (!belowAvgJob.isComplete() || !aboveAvgJob.isComplete()) {
			Thread.sleep(5000);
		}

		if (belowAvgJob.isSuccessful()) {
			System.out.println("Below average job completed successfully!");
		} else {
			System.out.println("Below average job failed!");
		}

		if (aboveAvgJob.isSuccessful()) {
			System.out.println("Above average job completed successfully!");
		} else {
			System.out.println("Above average job failed!");
		}

		System.exit(belowAvgJob.isSuccessful() && aboveAvgJob.isSuccessful() ? 0 : 1);
	}

	private static Job submitJob(Configuration conf, Path inputDir, Path outputDir) throws IOException, InterruptedException, ClassNotFoundException {

		Job job = new Job(conf, "ParallelJobs");
		job.setJarByClass(ParallelJobs.class);

		job.setMapperClass(AverageReputationMapper.class);
		job.setReducerClass(AverageReputationReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		job.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.addInputPath(job, inputDir);

		job.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setOutputPath(job, outputDir);

        // submit the job and then immediately return, allowing the application to continue.
		job.submit();
		return job;
	}
}
