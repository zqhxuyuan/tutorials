package com.zqh.hadoop.mr.joins.reduce;

import com.google.common.collect.ArrayListMultimap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.List;

/**
 * User: Bill Bejeck
 * Date: 6/8/13
 * Time: 9:26 PM
 */
public class CachingJoiningReducer extends Reducer<TaggedKey, Text, Text, Text> {

    private Text joinedText = new Text();
    private StringBuilder builder = new StringBuilder();
    private Text currentJoinKey = new Text("NOT_SET");
    private ArrayListMultimap<Text, String> keyValues = ArrayListMultimap.create();

    @Override
    protected void reduce(TaggedKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        if (!key.getJoinKey().equals(currentJoinKey)) {
            if (!currentJoinKey.toString().equals("NOT_SET")) {
                joinAllData();

                //只需要一个joinKey. value是多个文件连接后的结果
                context.write(currentJoinKey, joinedText);

                //每个要关联的文件都要移除joinKey
                keyValues.removeAll(currentJoinKey);
            }

            //由于reducer没有使用分组保证相同joinKey的多个文件记录被同一个reduce()函数调用
            //所以要在同一个reducer的多次reduce()调用之间保持当前处理的key的状态信息
            currentJoinKey.set(key.getJoinKey());
        }
        for (Text value : values) {
            keyValues.put(currentJoinKey, value.toString());
        }
    }

    private void joinAllData() throws IOException, InterruptedException {
        List<String> values = keyValues.get(currentJoinKey);
        if (values != null && !values.isEmpty()) {
            for (String value : values) {
                builder.append(value).append(",");
            }
            builder.setLength(builder.length() - 1);
            joinedText.set(builder.toString());
            builder.setLength(0);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        joinAllData();
        context.write(currentJoinKey,joinedText);
    }
}
