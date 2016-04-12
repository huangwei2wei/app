// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import java.util.List;
import atavism.server.util.LockFactory;
import atavism.server.util.Log;
import atavism.server.network.AOByteBuffer;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import atavism.server.engine.OID;
import java.util.HashSet;
import java.util.HashMap;
import atavism.msgsys.MessageType;
import java.util.Collection;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.Namespace;
import atavism.server.marshalling.Marshallable;
import atavism.server.engine.EventParser;
import atavism.msgsys.SubjectMessage;

public class PropertyMessage extends SubjectMessage implements EventParser, IPropertyMessage, Marshallable
{
    private Namespace namespace;
    protected transient Lock lock;
    protected Map<String, Serializable> propertyMap;
    protected Collection<String> removedProperties;
    private static final long serialVersionUID = 1L;
    public static MessageType MSG_TYPE_PROPERTY;
    
    public PropertyMessage() {
        this.lock = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.removedProperties = new HashSet<String>();
        this.setupTransient();
    }
    
    public PropertyMessage(final MessageType msgType) {
        super(msgType);
        this.lock = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.removedProperties = new HashSet<String>();
        this.setupTransient();
    }
    
    public PropertyMessage(final OID objOid) {
        super(PropertyMessage.MSG_TYPE_PROPERTY, objOid);
        this.lock = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.removedProperties = new HashSet<String>();
        this.setupTransient();
    }
    
    public PropertyMessage(final MessageType msgType, final OID objOid) {
        super(msgType, objOid);
        this.lock = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.removedProperties = new HashSet<String>();
        this.setupTransient();
    }
    
    public PropertyMessage(final OID objOid, final OID notifyOid) {
        super(PropertyMessage.MSG_TYPE_PROPERTY, objOid);
        this.lock = null;
        this.propertyMap = new HashMap<String, Serializable>();
        this.removedProperties = new HashSet<String>();
        this.setupTransient();
    }
    
    @Override
    public String toString() {
        String s = "[PropertyMessage super=" + super.toString();
        for (final Map.Entry<String, Serializable> entry : this.propertyMap.entrySet()) {
            final String key = entry.getKey();
            final Serializable val = entry.getValue();
            s = s + " key=" + key + ",value=" + val;
        }
        return s + "]";
    }
    
    public void setNamespace(final Namespace namespace) {
        this.namespace = namespace;
    }
    
    public Namespace getNamespace() {
        return this.namespace;
    }
    
    public void put(final String key, final Serializable val) {
        this.setProperty(key, val);
    }
    
    public void setProperty(final String key, final Serializable val) {
        this.lock.lock();
        try {
            this.propertyMap.put(key, val);
            this.removedProperties.remove(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setProperty(final String key, final Serializable val, final boolean clone) {
        if (!clone) {
            this.setProperty(key, val);
            return;
        }
        this.lock.lock();
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(val);
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            final ObjectInputStream ois = new ObjectInputStream(bais);
            final Serializable valDeepCopy = (Serializable)ois.readObject();
            this.propertyMap.put(key, valDeepCopy);
            this.removedProperties.remove(key);
        }
        catch (ClassNotFoundException e) {
            this.propertyMap.put(key, null);
            this.removedProperties.remove(key);
        }
        catch (IOException e2) {
            this.propertyMap.put(key, null);
            this.removedProperties.remove(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void removeProperty(final String key) {
        this.lock.lock();
        try {
            this.propertyMap.remove(key);
            this.removedProperties.add(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Serializable get(final String key) {
        return this.getProperty(key);
    }
    
    public Serializable getProperty(final String key) {
        this.lock.lock();
        try {
            return this.propertyMap.get(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<String> keySet() {
        this.lock.lock();
        try {
            return this.propertyMap.keySet();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public AOByteBuffer toBuffer(final String version) {
        return this.toBuffer(version, this.propertyMap, this.removedProperties, null);
    }
    
    public AOByteBuffer toBuffer(final String version, final Set<String> filteredProps) {
        return this.toBuffer(version, this.propertyMap, this.removedProperties, filteredProps);
    }
    
    public AOByteBuffer toBuffer(final String version, final Map<String, Serializable> propMap, final Collection<String> removedSet, final Set<String> filteredProps) {
        this.lock.lock();
        try {
            final AOByteBuffer buf = new AOByteBuffer(500);
            buf.putOID(this.getSubject());
            buf.putInt(62);
            buf.putFilteredPropertyMap((Map)propMap, (Set)filteredProps);
            buf.putFilteredPropertyCollection((Collection)removedSet, (Set)filteredProps);
            buf.flip();
            return buf;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void fromBuffer(final AOByteBuffer buf) {
        final OID oid = buf.getOID();
        final int msgNumber = buf.getInt();
        if (msgNumber != 62) {
            Log.error("PropertyMessage.fromBuffer: msgNumber " + msgNumber + " is not 62");
            return;
        }
        this.propertyMap = (Map<String, Serializable>)buf.getPropertyMap();
        final Collection<Serializable> collection = (Collection<Serializable>)buf.getCollection();
        this.removedProperties = new HashSet<String>();
        for (final Serializable entry : collection) {
            this.removedProperties.add((String)entry);
        }
        this.setSubject(oid);
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        this.fromBuffer(buf);
    }
    
    void setupTransient() {
        this.lock = LockFactory.makeLock("PropertyMessageLock");
    }
    
    public Map<String, Serializable> getPropertyMapRef() {
        return this.propertyMap;
    }
    
    public Collection<String> getRemovedPropertiesRef() {
        return this.removedProperties;
    }
    
    public static int createPropertyString(final List<String> propStrings, final Map<String, Serializable> propertyMap, final String version) {
        int len = 0;
        for (final Map.Entry<String, Serializable> entry : propertyMap.entrySet()) {
            final String key = entry.getKey();
            final Serializable val = entry.getValue();
            len = addPropertyStringElement(key, val, propStrings, version, len);
        }
        return len;
    }
    
    public static int createFilteredPropertyString(final List<String> propStrings, final Map<String, Serializable> propertyMap, final String version, final Set<String> filteredProps) {
        int len = 0;
        for (final Map.Entry<String, Serializable> entry : propertyMap.entrySet()) {
            final String key = entry.getKey();
            if (filteredProps.contains(key)) {
                continue;
            }
            final Serializable val = entry.getValue();
            len = addPropertyStringElement(key, val, propStrings, version, len);
        }
        return len;
    }
    
    protected static int addPropertyStringElement(final String key, final Serializable val, final List<String> propStrings, final String version, int len) {
        if (val instanceof Boolean) {
            final Boolean b = (Boolean)val;
            propStrings.add(key);
            propStrings.add("B");
            propStrings.add(((boolean)b) ? "true" : "false");
            ++len;
        }
        else if (val instanceof Integer) {
            propStrings.add(key);
            propStrings.add("I");
            propStrings.add(val.toString());
            ++len;
        }
        else if (val instanceof Long) {
            propStrings.add(key);
            propStrings.add("L");
            propStrings.add(val.toString());
            ++len;
        }
        else if (val instanceof OID) {
            propStrings.add(key);
            propStrings.add("O");
            propStrings.add(Long.toString(((OID)val).toLong()));
            ++len;
        }
        else if (val instanceof String) {
            propStrings.add(key);
            propStrings.add("S");
            propStrings.add((String)val);
            ++len;
        }
        else if (val instanceof Float) {
            propStrings.add(key);
            propStrings.add("F");
            propStrings.add(val.toString());
            ++len;
        }
        else if (val instanceof Point) {
            if (version != null) {
                propStrings.add(key);
                propStrings.add("V");
                final Point loc = (Point)val;
                propStrings.add(loc.toString());
                ++len;
            }
        }
        else if (val instanceof Quaternion) {
            if (version != null) {
                propStrings.add(key);
                propStrings.add("Q");
                final Quaternion q = (Quaternion)val;
                propStrings.add(q.toString());
                ++len;
            }
        }
        else if (val == null) {
            Log.warn("propertyMessage: null value for key=" + key);
        }
        else {
            Log.warn("propertyMessage: unknown type '" + val.getClass().getName() + "', skipping key=" + key);
        }
        if (Log.loggingDebug) {
            Log.debug("propertyMessage: key=" + key + ", val=" + val);
        }
        return len;
    }
    
    public static Map<String, Serializable> unmarshallProperyMap(final AOByteBuffer buffer) {
        final int nProps = buffer.getInt();
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>(nProps);
        for (int ii = 0; ii < nProps; ++ii) {
            final String key = buffer.getString();
            final String type = buffer.getString();
            final String value = buffer.getString();
            if (type.equals("I")) {
                props.put(key, Integer.valueOf(value));
            }
            else if (type.equals("B")) {
                props.put(key, Boolean.valueOf(value));
            }
            else if (type.equals("L")) {
                props.put(key, Long.valueOf(value));
            }
            else if (type.equals("O")) {
                props.put(key, (Serializable)OID.fromLong((long)Long.valueOf(value)));
            }
            else if (type.equals("S")) {
                props.put(key, value);
            }
            else if (type.equals("F")) {
                props.put(key, Float.valueOf(value));
            }
            else if (Log.loggingDebug) {
                Log.debug("unmarshallProperyMap: unknown type '" + type + "', skipping key=" + key);
            }
        }
        return props;
    }
    
    static {
        PropertyMessage.MSG_TYPE_PROPERTY = MessageType.intern("ao.PROPERTY");
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.namespace != null) {
            flag_bits = 1;
        }
        if (this.propertyMap != null) {
            flag_bits |= 0x2;
        }
        if (this.removedProperties != null) {
            flag_bits |= 0x4;
        }
        buf.putByte(flag_bits);
        if (this.namespace != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.namespace);
        }
        if (this.propertyMap != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.propertyMap);
        }
        if (this.removedProperties != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.removedProperties);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.namespace = (Namespace)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.propertyMap = (Map<String, Serializable>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.removedProperties = (Collection<String>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
