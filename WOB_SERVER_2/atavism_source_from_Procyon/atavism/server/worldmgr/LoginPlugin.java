// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.worldmgr;

import java.util.ArrayList;
import java.util.LinkedList;
import atavism.server.messages.PropertyMessage;
import atavism.server.util.ServerVersion;
import atavism.server.util.AORuntimeException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import atavism.server.util.NamedThreadFactory;
import java.util.List;
import atavism.server.engine.PluginStatus;
import java.util.Iterator;
import atavism.server.util.SecureTokenSpec;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import atavism.server.util.SecureToken;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.util.SecureTokenManager;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import atavism.server.engine.Database;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import atavism.server.network.TcpServer;
import java.util.concurrent.ExecutorService;
import atavism.server.network.TcpAcceptCallback;
import atavism.server.engine.EnginePlugin;

public class LoginPlugin extends EnginePlugin implements TcpAcceptCallback
{
    public static final int MSGCODE_CHARACTER_RESPONSE = 2;
    public static final int MSGCODE_CHARACTER_DELETE = 3;
    public static final int MSGCODE_CHARACTER_DELETE_RESPONSE = 4;
    public static final int MSGCODE_CHARACTER_CREATE = 5;
    public static final int MSGCODE_CHARACTER_CREATE_RESPONSE = 6;
    public static final int MSGCODE_CHARACTER_REQUEST = 7;
    public static final int MSGCODE_CHARACTER_SELECT_REQUEST = 8;
    public static final int MSGCODE_CHARACTER_SELECT_RESPONSE = 9;
    public static final int MSGCODE_SECURE_CHARACTER_REQUEST = 10;
    public static final int LOGIN_IDLE_TIMEOUT = 2000000;
    private static ExecutorService threadPool;
    private TcpServer loginListener;
    private Integer tcpPort;
    private Object characterCreateLock;
    public static boolean SecureToken;
    public static long TokenValidTime;
    public static Integer WorldId;
    private static CharacterGenerator characterGenerator;
    
    public LoginPlugin() {
        super("Login");
        this.loginListener = null;
        this.tcpPort = null;
        this.characterCreateLock = new Object();
        this.setPluginType("Login");
        final String secureTokenString = Engine.getProperty("atavism.security.secure_token");
        Log.debug("constructor: secureTokenString=" + secureTokenString);
        if (secureTokenString != null) {
            final Boolean secureToken = Boolean.parseBoolean(secureTokenString);
            LoginPlugin.SecureToken = secureToken;
        }
    }
    
    public void dbConnect() {
        if (Engine.getDatabase() == null) {
            Log.debug("Setting Database in WorldManager.dbConnect");
            Engine.setDatabase(new Database(Engine.getDBDriver()));
        }
        Engine.getDatabase().connect(Engine.getDBUrl(), Engine.getDBUser(), Engine.getDBPassword());
    }
    
    public void setTCPPort(final int port) {
        this.tcpPort = port;
    }
    
    public int getTCPPort() {
        if (this.tcpPort == null) {
            return Engine.getWorldMgrPort();
        }
        return this.tcpPort;
    }
    
    @Override
    public void onActivate() {
        try {
            (this.loginListener = new TcpServer(this.getTCPPort())).registerAcceptCallback(this);
            this.loginListener.start();
        }
        catch (Exception e) {
            Log.exception("LoginPlugin.onActivate caught exception", e);
            System.exit(1);
        }
    }
    
    private static String socketToString(final SocketChannel channel) {
        final Socket socket = channel.socket();
        return "remote=" + socket.getRemoteSocketAddress() + " local=" + socket.getLocalSocketAddress();
    }
    
    @Override
    public void onTcpAccept(final SocketChannel clientSocket) {
        try {
            Log.info("LoginPlugin: CONNECTION " + socketToString(clientSocket));
            LoginPlugin.threadPool.execute(new SocketHandler(clientSocket));
        }
        catch (IOException e) {
            Log.exception("LoginListener: ", e);
        }
    }
    
    protected CharacterResponseMessage handleCharacterRequestMessage(final CharacterRequestMessage message, final SocketHandler clientSocket) {
        final AOByteBuffer authToken = message.getAuthToken();
        final SecureToken token = SecureTokenManager.getInstance().importToken(authToken);
        boolean valid = true;
        if (LoginPlugin.SecureToken) {
            valid = token.getValid();
        }
        if (!token.getIssuerId().equals("master")) {
            valid = false;
        }
        OID uid = null;
        final Serializable uidObj = token.getProperty("account_id");
        if (uidObj instanceof Integer) {
            uid = OID.fromLong((int)uidObj);
        }
        if (uidObj instanceof Long) {
            uid = OID.fromLong((long)uidObj);
        }
        if (uidObj instanceof String) {
            uid = OID.parseLong((String)uidObj);
        }
        final CharacterResponseMessage response = new CharacterResponseMessage();
        response.setServerVersion("2.5.0");
        response.setWorldFilesUrl(Engine.getProperty("atavism.world_files_url"));
        if (valid) {
            clientSocket.setAccountId(uid);
            clientSocket.setCharacterInfo(response.getCharacters());
        }
        else {
            response.setErrorMessage("invalid master token");
        }
        return response;
    }
    
    protected CharacterCreateResponseMessage handleCharacterCreateMessage(final CharacterCreateMessage message, final SocketHandler clientSocket) {
        final CharacterCreateResponseMessage response = new CharacterCreateResponseMessage();
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("status", Boolean.FALSE);
        props.put("errorMessage", "character creation is not supported");
        response.setProperties(props);
        return response;
    }
    
    protected CharacterDeleteResponseMessage handleCharacterDeleteMessage(final CharacterDeleteMessage message, final SocketHandler clientSocket) {
        final CharacterDeleteResponseMessage response = new CharacterDeleteResponseMessage();
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("status", Boolean.FALSE);
        props.put("errorMessage", "character deletion is not supported");
        response.setProperties(props);
        return response;
    }
    
    protected CharacterSelectResponseMessage handleCharacterSelectRequestMessage(final CharacterSelectRequestMessage message, final SocketHandler clientSocket) {
        boolean charAllowed = false;
        final OID characterOid = message.getProperties().get("characterId");
        Log.debug("CharacterSelectResponse: characterOid: " + characterOid + " and data: " + characterOid.toLong());
        String characterName = null;
        for (final Map<String, Serializable> charInfo : clientSocket.getCharacterInfo()) {
            Log.debug("CharacterSelectResponse: charInfo: " + charInfo.toString());
            if (charInfo.containsKey("characterId") && charInfo.get("characterId") instanceof OID && charInfo.get("characterId").equals(characterOid)) {
                charAllowed = true;
                characterName = charInfo.get("characterName");
                break;
            }
        }
        Log.info("LoginPlugin: SELECT_CHARACTER oid=" + characterOid + " name=" + characterName + " allowed=" + charAllowed);
        final CharacterSelectResponseMessage response = new CharacterSelectResponseMessage();
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        final SecureTokenSpec tokenSpec = new SecureTokenSpec((byte)2, Engine.getAgent().getName(), System.currentTimeMillis() + LoginPlugin.TokenValidTime);
        tokenSpec.setProperty("character_oid", characterOid);
        if (charAllowed) {
            final Map<String, Serializable> characterProps = new HashMap<String, Serializable>();
            final PluginStatus proxyStatus = this.selectProxyPlugin(characterProps);
            if (proxyStatus == null) {
                props.put("errorMessage", "Server not ready for login");
                response.setProperties(props);
                return response;
            }
            tokenSpec.setProperty("proxy_server", proxyStatus.agent_name);
            final byte[] token = SecureTokenManager.getInstance().generateToken(tokenSpec);
            this.setProxyProperties(props, proxyStatus);
            props.put("token", token);
        }
        else {
            props.put("errorMessage", "Character oid not allowed for this account");
        }
        response.setProperties(props);
        return response;
    }
    
    protected boolean setProxyProperties(final Map<String, Serializable> props, final PluginStatus proxy) {
        if (proxy == null) {
            return false;
        }
        final Map<String, String> info = Engine.makeMapOfString(proxy.info);
        String hostname = info.get("host");
        int port;
        try {
            hostname = info.get("host");
            port = Integer.parseInt(info.get("port"));
        }
        catch (Exception e) {
            Log.exception("setProxyProperties: proxy " + proxy.plugin_name + " invalid port number: " + info.get("port"), e);
            return false;
        }
        props.put("proxyHostname", hostname);
        props.put("proxyPort", port);
        if (Log.loggingDebug) {
            Log.debug("LoginPlugin: assigned proxy " + proxy.plugin_name + " host=" + proxy.host_name + " port=" + port);
        }
        return true;
    }
    
    public static CharacterGenerator getCharacterGenerator() {
        return LoginPlugin.characterGenerator;
    }
    
    protected final PluginStatus selectProxyPlugin(final Map<String, Serializable> characterProperties) {
        final List<PluginStatus> plugins = Engine.getDatabase().getPluginStatus("Proxy");
        final Iterator<PluginStatus> iterator = plugins.iterator();
        while (iterator.hasNext()) {
            final PluginStatus plugin = iterator.next();
            if (plugin.run_id != Engine.getAgent().getDomainStartTime()) {
                iterator.remove();
            }
        }
        if (plugins.size() == 0) {
            return null;
        }
        return this.selectBestProxy(plugins, characterProperties);
    }
    
    protected PluginStatus selectBestProxy(final List<PluginStatus> plugins, final Map<String, Serializable> characterProperties) {
        PluginStatus selection = null;
        int selectionPlayerCount = Integer.MAX_VALUE;
        for (final PluginStatus plugin : plugins) {
            final Map<String, String> status = Engine.makeMapOfString(plugin.status);
            int playerCount;
            try {
                playerCount = Integer.parseInt(status.get("players"));
            }
            catch (Exception e) {
                Log.exception("selectBestProxy: proxy " + plugin.plugin_name + " invalid player count: " + status.get("players"), e);
                continue;
            }
            if (playerCount < selectionPlayerCount) {
                selection = plugin;
                selectionPlayerCount = playerCount;
            }
        }
        return selection;
    }
    
    static {
        LoginPlugin.threadPool = Executors.newCachedThreadPool(new NamedThreadFactory("LoginConnection"));
        LoginPlugin.SecureToken = true;
        LoginPlugin.TokenValidTime = 60000L;
        LoginPlugin.WorldId = null;
        LoginPlugin.characterGenerator = new CharacterGenerator();
    }
    
    protected class SocketHandler implements Runnable
    {
        private SocketChannel clientSocket;
        private Selector selector;
        private SelectionKey clientSelection;
        private OID accountId;
        private String accountName;
        private List<Map<String, Serializable>> characterInfo;
        
        public SocketHandler(final SocketChannel socket) throws IOException {
            this.clientSocket = null;
            this.selector = null;
            this.clientSelection = null;
            this.accountId = null;
            this.accountName = null;
            this.characterInfo = null;
            this.clientSocket = socket;
            this.selector = Selector.open();
            this.clientSelection = this.clientSocket.register(this.selector, 1);
        }
        
        public SocketAddress getRemoteSocketAddress() {
            return this.clientSocket.socket().getRemoteSocketAddress();
        }
        
        public void setAccountId(final OID accountId) {
            this.accountId = accountId;
        }
        
        public OID getAccountId() {
            return this.accountId;
        }
        
        public void setAccountName(final String accountName) {
            this.accountName = accountName;
        }
        
        public String getAccountName() {
            return this.accountName;
        }
        
        public void setCharacterInfo(final List<Map<String, Serializable>> charInfo) {
            this.characterInfo = charInfo;
        }
        
        public List<Map<String, Serializable>> getCharacterInfo() {
            return this.characterInfo;
        }
        
        private int fillBuffer(final SocketChannel socket, final ByteBuffer buffer) throws IOException {
            this.clientSelection.interestOps(1);
            while (buffer.remaining() > 0) {
                final int nReady = this.selector.select(2000000L);
                if (nReady != 1) {
                    Log.debug("Connection timeout while reading");
                    break;
                }
                this.selector.selectedKeys().clear();
                final int nBytes = socket.read(buffer);
                if (nBytes == -1) {
                    break;
                }
            }
            buffer.flip();
            return buffer.limit();
        }
        
        private boolean writeBuffer(final ByteBuffer buffer) throws IOException {
            this.clientSelection.interestOps(4);
            while (buffer.hasRemaining()) {
                final int nReady = this.selector.select(2000000L);
                if (nReady != 1) {
                    Log.debug("Connection timeout while writing");
                    break;
                }
                this.selector.selectedKeys().clear();
                if (this.clientSocket.write(buffer) == 0) {
                    break;
                }
            }
            return !buffer.hasRemaining();
        }
        
        @Override
        public void run() {
            try {
                final ByteBuffer header = ByteBuffer.allocate(8);
                ByteBuffer responseBuf;
                do {
                    int nBytes = this.fillBuffer(this.clientSocket, header);
                    if (nBytes == 0) {
                        Log.info("LoginPlugin: DISCONNECT " + socketToString(this.clientSocket));
                        break;
                    }
                    if (nBytes < 8) {
                        Log.error("LoginPlugin: reading header nBytes " + nBytes);
                        break;
                    }
                    final int messageLength = header.getInt();
                    final int messageCode = header.getInt();
                    header.clear();
                    if (Log.loggingDebug) {
                        Log.debug("LoginPlugin: code " + messageCode + " (" + messageLength + " bytes)");
                    }
                    if (messageLength > 64000) {
                        Log.error("LoginPlugin: max message length exceeded");
                        break;
                    }
                    if (messageLength < 0) {
                        Log.error("LoginPlugin: invalid message length");
                        break;
                    }
                    if (messageLength == 4) {
                        Log.error("LoginPlugin: invalid message length (possibly an old client)");
                        break;
                    }
                    ByteBuffer message = null;
                    if (messageLength > 4) {
                        message = ByteBuffer.allocate(messageLength - 4);
                        nBytes = this.fillBuffer(this.clientSocket, message);
                        if (nBytes == -1 || nBytes != messageLength - 4) {
                            Log.error("LoginPlugin: error reading message body");
                            break;
                        }
                    }
                    responseBuf = this.dispatchMessage(messageCode, message, this);
                    if (responseBuf == null) {
                        break;
                    }
                } while (this.writeBuffer(responseBuf));
            }
            catch (InterruptedIOException e4) {
                Log.info("LoginPlugin: closed connection due to timeout");
            }
            catch (IOException e) {
                Log.exception("LoginPlugin.SocketHandler: ", e);
            }
            catch (AORuntimeException e2) {
                Log.exception("LoginPlugin.SocketHandler: ", e2);
            }
            catch (Exception e3) {
                Log.exception("LoginPlugin.SocketHandler: ", e3);
            }
            try {
                this.clientSelection.cancel();
                this.clientSocket.close();
                this.selector.close();
            }
            catch (Exception ex) {}
        }
        
        ByteBuffer dispatchMessage(final int messageCode, final ByteBuffer messageBuf, final SocketHandler clientSocket) throws IOException {
            ByteBuffer responseBuf = null;
            if (messageCode == 10) {
                final CharacterRequestMessage msg = new CharacterRequestMessage();
                final AOByteBuffer buffer = new AOByteBuffer(messageBuf);
                msg.clientVersion = buffer.getString();
                msg.authToken = buffer.getByteBuffer();
                if (Log.loggingDebug) {
                    Log.debug("LoginPlugin: SecureCharacterRequestMessage version=" + msg.clientVersion + " token=" + msg.authToken);
                }
                final int versionCompare = ServerVersion.compareVersionStrings(msg.clientVersion, "2.5.0");
                CharacterResponseMessage response;
                if (versionCompare != 1 && versionCompare != 0) {
                    response = new CharacterResponseMessage();
                    response.setErrorMessage("Unsupported client version");
                }
                else {
                    response = LoginPlugin.this.handleCharacterRequestMessage(msg, clientSocket);
                }
                if (response.getServerVersion() == null || response.getServerVersion().equals("")) {
                    response.setServerVersion("2.5.0");
                }
                responseBuf = response.getEncodedMessage();
            }
            else if (messageCode == 7) {
                final CharacterRequestMessage msg = new CharacterRequestMessage();
                final AOByteBuffer buffer = new AOByteBuffer(messageBuf);
                msg.clientVersion = buffer.getString();
                msg.authToken = buffer.getByteBuffer();
                if (Log.loggingDebug) {
                    Log.debug("LoginPlugin: CharacterRequestMessage version=" + msg.clientVersion + " token=" + msg.authToken);
                }
                final int versionCompare = ServerVersion.compareVersionStrings(msg.clientVersion, "2.5.0");
                CharacterResponseMessage response;
                if (versionCompare != 1 && versionCompare != 0) {
                    response = new CharacterResponseMessage();
                    response.setErrorMessage("Unsupported client version");
                }
                else {
                    response = LoginPlugin.this.handleCharacterRequestMessage(msg, clientSocket);
                }
                if (response.getServerVersion() == null || response.getServerVersion().equals("")) {
                    response.setServerVersion("2.5.0");
                }
                responseBuf = response.getEncodedMessage();
            }
            else if (messageCode == 5) {
                synchronized (LoginPlugin.this.characterCreateLock) {
                    final CharacterCreateMessage msg2 = new CharacterCreateMessage();
                    final AOByteBuffer aoBuf = new AOByteBuffer(messageBuf);
                    msg2.props = PropertyMessage.unmarshallProperyMap(aoBuf);
                    if (Log.loggingDebug) {
                        Log.debug("LoginPlugin: CharacterCreateMessage prop count=" + msg2.props.size());
                    }
                    final CharacterCreateResponseMessage response2 = LoginPlugin.this.handleCharacterCreateMessage(msg2, clientSocket);
                    responseBuf = response2.getEncodedMessage();
                }
            }
            else if (messageCode == 3) {
                final CharacterDeleteMessage msg3 = new CharacterDeleteMessage();
                final AOByteBuffer aoBuf2 = new AOByteBuffer(messageBuf);
                msg3.props = PropertyMessage.unmarshallProperyMap(aoBuf2);
                if (Log.loggingDebug) {
                    Log.debug("LoginPlugin: CharacterDeleteMessage prop count=" + msg3.props.size());
                }
                final CharacterDeleteResponseMessage response3 = LoginPlugin.this.handleCharacterDeleteMessage(msg3, clientSocket);
                responseBuf = response3.getEncodedMessage();
            }
            else if (messageCode == 8) {
                final CharacterSelectRequestMessage msg4 = new CharacterSelectRequestMessage();
                final AOByteBuffer aoBuf2 = new AOByteBuffer(messageBuf);
                msg4.props = PropertyMessage.unmarshallProperyMap(aoBuf2);
                if (Log.loggingDebug) {
                    Log.debug("LoginPlugin: CharacterSelectRequestMessage prop count=" + msg4.props.size());
                }
                final CharacterSelectResponseMessage response4 = LoginPlugin.this.handleCharacterSelectRequestMessage(msg4, clientSocket);
                responseBuf = response4.getEncodedMessage();
            }
            else {
                Log.error("Unknown message code " + messageCode);
            }
            return responseBuf;
        }
    }
    
    public static class CharacterRequestMessage
    {
        private AOByteBuffer authToken;
        private String clientVersion;
        
        public AOByteBuffer getAuthToken() {
            this.authToken.rewind();
            return this.authToken;
        }
        
        public void setAuthToken(final AOByteBuffer token) {
            this.authToken = token;
        }
        
        public String getClientVersion() {
            return this.clientVersion;
        }
        
        public void setClientVersion(final String version) {
            this.clientVersion = version;
        }
        
        public AOByteBuffer toBytes() {
            final AOByteBuffer buf = new AOByteBuffer(200);
            buf.putInt(10);
            buf.putString(this.clientVersion);
            buf.putByteBuffer(this.authToken);
            return buf;
        }
    }
    
    public static class CharacterResponseMessage
    {
        private String worldToken;
        private String serverVersion;
        private String errorMessage;
        private String worldFilesUrl;
        private OID account;
        private int characterSlots;
        private LinkedList<Map<String, Serializable>> characters;
        
        public CharacterResponseMessage() {
            this.worldToken = "";
            this.errorMessage = "";
            this.worldFilesUrl = "";
            this.account = null;
            this.characterSlots = 2;
            this.characters = new LinkedList<Map<String, Serializable>>();
        }
        
        public String getWorldToken() {
            return this.worldToken;
        }
        
        public void setWorldToken(final String worldToken) {
            this.worldToken = worldToken;
        }
        
        public String getServerVersion() {
            return this.serverVersion;
        }
        
        public void setServerVersion(final String serverVersion) {
            this.serverVersion = serverVersion;
        }
        
        public String getErrorMessage() {
            return this.errorMessage;
        }
        
        public void setErrorMessage(final String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public String getWorldFilesUrl() {
            return this.worldFilesUrl;
        }
        
        public void setWorldFilesUrl(final String worldFilesUrl) {
            this.worldFilesUrl = worldFilesUrl;
        }
        
        public OID getAccount() {
            return this.account;
        }
        
        public void setAccount(final OID account) {
            this.account = account;
        }
        
        public int getCharacterSlots() {
            return this.characterSlots;
        }
        
        public void setCharacterSlots(final int characterSlots) {
            this.characterSlots = characterSlots;
        }
        
        public void addCharacter(final Map<String, Serializable> characterInfo) {
            this.characters.add(characterInfo);
        }
        
        public List<Map<String, Serializable>> getCharacters() {
            return this.characters;
        }
        
        ByteBuffer getEncodedMessage() {
            if (Log.loggingDebug) {
                Log.debug("LoginPlugin: returning characters: serverVersion=" + this.serverVersion + " worldToken=" + this.worldToken + " errorMessage=" + this.errorMessage + " worldFilesUrl=" + this.worldFilesUrl + " nChars=" + this.characters.size());
            }
            final AOByteBuffer buffer = new AOByteBuffer(1024);
            buffer.putInt(0);
            buffer.putInt(2);
            buffer.putString(this.serverVersion);
            buffer.putString(this.worldToken);
            buffer.putString(this.errorMessage);
            buffer.putString(this.worldFilesUrl);
            buffer.putOID(this.account);
            buffer.putInt(this.characterSlots);
            buffer.putInt(this.characters.size());
            for (final Map<String, Serializable> properties : this.characters) {
                final List<String> propStrings = new ArrayList<String>();
                final int nProps = PropertyMessage.createPropertyString(propStrings, properties, "");
                buffer.putInt(nProps);
                for (final String s : propStrings) {
                    buffer.putString(s);
                }
            }
            final int len = buffer.position();
            buffer.getNioBuf().rewind();
            buffer.putInt(len - 4);
            buffer.position(len);
            return (ByteBuffer)buffer.getNioBuf().flip();
        }
        
        public void decodeBuffer(final AOByteBuffer buffer) {
            this.serverVersion = buffer.getString();
            this.worldToken = buffer.getString();
            this.errorMessage = buffer.getString();
            this.worldFilesUrl = buffer.getString();
            this.account = buffer.getOID();
            this.characterSlots = buffer.getInt();
            for (int nChars = buffer.getInt(); nChars > 0; --nChars) {
                final Map<String, Serializable> props = PropertyMessage.unmarshallProperyMap(buffer);
                this.characters.add(props);
            }
        }
    }
    
    public static class CharacterCreateMessage
    {
        private Map<String, Serializable> props;
        
        public Map<String, Serializable> getProperties() {
            return this.props;
        }
    }
    
    public static class CharacterCreateResponseMessage
    {
        private Map<String, Serializable> props;
        
        public void setProperties(final Map<String, Serializable> props) {
            this.props = props;
        }
        
        public Map<String, Serializable> getProperties() {
            return this.props;
        }
        
        ByteBuffer getEncodedMessage() {
            if (Log.loggingDebug) {
                Log.debug("LoginPlugin: create character response: nProps=" + ((this.props == null) ? 0 : this.props.size()));
            }
            final AOByteBuffer buffer = new AOByteBuffer(1024);
            buffer.putInt(0);
            buffer.putInt(6);
            if (this.props == null) {
                buffer.putInt(0);
            }
            else {
                final List<String> propStrings = new ArrayList<String>();
                final int nProps = PropertyMessage.createPropertyString(propStrings, this.props, "");
                buffer.putInt(nProps);
                for (final String s : propStrings) {
                    buffer.putString(s);
                }
            }
            final int len = buffer.position();
            buffer.getNioBuf().rewind();
            buffer.putInt(len - 4);
            buffer.position(len);
            return (ByteBuffer)buffer.getNioBuf().flip();
        }
        
        public void decodeBuffer(final AOByteBuffer buffer) {
            this.props = PropertyMessage.unmarshallProperyMap(buffer);
        }
    }
    
    public static class CharacterDeleteResponseMessage
    {
        private Map<String, Serializable> props;
        
        public void setProperties(final Map<String, Serializable> props) {
            this.props = props;
        }
        
        public Map<String, Serializable> getProperties() {
            return this.props;
        }
        
        ByteBuffer getEncodedMessage() {
            if (Log.loggingDebug) {
                Log.debug("LoginPlugin: delete character response: nProps=" + ((this.props == null) ? 0 : this.props.size()));
            }
            final AOByteBuffer buffer = new AOByteBuffer(1024);
            buffer.putInt(0);
            buffer.putInt(4);
            if (this.props == null) {
                buffer.putInt(0);
            }
            else {
                final List<String> propStrings = new ArrayList<String>();
                final int nProps = PropertyMessage.createPropertyString(propStrings, this.props, "");
                buffer.putInt(nProps);
                for (final String s : propStrings) {
                    buffer.putString(s);
                }
            }
            final int len = buffer.position();
            buffer.getNioBuf().rewind();
            buffer.putInt(len - 4);
            buffer.position(len);
            return (ByteBuffer)buffer.getNioBuf().flip();
        }
    }
    
    public static class CharacterDeleteMessage
    {
        private Map<String, Serializable> props;
        
        public Map<String, Serializable> getProperties() {
            return this.props;
        }
    }
    
    public static class CharacterSelectRequestMessage
    {
        private Map<String, Serializable> props;
        
        public Map<String, Serializable> getProperties() {
            return this.props;
        }
    }
    
    public static class CharacterSelectResponseMessage
    {
        private Map<String, Serializable> props;
        
        public void setProperties(final HashMap<String, Serializable> props) {
            this.props = props;
        }
        
        public Map<String, Serializable> getProperties() {
            return this.props;
        }
        
        ByteBuffer getEncodedMessage() {
            if (Log.loggingDebug) {
                Log.debug("LoginPlugin: select character response: nProps=" + ((this.props == null) ? 0 : this.props.size()));
            }
            final AOByteBuffer buffer = new AOByteBuffer(1024);
            buffer.putInt(0);
            buffer.putInt(9);
            buffer.putPropertyMap(this.props);
            final int len = buffer.position();
            buffer.getNioBuf().rewind();
            buffer.putInt(len - 4);
            buffer.position(len);
            return (ByteBuffer)buffer.getNioBuf().flip();
        }
        
        public void decodeBuffer(final AOByteBuffer buffer) {
            this.props = buffer.getPropertyMap();
        }
    }
}
