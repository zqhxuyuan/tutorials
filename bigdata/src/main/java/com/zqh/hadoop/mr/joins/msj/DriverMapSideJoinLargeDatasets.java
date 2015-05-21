package com.zqh.hadoop.mr.joins.msj;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.join.CompositeInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class DriverMapSideJoinLargeDatasets {

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf("DriverMapSideJoinLargeDatasets");
        conf.setJarByClass(DriverMapSideJoinLargeDatasets.class);
        String[] jobArgs = new GenericOptionsParser(conf, args)
                .getRemainingArgs();

        Path dirEmployeesData = new Path(jobArgs[0]);
        Path dirSalaryData = new Path(jobArgs[1]);
        Path dirOutput = new Path(jobArgs[2]);

        conf.setMapperClass(MapperMapSideJoinLargeDatasets.class);

        conf.setInputFormat(CompositeInputFormat.class);
        String strJoinStmt = CompositeInputFormat.compose("inner",
                KeyValueLongInputFormat.class, dirEmployeesData, dirSalaryData);

        conf.set("mapred.join.expr", strJoinStmt);

        conf.setNumReduceTasks(0);

        conf.setOutputFormat(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(conf, dirOutput);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            Thread.sleep(1000);
        }

        System.exit(job.isSuccessful() ? 0 : 2);
    }
}