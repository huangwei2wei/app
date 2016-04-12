// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.io.ObjectInput;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import atavism.server.objects.Color;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import java.util.HashMap;
import java.util.Map;

public class SerialUtils
{
    private static final byte valueTypeNull = 0;
    private static final byte valueTypeString = 1;
    private static final byte valueTypeLong = 2;
    private static final byte valueTypeInteger = 3;
    private static final byte valueTypeBoolean = 4;
    private static final byte valueTypeBooleanFalse = 4;
    private static final byte valueTypeBooleanTrue = 5;
    private static final byte valueTypeFloat = 6;
    private static final byte valueTypePoint = 7;
    private static final byte valueTypeAOVector = 8;
    private static final byte valueTypeQuaternion = 9;
    private static final byte valueTypeColor = 10;
    private static final byte valueTypeLinkedList = 11;
    private static final byte valueTypeHashSet = 12;
    private static final byte valueTypeHashMap = 13;
    private static final byte valueTypeObject = 100;
    private static Map<Class, Byte> classToValueTypeMap;
    
    private static void initializeClassToValueTypeMap() {
        final Long v1 = 3L;
        final Integer v2 = 3;
        final Boolean v3 = true;
        final Float v4 = 3.0f;
        (SerialUtils.classToValueTypeMap = new HashMap<Class, Byte>()).put(new String().getClass(), (byte)1);
        SerialUtils.classToValueTypeMap.put(v1.getClass(), (byte)2);
        SerialUtils.classToValueTypeMap.put(v2.getClass(), (byte)3);
        SerialUtils.classToValueTypeMap.put(v3.getClass(), (byte)4);
        SerialUtils.classToValueTypeMap.put(v4.getClass(), (byte)6);
        SerialUtils.classToValueTypeMap.put(new Point().getClass(), (byte)7);
        SerialUtils.classToValueTypeMap.put(new AOVector().getClass(), (byte)8);
        SerialUtils.classToValueTypeMap.put(new Quaternion().getClass(), (byte)9);
        SerialUtils.classToValueTypeMap.put(new Color().getClass(), (byte)10);
        SerialUtils.classToValueTypeMap.put(new LinkedList().getClass(), (byte)11);
        SerialUtils.classToValueTypeMap.put(new HashSet().getClass(), (byte)12);
        SerialUtils.classToValueTypeMap.put(new HashMap().getClass(), (byte)13);
    }
    
    public static void writeEncodedObject(final ObjectOutputStream out, final Object val) throws IOException, ClassNotFoundException {
        if (SerialUtils.classToValueTypeMap == null) {
            initializeClassToValueTypeMap();
        }
        if (val == null) {
            out.writeByte(0);
        }
        else {
            final Class c = val.getClass();
            Byte index = SerialUtils.classToValueTypeMap.get(c);
            if (index == null) {
                index = 100;
            }
            switch (index) {
                case 1: {
                    out.writeByte(1);
                    out.writeUTF((String)val);
                    break;
                }
                case 2: {
                    out.writeByte(2);
                    out.writeLong((long)val);
                    break;
                }
                case 3: {
                    out.writeByte(3);
                    out.writeInt((int)val);
                    break;
                }
                case 4: {
                    out.writeByte(((boolean)val) ? 5 : 4);
                    break;
                }
                case 6: {
                    out.writeByte(6);
                    out.writeFloat((float)val);
                    break;
                }
                case 7: {
                    out.writeByte(7);
                    ((Point)val).writeExternal(out);
                    break;
                }
                case 8: {
                    out.writeByte(8);
                    ((AOVector)val).writeExternal(out);
                    break;
                }
                case 9: {
                    out.writeByte(9);
                    ((Quaternion)val).writeExternal(out);
                    break;
                }
                case 10: {
                    out.writeByte(10);
                    ((Color)val).writeExternal(out);
                    break;
                }
                case 100: {
                    out.writeByte(100);
                    out.writeObject(val);
                    break;
                }
                case 11: {
                    out.writeByte(11);
                    final LinkedList list = (LinkedList)val;
                    out.writeInt(list.size());
                    for (final Object obj : list) {
                        writeEncodedObject(out, obj);
                    }
                    break;
                }
                case 12: {
                    out.writeByte(12);
                    final HashSet set = (HashSet)val;
                    out.writeInt(set.size());
                    for (final Object obj2 : set) {
                        writeEncodedObject(out, obj2);
                    }
                    break;
                }
                case 13: {
                    out.writeByte(13);
                    final HashMap<String, Object> map = (HashMap<String, Object>)val;
                    out.writeInt(map.size());
                    for (final Map.Entry<String, Object> entry : map.entrySet()) {
                        out.writeUTF(entry.getKey());
                        writeEncodedObject(out, entry.getValue());
                    }
                    break;
                }
                default: {
                    Log.error("WorldManagerClient.writeEncodedObject: index " + index + " out of bounds; class " + c.getName());
                    break;
                }
            }
        }
    }
    
    public static Serializable readEncodedObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final byte typecode = in.readByte();
        switch (typecode) {
            case 0: {
                return null;
            }
            case 1: {
                return in.readUTF();
            }
            case 2: {
                return in.readLong();
            }
            case 3: {
                return in.readInt();
            }
            case 4: {
                return false;
            }
            case 5: {
                return true;
            }
            case 6: {
                return in.readFloat();
            }
            case 7: {
                final Point p = new Point();
                p.readExternal(in);
                return p;
            }
            case 8: {
                final AOVector v = new AOVector();
                v.readExternal(in);
                return v;
            }
            case 9: {
                final Quaternion q = new Quaternion();
                q.readExternal(in);
                return q;
            }
            case 10: {
                final Color color = new Color();
                color.readExternal(in);
                return color;
            }
            case 100: {
                return (Serializable)in.readObject();
            }
            case 11: {
                final int count = in.readInt();
                final LinkedList<Object> list = new LinkedList<Object>();
                for (int i = 0; i < count; ++i) {
                    list.add(readEncodedObject(in));
                }
                return list;
            }
            case 12: {
                final int count = in.readInt();
                final HashSet<Object> set = new HashSet<Object>();
                for (int j = 0; j < count; ++j) {
                    set.add(readEncodedObject(in));
                }
                return set;
            }
            case 13: {
                final int count = in.readInt();
                final HashMap<String, Object> map = new HashMap<String, Object>();
                for (int k = 0; k < count; ++k) {
                    final String key = in.readUTF();
                    final Object value = readEncodedObject(in);
                    map.put(key, value);
                }
                return map;
            }
            default: {
                Log.error("WorldManagerClient.readObjectUtility: Illegal value type code " + typecode);
                return null;
            }
        }
    }
    
    public static void writePropertyMap(final ObjectOutputStream out, final Map<String, Object> propertyMap) throws IOException, ClassNotFoundException {
        out.writeInt((propertyMap == null) ? 0 : propertyMap.size());
        if (propertyMap != null) {
            for (final Map.Entry<String, Object> entry : propertyMap.entrySet()) {
                final String key = entry.getKey();
                Log.debug("Writing property map with key: " + key);
                final Object val = entry.getValue();
                out.writeUTF(key);
                writeEncodedObject(out, val);
            }
        }
    }
    
    public static Map<String, Object> readPropertyMap(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final Integer count = in.readInt();
        if (count == 0) {
            return null;
        }
        final Map<String, Object> props = new HashMap<String, Object>();
        for (int i = 0; i < count; ++i) {
            final String key = in.readUTF();
            Log.debug("Reading property map with key: " + key);
            final Serializable val = readEncodedObject(in);
            props.put(key, val);
        }
        return props;
    }
    
    public static void writeSerializablePropertyMap(final ObjectOutputStream out, final Map<String, Serializable> propertyMap) throws IOException, ClassNotFoundException {
        out.writeInt((propertyMap == null) ? 0 : propertyMap.size());
        for (final Map.Entry<String, Serializable> entry : propertyMap.entrySet()) {
            final String key = entry.getKey();
            Log.debug("Writing serializable property map with key: " + key);
            final Object val = entry.getValue();
            out.writeUTF(key);
            writeEncodedObject(out, val);
        }
    }
    
    public static Map<String, Serializable> readSerializablePropertyMap(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final Integer count = in.readInt();
        if (count == 0) {
            return null;
        }
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        for (int i = 0; i < count; ++i) {
            final String key = in.readUTF();
            Log.debug("Reading serializable property map with key: " + key);
            final Serializable val = readEncodedObject(in);
            props.put(key, val);
        }
        return props;
    }
    
    static {
        SerialUtils.classToValueTypeMap = null;
    }
}
