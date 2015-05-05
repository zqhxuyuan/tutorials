/**
 * 
 */
package com.baidu.unbiz.common.ip;

import java.io.Serializable;

/**
 * 一个IP项
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午7:47:34
 */
public class IpEntry implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -5501876369273207808L;
    /** IP所在的国家 */
    private String country;
    /** IP所在的省 */
    private String province;
    /** IP所在的城市 */
    private String city;
    /** IP所在的具体街道 */
    private String address;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString() {
        return address;
    }

}
