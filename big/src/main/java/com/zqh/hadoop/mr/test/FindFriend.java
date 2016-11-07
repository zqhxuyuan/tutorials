package com.zqh.hadoop.mr.test;

/**
 * Created by zqhxuyuan on 15-3-10.
 */
import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 第一字母表示本人，其他是他的朋友，找出有共同朋友的人，和共同朋友是谁
 A B C D E F
 B A C D E
 C A B E
 D A B E
 E A B C D
 F A
 输出结果:
 AB	E:C:D
 AC	E:B
 AD	B:E
 AE	C:B:D
 BC	A:E
 BD	A:E
 BE	C:D:A
 BF	A
 CD	E:A:B
 CE	A:B
 CF	A
 DE	B:A
 DF	A
 EF	A
 */
public class FindFriend {

    public static class ChangeMapper extends Mapper<Object, Text, Text, Text>{
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // value=A B C D E F
            StringTokenizer itr = new StringTokenizer(value.toString());

            // 自己=A
            Text owner = new Text();
            // 朋友集合=B C D E F
            Set<String> set = new TreeSet<String>();

            owner.set(itr.nextToken());
            while (itr.hasMoreTokens()) {
                set.add(itr.nextToken());
            }

            String[] friends = new String[set.size()];
            friends = set.toArray(friends);

            for(int i=0;i<friends.length;i++){
                for(int j=i+1;j<friends.length;j++){
                    //value:A B C D E F,   BC BD BE BF CD CE CF DE DF EF --> A
                    //value:B A C D E      AC AD AE CD CE DE --> B
                    //value:C A B E        AB AE BE --> C
                    //value:D A B E        AB AE BE --> D
                    //value:E A B C D      AB AC AD BC BD CD --> E
                    //value:F A

                    // Reduce:
                    // AB: C, D, E
                    // AC: B, E

                    // C的朋友有A,B
                    // D的朋友有A,B
                    // 则C,D的共同朋友是A, B
                    // 即共同朋友为AB的人有C,D
                    String outputkey = friends[i]+friends[j];
                    context.write(new Text(outputkey),owner);
                }
            }
        }
    }

    public static class FindReducer extends Reducer<Text,Text,Text,Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String  commonfriends ="";
            for (Text val : values) {
                if(commonfriends == ""){
                    commonfriends = val.toString();
                }else{
                    commonfriends = commonfriends+":"+val.toString();
                }
            }
            context.write(key, new Text(commonfriends));
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        args = new String[]{"/home/hadoop/IdeaProjects/go-bigdata/helloworld/data/hadoop/mr/friend.txt",
        "/home/hadoop/tmp/friend"};
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        Job job = new Job(conf, "word count");
        job.setJarByClass(FindFriend.class);
        job.setMapperClass(ChangeMapper.class);
        job.setCombinerClass(FindReducer.class);
        job.setReducerClass(FindReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[otherArgs.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

}