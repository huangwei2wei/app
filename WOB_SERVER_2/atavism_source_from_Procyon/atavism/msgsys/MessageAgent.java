// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.net.InetAddress;
import atavism.server.marshalling.MarshallingRuntime;
import java.nio.ByteBuffer;
import atavism.server.engine.OID;
import atavism.server.util.SQThreadPool;
import java.util.ArrayList;
import atavism.server.util.AORuntimeException;
import java.net.UnknownHostException;
import atavism.server.util.Base64;
import atavism.server.util.SecureTokenManager;
import atavism.server.network.ChannelUtil;
import atavism.server.network.AOByteBuffer;
import java.net.ConnectException;
import atavism.server.util.Log;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import atavism.server.network.TcpServer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.Map;
import atavism.server.util.SquareQueue;
import java.util.List;
import atavism.server.network.TcpAcceptCallback;

public class MessageAgent implements MessageIO.Callback, TcpAcceptCallback, ResponseCallback
{
    private static final int DEFAULT_RPC_TIMEOUT = 120000;
    public static final int DOMAIN_FLAG_TRANSIENT = 1;
    public static final int NO_FLAGS = 0;
    public static final int BLOCKING = 1;
    public static final int COMPLETION_CALLBACK = 2;
    public static final int DEFERRED = 4;
    public static final int RESPONDER = 8;
    public static final int NON_BLOCKING = 16;
    private List<RemoteAgent> remoteAgents;
    private SquareQueue<RemoteAgent, Message> remoteAgentOutput;
    private RemoteAgent selfRemoteAgent;
    private long nextMessageId;
    private long nextSubId;
    private int defaultSubscriptionFlags;
    private Map<Long, RegisteredSubscription> subscriptions;
    private List<FilterTable> sendFilters;
    private List<FilterTable> receiveFilters;
    private List<FilterTable> responderSendFilters;
    private List<FilterTable> responderReceiveFilters;
    private FilterTable defaultSendFilterTable;
    private FilterTable defaultReceiveFilterTable;
    private FilterTable defaultResponderReceiveFilterTable;
    private FilterTable defaultResponderSendFilterTable;
    private long statAppMessageCount;
    private long statSystemMessageCount;
    private Map<Long, PendingRPC> pendingRPC;
    private ExecutorService responseCallbackPool;
    private String agentName;
    private int agentPort;
    private int agentId;
    private long domainStartTime;
    private int domainRetries;
    private SocketChannel domainServerSocket;
    private AgentInfo domainServerAgent;
    private int domainFlags;
    private MessageIO messageIO;
    private TcpServer listener;
    private ExecutorService threadPool;
    private List<String> remoteAgentNames;
    private Set<MessageType> advertisements;
    private String advertFileName;
    private Set<MessageType> noProducersExpected;
    private static int intervalBetweenStatsLogging;
    private static long localSubscriptionCreatedCount;
    private static long localSubscriptionRemovedCount;
    private static long localFilterUpdateCount;
    private static long remoteSubscriptionCreatedCount;
    private static long remoteSubscriptionRemovedCount;
    private static long remoteFilterUpdateCount;
    private static long lastLocalSubscriptionCreatedCount;
    private static long lastLocalSubscriptionRemovedCount;
    private static long lastLocalFilterUpdateCount;
    private static long lastRemoteSubscriptionCreatedCount;
    private static long lastRemoteSubscriptionRemovedCount;
    private static long lastRemoteFilterUpdateCount;
    
    public MessageAgent(final String name) {
        this.remoteAgents = new LinkedList<RemoteAgent>();
        this.remoteAgentOutput = new SquareQueue<RemoteAgent, Message>("MessageAgent");
        this.nextMessageId = 1L;
        this.nextSubId = 1L;
        this.defaultSubscriptionFlags = 0;
        this.defaultSendFilterTable = new DefaultFilterTable();
        this.defaultReceiveFilterTable = new DefaultFilterTable();
        this.defaultResponderReceiveFilterTable = new DefaultFilterTable();
        this.defaultResponderSendFilterTable = new DefaultFilterTable();
        this.statAppMessageCount = 0L;
        this.statSystemMessageCount = 0L;
        this.pendingRPC = new HashMap<Long, PendingRPC>();
        this.responseCallbackPool = Executors.newFixedThreadPool(10, new ResponseThreadFactory());
        this.domainRetries = Integer.MAX_VALUE;
        this.domainServerSocket = null;
        this.threadPool = Executors.newCachedThreadPool(new AgentConnectionThreadFactory());
        this.advertisements = new HashSet<MessageType>();
        this.advertFileName = "<unknown>";
        this.noProducersExpected = new HashSet<MessageType>();
        MessageTypes.initializeCatalog();
        this.agentName = name;
        RPCException.myAgentName = this.agentName;
        this.subscriptions = new HashMap<Long, RegisteredSubscription>();
        (this.sendFilters = new LinkedList<FilterTable>()).add(this.defaultSendFilterTable);
        (this.receiveFilters = new LinkedList<FilterTable>()).add(this.defaultReceiveFilterTable);
        (this.responderSendFilters = new LinkedList<FilterTable>()).add(this.defaultResponderSendFilterTable);
        (this.responderReceiveFilters = new LinkedList<FilterTable>()).add(this.defaultResponderReceiveFilterTable);
        this.addSelfRemoteAgent();
        (this.messageIO = new MessageIO(this)).start();
        new SelfMessageHandler();
    }
    
    public MessageAgent() {
        this.remoteAgents = new LinkedList<RemoteAgent>();
        this.remoteAgentOutput = new SquareQueue<RemoteAgent, Message>("MessageAgent");
        this.nextMessageId = 1L;
        this.nextSubId = 1L;
        this.defaultSubscriptionFlags = 0;
        this.defaultSendFilterTable = new DefaultFilterTable();
        this.defaultReceiveFilterTable = new DefaultFilterTable();
        this.defaultResponderReceiveFilterTable = new DefaultFilterTable();
        this.defaultResponderSendFilterTable = new DefaultFilterTable();
        this.statAppMessageCount = 0L;
        this.statSystemMessageCount = 0L;
        this.pendingRPC = new HashMap<Long, PendingRPC>();
        this.responseCallbackPool = Executors.newFixedThreadPool(10, new ResponseThreadFactory());
        this.domainRetries = Integer.MAX_VALUE;
        this.domainServerSocket = null;
        this.threadPool = Executors.newCachedThreadPool(new AgentConnectionThreadFactory());
        this.advertisements = new HashSet<MessageType>();
        this.advertFileName = "<unknown>";
        this.noProducersExpected = new HashSet<MessageType>();
        MessageTypes.initializeCatalog();
        (this.messageIO = new MessageIO(this)).start();
    }
    
    public String getName() {
        return this.agentName;
    }
    
    public Integer getAgentId() {
        return this.agentId;
    }
    
    public long getDomainStartTime() {
        return this.domainStartTime;
    }
    
    public int getListenerPort() {
        if (this.listener == null) {
            return -1;
        }
        return this.listener.getPort();
    }
    
    public void openListener() throws IOException {
        if (this.listener != null) {
            return;
        }
        (this.listener = new TcpServer()).bind();
        this.agentPort = this.listener.getPort();
        this.listener.registerAcceptCallback(this);
    }
    
    private void startListener() {
        this.listener.start();
    }
    
    public void setAdvertisements(final Collection<MessageType> typeIds) {
        synchronized (this.advertisements) {
            this.advertisements = new HashSet<MessageType>(typeIds);
            this.sendAdvertisements();
        }
        synchronized (this.remoteAgents) {
            if (!this.selfRemoteAgent.hasFlag(256)) {
                this.selfRemoteAgent.setFlag(256);
                this.remoteAgents.notify();
            }
        }
    }
    
    public void addAdvertisement(final MessageType msgType) {
        final List<MessageType> typeIds = new LinkedList<MessageType>();
        typeIds.add(msgType);
        this.addAdvertisements(typeIds);
    }
    
    public void addAdvertisements(final List<MessageType> typeIds) {
        synchronized (this.advertisements) {
            int count = 0;
            final Set<MessageType> newAdvertisements = new HashSet<MessageType>(this.advertisements);
            for (final MessageType typeId : typeIds) {
                if (!newAdvertisements.contains(typeId)) {
                    newAdvertisements.add(typeId);
                    ++count;
                }
            }
            if (count == 0) {
                return;
            }
            this.advertisements = newAdvertisements;
            this.sendAdvertisements();
        }
        synchronized (this.remoteAgents) {
            if (!this.selfRemoteAgent.hasFlag(256)) {
                this.selfRemoteAgent.setFlag(256);
                this.remoteAgents.notify();
            }
        }
    }
    
    public void removeAdvertisements(final List<MessageType> typeIds) {
        synchronized (this.advertisements) {
            int count = 0;
            final Set<MessageType> newAdvertisements = new HashSet<MessageType>(this.advertisements);
            for (final MessageType typeId : typeIds) {
                if (newAdvertisements.contains(typeId)) {
                    newAdvertisements.remove(typeId);
                    ++count;
                }
            }
            if (count == 0) {
                return;
            }
            this.advertisements = newAdvertisements;
            this.sendAdvertisements();
        }
    }
    
    public void setAdvertisementFileName(final String fileName) {
        this.advertFileName = fileName;
    }
    
    private void sendAdvertisements() {
        final AdvertiseMessage request = new AdvertiseMessage(this.advertisements);
        request.setMessageId(this.nextMessageId());
        request.setRPC();
        final PendingRPC rpc = new PendingRPC();
        rpc.messageId = request.getMsgId();
        rpc.responders = new HashSet<Object>();
        rpc.callback = this;
        synchronized (this.remoteAgents) {
            rpc.responders.addAll(this.remoteAgents);
        }
        synchronized (this.pendingRPC) {
            this.pendingRPC.put(rpc.messageId, rpc);
        }
        synchronized (rpc) {
            this.sendMessageToList(request, rpc.responders);
            while (rpc.responders.size() > 0) {
                try {
                    rpc.wait();
                }
                catch (InterruptedException ignore) {}
            }
        }
        synchronized (this.pendingRPC) {
            this.pendingRPC.remove(rpc.messageId);
        }
    }
    
    public void addNoProducersExpected(final MessageType messageType) {
        synchronized (this.noProducersExpected) {
            this.noProducersExpected.add(messageType);
        }
    }
    
    public Set<MessageType> getNoProducersExpected() {
        synchronized (this.noProducersExpected) {
            return new HashSet<MessageType>(this.noProducersExpected);
        }
    }
    
    public int getDomainFlags() {
        return this.domainFlags;
    }
    
    public void setDomainFlags(final int flags) {
        this.domainFlags = flags;
    }
    
    public int getDomainConnectRetries() {
        return this.domainRetries;
    }
    
    public void setDomainConnectRetries(final int retries) {
        this.domainRetries = retries;
    }
    
    public void connectToDomain(final String domainServerHost, final Integer domainServerPort) throws IOException, UnknownHostException, AORuntimeException {
        if (this.listener == null && this.agentName != null) {
            throw new RuntimeException("Call openListener first");
        }
        int retryCount = 0;
        while (true) {
            try {
                this.domainServerSocket = SocketChannel.open(new InetSocketAddress(domainServerHost, domainServerPort));
            }
            catch (ConnectException ex) {
                if (++retryCount > this.domainRetries) {
                    throw ex;
                }
                Log.debug("Could not connect to domain server " + domainServerHost + ":" + domainServerPort + " " + ex + ", Retrying ...");
                try {
                    Thread.sleep(1000L);
                }
                catch (Exception ex2) {}
                continue;
            }
            break;
        }
        this.domainServerSocket.configureBlocking(false);
        if (Log.loggingDebug) {
            Log.debug("MessageAgent: connected to domain server " + this.domainServerSocket);
        }
        if (this.agentName == null) {
            this.addDomainServerAgent(domainServerHost, domainServerPort);
            return;
        }
        final AOByteBuffer buffer = new AOByteBuffer(64);
        final AgentHelloMessage agentHello = new AgentHelloMessage(this.agentName, ":same", this.getListenerPort());
        agentHello.setFlags(this.domainFlags);
        Message.toBytes(agentHello, buffer);
        buffer.flip();
        if (!ChannelUtil.writeBuffer(buffer, this.domainServerSocket)) {
            throw new RuntimeException("could not connect to domain server");
        }
        final HelloResponseMessage helloResponse = (HelloResponseMessage)new DomainClient().readMessage();
        if (helloResponse.getMsgType() != MessageTypes.MSG_TYPE_HELLO_RESPONSE) {
            throw new RuntimeException("domain server invalid hello response");
        }
        this.agentId = helloResponse.getAgentId();
        this.domainStartTime = helloResponse.getDomainStartTime();
        this.selfRemoteAgent.agentId = this.agentId;
        Log.info("My agent-id: " + this.agentId);
        this.remoteAgentNames = helloResponse.getAgentNames();
        Log.debug("DOMAIN: helloResponse: " + helloResponse);
        Log.debug("DOMAIN: helloResponse key: " + helloResponse.getDomainKey());
        SecureTokenManager.getInstance().initDomain(Base64.decode(helloResponse.getDomainKey()));
        this.addDomainServerAgent(domainServerHost, domainServerPort);
        this.startListener();
    }
    
    void addDomainServerAgent(final String domainServerHost, final Integer domainServerPort) {
        this.domainServerAgent = new RemoteAgent();
        this.domainServerAgent.agentId = 0;
        this.domainServerAgent.socket = this.domainServerSocket;
        this.domainServerAgent.agentName = "DomainServer";
        this.domainServerAgent.agentIP = domainServerHost;
        this.domainServerAgent.agentPort = domainServerPort;
        this.domainServerAgent.outputBuf = new AOByteBuffer(512);
        this.domainServerAgent.inputBuf = new AOByteBuffer(512);
        this.messageIO.addAgent(this.domainServerAgent);
    }
    
    public DomainClient getDomainClient() {
        return new DomainClient();
    }
    
    private void addSelfRemoteAgent() {
        final RemoteAgent remoteAgent = new RemoteAgent();
        remoteAgent.agentId = this.agentId;
        remoteAgent.socket = null;
        remoteAgent.agentName = this.agentName;
        remoteAgent.agentIP = null;
        remoteAgent.agentPort = 0;
        remoteAgent.outputBuf = new AOByteBuffer(8192);
        remoteAgent.inputBuf = remoteAgent.outputBuf;
        remoteAgent.flags = 0;
        this.selfRemoteAgent = remoteAgent;
        this.remoteAgents.add(remoteAgent);
    }
    
    public void waitForRemoteAgents() {
        synchronized (this.remoteAgents) {
            while (!this.haveAllAdvertisements()) {
                try {
                    this.remoteAgents.wait();
                }
                catch (InterruptedException ignore) {}
            }
        }
    }
    
    private boolean haveAllAdvertisements() {
        final List<String> remotes = new LinkedList<String>(this.remoteAgentNames);
        for (final RemoteAgent remoteAgent : this.remoteAgents) {
            if ((remoteAgent != this.selfRemoteAgent && remoteAgent.socket == null) || !remoteAgent.hasFlag(256)) {
                return false;
            }
            if (remotes.contains(remoteAgent.agentName)) {
                remotes.remove(remoteAgent.agentName);
            }
            else {
                if (!remoteAgent.hasFlag(1)) {
                    Log.error("Unexpected agent '" + remoteAgent.agentName + "'");
                    return false;
                }
                remotes.remove(remoteAgent.agentName);
            }
        }
        return remotes.size() == 0;
    }
    
    public void setDefaultSubscriptionFlags(final int flags) {
        this.defaultSubscriptionFlags = flags;
    }
    
    public int getDefaultSubscriptionFlags() {
        return this.defaultSubscriptionFlags;
    }
    
    public long createSubscription(final IFilter filter, final MessageCallback callback) {
        return this.createSubscription(filter, callback, 0, null);
    }
    
    public long createSubscription(final IFilter filter, final MessageCallback callback, final int flags) {
        return this.createSubscription(filter, callback, flags, null);
    }
    
    public long createSubscription(final IFilter filter, final MessageCallback callback, int flags, final MessageTrigger trigger) {
        ++MessageAgent.localSubscriptionCreatedCount;
        flags |= this.defaultSubscriptionFlags;
        if ((flags & 0x10) != 0x0) {
            flags &= 0xFFFFFFFE;
        }
        final RegisteredSubscription sub = new RegisteredSubscription();
        sub.filter = filter;
        sub.trigger = trigger;
        sub.flags = (short)(flags & 0xC);
        sub.producers = new LinkedList<RemoteAgent>();
        sub.callback = callback;
        PendingRPC rpc = null;
        synchronized (this.subscriptions) {
            sub.subId = this.nextSubId++;
            this.subscriptions.put(sub.subId, sub);
            synchronized (this.remoteAgents) {
                for (final RemoteAgent remoteAgent : this.remoteAgents) {
                    if (filter.matchMessageType(remoteAgent.getAdvertisements())) {
                        sub.producers.add(remoteAgent);
                    }
                }
            }
            if (Log.loggingDebug) {
                Log.debug("subscribe " + filter + " matching agents " + sub.producers.size());
            }
            if (sub.producers.size() == 0) {
                String m = "";
                for (final MessageType type : filter.getMessageTypes()) {
                    if (!this.noProducersExpected.contains(type)) {
                        m = m + type + ",";
                    }
                }
                if (m.length() > 0) {
                    Log.error("No producers for types " + m);
                }
            }
            if ((flags & 0x8) == 0x0) {
                FilterTable filterTable = filter.getReceiveFilterTable();
                if (filterTable == null) {
                    filterTable = this.defaultReceiveFilterTable;
                }
                else {
                    this.addUniqueFilterTable(filterTable, this.receiveFilters);
                }
                filterTable.addFilter(sub, callback);
            }
            else {
                FilterTable filterTable = filter.getResponderReceiveFilterTable();
                if (filterTable == null) {
                    filterTable = this.defaultResponderReceiveFilterTable;
                }
                else {
                    this.addUniqueFilterTable(filterTable, this.responderReceiveFilters);
                }
                filterTable.addFilter(sub, callback);
            }
            if (sub.producers.size() > 0) {
                final SubscribeMessage message = new SubscribeMessage(sub.subId, filter, trigger, sub.flags);
                message.setMessageId(this.nextMessageId());
                if ((flags & 0x1) != 0x0) {
                    rpc = this.setupInternalRPC(message, sub.producers);
                }
                this.sendMessageToList(message, sub.producers);
            }
        }
        if (rpc != null && (flags & 0x1) != 0x0 && (flags & 0x2) == 0x0) {
            this.waitInternalRPC(rpc);
        }
        return sub.subId;
    }
    
    public boolean removeSubscription(final long subId) {
        synchronized (this.subscriptions) {
            return this._removeSubscription(subId);
        }
    }
    
    public boolean removeSubscriptions(final Collection<Long> subIds) {
        synchronized (this.subscriptions) {
            int count = 0;
            for (final Long subId : subIds) {
                if (this._removeSubscription(subId)) {
                    ++count;
                }
            }
            return count == subIds.size();
        }
    }
    
    private boolean _removeSubscription(final Long subId) {
        final RegisteredSubscription sub = this.subscriptions.remove(subId);
        if (sub == null) {
            return false;
        }
        ++MessageAgent.localSubscriptionRemovedCount;
        if ((sub.flags & 0x8) == 0x0) {
            FilterTable filterTable = sub.filter.getReceiveFilterTable();
            if (filterTable == null) {
                filterTable = this.defaultReceiveFilterTable;
            }
            filterTable.removeFilter(sub, sub.callback);
        }
        else {
            FilterTable filterTable = sub.filter.getResponderReceiveFilterTable();
            if (filterTable == null) {
                filterTable = this.defaultResponderReceiveFilterTable;
            }
            filterTable.removeFilter(sub, sub.callback);
        }
        final UnsubscribeMessage message = new UnsubscribeMessage(subId);
        message.setMessageId(this.nextMessageId());
        this.sendMessageToList(message, sub.producers);
        return true;
    }
    
    public static boolean responseExpected(final int flags) {
        return (flags & 0x1) != 0x0;
    }
    
    public boolean applyFilterUpdate(final long subId, final FilterUpdate update) {
        return this.applyFilterUpdate(subId, update, 0, (RemoteAgent)null);
    }
    
    public boolean applyFilterUpdate(final long subId, final FilterUpdate update, final int flags) {
        return this.applyFilterUpdate(subId, update, flags, (RemoteAgent)null);
    }
    
    public boolean applyFilterUpdate(final long subId, final FilterUpdate update, final int flags, final Message excludeSender) {
        return this.applyFilterUpdate(subId, update, flags, excludeSender.remoteAgent);
    }
    
    protected boolean applyFilterUpdate(final long subId, final FilterUpdate update, final int flags, final RemoteAgent excludeAgent) {
        PendingRPC rpc = null;
        synchronized (this.subscriptions) {
            ++MessageAgent.localFilterUpdateCount;
            final RegisteredSubscription sub = this.subscriptions.get(subId);
            if (sub == null) {
                return false;
            }
            final FilterUpdateMessage message = new FilterUpdateMessage(subId, update);
            message.setMessageId(this.nextMessageId());
            List<RemoteAgent> producers = sub.producers;
            if (excludeAgent != null) {
                producers = new ArrayList<RemoteAgent>(producers);
                producers.remove(excludeAgent);
            }
            if (producers.size() > 0) {
                if ((flags & 0x1) != 0x0) {
                    rpc = this.setupInternalRPC(message, producers);
                }
                this.sendMessageToList(message, producers);
            }
        }
        if (rpc != null) {
            this.waitInternalRPC(rpc);
        }
        return true;
    }
    
    public int sendBroadcast(final Message message) {
        message.setMessageId(this.nextMessageId());
        message.unsetRPC();
        return this._sendBroadcast(message);
    }
    
    private int _sendBroadcast(final Message message) {
        if (Log.loggingDebug) {
            Log.debug("sendBroadcast type=" + message.getMsgType().getMsgTypeString() + " id=" + message.getMsgId() + " class=" + message.getClass().getName());
        }
        if (!this.advertisements.contains(message.getMsgType())) {
            Log.error("NEED ADVERT - Add " + message.getMsgType() + " to " + this.advertFileName + " and restart server");
        }
        final Set<Object> matchingAgents = new HashSet<Object>(this.remoteAgents.size());
        final List<Subscription> triggers = new LinkedList<Subscription>();
        for (final FilterTable filterTable : this.sendFilters) {
            filterTable.match(message, matchingAgents, triggers);
        }
        for (final Subscription triggerSub : triggers) {
            triggerSub.getTrigger().trigger(message, triggerSub.filter, this);
        }
        this.sendMessageToList(message, matchingAgents);
        return matchingAgents.size();
    }
    
    public boolean sendDirect(final Message message, final AgentHandle destination, final SubscriptionHandle runTriggers) {
        if (!(destination instanceof RemoteAgent)) {
            return false;
        }
        if (!this.remoteAgents.contains(destination)) {
            return false;
        }
        message.setMessageId(this.nextMessageId());
        message.unsetRPC();
        if (runTriggers != null && runTriggers instanceof RemoteSubscription) {
            final RemoteSubscription remoteSub = (RemoteSubscription)runTriggers;
            if (remoteSub.getTrigger() != null) {
                remoteSub.getTrigger().trigger(message, remoteSub.filter, this);
            }
        }
        final Collection agents = new ArrayList(1);
        agents.add(destination);
        this.sendMessageToList(message, agents);
        return true;
    }
    
    public Message sendRPC(final Message message) {
        return this.sendRPC(message, 120000L);
    }
    
    public Message sendRPC(final Message message, final long timeout) {
        final PendingRPC rpc = this._sendRPC(message, this);
        final SQThreadPool pool = SQThreadPool.getRunningPool();
        if (pool != null) {
            pool.runningThreadWillBlock();
        }
        final long startTime = System.currentTimeMillis();
        synchronized (rpc) {
            while (rpc.response == null) {
                try {
                    final long elapsed = System.currentTimeMillis() - startTime;
                    final long waitTime = (timeout == 0L) ? 0L : (timeout - elapsed);
                    rpc.wait(waitTime);
                    if (timeout != 0L && System.currentTimeMillis() - startTime >= timeout) {
                        Log.error("Exceeded maximum wait time");
                        throw new RPCTimeoutException();
                    }
                    continue;
                }
                catch (InterruptedException ex) {}
            }
        }
        synchronized (this.pendingRPC) {
            this.pendingRPC.remove(rpc.messageId);
        }
        if (pool != null) {
            pool.doneBlocking();
        }
        if (!(rpc.response instanceof ExceptionResponseMessage)) {
            return rpc.response;
        }
        final ExceptionResponseMessage exceptionResponse = (ExceptionResponseMessage)rpc.response;
        if (exceptionResponse.getException().getExceptionClassName().equals("atavism.msgsys.NoRecipientsException")) {
            throw new NoRecipientsException(exceptionResponse.getException().getMessage());
        }
        throw new RPCException(exceptionResponse.getException());
    }
    
    public Boolean sendRPCReturnBoolean(final Message message) {
        final BooleanResponseMessage response = (BooleanResponseMessage)this.sendRPC(message);
        return response.getBooleanVal();
    }
    
    public Integer sendRPCReturnInt(final Message message) {
        final IntegerResponseMessage response = (IntegerResponseMessage)this.sendRPC(message);
        return response.getIntVal();
    }
    
    public Long sendRPCReturnLong(final Message message) {
        final LongResponseMessage response = (LongResponseMessage)this.sendRPC(message);
        return response.getLongVal();
    }
    
    public OID sendRPCReturnOID(final Message message) {
        final OIDResponseMessage response = (OIDResponseMessage)this.sendRPC(message);
        return response.getOIDVal();
    }
    
    public String sendRPCReturnString(final Message message) {
        final StringResponseMessage response = (StringResponseMessage)this.sendRPC(message);
        return response.getStringVal();
    }
    
    public Object sendRPCReturnObject(final Message message) {
        final GenericResponseMessage response = (GenericResponseMessage)this.sendRPC(message);
        return response.getData();
    }
    
    public void sendRPC(final Message message, final ResponseCallback callback) {
        this._sendRPC(message, callback);
    }
    
    private PendingRPC _sendRPC(final Message message, final ResponseCallback callback) {
        message.setMessageId(this.nextMessageId());
        message.setRPC();
        if (Log.loggingDebug) {
            Log.debug("sendRPC type=" + message.getMsgType().getMsgTypeString() + " id=" + message.getMsgId() + " class=" + message.getClass().getName());
        }
        if (!this.advertisements.contains(message.getMsgType())) {
            Log.error("NEED ADVERT - Add " + message.getMsgType() + " to " + this.advertFileName + " and restart server");
        }
        final PendingRPC rpc = new PendingRPC();
        rpc.messageId = message.getMsgId();
        rpc.responders = null;
        rpc.callback = callback;
        synchronized (this.pendingRPC) {
            this.pendingRPC.put(rpc.messageId, rpc);
        }
        final HashSet<Object> matchingAgents = new HashSet<Object>();
        final List<Subscription> triggers = new LinkedList<Subscription>();
        for (final FilterTable filterTable : this.responderSendFilters) {
            filterTable.match(message, matchingAgents, triggers);
        }
        if (matchingAgents.size() == 0) {
            synchronized (this.pendingRPC) {
                this.pendingRPC.remove(rpc.messageId);
            }
            throw new NoRecipientsException("sendRPC: no message recipients for type=" + message.getMsgType() + " " + message + ", class " + message.getClass().getName());
        }
        if (matchingAgents.size() != 1) {
            synchronized (this.pendingRPC) {
                this.pendingRPC.remove(rpc.messageId);
            }
            throw new MultipleRecipientsException("sendRPC: multiple message recipients for type=" + message.getMsgType() + " " + message + ", class " + message.getClass().getName());
        }
        rpc.responders = matchingAgents;
        for (final Subscription triggerSub : triggers) {
            triggerSub.getTrigger().trigger(message, triggerSub.filter, this);
        }
        synchronized (rpc) {
            this.sendMessageToList(message, matchingAgents);
        }
        return rpc;
    }
    
    private void sendMessageToList(final Message message, final Collection agents) {
        int count = 0;
        for (final Object agent : agents) {
            final AgentInfo remoteAgent = (AgentInfo)agent;
            if (Log.loggingDebug) {
                Log.debug("Sending " + message.getMsgType().getMsgTypeString() + " id=" + message.getMsgId() + " to " + remoteAgent.agentName);
            }
            synchronized (remoteAgent.outputBuf) {
                if (remoteAgent.socket != null) {
                    Message.toBytes(message, remoteAgent.outputBuf);
                    ++count;
                }
                else {
                    if (remoteAgent != this.selfRemoteAgent) {
                        continue;
                    }
                    Message.toBytes(message, remoteAgent.outputBuf);
                    remoteAgent.outputBuf.notify();
                }
            }
        }
        if (count > 0) {
            this.messageIO.outputReady();
        }
    }
    
    PendingRPC setupInternalRPC(final Message message, final List producers) {
        message.setRPC();
        final PendingRPC rpc = new PendingRPC();
        rpc.messageId = message.getMsgId();
        (rpc.responders = new HashSet<Object>()).addAll(producers);
        rpc.callback = this;
        synchronized (this.pendingRPC) {
            this.pendingRPC.put(rpc.messageId, rpc);
        }
        return rpc;
    }
    
    void waitInternalRPC(final PendingRPC rpc) {
        final SQThreadPool pool = SQThreadPool.getRunningPool();
        if (pool != null) {
            pool.runningThreadWillBlock();
        }
        synchronized (rpc) {
            while (rpc.responders.size() > 0) {
                try {
                    rpc.wait();
                }
                catch (InterruptedException ignore) {}
            }
        }
        synchronized (this.pendingRPC) {
            this.pendingRPC.remove(rpc.messageId);
        }
        if (pool != null) {
            pool.doneBlocking();
        }
    }
    
    public int sendBroadcastRPC(final Message message, final ResponseCallback callback) {
        message.setMessageId(this.nextMessageId());
        message.setRPC();
        if (Log.loggingDebug) {
            Log.debug("sendBroadcastRPC type=" + message.getMsgType().getMsgTypeString() + " id=" + message.getMsgId() + " class=" + message.getClass().getName());
        }
        if (!this.advertisements.contains(message.getMsgType())) {
            Log.error("NEED ADVERT - Add " + message.getMsgType() + " to " + this.advertFileName + " and restart server");
        }
        final PendingRPC rpc = new PendingRPC();
        rpc.messageId = message.getMsgId();
        rpc.responders = new HashSet<Object>();
        rpc.callback = callback;
        synchronized (this.pendingRPC) {
            this.pendingRPC.put(rpc.messageId, rpc);
        }
        final List<Subscription> triggers = new LinkedList<Subscription>();
        for (final FilterTable filterTable : this.responderSendFilters) {
            filterTable.match(message, rpc.responders, triggers);
        }
        for (final Subscription triggerSub : triggers) {
            triggerSub.getTrigger().trigger(message, triggerSub.filter, this);
        }
        final int responderCount = rpc.responders.size();
        synchronized (rpc) {
            this.sendMessageToList(message, rpc.responders);
        }
        if (responderCount == 0) {
            synchronized (this.pendingRPC) {
                this.pendingRPC.remove(rpc.messageId);
            }
        }
        return responderCount;
    }
    
    public int sendBroadcastRPCBlocking(final Message message, final ResponseCallback callback) {
        return this.sendBroadcastRPCBlocking(message, callback, 120000L);
    }
    
    public int sendBroadcastRPCBlocking(final Message message, final ResponseCallback callback, final long timeout) {
        final BlockingRPCState countingCallback = new BlockingRPCState(callback);
        final int responderCount = this.sendBroadcastRPC(message, countingCallback);
        final long startTime = System.currentTimeMillis();
        synchronized (countingCallback) {
            while (countingCallback.getResponseCount() < responderCount) {
                try {
                    final long elapsed = System.currentTimeMillis() - startTime;
                    final long waitTime = (timeout == 0L) ? 0L : (timeout - elapsed);
                    countingCallback.wait(waitTime);
                    if (timeout != 0L && System.currentTimeMillis() - startTime >= timeout) {
                        Log.error("Exceeded maximum wait time");
                        throw new RPCTimeoutException();
                    }
                    continue;
                }
                catch (InterruptedException ex) {}
            }
        }
        return responderCount;
    }
    
    public void sendResponse(final ResponseMessage message) {
        message.setMessageId(this.nextMessageId());
        if (Log.loggingDebug) {
            Log.debug("sendResponse to " + message.getRequestingAgent().agentName + "," + message.getRequestId() + " type=" + message.getMsgType().getMsgTypeString() + " id=" + message.getMsgId() + " class=" + message.getClass().getName());
        }
        synchronized (message.getRequestingAgent().outputBuf) {
            Message.toBytes(message, message.getRequestingAgent().outputBuf);
            if (message.getRequestingAgent() == this.selfRemoteAgent) {
                this.selfRemoteAgent.outputBuf.notify();
            }
        }
        if (message.getRequestingAgent() != this.selfRemoteAgent) {
            this.messageIO.outputReady();
        }
    }
    
    public void sendBooleanResponse(final Message message, final Boolean booleanVal) {
        if (!message.isRPC()) {
            Log.error("sendBooleanResponse, msg is not rpc, msg=" + message);
            return;
        }
        final BooleanResponseMessage response = new BooleanResponseMessage(message, booleanVal);
        this.sendResponse(response);
    }
    
    public void sendIntegerResponse(final Message message, final Integer intVal) {
        if (!message.isRPC()) {
            Log.error("sendIntegerResponse, msg is not rpc, msg=" + message);
            return;
        }
        final IntegerResponseMessage response = new IntegerResponseMessage(message, intVal);
        this.sendResponse(response);
    }
    
    public void sendLongResponse(final Message message, final Long longVal) {
        if (!message.isRPC()) {
            Log.error("sendLongResponse, msg is not rpc, msg=" + message);
            return;
        }
        final LongResponseMessage response = new LongResponseMessage(message, longVal);
        this.sendResponse(response);
    }
    
    public void sendOIDResponse(final Message message, final OID oidVal) {
        if (!message.isRPC()) {
            Log.error("sendLongResponse, msg is not rpc, msg=" + message);
            return;
        }
        final OIDResponseMessage response = new OIDResponseMessage(message, oidVal);
        this.sendResponse(response);
    }
    
    public void sendStringResponse(final Message message, final String stringVal) {
        if (!message.isRPC()) {
            Log.error("sendStringResponse, msg is not rpc, msg=" + message);
            return;
        }
        final StringResponseMessage response = new StringResponseMessage(message, stringVal);
        this.sendResponse(response);
    }
    
    public void sendObjectResponse(final Message message, final Object object) {
        if (!message.isRPC()) {
            Log.error("sendObjectResponse, msg is not rpc, msg=" + message);
            return;
        }
        final GenericResponseMessage response = new GenericResponseMessage(message, object);
        this.sendResponse(response);
    }
    
    public ExecutorService getResponseThreadPool() {
        return this.responseCallbackPool;
    }
    
    public void setResponseThreadPool(final ExecutorService threadPool) {
        this.responseCallbackPool = threadPool;
    }
    
    @Override
    public void handleResponse(final ResponseMessage message) {
    }
    
    public long getAppMessageCount() {
        return this.statAppMessageCount;
    }
    
    public long getSystemMessageCount() {
        return this.statSystemMessageCount;
    }
    
    public void startStatsThread() {
        final Thread messageAgentStatsLogger = new Thread(new MessageAgentStatsLogger(), "Stats:MessageAgent");
        messageAgentStatsLogger.setDaemon(true);
        messageAgentStatsLogger.start();
    }
    
    @Override
    public void onTcpAccept(final SocketChannel agentSocket) {
        try {
            agentSocket.socket().setTcpNoDelay(true);
            this.threadPool.execute(new NewConnectionHandler(agentSocket));
        }
        catch (IOException ex) {
            Log.exception("Agent listener", ex);
        }
    }
    
    private RemoteAgent waitForAgent(final SocketChannel agentSocket) throws IOException, AORuntimeException {
        final ByteBuffer buf = ByteBuffer.allocate(4);
        int nBytes = ChannelUtil.fillBuffer(buf, agentSocket);
        if (nBytes < 4) {
            Log.error("Agent: invalid agent hello bytes=" + nBytes);
            return null;
        }
        final int msgLen = buf.getInt();
        if (msgLen < 0) {
            return null;
        }
        final AOByteBuffer buffer = new AOByteBuffer(msgLen);
        nBytes = ChannelUtil.fillBuffer(buffer.getNioBuf(), agentSocket);
        if (nBytes < msgLen) {
            Log.error("Agent: invalid agent state, expecting " + msgLen + " got " + nBytes);
            return null;
        }
        final AgentStateMessage agentState = (AgentStateMessage)MarshallingRuntime.unmarshalObject(buffer);
        final RemoteAgent remoteAgent;
        synchronized (this.remoteAgents) {
            if (agentState.agentName == null || agentState.agentName.equals("")) {
                Log.error("Missing remote agent name");
                return null;
            }
            if (this.getAgent(agentState.agentName) != null) {
                Log.error("Already connected to '" + agentState.agentName + "'");
                return null;
            }
            if (this.getAgent(agentState.agentId) != null) {
                Log.error("Already connected to '" + agentState.agentName + "' agentId " + agentState.agentId);
                return null;
            }
            if (agentState.agentIP == null || agentState.agentIP.equals("")) {
                Log.error("Missing remote agent IP address");
                return null;
            }
            if (agentState.agentIP.equals(":same")) {
                final InetAddress agentAddress = agentSocket.socket().getInetAddress();
                agentState.agentIP = agentAddress.getHostAddress();
            }
            remoteAgent = new RemoteAgent();
            remoteAgent.agentId = agentState.agentId;
            remoteAgent.socket = agentSocket;
            remoteAgent.agentName = agentState.agentName;
            remoteAgent.agentIP = agentState.agentIP;
            remoteAgent.agentPort = agentState.agentPort;
            remoteAgent.outputBuf = new AOByteBuffer(8192);
            remoteAgent.inputBuf = new AOByteBuffer(8192);
            remoteAgent.flags = agentState.domainFlags;
            this.remoteAgents.add(remoteAgent);
        }
        return remoteAgent;
    }
    
    @Override
    public void handleMessageData(final int length, final AOByteBuffer messageData, final AgentInfo agentInfo) {
        if (length == -1 || messageData == null) {
            if (agentInfo.socket == this.domainServerSocket) {
                Log.error("Lost connection to domain server, exiting");
                System.exit(1);
                return;
            }
            if ((agentInfo.flags & 0x1) != 0x0) {
                Log.debug("Lost connection to agent '" + agentInfo.agentName + "' (transient) " + agentInfo.socket);
                this.messageIO.removeAgent(agentInfo);
                try {
                    agentInfo.socket.close();
                }
                catch (IOException ex) {}
                agentInfo.socket = null;
                synchronized (this.remoteAgents) {
                    this.remoteAgents.remove(agentInfo);
                }
                this.remoteAgentOutput.removeKey((RemoteAgent)agentInfo);
            }
            else {
                Log.error("Lost connection to agent '" + agentInfo.agentName + "' " + agentInfo.socket);
                synchronized (agentInfo.outputBuf) {
                    agentInfo.socket = null;
                    agentInfo.outputBuf = new AOByteBuffer(8192);
                    agentInfo.inputBuf = new AOByteBuffer(8192);
                }
            }
        }
        else {
            final Message message = (Message)MarshallingRuntime.unmarshalObject(messageData);
            final MessageType msgType = message.getMsgType();
            if (Log.loggingDebug) {
                String responseTo = "";
                if (message instanceof ResponseMessage) {
                    responseTo = " responseTo=" + ((ResponseMessage)message).getRequestId();
                }
                Log.debug("handleMessageData from " + agentInfo.agentName + "," + message.getMsgId() + responseTo + " type=" + msgType.getMsgTypeString() + " len=" + length + " class=" + message.getClass().getName());
            }
            if (!MessageTypes.isInternal(msgType)) {
                ++this.statAppMessageCount;
                message.remoteAgent = (RemoteAgent)agentInfo;
                if (message instanceof ResponseMessage) {
                    this.handleResponse((ResponseMessage)message, (RemoteAgent)agentInfo);
                }
                else {
                    this.deliverMessage(message);
                }
                return;
            }
            ++this.statSystemMessageCount;
            if (msgType == MessageTypes.MSG_TYPE_SUBSCRIBE) {
                ++MessageAgent.remoteSubscriptionCreatedCount;
                message.remoteAgent = (RemoteAgent)agentInfo;
                this.handleSubscribe((SubscribeMessage)message, (RemoteAgent)agentInfo);
            }
            else if (msgType == MessageTypes.MSG_TYPE_UNSUBSCRIBE) {
                ++MessageAgent.remoteSubscriptionRemovedCount;
                message.remoteAgent = (RemoteAgent)agentInfo;
                this.handleUnsubscribe((UnsubscribeMessage)message, (RemoteAgent)agentInfo);
            }
            else if (msgType == MessageTypes.MSG_TYPE_FILTER_UPDATE) {
                ++MessageAgent.remoteFilterUpdateCount;
                message.remoteAgent = (RemoteAgent)agentInfo;
                this.handleFilterUpdate((FilterUpdateMessage)message, (RemoteAgent)agentInfo);
            }
            else if (msgType == MessageTypes.MSG_TYPE_ADVERTISE) {
                message.remoteAgent = (RemoteAgent)agentInfo;
                this.handleAdvertise((AdvertiseMessage)message, (RemoteAgent)agentInfo);
            }
            else if (msgType == MessageTypes.MSG_TYPE_NEW_AGENT) {
                this.handleNewAgentMessage((NewAgentMessage)message);
            }
            else {
                Log.error("handleMessageData: unknown message type " + msgType);
                System.out.println("Unknown message type " + msgType);
            }
        }
    }
    
    private void handleSelfMessage(final Message message) {
        if (Log.loggingDebug) {
            String responseTo = "";
            if (message instanceof ResponseMessage) {
                responseTo = " responseTo=" + ((ResponseMessage)message).getRequestId();
            }
            Log.debug("handleSelfMessage id=" + message.getMsgId() + responseTo + " type=" + message.getMsgType().getMsgTypeString() + " class=" + message.getClass().getName());
        }
        final int msgTypeNumber = message.getMsgType().getMsgTypeNumber();
        if (message instanceof ResponseMessage) {
            this.handleResponse((ResponseMessage)message, this.selfRemoteAgent);
        }
        else if (msgTypeNumber < MessageTypes.catalog.getFirstMsgNumber() || msgTypeNumber > MessageTypes.catalog.getLastMsgNumber()) {
            this.deliverMessage(message);
        }
        else {
            ++this.statSystemMessageCount;
            final MessageType msgType = message.getMsgType();
            if (msgType == MessageTypes.MSG_TYPE_ADVERTISE) {
                this.handleAdvertise((AdvertiseMessage)message, this.selfRemoteAgent);
            }
            else if (msgType == MessageTypes.MSG_TYPE_SUBSCRIBE) {
                this.handleSubscribe((SubscribeMessage)message, this.selfRemoteAgent);
            }
            else if (msgType == MessageTypes.MSG_TYPE_UNSUBSCRIBE) {
                this.handleUnsubscribe((UnsubscribeMessage)message, this.selfRemoteAgent);
            }
            else if (msgType == MessageTypes.MSG_TYPE_FILTER_UPDATE) {
                this.handleFilterUpdate((FilterUpdateMessage)message, this.selfRemoteAgent);
            }
            else {
                Log.error("Unknown message type " + message.getMsgType());
            }
        }
    }
    
    private void handleNewAgentMessage(final NewAgentMessage message) {
        if (this.agentName.compareTo(message.agentName) > 0) {
            this.threadPool.execute(new AgentConnector(message));
        }
        else if (this.agentName.equals(message.agentName)) {
            Log.error("Duplicate agent name '" + this.agentName + "', exiting");
            System.exit(1);
        }
    }
    
    private void handleAdvertise(final AdvertiseMessage message, final RemoteAgent remoteAgent) {
        synchronized (this.advertisements) {
            final Collection<MessageType> newAdverts = message.getAdvertisements();
            final Collection<MessageType> oldAdverts = remoteAgent.getAdvertisements();
            final List<MessageType> addAdverts = new LinkedList<MessageType>();
            final List<MessageType> removeAdverts = new LinkedList<MessageType>();
            for (final MessageType ii : newAdverts) {
                if (!oldAdverts.contains(ii)) {
                    addAdverts.add(ii);
                }
            }
            for (final MessageType ii : oldAdverts) {
                if (!newAdverts.contains(ii)) {
                    removeAdverts.add(ii);
                }
            }
            if (Log.loggingDebug) {
                Log.debug("[" + remoteAgent.agentName + "," + message.getMsgId() + "] handleAdvertise: Adding " + addAdverts.size() + " and removing " + removeAdverts.size());
            }
            if (message.isRPC()) {
                final ResponseMessage response = new ResponseMessage(message);
                this.sendResponse(response);
            }
            synchronized (this.subscriptions) {
                final Collection<RegisteredSubscription> mySubs = this.subscriptions.values();
                boolean self = false;
                boolean remote = false;
                for (final RegisteredSubscription sub : mySubs) {
                    if (sub.filter.matchMessageType(addAdverts) && !sub.producers.contains(remoteAgent)) {
                        sub.producers.add(remoteAgent);
                        final SubscribeMessage subscribeMsg = new SubscribeMessage(sub.subId, sub.filter, sub.trigger, sub.flags);
                        subscribeMsg.setMessageId(this.nextMessageId());
                        if (Log.loggingDebug) {
                            Log.debug("Sending " + subscribeMsg.getMsgType().getMsgTypeString() + " id=" + subscribeMsg.getMsgId() + " to " + remoteAgent.agentName);
                        }
                        synchronized (remoteAgent.outputBuf) {
                            Message.toBytes(subscribeMsg, remoteAgent.outputBuf);
                        }
                        if (remoteAgent == this.selfRemoteAgent) {
                            self = true;
                        }
                        else {
                            remote = true;
                        }
                    }
                }
                if (self) {
                    this.selfRemoteAgent.outputBuf.notify();
                }
                if (remote) {
                    this.messageIO.outputReady();
                }
            }
            remoteAgent.setAdvertisements(newAdverts);
        }
        synchronized (this.remoteAgents) {
            if (!remoteAgent.hasFlag(256)) {
                remoteAgent.setFlag(256);
                this.remoteAgents.notify();
            }
        }
    }
    
    private void handleSubscribe(final SubscribeMessage message, final RemoteAgent remoteAgent) {
        if (Log.loggingDebug) {
            Log.debug("[" + remoteAgent.agentName + "," + message.getMsgId() + "] Got subscription subId=" + message.getSubId() + " filter " + message.getFilter());
        }
        final RemoteSubscription remoteSub = new RemoteSubscription();
        remoteSub.remoteAgent = remoteAgent;
        remoteSub.subId = message.getSubId();
        remoteSub.filter = message.getFilter();
        remoteSub.trigger = message.getTrigger();
        remoteSub.flags = message.getFlags();
        if (remoteSub.trigger != null) {
            remoteSub.trigger.setFilter(remoteSub.filter);
        }
        remoteAgent.addRemoteSubscription(remoteSub);
        FilterTable filterTable;
        if ((remoteSub.flags & 0x8) == 0x0) {
            filterTable = remoteSub.filter.getSendFilterTable();
            if (filterTable == null) {
                filterTable = this.defaultSendFilterTable;
            }
            else {
                this.addUniqueFilterTable(filterTable, this.sendFilters);
            }
        }
        else {
            filterTable = remoteSub.filter.getResponderSendFilterTable();
            if (filterTable == null) {
                filterTable = this.defaultResponderSendFilterTable;
            }
            else {
                this.addUniqueFilterTable(filterTable, this.responderSendFilters);
            }
        }
        filterTable.addFilter(remoteSub, remoteAgent);
        if (message.isRPC()) {
            final ResponseMessage response = new ResponseMessage(message);
            this.sendResponse(response);
        }
    }
    
    private void handleUnsubscribe(final UnsubscribeMessage message, final RemoteAgent remoteAgent) {
        final List<Long> subIds = message.getSubIds();
        if (Log.loggingDebug) {
            Log.debug("[" + remoteAgent.agentName + "," + message.getMsgId() + "] Got unsubscribe count=" + subIds.size());
        }
        for (final Long subId : subIds) {
            final RemoteSubscription remoteSub = remoteAgent.removeRemoteSubscription(subId);
            if (remoteSub == null) {
                Log.error("MessageAgent: duplicate remove sub");
            }
            else if ((remoteSub.flags & 0x8) == 0x0) {
                FilterTable filterTable = remoteSub.filter.getSendFilterTable();
                if (filterTable == null) {
                    filterTable = this.defaultSendFilterTable;
                }
                filterTable.removeFilter(remoteSub, remoteAgent);
            }
            else {
                FilterTable filterTable = remoteSub.filter.getResponderSendFilterTable();
                if (filterTable == null) {
                    filterTable = this.defaultResponderSendFilterTable;
                }
                filterTable.removeFilter(remoteSub, remoteAgent);
            }
        }
    }
    
    private void handleFilterUpdate(final FilterUpdateMessage message, final RemoteAgent remoteAgent) {
        ++MessageAgent.remoteFilterUpdateCount;
        if (Log.loggingDebug) {
            Log.debug("[" + remoteAgent.agentName + "," + message.getMsgId() + "] Got filter update subId=" + message.getSubId() + " rpc=" + message.isRPC());
        }
        final RemoteSubscription remoteSub = remoteAgent.getRemoteSubscription(message.getSubId());
        if (remoteSub == null) {
            Log.error("handleFilterUpdate: unknown subId=" + message.getSubId());
            return;
        }
        remoteSub.filter.applyFilterUpdate(message.getFilterUpdate(), remoteAgent, remoteSub);
        if (message.isRPC()) {
            final ResponseMessage response = new ResponseMessage(message);
            this.sendResponse(response);
        }
    }
    
    private void handleResponse(final ResponseMessage message, final RemoteAgent remoteAgent) {
        final PendingRPC rpc;
        synchronized (this.pendingRPC) {
            rpc = this.pendingRPC.get(message.getRequestId());
            if (rpc == null) {
                Log.error("Unexpected RPC response requestId=" + message.getRequestId() + " from=" + remoteAgent.agentName + "," + message.getMsgId());
                return;
            }
        }
        synchronized (rpc) {
            if (rpc.responders != null) {
                rpc.responders.remove(remoteAgent);
            }
            if (rpc.callback == this) {
                rpc.response = message;
                rpc.notify();
            }
            else {
                if (rpc.responders.size() == 0) {
                    synchronized (this.pendingRPC) {
                        this.pendingRPC.remove(message.getRequestId());
                    }
                }
                this.responseCallbackPool.execute(new AsyncRPCResponse(message, rpc.callback));
            }
        }
    }
    
    private void deliverMessage(final Message message) {
        int rpcCount = 0;
        final HashSet<Object> callbacks = new HashSet<Object>();
        if (message.isRPC()) {
            try {
                for (final FilterTable filterTable : this.responderReceiveFilters) {
                    filterTable.match(message, callbacks, null);
                }
                for (final Object callback : callbacks) {
                    if (Log.loggingDebug) {
                        Log.debug("deliverMessage " + message.getMsgId() + " rpc to " + callback);
                    }
                    if (callback instanceof MessageDispatch) {
                        ((MessageDispatch)callback).dispatchMessage(message, 1, (MessageCallback)callback);
                    }
                    else {
                        ((MessageCallback)callback).handleMessage(message, 1);
                    }
                    ++rpcCount;
                }
                if (rpcCount == 0) {
                    final ExceptionResponseMessage response = new ExceptionResponseMessage(message, new NoRecipientsException("sendRPC: no message recipients for type=" + message.getMsgType() + " " + message + ", class " + message.getClass().getName()));
                    this.sendResponse(response);
                }
            }
            catch (Exception e) {
                final ExceptionResponseMessage response2 = new ExceptionResponseMessage(message, e);
                this.sendResponse(response2);
            }
            callbacks.clear();
        }
        for (final FilterTable filterTable : this.receiveFilters) {
            filterTable.match(message, callbacks, null);
        }
        if (Log.loggingDebug && callbacks.size() == 0 && rpcCount == 0) {
            Log.debug("deliverMessage matched 0 callbacks");
        }
        for (final Object callback : callbacks) {
            if (Log.loggingDebug) {
                Log.debug("deliverMessage " + message.getMsgId() + " to " + callback);
            }
            if (callback instanceof MessageDispatch) {
                ((MessageDispatch)callback).dispatchMessage(message, 0, (MessageCallback)callback);
            }
            else {
                ((MessageCallback)callback).handleMessage(message, 0);
            }
        }
    }
    
    private RemoteAgent getAgent(final String agentName) {
        for (final RemoteAgent remoteAgent : this.remoteAgents) {
            if (agentName.equals(remoteAgent.agentName)) {
                return remoteAgent;
            }
        }
        return null;
    }
    
    private RemoteAgent getAgent(final int agentId) {
        for (final RemoteAgent remoteAgent : this.remoteAgents) {
            if (agentId == remoteAgent.agentId) {
                return remoteAgent;
            }
        }
        return null;
    }
    
    private void sendAdvertisements(final RemoteAgent remoteAgent) {
        final AdvertiseMessage message = new AdvertiseMessage(this.advertisements);
        message.setMessageId(this.nextMessageId());
        Message.toBytes(message, remoteAgent.outputBuf);
    }
    
    private synchronized long nextMessageId() {
        return this.nextMessageId++;
    }
    
    private void addUniqueFilterTable(final FilterTable filterTable, List<FilterTable> list) {
        synchronized (list) {
            if (!list.contains(filterTable)) {
                final List<FilterTable> newList = new LinkedList<FilterTable>(list);
                newList.add(filterTable);
                list = newList;
            }
        }
    }
    
    static {
        MessageAgent.intervalBetweenStatsLogging = 5000;
        MessageAgent.localSubscriptionCreatedCount = 0L;
        MessageAgent.localSubscriptionRemovedCount = 0L;
        MessageAgent.localFilterUpdateCount = 0L;
        MessageAgent.remoteSubscriptionCreatedCount = 0L;
        MessageAgent.remoteSubscriptionRemovedCount = 0L;
        MessageAgent.remoteFilterUpdateCount = 0L;
        MessageAgent.lastLocalSubscriptionCreatedCount = 0L;
        MessageAgent.lastLocalSubscriptionRemovedCount = 0L;
        MessageAgent.lastLocalFilterUpdateCount = 0L;
        MessageAgent.lastRemoteSubscriptionCreatedCount = 0L;
        MessageAgent.lastRemoteSubscriptionRemovedCount = 0L;
        MessageAgent.lastRemoteFilterUpdateCount = 0L;
    }
    
    public class BlockingRPCState implements ResponseCallback
    {
        private int responseCount;
        private ResponseCallback callback;
        
        public BlockingRPCState(final ResponseCallback callback) {
            this.callback = callback;
            this.responseCount = 0;
        }
        
        @Override
        public void handleResponse(final ResponseMessage response) {
            this.callback.handleResponse(response);
            synchronized (this) {
                ++this.responseCount;
                this.notifyAll();
            }
        }
        
        public int getResponseCount() {
            synchronized (this) {
                return this.responseCount;
            }
        }
    }
    
    private class NewConnectionHandler implements Runnable
    {
        SocketChannel agentSocket;
        
        public NewConnectionHandler(final SocketChannel socket) throws IOException {
            this.agentSocket = socket;
        }
        
        @Override
        public void run() {
            try {
                final RemoteAgent remoteAgent = MessageAgent.this.waitForAgent(this.agentSocket);
                if (remoteAgent == null) {
                    this.agentSocket.close();
                }
                else {
                    final AgentStateMessage agentState = new AgentStateMessage(MessageAgent.this.agentId, MessageAgent.this.agentName, ":same", MessageAgent.this.agentPort, MessageAgent.this.domainFlags);
                    Message.toBytes(agentState, remoteAgent.outputBuf);
                    MessageAgent.this.sendAdvertisements(remoteAgent);
                    if (Log.loggingDebug) {
                        Log.debug("received connect: Accepting connection from " + remoteAgent.agentName);
                    }
                    MessageAgent.this.messageIO.addAgent(remoteAgent);
                }
            }
            catch (Exception ex) {
                Log.exception("NewConnectionHandler", ex);
                try {
                    this.agentSocket.close();
                }
                catch (Exception ex2) {}
            }
        }
    }
    
    protected class RemoteAgent extends AgentInfo
    {
        Map<Long, RemoteSubscription> remoteSubs;
        Collection<MessageType> remoteAdverts;
        List<Message> outgoingQueue;
        static final int HAVE_ADVERTISEMENTS = 256;
        
        protected RemoteAgent() {
            this.remoteSubs = new HashMap<Long, RemoteSubscription>();
            this.remoteAdverts = new HashSet<MessageType>();
        }
        
        boolean isSelf() {
            return this == MessageAgent.this.selfRemoteAgent;
        }
        
        void addRemoteSubscription(final RemoteSubscription remoteSub) {
            synchronized (this.remoteSubs) {
                if (this.remoteSubs.get(remoteSub.subId) != null) {
                    Log.error("RemoteAgent " + this.agentName + ": Duplicate subId " + remoteSub.subId);
                    return;
                }
                this.remoteSubs.put(remoteSub.subId, remoteSub);
            }
        }
        
        RemoteSubscription removeRemoteSubscription(final Long subId) {
            synchronized (this.remoteSubs) {
                return this.remoteSubs.remove(subId);
            }
        }
        
        RemoteSubscription getRemoteSubscription(final Long subId) {
            synchronized (this.remoteSubs) {
                return this.remoteSubs.get(subId);
            }
        }
        
        Collection<MessageType> getAdvertisements() {
            return this.remoteAdverts;
        }
        
        void setAdvertisements(final Collection<MessageType> adverts) {
            this.remoteAdverts.clear();
            this.remoteAdverts.addAll(adverts);
        }
        
        int getFlags() {
            return this.flags;
        }
        
        boolean hasFlag(final int flag) {
            return (this.flags & flag) != 0x0;
        }
        
        void setFlag(final int flag) {
            this.flags |= flag;
        }
    }
    
    class RemoteSubscription extends Subscription
    {
        RemoteAgent remoteAgent;
        
        @Override
        public Object getAssociation() {
            return this.remoteAgent;
        }
    }
    
    private class AgentConnector implements Runnable
    {
        NewAgentMessage message;
        SocketChannel agentSocket;
        
        AgentConnector(final NewAgentMessage message) {
            this.agentSocket = null;
            this.message = message;
        }
        
        @Override
        public void run() {
            try {
                this.connect();
            }
            catch (Exception ex) {
                Log.exception("AgentConnector", ex);
                try {
                    if (this.agentSocket != null) {
                        this.agentSocket.close();
                    }
                }
                catch (Exception ex2) {}
            }
        }
        
        void connect() throws IOException, AORuntimeException {
            while (true) {
                try {
                    this.agentSocket = SocketChannel.open(new InetSocketAddress(this.message.agentIP, this.message.agentPort));
                }
                catch (ConnectException ex) {
                    Log.info("Could not connect to agent '" + this.message.agentName + "' at " + this.message.agentIP + ":" + this.message.agentPort + " " + ex);
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (Exception ex2) {}
                    continue;
                }
                break;
            }
            this.agentSocket.configureBlocking(false);
            this.agentSocket.socket().setTcpNoDelay(true);
            if (Log.loggingDebug) {
                Log.debug("MessageAgent: connected to agent " + this.agentSocket);
            }
            final AgentStateMessage agentState = new AgentStateMessage(MessageAgent.this.agentId, MessageAgent.this.agentName, ":same", MessageAgent.this.agentPort, MessageAgent.this.domainFlags);
            final AOByteBuffer buffer = new AOByteBuffer(64);
            Message.toBytes(agentState, buffer);
            buffer.flip();
            if (!ChannelUtil.writeBuffer(buffer, this.agentSocket)) {
                throw new RuntimeException("could not write to agent");
            }
            final RemoteAgent remoteAgent = MessageAgent.this.waitForAgent(this.agentSocket);
            if (remoteAgent == null) {
                this.agentSocket.close();
                return;
            }
            MessageAgent.this.sendAdvertisements(remoteAgent);
            if (Log.loggingDebug) {
                Log.debug("connect: Accepted connection from " + this.message.agentName);
            }
            MessageAgent.this.messageIO.addAgent(remoteAgent);
        }
    }
    
    private class MessageMarshaller implements Runnable
    {
        MessageMarshaller() {
            new Thread(this, "MessageMarshaller").start();
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        this.marshall();
                    }
                }
                catch (Exception ex) {
                    Log.exception("MessageMarshaller", ex);
                    continue;
                }
                break;
            }
        }
        
        void marshall() throws InterruptedException {
            final SquareQueue.SubQueue raq = MessageAgent.this.remoteAgentOutput.remove();
            try {
                if (raq.next()) {
                    final Message message = raq.getHeadValue();
                    final RemoteAgent remoteAgent = raq.getKey();
                    if (remoteAgent == MessageAgent.this.selfRemoteAgent) {
                        message.remoteAgent = remoteAgent;
                        MessageAgent.this.handleSelfMessage(message);
                    }
                    else {
                        synchronized (remoteAgent.outputBuf) {
                            if (remoteAgent.socket != null) {
                                Message.toBytes(message, remoteAgent.outputBuf);
                            }
                        }
                        MessageAgent.this.messageIO.outputReady();
                    }
                }
            }
            finally {
                MessageAgent.this.remoteAgentOutput.requeue(raq);
            }
        }
    }
    
    private class SelfMessageHandler implements Runnable
    {
        List<Message> selfMessages;
        
        SelfMessageHandler() {
            this.selfMessages = new LinkedList<Message>();
            final Thread thread = new Thread(this, "SelfMessage");
            thread.setDaemon(true);
            thread.start();
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        this.handle();
                    }
                }
                catch (Exception ex) {
                    Log.exception("SelfMessage", ex);
                    continue;
                }
                break;
            }
        }
        
        void handle() throws InterruptedException {
            synchronized (MessageAgent.this.selfRemoteAgent.outputBuf) {
                while (MessageAgent.this.selfRemoteAgent.outputBuf.position() == 0) {
                    MessageAgent.this.selfRemoteAgent.outputBuf.wait();
                }
                if (Log.loggingDebug) {
                    Log.debug("SelfMessageHandler.handle pos=" + MessageAgent.this.selfRemoteAgent.outputBuf.position());
                }
                final AOByteBuffer inputBuf = MessageAgent.this.selfRemoteAgent.outputBuf;
                inputBuf.flip();
                this.selfMessages.clear();
                while (inputBuf.remaining() >= 4) {
                    final int currentPos = inputBuf.position();
                    final int messageLen = inputBuf.getInt();
                    if (inputBuf.remaining() < messageLen) {
                        inputBuf.position(currentPos);
                        break;
                    }
                    final Message message = (Message)MarshallingRuntime.unmarshalObject(inputBuf);
                    message.remoteAgent = MessageAgent.this.selfRemoteAgent;
                    this.selfMessages.add(message);
                    inputBuf.position(currentPos + 4 + messageLen);
                }
                inputBuf.getNioBuf().compact();
            }
            for (final Message message2 : this.selfMessages) {
                MessageAgent.this.handleSelfMessage(message2);
            }
        }
    }
    
    class RegisteredSubscription extends Subscription
    {
        LinkedList<RemoteAgent> producers;
        MessageCallback callback;
        
        @Override
        public Object getAssociation() {
            return this.callback;
        }
    }
    
    public class DomainClient
    {
        public String allocName(final String type, final String namePattern) throws IOException {
            final AllocNameMessage allocName = new AllocNameMessage(type, namePattern);
            allocName.setMessageId(MessageAgent.this.nextMessageId());
            final List<AgentInfo> list = new ArrayList<AgentInfo>(1);
            list.add(MessageAgent.this.domainServerAgent);
            final PendingRPC rpc = MessageAgent.this.setupInternalRPC(allocName, list);
            MessageAgent.this.sendMessageToList(allocName, list);
            MessageAgent.this.waitInternalRPC(rpc);
            final AllocNameResponseMessage response = (AllocNameResponseMessage)rpc.response;
            return response.getName();
        }
        
        public void awaitPluginDependents(final String pluginType, final String pluginName) {
            final AwaitPluginDependentsMessage await = new AwaitPluginDependentsMessage(pluginType, pluginName);
            await.setMessageId(MessageAgent.this.nextMessageId());
            final List<AgentInfo> list = new ArrayList<AgentInfo>(1);
            list.add(MessageAgent.this.domainServerAgent);
            final PendingRPC rpc = MessageAgent.this.setupInternalRPC(await, list);
            MessageAgent.this.sendMessageToList(await, list);
            MessageAgent.this.waitInternalRPC(rpc);
        }
        
        public void pluginAvailable(final String pluginType, final String pluginName) {
            final PluginAvailableMessage available = new PluginAvailableMessage(pluginType, pluginName);
            available.setMessageId(MessageAgent.this.nextMessageId());
            final List<AgentInfo> list = new ArrayList<AgentInfo>(1);
            list.add(MessageAgent.this.domainServerAgent);
            MessageAgent.this.sendMessageToList(available, list);
        }
        
        Message readMessage() throws IOException {
            final ByteBuffer readBuffer = ByteBuffer.allocate(4);
            int nBytes = ChannelUtil.fillBuffer(readBuffer, MessageAgent.this.domainServerSocket);
            if (nBytes == 0) {
                throw new RuntimeException("domain server closed connection");
            }
            if (nBytes < 4) {
                throw new RuntimeException("domain server incomplete response");
            }
            final int msgLen = readBuffer.getInt();
            if (msgLen < 0) {
                throw new RuntimeException("domain server invalid response");
            }
            final AOByteBuffer buffer = new AOByteBuffer(msgLen);
            nBytes = ChannelUtil.fillBuffer(buffer.getNioBuf(), MessageAgent.this.domainServerSocket);
            if (nBytes == 0) {
                throw new RuntimeException("domain server closed connection");
            }
            if (nBytes < 4) {
                throw new RuntimeException("domain server invalid response, expecting " + msgLen + " got " + nBytes);
            }
            final Message message = (Message)MarshallingRuntime.unmarshalObject(buffer);
            return message;
        }
    }
    
    class PendingRPC
    {
        long messageId;
        Set<Object> responders;
        ResponseCallback callback;
        Message response;
    }
    
    class AsyncRPCResponse implements Runnable
    {
        ResponseMessage response;
        ResponseCallback callback;
        
        AsyncRPCResponse(final ResponseMessage m, final ResponseCallback c) {
            this.response = m;
            this.callback = c;
        }
        
        @Override
        public void run() {
            try {
                this.callback.handleResponse(this.response);
            }
            catch (Exception e) {
                Log.exception("ResponseCallback threw exception:  response=" + this.response + " callback=" + this.callback, e);
            }
        }
    }
    
    class ResponseThreadFactory implements ThreadFactory
    {
        int threadCount;
        
        ResponseThreadFactory() {
            this.threadCount = 1;
        }
        
        @Override
        public Thread newThread(final Runnable runnable) {
            return new Thread(runnable, "MessageResponse-" + this.threadCount++);
        }
    }
    
    class AgentConnectionThreadFactory implements ThreadFactory
    {
        int threadCount;
        
        AgentConnectionThreadFactory() {
            this.threadCount = 1;
        }
        
        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread thread = new Thread(runnable, "AgentConnection-" + this.threadCount++);
            thread.setDaemon(true);
            return thread;
        }
    }
    
    static class MessageAgentStatsLogger implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        Thread.sleep(MessageAgent.intervalBetweenStatsLogging);
                        Log.info("MessageAgent Local Subscription Counters: last interval/total: Created " + (MessageAgent.localSubscriptionCreatedCount - MessageAgent.lastLocalSubscriptionCreatedCount) + "/" + MessageAgent.localSubscriptionCreatedCount + ", Removed " + (MessageAgent.localSubscriptionRemovedCount - MessageAgent.lastLocalSubscriptionRemovedCount) + "/" + MessageAgent.localSubscriptionRemovedCount);
                        Log.info("MessageAgent Remote Subscription Counters: last interval/total: Created " + (MessageAgent.remoteSubscriptionCreatedCount - MessageAgent.lastRemoteSubscriptionCreatedCount) + "/" + MessageAgent.remoteSubscriptionCreatedCount + ", Removed " + (MessageAgent.remoteSubscriptionRemovedCount - MessageAgent.lastRemoteSubscriptionRemovedCount) + "/" + MessageAgent.remoteSubscriptionRemovedCount);
                        Log.info("MessageAgent Filter Updates: last interval/total: Local " + (MessageAgent.localFilterUpdateCount - MessageAgent.lastLocalFilterUpdateCount) + "/" + MessageAgent.localFilterUpdateCount + ", Remote " + (MessageAgent.remoteFilterUpdateCount - MessageAgent.lastRemoteFilterUpdateCount) + "/" + MessageAgent.remoteFilterUpdateCount);
                        MessageAgent.lastLocalSubscriptionCreatedCount = MessageAgent.localSubscriptionCreatedCount;
                        MessageAgent.lastLocalSubscriptionRemovedCount = MessageAgent.localSubscriptionRemovedCount;
                        MessageAgent.lastRemoteSubscriptionCreatedCount = MessageAgent.remoteSubscriptionCreatedCount;
                        MessageAgent.lastRemoteSubscriptionRemovedCount = MessageAgent.remoteSubscriptionRemovedCount;
                        MessageAgent.lastLocalFilterUpdateCount = MessageAgent.localFilterUpdateCount;
                        MessageAgent.lastRemoteFilterUpdateCount = MessageAgent.remoteFilterUpdateCount;
                    }
                }
                catch (Exception e) {
                    Log.exception("MessageAgent.MessageAgentStatsLogger.run thread interrupted", e);
                    continue;
                }
                break;
            }
        }
    }
}
