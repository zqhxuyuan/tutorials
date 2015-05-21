package com.zqh.hadoop.mr.joins.rsj;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

//********************************************************************************
//Class:    DriverRSJ
//Purpose:  Driver for Reduce Side Join of two datasets
//          with a 1..1 or 1..many cardinality on join key
//Author:   Anagha Khanolkar
//*********************************************************************************

public class DriverRSJ extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        // Exit job if required arguments have not been provided
        if (args.length != 3) {
            System.out.printf("Three parameters are required for DriverRSJ- <input dir1> <input dir2> <output dir>\n");
            return -1;
        }

        // Job instantiation
        Job job = new Job(getConf());
        Configuration conf = job.getConfiguration();
        job.setJarByClass(DriverRSJ.class);
        job.setJobName("ReduceSideJoin");

        // Add side data to distributed cache
        DistributedCache.addCacheArchive(new URI("/user/akhanolk/joinProject/data/departments_map.tar.gz"), conf);

        // Set sourceIndex for input files;
        // sourceIndex is an attribute of the compositeKey,
        // to drive order, and reference source
        // Can be done dynamically; Hard-coded file names for simplicity
        conf.setInt("part-e", 1);// Set Employee file to 1
        conf.setInt("part-sc", 2);// Set Current salary file to 2
        conf.setInt("part-sh", 3);// Set Historical salary file to 3

        // Build csv list of input files
        StringBuilder inputPaths = new StringBuilder();
        inputPaths.append(args[0].toString()).append(",").append(args[1].toString());

        // Configure remaining aspects of the job
        FileInputFormat.setInputPaths(job, inputPaths.toString());
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        job.setMapperClass(MapperRSJ.class);
        job.setMapOutputKeyClass(CompositeKeyWritableRSJ.class);
        job.setMapOutputValueClass(Text.class);

        job.setPartitionerClass(PartitionerRSJ.class);
        job.setSortComparatorClass(SortingComparatorRSJ.class);
        job.setGroupingComparatorClass(GroupingComparatorRSJ.class);

        job.setNumReduceTasks(4);
        job.setReducerClass(ReducerRSJ.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        boolean success = job.waitForCompletion(true);
        return success ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(), new DriverRSJ(), args);
        System.exit(exitCode);
    }
}