package com.zqh.hadoop.mr.joins.map;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.join.TupleWritable;

import java.io.IOException;

/**
 * User: Bill Bejeck
 * Date: 1/23/14
 * Time: 10:42 PM
 */
public class CombineValuesMapper extends Mapper<Text, TupleWritable, NullWritable, Text> {
    private static final NullWritable nullKey = NullWritable.get();
    private Text outValue = new Text();
    private StringBuilder valueBuilder = new StringBuilder();
    private String separator;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        separator = context.getConfiguration().get("separator");
    }

    //使用了CompositeInputFormat.composite对两个文件进行inner join
    //key是两个文件进行连接的连接键, value是一对元组. 分别来自于两个文件参与连接的值(值是除了第一列连接键外的所有其他值)
    @Override
    protected void map(Text key, TupleWritable value, Context context) throws IOException, InterruptedException {
        //连接键作为第一列
        valueBuilder.append(key).append(separator);
        //value是一行文本中除了第一列外的其他所有数据
        for (Writable writable : value) {
            valueBuilder.append(writable.toString()).append(separator);
        }
        valueBuilder.setLength(valueBuilder.length() - 1);
        outValue.set(valueBuilder.toString());
        //Mapper的输出key还是null. 但是outValue包含了连接键和参与连接的值.
        context.write(nullKey, outValue);
        valueBuilder.setLength(0);
    }
}
