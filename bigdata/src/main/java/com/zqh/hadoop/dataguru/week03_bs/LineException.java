package com.zqh.hadoop.dataguru.week03_bs;

/**
 * 定义异常类
 */
public class LineException extends Exception {
    private static final long serialVersionUID = 8245008693589452584L;
    int flag;
    public LineException(String msg, int flag)
    {
        super(msg);
        this.flag = flag;
    }
    public int getFlag()
    {
        return flag;
    }
}