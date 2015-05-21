package com.zqh.hadoop.mrdp.ch5;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.join.CompositeInputFormat;
import org.apache.hadoop.mapred.join.TupleWritable;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * Composite user comment join 组合用户信息和回复信息
 *
 * Problem: Given two large formatted data sets of user information and comments,
 * enrich the comments with user information data.
 * 具有相同userId的用户信息和回复信息都组合在一起
 */
public class CompositeJoinDriver {

	public static class CompositeMapper extends MapReduceBase implements Mapper<Text, TupleWritable, Text, Text> {

		@Override
		public void map(Text key, TupleWritable value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			// Get the first two elements in the tuple and output them
			output.collect((Text) value.get(0), (Text) value.get(1));
		}
	}

	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf("CompositeJoin");
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 4) {
			System.err.println("Usage: CompositeJoin <user data> <comment data> <out> [inner|outer]");
			System.exit(1);
		}

		Path userPath = new Path(otherArgs[0]);
		Path commentPath = new Path(otherArgs[1]);
		Path outputDir = new Path(otherArgs[2]);
		String joinType = otherArgs[3];
		if (!(joinType.equalsIgnoreCase("inner") || joinType.equalsIgnoreCase("outer"))) {
			System.err.println("Join type not set to inner or outer");
			System.exit(2);
		}

        conf.setJarByClass(CompositeJoinDriver.class);
		conf.setMapperClass(CompositeMapper.class);
		conf.setNumReduceTasks(0);

		// Set the input format class to a CompositeInputFormat class.
		// The CompositeInputFormat will parse all of our input files and output records to our mapper.
		conf.setInputFormat(CompositeInputFormat.class);

		// The composite input format join expression will set how the records are going to be read in, and in what input format.
        /**
         * To meet the preconditions of a composite join, both the user and comment data sets
         have been preprocessed by MapReduce and output using the TextOutputFormat .
         The key of each data set is the user ID, and the value is either the user XML or comment XML, based on the data set.
         Hadoop has a KeyValueTextOutputFormat that can parse these formatted data sets exactly as required.
         The key will be the output key of our format job (user ID) and the value will be the output value (user or comment data).
         */
		conf.set("mapred.join.expr", CompositeInputFormat.compose(joinType, KeyValueTextInputFormat.class, userPath, commentPath));

		TextOutputFormat.setOutputPath(conf, outputDir);

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		RunningJob job = JobClient.runJob(conf);
		while (!job.isComplete()) {
			Thread.sleep(1000);
		}

		System.exit(job.isSuccessful() ? 0 : 2);
	}
}
