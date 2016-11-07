package com.zqh.hadoop.dataguru.week05_rec;

import com.zqh.hadoop.HdfsDAO;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Map;

/**
 hadoop@hadoop:~$ hadoop fs -cat /input/hadoop/week05/step3_1/part-00000
 101	1:5.0
 101	5:4.0
 101	4:5.0
 101	3:2.0
 101	2:2.0
 102	2:2.5
 102	5:3.0
 102	1:3.0
 103	1:2.5
 103	4:3.0
 103	5:2.0
 103	2:5.0
 104	3:4.0
 104	4:4.5
 104	5:4.0
 104	2:2.0
 105	5:3.5
 105	3:4.5
 106	4:4.0
 106	5:4.0
 107	3:5.0

 $ hadoop fs -cat /input/hadoop/week05/step3_2/part-00000
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
public class Step3 {

    public static class Step31_UserVectorSplitterMapper extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private final static IntWritable k = new IntWritable();
        private final static Text v = new Text();

        @Override
        public void map(LongWritable key, Text values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String[] tokens = Recommend.DELIMITER.split(values.toString());
            for (int i = 1; i < tokens.length; i++) {
                String[] vector = tokens[i].split(":");
                int itemID = Integer.parseInt(vector[0]);
                String pref = vector[1];

                k.set(itemID);
                v.set(tokens[0] + ":" + pref);
                output.collect(k, v);
            }
        }
    }

    public static void run1(Map<String, String> path) throws IOException {
        JobConf conf = Recommend.config();

        String input = path.get("Step3Input1");
        String output = path.get("Step3Output1");

        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
        hdfs.rmr(output);

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Step31_UserVectorSplitterMapper.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            job.waitForCompletion();
        }
    }

    public static class Step32_CooccurrenceColumnWrapperMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static Text k = new Text();
        private final static IntWritable v = new IntWritable();

        @Override
        public void map(LongWritable key, Text values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String[] tokens = Recommend.DELIMITER.split(values.toString());
            k.set(tokens[0]);
            v.set(Integer.parseInt(tokens[1]));
            output.collect(k, v);
        }
    }

    public static void run2(Map<String, String> path) throws IOException {
        JobConf conf = Recommend.config();

        String input = path.get("Step3Input2");
        String output = path.get("Step3Output2");

        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
        hdfs.rmr(output);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Step32_CooccurrenceColumnWrapperMapper.class);

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
