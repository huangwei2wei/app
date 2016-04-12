// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.messages.PropertyMessage;
import java.io.Serializable;
import java.util.HashMap;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;

public class DataLoggerClient
{
    public static Namespace NAMESPACE;
    public static String WORLDNAME;
    public static final MessageType MSG_TYPE_DATA_LOG;
    public static final MessageType MSG_TYPE_EVENT_LOG;
    public static final MessageType MSG_TYPE_CHARACTER_CREATED;
    public static final MessageType MSG_TYPE_CHARACTER_DELETED;
    
    static {
        DataLoggerClient.NAMESPACE = null;
        DataLoggerClient.WORLDNAME = null;
        MSG_TYPE_DATA_LOG = MessageType.intern("ao.DATA_LOG");
        MSG_TYPE_EVENT_LOG = MessageType.intern("ao.EVENT_LOG");
        MSG_TYPE_CHARACTER_CREATED = MessageType.intern("ao.CHARACTER_CREATED");
        MSG_TYPE_CHARACTER_DELETED = MessageType.intern("ao.CHARACTER_DELETED");
    }
    
    public static void logEvent(final int eventID, final OID sourceOid, final OID targetOid, final int eventData, final int eventCount) {
        final LogEventMessage elogMsg = new LogEventMessage(eventID, sourceOid, targetOid, eventData, eventCount);
        Engine.getAgent().sendBroadcast((Message)elogMsg);
    }
    
    public static void logData(final String dataName, final OID sourceOid, final OID targetOid, final OID accountId, final HashMap<String, Serializable> map) {
        final DataLogMessage elogMsg = new DataLogMessage(DataLoggerClient.WORLDNAME, dataName, System.currentTimeMillis(), sourceOid, targetOid, accountId, map);
        Engine.getAgent().sendBroadcast((Message)elogMsg);
    }
    
    public static void characterCreated(final OID accountId, final String accountName, final OID sourceOid, final String characterName) {
        final CharacterCreatedMessage elogMsg = new CharacterCreatedMessage(accountId, accountName, sourceOid, characterName);
        Engine.getAgent().sendBroadcast((Message)elogMsg);
    }
    
    public static void characterDeleted(final OID accountId, final OID sourceOid, final String characterName) {
        final CharacterDeletedMessage elogMsg = new CharacterDeletedMessage(accountId, sourceOid, characterName);
        Engine.getAgent().sendBroadcast((Message)elogMsg);
    }
    
    public static class LogEventMessage extends PropertyMessage
    {
        protected int eventID;
        protected OID sourceOid;
        protected OID targetOid;
        protected int eventData;
        protected int eventCount;
        private static final long serialVersionUID = 1L;
        
        public LogEventMessage() {
            this.eventID = -1;
            this.sourceOid = null;
            this.targetOid = null;
            this.eventData = -1;
            this.eventCount = -1;
            this.setMsgType(DataLoggerClient.MSG_TYPE_EVENT_LOG);
        }
        
        public LogEventMessage(final int eventID, final OID sourceOid, final OID targetOid, final int eventData, final int eventCount) {
            this.eventID = -1;
            this.sourceOid = null;
            this.targetOid = null;
            this.eventData = -1;
            this.eventCount = -1;
            this.setMsgType(DataLoggerClient.MSG_TYPE_EVENT_LOG);
            this.setEventID(eventID);
            this.setSourceOid(sourceOid);
            this.setTargetOid(targetOid);
            this.setEventData(eventData);
            this.setEventCount(eventCount);
        }
        
        public void setEventID(final int eventID) {
            this.eventID = eventID;
        }
        
        public int getEventID() {
            return this.eventID;
        }
        
        public void setSourceOid(final OID sourceOid) {
            this.sourceOid = sourceOid;
        }
        
        public OID getSourceOid() {
            return this.sourceOid;
        }
        
        public void setTargetOid(final OID targetOid) {
            this.targetOid = targetOid;
        }
        
        public OID getTargetOid() {
            return this.targetOid;
        }
        
        public void setEventData(final int eventData) {
            this.eventData = eventData;
        }
        
        public int getEventData() {
            return this.eventData;
        }
        
        public void setEventCount(final int eventCount) {
            this.eventCount = eventCount;
        }
        
        public int getEventCount() {
            return this.eventCount;
        }
    }
    
    public static class DataLogMessage extends PropertyMessage
    {
        protected String worldName;
        protected String dataName;
        protected long timestamp;
        protected OID sourceOid;
        protected OID targetOid;
        protected OID accountId;
        protected HashMap<String, Serializable> map;
        private static final long serialVersionUID = 1L;
        
        public DataLogMessage() {
            this.worldName = null;
            this.dataName = null;
            this.timestamp = 0L;
            this.sourceOid = null;
            this.targetOid = null;
            this.accountId = null;
            this.map = null;
            this.setMsgType(DataLoggerClient.MSG_TYPE_DATA_LOG);
        }
        
        public DataLogMessage(final String worldName, final String dataName, final long timestamp, final OID sourceOid, final OID targetOid, final OID accountId, final HashMap<String, Serializable> map) {
            this.worldName = null;
            this.dataName = null;
            this.timestamp = 0L;
            this.sourceOid = null;
            this.targetOid = null;
            this.accountId = null;
            this.map = null;
            this.setMsgType(DataLoggerClient.MSG_TYPE_DATA_LOG);
            this.setWorldName(worldName);
            this.setDataName(dataName);
            this.setTimestamp(timestamp);
            this.setSourceOid(sourceOid);
            this.setTargetOid(targetOid);
            this.setAccountId(accountId);
            this.setMap(map);
        }
        
        public void setWorldName(final String worldName) {
            this.worldName = worldName;
        }
        
        public String getWorldName() {
            return this.worldName;
        }
        
        public void setDataName(final String dataName) {
            this.dataName = dataName;
        }
        
        public String getDataName() {
            return this.dataName;
        }
        
        public void setTimestamp(final long timestamp) {
            this.timestamp = timestamp;
        }
        
        public long getTimestamp() {
            return this.timestamp;
        }
        
        public void setSourceOid(final OID sourceOid) {
            this.sourceOid = sourceOid;
        }
        
        public OID getSourceOid() {
            return this.sourceOid;
        }
        
        public void setTargetOid(final OID targetOid) {
            this.targetOid = targetOid;
        }
        
        public OID getTargetOid() {
            return this.targetOid;
        }
        
        public void setAccountId(final OID accountId) {
            this.accountId = accountId;
        }
        
        public OID getAccountId() {
            return this.accountId;
        }
        
        public void setMap(final HashMap<String, Serializable> map) {
            this.map = map;
        }
        
        public HashMap<String, Serializable> getMap() {
            return this.map;
        }
    }
    
    public static class CharacterCreatedMessage extends PropertyMessage
    {
        protected String characterName;
        protected OID sourceOid;
        protected String accountName;
        protected OID accountId;
        private static final long serialVersionUID = 1L;
        
        public CharacterCreatedMessage() {
            this.characterName = null;
            this.sourceOid = null;
            this.accountName = null;
            this.accountId = null;
            this.setMsgType(DataLoggerClient.MSG_TYPE_CHARACTER_CREATED);
        }
        
        public CharacterCreatedMessage(final OID accountId, final String accountName, final OID sourceOid, final String characterName) {
            this.characterName = null;
            this.sourceOid = null;
            this.accountName = null;
            this.accountId = null;
            this.setMsgType(DataLoggerClient.MSG_TYPE_CHARACTER_CREATED);
            this.setCharacterName(characterName);
            this.setSourceOid(sourceOid);
            this.setAccountName(accountName);
            this.setAccountId(accountId);
        }
        
        public void setCharacterName(final String characterName) {
            this.characterName = characterName;
        }
        
        public String getCharacterName() {
            return this.characterName;
        }
        
        public void setSourceOid(final OID sourceOid) {
            this.sourceOid = sourceOid;
        }
        
        public OID getSourceOid() {
            return this.sourceOid;
        }
        
        public void setAccountName(final String accountName) {
            this.accountName = accountName;
        }
        
        public String getAccountName() {
            return this.accountName;
        }
        
        public void setAccountId(final OID accountId) {
            this.accountId = accountId;
        }
        
        public OID getAccountId() {
            return this.accountId;
        }
    }
    
    public static class CharacterDeletedMessage extends PropertyMessage
    {
        protected String characterName;
        protected OID sourceOid;
        protected OID accountId;
        private static final long serialVersionUID = 1L;
        
        public CharacterDeletedMessage() {
            this.characterName = null;
            this.sourceOid = null;
            this.accountId = null;
            this.setMsgType(DataLoggerClient.MSG_TYPE_CHARACTER_DELETED);
        }
        
        public CharacterDeletedMessage(final OID accountId, final OID sourceOid, final String characterName) {
            this.characterName = null;
            this.sourceOid = null;
            this.accountId = null;
            this.setMsgType(DataLoggerClient.MSG_TYPE_CHARACTER_DELETED);
            this.setCharacterName(characterName);
            this.setSourceOid(sourceOid);
            this.setAccountId(accountId);
        }
        
        public void setCharacterName(final String characterName) {
            this.characterName = characterName;
        }
        
        public String getCharacterName() {
            return this.characterName;
        }
        
        public void setSourceOid(final OID sourceOid) {
            this.sourceOid = sourceOid;
        }
        
        public OID getSourceOid() {
            return this.sourceOid;
        }
        
        public void setAccountId(final OID accountId) {
            this.accountId = accountId;
        }
        
        public OID getAccountId() {
            return this.accountId;
        }
    }
}
