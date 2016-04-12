// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.LinkedList;
import atavism.server.objects.ObjectType;
import atavism.msgsys.MessageType;
import java.util.List;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.HasTarget;
import atavism.msgsys.Message;

public class PerceptionMessage extends Message implements HasTarget, Marshallable
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
    
    public OID getTarget() {
        return this.target;
    }
    
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.target != null) {
            flag_bits = 1;
        }
        if (this.gainObjects != null) {
            flag_bits |= 0x2;
        }
        if (this.lostObjects != null) {
            flag_bits |= 0x4;
        }
        buf.putByte(flag_bits);
        if (this.target != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.target);
        }
        if (this.gainObjects != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.gainObjects);
        }
        if (this.lostObjects != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.lostObjects);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.target = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.gainObjects = (List<ObjectNote>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.lostObjects = (List<ObjectNote>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
    
    public static class ObjectNote implements Marshallable
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
        
        public void marshalObject(final AOByteBuffer buf) {
            byte flag_bits = 0;
            if (this.targetOid != null) {
                flag_bits = 1;
            }
            if (this.subjectOid != null) {
                flag_bits |= 0x2;
            }
            if (this.objectType != null) {
                flag_bits |= 0x4;
            }
            if (this.info != null) {
                flag_bits |= 0x8;
            }
            buf.putByte(flag_bits);
            if (this.targetOid != null) {
                MarshallingRuntime.marshalObject(buf, (Object)this.targetOid);
            }
            if (this.subjectOid != null) {
                MarshallingRuntime.marshalObject(buf, (Object)this.subjectOid);
            }
            if (this.objectType != null) {
                MarshallingRuntime.marshalObject(buf, (Object)this.objectType);
            }
            if (this.info != null) {
                MarshallingRuntime.marshalObject(buf, this.info);
            }
        }
        
        public Object unmarshalObject(final AOByteBuffer buf) {
            final byte flag_bits0 = buf.getByte();
            if ((flag_bits0 & 0x1) != 0x0) {
                this.targetOid = (OID)MarshallingRuntime.unmarshalObject(buf);
            }
            if ((flag_bits0 & 0x2) != 0x0) {
                this.subjectOid = (OID)MarshallingRuntime.unmarshalObject(buf);
            }
            if ((flag_bits0 & 0x4) != 0x0) {
                this.objectType = (ObjectType)MarshallingRuntime.unmarshalObject(buf);
            }
            if ((flag_bits0 & 0x8) != 0x0) {
                this.info = MarshallingRuntime.unmarshalObject(buf);
            }
            return this;
        }
    }
}
