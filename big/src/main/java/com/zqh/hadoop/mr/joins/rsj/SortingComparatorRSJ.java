package com.zqh.hadoop.mr.joins.rsj;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

//********************************************************************************
//Class:    SortingComparatorRSJ
//Purpose:  Sorting comparator
//Author:   Anagha Khanolkar
//*********************************************************************************

public class SortingComparatorRSJ extends WritableComparator {

    protected SortingComparatorRSJ() {
        super(CompositeKeyWritableRSJ.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
        // Sort on all attributes of composite key
        CompositeKeyWritableRSJ key1 = (CompositeKeyWritableRSJ) w1;
        CompositeKeyWritableRSJ key2 = (CompositeKeyWritableRSJ) w2;

        int cmpResult = key1.getjoinKey().compareTo(key2.getjoinKey());
        if (cmpResult == 0)// same joinKey
        {
            return Double.compare(key1.getsourceIndex(), key2.getsourceIndex());
        }
        return cmpResult;
    }
}