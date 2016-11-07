package com.zqh.hadoop.mr.secondary;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

public class RawMapreduce {

    public static void main( String[] args ) throws Exception {
        args = new String[]{
                "/home/hadoop/data/mralgs/transaction",
                "/home/hadoop/data/mralgs/users",
                "/home/hadoop/data/tmp/staging",
                "/home/hadoop/data/tmp/out"
        };

        Path transactions = new Path(args[0]);
        Path users = new Path(args[1]);
        Path staging = new Path(args[2]);
        Path output = new Path(args[3]);
        main(new Configuration(), transactions, users, staging, output );
    }

    // this allows testing easier as I can pass in a configuration object.
    public static void main(Configuration conf, Path transactions, Path users, Path staging, Path output) throws Exception {
        runFirstJob(transactions, users, staging, new Configuration(conf));
        runSecondJob(staging, output, new Configuration(conf));
    }

    protected static void runFirstJob(Path transactions, Path users, Path output, Configuration conf) throws Exception {
        Job job = new Job(conf);
        job.setJarByClass(RawMapreduce.class);
        job.setJobName("Raw Mapreduce Step 1");

        //分区,分组,二次排序
        job.setPartitionerClass(SecondarySort.SSPartitioner.class);
        job.setGroupingComparatorClass(SecondarySort.SSGroupComparator.class);
        job.setSortComparatorClass(SecondarySort.SSSortComparator.class);

        //多个输入源,指定输入路径的时候,要指定Mapper和InputFormat
        MultipleInputs.addInputPath(job, transactions, TextInputFormat.class, TransactionMapper.class);
        MultipleInputs.addInputPath(job, users, TextInputFormat.class, UserMapper.class);
        //一个Reduce输出,分别指定输出路径,输出格式和Reducer
        FileOutputFormat.setOutputPath(job, output);
        job.setReducerClass(JoinReducer.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setMapOutputKeyClass(TextTuple.class);
        job.setMapOutputValueClass(TextTuple.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        if (job.waitForCompletion(true)) return;
        else throw new Exception("First Job Failed");
    }

    protected static void runSecondJob(Path input, Path output, Configuration conf) throws Exception {
        Job job = new Job(conf);
        job.setJarByClass(RawMapreduce.class);
        job.setJobName("Raw Mapreduce Step 2");

        //分区,分组,二次排序
        job.setPartitionerClass(SecondarySort.SSPartitioner.class);
        job.setGroupingComparatorClass(SecondarySort.SSGroupComparator.class);
        job.setSortComparatorClass(SecondarySort.SSSortComparator.class);

        job.setMapperClass(SecondStage.SecondMapper.class);
        job.setReducerClass(SecondStage.SecondReducer.class);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);

        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setMapOutputKeyClass(TextTuple.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        if (job.waitForCompletion(true)) return;
        else throw new Exception("First Job Failed");
    }

    // structure of a user record is this:
    // id, email, language, location
    // in tab delimited format
    // example record:
    // 1  matthew@example.com EN US
    // We're assuming all records are good.
    public static class UserMapper extends Mapper<LongWritable, Text, TextTuple, TextTuple> {
        TextTuple outKey = new TextTuple();
        TextTuple outValue = new TextTuple();

        @Override
        public void map(LongWritable key, Text value, Context context) throws java.io.IOException, InterruptedException {
            String[] record = value.toString().split("\t");
            String uid = record[0];
            String location = record[3];
            //保证用户记录出现在交易记录之前
            outKey.set(uid, "a");

            //标记是location
            outValue.set("location", location);
            context.write(outKey, outValue);
        }
    }

    // a transaction contains these fields:
    // transactionId, productId, userId, purchaseAmount, productDescription
    // data is tab delimited
    public static class TransactionMapper extends Mapper<LongWritable, Text, TextTuple, TextTuple> {
        TextTuple outKey = new TextTuple();
        TextTuple outValue = new TextTuple();

        @Override
        public void map(LongWritable key, Text value, Context context) throws java.io.IOException, InterruptedException {
            String[] record = value.toString().split("\t");
            String productId = record[1];
            String uid = record[2];
            outKey.set(uid, "b");

            //标记是product
            outValue.set("product", productId);
            context.write(outKey, outValue);
        }
    }

    // the first value is location
    // if it's not, we don't have a user record, so we'll
    // record the location as UNKNOWN
    public static class JoinReducer extends Reducer<TextTuple, TextTuple, Text, Text> {

        Text location = new Text("UNKNOWN");

        /**
         * key: 用户记录: userId,a
         *      交易记录: userId,b
         * value: 用户记录: "location",location
         *        交易记录: "product", product
         *
         * 由于输出结果是product->location, 所以key在reduce中并没有用到
         * 但是key是用来保证相同userId的记录(包括用户记录和交易记录)到同一个reduce
         */
        @Override
        public void reduce(TextTuple key, Iterable<TextTuple> values, Context context) throws java.io.IOException, InterruptedException {
            for (TextTuple value: values) {
                //value的left是一个标记位,表示是location还是product. right是记录的值
                //第一个value是location
                if (value.left.toString().equals("location")) {
                    location = new Text(value.right);
                    continue;
                }

                //其他value是product
                Text productId = value.right;
                context.write(productId, location);
            }
        }

    }

    /**
     * 第二个Job
     */
    public static class SecondStage {
        public static class SecondMapper extends Mapper<Text, Text, TextTuple, Text> {
            TextTuple outputKey = new TextTuple();

            @Override
            public void map(Text key, Text value, Mapper.Context context) throws java.io.IOException, InterruptedException {
                outputKey.set(key, value);
                //<productId,location>,location
                //outputKey也是一个组合键. 其中第一个键productId用于分区和分组, 第二个键用于排序
                context.write(outputKey, value);
            }
        }

        public static class SecondReducer extends Reducer<TextTuple, Text, Text, LongWritable> {
            LongWritable valueOut = new LongWritable();

            //相同productId的记录,即使不同location,经过分组(根据productId进行分组)和排序后(location会被排序),
            //相同productId的排序过的locations集合会被同一个reducer[分区]的同一个reduce()[分组]调用
            @Override
            public void reduce(TextTuple product, Iterable<Text> locations, Context context) throws java.io.IOException, InterruptedException {
                //记录前一个处理的location
                String previous = null;
                //记录这个productId的不同的location的数量
                long totalLocations = 0;
                //不需要在内存中记录所有的locations的状态(先保存locations),然后计算不同locations的数量
                //而是使用排序过的locations; 当不同的location出现时,计数器才+1, 如果相同location则计数器维持不变
                for (Text location: locations) {
                    if (previous == null || !location.toString().equals(previous)) {
                        totalLocations += 1;
                        previous = location.toString();
                    }
                }
                //for循环后,这个productId所有的locations都遍历完,并用totalLocations记录不同的locations的数量
                //下一次reduce()调用针对的是不同的productId了. 因为相同productId的所有locations经过分组被同一个reduce()处理
                valueOut.set(totalLocations);
                context.write(product.left, valueOut);
            }
        }
    }
}
