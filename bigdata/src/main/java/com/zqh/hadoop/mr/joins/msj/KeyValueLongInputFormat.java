package com.zqh.hadoop.mr.joins.msj;

/**********************************
 *KeyValueLongInputFormat.java
 *Custom key value format
 **********************************/

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

/**
 * http://stackoverflow.com/questions/13415359/hadoop-compositeinputformat-not-joining-all-data
 *
 * Problem was the KeyValueTextInputFormat.
 * It has key and value both as Text where I should have had LongWritable key and Text value.
 * While I thought this would not be a problem, it seems to fail on this.
 * So I made my own input format based on KeyValueTextInputFormat.
 */
public class KeyValueLongInputFormat extends
        FileInputFormat<LongWritable, Text> implements JobConfigurable {

    private CompressionCodecFactory compressionCodecs = null;

    @Override
    public void configure(JobConf conf) {
        compressionCodecs = new CompressionCodecFactory(conf);
    }

    protected boolean isSplitable(FileSystem fs, Path file) {
        return compressionCodecs.getCodec(file) == null;
    }

    // 记录读取器. 返回结果为自定义的KeyValue类型
    @Override
    public RecordReader<LongWritable, Text> getRecordReader(
            InputSplit genericSplit, JobConf job, Reporter reporter)
            throws IOException {

        reporter.setStatus(genericSplit.toString());
        //创建一个RecordReader
        //InputFormat只负责返回文件要被怎么读取,以及读取后的KeyValue类型
        //具体一行一行地读取是交给MapReduce框架完成的
        return new KeyValueLongLineRecordReader(job, (FileSplit) genericSplit);
    }

}