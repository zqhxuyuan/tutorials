/**
 * 
 */
package com.baidu.unbiz.common.ip;

import java.util.concurrent.Callable;

import com.baidu.unbiz.common.ConcurrentUtil;
import com.baidu.unbiz.common.able.Computable;
import com.baidu.unbiz.common.cache.ConcurrentCache;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 下午8:22:27
 */
public class LazyLoadIpFactory implements IpManagerFactory {

    private static final Computable<IpData, DefaultIpManager> cache = new ConcurrentCache<IpData, DefaultIpManager>();

    @Override
    public IpManager getDefaultIpManager() {
        IpManager result = cache.get(IpData.YAHOO, new Callable<DefaultIpManager>() {
            @Override
            public DefaultIpManager call() throws Exception {
                DefaultIpManager instance = new DefaultIpManager(IpData.YAHOO);
                instance.init();
                return instance;
            }
        });

        return result;
    }

    @Override
    public IpManager getIpManager(final IpData ipdata) {
        IpManager result = cache.get(ipdata, new Callable<DefaultIpManager>() {
            @Override
            public DefaultIpManager call() throws Exception {
                DefaultIpManager instance = new DefaultIpManager(ipdata);
                instance.init();
                return instance;
            }
        });

        return result;
    }

    @Override
    public IpManager getDefaultIpManager(final boolean asyn) {
        IpManager result = cache.get(IpData.YAHOO, new Callable<DefaultIpManager>() {
            @Override
            public DefaultIpManager call() throws Exception {
                DefaultIpManager instance = new DefaultIpManager(IpData.YAHOO);
                ConcurrentUtil.execute(asyn, instance, "init");
                return instance;
            }
        });

        return result;
    }

    @Override
    public IpManager getIpManager(final IpData ipdata, final boolean asyn) {
        IpManager result = cache.get(ipdata, new Callable<DefaultIpManager>() {
            @Override
            public DefaultIpManager call() throws Exception {
                DefaultIpManager instance = new DefaultIpManager(ipdata);
                ConcurrentUtil.execute(asyn, instance, "init");
                return instance;
            }
        });

        return result;
    }

}
