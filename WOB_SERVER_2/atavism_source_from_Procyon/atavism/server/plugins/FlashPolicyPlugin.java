// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import java.io.Serializable;
import java.util.LinkedHashMap;
import atavism.msgsys.Message;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import atavism.server.engine.Hook;
import atavism.management.Management;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import atavism.server.util.Log;
import java.io.IOException;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import atavism.server.util.Logger;
import java.nio.ByteBuffer;
import atavism.server.network.TcpServer;
import atavism.server.network.TcpAcceptCallback;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.EnginePlugin;

public class FlashPolicyPlugin extends EnginePlugin implements MessageCallback, TcpAcceptCallback
{
    protected final String DEFAULT_POLICY = "<?xml version=\"1.0\"?>\n<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\n<cross-domain-policy>\n  <site-control permitted-cross-domain-policies=\"master-only\"/>\n  <allow-access-from domain=\"*\" to-ports=\"*\"/>\n</cross-domain-policy>\n";
    protected final int DEFAULT_PORT = 843;
    protected final String POLICY_REQUEST_STRING = "<policy-file-request/>";
    protected TcpServer tcpServer;
    protected int port;
    protected String policy;
    protected ByteBuffer policyBuffer;
    protected static final Logger log;
    public static int idleTimeout;
    boolean devMode;
    
    public FlashPolicyPlugin() {
        this.tcpServer = new TcpServer();
        this.port = 843;
        this.policy = null;
        this.policyBuffer = null;
        this.devMode = true;
        this.setPluginType("FlashPolicy");
        String flashPolicyPluginName;
        try {
            flashPolicyPluginName = Engine.getAgent().getDomainClient().allocName("PLUGIN", this.getPluginType() + "#");
        }
        catch (IOException e) {
            throw new AORuntimeException("Could not allocate flash policy plugin name", e);
        }
        this.setName(flashPolicyPluginName);
        this.setMessageHandler(null);
    }
    
    @Override
    public void onActivate() {
        try {
            this.registerHooks();
            final int flashPolicyPort = 843;
            final String policyStr = "<?xml version=\"1.0\"?>\n<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\n<cross-domain-policy>\n  <site-control permitted-cross-domain-policies=\"master-only\"/>\n  <allow-access-from domain=\"*\" to-ports=\"*\"/>\n</cross-domain-policy>\n";
            final InetSocketAddress bindAddress = this.getBindAddress();
            if (Log.loggingDebug) {
                Log.debug("FlashPolicy: binding for incoming client connections at: " + bindAddress);
            }
            this.setPolicy(policyStr);
            String flashPolicyFile = Engine.getProperty("atavism.flashpolicy.policyfile");
            if (flashPolicyFile != null) {
                flashPolicyFile = flashPolicyFile.trim();
                if (Log.loggingDebug) {
                    Log.debug("FlashPolicy: serving policy file from " + flashPolicyFile);
                }
                try {
                    final StringBuffer fileData = new StringBuffer();
                    final BufferedReader reader = new BufferedReader(new FileReader(flashPolicyFile));
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
            Engine.registerStatusReportingPlugin(this);
            Log.debug("FlashPolicy: activation done");
        }
        catch (Exception e) {
            Log.error("activate failed" + e);
            throw new AORuntimeException("activate failed", e);
        }
    }
    
    private InetSocketAddress getBindAddress() throws IOException {
        String propStr = Engine.getProperty("atavism.flashpolicy.bindport");
        int port = 0;
        if (propStr != null) {
            port = Integer.parseInt(propStr.trim());
        }
        propStr = Engine.getProperty("atavism.flashpolicy.bindaddress");
        InetAddress address = null;
        if (propStr != null) {
            address = InetAddress.getByName(propStr.trim());
        }
        return new InetSocketAddress(address, port);
    }
    
    void registerHooks() {
        FlashPolicyPlugin.log.debug("registering hooks");
        this.getHookManager().addHook(Management.MSG_TYPE_GET_PLUGIN_STATUS, new GetPluginStatusHook());
    }
    
    @Override
    protected Object createMBeanInstance() {
        return new FlashPolicyJMX();
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
                Log.info("Unrecognized flash policy request: " + readStr);
            }
            else {
                final ByteBuffer writeBuffer = this.policyBuffer;
                synchronized (writeBuffer) {
                    writeBuffer.rewind();
                    sc.write(writeBuffer);
                }
                Log.debug("Recognized flash policy request: " + readStr);
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
        log = new Logger("FlashPolicyPlugin");
        FlashPolicyPlugin.idleTimeout = 60;
    }
    
    class GetPluginStatusHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final LinkedHashMap<String, Serializable> status = new LinkedHashMap<String, Serializable>();
            status.put("plugin", FlashPolicyPlugin.this.getName());
            Engine.getAgent().sendObjectResponse(msg, status);
            return true;
        }
    }
    
    protected class FlashPolicyJMX implements FlashPolicyJMXMBean
    {
        @Override
        public int getPort() {
            return FlashPolicyPlugin.this.port;
        }
        
        @Override
        public void setPort(final int val) {
            FlashPolicyPlugin.this.port = val;
        }
        
        @Override
        public int getIdleTimeout() {
            return FlashPolicyPlugin.idleTimeout;
        }
        
        @Override
        public void setIdleTimeout(final int timeout) {
            if (timeout > 0) {
                FlashPolicyPlugin.idleTimeout = timeout;
            }
        }
        
        @Override
        public String getPolicy() {
            return FlashPolicyPlugin.this.policy;
        }
        
        @Override
        public void setPolicy(final String str) {
            FlashPolicyPlugin.this.setPolicy(str);
        }
    }
    
    public interface FlashPolicyJMXMBean
    {
        int getPort();
        
        void setPort(final int p0);
        
        int getIdleTimeout();
        
        void setIdleTimeout(final int p0);
        
        String getPolicy();
        
        void setPolicy(final String p0);
    }
}
