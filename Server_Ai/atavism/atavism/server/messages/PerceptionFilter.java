// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import java.util.LinkedList;
import atavism.server.network.AOByteBuffer;
import atavism.msgsys.SubscriptionHandle;
import atavism.msgsys.AgentHandle;
import atavism.msgsys.FilterUpdate;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.TargetMessage;
import atavism.msgsys.Message;
import java.util.ArrayList;
import java.util.Iterator;
import atavism.server.util.Log;
import atavism.server.objects.ObjectTypes;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import atavism.server.objects.ObjectType;
import java.util.List;
import atavism.server.engine.OID;
import java.util.Map;
import atavism.msgsys.MessageType;
import java.util.Set;
import atavism.msgsys.IMessageTypeFilter;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.Filter;

public class PerceptionFilter extends Filter implements Marshallable, IMessageTypeFilter
{
    public static final int FIELD_TARGETS = 1;
    public static final int FIELD_SUBJECTS = 2;
    private Set<MessageType> messageTypes;
    private Map<OID, IntHolder> targets;
    private transient Map<OID, SubjectInfo> subjects;
    private transient boolean matchAllSubjects;
    private boolean matchSubjects;
    private List<ObjectType> subjectTypeFilter;
    private static List<PerceptionUpdateTrigger> updateTriggers;
    
    public PerceptionFilter() {
        this.messageTypes = new HashSet<MessageType>();
        this.targets = new HashMap<OID, IntHolder>();
        this.subjects = new HashMap<OID, SubjectInfo>();
        this.matchAllSubjects = false;
        this.matchSubjects = false;
    }
    
    public PerceptionFilter(final Collection<MessageType> types) {
        this.messageTypes = new HashSet<MessageType>();
        this.targets = new HashMap<OID, IntHolder>();
        this.subjects = new HashMap<OID, SubjectInfo>();
        this.matchAllSubjects = false;
        this.matchSubjects = false;
        this.messageTypes.addAll(types);
    }
    
    @Override
    public void setTypes(final Collection<MessageType> types) {
        (this.messageTypes = new HashSet<MessageType>()).addAll(types);
    }
    
    @Override
    public void addType(final MessageType type) {
        this.messageTypes.add(type);
    }
    
    @Override
    public Collection<MessageType> getMessageTypes() {
        return this.messageTypes;
    }
    
    public boolean getMatchAllSubjects() {
        return this.matchAllSubjects;
    }
    
    public void setMatchAllSubjects(final boolean match) {
        this.matchAllSubjects = match;
    }
    
    public boolean getMatchSubjects() {
        return this.matchSubjects;
    }
    
    public void setMatchSubjects(final boolean match) {
        this.matchSubjects = match;
    }
    
    public synchronized boolean addSubject(final OID oid) {
        final SubjectInfo holder = this.subjects.get(oid);
        if (holder == null) {
            this.subjects.put(oid, new SubjectInfo(1, ObjectTypes.unknown));
            return true;
        }
        final SubjectInfo subjectInfo = holder;
        ++subjectInfo.count;
        return false;
    }
    
    public synchronized boolean addSubjectIfMissing(final OID oid) {
        final SubjectInfo holder = this.subjects.get(oid);
        if (holder == null) {
            this.subjects.put(oid, new SubjectInfo(1, ObjectTypes.unknown));
            return true;
        }
        return false;
    }
    
    public synchronized boolean hasSubject(final OID oid) {
        return this.subjects.containsKey(oid);
    }
    
    synchronized void addSubjects(final Collection<TypedSubject> newSubjects) {
        for (final TypedSubject subject : newSubjects) {
            if (this.subjects.get(subject.oid) != null) {
                Log.error("PerceptionFilter: already have subject " + subject.oid);
            }
            this.subjects.put(subject.oid, new SubjectInfo(1, subject.type));
        }
    }
    
    public synchronized boolean removeSubject(final OID oid) {
        final IntHolder holder = this.subjects.get(oid);
        if (holder == null) {
            Log.error("PerceptionFilter.removeSubject: oid " + oid + " not found");
            return false;
        }
        final IntHolder intHolder = holder;
        --intHolder.count;
        if (holder.count == 0) {
            this.subjects.remove(oid);
            return true;
        }
        return false;
    }
    
    synchronized void removeSubjects(final Collection<OID> freeOids) {
        for (final OID oid : freeOids) {
            if (this.subjects.get(oid) == null) {
                Log.error("PerceptionFilter.removeSubjects: duplicate remove " + oid);
            }
            this.subjects.remove(oid);
        }
    }
    
    public synchronized boolean addTarget(final OID oid) {
        final IntHolder holder = this.targets.get(oid);
        if (holder == null) {
            this.targets.put(oid, new IntHolder(1));
            return true;
        }
        final IntHolder intHolder = holder;
        ++intHolder.count;
        return false;
    }
    
    public synchronized boolean hasTarget(final OID oid) {
        return this.targets.containsKey(oid);
    }
    
    public synchronized void setSubjectObjectTypes(final Collection<ObjectType> subjectTypes) {
        if (subjectTypes != null) {
            this.subjectTypeFilter = new ArrayList<ObjectType>(subjectTypes);
        }
        else {
            this.subjectTypeFilter = null;
        }
    }
    
    public synchronized List<ObjectType> getSubjectObjectTypes() {
        return new ArrayList<ObjectType>(this.subjectTypeFilter);
    }
    
    public synchronized boolean removeTarget(final OID oid) {
        final IntHolder holder = this.targets.get(oid);
        if (holder == null) {
            Log.error("PerceptionFilter.removeTarget: oid " + oid + " not found");
            return false;
        }
        final IntHolder intHolder = holder;
        --intHolder.count;
        if (holder.count == 0) {
            this.targets.remove(oid);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean matchMessageType(final Collection<MessageType> types) {
        for (final MessageType tt : types) {
            if (this.messageTypes.contains(tt)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public synchronized boolean matchRemaining(final Message message) {
        if (!(message instanceof PerceptionMessage)) {
            if (message instanceof TargetMessage) {
                final TargetMessage msg = (TargetMessage)message;
                if (this.targets.get(msg.getTarget()) != null) {
                    return true;
                }
            }
            if (message instanceof SubjectMessage) {
                if (this.matchAllSubjects) {
                    return true;
                }
                final SubjectMessage msg2 = (SubjectMessage)message;
                final SubjectInfo subjectInfo = this.subjects.get(msg2.getSubject());
                if (subjectInfo != null) {
                    return this.subjectTypeFilter == null || this.subjectTypeFilter.contains(subjectInfo.type);
                }
                if (this.targets.get(msg2.getSubject()) != null) {
                    return true;
                }
            }
            return false;
        }
        final PerceptionMessage msg3 = (PerceptionMessage)message;
        if (this.targets.get(msg3.getTarget()) != null) {
            return true;
        }
        final List<PerceptionMessage.ObjectNote> gainObjects = msg3.getGainObjects();
        if (gainObjects != null) {
            for (final PerceptionMessage.ObjectNote gain : gainObjects) {
                if (this.targets.get(gain.targetOid) != null) {
                    return true;
                }
            }
        }
        final List<PerceptionMessage.ObjectNote> lostObjects = msg3.getLostObjects();
        if (lostObjects != null) {
            for (final PerceptionMessage.ObjectNote lost : lostObjects) {
                if (this.targets.get(lost.targetOid) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean applyFilterUpdate(final FilterUpdate update, final AgentHandle sender, final SubscriptionHandle sub) {
        final List<FilterUpdate.Instruction> instructions = update.getInstructions();
        if (PerceptionFilter.updateTriggers.size() > 0 && sender != null) {
            for (final FilterUpdate.Instruction instruction : instructions) {
                for (final PerceptionUpdateTrigger updateTrigger : PerceptionFilter.updateTriggers) {
                    updateTrigger.preUpdate(this, instruction, sender, sub);
                }
            }
        }
        synchronized (this) {
            for (final FilterUpdate.Instruction instruction2 : instructions) {
                switch (instruction2.opCode) {
                    case 2: {
                        if (instruction2.fieldId == 1) {
                            if (Log.loggingDebug) {
                                Log.debug("ADD TARGET " + instruction2.value);
                            }
                            this.targets.put((OID)instruction2.value, new IntHolder(1));
                            continue;
                        }
                        if (instruction2.fieldId == 2) {
                            if (Log.loggingDebug) {
                                Log.debug("ADD SUBJECT " + instruction2.value);
                            }
                            this.subjects.put((OID)instruction2.value, new SubjectInfo(1, ObjectTypes.unknown));
                            continue;
                        }
                        Log.error("PerceptionFilter.applyFilterUpdate: invalid fieldId " + instruction2.fieldId);
                        continue;
                    }
                    case 3: {
                        if (instruction2.fieldId == 1) {
                            if (Log.loggingDebug) {
                                Log.debug("REMOVE TARGET " + instruction2.value);
                            }
                            this.targets.remove(instruction2.value);
                            continue;
                        }
                        if (instruction2.fieldId == 2) {
                            if (Log.loggingDebug) {
                                Log.debug("REMOVE SUBJECT " + instruction2.value);
                            }
                            this.subjects.remove(instruction2.value);
                            continue;
                        }
                        Log.error("PerceptionFilter.applyFilterUpdate: invalid fieldId " + instruction2.fieldId);
                        continue;
                    }
                    case 1: {
                        Log.error("PerceptionFilter.applyFilterUpdate: OP_SET is not supported");
                        continue;
                    }
                    default: {
                        Log.error("PerceptionFilter.applyFilterUpdate: invalid opCode " + instruction2.opCode);
                        continue;
                    }
                }
            }
        }
        if (PerceptionFilter.updateTriggers.size() > 0 && sender != null) {
            for (final FilterUpdate.Instruction instruction : instructions) {
                for (final PerceptionUpdateTrigger updateTrigger : PerceptionFilter.updateTriggers) {
                    updateTrigger.postUpdate(this, instruction, sender, sub);
                }
            }
        }
        return false;
    }
    
    public static void addUpdateTrigger(final PerceptionUpdateTrigger updateTrigger) {
        synchronized (PerceptionFilter.updateTriggers) {
            PerceptionFilter.updateTriggers.add(updateTrigger);
        }
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        buf.putInt(this.messageTypes.size());
        for (final MessageType type : this.messageTypes) {
            type.marshalObject(buf);
        }
        buf.putBoolean(this.matchSubjects);
        buf.putInt(this.targets.size());
        for (final OID oid : this.targets.keySet()) {
            buf.putOID(oid);
        }
        if (this.matchSubjects) {
            buf.putInt(this.subjects.size());
            for (final OID oid : this.subjects.keySet()) {
                buf.putOID(oid);
            }
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        for (int size = buf.getInt(); size > 0; --size) {
            MessageType type = new MessageType();
            type = (MessageType)type.unmarshalObject(buf);
            this.messageTypes.add(type);
        }
        this.matchSubjects = buf.getBoolean();
        for (int size = buf.getInt(); size > 0; --size) {
            this.targets.put(buf.getOID(), new IntHolder(1));
        }
        if (this.matchSubjects) {
            for (int size = buf.getInt(); size > 0; --size) {
                this.subjects.put(buf.getOID(), new SubjectInfo(1, ObjectTypes.unknown));
            }
        }
        return this;
    }
    
    @Override
    public String toString() {
        return "[PerceptionFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        String result = "types=";
        for (final MessageType type : this.messageTypes) {
            result = result + type.getMsgTypeString() + ",";
        }
        result = result + " subjectCount=" + this.subjects.size();
        result += " targets=";
        for (final OID oid : this.targets.keySet()) {
            result = result + oid + ",";
        }
        return result;
    }
    
    static {
        PerceptionFilter.updateTriggers = new LinkedList<PerceptionUpdateTrigger>();
    }
    
    protected class IntHolder
    {
        int count;
        
        IntHolder() {
            this.count = 0;
        }
        
        IntHolder(final int initial) {
            this.count = 0;
            this.count = initial;
        }
    }
    
    protected class SubjectInfo extends IntHolder
    {
        ObjectType type;
        
        SubjectInfo() {
        }
        
        SubjectInfo(final int initial, final ObjectType objectType) {
            super(initial);
            this.type = objectType;
        }
    }
    
    public static class TypedSubject
    {
        OID oid;
        ObjectType type;
        
        public TypedSubject(final OID subjectOid, final ObjectType objectType) {
            this.oid = subjectOid;
            this.type = objectType;
        }
    }
}
