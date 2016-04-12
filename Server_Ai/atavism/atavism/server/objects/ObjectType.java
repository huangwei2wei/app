// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.beans.Expression;
import java.beans.Encoder;
import java.beans.DefaultPersistenceDelegate;
import java.util.HashMap;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Log;
import java.util.Map;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class ObjectType implements Serializable, Marshallable
{
    public static final int BASE_STRUCTURE = 1;
    public static final int BASE_MOB = 2;
    public static final int BASE_PLAYER = 4;
    transient short typeId;
    transient String typeName;
    transient int baseType;
    static Map<String, ObjectType> internedTypes;
    static Map<Short, ObjectType> internedTypeIds;
    private static final long serialVersionUID = 1L;
    
    public ObjectType() {
    }
    
    ObjectType(final short type, final String typeName, final int baseType) {
        this.typeId = type;
        this.typeName = typeName;
        this.baseType = baseType;
    }
    
    public static ObjectType intern(final short typeId, final String typeName) {
        return intern(typeId, typeName, 0);
    }
    
    public static ObjectType intern(final short typeId, final String typeName, final int baseType) {
        ObjectType objectType = ObjectType.internedTypes.get(typeName);
        if (objectType == null) {
            objectType = new ObjectType(typeId, typeName, baseType);
            ObjectType.internedTypes.put(typeName, objectType);
            ObjectType.internedTypeIds.put(typeId, objectType);
        }
        else if (objectType.getTypeId() != typeId) {
            Log.error("ObjectType.intern: typeId mismatch for \"" + typeName + "\": existing=" + objectType.getTypeId() + " new=" + typeId);
        }
        return objectType;
    }
    
    public static ObjectType getObjectType(final short typeId) {
        return ObjectType.internedTypeIds.get(typeId);
    }
    
    public static ObjectType getObjectType(final String typeName) {
        return ObjectType.internedTypes.get(typeName);
    }
    
    @Override
    public String toString() {
        return "[" + this.typeName + "," + this.typeId + "]";
    }
    
    public short getTypeId() {
        return this.typeId;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public boolean isStructure() {
        return (this.baseType & 0x1) > 0;
    }
    
    public boolean isA(final ObjectType objectType) {
        return this.typeId == objectType.typeId || (this.baseType & objectType.baseType) > 0;
    }
    
    public boolean isMob() {
        return (this.baseType & 0x2) > 0;
    }
    
    public boolean isPlayer() {
        return (this.baseType & 0x4) > 0;
    }
    
    public int getBaseType() {
        return this.baseType;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        buf.putShort(this.typeId);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        this.typeId = buf.getShort();
        final ObjectType type = getObjectType(this.typeId);
        if (type == null) {
            Log.info("ObjectType.unmarshalObject: no interned ObjectType for typeId=" + this.typeId);
            return new ObjectType(this.typeId, null, 0);
        }
        return type;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.typeId = in.readShort();
    }
    
    private Object readResolve() throws ObjectStreamException {
        return getObjectType(this.typeId);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeShort(this.typeId);
    }
    
    static {
        ObjectType.internedTypes = new HashMap<String, ObjectType>();
        ObjectType.internedTypeIds = new HashMap<Short, ObjectType>();
    }
    
    public static class PersistenceDelegate extends DefaultPersistenceDelegate
    {
        @Override
        protected Expression instantiate(final Object oldInstance, final Encoder out) {
            final ObjectType objectType = (ObjectType)oldInstance;
            return new Expression(ObjectType.class, "getObjectType", new Object[] { objectType.getTypeId() });
        }
        
        @Override
        protected boolean mutatesTo(final Object oldInstance, final Object newInstance) {
            return oldInstance == newInstance;
        }
    }
}
