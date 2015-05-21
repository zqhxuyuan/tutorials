package com.zqh.hadoop.mrdp.ch6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.zqh.hadoop.mrdp.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 多个Job的链接
 *
 * Basic job chaining
 The goal of this example is to output a list of users along with a couple pieces of information:
 their reputations and how many posts each has issued. This could be done in a single MapReduce job,
 but we also want to separate users into those with an aboveaverage number of posts and those with a below-average number.
 We need one job to perform the counts and another to separate the users into two bins based on the number of posts.

 Four different patterns are used in this example: numerical summarization,counting, binning, and a replicated join.
 The final output consists of a user ID, the number of times they posted, and their reputation.
 The average number of posts per user is calculated between the two jobs using the framework’s counters.
 The users data set is put in the DistributedCache in the second job to enrich the output data with the users’ reputations.
 This enrichment occurs in order to feed in to the next example in this section,
 which calculates the average reputation of the users in the two bins.

 Problem: Given a data set of StackOverflow posts,
 bin users based on if they are below or above[Job2] the number of average posts per user[Job1].
 Also to enrich each user with his or her reputation from a separate data set when generating the output.
 */
public class JobChainingDriver {

	public static final String AVERAGE_CALC_GROUP = "AverageCalculation";
	public static final String MULTIPLE_OUTPUTS_ABOVE_NAME = "aboveavg";
	public static final String MULTIPLE_OUTPUTS_BELOW_NAME = "belowavg";

    /**
     * Job one mapper. The mapper records the user ID from each record by assigning
     the value of the OwnerUserId attribute as the output key for the job, with a count of one
     as the value. It also increments a record counter by one. This value is later used in the
     driver to calculate the average number of posts per user. The AVERAGE_CALC_GROUP is a
     public static string at the driver level.
     */
	public static class UserIdCountMapper extends Mapper<Object, Text, Text, LongWritable> {

		public static final String RECORDS_COUNTER_NAME = "Records";

		private static final LongWritable ONE = new LongWritable(1);
		private Text outkey = new Text();

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			// Parse the input into a nice map.
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());

			// Get the value for the OwnerUserId attribute
			String userId = parsed.get("OwnerUserId");

			if (userId != null) {
				outkey.set(userId);
                // 一条记录表示一个用户创建了一个帖子
				context.write(outkey, ONE);
				context.getCounter(AVERAGE_CALC_GROUP, RECORDS_COUNTER_NAME).increment(1);
			}
		}
	}

    /**
     * Job one reducer. The reducer is also fairly trivial. It simply iterates through the input
     values (all of which we set to 1) and keeps a running sum, which is output along with
     the input key. A different counter is also incremented by one for each reduce group, in
     order to calculate the average
     */
	public static class UserIdSumReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

		public static final String USERS_COUNTER_NAME = "Users";
		private LongWritable outvalue = new LongWritable();

		@Override
		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            // UserIdCountMapper的key是userId, 到这里同一个userId会有多条记录, 但是要记录distinct user的数量,只需要在for循环外执行
			// Increment user counter, as each reduce group represents one user
			context.getCounter(AVERAGE_CALC_GROUP, USERS_COUNTER_NAME).increment(1);

			int sum = 0;
			for (LongWritable value : values) {
				sum += value.get();
			}

			outvalue.set(sum);
            // key还是那个key, value变成多个值的和了
            // key是userId, value是这个用户创建的帖子数量
			context.write(key, outvalue);
		}
	}

    /**
     * Job two mapper.
     */
	public static class UserIdBinningMapper extends Mapper<Object, Text, Text, Text> {

		public static final String AVERAGE_POSTS_PER_USER = "avg.posts.per.user";

		public static void setAveragePostsPerUser(Job job, double avg) {
			job.getConfiguration().set(AVERAGE_POSTS_PER_USER, Double.toString(avg));
		}

		public static double getAveragePostsPerUser(Configuration conf) {
			return Double.parseDouble(conf.get(AVERAGE_POSTS_PER_USER));
		}

		private double average = 0.0;
		private MultipleOutputs<Text, Text> mos = null;
		private Text outkey = new Text(), outvalue = new Text();
		private HashMap<String, String> userIdToReputation = new HashMap<String, String>();

        /**
         * The setup phase accomplishes three different things.
         1. The average number of posts per user is pulled from the Context object that was set during job configuration.
         2. The MultipleOutputs utility is initialized as well.This is used to write the output to different bins.
         3. Finally, the user data set is parsed from the DistributedCache to build a map of user ID to reputation.
         This map is used for the desired data enrichment during output.
         */
		protected void setup(Context context) throws IOException, InterruptedException {
			average = getAveragePostsPerUser(context.getConfiguration());
			mos = new MultipleOutputs<Text, Text>(context);

            Path[] files = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            if (files == null || files.length == 0) {
                throw new RuntimeException("User information is not set in DistributedCache");
            }

            // Read all files in the DistributedCache
            for (Path p : files) {
                BufferedReader rdr = new BufferedReader(
                        new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(p.toString())))));

                String line;
                // For each record in the user file
                while ((line = rdr.readLine()) != null) {
                    // Get the user ID and reputation
                    Map<String, String> parsed = MRDPUtils.transformXmlToMap(line);
                    String userId = parsed.get("Id");
                    String reputation = parsed.get("Reputation");

                    if (userId != null && reputation != null) {
                        // Map the user ID to the reputation
                        userIdToReputation.put(userId, reputation);
                    }
                }
            }
		}

        /**
         * The input value is parsed to get the user ID and number of times posted.
         This is done by simply splitting on tabs and getting the first two fields of data.
         Then the mapper sets the output key to the user ID and the output value to the number of posts
         along with the user’s reputation, delimited by a tab.
         The user’s number of posts is then compared to the average, and the user is binned appropriately.
         */
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String[] tokens = value.toString().split("\t");

            // 用户, 用户创建的帖子数量. 为什么不是key:userId, value:postsCount? 而是把两者都放在value里? 通过什么控制?
            // 解释: 因为这是第二个Job的mapper. 它的输入是第一个Job的输出. JOB的输出是文件格式.
            // Mapper读取文件, key是offset, value是每一行文件的内容. 因为第一个Job的key是userId, value是count
            // 所以输出到文件的格式是userId\tcount. 即第二个Job读取时,value=userId\tcount
			String userId = tokens[0];
			int posts = Integer.parseInt(tokens[1]);

			outkey.set(userId);
			outvalue.set((long) posts + "\t" + userIdToReputation.get(userId));

			if ((double) posts < average) {
				mos.write(MULTIPLE_OUTPUTS_BELOW_NAME, outkey, outvalue, MULTIPLE_OUTPUTS_BELOW_NAME + "/part");
			} else {
				mos.write(MULTIPLE_OUTPUTS_ABOVE_NAME, outkey, outvalue, MULTIPLE_OUTPUTS_ABOVE_NAME + "/part");
			}

		}

		protected void cleanup(Context context) throws IOException, InterruptedException {
			mos.close();
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		if (otherArgs.length != 3) {
			System.err.println("Usage: JobChainingDriver <posts> <users> <out>");
			System.exit(2);
		}

		Path postInput = new Path(otherArgs[0]);
		Path userInput = new Path(otherArgs[1]);
		Path outputDirIntermediate = new Path(otherArgs[2] + "_int");
		Path outputDir = new Path(otherArgs[2]);

		// Setup first job to counter user posts
		Job countingJob = new Job(conf, "JobChaining-Counting");
		countingJob.setJarByClass(JobChainingDriver.class);

		// Set our mapper and reducer, we can use the API's long sum reducer for a combiner!
		countingJob.setMapperClass(UserIdCountMapper.class);
		countingJob.setCombinerClass(LongSumReducer.class);
		countingJob.setReducerClass(UserIdSumReducer.class);

		countingJob.setOutputKeyClass(Text.class);
		countingJob.setOutputValueClass(LongWritable.class);

		countingJob.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.addInputPath(countingJob, postInput);

		countingJob.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setOutputPath(countingJob, outputDirIntermediate);

		// Execute job and grab exit code
        // The first job is checked for success before executing the second job.
		int code = countingJob.waitForCompletion(true) ? 0 : 1;

		if (code == 0) {
			// Calculate the average posts per user by getting counter values
            // RECORDS_COUNTER_NAME在Mapper中,每条记录增加一次. 总和表示总的记录数
			double numRecords = (double) countingJob.getCounters()
					.findCounter(AVERAGE_CALC_GROUP, UserIdCountMapper.RECORDS_COUNTER_NAME).getValue();
            // USERS_COUNTER_NAME在Reducer中, 每个用户增加一次, 总和表示总的用户数
			double numUsers = (double) countingJob.getCounters()
                    .findCounter(AVERAGE_CALC_GROUP, UserIdSumReducer.USERS_COUNTER_NAME).getValue();

            // 每个用户平均创建了多少条帖子
			double averagePostsPerUser = numRecords / numUsers;

			// Setup binning job
			Job binningJob = new Job(new Configuration(), "JobChaining-Binning");
			binningJob.setJarByClass(JobChainingDriver.class);

			// Set mapper and the average posts per user 在开始第二个Job之前,将计算的平均值设置到Job配置中
			binningJob.setMapperClass(UserIdBinningMapper.class);
			UserIdBinningMapper.setAveragePostsPerUser(binningJob, averagePostsPerUser);
			binningJob.setNumReduceTasks(0);

            // 第二个Job的输入是第一个Job的输出
			binningJob.setInputFormatClass(TextInputFormat.class);
			TextInputFormat.addInputPath(binningJob, outputDirIntermediate);

			// Add two named outputs for below/above average
            // 多个输出, 一条记录会根据和平均值的比较, 被分到某一个类别中
			MultipleOutputs.addNamedOutput(binningJob, MULTIPLE_OUTPUTS_BELOW_NAME, TextOutputFormat.class, Text.class, Text.class);
			MultipleOutputs.addNamedOutput(binningJob, MULTIPLE_OUTPUTS_ABOVE_NAME, TextOutputFormat.class, Text.class, Text.class);
			MultipleOutputs.setCountersEnabled(binningJob, true);

            // 最终的输出目录
			TextOutputFormat.setOutputPath(binningJob, outputDir);

			// Add the user files to the DistributedCache 添加用户数据到分布式缓存中
            // 缓存的使用在UserIdBinningMapper.setup阶段.
			FileStatus[] userFiles = FileSystem.get(conf).listStatus(userInput);
			for (FileStatus status : userFiles) {
				DistributedCache.addCacheFile(status.getPath().toUri(), binningJob.getConfiguration());
			}

			// Execute job and grab exit code
			code = binningJob.waitForCompletion(true) ? 0 : 1;
		}

		// Clean up the intermediate output
		FileSystem.get(conf).delete(outputDirIntermediate, true);

		System.exit(code);
	}
}
