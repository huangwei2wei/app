// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import atavism.server.util.AORuntimeException;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import atavism.server.util.Log;
import java.util.Properties;
import java.nio.ByteBuffer;
import atavism.server.network.TcpServer;
import atavism.server.network.TcpAcceptCallback;

public class SocketPolicyHandler implements TcpAcceptCallback
{
    protected final String DEFAULT_POLICY = "<?xml version=\"1.0\"?>\n<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\n<cross-domain-policy>\n  <site-control permitted-cross-domain-policies=\"master-only\"/>\n  <allow-access-from domain=\"*\" to-ports=\"*\"/>\n</cross-domain-policy>\n";
    protected final int DEFAULT_PORT = 843;
    protected final String POLICY_REQUEST_STRING = "<policy-file-request/>";
    protected TcpServer tcpServer;
    protected int port;
    protected String policy;
    protected ByteBuffer policyBuffer;
    public static int idleTimeout;
    boolean devMode;
    
    public SocketPolicyHandler(final Properties properties) {
        this.tcpServer = new TcpServer();
        this.port = 843;
        this.policy = null;
        this.policyBuffer = null;
        this.devMode = true;
        try {
            final String policyStr = "<?xml version=\"1.0\"?>\n<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\n<cross-domain-policy>\n  <site-control permitted-cross-domain-policies=\"master-only\"/>\n  <allow-access-from domain=\"*\" to-ports=\"*\"/>\n</cross-domain-policy>\n";
            final InetSocketAddress bindAddress = this.getBindAddress(properties);
            if (Log.loggingDebug) {
                Log.debug("SocketPolicy: binding for incoming client connections at: " + bindAddress);
            }
            this.setPolicy(policyStr);
            String socketPolicyFile = properties.getProperty("atavism.socketpolicy.policyfile");
            if (socketPolicyFile != null) {
                socketPolicyFile = socketPolicyFile.trim();
                if (Log.loggingDebug) {
                    Log.debug("SocketPolicy: serving policy file from " + socketPolicyFile);
                }
                try {
                    final StringBuffer fileData = new StringBuffer();
                    final BufferedReader reader = new BufferedReader(new FileReader(socketPolicyFile));
                    final char[] buf = new char[1024];
                    int bytesRead = 0;
                    while ((bytesRead = reader.read(buf)) != -1) {
                        fileData.append(buf, 0, bytesRead);
                    }
                    reader.close();
                    this.setPolicy(fileData.toString());
                }
                catch (IOException ex) {
                    Log.warn("Unable to load policy file: " + ex);
                }
            }
            Log.debug("Set policy complete");
            (this.tcpServer = new TcpServer()).bind(bindAddress);
            this.tcpServer.registerAcceptCallback(this);
            this.tcpServer.start();
            Log.debug("Started server");
            Log.debug("SocketPolicy: activation done");
        }
        catch (Exception e) {
            Log.error("activate failed" + e);
            throw new AORuntimeException("activate failed", e);
        }
    }
    
    private InetSocketAddress getBindAddress(final Properties properties) throws IOException {
        String propStr = properties.getProperty("atavism.socketpolicy.bindport");
        int port = 0;
        if (propStr != null) {
            port = Integer.parseInt(propStr.trim());
        }
        propStr = properties.getProperty("atavism.socketpolicy.bindaddress");
        InetAddress address = null;
        if (propStr != null) {
            address = InetAddress.getByName(propStr.trim());
        }
        return new InetSocketAddress(address, port);
    }
    
    protected Object createMBeanInstance() {
        return new SocketPolicyJMX();
    }
    
    @Override
    public void onTcpAccept(final SocketChannel sc) {
        try {
            final int DEFAULT_CAPACITY = 1024;
            final int TIMEOUT = 2000;
            final byte[] readData = new byte[DEFAULT_CAPACITY];
            final ByteBuffer readBuffer = ByteBuffer.wrap(readData);
            final Selector selector = Selector.open();
            final SelectionKey key = sc.register(selector, 1);
            final int keyCount = selector.select(TIMEOUT);
            if (keyCount == 0) {
                Log.info("Timed out waiting for data to read");
                return;
            }
            Log.info("Reading from socket channel");
            sc.read(readBuffer);
            Log.info("After read; position = " + readBuffer.position() + "; remaining = " + readBuffer.remaining() + "; limit = " + readBuffer.limit());
            readData[readBuffer.position()] = 0;
            final String readStr = new String(readData, 0, readBuffer.position(), "US-ASCII");
            if (!readStr.startsWith("<policy-file-request/>")) {
                Log.info("Unrecognized socket policy request: " + readStr);
            }
            else {
                final ByteBuffer writeBuffer = this.policyBuffer;
                synchronized (writeBuffer) {
                    writeBuffer.rewind();
                    sc.write(writeBuffer);
                }
                Log.debug("Recognized socket policy request: " + readStr);
            }
        }
        catch (IOException ex) {
            Log.warn("Failed to handle client connection: " + ex.getMessage());
            try {
                sc.close();
            }
            catch (IOException ex) {
                Log.warn("Failed to close client connection: " + ex.getMessage());
            }
        }
        finally {
            try {
                sc.close();
            }
            catch (IOException ex2) {
                Log.warn("Failed to close client connection: " + ex2.getMessage());
            }
        }
    }
    
    protected void setPolicy(final String policy) {
        final byte[] policyData = policy.getBytes();
        this.policyBuffer = ByteBuffer.wrap(policyData);
    }
    
    static {
        SocketPolicyHandler.idleTimeout = 60;
    }
    
    protected class SocketPolicyJMX implements SocketPolicyJMXMBean
    {
        @Override
        public int getPort() {
            return SocketPolicyHandler.this.port;
        }
        
        @Override
        public void setPort(final int val) {
            SocketPolicyHandler.this.port = val;
        }
        
        @Override
        public int getIdleTimeout() {
            return SocketPolicyHandler.idleTimeout;
        }
        
        @Override
        public void setIdleTimeout(final int timeout) {
            if (timeout > 0) {
                SocketPolicyHandler.idleTimeout = timeout;
            }
        }
        
        @Override
        public String getPolicy() {
            return SocketPolicyHandler.this.policy;
        }
        
        @Override
        public void setPolicy(final String str) {
            SocketPolicyHandler.this.setPolicy(str);
        }
    }
    
    public interface SocketPolicyJMXMBean
    {
        int getPort();
        
        void setPort(final int p0);
        
        int getIdleTimeout();
        
        void setIdleTimeout(final int p0);
        
        String getPolicy();
        
        void setPolicy(final String p0);
    }
}
