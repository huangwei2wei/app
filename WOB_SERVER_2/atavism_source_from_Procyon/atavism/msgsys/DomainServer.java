// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.nio.ByteBuffer;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.lang.management.RuntimeMXBean;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.lang.management.ManagementFactory;
import atavism.server.marshalling.MarshallingRuntime;
import java.util.Iterator;
import java.net.InetAddress;
import atavism.server.network.ChannelUtil;
import atavism.server.network.AOByteBuffer;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ThreadFactory;
import javax.crypto.SecretKey;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Properties;
import atavism.server.util.FileUtil;
import atavism.server.util.Base64;
import java.util.Random;
import atavism.server.util.SecureTokenUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import atavism.server.util.Log;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.util.LinkedList;
import atavism.server.util.ServerVersion;
import atavism.server.util.InitLogAndPid;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.List;
import atavism.server.network.TcpServer;
import atavism.server.network.TcpAcceptCallback;

public class DomainServer implements TcpAcceptCallback, MessageIO.Callback
{
    public static final int DEFAULT_PORT = 20374;
    static DomainServer domainServer;
    private int listenPort;
    private TcpServer listener;
    private List<String> agentNames;
    private ExecutorService threadPool;
    private PluginStartGroup pluginStartGroup;
    private String worldName;
    private long domainStartTime;
    private static String encodedDomainKey;
    private int nextAgentId;
    private Map<SocketChannel, AgentInfo> agents;
    private Map<String, Map<String, Integer>> nameTypes;
    private MessageIO messageIO;
    
    public static void main(final String[] args) {
        final String worldName = System.getProperty("atavism.worldname");
        final String hostName = determineHostName();
        final Properties properties = InitLogAndPid.initLogAndPid(args, worldName, hostName);
        System.err.println("Atavism server version " + ServerVersion.getVersionString());
        final List<String> agentNames = new LinkedList<String>();
        final LongOpt[] longopts = { new LongOpt("pid", 1, (StringBuffer)null, 2), new LongOpt("port", 1, (StringBuffer)null, 3) };
        final Getopt opt = new Getopt("DomainServer", args, "a:m:t:p:P:", longopts);
        int port = 20374;
        final String portStr = properties.getProperty("atavism.msgsvr_port");
        if (portStr != null) {
            port = Integer.parseInt(portStr);
        }
        final PluginStartGroup pluginStartGroup = new PluginStartGroup();
        final boolean pluginsDefined = populatePluginStartGroup(pluginStartGroup, properties);
        int c;
        while ((c = opt.getopt()) != -1) {
            switch (c) {
                case 97: {
                    agentNames.add(opt.getOptarg());
                    continue;
                }
                case 109:
                case 116: {
                    opt.getOptarg();
                    continue;
                }
                case 112: {
                    if (!pluginsDefined) {
                        final String pluginSpec = opt.getOptarg();
                        final String[] pluginDef = pluginSpec.split(",", 2);
                        if (pluginDef.length != 2) {
                            System.err.println("Invalid plugin spec format: " + pluginSpec);
                            Log.error("Invalid plugin spec format: " + pluginSpec);
                            System.exit(1);
                        }
                        final int expected = Integer.parseInt(pluginDef[1]);
                        pluginStartGroup.add(pluginDef[0], expected);
                        continue;
                    }
                    continue;
                }
                case 63: {
                    System.exit(1);
                }
                case 80: {
                    continue;
                }
                case 2: {
                    opt.getOptarg();
                    continue;
                }
                case 3: {
                    final String arg = opt.getOptarg();
                    port = Integer.parseInt(arg);
                    continue;
                }
            }
        }
        final String svrName = System.getProperty("atavism.loggername");
        final String runDir = System.getProperty("atavism.rundir");
        if (System.getProperty("os.name").contains("Windows") && svrName != null && runDir != null) {
            saveProcessID(svrName, runDir);
        }
        (DomainServer.domainServer = new DomainServer(port)).setAgentNames(agentNames);
        DomainServer.domainServer.setWorldName(worldName);
        DomainServer.domainServer.start();
        pluginStartGroup.prepareDependencies(properties, worldName);
        DomainServer.domainServer.addPluginStartGroup(pluginStartGroup);
        pluginStartGroup.pluginAvailable("Domain", "Domain");
        final String timeoutStr = properties.getProperty("atavism.startup_timeout");
        int timeout = 120;
        if (timeoutStr != null) {
            timeout = Integer.parseInt(timeoutStr);
        }
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final ScheduledFuture<?> timeoutHandler = scheduler.schedule(new TimeoutRunnable(timeout), timeout, TimeUnit.SECONDS);
        final SecretKey domainKey = SecureTokenUtil.generateDomainKey();
        final long keyId = new Random().nextLong();
        DomainServer.encodedDomainKey = Base64.encodeBytes(SecureTokenUtil.encodeDomainKey(keyId, domainKey));
        Log.debug("generated domain key: " + DomainServer.encodedDomainKey);
        try {
            pluginStartGroup.awaitDependency("Domain");
            timeoutHandler.cancel(false);
            final String availableMessage = properties.getProperty("atavism.world_available_message");
            final String availableFile = properties.getProperty("atavism.world_available_file");
            if (availableFile != null) {
                touchFile(FileUtil.expandFileName(availableFile));
            }
            if (availableMessage != null) {
                System.err.println("\n" + availableMessage);
            }
            while (true) {
                Thread.sleep(10000000L);
            }
        }
        catch (Exception ex) {
            Log.exception("DomainServer.main", ex);
        }
    }
    
    private static boolean populatePluginStartGroup(final PluginStartGroup pluginStartGroup, final Properties properties) {
        final String pluginList = properties.getProperty("atavism.plugin_types");
        if (pluginList == null) {
            Log.warn("Missing plugin list in properties file");
            return false;
        }
        final String[] pluginEntries = pluginList.split(",");
        for (int i = 0; i < pluginEntries.length; ++i) {
            String pluginDef;
            final String pluginEntry = pluginDef = pluginEntries[i].trim();
            int expected = 1;
            final int index = pluginEntry.indexOf(58);
            if (index >= 0) {
                pluginDef = pluginEntry.substring(0, index);
                try {
                    expected = Integer.parseInt(pluginEntry.substring(index + 1));
                }
                catch (Exception e) {
                    Log.error("Failed to parse plugin entry: " + pluginEntry);
                }
            }
            Log.debug("Adding plugin entry for " + pluginDef);
            pluginStartGroup.add(pluginDef, expected);
        }
        return true;
    }
    
    public DomainServer(final int port) {
        this.agentNames = new LinkedList<String>();
        this.threadPool = Executors.newCachedThreadPool(new DomainThreadFactory());
        this.nextAgentId = 1;
        this.agents = new HashMap<SocketChannel, AgentInfo>();
        this.nameTypes = new HashMap<String, Map<String, Integer>>();
        MessageTypes.initializeCatalog();
        this.listenPort = port;
        this.messageIO = new MessageIO(this);
    }
    
    public void setAgentNames(final List<String> names) {
        this.agentNames = new LinkedList<String>(names);
    }
    
    public List<String> getAgentNames() {
        return this.agentNames;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public void setWorldName(final String worldName) {
        this.worldName = worldName;
    }
    
    public void start() {
        try {
            this.domainStartTime = System.currentTimeMillis();
            this.messageIO.start();
            (this.listener = new TcpServer(this.listenPort)).registerAcceptCallback(this);
            this.listener.start();
        }
        catch (Exception e) {
            Log.exception("DomainServer listener", e);
            System.exit(1);
        }
    }
    
    @Override
    public void onTcpAccept(final SocketChannel agentSocket) {
        Log.debug("Got connection: " + agentSocket);
        try {
            this.threadPool.execute(new AgentHandler(agentSocket));
        }
        catch (IOException ex) {
            Log.exception("DomainServer listener", ex);
        }
    }
    
    void handleAllocName(final AllocNameMessage allocName, final SocketChannel agentSocket) throws IOException {
        if (allocName.getMsgType() != MessageTypes.MSG_TYPE_ALLOC_NAME) {
            Log.error("DomainServer: invalid alloc name message");
            return;
        }
        final String agentName = this.allocName(allocName.getType(), allocName.getAgentName());
        final AOByteBuffer buffer = new AOByteBuffer(1024);
        final AllocNameResponseMessage allocNameResponse = new AllocNameResponseMessage(allocName, agentName);
        Message.toBytes(allocNameResponse, buffer);
        buffer.flip();
        if (!ChannelUtil.writeBuffer(buffer, agentSocket)) {
            throw new RuntimeException("could not write alloc name response");
        }
    }
    
    void handleAwaitPluginDependents(final AwaitPluginDependentsMessage await, final SocketChannel agentSocket) throws IOException {
        if (await.getMsgType() != MessageTypes.MSG_TYPE_AWAIT_PLUGIN_DEPENDENTS) {
            Log.error("DomainServer: invalid await message");
            return;
        }
        if (this.pluginStartGroup == null) {
            Log.error("DomainServer: no start group defined for plugin type=" + await.getPluginType() + " name=" + await.getPluginName());
            return;
        }
        new Thread(new PluginDependencyWatcher(await, agentSocket)).start();
    }
    
    void handlePluginAvailable(final PluginAvailableMessage available, final SocketChannel agentSocket) throws IOException {
        if (available.getMsgType() != MessageTypes.MSG_TYPE_PLUGIN_AVAILABLE) {
            Log.error("DomainServer: invalid available message");
            return;
        }
        if (this.pluginStartGroup == null) {
            Log.error("DomainServer: no start group defined for plugin type=" + available.getPluginType() + " name=" + available.getPluginName());
            return;
        }
        this.pluginStartGroup.pluginAvailable(available.getPluginType(), available.getPluginName());
    }
    
    private int getNextAgentId() {
        return this.nextAgentId++;
    }
    
    private synchronized void addNewAgent(final int agentId, final SocketChannel socket, final String agentName, String agentIP, final int agentPort, final int flags) {
        if (agentIP.equals(":same")) {
            final InetAddress agentAddress = socket.socket().getInetAddress();
            agentIP = agentAddress.getHostAddress();
        }
        Log.info("New agent id=" + agentId + " name=" + agentName + " address=" + agentIP + ":" + agentPort + " flags=" + flags);
        final AgentInfo agentInfo = new AgentInfo();
        agentInfo.agentId = agentId;
        agentInfo.flags = flags;
        agentInfo.socket = socket;
        agentInfo.agentName = agentName;
        agentInfo.agentIP = agentIP;
        agentInfo.agentPort = agentPort;
        agentInfo.outputBuf = new AOByteBuffer(1024);
        agentInfo.inputBuf = new AOByteBuffer(1024);
        this.agents.put(socket, agentInfo);
        final NewAgentMessage newAgentMessage = new NewAgentMessage(agentId, agentName, agentIP, agentPort, flags);
        for (final Map.Entry<SocketChannel, AgentInfo> entry : this.agents.entrySet()) {
            if (entry.getKey() == socket) {
                continue;
            }
            synchronized (entry.getValue().outputBuf) {
                Message.toBytes(newAgentMessage, entry.getValue().outputBuf);
            }
            final NewAgentMessage otherAgentMessage = new NewAgentMessage(entry.getValue().agentId, entry.getValue().agentName, entry.getValue().agentIP, entry.getValue().agentPort, entry.getValue().flags);
            synchronized (agentInfo.outputBuf) {
                Message.toBytes(otherAgentMessage, agentInfo.outputBuf);
            }
        }
        this.messageIO.addAgent(agentInfo);
        this.messageIO.outputReady();
    }
    
    @Override
    public void handleMessageData(final int length, final AOByteBuffer messageData, final AgentInfo agentInfo) {
        if (length == -1 || messageData == null) {
            if ((agentInfo.flags & 0x1) != 0x0) {
                Log.info("Lost connection to '" + agentInfo.agentName + "' (transient)");
                this.agents.remove(agentInfo.socket);
                this.agentNames.remove(agentInfo.agentName);
                this.messageIO.removeAgent(agentInfo);
            }
            else {
                Log.info("Lost connection to '" + agentInfo.agentName + "'");
            }
            try {
                agentInfo.socket.close();
            }
            catch (IOException ex) {
                Log.exception("close", ex);
            }
            agentInfo.socket = null;
            return;
        }
        final Message message = (Message)MarshallingRuntime.unmarshalObject(messageData);
        final MessageType msgType = message.getMsgType();
        if (Log.loggingDebug) {
            Log.debug("handleMessageData from " + agentInfo.agentName + "," + message.getMsgId() + " type=" + msgType.getMsgTypeString() + " len=" + length + " class=" + message.getClass().getName());
        }
        try {
            if (message instanceof AllocNameMessage) {
                this.handleAllocName((AllocNameMessage)message, agentInfo.socket);
            }
            else if (message instanceof AwaitPluginDependentsMessage) {
                this.handleAwaitPluginDependents((AwaitPluginDependentsMessage)message, agentInfo.socket);
            }
            else if (message instanceof PluginAvailableMessage) {
                this.handlePluginAvailable((PluginAvailableMessage)message, agentInfo.socket);
            }
            else {
                Log.error("Unsupported message from " + agentInfo.agentName + "," + message.getMsgId() + " type=" + msgType.getMsgTypeString() + " len=" + length + " class=" + message.getClass().getName());
            }
        }
        catch (IOException e) {
            Log.error("IO error on message from " + agentInfo.agentName + "," + message.getMsgId() + " type=" + msgType.getMsgTypeString() + " len=" + length + " class=" + message.getClass().getName());
        }
    }
    
    private synchronized String allocName(final String type, final String namePattern) {
        Map<String, Integer> patterns = this.nameTypes.get(type);
        if (patterns == null) {
            patterns = new HashMap<String, Integer>();
            this.nameTypes.put(type, patterns);
        }
        Integer id = patterns.get(namePattern);
        if (id == null) {
            id = 0;
        }
        ++id;
        patterns.put(namePattern, id);
        final String agentName = namePattern.replaceFirst("#", id.toString());
        if (agentName.equals(namePattern)) {
            Log.warn("AllocName: missing '#' in name pattern '" + namePattern + "'");
        }
        else {
            Log.debug("AllocName: for type=" + type + " assigned '" + agentName + "' from pattern '" + namePattern + "'");
        }
        return agentName;
    }
    
    private static void saveProcessID(final String svrName, final String runDir) {
        Log.info("Server Name is " + svrName + " Run Dir is " + runDir);
        final RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        final String pid = rt.getName();
        if (Log.loggingDebug) {
            Log.info("PROCESS ID IS " + pid);
            Log.info("server name is " + svrName);
        }
        try {
            if (runDir != null) {
                final File outFile = new File(runDir + "\\" + svrName + ".bat");
                final PrintWriter out = new PrintWriter(new FileWriter(outFile));
                out.println("set pid=" + pid.substring(0, pid.indexOf("@")));
                out.close();
            }
        }
        catch (IOException e) {
            Log.exception("saveProcessID caught exception", e);
        }
    }
    
    private static void touchFile(final String fileName) {
        try {
            final FileWriter writer = new FileWriter(fileName);
            writer.close();
        }
        catch (IOException e) {
            Log.exception("touchFile " + fileName, e);
        }
    }
    
    public void addPluginStartGroup(final PluginStartGroup startGroup) {
        this.pluginStartGroup = startGroup;
    }
    
    private static String determineHostName() {
        String hostName = System.getProperty("atavism.hostname");
        if (hostName == null) {
            hostName = reverseLocalHostLookup();
        }
        if (hostName == null) {
            Log.warn("Could not determine host name from reverse lookup or atavism.hostname, using 'localhost'");
            hostName = "localhost";
        }
        return hostName;
    }
    
    private static String reverseLocalHostLookup() {
        InetAddress localMachine = null;
        try {
            localMachine = InetAddress.getLocalHost();
            return localMachine.getHostName();
        }
        catch (UnknownHostException e) {
            Log.warn("Could not get host name from local IP address " + localMachine);
            return null;
        }
    }
    
    private class AgentHandler implements Runnable
    {
        SocketChannel agentSocket;
        
        public AgentHandler(final SocketChannel socket) throws IOException {
            this.agentSocket = socket;
        }
        
        @Override
        public void run() {
            try {
                while (this.handleMessage()) {}
            }
            catch (InterruptedIOException ex2) {
                Log.info("DomainServer: closed connection due to timeout " + this.agentSocket);
            }
            catch (IOException ex3) {
                Log.info("DomainServer: agent closed connection " + this.agentSocket);
            }
            catch (Exception ex) {
                Log.exception("DomainServer.SocketHandler: ", ex);
            }
            try {
                if (this.agentSocket != null) {
                    this.agentSocket.close();
                }
            }
            catch (IOException ex4) {}
        }
        
        public boolean handleMessage() throws IOException {
            final ByteBuffer buf = ByteBuffer.allocate(4);
            int nBytes = ChannelUtil.fillBuffer(buf, this.agentSocket);
            if (nBytes == 0) {
                Log.info("DomainServer: agent closed connection " + this.agentSocket);
                return false;
            }
            if (nBytes < 4) {
                Log.error("DomainServer: invalid message " + nBytes);
                return false;
            }
            final int msgLen = buf.getInt();
            if (msgLen < 0) {
                return false;
            }
            final AOByteBuffer buffer = new AOByteBuffer(msgLen);
            nBytes = ChannelUtil.fillBuffer(buffer.getNioBuf(), this.agentSocket);
            if (nBytes == 0) {
                Log.info("DomainServer: agent closed connection " + this.agentSocket);
                return false;
            }
            if (nBytes < msgLen) {
                Log.error("DomainServer: invalid message, expecting " + msgLen + " got " + nBytes + " from " + this.agentSocket);
                return false;
            }
            final Message message = (Message)MarshallingRuntime.unmarshalObject(buffer);
            if (message instanceof AgentHelloMessage) {
                if (this.handleAgentHello((AgentHelloMessage)message)) {
                    this.agentSocket = null;
                }
                return false;
            }
            if (message instanceof AllocNameMessage) {
                DomainServer.this.handleAllocName((AllocNameMessage)message, this.agentSocket);
            }
            return true;
        }
        
        boolean handleAgentHello(final AgentHelloMessage agentHello) throws IOException {
            if (agentHello.getMsgType() != MessageTypes.MSG_TYPE_AGENT_HELLO) {
                Log.error("DomainServer: invalid agent hello, got message type " + agentHello.getMsgType() + " from " + this.agentSocket);
                return false;
            }
            final int agentId;
            synchronized (DomainServer.domainServer) {
                agentId = DomainServer.this.getNextAgentId();
                if (!DomainServer.this.agentNames.contains(agentHello.getAgentName())) {
                    DomainServer.this.agentNames.add(agentHello.getAgentName());
                }
            }
            final AOByteBuffer buffer = new AOByteBuffer(1024);
            final HelloResponseMessage helloResponse = new HelloResponseMessage(agentId, DomainServer.this.domainStartTime, DomainServer.this.agentNames, DomainServer.encodedDomainKey);
            Message.toBytes(helloResponse, buffer);
            buffer.flip();
            if (!ChannelUtil.writeBuffer(buffer, this.agentSocket)) {
                Log.error("could not write to new agent, " + this.agentSocket);
                return false;
            }
            DomainServer.this.addNewAgent(agentId, this.agentSocket, agentHello.getAgentName(), agentHello.getAgentIP(), agentHello.getAgentPort(), agentHello.getFlags());
            return true;
        }
    }
    
    static class PluginSpec
    {
        public String pluginType;
        public int expected;
        public int running;
        
        public PluginSpec(final String pluginType, final int expected) {
            this.pluginType = pluginType;
            this.expected = expected;
        }
    }
    
    static class PluginStartGroup
    {
        Map<String, PluginSpec> plugins;
        Map<String, String[]> dependencies;
        
        PluginStartGroup() {
            this.plugins = new HashMap<String, PluginSpec>();
            this.dependencies = new HashMap<String, String[]>();
        }
        
        public void add(final String pluginType, final int expected) {
            this.plugins.put(pluginType, new PluginSpec(pluginType, expected));
        }
        
        public void prepareDependencies(final Properties properties, final String worldName) {
            for (final PluginSpec plugin : this.plugins.values()) {
                String depString = properties.getProperty("atavism.plugin_dep." + worldName + "." + plugin.pluginType);
                if (depString == null) {
                    depString = properties.getProperty("atavism.plugin_dep." + plugin.pluginType);
                }
                if (depString == null) {
                    continue;
                }
                depString = depString.trim();
                String[] deps = null;
                if (!depString.equals("")) {
                    deps = depString.split(",");
                }
                this.dependencies.put(plugin.pluginType, deps);
                if (!Log.loggingDebug) {
                    continue;
                }
                Log.debug("plugin type " + plugin.pluginType + " depends on plugin types: " + ((deps == null) ? "*none*" : depString));
            }
        }
        
        public synchronized void pluginAvailable(final String pluginType, final String pluginName) {
            if (Log.loggingDebug) {
                Log.debug("Plugin available type=" + pluginType + " name=" + pluginName);
            }
            final PluginSpec pluginSpec = this.plugins.get(pluginType);
            if (pluginSpec == null) {
                Log.error("DomainServer: unexpected plugin type=" + pluginType + " name=" + pluginName);
                return;
            }
            final PluginSpec pluginSpec2 = pluginSpec;
            ++pluginSpec2.running;
            if (pluginSpec.running > pluginSpec.expected) {
                Log.warn("DomainServer: more plugins than expected, type=" + pluginType + " name=" + pluginName + " expected=" + pluginSpec.expected + " available=" + pluginSpec.running);
            }
            if (pluginSpec.running >= pluginSpec.expected) {
                this.notifyAll();
            }
        }
        
        public synchronized boolean hasDependencies(final String pluginType, final String pluginName) {
            final String[] deps = this.dependencies.get(pluginType);
            if (deps == null) {
                return false;
            }
            for (final String dependentType : deps) {
                final PluginSpec pluginSpec = this.plugins.get(dependentType);
                if (pluginSpec == null) {
                    Log.warn("No information for dependent type=" + dependentType);
                }
                else if (pluginSpec.running < pluginSpec.expected) {
                    if (Log.loggingDebug) {
                        Log.debug("Incomplete dependency for type=" + pluginType + " name=" + pluginName + " dependentType=" + dependentType);
                    }
                    return true;
                }
            }
            return false;
        }
        
        public synchronized void awaitDependency(final String pluginType) {
            while (this.hasDependencies(pluginType, pluginType)) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {}
            }
        }
        
        public synchronized void awaitAllAvailable() {
            while (!this.allAvailable()) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {}
            }
        }
        
        boolean allAvailable() {
            for (final Map.Entry<String, PluginSpec> plugin : this.plugins.entrySet()) {
                final PluginSpec pluginSpec = plugin.getValue();
                if (pluginSpec.running < pluginSpec.expected) {
                    System.err.println("STILL waiting for " + pluginSpec.pluginType + " expected " + pluginSpec.expected + " running " + pluginSpec.running);
                    return false;
                }
            }
            return true;
        }
    }
    
    class PluginDependencyWatcher implements Runnable
    {
        AwaitPluginDependentsMessage await;
        SocketChannel agentSocket;
        
        public PluginDependencyWatcher(final AwaitPluginDependentsMessage await, final SocketChannel agentSocket) {
            this.await = await;
            this.agentSocket = agentSocket;
        }
        
        @Override
        public void run() {
            synchronized (DomainServer.this.pluginStartGroup) {
                this.waitForDependencies();
            }
            if (Log.loggingDebug) {
                Log.debug("Dependency satisfied for type=" + this.await.getPluginType() + " name=" + this.await.getPluginName());
            }
            final AOByteBuffer buffer = new AOByteBuffer(1024);
            final ResponseMessage response = new ResponseMessage(this.await);
            Message.toBytes(response, buffer);
            buffer.flip();
            try {
                if (!ChannelUtil.writeBuffer(buffer, this.agentSocket)) {
                    Log.error("could not write await dependencies response");
                }
            }
            catch (IOException e) {
                Log.exception("could not write await dependencies response", e);
            }
        }
        
        void waitForDependencies() {
            while (DomainServer.this.pluginStartGroup.hasDependencies(this.await.getPluginType(), this.await.getPluginName())) {
                try {
                    DomainServer.this.pluginStartGroup.wait();
                }
                catch (InterruptedException e) {}
            }
        }
    }
    
    class DomainThreadFactory implements ThreadFactory
    {
        int threadCount;
        
        DomainThreadFactory() {
            this.threadCount = 1;
        }
        
        @Override
        public Thread newThread(final Runnable runnable) {
            return new Thread(runnable, "Domain-" + this.threadCount++);
        }
    }
    
    static class TimeoutRunnable implements Runnable
    {
        int timeout;
        
        public TimeoutRunnable(final int timeout) {
            this.timeout = timeout;
        }
        
        @Override
        public void run() {
            System.err.println("\nSTARTUP FAILED -- didnt complete after " + this.timeout + " seconds.\nPlease stop server.");
        }
    }
}
