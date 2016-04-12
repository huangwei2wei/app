// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import java.util.LinkedList;
import atavism.server.objects.ObjectType;
import atavism.msgsys.MessageType;
import java.util.List;
import atavism.server.engine.OID;
import atavism.msgsys.HasTarget;
import atavism.msgsys.Message;

public class PerceptionMessage extends Message implements HasTarget
{
    OID target;
    List<ObjectNote> gainObjects;
    List<ObjectNote> lostObjects;
    private static final long serialVersionUID = 1L;
    
    public PerceptionMessage() {
    }
    
    public PerceptionMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public PerceptionMessage(final MessageType msgType, final OID target) {
        super(msgType);
        this.target = target;
    }
    
    @Override
    public OID getTarget() {
        return this.target;
    }
    
    @Override
    public void setTarget(final OID target) {
        this.target = target;
    }
    
    public ObjectNote gainObject(final OID targetOid, final OID subjectOid, final ObjectType objectType) {
        if (this.gainObjects == null) {
            this.gainObjects = new LinkedList<ObjectNote>();
        }
        final ObjectNote note = new ObjectNote(targetOid, subjectOid, objectType);
        this.gainObjects.add(note);
        return note;
    }
    
    public void gainObject(final ObjectNote note) {
        if (this.gainObjects == null) {
            this.gainObjects = new LinkedList<ObjectNote>();
        }
        this.gainObjects.add(note);
    }
    
    public void lostObject(final OID targetOid, final OID subjectOid) {
        if (this.lostObjects == null) {
            this.lostObjects = new LinkedList<ObjectNote>();
        }
        this.lostObjects.add(new ObjectNote(targetOid, subjectOid));
    }
    
    public void lostObject(final OID targetOid, final OID subjectOid, final ObjectType objectType) {
        if (this.lostObjects == null) {
            this.lostObjects = new LinkedList<ObjectNote>();
        }
        this.lostObjects.add(new ObjectNote(targetOid, subjectOid, objectType));
    }
    
    public void lostObject(final ObjectNote note) {
        if (this.lostObjects == null) {
            this.lostObjects = new LinkedList<ObjectNote>();
        }
        this.lostObjects.add(note);
    }
    
    public List<ObjectNote> getGainObjects() {
        return this.gainObjects;
    }
    
    public List<ObjectNote> getLostObjects() {
        return this.lostObjects;
    }
    
    public int getGainObjectCount() {
        return (this.gainObjects == null) ? 0 : this.gainObjects.size();
    }
    
    public int getLostObjectCount() {
        return (this.lostObjects == null) ? 0 : this.lostObjects.size();
    }
    
    public static class ObjectNote
    {
        OID targetOid;
        OID subjectOid;
        ObjectType objectType;
        Object info;
        
        public ObjectNote() {
        }
        
        public ObjectNote(final OID targetOid, final OID subjectOid) {
            this.targetOid = targetOid;
            this.subjectOid = subjectOid;
        }
        
        public ObjectNote(final OID targetOid, final OID subjectOid, final ObjectType objectType) {
            this.targetOid = targetOid;
            this.subjectOid = subjectOid;
            this.objectType = objectType;
        }
        
        public ObjectNote(final OID targetOid, final OID subjectOid, final ObjectType objectType, final Object info) {
            this.targetOid = targetOid;
            this.subjectOid = subjectOid;
            this.objectType = objectType;
            this.info = info;
        }
        
        @Override
        public String toString() {
            return "targ=" + this.targetOid + " subj=" + this.subjectOid + " t=" + this.objectType;
        }
        
        public OID getTarget() {
            return this.targetOid;
        }
        
        public OID getSubject() {
            return this.subjectOid;
        }
        
        public ObjectType getObjectType() {
            return this.objectType;
        }
        
        public Object getObjectInfo() {
            return this.info;
        }
        
        public void setObjectInfo(final Object info) {
            this.info = info;
        }
    }
}
