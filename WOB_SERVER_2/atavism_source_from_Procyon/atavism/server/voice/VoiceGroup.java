// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import atavism.server.network.AOByteBuffer;
import java.util.List;
import java.util.Set;
import atavism.server.engine.OID;

public interface VoiceGroup
{
    boolean isPositional();
    
    OID getGroupOid();
    
    boolean addMemberAllowed(final OID p0);
    
    void setAllowedMembers(final Set<OID> p0);
    
    Set<OID> getAllowedMembers();
    
    GroupMember addMember(final OID p0, final VoiceConnection p1);
    
    void getAllMembers(final List<GroupMember> p0);
    
    GroupMember addMember(final OID p0, final VoiceConnection p1, final int p2, final boolean p3);
    
    void onAfterAddMember(final OID p0, final OID p1, final boolean p2, final byte p3, final boolean p4);
    
    GroupMember isMember(final OID p0);
    
    boolean removeMember(final OID p0);
    
    void onAfterRemoveMember(final OID p0, final OID p1, final boolean p2);
    
    int getDefaultPriority();
    
    void setAllowedSpeaker(final OID p0, final boolean p1);
    
    void setMemberSpeaking(final OID p0, final boolean p1);
    
    void setListener(final OID p0, final boolean p1);
    
    void sendVoiceFrameToListeners(final OID p0, final AOByteBuffer p1, final byte p2, final int p3);
    
    boolean isAllowedSpeaker(final OID p0);
    
    boolean isMemberSpeaking(final OID p0);
    
    boolean isListener(final OID p0);
}
