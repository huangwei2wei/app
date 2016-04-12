// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.io.Serializable;
import atavism.server.objects.Entity;
import atavism.agis.objects.Guild;
import atavism.server.engine.OID;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.Message;
import atavism.server.engine.Hook;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.Log;
import java.util.ArrayList;
import atavism.server.engine.EnginePlugin;

public class GuildPlugin extends EnginePlugin
{
    public static String GUILD_PLUGIN_NAME;
    public static int maxRanks;
    public static ArrayList<String> rankNames;
    public static ArrayList<ArrayList<String>> defaultPermissions;
    
    static {
        GuildPlugin.GUILD_PLUGIN_NAME = "GuildPlugin";
        GuildPlugin.maxRanks = 10;
        GuildPlugin.rankNames = new ArrayList<String>();
        GuildPlugin.defaultPermissions = new ArrayList<ArrayList<String>>();
    }
    
    public String getName() {
        return GuildPlugin.GUILD_PLUGIN_NAME;
    }
    
    public void onActivate() {
        Log.debug("GUILD PLUGIN: activated");
        this.registerHooks();
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(GuildClient.MSG_TYPE_CREATE_GUILD);
        filter.addType(GuildClient.MSG_TYPE_GUILD_COMMAND);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(GuildClient.MSG_TYPE_CREATE_GUILD, (Hook)new GuildCreateHook());
        this.getHookManager().addHook(GuildClient.MSG_TYPE_GUILD_COMMAND, (Hook)new GuildCommandHook());
    }
    
    class GuildCreateHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final GuildClient.createGuildMessage gmMsg = (GuildClient.createGuildMessage)msg;
            final OID oid = gmMsg.getSubject();
            String guildName = "";
            try {
                guildName = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "guildName");
            }
            catch (NullPointerException ex) {}
            if (!guildName.equals("")) {
                Log.warn("GUILD PLUGIN: player attempted to create a guild, but is already currently in a guild");
                WorldManagerClient.sendObjChatMsg(oid, 1, "You cannot create a guild while you are in a guild");
                return true;
            }
            guildName = (String)gmMsg.getProperty("guildName");
            boolean exists = true;
            try {
                ObjectManagerClient.loadObjectData("guild_" + guildName);
            }
            catch (NullPointerException e) {
                exists = false;
            }
            if (exists) {
                WorldManagerClient.sendObjChatMsg(oid, 1, "The guild " + guildName + " already exists. Please choose another name.");
                return true;
            }
            final ArrayList<OID> initiates = new ArrayList<OID>();
            final String race = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "race");
            String faction = "";
            if (race.equals("human")) {
                faction = "Order";
            }
            final Guild newGuild = new Guild(guildName, faction, GuildPlugin.rankNames, GuildPlugin.defaultPermissions, oid, initiates);
            final Entity guildEntity = new Entity(guildName);
            guildEntity.setPersistenceFlag(true);
            guildEntity.setName("guild_" + guildName);
            guildEntity.setProperty("guildObject", (Serializable)newGuild);
            ObjectManagerClient.saveObjectData("guild_" + guildName, guildEntity, WorldManagerClient.NAMESPACE);
            return true;
        }
    }
    
    class GuildCommandHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final GuildClient.guildCommandMessage gmMsg = (GuildClient.guildCommandMessage)msg;
            final OID oid = gmMsg.getSubject();
            String guildName = "";
            try {
                guildName = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "guildName");
            }
            catch (NullPointerException e1) {
                Log.warn("GUILD PLUGIN: player attempted guild command, but is not currently in a guild");
            }
            if (guildName.equals("")) {
                WorldManagerClient.sendObjChatMsg(oid, 1, "You are not in a guild.");
                return true;
            }
            final String commandType = (String)gmMsg.getProperty("commandType");
            final Serializable commandData = gmMsg.getProperty("commandData");
            final Serializable commandDataTwo = gmMsg.getProperty("commandDataTwo");
            final Entity guildEntity = ObjectManagerClient.loadObjectData("guild_" + guildName);
            final Guild playersGuild = (Guild)guildEntity.getProperty("guildObject");
            playersGuild.handleCommand(oid, commandType, commandData, commandDataTwo);
            return true;
        }
    }
    
    class GuildInviteResponseHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final GuildClient.guildCommandMessage gmMsg = (GuildClient.guildCommandMessage)msg;
            final OID oid = gmMsg.getSubject();
            final OID inviteeOid = (OID)gmMsg.getProperty("invitee");
            final int response = (int)gmMsg.getProperty("response");
            if (response == 1) {
                final String guildName = (String)EnginePlugin.getObjectProperty(inviteeOid, WorldManagerClient.NAMESPACE, "guildName");
                final Entity guildEntity = ObjectManagerClient.loadObjectData("guild_" + guildName);
                final Guild playersGuild = (Guild)guildEntity.getProperty("guildObject");
                playersGuild.handleCommand(inviteeOid, "acceptInvite", (Serializable)oid, null);
            }
            else {
                final String playerName = WorldManagerClient.getObjectInfo(oid).name;
                WorldManagerClient.sendObjChatMsg(inviteeOid, 1, String.valueOf(playerName) + " has declined your guild invitation.");
            }
            return true;
        }
    }
}
