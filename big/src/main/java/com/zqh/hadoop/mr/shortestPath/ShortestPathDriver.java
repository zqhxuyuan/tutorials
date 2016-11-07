package com.zqh.hadoop.mr.shortestPath;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ShortestPathDriver extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException("Bad number of arguments: "
					+ args.length);
		}
		String input = args[0];
		String firstInput = input;
		int iteration = 1;
		String output = firstInput + "." + iteration;
		long nodeDistanceSet = 1;
		long previousNodeDistanceSet = 0;

		// Iterate until no new node distances have been calculated.  The last iteration
		// will not compute anything new but will allow us to determine that there is no more
		// work to do.
		
		while (nodeDistanceSet != previousNodeDistanceSet) {
			JobConf conf = new JobConf(getConf(), ShortestPathDriver.class);
			conf.setJobName("shortest path");
			conf.setMapperClass(ShortestPathMapper.class);
			conf.setReducerClass(ShortestPathReducer.class);
			conf.setInputFormat(KeyValueTextInputFormat.class);
			conf.setOutputFormat(TextOutputFormat.class);
			conf.setMapOutputKeyClass(Text.class);
			conf.setMapOutputValueClass(NodeWritable.class);
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(NodeWritable.class);
			FileInputFormat.setInputPaths(conf, new Path(input));
			FileOutputFormat.setOutputPath(conf, new Path(output));
			
			RunningJob job = JobClient.runJob(conf);
			previousNodeDistanceSet = nodeDistanceSet;
			nodeDistanceSet = job.getCounters().findCounter("node", "nodeDistanceSet").getValue();
			System.out.println("\nIteration "+iteration+" completed with "+nodeDistanceSet+" nodes set. Output in "+output+"\n");
			
			iteration++;
			input = output;
			output = firstInput + "." + iteration;
		}
		System.out.println(iteration-1 + " iterations to compute shortest path.");
		return 0;
	}

	public static void main(String[] args) throws Exception {
		ShortestPathDriver driver = new ShortestPathDriver(); // TODO change
		int exitCode = ToolRunner.run(driver, args);
		System.exit(exitCode);
	}
}