// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.LinkedList;
import atavism.msgsys.ResponseMessage;
import atavism.server.objects.Entity;
import atavism.server.plugins.ObjectManagerClient;
import java.util.List;
import java.util.Collection;
import atavism.server.objects.Player;
import java.util.Set;
import java.util.Iterator;
import atavism.msgsys.Message;
import java.util.Map;
import java.io.Serializable;
import java.util.Calendar;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.server.events.AuthorizedLoginEvent;
import atavism.server.network.ClientConnection;
import atavism.server.engine.Hook;
import atavism.server.util.AORuntimeException;
import atavism.server.messages.LogoutMessage;
import atavism.server.messages.LoginMessage;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.agis.database.AccountDatabase;
import java.util.ArrayList;
import atavism.server.engine.OID;
import java.util.HashMap;
import atavism.server.plugins.ProxyPlugin;

public class AgisProxyPlugin extends ProxyPlugin
{
    public static HashMap<OID, Long> bannedUsers;
    public static HashMap<OID, ArrayList<OID>> friendReverseMap;
    public static HashMap<String, ArrayList<OID>> chatChannelSubscribers;
    AccountDatabase aDB;
    
    static {
        AgisProxyPlugin.bannedUsers = new HashMap<OID, Long>();
        AgisProxyPlugin.friendReverseMap = new HashMap<OID, ArrayList<OID>>();
        AgisProxyPlugin.chatChannelSubscribers = new HashMap<String, ArrayList<OID>>();
    }
    
    public void onActivate() {
        super.onActivate();
        try {
            AgisProxyPlugin.log.debug("AgisProxyPlugin.onActivate()");
            this.registerProxyHooks();
            final MessageTypeFilter filter = new MessageTypeFilter();
            filter.addType(GroupClient.MSG_TYPE_GET_FRIENDS);
            filter.addType(GroupClient.MSG_TYPE_ADD_FRIEND);
            Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
            final MessageTypeFilter responderFilter = new MessageTypeFilter();
            responderFilter.addType(GroupClient.MSG_TYPE_GET_PLAYER_BY_NAME);
            responderFilter.addType(LoginMessage.MSG_TYPE_LOGIN);
            responderFilter.addType(LogoutMessage.MSG_TYPE_LOGOUT);
            Engine.getAgent().createSubscription((IFilter)responderFilter, (MessageCallback)this, 8);
        }
        catch (Exception e) {
            throw new AORuntimeException("onActivate failed", (Throwable)e);
        }
        this.aDB = new AccountDatabase();
    }
    
    protected void registerProxyHooks() {
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GET_PLAYER_BY_NAME, (Hook)new GetPlayerOidFromNameHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GET_FRIENDS, (Hook)new GetFriendsHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_ADD_FRIEND, (Hook)new AddFriendHook());
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook)new LoginHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook)new LogoutHook());
    }
    
    protected boolean processLogin(final ClientConnection con, final AuthorizedLoginEvent loginEvent) {
        if (!super.processLogin(con, loginEvent)) {
            return false;
        }
        final OID oid = loginEvent.getOid();
        Log.debug("LOGIN: login oid: " + oid);
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        if (AgisProxyPlugin.bannedUsers.containsValue(accountID)) {
            final Calendar c = Calendar.getInstance();
            final Long currentTime = c.getTimeInMillis();
            if (currentTime.compareTo(AgisProxyPlugin.bannedUsers.get(accountID)) > 0) {
                removeAccountFromBanList(accountID);
            }
            else {
                Log.debug("LOGIN: user " + oid + " tried to login but they are banned, so closing connection");
                con.close();
            }
        }
        final String IPaddress = con.IPAndPort();
        Log.debug("LOGIN: login IPAddress: " + IPaddress);
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ipaddress", IPaddress);
        DataLoggerClient.logData("PLAYER_LOGGED_IN_EVENT", oid, null, accountID, props);
        Log.debug("LOGIN: sent login event");
        return true;
    }
    
    protected void sendPlayersOnline(final boolean loggingOut) {
        int playersOnline = this.playerManager.getPlayerCount();
        if (loggingOut) {
            --playersOnline;
        }
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "players_online");
        props.put("online", playersOnline);
        for (final OID oid : super.getPlayerOids()) {
            final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
    }
    
    public Set<OID> getOids() {
        return (Set<OID>)super.getPlayerOids();
    }
    
    public OID getPlayerOid(final String name) {
        final List<Player> players = new ArrayList<Player>(this.playerManager.getPlayerCount());
        this.playerManager.getPlayers((Collection)players);
        Log.debug("PROXY: searching for player oid from name: " + name + " and numPlayers: " + this.playerManager.getPlayerCount());
        for (final Player player : players) {
            if (name.equals(player.getName())) {
                return player.getOid();
            }
        }
        Log.debug("PROXY: found no oid for player " + name);
        return null;
    }
    
    public boolean isPlayerOnline(final OID oid) {
        final List<Player> players = new ArrayList<Player>(this.playerManager.getPlayerCount());
        this.playerManager.getPlayers((Collection)players);
        for (final Player player : players) {
            if (player.getOid().equals((Object)oid)) {
                return true;
            }
        }
        return false;
    }
    
    protected static void removeAccountFromBanList(final OID accountID) {
        final Entity banList = ObjectManagerClient.loadObjectData("banList");
        AgisProxyPlugin.bannedUsers.remove(accountID);
        banList.setProperty("bannedUsers", (Serializable)AgisProxyPlugin.bannedUsers);
        Log.debug("BAN: removed user: " + accountID + ". Banlist = " + banList);
        ObjectManagerClient.saveObjectData("banList", banList, WorldManagerClient.NAMESPACE);
    }
    
    public void SendFriendsList(final OID playerOid) {
        Log.debug("FRIENDS: sending friends list for player: " + playerOid);
        final HashMap<OID, String> friends = this.aDB.getFriends(playerOid);
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "friends_list");
        int numFriends = 0;
        for (final OID friendOID : friends.keySet()) {
            props.put("friend" + numFriends + "Oid", (Serializable)friendOID);
            props.put("friend" + numFriends + "Name", friends.get(friendOID));
            props.put("friend" + numFriends + "Status", this.isPlayerOnline(friendOID));
            ++numFriends;
        }
        props.put("numFriends", numFriends);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("FRIENDS: sent friends list for player: " + playerOid + " with props: " + props);
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            final OID instanceOid = message.getInstanceOid();
            Log.debug("LoginHook: playerOid=" + playerOid + " instanceOid=" + instanceOid);
            final String playerName = WorldManagerClient.getObjectInfo(playerOid).name;
            final LinkedList<OID> friendsOf = AgisProxyPlugin.this.aDB.getFriendsOf(playerOid);
            for (final OID friendOid : friendsOf) {
                if (AgisProxyPlugin.this.getOids().contains(friendOid)) {
                    WorldManagerClient.sendObjChatMsg(friendOid, 2, String.valueOf(playerName) + " has logged in.");
                    AgisProxyPlugin.this.SendFriendsList(friendOid);
                }
            }
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            AgisProxyPlugin.this.SendFriendsList(playerOid);
            WorldManagerClient.sendObjChatMsg(playerOid, 2, "Welcome to Smoo Online!");
            AgisProxyPlugin.this.sendPlayersOnline(false);
            return true;
        }
    }
    
    class LogoutHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage message = (LogoutMessage)msg;
            final OID playerOid = message.getSubject();
            Log.debug("LOGOUT: proxy logout started for: " + playerOid);
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            AgisProxyPlugin.this.sendPlayersOnline(true);
            final LinkedList<OID> friendsOf = AgisProxyPlugin.this.aDB.getFriendsOf(playerOid);
            final String playerName = WorldManagerClient.getObjectInfo(playerOid).name;
            for (final OID friendOid : friendsOf) {
                if (AgisProxyPlugin.this.getOids().contains(friendOid)) {
                    WorldManagerClient.sendObjChatMsg(friendOid, 2, String.valueOf(playerName) + " has logged out.");
                    AgisProxyPlugin.this.SendFriendsList(friendOid);
                }
            }
            Log.debug("LOGOUT: proxy logout finished for: " + playerOid);
            return true;
        }
    }
    
    class GetPlayerOidFromNameHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final GroupClient.getPlayerByNameMessage GPBNMsg = (GroupClient.getPlayerByNameMessage)msg;
            final String playerName = (String)GPBNMsg.getProperty("inviteeName");
            Log.debug("PROXY: getting player oid from name " + playerName);
            final OID oid = AgisProxyPlugin.this.getPlayerOid(playerName);
            Engine.getAgent().sendOIDResponse(msg, oid);
            return true;
        }
    }
    
    public class AddFriendHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = (OID)spawnMsg.getProperty("playerOid");
            final String friendName = (String)spawnMsg.getProperty("friend");
            Log.debug("FRIEND: adding friend " + friendName + " to " + playerOid);
            final OID friendOID = AgisProxyPlugin.this.getPlayerOid(friendName);
            if (friendOID == null) {
                WorldManagerClient.sendObjChatMsg(playerOid, 2, String.valueOf(friendName) + " could not be found.");
                return true;
            }
            if (AgisProxyPlugin.this.aDB.getFriends(playerOid).containsKey(friendOID)) {
                WorldManagerClient.sendObjChatMsg(playerOid, 2, String.valueOf(friendName) + " is already your friend.");
                return true;
            }
            AgisProxyPlugin.this.aDB.addFriend(playerOid, friendOID, friendName);
            WorldManagerClient.sendObjChatMsg(playerOid, 2, String.valueOf(friendName) + " added to friends.");
            AgisProxyPlugin.this.SendFriendsList(playerOid);
            return true;
        }
    }
    
    public class GetFriendsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = OID.fromLong((long)getMsg.getProperty("playerOid"));
            AgisProxyPlugin.this.SendFriendsList(playerOid);
            AgisProxyPlugin.this.sendPlayersOnline(false);
            return true;
        }
    }
}
