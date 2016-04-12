// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.plugins.VoiceClient;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import atavism.agis.plugins.GroupClient;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import java.util.Hashtable;
import atavism.server.objects.Entity;

public class AgisGroup extends Entity
{
    private Hashtable<OID, AgisGroupMember> _groupMembers;
    private static final long serialVersionUID = 1L;
    private OID _groupLeaderOid;
    private Boolean _groupMuted;
    
    public AgisGroup() {
        super("");
        this._groupMuted = false;
        this._groupMembers = new Hashtable<OID, AgisGroupMember>();
        if (Log.loggingDebug) {
            AgisGroup.log.debug("AgisGroup - creating new group " + this.getOid().toString());
        }
        this.SetupVoiceGroup();
    }
    
    public OID GetGroupOid() {
        return this.getOid();
    }
    
    public AgisGroupMember AddGroupMember(final CombatInfo combatInfo) {
        final AgisGroupMember newMember = new AgisGroupMember(combatInfo, this.getOid());
        this._groupMembers.put(newMember.getOid(), newMember);
        combatInfo.setGroupOid(this.GetGroupOid());
        combatInfo.setGroupMemberOid(newMember.getOid());
        GroupClient.SendGroupEventMessage(GroupClient.GroupEventType.JOINED, this, combatInfo.getOwnerOid());
        return newMember;
    }
    
    public void RemoveGroupMember(final CombatInfo combatInfo) {
        GroupClient.SendGroupEventMessage(GroupClient.GroupEventType.LEFT, this, combatInfo.getOwnerOid());
        this._groupMembers.remove(combatInfo.getGroupMemberOid());
        combatInfo.setGroupMemberOid(null);
        combatInfo.setGroupOid(null);
        if (this._groupMembers.size() == 0) {
            this.RemoveVoiceGroup();
        }
    }
    
    public Hashtable<OID, AgisGroupMember> GetGroupMembers() {
        return this._groupMembers;
    }
    
    public int GetNumGroupMembers() {
        return this._groupMembers.size();
    }
    
    public OID GetGroupLeaderOid() {
        return this._groupLeaderOid;
    }
    
    public AgisGroupMember GetGroupMember(final OID groupMemberOid) {
        for (final AgisGroupMember groupMember : this._groupMembers.values()) {
            if (groupMember.GetGroupMemberOid().equals((Object)groupMemberOid)) {
                return groupMember;
            }
        }
        return null;
    }
    
    public void SetGroupLeaderOid(final OID value) {
        this._groupLeaderOid = value;
        if (value != null) {
            GroupClient.SendGroupEventMessage(GroupClient.GroupEventType.LEADERCHANGED, this, value);
        }
        if (this._groupMembers.size() == 1) {
            final List<AgisGroupMember> groupMembers = new ArrayList<AgisGroupMember>(this._groupMembers.values());
            this._groupLeaderOid = groupMembers.get(0).GetGroupMemberOid();
        }
    }
    
    protected void SetupVoiceGroup() {
        int error = 0;
        error = VoiceClient.addVoiceGroup(this.GetGroupOid(), false, 4);
        if (error != 1) {
            Log.error("AgisGroup.SetupGroupVoice : Create Voice Group Response - " + VoiceClient.errorString(error));
        }
    }
    
    public void RemoveVoiceGroup() {
        int error = 0;
        error = VoiceClient.removeVoiceGroup(this.GetGroupOid());
        if (error != 1) {
            Log.error("AgisGroup.RemoveVoiceGroup : Remove Voice Group Response - " + VoiceClient.errorString(error));
        }
    }
    
    public void SetGroupMuted(final Boolean value) {
        this._groupMuted = value;
    }
    
    public Boolean GetGroupMuted() {
        return this._groupMuted;
    }
}
