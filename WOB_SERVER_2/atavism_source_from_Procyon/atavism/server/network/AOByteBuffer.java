// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import atavism.server.util.Log;
import atavism.msgsys.MessageType;
import atavism.server.objects.Color;
import atavism.server.math.AOVector;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

public class AOByteBuffer implements Cloneable, Comparable<AOByteBuffer>
{
    private static final byte valueTypeNull = 0;
    private static final byte valueTypeString = 1;
    private static final byte valueTypeLong = 2;
    private static final byte valueTypeInteger = 3;
    private static final byte valueTypeBoolean = 4;
    private static final byte valueTypeBooleanFalse = 4;
    private static final byte valueTypeBooleanTrue = 5;
    private static final byte valueTypeFloat = 6;
    private static final byte valueTypeDouble = 7;
    private static final byte valueTypePoint = 8;
    private static final byte valueTypeAOVector = 9;
    private static final byte valueTypeQuaternion = 10;
    private static final byte valueTypeColor = 11;
    private static final byte valueTypeByte = 12;
    private static final byte valueTypeShort = 13;
    private static final byte valueTypeOID = 14;
    private static final byte valueTypeLinkedList = 20;
    private static final byte valueTypeHashSet = 21;
    private static final byte valueTypeHashMap = 22;
    private static final byte valueTypeByteArray = 23;
    private static final byte valueTypeTreeMap = 24;
    private static Map<Class, Byte> classToValueTypeMap;
    private ByteBuffer mBB;
    
    public AOByteBuffer(final int size) {
        final byte[] backingArray = new byte[size];
        this.mBB = ByteBuffer.wrap(backingArray);
        if (!this.mBB.hasArray()) {
            System.err.println("does not have backing array");
            System.exit(1);
        }
    }
    
    public AOByteBuffer(final byte[] data) {
        this(data.length);
        this.mBB.put(data, 0, data.length);
        this.flip();
    }
    
    public AOByteBuffer(final ByteBuffer data) {
        this.mBB = data;
    }
    
    public Object clone() {
        final byte[] data = this.mBB.array();
        final AOByteBuffer newBuf = new AOByteBuffer(data.length);
        newBuf.putBytes(data, 0, data.length);
        return newBuf;
    }
    
    public AOByteBuffer cloneAtOffset(final int offset, final int length) {
        final byte[] data = this.mBB.array();
        final AOByteBuffer newBuf = new AOByteBuffer(length);
        newBuf.putBytes(data, offset + this.mBB.position(), length);
        newBuf.rewind();
        return newBuf;
    }
    
    public byte[] array() {
        return this.mBB.array();
    }
    
    public int capacity() {
        return this.mBB.capacity();
    }
    
    public AOByteBuffer clear() {
        this.mBB.clear();
        return this;
    }
    
    public AOByteBuffer flip() {
        this.mBB.flip();
        return this;
    }
    
    public boolean hasRemaining() {
        return this.mBB.hasRemaining();
    }
    
    public int limit() {
        return this.mBB.limit();
    }
    
    public AOByteBuffer limit(final int newLimit) {
        this.mBB.limit(newLimit);
        return this;
    }
    
    public int position() {
        return this.mBB.position();
    }
    
    public AOByteBuffer position(final int newPos) {
        this.mBB.position(newPos);
        return this;
    }
    
    public int remaining() {
        return this.mBB.remaining();
    }
    
    public AOByteBuffer rewind() {
        this.mBB.rewind();
        return this;
    }
    
    public byte getByte() {
        return this.mBB.get();
    }
    
    public AOByteBuffer getBytes(final byte[] dst, final int offset, final int length) {
        this.mBB.get(dst, offset, length);
        return this;
    }
    
    public byte[] copyBytes() {
        this.rewind();
        final byte[] copyBuf = new byte[this.mBB.limit()];
        this.getBytes(copyBuf, 0, this.mBB.limit());
        return copyBuf;
    }
    
    public byte[] copyBytesFromZeroToLimit() {
        final int len = this.mBB.limit();
        final byte[] buf = new byte[len];
        final byte[] arr = this.mBB.array();
        for (int i = 0; i < len; ++i) {
            buf[i] = arr[i];
        }
        return buf;
    }
    
    public boolean getBoolean() {
        return this.getInt() == 1;
    }
    
    public double getDouble() {
        return this.mBB.getDouble();
    }
    
    public float getFloat() {
        return this.mBB.getFloat();
    }
    
    public short getShort() {
        return this.mBB.getShort();
    }
    
    public int getInt() {
        return this.mBB.getInt();
    }
    
    public long getLong() {
        return this.mBB.getLong();
    }
    
    public String getString() {
        final int len = this.mBB.getInt();
        if (len > 64000) {
            throw new RuntimeException("AOByteBuffer.getString: over 64k string len=" + len);
        }
        final byte[] buf = new byte[len];
        this.getBytes(buf, 0, len);
        try {
            return new String(buf, "UTF8");
        }
        catch (UnsupportedEncodingException e) {
            return new String(buf);
        }
    }
    
    public OID getOID() {
        final long l = this.getLong();
        if (l == 0L) {
            return null;
        }
        return OID.fromLong(l);
    }
    
    public Point getPoint() {
        return new Point(this.getFloat(), this.getFloat(), this.getFloat());
    }
    
    public Quaternion getQuaternion() {
        return new Quaternion(this.getFloat(), this.getFloat(), this.getFloat(), this.getFloat());
    }
    
    public AOVector getAOVector() {
        return new AOVector(this.getFloat(), this.getFloat(), this.getFloat());
    }
    
    public Color getColor() {
        final int alpha = this.getByte() & 0xFF;
        final int blue = this.getByte() & 0xFF;
        final int green = this.getByte() & 0xFF;
        final int red = this.getByte() & 0xFF;
        return new Color(red, green, blue, alpha);
    }
    
    public AOByteBuffer getByteBuffer() {
        final int length = this.getInt();
        final byte[] data = new byte[length];
        this.getBytes(data, 0, length);
        final AOByteBuffer newBuf = new AOByteBuffer(length);
        newBuf.putBytes(data, 0, length);
        newBuf.flip();
        return newBuf;
    }
    
    public byte[] getByteArray() {
        final int length = this.getInt();
        final byte[] data = new byte[length];
        this.getBytes(data, 0, length);
        return data;
    }
    
    public AOByteBuffer putByte(final byte b) {
        if (this.remaining() <= 0) {
            this.reallocate();
        }
        this.mBB.put(b);
        return this;
    }
    
    public AOByteBuffer putBytes(final byte[] src, final int offset, final int length) {
        if (this.remaining() < length) {
            this.reallocate(this.position() + length);
        }
        this.mBB.put(src, offset, length);
        return this;
    }
    
    public AOByteBuffer putBoolean(final boolean b) {
        if (this.remaining() < 4) {
            this.reallocate();
        }
        this.mBB.putInt(b ? 1 : 0);
        return this;
    }
    
    public AOByteBuffer putDouble(final double d) {
        if (this.remaining() < 8) {
            this.reallocate();
        }
        this.mBB.putDouble(d);
        return this;
    }
    
    public AOByteBuffer putFloat(final float f) {
        if (this.remaining() < 4) {
            this.reallocate();
        }
        this.mBB.putFloat(f);
        return this;
    }
    
    public AOByteBuffer putShort(final short s) {
        if (this.remaining() < 2) {
            this.reallocate();
        }
        this.mBB.putShort(s);
        return this;
    }
    
    public AOByteBuffer putInt(final int i) {
        if (this.remaining() < 4) {
            this.reallocate();
        }
        this.mBB.putInt(i);
        return this;
    }
    
    public AOByteBuffer putLong(final long l) {
        return this.putLong(new Long(l));
    }
    
    public AOByteBuffer putLong(Long l) {
        if (l == null) {
            l = 0L;
        }
        if (this.remaining() < 8) {
            this.reallocate();
        }
        this.mBB.putLong(l);
        return this;
    }
    
    public AOByteBuffer putString(final String s) {
        if (s == null) {
            this.putInt(0);
            return this;
        }
        byte[] data;
        try {
            data = s.getBytes("UTF8");
        }
        catch (UnsupportedEncodingException e) {
            data = s.getBytes();
        }
        final int len = data.length;
        if (this.remaining() < len + 4) {
            this.reallocate(this.position() + len + 4);
        }
        this.mBB.putInt(len);
        this.mBB.put(data, 0, len);
        return this;
    }
    
    public AOByteBuffer putMsgTypeString(final MessageType msgType) {
        return this.putString(msgType.getMsgTypeString());
    }
    
    public AOByteBuffer putOID(final OID oid) {
        if (oid == null) {
            return this.putLong(0L);
        }
        return this.putLong(oid.toLong());
    }
    
    public AOByteBuffer putPoint(final Point p) {
        if (this.remaining() < 24) {
            this.reallocate();
        }
        this.mBB.putFloat(p.getX());
        this.mBB.putFloat(p.getY());
        this.mBB.putFloat(p.getZ());
        return this;
    }
    
    public AOByteBuffer putQuaternion(final Quaternion q) {
        if (this.remaining() < 32) {
            this.reallocate();
        }
        this.mBB.putFloat(q.getX());
        this.mBB.putFloat(q.getY());
        this.mBB.putFloat(q.getZ());
        this.mBB.putFloat(q.getW());
        return this;
    }
    
    public AOByteBuffer putAOVector(final AOVector v) {
        if (this.remaining() < 24) {
            this.reallocate();
        }
        this.mBB.putFloat(v.getX());
        this.mBB.putFloat(v.getY());
        this.mBB.putFloat(v.getZ());
        return this;
    }
    
    public AOByteBuffer putColor(final Color c) {
        if (this.remaining() < 4) {
            this.reallocate();
        }
        this.mBB.put((byte)c.getAlpha());
        this.mBB.put((byte)c.getBlue());
        this.mBB.put((byte)c.getGreen());
        this.mBB.put((byte)c.getRed());
        return this;
    }
    
    public AOByteBuffer putByteBuffer(final AOByteBuffer other) {
        final byte[] data = other.array();
        final int dataLen = other.limit();
        if (this.remaining() < dataLen + 4) {
            this.reallocate(this.position() + dataLen + 4);
        }
        this.mBB.putInt(dataLen);
        this.mBB.put(data, 0, dataLen);
        return this;
    }
    
    public AOByteBuffer putByteArray(final byte[] byteArray) {
        this.mBB.putInt(byteArray.length);
        this.mBB.put(byteArray, 0, byteArray.length);
        return this;
    }
    
    public ByteBuffer getNioBuf() {
        return this.mBB;
    }
    
    private void reallocate() {
        this.reallocate(this.capacity() * 2);
    }
    
    private void reallocate(final int minSize) {
        int newSize;
        for (newSize = this.capacity(); newSize < minSize; newSize *= 2) {}
        if (Log.loggingDebug) {
            Log.debug("AOByteBuffer.reallocate: size=" + this.capacity() + " requested=" + minSize + " newSize=" + newSize);
        }
        final int pos = this.position();
        final byte[] data = this.mBB.array();
        final int dataLen = this.mBB.position();
        final byte[] backingArray = new byte[newSize];
        (this.mBB = ByteBuffer.wrap(backingArray)).put(data, 0, dataLen);
        this.mBB.position(pos);
    }
    
    private static void initializeClassToValueTypeMap() {
        (AOByteBuffer.classToValueTypeMap = new HashMap<Class, Byte>()).put(String.class, (byte)1);
        AOByteBuffer.classToValueTypeMap.put(Long.class, (byte)2);
        AOByteBuffer.classToValueTypeMap.put(Integer.class, (byte)3);
        AOByteBuffer.classToValueTypeMap.put(Boolean.class, (byte)4);
        AOByteBuffer.classToValueTypeMap.put(Float.class, (byte)6);
        AOByteBuffer.classToValueTypeMap.put(Double.class, (byte)7);
        AOByteBuffer.classToValueTypeMap.put(Byte.class, (byte)12);
        AOByteBuffer.classToValueTypeMap.put(Short.class, (byte)13);
        AOByteBuffer.classToValueTypeMap.put(Point.class, (byte)8);
        AOByteBuffer.classToValueTypeMap.put(AOVector.class, (byte)9);
        AOByteBuffer.classToValueTypeMap.put(Quaternion.class, (byte)10);
        AOByteBuffer.classToValueTypeMap.put(Color.class, (byte)11);
        AOByteBuffer.classToValueTypeMap.put(OID.class, (byte)14);
        AOByteBuffer.classToValueTypeMap.put(LinkedList.class, (byte)20);
        AOByteBuffer.classToValueTypeMap.put(ArrayList.class, (byte)20);
        AOByteBuffer.classToValueTypeMap.put(HashSet.class, (byte)21);
        AOByteBuffer.classToValueTypeMap.put(LinkedHashSet.class, (byte)21);
        AOByteBuffer.classToValueTypeMap.put(TreeSet.class, (byte)21);
        AOByteBuffer.classToValueTypeMap.put(HashMap.class, (byte)22);
        AOByteBuffer.classToValueTypeMap.put(LinkedHashMap.class, (byte)22);
        AOByteBuffer.classToValueTypeMap.put(byte[].class, (byte)23);
        AOByteBuffer.classToValueTypeMap.put(TreeMap.class, (byte)24);
    }
    
    public void putEncodedObject(final Serializable val) {
        if (AOByteBuffer.classToValueTypeMap == null) {
            initializeClassToValueTypeMap();
        }
        if (val == null) {
            this.putByte((byte)0);
        }
        else if (val instanceof ClientSerializable) {
            ((ClientSerializable)val).encodeObject(this);
        }
        else {
            final Class c = val.getClass();
            final Byte index = AOByteBuffer.classToValueTypeMap.get(c);
            if (index == null) {
                Log.error("AOByteBuffer.putEncodedObject: no support for object of " + c);
                return;
            }
            switch (index) {
                case 1: {
                    this.putByte((byte)1);
                    this.putString((String)val);
                    break;
                }
                case 2: {
                    this.putByte((byte)2);
                    this.putLong((Long)val);
                    break;
                }
                case 12: {
                    this.putByte((byte)12);
                    this.putByte((byte)val);
                    break;
                }
                case 13: {
                    this.putByte((byte)13);
                    this.putShort((short)val);
                    break;
                }
                case 3: {
                    this.putByte((byte)3);
                    this.putInt((int)val);
                    break;
                }
                case 4: {
                    this.putByte((byte)(val ? 5 : 4));
                    break;
                }
                case 6: {
                    this.putByte((byte)6);
                    this.putFloat((float)val);
                    break;
                }
                case 7: {
                    this.putByte((byte)7);
                    this.putDouble((double)val);
                    break;
                }
                case 8: {
                    this.putByte((byte)8);
                    this.putPoint((Point)val);
                    break;
                }
                case 9: {
                    this.putByte((byte)9);
                    this.putAOVector((AOVector)val);
                    break;
                }
                case 10: {
                    this.putByte((byte)10);
                    this.putQuaternion((Quaternion)val);
                    break;
                }
                case 11: {
                    this.putByte((byte)11);
                    this.putColor((Color)val);
                    break;
                }
                case 14: {
                    this.putByte((byte)14);
                    this.putOID((OID)val);
                    break;
                }
                case 20: {
                    this.putByte((byte)20);
                    this.putCollection((Collection<Serializable>)val);
                    break;
                }
                case 21: {
                    this.putByte((byte)21);
                    this.putCollection((Collection<Serializable>)val);
                    break;
                }
                case 22: {
                    this.putByte((byte)22);
                    this.putPropertyMap((Map<String, Serializable>)val);
                    break;
                }
                case 23: {
                    this.putByte((byte)23);
                    this.putByteArray((byte[])(Object)val);
                    break;
                }
                case 24: {
                    this.putByte((byte)24);
                    this.putPropertyMap((Map<String, Serializable>)val);
                    break;
                }
                default: {
                    Log.error("WorldManagerClient.putEncodedObject: index " + index + " out of bounds; class " + c.getName());
                    break;
                }
            }
        }
    }
    
    public Serializable getEncodedObject() {
        final byte typecode = this.getByte();
        switch (typecode) {
            case 0: {
                return null;
            }
            case 1: {
                return this.getString();
            }
            case 12: {
                return this.getByte();
            }
            case 13: {
                return this.getShort();
            }
            case 2: {
                return this.getLong();
            }
            case 3: {
                return this.getInt();
            }
            case 4: {
                return false;
            }
            case 5: {
                return true;
            }
            case 6: {
                return this.getFloat();
            }
            case 7: {
                return this.getDouble();
            }
            case 8: {
                return this.getPoint();
            }
            case 9: {
                return this.getAOVector();
            }
            case 10: {
                return this.getQuaternion();
            }
            case 11: {
                return this.getColor();
            }
            case 14: {
                return this.getOID();
            }
            case 20: {
                return (Serializable)this.getCollection();
            }
            case 21: {
                final int count = this.getInt();
                final HashSet<Serializable> set = new HashSet<Serializable>();
                for (int i = 0; i < count; ++i) {
                    set.add(this.getEncodedObject());
                }
                return set;
            }
            case 22: {
                return (Serializable)this.getPropertyMap();
            }
            case 23: {
                return this.getByteArray();
            }
            case 24: {
                return (Serializable)this.getTreeMap();
            }
            default: {
                Log.error("WorldManagerClient.getObjectUtility: Illegal value type code " + typecode);
                return null;
            }
        }
    }
    
    public void putCollection(final Collection<Serializable> collection) {
        this.putInt((collection == null) ? 0 : collection.size());
        if (collection != null) {
            for (final Serializable entry : collection) {
                this.putEncodedObject(entry);
            }
        }
    }
    
    public void putPropertyMap(final Map<String, Serializable> propertyMap) {
        if (Log.loggingDebug) {
            this.logPropertyMap("putPropertyMap", propertyMap, null);
        }
        this.putInt((propertyMap == null) ? 0 : propertyMap.size());
        if (propertyMap != null) {
            for (final Map.Entry<String, Serializable> entry : propertyMap.entrySet()) {
                final String key = entry.getKey();
                final Serializable val = entry.getValue();
                this.putString(key);
                this.putEncodedObject(val);
            }
        }
    }
    
    public void putFilteredPropertyMap(final Map<String, Serializable> propertyMap, final Set<String> filteredProps) {
        if (filteredProps == null || filteredProps.size() == 0) {
            this.putPropertyMap(propertyMap);
        }
        if (propertyMap == null) {
            this.putInt(0);
            return;
        }
        int count = 0;
        for (final String key : propertyMap.keySet()) {
            if (filteredProps == null || !filteredProps.contains(key)) {
                ++count;
            }
        }
        this.putInt(count);
        if (Log.loggingDebug) {
            this.logPropertyMap("putFilteredPropertyMap", propertyMap, filteredProps);
        }
        for (final Map.Entry<String, Serializable> entry : propertyMap.entrySet()) {
            final String key2 = entry.getKey();
            final Serializable val = entry.getValue();
            if (filteredProps == null || !filteredProps.contains(key2)) {
                this.putString(key2);
                this.putEncodedObject(val);
            }
        }
    }
    
    private void logPropertyMap(final String prefix, final Map<String, Serializable> propertyMap, final Set<String> filteredProps) {
        String s = "";
        for (final Map.Entry<String, Serializable> entry : propertyMap.entrySet()) {
            final String key = entry.getKey();
            final Serializable val = entry.getValue();
            if (filteredProps == null || !filteredProps.contains(key)) {
                if (s != "") {
                    s += ", ";
                }
                s = s + key + "=" + val;
            }
        }
        Log.debug(prefix + ": " + s);
    }
    
    public void putFilteredPropertyCollection(final Collection<String> properties, final Set<String> filteredProps) {
        final LinkedList<Serializable> collection = new LinkedList<Serializable>(properties);
        if (filteredProps != null) {
            collection.removeAll(filteredProps);
        }
        this.putCollection(collection);
    }
    
    public Collection<Serializable> getCollection() {
        final int count = this.getInt();
        final Collection<Serializable> collection = new LinkedList<Serializable>();
        for (int i = 0; i < count; ++i) {
            collection.add(this.getEncodedObject());
        }
        return collection;
    }
    
    public Map<String, Serializable> getPropertyMap() {
        final int count = this.getInt();
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        for (int i = 0; i < count; ++i) {
            final String key = this.getString();
            final Serializable value = this.getEncodedObject();
            map.put(key, value);
        }
        return map;
    }
    
    public Map<String, Serializable> getTreeMap() {
        final int count = this.getInt();
        final TreeMap<String, Serializable> map = new TreeMap<String, Serializable>();
        for (int i = 0; i < count; ++i) {
            final String key = this.getString();
            final Serializable value = this.getEncodedObject();
            map.put(key, value);
        }
        return map;
    }
    
    @Override
    public int compareTo(final AOByteBuffer buffer) {
        return this.mBB.compareTo(buffer.mBB);
    }
    
    public static void main(final String[] args) {
        try {
            final AOByteBuffer b = new AOByteBuffer(10);
            b.putString("012345678910101010HELLO");
            b.flip();
            final String foo = b.getString();
            System.out.println("printing out: '" + foo + "'");
        }
        catch (Exception e) {
            Log.exception("AOByteBuffer.main caught exception", e);
        }
    }
    
    public static AOByteBuffer putInt(final AOByteBuffer buffer, final int i) {
        buffer.putByte((byte)3);
        return buffer.putInt(i);
    }
    
    public static AOByteBuffer putString(final AOByteBuffer buffer, final String str) {
        buffer.putByte((byte)1);
        return buffer.putString(str);
    }
    
    static {
        AOByteBuffer.classToValueTypeMap = null;
    }
}
