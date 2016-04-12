package com.app.dispatch;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;

import com.app.empire.protocol.Protocol;
import com.app.protocol.INetSegment;
import com.app.protocol.s2s.S2SSegment;
public class HttpAcceptor implements Runnable {
	private AtomicInteger ids = new AtomicInteger(1);
	@SuppressWarnings("unused")
	private int port = 80;
	private IoHandler handler;
	private HttpWYDDecoder decoder = new HttpWYDDecoder();
	private HttpWYDEncoder encoder = new HttpWYDEncoder();
	private ConcurrentHashMap<Integer, HttpSession> sessions = new ConcurrentHashMap<Integer, HttpSession>();
	private Server server;

	public void bind(String address, int port, IoHandler handler) throws Exception {
		this.port = port;
		this.handler = handler;
		this.server = new Server();
		// BoundedThreadPool threadPool = new BoundedThreadPool();
		// threadPool.setMinThreads(10);
		// threadPool.setMaxThreads(50);
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setHost(address);
		this.server.addConnector(connector);
		Context root = new Context(this.server, "/", 1);
		root.addServlet(new ServletHolder(new HttpUWAPServlet()), "/*");
		this.server.start();
		new Thread(this).start();
	}

	public void notifyClose(HttpSession session) {
		try {
			synchronized (session) {
				this.sessions.remove(Integer.valueOf(session.getSessionId()));
			}
			this.handler.sessionClosed(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int size() {
		return this.sessions.size();
	}

	private void handle(Packet[] packet, ServletOutputStream out, HttpServletRequest request) throws Exception {
		HttpSession httpSession = null;
		for (int i = 0; i < packet.length; ++i) {
			httpSession = handle(packet[i], out, request);
		}
		if (httpSession == null)
			return;
		IoBuffer[] segments = httpSession.getSegments();
		if ((segments == null) || (segments.length == 0)) {
			try {
				synchronized (httpSession) {
					httpSession.wait(1000L);
				}
			} catch (InterruptedException ex1) {
			}
			segments = httpSession.getSegments();
		}
		if ((segments == null) || (segments.length == 0)) {
			segments = new IoBuffer[1];
			INetSegment segment = new S2SSegment((byte) Protocol.MAIN_SYSTEM, (byte) Protocol.SYSTEM_NOP);
			segment.setSessionId(httpSession.getSessionId());
			segments[0] = IoBuffer.wrap(segment.getPacketByteArray());
		}
		try {
			this.encoder.encode(out, segments);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private HttpSession handle(Packet packet, ServletOutputStream out, HttpServletRequest request) throws Exception {
		if (packet.sessionId == -1) {
			InetSocketAddress address = new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort());
			HttpSession session = new HttpSession(this, address, this.ids.incrementAndGet());
			this.sessions.put(Integer.valueOf(session.getSessionId()), session);
			this.handler.sessionCreated(session);
			this.handler.messageReceived(session, packet.buffer);
			return session;
		}
		HttpSession session = (HttpSession) this.sessions.get(Integer.valueOf(packet.sessionId));
		if (session != null) {
			session.setLastReadTime(System.currentTimeMillis());
			if ((packet.pType == 64) && (packet.pSubType == 3)) {
				Log.info("HTTP Close");
				closeSession(session.getSessionId());
			} else if ((packet.pType != 64) || (packet.pSubType != 1)) {
				this.handler.messageReceived(session, packet.buffer);
			}
		}
		return session;
	}

	public HttpSession getSession(int sessionId) {
		return ((HttpSession) this.sessions.get(Integer.valueOf(sessionId)));
	}

	public void closeSession(int sessionId) {
		HttpSession session = (HttpSession) this.sessions.get(Integer.valueOf(sessionId));
		if (session != null)
			session.close();
	}

	public void broadcast(IoBuffer buffer) {
		for (IoSession session : this.sessions.values())
			session.write(buffer.duplicate());
	}

	public void stop() {
		try {
			this.server.stop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException ex) {
			}
			try {
				checkIdle();
			} catch (Throwable ex1) {
				ex1.printStackTrace();
			}
		}
	}

	private void checkIdle() {
		// synchronized (this.sessions) {
		Iterator<HttpSession> ite = this.sessions.values().iterator();
		long currTime = System.currentTimeMillis();
		while (ite.hasNext()) {
			HttpSession session = (HttpSession) ite.next();
			if (currTime - session.getLastReadTime() > 300000L)
				session.close();
		}
		// }
	}
	@SuppressWarnings("unused")
	private class HandleResult {
		HttpSession session;
		int idleTime;

		public void setIdleTime(int time) {
			if (this.idleTime < time)
				this.idleTime = time;
		}
	}
	@SuppressWarnings("serial")
	class HttpUWAPServlet extends HttpServlet {
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("application/octet-stream");
			response.setStatus(200);
			try {
				Packet[] packets = HttpAcceptor.this.decoder.decode(request.getInputStream());
				HttpAcceptor.this.handle(packets, response.getOutputStream(), request);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("application/octet-stream");
			response.setStatus(200);
		}
	}
}