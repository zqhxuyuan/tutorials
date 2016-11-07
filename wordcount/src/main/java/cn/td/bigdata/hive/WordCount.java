package cn.td.bigdata.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.StringTokenizer;

public class WordCount {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);

            String cql = context.getConfiguration().get("hadoop.param.str");
            System.out.println("CQL>>>"+cql);
        }

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    //使用hadoop jar包运行: hadoop jar wordcount.jar cn.td.bigdata.hive.WordCount -D hadoop.param.str="abc:"timestamp"," /Users/zhengqh/data/README.txt /Users/zhengqh/data/testsss
    //使用IDE运行时, VM Options: -Dhadoop.param.str="abc:"timestamp","  程序参数为: /Users/zhengqh/data/README.txt /Users/zhengqh/data/testsss
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        // 1. 默认本地文件系统
        //args = new String[]{"/test001","/test001-out"};
        //args = new String[]{"/home/hadoop/data/mralgs/wc", "/home/hadoop/tmp/wc",};
        //args = new String[]{"/Users/zhengqh/data/README.txt", "/Users/zhengqh/data/testsss"};

        // 2. 如果是hdfs，则必须加上hdfs://前缀
        //args = new String[]{"hdfs://127.0.0.1:9000/README.txt","hdfs://127.0.0.1:9000/output/wc3"};
        //args = new String[]{"hdfs://172.17.212.67:8020/test001","hdfs://172.17.212.67:8020/test001-out"};

        // 3. 在远程运行，但是实际上还是在本地起了hadoop.所以实际上没用到远程的hadoop集群
        // 所以我们的目的应该是要把作业提交到远程的Hadoop集群去执行.
        // TODO -> RemoteYarnApp
        //args = new String[]{"hdfs://192.168.6.52:9000/user/qiaoshi.wang/examples/input-data/text", "hdfs://192.168.6.52:9000/user/qiaoshi.wang/examples/output/wc3"};
        //conf.set("mapreduce.framework.name", "yarn");
        //conf.set("yarn.resourcemanager.address", "192.168.6.52:23140");

        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        /*
        if (otherArgs.length != 2) {
          System.err.println("Usage: wordcount <in> <out>");
          System.exit(2);
        }
        */
        Job job = new Job(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setNumReduceTasks(2);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
