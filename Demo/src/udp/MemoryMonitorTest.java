package udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.ExpiringSessionRecycler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class MemoryMonitorTest {

	public static final int PORT = 8088;

	public MemoryMonitorTest() throws IOException {

		NioDatagramAcceptor acceptor = new NioDatagramAcceptor();// 创建一个UDP的接收器
		acceptor.setHandler(new YourHandler());// 设置接收器的处理程序

		Executor threadPool = Executors.newFixedThreadPool(1500);// 建立线程池
		acceptor.getFilterChain().addLast("exector", new ExecutorFilter(threadPool));
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.setSessionRecycler(new ExpiringSessionRecycler(120));
		
		
		DatagramSessionConfig dcfg = acceptor.getSessionConfig();// 建立连接的配置文件
		dcfg.setReadBufferSize(4096);// 设置接收最大字节默认2048
		dcfg.setReceiveBufferSize(1024);// 设置输入缓冲区的大小
		dcfg.setSendBufferSize(1024);// 设置输出缓冲区的大小
		dcfg.setReuseAddress(true);// 设置每一个非主监听连接的端口可以重用

		dcfg.setIdleTime(IdleStatus.BOTH_IDLE, 5);
		acceptor.bind(new InetSocketAddress(PORT));// 绑定端口
		System.out.println("udp 服务开启");

	}

	public static void main(String[] args) throws IOException {
		new MemoryMonitorTest();
	}
}