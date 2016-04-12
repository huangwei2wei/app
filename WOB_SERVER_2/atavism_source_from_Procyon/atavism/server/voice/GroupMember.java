// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import atavism.server.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import atavism.server.plugins.WorldManagerClient;
import java.util.List;
import java.util.Map;
import java.util.Set;
import atavism.server.engine.OID;

public class GroupMember
{
    protected OID memberOid;
    protected int priority;
    protected boolean allowedSpeaker;
    protected boolean currentSpeaker;
    protected boolean listening;
    protected byte voiceNumber;
    protected VoiceConnection memberCon;
    protected int index;
    protected int priorityIndex;
    protected VoiceGroup group;
    protected static int indexCounter;
    private Set<GroupMember> listenersToMe;
    private Map<GroupMember, Byte> listenerVoicesMap;
    private Set<OID> ignoredSpeakerOids;
    private List<WorldManagerClient.ExtensionMessage> pendingIgnoreUpdateMessages;
    private GroupMember[] voiceNumberToSpeaker;
    protected boolean expunged;
    
    public GroupMember(final VoiceGroup group, final OID memberOid2, final int priority, final boolean allowedSpeaker, final boolean currentSpeaker, final VoiceConnection memberCon, final int maxVoiceChannels) {
        this.voiceNumber = -1;
        this.priorityIndex = -1;
        this.listenersToMe = new HashSet<GroupMember>();
        this.listenerVoicesMap = new HashMap<GroupMember, Byte>();
        this.ignoredSpeakerOids = null;
        this.expunged = false;
        this.group = group;
        this.memberOid = memberOid2;
        this.priority = priority;
        this.allowedSpeaker = allowedSpeaker;
        this.currentSpeaker = currentSpeaker;
        this.listening = false;
        this.memberCon = memberCon;
        this.index = GroupMember.indexCounter++;
        this.voiceNumberToSpeaker = new GroupMember[maxVoiceChannels];
        if (Log.loggingDebug) {
            Log.debug(this.logString("GroupMember constructor"));
        }
    }
    
    @Override
    public String toString() {
        return "GroupMember(oid " + this.memberOid + ", con " + this.memberCon + ")";
    }
    
    public String logString(final String intro) {
        return intro + ": oid " + this.memberOid + ", listenToYourself " + this.memberCon.listenToYourself + ", allowedSpeaker " + this.allowedSpeaker + ", currentSpeaker " + this.currentSpeaker;
    }
    
    public void setSpeakerForVoiceNumber(final byte voiceNumber, final GroupMember speaker) {
        if (speaker == null) {
            final GroupMember oldSpeaker = this.voiceNumberToSpeaker[voiceNumber];
            if (oldSpeaker == null) {
                Log.dumpStack("GroupMember.setSpeakerForVoiceNumber: Setting speaker to null for voiceNumber " + voiceNumber + ", but voiceNumberToSpeaker[voiceNumber] is already null!");
            }
            if (this.listenerVoicesMap.remove(oldSpeaker) == null) {
                Log.dumpStack("GroupMember.setSpeakerForVoiceNumber: Setting speaker to null for listener " + this + ", didn't find speaker " + oldSpeaker + " in listenerVoicesMap");
            }
            if (!oldSpeaker.listenersToMe.remove(this)) {
                Log.dumpStack("GroupMember.setSpeakerForVoiceNumber: For  speaker " + oldSpeaker + ", didn't find listener " + this + " in listenersToMe");
            }
        }
        else {
            final Byte oldVoiceNumber = this.listenerVoicesMap.put(speaker, voiceNumber);
            if (oldVoiceNumber != null) {
                Log.dumpStack("GroupMember.setSpeakerForVoiceNumber: For listener " + this + " and speaker, when adding voiceNumber " + voiceNumber + ", found " + oldVoiceNumber + " in listenerVoicesMap");
            }
            if (!speaker.listenersToMe.add(this)) {
                Log.dumpStack("GroupMember.setSpeakerForVoiceNumber: listener " + this + " was already in speaker " + speaker + " listenersToMe list!");
            }
            if (this.voiceNumberToSpeaker[voiceNumber] != null) {
                Log.dumpStack("GroupMember.setSpeakerForVoiceNumber: For  speaker " + speaker + ", voiceNumber " + voiceNumber + ", voiceNumberToSpeaker[voiceNumber] " + this.voiceNumberToSpeaker[voiceNumber] + " is non-null");
            }
        }
        this.voiceNumberToSpeaker[voiceNumber] = speaker;
    }
    
    public VoiceGroup getGroup() {
        return this.group;
    }
    
    public OID getGroupOid() {
        return this.group.getGroupOid();
    }
    
    public GroupMember getSpeakerForVoiceNumber(final byte voiceNumber) {
        return this.voiceNumberToSpeaker[voiceNumber];
    }
    
    public Byte findFreeVoiceNumber() {
        for (byte i = 0; i < this.voiceNumberToSpeaker.length; ++i) {
            if (this.voiceNumberToSpeaker[i] == null) {
                return i;
            }
        }
        return null;
    }
    
    public Byte findVoiceNumberForSpeaker(final GroupMember speaker) {
        return this.listenerVoicesMap.get(speaker);
    }
    
    public int voiceCount() {
        return this.listenerVoicesMap.size();
    }
    
    public boolean nowListeningTo(final GroupMember speaker) {
        return speaker.listenersToMe.contains(this);
    }
    
    public List<GroupMember> membersListeningToSpeaker() {
        return new LinkedList<GroupMember>(this.listenersToMe);
    }
    
    public boolean speakerIgnored(final GroupMember speaker) {
        return this.ignoredSpeakerOids != null && this.ignoredSpeakerOids.contains(speaker.memberOid);
    }
    
    public void initializeIgnoredSpeakers(final List<OID> ignored) {
        if (this.ignoredSpeakerOids != null) {
            Log.error("GroupMember.initializeIgnoredSpeakers: ignoredSpeakerOids for member " + this.memberOid + " is already initialized!");
        }
        else {
            (this.ignoredSpeakerOids = new HashSet<OID>()).addAll(ignored);
            if (this.pendingIgnoreUpdateMessages != null) {
                for (final WorldManagerClient.ExtensionMessage extMsg : this.pendingIgnoreUpdateMessages) {
                    this.applyIgnoreUpdateMessageInternal(extMsg);
                }
                this.pendingIgnoreUpdateMessages = null;
            }
        }
    }
    
    public void applyIgnoreUpdateMessage(final WorldManagerClient.ExtensionMessage extMsg) {
        if (this.ignoredSpeakerOids != null) {
            this.applyIgnoreUpdateMessageInternal(extMsg);
        }
        else {
            if (this.pendingIgnoreUpdateMessages == null) {
                this.pendingIgnoreUpdateMessages = new LinkedList<WorldManagerClient.ExtensionMessage>();
            }
            this.pendingIgnoreUpdateMessages.add(extMsg);
        }
    }
    
    private void applyIgnoreUpdateMessageInternal(final WorldManagerClient.ExtensionMessage extMsg) {
        final List<OID> nowIgnored = (List<OID>)extMsg.getProperty("now_ignored");
        final List<OID> noLongerIgnored = (List<OID>)extMsg.getProperty("no_longer_ignored");
        if (noLongerIgnored != null) {
            this.removeIgnoredSpeakerOids(noLongerIgnored);
        }
        if (nowIgnored != null) {
            this.addIgnoredSpeakerOids(nowIgnored);
        }
    }
    
    public void addIgnoredSpeakerOids(final List<OID> addToIgnored) {
        if (this.ignoredSpeakerOids == null) {
            Log.error("GroupMember.addIgnoredSpeakerOids: ignoredSpeakerOids for member " + this.memberOid + " is not yet initialized!");
        }
        else {
            this.ignoredSpeakerOids.addAll(addToIgnored);
        }
    }
    
    public void removeIgnoredSpeakerOids(final List<OID> noLongerIgnored) {
        if (this.ignoredSpeakerOids == null) {
            Log.error("GroupMember.removeIgnoredSpeakerOids: ignoredSpeakerOids for member " + this.memberOid + " is not yet initialized!");
        }
        else {
            this.ignoredSpeakerOids.removeAll(noLongerIgnored);
        }
    }
    
    public OID getMemberOid() {
        return this.memberOid;
    }
    
    public void setExpunged() {
        this.expunged = true;
    }
    
    public boolean getExpunged() {
        return this.expunged;
    }
    
    static {
        GroupMember.indexCounter = 0;
    }
}
