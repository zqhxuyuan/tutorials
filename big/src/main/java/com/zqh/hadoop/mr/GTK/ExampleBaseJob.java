package com.zqh.hadoop.mr.GTK;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;

/***
 * 
 * @author Deepika Mohan
 * 
 * This is the Base class for the graph algorithm tool kit.
 * This contains the methods to set the several classes that are related to a job.
 * Each program can set the classes specific to its implementation.
 * 
 * 
 * 
 *
 */
public abstract class ExampleBaseJob extends Configured implements Tool {

	// method to set the configuration for the job and the mapper and the reducer classes
	protected Job setupJob(String jobName,JobInfo jobInfo) throws Exception {
		
		
		Job job = new Job(new Configuration(), jobName);

		// set the several classes
		job.setJarByClass(jobInfo.getJarByClass());

		//set the mapper class
		job.setMapperClass(jobInfo.getMapperClass());

		//the combiner class is optional, so set it only if it is required by the program
		if (jobInfo.getCombinerClass() != null)
			job.setCombinerClass(jobInfo.getCombinerClass());

		//set the reducer class
		job.setReducerClass(jobInfo.getReducerClass());
		
		//the number of reducers is set to 3, this can be altered according to the program's requirements
		job.setNumReduceTasks(3);
	
		// set the type of the output key and value for the Map & Reduce
		// functions
		job.setOutputKeyClass(jobInfo.getOutputKeyClass());
		job.setOutputValueClass(jobInfo.getOutputValueClass());
		
		return job;
	}
	
	protected abstract class JobInfo {
		public abstract Class<?> getJarByClass();
		public abstract Class<? extends Mapper> getMapperClass();
		public abstract Class<? extends Reducer> getCombinerClass();
		public abstract Class<? extends Reducer> getReducerClass();
		public abstract Class<?> getOutputKeyClass();
		public abstract Class<?> getOutputValueClass();
		
 	}
}