package com.zqh.hadoop.mrdp.ch4;

import java.io.IOException;
import java.util.Map;

import com.zqh.hadoop.mrdp.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 全排序: Sort users by last visit -- P93
 *
 * Each individual reducer will sort its data by key, but unfortunately, this sorting is not
 global across all data. What we want to do here is a total order sorting where, if you
 concatenate the output files, the records are sorted. If we just concatenate the output of
 a simple MapReduce job, segments of the data will be sorted, but the whole set will not be.
 每个单独的Reduce会对数据进行排序, 但是多个Reduce的结果并不是全局有序的

 you first have to determine a set of partitions divided by ranges of values that
 will produce equal-sized subsets of data. These ranges will determine which reducer
 will sort which range of data. Then something similar to the partitioning pattern is run:
 a custom partitioner is used to partition data by the sort key. The lowest range of data
 goes to the first reducer, the next range goes to the second reducer, so on and so forth.
 首先要根据value的区间进行分区. 值在某个区间里的都进入同一个reduce. 多个有序的reduce合并起来, 也就是多个有序的区间合并
 因为每个去区间里的数据都是有序的. 所以合并后的区间也是有序的,从而实现了全局排序

 This pattern has two phases: an analyze phase that determines the ranges,
 and the order phase that actually sorts the data.
 这种模式有2个阶段: 首先决定区间, 然后根据每个区间进行单独排序

 */
public class TotalOrderSorting {

    /**
     * Analyze mapper: This mapper simply pulls the last access date for each user and sets
     it as the sort key for the record. The input value is output along with it.

     These key/value pairs, per our job configuration, are written to a SequenceFile that is
     used to create the partition list for the TotalOrderPartitioner .
     */
	public static class LastAccessDateMapper extends Mapper<Object, Text, Text, Text> {

		private Text outkey = new Text();

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			// Parse the input string into a nice map
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());

			String date = parsed.get("LastAccessDate");
			if (date != null) {
				outkey.set(date);
				context.write(outkey, value);
			}
		}
	}

    /**
     * TotalOrderPartitioner took care of all the sorting,
     * all the reducer needs to do is output the values with a NullWritable object.
     * This will produce a part file for this reducer that is sorted by last access date.
     *
     * The partitioner ensures that the concatenation of
     * all these part files (in order) produces a totally ordered data set.
     */
	public static class ValueReducer extends
			Reducer<Text, Text, Text, NullWritable> {

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text t : values) {
				context.write(t, NullWritable.get());
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    /**
     * building the partition list via sampling, then performing the sort.
     * 首先根据抽样构建分区列表, 然后进行排序
     *
     * It creates path files to the partition list and the staging directory.The partition list is used
     * 第一阶段的返回结果会在临时目录中创建分区文件. TotalOrderPartitioner会加载这个目录对区间进行排序
     * by the TotalOrderPartitioner to make sure the key/value pairs are sorted properly.
     * The staging directory is used to store intermediate output between the two jobs.
     */
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: TotalOrderSorting <user data> <out> <sample rate>");
			System.exit(1);
		}

        // 输入路径(要排序的文件), 分区文件(确定有哪些分区), Map的输出路径, 全排序的输出路径
		Path inputPath = new Path(otherArgs[0]);
		Path partitionFile = new Path(otherArgs[1] + "_partitions.lst");
		Path outputStage = new Path(otherArgs[1] + "_staging");
		Path outputOrder = new Path(otherArgs[1]);
		double sampleRate = Double.parseDouble(otherArgs[2]);

		FileSystem.get(new Configuration()).delete(outputOrder, true);
		FileSystem.get(new Configuration()).delete(outputStage, true);
		FileSystem.get(new Configuration()).delete(partitionFile, true);

		// Configure job to prepare for sampling
		Job sampleJob = new Job(conf, "TotalOrderSortingStage");
		sampleJob.setJarByClass(TotalOrderSorting.class);

		// Use the mapper implementation with zero reduce tasks
        // 第一个Job使用的Map只是根据lastAccessDate直接输出, 没看到sample的过程..
		sampleJob.setMapperClass(LastAccessDateMapper.class);
		sampleJob.setNumReduceTasks(0);

		sampleJob.setOutputKeyClass(Text.class);
		sampleJob.setOutputValueClass(Text.class);

		TextInputFormat.setInputPaths(sampleJob, inputPath);

		// Set the output format to a sequence file
		sampleJob.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setOutputPath(sampleJob, outputStage);

		// Submit the job and get completion code.
		int code = sampleJob.waitForCompletion(true) ? 0 : 1;

		if (code == 0) {
			Job orderJob = new Job(conf, "TotalOrderSortingStage");
			orderJob.setJarByClass(TotalOrderSorting.class);

			// Here, use the identity mapper to output the key/value pairs in the SequenceFile
            // This job simply uses the identity mapper to take each input key/value pair and output them
			orderJob.setMapperClass(Mapper.class);
			orderJob.setReducerClass(ValueReducer.class);

			// Set the number of reduce tasks to an appropriate number for the amount of data being sorted
            // Reduce的数量有10个, 则我们的数据会被分成10个区间. 每个区间里进入到相同Reduce后的数据都是有序的.
			orderJob.setNumReduceTasks(10);

            // ***** THIS IS WHAT MAGIC HAPPEN *****
			// Use Hadoop's TotalOrderPartitioner class
			orderJob.setPartitionerClass(TotalOrderPartitioner.class);

			// Set the partition file 设置分区文件
            // the partition file is configured, even though we have not created it yet.
			TotalOrderPartitioner.setPartitionFile(orderJob.getConfiguration(), partitionFile);

			orderJob.setOutputKeyClass(Text.class);
			orderJob.setOutputValueClass(Text.class);

			// Set the input to the previous job's output
			orderJob.setInputFormatClass(SequenceFileInputFormat.class);
			SequenceFileInputFormat.setInputPaths(orderJob, outputStage);

			// Set the output path to the command line parameter
			TextOutputFormat.setOutputPath(orderJob, outputOrder);

			// Set the separator to an empty string
			orderJob.getConfiguration().set("mapred.textoutputformat.separator", "");

			// Use the InputSampler to go through the output of the previous
			// job, sample it, and create the partition file
            // writes the partition file by reading through the configured input directory of the job.
            // 注意参数是第二个Job, 我们是对第一个Job的输出进行取样, 为什么这里却是第二个Job.
            // InputSampler的Job参数是输入对应的Job, 第一个Job是输出, 对于第二个Job就是输入.

            // 对第一个Job的输出结果进行取样. 第一个Job的key是lastAccessDate, 即会对lastAccessDate进行取样
            // 注意第一个Job并没有进行取样. 取样的工作在第二个Job的这里!!!
            // 取样过程会写入分区文件, 然后上面的TotalOrderPartitioner.setPartitionFile会使用分区文件
            // 这样第二个Job的Reduce过程会对每个分区里的数据进行排序.
			InputSampler.writePartitionFile(orderJob, new InputSampler.RandomSampler(sampleRate, 10000));

			// Submit the job
			code = orderJob.waitForCompletion(true) ? 0 : 2;
		}

		// Cleanup the partition file and the staging directory
		FileSystem.get(new Configuration()).delete(partitionFile, false);
		FileSystem.get(new Configuration()).delete(outputStage, true);

		System.exit(code);
	}
}
