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
 * This is a MapReduce implementation for determining the topological ordering in a given graph.
 * The program for topological ordering can be implemented
 * by a modification of the single-source shortest path problem.
 * 
 * Reference :
 *         http://www.johnandcailin.com/blog/cailin/breadth-first-graph
 *         -search-using-iterative-map-reduce-algorithm
 * 
 * Assumptions: The input graph is a directed acyclic graph
 * Hadoop version used : 0.20.2
 */

public class TopologicalJob extends ExampleBaseJob {

	public static enum MoreIterations {
		numberOfIterations // counter to determine of more iterations are needed
	}
	/**
	 * 
	 * Description : Mapper class that implements the map part of  Topological ordering algorithm. It extends the SearchMapper class.
	 * The map method calls the super class' map method.
	 *  Input format : nodeID<tab>list_of_adjacent_nodes|distance_from_the_source|color|parent

	 *      
	 * Reference : http://www.johnandcailin.com/blog/cailin/breadth-first-graph-search-using-iterative-map-reduce-algorithm
	 * 
	 *      
	 */
	public static class TopologicalMapper extends SearchMapper {

		
		public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException {
		
			Node inNode = new Node(value.toString());
			//calls the map method of the super class SearchMapper
			super.map(key, value, context, inNode);

		}
	}
	
	
 /** 
 *         Description : Reducer class that implements the reduce part of Topological sort
 *         algorithm. Make a new node which combines all information for this single node id that is for each key. The new node should have the full list of edges, the topological ordering, the darkest Color, and the parent/predecessor node 
 * 
 *         Input format <key, value> : <nodeID,  list_of_edges|order in the topological sorting|color>
 *
 *		   Output format <key, value> : <nodeID, (updated) list_of_edges|order in the topological sorting|color|parent node>
 * 
 *         Reference :
 *         http://www.johnandcailin.com/blog/cailin/breadth-first-graph
 *         -search-using-iterative-map-reduce-algorithm
 * 
 *         Hadoop version used : 0.20.2
 */

	public static class TopologicalReducer extends Reducer<Text, Text, Text, Text> {

		/*
		 * Make a new node which combines all information for this single node id.
		 * The new node should have - The full list of edges - The minimum distance
		 * - The darkest Color - parent 
		 */

			
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

		
			//create a new out node and set its values
			Node outNode = new Node();
			//set the key as the node id
			outNode.setId(key.toString());
			

			//the key is the node id and the values are the node information associated with the node
		
			for (Text value : values) {
				

				Node inNode = new Node(key.toString() + "\t" + value.toString());


                // One (and only one) copy of the node will be the fully expanded version, which includes the list of adjacent nodes, in other cases, the mapper emits the nodes with no adjacent nodes
                //In other words, when there are multiple values associated with the key (node Id), only one will have the complete list of adjacent nodes.
				if (inNode.getEdges().size() > 0) {
					outNode.setEdges(inNode.getEdges());
				}
				

				//  if the new distance is not infinity and if the new distance is greater than the current distance or if the current distance is infinity, update the distance value
				//this is to take care of multiple dependencies and update the order based on the last dependency, for example if node3 is connected to node1 and node2 and node1 
                            // has an order of 1 and node2 has an order of 2, then node3 should have an order of 3 (node2's distance+1), since node2 is the parent with the highest order.
				if ((inNode.getDistance()!= Integer.MAX_VALUE && inNode.getDistance() > outNode.getDistance()) || outNode.getDistance() == Integer.MAX_VALUE) {
					
					outNode.setDistance(inNode.getDistance());
					
					//set the corresponding node from whom the distance was obtained as the parent
					outNode.setParent(inNode.getParent());
				}	

				
				// Save the darkest color
				if (inNode.getColor().ordinal() > outNode.getColor().ordinal()) {
					outNode.setColor(inNode.getColor());
				}
						
			}
					
					
			context.write(key, new Text(outNode.getNodeInfo()));

			
			//if the color of the node is gray, the execution has to continue, this is done by incrementing the counter
			//otherwise the counter value remains unchanged, if there is no GRAY node, the counter value remains at 0 and so the iteration will not continue
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
				return TopologicalJob.class;
			}

			@Override
			public Class<? extends Mapper> getMapperClass() {
				return TopologicalMapper.class;
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
				return TopologicalReducer.class;
			}
		};
		
		return setupJob("topologicalsort", jobInfo);
	}

	// the driver to execute the job and invoke the map/reduce functions

	public int run(String[] args) throws Exception {

		int iterationCount = 0; // counter to set the ordinal number of the intermediate outputs

		Job job;

		long terminationValue = 0;
		// while there are more gray nodes to process
		while (iterationCount <= terminationValue) {

			job = getJobConf(args); // get the job configuration
			String input;
			if (iterationCount == 0) // for the first iteration , the input will be the input_graph
				input = args[0];
			else
				// for the remaining iterations, the input will be the output of the previous iteration
				input = args[1] + iterationCount;

			String output = args[1] + (iterationCount + 1); // setting the output file

			FileInputFormat.setInputPaths(job, new Path(input)); // setting the input files for the job
			FileOutputFormat.setOutputPath(job, new Path(output)); // setting the output files for the job

			iterationCount++;

			job.waitForCompletion(true); // wait for the job to complete

			Counters jobCntrs = job.getCounters();
			terminationValue = jobCntrs.findCounter(MoreIterations.numberOfIterations).getValue(); //the counter is used to determine how many iterations are needed, this is similar to a global variable between the reducer and the driver

		}

		return 0;
	}

	public static void main(String[] args) throws Exception {

		int res = ToolRunner.run(new Configuration(), new TopologicalJob(), args);
		System.exit(res);
	}

}