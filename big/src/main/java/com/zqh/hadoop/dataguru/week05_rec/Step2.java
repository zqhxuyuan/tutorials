package com.zqh.hadoop.dataguru.week05_rec;

import com.zqh.hadoop.HdfsDAO;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 $ hadoop fs -cat /input/hadoop/week05/step2/part-00000
 101:101	5
 101:102	3
 101:103	4
 101:104	4
 101:105	2
 101:106	2
 101:107	1
 102:101	3
 102:102	3
 102:103	3
 102:104	2
 102:105	1
 102:106	1
 103:101	4
 103:102	3
 103:103	4
 103:104	3
 103:105	1
 103:106	2
 104:101	4
 104:102	2
 104:103	3
 104:104	4
 104:105	2
 104:106	2
 104:107	1
 105:101	2
 105:102	1
 105:103	1
 105:104	2
 105:105	2
 105:106	1
 105:107	1
 106:101	2
 106:102	1
 106:103	2
 106:104	2
 106:105	1
 106:106	2
 107:101	1
 107:104	1
 107:105	1
 107:107	1

 */
public class Step2 {
    public static class Step2_UserVectorToCooccurrenceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static Text k = new Text();
        private final static IntWritable v = new IntWritable(1);

        @Override
        public void map(LongWritable key, Text values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String[] tokens = Recommend.DELIMITER.split(values.toString());
            for (int i = 1; i < tokens.length; i++) {
                String itemID = tokens[i].split(":")[0];
                for (int j = 1; j < tokens.length; j++) {
                    String itemID2 = tokens[j].split(":")[0];
                    k.set(itemID + ":" + itemID2);
                    output.collect(k, v);
                }
            }
        }
    }

    public static class Step2_UserVectorToConoccurrenceReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            result.set(sum);
            output.collect(key, result);
        }
    }

    public static void run(Map<String, String> path) throws IOException {
        JobConf conf = Recommend.config();

        String input = path.get("Step2Input"); 
        String output = path.get("Step2Output");

        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
        hdfs.rmr(output);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Step2_UserVectorToCooccurrenceMapper.class);
        conf.setCombinerClass(Step2_UserVectorToConoccurrenceReducer.class);
        conf.setReducerClass(Step2_UserVectorToConoccurrenceReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            job.waitForCompletion();
        }
    }
}
