package com.zqh.hadoop.mr.GTK;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

/**
 * Author : Deepika Mohan
 * 
 * Description: MapReduce program to find the intersection among the graphs. The
 * input, the output and the number of graphs are specified as command-line
 * arguments.
 * 
 * 
 * The input file contains data of the form : graphId<tab>source<tab>destination
 * 
 * The output file contains data of the form :
 * source:destination<tab>graphId_1^graphId_2^..graphId_n where 'n' is the
 * number of graphs for which we are finding the intersection and
 * source:destination pair is the common edge (source-destination pair) found in
 * all the n graphs.
 * 
 * Hadoop version used: 0.20.2
 * 
 * 
 **/

public class Intersect extends ExampleBaseJob {

	/**
	 * Mapper class that has the map method that outputs intermediate keys and
	 * values to be processed by the IntersectReducer.
	 * 
	 * The type parameters are the type of the input key, the type of the input
	 * values, the type of the output key and the type of the output values
	 * 
	 * 
	 * Input format : graphId<tab>source<tab>destination
	 * 
	 * Output format : <source:destination, graphId>
	 * 
	 **/
	public static class IntersectMapper extends
			Mapper<LongWritable, Text, Text, Text> {

		private Text gId = new Text(); // graphId
		private Text srcDestPair = new Text(); // source, destination pair

		
		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			StringTokenizer itr = new StringTokenizer(value.toString());

			while (itr.hasMoreTokens()) {

				// get the graph id
				gId.set(itr.nextToken());
				// setting the key as source and the value as the destination 
				//The source and the destination are delimited by ":" here
				srcDestPair.set(itr.nextToken() + ":" + itr.nextToken());

				//emit key, value pair from mapper
				//here the key is the source:destination pair and the graph id is the value
				context.write(srcDestPair, gId);
			}
		}
	}

	/**
	 * Reducer class for the intersection problem. The intermediate keys and
	 * values from the IntersectMapper form the input to the IntersectReducer.
	 * The output of the IntersectReducer will be the common edge found along
	 * with the graph ids.
	 * 
	 * Input format : <source: destination, graphId>
	 * 
	 * Output format :<source: destination, graphId>
	 * 
	 * 
	 **/
	public static class IntersectReducer extends
			Reducer<Text, Text, Text, Text> {

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context

		) throws IOException, InterruptedException {

			boolean first = true;//used for formatting the output

			StringBuilder toReturn = new StringBuilder();
			Set<String> graphIds = new HashSet<String>();
			Iterator<String> graphIdItr;

		
			int numberOfGraphs = 3; // the number of graphs is set as 3, it can be changed according to the number of graphs used in the program

			// add the graph ids to the set. This is to eliminate considering
			// multiple edges between same pair of nodes in same graphk
			for (Text val : values) {

				graphIds.add(val.toString());

			}

			graphIdItr = graphIds.iterator();

			// Iterate through the graphs and append the graph ids to a stringbuilder
			while (graphIdItr.hasNext()) {
			
				//for better formatting of the output
				if (!first){
					toReturn.append('^');
				}

				first = false;
				
				toReturn.append(graphIdItr.next());

			}

			String intersect = new String(toReturn);

			StringTokenizer st = new StringTokenizer(intersect, "^");

			// use StringTokenizer and if the number of graph ids is equal to
			// the number of graphs we're considering, write to the context
			if (st.countTokens() == numberOfGraphs){
				
				//emit the key, value pair from the reducer
				//here the key is the source:destination pair and the value will be the concatenated graph ids that has this source:destination pair
				context.write(key, new Text(intersect));
			}

		}
	}

	// method to set the configuration for the job and the mapper and the reducer classes
	private Job getJobConf(String[] args) throws Exception {

		JobInfo jobInfo = new JobInfo() {
			@Override
			public Class<? extends Reducer> getCombinerClass() {
				return IntersectReducer.class;
			}

			@Override
			public Class<?> getJarByClass() {
				return Intersect.class;
			}

			@Override
			public Class<? extends Mapper> getMapperClass() {
				return IntersectMapper.class;
			}

			@Override
			public Class<?> getOutputKeyClass() {
				return Text.class;
			}

			@Override
			public Class<?> getOutputValueClass() {
				return Text.class;
			}

			@Override
			public Class<? extends Reducer> getReducerClass() {
				return IntersectReducer.class;
			}
			
		};
		
		return setupJob("intersect", jobInfo);

		
	}

	@Override
	public int run(String[] args) throws Exception {
		Job job = getJobConf(args);

		FileInputFormat.addInputPath(job, new Path(args[0]));// specify the input path to be the first command-line argument passed by the user
		FileOutputFormat.setOutputPath(job, new Path(args[1]));// specify the output path to be the second command-line argument passed by the user

		return job.waitForCompletion(true) ? 0 : 1;// wait for the job to complete
	}

	public static void main(String[] args) throws Exception {

		int res = ToolRunner.run(new Configuration(), new Intersect(), args);
		if (args.length != 2) {
			System.err.println("Usage: Intersect <in> <out>" );
			System.exit(2);
		}
		System.exit(res);
	}
}
