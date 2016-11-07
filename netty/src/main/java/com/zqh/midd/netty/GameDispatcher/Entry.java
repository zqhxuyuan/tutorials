package com.zqh.midd.netty.GameDispatcher;

import com.zqh.midd.netty.GameDispatcher.client.ClientBootstrapStarter;
import com.zqh.midd.netty.GameDispatcher.server.ServerBootstrapStarter;
import com.zqh.midd.netty.GameDispatcher.util.ClassUtil;
import org.apache.commons.cli.*;

/**
 * 程序入口
 * 客户端与服务端均在此启动
 * 使用使用-h选项查看
 * java -jar xxx.jar -h
 * @author xingchencheng: http://damacheng009.iteye.com/blog/2017756
 *
 * Server Program Parameter: -m s -p 9999
 * Client Program Parameter: -m c -r localhost -p 9999
 */
public class Entry {
    public static void main(String[] args) {
        Options options = new Options(); 
        
        options.addOption("m", true, "c代表启动客户端，s表示启动服务端");    
        options.addOption("r", true, "服务端ip，客户端模式必填");
        options.addOption("p", true, "服务端端口");
        options.addOption("h", false, "帮助");
  
        CommandLineParser parser = new PosixParser();  
        CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			String mode = null;
			String remote = null;
			int port = 9999;
			
			if (cmd.hasOption("h")) {
				HelpFormatter formatter = new HelpFormatter();  
				formatter.printHelp("消息分发器 client & server", options);
				return;
			}
			
			if (cmd.hasOption("m")) {  
	            mode = cmd.getOptionValue("m");  
	        }  
	  
	        if (cmd.hasOption("r")) {  
	            remote = cmd.getOptionValue("r");   
	        } 
	        
	        if (cmd.hasOption("p")) {  
	            port = Integer.parseInt(cmd.getOptionValue("p"));   
	        }
	        
	        ClassUtil.initTypeToMsgClassMap();
	        if (mode.equals("c")) {
	        	ClientBootstrapStarter.start(remote, port);
	        } else if (mode.equals("s")) {
	        	ClassUtil.initTypeToExecutorClassMap();
	            ServerBootstrapStarter.start(port);
	        }
		} catch (Exception e) {
			System.err.println("解析参数异常");
		}  
    }
}