// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.concurrent.locks.Lock;
import java.io.IOException;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.msgsys.ResponseMessage;
import atavism.server.messages.LoginMessage;
import atavism.server.util.ObjectLockManager;
import java.util.HashSet;
import atavism.agis.objects.CombatInfo;
import java.util.Iterator;
import java.util.Set;
import atavism.msgsys.MessageAgent;
import atavism.msgsys.Message;
import atavism.agis.objects.AgisGroupMember;
import java.util.HashMap;
import java.util.Collection;
import java.util.TreeSet;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Hook;
import atavism.server.messages.PropertyMessage;
import atavism.server.util.Log;
import atavism.server.messages.LogoutMessage;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.VoiceClient;
import atavism.msgsys.MessageTypeFilter;
import java.util.Hashtable;
import java.util.ArrayList;
import atavism.agis.objects.AgisGroup;
import atavism.server.engine.OID;
import java.util.Map;
import java.util.List;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class GroupPlugin extends EnginePlugin
{
    protected static final Logger _log;
    protected static List<String> _registeredStats;
    protected static Map<OID, AgisGroup> _currentGroups;
    protected static int _maxGroupSize;
    
    static {
        _log = new Logger("GroupPlugin");
        GroupPlugin._registeredStats = new ArrayList<String>();
        GroupPlugin._currentGroups = new Hashtable<OID, AgisGroup>();
        GroupPlugin._maxGroupSize = 4;
    }
    
    public GroupPlugin() {
        super("Group");
        this.setPluginType("Group");
    }
    
    public String GetName() {
        return "GroupPlugin";
    }
    
    public void onActivate() {
        super.onActivate();
        this.RegisterHooks();
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(GroupClient.MSG_TYPE_GROUP_INVITE);
        filter.addType(GroupClient.MSG_TYPE_GROUP_REMOVE_MEMBER);
        filter.addType(GroupClient.MSG_TYPE_GROUP_CHAT);
        filter.addType(GroupClient.MSG_TYPE_GROUP_INVITE_RESPONSE);
        filter.addType(GroupClient.MSG_TYPE_GROUP_SET_ALLOWED_SPEAKER);
        filter.addType(GroupClient.MSG_TYPE_GROUP_MUTE_VOICE_CHAT);
        filter.addType(GroupClient.MSG_TYPE_GROUP_VOICE_CHAT_STATUS);
        filter.addType(VoiceClient.MSG_TYPE_VOICE_MEMBER_ADDED);
        filter.addType(GroupClient.MSG_TYPE_GROUP_INVITE_BY_NAME);
        filter.addType(GroupClient.MSG_TYPE_CREATE_GROUP);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter responderFilter = new MessageTypeFilter();
        responderFilter.addType(LogoutMessage.MSG_TYPE_LOGOUT);
        responderFilter.addType(GroupClient.MSG_TYPE_REQUEST_GROUP_INFO);
        Engine.getAgent().createSubscription((IFilter)responderFilter, (MessageCallback)this, 8);
        if (Log.loggingDebug) {
            GroupPlugin._log.debug("GroupPlugin activated.");
        }
    }
    
    public void RegisterHooks() {
        this.getHookManager().addHook(PropertyMessage.MSG_TYPE_PROPERTY, (Hook)new PropertyHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_INVITE, (Hook)new GroupInviteHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_INVITE_RESPONSE, (Hook)new GroupInviteResponseHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_REMOVE_MEMBER, (Hook)new GroupRemoveMemberHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_CHAT, (Hook)new GroupChatHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_REQUEST_GROUP_INFO, (Hook)new RequestGroupInfoHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook)new LogOutHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_SET_ALLOWED_SPEAKER, (Hook)new SetAllowedSpeakerHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_MUTE_VOICE_CHAT, (Hook)new MuteGroupHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_VOICE_CHAT_STATUS, (Hook)new VoiceStatusHook());
        this.getHookManager().addHook(VoiceClient.MSG_TYPE_VOICE_MEMBER_ADDED, (Hook)new VoiceMemberAddedHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_GROUP_INVITE_BY_NAME, (Hook)new GroupInviteByNameHook());
        this.getHookManager().addHook(GroupClient.MSG_TYPE_CREATE_GROUP, (Hook)new CreateGroupHook());
    }
    
    public static void RegisterStat(final String stat) {
        GroupPlugin._registeredStats.add(stat);
    }
    
    protected AgisGroup GetGroup(final OID groupOid) {
        return GroupPlugin._currentGroups.get(groupOid);
    }
    
    public static List<String> GetRegisteredStats() {
        return GroupPlugin._registeredStats;
    }
    
    protected void SendGroupUpdate(final AgisGroup group) {
        final MessageAgent agent = Engine.getAgent();
        final WorldManagerClient.TargetedExtensionMessage groupUpdateMsg = new WorldManagerClient.TargetedExtensionMessage();
        groupUpdateMsg.setExtensionType("ao.GROUP_UPDATE");
        groupUpdateMsg.setProperty("maxGroupSize", (Serializable)String.valueOf(GroupPlugin._maxGroupSize));
        groupUpdateMsg.setProperty("groupOid", (Serializable)group.getOid());
        int counter = 1;
        final Hashtable<OID, AgisGroupMember> groupMembers = group.GetGroupMembers();
        final Set<OID> groupMemberKeys = new TreeSet<OID>(group.GetGroupMembers().keySet());
        for (final OID groupMemberKey : groupMemberKeys) {
            final HashMap<String, Serializable> groupMemberInfo = new HashMap<String, Serializable>();
            final AgisGroupMember groupMember = groupMembers.get(groupMemberKey);
            groupMemberInfo.put("memberOid", (Serializable)groupMember.GetGroupMemberOid());
            groupMemberInfo.put("name", groupMember.GetGroupMemberName());
            groupMemberInfo.put("voiceEnabled", groupMember.GetVoiceEnabled());
            groupMemberInfo.put("allowedSpeaker", groupMember.GetAllowedSpeaker());
            groupMemberInfo.put("groupMuted", group.GetGroupMuted());
            for (final String stat : GroupPlugin._registeredStats) {
                groupMemberInfo.put(stat, groupMember.GetGroupMemberStat(stat));
            }
            groupUpdateMsg.setProperty(String.valueOf(counter), (Serializable)groupMemberInfo);
            if (group.GetGroupLeaderOid() == null && counter == 1) {
                group.SetGroupLeaderOid(groupMember.GetGroupMemberOid());
            }
            ++counter;
        }
        for (final AgisGroupMember groupMember2 : group.GetGroupMembers().values()) {
            groupUpdateMsg.setTarget(groupMember2.GetGroupMemberOid());
            agent.sendBroadcast((Message)groupUpdateMsg);
        }
    }
    
    protected void RemoveGroupMember(final CombatInfo info) {
        AgisGroup group = this.GetGroup(info.getGroupOid());
        if (group == null) {
            if (Log.loggingDebug) {
                GroupPlugin._log.error("GroupPlugin.RemoveGroupMember : group is null");
            }
            info.setGroupOid(null);
            info.setGroupMemberOid(null);
            return;
        }
        group.RemoveGroupMember(info);
        if (info.getOwnerOid().equals((Object)group.GetGroupLeaderOid())) {
            group.SetGroupLeaderOid(null);
        }
        if (group.GetNumGroupMembers() > 1) {
            this.SendGroupUpdate(group);
        }
        else {
            final CombatInfo groupLeader = CombatPlugin.getCombatInfo(group.GetGroupLeaderOid());
            if (groupLeader != null) {
                group.RemoveGroupMember(groupLeader);
                final WorldManagerClient.TargetedExtensionMessage groupUpdateMsg = new WorldManagerClient.TargetedExtensionMessage(groupLeader.getOwnerOid());
                groupUpdateMsg.setExtensionType("ao.GROUP_UPDATE");
                Engine.getAgent().sendBroadcast((Message)groupUpdateMsg);
            }
            else {
                GroupPlugin._log.error("GroupPlugin.RemoveGroupMember - Group leader is null");
            }
            GroupPlugin._currentGroups.remove(group);
            group = null;
        }
        final WorldManagerClient.TargetedExtensionMessage groupUpdateMsg2 = new WorldManagerClient.TargetedExtensionMessage(info.getOwnerOid());
        groupUpdateMsg2.setExtensionType("ao.GROUP_UPDATE");
        Engine.getAgent().sendBroadcast((Message)groupUpdateMsg2);
    }
    
    public static void SetMaxGroupSize(final int size) {
        GroupPlugin._maxGroupSize = size;
    }
    
    protected boolean UpdateGroupMemberProps(final PropertyMessage propMsg) {
        final CombatInfo subject = CombatPlugin.getCombatInfo(propMsg.getSubject());
        if (subject == null) {
            return false;
        }
        if (subject.isGrouped()) {
            final Set<String> props = (Set<String>)propMsg.keySet();
            final Map<String, Serializable> statsToUpdate = new HashMap<String, Serializable>();
            for (final String stat : GroupPlugin._registeredStats) {
                if (props.contains(stat)) {
                    statsToUpdate.put(stat, propMsg.getProperty(stat));
                }
            }
            if (statsToUpdate.size() > 0) {
                final AgisGroup group = this.GetGroup(subject.getGroupOid());
                if (group == null) {
                    GroupPlugin._log.error("GroupPlugin.UpdateGroupMemberProps - group is null");
                    subject.setGroupMemberOid(null);
                    subject.setGroupOid(null);
                    return false;
                }
                this.SendGroupPropertyUpdate(subject.getOwnerOid(), group, statsToUpdate);
            }
        }
        return true;
    }
    
    protected void SendGroupPropertyUpdate(final OID playerOid, final AgisGroup group, final Map<String, Serializable> statsToUpdate) {
        final Collection<AgisGroupMember> groupMembers = group.GetGroupMembers().values();
        for (final AgisGroupMember groupEntry : groupMembers) {
            final WorldManagerClient.TargetedExtensionMessage updateMessage = new WorldManagerClient.TargetedExtensionMessage(groupEntry.GetGroupMemberOid());
            updateMessage.setExtensionType("ao.GROUP_PROPERTY_UPDATE");
            updateMessage.setProperty("memberOid", (Serializable)playerOid);
            for (final String stat : statsToUpdate.keySet()) {
                updateMessage.setProperty(stat, (Serializable)statsToUpdate.get(stat));
            }
            Engine.getAgent().sendBroadcast((Message)updateMessage);
        }
    }
    
    protected boolean HandleInviteResponse(final WorldManagerClient.ExtensionMessage inviteMsg) {
        GroupPlugin._log.debug("GroupPlugin.HandleInviteResponse");
        final OID inviterOid = (OID)inviteMsg.getProperty("groupLeaderOid");
        final OID inviteeOid = inviteMsg.getSubject();
        GroupPlugin._log.debug("GroupPlugin.HandleInviteResponse: inviterOid=" + inviterOid + ", inviteeOid=" + inviteeOid);
        final CombatInfo invitee = CombatPlugin.getCombatInfo(inviteeOid);
        final CombatInfo inviter = CombatPlugin.getCombatInfo(inviterOid);
        if (inviter == null || invitee == null) {
            GroupPlugin._log.debug("GroupPlugin.HandleInviteResponse: null inviter/invitee, inviter=" + inviter + ", invitee=" + invitee);
            return false;
        }
        final String response = inviteMsg.getProperty("response").toString();
        if (response.equals("accept")) {
            AgisGroup group = null;
            final Boolean voiceEnabled = (Boolean)inviteMsg.getProperty("groupVoiceEnabled");
            if (inviter.isGrouped()) {
                group = this.GetGroup(inviter.getGroupOid());
                if (group == null) {
                    GroupPlugin._log.error("GroupPlugin.HandleInviteResponse - group is null");
                    inviter.setGroupMemberOid(null);
                    inviter.setGroupOid(null);
                    return false;
                }
                final AgisGroupMember groupMember = group.AddGroupMember(invitee);
                groupMember.SetVoiceEnabled(voiceEnabled);
            }
            else {
                group = new AgisGroup();
                final AgisGroupMember groupLeader = group.AddGroupMember(inviter);
                groupLeader.SetVoiceEnabled(true);
                group.SetGroupLeaderOid(inviter.getOwnerOid());
                final AgisGroupMember groupMember2 = group.AddGroupMember(invitee);
                groupMember2.SetVoiceEnabled(voiceEnabled);
                GroupPlugin._currentGroups.put(group.GetGroupOid(), group);
            }
            this.SendGroupUpdate(group);
        }
        else {
            GroupPlugin._log.debug("GroupPlugin.HandleInviteResponse: declined");
            final String inviteeName = WorldManagerClient.getObjectInfo(invitee.getOwnerOid()).name;
            this.SendTargetedGroupMessage(inviter.getOwnerOid(), String.valueOf(inviteeName) + " has declined your group invite.");
        }
        invitee.setPendingGroupInvite(false);
        GroupPlugin._log.debug("GroupPlugin.HandleInviteResponse: done");
        return true;
    }
    
    protected void HandleGroupChat(final WorldManagerClient.ExtensionMessage groupChatMsg) {
        final String message = groupChatMsg.getProperty("message").toString();
        final OID senderOid = OID.fromLong((long)groupChatMsg.getProperty("senderOid"));
        final CombatInfo sender = CombatPlugin.getCombatInfo(senderOid);
        if (sender.isGrouped()) {
            final String senderName = WorldManagerClient.getObjectInfo(sender.getOwnerOid()).name;
            final AgisGroup group = this.GetGroup(sender.getGroupOid());
            if (group == null) {
                GroupPlugin._log.error("GroupPlugin.HandleGroupChat - group is null");
                sender.setGroupMemberOid(null);
                sender.setGroupOid(null);
                return;
            }
            final Collection<AgisGroupMember> groupMembers = group.GetGroupMembers().values();
            for (final AgisGroupMember groupMember : groupMembers) {
                this.SendTargetedGroupMessage(groupMember.GetGroupMemberOid(), "[" + senderName + "]: " + message);
            }
        }
        else {
            this.SendTargetedGroupMessage(sender.getOwnerOid(), "You are not grouped!");
        }
    }
    
    protected boolean HandleGroupInvite(final OID inviterOid, final OID inviteeOid) {
        final CombatInfo inviter = CombatPlugin.getCombatInfo(inviterOid);
        final CombatInfo invitee = CombatPlugin.getCombatInfo(inviteeOid);
        if (inviter == null || invitee == null) {
            return false;
        }
        if (Log.loggingDebug) {
            GroupPlugin._log.debug("GroupPlugin.GroupInviteHook: Received group invite message inviter:" + inviter.getOwnerOid() + " invitee:" + invitee.getOwnerOid());
        }
        if (inviter.getOwnerOid().equals((Object)invitee.getOwnerOid())) {
            return true;
        }
        if (inviter.isGrouped()) {
            final AgisGroup group = this.GetGroup(inviter.getGroupOid());
            if (group == null) {
                GroupPlugin._log.error("GroupPlugin.HandleGroupInvite - Inviter's group is null");
                inviter.setGroupMemberOid(null);
                inviter.setGroupOid(null);
                return false;
            }
            if (group.GetGroupMembers().size() >= GroupPlugin._maxGroupSize) {
                this.SendTargetedGroupMessage(inviter.getOwnerOid(), "Your group is full.");
                return true;
            }
        }
        final String inviteeName = WorldManagerClient.getObjectInfo(invitee.getOwnerOid()).name;
        this.SendTargetedGroupMessage(inviter.getOwnerOid(), "You have invited " + inviteeName + " to your group.");
        if (invitee.isGrouped()) {
            this.SendTargetedGroupMessage(inviter.getOwnerOid(), String.valueOf(inviteeName) + " is already grouped.");
        }
        else if (invitee.isPendingGroupInvite()) {
            this.SendTargetedGroupMessage(inviter.getOwnerOid(), String.valueOf(inviteeName) + " is already considering a group invite.");
        }
        else if (!invitee.isGrouped()) {
            invitee.setPendingGroupInvite(true);
            final WorldManagerClient.TargetedExtensionMessage inviteRequestMsg = new WorldManagerClient.TargetedExtensionMessage(invitee.getOwnerOid());
            inviteRequestMsg.setExtensionType("ao.GROUP_INVITE_REQUEST");
            inviteRequestMsg.setProperty("groupLeaderOid", (Serializable)inviter.getOwnerOid());
            final String inviterName = WorldManagerClient.getObjectInfo(inviter.getOwnerOid()).name;
            inviteRequestMsg.setProperty("groupLeaderName", (Serializable)inviterName);
            if (Log.loggingDebug) {
                GroupPlugin._log.debug("GroupPlugin.GroupInviteHook: Sending group invite request inviter:" + inviter.getOwnerOid() + " invitee:" + invitee.getOwnerOid());
            }
            Engine.getAgent().sendBroadcast((Message)inviteRequestMsg);
        }
        return true;
    }
    
    protected GroupClient.GroupInfo HandleGroupInfoRequest(final CombatInfo subject) {
        final GroupClient.GroupInfo groupInfo = new GroupClient.GroupInfo();
        final HashSet<OID> memberOids = new HashSet<OID>();
        if (subject.isGrouped()) {
            final AgisGroup group = this.GetGroup(subject.getGroupOid());
            if (group == null) {
                GroupPlugin._log.error("GroupPlugin.HandleGroupInfoRequest - group is null");
                subject.setGroupMemberOid(null);
                subject.setGroupOid(null);
                return groupInfo;
            }
            groupInfo.groupOid = group.GetGroupOid();
            groupInfo.groupLeaderOid = group.GetGroupLeaderOid();
            final Collection<AgisGroupMember> groupMembers = group.GetGroupMembers().values();
            for (final AgisGroupMember groupMember : groupMembers) {
                memberOids.add(groupMember.GetGroupMemberOid());
            }
            groupInfo.memberOidSet = memberOids;
        }
        return groupInfo;
    }
    
    protected void SendTargetedGroupMessage(final OID target, final String message) {
        final WorldManagerClient.TargetedComMessage comMessage = new WorldManagerClient.TargetedComMessage();
        comMessage.setString(message);
        comMessage.setChannel(4);
        comMessage.setTarget(target);
        Engine.getAgent().sendBroadcast((Message)comMessage);
    }
    
    protected static AgisGroupMember GetGroupMember(final OID subjectOid) {
        final Collection<AgisGroup> groups = GroupPlugin._currentGroups.values();
        for (final AgisGroup group : groups) {
            final AgisGroupMember subject = group.GetGroupMember(subjectOid);
            if (subject != null) {
                return subject;
            }
        }
        return null;
    }
    
    protected boolean HandleSetAllowedSpeaker(final OID targetOid, final OID setterOid, final OID groupOid) {
        final AgisGroup group = this.GetGroup(groupOid);
        final AgisGroupMember target = group.GetGroupMember(targetOid);
        if (group == null) {
            Log.error("GroupPlugin.HandleSetAllowedSpeaker - Group is null.");
            return false;
        }
        if (target == null) {
            Log.error("GroupPlugin.HandleSetAllowedSpeaker - Target is null.");
            return false;
        }
        if (target.GetVoiceEnabled()) {
            final Map<String, Serializable> statToUpdate = new HashMap<String, Serializable>();
            if (!group.GetGroupMuted() || setterOid.equals((Object)group.GetGroupLeaderOid())) {
                target.SetAllowedSpeaker(!target.GetAllowedSpeaker());
                final int result = VoiceClient.setAllowedSpeaker(groupOid, targetOid, (boolean)target.GetAllowedSpeaker());
                if (result != 1) {
                    Log.error("GroupPlugin.HandleSetAllowedSpeaker : Create Voice Group Response - " + VoiceClient.errorString(result));
                }
            }
            statToUpdate.put("allowedSpeaker", target.GetAllowedSpeaker());
            this.SendGroupPropertyUpdate(targetOid, group, statToUpdate);
        }
        return true;
    }
    
    protected boolean HandleMuteGroup(final OID setterOid, final OID groupOid) {
        final AgisGroup group = this.GetGroup(groupOid);
        if (group == null) {
            Log.error("GroupPlugin.HandleMuteGroup - Group is null.");
            return false;
        }
        if (setterOid.equals((Object)group.GetGroupLeaderOid())) {
            group.SetGroupMuted(!group.GetGroupMuted());
            final Collection<AgisGroupMember> groupMembers = group.GetGroupMembers().values();
            for (final AgisGroupMember groupMember : groupMembers) {
                if (groupMember.GetVoiceEnabled() && !groupMember.GetGroupMemberOid().equals((Object)group.GetGroupLeaderOid())) {
                    groupMember.SetAllowedSpeaker(!group.GetGroupMuted());
                    VoiceClient.setAllowedSpeaker(groupOid, groupMember.GetGroupMemberOid(), !group.GetGroupMuted());
                    final Map<String, Serializable> statToUpdate = new HashMap<String, Serializable>();
                    statToUpdate.put("allowedSpeaker", !group.GetGroupMuted());
                    statToUpdate.put("groupMuted", group.GetGroupMuted());
                    this.SendGroupPropertyUpdate(groupMember.GetGroupMemberOid(), group, statToUpdate);
                }
            }
            GroupClient.GroupEventType eventType = GroupClient.GroupEventType.MUTED;
            if (!group.GetGroupMuted()) {
                eventType = GroupClient.GroupEventType.UNMUTED;
            }
            GroupClient.SendGroupEventMessage(eventType, group, setterOid);
        }
        return true;
    }
    
    protected boolean HandledVoiceStatus(final OID playerOid, final OID groupOid, final Boolean voiceEnabled) {
        final AgisGroup group = this.GetGroup(groupOid);
        if (group == null) {
            Log.error("GroupPlugin.HandledVoiceStatus - Group is null.");
            return false;
        }
        final AgisGroupMember player = group.GetGroupMember(playerOid);
        if (player == null) {
            Log.error("GroupPlugin.HandledVoiceStatus - Player is null.");
            return false;
        }
        player.SetVoiceEnabled(voiceEnabled);
        final Map<String, Serializable> statToUpdate = new HashMap<String, Serializable>();
        statToUpdate.put("voiceEnabled", voiceEnabled);
        this.SendGroupPropertyUpdate(playerOid, group, statToUpdate);
        return true;
    }
    
    protected boolean HandleVoiceMemberAdded(final OID memberOid, final OID groupOid) {
        GroupPlugin._log.debug("GroupPlugin.HandleVoiceMemberAdded - Got member added message");
        if (GroupPlugin._currentGroups.containsKey(groupOid)) {
            GroupPlugin._log.debug("GroupPlugin.HandleVoiceMemberAdded - Got member match");
            final AgisGroup group = GroupPlugin._currentGroups.get(groupOid);
            final AgisGroupMember groupMember = group.GetGroupMember(memberOid);
            if (groupMember != null) {
                final Map<String, Serializable> statsToUpdate = new HashMap<String, Serializable>();
                if (group.GetGroupMuted()) {
                    groupMember.SetAllowedSpeaker(Boolean.FALSE);
                    statsToUpdate.put("allowedSpeaker", Boolean.FALSE);
                }
                if (!groupMember.GetVoiceEnabled()) {
                    groupMember.SetVoiceEnabled(Boolean.TRUE);
                    statsToUpdate.put("voiceEnabled", Boolean.FALSE);
                }
                if (statsToUpdate.size() > 0) {
                    this.SendGroupPropertyUpdate(memberOid, group, statsToUpdate);
                }
            }
            else {
                GroupPlugin._log.error("GroupPlugin.HandleVoiceMemberAdded - Player with OID " + memberOid.toString() + " is not a member of the group with OID " + groupOid.toString());
            }
        }
        return true;
    }
    
    protected boolean HandleCreateGroup(final GroupClient.createGroupMessage createMsg) {
        final ArrayList<OID> groupMemberOids = (ArrayList<OID>)createMsg.getProperty("groupMembers");
        final ArrayList<CombatInfo> groupMembers = new ArrayList<CombatInfo>();
        for (final OID memberOid : groupMemberOids) {
            groupMembers.add(CombatPlugin.getCombatInfo(memberOid));
        }
        if (groupMembers.size() < 2) {
            return false;
        }
        AgisGroup group = null;
        group = new AgisGroup();
        final AgisGroupMember groupLeader = group.AddGroupMember(groupMembers.get(0));
        groupLeader.SetVoiceEnabled(false);
        group.SetGroupLeaderOid(groupMemberOids.get(0));
        for (int i = 1; i < groupMembers.size(); ++i) {
            final AgisGroupMember groupMember = group.AddGroupMember(groupMembers.get(i));
            groupMember.SetVoiceEnabled(false);
            groupMembers.get(i).setPendingGroupInvite(false);
        }
        GroupPlugin._currentGroups.put(group.GetGroupOid(), group);
        this.SendGroupUpdate(group);
        return true;
    }
    
    static /* synthetic */ ObjectLockManager access$0(final GroupPlugin groupPlugin) {
        return groupPlugin.getObjectLockManager();
    }
    
    class PropertyHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            return GroupPlugin.this.UpdateGroupMemberProps(propMsg);
        }
    }
    
    class GroupInviteResponseHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage inviteMsg = (WorldManagerClient.ExtensionMessage)msg;
            return GroupPlugin.this.HandleInviteResponse(inviteMsg);
        }
    }
    
    class GroupRemoveMemberHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage removeMemberMsg = (WorldManagerClient.ExtensionMessage)msg;
            final CombatInfo subject = CombatPlugin.getCombatInfo((OID)removeMemberMsg.getProperty("target"));
            if (subject == null) {
                return false;
            }
            GroupPlugin.this.RemoveGroupMember(subject);
            return true;
        }
    }
    
    class GroupChatHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage groupChatMsg = (WorldManagerClient.ExtensionMessage)msg;
            GroupPlugin.this.HandleGroupChat(groupChatMsg);
            return true;
        }
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            return true;
        }
    }
    
    class LogOutHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage logoutMsg = (LogoutMessage)msg;
            final OID playerOid = logoutMsg.getSubject();
            Log.debug("LOGOUT: group logout started for: " + playerOid);
            final CombatInfo subject = CombatPlugin.getCombatInfo(logoutMsg.getSubject());
            if (subject != null && subject.isGrouped()) {
                GroupPlugin.this.RemoveGroupMember(subject);
            }
            Engine.getAgent().sendResponse(new ResponseMessage((Message)logoutMsg));
            Log.debug("LOGOUT: group logout finished for: " + playerOid);
            return true;
        }
    }
    
    class GroupInviteHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage inviteMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID inviterOid = inviteMsg.getSubject();
            final OID inviteeOid = (OID)inviteMsg.getProperty("target");
            return GroupPlugin.this.HandleGroupInvite(inviterOid, inviteeOid);
        }
    }
    
    class GroupInviteByNameHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final GroupClient.groupInviteByNameMessage inviteMsg = (GroupClient.groupInviteByNameMessage)msg;
            final OID inviterOid = (OID)inviteMsg.getProperty("inviterOid");
            final String inviteeName = (String)inviteMsg.getProperty("inviteeName");
            try {
                final OID inviteeOid = GroupClient.getPlayerByName(inviteeName);
                if (inviteeOid != null) {
                    GroupPlugin.this.HandleGroupInvite(inviterOid, inviteeOid);
                    Log.debug("GROUP: invited player oid: " + inviteeOid);
                }
                else {
                    ExtendedCombatMessages.sendErrorMessage(inviterOid, "Player " + inviteeName + " could not be found.");
                }
            }
            catch (IOException ex) {}
            return true;
        }
    }
    
    class RequestGroupInfoHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage requestGroupInfoMsg = (WorldManagerClient.ExtensionMessage)msg;
            final CombatInfo subject = CombatPlugin.getCombatInfo(requestGroupInfoMsg.getSubject());
            if (subject == null) {
                return false;
            }
            final OID sOid = subject.getOwnerOid();
            final Lock lock = GroupPlugin.access$0(GroupPlugin.this).getLock(sOid);
            lock.lock();
            try {
                final GroupClient.GroupInfo groupInfo = GroupPlugin.this.HandleGroupInfoRequest(subject);
                Engine.getAgent().sendObjectResponse(msg, (Object)groupInfo);
            }
            finally {
                lock.unlock();
            }
            lock.unlock();
            return true;
        }
    }
    
    class SetAllowedSpeakerHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage setAllowedSpeakerMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID targetOid = (OID)setAllowedSpeakerMsg.getProperty("target");
            final OID setterOid = (OID)setAllowedSpeakerMsg.getProperty("setter");
            final OID groupOid = (OID)setAllowedSpeakerMsg.getProperty("groupOid");
            return GroupPlugin.this.HandleSetAllowedSpeaker(targetOid, setterOid, groupOid);
        }
    }
    
    class MuteGroupHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage setAllowedSpeakerMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID setterOid = (OID)setAllowedSpeakerMsg.getProperty("setter");
            final OID groupOid = (OID)setAllowedSpeakerMsg.getProperty("groupOid");
            return GroupPlugin.this.HandleMuteGroup(setterOid, groupOid);
        }
    }
    
    class VoiceStatusHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage voiceStatusMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = (OID)voiceStatusMsg.getProperty("playerOid");
            final OID groupOid = (OID)voiceStatusMsg.getProperty("groupOid");
            final Boolean voiceEnabled = (Boolean)voiceStatusMsg.getProperty("voiceEnabled");
            return GroupPlugin.this.HandledVoiceStatus(playerOid, groupOid, voiceEnabled);
        }
    }
    
    class VoiceMemberAddedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage voiceMemberAddedMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID memberOid = (OID)voiceMemberAddedMsg.getProperty("memberOid");
            final OID groupOid = (OID)voiceMemberAddedMsg.getProperty("groupOid");
            return GroupPlugin.this.HandleVoiceMemberAdded(memberOid, groupOid);
        }
    }
    
    class CreateGroupHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final GroupClient.createGroupMessage createMsg = (GroupClient.createGroupMessage)msg;
            return GroupPlugin.this.HandleCreateGroup(createMsg);
        }
    }
}
