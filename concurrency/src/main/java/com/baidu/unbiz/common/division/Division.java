package com.baidu.unbiz.common.division;

import java.io.Serializable;
import java.util.List;

import com.baidu.unbiz.common.lang.ToString;

/**
 * 省份城市
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 上午3:54:38
 */
public class Division extends ToString implements Serializable, Comparable<Division> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -7459417562260342641L;

    /**
     * 指定地区id,内部定义id
     */
    private int divisionId;

    /**
     * 地区名字,国标定义
     */
    private String divisionName;
    /**
     * 地区名字缩写,国标定义
     */
    private String divisionAbbName;
    /**
     * 地区邮编,国标定义
     */
    private String divisionZip;

    /**
     * 下级地区DivisionList
     */
    private List<Division> childDivision;

    /**
     * 上级地区Divison,如果是根节点,则为空
     */
    private Division parentDivision;

    public int compareTo(Division other) {
        if (other == null) {
            return -1;
        }

        return (divisionId < other.divisionId ? -1 : (divisionId == other.divisionId ? 0 : 1));

    }

    /**
     * 下级地区集合
     */
    public List<Division> getChildDivision() {
        return childDivision;
    }

    public void setChildDivision(List<Division> childDivision) {
        this.childDivision = childDivision;
    }

    /**
     * 指定地区id,内部定义id
     */
    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * 地区名字,国标定义
     */
    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * 地区名字缩写,国标定义
     */
    public String getDivisionAbbName() {
        return divisionAbbName;
    }

    public void setDivisionAbbName(String divisionAbbName) {
        this.divisionAbbName = divisionAbbName;
    }

    /**
     * 地区邮编,国标定义
     */
    public String getDivisionZip() {
        return divisionZip;
    }

    public void setDivisionZip(String divisionZip) {
        this.divisionZip = divisionZip;
    }

    /**
     * 上级地区Division,如果是根节点,则为空
     */
    public Division getParentDivision() {
        return parentDivision;
    }

    public void setParentDivision(Division parentDivision) {
        this.parentDivision = parentDivision;
    }

}
