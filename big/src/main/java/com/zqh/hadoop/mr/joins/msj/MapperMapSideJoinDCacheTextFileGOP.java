package com.zqh.hadoop.mr.joins.msj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperMapSideJoinDCacheTextFileGOP extends Mapper<LongWritable, Text, Text, Text> {

    private static HashMap<String, String> DepartmentMap = new HashMap<String, String>();
    private BufferedReader brReader;
    private String strDeptName = "";
    private Text txtMapOutputKey = new Text("");
    private Text txtMapOutputValue = new Text("");

    enum MYCOUNTER {
        RECORD_COUNT, FILE_EXISTS, FILE_NOT_FOUND, SOME_OTHER_ERROR
    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        File lookupFile = new File("departments_txt");
        String strLineRead = "";
        try {
            brReader = new BufferedReader(new FileReader(lookupFile));

            // Read each line, split and load to HashMap
            while ((strLineRead = brReader.readLine()) != null) {
                String deptFieldArray[] = strLineRead.split("\\t");
                DepartmentMap.put(deptFieldArray[0].trim(), deptFieldArray[1].trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            context.getCounter(MYCOUNTER.FILE_NOT_FOUND).increment(1);
        } catch (IOException e) {
            context.getCounter(MYCOUNTER.SOME_OTHER_ERROR).increment(1);
            e.printStackTrace();
        }
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.getCounter(MYCOUNTER.RECORD_COUNT).increment(1);

        if (value.toString().length() > 0) {
            String arrEmpAttributes[] = value.toString().split("\\t");
            try {
                strDeptName = DepartmentMap.get(arrEmpAttributes[6].toString());
            } finally {
                strDeptName = ((strDeptName.equals(null) || strDeptName.equals("")) ? "NOT-FOUND" : strDeptName);
            }

            txtMapOutputKey.set(arrEmpAttributes[0].toString());
            txtMapOutputValue.set(arrEmpAttributes[1].toString() + "\t"
                    + arrEmpAttributes[1].toString() + "\t"
                    + arrEmpAttributes[2].toString() + "\t"
                    + arrEmpAttributes[3].toString() + "\t"
                    + arrEmpAttributes[4].toString() + "\t"
                    + arrEmpAttributes[5].toString() + "\t"
                    + arrEmpAttributes[6].toString() + "\t" + strDeptName);
        }
        context.write(txtMapOutputKey, txtMapOutputValue);
        strDeptName = "";
    }
}