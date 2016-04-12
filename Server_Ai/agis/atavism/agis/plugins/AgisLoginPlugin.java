// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.HashSet;
import atavism.server.objects.AOObject;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.worldmgr.CharacterFactory;
import atavism.server.util.AORuntimeException;
import atavism.server.objects.DisplayContext;
import atavism.server.objects.Entity;
import java.util.Iterator;
import java.util.List;
import atavism.server.engine.Database;
import atavism.server.util.SecureToken;
import atavism.server.network.AOByteBuffer;
import atavism.agis.objects.CombatInfo;
import atavism.msgsys.Message;
import java.io.Serializable;
import atavism.msgsys.GenericMessage;
import atavism.server.plugins.ProxyPlugin;
import atavism.server.util.SecureTokenManager;
import atavism.server.engine.OID;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import atavism.agis.database.ContentDatabase;
import java.util.HashMap;
import atavism.agis.database.AccountDatabase;
import atavism.agis.objects.CharacterTemplate;
import java.util.Set;
import atavism.server.engine.Namespace;
import java.util.Map;
import atavism.server.worldmgr.LoginPlugin;

public class AgisLoginPlugin extends LoginPlugin
{
    protected static Map<Namespace, Set<String>> characterProps;
    public static Map<String, CharacterTemplate> characterTemplates;
    protected AccountDatabase aDB;
    public static final int ACCOUNT_ADMIN = 5;
    public static final int ACCOUNT_GM = 3;
    public static final int ACCOUNT_NORMAL = 1;
    
    static {
        AgisLoginPlugin.characterProps = new HashMap<Namespace, Set<String>>();
        AgisLoginPlugin.characterTemplates = new HashMap<String, CharacterTemplate>();
    }
    
    public void onActivate() {
        super.onActivate();
        this.loadCharacterFactoryTemplatesFromDatabase();
        this.aDB = new AccountDatabase();
    }
    
    public void loadCharacterFactoryTemplatesFromDatabase() {
        final ContentDatabase cDB = new ContentDatabase(false);
        AgisLoginPlugin.characterTemplates = cDB.loadCharacterFactoryTemplates();
        cDB.close();
    }
    
    protected LoginPlugin.CharacterResponseMessage handleCharacterRequestMessage(final LoginPlugin.CharacterRequestMessage message, final LoginPlugin.SocketHandler clientSocket) {
        final AOByteBuffer authToken = message.getAuthToken();
        final LoginPlugin.CharacterResponseMessage response = new LoginPlugin.CharacterResponseMessage();
        response.setWorldFilesUrl(Engine.getProperty("atavism.world_files_url"));
        OID uid = null;
        String accountName = null;
        final byte version = authToken.getByte();
        authToken.rewind();
        if (version == 7) {
            Log.debug("LOGIN: got version 7");
            authToken.getByte();
            uid = OID.fromLong(authToken.getLong());
            clientSocket.setAccountId(uid);
        }
        else if (clientSocket.getAccountId() == null) {
            final SecureToken token = SecureTokenManager.getInstance().importToken(authToken);
            boolean valid = true;
            Log.debug("SecureToken: " + token);
            if (LoginPlugin.SecureToken) {
                valid = token.getValid();
            }
            if (valid && LoginPlugin.WorldId != null && token.getProperty("world_id").equals(LoginPlugin.WorldId)) {
                valid = false;
            }
            if ((valid && token.getIssuerId() == null) || !token.getIssuerId().equals("master")) {
                valid = false;
            }
            if (!valid) {
                response.setErrorMessage("invalid master token");
                return response;
            }
            final Serializable uidObj = token.getProperty("account_id");
            if (uidObj instanceof Integer) {
                uid = OID.fromLong((long)uidObj);
            }
            if (uidObj instanceof Long) {
                uid = OID.fromLong((long)uidObj);
            }
            if (uidObj instanceof String) {
                uid = OID.fromLong(Long.parseLong((String)uidObj));
            }
            accountName = (String)token.getProperty("account_name");
            clientSocket.setAccountId(uid);
            clientSocket.setAccountName(accountName);
        }
        else {
            uid = clientSocket.getAccountId();
            accountName = clientSocket.getAccountName();
        }
        response.setAccount(uid);
        Log.debug("About to send account login with accountId=" + uid);
        final GenericMessage accountLoginMessage = new GenericMessage(ProxyPlugin.MSG_TYPE_ACCOUNT_LOGIN);
        accountLoginMessage.setProperty("accountId", (Serializable)uid);
        Engine.getAgent().sendBroadcast((Message)accountLoginMessage);
        final Database db = Engine.getDatabase();
        final String worldName = Engine.getWorldName();
        final List<OID> charIds = (List<OID>)db.getGameIDs(worldName, uid);
        int characterCount = 0;
        String characterNames = "";
        for (final OID oid : charIds) {
            if (Log.loggingDebug) {
                Log.debug("AgisLoginPlugin: character oid: " + oid);
            }
            final Entity entity = Engine.getDatabase().loadEntity(oid, Namespace.WORLD_MANAGER);
            final CombatInfo entity2 = (CombatInfo)Engine.getDatabase().loadEntity(oid, Namespace.COMBAT);
            if (Log.loggingDebug) {
                Log.debug("AgisLoginPlugin: loaded character from db: " + entity);
            }
            final Map<String, Serializable> charInfo = new HashMap<String, Serializable>();
            charInfo.put("characterId", (Serializable)entity.getOid());
            charInfo.put("characterName", entity.getName());
            Log.debug("LOGIN: props: " + entity.getPropertyMap());
            final String race = entity.getStringProperty("race");
            final String gender = entity.getStringProperty("gender");
            final String model = entity.getStringProperty("model");
            final String world = entity.getStringProperty("world");
            final String zone = entity.getStringProperty("zone");
            if (this.aDB.getIslandAdministrator(world).equals((Object)uid)) {
                charInfo.put("worldAdmin", true);
            }
            else {
                charInfo.put("worldAdmin", false);
            }
            for (final String prop : entity.getPropertyMap().keySet()) {
                if (entity.getProperty(prop) instanceof String || entity.getProperty(prop) instanceof Integer || entity.getProperty(prop) instanceof Float) {
                    charInfo.put(prop, entity.getProperty(prop));
                }
                else {
                    if (!(entity.getProperty(prop) instanceof HashMap)) {
                        continue;
                    }
                    final HashMap<Object, Serializable> mapProps = (HashMap<Object, Serializable>)entity.getProperty(prop);
                    Log.debug("PROPS: mapProps: " + mapProps);
                    for (final Object key : mapProps.keySet()) {
                        if (!(key instanceof String)) {
                            continue;
                        }
                        final String sKey = (String)key;
                        if (mapProps.get(sKey) instanceof Double) {
                            final double val = mapProps.get(sKey);
                            charInfo.put("custom:" + prop + ":" + sKey, (float)val);
                            Log.debug("CV: converted double " + sKey + " to float");
                        }
                        else {
                            charInfo.put("custom:" + prop + ":" + sKey, mapProps.get(sKey));
                        }
                    }
                }
            }
            for (final String prop : entity2.getPropertyMap().keySet()) {
                if (entity2.getProperty(prop) instanceof String || entity2.getProperty(prop) instanceof Integer || entity2.getProperty(prop) instanceof Float) {
                    charInfo.put(prop, entity2.getProperty(prop));
                }
            }
            charInfo.put("level", entity2.statGetCurrentValue("level"));
            charInfo.put("accountId", (Serializable)uid);
            characterNames = String.valueOf(characterNames) + entity.getName() + "(" + entity.getOid() + "),";
            ++characterCount;
            final DisplayContext displayContext = this.getDisplayContext(entity);
            if (displayContext != null) {
                charInfo.put("displayContext", this.marshallDisplayContext(displayContext));
            }
            this.setCharacterProperties(charInfo, entity);
            response.addCharacter((Map)charInfo);
        }
        response.setCharacterSlots(this.aDB.getNumCharacterSlots(uid));
        Log.info("LoginPlugin: GET_CHARACTERS remote=" + clientSocket.getRemoteSocketAddress() + " account=" + uid + " accountName=" + clientSocket.getAccountName() + " count=" + characterCount + " names=" + characterNames);
        clientSocket.setCharacterInfo(response.getCharacters());
        return response;
    }
    
    protected LoginPlugin.CharacterDeleteResponseMessage handleCharacterDeleteMessage(final LoginPlugin.CharacterDeleteMessage message, final LoginPlugin.SocketHandler clientSocket) {
        final LoginPlugin.CharacterDeleteResponseMessage response = new LoginPlugin.CharacterDeleteResponseMessage();
        final Map<String, Serializable> props = (Map<String, Serializable>)message.getProperties();
        final Map<String, Serializable> errorProps = new HashMap<String, Serializable>();
        if (Log.loggingDebug) {
            Log.debug("AgisLoginPlugin: delete character properties: ");
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                Log.debug("character property " + entry.getKey() + "=" + entry.getValue());
            }
        }
        errorProps.put("status", Boolean.FALSE);
        response.setProperties((Map)errorProps);
        if (clientSocket.getAccountId() == null) {
            errorProps.put("errorMessage", "Permission denied");
            return response;
        }
        final OID uid = clientSocket.getAccountId();
        final OID oid = (OID)props.get("characterId");
        final Database db = Engine.getDatabase();
        List<OID> characterOids = null;
        try {
            characterOids = (List<OID>)db.getGameIDs(Engine.getWorldName(), uid);
        }
        catch (AORuntimeException ex) {
            errorProps.put("errorMessage", ex.toString());
            return response;
        }
        if (!characterOids.contains(oid)) {
            errorProps.put("errorMessage", "Character does not exist.");
            return response;
        }
        final CharacterFactory factory = getCharacterGenerator().getCharacterFactory();
        if (factory == null) {
            Log.error("AgisLoginPlugin: missing character factory");
            errorProps.put("errorMessage", "Missing character factory.");
            return response;
        }
        String errorMessage;
        try {
            errorMessage = factory.deleteCharacter(Engine.getWorldName(), uid, oid, (Map)props);
        }
        catch (Exception ex2) {
            Log.exception("Exception deleting character", ex2);
            errorProps.put("errorMessage", ex2.toString());
            return response;
        }
        if (errorMessage != null) {
            errorProps.put("errorMessage", errorMessage);
            return response;
        }
        final String characterName = db.getObjectName(oid, Namespace.OBJECT_MANAGER);
        try {
            db.deletePlayerCharacter(oid);
        }
        catch (Exception ex3) {
            errorProps.put("errorMessage", ex3.toString());
            return response;
        }
        try {
            db.deleteObjectData(oid);
        }
        catch (Exception ex4) {}
        Log.info("LoginPlugin: CHARACTER_DELETE remote=" + clientSocket.getRemoteSocketAddress() + " account=" + uid + " accountName=" + clientSocket.getAccountName() + " oid=" + oid + " name=" + characterName);
        final OID accountID = uid;
        final HashMap<String, Serializable> logData = new HashMap<String, Serializable>();
        logData.put("aspect", props.get("aspect"));
        DataLoggerClient.logData("CHARACTER_DELETED", oid, null, accountID, logData);
        DataLoggerClient.characterDeleted(accountID, oid, characterName);
        props.put("status", Boolean.TRUE);
        response.setProperties((Map)props);
        return response;
    }
    
    protected LoginPlugin.CharacterCreateResponseMessage handleCharacterCreateMessage(final LoginPlugin.CharacterCreateMessage message, final LoginPlugin.SocketHandler clientSocket) {
        final LoginPlugin.CharacterCreateResponseMessage response = new LoginPlugin.CharacterCreateResponseMessage();
        final Map<String, Serializable> props = (Map<String, Serializable>)message.getProperties();
        if (clientSocket.getAccountId() == null) {
            props.clear();
            props.put("status", Boolean.FALSE);
            props.put("errorMessage", "Permission denied");
            response.setProperties((Map)props);
            return response;
        }
        final OID uid = clientSocket.getAccountId();
        String propertyText = "";
        for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
            propertyText = String.valueOf(propertyText) + "[" + entry.getKey() + "=" + entry.getValue() + "] ";
        }
        props.put("accountName", clientSocket.getAccountName());
        Log.info("LoginPlugin: CHARACTER_CREATE remote=" + clientSocket.getRemoteSocketAddress() + " account=" + uid + " accountName=" + clientSocket.getAccountName() + " properties=" + propertyText);
        final Database db = Engine.getDatabase();
        final String worldName = Engine.getWorldName();
        final String race = props.get("race");
        final String aspect = props.get("aspect");
        if (AgisLoginPlugin.characterTemplates.containsKey(String.valueOf(race) + aspect)) {
            if (Log.loggingDebug) {
                Log.debug("AgisLoginPlugin: creating character");
            }
            OID oid = null;
            try {
                final String name = props.get("characterName");
                final String nameCheckResult = this.isNameTaken(name);
                if (!nameCheckResult.equals("")) {
                    props.put("errorMessage", nameCheckResult);
                }
                else {
                    final CharacterTemplate factory = AgisLoginPlugin.characterTemplates.get(String.valueOf(race) + aspect);
                    oid = factory.createCharacter(worldName, uid, props);
                }
            }
            catch (Exception e) {
                Log.exception("Caught exception in character factory: ", e);
                props.clear();
                props.put("errorMessage", "Internal error");
            }
            if (oid == null) {
                Log.error("Character factory returned null OID");
                if (props.get("errorMessage") == null) {
                    props.put("errorMessage", "Internal error");
                }
            }
            if (props.get("errorMessage") != null) {
                Log.error("AgisLoginPlugin: character creation failed, account=" + uid + " errorMessage=" + props.get("errorMessage") + " characterName=" + props.get("characterName"));
                props.put("status", Boolean.FALSE);
                response.setProperties((Map)props);
                return response;
            }
            if (Log.loggingDebug) {
                Log.debug("AgisLoginPlugin: saving oid " + oid);
            }
            boolean success = false;
            if (oid != null) {
                success = ObjectManagerClient.saveObject(oid);
            }
            if (success) {
                if (Log.loggingDebug) {
                    Log.debug("AgisLoginPlugin: saved oid " + oid);
                }
                db.mapAtavismID(worldName, uid, oid);
                props.put("status", Boolean.TRUE);
                props.put("characterId", (Serializable)oid);
                final Entity entity = Engine.getDatabase().loadEntity(oid, Namespace.WORLD_MANAGER);
                props.put("characterName", entity.getName());
                props.put("world", entity.getStringProperty("world"));
                props.put("model", entity.getStringProperty("model"));
                if (this.aDB.getIslandAdministrator(entity.getStringProperty("world")).equals((Object)uid)) {
                    props.put("worldAdmin", true);
                }
                else {
                    props.put("worldAdmin", false);
                }
                final DisplayContext displayContext = this.getDisplayContext(entity);
                if (displayContext != null) {
                    props.put("displayContext", this.marshallDisplayContext(displayContext));
                }
                this.setCharacterProperties(props, entity);
                clientSocket.getCharacterInfo().add(props);
                Log.info("LoginPlugin: CHARACTER_CREATE remote=" + clientSocket.getRemoteSocketAddress() + " account=" + uid + " accountName=" + clientSocket.getAccountName() + " oid=" + oid + " name=" + entity.getName());
                final OID accountID = uid;
                final HashMap<String, Serializable> logData = new HashMap<String, Serializable>();
                logData.put("aspect", props.get("aspect"));
                DataLoggerClient.logData("CHARACTER_CREATED", oid, null, accountID, logData);
                final String characterName = WorldManagerClient.getObjectInfo(oid).name;
                DataLoggerClient.characterCreated(accountID, clientSocket.getAccountName(), oid, characterName);
            }
            else {
                Log.error("AgisLoginPlugin: failed to save oid " + oid);
                props.clear();
                props.put("status", Boolean.FALSE);
                props.put("errorMessage", "Failed to save new character");
            }
        }
        else {
            Log.error("AgisLoginPlugin: missing character factory");
            props.clear();
            props.put("status", Boolean.FALSE);
            props.put("errorMessage", "Could not find Character Template for Race & Aspect specified");
        }
        response.setProperties((Map)props);
        return response;
    }
    
    public String isNameTaken(final String name) {
        final Database db = Engine.getDatabase();
        final boolean taken = db.characterNameTaken(name);
        if (taken) {
            return "That name is already in use.  Please pick another.";
        }
        return "";
    }
    
    protected DisplayContext getDisplayContext(final Entity entity) {
        DisplayContext displayContext = null;
        final AOObject aoObject = (AOObject)Engine.getDatabase().loadEntity(entity.getOid(), Namespace.WORLD_MANAGER);
        displayContext = aoObject.displayContext();
        if (displayContext != null && Log.loggingDebug) {
            Log.debug("Display context for '" + entity.getName() + "': " + displayContext);
        }
        return displayContext;
    }
    
    protected String marshallDisplayContext(final DisplayContext displayContext) {
        String result = displayContext.getMeshFile();
        for (final DisplayContext.Submesh submesh : displayContext.getSubmeshes()) {
            result = String.valueOf(result) + "\u0002" + submesh.getName() + "\u0002" + submesh.getMaterial();
        }
        final Map<String, DisplayContext> childDCs = (Map<String, DisplayContext>)displayContext.getChildDCMap();
        for (final Map.Entry<String, DisplayContext> entry : childDCs.entrySet()) {
            final DisplayContext childDC = entry.getValue();
            result = String.valueOf(result) + "\u0001" + entry.getKey() + "\u0002" + childDC.getMeshFile();
            for (final DisplayContext.Submesh submesh2 : childDC.getSubmeshes()) {
                result = String.valueOf(result) + "\u0002" + submesh2.getName() + "\u0002" + submesh2.getMaterial();
            }
        }
        return result;
    }
    
    protected void setCharacterProperties(final Map<String, Serializable> props, final Entity entity) {
        for (final Map.Entry<Namespace, Set<String>> entry : AgisLoginPlugin.characterProps.entrySet()) {
            final Namespace namespace = entry.getKey();
            final Entity subObj = Engine.getDatabase().loadEntity(entity.getOid(), namespace);
            for (final String propName : entry.getValue()) {
                final Serializable propValue = subObj.getProperty(propName);
                if (propValue != null) {
                    props.put(propName, propValue);
                }
            }
        }
    }
    
    public static void registerCharacterProperty(final Namespace namespace, final String propName) {
        Set<String> propSet = AgisLoginPlugin.characterProps.get(namespace);
        if (propSet == null) {
            propSet = new HashSet<String>();
            AgisLoginPlugin.characterProps.put(namespace, propSet);
        }
        propSet.add(propName);
    }
}
