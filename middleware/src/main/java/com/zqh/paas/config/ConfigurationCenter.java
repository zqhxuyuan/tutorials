package com.zqh.paas.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.zqh.paas.util.CiperTools;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zqh.paas.PaasException;
import com.zqh.paas.util.StringUtil;

/**
 * 统一配置ZK管理类，实现初始化自动创建
 *
 */
public class ConfigurationCenter {
	private static final Logger log = Logger.getLogger(ConfigurationCenter.class);
	private final String UNIX_FILE_SEPARATOR = "/";

	private ZooKeeper zk = null;

	private String centerAddr = null;
	private boolean createZKNode = false;
	private int timeOut = 2000;

	private String runMode = PROD_MODE;// P:product mode; D:dev mode
	public static final String DEV_MODE = "D";
	public static final String PROD_MODE = "P";
	private List<String> configurationFiles = new ArrayList<String>();
	private Properties props = new Properties();

    private String auth = null;

    // 订阅者. key:path, value:AppImpl
	private HashMap<String, ArrayList<ConfigurationWatcher>> subsMap = null;

	public ConfigurationCenter(String centerAddr, int timeOut, String runMode, List<String> configurationFiles) {
		this.centerAddr = centerAddr;
		this.timeOut = timeOut;
		this.runMode = runMode;
		if (configurationFiles != null) {
			this.configurationFiles.addAll(configurationFiles);
		}
	}

	public ConfigurationCenter(String centerAddr, int timeOut, String runMode) {
		this.centerAddr = centerAddr;
		this.timeOut = timeOut;
		this.runMode = runMode;
	}

    /**
     * 初始化连接ZooKeeper
     */
    public void init() {
        try {
            for (String configurationFile : configurationFiles) {
                props.load(this.getClass().getResourceAsStream(configurationFile));
            }
        } catch (IOException e) {
            log.error("Error load proerpties file," + configurationFiles, e);
        }
        // 增加watch,开发模式没有，生产有
        try {
            zk = connectZookeeper(centerAddr, timeOut, runMode);
        } catch (Exception e) {
            log.error("Error connect to Zookeeper," + centerAddr, e);
        }
        subsMap = new HashMap<String, ArrayList<ConfigurationWatcher>>();
        if (isCreateZKNode()) {
            writeData();
        }
    }

	private ZooKeeper connectZookeeper(String address, int timeout, String runMode) throws Exception {
		if (DEV_MODE.equals(runMode)) {
			ZooKeeper zk = new ZooKeeper(centerAddr, timeout, new Watcher() {
				public void process(WatchedEvent event) {
					// 不做处理
				}
			});
			return zk;
		} else {
			ZooKeeper zk = new ZooKeeper(centerAddr, timeout, new Watcher() {
				public void process(WatchedEvent event) {
					if (log.isInfoEnabled()) {
						log.info(event.toString());
					}
					if (Event.EventType.NodeDataChanged.equals(event.getType()) && subsMap.size() > 0) {
						String path = event.getPath();
						ArrayList<ConfigurationWatcher> watcherList = subsMap.get(path);
						if (watcherList != null && watcherList.size() > 0) {
							for (ConfigurationWatcher watcher : watcherList) {
								try {
									watcher.process(getConf(path));
								} catch (PaasException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			});
            if ((this.auth != null) && (this.auth.length() > 0))
                this.zk.addAuthInfo("digest", CiperTools.decrypt(this.auth).getBytes());
			return zk;
		}
	}

	@SuppressWarnings("rawtypes")
	private void writeData() {
		// 开始创建节点
		Set keyValue = props.keySet();
		for (Iterator it = keyValue.iterator(); it.hasNext();) {
			String path = (String) it.next();
			String pathValue = (String) props.getProperty(path);
			// 开始创建
			try {
				setZKPathNode(zk, path, pathValue);
			} catch (Exception e) {
				log.error("Error create to set node data,key=" + path + ",value=" + pathValue, e);
			}
		}
	}

	private void setZKPathNode(ZooKeeper zk, String path, String pathValue) throws Exception {
		if (zk.exists(path, false) == null) {
			createPathNode(zk, path.split(UNIX_FILE_SEPARATOR));
		}
		// 设置值,匹配所有版本
		zk.setData(path, pathValue.getBytes("UTF-8"), -1);
		log.info("Set zk node data: node=" + path + ",value=" + pathValue);
	}

	private void createPathNode(ZooKeeper zk, String[] pathParts) throws Exception {
		StringBuilder path = new StringBuilder();
		for (int i = 0; i < pathParts.length; i++) {
			if (!StringUtil.isBlank(pathParts[i])) {
				path.append(UNIX_FILE_SEPARATOR).append(pathParts[i]);
				String pathString = path.toString();
				try {
					if (zk.exists(pathString, false) == null) {
						// 前面都是空
						zk.create(pathString, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					}
				} catch (KeeperException e) {
					if (e.code() != KeeperException.Code.NODEEXISTS)
						throw e;
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void removeWatcher(String confPath, Class warcherClazz) throws PaasException {
		ArrayList<ConfigurationWatcher> watcherList = subsMap.get(confPath);
		try {
			if (watcherList == null) {
				zk.getData(confPath, false, null);
			} else {
				int size = watcherList.size();
				// ConfigurationWatcher watcher = null;
				for (int i = size - 1; i >= 0; i--) {
					if (watcherList.get(i).getClass().equals(warcherClazz)) {
						watcherList.remove(i);
					}
				}
				if (watcherList.size() == 0) {
					zk.getData(confPath, false, null);
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new PaasException("9999", "failed to get configuration from configuration center", e);
		}

	}

	public String getRunMode() {
		return runMode;
	}

	public List<String> getConfigurationFiles() {
		return configurationFiles;
	}

	public void setConfigurationFile(List<String> configurationFile) {
		this.configurationFiles.addAll(configurationFiles);
	}

    /**
     * 订阅者的实现类在初始化后应该注册到ConfCenter
     * @param confPath
     * @param warcher
     */
    public String getConfAndWatch(String confPath, ConfigurationWatcher warcher) throws PaasException {
        ArrayList<ConfigurationWatcher> watcherList = subsMap.get(confPath);
        if (watcherList == null) {
            watcherList = new ArrayList<ConfigurationWatcher>();
            subsMap.put(confPath, watcherList);
        }
        watcherList.add(warcher);
        return this.getConf(confPath);
    }

    public String getConf(String confPath) throws PaasException {
        String conf = null;
        try {
            if (DEV_MODE.equals(this.getRunMode())) {
                return props.getProperty(confPath);
            } else {
                conf = new String(zk.getData(confPath, true, null),"UTF-8");
            }

        } catch (Exception e) {
            log.error("", e);
            throw new PaasException("9999", "failed to get configuration from configuration center", e);
        }
        return conf;
    }

	public void destory() {
		if (null != zk) {
			try {
				log.info("Start to closing zk client," + zk);
				zk.close();
				log.info("ZK client closed," + zk);
			} catch (InterruptedException e) {
				log.error("Can not close zk client", e);
			}
		}
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public boolean isCreateZKNode() {
		return createZKNode;
	}

	public void setCreateZKNode(boolean createZKNode) {
		this.createZKNode = createZKNode;
	}

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
