/**
 * 
 */
package com.baidu.unbiz.common.ip;

/**
 * 提供IP所在地查询的工具
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午7:52:19
 */
public interface IpManager {

    /**
     * 获取IP所在地信息,如果IP地址不存在，则返回<code>null</code>
     * 
     * @param ip IP地址，如:220.189.213.3
     * @return IpEntry
     */
    IpEntry getIpInfo(String ip);

    /**
     * 获取IP对应所在地
     * 
     * @param ip IP地址，如:220.189.213.3
     * @return IP对应所在地
     */
    String getIpLocation(String ip);

}
