package com.app.net.http;
import javax.servlet.http.HttpServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
public class JettyServer {
    private Server  server = null;
    private Context root   = null;

    public JettyServer(String host, int port, int minThread, int maxThread) {
        this.server = new Server(); // 创建一个新的HttpServer
        // BoundedThreadPool threadPool = new BoundedThreadPool();
        // threadPool.setMinThreads(minThread);
        // threadPool.setMaxThreads(maxThread);
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port); // 设置监听端口
        connector.setHost(host); // 设置监听主机
        this.root = new Context(this.server, "/", 1); // 建一个新HttpContext并将访问路径设为根目录
        this.server.addConnector(connector);
    }

    public void addServlet(String url, HttpServlet servlet) {
        this.root.addServlet(new ServletHolder(servlet), url);
    }

    public void start() throws Exception {
        this.server.start();
    }
}
