package com.zqh.hadoop.mr.joins.map;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * User: Bill Bejeck
 * Date: 1/19/14
 * Time: 10:33 PM
 */
public class SortByKeyReducer extends Reducer<Text,Text,NullWritable,Text> {

    private static final NullWritable nullKey = NullWritable.get();

    // key是joinKey, values是来自于多个文件中相同joinKey的文本行
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            //reducer的输出key为null, value为每一行文本
            //把key即joinKey扔掉了! 实际上value中也包含了joinKey, 即value的第一列就是joinKey
            context.write(nullKey,value);
        }
    }
}
