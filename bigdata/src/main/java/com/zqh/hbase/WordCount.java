package com.zqh.hbase;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class WordCount {

    // 实现 Map 类
    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    // 实现 Reduce 类
    public static class Reduce extends TableReducer<Text, IntWritable, NullWritable> {

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;

            Iterator<IntWritable> iterator = values.iterator();
            while (iterator.hasNext()) {
                sum += iterator.next().get();
            }

            // Put 实例化，每个词存一行
            Put put = new Put(Bytes.toBytes(key.toString()));
            // 列族为 content，列修饰符为 count，列值为数目
            put.add(Bytes.toBytes("content"), Bytes.toBytes("count"), Bytes.toBytes(String.valueOf(sum)));

            context.write(NullWritable.get(), put);
        }
    }

    // 创建 HBase 数据表
    public static void createHBaseTable(String tableName) throws IOException {
        // 创建表描述
        HTableDescriptor htd = new HTableDescriptor(tableName);
        // 创建列族描述
        HColumnDescriptor col = new HColumnDescriptor("content");
        htd.addFamily(col);

        // 配置 HBase
        Configuration conf = HBaseConfiguration.create();

        conf.set("hbase.zookeeper.quorum","master");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        HBaseAdmin hAdmin = new HBaseAdmin(conf);

        if (hAdmin.tableExists(tableName)) {
            System.out.println("该数据表已经存在，正在重新创建。");
            hAdmin.disableTable(tableName);
            hAdmin.deleteTable(tableName);
        }

        System.out.println("创建表：" + tableName);
        hAdmin.createTable(htd);
    }

    public static void main(String[] args) throws Exception {
        String tableName = "wordcount";
        // 第一步：创建数据库表
        WordCount.createHBaseTable(tableName);

        // 第二步：进行 MapReduce 处理
        // 配置 MapReduce
        Configuration conf = new Configuration();
        // 这几句话很关键
        conf.set("mapred.job.tracker", "master:9001");
        conf.set("hbase.zookeeper.quorum","master");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set(TableOutputFormat.OUTPUT_TABLE, tableName);

        Job job = new Job(conf, "New Word Count");
        job.setJarByClass(WordCount.class);

        // 设置 Map 和 Reduce 处理类
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        // 设置输出类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 设置输入和输出格式
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TableOutputFormat.class);

        // 设置输入目录
        FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/in/"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
