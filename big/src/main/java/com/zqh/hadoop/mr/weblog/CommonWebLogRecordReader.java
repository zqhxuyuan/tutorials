/**
 * 
 */
package com.zqh.hadoop.mr.weblog;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

/**
 * @author training
 *
 */

/*
 * Common Weblog line looks like: 
	  127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326 
 */
public class CommonWebLogRecordReader extends RecordReader<LongWritable, CommonWebLog> {

	// Since the data is coming from a text file, the CommonWebLogRecordReader
	// simply delegates the file handling to the record reader for text files.
	private LineRecordReader delegate;
	private CommonWebLog currentValue;
		
	public CommonWebLogRecordReader(InputSplit split,TaskAttemptContext context) throws IOException, InterruptedException {
		initialize(split,context);
	}

	public CommonWebLogRecordReader() {

	}
	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		delegate = new LineRecordReader();
		delegate.initialize(split, context);
		
	}
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (delegate.nextKeyValue()) {
			if (currentValue==null) {
				currentValue = new CommonWebLog();
			}
			setCurrent(getCurrentKey(), currentValue, delegate.getCurrentValue());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public LongWritable getCurrentKey() throws IOException,
			InterruptedException {
		return delegate.getCurrentKey();
	}

	@Override
	public CommonWebLog getCurrentValue() throws IOException,
			InterruptedException {
		return currentValue;
	}	
	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public float getProgress() throws IOException {
		return delegate.getProgress();
	}



	
	public void setCurrent(LongWritable key, CommonWebLog value, Text lineOfText) {
		value.clear();
		// parse lineOfText into key and value
		String[] tokens = lineOfText.toString().split("\\s+");
		if (tokens.length>0) value.setHost(tokens[0]);
		if (tokens.length>1) value.setRemoteLogName(tokens[1]);
		if (tokens.length>2) value.setUserId(tokens[2]);
		if (tokens.length>4) value.setDate(parseDate(tokens[3],tokens[4]));
		if (tokens.length>5) value.setOperation(parseOperation(tokens[5]));
		if (tokens.length>6) value.setResource(tokens[6]);
		if (tokens.length>7) value.setHttpVersion(parseHttpVersion(tokens[7]));
		try {
			if (tokens.length > 8) value.setReturnCode(Integer.parseInt(tokens[8]));
		} catch (NumberFormatException e) {
			value.setReturnCode(-1);
		}
		try {
			if (tokens.length > 9) value.setReturnObjectSize(Long.parseLong(tokens[9]));
		} catch (NumberFormatException e) {
			value.setReturnObjectSize(-1);
		}
			
	}

	private String parseHttpVersion(String httpVersion) {
		if (httpVersion.endsWith("\"")) {
			return httpVersion.substring(0, httpVersion.length()-1);
		}
		return httpVersion;
	}

	private String parseOperation(String operation) {
		if (operation.startsWith("\"")) {
			return operation.substring(1);
		}
		return operation;
	}

	private Date parseDate(String dateTimeString, String timezone){
		Date date = CommonWebLog.MISSINGDATE;
		try {
			return CommonWebLog.dateFormatter.parse(dateTimeString.substring(1)+" "+timezone.substring(0,timezone.length()-1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}





}
