package com.zqh.hadoop.mr.GTK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Partitioner;


/**
 * 
 * 
 * @author Deepika Mohan Description: This example illustrates the use of a
 *         custom combiner by running the mappers and reducers for augmenting
 *         the edges with degree and enumerating the triads. This application is
 *         useful to write MapReduce program for enumerating the triangles in a
 *         graph.
 * 
 *         Input format : Node1<tab>Node2
 * 
 *         Reference:
 *         http://www.cse.usf.edu/~anda/CIS6930-S11/papers/graph-processing
 *         -w-mapreduce.pdf
 * 
 *         Hadoop version used: 0.20.2
 */
public class EnumerateTriads extends ExampleBaseJob {

	// AugmentDegreeMapper is a part of the job to augment the edges with the
	// degree of the nodes present in it

	// the type parameters are the input keys type, the input values type, the
	// output keys type, the output values type
	public static class AugmentDegreeMapper extends
			Mapper<Object, Text, Text, Text> {

		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			// the input format for the mapper is Node1<tab>Node2
			// the output format for the mapper is in the form of the <key,value> pairs where the key is one of the nodes and the value is the edge
			// for example, for the input Node1<tab>Node2, the mapper will emit : <Node1, <Node1, Node2>> and <Node2, <Node1,Node2>>

			String[] tokens = value.toString().split(",");

			context.write(new Text(tokens[0]), value);
			context.write(new Text(tokens[1]), value);

		}
	}

	// the reducer is used to aggregate the values associated with a particular
	// key.
	// the node is the input key and the values are the edges associated with
	// the node
	// the degree of the node is calculated by iterating through the values and
	// counting the number of edges associated with the node
	// The output of the reducer is in the form of key, value pair where the key
	// is the edge and the value is the degree of one of the ndoes in the edge
	public static class AugmentDegreeReducer extends
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			ArrayList<String> list = new ArrayList<String>();

			int count = 0;
			
			while (values.iterator().hasNext()) {

				list.add(values.iterator().next().toString());

			}

			count = list.size();

			for (String listVal : list) {

				context.write(new Text(listVal), new Text(listVal + "|"
						+ "degree(" + key.toString() + ")" + count + "\t"));
			}
		}
	}

	// The second mapper in the process of augmenting the edges with the
	// degrees. This is an identity mapper where the output key is the edge and
	// the output value is the degree of one of the nodes in the edge
	public static class AugmentDegreeIdentityMapper extends
			Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] tokens = value.toString().split("\\s");

			context.write(new Text(tokens[0]), new Text(tokens[1]));

		}

	}

	// the second reducer in the process of augmenting the edges with the
	// degrees.
	// The edge forms the input and output keys and the degree of each the nodes
	// in the edge forms the value

	public static class AugmentDegreeForEdge_Reducer extends
			Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			String concatenatedData = "";
			boolean first = true;

			for (Text val : values) {

				String[] tokens = val.toString().split("\\|");
				if (first) {

					concatenatedData += tokens[0];
					first = false;
				}
				concatenatedData += "|" + tokens[1];

			}
			context.write(key, new Text(concatenatedData));
		}
	}

	// The mapper class for enumerating the open triads (pairs of edges of the
	// form {(A,B),(B,C)}
	// The mapper records each edge under its low degree member
	// Therefore the output key of the mapper will be the node with the lowest
	// degree and the value would be the edge
	public static class TriadMapper extends
			Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] tokens = value.toString().split("\\s");
			String[] splitTokens = tokens[1].split("\\|");

			String[] node1 = splitTokens[1].split("[()]");

			String[] degreeToken1 = splitTokens[1].split("\\)");
			int degree1 = Integer.parseInt(degreeToken1[1]);

			String[] node2 = splitTokens[2].split("[()]");
			String[] degreeToken2 = splitTokens[2].split("\\)");
			int degree2 = Integer.parseInt(degreeToken2[1]);

			Text edge = new Text(tokens[0]);

			if (degree1 <= degree2) {
				context.write(new Text(node1[1]), edge);
			} else {
				context.write(new Text(node2[1]), edge);
			}

		}

	}
	

	// The combiner is an optimization for the reducer. This combiner eliminates the nodes which are associated with at most one edge
	// Thus the combiner reduces the bandwidth of data flowing to the
	// reducer
	public static class TriadCombiner extends
			Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			// emit the keys that have a degree greater than 1
			ArrayList<String> list = new ArrayList<String>();

			int count = 0;
			while (values.iterator().hasNext()) {

				list.add(values.iterator().next().toString());

			}

			count = list.size();

			if (count > 1) {
				for (String listVal : list) {

					context.write(key, new Text(listVal));
				}

			}
		}
	}

	// the type parameters are the input keys type, the input values type, the
	// output keys type, the output values type

	// The reducer outputs the key, value pair where the key is the outer
	// vertices of the triad associated with it
	// for example, if the input to the reducer is <2, {1,2}> and <2,{2,3}>,
	// then the following reducer will output < {1,3},<{1,2},{2,3}>
	// the output of this reducer can be used as a partial input to the mapper
	// and the reducer to enumerate triangles
	public static class TriadReducer extends
			Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			String concatVal = "";
			Set<String> nodeSet = new HashSet<String>();
			for (Text val : values) {

				String[] valTokens = val.toString().split(",");

				for (int i = 0; i < valTokens.length; i++) {
					nodeSet.add(valTokens[i]);
				}
				concatVal += "|" + val;
			}
			nodeSet.remove(key.toString());

			String nodesKey = " ";

			for (String node : nodeSet) {

				nodesKey += node + ",";
			}
			context.write(new Text(nodesKey), new Text(concatVal));
		}
	}

	public int run(String[] args) throws Exception {

		augmentEdgesWithDegreesFirstJob(args[0], args[1]);
		augmentEdgesWithDegreesSecondJob(args[1], args[2]);

		enumerateTriadsJob(args[2], args[3]);

		return 0;

	}

	// method to execute or run the first job for enumerating the triads
	private void enumerateTriadsJob(String inputPath, String outputPath)
			throws Exception {

		Job Triad_job = getJobConfTriadJob();

		String input = inputPath;
		String output = outputPath;

		FileSystem fs = FileSystem.getLocal(Triad_job.getConfiguration());
		Path opPath = new Path(output);
		fs.delete(opPath, true);

		FileInputFormat.setInputPaths(Triad_job, new Path(input)); // setting the input files for the job
		FileOutputFormat.setOutputPath(Triad_job, new Path(output)); // setting the output files for the job

		Triad_job.waitForCompletion(true);
	}

	// method to get the job configuration for the first job for enumerating the
	// triads
	private Job getJobConfTriadJob() throws Exception {
		JobInfo jobInfo = new JobInfo() {

			@Override
			public Class<? extends Reducer> getReducerClass() {
				return TriadReducer.class;
			}

			@Override
			public Class<?> getOutputValueClass() {
				return Text.class;
			}

			@Override
			public Class<?> getOutputKeyClass() {
				return Text.class;
			}

			@Override
			public Class<? extends Mapper> getMapperClass() {
				return TriadMapper.class;
			}

			@Override
			public Class<?> getJarByClass() {
				return EnumerateTriads.class;
			}

			@Override
			public Class<? extends Reducer> getCombinerClass() {
				return TriadCombiner.class;
			}
		};
        
		
		return setupJob("EnumerateTriadFirst", jobInfo);
	}

	// method to run the second job to augment edges with degree
	private void augmentEdgesWithDegreesSecondJob(String inputPath,
			String outputPath) throws Exception {
		Job augmentDegree_job = getJobConfAugmentDegreeSecond();

		String input = inputPath;
		String output = outputPath;

		FileSystem fs = FileSystem.getLocal(augmentDegree_job
				.getConfiguration());
		Path opPath = new Path(output);
		fs.delete(opPath, true);

		FileInputFormat.setInputPaths(augmentDegree_job, new Path(input)); // setting the input files for the job
		
		FileOutputFormat.setOutputPath(augmentDegree_job, new Path(output)); // setting the output files for the job

		augmentDegree_job.waitForCompletion(true);

	}

	// method to configure the second job to augment edges with degree
	private Job getJobConfAugmentDegreeSecond() throws Exception {
		JobInfo jobInfo = new JobInfo() {

			@Override
			public Class<? extends Reducer> getReducerClass() {
				return AugmentDegreeForEdge_Reducer.class;
			}

			@Override
			public Class<?> getOutputValueClass() {
				return Text.class;
			}

			@Override
			public Class<?> getOutputKeyClass() {
				return Text.class;
			}

			@Override
			public Class<? extends Mapper> getMapperClass() {
				return AugmentDegreeIdentityMapper.class;
			}

			@Override
			public Class<?> getJarByClass() {
				return EnumerateTriads.class;
			}

			@Override
			public Class<? extends Reducer> getCombinerClass() {
				return null;
			}
		};

		return setupJob("AugmentDegreeSecond", jobInfo);
	}

	// method to get the configuration of the job to augment the edges with the
	// degree
	private Job getJobConfAugmentDegreeFirst() throws Exception {
		JobInfo jobInfo = new JobInfo() {

			@Override
			public Class<? extends Reducer> getReducerClass() {
				return AugmentDegreeReducer.class;
			}

			@Override
			public Class<?> getOutputValueClass() {
				return Text.class;
			}

			@Override
			public Class<?> getOutputKeyClass() {
				return Text.class;
			}

			@Override
			public Class<? extends Mapper> getMapperClass() {
				return AugmentDegreeMapper.class;
			}

			@Override
			public Class<?> getJarByClass() {
				return EnumerateTriads.class;
			}

			@Override
			public Class<? extends Reducer> getCombinerClass() {
				return null;
			}
		};

		return setupJob("AugmentDegree", jobInfo);
	}

	// method to run the first job to augment edges with degree
	private void augmentEdgesWithDegreesFirstJob(String inputPath,
			String outputPath) throws Exception {

		Job augmentDegree_job = getJobConfAugmentDegreeFirst();

		String input = inputPath;
		String output = outputPath;

		FileSystem fs = FileSystem.getLocal(augmentDegree_job
				.getConfiguration());
		Path opPath = new Path(output);
		fs.delete(opPath, true);

		FileInputFormat.setInputPaths(augmentDegree_job, new Path(input)); // setting the input files for the job
		 
		FileOutputFormat.setOutputPath(augmentDegree_job, new Path(output)); // setting the output files for the job

		augmentDegree_job.waitForCompletion(true);

	}

	public static void main(String[] args) throws Exception {

		int res = ToolRunner.run(new Configuration(), new EnumerateTriads(),
				args);
		if (args.length != 4) {
			System.err
					.println("Usage: Enumerate triads <inAugmentDegree> <intermediateAugmentdegree> <outputAugmentDegree> <outputTriad>");
			System.exit(2);
		}
		System.exit(res);
	}

}
