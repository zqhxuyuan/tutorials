package com.zqh.hadoop.dataguru.week07_token;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

public class MyInputFormat extends CombineFileInputFormat<Text, Text> {

	/**
	 *   make sure file will not be splitted
	 */
	@Override
	protected boolean isSplitable(JobContext context, Path file) {
		return false;
	}


	/**
	 *   specify record reader
	 */
	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
		CombineFileRecordReader<Text, Text> recordReader = 	new CombineFileRecordReader<Text, Text>(
				(CombineFileSplit)split, context, MyRecordReader.class);
		return recordReader;
	}

}



