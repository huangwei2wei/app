// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.HashMap;
import java.util.Map;
import javax.sql.rowset.serial.SerialBlob;
import atavism.server.math.Point;
import java.beans.PersistenceDelegate;
import java.io.OutputStream;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.objects.ObjectType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.sql.Blob;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.util.HashSet;
import java.util.Set;
import atavism.server.math.Geometry;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
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
import org.apache.log4j.Logger;

public class Database
{
    private static Logger logger;
    private Connection conn;
    transient Lock dbLock;
    private static int largestNamespaceInt;
    
    public Database() {
        this.conn = null;
        this.dbLock = LockFactory.makeLock("databaseLock");
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
    
    public Database(final String sDriver) {
        this.conn = null;
        this.dbLock = LockFactory.makeLock("databaseLock");
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
    
    public Connection getConnection() {
        return this.conn;
    }
    
    public Lock getLock() {
        return this.dbLock;
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
    
    public int executeUpdate(final String update) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            try {
                stmt = this.conn.createStatement();
                return stmt.executeUpdate(update);
            }
            catch (Exception e) {
                Log.exception("Database.executeUpdate: Running update " + update, e);
                return -1;
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
            Log.error("Database.close: unable to close connection");
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public void encacheNamespaceMapping() {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT namespace_string, namespace_int FROM namespaces";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                final String nsString = rs.getString("namespace_string");
                final Integer nsInt = rs.getInt("namespace_int");
                final Namespace ns = Namespace.getNamespaceFromInt(nsInt);
                if (ns == null) {
                    Namespace.addDBNamespace(nsString, nsInt);
                }
                else if (!ns.getName().equals(nsString)) {
                    throw new AORuntimeException("Database.encacheNamespaceMapping: Encached namespace " + ns + " doesn't have the right string " + nsString);
                }
                if (nsInt > Database.largestNamespaceInt) {
                    Database.largestNamespaceInt = nsInt;
                }
            }
        }
        catch (Exception e) {
            Log.exception("encacheNamespaceMapping", e);
            throw new AORuntimeException("database error: " + e);
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
    
    public Namespace findExistingNamespace(final String nsString) {
        this.encacheNamespaceMapping();
        return Namespace.getNamespaceIfExists(nsString);
    }
    
    public Namespace findExistingNamespace(final Integer nsInt) {
        this.encacheNamespaceMapping();
        return Namespace.getNamespaceFromInt(nsInt);
    }
    
    public Namespace createNamespace(final String nsString) {
        Statement stmt = null;
        this.dbLock.lock();
        try {
            Namespace ns = this.findExistingNamespace(nsString);
            if (ns != null) {
                return ns;
            }
            if (Database.largestNamespaceInt >= 31) {
                Log.error("Database.createNamespace: There are " + Database.largestNamespaceInt + " namespaces already, so you can't create another one");
                throw new AORuntimeException("When creating namespace " + nsString + ", too many Namespaces");
            }
            stmt = this.conn.createStatement();
            final int nsInt = Database.largestNamespaceInt + 1;
            final String update = "INSERT INTO namespaces (namespace_string, namespace_int) VALUES ('" + nsString + "'" + ", " + nsInt + ")";
            int rows = 0;
            try {
                rows = stmt.executeUpdate(update);
            }
            catch (SQLException ex) {
                ns = this.findExistingNamespace(nsString);
                if (ns != null) {
                    return ns;
                }
            }
            if (rows != 1) {
                throw new AORuntimeException("Could not create namespace '" + nsString + "'");
            }
            Database.largestNamespaceInt = nsInt;
            if (Log.loggingDebug) {
                Log.debug("Database.getOrCreateNamespaceInt: string " + nsString + " <=> " + nsInt);
            }
            return Namespace.addDBNamespace(nsString, nsInt);
        }
        catch (Exception e) {
            Log.exception("createNamespace", e);
            return null;
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
    
    public void createCharacter(final String worldName, final OID aoid, final AOObject user, final Namespace namespace) {
        try {
            this.dbLock.lock();
            user.atavismID(aoid);
            this.saveObject(null, user, namespace);
            this.mapAtavismID(worldName, aoid, user.getOid());
        }
        finally {
            this.dbLock.unlock();
        }
    }
    
    public void mapAtavismID(final String worldName, final OID atavismID, final OID objID) {
        Statement stmt = null;
        final int nsInt = Namespace.WORLD_MANAGER.getNumber();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String update = "INSERT INTO player_character (account_id, obj_id, namespace_int, world_name) VALUES (" + atavismID.toLong() + ", " + objID.toLong() + ", " + nsInt + ", '" + worldName + "')";
            final int rows = stmt.executeUpdate(update);
            if (rows != 1) {
                throw new AORuntimeException("failed to map atavismid");
            }
            if (Log.loggingDebug) {
                Log.debug("Database.mapAtavismID: mapping aoid " + atavismID + " to objID " + objID);
            }
        }
        catch (Exception e) {
            Log.exception("mapAtavismID", e);
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
    
    public Set<OID> getPersistedObjects(final Namespace namespace, final Geometry g) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT obj_id FROM objstore WHERE world_name='" + Engine.getWorldName() + "'" + " AND namespace_int = " + namespace.getNumber() + " AND ((locX > " + g.getMinX() + " AND locX < " + g.getMaxX() + " AND locZ > " + g.getMinZ() + " AND locZ < " + g.getMaxZ() + ") OR (locX IS NULL))";
            rs = stmt.executeQuery(query);
            final Set<OID> l = new HashSet<OID>();
            while (rs.next()) {
                final OID oid = OID.fromLong(rs.getLong(1));
                l.add(oid);
            }
            if (Log.loggingDebug) {
                Log.debug("Database.getPersistedObjects: found " + l.size() + " persisted objects for geometry " + g);
            }
            return l;
        }
        catch (Exception e) {
            Log.exception("getPersistedObjects", e);
            throw new AORuntimeException("database: " + e);
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
    
    public Entity loadEntity(final OID oid, final Namespace namespace) {
        try {
            final InputStream is = this.retrieveEntityDataByOidAndNamespace(oid, namespace);
            if (is == null) {
                return null;
            }
            final XMLDecoder decoder = new XMLDecoder(is, null, new XMLExceptionListener(), this.getClass().getClassLoader());
            final Entity entity = (Entity)decoder.readObject();
            decoder.close();
            return entity;
        }
        catch (Exception e) {
            throw new AORuntimeException("database.loadObject", e);
        }
    }
    
    public Entity loadEntity(final String persistenceKey) {
        final InputStream is = this.retrieveEntityDataByPersistenceKey(persistenceKey);
        if (is == null) {
            return null;
        }
        final XMLDecoder decoder = new XMLDecoder(is, null, new XMLExceptionListener(), this.getClass().getClassLoader());
        final Entity entity = (Entity)decoder.readObject();
        decoder.close();
        return entity;
    }
    
    public InputStream retrieveEntityDataByOidAndNamespace(final OID oid, final Namespace namespace) {
        Statement stmt = null;
        final int nsInt = namespace.getNumber();
        final long oidLong = oid.toLong();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            final ResultSet rs = stmt.executeQuery("SELECT data FROM objstore WHERE world_name='" + Engine.getWorldName() + "' AND obj_id = " + oidLong + " AND namespace_int=" + nsInt);
            if (!rs.next()) {
                Log.error("retrieveEntityDataByOidAndNamespace: not found oid=" + oid + " namespace=" + namespace);
                return null;
            }
            final Blob dataBlob = rs.getBlob("data");
            final long blobLen = dataBlob.length();
            final byte[] blobBytes = dataBlob.getBytes(1L, (int)blobLen);
            final ByteArrayInputStream bis = new ByteArrayInputStream(blobBytes);
            if (Log.loggingDebug) {
                Log.debug("retrieveEntityDataByOidAndNamespace: oid=" + oid + " size=" + blobLen);
            }
            return bis;
        }
        catch (Exception e) {
            Log.exception("retrieveEntityDataByOidAndNamespace", e);
            throw new AORuntimeException("database error", e);
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
    
    public InputStream retrieveEntityDataByPersistenceKey(final String persistenceKey) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            final ResultSet rs = stmt.executeQuery("SELECT data FROM objstore WHERE world_name='" + Engine.getWorldName() + "' AND persistence_key='" + persistenceKey + "'");
            if (!rs.next()) {
                Log.error("retrieveEntityDataByPersistenceKey: not found key=" + persistenceKey);
                return null;
            }
            final Blob dataBlob = rs.getBlob("data");
            final long blobLen = dataBlob.length();
            final byte[] blobBytes = dataBlob.getBytes(1L, (int)blobLen);
            final ByteArrayInputStream bis = new ByteArrayInputStream(blobBytes);
            if (Log.loggingDebug) {
                Log.debug("retrieveEntityDataByPersistenceKey: key=" + persistenceKey + " size=" + blobLen);
            }
            return bis;
        }
        catch (Exception e) {
            Log.exception("retrieveEntityDataByPersistenceKey", e);
            throw new AORuntimeException("database error", e);
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
    
    public OID getOidByName(final String name, final Namespace namespace) {
        return this.getOidByName(name, namespace, null);
    }
    
    public OID getOidByName(final String name, final Namespace namespace, final OID instanceOid) {
        Statement stmt = null;
        final int nsInt = namespace.getNumber();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            String query = "SELECT obj_id FROM objstore WHERE world_name='" + Engine.getWorldName() + "'" + " AND namespace_int=" + nsInt + " AND name='" + name + "'";
            if (instanceOid != null) {
                query = query + " AND instance=" + instanceOid.toLong();
            }
            final ResultSet rs = stmt.executeQuery(query);
            if (!rs.next()) {
                Log.debug("getOidByName: unknown name=" + name + " namespace=" + namespace + " instanceOid=" + instanceOid);
                return null;
            }
            return OID.fromLong(rs.getLong(1));
        }
        catch (Exception e) {
            Log.exception("getOidByName", e);
            throw new AORuntimeException("database error", e);
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
    
    public String getObjectName(final OID oid, final Namespace namespace) {
        Statement stmt = null;
        final int nsInt = namespace.getNumber();
        final long oidLong = oid.toLong();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            final String query = "SELECT name FROM objstore WHERE world_name='" + Engine.getWorldName() + "'" + " AND namespace_int=" + nsInt + " AND obj_id=" + oidLong;
            final ResultSet rs = stmt.executeQuery(query);
            if (!rs.next()) {
                Log.debug("getObjectName: unknown oid=" + oid + " namespace=" + namespace);
                return null;
            }
            return rs.getString(1);
        }
        catch (Exception e) {
            Log.exception("getObjectName", e);
            throw new AORuntimeException("database error", e);
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
    
    public List<Object> getOidsAndNamesMatchingName(final String playerName, final boolean exactMatch) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            final ResultSet rs = stmt.executeQuery("SELECT obj_id, name from objstore WHERE world_name = '" + Engine.getWorldName() + "'" + " AND namespace_int = " + Namespace.WORLD_MANAGER.getNumber() + " AND name" + (exactMatch ? "=" : " LIKE ") + "'" + playerName + (exactMatch ? "" : "%") + "'");
            final List<OID> oids = new LinkedList<OID>();
            final List<String> names = new LinkedList<String>();
            while (rs.next()) {
                oids.add(OID.fromLong(rs.getLong("obj_id")));
                names.add(rs.getString("name"));
            }
            final List<Object> result = new LinkedList<Object>();
            result.add(oids);
            result.add(names);
            if (Log.loggingDebug) {
                Log.debug("Database.getOidsAndNamesMatching: For playerName '" + playerName + "'" + ", found " + oids.size() + " oids: " + makeOidCollectionString(oids) + " and " + names.size() + " names: " + makeNameCollectionString(names));
            }
            return result;
        }
        catch (Exception e) {
            Log.exception("getOidsAndNamesMatchingName", e);
            throw new AORuntimeException("database error", e);
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
    
    public List<String> getObjectNames(final List<OID> inputOids, final Namespace namespace, final String unknownName) {
        final List<String> names = new LinkedList<String>();
        if (inputOids == null || inputOids.size() == 0) {
            if (Log.loggingDebug) {
                Log.debug("Database.getObjectNames: No oids in inputOids so returning empty name list");
            }
            return names;
        }
        String whereList = "";
        for (final OID oid : inputOids) {
            final long oidLong = oid.toLong();
            if (whereList != "") {
                whereList += " OR ";
            }
            whereList = whereList + "(obj_id = " + oidLong + ")";
        }
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            final ResultSet rs = stmt.executeQuery("SELECT obj_id, name from objstore WHERE world_name = '" + Engine.getWorldName() + "'" + " AND namespace_int = " + namespace.getNumber() + " AND (" + whereList + ")");
            final List<OID> readOids = new LinkedList<OID>();
            final List<String> readNames = new LinkedList<String>();
            while (rs.next()) {
                readOids.add(OID.fromLong(rs.getLong("obj_id")));
                readNames.add(rs.getString("name"));
            }
            final List<String> returnedNames = new LinkedList<String>();
            for (final OID oid2 : inputOids) {
                final int index = readOids.indexOf(oid2);
                String name = unknownName;
                if (index != -1) {
                    name = readNames.get(index);
                }
                returnedNames.add(name);
            }
            if (Log.loggingDebug) {
                Log.debug("Database.getObjectNames: For oids " + makeOidCollectionString(inputOids) + ", returning names " + makeNameCollectionString(returnedNames));
            }
            return returnedNames;
        }
        catch (Exception e) {
            Log.exception("getObjectNames", e);
            throw new AORuntimeException("database error", e);
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
    
    public boolean characterNameTaken(final String name) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT count(1) AS found FROM objstore WHERE type = 'PLAYER' AND LOWER(name) = lower('" + name + "')";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                final int count = rs.getInt(1);
                if (count > 0) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            Log.warn("Database.getUserName: unable to check username, this is ok if you are not on production server: " + e);
            return false;
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
    
    public static String makeOidCollectionString(final List<OID> oids) {
        String oidString = "";
        for (final OID oid : oids) {
            if (oidString == "") {
                oidString += oid;
            }
            else {
                oidString = oidString + "," + oid;
            }
        }
        return oidString;
    }
    
    public static String makeNameCollectionString(final Collection<String> names) {
        String nameString = "";
        for (final String name : names) {
            if (nameString != "") {
                nameString += ",";
            }
            nameString = nameString + "'" + name + "'";
        }
        return nameString;
    }
    
    public List<Namespace> getObjectNamespaces(final OID oid) {
        Statement stmt = null;
        final long oidLong = oid.toLong();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            final ResultSet rs = stmt.executeQuery("SELECT namespace_int FROM objstore WHERE world_name='" + Engine.getWorldName() + "'" + " AND obj_id=" + oidLong);
            final List<Namespace> result = new ArrayList<Namespace>(6);
            while (rs.next()) {
                final int nsInt = rs.getInt(1);
                final Namespace namespace = Namespace.getNamespaceFromInt(nsInt);
                if (namespace != null) {
                    result.add(namespace);
                }
                else {
                    Log.error("getObjectNamespaces: unknown namespace id for oid=" + oid + " nsInt=" + nsInt);
                }
            }
            if (result.size() == 0) {
                return null;
            }
            return result;
        }
        catch (Exception e) {
            Log.exception("getOidByName", e);
            throw new AORuntimeException("database error", e);
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
    
    public List<OID> getInstanceContent(final OID instanceOid, final ObjectType exclusion) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1004, 1007);
            String queryString = "SELECT obj_id FROM objstore WHERE world_name='" + Engine.getWorldName() + "'" + " AND instance=" + instanceOid + " AND namespace_int=" + Namespace.WORLD_MANAGER.getNumber();
            if (exclusion != null) {
                queryString = queryString + " AND type<>'" + exclusion.getTypeName() + "'";
            }
            final ResultSet rs = stmt.executeQuery(queryString);
            final List<OID> result = new ArrayList<OID>(100);
            while (rs.next()) {
                final OID oid = OID.fromLong(rs.getLong(1));
                result.add(oid);
            }
            if (Log.loggingDebug) {
                Log.debug("getInstanceContent: instanceOid=" + instanceOid + " returning " + result.size() + " oids");
            }
            return result;
        }
        catch (Exception e) {
            Log.exception("getInstanceContent", e);
            throw new AORuntimeException("database error", e);
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
    
    public void deleteObjectData(final OID oid) {
        Statement stmt = null;
        final long oidLong = oid.toLong();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            stmt.execute("DELETE FROM objstore WHERE world_name=\"" + Engine.getWorldName() + "\" AND obj_id = " + oidLong);
        }
        catch (Exception e) {
            Log.exception("deleteObjectData", e);
            throw new AORuntimeException("database error", e);
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
    
    public void deleteObjectData(final OID oid, final Namespace namespace) {
        final int nsInt = namespace.getNumber();
        final long oidLong = oid.toLong();
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            stmt.execute("DELETE FROM objstore WHERE world_name=\"" + Engine.getWorldName() + "\" AND obj_id = " + oidLong + " AND namespace_int=" + nsInt);
        }
        catch (Exception e) {
            Log.exception("deleteObjectData", e);
            throw new AORuntimeException("database error: ", e);
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
    
    public void deletePlayerCharacter(final OID oid) {
        Statement stmt = null;
        final long oidLong = oid.toLong();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            stmt.execute("DELETE FROM player_character WHERE obj_id = " + oidLong);
        }
        catch (Exception e) {
            Log.exception("deletePlayerCharacter", e);
            throw new AORuntimeException("database error: ", e);
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
    
    public void saveObject(final Entity entity, final Namespace namespace) {
        this.saveObject(null, entity, namespace);
    }
    
    public void saveObject(final String persistenceKey, final byte[] data, final Namespace namespace) {
        try {
            final ByteArrayInputStream bs = new ByteArrayInputStream(data);
            final ObjectInputStream ois = new ObjectInputStream(bs);
            Entity entity = null;
            try {
                entity = (Entity)ois.readObject();
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("call not found", e);
            }
            this.saveObject(persistenceKey, entity, namespace);
        }
        catch (IOException e2) {
            throw new AORuntimeException("saveObject", e2);
        }
    }
    
    public void saveObject(final String persistenceKey, final Entity entity, final Namespace namespace) {
        final Lock entityLock = entity.getLock();
        Lock worldNodeLock = null;
        AOObject obj = null;
        if (entity instanceof AOObject) {
            obj = (AOObject)entity;
            final WMWorldNode node = (WMWorldNode)obj.worldNode();
            if (node != null && node.getQuadNode() != null) {
                worldNodeLock = node.getQuadNode().getTree().getLock();
            }
        }
        byte[] data = null;
        try {
            AOObject.transferLock.lock();
            try {
                if (worldNodeLock != null) {
                    worldNodeLock.lock();
                }
                try {
                    entityLock.lock();
                    Log.debug("encoding entity type=" + entity.getType());
                    final ByteArrayOutputStream ba = new ByteArrayOutputStream();
                    this.encodeEntity(ba, entity);
                    data = ba.toByteArray();
                    if (Database.logger.isDebugEnabled()) {
                        Database.logger.debug((Object)("Database.saveObject: persistenceKey=" + persistenceKey + ", ns=" + namespace + " type=" + entity.getType() + " xml conversion: length=" + data.length + ", string=" + new String(data)));
                    }
                }
                catch (Exception e) {
                    throw new AORuntimeException("Database.saveObject: failed on " + obj.getName(), e);
                }
                finally {
                    entityLock.unlock();
                }
            }
            finally {
                if (worldNodeLock != null) {
                    worldNodeLock.unlock();
                }
            }
        }
        finally {
            AOObject.transferLock.unlock();
        }
        this.saveObjectHelper(persistenceKey, entity, namespace, data);
    }
    
    protected void encodeEntity(final ByteArrayOutputStream ba, final Entity entity) {
        final Thread cur = Thread.currentThread();
        final ClassLoader ccl = cur.getContextClassLoader();
        final ClassLoader myClassLoader = this.getClass().getClassLoader();
        cur.setContextClassLoader(myClassLoader);
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(ba);
            encoder.setExceptionListener(new ExceptionListener() {
                @Override
                public void exceptionThrown(final Exception exception) {
                    Log.exception(exception);
                }
            });
            encoder.setPersistenceDelegate(ObjectType.class, new ObjectType.PersistenceDelegate());
            encoder.writeObject(entity);
        }
        finally {
            if (null != encoder) {
                encoder.close();
                try {
                    ba.flush();
                }
                catch (Exception e) {
                    Log.exception("Database.encodeEntity", e);
                }
            }
            cur.setContextClassLoader(ccl);
        }
    }
    
    public void saveObjectHelper(final String persistenceKey, final Entity entity, final Namespace namespace, final byte[] data) {
        AOObject obj = null;
        Point loc = null;
        OID instanceOid = null;
        final int nsInt = namespace.getNumber();
        if (entity instanceof AOObject) {
            obj = (AOObject)entity;
            loc = obj.getLoc();
            if (obj.worldNode() != null) {
                instanceOid = obj.worldNode().getInstanceOid();
            }
        }
        Statement stmt = null;
        final long oidLong = entity.getOid().toLong();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement(1005, 1008);
            final ResultSet uprs = stmt.executeQuery("SELECT * FROM objstore WHERE world_name='" + Engine.getWorldName() + "' AND obj_id=" + oidLong + " AND namespace_int=" + nsInt);
            final boolean prevSaved = uprs.first();
            if (!prevSaved) {
                uprs.moveToInsertRow();
                if (Log.loggingDebug) {
                    Log.debug("Database.saveObject: obj not in database, moved to insert row: " + obj);
                }
            }
            if (Log.loggingDebug) {
                Log.debug("Database.saveObjectHelper: saving obj: " + entity.getName());
            }
            this.updateRow(uprs, instanceOid, loc, entity.getOid(), nsInt, data, entity.getName(), entity.getType().getTypeName(), persistenceKey);
            if (prevSaved) {
                uprs.updateRow();
            }
            else {
                uprs.insertRow();
            }
            Log.debug("done with saving char to the database");
        }
        catch (Exception e) {
            Log.exception("saveObjectHelper", e);
            throw new AORuntimeException("database error", e);
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
    
    private void updateRow(final ResultSet uprs, final OID instanceOid, final Point loc, final OID oid, final int nsInt, final byte[] data, final String name, final String type, final String persistenceKey) throws SQLException, IOException {
        if (Log.loggingDebug) {
            Log.debug("byte array length=" + data.length);
        }
        Log.debug("Database.updateRow: creating blob from byte stream");
        final Blob blob = new SerialBlob(data);
        if (Log.loggingDebug) {
            Log.debug("Database.updateRow: created blob, datalength=" + data.length + ", bloblength=" + blob.length());
        }
        uprs.updateLong("obj_id", oid.toLong());
        uprs.updateInt("namespace_int", nsInt);
        uprs.updateString("world_name", Engine.getWorldName());
        if (instanceOid != null) {
            uprs.updateLong("instance", instanceOid.toLong());
        }
        else {
            uprs.updateNull("instance");
        }
        if (loc != null) {
            uprs.updateFloat("locX", loc.getX());
            uprs.updateFloat("locY", loc.getY());
            uprs.updateFloat("locZ", loc.getZ());
        }
        else {
            uprs.updateNull("locX");
            uprs.updateNull("locY");
            uprs.updateNull("locZ");
        }
        uprs.updateString("type", type);
        uprs.updateString("name", name);
        if (persistenceKey != null) {
            uprs.updateString("persistence_key", persistenceKey);
        }
        uprs.updateBlob("data", blob);
    }
    
    public List<OID> getGameIDs(final String worldName, final OID atavismID) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT obj_id FROM player_character WHERE account_id = " + atavismID.toLong() + " AND world_name = '" + worldName + "'";
            rs = stmt.executeQuery(query);
            final LinkedList<OID> l = new LinkedList<OID>();
            while (rs.next()) {
                final OID gameId = OID.fromLong(rs.getLong(1));
                l.add(gameId);
                if (Log.loggingDebug) {
                    Log.debug("getgameid: atavismid " + atavismID + " maps to gameID=" + gameId);
                }
            }
            if (l.isEmpty() && Log.loggingDebug) {
                Log.debug("getgameid: found no mapping gameids for atavismid " + atavismID + " and worldName " + worldName);
            }
            return l;
        }
        catch (Exception e) {
            Log.exception("getGameIDs", e);
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
    
    public int getAccountCount(final String worldName) {
        Statement stmt = null;
        ResultSet rs = null;
        int count = -1;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT COUNT(*) FROM player_character WHERE world_name = '" + worldName + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }
        catch (Exception e) {
            Log.exception("getAccountCount", e);
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
    
    public String getUserName(final OID uid) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String query = "SELECT username FROM account WHERE account_id = " + uid.toLong();
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
    
    public OID getLocation(final OID oid, final Namespace ns, final Point location) {
        Statement stmt = null;
        ResultSet rs = null;
        final int nsInt = ns.getNumber();
        final long oidLong = oid.toLong();
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery("SELECT locX,locY,locZ,instance FROM objstore WHERE obj_id=" + oidLong + " AND namespace_int=" + nsInt);
            if (!rs.next()) {
                return null;
            }
            location.setX(rs.getInt(1));
            location.setY(rs.getInt(2));
            location.setZ(rs.getInt(3));
            return OID.fromLong(rs.getLong(4));
        }
        catch (SQLException ex) {
            Log.exception("Database.getLocation()", ex);
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
    
    public OidChunk getOidChunk(final int chunkSize) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            this.conn.setAutoCommit(false);
            stmt = this.conn.createStatement(1004, 1007);
            final ResultSet rs = stmt.executeQuery("SELECT free_oid FROM oid_manager WHERE token = 1");
            if (!rs.next()) {
                throw new AORuntimeException("Database.getOidChunk: no free chunks");
            }
            final long freeOid = rs.getLong("free_oid");
            stmt.close();
            stmt = this.conn.createStatement();
            final String update = "UPDATE oid_manager SET free_oid = " + (freeOid + chunkSize);
            stmt.executeUpdate(update);
            this.conn.commit();
            return new OidChunk(freeOid, freeOid + chunkSize - 1L);
        }
        catch (Exception e) {
            throw new AORuntimeException("Database.getOidChunk", e);
        }
        finally {
            try {
                this.conn.setAutoCommit(true);
            }
            catch (SQLException ex) {}
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
    
    public boolean registerStatusReportingPlugin(final EnginePlugin plugin, final long runId) {
        Log.debug("Registering plugin: " + plugin.getName());
        Statement stmt = null;
        this.unregisterStatusReportingPlugin(plugin);
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String update = "INSERT INTO plugin_status (world_name, agent_name, plugin_name, plugin_type, host_name, pid, run_id, percent_cpu_load, last_update_time, next_update_time, status, info) VALUES ('" + Engine.getWorldName() + "', " + "'" + Engine.getAgent().getName() + "', " + "'" + plugin.getName() + "', " + "'" + plugin.getPluginType() + "', " + "'" + Engine.getEngineHostName() + "', " + "0, " + runId + ", " + "'" + plugin.getPercentCPULoad() + "', " + System.currentTimeMillis() + ", " + "0, " + "'" + StringEscaper.escapeString(plugin.getPluginStatus()) + "', " + "'" + StringEscaper.escapeString(plugin.getPluginInfo()) + "' " + ")";
            final int rows = stmt.executeUpdate(update);
            return rows >= 1;
        }
        catch (Exception e) {
            Log.exception("Database.registerStatusReportingPlugin", e);
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
    
    public boolean unregisterStatusReportingPlugin(final EnginePlugin plugin) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final String update = "DELETE FROM plugin_status WHERE world_name='" + Engine.getWorldName() + "' AND " + "plugin_name='" + plugin.getName() + "'";
            final int rows = stmt.executeUpdate(update);
            return rows >= 1;
        }
        catch (Exception e) {
            Log.exception("Database.unregisterStatusReportingPlugin", e);
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
    
    public boolean updatePluginStatus(final EnginePlugin plugin, final long nextUpdateTime) {
        Statement stmt = null;
        try {
            this.dbLock.lock();
            stmt = this.conn.createStatement();
            final long now = System.currentTimeMillis();
            final String update = "UPDATE plugin_status SET last_update_time=" + now + ", " + "next_update_time=" + nextUpdateTime + ", " + "status='" + StringEscaper.escapeString(plugin.getPluginStatus()) + "', " + "percent_cpu_load='" + plugin.getPercentCPULoad() + "' " + "WHERE world_name='" + Engine.getWorldName() + "' AND " + "plugin_name='" + plugin.getName() + "'";
            final int rows = stmt.executeUpdate(update);
            return rows >= 1;
        }
        catch (Exception e) {
            Log.exception("updatePluginStatus", e);
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
    
    public List<PluginStatus> getPluginStatus(final String pluginType) {
        Statement stmt = null;
        final List<PluginStatus> statusList = new LinkedList<PluginStatus>();
        try {
            this.dbLock.lock();
            this.conn.setAutoCommit(false);
            stmt = this.conn.createStatement(1004, 1007);
            String select = "SELECT * FROM plugin_status WHERE world_name='" + Engine.getWorldName() + "'";
            if (pluginType != null) {
                select = select + " AND plugin_type='" + pluginType + "'";
            }
            final ResultSet rs = stmt.executeQuery(select);
            while (rs.next()) {
                final PluginStatus status = new PluginStatus();
                statusList.add(status);
                status.world_name = rs.getString("world_name");
                status.agent_name = rs.getString("agent_name");
                status.plugin_name = rs.getString("plugin_name");
                status.plugin_type = rs.getString("plugin_type");
                status.host_name = rs.getString("host_name");
                status.pid = rs.getInt("pid");
                status.run_id = rs.getLong("run_id");
                status.percent_cpu_load = rs.getInt("percent_cpu_load");
                status.last_update_time = rs.getLong("last_update_time");
                status.next_update_time = rs.getLong("next_update_time");
                status.status = rs.getString("status");
                status.info = rs.getString("info");
            }
            return statusList;
        }
        catch (Exception e) {
            Log.exception("Database.getPluginStatus", e);
            return statusList;
        }
        finally {
            try {
                this.conn.setAutoCommit(true);
            }
            catch (SQLException ex) {}
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
        Log.error("Database reconnect: url=" + Engine.getDBUrl());
        int failCount = 0;
        this.dbLock.lock();
        while (true) {
            try {
                this.conn = DriverManager.getConnection(Engine.getDBUrl(), Engine.getDBUser(), Engine.getDBPassword());
                Log.info("Database: reconnected to " + Engine.getDBUrl());
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
    
    static {
        Database.logger = Logger.getLogger((Class)Database.class);
        Database.largestNamespaceInt = 0;
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
                    if (Database.this.conn == null) {
                        continue;
                    }
                    Database.this.ping();
                }
                catch (AORuntimeException e2) {
                    Log.exception("Database.KeepAlive: ping caught exception", e2);
                }
            }
        }
    }
    
    public static class XMLExceptionListener implements ExceptionListener
    {
        @Override
        public void exceptionThrown(final Exception e) {
            Log.exception("Database.loadEntity", e);
        }
    }
    
    public static class StringEscaper
    {
        private static Map<Character, Character> toStringSequences;
        private static Map<Character, Character> fromStringSequences;
        private static StringEscaper instance;
        
        private StringEscaper() {
            StringEscaper.toStringSequences = new HashMap<Character, Character>();
            StringEscaper.fromStringSequences = new HashMap<Character, Character>();
            this.add('\0', '0');
            this.add('\'', '\'');
            this.add('\"', '\"');
            this.add('\b', 'b');
            this.add('\r', 'r');
            this.add('\n', 'n');
            this.add('\t', 't');
            this.add('\u001a', 'Z');
            this.add('\\', '\\');
            this.add('%', '%');
        }
        
        private void add(final char from, final char to) {
            StringEscaper.toStringSequences.put(from, to);
            StringEscaper.fromStringSequences.put(to, from);
        }
        
        public static String escapeString(final String input) {
            if (StringEscaper.instance == null) {
                StringEscaper.instance = new StringEscaper();
            }
            final int length = (input == null) ? 0 : input.length();
            final StringBuilder sb = new StringBuilder(length + 10);
            for (int i = 0; i < length; ++i) {
                final char ch = input.charAt(i);
                final Character replacement = StringEscaper.toStringSequences.get(ch);
                if (replacement != null) {
                    sb.append('\\');
                    sb.append(replacement);
                }
                else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
        
        public static String unescapeString(final String input) {
            if (StringEscaper.instance == null) {
                StringEscaper.instance = new StringEscaper();
            }
            final StringBuilder sb = new StringBuilder(input.length() + 10);
            int i = 0;
            while (i < input.length()) {
                char ch = input.charAt(i);
                if (ch == '\\') {
                    ++i;
                    ch = input.charAt(i);
                    final char replacement = StringEscaper.fromStringSequences.get(ch);
                    sb.append(replacement);
                }
                else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
        
        static {
            StringEscaper.toStringSequences = null;
            StringEscaper.fromStringSequences = null;
            StringEscaper.instance = null;
        }
    }
    
    public static class OidChunk
    {
        public long begin;
        public long end;
        
        public OidChunk(final long begin, final long end) {
            this.begin = begin;
            this.end = end;
        }
    }
}
