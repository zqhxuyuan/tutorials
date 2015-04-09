package com.zqh.midd.netty.gameserver;

import com.zqh.midd.netty.gameserver.common.Config;
import com.zqh.midd.netty.gameserver.manager.SocketServer;

/**
 * https://github.com/iwinstar/gameserver
 *
 * 服务器端
 *
 * User: mengmeng.cheng
 * Date: 4/4/14
 * Time: 2:10 PM
 * Email: chengmengmeng@gmail.com
 */
public class GameServer {
	public static void main(String[] args) throws Exception {

        // 从配置文件config.properties获取端口配置
		int port = Config.SERVER_PORT;

        // 支持 Cloud Foundry 平台
		String envPort = System.getenv("VCAP_APP_PORT");

		if (envPort != null && envPort.trim().length() > 0) {
			port = Integer.parseInt(envPort.trim());
		}

		SocketServer server = new SocketServer(port);
        server.run();
	}
}