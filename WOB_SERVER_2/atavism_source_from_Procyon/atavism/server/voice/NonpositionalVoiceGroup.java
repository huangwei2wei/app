// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import java.util.Collections;
import java.util.Iterator;
import atavism.server.util.Log;
import java.util.LinkedList;
import java.util.HashSet;
import atavism.server.engine.OID;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class NonpositionalVoiceGroup extends BasicVoiceGroup
{
    protected Set<GroupMember> listeners;
    private List<GroupMember> currentSpeakers;
    private Comparator<GroupMember> comparePriorities;
    
    public NonpositionalVoiceGroup(final OID groupOid, final Object association, final VoiceSender voiceSender, final int maxVoices) {
        super(groupOid, association, voiceSender, maxVoices);
        this.listeners = new HashSet<GroupMember>();
        this.comparePriorities = new ComparePriorities();
        this.currentSpeakers = new LinkedList<GroupMember>();
    }
    
    @Override
    public GroupMember addMember(final OID memberOid, final VoiceConnection memberCon, final int priority, final boolean allowedSpeaker) {
        this.lock.lock();
        try {
            GroupMember member = this.members.get(memberOid);
            if (member != null) {
                Log.dumpStack("NonpositionalVoiceGroup.addMember: Member " + memberOid + " is already a member of voice group " + this.groupOid);
            }
            else {
                member = new GroupMember(this, memberOid, priority, allowedSpeaker, false, memberCon, this.maxVoices);
                this.members.put(memberOid, member);
            }
            this.onAfterAddMember(memberOid, this.groupOid, allowedSpeaker, memberCon.micVoiceNumber, memberCon.listenToYourself);
            return member;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean isPositional() {
        return false;
    }
    
    @Override
    public boolean isAllowedSpeaker(final OID memberOid) {
        final GroupMember member = this.getMember(memberOid);
        return member != null && member.allowedSpeaker;
    }
    
    @Override
    protected void changeSpeaking(final GroupMember speaker, final boolean add) {
        if (Log.loggingDebug) {
            Log.debug("NonpositionalVoiceGroup.changeSpeaking entering " + this.addString(add) + ": listeners.size() " + this.listeners.size() + ", speaker " + speaker + ", speaker.voiceNumber " + speaker.voiceNumber);
        }
        this.lock.lock();
        try {
            speaker.currentSpeaker = add;
            if (add) {
                if (!this.currentSpeakers.add(speaker)) {
                    Log.error("NonpositionalVoiceGroup.changeSpeaking start: currentSpeakers already contains speaker " + speaker);
                }
            }
            else if (!this.currentSpeakers.remove(speaker)) {
                Log.error("NonpositionalVoiceGroup.changeSpeaking stop: currentSpeakers doesn't contain speaker " + speaker);
            }
            this.speakingStatusChanged();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    protected void changeListening(final GroupMember listener, final boolean add) {
        if (Log.loggingDebug) {
            Log.debug("NonpositionalVoiceGroup.changeListening " + this.addString(add) + ": listener " + listener);
        }
        this.lock.lock();
        try {
            if (add) {
                if (!this.listeners.add(listener)) {
                    Log.error("NonpositionalVoiceGroup.changeListening " + this.addString(add) + ": listener " + listener + " already in listeners");
                }
                this.recomputeListenerVoices(listener);
            }
            else {
                if (!this.listeners.remove(listener)) {
                    Log.error("NonpositionalVoiceGroup.changeListening " + this.addString(add) + ": listener " + listener + " not in listeners");
                }
                for (byte voiceNumber = 0; voiceNumber < this.maxVoices; ++voiceNumber) {
                    final GroupMember speaker = listener.getSpeakerForVoiceNumber(voiceNumber);
                    if (speaker != null) {
                        this.endListeningToSpeaker(speaker, listener, voiceNumber);
                    }
                }
                if (listener.voiceCount() > 0) {
                    Log.dumpStack("NonpositionalVoiceGroup.changeListening stop: After removing, listener.voiceCount() " + listener.voiceCount());
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void recomputeListenerVoices(final GroupMember listener) {
        this.recomputeListenerVoices(listener, this.currentSpeakers);
    }
    
    protected void recomputeListenerVoices(final GroupMember listener, final List<GroupMember> membersToConsider) {
        final Iterator<GroupMember> speakerIter = membersToConsider.iterator();
        final int speakerCount = membersToConsider.size();
        final int iterCount = Math.min(speakerCount, this.maxVoices);
        this.recomputeVoicesFromSpeakerIterator(listener, speakerIter, iterCount);
    }
    
    protected void speakingStatusChanged() {
        this.lock.lock();
        try {
            Collections.sort(this.currentSpeakers, this.comparePriorities);
        }
        finally {
            this.lock.unlock();
        }
        for (final GroupMember listener : this.listeners) {
            this.recomputeListenerVoices(listener);
        }
    }
    
    public static class ComparePriorities implements Comparator<GroupMember>
    {
        @Override
        public int compare(final GroupMember m1, final GroupMember m2) {
            if (m1 == m2) {
                return 0;
            }
            if (m1.priority < m2.priority) {
                return -1;
            }
            if (m1.priority > m2.priority) {
                return 1;
            }
            return (m1.index < m2.index) ? -1 : 1;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other;
        }
    }
}
