package com.zqh.hadoop.mr.joins.rsj;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

//********************************************************************************
//Class:    ReducerRSJ
//Purpose:  Reducer
//Author:   Anagha Khanolkar
//*********************************************************************************

public class ReducerRSJ extends
        Reducer<CompositeKeyWritableRSJ, Text, NullWritable, Text> {

    StringBuilder reduceValueBuilder = new StringBuilder("");
    NullWritable nullWritableKey = NullWritable.get();
    Text reduceOutputValue = new Text("");
    String strSeparator = ",";

    //MapFile是排序后的SequenceFile. 由两部分组成，分别是data和index
    //index作为文件的数据索引，主要记录了每个Record的key值，以及该Record在文件中的偏移位置。
    //在MapFile被访问的时候,索引文件会被加载到内存，通过索引映射关系可迅速定位到指定Record所在文件位置，
    //因此，相对SequenceFile而言，MapFile的检索效率是高效的，缺点是会消耗一部分内存来存储index数据
    private MapFile.Reader deptMapReader = null;
    Text txtMapFileLookupKey = new Text("");
    Text txtMapFileLookupValue = new Text("");

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Get side data from the distributed cache
        Path[] cacheFilesLocal = DistributedCache.getLocalCacheArchives(context.getConfiguration());

        for (Path eachPath : cacheFilesLocal) {
            if (eachPath.getName().toString().trim().equals("departments_map.tar.gz")) {
                URI uriUncompressedFile = new File(eachPath.toString() + "/departments_map").toURI();
                initializeDepartmentsMap(uriUncompressedFile, context);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void initializeDepartmentsMap(URI uriUncompressedFile, Context context) throws IOException {
        // Initialize the reader of the map file (side data)
        FileSystem dfs = FileSystem.get(context.getConfiguration());
        try {
            deptMapReader = new MapFile.Reader(dfs, uriUncompressedFile.toString(), context.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StringBuilder buildOutputValue(CompositeKeyWritableRSJ key, StringBuilder reduceValueBuilder, Text value) {

        if (key.getsourceIndex() == 1) {
            // Employee data
            // Get the department name from the MapFile in distributedCache

            // Insert the joinKey (empNo) to beginning of the stringBuilder
            reduceValueBuilder.append(key.getjoinKey()).append(strSeparator);

            String arrEmpAttributes[] = value.toString().split(",");
            txtMapFileLookupKey.set(arrEmpAttributes[3].toString());
            try {
                deptMapReader.get(txtMapFileLookupKey, txtMapFileLookupValue);
            } catch (Exception e) {
                txtMapFileLookupValue.set("");
            } finally {
                txtMapFileLookupValue.set((txtMapFileLookupValue.equals(null) || txtMapFileLookupValue
                        .equals("")) ? "NOT-FOUND"
                        : txtMapFileLookupValue.toString());
            }

            // Append the department name to the map values to form a complete CSV of employee attributes
            reduceValueBuilder.append(value.toString()).append(strSeparator)
                    .append(txtMapFileLookupValue.toString())
                    .append(strSeparator);
        } else if (key.getsourceIndex() == 2) {
            // Current recent salary data (1..1 on join key)
            // Salary data; Just append the salary, drop the effective-to-date
            String arrSalAttributes[] = value.toString().split(",");
            reduceValueBuilder.append(arrSalAttributes[0].toString()).append(strSeparator);
        } else {
            // key.getsourceIndex() == 3; Historical salary data
            // Get the salary data but extract only current salary (to_date='9999-01-01')
            String arrSalAttributes[] = value.toString().split(",");
            if (arrSalAttributes[1].toString().equals("9999-01-01")) {
                // Salary data; Just append
                reduceValueBuilder.append(arrSalAttributes[0].toString()).append(strSeparator);
            }
        }

        // Reset
        txtMapFileLookupKey.set("");
        txtMapFileLookupValue.set("");

        return reduceValueBuilder;
    }

    @Override
    public void reduce(CompositeKeyWritableRSJ key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Iterate through values; First set is csv of employee data
        // second set is salary data; The data is already ordered
        // by virtue of secondary sort; Append each value;
        for (Text value : values) {
            buildOutputValue(key, reduceValueBuilder, value);
        }

        // Drop last comma, set value, and emit output
        if (reduceValueBuilder.length() > 1) {
            reduceValueBuilder.setLength(reduceValueBuilder.length() - 1);
            // Emit output
            reduceOutputValue.set(reduceValueBuilder.toString());
            context.write(nullWritableKey, reduceOutputValue);
        } else {
            System.out.println("Key=" + key.getjoinKey() + "src=" + key.getsourceIndex());
        }

        // Reset variables
        reduceValueBuilder.setLength(0);
        reduceOutputValue.set("");
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        deptMapReader.close();
    }
}