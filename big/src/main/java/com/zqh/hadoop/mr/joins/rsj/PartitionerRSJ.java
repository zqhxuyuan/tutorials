package com.zqh.hadoop.mr.joins.rsj;

//********************************************************************************
//Class:    PartitionerRSJ
//Purpose:  Custom partitioner
//Author:   Anagha Khanolkar
//*********************************************************************************

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class PartitionerRSJ extends Partitioner<CompositeKeyWritableRSJ, Text> {

    @Override
    public int getPartition(CompositeKeyWritableRSJ key, Text value, int numReduceTasks) {
        // Partitions on joinKey (EmployeeID)
        return (key.getjoinKey().hashCode() % numReduceTasks);
    }
}