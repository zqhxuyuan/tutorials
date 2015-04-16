package com.zqh.hadoop.dataguru.week02_kpi;

import com.zqh.util.Common;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 hadoop@hadoop:~/github-example/hello-samza$ hadoop fs -ls /output/hadoop/week02/ip
 Found 2 items
 -rw-r--r--   3 hadoop supergroup          0 2015-01-28 10:01 /output/hadoop/week02/ip/_SUCCESS
 -rw-r--r--   3 hadoop supergroup        179 2015-01-28 10:01 /output/hadoop/week02/ip/part-00000
 hadoop@hadoop:~/github-example/hello-samza$ hadoop fs -cat /output/hadoop/week02/ip/part-00000
 /about	1
 /black-ip-list/	2
 /cassandra-clustor/	3
 /finance-rhive-repurchase/	4
 /hadoop-family-roadmap/	5
 /hadoop-hive-intro/	6
 /hadoop-mahout-roadmap/	7
 /hadoop-zookeeper-intro/	8

 */
public class KPIIP {

    public static class KPIIPMapper extends MapReduceBase implements Mapper<Object, Text, Text, Text> {
        private Text word = new Text();
        private Text ips = new Text();

        @Override
        public void map(Object key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            KPI kpi = KPI.filterIPs(value.toString());
            if (kpi.isValid()) {
                word.set(kpi.getRequest());
                ips.set(kpi.getRemote_addr());
                output.collect(word, ips);
            }
        }
    }

    public static class KPIIPReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
        private Text result = new Text();
        private Set<String> count = new HashSet<String>();

        @Override
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            while (values.hasNext()) {
                count.add(values.next().toString());
            }
            result.set(String.valueOf(count.size()));
            output.collect(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        String input = Common.hdfsUrl()+"input/hadoop/week02/";
        String output = Common.hdfsUrl()+"output/hadoop/week02/ip";

        JobConf conf = new JobConf(KPIIP.class);
        conf.setJobName("KPIIP");
        //conf.addResource("classpath:/hadoop/core-site.xml");
        //conf.addResource("classpath:/hadoop/hdfs-site.xml");
        //conf.addResource("classpath:/hadoop/mapred-site.xml");
        
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(Text.class);
        
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        
        conf.setMapperClass(KPIIPMapper.class);
        conf.setCombinerClass(KPIIPReducer.class);
        conf.setReducerClass(KPIIPReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        JobClient.runJob(conf);
        System.exit(0);
    }

}
