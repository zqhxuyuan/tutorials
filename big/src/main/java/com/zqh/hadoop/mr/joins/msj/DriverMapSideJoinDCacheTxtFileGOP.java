package com.zqh.hadoop.mr.joins.msj;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DriverMapSideJoinDCacheTxtFileGOP extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.printf("Two parameters are required for DriverMapSideJoinDCacheTxtFileGOP- <input dir> <output dir>\n");
            return -1;
        }

        Job job = new Job(getConf());
        job.setJobName("Map-side join with text lookup file in DCache-GenericOptionsParser");

        job.setJarByClass(DriverMapSideJoinDCacheTxtFileGOP.class);
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setMapperClass(MapperMapSideJoinDCacheTextFileGOP.class);

        job.setNumReduceTasks(0);

        boolean success = job.waitForCompletion(true);
        return success ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(), new DriverMapSideJoinDCacheTxtFileGOP(), args);
        System.exit(exitCode);
    }
}