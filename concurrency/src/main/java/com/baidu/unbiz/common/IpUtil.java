/**
 * 
 */
package com.baidu.unbiz.common;

import static com.baidu.unbiz.common.StringPool.Symbol.DOT;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午7:10:13
 */
public abstract class IpUtil {

    /**
     * string类型的ip转换为number类型
     * 
     * @param ipString xxx.xxx.xxx.xxx
     * @return long 3663452325
     */
    public static long encodeIp(String ip) {
        long ret = 0;
        if (ip == null) {
            return ret;
        }
        String[] segs = ip.split("\\.");

        for (int i = 0; i < segs.length; i++) {
            long seg = Long.parseLong(segs[i]);
            ret += (seg << ((3 - i) * 8));
        }

        return ret;
    }

    /**
     * number类型的ip转换为string类型
     * 
     * @param ip 3663452325
     * @return String xxx.xxx.xxx.xxx
     */
    public static String decodeIp(long ipLong) {
        StringBuilder ip = new StringBuilder(String.valueOf(ipLong >> 24) + DOT);

        ip.append(String.valueOf((ipLong & 16711680) >> 16) + DOT);
        ip.append(String.valueOf((ipLong & 65280) >> 8) + DOT);
        ip.append(String.valueOf(ipLong & 255));

        return ip.toString();
    }

}
