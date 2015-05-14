package com.baidu.unbiz.common.bean.sample;

import com.baidu.unbiz.common.bean.CopyField;
import com.baidu.unbiz.common.lang.ToString;

public class Overload extends ToString {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8428973675621183122L;
    @CopyField
    String company;

    // not a property setter
    public void setCompany(StringBuilder sb) {
        this.company = sb.toString();
    }

    public String getCompany() {
        return company;
    }
}