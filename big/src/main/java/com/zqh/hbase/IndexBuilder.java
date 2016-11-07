package com.zqh.hbase;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.MultiTableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * http://blog.csdn.net/hadoop_/article/details/11599799
 */
public class IndexBuilder {

    // 索引表唯一的一列为 INDEX_ROW，其中 INDEX 为列族
    private static final byte[] INDEX_COLUMN = Bytes.toBytes("INDEX");
    private static final byte[] INDEX_QUALIFIER = Bytes.toBytes("ROW");

    // 实现 Map 类
    public static class Map extends
            //hbase-0.98 修改最后一个参数的类型Writable为Put
            Mapper<ImmutableBytesWritable, Result, ImmutableBytesWritable, Put> {

        // 存储了“列名”到“表名——列名”的映射
        // 前者用于获取某列的值，并作为索引表的键值；后者用户作为索引表的表名
        private HashMap<byte[], ImmutableBytesWritable> indexes;
        private byte[] family;

        // 实现 map 函数
        public void map(ImmutableBytesWritable key, Result value,
                        Context context) throws IOException, InterruptedException {
            for (java.util.Map.Entry<byte[], ImmutableBytesWritable> index : indexes
                    .entrySet()) {
                // 获取列名
                byte[] qualifier = index.getKey();
                // 索引表的表名
                ImmutableBytesWritable tableName = index.getValue();
                // 根据“列族：列名”获得元素值
                byte[] newValue = value.getValue(family, qualifier);

                if (newValue != null) {
                    // 以列值作为行健，在列“INDEX：ROW”中插入行健
                    Put put = new Put(newValue);
                    put.add(INDEX_COLUMN, INDEX_QUALIFIER, key.get());

                    // 在 tableName 表上执行 put
                    // 操作使用 MultipleOutputFormat 时，
                    //第二个参数必须是 Put 和 Delete 类型
                    context.write(tableName, put);
                }
            }
        }

        // setup为Mapper中的方法，该方法只在任务初始化时执行一次
        protected void setup(Context context) throws IOException,
                InterruptedException {
            Configuration conf = context.getConfiguration();

            // 通过 Configuration.set()方法传递参数
            String tableName = conf.get("index.tablename");
            String[] fields = conf.getStrings("index.fields");

            // fields 内为需要做索引的列名
            String familyName = conf.get("index.familyname");
            family = Bytes.toBytes(familyName);

            // 初始化 indexes 方法
            indexes = new HashMap<byte[], ImmutableBytesWritable>();

            for (String field : fields) {
                // 如果给 name 做索引，则索引表的名称为“heroes‐name”
                indexes.put(Bytes.toBytes(field),
                        new ImmutableBytesWritable(
                                Bytes.toBytes(tableName + "‐" + field)));
            }
        }
    }

    // 初始化示例数据表——“heroes”
    public static void initHBaseTable(Configuration conf, String tableName)
            throws IOException {
        // 创建表描述
        HTableDescriptor htd = new HTableDescriptor(tableName);
        // 创建列族描述
        HColumnDescriptor col = new HColumnDescriptor("info");

        htd.addFamily(col);

        HBaseAdmin hAdmin = new HBaseAdmin(conf);

        if (hAdmin.tableExists(tableName)) {
            System.out.println("该数据表已经存在，正在重新创建。");
            hAdmin.disableTable(tableName);
            hAdmin.deleteTable(tableName);
        }

        System.out.println("创建表：" + tableName);
        // 创建表
        hAdmin.createTable(htd);
        HTable table = new HTable(conf, tableName);
        System.out.println("向表中插入数据");
        // 添加数据
        addRow(table, "1", "info", "name", "peter");
        addRow(table, "1", "info", "email", "peter@heroes.com");
        addRow(table, "1", "info", "power", "absorb abilities");

        addRow(table, "2", "info", "name", "hiro");
        addRow(table, "2", "info", "email", "hiro@heroes.com");
        addRow(table, "2", "info", "power", "bend time and space");

        addRow(table, "3", "info", "name", "sylar");
        addRow(table, "3", "info", "email", "sylar@heroes.com");
        addRow(table, "3", "info", "power", "hnow how things work");

        addRow(table, "4", "info", "name", "claire");
        addRow(table, "4", "info", "email", "claire@heroes.com");
        addRow(table, "4", "info", "power", "heal");

        addRow(table, "5", "info", "name", "noah");
        addRow(table, "5", "info", "email", "noah@heroes.com");
        addRow(table, "5", "info", "power", "cath the people with ablities");
    }

    // 添加一条数据
    private static void addRow(HTable table, String row,
                               String columnFamily,String column, String value) throws IOException {
        Put put = new Put(Bytes.toBytes(row));
        // 参数出分别：列族、列、值
        put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column),
                Bytes.toBytes(value));
        table.put(put);
    }

    // 创建数据库表
    public static void createIndexTable(Configuration conf,
                                        String tableName) throws Exception {
        // 新建一个数据库管理员
        HBaseAdmin hAdmin = new HBaseAdmin(conf);

        if (hAdmin.tableExists(tableName)) {
            System.out.println("该数据表已经存在，正在重新创建。");
            hAdmin.disableTable(tableName);
            hAdmin.deleteTable(tableName);
        }

        // 新建一个表的描述
        HTableDescriptor tableDesc = new HTableDescriptor(tableName);
        // 在描述里添加列族
        tableDesc.addFamily(new HColumnDescriptor(INDEX_COLUMN));

        // 根据配置好的描述建表
        hAdmin.createTable(tableDesc);
        System.out.println("创建" + tableName + "表成功");
    }

    public static Job configureJob(Configuration conf, String jobName)
            throws IOException {
        Job job = new Job(conf, jobName);
        job.setJarByClass(IndexBuilder.class);

        // 设置 Map 处理类
        job.setMapperClass(Map.class);

        // 设置 Reduce 个数
        job.setNumReduceTasks(0);

        // 设置输入和输出格式
        job.setInputFormatClass(TableInputFormat.class);
        job.setOutputFormatClass(MultiTableOutputFormat.class);

        return job;
    }

    private static String convertScanToString(Scan scan)
            throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(out);
//        scan.write(dos);
//        return Base64.encodeBytes(out.toByteArray());

        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        return Base64.encodeBytes(proto.toByteArray());
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "master");
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        String tableName = "heroes";
        String columnFamily = "info";
        String[] fields = { "name", "power" };
        // 第一步：初始化数据库表
        IndexBuilder.initHBaseTable(conf, tableName);

        // 第二步：创建索引表
        for (String field : fields) {
            IndexBuilder.createIndexTable(conf, tableName + "‐" + field);
        }

        // 第三步：进行 MapReduce 处理
        conf.set("mapred.job.tracker", "master:9001");
        conf.set(TableInputFormat.SCAN, convertScanToString(new Scan()));
        conf.set(TableInputFormat.INPUT_TABLE, tableName);
        // 设置传递属性值
        conf.set("index.tablename", tableName);
        conf.set("index.familyname", columnFamily);
        conf.setStrings("index.fields", fields);

        Job job = IndexBuilder.configureJob(conf, "Index Builder");

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
