// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

import java.sql.Timestamp;
import atavism.agis.plugins.AgisInventoryPlugin;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import atavism.agis.objects.Mail;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ArrayList;
import java.sql.Date;
import atavism.server.objects.InstanceTemplate;
import java.util.LinkedList;
import atavism.agis.util.HelperFunctions;
import java.util.HashMap;
import atavism.agis.plugins.AgisInventoryClient;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import atavism.server.util.Log;
import atavism.server.engine.OID;

public class AccountDatabase
{
    protected static AdminQueries queries;
    private static final String accountTableName = "account";
    private static final String characterTableName = "account_character";
    private static final String statsTableName = "server_stats";
    private static final String instanceTemplateTableName = "instance_template";
    private static final String mailTableName = "character_mail";
    
    static {
        AccountDatabase.queries = new AdminQueries();
    }
    
    public boolean characterCreated(final OID accountID, final String accountName, final OID characterOID, final String characterName) {
        try {
            Log.debug("ACCOUNT: attempting to add character oid: " + characterOID + " to account entry: " + accountID);
            this.addAccountCharacter(accountID, characterOID, characterName);
            final String selectString = "SELECT username FROM account where id=" + accountID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null && !rs.next()) {
                this.createAccount(accountID, accountName, characterOID, characterName);
            }
        }
        catch (SQLException ex) {}
        return true;
    }
    
    public int createAccount(final OID accountID, final String accountName, final OID characterOID, final String characterName) {
        Log.debug("ACCOUNT: creating entry for accountID: " + accountID + " with character OID:" + characterOID + " and character name: " + characterName);
        final long timestamp = System.currentTimeMillis();
        final String columnNames = "id,username,status,last_login,created,coin_current,coin_total,coin_used";
        final String values = String.valueOf(accountID.toLong()) + ",'" + accountName + "'," + 1 + ",FROM_UNIXTIME(" + timestamp + "),FROM_UNIXTIME(" + timestamp + ")," + 0 + "," + 0 + "," + 0;
        final String insertString = "INSERT INTO `account` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = AccountDatabase.queries.executeInsert(insertString);
        return inserted;
    }
    
    private boolean addAccountCharacter(final OID accountID, final OID characterOID, final String characterName) {
        Log.debug("ACCOUNT: inserting character:" + characterOID + " in account: " + accountID);
        try {
            final String columnNames = "characterId,characterName,accountId";
            final PreparedStatement stmt = AccountDatabase.queries.prepare("INSERT INTO account_character (" + columnNames + ") values (?, ?, ?)");
            stmt.setLong(1, characterOID.toLong());
            stmt.setString(2, characterName);
            stmt.setLong(3, accountID.toLong());
            AccountDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    public boolean characterDeleted(final OID accountID, final OID characterOID, final String characterName) {
        final String deleteString = "DELETE FROM `account_character` WHERE characterId = " + characterOID.toLong();
        AccountDatabase.queries.executeUpdate(deleteString);
        return true;
    }
    
    public int getAccountStatus(final OID accountID) {
        try {
            Log.debug("ACCOUNT: getting account status for account entry: " + accountID);
            final String selectString = "SELECT status FROM account where id=" + accountID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null && rs.next()) {
                final int status = rs.getInt("status");
                Log.debug("ACCOUNT: account status for account: " + accountID + " is: " + status);
                return status;
            }
        }
        catch (SQLException ex) {}
        return 1;
    }
    
    public int getNumIslands(final OID accountID) {
        try {
            Log.debug("ACCOUNT: getting island limit for account entry: " + accountID);
            final String selectString = "SELECT islands_available FROM account where id=" + accountID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null && rs.next()) {
                final int numAvailable = rs.getInt("islands_available");
                Log.debug("ACCOUNT: account status for account: " + accountID + " is: " + numAvailable);
                return numAvailable;
            }
        }
        catch (SQLException ex) {}
        return 0;
    }
    
    public int getNumCharacterSlots(final OID accountID) {
        try {
            Log.debug("ACCOUNT: getting account status for account entry: " + accountID);
            final String selectString = "SELECT character_slots FROM account where id=" + accountID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null && rs.next()) {
                final int characterSlots = rs.getInt("character_slots");
                return characterSlots;
            }
        }
        catch (SQLException ex) {}
        return 2;
    }
    
    public void checkCharacterPurchases(final OID characterOID) {
        try {
            Log.debug("ACCOUNT: getting purchases for character: " + characterOID.toLong());
            final String selectString = "SELECT * FROM character_purchases where used = 0 and character_id=" + characterOID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    final int itemID = rs.getInt("itemID");
                    AgisInventoryClient.sendPurchaseMail(characterOID, itemID);
                    final String updateString = "UPDATE character_purchases set used = 1 where id=" + rs.getInt("id");
                    AccountDatabase.queries.executeUpdate(updateString);
                }
            }
        }
        catch (SQLException ex) {}
    }
    
    public int getCharacterCoinAmount(final OID accountID) {
        try {
            Log.debug("ACCOUNT: getting coin amount for account entry: " + accountID);
            final String selectString = "SELECT coin_current FROM account where id=" + accountID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null && rs.next()) {
                return rs.getInt("coin_current");
            }
        }
        catch (SQLException ex) {}
        return 0;
    }
    
    public void alterCharacterCoinAmount(final OID accountID, final int delta) {
        Log.debug("ACCOUNT: getting purchases for character: " + accountID.toLong());
        final String updateString = "UPDATE account set coin_current = coin_current + " + delta + " where id=" + accountID.toLong();
        AccountDatabase.queries.executeUpdate(updateString);
    }
    
    public boolean characterLoggedIn(final OID accountID) {
        Log.debug("ACCOUNT: updating last login: for account: " + accountID);
        final long timestamp = System.currentTimeMillis();
        final String updateString = "UPDATE account set last_login = FROM_UNIXTIME(" + timestamp + ") where id=" + accountID.toLong();
        AccountDatabase.queries.executeUpdate(updateString);
        return true;
    }
    
    public boolean characterLoggedOut(final OID accountID) {
        Log.debug("ACCOUNT: updating last logout: for account: " + accountID);
        final long timestamp = System.currentTimeMillis();
        final String updateString = "UPDATE account set last_logout = FROM_UNIXTIME(" + timestamp + ") where id=" + accountID.toLong();
        AccountDatabase.queries.executeUpdate(updateString);
        return true;
    }
    
    public HashMap<OID, String> getFriends(final OID characterOID) {
        final HashMap<OID, String> friends = new HashMap<OID, String>();
        try {
            Log.debug("ACCOUNT: getting friends of character: " + characterOID.toLong());
            final String selectString = "SELECT * FROM character_friends where character_id=" + characterOID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    final String friendName = HelperFunctions.readEncodedString(rs.getBytes("friend_name"));
                    final OID friendOID = OID.fromLong(rs.getLong("friend_id"));
                    friends.put(friendOID, friendName);
                }
            }
        }
        catch (SQLException ex) {}
        return friends;
    }
    
    public LinkedList<OID> getFriendsOf(final OID characterOID) {
        final LinkedList<OID> friends = new LinkedList<OID>();
        try {
            Log.debug("ACCOUNT: getting friends of character: " + characterOID.toLong());
            final String selectString = "SELECT * FROM character_friends where friend_id=" + characterOID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    final OID friendOID = OID.fromLong(rs.getLong("character_id"));
                    friends.add(friendOID);
                }
            }
        }
        catch (SQLException ex) {}
        return friends;
    }
    
    public void addFriend(final OID characterOID, final OID friendOID, final String friendName) {
        Log.debug("ACCOUNT: creating friend entry linking character OID:" + characterOID.toLong() + " and friend name: " + friendName);
        final String tableName = "character_friends";
        final String columnNames = "character_id,friend_id,friend_name";
        final String values = characterOID.toLong() + "," + friendOID.toLong() + ",'" + friendName + "'";
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        AccountDatabase.queries.executeInsert(insertString);
    }
    
    public boolean addSkin(final OID accountOID, final String skin) {
        Log.debug("ACCOUNT: creating skin entry with account OID:" + accountOID.toLong() + " and skin: " + skin);
        final String tableName = "character_skins";
        try {
            Log.debug("ACCOUNT: getting skins of account: " + accountOID.toLong());
            final String selectString = "SELECT * FROM " + tableName + " where account_id=" + accountOID.toLong() + " and character_skin='" + skin + "'";
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null && rs.next()) {
                return false;
            }
        }
        catch (SQLException ex) {}
        final String columnNames = "account_id,character_skin,created,source";
        final String values = accountOID.toLong() + ",'" + skin + "', NOW(), 'Merchant'";
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        AccountDatabase.queries.executeInsert(insertString);
        return true;
    }
    
    public boolean addItem(final OID accountOID, final int itemID, final int amount) {
        Log.debug("ACCOUNT: creating item entry with account OID:" + accountOID.toLong() + " and item: " + itemID);
        final String tableName = "character_items";
        try {
            Log.debug("ACCOUNT: getting items of account: " + accountOID.toLong());
            final String selectString = "SELECT * FROM " + tableName + " where account_id=" + accountOID.toLong() + " and itemID=" + itemID;
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null && rs.next()) {
                return false;
            }
        }
        catch (SQLException ex) {}
        final String columnNames = "account_id,itemID,amount";
        final String values = accountOID.toLong() + "," + itemID + ", " + amount;
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        AccountDatabase.queries.executeInsert(insertString);
        return true;
    }
    
    public void alterItemAmount(final OID accountID, final int itemID, final int delta) {
        Log.debug("ACCOUNT: altering item amount for account " + accountID.toLong());
        final String tableName = "character_items";
        final String updateString = "UPDATE " + tableName + " set amount = amount + " + delta + " where account_id=" + accountID.toLong() + " and itemID = " + itemID;
        AccountDatabase.queries.executeUpdate(updateString);
    }
    
    public boolean removeItem(final OID accountOID, final int itemID) {
        Log.debug("ACCOUNT: creating weapon entry with account OID:" + accountOID.toLong() + " and skin: " + itemID);
        final String tableName = "character_items";
        int entryID = -1;
        try {
            Log.debug("ACCOUNT: getting items of account: " + accountOID.toLong());
            final String selectString = "SELECT id FROM " + tableName + " where account_id=" + accountOID.toLong() + " and itemID=" + itemID;
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    entryID = rs.getInt("id");
                }
            }
        }
        catch (SQLException ex) {}
        if (entryID == -1) {
            return false;
        }
        final String deleteString = "DELETE FROM `" + tableName + "` where id = " + entryID;
        AccountDatabase.queries.executeUpdate(deleteString);
        return true;
    }
    
    public HashMap<Integer, Integer> getItems(final OID accountOID) {
        final HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
        try {
            Log.debug("ACCOUNT: getting items from account: " + accountOID.toLong());
            final String selectString = "SELECT * FROM character_items where account_id=" + accountOID.toLong();
            final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    items.put(rs.getInt("itemID"), rs.getInt("amount"));
                }
            }
        }
        catch (SQLException ex) {}
        return items;
    }
    
    public HashMap<String, InstanceTemplate> loadInstanceTemplateData() {
        final HashMap<String, InstanceTemplate> list = new HashMap<String, InstanceTemplate>();
        try {
            final PreparedStatement ps = AccountDatabase.queries.prepare("SELECT * FROM instance_template");
            final ResultSet rs = AccountDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final InstanceTemplate island = new InstanceTemplate();
                    island.setID(rs.getInt("id"));
                    island.setCategory(rs.getInt("category"));
                    island.setName(HelperFunctions.readEncodedString(rs.getBytes("island_name")));
                    final OID administrator = OID.fromLong(rs.getLong("administrator"));
                    island.setAdministrator(administrator);
                    island.setIsPublic(rs.getBoolean("public"));
                    island.setPassword(HelperFunctions.readEncodedString(rs.getBytes("password")));
                    island.setDevelopers((LinkedList)this.loadIslandDevelopers(island.getID()));
                    island.setIslandType(rs.getInt("islandType"));
                    island.setCreateOnStartup(rs.getBoolean("createOnStartup"));
                    island.setStyle(HelperFunctions.readEncodedString(rs.getBytes("style")));
                    island.setDescription(HelperFunctions.readEncodedString(rs.getBytes("description")));
                    island.setRating(rs.getInt("rating"));
                    island.setSize(rs.getInt("size"));
                    island.setPopulationLimit(rs.getInt("populationLimit"));
                    final Date subscriptionExpiration = rs.getDate("subscription");
                    Log.warn("Island " + island.getName() + " has subscription Expiration: " + subscriptionExpiration);
                    island.setContentPacks((LinkedList)this.loadIslandContentPacks(island.getID()));
                    island.setPortals((HashMap)this.loadIslandPortals(island.getID()));
                    list.put(HelperFunctions.readEncodedString(rs.getBytes("island_name")), island);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public LinkedList<String> loadIslandContentPacks(final int islandID) {
        final LinkedList<String> contentPacks = new LinkedList<String>();
        return contentPacks;
    }
    
    public LinkedList<OID> loadIslandDevelopers(final int islandID) {
        final LinkedList<OID> list = new LinkedList<OID>();
        try {
            final PreparedStatement ps = AccountDatabase.queries.prepare("SELECT * FROM island_developers where island = " + islandID);
            final ResultSet rs = AccountDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final OID developer = OID.fromLong(rs.getLong("developer"));
                    list.add(developer);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<String> getIslandName(final String islandName) {
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final PreparedStatement ps = AccountDatabase.queries.prepare("SELECT island_name FROM instance_template where island_name = '" + islandName + "'");
            final ResultSet rs = AccountDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    list.add(HelperFunctions.readEncodedString(rs.getBytes("island_name")));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public OID getIslandAdministrator(final String islandName) {
        OID administrator = null;
        try {
            final PreparedStatement ps = AccountDatabase.queries.prepare("SELECT administrator FROM instance_template where island_name = '" + islandName + "'");
            final ResultSet rs = AccountDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    administrator = OID.fromLong(rs.getLong("administrator"));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return administrator;
    }
    
    public HashMap<String, HashMap<String, Float>> loadIslandPortals(final int islandID) {
        final HashMap<String, HashMap<String, Float>> list = new HashMap<String, HashMap<String, Float>>();
        try {
            final PreparedStatement ps = AccountDatabase.queries.prepare("SELECT * FROM island_portals where island = " + islandID);
            final ResultSet rs = AccountDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final HashMap<String, Float> portalProps = new HashMap<String, Float>();
                    portalProps.put("id", (float)rs.getInt("id"));
                    portalProps.put("portalType", (float)rs.getInt("portalType"));
                    portalProps.put("faction", (float)rs.getInt("faction"));
                    portalProps.put("displayID", (float)rs.getInt("displayID"));
                    portalProps.put("locX", rs.getFloat("locX"));
                    portalProps.put("locY", rs.getFloat("locY"));
                    portalProps.put("locZ", rs.getFloat("locZ"));
                    portalProps.put("orientX", rs.getFloat("orientX"));
                    portalProps.put("orientY", rs.getFloat("orientY"));
                    portalProps.put("orientZ", rs.getFloat("orientZ"));
                    portalProps.put("orientW", rs.getFloat("orientW"));
                    list.put(HelperFunctions.readEncodedString(rs.getBytes("name")), portalProps);
                    Log.debug("PORTAL: loaded portal " + rs.getString("name") + " in island: " + islandID);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void writeIslandData(final InstanceTemplate island, final String template) {
        Log.debug("Writing island data to database");
        final String tableName = "instance_template";
        final String columnNames = "island_name,template,administrator,status,public,password,islandType,createOnStartup,style,recommendedLevel,description,size";
        final String values = "'" + island.getName() + "','" + template + "'," + island.getAdministrator().toLong() + ",'Active'," + island.getIsPublic() + ",'',0," + island.getCreateOnStartup() + ",'',1,'',1";
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = AccountDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote island data to database with inserted: " + inserted);
    }
    
    public HashMap<String, HashMap<String, Float>> writePortalData(final int islandID, final HashMap<String, HashMap<String, Float>> portals) {
        Log.debug("Writing island portal data to database");
        final HashMap<String, HashMap<String, Float>> newPortals = new HashMap<String, HashMap<String, Float>>();
        final String tableName = "island_portals";
        final String columnNames = "island,portalType,faction,locX,locY,locZ,orientX,orientY,orientZ,orientW,displayID";
        for (final String name : portals.keySet()) {
            final HashMap<String, Float> portalProps = portals.get(name);
            final String values = String.valueOf(islandID) + "," + 1 + "," + portalProps.get("faction") + "," + portalProps.get("locX") + "," + portalProps.get("locY") + "," + portalProps.get("locZ") + "," + portalProps.get("orientX") + "," + portalProps.get("orientY") + "," + portalProps.get("orientZ") + "," + portalProps.get("orientW") + "," + portalProps.get("displayID");
            final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
            final int inserted = AccountDatabase.queries.executeInsert(insertString);
            Log.debug("Wrote island portal data to database with inserted: " + inserted);
            if (inserted != -1) {
                newPortals.put(name, portalProps);
            }
        }
        return newPortals;
    }
    
    public void editPortalData(final String portalName, final HashMap<String, Float> portalProps) {
        Log.debug("Writing portal data to database");
        final String tableName = "island_portals";
        try {
            final PreparedStatement stmt = AccountDatabase.queries.prepare("UPDATE " + tableName + " set portalType=?, faction=?, locX=?, " + "locY=?, locZ=?, orientX=?, orientY=?, orientZ=?, orientW=?, name=?,  where id=?");
            stmt.setInt(1, (int)(Object)portalProps.get("portalType"));
            stmt.setInt(2, (int)(Object)portalProps.get("faction"));
            stmt.setFloat(3, portalProps.get("locX"));
            stmt.setFloat(4, portalProps.get("locY"));
            stmt.setFloat(5, portalProps.get("locZ"));
            stmt.setFloat(6, portalProps.get("orientX"));
            stmt.setFloat(7, portalProps.get("orientY"));
            stmt.setFloat(8, portalProps.get("orientZ"));
            stmt.setFloat(9, portalProps.get("orientW"));
            stmt.setInt(10, (int)(Object)portalProps.get("displayID"));
            stmt.setString(11, portalName);
            stmt.setInt(12, (int)(Object)portalProps.get("id"));
            Log.debug("ISLANDDB: updating island portal with statement: " + stmt.toString());
            AccountDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {}
    }
    
    public LinkedList<HashMap<String, Serializable>> loadTemplateIslands() {
        final LinkedList<HashMap<String, Serializable>> list = new LinkedList<HashMap<String, Serializable>>();
        try {
            final PreparedStatement ps = AccountDatabase.queries.prepare("SELECT * FROM instance_template");
            final ResultSet rs = AccountDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final HashMap<String, Serializable> templateProps = new HashMap<String, Serializable>();
                    templateProps.put("templateID", rs.getInt("id"));
                    templateProps.put("name", HelperFunctions.readEncodedString(rs.getBytes("name")));
                    templateProps.put("size", rs.getInt("size"));
                    list.add(templateProps);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public HashMap<String, HashMap<String, Float>> loadIslandTemplatePortals(final int templateID) {
        final HashMap<String, HashMap<String, Float>> list = new HashMap<String, HashMap<String, Float>>();
        try {
            final PreparedStatement ps = AccountDatabase.queries.prepare("SELECT * FROM templatePortals where templateID = " + templateID);
            final ResultSet rs = AccountDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final HashMap<String, Float> portalProps = new HashMap<String, Float>();
                    portalProps.put("id", (float)rs.getInt("id"));
                    portalProps.put("portalType", (float)rs.getInt("portalType"));
                    portalProps.put("faction", (float)rs.getInt("faction"));
                    portalProps.put("displayID", (float)rs.getInt("displayID"));
                    portalProps.put("locX", rs.getFloat("locX"));
                    portalProps.put("locY", rs.getFloat("locY"));
                    portalProps.put("locZ", rs.getFloat("locZ"));
                    portalProps.put("orientX", rs.getFloat("orientX"));
                    portalProps.put("orientY", rs.getFloat("orientY"));
                    portalProps.put("orientZ", rs.getFloat("orientZ"));
                    portalProps.put("orientW", rs.getFloat("orientW"));
                    list.put(HelperFunctions.readEncodedString(rs.getBytes("name")), portalProps);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean addNewMail(final Mail iMailToSend) {
        Log.debug("MAILING: attempting to add mail oid: " + iMailToSend.getID());
        final PreparedStatement stmt = AccountDatabase.queries.prepare("INSERT INTO character_mail (mailArchive, recipientId, recipientName, senderId, senderName, mailRead, mailSubject, mailMessage, currencyType, currencyAmount, currencyTaken, CoD, expiry, mailAttachmentItemId1Taken, mailAttachmentItemId1, mailAttachmentItemId2Taken, mailAttachmentItemId2, mailAttachmentItemId3Taken, mailAttachmentItemId3, mailAttachmentItemId4Taken, mailAttachmentItemId4, mailAttachmentItemId5Taken, mailAttachmentItemId5, mailAttachmentItemId6Taken, mailAttachmentItemId6, mailAttachmentItemId7Taken, mailAttachmentItemId7, mailAttachmentItemId8Taken, mailAttachmentItemId8, mailAttachmentItemId9Taken, mailAttachmentItemId9, mailAttachmentItemId10Taken, mailAttachmentItemId10) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            stmt.setBoolean(1, iMailToSend.getMailArchive());
            stmt.setLong(2, iMailToSend.getRecipientOID().toLong());
            stmt.setString(3, iMailToSend.getRecipientName());
            stmt.setLong(4, iMailToSend.getSenderOID().toLong());
            stmt.setString(5, iMailToSend.getSenderName());
            stmt.setBoolean(6, iMailToSend.getMailRead());
            stmt.setString(7, iMailToSend.getSubject());
            stmt.setString(8, iMailToSend.getMessage());
            stmt.setInt(9, iMailToSend.getCurrencyType());
            stmt.setInt(10, iMailToSend.getCurrencyAmount());
            stmt.setBoolean(11, false);
            stmt.setBoolean(12, iMailToSend.getCoD());
            final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Calendar cal = Calendar.getInstance();
            cal.setTime(new java.util.Date());
            if (iMailToSend.getCoD()) {
                cal.add(5, AgisInventoryPlugin.MAIL_COD_LIFE_DAYS);
            }
            else {
                cal.add(5, AgisInventoryPlugin.MAIL_LIFE_DAYS);
            }
            final String strDate = sdfDate.format(cal.getTime());
            stmt.setTimestamp(13, Timestamp.valueOf(strDate));
            final int startingNum = 14;
            for (int i = 0; i < AgisInventoryPlugin.MAIL_ATTACHMENT_COUNT; ++i) {
                stmt.setBoolean(startingNum + i * 2, false);
                if (iMailToSend.getItems() != null && iMailToSend.getItems().size() > i && iMailToSend.getItems().get(i) != null) {
                    stmt.setLong(startingNum + i * 2 + 1, iMailToSend.getItems().get(i).toLong());
                }
                else {
                    stmt.setLong(startingNum + i * 2 + 1, -1L);
                }
            }
            final int id = AccountDatabase.queries.executeInsert(stmt);
            iMailToSend.setID(id);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public ArrayList<Mail> retrieveMail(final OID characterOID) {
        try {
            final ArrayList<Mail> wMails = new ArrayList<Mail>();
            Log.debug("MAIL: retrieving all mail by char oid: " + characterOID);
            String selectString = "SELECT * FROM character_mail WHERE senderId=" + characterOID.toLong() + " AND mailArchive=0 AND expiry < now()";
            ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    Log.debug("MAIL: found sent expired mail");
                    this.returnMail(rs.getInt("mailId"), OID.fromLong(rs.getLong("senderId")), HelperFunctions.readEncodedString(rs.getBytes("senderName")), OID.fromLong(rs.getLong("recipientId")), HelperFunctions.readEncodedString(rs.getBytes("recipientName")), HelperFunctions.readEncodedString(rs.getBytes("mailSubject")), rs.getBoolean("CoD"));
                }
            }
            selectString = "SELECT * FROM character_mail WHERE recipientId=" + characterOID.toLong() + " AND mailArchive=0 AND expiry < now()";
            rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    Log.debug("MAIL: found expired mail in users inbox");
                    this.returnMail(rs.getInt("mailId"), OID.fromLong(rs.getLong("senderId")), HelperFunctions.readEncodedString(rs.getBytes("senderName")), OID.fromLong(rs.getLong("recipientId")), HelperFunctions.readEncodedString(rs.getBytes("recipientName")), HelperFunctions.readEncodedString(rs.getBytes("mailSubject")), rs.getBoolean("CoD"));
                }
            }
            Log.debug("MAIL: completed expired mail check");
            selectString = "SELECT mailId FROM character_mail WHERE recipientId=" + characterOID.toLong() + " AND mailArchive=0";
            rs = AccountDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                while (rs.next()) {
                    wMails.add(this.retrieveSingleMail(rs.getInt("mailId")));
                }
            }
            return wMails;
        }
        catch (SQLException e) {
            Log.error("MAIL: got error retreiving mail: " + e.toString());
            return null;
        }
    }
    
    public Mail retrieveSingleMail(final int mailOID) {
        Log.debug("MAIL: Retrieving single email by oid: " + mailOID);
        final String selectString = "SELECT * FROM character_mail WHERE mailId=" + mailOID;
        final ResultSet rs = AccountDatabase.queries.executeSelect(selectString);
        if (rs != null) {
            try {
                if (rs.first()) {
                    Log.debug("MAIL: Found mail");
                    final Mail wMail = new Mail();
                    wMail.setID(mailOID);
                    wMail.setRecipientOID(this.getOIDFromLongInResultSet(rs, "recipientId"));
                    wMail.setRecipientName(HelperFunctions.readEncodedString(rs.getBytes("recipientName")));
                    wMail.setSenderOID(this.getOIDFromLongInResultSet(rs, "senderId"));
                    wMail.setSenderName(HelperFunctions.readEncodedString(rs.getBytes("senderName")));
                    wMail.setMailRead(rs.getBoolean("mailRead"));
                    wMail.setSubject(HelperFunctions.readEncodedString(rs.getBytes("mailSubject")));
                    wMail.setMessage(HelperFunctions.readEncodedString(rs.getBytes("mailMessage")));
                    wMail.setCurrencyType(rs.getInt("currencyType"));
                    if (!rs.getBoolean("currencyTaken")) {
                        wMail.setCurrencyAmount(rs.getInt("currencyAmount"));
                    }
                    else {
                        wMail.setCurrencyAmount(0);
                    }
                    wMail.setCoD(rs.getBoolean("CoD"));
                    Log.debug("MAIL: Retrieving attachment data");
                    final ArrayList<OID> attachments = new ArrayList<OID>();
                    for (int i = 1; i <= AgisInventoryPlugin.MAIL_ATTACHMENT_COUNT; ++i) {
                        final Long itemOid = rs.getLong("mailAttachmentItemId" + i);
                        if (itemOid != null && itemOid > 0L && !rs.getBoolean("mailAttachmentItemId" + i + "Taken")) {
                            attachments.add(OID.fromLong((long)itemOid));
                        }
                        else {
                            attachments.add(null);
                        }
                    }
                    wMail.setItems(attachments);
                    Log.debug("MAIL: Added mail: " + mailOID);
                    return wMail;
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public boolean returnMail(final int mailID, final OID senderOid, final String senderName, final OID recipientOid, final String recipientName, final String subject, final boolean removeCurrency) {
        Log.debug("MAILING: Deleting mail by oid: " + mailID);
        try {
            final PreparedStatement stmt = AccountDatabase.queries.prepare("UPDATE character_mail set recipientId=?, recipientName=?, senderId=?, senderName=?, mailSubject=?, expiry=?, currencyTaken=?, CoD=? where mailId=?");
            stmt.setLong(1, senderOid.toLong());
            stmt.setString(2, senderName);
            stmt.setLong(3, recipientOid.toLong());
            stmt.setString(4, recipientName);
            if (!subject.startsWith("Returned")) {
                stmt.setString(5, "Returned: " + subject);
            }
            else {
                stmt.setString(5, subject);
            }
            final Calendar cal = Calendar.getInstance();
            cal.setTime(new java.util.Date());
            cal.add(5, AgisInventoryPlugin.MAIL_LIFE_DAYS);
            final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String strDate = sdfDate.format(cal.getTime());
            stmt.setTimestamp(6, Timestamp.valueOf(strDate));
            stmt.setBoolean(7, removeCurrency);
            stmt.setBoolean(8, false);
            stmt.setInt(9, mailID);
            Log.debug("MAILING: returning mail stmt=" + stmt.toString());
            AccountDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean deleteMail(final int mailOID) {
        Log.debug("MAILING: Deleting mail by oid: " + mailOID);
        final String updateString = "UPDATE character_mail SET mailArchive=1 WHERE mailId=" + mailOID;
        AccountDatabase.queries.executeUpdate(updateString);
        return true;
    }
    
    public boolean readMail(final int mailOID) {
        Log.debug("MAILING: putting status read to: " + mailOID);
        final String updateString = "UPDATE character_mail SET mailRead=1 WHERE mailId=" + mailOID;
        AccountDatabase.queries.executeUpdate(updateString);
        return true;
    }
    
    public boolean takeMailItem(final int mailOID, final int itemPos, final boolean CoD) {
        Log.debug("MAILING: taking mail item: " + mailOID);
        if (CoD) {
            final String updateString = "UPDATE character_mail SET mailAttachmentItemId" + (itemPos + 1) + "Taken=1, CoD=0, currencyTaken=1 WHERE mailId=" + mailOID;
            AccountDatabase.queries.executeUpdate(updateString);
        }
        else {
            final String updateString = "UPDATE character_mail SET mailAttachmentItemId" + (itemPos + 1) + "Taken=1 WHERE mailId=" + mailOID;
            AccountDatabase.queries.executeUpdate(updateString);
        }
        return true;
    }
    
    public boolean takeMailCurrency(final int mailOID) {
        Log.debug("MAILING: setting currency to 0 for mail: " + mailOID);
        final String updateString = "UPDATE character_mail SET currencyTaken=1 WHERE mailId=" + mailOID;
        AccountDatabase.queries.executeUpdate(updateString);
        return true;
    }
    
    public OID getOIDFromLongInResultSet(final ResultSet rs, final String wColoumnLabel) throws SQLException {
        return OID.fromLong(rs.getLong(wColoumnLabel));
    }
    
    public void updateServerStat(final String event) {
        Log.debug("SERVER: updating server event: " + event);
        final long timestamp = System.currentTimeMillis();
        if (event.equals("player_login")) {
            final String updateString = "UPDATE server_stats set players_online = players_online + 1, last_login = FROM_UNIXTIME(" + timestamp + "), logins_since_restart = logins_since_restart + 1";
            AccountDatabase.queries.executeUpdate(updateString);
        }
        else if (event.equals("player_logout")) {
            final String updateString = "UPDATE server_stats set players_online = players_online - 1";
            AccountDatabase.queries.executeUpdate(updateString);
        }
        else if (event.equals("restart")) {
            final String updateString = "UPDATE server_stats set last_restart = FROM_UNIXTIME(" + timestamp + ")" + ", players_online = 0, logins_since_restart = 0";
            AccountDatabase.queries.executeUpdate(updateString);
        }
    }
}
