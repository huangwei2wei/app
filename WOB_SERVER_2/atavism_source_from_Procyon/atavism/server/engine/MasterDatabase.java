// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.sql.Statement;
import java.sql.DriverManager;
import atavism.server.util.Log;
import atavism.server.util.AORuntimeException;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import java.sql.Connection;

public class MasterDatabase
{
    private Connection conn;
    private final String localAccountTable = "account";
    private final String remoteAccountTable = "jos_users";
    private final String idField = "id";
    transient Lock dbLock;
    
    public MasterDatabase() {
        this.conn = null;
        this.dbLock = LockFactory.makeLock("masterDBLock");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (Exception e) {
            throw new AORuntimeException("could not find class: " + e);
        }
        Log.debug("Database: starting keepalive");
        final Thread keepAliveThread = new Thread(new KeepAlive(), "DBKeepalive");
        keepAliveThread.start();
    }
    
    public MasterDatabase(final String sDriver) {
        this.conn = null;
        this.dbLock = LockFactory.makeLock("masterDBLock");
        if (Log.loggingDebug) {
            Log.debug("Initializing Database with driver " + sDriver);
            Log.debug("classpath = " + System.getProperty("java.class.path"));
        }
        try {
            Class.forName(sDriver).newInstance();
            if (Log.loggingDebug) {
                Log.debug(sDriver + " driver loaded");
            }
        }
        catch (Exception e) {
            throw new AORuntimeException("could not find class: " + sDriver);
        }
        Log.debug("Database: starting keepalive");
        final Thread keepAliveThread = new Thread(new KeepAlive(), "DBKeepalive");
        keepAliveThread.start();
    }
    
    public void connect(final String url, final String username, final String password) {
        try {
            this.dbLock.lock();
            if (Log.loggingDebug) {
                Log.debug("*** url = " + url + " username = " + username + " password = " + password);
            }
            try {
                this.conn = DriverManager.getConnection(url, username, password);
            }
            catch (Exception e) {
                throw new AORuntimeException("could not connect to database: " + e);
            }
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public void executeUpdate(final String update) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            try {
                stmt = this.conn.createStatement();
                stmt.executeUpdate(update);
            }
            catch (Exception e) {
                Log.exception("Database.executeUpdate: Running update " + update, e);
            }
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public void executeBatch(final List<String> statements) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            try {
                stmt = this.conn.createStatement();
                for (final String statement : statements) {
                    stmt.addBatch(statement);
                }
                stmt.executeBatch();
            }
            catch (Exception e) {
                Log.exception("Database.executeBatch: Running statements " + statements, e);
            }
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public boolean databaseTableContainsColumn(final String dbName, final String tableName, final String columnName) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            try {
                stmt = this.conn.createStatement();
                final String query = "SHOW COLUMNS FROM " + dbName + "." + tableName + " LIKE '" + columnName + "'";
                if (Log.loggingDebug) {
                    Log.debug("Database.databaseTableContainsColumn query: " + query);
                }
                rs = stmt.executeQuery(query);
                return rs.next();
            }
            catch (Exception e) {
                throw new AORuntimeException("Could not run select statement to determine the presence of the '" + columnName + "' in table '" + tableName + "': " + e);
            }
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public boolean databaseContainsTable(final String dbName, final String tableName) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            try {
                stmt = this.conn.createStatement();
                final String query = "SELECT count(*) FROM information_schema.tables WHERE table_schema = '" + dbName + "' AND table_name = '" + tableName + "'";
                rs = stmt.executeQuery(query);
                if (!rs.next()) {
                    return false;
                }
                final int count = rs.getInt(1);
                return count == 1;
            }
            catch (Exception e) {
                throw new AORuntimeException("Exception running select statement to find table " + tableName + ": " + e);
            }
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public void close() {
        try {
            this.dbLock.lock();
            if (this.conn != null) {
                this.conn.close();
                this.conn = null;
            }
        }
        catch (Exception e) {
            Log.error("MasterDatabase.close: unable to close connection");
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public void printDevelopers() {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT * FROM developer";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                final int devId = rs.getInt("dev_id");
                final String email = rs.getString("email");
                final String company = rs.getString("company");
                final String skill = rs.getString("skill");
                final String prior = rs.getString("prior");
                final String genre = rs.getString("genre");
                final String idea = rs.getString("idea");
                System.out.println("devId=" + devId + "\nemail=" + email + "\ncompany=" + company + "\nskill=" + skill + "\nprior=" + prior + "\ngenre=" + genre + "\nidea=" + idea);
            }
        }
        catch (Exception e) {
            Log.exception("printDevelopers", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public int AOAcctPasswdCheck(final String username, final String password) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT id, password FROM account WHERE username = '" + username + "'";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return -1;
            }
            final String realPassword = rs.getString(2);
            if (!realPassword.equals(password)) {
                return -1;
            }
            final int uid = rs.getInt(1);
            if (Log.loggingDebug) {
                Log.debug("username=" + username + ", uid=" + uid);
            }
            return uid;
        }
        catch (Exception e) {
            Log.exception("AOAcctPasswdCheck", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public WorldInfo resolveWorldID(final String worldName) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT server_name, server_port, patcher_URL, media_URL FROM world WHERE world_name = '" + worldName + "'";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return null;
            }
            final String hostname = rs.getString(1);
            final int port = rs.getInt(2);
            final String patcherURL = rs.getString(3);
            final String mediaURL = rs.getString(4);
            final WorldInfo worldInfo = new WorldInfo(worldName, hostname, port, patcherURL, mediaURL);
            return worldInfo;
        }
        catch (Exception e) {
            Log.exception("resolveWorldID", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public String getPassword(final String username) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT id, password FROM account WHERE username = '" + username + "'";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return null;
            }
            return rs.getString(2);
        }
        catch (Exception e) {
            Log.exception("getPassword", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public Integer getAccountId(final String username) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT id FROM account WHERE username = '" + username + "'";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return null;
            }
            return rs.getInt(1);
        }
        catch (Exception e) {
            Log.exception("getAccountId", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public OID passwordCheck(final String username, final String password) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT id, password FROM account WHERE username = '" + username + "' OR email = '" + "'";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                Log.debug("AUTH: no account found, check remote server? " + MasterServer.remoteDatabaseEnabled());
                if (MasterServer.remoteDatabaseEnabled()) {
                    rs = this.checkRemoteServer(username, true);
                }
                if (rs == null) {
                    return null;
                }
            }
            String realPassword = rs.getString(2);
            Log.debug("COMPARE: comparing local password: " + realPassword + " with password: " + password);
            boolean passwordsMatch = this.comparePasswords(password, realPassword);
            if (!passwordsMatch && MasterServer.remoteDatabaseEnabled()) {
                this.checkRemoteServer(username, false);
                rs = stmt.executeQuery(query);
                if (!rs.next()) {
                    return null;
                }
                realPassword = rs.getString("password");
                Log.debug("COMPARE: comparing remote password: " + realPassword + " with password: " + password);
                passwordsMatch = this.comparePasswords(password, realPassword);
                if (!passwordsMatch) {
                    return null;
                }
            }
            else if (!passwordsMatch) {
                Log.debug("COMPARE: passwords do not match");
                return null;
            }
            final long uidLong = rs.getInt(1);
            if (Log.loggingDebug) {
                Log.debug("username=" + username + ", uid=" + uidLong);
            }
            return OID.fromLong(uidLong);
        }
        catch (Exception e) {
            Log.exception("passwordCheck", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    private boolean comparePasswords(final String password, final String realPassword) {
        try {
            final boolean saltedPassword = MasterServer.useSaltedMd5Passwords();
            if (saltedPassword) {
                if (!realPassword.contains(":")) {
                    final MessageDigest md = MessageDigest.getInstance("MD5");
                    final byte[] array = md.digest(password.getBytes());
                    final StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < array.length; ++i) {
                        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
                    }
                    return sb.toString().equals(realPassword);
                }
                Log.debug("AUTH: using salted passwords");
                final String[] passwordPieces = realPassword.split(":");
                final MessageDigest md2 = MessageDigest.getInstance("MD5");
                final String salted = password + passwordPieces[1];
                md2.reset();
                md2.update(salted.getBytes());
                final byte[] digest = md2.digest();
                final StringBuffer hexString = new StringBuffer();
                for (int j = 0; j < digest.length; ++j) {
                    final String hex = Integer.toHexString(0xFF & digest[j]);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                final String hash = hexString.toString();
                final String md3 = passwordPieces[0];
                Log.debug("Password Check: " + password + " against real password: " + realPassword);
                Log.debug("Password Check: " + password + " has hash: [" + hash + "] and md5: [" + md3 + "]");
                if (!hash.equals(md3)) {
                    Log.debug("hash does not equal md5");
                    return false;
                }
            }
            else {
                Log.debug("COMPARE: checking not salted password");
                if (!password.equals(realPassword)) {
                    return false;
                }
            }
        }
        catch (Exception e) {
            Log.exception("comparePasswords", e);
            throw new AORuntimeException("database: ", e);
        }
        return true;
    }
    
    public int createAccount(final String username, String password, final String emailAddress) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            String query = "SELECT id, password FROM account WHERE username = '" + username + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                Log.debug("Create Account Failed: username already used");
                return 6;
            }
            query = "SELECT id, password FROM account WHERE email = '" + emailAddress + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                Log.debug("Create Account Failed: email address already used");
                return 7;
            }
            if (MasterServer.useSaltedMd5Passwords()) {
                password = generateSaltedPassword(password);
                Log.debug("AUTH: created salted password: " + password);
            }
            stmt = this.conn.createStatement();
            final String update = "INSERT INTO account (username, password, email, status, created_at) VALUES ( \"" + username + "\", \"" + password + "\", \"" + emailAddress + "\", " + 1 + ", NOW())";
            final int rows = stmt.executeUpdate(update);
            if (rows >= 1) {
                return 1;
            }
            Log.debug("Unknown error when creating account");
            return 8;
        }
        catch (Exception e) {
            Log.exception("createAccount", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public static String generateSaltedPassword(final String passwd) {
        final StringBuffer saltBuf = new StringBuffer();
        final SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < 32; ++i) {
            saltBuf.append(Integer.toString(rnd.nextInt(36), 36));
        }
        final String salt = saltBuf.toString();
        Log.debug("AUTH: generated salt: " + salt);
        return md5(passwd + salt) + ":" + salt;
    }
    
    private static String md5(final String data) {
        final byte[] bdata = new byte[data.length()];
        for (int i = 0; i < data.length(); ++i) {
            bdata[i] = (byte)(data.charAt(i) & '\u00ff');
        }
        byte[] hash;
        try {
            final MessageDigest md5er = MessageDigest.getInstance("MD5");
            hash = md5er.digest(bdata);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        final StringBuffer r = new StringBuffer(32);
        for (int i = 0; i < hash.length; ++i) {
            final String x = Integer.toHexString(hash[i] & 0xFF);
            if (x.length() < 2) {
                r.append("0");
            }
            r.append(x);
        }
        return r.toString();
    }
    
    private ResultSet checkRemoteServer(final String username, final boolean createUser) {
        Log.debug("Checking remote server for account details.");
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            final Connection tempConn = DriverManager.getConnection(MasterServer.getRemoteDBUrl(), MasterServer.getRemoteDBUser(), MasterServer.getRemoteDBPassword());
            stmt = tempConn.createStatement();
            final String query = "SELECT * FROM jos_users WHERE username = '" + username + "' OR email = '" + username + "'";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return null;
            }
            final String realPassword = rs.getString("password");
            final String email = rs.getString("email");
            final int status = 1;
            if (!createUser) {
                this.updateUser(username, realPassword, status);
                return null;
            }
            final boolean userCreated = this.createUser(username, realPassword, email, status);
            if (userCreated) {
                return rs;
            }
        }
        catch (Exception e) {
            Log.exception("checkRemoteServer", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
        return null;
    }
    
    public boolean createUser(final String username, final String password, final String email, final int status) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String update = "INSERT INTO account (username, password, email, status, created_at) VALUES ( \"" + username + "\", \"" + password + "\", \"" + email + "\", " + status + ", NOW())";
            final int rows = stmt.executeUpdate(update);
            return rows >= 1;
        }
        catch (Exception e) {
            Log.exception("createUser", e);
            Log.error("database error: " + e);
            return false;
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException sqlEx) {
                    stmt = null;
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public void updateUser(final String username, final String password, final int status) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String update = "UPDATE account set password = \"" + password + "\", status = " + status + " where username = \"" + username + "\"";
            final int rows = stmt.executeUpdate(update);
        }
        catch (Exception e) {
            Log.exception("createUser", e);
            Log.error("database error: " + e);
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException sqlEx) {
                    stmt = null;
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public int statusCheck(final String username) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT status FROM account WHERE username = '" + username + "'";
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return 0;
            }
            final int status = rs.getInt("status");
            return status;
        }
        catch (Exception e) {
            Log.exception("statusCheck", e);
            throw new AORuntimeException("database: ", e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public String getUserName(final long uid) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT username FROM account WHERE id = " + uid;
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                return null;
            }
            final String name = rs.getString(1);
            if (Log.loggingDebug) {
                Log.debug("uid:" + uid + "=" + name);
            }
            return name;
        }
        catch (Exception e) {
            Log.warn("Database.getUserName: unable to get username, this is ok if you are not on production server: " + e);
            return null;
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException sqlEx) {
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException sqlEx) {
                        stmt = null;
                    }
                }
            }
            this.dbLock.unlock();
        }
    }
    
    public void ping() {
        Log.debug("Database: ping");
        Statement stmt = null;
        this.dbLock.lock();
        try {
            final String sql = "SELECT 1 from player_character";
            stmt = this.conn.createStatement();
            stmt.executeQuery(sql);
        }
        catch (Exception e) {
            this.reconnect();
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException sqlEx) {
                    stmt = null;
                }
            }
            this.dbLock.unlock();
        }
    }
    
    void reconnect() {
        Log.error("Database reconnect: url=" + MasterServer.getDBUrl());
        this.close();
        int failCount = 0;
        this.dbLock.lock();
        while (true) {
            try {
                this.conn = DriverManager.getConnection(MasterServer.getDBUrl(), MasterServer.getDBUser(), MasterServer.getDBPassword());
                Log.info("Database: reconnected to " + MasterServer.getDBUrl());
            }
            catch (Exception e) {
                try {
                    if (failCount == 0) {
                        Log.exception("Database reconnect failed, retrying", e);
                    }
                    else if (failCount % 300 == 299) {
                        Log.error("Database reconnect failed, retrying: " + e);
                    }
                    ++failCount;
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ex) {}
                continue;
            }
            finally {
                this.dbLock.unlock();
            }
            break;
        }
    }
    
    public static void main(final String[] args) {
        try {
            if (args.length != 3) {
                System.err.println("creates <count> users named <username>0 <username>1 ... <username><count-1> all with same password");
                System.err.println("java Database <username> <password> <count> <namespace>");
                System.exit(1);
            }
            final String username = args[0];
            final String password = args[1];
            final int count = Integer.valueOf(args[2]);
            final MasterDatabase db = new MasterDatabase(Engine.getDBDriver());
            db.connect(Engine.getDBUrl(), Engine.getDBUser(), Engine.getDBPassword());
            for (int i = 0; i < count; ++i) {
                final String uname = username + i;
                final OID oid = db.passwordCheck(uname, password);
                if (oid == null) {
                    System.out.println("password check failed in database");
                }
                else {
                    System.out.println("password check passed, oid=" + oid);
                }
            }
        }
        catch (Exception e) {
            Log.error("Database: " + e);
        }
    }
    
    class KeepAlive implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(60000L);
                }
                catch (InterruptedException e) {
                    Log.exception("Database.KeepAlive: interrupted", e);
                }
                try {
                    if (MasterDatabase.this.conn == null) {
                        continue;
                    }
                    MasterDatabase.this.ping();
                }
                catch (AORuntimeException e2) {
                    Log.exception("Database.KeepAlive: ping caught exception", e2);
                }
            }
        }
    }
    
    public static class WorldInfo
    {
        public String worldName;
        public String svrHostName;
        public int port;
        public String patcherURL;
        public String mediaURL;
        
        WorldInfo(final String worldName, final String svrHostName, final int port, final String patcherURL, final String mediaURL) {
            this.worldName = null;
            this.svrHostName = null;
            this.port = -1;
            this.patcherURL = null;
            this.mediaURL = null;
            this.worldName = worldName;
            this.svrHostName = svrHostName;
            this.port = port;
            this.patcherURL = patcherURL;
            this.mediaURL = mediaURL;
        }
    }
}
