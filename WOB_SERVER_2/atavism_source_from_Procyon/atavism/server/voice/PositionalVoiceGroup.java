// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import java.util.HashSet;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedList;
import atavism.server.math.Point;
import atavism.server.engine.BasicWorldNode;
import java.util.Iterator;
import java.util.List;
import atavism.server.util.Log;
import java.util.HashMap;
import java.util.Set;
import atavism.server.engine.OID;
import java.util.Map;

public class PositionalVoiceGroup extends BasicVoiceGroup
{
    protected float audibleRadius;
    protected float hystericalMargin;
    private Map<OID, Set<PositionalGroupMember>> instanceMembers;
    
    public PositionalVoiceGroup(final OID groupOid, final Object association, final VoiceSender voiceSender, final int maxVoices, final float audibleRadius, final float hystericalMargin) {
        super(groupOid, association, voiceSender, maxVoices);
        this.instanceMembers = new HashMap<OID, Set<PositionalGroupMember>>();
        this.audibleRadius = audibleRadius;
        this.hystericalMargin = hystericalMargin;
    }
    
    @Override
    public GroupMember addMember(final OID memberOid, final VoiceConnection memberCon, final int priority, final boolean allowedSpeaker) {
        this.lock.lock();
        try {
            GroupMember member = this.members.get(memberOid);
            if (member != null) {
                Log.dumpStack("PositionalVoiceGroup.addMember: Member " + memberOid + " is already a member of voice group " + this.groupOid);
            }
            else {
                member = new PositionalGroupMember(this, memberOid, priority, allowedSpeaker, false, memberCon, this.maxVoices);
                this.members.put(memberOid, member);
                if (Log.loggingDebug) {
                    Log.debug("PositionalVoiceGroup.addMember: For group " + this.groupOid + ", adding member " + memberOid);
                }
            }
            this.onAfterAddMember(memberOid, this.groupOid, allowedSpeaker, memberCon.micVoiceNumber, memberCon.listenToYourself);
            return member;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    protected void changeSpeaking(final GroupMember gspeaker, final boolean add) {
        final PositionalGroupMember speaker = (PositionalGroupMember)gspeaker;
        if (Log.loggingDebug) {
            Log.debug("PositionalVoiceGroup.changeSpeaking " + this.addString(add) + ": speaker " + speaker);
        }
        this.lock.lock();
        try {
            speaker.currentSpeaker = add;
            if (add) {
                this.recomputeListenersInRadius(speaker);
            }
            else {
                final List<GroupMember> listenersToSpeaker = speaker.membersListeningToSpeaker();
                for (final GroupMember listener : listenersToSpeaker) {
                    final Byte voiceNumber = listener.findVoiceNumberForSpeaker(speaker);
                    if (Log.loggingDebug) {
                        Log.debug("PositionalVoiceGroup.changeSpeaking " + this.addString(add) + ": listeners cnt " + listenersToSpeaker.size() + ", speaker " + speaker + ", voiceNumber " + voiceNumber + ", listener " + listener);
                    }
                    if (voiceNumber == null) {
                        Log.error("PositionalVoiceGroup.changeSpeaking " + this.addString(add) + ": Voice number for speaker " + speaker + " and listener " + listener + " is null!");
                    }
                    else {
                        this.recomputeListenerVoices(listener);
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    protected void changeListening(final GroupMember listener, final boolean add) {
        if (Log.loggingDebug) {
            Log.debug("PositionalVoiceGroup.changeListening " + this.addString(add) + ": listener " + listener);
        }
        this.lock.lock();
        try {
            if (add) {
                this.recomputeListenerVoices(listener);
            }
            else {
                for (byte voiceNumber = 0; voiceNumber < this.maxVoices; ++voiceNumber) {
                    final GroupMember speaker = listener.getSpeakerForVoiceNumber(voiceNumber);
                    if (speaker != null) {
                        this.endListeningToSpeaker(speaker, listener, voiceNumber);
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void updateWorldNode(final PositionalGroupMember perceiverMember, final BasicWorldNode bwnode) {
        if (perceiverMember.wnode != null) {
            perceiverMember.previousLoc = perceiverMember.lastLoc;
            perceiverMember.wnode.setDirLocOrient(bwnode);
            perceiverMember.wnode.setInstanceOid(bwnode.getInstanceOid());
            perceiverMember.lastLoc = perceiverMember.wnode.getLoc();
            for (final OID perceivedOid : perceiverMember.perceivedOids) {
                if (perceiverMember.memberOid.equals(perceivedOid)) {
                    continue;
                }
                final PositionalGroupMember perceivedMember = (PositionalGroupMember)this.getMember(perceivedOid);
                if (perceivedMember == null || perceivedMember.wnode == null) {
                    continue;
                }
                this.testProximity(perceiverMember, perceivedMember, false, true);
            }
        }
        else {
            Log.error("PositionalVoiceGroup.updateWorldNode: In UpdateWorldNodeMessage for oid " + perceiverMember.memberOid + ", perceiverMember.wnode is null!");
        }
    }
    
    public void testProximity(final PositionalGroupMember perceiverMember, final PositionalGroupMember perceivedMember, final boolean interpolatePerceiver, final boolean interpolatePerceived) {
        final Point perceiverLoc = interpolatePerceiver ? perceiverMember.wnode.getLoc() : perceiverMember.lastLoc;
        final Point perceivedLoc = interpolatePerceived ? perceivedMember.wnode.getLoc() : perceivedMember.lastLoc;
        if (perceiverLoc == null) {
            Log.dumpStack("PositionalVoiceGroup.testProximity: perceiver " + perceiverMember.getMemberOid() + " loc is null!");
            return;
        }
        if (perceivedLoc == null) {
            Log.dumpStack("PositionalVoiceGroup.testProximity: perceived " + perceivedMember.getMemberOid() + " loc is null!");
            return;
        }
        final float distance = Point.distanceTo(perceiverLoc, perceivedLoc);
        final OID perceiverInstance = perceiverMember.wnode.getInstanceOid();
        final OID perceivedInstance = perceivedMember.wnode.getInstanceOid();
        final boolean sameInstance = perceiverInstance.equals(perceivedInstance);
        boolean inRadius = sameInstance && distance < this.audibleRadius;
        final boolean wasInRadius = perceiverMember.membersInRadius.contains(perceivedMember);
        if (Log.loggingDebug) {
            Log.debug("PositionalVoiceGroup.testProximity: perceiver " + perceiverMember.getMemberOid() + ", perceiverLoc = " + perceiverLoc + ", perceived " + perceivedMember.getMemberOid() + ", perceivedLoc = " + perceivedLoc + ", distance " + distance + ", audibleRadius " + this.audibleRadius + ", perceiverInstance " + perceiverInstance + ", perceivedInstance " + perceivedInstance + ", inRadius " + inRadius + ", wasInRadius " + wasInRadius);
        }
        if (inRadius == wasInRadius) {
            return;
        }
        if (sameInstance && this.hystericalMargin != 0.0f) {
            if (wasInRadius) {
                inRadius = (distance < this.audibleRadius + this.hystericalMargin);
            }
            else {
                inRadius = (distance < this.audibleRadius - this.hystericalMargin);
            }
            if (inRadius == wasInRadius) {
                return;
            }
        }
        this.handlePositionalSpeakerChange(perceiverMember, perceivedMember, inRadius);
        this.handlePositionalSpeakerChange(perceivedMember, perceiverMember, inRadius);
    }
    
    public void handlePositionalSpeakerChange(final PositionalGroupMember speaker, final PositionalGroupMember listener, final boolean inRadius) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                Log.debug("PositionalVoiceGroup.handlePositionalSpeakerChange: speakerOid " + speaker.memberOid + ", speaker " + speaker + ", listenerOid " + listener.memberOid + ", listeneer " + listener + ", inRadius " + inRadius);
            }
            if (inRadius && !listener.speakerIgnored(speaker)) {
                this.addSpeakerListenerPair(speaker, listener);
            }
            else {
                this.removeSpeakerListenerPair(speaker, listener);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean isPositional() {
        return true;
    }
    
    protected void addSpeakerListenerPair(final PositionalGroupMember speaker, final PositionalGroupMember listener) {
        final Set<PositionalGroupMember> membersInRadius = listener.membersInRadius;
        if (Log.loggingDebug) {
            Log.debug("PositionalVoiceGroup.addSpeakerListenerPair: speaker + " + speaker + ", listener " + listener + ", membersInRadius.size() " + membersInRadius.size());
        }
        if (speaker.getExpunged()) {
            Log.warn("PositionalVoiceGroup.addSpeakerListenerPair: For listener " + listener.getMemberOid() + ", speaker " + speaker.getMemberOid() + " is expunged!");
            return;
        }
        if (listener.getExpunged()) {
            Log.warn("PositionalVoiceGroup.addSpeakerListenerPair: For speaker " + speaker.getMemberOid() + ", listener " + listener.getMemberOid() + " is expunged!");
            return;
        }
        if (!membersInRadius.add(speaker)) {
            if (Log.loggingDebug) {
                Log.debug("PositionalVoiceGroup.addSpeakerListenerPair: listener " + listener + " already in membersInRadiusOfSpeakerMap for speaker " + speaker);
            }
        }
        else {
            this.recomputeListenerVoices(listener);
        }
    }
    
    protected void removeSpeakerListenerPair(final PositionalGroupMember speaker, final PositionalGroupMember listener) {
        final Set<PositionalGroupMember> membersInRadius = listener.membersInRadius;
        boolean found = false;
        if (membersInRadius != null && membersInRadius.remove(speaker)) {
            found = true;
        }
        if (!found) {
            if (Log.loggingDebug) {
                Log.debug("PositionalVoiceGroup.removeSpeakerListenerPair: listener " + listener + " is not in membersInRadiusOfSpeakerMap for speaker " + speaker);
            }
        }
        else if (Log.loggingDebug) {
            Log.debug("PositionalVoiceGroup.removeSpeakerListenerPair: listener " + listener + " removed from membersInRadius of speaker " + speaker);
        }
        this.recomputeListenerVoices(listener);
    }
    
    protected void recomputeListenersInRadius(final PositionalGroupMember speaker) {
        for (final PositionalGroupMember listener : speaker.membersInRadius) {
            if (listener.listening) {
                this.recomputeListenerVoices(listener);
            }
        }
    }
    
    @Override
    protected void recomputeListenerVoices(final GroupMember glistener) {
        final PositionalGroupMember listener = (PositionalGroupMember)glistener;
        List<PositionalGroupMember> expungedMembers = null;
        for (final PositionalGroupMember member : listener.membersInRadius) {
            if (member.getExpunged()) {
                if (expungedMembers == null) {
                    expungedMembers = new LinkedList<PositionalGroupMember>();
                }
                if (Log.loggingDebug) {
                    Log.debug("PositionalVoiceGroup:recomputeListenerVoices: listener " + listener.getMemberOid() + " memberInRadius " + member.getMemberOid() + " is expunged; removing.");
                }
                expungedMembers.add(member);
            }
        }
        if (expungedMembers != null) {
            listener.membersInRadius.removeAll(expungedMembers);
        }
        this.recomputeListenerVoices(listener, listener.membersInRadius);
    }
    
    protected void recomputeListenerVoices(final PositionalGroupMember listener, final Set<PositionalGroupMember> membersToConsider) {
        if (Log.loggingDebug) {
            Log.debug("PositionalVoiceGroup.recomputeListenerVoices: listener " + listener + ", membersToConsider.size() " + membersToConsider.size());
        }
        final List<GroupMember> currentSpeakersForListener = new LinkedList<GroupMember>();
        for (final PositionalGroupMember speaker : membersToConsider) {
            if (speaker.currentSpeaker) {
                currentSpeakersForListener.add(speaker);
            }
        }
        Collections.sort(currentSpeakersForListener, new CompareLocations(listener.getCurrentLoc()));
        final Iterator<GroupMember> speakerIter = currentSpeakersForListener.iterator();
        final int speakerCount = currentSpeakersForListener.size();
        final int iterCount = Math.min(speakerCount, this.maxVoices);
        this.recomputeVoicesFromSpeakerIterator(listener, speakerIter, iterCount);
    }
    
    public byte addListenerVoice(final PositionalGroupMember speaker, final PositionalGroupMember listener) {
        if (Log.loggingDebug) {
            Log.debug("PositionalGroupMember.addListenerVoice:  speaker " + speaker + ", listener " + listener);
        }
        this.lock.lock();
        try {
            final Byte voiceNumber = listener.findFreeVoiceNumber();
            if (voiceNumber == null) {
                Log.error("PositionalGroupMember.addListenerVoice: Too many voices allocating voice for member " + listener);
                return -1;
            }
            listener.setSpeakerForVoiceNumber(voiceNumber, speaker);
            return voiceNumber;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<GroupMember> membersListeningToSpeaker(final PositionalGroupMember speaker) {
        this.lock.lock();
        try {
            return speaker.membersListeningToSpeaker();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean nowListeningTo(final PositionalGroupMember speaker, final PositionalGroupMember listener) {
        this.lock.lock();
        try {
            return listener.nowListeningTo(speaker);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void unloadInstance(final OID instanceOid) {
        this.lock.lock();
        try {
            final Set<PositionalGroupMember> members = this.instanceMembers.remove(instanceOid);
            if (members != null && members.size() > 0) {
                Log.warn("PositionalVoiceGroup.unloadInstance: Group " + this.groupOid + " in instance " + instanceOid + ", has active members " + this.makeOidStringFromMembers(members));
                for (final PositionalGroupMember member : members) {
                    this.clearMembersPerceived(member);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addTrackedPerceiver(final PositionalGroupMember perceiverMember, final OID instanceOid) {
        this.lock.lock();
        try {
            Set<PositionalGroupMember> members = this.instanceMembers.get(instanceOid);
            if (members == null) {
                members = new HashSet<PositionalGroupMember>();
                this.instanceMembers.put(instanceOid, members);
            }
            if (!members.add(perceiverMember)) {
                Log.error("PositionalVoiceGroup.addTrackedPerceiver: Member " + perceiverMember.getMemberOid() + " is already a member of group " + this.groupOid + ", instanceOid " + instanceOid);
            }
            else {
                this.clearMembersPerceived(perceiverMember);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected String makeOidString(final Collection<OID> oids) {
        String oidString = "";
        for (final OID oid : oids) {
            if (oidString.length() > 0) {
                oidString += ", ";
            }
            oidString += oid;
        }
        return oidString;
    }
    
    protected String makeOidStringFromMembers(final Collection<PositionalGroupMember> members) {
        String oidString = "";
        for (final PositionalGroupMember member : members) {
            if (oidString.length() > 0) {
                oidString += ", ";
            }
            oidString += member.getMemberOid();
        }
        return oidString;
    }
    
    protected void clearMembersPerceived(final PositionalGroupMember perceiverMember) {
        final boolean listeningToHimself = perceiverMember.membersInRadius.contains(perceiverMember);
        perceiverMember.perceivedOids.clear();
        perceiverMember.membersInRadius.clear();
        if (listeningToHimself) {
            perceiverMember.membersInRadius.add(perceiverMember);
        }
        this.recomputeListenerVoices(perceiverMember);
    }
    
    public void removeTrackedPerceiver(final OID playerOid) {
        final PositionalGroupMember perceiverMember = (PositionalGroupMember)this.isMember(playerOid);
        if (perceiverMember != null) {
            this.removeTrackedPerceiver(perceiverMember);
        }
        else {
            Log.error("PositionalVoiceGroup.removeTrackedPerceiver: Could not find member " + playerOid);
        }
    }
    
    public void removeTrackedPerceiver(final PositionalGroupMember perceiverMember) {
        this.lock.lock();
        try {
            final OID instanceOid = perceiverMember.getInstanceOid();
            if (instanceOid != null) {
                perceiverMember.wnode = null;
                perceiverMember.lastLoc = null;
                perceiverMember.previousLoc = null;
                final Set<PositionalGroupMember> members = this.instanceMembers.get(instanceOid);
                if (members == null) {
                    Log.error("PositionalVoiceGroup.removeTrackedPerceiver: For perceiver " + perceiverMember.getMemberOid() + ", instanceMembers.get(" + instanceOid + ") is null!");
                }
                else if (!members.remove(perceiverMember)) {
                    Log.error("PositionalVoiceGroup.removeTrackedPerceiver: Member " + perceiverMember.getMemberOid() + " is not a member of group " + this.groupOid + ", instanceOid " + instanceOid);
                }
                final OID perceiverOid = perceiverMember.getMemberOid();
                for (final OID perceivedOid : perceiverMember.perceivedOids) {
                    final PositionalGroupMember perceivedMember = (PositionalGroupMember)this.isMember(perceivedOid);
                    if (perceivedMember != null) {
                        perceivedMember.membersInRadius.remove(perceiverOid);
                        if (!perceivedMember.perceivedOids.remove(perceiverOid) && Log.loggingDebug) {
                            Log.debug("PositionalVoiceGroup.removeTrackedPerceiver: Member " + perceiverMember.getMemberOid() + " is not perceived by perceived member " + perceivedOid);
                        }
                        this.recomputeListenerVoices(perceivedMember);
                    }
                    else {
                        if (!Log.loggingDebug) {
                            continue;
                        }
                        Log.debug("PositionalVoiceGroup.removeTrackedPerceiver: Member " + perceivedOid + " could not be found!");
                    }
                }
                this.clearMembersPerceived(perceiverMember);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void maybeChangePerceivedObject(final PositionalGroupMember perceiverMember, final OID perceivedOid, final boolean added) {
        final OID perceiverOid = perceiverMember.getMemberOid();
        if (Log.loggingDebug) {
            Log.debug("PositionalVoiceGroup.maybeChangePerceivedObject: " + (added ? "gain" : "loss") + ", oid=" + perceivedOid + " detected by " + perceiverOid + ", instanceOid=" + perceiverMember.getInstanceOid());
        }
        this.lock.lock();
        try {
            if (added) {
                if (!perceiverMember.perceivedOids.add(perceivedOid)) {
                    Log.error("PositionalVoiceGroup.maybeChangePerceivedObject: Adding member " + perceivedOid + " for perceiver " + perceiverOid + "; already in perceivedOids");
                }
            }
            else if (!perceiverMember.perceivedOids.remove(perceivedOid)) {}
            final PositionalGroupMember perceivedMember = this.members.get(perceivedOid);
            if (perceiverMember.wnode != null && perceivedMember != null && perceivedMember.wnode != null) {
                perceivedMember.previousLoc = perceivedMember.lastLoc;
                perceivedMember.lastLoc = perceivedMember.wnode.getLoc();
                this.testProximity(perceiverMember, perceivedMember, true, false);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public static class CompareLocations implements Comparator<GroupMember>
    {
        private Point center;
        
        public CompareLocations(final Point center) {
            this.center = center;
        }
        
        @Override
        public int compare(final GroupMember m1, final GroupMember m2) {
            if (m1 == m2) {
                return 0;
            }
            final Point m1Loc = ((PositionalGroupMember)m1).getCurrentLoc();
            final Point m2Loc = ((PositionalGroupMember)m2).getCurrentLoc();
            if (m1Loc == null) {
                if (Log.loggingDebug) {
                    Log.debug("PositionalVoiceGroup.CompareLocations.compare: For member " + m1.getMemberOid() + ", currentLoc is null");
                }
                return -1;
            }
            if (m2Loc == null) {
                Log.debug("PositionalVoiceGroup.CompareLocations.compare: For member " + m2.getMemberOid() + ", currentLoc is null");
                return -1;
            }
            final float d1Squared = Point.distanceToSquared(this.center, m1Loc);
            final float d2Squared = Point.distanceToSquared(this.center, m2Loc);
            if (d1Squared < d2Squared) {
                return -1;
            }
            if (d1Squared > d2Squared) {
                return 1;
            }
            return (m1.index < m2.index) ? -1 : 1;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other;
        }
        
        public Point getCenter() {
            return this.center;
        }
        
        public void setCenter(final Point center) {
            this.center = center;
        }
    }
}
