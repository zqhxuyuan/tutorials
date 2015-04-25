package com.zqh.hadoop.mralgs;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * Created by zqhxuyuan on 15-4-24.
 *
 * http://blog.csdn.net/wzhg0508/article/details/17382541
 *
 * Problem
 ”If two people in a social network have a friend in common, then there is an increased likelihood that they will become friends themselves at some point in the future.“
 ------by  Networks, Crowds, and Markets: Reasoning About a Highly Connected World

 也就是说如果B和C有一个共同好友A，那么未来B和C成为好友的可能性会很大，这种叫“三角闭合”原理，我们会发现这种由朋友连接成的图为无向图，因为朋友是相互的。但是像Facebook，微博这种粉丝关系就是有向图，因为你可能follow某明星，但是明星又不会粉你，那么你和明星之间就是单向连接，就是有向图。

 Input
 <user><TAB><comma-separated list of user's friends>

 Output
 我们想向用户U推荐还不是其好友，但是和用户U共享好友的用户，最多推送N个，已共享好友数降序排列。
 <user><TAB><comma-separated list of people the user may know>
 */
public class FriendshipRecommender {

    public static class Map extends Mapper<LongWritable, Text, IntWritable, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] userAndFriends = line.split("\t");
            if (userAndFriends.length == 2) {
                String user = userAndFriends[0];
                IntWritable userKey = new IntWritable(Integer.parseInt(user));
                String[] friends = userAndFriends[1].split(",");
                String friend1;
                IntWritable friend1Key = new IntWritable();
                Text friend1Value = new Text();
                String friend2;
                IntWritable friend2Key = new IntWritable();
                Text friend2Value = new Text();
                for (int i = 0; i < friends.length; i++) {
                    friend1 = friends[i];
                    friend1Value.set("1," + friend1);
                    context.write(userKey, friend1Value);   // Paths of length 1.
                    friend1Key.set(Integer.parseInt(friend1));
                    friend1Value.set("2," + friend1);
                    for (int j = i+1; j < friends.length; j++) {
                        friend2 = friends[j];
                        friend2Key.set(Integer.parseInt(friend2));
                        friend2Value.set("2," + friend2);
                        context.write(friend1Key, friend2Value);   // Paths of length 2.
                        context.write(friend2Key, friend1Value);   // Paths of length 2.
                    }
                }
            }
        }
    }

    public static class Reduce extends Reducer<IntWritable, Text, IntWritable, Text> {
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] value;
            HashMap<String, Integer> hash = new HashMap<String, Integer>();
            for (Text val : values) {
                value = (val.toString()).split(",");
                if (value[0].equals("1")) { // Paths of length 1.
                    hash.put(value[1], -1);
                } else if (value[0].equals("2")) {  // Paths of length 2.
                    if (hash.containsKey(value[1])) {
                        if (hash.get(value[1]) != -1) {
                            hash.put(value[1], hash.get(value[1]) + 1);
                        }
                    } else {
                        hash.put(value[1], 1);
                    }
                }
            }
            // Convert hash to list and remove paths of length 1.
            ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>();
            for (Entry<String, Integer> entry : hash.entrySet()) {
                if (entry.getValue() != -1) {   // Exclude paths of length 1.
                    list.add(entry);
                }
            }
            // Sort key-value pairs in the list by values (number of common friends).
            Collections.sort(list, new Comparator<Entry<String, Integer>>() {
                public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                    return e2.getValue().compareTo(e1.getValue());
                }
            });
            int MAX_RECOMMENDATION_COUNT = 10;
            if (MAX_RECOMMENDATION_COUNT < 1) {
                // Output all key-value pairs in the list.
                context.write(key, new Text(StringUtils.join(list, ",")));
            } else {
                // Output at most MAX_RECOMMENDATION_COUNT keys with the highest values (number of common friends).
                ArrayList<String> top = new ArrayList<String>();
                for (int i = 0; i < Math.min(MAX_RECOMMENDATION_COUNT, list.size()); i++) {
                    top.add(list.get(i).getKey());
                }
                context.write(key, new Text(StringUtils.join(top, ",")));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = new Job(conf, "FriendshipRecommender");
        job.setJarByClass(FriendshipRecommender.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}