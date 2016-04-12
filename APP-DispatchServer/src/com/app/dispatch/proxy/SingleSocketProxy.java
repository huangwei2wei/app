package com.app.dispatch.proxy;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.app.dispatch.Packet1;
import com.app.dispatch.ServerWYDEncoder;
import com.app.dispatch.SimpleWYDDecoderForProxyBind;
import com.app.dispatch.SimpleWYDDecoderForProxyConnect;
import com.app.dispatch.SimpleWYDEncoder;
import com.app.dispatch.TrustIpService;
import com.app.empire.protocol.Protocol;
import com.app.net.ProtocolFactory;
import com.app.protocol.INetSegment;
import com.app.protocol.utils.IpUtil;
public class SingleSocketProxy implements Runnable {
    private static final Logger                   log                = Logger.getLogger(SingleSocketProxy.class);
    private AtomicInteger                         ids;
    private ConcurrentHashMap<Integer, IoSession> clientSessions;
    private ConcurrentHashMap<String, IoSession>  serverSessions;
    //private static final String                   ATTRIBUTE_STRING   = "UWAPSESSIONID";
    public static final String                    ATTRIBUTE_SERVERID = "SERVERID";
    private NioSocketAcceptor                     acceptor;
    private NioSocketConnector                    connector;
    private TrustIpService                        trustIpService;
    private Configuration                         configuration;
    private List<ServerInfo>                      serverInfoList;
    private boolean                               shutdown;
    private File                                  propertyFile;
    private long                                  lastModified;

    public SingleSocketProxy() {
        this.ids = new AtomicInteger(1);
        this.clientSessions = new ConcurrentHashMap<Integer, IoSession>();
        this.serverSessions = new ConcurrentHashMap<String, IoSession>();
        this.acceptor = null;
        this.connector = null;
        this.trustIpService = null;
        this.configuration = null;
        this.serverInfoList = new ArrayList<ServerInfo>();
        this.shutdown = false;
    }

    public static void main(String[] args) throws Throwable {
        SingleSocketProxy main = new SingleSocketProxy();
        main.launch();
        System.out.println("proxy launched");
    }

    public void launch() throws Exception {
        ProtocolFactory.init(Protocol.class, "com.app.empire.protocol.data", "com.app.empire.server.handler");
        this.propertyFile = new File(System.getProperty("user.dir") + "/configProxy.properties");
        this.lastModified = this.propertyFile.lastModified();
        this.configuration = new PropertiesConfiguration("configProxy.properties");
        this.trustIpService = new TrustIpService("socket");
        loadServerInfoList();
        int clientreceivebuffsize = this.configuration.getInt("clientreceivebuffsize");
        int clientwritebuffsize = this.configuration.getInt("clientwritebuffsize");
        bind(new InetSocketAddress(this.configuration.getString("localip"), this.configuration.getInt("port")), clientreceivebuffsize, clientwritebuffsize);
        new Thread(this, "ProxyOnlinePrinter").start();
    }

    @SuppressWarnings("rawtypes")
    private void loadServerInfoList() {
        List list = this.configuration.getList("server");
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Object server = iterator.next();
            String s = server.toString();
            String[] serverInfo = s.split(":");
            ServerInfo info = new ServerInfo(serverInfo[0], Short.parseShort(serverInfo[1]));
            if (!(this.serverInfoList.contains(info))) {
                this.serverInfoList.add(info);
                int dispatchreceivebuffsize = this.configuration.getInt("dispatchreceivebuffsize");
                int dispatchwritebuffsize = this.configuration.getInt("dispatchwritebuffsize");
                ConnectFuture future = connect(new InetSocketAddress(info.getIp(), info.getPort()), info.getId(), dispatchreceivebuffsize, dispatchwritebuffsize);
                future.awaitUninterruptibly();
                log.info(info.getIp() + ":" + info.getPort() + " connected");
            }
        }
    }

    private void reloadServerInfoList() {
        long t = this.propertyFile.lastModified();
        boolean modified = false;
        if (t > this.lastModified) {
            this.lastModified = t;
            modified = true;
        }
        if (!(modified)) return;
        try {
            this.configuration = new PropertiesConfiguration("configProxy.properties");
            loadServerInfoList();
        } catch (ConfigurationException e) {
        }
    }

    public void bind(SocketAddress address, int clientreceivebuffsize, int clientwritebuffsize) throws IOException {
        this.acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
        this.acceptor.getSessionConfig().setTcpNoDelay(true);
        this.acceptor.getSessionConfig().setReceiveBufferSize(clientreceivebuffsize);
        this.acceptor.getSessionConfig().setSendBufferSize(clientwritebuffsize);
        this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SimpleWYDEncoder(), new SimpleWYDDecoderForProxyBind()));
        this.acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
        this.acceptor.setHandler(new ClientSessionHandler());
        this.acceptor.setDefaultLocalAddress(address);
        this.acceptor.bind();
        log.info("SingleSocketProxy launched on " + this.configuration.getString("localip") + ":" + this.configuration.getInt("port") + " with " + Runtime.getRuntime().availableProcessors() + " processors");
    }

    public ConnectFuture connect(SocketAddress address, String serverID, int dispatchreceivebuffsize, int dispatchwritebuffsize) {
        this.connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
        connector.getSessionConfig().setTcpNoDelay(true);
        connector.getSessionConfig().setReceiveBufferSize(dispatchreceivebuffsize);
        connector.getSessionConfig().setSendBufferSize(dispatchwritebuffsize);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerWYDEncoder(), new SimpleWYDDecoderForProxyConnect()));
        this.connector.setHandler(new ServerSessionHandler(serverID));
        this.connector.setDefaultRemoteAddress(address);
        return this.connector.connect();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(60000L);
            } catch (InterruptedException ex) {
            }
            log.info("Proxy ONLINE[" + this.clientSessions.size() + "]");
            reloadServerInfoList();
            try {
                Thread.sleep(20000L);
            } catch (InterruptedException ex) {
            }
            refreshServers();
        }
    }

    private void refreshServers() {
        for (ServerInfo info : this.serverInfoList)
            if (!(info.connected)) {
                int dispatchreceivebuffsize = this.configuration.getInt("dispatchreceivebuffsize");
                int dispatchwritebuffsize = this.configuration.getInt("dispatchwritebuffsize");
                ConnectFuture future = connect(new InetSocketAddress(info.getIp(), info.getPort()), info.getId(), dispatchreceivebuffsize, dispatchwritebuffsize);
                future.awaitUninterruptibly();
                log.info(info.getIp() + ":" + info.getPort() + " connected");
            }
    }

    public void registerClient(IoSession session) {
        int id = this.ids.incrementAndGet();
        if (id < 0) {
            log.info("SessionId[-1]");
        }
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
        session.setAttribute("UWAPSESSIONID", id);
        this.clientSessions.put(id, session);
    }

    protected void unRegisterClient(IoSession session) {
        Integer sessionId = (Integer) session.getAttribute("UWAPSESSIONID");
        if (sessionId != null) {
            sendControlSegment(session);
            this.clientSessions.remove(sessionId);
        }
    }

    private IoBuffer getEmtpySegment(int sessionId) {
        IoBuffer buf = IoBuffer.allocate(INetSegment.EMTPY_PACKET.length);
        buf.put(INetSegment.EMTPY_PACKET);
        buf.putInt(4, sessionId);
        buf.flip();
        return buf;
    }

    public void sendControlSegment(IoSession session) {
        Integer sessionId = (Integer) session.getAttribute("UWAPSESSIONID");
        if (sessionId != null) {
            IoBuffer buffer = getEmtpySegment(sessionId.intValue());
            String serverID = (String) session.getAttribute("SERVERID");
            if (serverID != null) {
                IoSession serverSession = (IoSession) this.serverSessions.get(serverID);
                if (serverSession == null) {
                    return;
                }
                serverSession.write(buffer);
            }
        }
    }

    public void unRegisterClient(int sessionId) {
        IoSession session = (IoSession) this.clientSessions.remove(Integer.valueOf(sessionId));
        if ((session != null) && (session.isConnected())) {
            session.close(true);
        }
    }

    public void dispatchToServer(IoSession session, Object object) {
        String serverID = (String) session.getAttribute("SERVERID");
        if (serverID == null) {
            log.error("ServerID Mapping Error [session:" + session.getAttribute("UWAPSESSIONID") + "]");
            unRegisterClient(session);
            return;
        }
        IoSession serverSession = (IoSession) this.serverSessions.get(serverID);
        if (serverSession == null) {
            unRegisterClient(session);
        } else {
            Integer id = (Integer) session.getAttribute("UWAPSESSIONID");
            if (id != null) {
                IoBuffer buffer = (IoBuffer) object;
                buffer.putInt(4, id.intValue());
                serverSession.write(buffer.duplicate());
            }
        }
    }

    public void dispatchToClient(Packet1 packet) {
        dispatchToClient(packet.sessionId, packet.buffer);
    }

    public void dispatchToClient(int sessionId, IoBuffer buffer) {
        IoSession s = this.clientSessions.get(Integer.valueOf(sessionId));
        if (s != null) s.write(buffer);
    }

    private void serverDisconnected(String serverID) {
        ServerInfo info = findServerInfo(serverID);
        if (info != null) info.connected = false;
    }

    private void serverConnected(String serverID) {
        ServerInfo info = findServerInfo(serverID);
        if (info != null) info.connected = true;
    }

    private ServerInfo findServerInfo(String serverID) {
        for (ServerInfo info : this.serverInfoList) {
            if (info.id.equals(serverID)) {
                return info;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void removeServerinfo(String serverID) {
        for (ServerInfo info : this.serverInfoList)
            if (info.id.equals(serverID)) {
                this.serverInfoList.remove(info);
                return;
            }
    }
    class ServerInfo {
        private String  id;
        private String  ip;
        private boolean connected;
        private int     port;

        private ServerInfo(String ip, short port) {
            this.ip = ip;
            this.port = port;
            id = IpUtil.toServerID(ip, port);
        }

        public String getId() {
            return this.id;
        }

        public String getIp() {
            return this.ip;
        }

        public int getPort() {
            return this.port;
        }

        public boolean equals(Object object) {
            if (object instanceof ServerInfo) {
                return ((ServerInfo) object).id.equals(this.id);
            }
            return false;
        }

        public boolean isConnected() {
            return this.connected;
        }
    }
    class ServerSessionHandler extends IoHandlerAdapter {
        private String serverID;

        public ServerSessionHandler(String paramString) {
            this.serverID = paramString;
        }

        public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
            SingleSocketProxy.log.error(throwable, throwable);
        }

        public void messageReceived(IoSession session, Object object) throws Exception {
            Packet1 packet = (Packet1) object;
            if (packet.type == Packet1.TYPE.BUFFER) {
                SingleSocketProxy.this.dispatchToClient(packet);
            } else if (packet.param == 0) {
                SingleSocketProxy.this.unRegisterClient(packet.sessionId);
            } else {
                IoSession[] sessions = new IoSession[SingleSocketProxy.this.clientSessions.values().size()];
                SingleSocketProxy.this.clientSessions.values().toArray(sessions);
                for (IoSession s : sessions) {
                    String serverID = (String) s.getAttribute("SERVERID");
                    if (this.serverID.equals(serverID)) {
                        Integer sessionId = (Integer) s.getAttribute("UWAPSESSIONID");
                        if (sessionId != null) SingleSocketProxy.this.unRegisterClient(sessionId.intValue());
                    }
                }
            }
        }

        public void sessionClosed(IoSession session) throws Exception {
            SingleSocketProxy.this.serverSessions.remove(this.serverID);
            SingleSocketProxy.this.serverDisconnected(this.serverID);
            IoSession[] sessions = new IoSession[SingleSocketProxy.this.clientSessions.values().size()];
            SingleSocketProxy.this.clientSessions.values().toArray(sessions);
            for (IoSession s : sessions) {
                String serverID = (String) s.getAttribute("SERVERID");
                if (this.serverID.equals(serverID)) {
                    Integer sessionId = (Integer) s.getAttribute("UWAPSESSIONID");
                    if (sessionId != null) SingleSocketProxy.this.unRegisterClient(sessionId.intValue());
                }
            }
        }

        public void sessionOpened(IoSession session) throws Exception {
            SingleSocketProxy.this.serverSessions.put(this.serverID, session);
            SingleSocketProxy.this.serverConnected(this.serverID);
            IoBuffer byteBuffer = IoBuffer.allocate(2);
            byteBuffer.putShort(SingleSocketProxy.this.configuration.getShort("port"));
            byteBuffer.flip();
            session.write(byteBuffer);
        }

        public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
        }
    }
    class ClientSessionHandler extends IoHandlerAdapter {
        public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
            SingleSocketProxy.log.error(throwable, throwable);
        }

        public void messageReceived(IoSession session, Object object) throws Exception {
            SingleSocketProxy.this.dispatchToServer(session, object);
        }

        public void sessionClosed(IoSession session) throws Exception {
            if (!(SingleSocketProxy.this.shutdown)) SingleSocketProxy.this.unRegisterClient(session);
        }

        public void sessionOpened(IoSession session) throws Exception {
            InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
            if (!(SingleSocketProxy.this.trustIpService.isTrustIp(address)))
                session.close(true);
            else
                SingleSocketProxy.this.registerClient(session);
        }

        public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
            session.close(true);
        }
    }
}