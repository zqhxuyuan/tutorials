package com.zqh.hadoop.mr.weblog;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.RecordReader;

public class CommonWebLogInputFormat extends FileInputFormat<LongWritable,CommonWebLog>  {

	@Override
	protected boolean isSplitable(JobContext context, Path file) {
	    CompressionCodec codec = 
	      new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
	    return codec == null;
	  }
	
	@Override
	public RecordReader<LongWritable, CommonWebLog> createRecordReader(
			InputSplit genericSplit, TaskAttemptContext context) throws IOException,
			InterruptedException {
		context.setStatus(genericSplit.toString());
	    return new CommonWebLogRecordReader(genericSplit,context);
	}

}
