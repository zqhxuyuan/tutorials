package com.zqh.hadoop.mr.joins.rsj;

//********************************************************************************
//Class:    MapperRSJ
//Purpose:  Mapper
//Author:   Anagha Khanolkar
//*********************************************************************************

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class MapperRSJ extends Mapper<LongWritable, Text, CompositeKeyWritableRSJ, Text> {

    CompositeKeyWritableRSJ ckwKey = new CompositeKeyWritableRSJ();
    Text txtValue = new Text("");
    int intSrcIndex = 0;
    StringBuilder strMapValueBuilder = new StringBuilder("");
    List<Integer> lstRequiredAttribList = new ArrayList<Integer>();

    @Override
    protected void setup(Context context) throws IOException,
            InterruptedException {
        // Get the source index; (employee = 1, salary = 2)
        // Added as configuration in driver
        FileSplit fsFileSplit = (FileSplit) context.getInputSplit();
        intSrcIndex = Integer.parseInt(context.getConfiguration().get(fsFileSplit.getPath().getName()));

        // Initialize the list of fields to emit as output based on
        // intSrcIndex (1=employee, 2=current salary, 3=historical salary)
        if (intSrcIndex == 1) // employee
        {
            lstRequiredAttribList.add(2); // FName
            lstRequiredAttribList.add(3); // LName
            lstRequiredAttribList.add(4); // Gender
            lstRequiredAttribList.add(6); // DeptNo
        } else // salary
        {
            lstRequiredAttribList.add(1); // Salary
            lstRequiredAttribList.add(3); // Effective-to-date (Value of 9999-01-01 indicates current salary)

        }
    }

    private String buildMapValue(String arrEntityAttributesList[]) {
        // This method returns csv list of values to emit based on data entity

        strMapValueBuilder.setLength(0);// Initialize

        // Build list of attributes to output based on source - employee/salary
        for (int i = 1; i < arrEntityAttributesList.length; i++) {
            // If the field is in the list of required output
            // append to stringbuilder
            if (lstRequiredAttribList.contains(i)) {
                strMapValueBuilder.append(arrEntityAttributesList[i]).append(",");
            }
        }
        if (strMapValueBuilder.length() > 0) {
            // Drop last comma
            strMapValueBuilder.setLength(strMapValueBuilder.length() - 1);
        }

        return strMapValueBuilder.toString();
    }

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        if (value.toString().length() > 0) {
            String arrEntityAttributes[] = value.toString().split(",");

            ckwKey.setjoinKey(arrEntityAttributes[0].toString());
            ckwKey.setsourceIndex(intSrcIndex);
            txtValue.set(buildMapValue(arrEntityAttributes));

            context.write(ckwKey, txtValue);
        }

    }
}