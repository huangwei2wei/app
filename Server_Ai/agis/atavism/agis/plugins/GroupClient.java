// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.messages.PropertyMessage;
import atavism.msgsys.GenericMessage;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.io.Serializable;
import java.util.HashMap;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import atavism.msgsys.MessageAgent;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.agis.objects.AgisGroupMember;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.agis.objects.AgisGroup;
import atavism.msgsys.MessageType;

public class GroupClient
{
    public static final String EXTMSG_GROUP_UPDATE = "ao.GROUP_UPDATE";
    public static final String EXTMSG_GROUP_PROPERTY_UPDATE = "ao.GROUP_PROPERTY_UPDATE";
    public static final String EXTMSG_GROUP_INVITE_REQUEST = "ao.GROUP_INVITE_REQUEST";
    public static final String EXTMSG_GROUP_INVITE_DECLINED = "ao.GROUP_INVITE_DECLINED";
    public static final MessageType MSG_TYPE_GROUP_INVITE;
    public static final MessageType MSG_TYPE_GROUP_INVITE_RESPONSE;
    public static final MessageType MSG_TYPE_GROUP_REMOVE_MEMBER;
    public static final MessageType MSG_TYPE_GROUP_CHAT;
    public static final MessageType MSG_TYPE_REQUEST_GROUP_INFO;
    public static final MessageType MSG_TYPE_GROUP_INFO_RESPONSE;
    public static final MessageType MSG_TYPE_GROUP_SET_ALLOWED_SPEAKER;
    public static final MessageType MSG_TYPE_GROUP_MUTE_VOICE_CHAT;
    public static final MessageType MSG_TYPE_GROUP_VOICE_CHAT_STATUS;
    public static final MessageType MSG_TYPE_GROUP_INVITE_BY_NAME;
    public static final MessageType MSG_TYPE_GET_PLAYER_BY_NAME;
    public static final MessageType MSG_TYPE_CREATE_GROUP;
    public static final MessageType MSG_TYPE_GET_FRIENDS;
    public static final MessageType MSG_TYPE_ADD_FRIEND;
    
    static {
        MSG_TYPE_GROUP_INVITE = MessageType.intern("ao.GROUP_INVITE");
        MSG_TYPE_GROUP_INVITE_RESPONSE = MessageType.intern("ao.GROUP_INVITE_RESPONSE");
        MSG_TYPE_GROUP_REMOVE_MEMBER = MessageType.intern("ao.GROUP_REMOVE_MEMBER");
        MSG_TYPE_GROUP_CHAT = MessageType.intern("ao.GROUP_CHAT");
        MSG_TYPE_REQUEST_GROUP_INFO = MessageType.intern("ao.REQUEST_GROUP_INFO");
        MSG_TYPE_GROUP_INFO_RESPONSE = MessageType.intern("ao.GROUP_INFO_RESPONSE");
        MSG_TYPE_GROUP_SET_ALLOWED_SPEAKER = MessageType.intern("ao.GROUP_SET_ALLOWED_SPEAKER");
        MSG_TYPE_GROUP_MUTE_VOICE_CHAT = MessageType.intern("ao.GROUP_MUTE_VOICE_CHAT");
        MSG_TYPE_GROUP_VOICE_CHAT_STATUS = MessageType.intern("ao.GROUP_VOICE_CHAT_STATUS");
        MSG_TYPE_GROUP_INVITE_BY_NAME = MessageType.intern("iow.GROUP_INVITE_BY_NAME");
        MSG_TYPE_GET_PLAYER_BY_NAME = MessageType.intern("iow.GET_PLAYER_BY_NAME");
        MSG_TYPE_CREATE_GROUP = MessageType.intern("iow.CREATE_GROUP");
        MSG_TYPE_GET_FRIENDS = MessageType.intern("ao.GET_FRIENDS");
        MSG_TYPE_ADD_FRIEND = MessageType.intern("ao.ADD_FRIEND");
    }
    
    public static void SendGroupEventMessage(final GroupEventType eventType, final AgisGroup group, final OID subjectOid) {
        final AgisGroupMember subject = group.GetGroupMember(subjectOid);
        if (subject != null) {
            String message = subject.GetGroupMemberName();
            switch (eventType) {
                case JOINED: {
                    message = String.valueOf(message) + " has joined the group.";
                    break;
                }
                case LEFT: {
                    message = String.valueOf(message) + " has left the group.";
                    break;
                }
                case DISBANDED: {
                    message = String.valueOf(message) + " has disbanded the group";
                    break;
                }
                case LEADERCHANGED: {
                    message = String.valueOf(message) + " is now the group leader.";
                    break;
                }
                case MUTED: {
                    message = String.valueOf(message) + " has muted the group.";
                    break;
                }
                case UNMUTED: {
                    message = String.valueOf(message) + " has un-muted the group.";
                    break;
                }
            }
            final MessageAgent agent = Engine.getAgent();
            final WorldManagerClient.TargetedComMessage groupEventMessage = new WorldManagerClient.TargetedComMessage();
            groupEventMessage.setString(message);
            groupEventMessage.setChannel(4);
            final Collection<AgisGroupMember> groupMembers = group.GetGroupMembers().values();
            for (final AgisGroupMember groupMember : groupMembers) {
                if (!groupMember.GetGroupMemberOid().equals((Object)subjectOid)) {
                    groupEventMessage.setSubject(groupMember.GetGroupMemberOid());
                    groupEventMessage.setTarget(groupMember.GetGroupMemberOid());
                    agent.sendBroadcast((Message)groupEventMessage);
                }
            }
        }
        else {
            Log.error("GroupClient.SendGroupEventMessage - AgisGroup.GetGroupMember(" + subjectOid + ") returned null object");
        }
    }
    
    public static GroupInfo GetGroupMemberOIDs(final OID subject) {
        final WorldManagerClient.ExtensionMessage groupInfoRequest = new WorldManagerClient.ExtensionMessage(GroupClient.MSG_TYPE_REQUEST_GROUP_INFO, "ao.REQUEST_GROUP_INFO", subject);
        final Object groupInfo = Engine.getAgent().sendRPCReturnObject((Message)groupInfoRequest);
        if (Log.loggingDebug) {
            Log.debug("GroupClient.GetGroupMemberOIDs - Received group info - " + groupInfo.toString());
        }
        return (GroupInfo)groupInfo;
    }
    
    public static void groupInviteByName(final OID oid, final String name) throws IOException {
        final groupInviteByNameMessage msg = new groupInviteByNameMessage(oid, name);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("GROUP CLIENT: groupInviteByNameMessage hit 2");
    }
    
    public static OID getPlayerByName(final String name) throws IOException {
        final getPlayerByNameMessage msg = new getPlayerByNameMessage(name);
        Log.debug("GROUP CLIENT: getPlayerByNameMessage hit 1");
        return Engine.getAgent().sendRPCReturnOID((Message)msg);
    }
    
    public static void removeMember(final OID oid) {
        final Map<String, Serializable> propertyMap = new HashMap<String, Serializable>();
        propertyMap.put("target", (Serializable)oid);
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(GroupClient.MSG_TYPE_GROUP_REMOVE_MEMBER, oid, (Map)propertyMap);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("GROUP CLIENT: removeMember hit 2");
    }
    
    public static void createGroup(final ArrayList<OID> groupMembers) {
        final createGroupMessage msg = new createGroupMessage(groupMembers);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("GROUP CLIENT: createGroupMessage hit 2");
    }
    
    public enum GroupEventType
    {
        JOINED("JOINED", 0), 
        LEFT("LEFT", 1), 
        DISBANDED("DISBANDED", 2), 
        LEADERCHANGED("LEADERCHANGED", 3), 
        MUTED("MUTED", 4), 
        UNMUTED("UNMUTED", 5);
        
        private GroupEventType(final String s, final int n) {
        }
    }
    
    public static class GroupInfo implements Serializable
    {
        public OID groupOid;
        public OID groupLeaderOid;
        public HashSet<OID> memberOidSet;
        
        public GroupInfo() {
            this.groupOid = null;
            this.groupLeaderOid = null;
            this.memberOidSet = new HashSet<OID>();
        }
    }
    
    public static class groupInviteByNameMessage extends GenericMessage
    {
        private static final long serialVersionUID = 1L;
        
        public groupInviteByNameMessage() {
            Log.debug("GROUP CLIENT: groupInviteByNameMessage hit 1");
        }
        
        public groupInviteByNameMessage(final OID oid, final String name) {
            this.setMsgType(GroupClient.MSG_TYPE_GROUP_INVITE_BY_NAME);
            this.setProperty("inviterOid", (Serializable)oid);
            this.setProperty("inviteeName", (Serializable)name);
            Log.debug("GROUP CLIENT: groupInviteByNameMessage hit 1");
        }
    }
    
    public static class getPlayerByNameMessage extends GenericMessage
    {
        private static final long serialVersionUID = 1L;
        
        public getPlayerByNameMessage() {
            Log.debug("GROUP CLIENT: getPlayerByNameMessage hit 1");
        }
        
        public getPlayerByNameMessage(final String name) {
            this.setMsgType(GroupClient.MSG_TYPE_GET_PLAYER_BY_NAME);
            this.setProperty("inviteeName", (Serializable)name);
            Log.debug("GROUP CLIENT: getPlayerByNameMessage hit 1");
        }
    }
    
    public static class createGroupMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public createGroupMessage() {
        }
        
        public createGroupMessage(final ArrayList<OID> groupMembers) {
            this.setMsgType(GroupClient.MSG_TYPE_CREATE_GROUP);
            this.setProperty("groupMembers", (Serializable)groupMembers);
            Log.debug("GROUP CLIENT: createGroupMessage hit 1");
        }
    }
}
