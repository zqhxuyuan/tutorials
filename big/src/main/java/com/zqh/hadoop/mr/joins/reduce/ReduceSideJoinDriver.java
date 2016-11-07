package com.zqh.hadoop.mr.joins.reduce;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * User: Bill Bejeck
 * Date: 6/11/13
 * Time: 9:27 PM
 */
public class ReduceSideJoinDriver {


    public static void main(String[] args) throws Exception {
        args = new String[]{
                "/home/hadoop/data/mralgs/userInfo",
                "/home/hadoop/data/mralgs/userCard",
                "/home/hadoop/tmp/reduce-side-join"
        };

        //路径分隔符
        Splitter splitter = Splitter.on('/');
        StringBuilder filePaths = new StringBuilder();

        Configuration config = new Configuration();
        config.set("keyIndex", "0");	//文件中第一列作为连接键
        config.set("separator", ",");	//使用逗号作为分隔符

        //注意结束的长度不是args.length, 而是args.length-1, 所以最后一个参数不会在这里处理
        for(int i = 0; i< args.length - 1; i++) {
            //以/分割的最后一个就是文件名. 如果指定的输入是文件夹呢?
            String fileName = Iterables.getLast(splitter.split(args[i]));
            //文件名出现的顺序. 比如<userInfo,1>, <userCard,2>
            //其中第二个是joinOrder, 即记录的tag. 确保userInfo的记录会出现在userCard之前
            config.set(fileName, Integer.toString(i+1));
            ///home/hadoop/data/mralgs/userInfo,/home/hadoop/data/mralgs/userCard,
            filePaths.append(args[i]).append(",");
        }

        //去掉最后一个逗号
        filePaths.setLength(filePaths.length() - 1);
        Job job = Job.getInstance(config, "ReduceSideJoin");
        job.setJarByClass(ReduceSideJoinDriver.class);

        //输入路径. 如果是多个文件, 要以逗号分割. 或者可以用MultiXXX,
        //一般使用MultiXX是每个输入路径对应不同的Mapper.
        //而这里多个输入文件的Mapper是一样的,所以使用addInputPath的复数形式
        FileInputFormat.addInputPaths(job, filePaths.toString());
        //输出路径是最后一个参数
        FileOutputFormat.setOutputPath(job, new Path(args[args.length-1]));

        job.setMapperClass(JoiningMapper.class);
        job.setReducerClass(JoiningReducer.class);
        //没有指定mapper output key和value??
        job.setOutputKeyClass(TaggedKey.class);
        job.setOutputValueClass(Text.class);

        //分区器
        job.setPartitionerClass(TaggedJoiningPartitioner.class);
        //分组: 在Reducer之前对key进行分组, 确保相同key的所有记录被同一个reduce函数调用
        job.setGroupingComparatorClass(TaggedJoiningGroupingComparator.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
