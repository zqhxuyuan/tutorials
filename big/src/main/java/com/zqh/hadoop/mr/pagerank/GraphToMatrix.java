package com.zqh.hadoop.mr.pagerank;

/**
 * Created by zqhxuyuan on 15-3-4.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class GraphToMatrix {

    static class Map extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            String[] values  = value.toString().split("\\s+");
            IntWritable outputKey = new IntWritable(Integer.parseInt(values[0]));
            IntWritable outputValue = new IntWritable(Integer.parseInt(values[1]));
            context.write(outputKey, outputValue);
        }
    }

    static class Reduce extends Reducer<IntWritable, IntWritable, NullWritable, Text> {

        protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{

            Text returnValue;
            NullWritable returnKey = null;

            ArrayList<IntWritable> listSuccessors = new ArrayList<IntWritable> ();
            for (IntWritable value : values)
            {
                listSuccessors.add(new IntWritable(value.get()));
            }

            for (IntWritable suc : listSuccessors)
            {
                returnValue = new Text(suc.toString() + " " + key.toString() + " "+ Double.toString(1.0/listSuccessors.size()));
                context.write(returnKey, returnValue);
            }


        }
    }

    public static void job(Configuration conf) throws IOException, ClassNotFoundException, InterruptedException {
        Job job = Job.getInstance(conf);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(conf.get("processedGraphPath")));
        FileOutputFormat.setOutputPath(job, new Path(conf.get("stochasticMatrixPath")));
        job.waitForCompletion(true);
    }

}