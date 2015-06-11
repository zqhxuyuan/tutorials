package cn.tongdun.hadoop;

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

/**
 * Created by zhengqh on 15/6/10.
 *
 * How To Run Yarn Application On Remote Hadoop Cluster?
 * There are some ways :
 *
 * 1. Package your app jar, then put it to remote hadoop cluster, and run app on remote machine
 * 2. Just Run this Application on IDEA. But there are some setup tips to know.
 *
 * 运行结果验证:
 * 运行成功后,会在远程的Application上显示,但是注意作业的ApplicationType仍然是MAPREDUCE.
 *
 * Reference:
 * http://blog.csdn.net/liuxingjiaofu/article/details/7094131
 * http://blog.csdn.net/mercedesqq/article/details/16885115
 * http://sgq0085.iteye.com/blog/1879442
 * http://blog.novacloud.com/post/java-remote-submit-mapreduce-job-with-third-party-jars.html
 *
 * KeyWord:
 * hadoop远程提交作业
*/
public class RemoteWordCount {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

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

    public static void main(String[] args) throws Exception{
        //在IDEA的运行窗口(Edit Configuration)的Program Argument中设置Main函数的参数
        //args = new String[]{"hdfs://192.168.6.52:9000/user/zhengqh/README.txt", "hdfs://192.168.6.52:9000/user/zhengqh/output/wc"};

        Configuration conf = new Configuration();
        //把配置文件加到resources中即可,这里并不需要addResource
        //conf.addResource("core-site.xml");
        //conf.addResource("hdfs-site.xml");
        //conf.addResource("mapred-site.xml");
        //conf.addResource("yarn-site.xml");

        //通过Configuration手动设置
        //注意一旦手动设置,在配置文件中不需要添加这些配置项
        setClasspath(conf);

        //设置运行模式为YARN
        conf.set("mapreduce.framework.name", "yarn");

        //设置运行的Jar包,否则会报错说找不到Mapper.
        //注意jar包的路径是IDEA打包后的本地路径.
        conf.set("mapred.jar","/Users/zhengqh/IdeaProjects/bigdata/out/artifacts/wordcount.jar");

        //验证下是否成功加载进来? 两个配置项分别是通过Configuration手动设置的yarn.application.classpath以及配置文件中的fs.defaultFS
        System.out.println(conf.get("yarn.application.classpath"));
        System.out.println(conf.get("fs.defaultFS"));

        //注意Configuration一定要在Job之前完成!
        Job job = Job.getInstance(conf, "RemoteWordCount");
        //Job job = new Job(conf, "Remote Word Count");
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        job.setJarByClass(RemoteWordCount.class);
        job.setNumReduceTasks(2);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //应用程序的运行参数在Run时调整,主要是输入路径必须存在,输出路径不能存在!
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    //设置应用程序的classpath.
    //注意:这是远程Hadoop集群的配置!
    public static void setClasspath(Configuration conf){
        conf.set("mapreduce.application.classpath",
                "/usr/install/hadoop/etc/hadoop," +
                "/usr/install/hadoop/share/hadoop/common/*," +
                "/usr/install/hadoop/share/hadoop/common/lib/*," +
                "/usr/install/hadoop/share/hadoop/hdfs/*," +
                "/usr/install/hadoop/share/hadoop/hdfs/lib/*," +
                "/usr/install/hadoop/share/hadoop/mapreduce/*," +
                "/usr/install/hadoop/share/hadoop/mapreduce/lib/*," +
                "/usr/install/hadoop/share/hadoop/yarn/*," +
                "/usr/install/hadoop/share/hadoop/yarn/lib/*");

        conf.set("yarn.application.classpath",
                "/usr/install/hadoop/etc/hadoop," +
                "/usr/install/hadoop/share/hadoop/common/*," +
                "/usr/install/hadoop/share/hadoop/common/lib/*," +
                "/usr/install/hadoop/share/hadoop/hdfs/*," +
                "/usr/install/hadoop/share/hadoop/hdfs/lib/*," +
                "/usr/install/hadoop/share/hadoop/mapreduce/*," +
                "/usr/install/hadoop/share/hadoop/mapreduce/lib/*," +
                "/usr/install/hadoop/share/hadoop/yarn/*," +
                "/usr/install/hadoop/share/hadoop/yarn/lib/*");
    }

    @Deprecated
    public void setConf(Configuration conf){
        //conf = job.getConfiguration();
        conf.set("mapreduce.framework.name", "yarn");
        //conf.set("fs.default.name", "hdfs://192.168.6.52:9000");
        conf.set("fs.defaultFS","hdfs://tdhdfs");
        //conf.set("ha.zookeeper.quorum","192.168.6.55:2181,192.168.6.56:2181,192.168.6.57:2181");

        conf.set("mapreduce.jobhistory.address", "192.168.6.52:10020");
        conf.set("mapreduce.jobhistory.webapp.address", "192.168.6.52:19888");

        conf.set("yarn.resourcemanager.address", "192.168.6.52:23140");
        conf.set("yarn.resourcemanager.admin.address", "192.168.6.52:23141");
        conf.set("yarn.resourcemanager.scheduler.address", "192.168.6.52:23130");
        conf.set("yarn.resourcemanager.resource-tracker.address", "192.168.6.52:23125");

//        conf.set("yarn.resourcemanager.address.rm1", "192.168.6.52:23140");
//        conf.set("yarn.resourcemanager.admin.address.rm1", "192.168.6.52:23141");
//        conf.set("yarn.resourcemanager.scheduler.address.rm1", "192.168.6.52:23130");
//        conf.set("yarn.resourcemanager.resource-tracker.address.rm1", "192.168.6.52:23125");
//        conf.set("yarn.resourcemanager.address.rm2", "192.168.6.53:23140");
//        conf.set("yarn.resourcemanager.admin.address.rm2", "192.168.6.53:23141");
//        conf.set("yarn.resourcemanager.scheduler.address.rm2", "192.168.6.53:23130");
//        conf.set("yarn.resourcemanager.resource-tracker.address.rm2", "192.168.6.53:23125");

        conf.set("yarn.application.classpath", "$HADOOP_CONF_DIR,"
                +"$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,"
                +"$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,"
                +"$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,"
                +"$YARN_HOME/*,$YARN_HOME/lib/*");
        conf.set("mapred.child.java.opts", "-Xmx1024m");
    }
}
