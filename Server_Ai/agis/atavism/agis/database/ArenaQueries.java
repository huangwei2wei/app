// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

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
import java.sql.Connection;

public class ArenaQueries
{
    static Connection con;
    private static int PORT;
    private static String USERNAME;
    private static String PASSWORD;
    private static String IPADDRESS;
    private static String DATABASENAME;
    
    static {
        ArenaQueries.con = initConnection();
    }
    
    private static boolean loadStrings() {
        final Properties p = new Properties();
        FileReader r;
        try {
            r = new FileReader("arena.ini");
        }
        catch (FileNotFoundException e) {
            System.out.println("ANDREW: File arena.ini could not be found, using default connection strings");
            ArenaQueries.PORT = 3306;
            ArenaQueries.USERNAME = "root";
            ArenaQueries.PASSWORD = "test";
            ArenaQueries.DATABASENAME = "admin";
            ArenaQueries.IPADDRESS = "127.0.0.1";
            return true;
        }
        final BufferedReader b = new BufferedReader(r);
        try {
            p.load(b);
        }
        catch (IOException e2) {
            System.out.println("ANDREW: File arena.ini could not be read, using default connection strings");
            ArenaQueries.PORT = 3306;
            ArenaQueries.USERNAME = "root";
            ArenaQueries.PASSWORD = "test";
            ArenaQueries.DATABASENAME = "admin";
            ArenaQueries.IPADDRESS = "127.0.0.1";
            return true;
        }
        try {
            ArenaQueries.PORT = Integer.parseInt(p.getProperty("port"));
        }
        catch (NumberFormatException e3) {
            ArenaQueries.PORT = 3306;
        }
        ArenaQueries.IPADDRESS = p.getProperty("ip");
        ArenaQueries.USERNAME = p.getProperty("username");
        ArenaQueries.PASSWORD = p.getProperty("password");
        ArenaQueries.DATABASENAME = p.getProperty("database");
        System.out.println("ANDREW: File arena.ini should have been successfully read");
        return true;
    }
    
    public static String getConnectionString() {
        return "jdbc:mysql://" + ArenaQueries.IPADDRESS + ":" + ArenaQueries.PORT + "/" + ArenaQueries.DATABASENAME;
    }
    
    public static Connection initConnection() {
        try {
            if (loadStrings()) {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                return DriverManager.getConnection(getConnectionString(), ArenaQueries.USERNAME, ArenaQueries.PASSWORD);
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
        ArenaQueries.con.close();
    }
    
    public void close() {
        try {
            ArenaQueries.con.commit();
            ArenaQueries.con.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public PreparedStatement prepare(final String sql) {
        this.checkConnection();
        try {
            return ArenaQueries.con.prepareStatement(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ResultSet executeSelect(final String query) {
        this.checkConnection();
        try {
            return ArenaQueries.con.createStatement().executeQuery(query);
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
    
    public boolean executeInsert(final String query) {
        this.checkConnection();
        try {
            return ArenaQueries.con.createStatement().execute(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int executeInsert(final PreparedStatement ps) {
        this.checkConnection();
        try {
            return ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int executeUpdate(final String query) {
        this.checkConnection();
        try {
            return ArenaQueries.con.createStatement().executeUpdate(query);
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
            if (ArenaQueries.con.isClosed()) {
                ArenaQueries.con = initConnection();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
