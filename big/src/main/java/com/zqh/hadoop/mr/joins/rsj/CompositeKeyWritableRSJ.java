package com.zqh.hadoop.mr.joins.rsj;

//********************************************************************************
//Class:    CompositeKeyWritableRSJ
//Purpose:  Custom Writable that serves as composite key
//          with attributes joinKey and sourceIndex
//Author:   Anagha Khanolkar
//*********************************************************************************

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

public class CompositeKeyWritableRSJ implements Writable,
        WritableComparable<CompositeKeyWritableRSJ> {

    // Data members
    private String joinKey;// EmployeeID
    private int sourceIndex;// 1=Employee data; 2=Salary (current) data; 3=Salary historical data

    public CompositeKeyWritableRSJ() {
    }

    public CompositeKeyWritableRSJ(String joinKey, int sourceIndex) {
        this.joinKey = joinKey;
        this.sourceIndex = sourceIndex;
    }

    @Override
    public String toString() {
        return (new StringBuilder().append(joinKey).append("\t")
                .append(sourceIndex)).toString();
    }

    public void readFields(DataInput dataInput) throws IOException {
        joinKey = WritableUtils.readString(dataInput);
        sourceIndex = WritableUtils.readVInt(dataInput);
    }

    public void write(DataOutput dataOutput) throws IOException {
        WritableUtils.writeString(dataOutput, joinKey);
        WritableUtils.writeVInt(dataOutput, sourceIndex);
    }

    public int compareTo(CompositeKeyWritableRSJ objKeyPair) {
        int result = joinKey.compareTo(objKeyPair.joinKey);
        if (0 == result) {
            result = Double.compare(sourceIndex, objKeyPair.sourceIndex);
        }
        return result;
    }

    public String getjoinKey() {
        return joinKey;
    }

    public void setjoinKey(String joinKey) {
        this.joinKey = joinKey;
    }

    public int getsourceIndex() {
        return sourceIndex;
    }

    public void setsourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }
}