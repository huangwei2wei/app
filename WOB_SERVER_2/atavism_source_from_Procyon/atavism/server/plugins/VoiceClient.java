// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import java.util.Set;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;

public class VoiceClient
{
    public static MessageType MSG_TYPE_VOICECLIENT;
    public static MessageType MSG_TYPE_VOICE_MEMBER_ADDED;
    public static MessageType MSG_TYPE_VOICE_MEMBER_REMOVED;
    public static final int SUCCESS = 1;
    public static final int SUCCESS_TRUE = 2;
    public static final int SUCCESS_FALSE = 3;
    public static final int ERROR_NO_SUCH_GROUP = -1;
    public static final int ERROR_GROUP_ALREADY_EXISTS = -2;
    public static final int ERROR_NO_SUCH_MEMBER = -3;
    public static final int ERROR_MEMBER_ALREADY_EXISTS = -4;
    public static final int ERROR_NO_SUCH_OPCODE = -5;
    public static final int ERROR_PLAYER_NOT_CONNECTED = -6;
    public static final int ERROR_MISSING_ADD_PROPERTY = -7;
    public static final int ERROR_MISSING_MAX_VOICES = -8;
    public static final int ERROR_MISSING_POSITIONAL = -9;
    
    public static int addVoiceGroup(final OID oid, final boolean positional, final int maxVoices) {
        return sendNewGroupMessage("addVoiceGroup", oid, positional, maxVoices);
    }
    
    public static int removeVoiceGroup(final OID groupOid) {
        return sendVoicePluginRPC("removeVoiceGroup", groupOid);
    }
    
    public static int isPositional(final OID groupOid) {
        return sendVoicePluginRPC("isPositional", groupOid);
    }
    
    public int addMemberAllowed(final OID groupOid, final OID memberOid, final String authToken) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage("addMemberAllowed", groupOid, memberOid);
        msg.setProperty("authToken", authToken);
        return Engine.getAgent().sendRPCReturnInt(msg);
    }
    
    public int setAllowedMembers(final OID groupOid, final Set<OID> allowedMembers) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage("addMemberAllowed", groupOid);
        msg.setProperty("allowedMembers", (Serializable)allowedMembers);
        return Engine.getAgent().sendRPCReturnInt(msg);
    }
    
    public Set<OID> getAllowedMembers(final OID groupOid) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage("getAllowedMembers", groupOid);
        return (Set<OID>)Engine.getAgent().sendRPCReturnObject(msg);
    }
    
    public static int addMember(final OID groupOid, final OID memberOid, final int priority, final boolean allowedSpeaker) {
        return sendAddMemberMessage(groupOid, memberOid, priority, allowedSpeaker);
    }
    
    public static int isMember(final OID groupOid, final OID memberOid) {
        return sendVoicePluginRPC("isMember", groupOid, memberOid);
    }
    
    public static int removeMember(final OID groupOid, final OID memberOid) {
        return sendVoicePluginRPC("isPositional", groupOid, memberOid);
    }
    
    public static int isMemberSpeaking(final OID groupOid, final OID memberOid) {
        return sendVoicePluginRPC("isMemberSpeaking", groupOid, memberOid);
    }
    
    public static int isListener(final OID groupOid, final OID memberOid) {
        return sendVoicePluginRPC("isListener", groupOid, memberOid);
    }
    
    public static int setAllowedSpeaker(final OID groupOid, final OID memberOid, final boolean add) {
        return sendVoicePluginRPC("setAllowedSpeaker", groupOid, memberOid, add);
    }
    
    public static int setMemberSpeaking(final OID groupOid, final OID memberOid, final boolean add) {
        return sendVoicePluginRPC("setMemberSpeaking", groupOid, memberOid, add);
    }
    
    public static int setListener(final OID groupOid, final OID memberOid, final boolean add) {
        return sendVoicePluginRPC("setListener", groupOid, memberOid, add);
    }
    
    public static int isAllowedSpeaker(final OID groupOid, final OID memberOid) {
        return sendVoicePluginRPC("isAllowedSpeaker", groupOid, memberOid);
    }
    
    public static OID getPlayerGroup(final OID memberOid) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage("getPlayerGroup", memberOid);
        return Engine.getAgent().sendRPCReturnOID(msg);
    }
    
    public static String errorString(final int errorCode) {
        switch (errorCode) {
            case -1: {
                return "There is no group with the supplied groupOid";
            }
            case -2: {
                return "The group with the supplied groupOid already exists";
            }
            case -3: {
                return "There is no member in the group identified by the supplied groupOid with the supplied memberOid";
            }
            case -4: {
                return "There is already a member with the supplied memberOid in the group identified by the groupOid";
            }
            case -5: {
                return "The VoicePlugin doesn't recognize the supplied voice message opcode";
            }
            case -6: {
                return "The player identified by the memberOid is not currently connected to the VoicePlugin";
            }
            case -7: {
                return "There is no 'add' property in the message";
            }
            case -8: {
                return "There is no 'maxVoices' property in the message";
            }
            case -9: {
                return "There is no 'positional' property in the message";
            }
            default: {
                return "No error corresponding to the supplied error code";
            }
        }
    }
    
    protected static WorldManagerClient.ExtensionMessage makeVoicePluginMessage(final String opcode, final OID groupOid) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage();
        msg.setMsgType(VoiceClient.MSG_TYPE_VOICECLIENT);
        msg.setProperty("opcode", opcode);
        msg.setProperty("groupOid", groupOid);
        return msg;
    }
    
    protected static WorldManagerClient.ExtensionMessage makeVoicePluginMessage(final String opcode, final OID groupOid, final OID memberOid) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage(opcode, groupOid);
        msg.setProperty("memberOid", memberOid);
        return msg;
    }
    
    protected static WorldManagerClient.ExtensionMessage makeVoicePluginMessage(final String opcode, final OID groupOid, final OID memberOid, final boolean add) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage(opcode, groupOid, memberOid);
        msg.setProperty("add", add);
        return msg;
    }
    
    protected static int sendNewGroupMessage(final String opcode, final OID groupOid, final boolean positional, final int maxVoices) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage(opcode, groupOid);
        msg.setProperty("positional", positional);
        msg.setProperty("maxVoices", maxVoices);
        return Engine.getAgent().sendRPCReturnInt(msg);
    }
    
    protected static int sendAddMemberMessage(final OID groupOid, final OID memberOid, final int priority, final boolean allowedSpeaker) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage("addMember", groupOid, memberOid);
        msg.setProperty("priority", priority);
        msg.setProperty("allowedSpeaker", allowedSpeaker);
        return Engine.getAgent().sendRPCReturnInt(msg);
    }
    
    protected static int sendVoicePluginRPC(final String opcode, final OID groupOid) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage(opcode, groupOid);
        return Engine.getAgent().sendRPCReturnInt(msg);
    }
    
    protected static int sendVoicePluginRPC(final String opcode, final OID groupOid, final OID memberOid) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage(opcode, groupOid, memberOid);
        return Engine.getAgent().sendRPCReturnInt(msg);
    }
    
    protected static int sendVoicePluginRPC(final String opcode, final OID groupOid, final OID memberOid, final boolean add) {
        final WorldManagerClient.ExtensionMessage msg = makeVoicePluginMessage(opcode, groupOid, memberOid, add);
        return Engine.getAgent().sendRPCReturnInt(msg);
    }
    
    static {
        VoiceClient.MSG_TYPE_VOICECLIENT = MessageType.intern("ao.VOICECLIENT");
        VoiceClient.MSG_TYPE_VOICE_MEMBER_ADDED = MessageType.intern("ao.VOICE_MEMBER_ADDED");
        VoiceClient.MSG_TYPE_VOICE_MEMBER_REMOVED = MessageType.intern("ao.VOICE_MEMBER_REMOVED");
    }
}
