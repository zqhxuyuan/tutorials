package com.baidu.unbiz.common.bean.sample;

import com.baidu.unbiz.common.lang.ToString;

public class Cbean extends ToString {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4004198374926124548L;
    private String s1;
    private String s2;
    private String s3;

    public String getS1() {
        return s1;
    }

    protected void setS1(String s1) {
        this.s1 = s1;
    }

    protected String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }
}