package com.zqh.hadoop.mr.joins.msj;

/**********************************
 *KeyValueLongLineRecordReader.java
 *Custom record reader
 **********************************/

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.LineRecordReader;
import org.apache.hadoop.mapred.RecordReader;

public class KeyValueLongLineRecordReader implements RecordReader<LongWritable, Text> {

    private final LineRecordReader lineRecordReader;
    //key和value的分隔符
    private byte separator = (byte) ',';
    //key? 实际上不是我们要的真正的Key,因为LineRecordReader的key是一行文本在文件中的偏移量.
    //而我们要的是这一行文本的第一列作为key
    private LongWritable dummyKey;
    //value
    private Text innerValue;

    public Class getKeyClass() {
        return LongWritable.class;
    }

    public LongWritable createKey() {
        return new LongWritable();
    }

    public Text createValue() {
        return new Text();
    }

    public KeyValueLongLineRecordReader(Configuration job, FileSplit split) throws IOException {
        lineRecordReader = new LineRecordReader(job, split);
        //初始化Key,Value时, 只是创建一个空的对象. 这两个变量会在next读取文件时被填充
        //由于不需要每次读取一行就重新分配KeyValue对象,所以可以把它们设置为实例变量
        dummyKey = lineRecordReader.createKey();
        innerValue = lineRecordReader.createValue();
        String sepStr = job.get("key.value.separator.in.input.line", ",");
        this.separator = (byte) sepStr.charAt(0);
    }

    public static int findSeparator(byte[] utf, int start, int length, byte sep) {
        for (int i = start; i < (start + length); i++) {
            if (utf[i] == sep) {
                return i;
            }
        }
        return -1;
    }

    /** Read key/value pair in a line. */
    public synchronized boolean next(LongWritable key, Text value) throws IOException {
        LongWritable tKey = key;
        Text tValue = value;
        byte[] line = null;
        int lineLen = -1;
        //next调用会填充dummyKey和innerValue的值
        if (lineRecordReader.next(dummyKey, innerValue)) {
            //获取value和value的长度
            line = innerValue.getBytes();
            lineLen = innerValue.getLength();
        } else {
            //没有下一个
            return false;
        }
        //获取读取到的为空, 不需要再调用next了. 实际上next是由应用程序进行循环控制的
        if (line == null)
            return false;

        //根据配置的分隔符, 将一行文本分成key和value两部分
        //pos为key和value的分割位置
        int pos = findSeparator(line, 0, lineLen, this.separator);
        if (pos == -1) {
            tKey.set(Long.valueOf(new String(line, 0, lineLen)));
            tValue.set("");
        } else {
            //key的长度就是pos, 因为找到的分隔符就是key和value进行分割的地方.所以分隔符之前的是key,分隔符之后的是value
            int keyLen = pos;
            //构造一个字节数组用来填充key
            byte[] keyBytes = new byte[keyLen];
            //从文本行line中复制指定数量keyLen的字节到keyBytes字节数组中
            System.arraycopy(line, 0, keyBytes, 0, keyLen);
            //文本行的长度=key+seperator[1]+value. 所以可以计算出value的长度
            int valLen = lineLen - keyLen - 1;
            byte[] valBytes = new byte[valLen];
            //value从pos+1处开始读取
            System.arraycopy(line, pos + 1, valBytes, 0, valLen);
            //设置key,value
            tKey.set(Long.valueOf(new String(keyBytes)));
            tValue.set(valBytes);
        }
        return true;
    }

    public float getProgress() throws IOException {
        return lineRecordReader.getProgress();
    }

    public synchronized long getPos() throws IOException {
        return lineRecordReader.getPos();
    }

    public synchronized void close() throws IOException {
        lineRecordReader.close();
    }
}