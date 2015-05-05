/**
 * 
 */
package com.baidu.unbiz.common.ip;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 下午4:17:21
 */
public interface IpManagerFactory {

    IpManager getDefaultIpManager();

    IpManager getDefaultIpManager(boolean asyn);

    IpManager getIpManager(IpData ipdata);

    IpManager getIpManager(IpData ipdata, boolean asyn);

}
