package com.baidu.unbiz.common.bean.sample;

import com.baidu.unbiz.common.lang.ToString;

public class Abean extends ToString {

    /**
	 * 
	 */
    private static final long serialVersionUID = -219989959508824582L;

    protected Integer shared;

    private String fooProp = "abean_value";

    public void setFooProp(String v) {
        fooProp = v;
    }

    public String getFooProp() {
        return fooProp;
    }

    public boolean isSomething() {
        return true;
    }
}
