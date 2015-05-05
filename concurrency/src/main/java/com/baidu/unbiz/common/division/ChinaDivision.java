package com.baidu.unbiz.common.division;

/**
 * 中国省份城市
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 上午3:59:00
 */
public class ChinaDivision extends Division {
    /**
	 * 
	 */
    private static final long serialVersionUID = -7459417562260342642L;
    public static final int DEFAULT_ALL_DIVISION = 1;

    /**
     * 繁体名字
     */
    private String divisionTname;

    public String getDivisionTname() {
        return divisionTname;
    }

    public void setDivisionTname(String divisionTname) {
        this.divisionTname = divisionTname;
    }

}
