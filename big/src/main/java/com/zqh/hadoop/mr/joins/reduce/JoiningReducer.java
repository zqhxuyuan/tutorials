package com.zqh.hadoop.mr.joins.reduce;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * User: Bill Bejeck
 * Date: 6/8/13
 * Time: 9:26 PM
 */
public class JoiningReducer extends Reducer<TaggedKey, Text, NullWritable, Text> {

    private Text joinedText = new Text();
    private StringBuilder builder = new StringBuilder();
    private NullWritable nullKey = NullWritable.get();

    @Override
    protected void reduce(TaggedKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //key包括了joinKey和joinOrder. 由于在Mapper中对每个文件都把joinKey删除掉
        //这里Reducer要把joinKey补上.
        builder.append(key.getJoinKey()).append(",");
        for (Text value : values) {
            builder.append(value.toString()).append(",");
        }
        //同样也要删除最后一个逗号.因为上面的循环中对每个value都加上了逗号
        builder.setLength(builder.length()-1);
        //先输出joinKey, 然后输出所有的value. joinOrder不需要输出,只是用来保证出现的顺序
        joinedText.set(builder.toString());
        //key为空, 这样所有的记录都会发送到一个Reducer上. 实际上就是最后的输出了
        context.write(nullKey, joinedText);
        builder.setLength(0);
    }
}
