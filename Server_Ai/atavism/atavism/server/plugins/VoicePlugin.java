// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.server.math.Point;
import atavism.server.util.AORuntimeException;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.util.LinkedHashMap;
import atavism.server.engine.BasicWorldNode;
import atavism.server.messages.PerceptionMessage;
import atavism.msgsys.IntegerResponseMessage;
import atavism.msgsys.GenericResponseMessage;
import atavism.msgsys.OIDResponseMessage;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.ResponseMessage;
import java.io.OutputStream;
import java.io.FileOutputStream;
import atavism.msgsys.FilterUpdate;
import atavism.server.engine.InterpolatedWorldNode;
import java.util.LinkedList;
import atavism.msgsys.Message;
import atavism.msgsys.TargetMessage;
import atavism.server.util.SecureToken;
import atavism.server.voice.PositionalGroupMember;
import atavism.server.util.Base64;
import atavism.server.util.SecureTokenManager;
import java.io.BufferedOutputStream;
import atavism.server.util.DebugUtils;
import java.io.IOException;
import atavism.server.network.AOByteBuffer;
import atavism.server.voice.GroupMember;
import java.util.Iterator;
import java.util.List;
import atavism.server.engine.Interpolator;
import atavism.server.engine.BasicInterpolator;
import atavism.server.messages.PopulationFilter;
import atavism.msgsys.MessageTrigger;
import atavism.server.messages.PerceptionTrigger;
import java.util.Collection;
import atavism.server.objects.ObjectTypes;
import atavism.server.objects.ObjectType;
import java.util.ArrayList;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.msgsys.MessageTypeFilter;
import atavism.management.Management;
import atavism.server.engine.Hook;
import atavism.server.messages.LoginMessage;
import atavism.server.engine.OID;
import atavism.server.voice.NonpositionalVoiceGroup;
import atavism.server.voice.VoiceGroup;
import atavism.server.voice.PositionalVoiceGroup;
import atavism.server.engine.Engine;
import java.util.HashSet;
import atavism.server.util.Log;
import atavism.server.util.ServerVersion;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import atavism.server.messages.PerceptionFilter;
import atavism.server.network.ClientTCPMessageIO;
import atavism.server.voice.VoiceConnection;
import java.util.Set;
import atavism.server.util.TimeHistogram;
import atavism.server.util.CountLogger;
import atavism.server.network.ClientConnection;
import atavism.server.voice.VoiceSender;
import atavism.server.engine.EnginePlugin;

public class VoicePlugin extends EnginePlugin implements VoiceSender, ClientConnection.AcceptCallback, ClientConnection.MessageCallback
{
    public static final byte voicePacketHeaderSize = 4;
    public static final byte opcodeVoiceUnallocated = 0;
    public static final byte opcodeAuthenticate = 1;
    public static final byte opcodeAllocateCodec = 2;
    public static final byte opcodeAllocatePositionalCodec = 3;
    public static final byte opcodeReconfirmCodec = 4;
    public static final byte opcodeDeallocate = 5;
    public static final byte opcodeData = 6;
    public static final byte opcodeAggregatedData = 7;
    public static final byte opcodeLoginStatus = 8;
    public static final byte opcodeChangeIgnoredStatus = 9;
    public static final int opcodeHighest = 9;
    public static String[] opcodeNames;
    public static int[] voiceMsgSize;
    public static int lengthBytes;
    public static int[] speexNarrowBandFrameSize;
    public static int[] speexWideBandFrameSize;
    public static int maxVoiceChannels;
    private CountLogger countLogger;
    private static CountLogger.Counter countPacketsIgnored;
    private static CountLogger.Counter countSeqNumGaps;
    public static CountLogger.Counter countSendLoginStatus;
    private static CountLogger.Counter countPacketsReceived;
    private static CountLogger.Counter countDataFramesReceived;
    public static CountLogger.Counter countAllocateVoiceReceived;
    public static CountLogger.Counter countDeallocateVoiceReceived;
    private static CountLogger.Counter countPacketsSent;
    private static CountLogger.Counter countDataFramesSent;
    public static CountLogger.Counter countAllocateVoiceSent;
    public static CountLogger.Counter countDeallocateVoiceSent;
    public static boolean runHistograms;
    public static TimeHistogram processPacketHistogram;
    public static TimeHistogram dataSendHistogram;
    public static TimeHistogram voiceAllocHistogram;
    public static TimeHistogram voiceDeallocHistogram;
    public static boolean checkAuthToken;
    private VoiceConManager conMgr;
    private Integer voicePort;
    private boolean recordVoices;
    private Set<VoiceConnection> loginStatusEventReceivers;
    private short loginStatusSeqNum;
    private ClientTCPMessageIO clientTCPMessageIO;
    protected static boolean createGroupWhenReferenced;
    protected static boolean allowVoiceBots;
    private static float audibleRadius;
    protected static float hystericalMargin;
    protected static VoicePlugin instance;
    private String serverVersion;
    protected PerceptionFilter perceptionFilter;
    protected long perceptionSubId;
    protected Updater updater;
    protected Thread updaterThread;
    protected boolean running;
    protected transient Lock lock;
    
    public VoicePlugin() {
        super("Voice");
        this.conMgr = new VoiceConManager();
        this.recordVoices = false;
        this.loginStatusEventReceivers = null;
        this.loginStatusSeqNum = 0;
        this.clientTCPMessageIO = null;
        this.serverVersion = null;
        this.updater = null;
        this.updaterThread = null;
        this.running = true;
        this.lock = LockFactory.makeLock("VoicePlugin");
        this.setPluginType("Voice");
        this.serverVersion = "2.5.0 " + ServerVersion.getBuildNumber();
        Log.info("VoicePlugin (server version " + this.serverVersion + ") starting up");
        this.loginStatusEventReceivers = new HashSet<VoiceConnection>();
        this.countLogger = new CountLogger("VoiceMsg", 5000, 2, true);
        final String log_voice_counters = Engine.getProperty("atavism.log_voice_counters");
        if (log_voice_counters == null || log_voice_counters.equals("false")) {
            this.countLogger.setLogging(false);
        }
        VoicePlugin.countPacketsReceived = this.countLogger.addCounter("pkts received");
        VoicePlugin.countDataFramesReceived = this.countLogger.addCounter("data frames recvd");
        VoicePlugin.countAllocateVoiceReceived = this.countLogger.addCounter("alloc voice recvd");
        VoicePlugin.countDeallocateVoiceReceived = this.countLogger.addCounter("dealloc voice recvd");
        VoicePlugin.countSeqNumGaps = this.countLogger.addCounter("seqnum gaps");
        VoicePlugin.countPacketsIgnored = this.countLogger.addCounter("pkts ignored");
        VoicePlugin.countPacketsSent = this.countLogger.addCounter("pkts sent");
        VoicePlugin.countDataFramesSent = this.countLogger.addCounter("data frames sent");
        VoicePlugin.countAllocateVoiceSent = this.countLogger.addCounter("alloc voice sent");
        VoicePlugin.countDeallocateVoiceSent = this.countLogger.addCounter("dealloc voice sent");
        VoicePlugin.countSendLoginStatus = this.countLogger.addCounter("login status");
        this.handleVoiceProperties();
        if (VoicePlugin.runHistograms) {
            VoicePlugin.processPacketHistogram = new TimeHistogram("Process Packet");
            VoicePlugin.dataSendHistogram = new TimeHistogram("Process Data Frames");
            VoicePlugin.voiceAllocHistogram = new TimeHistogram("Process Allocate");
            VoicePlugin.voiceDeallocHistogram = new TimeHistogram("Process Deallocate");
        }
        VoicePlugin.instance = this;
        this.countLogger.start();
        this.updater = new Updater();
        final Thread updaterThread = new Thread(this.updater);
        updaterThread.start();
    }
    
    private void handleVoiceProperties() {
        final OID precreatedPositionalOid = this.parseOIDOrNull(Engine.getProperty("atavism.precreated_positional_voice_group"));
        if (precreatedPositionalOid != null) {
            this.conMgr.addGroup(precreatedPositionalOid, new PositionalVoiceGroup(precreatedPositionalOid, null, this, VoicePlugin.maxVoiceChannels, VoicePlugin.audibleRadius, VoicePlugin.hystericalMargin));
        }
        final OID precreatedNonpositionalOid = this.parseOIDOrNull(Engine.getProperty("atavism.precreated_nonpositional_voice_group"));
        if (precreatedNonpositionalOid != null) {
            this.conMgr.addGroup(precreatedNonpositionalOid, new NonpositionalVoiceGroup(precreatedNonpositionalOid, null, this, VoicePlugin.maxVoiceChannels));
        }
        final Boolean autoCreateGroups = Boolean.valueOf(Engine.getProperty("atavism.autocreate_referenced_voice_groups"));
        if (autoCreateGroups != null && autoCreateGroups) {
            VoicePlugin.createGroupWhenReferenced = true;
        }
        final Boolean voiceBots = Boolean.valueOf(Engine.getProperty("atavism.voice_bots"));
        if (voiceBots != null && voiceBots) {
            VoicePlugin.allowVoiceBots = true;
        }
        final Boolean histograms = Boolean.valueOf(Engine.getProperty("atavism.voice_packet_histograms"));
        if (histograms != null && histograms) {
            VoicePlugin.runHistograms = true;
        }
        final Boolean checkAuthTokenValue = Boolean.valueOf(Engine.getProperty("atavism.check_auth_token"));
        if (checkAuthTokenValue != null) {
            VoicePlugin.checkAuthToken = checkAuthTokenValue;
        }
    }
    
    private OID parseOIDOrNull(final String s) {
        try {
            return OID.fromLong(Long.parseLong(s));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static VoicePlugin getInstance() {
        return VoicePlugin.instance;
    }
    
    @Override
    public void onActivate() {
        this.voicePort = Integer.parseInt(Engine.getProperty("atavism.voiceport"));
        final String s = Engine.getProperty("atavism.record_voices");
        if (s != null && s.length() > 3) {
            this.recordVoices = Boolean.parseBoolean(s);
        }
        if (VoicePlugin.allowVoiceBots) {
            this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, new LoginHook());
        }
        this.getHookManager().addHook(VoiceClient.MSG_TYPE_VOICECLIENT, new VoiceClientMessageHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_INSTANCE_DELETED, new InstanceUnloadedHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_INSTANCE_UNLOADED, new InstanceUnloadedHook());
        this.getHookManager().addHook(Management.MSG_TYPE_GET_PLUGIN_STATUS, new GetPluginStatusHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_PERCEPTION, new PerceptionHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATEWNODE, new UpdateWorldNodeHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, new SpawnedHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, new DespawnedHook());
        this.getHookManager().addHook(ProxyPlugin.MSG_TYPE_RELAY_UPDATE_PLAYER_IGNORE_LIST, new RelayUpdatePlayerIgnoreListHook());
        final MessageTypeFilter filter = new MessageTypeFilter();
        if (VoicePlugin.allowVoiceBots) {
            filter.addType(LoginMessage.MSG_TYPE_LOGIN);
        }
        filter.addType(VoiceClient.MSG_TYPE_VOICECLIENT);
        filter.addType(InstanceClient.MSG_TYPE_INSTANCE_DELETED);
        filter.addType(InstanceClient.MSG_TYPE_INSTANCE_UNLOADED);
        filter.addType(Management.MSG_TYPE_GET_PLUGIN_STATUS);
        Engine.getAgent().createSubscription(filter, this, 8);
        (this.clientTCPMessageIO = ClientTCPMessageIO.setup(2, this.voicePort, this, this)).start("VoiceIO");
        Engine.registerStatusReportingPlugin(this);
        (this.perceptionFilter = new PerceptionFilter()).addType(WorldManagerClient.MSG_TYPE_PERCEPTION);
        this.perceptionFilter.addType(WorldManagerClient.MSG_TYPE_UPDATEWNODE);
        this.perceptionFilter.addType(ProxyPlugin.MSG_TYPE_RELAY_UPDATE_PLAYER_IGNORE_LIST);
        this.perceptionFilter.setMatchAllSubjects(false);
        final List<ObjectType> subjectTypes = new ArrayList<ObjectType>(1);
        subjectTypes.add(ObjectTypes.player);
        this.perceptionFilter.setSubjectObjectTypes(subjectTypes);
        final PerceptionTrigger perceptionTrigger = new PerceptionTrigger();
        this.perceptionSubId = Engine.getAgent().createSubscription(this.perceptionFilter, this, 0, perceptionTrigger);
        final PopulationFilter populationFilter = new PopulationFilter(ObjectTypes.player);
        Engine.getAgent().createSubscription(populationFilter, this);
        if (Engine.getInterpolator() == null) {
            Engine.setInterpolator(new BasicInterpolator());
        }
    }
    
    @Override
    public void acceptConnection(final ClientConnection con) {
        con.setAssociation(new VoiceConnection(con));
        Log.info("VoicePlugin: CONNECT remote=" + con.IPAndPort());
    }
    
    protected void sendLoginStatusToReceivers(final OID playerOid, final boolean login) {
        for (final VoiceConnection playerCon : this.loginStatusEventReceivers) {
            this.sendLoginStatus(playerCon, playerOid, login);
        }
    }
    
    protected GroupMember getPlayerMember(final OID playerOid) {
        final VoiceConnection con = this.conMgr.getPlayerCon(playerOid);
        if (con == null || con.group == null) {
            Log.error("VoicePlugin.getPlayerMember: For " + playerOid + ", con " + con + " group field is null!");
            return null;
        }
        return con.groupMember;
    }
    
    @Override
    public void processPacket(final ClientConnection con, final AOByteBuffer buf) {
        byte opcode = 0;
        final long startTime = System.nanoTime();
        this.lock.lock();
        try {
            VoicePlugin.countPacketsReceived.add();
            final short seqNum = buf.getShort();
            opcode = buf.getByte();
            final byte micVoiceNumber = buf.getByte();
            final VoiceConnection speaker = this.getConnectionData(con);
            if (speaker == null) {
                Log.error("VoicePlugin.processPacket: Could not find VoiceConnection for con " + con + ", micVoiceNumber " + micVoiceNumber + ", opcode " + opcodeString(opcode));
                return;
            }
            if (incSeqNum(speaker.seqNum) != seqNum) {
                VoicePlugin.countSeqNumGaps.add();
            }
            speaker.seqNum = seqNum;
            if (speaker.authToken == null) {
                if (opcode != 1) {
                    VoicePlugin.countPacketsIgnored.add();
                    Log.error("VoicePlugin.processPacket: Have not yet received authentication token, but packet opcode is " + opcodeString(opcode));
                }
                else {
                    this.processAuthenticationPacket(speaker, buf);
                }
            }
            else {
                speaker.micVoiceNumber = micVoiceNumber;
                final OID oid = speaker.playerOid;
                if (Log.loggingNet) {
                    Log.net("VoicePlugin.processPacket: micVoiceNumber " + micVoiceNumber + ", opcode " + opcodeString(opcode) + ", seqNum " + seqNum + ", oid " + oid);
                }
                else if (Log.loggingDebug && opcode != 6 && opcode != 7) {
                    Log.debug("VoicePlugin.processPacket: micVoiceNumber " + micVoiceNumber + ", opcode " + opcodeString(opcode) + ", seqNum " + seqNum + ", oid " + oid);
                }
                final VoiceGroup group = speaker.group;
                if (group == null) {
                    Log.error("VoicePlugin.processPacket: For speaker " + oid + ", connection " + con + ", group is null");
                    return;
                }
                switch (opcode) {
                    case 1: {
                        this.processAuthenticationPacket(speaker, buf);
                        break;
                    }
                    case 2: {
                        if (this.recordVoices && speaker.recordSpeexStream == null) {
                            speaker.recordSpeexStream = this.openSpeexVoiceFile(oid);
                        }
                        VoicePlugin.countAllocateVoiceReceived.add();
                        group.setMemberSpeaking(oid, true);
                        break;
                    }
                    case 3: {
                        Log.error("VoicePlugin.processPacket: shouldn't get AllocatePositionalCodec message!");
                        break;
                    }
                    case 4: {
                        group.setMemberSpeaking(oid, true);
                        break;
                    }
                    case 5: {
                        if (this.recordVoices && speaker.recordSpeexStream == null) {
                            try {
                                speaker.recordSpeexStream.close();
                            }
                            catch (IOException e) {
                                Log.exception("Error closing Speex stream for voice " + speaker.playerOid, e);
                            }
                            speaker.recordSpeexStream = null;
                        }
                        VoicePlugin.countDeallocateVoiceReceived.add();
                        group.setMemberSpeaking(oid, false);
                        break;
                    }
                    case 6: {
                        final int dataSize = buf.limit();
                        if (speaker.recordSpeexStream != null) {
                            this.writeSpeexData(speaker.recordSpeexStream, buf.array(), 4, dataSize - 4);
                        }
                        VoicePlugin.countDataFramesReceived.add();
                        if (group.isMemberSpeaking(speaker.playerOid)) {
                            group.sendVoiceFrameToListeners(oid, buf, (byte)6, dataSize);
                            break;
                        }
                        Log.error("VoicePlugin.processPacket: got data pkt for speaker " + speaker.playerOid + ", but speaker.currentSpeaker false.  micVoiceNumber " + micVoiceNumber + ", opcode " + opcodeString(opcode) + ", seqNum " + seqNum + DebugUtils.byteArrayToHexString(buf));
                        break;
                    }
                    case 7: {
                        final int dataSize = buf.limit();
                        final byte dataFrameCount = buf.getByte();
                        if (speaker.recordSpeexStream != null) {
                            final byte[] array = buf.array();
                            int currentIndex = 5;
                            for (int i = 0; i < dataFrameCount; ++i) {
                                final byte frameLength = array[currentIndex];
                                ++currentIndex;
                                this.writeSpeexData(speaker.recordSpeexStream, array, currentIndex, frameLength);
                                currentIndex += frameLength;
                            }
                            if (currentIndex != dataSize) {
                                Log.error("VoicePlugin.processPacket: While recording agg data packet: currentIndex " + currentIndex + " != dataSize " + dataSize);
                            }
                        }
                        if (group.isMemberSpeaking(speaker.playerOid)) {
                            group.sendVoiceFrameToListeners(oid, buf, (byte)7, dataSize);
                        }
                        else {
                            Log.error("VoicePlugin.processPacket: got data pkt for speaker " + speaker.playerOid + ", but speaker.currentSpeaker false.  micVoiceNumber " + micVoiceNumber + ", opcode " + opcodeString(opcode) + ", seqNum " + seqNum + DebugUtils.byteArrayToHexString(buf));
                        }
                        VoicePlugin.countDataFramesReceived.add(dataFrameCount);
                        speaker.seqNum = incSeqNum(speaker.seqNum, dataFrameCount - 1);
                        break;
                    }
                    case 9: {
                        this.processIgnoreListChangeMessage(speaker, buf);
                        break;
                    }
                }
            }
        }
        catch (Exception e2) {
            Log.exception("VoicePlugin.processPacket: For packet " + DebugUtils.byteArrayToHexString(buf), e2);
        }
        finally {
            this.lock.unlock();
        }
        if (VoicePlugin.runHistograms) {
            final long packetTime = System.nanoTime() - startTime;
            switch (opcode) {
                case 2: {
                    VoicePlugin.voiceAllocHistogram.addTime(packetTime);
                    break;
                }
                case 5: {
                    VoicePlugin.voiceDeallocHistogram.addTime(packetTime);
                    break;
                }
                case 6:
                case 7: {
                    VoicePlugin.dataSendHistogram.addTime(packetTime);
                    break;
                }
            }
            VoicePlugin.processPacketHistogram.addTime(packetTime);
        }
    }
    
    protected void writeSpeexData(final BufferedOutputStream recordSpeexStream, final byte[] buf, final int startIndex, final int byteCount) {
        try {
            recordSpeexStream.write(buf, startIndex, byteCount);
        }
        catch (IOException e) {
            Log.exception("VoicePlugin.writeVoiceData: Exception writing voice data", e);
        }
    }
    
    public void processAuthenticationPacket(final VoiceConnection playerCon, final AOByteBuffer buf) {
        final OID playerOid = buf.getOID();
        final OID groupOid = buf.getOID();
        final String encodedToken = buf.getString();
        final byte listenToYourselfByte = buf.getByte();
        final boolean listenToYourself = listenToYourselfByte != 0;
        if (Log.loggingDebug) {
            Log.debug("VoicePlugin.processAuthenticationPacket: Received auth packet; playerOid " + playerOid + ", groupOid " + groupOid + ", authToken " + encodedToken + ", listenToYourself " + listenToYourself);
        }
        final VoiceConnection previousPlayerCon = this.conMgr.getPlayerCon(playerOid);
        if (previousPlayerCon != null && previousPlayerCon != playerCon) {
            this.expungeVoiceClient(playerOid, previousPlayerCon);
        }
        if (playerCon.group != null) {
            this.removePlayerFromGroup(playerCon);
        }
        final SecureToken authToken = SecureTokenManager.getInstance().importToken(Base64.decode(encodedToken));
        if (VoicePlugin.checkAuthToken && (!authToken.getValid() || !playerOid.equals(authToken.getProperty("player_oid")))) {
            Log.error("VoicePlugin.processAuthenticationPacket: token rejected for playerOid=" + playerOid + " token=" + authToken);
            playerCon.con.close();
            return;
        }
        if (VoicePlugin.allowVoiceBots && playerOid == null && groupOid == null) {
            this.loginStatusEventReceivers.add(playerCon);
        }
        else {
            final VoiceGroup group = this.findVoiceGroup(groupOid);
            if (group != null) {
                if (group.addMemberAllowed(playerOid)) {
                    playerCon.groupOid = groupOid;
                    playerCon.group = group;
                    playerCon.authToken = authToken;
                    playerCon.playerOid = playerOid;
                    playerCon.listenToYourself = listenToYourself;
                    this.conMgr.setPlayerCon(playerOid, playerCon);
                    playerCon.groupMember = group.addMember(playerOid, playerCon);
                    this.initializeIgnoreList(playerCon);
                    this.conMgr.setPlayerGroup(playerOid, group);
                    group.setListener(playerOid, true);
                    if (group.isPositional()) {
                        final PositionalGroupMember member = (PositionalGroupMember)playerCon.groupMember;
                        if (member.wnode == null) {
                            this.trackNewPerceiver(member);
                        }
                    }
                    Log.info("VoicePlugin: VOICE_AUTH remote=" + playerCon.con.IPAndPort() + " playerOid=" + playerOid + " groupOid=" + groupOid);
                }
                else {
                    Log.error("VoicePlugin.processAuthenticationPacket: Player " + playerOid + " with authToken '" + encodedToken + "' was denied access to group " + groupOid + " - - auth packet ignored!");
                }
            }
            else {
                Log.error("VoicePlugin.processAuthenticationPacket: Could not find group " + groupOid + " for playerOid " + playerOid + " - - auth packet ignored!");
            }
        }
    }
    
    protected void initializeIgnoreList(final VoiceConnection playerCon) {
        final OID playerOid = playerCon.playerOid;
        final TargetMessage msg = new TargetMessage(ProxyPlugin.MSG_TYPE_PLAYER_IGNORE_LIST_REQ, playerOid, playerOid);
        try {
            final List<OID> ignoreList = (List<OID>)Engine.getAgent().sendRPCReturnObject(msg);
            playerCon.groupMember.initializeIgnoredSpeakers(ignoreList);
        }
        catch (Exception NoRecipientsException) {
            Log.error("VoicePlugin.initializeIgnoreList: Could not retrieve ignore list for player " + playerCon.playerOid);
            playerCon.groupMember.initializeIgnoredSpeakers(new LinkedList<OID>());
        }
    }
    
    protected void trackNewPerceiver(final PositionalGroupMember member) {
        final OID memberOid = member.getMemberOid();
        final WorldManagerClient.ObjectInfo info = WorldManagerClient.getObjectInfo(memberOid);
        member.wnode = new InterpolatedWorldNode(info);
        final PositionalVoiceGroup pGroup = (PositionalVoiceGroup)member.getGroup();
        pGroup.addTrackedPerceiver(member, info.instanceOid);
        this.addToPerceptionFilter(memberOid);
    }
    
    protected void processIgnoreListChangeMessage(final VoiceConnection playerCon, final AOByteBuffer buf) {
        final GroupMember member = playerCon.group.isMember(playerCon.playerOid);
        if (member == null) {
            Log.error("VoicePlugin.processBlacklistChangeMessage: player " + playerCon.playerOid + " is not associated with a group member!");
            return;
        }
        final int count = buf.getShort();
        final List<OID> addToIgnored = new LinkedList<OID>();
        final List<OID> removeFromIgnored = new LinkedList<OID>();
        for (int i = 0; i < count; ++i) {
            final byte which = buf.getByte();
            final OID oid = buf.getOID();
            if (which != 0) {
                addToIgnored.add(oid);
            }
            else {
                removeFromIgnored.add(oid);
            }
        }
        member.addIgnoredSpeakerOids(addToIgnored);
        member.removeIgnoredSpeakerOids(removeFromIgnored);
    }
    
    protected void addToPerceptionFilter(final OID playerOid) {
        if (Log.loggingDebug) {
            Log.debug("VoicePlugin.addToPerceptionFilter: Adding playerOid " + playerOid);
        }
        if (this.perceptionFilter.addTarget(playerOid)) {
            final FilterUpdate filterUpdate = new FilterUpdate(1);
            filterUpdate.addFieldValue(1, playerOid);
            Engine.getAgent().applyFilterUpdate(this.perceptionSubId, filterUpdate);
        }
        else if (Log.loggingDebug) {
            Log.debug("VoicePlugin.addToPerceptionFilter: PlayerOid " + playerOid + " was already in the filter");
        }
    }
    
    protected void removeFromPerceptionFilter(final OID playerOid) {
        if (this.perceptionFilter.hasTarget(playerOid)) {
            this.perceptionFilter.removeTarget(playerOid);
            final FilterUpdate filterUpdate = new FilterUpdate(1);
            filterUpdate.removeFieldValue(1, playerOid);
            Engine.getAgent().applyFilterUpdate(this.perceptionSubId, filterUpdate);
        }
        else if (Log.loggingDebug) {
            Log.debug("VoicePlugin.removeFromPerceptionFilter: PlayerOid " + playerOid + " was not in the filter");
        }
    }
    
    public void removePlayerFromGroup(final VoiceConnection playerCon) {
        if (playerCon.group == null) {
            Log.error("VoicePlugin.removePlayerFromGroup: playerCon " + playerCon + " group is null!");
        }
        else {
            playerCon.group.removeMember(playerCon.playerOid);
            playerCon.group = null;
            playerCon.groupOid = null;
            playerCon.authToken = null;
        }
    }
    
    protected VoiceGroup findVoiceGroup(final OID groupOid) {
        VoiceGroup group = this.conMgr.getGroup(groupOid);
        if (group == null) {
            if (!VoicePlugin.createGroupWhenReferenced) {
                return null;
            }
            group = new NonpositionalVoiceGroup(groupOid, null, this, VoicePlugin.maxVoiceChannels);
            this.conMgr.addGroup(groupOid, group);
        }
        return group;
    }
    
    public void addGroup(final OID groupOid, final VoiceGroup group) {
        this.conMgr.addGroup(groupOid, group);
    }
    
    public void removeGroup(final OID groupOid) {
        final List<OID> playerOids = this.conMgr.groupPlayers(groupOid);
        for (final OID playerOid : playerOids) {
            this.expungeVoiceClient(playerOid);
        }
    }
    
    private void sendPacketToListener(final VoiceConnection listener, final AOByteBuffer buf) {
        VoicePlugin.countPacketsSent.add();
        listener.con.send(buf);
    }
    
    @Override
    public void sendAllocateVoice(final VoiceConnection speaker, final VoiceConnection listener, final byte voiceNumber, final boolean positional) {
        this.sendAllocateVoice(speaker, listener, voiceNumber, (byte)(positional ? 3 : 2));
    }
    
    @Override
    public void sendAllocateVoice(final VoiceConnection speaker, final VoiceConnection listener, final byte voiceNumber, final byte opcode) {
        final short msgSize = (short)VoicePlugin.voiceMsgSize[opcode];
        final AOByteBuffer buf = new AOByteBuffer(msgSize);
        buf.putShort(speaker.seqNum);
        buf.putByte(opcode);
        buf.putByte(voiceNumber);
        buf.putOID(speaker.playerOid);
        if (Log.loggingDebug) {
            Log.debug("VoicePlugin.sendAllocateVoice: speaker " + speaker + ", listener " + listener + ", voiceNumber " + voiceNumber + ", opcode " + opcodeString(opcode) + ", seqNum " + speaker.seqNum + ", oid " + speaker.playerOid + " " + DebugUtils.byteArrayToHexString(buf));
        }
        VoicePlugin.countAllocateVoiceSent.add();
        this.sendPacketToListener(listener, buf);
    }
    
    @Override
    public void sendDeallocateVoice(final VoiceConnection speaker, final VoiceConnection listener, final byte voiceNumber) {
        final AOByteBuffer buf = new AOByteBuffer(4);
        buf.putShort(speaker.seqNum);
        buf.putByte((byte)5);
        buf.putByte(voiceNumber);
        if (Log.loggingDebug) {
            Log.debug("VoicePlugin.sendDeallocateVoice: speaker " + speaker + ", listener " + listener + ", voiceNumber " + voiceNumber + ", seqNum " + speaker.seqNum + ", oid " + speaker.playerOid + " " + DebugUtils.byteArrayToHexString(buf));
        }
        VoicePlugin.countDeallocateVoiceSent.add();
        this.sendPacketToListener(listener, buf);
    }
    
    @Override
    public void sendVoiceFrame(final VoiceConnection speaker, final VoiceConnection listener, final byte opcode, final byte voiceNumber, final AOByteBuffer sourceBuf, final short pktLength) {
        if (Log.loggingNet) {
            Log.net("VoicePlugin.sendVoiceFrame: length " + pktLength + ", speaker " + speaker + ", listener " + listener + ", packet " + DebugUtils.byteArrayToHexString(sourceBuf));
        }
        final AOByteBuffer buf = new AOByteBuffer(pktLength);
        buf.putShort(speaker.seqNum);
        buf.putByte(opcode);
        buf.putByte(voiceNumber);
        buf.putBytes(sourceBuf.array(), 4, pktLength - 4);
        VoicePlugin.countDataFramesSent.add();
        this.sendPacketToListener(listener, buf);
    }
    
    public void sendLoginStatus(final VoiceConnection receiver, final OID playerOid, final boolean login) {
        VoicePlugin.countSendLoginStatus.add();
        final short msgSize = (short)VoicePlugin.voiceMsgSize[8];
        final AOByteBuffer buf = new AOByteBuffer(msgSize);
        buf.putShort(this.loginStatusSeqNum = incSeqNum(this.loginStatusSeqNum));
        buf.putByte((byte)8);
        buf.putByte((byte)(login ? 1 : 0));
        buf.putOID(playerOid);
        if (Log.loggingDebug) {
            Log.debug("VoicePlugin.sendLoginStatus: receiver " + receiver + ", opcode " + opcodeString((byte)8) + ", seqNum " + this.loginStatusSeqNum + " " + DebugUtils.byteArrayToHexString(buf));
        }
        receiver.con.send(buf);
    }
    
    @Override
    public void sendExtensionMessage(final WorldManagerClient.ExtensionMessage msg) {
        Engine.getAgent().sendBroadcast(msg);
    }
    
    protected BufferedOutputStream openSpeexVoiceFile(final OID oid) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream("Voice-" + oid + ".speex");
            final BufferedOutputStream bufferedStream = new BufferedOutputStream(fileOutputStream);
            return bufferedStream;
        }
        catch (Exception e) {
            Log.exception("VoicePlugin.openSpeexVoiceFile: Exception opening file for oid " + oid, e);
            return null;
        }
    }
    
    public VoiceConnection getConnectionData(final ClientConnection con) {
        final VoiceConnection data = (VoiceConnection)con.getAssociation();
        if (data == null) {
            final String s = "getConnectionData: Could not find connection " + this.formatCon(con);
            Log.dumpStack(s);
            return null;
        }
        return data;
    }
    
    public String formatCon(final ClientConnection con) {
        return con.toString();
    }
    
    @Override
    public void connectionReset(final ClientConnection con) {
        final VoiceConnection data = this.getConnectionData(con);
        if (data == null) {
            Log.error("VoicePlugin.connectionReset: Could not find connection " + con);
        }
        else {
            final OID playerOid = data.playerOid;
            Log.info("VoicePlugin: DISCONNECT remote=" + data.con.IPAndPort() + " playerOid=" + playerOid);
            if (data.recordSpeexStream != null) {
                try {
                    data.recordSpeexStream.close();
                }
                catch (IOException e) {
                    Log.exception("VoicePlugin.connectionReset: Exception closing record stream", e);
                }
                data.recordSpeexStream = null;
            }
            this.expungeVoiceClient(playerOid, data);
        }
    }
    
    protected void expungeVoiceClient(final OID playerOid) {
        this.expungeVoiceClient(playerOid, this.conMgr.getPlayerCon(playerOid));
    }
    
    protected void expungeVoiceClient(final OID playerOid, final VoiceConnection con) {
        if (con.groupMember != null && con.groupMember.getExpunged()) {
            return;
        }
        final VoiceGroup group = con.group;
        if (group != null) {
            con.group = null;
            if (group.isPositional()) {
                final PositionalVoiceGroup pGroup = (PositionalVoiceGroup)group;
                pGroup.removeTrackedPerceiver(playerOid);
                if (con.groupMember != null) {
                    this.removeFromPerceptionFilter(playerOid);
                }
                else if (Log.loggingDebug) {
                    Log.debug("VoicePlugin.expungeVoiceClient: For playerOid " + playerOid + ", con.groupMember is null");
                }
            }
            final GroupMember member = con.groupMember;
            if (member == null) {
                Log.info("VoicePlugin.expungeVoiceClient: For playerOid " + playerOid + ", could not find member in group " + group);
            }
            else {
                member.setExpunged();
                group.removeMember(playerOid);
            }
        }
        con.groupMember = null;
        this.conMgr.removePlayer(playerOid);
        if (Log.loggingDebug) {
            Log.debug("VoicePlugin.expungeVoiceClient: PlayerOid " + playerOid + " expunged");
        }
    }
    
    public static short incSeqNum(final short original) {
        if (original == 32767) {
            return -32768;
        }
        return (short)(original + 1);
    }
    
    public static short incSeqNum(final short original, final int byWhat) {
        final int sum = byWhat + original;
        if (sum >= 32767) {
            return (short)(-32768 + (sum - 32767));
        }
        return (short)(original + byWhat);
    }
    
    public static int encodedFrameSizeForMode(int mode, final boolean wideBand) {
        if (wideBand) {
            if (mode < 0 || mode > 4) {
                Log.error("VoicePlugin.encodedFrameSizeForMode: wide-band mode " + mode + " is outside the range of 0-4");
                mode = 3;
            }
            return VoicePlugin.speexNarrowBandFrameSize[mode] + VoicePlugin.speexWideBandFrameSize[mode];
        }
        if (mode < 0 || mode > 8) {
            Log.error("VoicePlugin.encodedFrameSizeForMode: narrow-band mode " + mode + " is outside the range of 0-8");
            mode = 5;
        }
        return VoicePlugin.speexWideBandFrameSize[mode];
    }
    
    public static int encodedFrameSizeFromFirstByte(final byte b) {
        if ((b & 0x80) != 0x0) {
            return encodedFrameSizeForMode((b & 0x70) >> 4, true);
        }
        return encodedFrameSizeForMode((b & 0x78) >> 3, true);
    }
    
    public static String opcodeString(final byte opcode) {
        String name;
        if (opcode >= 0 && opcode <= 9) {
            name = VoicePlugin.opcodeNames[opcode];
        }
        else {
            Log.error("VoicePlugin.opcodeString: opcode " + opcode + " is out of range!");
            name = "????????";
        }
        return name + "(" + opcode + ")";
    }
    
    static {
        VoicePlugin.opcodeNames = new String[] { "Unallocd", "Auth    ", "Alloc   ", "AllocPos", "Confirm ", "Dealloc ", "Data    ", "AggrData", "LgnStatus" };
        VoicePlugin.voiceMsgSize = new int[] { 0, 25, 12, 12, 12, 4, 4, 5, 12, 6 };
        VoicePlugin.lengthBytes = 2;
        VoicePlugin.speexNarrowBandFrameSize = new int[] { 1, 6, 15, 20, 28, 38, 46, 62, 10 };
        VoicePlugin.speexWideBandFrameSize = new int[] { 1, 5, 14, 24, 44 };
        VoicePlugin.maxVoiceChannels = 4;
        VoicePlugin.runHistograms = false;
        VoicePlugin.checkAuthToken = true;
        VoicePlugin.createGroupWhenReferenced = false;
        VoicePlugin.allowVoiceBots = false;
        VoicePlugin.audibleRadius = 20.0f;
        VoicePlugin.hystericalMargin = 3.0f;
        VoicePlugin.instance = null;
    }
    
    class LoginHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            final OID instanceOid = message.getInstanceOid();
            Log.debug("LoginHook: playerOid=" + playerOid + " instanceOid=" + instanceOid);
            Engine.getAgent().sendResponse(new ResponseMessage(message));
            VoicePlugin.this.sendLoginStatusToReceivers(playerOid, true);
            return true;
        }
    }
    
    class InstanceUnloadedHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final SubjectMessage message = (SubjectMessage)msg;
            final OID instanceOid = message.getSubject();
            final Set<PositionalVoiceGroup> groups = VoicePlugin.this.conMgr.removeInstance(instanceOid);
            if (groups != null) {
                for (final PositionalVoiceGroup group : groups) {
                    group.unloadInstance(instanceOid);
                }
            }
            Engine.getAgent().sendResponse(new ResponseMessage(message));
            return true;
        }
    }
    
    class VoiceClientMessageHook implements Hook
    {
        @Override
        public boolean processMessage(final Message amsg, final int flags) {
            final WorldManagerClient.ExtensionMessage msg = (WorldManagerClient.ExtensionMessage)amsg;
            final String opcode = (String)msg.getProperty("opcode");
            if (Log.loggingDebug) {
                Log.debug("VoiceClientMessageHook.processMessage: Received VoiceClient msg for opcode " + opcode);
            }
            int returnCode = 1;
            if (opcode.equals("getPlayerGroup")) {
                OID groupOid = null;
                final OID memberOid = (OID)msg.getProperty("memberOid");
                if (memberOid != null) {
                    final VoiceGroup group = VoicePlugin.this.conMgr.getPlayerGroup(memberOid);
                    if (group != null) {
                        groupOid = group.getGroupOid();
                    }
                }
                Engine.getAgent().sendResponse(new OIDResponseMessage(amsg, groupOid));
                return true;
            }
            OID groupOid = (OID)msg.getProperty("groupOid");
            if (opcode.equals("addVoiceGroup")) {
                if (VoicePlugin.this.conMgr.getGroup(groupOid) != null) {
                    returnCode = -2;
                }
                else {
                    final Integer maxVoices = (Integer)msg.getProperty("maxVoices");
                    final Boolean positional = (Boolean)msg.getProperty("positional");
                    if (maxVoices == null) {
                        returnCode = -8;
                    }
                    else if (positional == null) {
                        returnCode = -9;
                    }
                    else {
                        final VoiceGroup group2 = ((boolean)positional) ? new PositionalVoiceGroup(groupOid, null, VoicePlugin.this, maxVoices, VoicePlugin.audibleRadius, VoicePlugin.hystericalMargin) : new NonpositionalVoiceGroup(groupOid, null, VoicePlugin.this, maxVoices);
                        VoicePlugin.this.addGroup(groupOid, group2);
                    }
                }
            }
            else {
                final VoiceGroup group3 = VoicePlugin.this.conMgr.getGroup(groupOid);
                if (group3 == null) {
                    returnCode = -1;
                }
                else if (opcode.equals("removeVoiceGroup")) {
                    VoicePlugin.this.removeGroup(groupOid);
                }
                else if (opcode.equals("isPositional")) {
                    returnCode = this.successTrueOrFalse(group3.isPositional());
                }
                else if (opcode.equals("setAllowedMembers")) {
                    group3.setAllowedMembers((Set<OID>)msg.getProperty("allowedMembers"));
                }
                else {
                    if (opcode.equals("getAllowedMembers")) {
                        Engine.getAgent().sendResponse(new GenericResponseMessage(amsg, group3.getAllowedMembers()));
                        return true;
                    }
                    final OID memberOid2 = (OID)msg.getProperty("memberOid");
                    if (memberOid2 == null) {
                        returnCode = -3;
                    }
                    else if (opcode.equals("addMember")) {
                        final VoiceConnection con = VoicePlugin.this.conMgr.getPlayerCon(memberOid2);
                        if (con == null) {
                            returnCode = -6;
                        }
                        else {
                            group3.addMember(memberOid2, con, (int)msg.getProperty("priority"), (boolean)msg.getProperty("allowedSpeaker"));
                        }
                    }
                    else if (opcode.equals("isMember")) {
                        returnCode = this.successTrueOrFalse(group3.isMember(memberOid2) != null);
                    }
                    else if (opcode.equals("addMemberAllowed")) {
                        returnCode = this.successTrueOrFalse(group3.addMemberAllowed(memberOid2));
                    }
                    else if (group3.isMember(memberOid2) == null) {
                        returnCode = -3;
                    }
                    else if (opcode.equals("removeMember")) {
                        group3.removeMember(memberOid2);
                    }
                    else if (opcode.equals("isMemberSpeaking")) {
                        returnCode = this.successTrueOrFalse(group3.isMemberSpeaking(memberOid2));
                    }
                    else if (opcode.equals("isListener")) {
                        returnCode = this.successTrueOrFalse(group3.isListener(memberOid2));
                    }
                    else {
                        final Boolean add = (Boolean)msg.getProperty("add");
                        if (add == null) {
                            returnCode = -7;
                        }
                        else if (opcode.equals("setAllowedSpeaker")) {
                            group3.setAllowedSpeaker(memberOid2, add);
                        }
                        else if (opcode.equals("setMemberSpeaking")) {
                            group3.setMemberSpeaking(memberOid2, add);
                        }
                        else if (opcode.equals("setListener")) {
                            group3.setListener(memberOid2, add);
                        }
                        else {
                            returnCode = -5;
                        }
                    }
                }
            }
            if (Log.loggingDebug) {
                Log.debug("VoiceClientMessageHook.processMessage: Response to VoiceClient msg for opcode " + opcode + " is returnCode " + returnCode);
            }
            Engine.getAgent().sendResponse(new IntegerResponseMessage(amsg, Integer.valueOf(returnCode)));
            return true;
        }
        
        protected int successTrueOrFalse(final boolean which) {
            return which ? 2 : 3;
        }
    }
    
    class PerceptionHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final PerceptionMessage perceptionMessage = (PerceptionMessage)msg;
            final OID perceiverOid = perceptionMessage.getTarget();
            final PositionalGroupMember perceiverMember = VoicePlugin.this.conMgr.getPositionalMember(perceiverOid);
            if (perceiverMember != null) {
                final List<PerceptionMessage.ObjectNote> gain = perceptionMessage.getGainObjects();
                final List<PerceptionMessage.ObjectNote> lost = perceptionMessage.getLostObjects();
                if (Log.loggingDebug) {
                    Log.debug("PerceptionHook.processMessage: perceiverOid " + perceiverOid + ", instanceOid=" + perceiverMember.getInstanceOid() + " " + ((gain == null) ? 0 : gain.size()) + " gain and " + ((lost == null) ? 0 : lost.size()) + " lost");
                }
                if (gain != null) {
                    for (final PerceptionMessage.ObjectNote note : gain) {
                        this.processNote(perceiverOid, perceiverMember, note, true);
                    }
                }
                if (lost != null) {
                    for (final PerceptionMessage.ObjectNote note : lost) {
                        this.processNote(perceiverOid, perceiverMember, note, false);
                    }
                }
            }
            else if (Log.loggingDebug) {
                Log.debug("PerceptionHook.processMessage: Could not find PositionalGroupMember for player " + perceiverOid);
            }
            return true;
        }
        
        protected void processNote(final OID perceiverOid, final PositionalGroupMember perceiverMember, final PerceptionMessage.ObjectNote note, final boolean add) {
            final OID perceivedOid = note.getSubject();
            final PositionalVoiceGroup group = (PositionalVoiceGroup)VoicePlugin.this.conMgr.getPlayerGroup(perceiverOid);
            final ObjectType objType = note.getObjectType();
            if (objType.isPlayer()) {
                group.maybeChangePerceivedObject(perceiverMember, perceivedOid, add);
            }
        }
    }
    
    class UpdateWorldNodeHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.UpdateWorldNodeMessage wnodeMsg = (WorldManagerClient.UpdateWorldNodeMessage)msg;
            final OID playerOid = wnodeMsg.getSubject();
            final PositionalGroupMember perceiverMember = VoicePlugin.this.conMgr.getPositionalMember(playerOid);
            if (perceiverMember != null && perceiverMember.wnode != null) {
                final BasicWorldNode bwnode = wnodeMsg.getWorldNode();
                if (Log.loggingDebug) {
                    Log.debug("VoicePlugin.handleMessage: UpdateWnode for " + playerOid + ", loc " + bwnode.getLoc() + ", dir " + bwnode.getDir());
                }
                final PositionalVoiceGroup group = (PositionalVoiceGroup)perceiverMember.getGroup();
                group.updateWorldNode(perceiverMember, bwnode);
            }
            return true;
        }
    }
    
    class SpawnedHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.SpawnedMessage spawnedMsg = (WorldManagerClient.SpawnedMessage)msg;
            final OID playerOid = spawnedMsg.getSubject();
            final OID instanceOid = spawnedMsg.getInstanceOid();
            final PositionalGroupMember member = VoicePlugin.this.conMgr.getPositionalMember(playerOid);
            if (member != null && member.wnode == null) {
                if (Log.loggingDebug) {
                    Log.debug("SpawnedHook.processMessage: playerOid " + playerOid + " spawned, instanceOid " + instanceOid);
                }
                VoicePlugin.this.trackNewPerceiver(member);
            }
            else if (Log.loggingDebug) {
                Log.debug("SpawnedHook.processMessage: playerOid " + playerOid + " spawn ignored, instanceOid " + instanceOid);
            }
            return true;
        }
    }
    
    class DespawnedHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.DespawnedMessage despawnedMsg = (WorldManagerClient.DespawnedMessage)msg;
            final OID playerOid = despawnedMsg.getSubject();
            final PositionalGroupMember member = VoicePlugin.this.conMgr.getPositionalMember(playerOid);
            if (member != null && member.wnode != null) {
                if (Log.loggingDebug) {
                    Log.debug("DespawnedHook.processMessage: Despawn for " + playerOid + ", instanceOid " + member.getInstanceOid());
                }
                final PositionalVoiceGroup group = (PositionalVoiceGroup)member.getGroup();
                group.removeTrackedPerceiver(member);
                VoicePlugin.this.removeFromPerceptionFilter(playerOid);
            }
            else if (Log.loggingDebug) {
                Log.debug("DespawnedHook.processMessage: Ignored despawn for player " + playerOid + " because " + ((member != null) ? "member was null" : "member.wnode wasn't null"));
            }
            return true;
        }
    }
    
    class GetPluginStatusHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final LinkedHashMap<String, Serializable> status = new LinkedHashMap<String, Serializable>();
            status.put("plugin", VoicePlugin.this.getName());
            status.put("voice_user", VoicePlugin.this.conMgr.getPlayerCount());
            status.put("voice_alloc", VoicePlugin.countAllocateVoiceReceived.getCount());
            status.put("voice_frame", VoicePlugin.countDataFramesReceived.getCount());
            Engine.getAgent().sendObjectResponse(msg, status);
            return true;
        }
    }
    
    class RelayUpdatePlayerIgnoreListHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage extMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = extMsg.getSubject();
            final VoiceConnection playerCon = VoicePlugin.this.conMgr.getPlayerCon(playerOid);
            if (playerCon == null) {
                return true;
            }
            final GroupMember member = playerCon.groupMember;
            member.applyIgnoreUpdateMessage(extMsg);
            return true;
        }
    }
    
    public static class VoiceConManager
    {
        private Map<OID, VoiceConnection> playerOidToVoiceConnectionMap;
        private Map<OID, VoiceGroup> groupOidToGroupMap;
        private Map<OID, VoiceGroup> playerOidToGroupMap;
        private Map<OID, Set<PositionalVoiceGroup>> groupsInInstance;
        protected transient Lock lock;
        
        public VoiceConManager() {
            this.playerOidToVoiceConnectionMap = new HashMap<OID, VoiceConnection>();
            this.groupOidToGroupMap = new HashMap<OID, VoiceGroup>();
            this.playerOidToGroupMap = new HashMap<OID, VoiceGroup>();
            this.groupsInInstance = new HashMap<OID, Set<PositionalVoiceGroup>>();
            this.lock = LockFactory.makeLock("VoiceConManager");
        }
        
        public void addGroup(final OID groupOid, final VoiceGroup group) {
            this.lock.lock();
            try {
                this.groupOidToGroupMap.put(groupOid, group);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public List<OID> groupPlayers(final OID groupOid) {
            final List<OID> playerOids = new LinkedList<OID>();
            final VoiceGroup group = this.getGroup(groupOid);
            if (group == null) {
                Log.error("VoicePlugin.VoiceConManager.groupPlayers: There is no group associated with groupOid " + groupOid);
            }
            else {
                this.lock.lock();
                try {
                    for (final Map.Entry<OID, VoiceGroup> entry : this.playerOidToGroupMap.entrySet()) {
                        if (entry.getValue() == group) {
                            playerOids.add(entry.getKey());
                        }
                    }
                }
                finally {
                    this.lock.unlock();
                }
            }
            return playerOids;
        }
        
        public VoiceGroup getGroup(final OID groupOid) {
            this.lock.lock();
            try {
                return this.groupOidToGroupMap.get(groupOid);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public void removePlayer(final OID playerOid) {
            this.lock.lock();
            try {
                this.playerOidToVoiceConnectionMap.remove(playerOid);
                this.playerOidToGroupMap.remove(playerOid);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public VoiceConnection getPlayerCon(final OID playerOid) {
            this.lock.lock();
            try {
                return this.playerOidToVoiceConnectionMap.get(playerOid);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public void setPlayerCon(final OID playerOid, final VoiceConnection con) {
            this.lock.lock();
            try {
                this.playerOidToVoiceConnectionMap.put(playerOid, con);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public void setPlayerGroup(final OID playerOid, final VoiceGroup group) {
            this.lock.lock();
            try {
                this.playerOidToGroupMap.put(playerOid, group);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public VoiceGroup getPlayerGroup(final OID playerOid) {
            this.lock.lock();
            try {
                return this.playerOidToGroupMap.get(playerOid);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public PositionalGroupMember getPositionalMember(final OID playerOid) {
            this.lock.lock();
            try {
                final VoiceGroup group = this.playerOidToGroupMap.get(playerOid);
                if (group != null && group.isPositional()) {
                    return (PositionalGroupMember)group.isMember(playerOid);
                }
                return null;
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public List<GroupMember> getAllPositionalGroupMembers() {
            final List<GroupMember> pGroupMembers = new LinkedList<GroupMember>();
            this.lock.lock();
            try {
                for (final VoiceGroup group : this.groupOidToGroupMap.values()) {
                    if (group.isPositional()) {
                        group.getAllMembers(pGroupMembers);
                    }
                }
                return pGroupMembers;
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public boolean maybeAddToGroupInstances(final OID instanceOid, final PositionalVoiceGroup group) {
            this.lock.lock();
            try {
                Set<PositionalVoiceGroup> groups = this.groupsInInstance.get(instanceOid);
                if (groups == null) {
                    groups = new HashSet<PositionalVoiceGroup>();
                    this.groupsInInstance.put(instanceOid, groups);
                }
                return groups.add(group);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public Set<PositionalVoiceGroup> removeInstance(final OID instanceOid) {
            this.lock.lock();
            try {
                return this.groupsInInstance.remove(instanceOid);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public int getPlayerCount() {
            return this.playerOidToVoiceConnectionMap.size();
        }
    }
    
    class Updater implements Runnable
    {
        @Override
        public void run() {
            while (VoicePlugin.this.running) {
                try {
                    this.update();
                }
                catch (AORuntimeException e) {
                    Log.exception("ProximityTracker.Updater.run caught AORuntimeException", e);
                }
                catch (Exception e2) {
                    Log.exception("ProximityTracker.Updater.run caught exception", e2);
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e3) {
                    Log.warn("Updater: " + e3);
                    e3.printStackTrace();
                }
            }
        }
        
        protected void update() {
            final List<GroupMember> pMembers = VoicePlugin.this.conMgr.getAllPositionalGroupMembers();
            for (final GroupMember member : pMembers) {
                final PositionalGroupMember pMember = (PositionalGroupMember)member;
                if (pMember.wnode == null) {
                    continue;
                }
                pMember.previousLoc = pMember.lastLoc;
                pMember.lastLoc = pMember.wnode.getLoc();
            }
            for (final GroupMember member : pMembers) {
                final PositionalGroupMember pMember = (PositionalGroupMember)member;
                if (pMember.wnode == null) {
                    continue;
                }
                if (Log.loggingDebug) {
                    Log.debug("Updater.update: perceiverOid " + pMember.getMemberOid() + " previousLoc " + pMember.previousLoc + ", lastLoc " + pMember.lastLoc);
                }
                if (pMember.previousLoc != null && Point.distanceToSquared(pMember.previousLoc, pMember.lastLoc) < 100.0f) {
                    continue;
                }
                final ArrayList<OID> perceivedOids = new ArrayList<OID>(pMember.perceivedOids);
                if (Log.loggingDebug) {
                    Log.debug("Updater.update: perceiverOid " + pMember.getMemberOid() + " has " + perceivedOids.size() + " perceivedOids");
                }
                for (final OID perceivedOid : perceivedOids) {
                    final VoiceConnection con = VoicePlugin.this.conMgr.getPlayerCon(perceivedOid);
                    if (con == null) {
                        continue;
                    }
                    final PositionalGroupMember perceivedMember = (PositionalGroupMember)con.groupMember;
                    if (perceivedMember == null) {
                        continue;
                    }
                    if (perceivedMember.wnode == null) {
                        continue;
                    }
                    final PositionalVoiceGroup pGroup = (PositionalVoiceGroup)pMember.getGroup();
                    pGroup.testProximity(pMember, perceivedMember, false, false);
                }
            }
        }
    }
}
