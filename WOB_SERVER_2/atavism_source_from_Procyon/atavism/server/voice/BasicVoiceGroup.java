// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import java.util.Iterator;
import atavism.server.network.AOByteBuffer;
import java.util.Collection;
import java.util.List;
import java.io.Serializable;
import atavism.server.plugins.VoiceClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import java.util.HashMap;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.Set;
import atavism.server.engine.OID;

public abstract class BasicVoiceGroup implements VoiceGroup
{
    protected OID groupOid;
    protected Object association;
    protected VoiceSender voiceSender;
    protected int maxVoices;
    public int defaultPriority;
    protected Set<OID> allowedMembers;
    protected Map<OID, GroupMember> members;
    protected static boolean loggingRecomputeVoices;
    protected transient Lock lock;
    
    public BasicVoiceGroup(final OID groupOid, final Object association, final VoiceSender voiceSender, final int maxVoices) {
        this.voiceSender = null;
        this.defaultPriority = 0;
        this.allowedMembers = null;
        this.lock = LockFactory.makeLock("BasicVoiceGroup");
        this.groupOid = groupOid;
        this.association = association;
        this.voiceSender = voiceSender;
        this.maxVoices = maxVoices;
        this.members = new HashMap<OID, GroupMember>();
    }
    
    @Override
    public OID getGroupOid() {
        return this.groupOid;
    }
    
    @Override
    public boolean addMemberAllowed(final OID memberOid) {
        if (memberOid == null) {
            if (Log.loggingDebug) {
                Log.debug("BasicVoiceGroup.addMemberAllowed: memberOid is null, so member not allowed");
            }
            return false;
        }
        if (this.allowedMembers != null) {
            final boolean allowed = this.allowedMembers.contains(memberOid);
            if (!allowed && Log.loggingDebug) {
                Log.debug("BasicVoiceGroup.addMemberAllowed: allowedMembers does not contain memberOid " + memberOid + ", so member not allowed");
            }
            return allowed;
        }
        return true;
    }
    
    @Override
    public GroupMember addMember(final OID memberOid, final VoiceConnection memberCon) {
        return this.addMember(memberOid, memberCon, this.getDefaultPriority(), true);
    }
    
    @Override
    public void setAllowedMembers(final Set<OID> allowedMembers) {
        this.allowedMembers = allowedMembers;
    }
    
    @Override
    public Set<OID> getAllowedMembers() {
        return this.allowedMembers;
    }
    
    @Override
    public abstract GroupMember addMember(final OID p0, final VoiceConnection p1, final int p2, final boolean p3);
    
    @Override
    public void onAfterAddMember(final OID memberOid, final OID groupOid, final boolean allowedSpeaker, final byte micVoiceNumber, final boolean listenToYourself) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage();
        msg.setMsgType(VoiceClient.MSG_TYPE_VOICE_MEMBER_ADDED);
        msg.setProperty("memberOid", memberOid);
        msg.setProperty("groupOid", groupOid);
        msg.setProperty("allowedSpeaker", allowedSpeaker);
        msg.setProperty("micVoiceNumber", (int)micVoiceNumber);
        msg.setProperty("listenToYourself", listenToYourself);
        this.voiceSender.sendExtensionMessage(msg);
    }
    
    @Override
    public abstract boolean isPositional();
    
    protected abstract void changeSpeaking(final GroupMember p0, final boolean p1);
    
    protected abstract void changeListening(final GroupMember p0, final boolean p1);
    
    protected abstract void recomputeListenerVoices(final GroupMember p0);
    
    @Override
    public boolean removeMember(final OID memberOid) {
        if (Log.loggingDebug) {
            Log.debug("BasicVoiceGroup.removeMember: For group " + this.groupOid + ", called to remove member " + memberOid);
        }
        this.lock.lock();
        try {
            final GroupMember member = this.isMember(memberOid);
            if (member != null) {
                if (member.allowedSpeaker) {
                    this.setAllowedSpeaker(member, false);
                }
                if (member.listening) {
                    this.setListener(member, false);
                }
                if (this.members.remove(memberOid) == null) {
                    Log.error("BasicVoiceGroup.removeMember: For group " + this.groupOid + ", didn't find member " + memberOid + " in member map!");
                }
                else if (Log.loggingDebug) {
                    Log.debug("BasicVoiceGroup.removeMember: For group " + this.groupOid + ", removed member " + memberOid);
                }
            }
            else {
                Log.info("BasicVoiceGroup.removeMember: For group " + this.groupOid + ", member " + memberOid + " not found!");
            }
            if (member != null) {
                this.onAfterRemoveMember(memberOid, this.groupOid, member.allowedSpeaker);
            }
            return member != null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void onAfterRemoveMember(final OID memberOid, final OID groupOid, final boolean allowedSpeaker) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage();
        msg.setMsgType(VoiceClient.MSG_TYPE_VOICE_MEMBER_REMOVED);
        msg.setProperty("memberOid", memberOid);
        msg.setProperty("groupOid", groupOid);
        msg.setProperty("allowedSpeaker", allowedSpeaker);
        this.voiceSender.sendExtensionMessage(msg);
    }
    
    @Override
    public GroupMember isMember(final OID memberOid) {
        return this.members.get(memberOid);
    }
    
    @Override
    public int getDefaultPriority() {
        return this.defaultPriority;
    }
    
    @Override
    public void setAllowedSpeaker(final OID memberOid, final boolean add) {
        this.lock.lock();
        try {
            final GroupMember member = this.getMember(memberOid);
            if (member != null) {
                this.setAllowedSpeaker(member, add);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected void setAllowedSpeaker(final GroupMember member, final boolean add) {
        this.lock.lock();
        try {
            if (member.allowedSpeaker == add) {
                Log.error("BasicVoiceGroup.setAllowedSpeaker: Group " + this.groupOid + " member " + member.memberOid + ", add " + add + ".  Condition already true!");
            }
            else if (add) {
                member.allowedSpeaker = true;
            }
            else {
                if (member.currentSpeaker) {
                    this.changeSpeaking(member, false);
                }
                member.allowedSpeaker = false;
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean isAllowedSpeaker(final OID memberOid) {
        final GroupMember member = this.getMember(memberOid);
        return member != null && member.allowedSpeaker;
    }
    
    @Override
    public void setMemberSpeaking(final OID memberOid, final boolean add) {
        this.lock.lock();
        try {
            final GroupMember member = this.getMember(memberOid);
            if (member == null) {
                Log.error("BasicVoiceGroup.setMemberSpeaking: memberOid " + memberOid + ", add " + add + " could not be found in group " + this.groupOid);
            }
            else if (member.allowedSpeaker && member.currentSpeaker == add) {
                Log.dumpStack("BasicVoiceGroup.setMemberSpeaking: Group " + this.groupOid + " member " + member.memberOid + ", add " + add + ".  Condition already true!");
            }
            else if (add) {
                if (member.allowedSpeaker) {
                    this.changeSpeaking(member, true);
                }
            }
            else {
                this.changeSpeaking(member, false);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean isMemberSpeaking(final OID memberOid) {
        final GroupMember member = this.getMember(memberOid);
        return member != null && member.currentSpeaker;
    }
    
    @Override
    public boolean isListener(final OID memberOid) {
        final GroupMember member = this.getMember(memberOid);
        return member != null && member.listening;
    }
    
    @Override
    public void setListener(final OID memberOid, final boolean add) {
        this.lock.lock();
        try {
            final GroupMember member = this.getMember(memberOid);
            if (member != null) {
                this.setListener(member, add);
            }
            else {
                Log.error("BasicVoiceGroup.setListener: Group " + this.groupOid + " member " + memberOid + ", could not find member!");
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setListener(final GroupMember member, final boolean add) {
        this.lock.lock();
        try {
            if (member.listening == add) {
                Log.error("BasicVoiceGroup.setListener: Group " + this.groupOid + " member " + member.memberOid + ", add " + add + ".  Condition already true!");
            }
            else {
                this.changeListening(member, member.listening = add);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected void endListeningToSpeaker(final GroupMember speaker, final GroupMember listener, final byte voiceNumber) {
        listener.setSpeakerForVoiceNumber(voiceNumber, null);
        if (Log.loggingDebug) {
            Log.debug("BasicVoiceGroup.endListeningToSpeaker: Sending dealloc of speaker " + speaker + " to listener " + listener + ", voiceNumber " + voiceNumber);
        }
        this.voiceSender.sendDeallocateVoice(speaker.memberCon, listener.memberCon, voiceNumber);
    }
    
    protected boolean eligibleSpeakerListenerPair(final GroupMember speaker, final GroupMember listener) {
        final boolean sameMember = speaker == listener;
        final boolean sameOid = speaker.getMemberOid() == listener.getMemberOid();
        if (sameOid && !sameMember) {
            Log.warn("BasicVoiceGroup.eligibleSpeakerListenerPair: Speaker and listener both have memberOid " + speaker.getMemberOid() + " but they are not the same object. speaker.expunged " + speaker.getExpunged() + ", listener.expunged " + listener.getExpunged());
        }
        return !listener.speakerIgnored(speaker) && (!sameOid || speaker.memberCon.listenToYourself);
    }
    
    protected GroupMember getMember(final OID oid) {
        final GroupMember member = this.members.get(oid);
        if (member == null) {
            return null;
        }
        return member;
    }
    
    @Override
    public void getAllMembers(final List<GroupMember> memberList) {
        memberList.addAll(this.members.values());
    }
    
    @Override
    public void sendVoiceFrameToListeners(final OID speakerOid, final AOByteBuffer buf, final byte opcode, final int pktSize) {
        this.lock.lock();
        try {
            final GroupMember speaker = this.getMember(speakerOid);
            if (speaker != null && speaker.allowedSpeaker) {
                final List<GroupMember> listenersToSpeaker = speaker.membersListeningToSpeaker();
                for (final GroupMember listener : listenersToSpeaker) {
                    if (this.eligibleSpeakerListenerPair(speaker, listener)) {
                        final Byte voiceNumber = listener.findVoiceNumberForSpeaker(speaker);
                        if (voiceNumber == null) {
                            Log.error("PositionalVoiceGroup.sendVoiceFrameToListeners: Voice number for speaker " + speaker + " and listener " + listener + " is null!");
                        }
                        else {
                            this.voiceSender.sendVoiceFrame(speaker.memberCon, listener.memberCon, opcode, voiceNumber, buf, (short)pktSize);
                        }
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected void recomputeVoicesFromSpeakerIterator(final GroupMember listener, final Iterator<GroupMember> memberIterator, final int count) {
        if (!listener.listening) {
            for (byte i = 0; i < this.maxVoices; ++i) {
                final GroupMember speaker = listener.getSpeakerForVoiceNumber(i);
                if (speaker != null && this.eligibleSpeakerListenerPair(speaker, listener)) {
                    this.endListeningToSpeaker(speaker, listener, i);
                }
            }
            if (Log.loggingDebug) {
                Log.debug("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: Returning because !listener.listening");
            }
            return;
        }
        final GroupMember[] newVoiceNumberToMember = new GroupMember[this.maxVoices];
        byte priorityCount;
        for (priorityCount = 0; priorityCount < count && memberIterator.hasNext(); ++priorityCount) {
            final GroupMember speaker2 = memberIterator.next();
            if (BasicVoiceGroup.loggingRecomputeVoices && Log.loggingDebug) {
                Log.debug("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: In while loop; priorityCount " + priorityCount + ", speaker " + speaker2 + ", listener " + listener);
            }
            if (this.eligibleSpeakerListenerPair(speaker2, listener)) {
                if (BasicVoiceGroup.loggingRecomputeVoices && Log.loggingDebug) {
                    Log.debug("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: Eligible!: equal " + (speaker2 == listener) + ", speaker listenToYourself " + speaker2.memberCon.listenToYourself);
                }
                newVoiceNumberToMember[priorityCount] = speaker2;
                speaker2.priorityIndex = priorityCount;
            }
        }
        int deallocCount = 0;
        for (byte voiceNumber = 0; voiceNumber < this.maxVoices; ++voiceNumber) {
            final GroupMember speaker3 = listener.getSpeakerForVoiceNumber(voiceNumber);
            if (speaker3 != null && speaker3.priorityIndex == -1 && this.eligibleSpeakerListenerPair(speaker3, listener)) {
                this.endListeningToSpeaker(speaker3, listener, voiceNumber);
                ++deallocCount;
            }
        }
        if (Log.loggingDebug) {
            Log.debug("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: listener " + listener + ", count " + count + ", deallocCount " + deallocCount + ", priorityCount " + priorityCount);
        }
        for (int j = 0; j < priorityCount; ++j) {
            final GroupMember speaker3 = newVoiceNumberToMember[j];
            if (speaker3 == null) {
                Log.error("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: speaker newVoiceNumberToMember[" + j + "] is null!");
            }
            else {
                speaker3.priorityIndex = -1;
                if (BasicVoiceGroup.loggingRecomputeVoices && Log.loggingDebug) {
                    Log.debug("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: listener " + listener + ", speaker[" + j + "] " + speaker3);
                }
                if (listener.findVoiceNumberForSpeaker(speaker3) == null) {
                    final Byte voiceNumber2 = listener.findFreeVoiceNumber();
                    if (BasicVoiceGroup.loggingRecomputeVoices && Log.loggingDebug) {
                        Log.debug("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: For speaker " + speaker3 + ", found voiceNumber " + voiceNumber2);
                    }
                    if (voiceNumber2 != null) {
                        listener.setSpeakerForVoiceNumber(voiceNumber2, speaker3);
                        this.voiceSender.sendAllocateVoice(speaker3.memberCon, listener.memberCon, voiceNumber2, this.isPositional());
                    }
                    else {
                        Log.error("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: In listener " + listener + ", didn't find unused voiceNumber for speaker " + speaker3.memberCon);
                    }
                }
            }
        }
        if (BasicVoiceGroup.loggingRecomputeVoices && Log.loggingDebug) {
            Log.debug("BasicVoiceGroup.recomputeVoicesFromSpeakerIterator: Exiting for listener " + listener);
        }
    }
    
    protected String addString(final boolean add) {
        return add ? "start" : "stop";
    }
    
    @Override
    public String toString() {
        return "group[oid " + this.groupOid + "]";
    }
    
    static {
        BasicVoiceGroup.loggingRecomputeVoices = false;
    }
}
