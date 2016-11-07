package com.zqh.hadoop.mr.joins.msj;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.join.TupleWritable;

public class MapperMapSideJoinLargeDatasets extends MapReduceBase implements
        Mapper<LongWritable, TupleWritable, Text, Text> {

    Text txtKey = new Text("");
    Text txtValue = new Text("");

    @Override
    public void map(LongWritable key, TupleWritable value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        if (value.toString().length() > 0) {
            //key是joinKey: EmpNo
            txtKey.set(key.toString());

            //value是两个数据集join之后的结果
            //所以value第一个数据来自第一个文件,第二个数据来自第二个文件,它们之间以逗号分割
            String arrEmpAttributes[] = value.get(0).toString().split(",");
            String arrDeptAttributes[] = value.get(1).toString().split(",");

            //取第一个文件的value的第2,3列
            //取第二个文件的value的第1列
            txtValue.set(arrEmpAttributes[1].toString() + "\t"
                    + arrEmpAttributes[2].toString() + "\t"
                    + arrDeptAttributes[0].toString());

            output.collect(txtKey, txtValue);

        }

    }
}