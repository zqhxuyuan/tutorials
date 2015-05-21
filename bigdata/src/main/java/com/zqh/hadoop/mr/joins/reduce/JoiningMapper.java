package com.zqh.hadoop.mr.joins.reduce;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.List;

/**
 * User: Bill Bejeck
 * Date: 6/8/13
 * Time: 10:12 PM
 */
public class JoiningMapper extends Mapper<LongWritable, Text, TaggedKey, Text> {

    private int keyIndex;
    private Splitter splitter;
    private Joiner joiner;
    private TaggedKey taggedKey = new TaggedKey();
    private Text data = new Text();
    private int joinOrder;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        keyIndex = Integer.parseInt(context.getConfiguration().get("keyIndex"));
        //输入文件以分隔符区分列,所以要构建一个分割器,来读取文件内容
        String separator = context.getConfiguration().get("separator");
        splitter = Splitter.on(separator).trimResults();
        joiner = Joiner.on(separator);

        FileSplit fileSplit = (FileSplit)context.getInputSplit();
        //config.set(fileName, joinOrder) : the joinOrder is the tag
        joinOrder = Integer.parseInt(context.getConfiguration().get(fileSplit.getPath().getName()));
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //将一行文本按照分隔符分割后,相当于String.Split(",")返回字符串数组,表示这行的每个字符串
        List<String> values = Lists.newArrayList(splitter.split(value.toString()));
        //为什么要移除进行连接的列,因为两个文件都有一样的连接键,只要出现一次即可.如果是三个文件,也只需要出现一次!
        //删除方法删除指定位置即连接键的内容,返回的就是删除的内容
        //两个文件都会删除连接键,只有最后的时候把连接键补上就可以
        String joinKey = values.remove(keyIndex);
        //剩余的都是值: 除了连接键外的所有其他值
        String valuesWithOutKey = joiner.join(values);

        taggedKey.set(joinKey, joinOrder);  //key,tag=joinKey,joinOrder
        data.set(valuesWithOutKey);         //value
        context.write(taggedKey, data);     //[key,tag], value
    }

}
