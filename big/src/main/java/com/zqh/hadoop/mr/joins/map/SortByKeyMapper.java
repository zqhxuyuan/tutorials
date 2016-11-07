package com.zqh.hadoop.mr.joins.map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.List;

/**
 * User: Bill Bejeck
 * Date: 1/19/14
 * Time: 10:08 PM
 */
public class SortByKeyMapper extends Mapper<LongWritable, Text, Text, Text> {

    private int keyIndex;
    private Splitter splitter;
    private Joiner joiner;
    private Text joinKey = new Text();


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        String separator =  context.getConfiguration().get("separator");
        keyIndex = Integer.parseInt(context.getConfiguration().get("keyIndex"));
        splitter = Splitter.on(separator);
        joiner = Joiner.on(separator);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Iterable<String> values = splitter.split(value.toString());

        //从values形成的迭代器中,获取keyIndex的值(一般是第一个即keyIndex=0),作为joinKey
        joinKey.set(Iterables.get(values,keyIndex));

        //一般是keyIndex=0, 如果不是0呢? 比如用于连接的键并不在第一列
        if(keyIndex != 0){
            value.set(reorderValue(values,keyIndex));
        }

        //如果是文本的话,则value是这一行文本. key表示在文件中的偏移量并没有用到. 而是使用joinKey
        //value的第一列是连接键. 即使keyIndex不是0, 也会被交换到第一列上去
        context.write(joinKey,value);
    }

    /**
     * 重新排序value
     * @param value 字符串数组的迭代器
     * @param index 连接键在字符串数组中的位置
     * @return 排序后的字符串
     */
    private String reorderValue(Iterable<String> value, int index){
        List<String> temp = Lists.newArrayList(value);
        //原始字符串数组中第一列
        String originalFirst = temp.get(0);
        //index位置要变成第一列了
        String newFirst = temp.get(index);
        temp.set(0,newFirst);
        //将index的位置和原先第一列进行交换
        temp.set(index,originalFirst);
        return joiner.join(temp);
    }

    public static void main(String[] args) {
        Iterable<String> values = Splitter.on(" ").split("the quick brown fox jump over the quick dog");
        String joinKey = Iterables.get(values,0);
        //System.out.println(joinKey);

        String joinWithCom = Joiner.on(",").join(values);
        System.out.println(joinWithCom);
    }
}
