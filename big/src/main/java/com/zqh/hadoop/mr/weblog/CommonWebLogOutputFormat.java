package com.zqh.hadoop.mr.weblog;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.ReflectionUtils;

public class CommonWebLogOutputFormat extends
		FileOutputFormat<CommonWebLog,NullWritable> {

	@Override
	public RecordWriter<CommonWebLog, NullWritable> getRecordWriter(
			FileSystem ignored, JobConf job, String name, Progressable progress)
			throws IOException {
		boolean isCompressed = getCompressOutput(job);
		String keyValueSeparator = job.get("mapred.textoutputformat.separator"," ");
		if (!isCompressed) {
			Path file = FileOutputFormat.getTaskOutputPath(job, name);
			FileSystem fs = file.getFileSystem(job);
			FSDataOutputStream fileOut = fs.create(file, progress);
			return new CommonWebLogRecordWriter(fileOut,keyValueSeparator);
		} else {
			Class<? extends CompressionCodec> codecClass = getOutputCompressorClass(job, GzipCodec.class);
			// create the named codec
			CompressionCodec codec = ReflectionUtils.newInstance(codecClass,job);
			// build the filename including the extension
			Path file = FileOutputFormat.getTaskOutputPath(job,
					name + codec.getDefaultExtension());
			FileSystem fs = file.getFileSystem(job);
			FSDataOutputStream fileOut = fs.create(file, progress);
			return new CommonWebLogRecordWriter(new DataOutputStream(
					codec.createOutputStream(fileOut)),keyValueSeparator);
		}
	}
}
