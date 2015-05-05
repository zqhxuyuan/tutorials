package com.baidu.unbiz.common.division;

import java.util.List;

/**
 * 中国省份城市
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 上午3:59:34
 */
public interface DivisionManager {
    // /**
    // * 初始化,加载地区对象
    // */
    // public void init() throws DivisionException;

    /**
     * 重新加载地区
     */
    public void reloadDivision() throws DivisionException;

    /**
     * 根据内部定义id取地区对象,没有找到返回null
     */
    public Division getDivisionById(int id);

    /**
     * 根据国标定义邮编取地区对象,没有找到返回null
     */
    public List<Division> getDivisionByZip(String zip);

    /**
     * 根据国标定义名称取地区对象,没有找到返回null
     */
    public List<Division> getDivisionByName(String name);

    /**
     * 根据国标定义缩写名称取地区对象,没有找到返回null
     */
    public List<Division> getDivisionByAbbName(String abbName);

    /**
     * 根据国标定义名称模糊取地区对象,没有找到返回null
     */
    public List<Division> getObscureDivisionByName(String name);

    /**
     * 根据繁体名称模糊取地区对象,没有找到返回null
     */
    public List<Division> getObscureDivisionByTName(String name);

    /**
     * 根据国标定义繁体名称取地区对象,没有找到返回null
     */
    public List<Division> getDivisionByTName(String name);

    /**
     * 得到所有省份级别的List<Division>
     * 
     * @return List<Division>
     */
    public List<Division> getProvinceDisivion();

    /**
     * 检查id是否省级
     */
    public boolean isProvinceDivision(int id);

    /**
     * 检查名称是否省级
     */
    public boolean isProvinceNameDivision(String provName);

    /**
     * 检查id是否市级
     */
    public boolean isCityDivision(int id);

    /**
     * 检查名称是否市级
     */
    public boolean isCityNameDivision(String cityName);

    /**
     * 检查id是否地区级
     */
    public boolean isRegionDivision(int id);

    /**
     * 检查名称是否地区级
     */
    public boolean isRegionNameDivision(String regionName);

}
