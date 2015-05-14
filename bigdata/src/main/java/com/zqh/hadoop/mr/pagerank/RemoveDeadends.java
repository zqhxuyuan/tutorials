package com.zqh.hadoop.mr.pagerank;

/**
 * Created by zqhxuyuan on 15-3-4.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class RemoveDeadends {

    enum myCounters{
        NUMNODES;
    }

    private static int cpt = 0;

    private final static String TYPE_P = "P";
    private final static String TYPE_S = "S";

    static class Map extends Mapper<LongWritable, Text, Text, Text> {

        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
        {
            String[] values  = value.toString().split("\\s+");
            Text pred = new Text(values[0]);
            Text suc = new Text(values[1]);

            context.write(pred, new Text(suc + " " + TYPE_S));
            context.write(suc, new Text(pred + " " + TYPE_P));
        }
    }


    static class Reduce extends Reducer<Text, Text, Text, Text> {

        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{

            ArrayList<String> listPredecessors = new ArrayList<String> ();
            ArrayList<String> listSuccessors = new ArrayList<String> ();

            for (Text value : values)
            {
                String[] splits  = value.toString().split("\\s+");

                //If there is a predecessor
                if(splits[1].equalsIgnoreCase(TYPE_P))
                {
                    listPredecessors.add(splits[0]);
                }
                //If there is a succesor
                else if(splits[1].equalsIgnoreCase(TYPE_S))
                {
                    listSuccessors.add(splits[0]);
                }

            }

            //If there are successors
            if (!listSuccessors.isEmpty()) {
                if (!(listPredecessors.size() == 1
                        && listSuccessors.size()==1
                        && listPredecessors.get(0).equalsIgnoreCase(key.toString()))) {
                    //Increment the counter
                    Counter c = context.getCounter(myCounters.NUMNODES);
                    c.increment(1);
                    for (String pred : listPredecessors)
                    {
                        //Return predecessor and successor
                        context.write(new Text(pred), key);
                    }
                }
            }

        }
    }

    public static void job(Configuration conf) throws IOException, ClassNotFoundException, InterruptedException{


        boolean existDeadends = true;

        //you don't need to use or create other folders besides the two listed below
        String intermediaryDir = conf.get("intermediaryResultPath");
        String currentInput = conf.get("processedGraphPath");


        FileUtils.copyDirectory(new File(conf.get("graphPath")), new File(conf.get("processedGraphPath")));
        long nNodes = conf.getLong("numNodes", 0);


        Job job = Job.getInstance(conf);
        job.setJobName("deadends job");

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(currentInput));
        FileOutputFormat.setOutputPath(job, new Path(intermediaryDir));
        job.waitForCompletion(true);

        Counters counters = job.getCounters();
        Counter c = counters.findCounter(myCounters.NUMNODES);

        if (c.getValue() == nNodes) {
            existDeadends = false;
        } else {
            nNodes = c.getValue();
            conf.setLong("numNodes", nNodes);
        }


        while(existDeadends)
        {
            //Delete currentInput
            FileUtils.deleteDirectory(new File(currentInput));
            //Copy currentInput in currentInput
            FileUtils.copyDirectory(new File(intermediaryDir), new File(currentInput));
            FileUtils.deleteDirectory(new File(intermediaryDir));

            job = Job.getInstance(conf);
            job.setJobName("deadends job");

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            job.setMapperClass(Map.class);
            job.setReducerClass(Reduce.class);

            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TextOutputFormat.class);

            FileInputFormat.setInputPaths(job, new Path(currentInput));
            FileOutputFormat.setOutputPath(job, new Path(intermediaryDir));
            job.waitForCompletion(true);

            counters = job.getCounters();
            c = counters.findCounter(myCounters.NUMNODES);

            if (c.getValue() == nNodes) {
                existDeadends = false;

            } else {
                nNodes = c.getValue();
                conf.setLong("numNodes", nNodes);
            }

        }

    }

}