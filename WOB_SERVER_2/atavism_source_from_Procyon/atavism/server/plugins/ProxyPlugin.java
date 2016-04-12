// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.msgsys.ResponseMessage;
import atavism.msgsys.GenericMessage;
import java.util.LinkedHashMap;
import atavism.server.objects.Road;
import atavism.server.objects.FogRegionConfig;
import atavism.server.math.Quaternion;
import atavism.server.util.Base64;
import atavism.server.util.SecureTokenSpec;
import atavism.server.util.AnimationCommand;
import atavism.server.events.NotifyPlayAnimationEvent;
import atavism.server.events.DetachEvent;
import atavism.server.objects.Color;
import atavism.server.events.AmbientLightEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import atavism.server.util.ObjectLockManager;
import atavism.server.objects.OceanData;
import atavism.server.objects.Template;
import atavism.server.engine.Database;
import atavism.server.events.NotifyFreeObjectEvent;
import atavism.server.events.FreeTerrainDecalEvent;
import atavism.server.objects.SoundData;
import atavism.server.objects.ObjectType;
import atavism.server.events.NewTerrainDecalEvent;
import atavism.server.objects.TerrainDecalData;
import atavism.server.events.NewLightEvent;
import atavism.server.objects.Light;
import atavism.server.objects.LightData;
import atavism.server.objects.ObjectTypes;
import atavism.server.messages.PerceptionMessage;
import atavism.server.events.ExtensionMessageEvent;
import atavism.agis.events.AbilityStatusMessage;
import atavism.agis.events.AbilityStatusEvent;
import atavism.server.events.ActivateItemEvent;
import atavism.server.events.AutoAttackEvent;
import atavism.server.events.CommandEvent;
import atavism.server.events.ComEvent;
import atavism.server.events.DirLocOrientEvent;
import atavism.server.messages.LogoutMessage;
import atavism.agis.events.ConcludeQuest;
import atavism.agis.events.QuestResponse;
import atavism.agis.plugins.QuestClient;
import atavism.agis.events.RequestQuestInfo;
import atavism.server.events.LoadingStateEvent;
import atavism.server.events.UITheme;
import atavism.server.events.AttachEvent;
import atavism.server.objects.DisplayContext;
import atavism.server.events.ModelInfoEvent;
import atavism.server.events.WorldFileEvent;
import atavism.msgsys.ResponseCallback;
import atavism.server.messages.LoginMessage;
import atavism.server.objects.World;
import atavism.server.math.AOVector;
import atavism.server.objects.InstanceRestorePoint;
import atavism.server.engine.BasicWorldNode;
import java.io.Serializable;
import atavism.server.math.Point;
import atavism.server.engine.Namespace;
import atavism.server.util.SecureToken;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.FilterUpdate;
import atavism.msgsys.NoRecipientsException;
import atavism.msgsys.TargetMessage;
import atavism.server.util.SecureTokenManager;
import atavism.server.events.AuthorizedLoginResponseEvent;
import java.util.Iterator;
import atavism.server.events.AuthorizedLoginEvent;
import atavism.server.util.DebugUtils;
import atavism.server.network.AOByteBuffer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import atavism.server.util.Log;
import atavism.server.network.rdp.RDPServer;
import atavism.management.Management;
import atavism.msgsys.MessageTrigger;
import atavism.server.messages.PerceptionTrigger;
import java.util.Collection;
import atavism.agis.plugins.AnimationClient;
import atavism.agis.plugins.CombatClient;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.IFilter;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.network.PacketAggregator;
import java.util.ArrayList;
import atavism.server.engine.Hook;
import java.util.LinkedList;
import atavism.server.util.ServerVersion;
import java.io.IOException;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import java.util.HashSet;
import atavism.server.util.SQCallback;
import atavism.server.util.LockFactory;
import atavism.server.objects.ProxyExtensionHook;
import atavism.server.engine.OID;
import java.util.Set;
import atavism.server.network.ClientTCPMessageIO;
import atavism.server.objects.InstanceEntryCallback;
import atavism.server.objects.ProxyLoginCallback;
import java.util.List;
import atavism.server.util.TimeHistogram;
import atavism.server.objects.PlayerManager;
import atavism.server.util.Logger;
import atavism.server.network.rdp.RDPServerSocket;
import atavism.server.messages.PerceptionFilter;
import atavism.msgsys.MessageType;
import java.util.HashMap;
import atavism.server.util.AOMeter;
import atavism.server.engine.Event;
import atavism.server.util.SQThreadPool;
import atavism.msgsys.Message;
import atavism.server.util.SquareQueue;
import atavism.server.objects.Player;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import atavism.server.util.CountLogger;
import atavism.server.network.ClientConnection;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.EnginePlugin;

public class ProxyPlugin extends EnginePlugin implements atavism.msgsys.MessageCallback, ClientConnection.AcceptCallback, ClientConnection.MessageCallback
{
    public static final int PERCEPTION_GAIN_THRESHOLD = 20;
    Thread periodicGC;
    CountLogger countLogger;
    CountLogger.Counter countMsgPerception;
    CountLogger.Counter countMsgPerceptionGain;
    CountLogger.Counter countMsgPerceptionLost;
    CountLogger.Counter countMsgUpdateWNodeIn;
    CountLogger.Counter countMsgUpdateWNodeOut;
    CountLogger.Counter countMsgPropertyIn;
    CountLogger.Counter countMsgPropertyOut;
    CountLogger.Counter countMsgTargetedProperty;
    CountLogger.Counter countMsgWNodeCorrectIn;
    CountLogger.Counter countMsgWNodeCorrectOut;
    CountLogger.Counter countMsgMobPathIn;
    CountLogger.Counter countMsgMobPathOut;
    protected Lock commandMapLock;
    Map<String, RegisteredCommand> commandMap;
    CommandAccessCheck defaultCommandAccess;
    private static final Player loginSerializer;
    SquareQueue<Player, Message> messageQQ;
    SQThreadPool messageThreadPool;
    SquareQueue<Player, Event> eventQQ;
    SQThreadPool eventThreadPool;
    AOMeter clientMsgMeter;
    public boolean defaultAllowClientToClientMessage;
    protected HashMap<String, MessageType> extensionMessageRegistry;
    protected PerceptionFilter perceptionFilter;
    protected long perceptionSubId;
    protected PerceptionFilter responderFilter;
    protected long responderSubId;
    protected RDPServerSocket serverSocket;
    protected int clientPort;
    protected static final Logger log;
    PlayerMessageCallback playerMessageCallback;
    protected PlayerManager playerManager;
    protected TimeHistogram proxyQueueHistogram;
    protected TimeHistogram proxyCallbackHistogram;
    protected List<MessageType> extraPlayerMessageTypes;
    private ProxyLoginCallback proxyLoginCallback;
    private InstanceEntryCallback instanceEntryCallback;
    private int instanceEntryCount;
    private int chatSentCount;
    private int privateChatSentCount;
    public static final MessageType MSG_TYPE_VOICE_PARMS;
    public static final MessageType MSG_TYPE_PLAYER_PATH_REQ;
    public static final MessageType MSG_TYPE_UPDATE_PLAYER_IGNORE_LIST;
    public static final MessageType MSG_TYPE_GET_MATCHING_PLAYERS;
    public static final MessageType MSG_TYPE_PLAYER_IGNORE_LIST;
    public static final MessageType MSG_TYPE_PLAYER_IGNORE_LIST_REQ;
    public static final MessageType MSG_TYPE_RELAY_UPDATE_PLAYER_IGNORE_LIST;
    public static final MessageType MSG_TYPE_GET_PLAYER_LOGIN_STATUS;
    public static final MessageType MSG_TYPE_LOGOUT_PLAYER;
    public static final MessageType MSG_TYPE_ADD_STATIC_PERCEPTION;
    public static final MessageType MSG_TYPE_REMOVE_STATIC_PERCEPTION;
    public static final MessageType MSG_TYPE_LOGIN_SPAWNED;
    public static final MessageType MSG_TYPE_ACCOUNT_LOGIN;
    protected static String voiceServerHost;
    protected static Integer voiceServerPort;
    public String serverCapabilitiesSentToClient;
    static int serverSocketReceiveBufferSize;
    public static int MaxConcurrentUsers;
    public static int idleTimeout;
    public static int silenceTimeout;
    public static int maxMessagesBeforeConnectionReset;
    public static int maxByteCountBeforeConnectionReset;
    public String capacityError;
    public String tokenError;
    private ClientTCPMessageIO clientTCPMessageIO;
    Set<OID> adminSet;
    HashMap<OID, ClientConnection> clientConnections;
    Set<String> filteredProps;
    Set<String> playerSpecificProps;
    Set<String> cachedPlayerSpecificFilterProps;
    String serverVersion;
    protected Map<String, List<ProxyExtensionHook>> extensionHooks;
    boolean devMode;
    
    public ProxyPlugin() {
        this.countLogger = new CountLogger("ProxyMsg", 5000, 2);
        this.commandMapLock = LockFactory.makeLock("CommandMapLock");
        this.commandMap = new HashMap<String, RegisteredCommand>();
        this.defaultCommandAccess = new DefaultCommandAccess();
        this.messageQQ = new SquareQueue<Player, Message>("Message");
        this.messageThreadPool = new SQThreadPool(this.messageQQ, new MessageCallback(this));
        this.eventQQ = new SquareQueue<Player, Event>("Event");
        this.eventThreadPool = new SQThreadPool(this.eventQQ, new EventCallback());
        this.clientMsgMeter = new AOMeter("ClientEventProcessorMeter");
        this.defaultAllowClientToClientMessage = false;
        this.extensionMessageRegistry = new HashMap<String, MessageType>();
        this.serverSocket = null;
        this.playerMessageCallback = new PlayerMessageCallback();
        this.playerManager = new PlayerManager();
        this.proxyQueueHistogram = null;
        this.proxyCallbackHistogram = null;
        this.extraPlayerMessageTypes = null;
        this.proxyLoginCallback = new DefaultProxyLoginCallback();
        this.instanceEntryCallback = new DefaultInstanceEntryCallback();
        this.instanceEntryCount = 0;
        this.chatSentCount = 0;
        this.privateChatSentCount = 0;
        this.serverCapabilitiesSentToClient = "DirLocOrient";
        this.capacityError = "Login Failed: Servers at capacity, please try again later.";
        this.tokenError = "Login Failed: Secure token invalid.";
        this.clientTCPMessageIO = null;
        this.adminSet = new HashSet<OID>();
        this.clientConnections = new HashMap<OID, ClientConnection>();
        this.filteredProps = null;
        this.playerSpecificProps = null;
        this.cachedPlayerSpecificFilterProps = null;
        this.serverVersion = null;
        this.extensionHooks = new HashMap<String, List<ProxyExtensionHook>>();
        this.devMode = true;
        this.setPluginType("Proxy");
        String proxyPluginName;
        try {
            proxyPluginName = Engine.getAgent().getDomainClient().allocName("PLUGIN", this.getPluginType() + "#");
        }
        catch (IOException e) {
            throw new AORuntimeException("Could not allocate proxy plugin name", e);
        }
        this.setName(proxyPluginName);
        this.serverVersion = "2.5.0 " + ServerVersion.getBuildNumber();
        this.setMessageHandler(null);
        this.countMsgPerception = this.countLogger.addCounter("ao.PERCEPTION_INFO");
        this.countMsgPerceptionGain = this.countLogger.addCounter("Perception.gain");
        this.countMsgPerceptionLost = this.countLogger.addCounter("Perception.lost");
        this.countMsgUpdateWNodeIn = this.countLogger.addCounter("ao.UPDATEWNODE.in");
        this.countMsgUpdateWNodeOut = this.countLogger.addCounter("ao.UPDATEWNODE.out");
        this.countMsgPropertyIn = this.countLogger.addCounter("ao.PROPERTY.in");
        this.countMsgPropertyOut = this.countLogger.addCounter("ao.PROPERTY.out");
        this.countMsgTargetedProperty = this.countLogger.addCounter("ao.TARGETED_PROPERTY");
        this.countMsgWNodeCorrectIn = this.countLogger.addCounter("ao.WNODECORRECT.in");
        this.countMsgWNodeCorrectOut = this.countLogger.addCounter("ao.WNODECORRECT.out");
        this.countMsgMobPathIn = this.countLogger.addCounter("ao.MOB_PATH.in");
        this.countMsgMobPathOut = this.countLogger.addCounter("ao.MOB_PATH.out");
        this.addProxyExtensionHook("ao.heartbeat", new PlayerHeartbeat());
        new Thread(new PlayerTimeout(), "PlayerTimeout").start();
    }
    
    public boolean isDevMode() {
        return this.devMode;
    }
    
    public void setDevMode(final boolean mode) {
        this.devMode = mode;
    }
    
    public List<MessageType> getExtraPlayerMessageTypes() {
        return this.extraPlayerMessageTypes;
    }
    
    public void setExtraPlayerMessageTypes(final List<MessageType> extraPlayerMessageTypes) {
        this.extraPlayerMessageTypes = extraPlayerMessageTypes;
    }
    
    public void addExtraPlayerMessageType(final MessageType messageType) {
        if (this.extraPlayerMessageTypes == null) {
            this.extraPlayerMessageTypes = new LinkedList<MessageType>();
        }
        this.extraPlayerMessageTypes.add(messageType);
    }
    
    public void addExtraPlayerExtensionMessageType(final MessageType messageType) {
        this.addExtraPlayerMessageType(messageType);
        this.getHookManager().addHook(messageType, new ExtensionHook());
    }
    
    public void addProxyExtensionHook(final String subType, final ProxyExtensionHook hook) {
        synchronized (this.extensionHooks) {
            List<ProxyExtensionHook> hookList = this.extensionHooks.get(subType);
            if (hookList == null) {
                hookList = new ArrayList<ProxyExtensionHook>();
                this.extensionHooks.put(subType, hookList);
            }
            hookList.add(hook);
        }
    }
    
    public Map<String, List<ProxyExtensionHook>> getProxyExtensionHooks(final String subType) {
        return this.extensionHooks;
    }
    
    @Override
    public void onActivate() {
        try {
            PacketAggregator.initializeAggregation(Engine.getProperties());
            final String logProxyHistograms = Engine.properties.getProperty("atavism.log_proxy_histograms");
            if (logProxyHistograms != null && logProxyHistograms.equals("true")) {
                int interval = 5000;
                final String intervalString = Engine.properties.getProperty("atavism.log_proxy_histograms_interval");
                if (intervalString != null) {
                    final int newInterval = Integer.parseInt(intervalString);
                    if (newInterval > 0) {
                        interval = newInterval;
                    }
                }
                this.proxyQueueHistogram = new TimeHistogram("TimeInQ", interval);
                this.proxyCallbackHistogram = new TimeHistogram("TimeInCallback", interval);
                this.countLogger.start();
            }
            this.filteredProps = new HashSet<String>();
            this.playerSpecificProps = new HashSet<String>();
            this.cachedPlayerSpecificFilterProps = new HashSet<String>();
            this.addFilteredProperty("inv.bag");
            this.addFilteredProperty(":loc");
            this.addFilteredProperty("masterOid");
            this.addFilteredProperty("agisobj.basedc");
            this.addFilteredProperty("aoobj.dc");
            this.addFilteredProperty("aoobj.followsterrainflag");
            this.addFilteredProperty("aoobj.perceiver");
            this.addFilteredProperty("aoobj.scale");
            this.addFilteredProperty("aoobj.mobflag");
            this.addFilteredProperty("aoobj.structflag");
            this.addFilteredProperty("aoobj.userflag");
            this.addFilteredProperty("aoobj.itemflag");
            this.addFilteredProperty("aoobj.lightflag");
            this.addFilteredProperty("namespace");
            this.addFilteredProperty("regenEffectMap");
            this.addFilteredProperty(WorldManagerClient.MOB_PATH_PROPERTY);
            this.addFilteredProperty(WorldManagerClient.TEMPL_SOUND_DATA_LIST);
            this.addFilteredProperty(WorldManagerClient.TEMPL_TERRAIN_DECAL_DATA);
            this.addFilteredProperty("instanceStack");
            this.addFilteredProperty("currentInstanceName");
            this.addFilteredProperty("ignored_oids");
            this.registerHooks();
            final PluginMessageCallback pluginMessageCallback = new PluginMessageCallback();
            final MessageTypeFilter filter = new MessageTypeFilter();
            filter.addType(WorldManagerClient.MSG_TYPE_SYS_CHAT);
            Engine.getAgent().createSubscription(filter, pluginMessageCallback);
            this.perceptionFilter = new PerceptionFilter();
            final LinkedList<MessageType> types = new LinkedList<MessageType>();
            types.add(WorldManagerClient.MSG_TYPE_PERCEPTION_INFO);
            types.add(WorldManagerClient.MSG_TYPE_ANIMATION);
            types.add(WorldManagerClient.MSG_TYPE_DISPLAY_CONTEXT);
            types.add(WorldManagerClient.MSG_TYPE_DETACH);
            types.add(PropertyMessage.MSG_TYPE_PROPERTY);
            types.add(WorldManagerClient.MSG_TYPE_COM);
            types.add(CombatClient.MSG_TYPE_DAMAGE);
            types.add(WorldManagerClient.MSG_TYPE_UPDATEWNODE);
            types.add(WorldManagerClient.MSG_TYPE_MOB_PATH);
            types.add(WorldManagerClient.MSG_TYPE_WNODECORRECT);
            types.add(WorldManagerClient.MSG_TYPE_ORIENT);
            types.add(WorldManagerClient.MSG_TYPE_SOUND);
            types.add(AnimationClient.MSG_TYPE_INVOKE_EFFECT);
            types.add(WorldManagerClient.MSG_TYPE_EXTENSION);
            types.add(WorldManagerClient.MSG_TYPE_P2P_EXTENSION);
            types.add(InventoryClient.MSG_TYPE_INV_UPDATE);
            types.add(CombatClient.MSG_TYPE_ABILITY_STATUS);
            types.add(CombatClient.MSG_TYPE_ABILITY_UPDATE);
            types.add(WorldManagerClient.MSG_TYPE_FOG);
            types.add(WorldManagerClient.MSG_TYPE_ROAD);
            types.add(WorldManagerClient.MSG_TYPE_NEW_DIRLIGHT);
            types.add(WorldManagerClient.MSG_TYPE_SET_AMBIENT);
            types.add(WorldManagerClient.MSG_TYPE_TARGETED_PROPERTY);
            types.add(WorldManagerClient.MSG_TYPE_FREE_OBJECT);
            types.add(ProxyPlugin.MSG_TYPE_VOICE_PARMS);
            types.add(ProxyPlugin.MSG_TYPE_UPDATE_PLAYER_IGNORE_LIST);
            types.add(ProxyPlugin.MSG_TYPE_GET_MATCHING_PLAYERS);
            types.add(ProxyPlugin.MSG_TYPE_ADD_STATIC_PERCEPTION);
            types.add(ProxyPlugin.MSG_TYPE_REMOVE_STATIC_PERCEPTION);
            if (this.extraPlayerMessageTypes != null) {
                types.addAll(this.extraPlayerMessageTypes);
            }
            this.perceptionFilter.setTypes(types);
            this.perceptionFilter.setMatchAllSubjects(true);
            final PerceptionTrigger perceptionTrigger = new PerceptionTrigger();
            this.perceptionSubId = Engine.getAgent().createSubscription(this.perceptionFilter, this.playerMessageCallback, 0, perceptionTrigger);
            this.responderFilter = new PerceptionFilter();
            types.clear();
            types.add(InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ);
            types.add(ProxyPlugin.MSG_TYPE_PLAYER_IGNORE_LIST_REQ);
            types.add(ProxyPlugin.MSG_TYPE_GET_PLAYER_LOGIN_STATUS);
            types.add(ProxyPlugin.MSG_TYPE_LOGOUT_PLAYER);
            this.responderFilter.setTypes(types);
            this.responderSubId = Engine.getAgent().createSubscription(this.responderFilter, this.playerMessageCallback, 8);
            types.clear();
            types.add(Management.MSG_TYPE_GET_PLUGIN_STATUS);
            Engine.getAgent().createSubscription(new MessageTypeFilter(types), pluginMessageCallback, 8);
            final MessageTypeFilter filter2 = new MessageTypeFilter();
            filter2.addType(ProxyPlugin.MSG_TYPE_ACCOUNT_LOGIN);
            Engine.getAgent().createSubscription(filter2, this);
            this.serverSocket = new RDPServerSocket();
            final String log_rdp_counters = Engine.getProperty("atavism.log_rdp_counters");
            if (log_rdp_counters == null || log_rdp_counters.equals("false")) {
                RDPServer.setCounterLogging(false);
            }
            RDPServer.startRDPServer();
            this.serverSocket.registerAcceptCallback(this);
            this.initializeVoiceServerInformation();
            this.registerExtensionSubtype("voice_parms", ProxyPlugin.MSG_TYPE_VOICE_PARMS);
            this.registerExtensionSubtype("player_path_req", ProxyPlugin.MSG_TYPE_PLAYER_PATH_REQ);
            this.registerExtensionSubtype("player_path_req", ProxyPlugin.MSG_TYPE_PLAYER_PATH_REQ);
            this.registerExtensionSubtype("ao.UPDATE_PLAYER_IGNORE_LIST", ProxyPlugin.MSG_TYPE_UPDATE_PLAYER_IGNORE_LIST);
            this.registerExtensionSubtype("ao.GET_MATCHING_PLAYERS", ProxyPlugin.MSG_TYPE_GET_MATCHING_PLAYERS);
            this.registerExtensionSubtype("ao.PLAYER_IGNORE_LIST_REQ", ProxyPlugin.MSG_TYPE_PLAYER_IGNORE_LIST_REQ);
            final InetSocketAddress bindAddress = this.getBindAddress();
            if (Log.loggingDebug) {
                Log.debug("BIND: binding for client tcp and rdp packets on port " + bindAddress.getPort() + " with rdpsocket: " + this.serverSocket.toString());
            }
            this.serverSocket.bind(Integer.parseInt(Engine.getProperty("atavism.proxy.bindport")), ProxyPlugin.serverSocketReceiveBufferSize);
            Log.debug("BIND: bound server socket");
            this.clientTCPMessageIO = ClientTCPMessageIO.setup(bindAddress, this, this);
            Log.debug("BIND: setup clientTCPMessage");
            this.clientTCPMessageIO.start("ClientIO");
            Log.debug("BIND: started clientTCPMessage");
            final InetSocketAddress externalAddress = this.getExternalAddress(bindAddress);
            Log.debug("BIND: got external address");
            this.setPluginInfo("host=" + externalAddress.getHostName() + ",port=" + externalAddress.getPort());
            Log.debug("Registering proxy plugin");
            Engine.registerStatusReportingPlugin(this);
            Log.debug("Proxy: activation done");
        }
        catch (Exception e) {
            throw new AORuntimeException("activate failed", e);
        }
    }
    
    private InetSocketAddress getBindAddress() throws IOException {
        String propStr = Engine.getProperty("atavism.proxy.bindport");
        int port;
        if (propStr != null) {
            port = Integer.parseInt(propStr.trim());
        }
        else {
            port = Integer.parseInt(Engine.getProperty("atavism.proxyport").trim());
        }
        propStr = Engine.getProperty("atavism.proxy.bindaddress");
        InetAddress address = null;
        if (propStr != null) {
            address = InetAddress.getByName(propStr.trim());
        }
        return new InetSocketAddress(address, port);
    }
    
    private InetSocketAddress getExternalAddress(final InetSocketAddress bindAddress) throws IOException {
        String propStr = Engine.getProperty("atavism.proxy.externalport");
        int port;
        if (propStr != null) {
            port = Integer.parseInt(propStr.trim());
        }
        else {
            port = bindAddress.getPort();
        }
        propStr = Engine.getProperty("atavism.proxy.externaladdress");
        InetAddress address;
        if (propStr != null) {
            address = InetAddress.getByName(propStr.trim());
        }
        else {
            address = bindAddress.getAddress();
            if (address.isAnyLocalAddress()) {
                address = InetAddress.getLocalHost();
            }
        }
        return new InetSocketAddress(address, port);
    }
    
    @Override
    public Map<String, String> getStatusMap() {
        final Map<String, String> status = new HashMap<String, String>();
        status.put("players", Integer.toString(this.playerManager.getPlayerCount()));
        return status;
    }
    
    public void registerCommand(final String command, final CommandParser parser) {
        this.commandMapLock.lock();
        try {
            this.commandMap.put(command, new RegisteredCommand(parser, this.defaultCommandAccess));
        }
        finally {
            this.commandMapLock.unlock();
        }
    }
    
    public void registerCommand(final String command, final CommandParser parser, final CommandAccessCheck access) {
        this.commandMapLock.lock();
        try {
            this.commandMap.put(command, new RegisteredCommand(parser, access));
        }
        finally {
            this.commandMapLock.unlock();
        }
    }
    
    void registerHooks() {
        ProxyPlugin.log.debug("registering hooks");
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DISPLAY_CONTEXT, new DisplayContextHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DETACH, new DetachHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_ANIMATION, new AnimationHook());
        this.getHookManager().addHook(AnimationClient.MSG_TYPE_INVOKE_EFFECT, new InvokeEffectHook());
        this.getHookManager().addHook(PropertyMessage.MSG_TYPE_PROPERTY, new PropertyHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_EXTENSION, new ExtensionHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_ABILITY_STATUS, new AbilityStatusHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_TARGETED_PROPERTY, new TargetedPropertyHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_PERCEPTION_INFO, new PerceptionHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_COM, new ComHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_DAMAGE, new DamageHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SYS_CHAT, new SysChatHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATEWNODE, new UpdateWNodeHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_MOB_PATH, new UpdateMobPathHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_WNODECORRECT, new WNodeCorrectHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_ORIENT, new OrientHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SOUND, new SoundHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_FOG, new FogHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_ROAD, new RoadHook());
        this.getHookManager().addHook(InventoryClient.MSG_TYPE_INV_UPDATE, new InvUpdateHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_ABILITY_UPDATE, new AbilityUpdateHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_NEW_DIRLIGHT, new NewDirLightHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_FREE_OBJECT, new FreeObjectHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SET_AMBIENT, new SetAmbientHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_P2P_EXTENSION, new P2PExtensionHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_VOICE_PARMS, new VoiceParmsHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_PLAYER_PATH_REQ, new PlayerPathReqHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ, new InstanceEntryReqHook());
        this.getHookManager().addHook(Management.MSG_TYPE_GET_PLUGIN_STATUS, new GetPluginStatusHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_UPDATE_PLAYER_IGNORE_LIST, new UpdatePlayerIgnoreListHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_GET_MATCHING_PLAYERS, new GetMatchingPlayersHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_PLAYER_IGNORE_LIST_REQ, new PlayerIgnoreListReqHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_GET_PLAYER_LOGIN_STATUS, new GetPlayerLoginStatusHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_LOGOUT_PLAYER, new LogoutPlayerHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_ADD_STATIC_PERCEPTION, new AddStaticPerceptionHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_REMOVE_STATIC_PERCEPTION, new RemoveStaticPerceptionHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_ACCOUNT_LOGIN, new AccountLoginHook());
    }
    
    void callEngineOnMessage(final Message message, final int flags) {
        super.handleMessage(message, flags);
    }
    
    protected void initializeVoiceServerInformation() {
        ProxyPlugin.voiceServerHost = Engine.properties.getProperty("atavism.voiceserver");
        final String s = Engine.properties.getProperty("atavism.voiceport");
        if (s != null) {
            ProxyPlugin.voiceServerPort = Integer.parseInt(s);
        }
        if (Log.loggingDebug) {
            ProxyPlugin.log.debug("initializeVoiceServerInformation: voiceServerHost " + ProxyPlugin.voiceServerHost + ", voiceServerPort " + ProxyPlugin.voiceServerPort);
        }
    }
    
    @Override
    public void acceptConnection(final ClientConnection con) {
        Log.info("ProxyPlugin: CONNECTION remote=" + con);
        con.registerMessageCallback(this);
    }
    
    @Override
    public void processPacket(final ClientConnection con, final AOByteBuffer buf) {
        try {
            if (Log.loggingNet) {
                if (ClientConnection.getLogMessageContents()) {
                    Log.net("ProxyPlugin.processPacket: con " + con + ", length " + buf.limit() + ", packet " + DebugUtils.byteArrayToHexString(buf));
                }
                else {
                    Log.net("ProxyPlugin.processPacket: con " + con + ", buf " + buf);
                }
            }
            final Event event = Engine.getEventServer().parseBytes(buf, con);
            if (event == null) {
                Log.error("Engine: could not parse packet data, remote=" + con);
                return;
            }
            Player player = (Player)con.getAssociation();
            if (player == null) {
                player = ProxyPlugin.loginSerializer;
                if (event instanceof AuthorizedLoginEvent) {
                    Log.info("ProxyPlugin: LOGIN_RECV remote=" + con + " playerOid=" + ((AuthorizedLoginEvent)event).getOid());
                }
            }
            this.playerManager.processEvent(player, event, this.eventQQ);
        }
        catch (AORuntimeException e) {
            Log.exception("ProxyPlugin.processPacket caught exception", e);
        }
    }
    
    public Set<OID> getPlayerOids() {
        final List<Player> players = new ArrayList<Player>(this.playerManager.getPlayerCount());
        this.playerManager.getPlayers(players);
        final Set<OID> result = new HashSet<OID>(players.size());
        for (final Player player : players) {
            result.add(player.getOid());
        }
        return result;
    }
    
    public List<String> getPlayerNames() {
        final List<Player> players = new ArrayList<Player>(this.playerManager.getPlayerCount());
        Log.debug("ProxyPlugin.getPlayerNames: count is " + this.playerManager.getPlayerCount());
        this.playerManager.getPlayers(players);
        final List<String> result = new ArrayList<String>(players.size());
        for (final Player player : players) {
            result.add(player.getName());
        }
        return result;
    }
    
    public List<Player> getPlayers() {
        final List<Player> players = new ArrayList<Player>(this.playerManager.getPlayerCount());
        this.playerManager.getPlayers(players);
        return players;
    }
    
    public Player getPlayer(final OID oid) {
        return this.playerManager.getPlayer(oid);
    }
    
    public void addPlayerMessage(final Message message, final Player player) {
        message.setEnqueueTime();
        this.messageQQ.insert(player, message);
    }
    
    public void addFilteredProperty(final String filteredProperty) {
        this.filteredProps.add(filteredProperty);
        this.cachedPlayerSpecificFilterProps.add(filteredProperty);
    }
    
    public void addPlayerSpecificProperty(final String filteredProperty) {
        this.playerSpecificProps.add(filteredProperty);
        this.cachedPlayerSpecificFilterProps.add(filteredProperty);
    }
    
    protected void recreatePlayerSpecificCache() {
        (this.cachedPlayerSpecificFilterProps = new HashSet<String>()).addAll(this.filteredProps);
        this.cachedPlayerSpecificFilterProps.addAll(this.playerSpecificProps);
    }
    
    protected boolean processLogin(final ClientConnection con, final AuthorizedLoginEvent loginEvent) {
        final OID playerOid = loginEvent.getOid();
        final String version = loginEvent.getVersion();
        final int nPlayers = this.playerManager.getPlayerCount();
        String[] clientVersionCapabilities = null;
        if (version != null && version.length() > 0) {
            clientVersionCapabilities = version.split(",");
        }
        final LinkedList<String> clientCapabilities = new LinkedList<String>();
        String clientVersion = "";
        if (clientVersionCapabilities != null && clientVersionCapabilities.length > 0) {
            clientVersion = clientVersionCapabilities[0];
            for (int i = 1; i < clientVersionCapabilities.length; ++i) {
                clientCapabilities.add(clientVersionCapabilities[i].trim());
            }
        }
        final int versionCompare = ServerVersion.compareVersionStrings(clientVersion, "2.5.0");
        if (versionCompare != 1 && versionCompare != 0) {
            Log.warn("processLogin: unsupported version " + clientVersion + " from player: " + playerOid);
            final AuthorizedLoginResponseEvent loginResponse = new AuthorizedLoginResponseEvent(playerOid, false, "Login Failed: Unsupported client version", this.serverVersion);
            con.send(loginResponse.toBytes());
            return false;
        }
        if (!this.isAdmin(playerOid)) {
            Log.debug("processLogin: player is not admin");
            if (nPlayers >= ProxyPlugin.MaxConcurrentUsers) {
                Log.warn("processLogin: too many users, failed for player: " + playerOid);
                final Event loginResponse2 = new AuthorizedLoginResponseEvent(playerOid, false, this.capacityError, this.serverVersion);
                con.send(loginResponse2.toBytes());
                return false;
            }
        }
        else {
            Log.debug("processLogin: player is admin, bypassing max check");
        }
        final SecureToken token = SecureTokenManager.getInstance().importToken(loginEvent.getWorldToken());
        boolean validToken = true;
        if (!token.getValid()) {
            Log.debug("token is not valid");
            validToken = false;
        }
        final OID characterOid = (OID)token.getProperty("character_oid");
        Log.debug("PlayerOID: " + playerOid);
        Log.debug("CharacterOID: " + characterOid);
        if (!playerOid.equals(characterOid)) {
            Log.debug("playerOid does not match character_oid");
            validToken = false;
        }
        if (!token.getProperty("proxy_server").equals(Engine.getAgent().getName())) {
            Log.debug("proxy_server does not match engine agent name");
            validToken = false;
        }
        if (!validToken) {
            Log.error("processLogin: invalid proxy token");
            final Event loginResponse3 = new AuthorizedLoginResponseEvent(playerOid, false, this.tokenError, this.serverVersion);
            con.send(loginResponse3.toBytes());
            return false;
        }
        try {
            final TargetMessage getPlayerLoginStatus = new TargetMessage(ProxyPlugin.MSG_TYPE_GET_PLAYER_LOGIN_STATUS, playerOid);
            final PlayerLoginStatus loginStatus = (PlayerLoginStatus)Engine.getAgent().sendRPCReturnObject(getPlayerLoginStatus);
            if (this.proxyLoginCallback.duplicateLogin(loginStatus, con)) {
                if (loginStatus != null) {
                    Log.info("processLogin: LOGIN_DUPLICATE remote=" + con + " playerOid=" + playerOid + " name=" + loginStatus.name + " existingCon=" + loginStatus.clientCon + " existingProxy=" + loginStatus.proxyPluginName);
                }
                else {
                    Log.info("processLogin: LOGIN_DUPLICATE remote=" + con + " playerOid=" + playerOid + " loginStatus is null");
                }
                final AuthorizedLoginResponseEvent loginResponse4 = new AuthorizedLoginResponseEvent(playerOid, false, "Login Failed: Already connected", this.serverVersion);
                con.send(loginResponse4.toBytes());
                return false;
            }
        }
        catch (NoRecipientsException ex) {}
        Player player = new Player(playerOid, con);
        player.setStatus(1);
        if (!this.playerManager.addPlayer(player)) {
            player = this.playerManager.getPlayer(playerOid);
            Log.info("processLogin: LOGIN_DUPLICATE remote=" + con + " playerOid=" + playerOid + " existing=" + player.getConnection());
            final AuthorizedLoginResponseEvent loginResponse5 = new AuthorizedLoginResponseEvent(playerOid, false, "Login Failed: Already connected", this.serverVersion);
            con.send(loginResponse5.toBytes());
            return false;
        }
        con.setAssociation(player);
        player.setVersion(clientVersion);
        player.setCapabilities(clientCapabilities);
        String errorMessage = this.proxyLoginCallback.preLoad(player, con);
        if (errorMessage != null) {
            this.playerManager.removePlayer(playerOid);
            final Event loginResponse6 = new AuthorizedLoginResponseEvent(playerOid, false, errorMessage, this.serverVersion);
            con.send(loginResponse6.toBytes());
            return false;
        }
        if (Log.loggingDebug) {
            Log.debug("processLogin: loading object: " + playerOid + ", con=" + con);
        }
        if (!this.loadPlayerObject(player)) {
            Log.error("processLogin: could not load object " + playerOid);
            this.playerManager.removePlayer(playerOid);
            final Event loginResponse6 = new AuthorizedLoginResponseEvent(playerOid, false, "Login Failed", this.serverVersion);
            con.send(loginResponse6.toBytes());
            return false;
        }
        if (Log.loggingDebug) {
            Log.debug("processLogin: loaded player object: " + playerOid);
        }
        errorMessage = this.proxyLoginCallback.postLoad(player, con);
        if (errorMessage != null) {
            this.playerManager.removePlayer(playerOid);
            final Event loginResponse6 = new AuthorizedLoginResponseEvent(playerOid, false, errorMessage, this.serverVersion);
            con.send(loginResponse6.toBytes());
            return false;
        }
        final AuthorizedLoginResponseEvent loginResponse4 = new AuthorizedLoginResponseEvent(playerOid, true, "Login Succeeded", this.serverVersion + ", " + this.serverCapabilitiesSentToClient);
        con.send(loginResponse4.toBytes());
        if (Log.loggingDebug) {
            Log.debug("Login response sent for playerOid=" + playerOid + " the authorized login response message is : " + loginResponse4.getMessage());
        }
        this.playerManager.addStaticPerception(player, playerOid);
        final boolean loginOK = this.processLoginHelper(con, player);
        if (!loginOK) {
            con.setAssociation(null);
            if (this.perceptionFilter.removeTarget(playerOid)) {
                final FilterUpdate filterUpdate = new FilterUpdate(1);
                filterUpdate.removeFieldValue(1, playerOid);
                Engine.getAgent().applyFilterUpdate(this.perceptionSubId, filterUpdate);
                this.responderFilter.removeTarget(playerOid);
                Engine.getAgent().applyFilterUpdate(this.responderSubId, filterUpdate);
            }
            this.playerManager.removeStaticPerception(player, playerOid);
            this.playerManager.removePlayer(playerOid);
            con.close();
        }
        else {
            this.proxyLoginCallback.postSpawn(player, con);
            this.playerManager.loginComplete(player, this.eventQQ);
            final SubjectMessage loginSpawned = new SubjectMessage(ProxyPlugin.MSG_TYPE_LOGIN_SPAWNED);
            loginSpawned.setSubject(playerOid);
            Engine.getAgent().sendBroadcast(loginSpawned);
            this.processPlayerIgnoreList(player);
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            this.clientConnections.put(accountID, con);
        }
        return loginOK;
    }
    
    protected boolean loadPlayerObject(final Player player) {
        InstanceRestorePoint restorePoint = null;
        LinkedList restoreStack = null;
        boolean first = true;
        final OID playerOid = player.getOid();
        final List<Namespace> namespaces = new ArrayList<Namespace>();
        final OID oidResult = ObjectManagerClient.loadSubObject(playerOid, namespaces);
        if (oidResult == null) {
            return false;
        }
        Point location = new Point();
        OID instanceOid = Engine.getDatabase().getLocation(playerOid, WorldManagerClient.NAMESPACE, location);
        while (true) {
            OID result = null;
            if (this.instanceEntryAllowed(playerOid, instanceOid, location)) {
                final InstanceClient.InstanceInfo instanceInfo = InstanceClient.getInstanceInfo(instanceOid, -8193);
                if (instanceInfo.populationLimit < 1 || instanceInfo.playerPopulation < instanceInfo.populationLimit) {
                    result = ObjectManagerClient.loadObject(playerOid);
                    Log.debug("POP: loading player into instance: " + instanceOid);
                }
            }
            if (restoreStack != null && !restorePoint.getFallbackFlag()) {
                EnginePlugin.setObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "instanceStack", restoreStack);
            }
            if (result != null) {
                if (restorePoint != null) {
                    EnginePlugin.setObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "currentInstanceName", restorePoint.getInstanceName());
                }
                return true;
            }
            if (first) {
                final String instanceName = (String)EnginePlugin.getObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "currentInstanceName");
                Log.debug("Failed initial load, retrying with current instanceName=" + instanceName);
                if (instanceName != null) {
                    instanceOid = this.instanceEntryCallback.selectInstance(player, instanceName);
                    if (instanceOid != null) {
                        InstanceClient.InstanceInfo instanceInfo2 = InstanceClient.getInstanceInfo(instanceOid, -8193);
                        if (instanceInfo2.populationLimit > 0 && instanceInfo2.playerPopulation >= instanceInfo2.populationLimit) {
                            Log.debug("POP: got population: " + instanceInfo2.playerPopulation + " and limit: " + instanceInfo2.populationLimit);
                            instanceOid = handleFullInstance(instanceInfo2.templateName, instanceInfo2);
                            instanceInfo2 = InstanceClient.getInstanceInfo(instanceOid, -8193);
                        }
                        Log.debug("Failed initial load, retrying with instanceOid=" + instanceOid + " and instanceName: " + instanceInfo2.name);
                        final BasicWorldNode wnode = new BasicWorldNode();
                        wnode.setInstanceOid(instanceOid);
                        ObjectManagerClient.fixWorldNode(playerOid, wnode);
                        first = false;
                        continue;
                    }
                }
            }
            if (restorePoint != null && restorePoint.getFallbackFlag()) {
                return false;
            }
            restoreStack = (LinkedList)EnginePlugin.getObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "instanceStack");
            if (restoreStack == null || restoreStack.size() == 0) {
                return false;
            }
            final int size = restoreStack.size();
            restorePoint = restoreStack.get(size - 1);
            instanceOid = restorePoint.getInstanceOid();
            if (restorePoint.getInstanceName() != null) {
                instanceOid = this.instanceEntryCallback.selectInstance(player, restorePoint.getInstanceName());
            }
            if (instanceOid != null) {
                final BasicWorldNode wnode2 = new BasicWorldNode();
                wnode2.setInstanceOid(instanceOid);
                if (!first) {
                    wnode2.setLoc(restorePoint.getLoc());
                    wnode2.setOrientation(restorePoint.getOrientation());
                    wnode2.setDir(new AOVector(0.0f, 0.0f, 0.0f));
                }
                final boolean rc = ObjectManagerClient.fixWorldNode(playerOid, wnode2);
                if (!rc) {
                    return false;
                }
                location = restorePoint.getLoc();
            }
            first = false;
            if (restorePoint.getFallbackFlag()) {
                continue;
            }
            restoreStack.remove(size - 1);
        }
    }
    
    protected boolean processLoginHelper(final ClientConnection con, final Player player) {
        final OID playerOid = player.getOid();
        final WorldManagerClient.ObjectInfo objInfo = WorldManagerClient.getObjectInfo(playerOid);
        if (objInfo == null) {
            Log.error("processLogin: Could not get player ObjectInfo oid=" + playerOid);
            return false;
        }
        if (World.FollowsTerrainOverride != null) {
            Log.debug("using follows terrain override");
            objInfo.followsTerrain = World.FollowsTerrainOverride;
        }
        if (Log.loggingDebug) {
            Log.debug("processLogin: got object info: " + objInfo);
        }
        player.setName(objInfo.name);
        final InstanceClient.InstanceInfo instanceInfo = InstanceClient.getInstanceInfo(objInfo.instanceOid, -8193);
        if (instanceInfo == null) {
            Log.error("processLogin: unknown instanceOid=" + objInfo.instanceOid);
            return false;
        }
        final LoginMessage loginMessage = new LoginMessage(playerOid, objInfo.name);
        loginMessage.setInstanceOid(objInfo.instanceOid);
        final AsyncRPCCallback asyncRPCCallback = new AsyncRPCCallback(player, "processLogin: got LoginMessage response");
        final int expectedResponses = Engine.getAgent().sendBroadcastRPC(loginMessage, asyncRPCCallback);
        asyncRPCCallback.waitForResponses(expectedResponses);
        Log.debug("processLogin: sending template (scene) name: " + instanceInfo.templateName);
        final Event worldFileEvent = new WorldFileEvent(instanceInfo.templateName);
        con.send(worldFileEvent.toBytes());
        Log.debug("INFO: process login helper");
        con.send(objInfo.toBuffer(playerOid));
        final DisplayContext dc = WorldManagerClient.getDisplayContext(playerOid);
        final ModelInfoEvent modelInfoEvent = new ModelInfoEvent(playerOid);
        modelInfoEvent.setDisplayContext(dc);
        if (Log.loggingDebug) {
            Log.debug("processLogin: got dc: " + dc);
        }
        con.send(modelInfoEvent.toBytes());
        final Map<String, DisplayContext> childMap = dc.getChildDCMap();
        if (childMap != null && !childMap.isEmpty()) {
            for (final String slot : childMap.keySet()) {
                final DisplayContext attachDC = childMap.get(slot);
                if (attachDC == null) {
                    throw new AORuntimeException("attach DC is null for obj: " + playerOid);
                }
                final OID attacheeOID = attachDC.getObjRef();
                if (attacheeOID == null) {
                    throw new AORuntimeException("attachee oid is null for obj: " + playerOid);
                }
                if (Log.loggingDebug) {
                    Log.debug("processLogin: sending attach message to " + playerOid + " attaching to obj " + playerOid + ", object being attached=" + attacheeOID + " to slot " + slot + ", attachmentDC=" + attachDC);
                }
                final AttachEvent event = new AttachEvent(playerOid, attacheeOID, slot, attachDC);
                con.send(event.toBytes());
            }
            Log.debug("processLogin: done with processing attachments");
        }
        if (this.perceptionFilter.addTarget(playerOid)) {
            final FilterUpdate filterUpdate = new FilterUpdate(1);
            filterUpdate.addFieldValue(1, playerOid);
            Engine.getAgent().applyFilterUpdate(this.perceptionSubId, filterUpdate);
            this.responderFilter.addTarget(playerOid);
            Engine.getAgent().applyFilterUpdate(this.responderSubId, filterUpdate);
        }
        WorldManagerClient.updateObject(playerOid, playerOid);
        final List<String> uiThemes = World.getThemes();
        if (Log.loggingDebug) {
            Log.debug("processLogin: setting themes: " + uiThemes);
        }
        final Event uiThemeEvent = new UITheme(uiThemes);
        con.send(uiThemeEvent.toBytes());
        final WorldManagerClient.TargetedExtensionMessage spawnBegin = new WorldManagerClient.TargetedExtensionMessage(playerOid, playerOid);
        spawnBegin.setExtensionType("ao.SCENE_BEGIN");
        spawnBegin.setProperty("action", "login");
        spawnBegin.setProperty("name", instanceInfo.name);
        spawnBegin.setProperty("templateName", instanceInfo.templateName);
        final WorldManagerClient.TargetedExtensionMessage spawnEnd = new WorldManagerClient.TargetedExtensionMessage(playerOid, playerOid);
        spawnEnd.setExtensionType("ao.SCENE_END");
        spawnEnd.setProperty("action", "login");
        spawnEnd.setProperty("name", instanceInfo.name);
        spawnEnd.setProperty("templateName", instanceInfo.templateName);
        final Integer perceptionCount = WorldManagerClient.spawn(playerOid, spawnBegin, spawnEnd);
        if (perceptionCount < 0) {
            Log.error("processLogin: spawn failed error=" + perceptionCount + " playerOid=" + playerOid);
            return perceptionCount == -2 && false;
        }
        if (perceptionCount == 0 && player.supportsLoadingState()) {
            player.setLoadingState(1);
            con.send(new LoadingStateEvent(false).toBytes());
        }
        if (Log.loggingDebug) {
            Log.debug("processLogin: During login, perceptionCount is " + perceptionCount);
            Log.debug("processLogin: spawned player, master playerOid=" + playerOid);
        }
        if (Log.loggingDebug) {
            Log.debug("processLogin: sending over instance information for player " + playerOid + ", instance=" + instanceInfo.name + ", instanceOid=" + instanceInfo.oid);
        }
        this.updateInstancePerception(player.getOid(), null, instanceInfo.oid, instanceInfo.name);
        return true;
    }
    
    public ProxyLoginCallback getProxyLoginCallback() {
        return this.proxyLoginCallback;
    }
    
    public void setProxyLoginCallback(final ProxyLoginCallback callback) {
        this.proxyLoginCallback = callback;
    }
    
    private boolean instanceEntryAllowed(final OID playerOid, final OID instanceOid, final Point location) {
        return instanceOid != null && this.instanceEntryCallback.instanceEntryAllowed(playerOid, instanceOid, location);
    }
    
    public InstanceEntryCallback getInstanceEntryCallback() {
        return this.instanceEntryCallback;
    }
    
    public void setInstanceEntryCallback(final InstanceEntryCallback callback) {
        this.instanceEntryCallback = callback;
    }
    
    public void processRequestQuestInfo(final ClientConnection con, final RequestQuestInfo event) {
        final OID npcOid = event.getQuestNpcOid();
        final Player player = this.verifyPlayer("processRequestQuestInfo", (Event)event, con);
        if (Log.loggingDebug) {
            Log.debug("processRequestQuestInfo: player=" + player + ", npc=" + npcOid);
        }
        QuestClient.requestQuestInfo(npcOid, player.getOid());
    }
    
    public void processQuestResponse(final ClientConnection con, final QuestResponse event) {
        final Player player = this.verifyPlayer("processQuestResponse", (Event)event, con);
        final boolean acceptStatus = event.getResponse();
        final OID npcOid = event.getQuestNpcOid();
        final OID questOid = event.getQuestId();
        if (Log.loggingDebug) {
            Log.debug("processQuestResponse: player=" + player + ", npcOid=" + npcOid + ", acceptStatus=" + acceptStatus);
        }
        final QuestClient.QuestResponseMessage msg = new QuestClient.QuestResponseMessage(npcOid, player.getOid(), questOid, acceptStatus);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public void processReqConcludeQuest(final ClientConnection con, final ConcludeQuest event) {
        final Player player = this.verifyPlayer("processReqConcludeQuest", (Event)event, con);
        final OID mobOid = event.getQuestNpcOid();
        if (Log.loggingDebug) {
            Log.debug("processReqConclude: player=" + player + ", mobOid=" + mobOid);
        }
        QuestClient.requestConclude(mobOid, player.getOid());
    }
    
    @Override
    public void connectionReset(final ClientConnection con) {
        final Player player = (Player)con.getAssociation();
        if (player == null) {
            Log.info("ProxyPlugin: DISCONNECT remote=" + con);
            return;
        }
        Log.info("ProxyPlugin: DISCONNECT remote=" + con + " playerOid=" + player.getOid() + " name=" + player.getName());
        if (this.playerManager.logout(player)) {
            final ConnectionResetMessage message = new ConnectionResetMessage(con, player);
            message.setEnqueueTime(System.currentTimeMillis());
            this.messageQQ.insert(player, message);
        }
    }
    
    protected void processConnectionResetInternal(final ConnectionResetMessage message) {
        final long startTime = System.currentTimeMillis();
        final ClientConnection con = message.getConnection();
        final Player player = message.getPlayer();
        final OID playerOid = player.getOid();
        Log.info("ProxyPlugin: LOGOUT_BEGIN remote=" + con + " playerOid=" + player.getOid() + " name=" + player.getName());
        if (player.getStatus() != 3) {
            ProxyPlugin.log.error("processConnectionReset: player status is " + Player.statusToString(player.getStatus()) + " should be " + Player.statusToString(3));
        }
        try {
            if (!WorldManagerClient.despawn(playerOid)) {
                ProxyPlugin.log.warn("processConnectionReset: despawn player failed for " + playerOid);
            }
            if (this.perceptionFilter.removeTarget(playerOid)) {
                final FilterUpdate filterUpdate = new FilterUpdate(1);
                filterUpdate.removeFieldValue(1, playerOid);
                Engine.getAgent().applyFilterUpdate(this.perceptionSubId, filterUpdate);
                this.responderFilter.removeTarget(playerOid);
                Engine.getAgent().applyFilterUpdate(this.responderSubId, filterUpdate);
            }
            final LogoutMessage logoutMessage = new LogoutMessage(playerOid, player.getName());
            final AsyncRPCCallback asyncRPCCallback = new AsyncRPCCallback(player, "processLogout: got LogoutMessage response");
            final int expectedResponses = Engine.getAgent().sendBroadcastRPCBlocking(logoutMessage, asyncRPCCallback);
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            Log.debug("Removing connection from client connection map for account: " + accountID);
            this.clientConnections.remove(accountID);
            if (!ObjectManagerClient.unloadObject(playerOid)) {
                ProxyPlugin.log.warn("processConnectionReset: unloadObject failed oid=" + playerOid);
            }
        }
        catch (NoRecipientsException nre) {
            ProxyPlugin.log.exception("ProxyPlugin.processConnectionResetInternal(): ", nre);
        }
        this.messageQQ.removeKey(player);
        this.eventQQ.removeKey(player);
        this.playerManager.removeStaticPerception(player, playerOid);
        this.playerManager.removePlayer(playerOid);
        Log.info("ProxyPlugin: LOGOUT_END remote=" + con + " playerOid=" + player.getOid() + " name=" + player.getName() + " in-queue=" + (startTime - message.getEnqueueTime()) + " processing=" + (System.currentTimeMillis() - startTime) + " nPlayers=" + this.playerManager.getPlayerCount());
        synchronized (player) {
            player.clearConnection();
            player.notifyAll();
        }
    }
    
    protected void processDirLocOrient(final ClientConnection con, final DirLocOrientEvent event) {
        if (Log.loggingDebug) {
            Log.debug("processDirLocOrient: got dir loc orient event: " + event);
        }
        final Player player = this.verifyPlayer("processDirLoc", event, con);
        final BasicWorldNode wnode = new BasicWorldNode();
        wnode.setDir(event.getDir());
        wnode.setLoc(event.getLoc());
        wnode.setOrientation(event.getQuaternion());
        WorldManagerClient.updateWorldNode(player.getOid(), wnode);
    }
    
    protected void processCom(final ClientConnection con, final ComEvent event) {
        final Player player = this.verifyPlayer("processCom", event, con);
        Log.info("ProxyPlugin: CHAT_SENT player=" + player + " channel=" + event.getChannelId() + " msg=[" + event.getMessage() + "]");
        this.incrementChatCount();
        WorldManagerClient.sendChatMsg(player.getOid(), player.getName(), event.getChannelId(), event.getMessage());
    }
    
    protected void processCommand(final ClientConnection con, final CommandEvent event) {
        final Player player = this.verifyPlayer("processCommand", event, con);
        final String cmd = event.getCommand().split(" ")[0];
        if (Log.loggingDebug) {
            ProxyPlugin.log.debug("processCommand: cmd=" + cmd + ", fullCmd=" + event.getCommand());
        }
        this.commandMapLock.lock();
        RegisteredCommand command;
        try {
            command = this.commandMap.get(cmd);
        }
        finally {
            this.commandMapLock.unlock();
        }
        if (command == null) {
            Log.warn("processCommand: no parser for command: " + event.getCommand());
            command = this.commandMap.get("/unknowncmd");
            if (command != null) {
                Engine.setCurrentPlugin(this);
                command.parser.parse(event);
                Engine.setCurrentPlugin(null);
            }
            return;
        }
        Engine.setCurrentPlugin(this);
        if (command.access.allowed(event, this)) {
            command.parser.parse(event);
        }
        else {
            Log.warn("Player " + player + " not allowed to run command '" + event.getCommand() + "'");
        }
        Engine.setCurrentPlugin(null);
    }
    
    protected void processAutoAttack(final ClientConnection con, final AutoAttackEvent event) {
        CombatClient.autoAttack(event.getAttackerOid(), event.getTargetOid(), event.getAttackStatus());
    }
    
    protected void processActivateItem(final ClientConnection con, final ActivateItemEvent event) {
        InventoryClient.activateObject(event.getItemOid(), event.getObjectOid(), event.getTargetOid());
    }
    
    protected void processAbilityStatusEvent(final ClientConnection con, final AbilityStatusEvent event) {
        final Player player = (Player)con.getAssociation();
        final AbilityStatusMessage msg = new AbilityStatusMessage(CombatClient.MSG_TYPE_ABILITY_STATUS, player.getOid(), event.getPropertyMap());
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected void processExtensionMessageEvent(final ClientConnection con, final ExtensionMessageEvent event) {
        final String key = event.getPropertyMap().get("ext_msg_subtype");
        final OID target = event.getTargetOid();
        final Player player = (Player)con.getAssociation();
        if (Log.loggingDebug) {
            Log.debug("processExtensionMessageEvent: " + player + " subType=" + key + " target=" + target);
        }
        final List<ProxyExtensionHook> proxyHookList = this.extensionHooks.get(key);
        if (proxyHookList != null) {
            for (final ProxyExtensionHook hook : proxyHookList) {
                hook.processExtensionEvent(event, player, this);
            }
            return;
        }
        if (target != null) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, target, player.getOid(), event.getClientTargeted(), event.getPropertyMap());
            if (event.getClientTargeted()) {
                msg.setMsgType(WorldManagerClient.MSG_TYPE_P2P_EXTENSION);
                if (this.allowClientToClientMessage(player, target, msg)) {
                    Engine.getAgent().sendBroadcast(msg);
                }
            }
            else {
                final MessageType msgType = this.getExtensionMessageType(key);
                if (msgType == null) {
                    Log.error("processExtensionMessageEvent: key '" + key + "' has no corresponding MessageType");
                    return;
                }
                msg.setMsgType(msgType);
                Engine.getAgent().sendBroadcast(msg);
            }
        }
        else {
            final MessageType msgType2 = this.getExtensionMessageType(key);
            if (msgType2 == null) {
                Log.error("processExtensionMessageEvent: key '" + key + "' has no corresponding MessageType");
                return;
            }
            final WorldManagerClient.ExtensionMessage msg2 = new WorldManagerClient.ExtensionMessage(msgType2, player.getOid(), event.getPropertyMap());
            Engine.getAgent().sendBroadcast(msg2);
        }
    }
    
    public void registerExtensionSubtype(final String subtype, final MessageType type) {
        this.extensionMessageRegistry.put(subtype, type);
    }
    
    public MessageType unregisterExtensionSubtype(final String subtype) {
        return this.extensionMessageRegistry.remove(subtype);
    }
    
    public MessageType getExtensionMessageType(final String subtype) {
        return this.extensionMessageRegistry.get(subtype);
    }
    
    public boolean allowClientToClientMessage(final Player sender, final OID targetOid, final WorldManagerClient.TargetedExtensionMessage message) {
        return this.defaultAllowClientToClientMessage;
    }
    
    protected boolean specialCaseNewProcessing(final PerceptionMessage.ObjectNote objectNote, final Player player) {
        final long start = System.currentTimeMillis();
        final ClientConnection con = player.getConnection();
        final OID objOid = objectNote.getSubject();
        final ObjectType objType = objectNote.getObjectType();
        if (objType == ObjectTypes.light) {
            Log.debug("specialCaseNewProcessing: got a light object");
            final LightData lightData = (LightData)EnginePlugin.getObjectProperty(objOid, Namespace.WORLD_MANAGER, Light.LightDataPropertyKey);
            if (Log.loggingDebug) {
                Log.debug("specialCaseNewProcessing: light data=" + lightData);
            }
            final NewLightEvent lightEvent = new NewLightEvent(player.getOid(), objOid, lightData);
            con.send(lightEvent.toBytes());
            return true;
        }
        if (objType.equals(WorldManagerClient.TEMPL_OBJECT_TYPE_TERRAIN_DECAL)) {
            Log.debug("specialCaseNewProcessing: got a terrain decal object");
            final TerrainDecalData decalData = (TerrainDecalData)EnginePlugin.getObjectProperty(objOid, Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_TERRAIN_DECAL_DATA);
            if (Log.loggingDebug) {
                Log.debug("specialCaseNewProcessing: terrain decal data=" + decalData);
            }
            final NewTerrainDecalEvent decalEvent = new NewTerrainDecalEvent(objOid, decalData);
            con.send(decalEvent.toBytes());
            return true;
        }
        if (objType.equals(WorldManagerClient.TEMPL_OBJECT_TYPE_POINT_SOUND)) {
            Log.debug("specialCaseNewProcessing: got a point sound object");
            final List<SoundData> soundData = (List<SoundData>)EnginePlugin.getObjectProperty(objOid, Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SOUND_DATA_LIST);
            if (Log.loggingDebug) {
                Log.debug("specialCaseNewProcessing: sound data=" + soundData);
            }
            final WorldManagerClient.SoundMessage soundMsg = new WorldManagerClient.SoundMessage(objOid);
            soundMsg.setSoundData(soundData);
            con.send(soundMsg.toBuffer());
            return true;
        }
        final WorldManagerClient.PerceptionInfo perceptionInfo = (WorldManagerClient.PerceptionInfo)objectNote.getObjectInfo();
        if (perceptionInfo.objectInfo == null) {
            return true;
        }
        final WorldManagerClient.MobPathMessage pathMsg = (WorldManagerClient.MobPathMessage)perceptionInfo.objectInfo.getProperty(WorldManagerClient.MOB_PATH_PROPERTY);
        Log.debug("INFO: special case with player/npc: " + perceptionInfo.objectInfo.name);
        con.send(perceptionInfo.objectInfo.toBuffer(player.getOid()));
        if (perceptionInfo.displayContext != null) {
            final ModelInfoEvent modelInfoEvent = new ModelInfoEvent(objOid);
            modelInfoEvent.setDisplayContext(perceptionInfo.displayContext);
            con.send(modelInfoEvent.toBytes());
        }
        else if (Log.loggingDebug) {
            Log.debug("No display context for " + objOid);
        }
        if (pathMsg != null) {
            if (pathMsg.pathExpired()) {
                if (Log.loggingDebug) {
                    Log.debug("specialCaseNewProcessing: for mob " + objOid + ", last mob path expired " + pathMsg.toString());
                }
            }
            else {
                if (Log.loggingDebug) {
                    Log.debug("specialCaseNewProcessing: for mob " + objOid + ", sending last mob path " + pathMsg.toString());
                }
                final AOByteBuffer pathBuf = pathMsg.toBuffer();
                con.send(pathBuf);
            }
        }
        Map<String, DisplayContext> childMap = null;
        if (perceptionInfo.displayContext != null) {
            childMap = perceptionInfo.displayContext.getChildDCMap();
        }
        if (childMap != null && !childMap.isEmpty()) {
            for (final String slot : childMap.keySet()) {
                final DisplayContext attachDC = childMap.get(slot);
                if (attachDC == null) {
                    throw new AORuntimeException("attach DC is null for obj: " + objOid);
                }
                final OID attacheeOID = attachDC.getObjRef();
                if (attacheeOID == null) {
                    throw new AORuntimeException("attachee oid is null for obj: " + objOid);
                }
                if (Log.loggingDebug) {
                    Log.debug("specialCaseNewProcessing: sending attach message to " + player.getOid() + " attaching to obj " + objOid + ", object being attached=" + attacheeOID + " to slot " + slot + ", attachmentDC=" + attachDC);
                }
                final AttachEvent event = new AttachEvent(objOid, attacheeOID, slot, attachDC);
                con.send(event.toBytes());
            }
            if (Log.loggingDebug) {
                Log.debug("specialCaseNewProcessing: done with processing attachments");
            }
        }
        final long finish = System.currentTimeMillis();
        if (Log.loggingDebug) {
            Log.debug("specialCaseNewProcessing: finished.\tplayerOid=" + player.getOid() + ", oid=" + objOid + " in " + (finish - start) + " ms");
        }
        return false;
    }
    
    protected boolean specialCaseFreeProcessing(final PerceptionMessage.ObjectNote objectNote, final Player player) {
        if (player.getOid().equals(objectNote.getSubject())) {
            Log.debug("ignoring free object message to self");
            return true;
        }
        ClientConnection con = player.getConnection();
        if (!con.isOpen()) {
            con = null;
        }
        final OID objOid = objectNote.getSubject();
        if (objectNote.getObjectType() == ObjectTypes.road) {
            if (Log.loggingDebug) {
                Log.debug("specialCaseFreeProcessing: playerOid=" + player.getOid() + ", roadSegmentOid=" + objOid);
            }
            this.handleFreeRoad(con, objOid);
            return true;
        }
        if (objectNote.getObjectType().equals(WorldManagerClient.TEMPL_OBJECT_TYPE_TERRAIN_DECAL)) {
            if (Log.loggingDebug) {
                Log.debug("specialCaseFreeProcessing: playerOid=" + player.getOid() + ", decalOid=" + objOid);
            }
            final FreeTerrainDecalEvent decalEvent = new FreeTerrainDecalEvent(objOid);
            if (con != null) {
                con.send(decalEvent.toBytes());
            }
            return true;
        }
        if (Log.loggingDebug) {
            Log.debug("specialCaseFreeProcessing: playerOid=" + player.getOid() + ", objOid=" + objOid);
        }
        final NotifyFreeObjectEvent freeEvent = new NotifyFreeObjectEvent(player.getOid(), objOid);
        if (con != null) {
            con.send(freeEvent.toBytes());
        }
        return false;
    }
    
    protected void handleFreeRoad(final ClientConnection con, final OID objOid) {
        final WorldManagerClient.FreeRoadMessage freeRoadMsg = new WorldManagerClient.FreeRoadMessage(objOid);
        final AOByteBuffer buf = freeRoadMsg.toBuffer();
        if (con != null) {
            con.send(buf);
        }
    }
    
    public static void addStaticPerception(final OID playerOid, final OID oid) {
        addStaticPerception(playerOid, oid, null, null, false);
    }
    
    public static void addStaticPerception(final OID oid, final OID oid2, final String name, final ObjectType type) {
        addStaticPerception(oid, oid2, name, type, true);
    }
    
    private static void addStaticPerception(final OID playerOid, final OID oid, final String name, final ObjectType type, final boolean hasObjectInfo) {
        final AddStaticPerceptionMessage message = new AddStaticPerceptionMessage(ProxyPlugin.MSG_TYPE_ADD_STATIC_PERCEPTION);
        message.setTarget(playerOid);
        message.setSubject(oid);
        message.setName(name);
        message.setType(type);
        message.setHasObjectInfo(hasObjectInfo);
        Engine.getAgent().sendBroadcast(message);
    }
    
    public static void removeStaticPerception(final OID playerOid, final OID oid) {
        final TargetMessage message = new TargetMessage(ProxyPlugin.MSG_TYPE_REMOVE_STATIC_PERCEPTION);
        message.setTarget(playerOid);
        message.setSubject(oid);
        Engine.getAgent().sendBroadcast(message);
    }
    
    protected void processPlayerIgnoreList(final Player player) {
        if (player.getStatus() == 3) {
            Log.error("ProxyPlugin.processPlayerIgnoreList: Aborting... player.getStatus() is STATUS_LOGOUT");
            return;
        }
        this.sendPlayerIgnoreList(player);
    }
    
    protected void sendPlayerIgnoreList(final Player player) {
        final String missing = " Missing ";
    }
    
    public void updateIgnoredOids(final Player player, final List<OID> nowIgnored, final List<OID> noLongerIgnored) {
    }
    
    public List<Object> matchingPlayers(final Player player, final String playerName, final Boolean exactMatch) {
        final boolean match = exactMatch == null || exactMatch;
        final List<Object> matchLists = Engine.getDatabase().getOidsAndNamesMatchingName(playerName, match);
        final List<OID> oids = matchLists.get(0);
        final List<String> names = matchLists.get(1);
        if (Log.loggingDebug) {
            ProxyPlugin.log.debug("ProxyPlugin.matchingPlayers: For player " + player.getOid() + ", found " + ((oids == null) ? 0 : oids.size()) + " players: " + Database.makeOidCollectionString(oids) + " " + (match ? "exactly matching" : "starting with") + " name '" + playerName + "':" + Database.makeNameCollectionString(names));
        }
        return matchLists;
    }
    
    public static OID handleFullInstance(final String instanceTemplateName, InstanceClient.InstanceInfo instanceInfo) {
        Log.debug("POP: instance full with template: " + instanceTemplateName);
        int instanceNum = 1;
        String instanceName = "";
        OID instanceOid;
        while (true) {
            instanceName = instanceTemplateName + "_" + instanceNum;
            instanceOid = InstanceClient.getInstanceOid(instanceName);
            if (instanceOid != null) {
                instanceInfo = InstanceClient.getInstanceInfo(instanceOid, -8193);
                if (instanceInfo.populationLimit < 1) {
                    break;
                }
                if (instanceInfo.playerPopulation < instanceInfo.populationLimit) {
                    break;
                }
            }
            else {
                final Template overrideTemplate = new Template();
                overrideTemplate.put(Namespace.INSTANCE, "name", instanceName);
                instanceOid = InstanceClient.createInstance(instanceTemplateName, overrideTemplate);
                if (instanceOid != null) {
                    break;
                }
            }
            ++instanceNum;
        }
        return instanceOid;
    }
    
    private void updateInstancePerception(final OID playerOid, final OID prevInstanceOid, final OID destInstanceOid, final String destInstanceName) {
        if (prevInstanceOid != null) {
            removeStaticPerception(playerOid, prevInstanceOid);
        }
        addStaticPerception(playerOid, destInstanceOid, destInstanceName, ObjectTypes.instance);
    }
    
    protected void pushInstanceRestorePoint(final Player player, final BasicWorldNode loc) {
        final OID playerOid = player.getOid();
        final InstanceRestorePoint restorePoint = new InstanceRestorePoint();
        restorePoint.setInstanceOid(loc.getInstanceOid());
        restorePoint.setLoc(loc.getLoc());
        restorePoint.setOrientation(loc.getOrientation());
        final InstanceClient.InstanceInfo instanceInfo = InstanceClient.getInstanceInfo(loc.getInstanceOid(), 2);
        restorePoint.setInstanceName(instanceInfo.name);
        LinkedList<Object> restoreStack = (LinkedList<Object>)EnginePlugin.getObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "instanceStack");
        if (restoreStack == null) {
            restoreStack = new LinkedList<Object>();
        }
        restoreStack.add(restorePoint);
        EnginePlugin.setObjectProperty(playerOid, Namespace.OBJECT_MANAGER, "instanceStack", restoreStack);
    }
    
    protected void sendOceanData(final OceanData oceanData, final Player player) {
        final WorldManagerClient.TargetedExtensionMessage oceanMsg = new ClientParameter.ClientParameterMessage(player.getOid());
        oceanMsg.setProperty("Ocean.DisplayOcean", oceanData.displayOcean.toString());
        if (oceanData.useParams != null) {
            oceanMsg.setProperty("Ocean.UseParams", oceanData.useParams.toString());
        }
        if (oceanData.waveHeight != null) {
            oceanMsg.setProperty("Ocean.WaveHeight", oceanData.waveHeight.toString());
        }
        if (oceanData.seaLevel != null) {
            oceanMsg.setProperty("Ocean.SeaLevel", oceanData.seaLevel.toString());
        }
        if (oceanData.bumpScale != null) {
            oceanMsg.setProperty("Ocean.BumpScale", oceanData.bumpScale.toString());
        }
        if (oceanData.bumpSpeedX != null) {
            oceanMsg.setProperty("Ocean.BumpSpeedX", oceanData.bumpSpeedX.toString());
        }
        if (oceanData.bumpSpeedZ != null) {
            oceanMsg.setProperty("Ocean.BumpSpeedZ", oceanData.bumpSpeedZ.toString());
        }
        if (oceanData.textureScaleX != null) {
            oceanMsg.setProperty("Ocean.TextureScaleX", oceanData.textureScaleX.toString());
        }
        if (oceanData.textureScaleZ != null) {
            oceanMsg.setProperty("Ocean.TextureScaleZ", oceanData.textureScaleZ.toString());
        }
        if (oceanData.deepColor != null) {
            oceanMsg.setProperty("Ocean.DeepColor", oceanData.deepColor.toString());
        }
        if (oceanData.shallowColor != null) {
            oceanMsg.setProperty("Ocean.ShallowColor", oceanData.shallowColor.toString());
        }
        player.getConnection().send(oceanMsg.toBuffer(player.getVersion()));
    }
    
    protected Player verifyPlayer(final String context, final Event event, final ClientConnection con) {
        final Player player = (Player)con.getAssociation();
        if (!player.getOid().equals(event.getObjectOid())) {
            throw new AORuntimeException(context + ": con doesn't match player " + player + " against eventOid " + event.getObjectOid());
        }
        return player;
    }
    
    public void incrementChatCount() {
        ++this.chatSentCount;
    }
    
    public void incrementPrivateChatCount() {
        ++this.privateChatSentCount;
    }
    
    public void addAdmin(final OID oid) {
        if (Log.loggingDebug) {
            ProxyPlugin.log.debug("ProxyPlugin.addAdmin: adding oid " + oid);
        }
        this.lock.lock();
        try {
            this.adminSet.add(oid);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<OID> getAdmins() {
        this.lock.lock();
        try {
            return new HashSet<OID>(this.adminSet);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean isAdmin(final OID playerOid) {
        this.lock.lock();
        try {
            return playerOid != null && this.adminSet.contains(playerOid);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    protected Object createMBeanInstance() {
        return new ProxyJMX();
    }
    
    static {
        loginSerializer = new Player(null, null);
        log = new Logger("ProxyPlugin");
        MSG_TYPE_VOICE_PARMS = MessageType.intern("ao.VOICE_PARMS");
        MSG_TYPE_PLAYER_PATH_REQ = MessageType.intern("ao.PLAYER_PATH_REQ");
        MSG_TYPE_UPDATE_PLAYER_IGNORE_LIST = MessageType.intern("ao.UPDATE_PLAYER_IGNORE_LIST");
        MSG_TYPE_GET_MATCHING_PLAYERS = MessageType.intern("ao.GET_MATCHING_PLAYERS");
        MSG_TYPE_PLAYER_IGNORE_LIST = MessageType.intern("ao.PLAYER_IGNORE_LIST");
        MSG_TYPE_PLAYER_IGNORE_LIST_REQ = MessageType.intern("ao.PLAYER_IGNORE_LIST_REQ");
        MSG_TYPE_RELAY_UPDATE_PLAYER_IGNORE_LIST = MessageType.intern("ao.RELAY_UPDATE_PLAYER_IGNORE_LIST");
        MSG_TYPE_GET_PLAYER_LOGIN_STATUS = MessageType.intern("ao.GET_PLAYER_LOGIN_STATUS");
        MSG_TYPE_LOGOUT_PLAYER = MessageType.intern("ao.LOGOUT_PLAYER");
        MSG_TYPE_ADD_STATIC_PERCEPTION = MessageType.intern("ao.ADD_STATIC_PERCEPTION");
        MSG_TYPE_REMOVE_STATIC_PERCEPTION = MessageType.intern("ao.REMOVE_STATIC_PERCEPTION");
        MSG_TYPE_LOGIN_SPAWNED = MessageType.intern("ao.LOGIN_SPAWNED");
        MSG_TYPE_ACCOUNT_LOGIN = MessageType.intern("ao.ACCOUNT_LOGIN");
        ProxyPlugin.voiceServerHost = "";
        ProxyPlugin.voiceServerPort = null;
        ProxyPlugin.serverSocketReceiveBufferSize = 131072;
        ProxyPlugin.MaxConcurrentUsers = 1000;
        ProxyPlugin.idleTimeout = 900;
        ProxyPlugin.silenceTimeout = 60;
        ProxyPlugin.maxMessagesBeforeConnectionReset = 15000;
        ProxyPlugin.maxByteCountBeforeConnectionReset = 2000000;
    }
    
    class ReceivedMessage implements Runnable
    {
        Message message;
        int flags;
        
        ReceivedMessage(final Message message, final int flags) {
            this.message = message;
            this.flags = flags;
        }
        
        @Override
        public void run() {
            ProxyPlugin.this.callEngineOnMessage(this.message, this.flags);
        }
    }
    
    public class PluginMessageCallback implements MessageCallback
    {
        ExecutorService executor;
        
        public PluginMessageCallback() {
            this.executor = Executors.newSingleThreadExecutor();
        }
        
        @Override
        public void handleMessage(final Message message, final int flags) {
            this.executor.execute(new ReceivedMessage(message, flags));
        }
    }
    
    public class PlayerMessageCallback implements MessageCallback
    {
        @Override
        public void handleMessage(final Message message, final int flags) {
            if (message instanceof TargetMessage) {
                if (message.getMsgType() == WorldManagerClient.MSG_TYPE_TARGETED_PROPERTY) {
                    ProxyPlugin.this.countMsgTargetedProperty.add();
                }
                final OID playerOid = ((TargetMessage)message).getTarget();
                final Player player = ProxyPlugin.this.playerManager.getPlayer(playerOid);
                if (player == null) {
                    Log.debug("TargetMessage: player " + playerOid + " not found");
                    if (message.isRPC()) {
                        if (message.getMsgType() == InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ) {
                            Engine.getAgent().sendBooleanResponse(message, false);
                        }
                        else if (message.getMsgType() == ProxyPlugin.MSG_TYPE_PLAYER_IGNORE_LIST_REQ) {
                            Engine.getAgent().sendObjectResponse(message, null);
                        }
                        else if (message.getMsgType() == ProxyPlugin.MSG_TYPE_GET_PLAYER_LOGIN_STATUS) {
                            Engine.getAgent().sendObjectResponse(message, null);
                        }
                        else {
                            if (message.getMsgType() != ProxyPlugin.MSG_TYPE_LOGOUT_PLAYER) {
                                throw new RuntimeException("Unexpected RPC message " + message + " for player " + player);
                            }
                            Engine.getAgent().sendObjectResponse(message, null);
                        }
                    }
                }
                else {
                    message.setEnqueueTime();
                    ProxyPlugin.this.messageQQ.insert(player, message);
                }
                return;
            }
            if (message instanceof SubjectMessage) {
                final List<Player> perceivers = ProxyPlugin.this.playerManager.getPerceivers(((SubjectMessage)message).getSubject());
                if (perceivers == null) {
                    Log.warn("No perceivers for " + message);
                    return;
                }
                if (message instanceof WorldManagerClient.UpdateWorldNodeMessage) {
                    final WorldManagerClient.UpdateWorldNodeMessage wMsg = (WorldManagerClient.UpdateWorldNodeMessage)message;
                    final DirLocOrientEvent dloEvent = new DirLocOrientEvent(wMsg.getSubject(), wMsg.getWorldNode());
                    wMsg.setEventBuf(dloEvent.toBytes());
                }
                if (message.getMsgType() == WorldManagerClient.MSG_TYPE_UPDATEWNODE) {
                    ProxyPlugin.this.countMsgUpdateWNodeIn.add();
                    ProxyPlugin.this.countMsgUpdateWNodeOut.add(perceivers.size());
                }
                else if (message.getMsgType() == PropertyMessage.MSG_TYPE_PROPERTY) {
                    ProxyPlugin.this.countMsgPropertyIn.add();
                    ProxyPlugin.this.countMsgPropertyOut.add(perceivers.size());
                }
                else if (message.getMsgType() == WorldManagerClient.MSG_TYPE_WNODECORRECT) {
                    ProxyPlugin.this.countMsgWNodeCorrectIn.add();
                    ProxyPlugin.this.countMsgWNodeCorrectOut.add(perceivers.size());
                }
                else if (message.getMsgType() == WorldManagerClient.MSG_TYPE_MOB_PATH) {
                    ProxyPlugin.this.countMsgMobPathIn.add();
                    ProxyPlugin.this.countMsgMobPathOut.add(perceivers.size());
                }
                message.setEnqueueTime();
                ProxyPlugin.this.messageQQ.insert(perceivers, message);
            }
            else {
                if (message instanceof PerceptionMessage) {
                    final PerceptionMessage pMsg = (PerceptionMessage)message;
                    ProxyPlugin.this.countMsgPerception.add();
                    ProxyPlugin.this.countMsgPerceptionGain.add(pMsg.getGainObjectCount());
                    ProxyPlugin.this.countMsgPerceptionLost.add(pMsg.getLostObjectCount());
                    final OID playerOid2 = pMsg.getTarget();
                    Log.debug("PERCEP: got perception message with player: " + playerOid2);
                    final Player player2 = ProxyPlugin.this.playerManager.getPlayer(playerOid2);
                    if (player2 == null) {
                        Log.debug("PerceptionMessage: player " + playerOid2 + " not found");
                    }
                    else {
                        message.setEnqueueTime();
                        ProxyPlugin.this.messageQQ.insert(player2, message);
                        Log.debug("PERCEP: added perception message to messageQQ: ");
                    }
                    return;
                }
                Log.error("PlayerMessageCallback unknown type=" + message.getMsgType());
            }
        }
    }
    
    private static class RegisteredCommand
    {
        public CommandParser parser;
        public CommandAccessCheck access;
        
        public RegisteredCommand(final CommandParser p, final CommandAccessCheck a) {
            this.parser = p;
            this.access = a;
        }
    }
    
    private static class DefaultCommandAccess implements CommandAccessCheck
    {
        @Override
        public boolean allowed(final CommandEvent event, final ProxyPlugin proxy) {
            return true;
        }
    }
    
    class MatchedMessage
    {
        final Long sub;
        final Message message;
        final long enqueueTime;
        
        MatchedMessage(final Long sub, final Message message) {
            this.sub = sub;
            this.message = message;
            this.enqueueTime = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return "MatchedMessage[subId=" + this.sub + ", enqueueTime=" + this.enqueueTime + ",msg=" + this.message;
        }
    }
    
    class MessageCallback implements SQCallback
    {
        protected ProxyPlugin proxyPlugin;
        
        public MessageCallback(final ProxyPlugin proxyPlugin) {
            this.proxyPlugin = proxyPlugin;
        }
        
        @Override
        public void doWork(final Object value, final Object key) {
            final Message message = (Message)value;
            final Player player = (Player)key;
            if (message == null) {
                Log.dumpStack("DOMESSAGE: Message for oid=" + player.getOid() + " is not a Message: " + value);
                return;
            }
            if (message instanceof ConnectionResetMessage) {
                if (player == ProxyPlugin.loginSerializer) {
                    ProxyPlugin.this.processConnectionResetInternal((ConnectionResetMessage)message);
                }
                else {
                    ProxyPlugin.this.messageQQ.insert(ProxyPlugin.loginSerializer, message);
                }
                return;
            }
            final int status = player.getStatus();
            if (status == 3 || status == 0) {
                Log.debug("Ignoring message: id=" + message.getMsgId() + " type=" + message.getMsgType() + " for " + player);
                if (message.isRPC()) {
                    if (message.getMsgType() == InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ) {
                        Engine.getAgent().sendBooleanResponse(message, false);
                    }
                    else if (message.getMsgType() == ProxyPlugin.MSG_TYPE_PLAYER_IGNORE_LIST_REQ) {
                        Engine.getAgent().sendObjectResponse(message, null);
                    }
                    else if (message.getMsgType() == ProxyPlugin.MSG_TYPE_GET_PLAYER_LOGIN_STATUS) {
                        Engine.getAgent().sendObjectResponse(message, null);
                    }
                    else {
                        if (message.getMsgType() != ProxyPlugin.MSG_TYPE_LOGOUT_PLAYER) {
                            throw new RuntimeException("Unexpected RPC message " + message + " for player " + player);
                        }
                        Engine.getAgent().sendObjectResponse(message, null);
                    }
                }
                return;
            }
            try {
                long inQueue = 0L;
                if (Log.loggingDebug) {
                    inQueue = System.nanoTime() - message.getEnqueueTime();
                    Log.debug("DOINGSVRMESSAGE: Message for oid=" + player.getOid() + ",msgId=" + message.getMsgId() + ",in-queue=" + inQueue / 1000L + " usec: " + message.getMsgType());
                }
                if (Log.loggingInfo && ProxyPlugin.this.proxyQueueHistogram != null) {
                    ProxyPlugin.this.proxyQueueHistogram.addTime(inQueue);
                }
                final List<Hook> hooks = ProxyPlugin.this.getHookManager().getHooks(message.getMsgType());
                final long callbackStart = System.nanoTime();
                for (final Hook hook : hooks) {
                    ((ProxyHook)hook).processMessage(message, 0, player);
                }
                long callbackTime = 0L;
                if (Log.loggingDebug || Log.loggingInfo) {
                    callbackTime = System.nanoTime() - callbackStart;
                }
                if (Log.loggingDebug) {
                    Log.debug("DONESVRMESSAGE: Message for oid=" + player.getOid() + ",msgId=" + message.getMsgId() + ",in-queue=" + inQueue / 1000L + " usec: " + ",execute=" + callbackTime / 1000L + " usec: " + message.getMsgType());
                }
                if (Log.loggingInfo && ProxyPlugin.this.proxyCallbackHistogram != null) {
                    ProxyPlugin.this.proxyCallbackHistogram.addTime(callbackTime);
                }
            }
            catch (Exception ex) {
                Log.exception("SQ MessageCallback", ex);
            }
        }
    }
    
    public class EventCallback implements SQCallback
    {
        @Override
        public void doWork(final Object value, final Object key) {
            final Event event = (Event)value;
            if (event == null) {
                Log.dumpStack("EventCallback.doWork: event object is null, for key " + key);
                return;
            }
            final ClientConnection con = event.getConnection();
            final Player player = (Player)key;
            try {
                final long startTime = System.currentTimeMillis();
                final long inQueue = startTime - event.getEnqueueTime();
                if (player == ProxyPlugin.loginSerializer && event instanceof AuthorizedLoginEvent) {
                    final AuthorizedLoginEvent loginEvent = (AuthorizedLoginEvent)event;
                    final OID playerOid = loginEvent.getOid();
                    Log.info("ProxyPlugin: LOGIN_BEGIN remote=" + con + " playerOid=" + playerOid + " in-queue=" + inQueue + " ms");
                    final boolean loginOK = ProxyPlugin.this.processLogin(con, loginEvent);
                    final Player newPlayer = ProxyPlugin.this.playerManager.getPlayer(playerOid);
                    String playerName = null;
                    if (newPlayer != null) {
                        playerName = newPlayer.getName();
                    }
                    Log.info("ProxyPlugin: LOGIN_END remote=" + con + (loginOK ? " SUCCESS " : " FAILURE ") + " playerOid=" + playerOid + " name=" + playerName + " in-queue=" + inQueue + " ms" + " processing=" + (System.currentTimeMillis() - startTime) + " ms" + " nPlayers=" + ProxyPlugin.this.playerManager.getPlayerCount());
                    return;
                }
                if (player == ProxyPlugin.loginSerializer) {
                    Log.error("ClientEvent: Illegal event for loginSerializer: " + event.getClass().getName() + ", con=" + con);
                    return;
                }
                if (Log.loggingDebug) {
                    Log.debug("ClientEvent: player=" + player + ", in-queue=" + inQueue + " ms: " + event.getName());
                }
                if (Log.loggingInfo && inQueue > 2000L) {
                    Log.info("LONG IN-QUEUE: " + inQueue + " ms: player=" + player + " " + event.getName());
                }
                final Lock objLock = ProxyPlugin.this.getObjectLockManager().getLock(player.getOid());
                objLock.lock();
                try {
                    if (Log.loggingDebug) {
                        Log.debug("ClientEvent: event detail: " + event);
                    }
                    if (event instanceof ComEvent) {
                        ProxyPlugin.this.processCom(con, (ComEvent)event);
                    }
                    else if (event instanceof DirLocOrientEvent) {
                        ProxyPlugin.this.processDirLocOrient(con, (DirLocOrientEvent)event);
                    }
                    else if (event instanceof CommandEvent) {
                        ProxyPlugin.this.processCommand(con, (CommandEvent)event);
                    }
                    else if (event instanceof AutoAttackEvent) {
                        ProxyPlugin.this.processAutoAttack(con, (AutoAttackEvent)event);
                    }
                    else if (event instanceof ExtensionMessageEvent) {
                        ProxyPlugin.this.processExtensionMessageEvent(con, (ExtensionMessageEvent)event);
                    }
                    else if (event instanceof AbilityStatusEvent) {
                        ProxyPlugin.this.processAbilityStatusEvent(con, (AbilityStatusEvent)event);
                    }
                    else if (event instanceof RequestQuestInfo) {
                        ProxyPlugin.this.processRequestQuestInfo(con, (RequestQuestInfo)event);
                    }
                    else if (event instanceof QuestResponse) {
                        ProxyPlugin.this.processQuestResponse(con, (QuestResponse)event);
                    }
                    else if (event instanceof ConcludeQuest) {
                        ProxyPlugin.this.processReqConcludeQuest(con, (ConcludeQuest)event);
                    }
                    else {
                        if (!(event instanceof ActivateItemEvent)) {
                            throw new RuntimeException("Unknown event: " + event);
                        }
                        ProxyPlugin.this.processActivateItem(con, (ActivateItemEvent)event);
                    }
                    final long elapsed = System.currentTimeMillis() - startTime;
                    if (Log.loggingDebug) {
                        Log.debug("ClientEvent: processed event " + event + ", player=" + player + ", processing=" + elapsed + " ms");
                        ProxyPlugin.this.clientMsgMeter.add(elapsed);
                    }
                    if (elapsed > 2000L) {
                        Log.info("LONG PROCESS: " + elapsed + " ms: player=" + player + " " + event.getName());
                    }
                }
                finally {
                    objLock.unlock();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("ProxyPlugin.EventCallback", e);
            }
        }
    }
    
    private static class DefaultProxyLoginCallback implements ProxyLoginCallback
    {
        @Override
        public boolean duplicateLogin(final PlayerLoginStatus existingLogin, final ClientConnection con) {
            return existingLogin != null;
        }
        
        @Override
        public String preLoad(final Player player, final ClientConnection con) {
            return null;
        }
        
        @Override
        public String postLoad(final Player player, final ClientConnection con) {
            return null;
        }
        
        @Override
        public void postSpawn(final Player player, final ClientConnection con) {
        }
    }
    
    private static class DefaultInstanceEntryCallback implements InstanceEntryCallback
    {
        @Override
        public boolean instanceEntryAllowed(final OID playerOid, final OID instanceOid, final Point location) {
            return true;
        }
        
        @Override
        public OID selectInstance(final Player player, final String instanceName) {
            return InstanceClient.getInstanceOid(instanceName);
        }
    }
    
    class ConnectionResetMessage extends Message
    {
        ClientConnection con;
        Player player;
        public static final long serialVersionUID = 1L;
        
        ConnectionResetMessage(final ClientConnection con, final Player player) {
            this.con = con;
            this.player = player;
        }
        
        public Player getPlayer() {
            return this.player;
        }
        
        public ClientConnection getConnection() {
            return this.con;
        }
    }
    
    abstract class BasicProxyHook implements ProxyHook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            return true;
        }
        
        @Override
        public abstract void processMessage(final Message p0, final int p1, final Player p2);
    }
    
    class DisplayContextHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.DisplayContextMessage dcMsg = (WorldManagerClient.DisplayContextMessage)msg;
            final OID dcObjOid = dcMsg.getSubject();
            final DisplayContext dc = dcMsg.getDisplayContext();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("handleDC: oid=" + dcObjOid + " dc=" + dc);
            }
            final ClientConnection con = player.getConnection();
            if (dc != null) {
                final ModelInfoEvent event = new ModelInfoEvent(dcObjOid);
                event.setDisplayContext(dc);
                event.setForceInstantLoad(dcMsg.getForceInstantLoad());
                con.send(event.toBytes());
            }
            final Map<String, DisplayContext> childMap = dc.getChildDCMap();
            if (childMap != null && !childMap.isEmpty()) {
                for (final String slot : childMap.keySet()) {
                    final DisplayContext attachDC = childMap.get(slot);
                    if (attachDC == null) {
                        throw new AORuntimeException("attach DC is null for obj: " + dcObjOid);
                    }
                    final OID attacheeOID = attachDC.getObjRef();
                    if (attacheeOID == null) {
                        throw new AORuntimeException("attachee oid is null for obj: " + dcObjOid);
                    }
                    if (Log.loggingDebug) {
                        ProxyPlugin.log.debug("DisplayContextHook: sending attach message to " + player.getOid() + " attaching to obj " + dcObjOid + ", object being attached=" + attacheeOID + " to slot " + slot + ", attachmentDC=" + attachDC);
                    }
                    final AttachEvent event2 = new AttachEvent(dcObjOid, attacheeOID, slot, attachDC);
                    con.send(event2.toBytes());
                }
                ProxyPlugin.log.debug("DisplayContextHook: done with processing attachments");
            }
        }
    }
    
    class NewDirLightHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message m, final int flags, final Player player) {
            final WorldManagerClient.NewDirLightMessage msg = (WorldManagerClient.NewDirLightMessage)m;
            final OID playerOid = msg.getTarget();
            final OID lightOid = msg.getSubject();
            final LightData lightData = msg.getLightData();
            if (!playerOid.equals(player.getOid())) {
                Log.error("Message target and perceiver mismatch");
            }
            final ClientConnection con = player.getConnection();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("NewDirLightHook: notifyOid=" + playerOid + ", lightOid=" + lightOid + ", light=" + lightData);
            }
            final NewLightEvent lightEvent = new NewLightEvent(playerOid, lightOid, lightData);
            con.send(lightEvent.toBytes());
        }
    }
    
    class FreeObjectHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.FreeObjectMessage message = (WorldManagerClient.FreeObjectMessage)msg;
            player.getConnection().send(message.toBuffer());
        }
    }
    
    class SetAmbientHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message m, final int flags, final Player player) {
            final WorldManagerClient.SetAmbientLightMessage msg = (WorldManagerClient.SetAmbientLightMessage)m;
            final Color ambientLight = msg.getColor();
            final OID playerOid = msg.getTarget();
            if (!playerOid.equals(player.getOid())) {
                Log.error("Message target and perceiver mismatch");
            }
            final ClientConnection con = player.getConnection();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("SetAmbientHook: targetOid=" + playerOid + ", ambient=" + ambientLight);
            }
            final Event ambientLightEvent = new AmbientLightEvent(ambientLight);
            con.send(ambientLightEvent.toBytes());
        }
    }
    
    class DetachHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.DetachMessage dMsg = (WorldManagerClient.DetachMessage)msg;
            final OID dcObjOid = dMsg.getSubject();
            final OID objBeingDetached = dMsg.getObjBeingDetached();
            final String socket = dMsg.getSocketName();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("DetachHook: dcObjOid=" + dcObjOid + ", objBeingDetached=" + objBeingDetached + ", socket=" + socket);
            }
            final ClientConnection con = player.getConnection();
            final DetachEvent detachEvent = new DetachEvent(dcObjOid, objBeingDetached, socket);
            con.send(detachEvent.toBytes());
        }
    }
    
    class AnimationHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.AnimationMessage animMsg = (WorldManagerClient.AnimationMessage)msg;
            final OID playerOid = player.getOid();
            final ClientConnection con = player.getConnection();
            final OID objOid = animMsg.getSubject();
            final List<AnimationCommand> animList = animMsg.getAnimationList();
            final NotifyPlayAnimationEvent animEvent = new NotifyPlayAnimationEvent(objOid);
            animEvent.setAnimList(animList);
            con.send(animEvent.toBytes());
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("AnimationHook: send anim msg for playerOid " + playerOid + ", objId=" + objOid + ", animEvent=" + animEvent);
            }
        }
    }
    
    class InvokeEffectHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final AnimationClient.InvokeEffectMessage effectMsg = (AnimationClient.InvokeEffectMessage)msg;
            final OID objOid = effectMsg.getSubject();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("InvokeEffectHook: got msg=" + effectMsg.toString());
            }
            final ClientConnection con = player.getConnection();
            final AOByteBuffer buf = effectMsg.toBuffer(player.getVersion());
            if (buf != null) {
                con.send(buf);
                if (Log.loggingDebug) {
                    ProxyPlugin.log.debug("InvokeEffectHook: sent ext msg for notifyOid " + objOid);
                }
            }
        }
    }
    
    class AbilityStatusHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            AOByteBuffer buf = null;
            final ClientConnection con = player.getConnection();
            final AbilityStatusMessage asMsg = (AbilityStatusMessage)msg;
            buf = asMsg.toBuffer();
            if (buf != null) {
                con.send(buf);
            }
        }
    }
    
    class ExtensionHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            AOByteBuffer buf = null;
            final ClientConnection con = player.getConnection();
            OID subject = null;
            OID target = null;
            String subType = null;
            if (msg instanceof WorldManagerClient.TargetedExtensionMessage) {
                final WorldManagerClient.TargetedExtensionMessage extMsg = (WorldManagerClient.TargetedExtensionMessage)msg;
                subject = extMsg.getSubject();
                target = extMsg.getTarget();
                subType = extMsg.getExtensionType();
                if (Log.loggingDebug) {
                    final Set<String> keySet = extMsg.keySet();
                    for (final String key : keySet) {
                        ProxyPlugin.log.debug("ExtensionHook: playerOid=" + player.getOid() + ", oid=" + subject + ", key " + key + ", value=" + extMsg.getProperty(key));
                    }
                }
                buf = extMsg.toBuffer(player.getVersion());
            }
            else {
                final WorldManagerClient.ExtensionMessage extMsg2 = (WorldManagerClient.ExtensionMessage)msg;
                subject = extMsg2.getSubject();
                subType = extMsg2.getExtensionType();
                if (Log.loggingDebug) {
                    final Set<String> keySet = extMsg2.keySet();
                    for (final String key : keySet) {
                        ProxyPlugin.log.debug("ExtensionHook: playerOid=" + player.getOid() + ", oid=" + subject + ", key " + key + ", value=" + extMsg2.getProperty(key));
                    }
                }
                buf = extMsg2.toBuffer(player.getVersion());
            }
            if (buf != null) {
                con.send(buf);
                if (Log.loggingDebug) {
                    ProxyPlugin.log.debug("ExtensionHook: sent subType " + subType + " for playerOid=" + player.getOid() + ", target=" + target + ", subject=" + subject);
                }
            }
        }
    }
    
    class P2PExtensionHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.TargetedExtensionMessage extMsg = (WorldManagerClient.TargetedExtensionMessage)msg;
            final OID objOid = extMsg.getSubject();
            final Set<String> keySet = extMsg.keySet();
            for (final String key : keySet) {
                if (Log.loggingDebug) {
                    ProxyPlugin.log.debug("P2PExtensionHook: playerOid=" + player.getOid() + ", oid = " + objOid + ", got key " + key + ", value=" + extMsg.getProperty(key));
                }
            }
            final ClientConnection con = player.getConnection();
            final AOByteBuffer buf = extMsg.toBuffer(player.getVersion());
            if (buf != null) {
                con.send(buf);
                if (Log.loggingDebug) {
                    ProxyPlugin.log.debug("P2PExtensionHook: sent ext msg for notifyOid " + objOid);
                }
            }
        }
    }
    
    class PropertyHook extends BasicProxyHook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            return true;
        }
        
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final OID subjectOid = propMsg.getSubject();
            if (Log.loggingDebug) {
                final Set<String> keySet = propMsg.keySet();
                for (final String key : keySet) {
                    ProxyPlugin.log.debug("handlePropertyMsg: player=" + player + ", oid=" + subjectOid + ", got key " + key + ", value=" + propMsg.getProperty(key));
                }
            }
            final ClientConnection con = player.getConnection();
            AOByteBuffer buf = null;
            if (ProxyPlugin.this.playerSpecificProps.size() > 0 && subjectOid != player.getOid()) {
                buf = propMsg.toBuffer(player.getVersion(), ProxyPlugin.this.cachedPlayerSpecificFilterProps);
            }
            else {
                buf = propMsg.toBuffer(player.getVersion(), ProxyPlugin.this.filteredProps);
            }
            if (buf != null) {
                con.send(buf);
                if (Log.loggingDebug) {
                    ProxyPlugin.log.debug("sent prop msg for player " + player + ", subjectId=" + subjectOid);
                }
            }
            else if (Log.loggingDebug) {
                ProxyPlugin.log.debug("filtered out prop msg for player " + player + ", subjectId=" + subjectOid + " because all props were filtered");
            }
        }
    }
    
    class TargetedPropertyHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.TargetedPropertyMessage propMsg = (WorldManagerClient.TargetedPropertyMessage)msg;
            final OID targetOid = propMsg.getTarget();
            final OID subjectOid = propMsg.getSubject();
            if (Log.loggingDebug) {
                final Set<String> keySet = propMsg.keySet();
                for (final String key : keySet) {
                    ProxyPlugin.log.debug("handleTargetedPropertyMsg: playerOid=" + player.getOid() + ", targetOid=" + targetOid + ", oid = " + subjectOid + ", got key " + key + ", value=" + propMsg.getProperty(key));
                }
            }
            final ClientConnection con = player.getConnection();
            AOByteBuffer buf = null;
            if (ProxyPlugin.this.playerSpecificProps.size() > 0 && subjectOid != player.getOid()) {
                buf = propMsg.toBuffer(player.getVersion(), ProxyPlugin.this.cachedPlayerSpecificFilterProps);
            }
            else {
                buf = propMsg.toBuffer(player.getVersion(), ProxyPlugin.this.filteredProps);
            }
            if (buf != null) {
                con.send(buf);
                if (Log.loggingDebug) {
                    ProxyPlugin.log.debug("sent targeted prop msg for targetOid " + targetOid + ", subjectOid=" + subjectOid);
                }
            }
            else if (Log.loggingDebug) {
                ProxyPlugin.log.debug("filtered out targeted prop msg for targetOid " + targetOid + ", subjectOid=" + subjectOid + " because all props were filtered");
            }
        }
    }
    
    class PerceptionHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final PerceptionMessage perceptionMessage = (PerceptionMessage)msg;
            final List<PerceptionMessage.ObjectNote> gain = perceptionMessage.getGainObjects();
            final List<PerceptionMessage.ObjectNote> lost = perceptionMessage.getLostObjects();
            if (Log.loggingDebug) {
                Log.debug("PerceptionHook.processMessage: start " + ((gain == null) ? 0 : gain.size()) + " gain and " + ((lost == null) ? 0 : lost.size()) + " lost");
            }
            if (player.getOid().equals(World.DEBUG_OID)) {
                Log.info("PerceptionHook: oid=" + World.DEBUG_OID + " start " + ((gain == null) ? 0 : gain.size()) + " gain and " + ((lost == null) ? 0 : lost.size()) + " lost");
            }
            final ClientConnection con = player.getConnection();
            synchronized (ProxyPlugin.this.playerManager) {
                final List<OID> newSubjects = new LinkedList<OID>();
                final List<OID> deleteSubjects = new LinkedList<OID>();
                if (lost != null) {
                    ProxyPlugin.this.playerManager.removeWorldPerception(player, lost, deleteSubjects);
                }
                if (gain != null) {
                    ProxyPlugin.this.playerManager.addWorldPerception(player, gain, newSubjects);
                }
                if (deleteSubjects.size() > 0 || newSubjects.size() > 0) {
                    final FilterUpdate perceptionUpdate = new FilterUpdate(deleteSubjects.size() + newSubjects.size());
                    for (final OID oid : deleteSubjects) {
                        perceptionUpdate.removeFieldValue(2, oid);
                    }
                    for (final OID oid : newSubjects) {
                        perceptionUpdate.addFieldValue(2, oid);
                    }
                    if (player.getOid().equals(World.DEBUG_OID)) {
                        Log.info("subject changes: " + newSubjects.size() + " gained " + deleteSubjects.size() + " lost");
                    }
                    Engine.getAgent().applyFilterUpdate(ProxyPlugin.this.perceptionSubId, perceptionUpdate, 0, perceptionMessage);
                }
            }
            boolean loadingState = false;
            if (player.supportsLoadingState() && (player.getLoadingState() == 0 || (gain != null && gain.size() > 20) || (lost != null && lost.size() > 20))) {
                con.send(new LoadingStateEvent(true).toBytes());
                loadingState = true;
            }
            if (lost != null) {
                for (final PerceptionMessage.ObjectNote objectNote : lost) {
                    ProxyPlugin.this.specialCaseFreeProcessing(objectNote, player);
                }
            }
            if (gain != null) {
                for (final PerceptionMessage.ObjectNote objectNote : gain) {
                    try {
                        ProxyPlugin.this.specialCaseNewProcessing(objectNote, player);
                        WorldManagerClient.updateObject(player.getOid(), objectNote.getSubject());
                    }
                    catch (Exception e) {
                        Log.exception("specialCaseNewProcessing: player=" + player + " oid=" + objectNote.getSubject(), e);
                    }
                }
            }
            if (loadingState) {
                player.setLoadingState(1);
                con.send(new LoadingStateEvent(false).toBytes());
            }
        }
    }
    
    public static class AddStaticPerceptionMessage extends TargetMessage
    {
        private String name;
        private ObjectType type;
        private boolean objectInfoProvided;
        
        public AddStaticPerceptionMessage() {
        }
        
        public AddStaticPerceptionMessage(final MessageType type) {
            super(type);
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public ObjectType getType() {
            return this.type;
        }
        
        public void setType(final ObjectType type) {
            this.type = type;
        }
        
        public boolean hasObjectInfo() {
            return this.objectInfoProvided;
        }
        
        public void setHasObjectInfo(final boolean flag) {
            this.objectInfoProvided = flag;
        }
    }
    
    private class AddStaticPerceptionHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final AddStaticPerceptionMessage message = (AddStaticPerceptionMessage)msg;
            if (Log.loggingDebug) {
                Log.debug("AddStaticPerceptionHook: player=" + player.getOid() + ", subject=" + message.getSubject());
            }
            if (!message.hasObjectInfo()) {
                final ObjectManagerClient.ObjectStatus objectStatus = ObjectManagerClient.getObjectStatus(message.getSubject());
                if (objectStatus == null || objectStatus.namespaces == null) {
                    Log.error("AddStaticPerceptionHook: ignoring unknown subject=" + message.getSubject() + " added to " + player);
                    return;
                }
                message.setName(objectStatus.name);
                message.setType(objectStatus.type);
            }
            final boolean perceptionGain = ProxyPlugin.this.playerManager.addStaticPerception(player, message.getSubject());
            if (perceptionGain) {
                final FilterUpdate perceptionUpdate = new FilterUpdate(1);
                perceptionUpdate.addFieldValue(2, message.getSubject());
                Engine.getAgent().applyFilterUpdate(ProxyPlugin.this.perceptionSubId, perceptionUpdate, 0);
                final WorldManagerClient.ObjectInfo info = new WorldManagerClient.ObjectInfo();
                info.oid = message.getSubject();
                info.name = message.getName();
                info.objType = message.getType();
                info.dir = new AOVector(0.0f, 0.0f, 0.0f);
                Log.debug("INFO: add static perception hook");
                player.getConnection().send(info.toBuffer(player.getOid()));
                WorldManagerClient.updateObject(player.getOid(), message.getSubject());
            }
        }
    }
    
    private class RemoveStaticPerceptionHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final TargetMessage message = (TargetMessage)msg;
            final boolean proxyPerceptionLoss = ProxyPlugin.this.playerManager.removeStaticPerception(player, message.getSubject());
            Log.debug("ProxyPlugin.RemoveStaticPerceptionHook(): proxyPerceptionLoss = " + proxyPerceptionLoss + ", playerOid=" + player.getOid() + ", oid=" + message.getSubject());
            if (proxyPerceptionLoss) {
                final FilterUpdate proxyPerceptionUpdate = new FilterUpdate(1);
                proxyPerceptionUpdate.removeFieldValue(2, message.getSubject());
                Engine.getAgent().applyFilterUpdate(ProxyPlugin.this.perceptionSubId, proxyPerceptionUpdate, 0);
            }
            final NotifyFreeObjectEvent freeEvent = new NotifyFreeObjectEvent(player.getOid(), message.getSubject());
            player.getConnection().send(freeEvent.toBytes());
        }
    }
    
    class VoiceParmsHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.TargetedExtensionMessage extMsg = new WorldManagerClient.TargetedExtensionMessage("voice_parms_response", player.getOid());
            extMsg.setProperty("host", ProxyPlugin.voiceServerHost);
            extMsg.setProperty("port", ProxyPlugin.voiceServerPort);
            final SecureTokenSpec tokenSpec = new SecureTokenSpec((byte)2, Engine.getAgent().getName(), System.currentTimeMillis() + 30000L);
            tokenSpec.setProperty("player_oid", player.getOid());
            final byte[] authToken = SecureTokenManager.getInstance().generateToken(tokenSpec);
            extMsg.setProperty("auth_token", Base64.encodeBytes(authToken));
            final ClientConnection con = player.getConnection();
            final AOByteBuffer buf = extMsg.toBuffer(player.getVersion());
            if (buf != null) {
                con.send(buf);
                if (Log.loggingDebug) {
                    ProxyPlugin.log.debug("VoiceParmsHook: sent voice_parm_response ext msg for player " + player.getOid());
                }
            }
        }
    }
    
    class UpdatePlayerIgnoreListHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.TargetedExtensionMessage extMsg = (WorldManagerClient.TargetedExtensionMessage)msg;
            final LinkedList<OID> nowIgnored = (LinkedList<OID>)extMsg.getProperty("now_ignored");
            final LinkedList<OID> noLongerIgnored = (LinkedList<OID>)extMsg.getProperty("no_longer_ignored");
            ProxyPlugin.this.updateIgnoredOids(player, nowIgnored, noLongerIgnored);
        }
    }
    
    class GetMatchingPlayersHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.TargetedExtensionMessage extMsg = (WorldManagerClient.TargetedExtensionMessage)msg;
            final String playerName = (String)extMsg.getProperty("player_name");
            final Boolean exactMatch = (Boolean)extMsg.getProperty("exact_match");
            final boolean match = exactMatch == null || exactMatch;
            final List<Object> matchLists = Engine.getDatabase().getOidsAndNamesMatchingName(playerName, match);
            final WorldManagerClient.TargetedExtensionMessage response = new WorldManagerClient.TargetedExtensionMessage("player_ignore_list", player.getOid());
            response.setSubject(player.getOid());
            final List<OID> oids = matchLists.get(0);
            final List<String> names = matchLists.get(1);
            response.setProperty("ignored_oids", (Serializable)oids);
            response.setProperty("ignored_player_names", (Serializable)names);
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("ProxyPlugin.GetMatchingPlayersHook: For player " + player.getOid() + ", found " + ((oids == null) ? 0 : oids.size()) + " players: " + Database.makeOidCollectionString(oids) + " " + (match ? "exactly matching" : "starting with") + " name '" + playerName + "':" + Database.makeNameCollectionString(names));
            }
            player.getConnection().send(response.toBuffer(player.getVersion()));
        }
    }
    
    class PlayerIgnoreListReqHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
        }
    }
    
    class PlayerPathReqHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final OID playerOid = player.getOid();
            final BasicWorldNode wnode = WorldManagerClient.getWorldNode(playerOid);
            final WorldManagerClient.ExtensionMessage extMsg = (WorldManagerClient.ExtensionMessage)msg;
            final WorldManagerClient.PlayerPathWMReqMessage reqMsg = new WorldManagerClient.PlayerPathWMReqMessage(playerOid, wnode.getInstanceOid(), (String)extMsg.getProperty("room_id"), (AOVector)extMsg.getProperty("start"), (float)extMsg.getProperty("speed"), (Quaternion)extMsg.getProperty("start_orient"), (AOVector)extMsg.getProperty("dest"), (Quaternion)extMsg.getProperty("dest_orient"), (List<AOVector>)extMsg.getProperty("boundary"), (List<List<AOVector>>)extMsg.getProperty("obstacles"), (float)extMsg.getProperty("avatar_width"));
            Engine.getAgent().sendBroadcast(reqMsg);
        }
    }
    
    class ComHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            AOByteBuffer buf = null;
            if (msg instanceof WorldManagerClient.ComMessage) {
                final WorldManagerClient.ComMessage comMsg = (WorldManagerClient.ComMessage)msg;
                final OID oid = comMsg.getSubject();
                if (player.oidIgnored(oid)) {
                    if (Log.loggingDebug) {
                        Log.debug("ComHook.processMessage: Ignoring chat from player " + oid + " to player " + player.getOid() + " because originator is in the player's ignored list");
                    }
                    return;
                }
                buf = comMsg.toBuffer();
                Log.info("ProxyPlugin: CHAT_RECV player=" + player + " from=" + comMsg.getSubject() + " private=false" + " msg=[" + comMsg.getString() + "]");
            }
            else {
                if (!(msg instanceof WorldManagerClient.TargetedComMessage)) {
                    return;
                }
                final WorldManagerClient.TargetedComMessage comMsg2 = (WorldManagerClient.TargetedComMessage)msg;
                final OID oid = comMsg2.getSubject();
                if (player.oidIgnored(oid)) {
                    if (Log.loggingDebug) {
                        Log.debug("ComHook.processMessage: Ignoring chat from player " + oid + " to player " + player.getOid() + " because originator is in the player's ignored list");
                    }
                    return;
                }
                buf = comMsg2.toBuffer();
                Log.info("ProxyPlugin: CHAT_RECV player=" + player + " from=" + comMsg2.getSubject() + " private=true" + " msg=[" + comMsg2.getString() + "]");
            }
            final ClientConnection con = player.getConnection();
            con.send(buf);
        }
    }
    
    class DamageHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final CombatClient.DamageMessage dmgMsg = (CombatClient.DamageMessage)msg;
            final OID attackerOid = dmgMsg.getAttackerOid();
            final OID targetOid = dmgMsg.getTargetOid();
            final AOByteBuffer buf = dmgMsg.toBuffer();
            final ClientConnection con = player.getConnection();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("DamageHook: attackerOid= " + attackerOid + ", attacks targetOid=" + targetOid + " for " + dmgMsg.getDmg() + " damage");
            }
            con.send(buf);
        }
    }
    
    class SysChatHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.SysChatMessage sysMsg = (WorldManagerClient.SysChatMessage)msg;
            final AOByteBuffer buf = sysMsg.toBuffer();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("syschathook:\t " + sysMsg.getString());
            }
            final Collection<Player> players = new ArrayList<Player>(ProxyPlugin.this.playerManager.getPlayerCount());
            ProxyPlugin.this.playerManager.getPlayers(players);
            for (final Player pp : players) {
                pp.getConnection().send(buf);
            }
            return true;
        }
    }
    
    class UpdateWNodeHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.UpdateWorldNodeMessage wMsg = (WorldManagerClient.UpdateWorldNodeMessage)msg;
            final OID subjectOid = wMsg.getSubject();
            final OID playerOid = player.getOid();
            if (Log.loggingDebug) {
                Log.debug("UpdateWNodeHook.processMessage: subjectOid=" + subjectOid + ", playerOid=" + playerOid + " msg=" + msg);
            }
            if (playerOid.equals(subjectOid)) {
                if (Log.loggingDebug) {
                    Log.debug("UpdateWNodeHook.processMessage: subjectOid=" + subjectOid + ", ignoring msg since playerOid matches subjectOid");
                }
                return;
            }
            player.getConnection().send(wMsg.getEventBuf());
        }
    }
    
    class UpdateMobPathHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.MobPathMessage pathMsg = (WorldManagerClient.MobPathMessage)msg;
            final OID subjectOid = pathMsg.getSubject();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("UpdateMobPathHook.processMessage: subjectOid=" + subjectOid + ", msg=" + msg);
            }
            final AOByteBuffer buf = pathMsg.toBuffer();
            final ClientConnection con = player.getConnection();
            con.send(buf);
        }
    }
    
    class WNodeCorrectHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.WorldNodeCorrectMessage wMsg = (WorldManagerClient.WorldNodeCorrectMessage)msg;
            final OID oid = wMsg.getSubject();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("WNodeCorrectHook.processMessage: oid=" + oid + ", msg=" + msg);
            }
            final AOByteBuffer buf = wMsg.toBuffer();
            final ClientConnection con = player.getConnection();
            con.send(buf);
        }
    }
    
    class OrientHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.OrientMessage oMsg = (WorldManagerClient.OrientMessage)msg;
            final AOByteBuffer buf = oMsg.toBuffer();
            final ClientConnection con = player.getConnection();
            con.send(buf);
        }
    }
    
    class SoundHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.SoundMessage sMsg = (WorldManagerClient.SoundMessage)msg;
            final OID target = sMsg.getTarget();
            if (target != null && !target.equals(player.getOid())) {
                return;
            }
            final ClientConnection con = player.getConnection();
            con.send(sMsg.toBuffer());
        }
    }
    
    class InvUpdateHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final InventoryClient.InvUpdateMessage uMsg = (InventoryClient.InvUpdateMessage)msg;
            if (!player.getOid().equals(uMsg.getSubject())) {
                return;
            }
            final ClientConnection con = player.getConnection();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("InvUpdateHook: sending update to player " + player.getOid() + " msgOid=" + uMsg.getSubject());
            }
            con.send(uMsg.toBuffer());
        }
    }
    
    class FogHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.FogMessage fogMsg = (WorldManagerClient.FogMessage)msg;
            final FogRegionConfig fogConfig = fogMsg.getFogConfig();
            final OID targetOid = fogMsg.getTarget();
            final ClientConnection con = player.getConnection();
            final WorldManagerClient.FogMessage fogMessage = new WorldManagerClient.FogMessage(null, fogConfig);
            con.send(fogMessage.toBuffer());
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("FogHook: sending new fog to targetOid " + targetOid + fogConfig);
            }
        }
    }
    
    class RoadHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final WorldManagerClient.RoadMessage roadMsg = (WorldManagerClient.RoadMessage)msg;
            final Set<Road> roads = roadMsg.getRoads();
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("RoadHook: got " + roads.size() + " roads");
            }
            final OID targetOid = roadMsg.getTarget();
            final ClientConnection con = player.getConnection();
            final List<AOByteBuffer> bufList = roadMsg.toBuffer();
            for (final AOByteBuffer buf : bufList) {
                con.send(buf);
            }
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("RoadHook: sent new roads to targetOid " + targetOid);
            }
        }
    }
    
    class AbilityUpdateHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final CombatClient.AbilityUpdateMessage pMsg = (CombatClient.AbilityUpdateMessage)msg;
            if (Log.loggingDebug) {
                ProxyPlugin.log.debug("AbilityUpdateHook: got AbilityUpdate message: " + msg);
            }
            final ClientConnection con = player.getConnection();
            con.send(pMsg.toBuffer());
        }
    }
    
    class GetPluginStatusHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final LinkedHashMap<String, Serializable> status = new LinkedHashMap<String, Serializable>();
            status.put("plugin", ProxyPlugin.this.getName());
            status.put("user", ProxyPlugin.this.playerManager.getPlayerCount());
            status.put("login", ProxyPlugin.this.playerManager.getLoginCount());
            status.put("login_sec", ProxyPlugin.this.playerManager.getLoginSeconds());
            status.put("instance_entry", ProxyPlugin.this.instanceEntryCount);
            status.put("chat", ProxyPlugin.this.chatSentCount);
            status.put("private_chat", ProxyPlugin.this.privateChatSentCount);
            Engine.getAgent().sendObjectResponse(msg, status);
            return true;
        }
    }
    
    public static class PlayerLoginStatus
    {
        public OID oid;
        public int status;
        public String name;
        public String clientCon;
        public String proxyPluginName;
        private static final long serialVersionUID = 1L;
        
        @Override
        public String toString() {
            return "[PlayerLoginStatus: oid=" + this.oid + ", status=" + this.status + ", name=" + this.name + ", proxyPluginName=" + this.proxyPluginName + "]";
        }
    }
    
    private class GetPlayerLoginStatusHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            Log.debug("GetPlayerLoginStatusHook: player=" + player);
            final PlayerLoginStatus loginStatus = new PlayerLoginStatus();
            loginStatus.oid = player.getOid();
            loginStatus.status = player.getStatus();
            loginStatus.name = player.getName();
            loginStatus.clientCon = player.getConnection().toString();
            loginStatus.proxyPluginName = ProxyPlugin.this.getName();
            Log.debug("GetPlayerLoginStatusHook: response=" + loginStatus);
            Engine.getAgent().sendObjectResponse(msg, loginStatus);
        }
    }
    
    private class LogoutPlayerHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message message, final int flags, final Player player) {
            new Thread(new LogoutPlayerRPCThread(message, player), "LogoutPlayer" + player.getOid()).start();
        }
    }
    
    private class LogoutPlayerRPCThread implements Runnable
    {
        private Player player;
        private Message message;
        
        public LogoutPlayerRPCThread(final Message message, final Player player) {
            this.player = player;
            this.message = message;
        }
        
        @Override
        public void run() {
            Log.debug("[CYC] ProxyPlugin.LogoutPlayerRPCThread.run(): start");
            try {
                Log.debug("[CYC] ProxyPlugin.LogoutPlayerRPCThread.run(): try { logoutPlayer(); }");
                this.logoutPlayer();
            }
            catch (Exception e) {
                Log.exception("LogoutPlayer", e);
                Engine.getAgent().sendObjectResponse(this.message, null);
            }
            Log.debug("[CYC] ProxyPlugin.LogoutPlayerRPCThread.run(): done");
        }
        
        public void logoutPlayer() {
            Log.debug("[CYC] ProxyPlugin.LogoutPlayerRPCThread.logoutPlayer(): start");
            final PlayerLoginStatus loginStatus = new PlayerLoginStatus();
            loginStatus.oid = this.player.getOid();
            loginStatus.status = this.player.getStatus();
            loginStatus.name = this.player.getName();
            loginStatus.clientCon = this.player.getConnection().toString();
            loginStatus.proxyPluginName = ProxyPlugin.this.getName();
            final WorldManagerClient.ComMessage comMessage = new WorldManagerClient.ComMessage(this.player.getOid(), "", 0, "Your player logged in from a different location.");
            this.player.getConnection().sendInternal(comMessage.toBuffer());
            try {
                Thread.sleep(20L);
            }
            catch (InterruptedException ex) {}
            this.player.getConnection().close();
            synchronized (this.player) {
                while (this.player.getConnection() != null) {
                    try {
                        this.player.wait();
                    }
                    catch (InterruptedException ignore) {}
                }
            }
            Log.debug("[CYC] ProxyPlugin.LogoutPlayerRPCThread.logoutPlayer(): Engine.getAgent().sendObjectResponse() message=" + this.message + ", loginStatus=" + loginStatus);
            Engine.getAgent().sendObjectResponse(this.message, loginStatus);
            Log.debug("[CYC] ProxyPlugin.LogoutPlayerRPCThread.logoutPlayer(): done");
        }
    }
    
    private class AccountLoginHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            Log.debug("AccountLoginHook hit");
            final GenericMessage tMsg = (GenericMessage)msg;
            final OID accountId = (OID)tMsg.getProperty("accountId");
            Log.debug("AccountLoginHook accountId=" + accountId + "; map=" + ProxyPlugin.this.clientConnections);
            if (ProxyPlugin.this.clientConnections.containsKey(accountId)) {
                Log.debug("Closing client connection");
                ProxyPlugin.this.clientConnections.get(accountId).close();
                ProxyPlugin.this.clientConnections.remove(accountId);
            }
            return true;
        }
    }
    
    static class InstanceEntryState
    {
        int step;
        InstanceClient.InstanceInfo instanceInfo;
        LinkedList restoreStack;
        BasicWorldNode previousLoc;
        
        InstanceEntryState() {
            this.step = 1;
        }
    }
    
    class InstanceEntryReqHook extends BasicProxyHook
    {
        @Override
        public void processMessage(final Message msg, final int flags, final Player player) {
            final InstanceClient.InstanceEntryReqMessage entryMessage = (InstanceClient.InstanceEntryReqMessage)msg;
            InstanceEntryState state = (InstanceEntryState)entryMessage.getProcessingState();
            if (state == null) {
                state = new InstanceEntryState();
                entryMessage.setProcessingState(state);
            }
            if (state.step == 1) {
                this.entryStep1(entryMessage, state, player);
            }
            else if (state.step == 2) {
                this.entryStep2(entryMessage, state, player);
            }
        }
        
        protected void entryStep1(final InstanceClient.InstanceEntryReqMessage entryMessage, final InstanceEntryState state, final Player player) {
            BasicWorldNode destination = entryMessage.getWorldNode();
            final int entryFlags = entryMessage.getFlags();
            String flagStr = "";
            if ((entryFlags & 0x1) != 0x0) {
                flagStr += "push,";
            }
            if ((entryFlags & 0x2) != 0x0) {
                flagStr += "pop,";
            }
            Log.info("ProxyPlugin: INSTANCE_BEGIN player=" + player + " destination=" + destination + " flags=" + flagStr);
            if ((entryFlags & 0x1) != 0x0 && (entryFlags & 0x2) != 0x0) {
                Log.error("InstanceEntryReqHook: push and pop flags cannot be combined oid=" + player.getOid());
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            if ((entryFlags & 0x1) != 0x0 && destination == null) {
                Log.error("InstanceEntryReqHook: push without destination oid=" + player.getOid());
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            if ((entryFlags & 0x2) != 0x0 && destination != null) {
                Log.error("InstanceEntryReqHook: pop with destination oid=" + player.getOid());
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            if (player.getStatus() != 2) {
                Log.error("InstanceEntryReqHook: invalid player status " + player);
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            if ((entryFlags & 0x2) != 0x0) {
                final LinkedList restoreStack = (LinkedList)EnginePlugin.getObjectProperty(player.getOid(), Namespace.OBJECT_MANAGER, "instanceStack");
                if (restoreStack == null || restoreStack.size() == 0) {
                    Log.error("InstanceEntryReqHook: player has no stack to pop " + player);
                    Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                    return;
                }
                state.restoreStack = restoreStack;
                final InstanceRestorePoint restorePoint = restoreStack.get(restoreStack.size() - 1);
                if (restoreStack.size() == 1) {
                    if (restorePoint.getFallbackFlag()) {
                        Log.warn("InstanceEntryReqHook: popping to fallback restore point " + player);
                    }
                    else {
                        Log.warn("InstanceEntryReqHook: popping last instance restore point " + player);
                    }
                }
                destination = new BasicWorldNode();
                OID instanceOid = restorePoint.getInstanceOid();
                if (restorePoint.getInstanceName() != null) {
                    instanceOid = ProxyPlugin.this.instanceEntryCallback.selectInstance(player, restorePoint.getInstanceName());
                }
                if (instanceOid != null) {
                    destination.setInstanceOid(instanceOid);
                    destination.setLoc(restorePoint.getLoc());
                    destination.setOrientation(restorePoint.getOrientation());
                    destination.setDir(new AOVector(0.0f, 0.0f, 0.0f));
                }
                entryMessage.setWorldNode(destination);
            }
            if (!ProxyPlugin.this.instanceEntryAllowed(player.getOid(), destination.getInstanceOid(), destination.getLoc())) {
                Log.info("ProxyPlugin: INSTANCE_REJECT player=" + player + " current=" + state.previousLoc + " destination=" + destination);
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            state.instanceInfo = InstanceClient.getInstanceInfo(destination.getInstanceOid(), -8193);
            if (state.instanceInfo.oid == null) {
                Log.error("InstanceEntryReqHook: unknown instanceOid=" + destination.getInstanceOid());
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            if (state.instanceInfo.populationLimit > 0 && state.instanceInfo.playerPopulation >= state.instanceInfo.populationLimit) {
                Log.debug("POP: got population: " + state.instanceInfo.playerPopulation + " and limit: " + state.instanceInfo.populationLimit);
                final OID instanceOid2 = ProxyPlugin.handleFullInstance(state.instanceInfo.templateName, state.instanceInfo);
                destination.setInstanceOid(instanceOid2);
                state.instanceInfo = InstanceClient.getInstanceInfo(destination.getInstanceOid(), -8193);
            }
            if (Log.loggingDebug) {
                Log.debug("InstanceEntryReqHook: instance terrain config: " + state.instanceInfo.terrainConfig);
            }
            final WorldManagerClient.TargetedExtensionMessage instanceBegin = new WorldManagerClient.TargetedExtensionMessage(player.getOid(), player.getOid());
            instanceBegin.setExtensionType("ao.SCENE_BEGIN");
            instanceBegin.setProperty("action", "instance");
            instanceBegin.setProperty("name", state.instanceInfo.name);
            instanceBegin.setProperty("templateName", state.instanceInfo.templateName);
            boolean rc = WorldManagerClient.despawn(player.getOid(), instanceBegin, null);
            if (!rc) {
                Log.error("InstanceEntryReqHook: despawn failed " + player);
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            state.previousLoc = WorldManagerClient.getWorldNode(player.getOid());
            Log.info("ProxyPlugin: INSTANCE_STEP1 player=" + player + " current=" + state.previousLoc + " destination=" + destination + " destName=" + state.instanceInfo.name);
            final ArrayList<Namespace> unloadWM = new ArrayList<Namespace>(1);
            unloadWM.add(WorldManagerClient.NAMESPACE);
            rc = ObjectManagerClient.unloadSubObject(player.getOid(), unloadWM);
            if (!rc) {
                Log.error("InstanceEntryReqHook: unload wm sub-object failed " + player);
                Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                return;
            }
            state.step = 2;
            ProxyPlugin.this.messageQQ.insert(player, entryMessage);
        }
        
        protected void entryStep2(final InstanceClient.InstanceEntryReqMessage entryMessage, final InstanceEntryState state, final Player player) {
            int entryFlags = entryMessage.getFlags();
            final ClientConnection con = player.getConnection();
            BasicWorldNode destination = entryMessage.getWorldNode();
            final BasicWorldNode previousLoc = state.previousLoc;
            BasicWorldNode restoreLoc = null;
            if ((entryFlags & 0x1) != 0x0) {
                restoreLoc = entryMessage.getRestoreNode();
                if (restoreLoc == null) {
                    restoreLoc = previousLoc;
                }
            }
            while (true) {
                final boolean rc = ObjectManagerClient.fixWorldNode(player.getOid(), destination);
                if (!rc) {
                    Log.error("InstanceEntryReqHook: fixWorldNode failed " + player + " node=" + destination);
                    Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                    return;
                }
                final InstanceClient.InstanceInfo instanceInfo = InstanceClient.getInstanceInfo(destination.getInstanceOid(), 2);
                EnginePlugin.setObjectProperty(player.getOid(), Namespace.OBJECT_MANAGER, "currentInstanceName", instanceInfo.name);
                Log.debug("instanceReq: sending template (scene) name: " + state.instanceInfo.templateName);
                final Event worldFileEvent = new WorldFileEvent(state.instanceInfo.templateName, destination.getLoc());
                con.send(worldFileEvent.toBytes());
                final WorldManagerClient.WorldNodeCorrectMessage correctMsg = new WorldManagerClient.WorldNodeCorrectMessage(player.getOid(), destination);
                con.send(correctMsg.toBuffer());
                if ((entryFlags & 0x1) != 0x0) {
                    ProxyPlugin.this.pushInstanceRestorePoint(player, restoreLoc);
                }
                final WorldManagerClient.TargetedExtensionMessage instanceEnd = new WorldManagerClient.TargetedExtensionMessage(player.getOid(), player.getOid());
                instanceEnd.setExtensionType("ao.SCENE_END");
                instanceEnd.setProperty("action", "instance");
                instanceEnd.setProperty("name", state.instanceInfo.name);
                instanceEnd.setProperty("templateName", state.instanceInfo.templateName);
                final ArrayList<Namespace> loadWM = new ArrayList<Namespace>(1);
                loadWM.add(WorldManagerClient.NAMESPACE);
                final OID oid = ObjectManagerClient.loadSubObject(player.getOid(), loadWM);
                if (oid == null) {
                    Log.error("InstanceEntryReqHook: load wm sub-object failed " + player);
                    if (previousLoc != null && destination != previousLoc) {
                        Log.error("InstanceEntryReqHook: attempting to restore previous location " + player + " previous=" + previousLoc);
                        destination = previousLoc;
                        entryFlags &= 0xFFFFFFFD;
                        continue;
                    }
                }
                final Integer result = WorldManagerClient.spawn(player.getOid(), null, instanceEnd);
                if (result >= 0) {
                    WorldManagerClient.correctWorldNode(player.getOid(), destination);
                    ProxyPlugin.this.updateInstancePerception(player.getOid(), previousLoc.getInstanceOid(), destination.getInstanceOid(), instanceInfo.name);
                    Log.info("ProxyPlugin: INSTANCE_END player=" + player + " destination=" + destination);
                    if ((entryFlags & 0x2) != 0x0) {
                        final LinkedList restoreStack = state.restoreStack;
                        final InstanceRestorePoint top = restoreStack.get(restoreStack.size() - 1);
                        if (!top.getFallbackFlag()) {
                            restoreStack.remove(restoreStack.size() - 1);
                            EnginePlugin.setObjectProperty(player.getOid(), Namespace.OBJECT_MANAGER, "instanceStack", restoreStack);
                        }
                    }
                    ProxyPlugin.this.instanceEntryCount++;
                    Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.TRUE);
                    return;
                }
                Log.error("InstanceEntryReqHook: spawn failed " + player);
                if (result != -2 || previousLoc == null || destination == previousLoc) {
                    Engine.getAgent().sendBooleanResponse(entryMessage, Boolean.FALSE);
                    return;
                }
                Log.error("InstanceEntryReqHook: attempting to restore previous location " + player + " previous=" + previousLoc);
                destination = previousLoc;
                entryFlags &= 0xFFFFFFFD;
            }
        }
    }
    
    private class PlayerTimeout implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    Log.debug("PlayerTimeout thread running..");
                    this.timeoutPlayers();
                }
                catch (Exception e) {
                    Log.exception("PlayerTimeout", e);
                }
                try {
                    Thread.sleep(10000L);
                }
                catch (InterruptedException e2) {}
            }
        }
        
        private void timeoutPlayers() {
            final List<Player> timedoutPlayers = ProxyPlugin.this.playerManager.getTimedoutPlayers(ProxyPlugin.idleTimeout * 1000, ProxyPlugin.silenceTimeout * 1000);
            for (final Player player : timedoutPlayers) {
                if (!ProxyPlugin.this.isAdmin(player.getOid())) {
                    Log.info("ProxyPlugin: IDLE_TIMEOUT remote=" + player.getConnection() + " player=" + player);
                    player.getConnection().close();
                }
            }
        }
    }
    
    private static class PlayerHeartbeat implements ProxyExtensionHook
    {
        @Override
        public void processExtensionEvent(final ExtensionMessageEvent event, final Player player, final ProxyPlugin proxy) {
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "ao.heartbeat");
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, player.getOid(), player.getOid(), false, props);
            Engine.getAgent().sendBroadcast(msg);
        }
    }
    
    static class AsyncRPCCallback implements ResponseCallback
    {
        Player player;
        String debugPrefix;
        int responders;
        
        AsyncRPCCallback(final Player player, final String debugPrefix) {
            this.responders = 0;
            this.player = player;
            this.debugPrefix = debugPrefix;
        }
        
        @Override
        public synchronized void handleResponse(final ResponseMessage response) {
            --this.responders;
            Log.debug(this.debugPrefix + ", fromAgent=" + response.getSenderName() + " playerOid=" + this.player.getOid());
            if (this.responders == 0) {
                this.notify();
            }
        }
        
        public synchronized void waitForResponses(final int expectedResponses) {
            this.responders += expectedResponses;
            while (this.responders != 0) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {}
            }
        }
    }
    
    protected class ProxyJMX implements ProxyJMXMBean
    {
        @Override
        public int getMaxConcurrentUsers() {
            return ProxyPlugin.MaxConcurrentUsers;
        }
        
        @Override
        public void setMaxConcurrentUsers(final int users) {
            if (users >= 0) {
                ProxyPlugin.MaxConcurrentUsers = users;
            }
        }
        
        @Override
        public int getIdleTimeout() {
            return ProxyPlugin.idleTimeout;
        }
        
        @Override
        public void setIdleTimeout(final int timeout) {
            if (timeout > 0) {
                ProxyPlugin.idleTimeout = timeout;
            }
        }
        
        @Override
        public int getSilenceTimeout() {
            return ProxyPlugin.silenceTimeout;
        }
        
        @Override
        public void setSilenceTimeout(final int timeout) {
            if (timeout > 0) {
                ProxyPlugin.silenceTimeout = timeout;
            }
        }
        
        @Override
        public int getCurrentUsers() {
            return ProxyPlugin.this.playerManager.getPlayerCount();
        }
        
        @Override
        public int getPeakUsers() {
            return ProxyPlugin.this.playerManager.getPeakPlayerCount();
        }
        
        @Override
        public int getLoginCount() {
            return ProxyPlugin.this.playerManager.getLoginCount();
        }
        
        @Override
        public int getLogoutCount() {
            return ProxyPlugin.this.playerManager.getLogoutCount();
        }
        
        @Override
        public int getClientPort() {
            return ProxyPlugin.this.clientPort;
        }
        
        @Override
        public int getMaxMessagesBeforeConnectionReset() {
            return ProxyPlugin.maxMessagesBeforeConnectionReset;
        }
        
        @Override
        public void setMaxMessagesBeforeConnectionReset(final int count) {
            if (count > 0) {
                ProxyPlugin.maxMessagesBeforeConnectionReset = count;
            }
        }
        
        @Override
        public int getMaxByteCountBeforeConnectionReset() {
            return ProxyPlugin.maxByteCountBeforeConnectionReset;
        }
        
        @Override
        public void setMaxByteCountBeforeConnectionReset(final int bytes) {
            if (bytes > 0) {
                ProxyPlugin.maxByteCountBeforeConnectionReset = bytes;
            }
        }
        
        @Override
        public String getCapacityErrorMessage() {
            return ProxyPlugin.this.capacityError;
        }
        
        @Override
        public void setCapacityErrorMessage(final String errorMessage) {
            if (errorMessage != null) {
                ProxyPlugin.this.capacityError = errorMessage;
            }
        }
    }
    
    class PeriodicGC implements Runnable
    {
        @Override
        public void run() {
            int count = 1;
            while (true) {
                try {
                    Thread.sleep(60000L);
                }
                catch (InterruptedException ex) {}
                System.out.println("Proxy running GC " + count);
                System.gc();
                ++count;
            }
        }
    }
    
    public interface ProxyJMXMBean
    {
        int getMaxConcurrentUsers();
        
        void setMaxConcurrentUsers(final int p0);
        
        int getIdleTimeout();
        
        void setIdleTimeout(final int p0);
        
        int getSilenceTimeout();
        
        void setSilenceTimeout(final int p0);
        
        int getCurrentUsers();
        
        int getPeakUsers();
        
        int getLoginCount();
        
        int getLogoutCount();
        
        int getClientPort();
        
        int getMaxMessagesBeforeConnectionReset();
        
        void setMaxMessagesBeforeConnectionReset(final int p0);
        
        int getMaxByteCountBeforeConnectionReset();
        
        void setMaxByteCountBeforeConnectionReset(final int p0);
        
        String getCapacityErrorMessage();
        
        void setCapacityErrorMessage(final String p0);
    }
    
    public interface CommandAccessCheck
    {
        boolean allowed(final CommandEvent p0, final ProxyPlugin p1);
    }
    
    public interface CommandParser
    {
        void parse(final CommandEvent p0);
    }
}
