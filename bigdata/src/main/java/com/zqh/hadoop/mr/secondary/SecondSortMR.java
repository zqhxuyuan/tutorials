package com.zqh.hadoop.mr.secondary;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zengzhaozheng http://zengzhaozheng.blog.51cto.com/8219051/1379271
 *
 * 用途说明：二次排序mapreduce
 * 需求描述:
 * ---------------输入-----------------
 * sort1,1
 * sort2,3
 * sort2,77
 * sort2,54
 * sort1,2
 * sort6,22
 * sort6,221
 * sort6,20
 * ---------------输出-----------------
 * sort1 1,2
 * sort2 3,54,77
 * sort6 20,22,221
 */
public class SecondSortMR extends Configured  implements Tool {
    private static final Logger logger = LoggerFactory.getLogger(SecondSortMR.class);

    public static class SortMapper extends Mapper<Text, Text, CombinationKey, IntWritable> {
        /**
         * 这里特殊要说明一下，为什么要将这些变量写在map函数外边。
         * 对于分布式的程序，我们一定要注意到内存的使用情况，对于mapreduce框架，
         * 每一行的原始记录的处理都要调用一次map函数，假设，此个map要处理1亿条输
         * 入记录，如果将这些变量都定义在map函数里边则会导致这4个变量的对象句柄线
         * 程非常多（极端情况下将产生4*1亿个句柄，当然java也是有自动的gc机制的，
         * 一定不会达到这么多，但是会浪费很多时间去GC），导致栈内存被浪费掉。
         * 我们将其写在map函数外边，顶多就只有4个对象句柄。
         */
        CombinationKey combinationKey = new CombinationKey();
        Text sortName = new Text();
        IntWritable score = new IntWritable();
        String[] inputString = null;

        @Override
        protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            logger.info("---------enter map function flag---------");
            //过滤非法记录
            if(key == null || value == null || key.toString().equals("") || value.equals("")){
                return;
            }
            sortName.set(key.toString());
            score.set(Integer.parseInt(value.toString()));
            combinationKey.setFirstKey(sortName);
            combinationKey.setSecondKey(score);
            //map输出
            context.write(combinationKey, score);
            logger.info("---------out map function flag---------");
        }
    }

    public static class SortReducer extends Reducer<CombinationKey, IntWritable, Text, Text> {
        StringBuffer sb = new StringBuffer();
        Text sore = new Text();
        /**
         * 这里要注意一下reduce的调用时机和次数:reduce每处理一个分组的时候会调用一
         * 次reduce函数。也许有人会疑问，分组是什么？看个例子就明白了：
         * eg:
         * {{sort1,{1,2}},{sort2,{3,54,77}},{sort6,{20,22,221}}}
         * 这个数据结果是分组过后的数据结构，那么一个分组分别为{sort1,{1,2}}、
         * {sort2,{3,54,77}}、{sort6,{20,22,221}}
         */
        @Override
        protected void reduce(CombinationKey key, Iterable<IntWritable> value, Context context) throws IOException, InterruptedException {
            sb.delete(0, sb.length());//先清除上一个组的数据
            Iterator<IntWritable> it = value.iterator();
            while(it.hasNext()){
                sb.append(it.next()+",");
            }
            //去除最后一个逗号
            if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);
            }
            sore.set(sb.toString());
            context.write(key.getFirstKey(),sore);
            logger.info("---------enter reduce function flag---------");
            logger.info("reduce Input data:{["+key.getFirstKey()+","+ key.getSecondKey()+"],["+sore+"]}");
            logger.info("---------out reduce function flag---------");
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        args=new String[]{
                "/home/hadoop/data/mralgs/secondsort.input",
                "/home/hadoop/data/tmp/secondsort"
        };

        Configuration conf=getConf(); //获得配置文件对象
        Job job=new Job(conf,"SoreSort");
        job.setJarByClass(SecondSortMR.class);

        FileInputFormat.addInputPath(job, new Path(args[0])); //设置map输入文件路径
        FileOutputFormat.setOutputPath(job, new Path(args[1])); //设置reduce输出文件路径

        job.setMapperClass(SortMapper.class);
        job.setReducerClass(SortReducer.class);

        //Map, memory-sort[组合键排序], partition[分区], merge-sort[二次排序] , Group[分组策略], Reduce
        job.setPartitionerClass(DefinedPartition.class); //设置自定义分区策略
        job.setGroupingComparatorClass(DefinedGroupSort.class); //设置自定义分组策略
        job.setSortComparatorClass(DefinedComparator.class); //设置自定义二次排序策略

        job.setInputFormatClass(KeyValueTextInputFormat.class); //设置文件输入格式
        job.setOutputFormatClass(TextOutputFormat.class);//使用默认的output格式

        //设置map的输出key和value类型
        job.setMapOutputKeyClass(CombinationKey.class);
        job.setMapOutputValueClass(IntWritable.class);

        //设置reduce的输出key和value类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.waitForCompletion(true);
        return job.isSuccessful()?0:1;
    }

    public static void main(String[] args) {
        try {
            int returnCode =  ToolRunner.run(new SecondSortMR(),args);
            System.exit(returnCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 自定义组合键
 */
class CombinationKey implements WritableComparable<CombinationKey> {
    private final Logger logger = LoggerFactory.getLogger(CombinationKey.class);
    private Text firstKey;
    private IntWritable secondKey;
    public CombinationKey() {
        this.firstKey = new Text();
        this.secondKey = new IntWritable();
    }
    public Text getFirstKey() {
        return this.firstKey;
    }
    public void setFirstKey(Text firstKey) {
        this.firstKey = firstKey;
    }
    public IntWritable getSecondKey() {
        return this.secondKey;
    }
    public void setSecondKey(IntWritable secondKey) {
        this.secondKey = secondKey;
    }
    @Override
    public void readFields(DataInput dateInput) throws IOException {
        this.firstKey.readFields(dateInput);
        this.secondKey.readFields(dateInput);
    }
    @Override
    public void write(DataOutput outPut) throws IOException {
        this.firstKey.write(outPut);
        this.secondKey.write(outPut);
    }
    /**
     * 自定义比较策略
     * 注意：该比较策略用于mapreduce的第一次默认排序，也就是发生在map阶段的sort小阶段，
     * 发生地点为环形缓冲区(可以通过io.sort.mb进行大小调整)
     * 但是其对我们最终的二次排序结果是没有影响的。二次排序的最终结果是由自定义比较器决定的
     */
    @Override
    public int compareTo(CombinationKey combinationKey) {
        logger.info("-------CombinationKey flag-------");
        return this.firstKey.compareTo(combinationKey.getFirstKey());
    }
}

/**
 * 自定义分区
 */
class DefinedPartition extends Partitioner<CombinationKey,IntWritable> {
    private final Logger logger = LoggerFactory.getLogger(DefinedPartition.class);
    /**
     *  数据输入来源：map输出
     * @author zengzhaozheng
     * @param key map输出键值
     * @param value map输出value值
     * @param numPartitions 分区总数，即reduce task个数
     */
    @Override
    public int getPartition(CombinationKey key, IntWritable value,int numPartitions) {
        logger.info("--------enter DefinedPartition flag--------");
        /**
         * 注意：这里采用默认的hash分区实现方法
         * 根据组合键的第一个值作为分区
         * 这里需要说明一下，如果不自定义分区的话，mapreduce框架会根据默认的hash分区方法，
         * 将整个组合键相等的分到一个分区中，这样的话显然不是我们要的效果
         */
        logger.info("--------out DefinedPartition flag--------");
        /**
         * 此处的分区方法选择比较重要，其关系到是否会产生严重的数据倾斜问题
         * 采取什么样的分区方法要根据自己的数据分布情况来定，尽量将不同key的数据打散
         * 分散到各个不同的reduce进行处理，实现最大程度的分布式处理。
         */
        return (key.getFirstKey().hashCode()&Integer.MAX_VALUE)%numPartitions;
    }
}

/**
 * 自定义二次排序策略
 */
class DefinedComparator extends WritableComparator {
    private final Logger logger = LoggerFactory.getLogger(DefinedComparator.class);

    public DefinedComparator() {
        super(CombinationKey.class, true);
    }

    @Override
    public int compare(WritableComparable combinationKeyOne,
                       WritableComparable CombinationKeyOther) {
        logger.info("---------enter DefinedComparator flag---------");

        CombinationKey c1 = (CombinationKey) combinationKeyOne;
        CombinationKey c2 = (CombinationKey) CombinationKeyOther;

        /**
         * 确保进行排序的数据在同一个区内，如果不在同一个区则按照组合键中第一个键排序
         * 另外，这个判断是可以调整最终输出的组合键第一个值的排序
         * 下面这种比较对第一个字段的排序是升序的，如果想降序这将c1和c2倒过来（假设1）
         */
        if (!c1.getFirstKey().equals(c2.getFirstKey())) {
            logger.info("---------out DefinedComparator flag---------");
            return c1.getFirstKey().compareTo(c2.getFirstKey()); //①
        } else {//按照组合键的第二个键的升序排序，将c1和c2倒过来则是按照数字的降序排序(假设2)
            logger.info("---------out DefinedComparator flag---------");
            return c1.getSecondKey().get() - c2.getSecondKey().get();//②
        }
        /**
         * （1）按照上面的这种实现最终的二次排序结果为： key升序①[key不相同时key升序],value升序[key相同时,value升序]
         * sort1    1,2
         * sort2    3,54,77
         * sort6    20,22,221
         * （2）如果实现假设1，则最终的二次排序结果为:  key降序[让①的比较反过来],value升序[②不变]
         * sort6    20,22,221
         * sort2    3,54,77
         * sort1    1,2
         * （3）如果实现假设2，则最终的二次排序结果为:  key升序[①不变],value降序[②反过来]
         * sort1    2,1
         * sort2    77,54,3
         * sort6    221,22,20
         */
    }
}

/**
 * 自定义分组策略: 将组合键中第一个值相同的分在一组
 */
class DefinedGroupSort extends WritableComparator{
    private final Logger logger = LoggerFactory.getLogger(DefinedGroupSort.class);

    public DefinedGroupSort() {
        super(CombinationKey.class,true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        logger.info("-------enter DefinedGroupSort flag-------");
        CombinationKey ck1 = (CombinationKey)a;
        CombinationKey ck2 = (CombinationKey)b;
        logger.info("-------Grouping result:"+ck1.getFirstKey().
                compareTo(ck2.getFirstKey())+"-------");
        logger.info("-------out DefinedGroupSort flag-------");
        return ck1.getFirstKey().compareTo(ck2.getFirstKey());
    }
}