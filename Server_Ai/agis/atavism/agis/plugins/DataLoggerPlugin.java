// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.msgsys.ResponseMessage;
import atavism.server.plugins.WorldManagerClient;
import java.util.LinkedList;
import atavism.server.math.Point;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.msgsys.Message;
import java.io.Serializable;
import java.net.URLEncoder;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import atavism.server.engine.Hook;
import atavism.server.util.AORuntimeException;
import atavism.server.messages.LogoutMessage;
import atavism.server.messages.LoginMessage;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import java.io.File;
import atavism.agis.objects.Achievement;
import java.util.HashMap;
import atavism.agis.database.AccountDatabase;
import atavism.agis.database.AdminQueries;
import java.io.FileWriter;
import atavism.server.engine.EnginePlugin;

public class DataLoggerPlugin extends EnginePlugin
{
    public static final String PLUGIN_NAME = "DataLogger";
    protected static final String LOGS_DIR;
    protected static FileWriter out;
    protected static AdminQueries logQueries;
    protected static AccountDatabase accDB;
    protected HashMap<Integer, Achievement> achievements;
    
    static {
        LOGS_DIR = System.getProperty("atavism.logs");
        DataLoggerPlugin.out = null;
        DataLoggerPlugin.logQueries = new AdminQueries();
        DataLoggerPlugin.accDB = new AccountDatabase();
    }
    
    public String getName() {
        return "DataLogger";
    }
    
    public DataLoggerPlugin() {
        super("DataLogger");
        this.achievements = new HashMap<Integer, Achievement>();
        this.setPluginType("DataLogger");
        if (DataLoggerPlugin.out == null) {
            try {
                DataLoggerPlugin.out = new FileWriter(new File(String.valueOf(DataLoggerPlugin.LOGS_DIR) + "/datalogger_db_fallback.out"));
            }
            catch (Exception ex) {}
        }
    }
    
    public void onActivate() {
        try {
            this.registerHooks();
            final MessageTypeFilter filter = new MessageTypeFilter();
            filter.addType(DataLoggerClient.MSG_TYPE_EVENT_LOG);
            filter.addType(DataLoggerClient.MSG_TYPE_DATA_LOG);
            filter.addType(DataLoggerClient.MSG_TYPE_CHARACTER_CREATED);
            filter.addType(DataLoggerClient.MSG_TYPE_CHARACTER_DELETED);
            Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
            final MessageTypeFilter filter2 = new MessageTypeFilter();
            filter2.addType(LoginMessage.MSG_TYPE_LOGIN);
            filter2.addType(LogoutMessage.MSG_TYPE_LOGOUT);
            Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this, 8);
            DataLoggerPlugin.accDB.updateServerStat("restart");
        }
        catch (Exception e) {
            throw new AORuntimeException("onActivate failed", (Throwable)e);
        }
    }
    
    public void registerHooks() {
        this.getHookManager().addHook(DataLoggerClient.MSG_TYPE_DATA_LOG, (Hook)new DataLogMessageHook());
        this.getHookManager().addHook(DataLoggerClient.MSG_TYPE_EVENT_LOG, (Hook)new LogEventHook());
        this.getHookManager().addHook(DataLoggerClient.MSG_TYPE_CHARACTER_CREATED, (Hook)new CharacterCreatedHook());
        this.getHookManager().addHook(DataLoggerClient.MSG_TYPE_CHARACTER_DELETED, (Hook)new CharacterDeletedHook());
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook)new LoginHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook)new LogoutHook());
    }
    
    protected void writeData(final String worldName, final String dataName, final long timestamp, final OID sourceOid, final OID targetOid, final OID accountId, final String data) {
        final String tableName = "data_logs";
        final String columnNames = "world_name,data_name,data_timestamp,source_oid,target_oid,account_id,additional_data";
        final String values = "'" + worldName + "','" + dataName + "',FROM_UNIXTIME(" + timestamp + ")," + ((sourceOid == null) ? 0L : sourceOid.toLong()) + "," + ((targetOid == null) ? 0L : targetOid.toLong()) + "," + ((accountId == null) ? 0L : accountId.toLong()) + "," + ((data.length() > 0) ? ("'" + data + "'") : "NULL");
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        if (DataLoggerPlugin.logQueries.executeUpdate(insertString) <= 0) {
            final String logString = String.valueOf(worldName) + "," + this.encode(dataName) + "," + timestamp + "," + ((sourceOid == null) ? 0L : sourceOid.toLong()) + "," + ((targetOid == null) ? 0L : targetOid.toLong()) + "," + ((accountId == null) ? 0L : accountId.toLong()) + "," + ((data.length() > 0) ? data : "") + "," + System.currentTimeMillis();
            try {
                DataLoggerPlugin.out.write(String.valueOf(logString) + "\n");
                DataLoggerPlugin.out.flush();
            }
            catch (Exception e) {
                Log.exception("Exception: trying to write '" + logString + "'", e);
            }
        }
        final String logString = String.valueOf(worldName) + "," + this.encode(dataName) + "," + timestamp + "," + ((sourceOid == null) ? 0L : sourceOid.toLong()) + "," + ((targetOid == null) ? 0L : targetOid.toLong()) + "," + ((accountId == null) ? 0L : accountId.toLong()) + "," + ((data.length() > 0) ? data : "") + "," + System.currentTimeMillis();
        try {
            DataLoggerPlugin.out.write(String.valueOf(logString) + "\n");
            DataLoggerPlugin.out.flush();
        }
        catch (Exception e) {
            Log.exception("Exception: trying to write '" + logString + "'", e);
        }
    }
    
    public String encode(final String inStr) {
        String encStr = new String();
        try {
            encStr = URLEncoder.encode(inStr, "UTF-8");
        }
        catch (Exception ex) {}
        return encStr;
    }
    
    public class DataLogMessageHook implements Hook
    {
        protected String worldName;
        protected String dataName;
        protected long timestamp;
        protected OID sourceOid;
        protected OID targetOid;
        protected OID accountId;
        protected HashMap<String, Serializable> map;
        protected String data;
        
        public DataLogMessageHook() {
            this.worldName = null;
            this.dataName = null;
            this.timestamp = 0L;
            this.sourceOid = null;
            this.targetOid = null;
            this.accountId = null;
            this.map = null;
            this.data = null;
        }
        
        public boolean processMessage(final Message m, final int flags) {
            this.processData(m);
            DataLoggerPlugin.this.writeData(this.worldName, this.dataName, this.timestamp, this.sourceOid, this.targetOid, this.accountId, this.data);
            return true;
        }
        
        protected void processData(final Message m) {
            final DataLoggerClient.DataLogMessage msg = (DataLoggerClient.DataLogMessage)m;
            this.worldName = msg.getWorldName();
            this.dataName = msg.getDataName();
            this.timestamp = msg.getTimestamp();
            this.sourceOid = msg.getSourceOid();
            this.targetOid = msg.getTargetOid();
            this.accountId = msg.getAccountId();
            this.map = msg.getMap();
            this.data = new String();
            if (this.map == null) {
                return;
            }
            if (this.dataName.equals("ITEM_LOOTED_EVENT") && this.map.containsKey("item")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("item"));
            }
            if (this.dataName.equals("ITEM_LOOTED_FAILED_EVENT") && this.map.containsKey("item")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("item"));
            }
            if (this.dataName.equals("NEW_OBJECT_EVENT") && this.map.containsKey("name") && this.map.containsKey("type")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("name"));
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("type"));
            }
            if (this.dataName.equals("CURRENCY_CHANGED_EVENT") && this.map.containsKey("name") && this.map.containsKey("reason")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("name"));
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("reason"));
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("value"));
            }
            if (this.dataName.equals("CHAT_MESSAGE_EVENT") && this.map.containsKey("chat")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("chat"));
            }
            if (this.dataName.equals("KEYBOARD_EVENT") && this.map.containsKey("keycode")) {
                this.data = String.valueOf(this.data) + this.map.get("keycode");
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + ((this.map.containsKey("shift") && this.map.get("shift")) ? "true" : "false");
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + ((this.map.containsKey("control") && this.map.get("control")) ? "true" : "false");
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + ((this.map.containsKey("alt") && this.map.get("alt")) ? "true" : "false");
            }
            if (this.dataName.equals("GESTURE_EVENT") && this.map.containsKey("name")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("name"));
            }
            if (this.dataName.equals("ABILITY_TRIGGERED_EVENT") && this.map.containsKey("name")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("name"));
            }
            if (this.dataName.equals("LOCATION_EVENT")) {
                final AOVector dir = (AOVector)this.map.get("dir");
                final Quaternion quat = (Quaternion)this.map.get("orient");
                final AOVector axis = new AOVector();
                double degrees = 0.0;
                if (quat != null) {
                    degrees = quat.getAngleAxisDegrees(axis);
                }
                else {
                    degrees = 180.0;
                }
                if (axis.getY() > 0.0f) {
                    degrees = 360.0 - degrees;
                }
                degrees += 90.0;
                if (degrees > 360.0) {
                    degrees -= 360.0;
                }
                this.data = String.valueOf(this.data) + this.map.get("loc");
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + this.map.get("orient");
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + String.format("%.2f", degrees);
                this.data = String.valueOf(this.data) + ",";
                if (this.map.get("camera_loc") != null) {
                    this.data = String.valueOf(this.data) + this.map.get("camera_loc");
                }
                else {
                    this.data = String.valueOf(this.data) + "()";
                }
                this.data = String.valueOf(this.data) + ",";
                if (this.map.get("camera_orient") != null) {
                    this.data = String.valueOf(this.data) + this.map.get("camera_orient");
                }
                else {
                    this.data = String.valueOf(this.data) + "()";
                }
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + this.map.get("list_10m");
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + this.map.get("list_30m");
            }
            if (this.dataName.equals("SYSTEM_INFO_EVENT:") && this.map.containsKey("memory") && this.map.containsKey("os")) {
                this.data = String.valueOf(this.data) + this.map.get("memory");
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("os"));
            }
            if (this.dataName.equals("CHARACTER_DELETED") && this.map.containsKey("aspect")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("aspect"));
            }
            if (this.dataName.equals("CHARACTER_CREATED") && this.map.containsKey("aspect")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("aspect"));
            }
            if (this.dataName.equals("PLAYER_LOGGED_IN_EVENT") && this.map.containsKey("ipaddress")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("ipaddress"));
            }
            if (this.dataName.equals("USER_BANNED")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("hours"));
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(", " + this.map.get("banExpires"));
            }
            if (this.dataName.equals("PLAYER_JOINED_ARENA_QUEUE")) {
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(this.map.get("arenaTypes"));
            }
            if (this.dataName.equals("PLAYER_LEFT_ARENA_QUEUE")) {
                final int arenaType = this.map.get("arenaType");
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(new StringBuilder().append(arenaType).toString());
            }
            if (this.dataName.equals("PLAYER_JOINED_ARENA")) {
                final int arenaType = this.map.get("arenaType");
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(new StringBuilder().append(arenaType).toString());
            }
            if (this.dataName.equals("PLAYER_LEFT_ARENA")) {
                final int arenaID = this.map.get("arenaID");
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(new StringBuilder().append(arenaID).toString());
            }
            if (this.dataName.equals("ARENA_STARTED")) {
                final String arenaName = this.map.get("arenaName");
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(arenaName);
            }
            if (this.dataName.equals("ARENA_RESULT")) {
                final String arenaName = this.map.get("arenaName");
                final int winningTeam = this.map.get("winningTeam");
                final int numTeams = this.map.get("numTeams");
                final int timeLeft = this.map.get("timeLeft");
                this.data = String.valueOf(this.data) + DataLoggerPlugin.this.encode(arenaName);
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + winningTeam;
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + numTeams;
                this.data = String.valueOf(this.data) + ",";
                this.data = String.valueOf(this.data) + timeLeft;
            }
        }
    }
    
    public class LogEventHook implements Hook
    {
        public boolean processMessage(final Message m, final int flags) {
            return true;
        }
    }
    
    class CharacterCreatedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final DataLoggerClient.CharacterCreatedMessage getMsg = (DataLoggerClient.CharacterCreatedMessage)msg;
            final OID characterOID = getMsg.getSourceOid();
            final OID accountID = getMsg.getAccountId();
            final String characterName = getMsg.getCharacterName();
            final String accountName = getMsg.getAccountName();
            DataLoggerPlugin.accDB.characterCreated(accountID, accountName, characterOID, characterName);
            return true;
        }
    }
    
    class CharacterDeletedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final DataLoggerClient.CharacterDeletedMessage getMsg = (DataLoggerClient.CharacterDeletedMessage)msg;
            final OID characterOID = getMsg.getSourceOid();
            final OID accountID = getMsg.getAccountId();
            final String characterName = getMsg.getCharacterName();
            DataLoggerPlugin.accDB.characterDeleted(accountID, characterOID, characterName);
            return true;
        }
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            final OID accountID = (OID)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "accountId");
            final int accountStatus = DataLoggerPlugin.accDB.getAccountStatus(accountID);
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "adminLevel", (Serializable)accountStatus);
            Log.debug("ACCOUNT: set account status for: " + playerOid + " to: " + accountStatus);
            DataLoggerPlugin.accDB.characterLoggedIn(accountID);
            DataLoggerPlugin.accDB.updateServerStat("player_login");
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            return true;
        }
    }
    
    class LogoutHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage message = (LogoutMessage)msg;
            Log.debug("LOGOUT: datalogger logout started");
            final OID playerOid = message.getSubject();
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            DataLoggerClient.logData("PLAYER_LOGGED_OUT_EVENT", playerOid, null, null, null);
            DataLoggerPlugin.accDB.updateServerStat("player_logout");
            Log.debug("LOGOUT: datalogger logout finished");
            return true;
        }
    }
}
