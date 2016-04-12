// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.util.Iterator;
import atavism.server.engine.EnginePlugin;
import atavism.agis.plugins.GroupPlugin;
import atavism.server.plugins.WorldManagerClient;
import java.util.HashMap;
import atavism.server.util.Logger;
import java.io.Serializable;
import java.util.Map;
import atavism.server.engine.OID;
import atavism.server.objects.Entity;

public class AgisGroupMember extends Entity
{
    private static final long serialVersionUID = 1L;
    private OID _groupMemberOid;
    private OID _groupOid;
    private String _groupMemberName;
    private Boolean _voiceEnabled;
    private Boolean _allowedSpeaker;
    private Map<String, Serializable> _entryStats;
    private static final Logger _log;
    
    static {
        _log = new Logger("GroupMember");
    }
    
    public AgisGroupMember(final CombatInfo combatInfo, final OID groupOid) {
        super("");
        this._voiceEnabled = Boolean.FALSE;
        this._allowedSpeaker = Boolean.TRUE;
        this._entryStats = new HashMap<String, Serializable>();
        this._groupMemberOid = combatInfo.getOwnerOid();
        this._groupMemberName = WorldManagerClient.getObjectInfo(combatInfo.getOwnerOid()).name;
        this._groupOid = groupOid;
        this.SetGroupMemberStats(combatInfo);
    }
    
    public OID GetGroupMemberOid() {
        return this._groupMemberOid;
    }
    
    public String GetGroupMemberName() {
        return this._groupMemberName;
    }
    
    public OID GetGroupOid() {
        return this._groupOid;
    }
    
    protected void SetGroupMemberStats(final CombatInfo combatInfo) {
        for (final String stat : GroupPlugin.GetRegisteredStats()) {
            final int statVal = (int)EnginePlugin.getObjectProperty(this._groupMemberOid, WorldManagerClient.NAMESPACE, stat);
            this._entryStats.put(stat, statVal);
        }
    }
    
    public Serializable GetGroupMemberStat(final String stat) {
        if (Log.loggingDebug) {
            AgisGroupMember._log.debug("AgisGroup.GetGroupMemberStat : " + stat + " = " + this._entryStats.get(stat));
        }
        return this._entryStats.get(stat);
    }
    
    public void SetVoiceEnabled(final Boolean value) {
        this._voiceEnabled = value;
    }
    
    public Boolean GetVoiceEnabled() {
        return this._voiceEnabled;
    }
    
    public void SetAllowedSpeaker(final Boolean value) {
        this._allowedSpeaker = value;
    }
    
    public Boolean GetAllowedSpeaker() {
        return this._allowedSpeaker;
    }
}
