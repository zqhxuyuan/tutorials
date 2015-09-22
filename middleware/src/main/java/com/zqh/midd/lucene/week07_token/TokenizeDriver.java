package com.zqh.midd.lucene.week07_token;

import com.zqh.util.Common;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**

 由于paoding-analysis和lucene(4.10.2)的版本问题，报错：
 尽管修改了PaodingMaker的lock.close()还是报下面的错误
 java.lang.Exception: java.lang.IllegalStateException: TokenStream contract violation: close() call missing
 at org.apache.hadoop.mapred.LocalJobRunner$Job.runTasks(LocalJobRunner.java:462)
 at org.apache.hadoop.mapred.LocalJobRunner$Job.run(LocalJobRunner.java:522)
 Caused by: java.lang.IllegalStateException: TokenStream contract violation: close() call missing
 at org.apache.lucene.analysis.Tokenizer.setReader(Tokenizer.java:90)
 at org.apache.lucene.analysis.Analyzer$TokenStreamComponents.setReader(Analyzer.java:323)
 at org.apache.lucene.analysis.Analyzer.tokenStream(Analyzer.java:147)
 at com.zqh.hadoop.dataguru.week07_token.TokenizeMapper.map(TokenizeMapper.java:30)
 at com.zqh.hadoop.dataguru.week07_token.TokenizeMapper.map(TokenizeMapper.java:14)
 at org.apache.hadoop.mapreduce.Mapper.run(Mapper.java:145)
 at org.apache.hadoop.mapred.MapTask.runNewMapper(MapTask.java:764)
 at org.apache.hadoop.mapred.MapTask.run(MapTask.java:340)
 at org.apache.hadoop.mapred.LocalJobRunner$Job$MapTaskRunnable.run(LocalJobRunner.java:243)
 at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:471)
 at java.util.concurrent.FutureTask.run(FutureTask.java:262)
 at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
 at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
 at java.lang.Thread.run(Thread.java:745)

 单独使用MRTokennize工程. 引入hadoop-2.5.0所有jar包和lib下的lucene-core-3.1.0和paoding-analysis

 */
public class TokenizeDriver {

	public static void main(String[] args) throws Exception {
		args = new String[]{Common.hdfsUrl()+"input/hadoop/week07/",Common.hdfsUrl()+"output/hadoop/week07/"};

		// set configuration
		Configuration conf = new Configuration();
		conf.setLong("mapreduce.input.fileinputformat.split.maxsize", 4000000);    //max size of Split
		
		Job job = new Job(conf,"Tokenizer");
		job.setJarByClass(TokenizeDriver.class);

	    // specify input format
		job.setInputFormatClass(MyInputFormat.class);
		
        //  specify mapper
		job.setMapperClass(TokenizeMapper.class);
		
		// specify output types
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		// specify input and output DIRECTORIES 
		Path inPath = new Path(args[0]);
		Path outPath = new Path(args[1]);
		FileSystem fs = inPath.getFileSystem(conf);
		FileStatus[] stats = fs.listStatus(inPath);
		for(int i=0; i<stats.length; i++)
			FileInputFormat.addInputPath(job, stats[i].getPath());
        FileOutputFormat.setOutputPath(job,outPath);     //  output path

		// delete output directory
		FileSystem hdfs = outPath.getFileSystem(conf);
		if(hdfs.exists(outPath))
			hdfs.delete(outPath);
		hdfs.close();

		//  run the job
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
	}

}
