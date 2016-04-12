// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import java.util.HashSet;
import atavism.server.engine.OID;
import java.util.Set;
import atavism.server.math.Point;
import atavism.server.engine.InterpolatedWorldNode;

public class PositionalGroupMember extends GroupMember
{
    public InterpolatedWorldNode wnode;
    public Point lastLoc;
    public Point previousLoc;
    public Set<OID> perceivedOids;
    public Set<PositionalGroupMember> membersInRadius;
    
    public PositionalGroupMember(final VoiceGroup group, final OID memberOid, final int priority, final boolean allowedSpeaker, final boolean currentSpeaker, final VoiceConnection memberCon, final int maxVoiceChannels) {
        super(group, memberOid, priority, allowedSpeaker, currentSpeaker, memberCon, maxVoiceChannels);
        this.perceivedOids = new HashSet<OID>();
        this.membersInRadius = new HashSet<PositionalGroupMember>();
        if (memberCon.listenToYourself) {
            this.membersInRadius.add(this);
        }
    }
    
    public OID getInstanceOid() {
        if (this.wnode != null) {
            return this.wnode.getInstanceOid();
        }
        return null;
    }
    
    public Point getCurrentLoc() {
        if (this.wnode != null) {
            return this.wnode.getCurrentLoc();
        }
        return null;
    }
}
