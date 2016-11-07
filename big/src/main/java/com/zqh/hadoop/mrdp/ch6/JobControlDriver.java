package com.zqh.hadoop.mrdp.ch6;

import java.io.IOException;
import com.zqh.hadoop.mrdp.ch6.JobChainingDriver.UserIdBinningMapper;
import com.zqh.hadoop.mrdp.ch6.JobChainingDriver.UserIdCountMapper;
import com.zqh.hadoop.mrdp.ch6.JobChainingDriver.UserIdSumReducer;
import com.zqh.hadoop.mrdp.ch6.ParallelJobs.AverageReputationMapper;
import com.zqh.hadoop.mrdp.ch6.ParallelJobs.AverageReputationReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;

/**
 * Job Chaining with JobControl
 *
 * It uses basic job chaining to launch the first job,
 and then uses JobControl to execute the remaining job in the chain and the two parallel jobs.
 The initial job is not added via JobControl because you need to interrupt the control
 for the in-between step of using the counters of the first job to help assist in configuration of the second job.
 All jobs must be completely configured before executing the entire job chain, which can be limiting.
 */
public class JobControlDriver {

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.err.println("Usage: JobChainingDriver <posts> <users> <belowavgrepout> <aboveavgrepout>");
			System.exit(2);
		}

		Path postInput = new Path(args[0]);
		Path userInput = new Path(args[1]);
		Path countingOutput = new Path(args[3] + "_count");
		Path binningOutputRoot = new Path(args[3] + "_bins");
		Path binningOutputBelow = new Path(binningOutputRoot + "/" + JobChainingDriver.MULTIPLE_OUTPUTS_BELOW_NAME);
		Path binningOutputAbove = new Path(binningOutputRoot + "/" + JobChainingDriver.MULTIPLE_OUTPUTS_ABOVE_NAME);

		Path belowAverageRepOutput = new Path(args[2]);
		Path aboveAverageRepOutput = new Path(args[3]);

        // 第一个Job
		Job countingJob = getCountingJob(postInput, countingOutput);

		int code = 1;
		if (countingJob.waitForCompletion(true)) {
            // 第2个Job对用户进行分类
			ControlledJob binningControlledJob = new ControlledJob(
					getBinningJobConf(countingJob, countingOutput, userInput, binningOutputRoot));

            // 下面2个Job可以并行执行. 但是必须在第2个Job完成之后才可以执行.
			ControlledJob belowAvgControlledJob = new ControlledJob(
					getAverageJobConf(binningOutputBelow, belowAverageRepOutput));
            // 依赖于第2个Job: binningControlledJob
			belowAvgControlledJob.addDependingJob(binningControlledJob);

			ControlledJob aboveAvgControlledJob = new ControlledJob(
					getAverageJobConf(binningOutputAbove, aboveAverageRepOutput));
			aboveAvgControlledJob.addDependingJob(binningControlledJob);

            /**
             * JobGraph , DAG??
             *
             * The binningControlledJob has no dependencies, other than verifying that previous job executed and completed successfully.
             The next two jobs are dependent on the binning ControlledJob .
             These two jobs will not be executed by JobControl until the binning job completes successfully.
             If it doesn’t complete successfully, the other jobs won’t be executed at all.
             *                    countingJob
             *                          |
             *                 binningControlledJob
             *      -------------|              |--------------
             *      |                                         |
             *  belowAvgControlledJob                 aboveAvgControlledJob
             */
			JobControl jc = new JobControl("AverageReputation");
			jc.addJob(binningControlledJob);
			jc.addJob(belowAvgControlledJob);
			jc.addJob(aboveAvgControlledJob);

			jc.run();
			code = jc.getFailedJobList().size() == 0 ? 0 : 1;
		}

		FileSystem fs = FileSystem.get(new Configuration());
		fs.delete(countingOutput, true);
		fs.delete(binningOutputRoot, true);

		System.out.println("All Done");
		System.exit(code);
	}

    // 输入是帖子, 计算用户->用户创建的帖子数
	public static Job getCountingJob(Path postInput, Path outputDirIntermediate) throws IOException {
		// Setup first job to counter user posts
		Job countingJob = new Job(new Configuration(), "JobChaining-Counting");
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

		return countingJob;
	}

    // 输入是CountingJob, 求每个用户创建的帖子数的平均值. 和平均值比较, 将用户进行分类: <userId, posts\tReputation>
	public static Configuration getBinningJobConf(Job countingJob,
			Path jobchainOutdir, Path userInput, Path binningOutput) throws IOException {
		// Calculate the average posts per user by getting counter values
		double numRecords = (double) countingJob.getCounters()
				.findCounter(JobChainingDriver.AVERAGE_CALC_GROUP, UserIdCountMapper.RECORDS_COUNTER_NAME).getValue();
		double numUsers = (double) countingJob.getCounters()
				.findCounter(JobChainingDriver.AVERAGE_CALC_GROUP, UserIdSumReducer.USERS_COUNTER_NAME).getValue();

		double averagePostsPerUser = numRecords / numUsers;

		// Setup binning job
		Job binningJob = new Job(new Configuration(), "JobChaining-Binning");
		binningJob.setJarByClass(JobChainingDriver.class);

		// Set mapper and the average posts per user
		binningJob.setMapperClass(UserIdBinningMapper.class);
		UserIdBinningMapper.setAveragePostsPerUser(binningJob, averagePostsPerUser);

		binningJob.setNumReduceTasks(0);

		binningJob.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.addInputPath(binningJob, jobchainOutdir);

		// Add two named outputs for below/above average
		MultipleOutputs.addNamedOutput(binningJob, JobChainingDriver.MULTIPLE_OUTPUTS_BELOW_NAME, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(binningJob, JobChainingDriver.MULTIPLE_OUTPUTS_ABOVE_NAME, TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.setCountersEnabled(binningJob, true);

		TextOutputFormat.setOutputPath(binningJob, binningOutput);

		// Add the user files to the DistributedCache
		FileStatus[] userFiles = FileSystem.get(new Configuration()).listStatus(userInput);
		for (FileStatus status : userFiles) {
			DistributedCache.addCacheFile(status.getPath().toUri(), binningJob.getConfiguration());
		}

		// Execute job and grab exit code
		return binningJob.getConfiguration();
	}

	public static Configuration getAverageJobConf(Path averageOutputDir, Path outputDir) throws IOException {

		Job averageJob = new Job(new Configuration(), "ParallelJobs");
		averageJob.setJarByClass(ParallelJobs.class);

		averageJob.setMapperClass(AverageReputationMapper.class);
		averageJob.setReducerClass(AverageReputationReducer.class);

		averageJob.setOutputKeyClass(Text.class);
		averageJob.setOutputValueClass(DoubleWritable.class);

		averageJob.setInputFormatClass(TextInputFormat.class);

		TextInputFormat.addInputPath(averageJob, averageOutputDir);

		averageJob.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setOutputPath(averageJob, outputDir);

		// Execute job and grab exit code
		return averageJob.getConfiguration();
	}

}
