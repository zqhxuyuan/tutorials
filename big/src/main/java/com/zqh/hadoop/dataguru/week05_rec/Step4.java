package com.zqh.hadoop.dataguru.week05_rec;

import com.zqh.hadoop.HdfsDAO;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.*;

/**
 $ hadoop fs -cat /input/hadoop/week05/step4/part-00000
 1	107,5.0
 1	106,18.0
 1	105,15.5
 1	104,33.5
 1	103,39.0
 1	102,31.5
 1	101,44.0
 2	107,4.0
 2	106,20.5
 2	105,15.5
 2	104,36.0
 2	103,41.5
 2	102,32.5
 2	101,45.5
 3	107,15.5
 3	106,16.5
 3	105,26.0
 3	104,38.0
 3	103,24.5
 3	102,18.5
 3	101,40.0
 4	107,9.5
 4	106,33.0
 4	105,26.0
 4	104,55.0
 4	103,53.5
 4	102,37.0
 4	101,63.0
 5	107,11.5
 5	106,34.5
 5	105,32.0
 5	104,59.0
 5	103,56.5
 5	102,42.5
 5	101,68.0
 */
public class Step4 {

    public static class Step4_PartialMultiplyMapper extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private final static IntWritable k = new IntWritable();
        private final static Text v = new Text();

        private final static Map<Integer, List<Cooccurrence>> cooccurrenceMatrix = new HashMap<Integer, List<Cooccurrence>>();

        @Override
        public void map(LongWritable key, Text values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String[] tokens = Recommend.DELIMITER.split(values.toString());
            
            String[] v1 = tokens[0].split(":");
            String[] v2 = tokens[1].split(":");

            if (v1.length > 1) {// cooccurrence
                int itemID1 = Integer.parseInt(v1[0]);
                int itemID2 = Integer.parseInt(v1[1]);
                int num = Integer.parseInt(tokens[1]);

                List<Cooccurrence> list = null;
                if (!cooccurrenceMatrix.containsKey(itemID1)) {
                    list = new ArrayList<Cooccurrence>();
                } else {
                    list = cooccurrenceMatrix.get(itemID1);
                }
                list.add(new Cooccurrence(itemID1, itemID2, num));
                cooccurrenceMatrix.put(itemID1, list);
            }

            if (v2.length > 1) {// userVector
                int itemID = Integer.parseInt(tokens[0]);
                int userID = Integer.parseInt(v2[0]);
                double pref = Double.parseDouble(v2[1]);
                k.set(userID);
                for (Cooccurrence co : cooccurrenceMatrix.get(itemID)) {
                    v.set(co.getItemID2() + "," + pref * co.getNum());
                    output.collect(k, v);
                }
            }
        }
    }

    public static class Step4_AggregateAndRecommendReducer extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {
        private final static Text v = new Text();

        @Override
        public void reduce(IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            Map<String, Double> result = new HashMap<String, Double>();
            while (values.hasNext()) {
                String[] str = values.next().toString().split(",");
                if (result.containsKey(str[0])) {
                    result.put(str[0], result.get(str[0]) + Double.parseDouble(str[1]));
                } else {
                    result.put(str[0], Double.parseDouble(str[1]));
                }
            }
            Iterator<String> iter = result.keySet().iterator();
            while (iter.hasNext()) {
                String itemID = iter.next();
                double score = result.get(itemID);
                v.set(itemID + "," + score);
                output.collect(key, v);
            }
        }
    }

    public static void run(Map<String, String> path) throws IOException {
        JobConf conf = Recommend.config();

        String input1 = path.get("Step4Input1");
        String input2 = path.get("Step4Input2");
        String output = path.get("Step4Output");

        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
        hdfs.rmr(output);

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Step4_PartialMultiplyMapper.class);
        conf.setCombinerClass(Step4_AggregateAndRecommendReducer.class);
        conf.setReducerClass(Step4_AggregateAndRecommendReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input1), new Path(input2));
        FileOutputFormat.setOutputPath(conf, new Path(output));
        
        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            job.waitForCompletion();
        }
    }

    public static class Cooccurrence {
        private int itemID1;
        private int itemID2;
        private int num;

        public Cooccurrence(int itemID1, int itemID2, int num) {
            super();
            this.itemID1 = itemID1;
            this.itemID2 = itemID2;
            this.num = num;
        }

        public int getItemID1() {
            return itemID1;
        }

        public void setItemID1(int itemID1) {
            this.itemID1 = itemID1;
        }

        public int getItemID2() {
            return itemID2;
        }

        public void setItemID2(int itemID2) {
            this.itemID2 = itemID2;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

    }
}


