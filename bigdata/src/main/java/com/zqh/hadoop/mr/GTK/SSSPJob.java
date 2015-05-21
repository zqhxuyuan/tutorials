package com.zqh.hadoop.mr.GTK;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

/**
 * 
 * @author Deepika Mohan
 * 
 * Description : MapReduce program to solve the single-source shortest path problem using parallel breadth-first search. This program also illustrates how to perform iterative map-reduce.
 *         
 * 
 * The single source shortest path is implemented by using Breadth-first search concept.
 * 
 * Reference : http://www.johnandcailin.com/blog/cailin/breadth-first-graph-search-using-iterative-map-reduce-algorithm
 * 
 * Hadoop version used : 0.20.2
 */

public class SSSPJob extends ExampleBaseJob {

	//counter to determine the number of iterations or if more iterations are required to execute the map and reduce functions
	
	static enum MoreIterations {
		numberOfIterations
	}

	/**
	 * 
	 * Description : Mapper class that implements the map part of Single-source shortest path algorithm. It extends the SearchMapper class. 
	 * The map method calls the super class' map method.
	 *  Input format : nodeID<tab>list_of_adjacent_nodes|distance_from_the_source|color|parent

	 *      
	 * Reference : http://www.johnandcailin.com/blog/cailin/breadth-first-graph-search-using-iterative-map-reduce-algorithm
	 * 
	 *      
	 */
	public static class SearchMapperSSSP extends SearchMapper {

		
		public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException {
		
			Node inNode = new Node(value.toString());
			//calls the map method of the super class SearchMapper
			super.map(key, value, context, inNode);

		}
	}
/**
 * 
 * Description : Reducer class that implements the reduce part of the Single-source shortest path algorithm. This class extends the SearchReducer class that implements parallel breadth-first search algorithm. 
 *      The reduce method implements the super class' reduce method and increments the counter if the color of the node returned from the super class is GRAY.   
 * 
   
 *      
 */	

	// the type parameters are the input keys type, the input values type, the
	// output keys type, the output values type

public static class SearchReducerSSSP extends SearchReducer{


	//the parameters are the types of the input key, the values associated with the key and the Context object through which the Reducer communicates with the Hadoop framework

	
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		   //create a new out node and set its values
			Node outNode = new Node();
			
			//call the reduce method of SearchReducer class 
			outNode = super.reduce(key, values, context, outNode);										

			//if the color of the node is gray, the execution has to continue, this is done by incrementing the counter
			if (outNode.getColor() == Node.Color.GRAY)
				context.getCounter(MoreIterations.numberOfIterations).increment(1L);
	}
}

	
	// method to set the configuration for the job and the mapper and the reducer classes
	private Job getJobConf(String[] args) throws Exception {

		JobInfo jobInfo = new JobInfo() {
			@Override
			public Class<? extends Reducer> getCombinerClass() {
				return null;
			}

			@Override
			public Class<?> getJarByClass() {
				return SSSPJob.class;
			}

			@Override
			public Class<? extends Mapper> getMapperClass() {
				return SearchMapperSSSP.class;
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
				return SearchReducerSSSP.class;
			}
		};
		
		return setupJob("ssspjob", jobInfo);

		
	}

	// the driver to execute the job and invoke the map/reduce functions

	public int run(String[] args) throws Exception {

		int iterationCount = 0; // counter to set the ordinal number of the intermediate outputs

		Job job;
		
		long terminationValue =0;
		
		
		// while there are more gray nodes to process
		
		while(iterationCount <= terminationValue){

			job = getJobConf(args); // get the job configuration
			String input, output;
			
			//setting the input file and output file for each iteration
			//during the first time the user-specified file will be the input whereas for the subsequent iterations
			// the output of the previous iteration will be the input
			if (iterationCount == 0) // for the first iteration the input will be the first input argument
				input = args[0];
			else
				// for the remaining iterations, the input will be the output of the previous iteration
				input = args[1] + iterationCount;

			output = args[1] + (iterationCount + 1); // setting the output file

			FileInputFormat.setInputPaths(job, new Path(input)); // setting the input files for the job
			FileOutputFormat.setOutputPath(job, new Path(output)); // setting the output files for the job

			job.waitForCompletion(true); // wait for the job to complete

			Counters jobCntrs = job.getCounters();
			terminationValue = jobCntrs.findCounter(MoreIterations.numberOfIterations).getValue();//if the counter's value is incremented in the reducer(s), then there are more GRAY nodes to process implying that the iteration has to be continued.
			iterationCount++;
		
		}

		return 0;
	}

	public static void main(String[] args) throws Exception {

		int res = ToolRunner.run(new Configuration(), new SSSPJob(), args);
		if(args.length != 2){
			System.err.println("Usage: <in> <output name> ");
		}
		System.exit(res);
	}

}
