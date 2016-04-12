// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;
import atavism.server.util.Log;
import java.sql.Connection;

public class AdminQueries
{
    static Connection con;
    private static int PORT;
    private static String USERNAME;
    private static String PASSWORD;
    private static String IPADDRESS;
    private static String DATABASENAME;
    
    static {
        AdminQueries.con = initConnection();
    }
    
    public AdminQueries() {
        Log.debug("AdminDatabase: starting keepalive");
        final Thread keepAliveThread = new Thread(new KeepAlive(), "DBKeepalive");
        keepAliveThread.start();
    }
    
    private static boolean loadStrings() {
        final Properties p = new Properties();
        FileReader r;
        try {
            r = new FileReader("content_admin_connection.ini");
        }
        catch (FileNotFoundException e) {
            System.out.println("ADMIN: File content_admin_connection.ini could not be found, using default connection strings");
            AdminQueries.PORT = 3306;
            AdminQueries.USERNAME = "root";
            AdminQueries.PASSWORD = "test";
            AdminQueries.DATABASENAME = "admin";
            AdminQueries.IPADDRESS = "127.0.0.1";
            return true;
        }
        final BufferedReader b = new BufferedReader(r);
        try {
            p.load(b);
        }
        catch (IOException e2) {
            System.out.println("ADMIN: File admin.ini could not be read, using default connection strings");
            AdminQueries.PORT = 3306;
            AdminQueries.USERNAME = "root";
            AdminQueries.PASSWORD = "test";
            AdminQueries.DATABASENAME = "admin";
            AdminQueries.IPADDRESS = "127.0.0.1";
            return true;
        }
        try {
            AdminQueries.PORT = Integer.parseInt(p.getProperty("admin_port"));
        }
        catch (NumberFormatException e3) {
            AdminQueries.PORT = 3306;
        }
        AdminQueries.IPADDRESS = p.getProperty("admin_ip");
        AdminQueries.USERNAME = p.getProperty("admin_username");
        AdminQueries.PASSWORD = p.getProperty("admin_password");
        AdminQueries.DATABASENAME = p.getProperty("admin_database");
        return true;
    }
    
    public static String getConnectionString() {
        return "jdbc:mysql://" + AdminQueries.IPADDRESS + ":" + AdminQueries.PORT + "/" + AdminQueries.DATABASENAME + "?useUnicode=yes&characterEncoding=UTF-8";
    }
    
    public static Connection initConnection() {
        try {
            if (loadStrings()) {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                return DriverManager.getConnection(getConnectionString(), AdminQueries.USERNAME, AdminQueries.PASSWORD);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        catch (InstantiationException e3) {
            e3.printStackTrace();
        }
        catch (IllegalAccessException e4) {
            e4.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        AdminQueries.con.close();
    }
    
    public void close() {
        try {
            AdminQueries.con.commit();
            AdminQueries.con.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public PreparedStatement prepare(final String sql) {
        this.checkConnection();
        try {
            return AdminQueries.con.prepareStatement(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ResultSet executeSelect(final String query) {
        this.checkConnection();
        try {
            return AdminQueries.con.createStatement().executeQuery(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ResultSet executeSelect(final PreparedStatement ps) {
        this.checkConnection();
        try {
            return ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int executeInsert(final String query) {
        this.checkConnection();
        try {
            final Statement stmt = AdminQueries.con.createStatement();
            stmt.execute(query, 1);
            final ResultSet rs = stmt.getGeneratedKeys();
            int insertedKeyValue = -1;
            if (rs.next()) {
                insertedKeyValue = rs.getInt(1);
            }
            Log.debug("Executed insert and got key: " + insertedKeyValue);
            return insertedKeyValue;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int executeInsert(final PreparedStatement ps) {
        this.checkConnection();
        try {
            ps.executeUpdate();
            final ResultSet rs = ps.getGeneratedKeys();
            int insertedKeyValue = -1;
            if (rs.next()) {
                insertedKeyValue = rs.getInt(1);
            }
            Log.debug("Executed insert and got key: " + insertedKeyValue);
            return insertedKeyValue;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int executeUpdate(final String query) {
        this.checkConnection();
        try {
            return AdminQueries.con.createStatement().executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int executeUpdate(final PreparedStatement ps) {
        this.checkConnection();
        try {
            return ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private void checkConnection() {
        try {
            if (AdminQueries.con.isClosed()) {
                AdminQueries.con = initConnection();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void ping() {
        Log.debug("AdminDatabase: ping");
        Statement stmt = null;
        try {
            final String sql = "SELECT 1 from account";
            stmt = AdminQueries.con.createStatement();
            stmt.executeQuery(sql);
        }
        catch (Exception e) {
            this.reconnect();
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException sqlEx) {
                    stmt = null;
                }
            }
            return;
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
    
    void reconnect() {
        Log.error("Database reconnect: url=" + Engine.getDBUrl());
        int failCount = 0;
        try {
            AdminQueries.con = initConnection();
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
                    Log.exception("AdminDatabase.KeepAlive: interrupted", (Exception)e);
                }
                try {
                    if (AdminQueries.con == null) {
                        continue;
                    }
                    AdminQueries.this.ping();
                }
                catch (AORuntimeException e2) {
                    Log.exception("AdminDatabase.KeepAlive: ping caught exception", (Exception)e2);
                }
            }
        }
    }
}
