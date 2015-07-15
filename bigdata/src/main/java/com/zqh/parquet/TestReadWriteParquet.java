package com.zqh.parquet;

import static java.lang.Thread.sleep;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import  org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import parquet.Log;
import parquet.example.data.Group;
import parquet.hadoop.example.GroupWriteSupport;
import parquet.hadoop.example.ExampleInputFormat;
import parquet.hadoop.example.ExampleOutputFormat;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.hadoop.ParquetFileReader;
import parquet.hadoop.metadata.ParquetMetadata;
import parquet.schema.MessageType;
import parquet.schema.MessageTypeParser;
import parquet.schema.Type;

/**
 * Input is a Parquet format, then output the parquet file's metadata and data
 -rw-r--r--  1 zhengqh  staff     0B  6 23 09:53 _SUCCESS
 -rw-r--r--  1 zhengqh  staff   531B  6 23 09:53 _common_metadata
 -rw-r--r--  1 zhengqh  staff   2.2K  6 23 09:53 _metadata
 -rw-r--r--  1 zhengqh  staff   2.8K  6 23 09:53 part-m-00000.parquet


 */
public class TestReadWriteParquet  extends Configured implements Tool {
    private static final Log LOG = Log.getLog(TestReadWriteParquet.class);
    /*
     * Read a Parquet record, write a Parquet record
     */
    public static class ReadRequestMap extends Mapper<LongWritable, Group, Void, Group> {
        @Override
        public void map(LongWritable key, Group value, Context context) throws IOException, InterruptedException {
            context.write(null, value);
        }
    }

    public int run(String[] args) throws Exception {
        if(args.length < 2) {
            LOG.error("Usage: " + getClass().getName() + " INPUTFILE OUTPUTFILE [compression]");
            return 1;
        }
        String inputFile = args[0];
        String outputFile = args[1];
        String compression = (args.length > 2) ? args[2] : "none";

        Path parquetFilePath = null;
        // Find a file in case a directory was passed
        RemoteIterator<LocatedFileStatus> it = FileSystem.get(getConf()).listFiles(new Path(inputFile), true);
        while(it.hasNext()) {
            FileStatus fs = it.next();
            if(fs.isFile()) {
                parquetFilePath = fs.getPath();
                break;
            }
        }
        if(parquetFilePath == null) {
            LOG.error("No file found for " + inputFile);
            return 1;
        }
        LOG.info("Getting schema from " + parquetFilePath);

        //获取Parquet文件的元数据. 一个parquet文件,本身记录了元数据, 因为parquet和json文件类似都是self-describe file
        ParquetMetadata readFooter = ParquetFileReader.readFooter(getConf(), parquetFilePath);
        MessageType schema = readFooter.getFileMetaData().getSchema();
        LOG.info(schema);
        //在Parquet中有一个Group的概念
        GroupWriteSupport.setSchema(schema, getConf());

        Job job = new Job(getConf());
        job.setJarByClass(getClass());
        job.setJobName(getClass().getName());
        job.setMapperClass(ReadRequestMap.class);
        job.setNumReduceTasks(0);

        //输入输出格式为Parquet类型
        job.setInputFormatClass(ExampleInputFormat.class);
        job.setOutputFormatClass(ExampleOutputFormat.class);

        CompressionCodecName codec = CompressionCodecName.UNCOMPRESSED;
        if(compression.equalsIgnoreCase("snappy")) {
            codec = CompressionCodecName.SNAPPY;
        } else if(compression.equalsIgnoreCase("gzip")) {
            codec = CompressionCodecName.GZIP;
        }
        LOG.info("Output compression: " + codec);
        ExampleOutputFormat.setCompression(job, codec);

        FileInputFormat.setInputPaths(job, new Path(inputFile));
        FileOutputFormat.setOutputPath(job, new Path(outputFile));

        job.waitForCompletion(true);

        return 0;
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{
                "file:///Users/zhengqh/data/hive_alltypes.parquet",
                "file:///Users/zhengqh/data/parquet-mr-out3"
        };
        try {
            int res = ToolRunner.run(new Configuration(), new TestReadWriteParquet(), args);
            System.exit(res);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(255);
        }
    }
}