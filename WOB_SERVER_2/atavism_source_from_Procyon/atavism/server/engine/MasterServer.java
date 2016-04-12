// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;
import atavism.server.util.SecureTokenSpec;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import atavism.server.util.Base64;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;
import java.util.concurrent.Executors;
import java.net.Socket;
import java.net.ServerSocket;
import atavism.server.network.rdp.RDPServerSocket;
import atavism.server.network.rdp.RDPServer;
import atavism.server.util.SecureTokenManager;
import atavism.server.util.SecureTokenUtil;
import atavism.server.util.InitLogAndPid;
import atavism.server.util.AORuntimeException;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Log;
import java.util.Properties;
import atavism.server.network.ClientTCPMessageIO;
import java.util.concurrent.ExecutorService;
import atavism.server.objects.RemoteAccountConnector;
import atavism.server.network.ClientConnection;

public class MasterServer implements ClientConnection.AcceptCallback, ClientConnection.MessageCallback
{
    int tcpPort;
    int rdpPort;
    SocketPolicyHandler policyHandler;
    MasterDatabase db;
    RemoteAccountConnector remoteConnector;
    private static MasterServer masterServer;
    private static ExecutorService threadPool;
    private static ClientTCPMessageIO clientTCPMessageIO;
    public static long masterTokenValidTime;
    private static final int CHALLENGE_LEN = 20;
    public static final int AUTH_PROTOCOL_VERSION = 1;
    public static final int defaultTcpPort = 9005;
    public static final int defaultRdpPort = 9010;
    public static Properties properties;
    
    public MasterServer() {
        this.tcpPort = -1;
        this.rdpPort = -1;
        this.db = null;
        final String tcpPortStr = MasterServer.properties.getProperty("atavism.master_tcp_port");
        if (tcpPortStr == null) {
            this.tcpPort = 9005;
        }
        else {
            this.tcpPort = Integer.parseInt(tcpPortStr.trim());
        }
        final String rdpPortStr = MasterServer.properties.getProperty("atavism.master_rdp_port");
        if (rdpPortStr == null) {
            this.rdpPort = 9010;
        }
        else {
            this.rdpPort = Integer.parseInt(rdpPortStr.trim());
        }
        this.policyHandler = new SocketPolicyHandler(MasterServer.properties);
    }
    
    public void dbConnect() {
        if (this.db == null) {
            this.db = new MasterDatabase();
        }
        this.db.connect(getDBUrl(), getDBUser(), getDBPassword());
    }
    
    public void setTCPPort(final int port) {
        this.tcpPort = port;
    }
    
    public int getTCPPort() {
        return this.tcpPort;
    }
    
    public void setRDPPort(final int port) {
        this.rdpPort = port;
    }
    
    public int getRDPPort() {
        return this.rdpPort;
    }
    
    public void setRemoteConnector(final RemoteAccountConnector connector) {
        this.remoteConnector = connector;
    }
    
    @Override
    public void acceptConnection(final ClientConnection con) {
        if (Log.loggingDebug) {
            Log.debug("masterserver: new incoming connection: " + con);
        }
        con.registerMessageCallback(this);
    }
    
    @Override
    public void processPacket(final ClientConnection con, final AOByteBuffer buf) {
        try {
            final int msgType = buf.getInt();
            if (msgType == 0) {
                Log.debug("masterserver: got name resolution request");
                this.resolveName(con, buf);
                return;
            }
            if (msgType != 1) {
                Log.warn("masterserver.processPacket: ignoring unknown msg type");
                return;
            }
            Log.debug("masterserver: got chat request");
            this.chatMsg(con, buf);
        }
        catch (AORuntimeException e) {
            Log.exception("Masterserver.processPacket got exception", e);
        }
    }
    
    @Override
    public void connectionReset(final ClientConnection con) {
        Log.debug("Masterserver: connection reset");
    }
    
    public void resolveName(final ClientConnection con, final AOByteBuffer buf) {
        final String worldName = buf.getString();
        if (Log.loggingDebug) {
            Log.debug("masterserver.resolvename: looking up worldName " + worldName);
        }
        final MasterDatabase.WorldInfo worldInfo = this.db.resolveWorldID(worldName);
        String hostname = null;
        int port = -1;
        String patcherURL = null;
        String mediaURL = null;
        if (worldInfo == null) {
            Log.warn("masterserver.resolvename: failed to resolve worldName " + worldName);
        }
        else {
            hostname = worldInfo.svrHostName;
            port = worldInfo.port;
            patcherURL = worldInfo.patcherURL;
            mediaURL = worldInfo.mediaURL;
            if (Log.loggingDebug) {
                Log.debug("masterverse.resolvename: resolved worldName " + worldName + " to " + hostname + ":" + port);
            }
        }
        final AOByteBuffer returnBuf = new AOByteBuffer((hostname == null) ? 200 : (hostname.length() + 32));
        returnBuf.putInt(2);
        returnBuf.putString(worldName);
        returnBuf.putInt((hostname != null) ? 1 : 0);
        returnBuf.putString(hostname);
        returnBuf.putInt(port);
        if (patcherURL != null && mediaURL != null) {
            if (Log.loggingDebug) {
                Log.debug("masterverse.resolvename: patcher for worldName " + worldName + " at " + patcherURL + " with media at: " + mediaURL);
            }
            returnBuf.putString(patcherURL);
            returnBuf.putString(mediaURL);
        }
        returnBuf.flip();
        con.send(returnBuf);
    }
    
    public void chatMsg(final ClientConnection con, final AOByteBuffer buf) {
    }
    
    public static MasterServer getMasterServer() {
        if (MasterServer.masterServer == null) {
            MasterServer.masterServer = new MasterServer();
        }
        return MasterServer.masterServer;
    }
    
    public static String getDBType() {
        final String dbtype = MasterServer.properties.getProperty("atavism.db_type");
        if (dbtype == null) {
            return "mysql";
        }
        return dbtype;
    }
    
    public static String getDBUrl() {
        String url = MasterServer.properties.getProperty("atavism.db_url");
        if (url == null) {
            url = "jdbc:" + getDBType() + "://" + getDBHostname() + "/" + getDBName();
        }
        return url;
    }
    
    public static String getDBUser() {
        return MasterServer.properties.getProperty("atavism.db_user");
    }
    
    public static String getDBPassword() {
        return MasterServer.properties.getProperty("atavism.db_password");
    }
    
    public static String getDBHostname() {
        return MasterServer.properties.getProperty("atavism.db_hostname");
    }
    
    public static String getDBName() {
        final String dbname = MasterServer.properties.getProperty("atavism.db_name");
        if (dbname == null) {
            return "atavism";
        }
        return dbname;
    }
    
    public static String getRemoteDBUrl() {
        String url = MasterServer.properties.getProperty("atavism.remote_db_url");
        if (url == null) {
            url = "jdbc:" + getDBType() + "://" + getRemoteDBHostname() + "/" + getRemoteDBName();
        }
        return url;
    }
    
    public static String getRemoteDBUser() {
        return MasterServer.properties.getProperty("atavism.remote_db_user");
    }
    
    public static String getRemoteDBPassword() {
        return MasterServer.properties.getProperty("atavism.remote_db_password");
    }
    
    public static String getRemoteDBHostname() {
        return MasterServer.properties.getProperty("atavism.remote_db_hostname");
    }
    
    public static String getRemoteDBName() {
        final String dbname = MasterServer.properties.getProperty("atavism.remote_db_name");
        if (dbname == null) {
            return "master";
        }
        return dbname;
    }
    
    public static String getRemoteAccountTableName() {
        final String dbname = MasterServer.properties.getProperty("atavism.remote_db_account_table");
        if (dbname == null) {
            return "account";
        }
        return dbname;
    }
    
    public static boolean remoteDatabaseEnabled() {
        final String remoteDatabaseEnabled = MasterServer.properties.getProperty("atavism.remote_db_enabled");
        return remoteDatabaseEnabled != null && remoteDatabaseEnabled.equals("true");
    }
    
    public static boolean useSaltedMd5Passwords() {
        final String useMd5Passwords = MasterServer.properties.getProperty("atavism.use_salted_passwords");
        return useMd5Passwords != null && useMd5Passwords.equals("true");
    }
    
    public static void main(final String[] args) {
        if (args.length != 1) {
            Log.error("specify script file");
            System.exit(1);
        }
        try {
            MasterServer.properties = InitLogAndPid.initLogAndPid(args);
            final byte[] domainKey = SecureTokenUtil.encodeDomainKey(1L, SecureTokenUtil.generateDomainKey());
            SecureTokenManager.getInstance().initDomain(domainKey);
            final MasterServer ms = getMasterServer();
            final String scriptFilename = args[0];
            final ScriptManager scriptManager = new ScriptManager();
            scriptManager.init();
            if (Log.loggingDebug) {
                Log.debug("Executing script file: " + scriptFilename);
            }
            scriptManager.runFile(scriptFilename);
            Log.debug("script completed");
            ms.dbConnect();
            final String log_rdp_counters = MasterServer.properties.getProperty("atavism.log_rdp_counters");
            if (log_rdp_counters == null || log_rdp_counters.equals("false")) {
                RDPServer.setCounterLogging(false);
            }
            RDPServerSocket serverSocket = null;
            serverSocket = new RDPServerSocket();
            RDPServer.startRDPServer();
            serverSocket.registerAcceptCallback(MasterServer.masterServer);
            serverSocket.bind(ms.getRDPPort());
            Log.info("masterserver: rdp on port " + ms.getRDPPort());
            (MasterServer.clientTCPMessageIO = ClientTCPMessageIO.setup(ms.getRDPPort(), MasterServer.masterServer)).start();
            Log.info("masterserver: tcp on port " + ms.getTCPPort());
            final ServerSocket socket = new ServerSocket(ms.getTCPPort());
            if (Log.loggingDebug) {
                Log.debug("masterserver: tcp server listening on port " + ms.getTCPPort());
            }
            while (true) {
                final Socket clientSocket = socket.accept();
                MasterServer.threadPool.execute(new SocketHandler(clientSocket, getMasterServer().db, getMasterServer().remoteConnector));
            }
        }
        catch (Exception e) {
            Log.exception("MasterServer.main caught exception", e);
            System.exit(1);
            Log.info("connected to database");
        }
    }
    
    static {
        MasterServer.masterServer = null;
        MasterServer.threadPool = Executors.newCachedThreadPool();
        MasterServer.clientTCPMessageIO = null;
        MasterServer.masterTokenValidTime = 120000L;
        MasterServer.properties = new Properties();
    }
    
    public static class SocketHandler implements Runnable
    {
        private Socket clientSocket;
        private MasterDatabase db;
        private RemoteAccountConnector remoteConnector;
        private Random random;
        
        public SocketHandler(final Socket socket, final MasterDatabase db, final RemoteAccountConnector connector) {
            this.clientSocket = null;
            this.db = null;
            this.remoteConnector = null;
            this.random = new Random();
            this.clientSocket = socket;
            this.db = db;
            this.remoteConnector = connector;
        }
        
        private byte[] generateAuthResponse(final String username, final String password, final byte[] challenge) {
            final byte[] keyData = password.getBytes();
            try {
                final SecretKeySpec key = new SecretKeySpec(keyData, "HmacSHA1");
                final Mac mac = Mac.getInstance(key.getAlgorithm());
                mac.init(key);
                final AOByteBuffer buf = new AOByteBuffer(256);
                buf.putString(username);
                buf.putInt(1);
                buf.putInt(challenge.length);
                buf.putBytes(challenge, 0, challenge.length);
                final byte[] data = new byte[buf.position()];
                buf.rewind();
                buf.getBytes(data, 0, data.length);
                Log.debug("dataLen=" + data.length);
                Log.debug("data=" + Base64.encodeBytes(data));
                return mac.doFinal(data);
            }
            catch (NoSuchAlgorithmException e) {
                Log.exception("SecureTokenManager.generateDomainAuthenticator: bad implementation", e);
                return null;
            }
            catch (InvalidKeyException e2) {
                Log.exception("SecureTokenManager.generateDomainAuthenticator: invalid key", e2);
                throw new RuntimeException(e2);
            }
            catch (IllegalStateException e3) {
                Log.exception("SecureTokenManager.generateDomainAuthenticator: illegal state", e3);
                throw new RuntimeException(e3);
            }
        }
        
        private void handleAuth(final DataInputStream in, final DataOutputStream out) throws IOException {
            final int magicCookie = in.readInt();
            final int version = in.readInt();
            Log.debug("cookie=" + magicCookie + " version=" + version);
            if (version != 1) {
                throw new RuntimeException("unsupported version=" + version);
            }
            final int usernameLen = in.readInt();
            if (Log.loggingDebug) {
                Log.debug("MasterServer.handleAuth: username len=" + usernameLen);
            }
            if (usernameLen > 1000) {
                throw new RuntimeException("username too long, len=" + usernameLen);
            }
            final byte[] usernameBuf = new byte[usernameLen];
            in.readFully(usernameBuf);
            final String username = new String(usernameBuf);
            if (Log.loggingDebug) {
                Log.debug("MasterServer.handleAuth: login username=" + username);
            }
            final int passwordLen = in.readInt();
            final byte[] passwordBuf = new byte[passwordLen];
            in.readFully(passwordBuf);
            final String password = new String(passwordBuf);
            final int createAccount = in.readInt();
            Log.debug("MasterServer.handleAuth: createaccount=" + createAccount);
            if (createAccount == 1) {
                Log.debug("MasterServer.handleAuth: creating account=" + username);
                final int emailLen = in.readInt();
                final byte[] emailBuf = new byte[emailLen];
                in.readFully(emailBuf);
                final String email = new String(emailBuf);
                final int status = this.db.createAccount(username, password, email);
                out.writeInt(status);
                return;
            }
            final byte[] challenge = new byte[20];
            this.random.nextBytes(challenge);
            byte[] authResponse = null;
            if (password != null) {
                authResponse = this.generateAuthResponse(username, password, challenge);
            }
            Log.debug("password=" + password);
            if (authResponse != null) {
                Log.debug("authResponse=" + Base64.encodeBytes(authResponse));
            }
            OID accountOID = null;
            if (this.remoteConnector != null && this.remoteConnector.verifyAccount(username, password)) {
                Log.debug("Got remote connector verfication: " + this.remoteConnector);
                Integer accountId = this.db.getAccountId(username);
                if (accountId != null) {
                    accountOID = OID.fromLong(accountId);
                }
                else if (this.db.createAccount(username, password, username) == 1) {
                    accountId = this.db.getAccountId(username);
                    accountOID = OID.fromLong(accountId);
                }
            }
            else {
                accountOID = this.db.passwordCheck(username, password);
            }
            if (accountOID != null) {
                final int accountStatus = this.db.statusCheck(username);
                if (accountStatus > 0) {
                    out.writeInt(1);
                    final Integer accountId2 = this.db.getAccountId(username);
                    final SecureTokenSpec masterSpec = new SecureTokenSpec((byte)1, "master", System.currentTimeMillis() + MasterServer.masterTokenValidTime);
                    masterSpec.setProperty("account_id", accountId2);
                    masterSpec.setProperty("account_name", username);
                    final byte[] masterToken = SecureTokenManager.getInstance().generateToken(masterSpec);
                    Log.debug("tokenLen=" + masterToken.length + " token=" + Base64.encodeBytes(masterToken));
                    final AOByteBuffer tmpBuf = new AOByteBuffer(16);
                    tmpBuf.putInt(~accountId2);
                    tmpBuf.flip();
                    final byte[] oldToken = new byte[4];
                    tmpBuf.getBytes(oldToken, 0, oldToken.length);
                    if (masterToken == null) {
                        Log.debug("null token");
                    }
                    else {
                        Log.debug("tokenLen=" + masterToken.length + " token=" + Base64.encodeBytes(masterToken));
                    }
                    out.writeInt(masterToken.length);
                    out.write(masterToken);
                    out.writeInt(oldToken.length);
                    out.write(oldToken);
                }
                else {
                    if (accountStatus == 0) {
                        out.writeInt(4);
                        out.writeInt(0);
                        out.writeInt(0);
                        return;
                    }
                    out.writeInt(5);
                    out.writeInt(0);
                    out.writeInt(0);
                }
            }
            else {
                out.writeInt(0);
                out.writeInt(0);
                out.writeInt(0);
            }
        }
        
        private void handleOldStyleAuth(final DataInputStream in, final DataOutputStream out) throws IOException {
            final int usernameLen = in.readInt();
            if (Log.loggingDebug) {
                Log.debug("masterserver: username len=" + usernameLen);
            }
            if (usernameLen > 1000) {
                throw new RuntimeException("username too long, len=" + usernameLen);
            }
            final byte[] nameBuf = new byte[usernameLen];
            in.readFully(nameBuf);
            final String username = new String(nameBuf);
            if (Log.loggingDebug) {
                Log.debug("masterserver: login username=" + username);
            }
            final int passwordLen = in.readInt();
            final byte[] passwordBuf = new byte[passwordLen];
            in.readFully(passwordBuf);
            final String password = new String(passwordBuf);
            if (Log.loggingDebug) {
                Log.debug("login info: password=" + password);
            }
            final int uid = this.db.AOAcctPasswdCheck(username, password);
            if (uid == -1) {
                Log.warn("MasterServer: password check failed for username " + username);
            }
            else if (Log.loggingDebug) {
                Log.debug("MasterServer: password verified, uid=" + uid + ", token=" + ~uid);
            }
            out.writeInt((uid != -1) ? 1 : 0);
            out.writeInt(4);
            out.writeInt(~uid);
        }
        
        @Override
        public void run() {
            try {
                final BufferedInputStream bufferedIn = new BufferedInputStream(this.clientSocket.getInputStream());
                final DataInputStream in = new DataInputStream(bufferedIn);
                final DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());
                if (!in.markSupported()) {
                    throw new RuntimeException("MasterServer.run: cannot use mark/reset on input stream");
                }
                in.mark(4);
                final int magicCookie = in.readInt();
                Log.debug("Magic cookie: " + magicCookie);
                in.reset();
                if (magicCookie == -1) {
                    this.handleAuth(in, out);
                }
                else {
                    this.handleOldStyleAuth(in, out);
                }
            }
            catch (Exception e) {
                Log.exception("MasterServer.run caught exception", e);
            }
            finally {
                try {
                    this.clientSocket.close();
                    if (Log.loggingDebug) {
                        Log.debug("SocketHandler: closed socket: " + this.clientSocket);
                    }
                }
                catch (Exception ex) {}
            }
        }
    }
}
