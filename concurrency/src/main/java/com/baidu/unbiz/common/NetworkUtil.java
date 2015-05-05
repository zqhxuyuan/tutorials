/**
 * 
 */
package com.baidu.unbiz.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月18日 下午3:57:56
 */
public abstract class NetworkUtil {

    public static final String LOCALHOST = "127.0.0.1";

    /**
     * loopback address in IPV6
     */
    public static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    public static String getLocalHostname() {
        InetAddress address;
        String hostname;
        try {
            address = InetAddress.getLocalHost();
            // force a best effort reverse DNS lookup
            hostname = address.getHostName();
            if (StringUtil.isEmpty(hostname)) {
                hostname = address.toString();
            }
        } catch (UnknownHostException noIpAddrException) {
            hostname = LOCALHOST;
        }
        return hostname;
    }

    public static String getLocalHostIp() {
        InetAddress address;
        String hostAddress;
        try {
            address = InetAddress.getLocalHost();
            // force a best effort reverse DNS lookup
            hostAddress = address.getHostAddress();
            if (StringUtil.isEmpty(hostAddress)) {
                hostAddress = address.toString();
            }
        } catch (UnknownHostException noIpAddrException) {
            hostAddress = LOCALHOST;
        }
        return hostAddress;
    }

}
