package com.wyd.channel;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.BoundedThreadPool;

import com.wyd.channel.service.factory.ServiceManager;
import com.wyd.channel.servlet.ChannelLoginServlet;
import com.wyd.channel.utils.TaskThread;



public class Server {
	 public static Server   instance       = null;
	    private static final Logger log            = Logger.getLogger(Server.class);
	    /**
	     * @param args
	     */
	    public static void main(String[] args) {
	        try {
	            ServiceManager.getManager().init();
	            instance = new Server();
	            instance.launch();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * 描述：启动服务
	     * 
	     * @throws Exception
	     */
	    public void launch() throws Exception {
	        long ns = System.currentTimeMillis();
	        log.info("渠道登陆接口服务器启动...");
	        openManagerServlet();
	        long ne = System.currentTimeMillis() - ns;
	        System.out.println("渠道登陆接口启动...启动耗时: " + ne + " ms");
	    }
	    
	    /**
	     * 后台管理服务
	     * @throws Exception
	     */
	    private void openManagerServlet() throws Exception {
	    	Configuration config = ServiceManager.getManager().getConfiguration();
	        org.mortbay.jetty.Server server = new org.mortbay.jetty.Server();
	        // 设置jetty线程池
	        BoundedThreadPool threadPool = new BoundedThreadPool();
	        // 设置连接参数	        
	        threadPool.setMinThreads(10);
	        threadPool.setMaxThreads(100);
	        // 设置监听端口，ip地址
	        SelectChannelConnector connector = new SelectChannelConnector();
	        connector.setPort(config.getInt("port"));
	        connector.setHost(config.getString("localip"));
	        server.addConnector(connector);
	        // 访问项目地址
	        Context root = new Context(server, "/", 1);
	        // 注册服务
	        root.addServlet(new ServletHolder(new ChannelLoginServlet()), "/ChannelLogin/*");
	        server.start();
	        //清理超时任务进程
	        TaskThread taskThread =new TaskThread();
	        taskThread.setTimeout(config.getInt("timeout"));
	        Thread t = new Thread(taskThread);	        
	        t.start();
	        System.out.println("端口: " + config.getInt("port"));
	    }
	

}
