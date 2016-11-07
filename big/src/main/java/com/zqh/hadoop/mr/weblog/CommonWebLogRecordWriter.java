package com.zqh.hadoop.mr.weblog;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;

public class CommonWebLogRecordWriter implements
		RecordWriter<CommonWebLog,NullWritable> {

	private DataOutputStream out;
	private String kvs;
	
	public CommonWebLogRecordWriter(DataOutputStream dataOutputStream, String keyValueSeparator) {
		out=dataOutputStream;
		kvs=keyValueSeparator;
	}

	@Override
	public void write(CommonWebLog key, NullWritable value) throws IOException {
		out.writeUTF(key.toString(kvs)+"\n");
	}

	@Override
	public void close(Reporter reporter) throws IOException {
		out.close();
	}

}
