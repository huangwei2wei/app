// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.marshalling;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import atavism.server.engine.OID;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.apache.bcel.classfile.Field;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import atavism.server.network.AOByteBuffer;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.Repository;
import java.util.Iterator;
import java.util.Map;
import atavism.server.util.Log;
import java.util.HashMap;

public class MarshallingRuntime
{
    protected static HashMap<String, ClassProperties> classToClassProperties;
    protected static Class[] marshallers;
    protected static boolean predefinedTypesInstalled;
    public static boolean initialized;
    static final byte[] HEX_CHAR_TABLE;
    protected static final short nonStoredStart = -10;
    protected static final short typeNumPrimitiveBoolean = -9;
    protected static final short firstAtomicTypeNum = -9;
    protected static final short firstPrimitiveAtomicTypeNum = -9;
    protected static final short typeNumPrimitiveByte = -8;
    protected static final short typeNumPrimitiveDouble = -7;
    protected static final short typeNumPrimitiveFloat = -6;
    protected static final short typeNumPrimitiveInteger = -5;
    protected static final short typeNumPrimitiveLong = -4;
    protected static final short typeNumPrimitiveShort = -3;
    protected static final short lastPrimitiveAtomicTypeNum = -3;
    protected static final short builtinStart = 0;
    protected static final short typeNumBoolean = 1;
    protected static final short firstNonPrimitiveAtomicTypeNum = 1;
    protected static final short typeNumByte = 2;
    protected static final short typeNumDouble = 3;
    protected static final short typeNumFloat = 4;
    protected static final short typeNumInteger = 5;
    protected static final short typeNumLong = 6;
    protected static final short typeNumShort = 7;
    protected static final short typeNumString = 8;
    protected static final short lastNonPrimitiveAtomicTypeNum = 8;
    protected static final short lastAtomicTypeNum = 8;
    protected static final short typeNumLinkedList = 9;
    protected static final short typeNumArrayList = 10;
    protected static final short typeNumHashMap = 11;
    protected static final short typeNumLinkedHashMap = 12;
    protected static final short typeNumTreeMap = 13;
    protected static final short typeNumHashSet = 14;
    protected static final short typeNumLinkedHashSet = 15;
    protected static final short typeNumTreeSet = 16;
    protected static final short typeNumByteArray = 17;
    protected static final short registeredBuiltTypeCount = 17;
    protected static final short firstAggregateTypeNum = 9;
    protected static final short lastAggregateTypeNum = 17;
    protected static final short typeNumJavaSerializable = 18;
    protected static final short typeNumBooleanFalse = 19;
    protected static final short typeNumBooleanTrue = 20;
    protected static final short typeNumNull = 21;
    protected static final short firstExpansionTypeNum = 23;
    protected static final short lastExpansionTypeNum = 26;
    protected static final short lastBuiltinTypeNum = 26;
    protected static short firstGeneratedValueType;
    protected static short nextGeneratedValueType;
    
    public static void registerMarshallingClass(final String className, final Short typeNum) {
        if (builtinType(typeNum)) {
            Log.error("For class " + className + ", the explicit type number " + typeNum + " is illegal, because " + " it conflicts with the builtin type numbers");
            return;
        }
        Short n = getTypeNumForClassName(className);
        if (n != null) {
            Log.error("The type number for class '" + className + "' has already been defined as " + n);
            return;
        }
        for (final Map.Entry<String, ClassProperties> entry : MarshallingRuntime.classToClassProperties.entrySet()) {
            n = entry.getValue().typeNum;
            if (n.equals(typeNum)) {
                Log.error("For class '" + className + "', the explicit type number " + typeNum + " is already used by class '" + entry.getKey() + "'");
                return;
            }
        }
        final ClassProperties props = new ClassProperties(className, typeNum, false);
        MarshallingRuntime.classToClassProperties.put(className, props);
    }
    
    public static void registerMarshallingClass(final String className) {
        registerMarshallingClass(className, getNextGeneratedValueType());
    }
    
    protected static short getNextGeneratedValueType() {
        while (getClassForTypeNum(MarshallingRuntime.nextGeneratedValueType) != null) {
            ++MarshallingRuntime.nextGeneratedValueType;
        }
        return MarshallingRuntime.nextGeneratedValueType;
    }
    
    public static void addMarshallingClass(final String className, final Class c) {
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        if (props == null) {
            throwError("MarshallingRuntime.addMarshallingClass: could not look up class '" + className + "'");
        }
        else {
            final Short typeNum = props.typeNum;
            if (MarshallingRuntime.marshallers.length > typeNum && MarshallingRuntime.marshallers[typeNum] != null) {
                throwError("MarshallingRuntime.addMarshallingClass: a marshaller for class '" + className + "' has already been inserted");
            }
            else {
                addMarshaller(c, typeNum);
            }
        }
    }
    
    public static boolean hasMarshallingProperties(final String className) {
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        return props != null;
    }
    
    public static boolean injectedClass(final String className) {
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        return props != null && !props.builtin;
    }
    
    public static byte[] maybeInjectMarshalling(final String className) throws ClassNotFoundException {
        final Short typeNum = classRequiresInjection(className);
        if (typeNum != null) {
            JavaClass clazz = Repository.lookupClass(className);
            if (clazz != null) {
                if (!InjectionGenerator.handlesMarshallable(clazz)) {
                    clazz = InjectionGenerator.instance.maybeInjectMarshalling(clazz, typeNum);
                    final byte[] bytes = clazz.getBytes();
                    return bytes;
                }
            }
            else {
                Log.error("MarshallingRuntime.classRequiresInjection: Could not look up class '" + className + "'");
            }
        }
        return null;
    }
    
    public static void marshalObject(final AOByteBuffer buf, final Object object) {
        if (object == null) {
            writeTypeNum(buf, (short)21);
        }
        else {
            final Class c = object.getClass();
            final Short typeNum = getTypeNumForClass(c);
            if (typeNum == null) {
                writeTypeNum(buf, (short)18);
                marshalSerializable(buf, object);
            }
            else if (typeNum > 26) {
                if (!(object instanceof Marshallable)) {
                    Log.dumpStack("MarshallingRuntime:marshalObject: class '" + c.getName() + "' has typeNum " + typeNum + " but does not support interface Marshallable");
                    writeTypeNum(buf, (short)21);
                }
                else {
                    final Marshallable marshallingObject = (Marshallable)object;
                    writeTypeNum(buf, typeNum);
                    marshallingObject.marshalObject(buf);
                }
            }
            else if (typeNum.equals((short)1)) {
                final Short booleanLiteral = (short)(object ? 20 : 19);
                writeTypeNum(buf, booleanLiteral);
            }
            else {
                writeTypeNum(buf, typeNum);
                switch (typeNum) {
                    case 2: {
                        final Byte byteVal = (Byte)object;
                        buf.putByte(byteVal);
                        break;
                    }
                    case 3: {
                        final Double doubleVal = (Double)object;
                        buf.putDouble(doubleVal);
                        break;
                    }
                    case 4: {
                        final Float floatVal = (Float)object;
                        buf.putFloat(floatVal);
                        break;
                    }
                    case 5: {
                        final Integer integerVal = (Integer)object;
                        buf.putInt(integerVal);
                        break;
                    }
                    case 6: {
                        final Long longVal = (Long)object;
                        buf.putLong(longVal);
                        break;
                    }
                    case 7: {
                        final Short shortVal = (Short)object;
                        buf.putShort(shortVal);
                        break;
                    }
                    case 8: {
                        buf.putString((String)object);
                        break;
                    }
                    case 9: {
                        marshalLinkedList(buf, object);
                        break;
                    }
                    case 10: {
                        marshalArrayList(buf, object);
                        break;
                    }
                    case 11: {
                        marshalHashMap(buf, object);
                        break;
                    }
                    case 12: {
                        marshalLinkedHashMap(buf, object);
                        break;
                    }
                    case 13: {
                        marshalTreeMap(buf, object);
                        break;
                    }
                    case 14: {
                        marshalHashSet(buf, object);
                        break;
                    }
                    case 15: {
                        marshalLinkedHashSet(buf, object);
                        break;
                    }
                    case 16: {
                        marshalTreeSet(buf, object);
                        break;
                    }
                    case 17: {
                        marshalByteArray(buf, object);
                        break;
                    }
                    default: {
                        throwError("In MarshallingRuntime.marshalObject: unknown typeNum '" + typeNum + "'");
                        break;
                    }
                }
            }
        }
    }
    
    public static Object unmarshalObject(final AOByteBuffer buf) {
        final Short typeNum = readTypeNum(buf);
        if (typeNum > 26) {
            final Class marshallingClass = MarshallingRuntime.marshallers[typeNum];
            if (marshallingClass == null) {
                throwError("MarshallingRuntime.unmarshalObject: no marshalling class for typeNum '" + typeNum + "'");
                return null;
            }
            try {
                final Object object = marshallingClass.newInstance();
                final Marshallable marshallingObject = (Marshallable)object;
                return marshallingObject.unmarshalObject(buf);
            }
            catch (Exception e) {
                throwError("MarshallingRuntime.unmarshalObject, exception running unmarshaller: " + e);
                return null;
            }
        }
        switch (typeNum) {
            case 21: {
                return null;
            }
            case 19: {
                return false;
            }
            case 20: {
                return true;
            }
            case 1: {
                return buf.getByte() != 0;
            }
            case 2: {
                return buf.getByte();
            }
            case 3: {
                return buf.getDouble();
            }
            case 4: {
                return buf.getFloat();
            }
            case 5: {
                return buf.getInt();
            }
            case 6: {
                return buf.getLong();
            }
            case 7: {
                return buf.getShort();
            }
            case 8: {
                return buf.getString();
            }
            case 9: {
                return unmarshalLinkedList(buf);
            }
            case 10: {
                return unmarshalArrayList(buf);
            }
            case 11: {
                return unmarshalHashMap(buf);
            }
            case 12: {
                return unmarshalLinkedHashMap(buf);
            }
            case 13: {
                return unmarshalTreeMap(buf);
            }
            case 14: {
                return unmarshalHashSet(buf);
            }
            case 15: {
                return unmarshalLinkedHashSet(buf);
            }
            case 16: {
                return unmarshalTreeSet(buf);
            }
            case 17: {
                return unmarshalByteArray(buf);
            }
            case 18: {
                return unmarshalSerializable(buf);
            }
            default: {
                throwError("In MarshallingRuntime.unmarshalObject: unknown typeNum '" + typeNum + "'");
                return null;
            }
        }
    }
    
    public static void marshalMarshallingObject(final AOByteBuffer buf, final Object object) {
        final Marshallable marshallingObject = (Marshallable)object;
        marshallingObject.marshalObject(buf);
    }
    
    public static Object unmarshalMarshallingObject(final AOByteBuffer buf, final Object object) {
        final Marshallable marshallingObject = (Marshallable)object;
        final Object result = marshallingObject.unmarshalObject(buf);
        return result;
    }
    
    public static String getClassForTypeNum(final Short typeNum) {
        for (final Map.Entry<String, ClassProperties> entry : MarshallingRuntime.classToClassProperties.entrySet()) {
            final Short entryTypeNum = entry.getValue().typeNum;
            if (entryTypeNum.equals(typeNum)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public static String registeredClassesAndTypes() {
        String s = "";
        for (final Map.Entry<String, ClassProperties> entry : MarshallingRuntime.classToClassProperties.entrySet()) {
            final Short typeNum = entry.getValue().typeNum;
            if (builtinType(typeNum)) {
                continue;
            }
            if (s == "") {
                s += ", ";
            }
            s = s + "Class '" + entry.getKey() + "': " + typeNum;
        }
        return s;
    }
    
    protected static void writeTypeNum(final AOByteBuffer buf, final Short typeNum) {
        if (typeNum <= 255) {
            final short b = typeNum;
            buf.putByte((byte)b);
        }
        else {
            final int firstByte = (typeNum >> 8) - 1 + 23;
            final int secondByte = typeNum & 0xFF;
            buf.putByte((byte)firstByte);
            buf.putByte((byte)secondByte);
        }
    }
    
    protected static Short readTypeNum(final AOByteBuffer buf) {
        final int firstByte = byteToIntNoSignExtend(buf.getByte());
        if (firstByte >= 23 && firstByte <= 26) {
            final int secondByte = byteToIntNoSignExtend(buf.getByte());
            final short typeNum = (short)(firstByte - 23 + 1 << 8 | secondByte);
            return typeNum;
        }
        return (short)firstByte;
    }
    
    protected static int byteToIntNoSignExtend(final byte b) {
        return b & 0xFF;
    }
    
    protected static boolean valueTypeNum(final Short typeNum) {
        return typeNum.equals((short)19) || typeNum.equals((short)20) || typeNum.equals((short)21);
    }
    
    public static boolean builtinType(final Short typeNum) {
        return typeNum < 26;
    }
    
    public static boolean builtinType(final String className) {
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        return props != null && props.builtin;
    }
    
    public static Short builtinAggregateTypeNum(final String className) {
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        if (props == null || !props.builtin) {
            return null;
        }
        final short typeNum = props.typeNum;
        if (typeNum >= 9 && typeNum <= 17) {
            return typeNum;
        }
        return null;
    }
    
    protected static boolean marshalledTypeNum(final Short typeNum) {
        return typeNum > 26;
    }
    
    public static Short getTypeNumForClassOrBarf(final Class c) {
        final Short typeNum = getTypeNumForClass(c);
        if (typeNum == null) {
            Log.dumpStack("Did not find class '" + c.getName() + "' in the type num map");
            return 21;
        }
        return typeNum;
    }
    
    protected static Short getTypeNumForClass(final Class c) {
        return getTypeNumForClassName(c.getName());
    }
    
    protected static Short getTypeNumForClassName(final String className) {
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        if (props != null) {
            return props.typeNum;
        }
        return null;
    }
    
    protected static Class getClassForClassName(final String className) {
        try {
            final Class c = Class.forName(className);
            if (c == null) {
                Log.error("MarshallingRuntime.getClassForClassName: could not find class '" + className + "'");
            }
            return c;
        }
        catch (Exception e) {
            Log.exception("MarshallingRuntime.getClassForClassName: could not find class", e);
            return null;
        }
    }
    
    protected static String getClassNameForObject(final Object object) {
        final Class c = object.getClass();
        if (c != null) {
            return c.getName();
        }
        return "<Unknown>";
    }
    
    protected static Short classRequiresInjection(final String className) {
        if (MarshallingRuntime.classToClassProperties == null) {
            return null;
        }
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        if (props != null && !props.injected) {
            return props.typeNum;
        }
        return null;
    }
    
    public static void markInjected(final String className) {
        final ClassProperties props = MarshallingRuntime.classToClassProperties.get(className);
        if (props == null) {
            Log.error("MarshallingRuntime.markInjected: Didn't find class '" + className + "' in map");
        }
        else if (props.injected) {
            Log.error("MarshallingRuntime.markInjected: Class '" + className + "' is already injected");
        }
        else {
            props.injected = true;
        }
    }
    
    protected static void addMarshaller(final Class c, final Short typeNum) {
        if (MarshallingRuntime.marshallers.length <= typeNum) {
            final int newSize = typeNum + 256;
            final Class[] newMarshallers = new Class[newSize];
            for (int i = 0; i < MarshallingRuntime.marshallers.length; ++i) {
                newMarshallers[i] = MarshallingRuntime.marshallers[i];
            }
            MarshallingRuntime.marshallers = newMarshallers;
        }
        Log.info("Added marshaller for class :" + c + " with typeNum: " + typeNum);
        MarshallingRuntime.marshallers[typeNum] = c;
    }
    
    public static void marshalSerializable(final AOByteBuffer buf, final Object object) {
        Log.info("MarshallingRuntime.marshalSerializable: object " + object + ", class " + getClassNameForObject(object));
        ByteArrayOutputStream ba = null;
        if (!(object instanceof Serializable)) {
            Log.error("marshalSerializable: " + objectDescription(object) + " is not Serializable");
            Log.dumpStack();
        }
        else {
            ba = serialHelper(object);
        }
        if (ba == null) {
            ba = serialHelper(null);
        }
        final byte[] cereal = ba.toByteArray();
        buf.putInt(cereal.length);
        buf.putBytes(cereal, 0, cereal.length);
    }
    
    private static ByteArrayOutputStream serialHelper(final Object object) {
        try {
            final ByteArrayOutputStream ba = new ByteArrayOutputStream();
            final ObjectOutputStream os = new ObjectOutputStream(ba);
            if (!(object instanceof Serializable)) {
                throw new RuntimeException("MarshallingRuntime.serialHelper: " + objectDescription(object) + " is not Serializable");
            }
            os.writeObject(object);
            os.flush();
            ba.flush();
            return ba;
        }
        catch (Exception e) {
            Log.exception("Exception during marshalSerializable of " + objectDescription(object) + "; writing null value", e);
            return null;
        }
    }
    
    private static String objectDescription(final Object object) {
        if (object == null) {
            return "null";
        }
        return " object " + object + " of class " + getClassNameForObject(object);
    }
    
    public static Object unmarshalSerializable(final AOByteBuffer buf) {
        int length = 0;
        try {
            length = buf.getInt();
            final byte[] cereal = new byte[length];
            buf.getBytes(cereal, 0, length);
            final ByteArrayInputStream bs = new ByteArrayInputStream(cereal);
            final ObjectInputStream ois = new ObjectInputStream(bs);
            final Object object = ois.readObject();
            Log.info("MarshallingRuntime.unmarshalSerializable: " + objectDescription(object));
            return object;
        }
        catch (Exception e) {
            Log.exception("MarshallingRuntime.unmarshalSerializable", e);
            return null;
        }
    }
    
    protected static void throwError(final String msg) {
        Log.error(msg);
        throw new RuntimeException(msg);
    }
    
    private static void marshalListInternal(final AOByteBuffer buf, final Object object) {
        final List list = (List)object;
        buf.putInt(list.size());
        for (final Object elt : list) {
            marshalObject(buf, elt);
        }
    }
    
    private static Object unmarshalListInternal(final AOByteBuffer buf, final List<Object> list, final int count) {
        for (int i = 0; i < count; ++i) {
            list.add(unmarshalObject(buf));
        }
        return list;
    }
    
    private static void marshalMapInternal(final AOByteBuffer buf, final Object object) {
        final Map<Object, Object> map = (Map<Object, Object>)object;
        buf.putInt(map.size());
        for (final Map.Entry<Object, Object> entry : map.entrySet()) {
            marshalObject(buf, entry.getKey());
            marshalObject(buf, entry.getValue());
        }
    }
    
    private static Object unmarshalMapInternal(final AOByteBuffer buf, final Map<Object, Object> map) {
        for (int count = buf.getInt(), i = 0; i < count; ++i) {
            map.put(unmarshalObject(buf), unmarshalObject(buf));
        }
        return map;
    }
    
    private static void marshalSetInternal(final AOByteBuffer buf, final Object object) {
        final Set<Object> set = (Set<Object>)object;
        buf.putInt(set.size());
        for (final Object obj : set) {
            marshalObject(buf, obj);
        }
    }
    
    private static Object unmarshalSetInternal(final AOByteBuffer buf, final Set<Object> set) {
        for (int count = buf.getInt(), i = 0; i < count; ++i) {
            set.add(unmarshalObject(buf));
        }
        return set;
    }
    
    public static void marshalLinkedList(final AOByteBuffer buf, final Object object) {
        marshalListInternal(buf, object);
    }
    
    public static Object unmarshalLinkedList(final AOByteBuffer buf) {
        final int count = buf.getInt();
        final LinkedList<Object> list = new LinkedList<Object>();
        return unmarshalListInternal(buf, list, count);
    }
    
    public static void marshalArrayList(final AOByteBuffer buf, final Object object) {
        marshalListInternal(buf, object);
    }
    
    public static Object unmarshalArrayList(final AOByteBuffer buf) {
        final int count = buf.getInt();
        final ArrayList<Object> arrayList = new ArrayList<Object>(count);
        return unmarshalListInternal(buf, arrayList, count);
    }
    
    public static void marshalHashMap(final AOByteBuffer buf, final Object object) {
        marshalMapInternal(buf, object);
    }
    
    public static Object unmarshalHashMap(final AOByteBuffer buf) {
        final HashMap<Object, Object> map = new HashMap<Object, Object>();
        return unmarshalMapInternal(buf, map);
    }
    
    public static void marshalLinkedHashMap(final AOByteBuffer buf, final Object object) {
        marshalMapInternal(buf, object);
    }
    
    public static Object unmarshalLinkedHashMap(final AOByteBuffer buf) {
        final LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
        return unmarshalMapInternal(buf, map);
    }
    
    public static void marshalTreeMap(final AOByteBuffer buf, final Object object) {
        marshalMapInternal(buf, object);
    }
    
    public static Object unmarshalTreeMap(final AOByteBuffer buf) {
        final TreeMap<Object, Object> map = new TreeMap<Object, Object>();
        return unmarshalMapInternal(buf, map);
    }
    
    public static void marshalHashSet(final AOByteBuffer buf, final Object object) {
        marshalSetInternal(buf, object);
    }
    
    public static Object unmarshalHashSet(final AOByteBuffer buf) {
        final HashSet<Object> set = new HashSet<Object>();
        return unmarshalSetInternal(buf, set);
    }
    
    public static void marshalLinkedHashSet(final AOByteBuffer buf, final Object object) {
        marshalSetInternal(buf, object);
    }
    
    public static Object unmarshalLinkedHashSet(final AOByteBuffer buf) {
        final LinkedHashSet<Object> set = new LinkedHashSet<Object>();
        return unmarshalSetInternal(buf, set);
    }
    
    public static void marshalTreeSet(final AOByteBuffer buf, final Object object) {
        marshalSetInternal(buf, object);
    }
    
    public static Object unmarshalTreeSet(final AOByteBuffer buf) {
        final TreeSet<Object> set = new TreeSet<Object>();
        return unmarshalSetInternal(buf, set);
    }
    
    public static void marshalByteArray(final AOByteBuffer buf, final Object object) {
        final byte[] bytes = (byte[])object;
        buf.putInt(bytes.length);
        for (final byte b : bytes) {
            buf.putByte(b);
        }
    }
    
    public static Object unmarshalByteArray(final AOByteBuffer buf) {
        final int count = buf.getInt();
        final byte[] bytes = new byte[count];
        for (int i = 0; i < count; ++i) {
            bytes[i] = buf.getByte();
        }
        return bytes;
    }
    
    public static HashSet<ClassNameAndTypeNumber> getClassesToBeMarshalled() {
        final HashSet<ClassNameAndTypeNumber> pairs = new HashSet<ClassNameAndTypeNumber>();
        for (final Map.Entry<String, ClassProperties> entry : MarshallingRuntime.classToClassProperties.entrySet()) {
            final Short typeNum = entry.getValue().typeNum;
            if (typeNum <= 26) {
                continue;
            }
            pairs.add(new ClassNameAndTypeNumber(entry.getKey(), typeNum));
        }
        return pairs;
    }
    
    protected static void addPrimitiveToTypeMap(final Object object, final Short typeNum) {
        final String className = object.getClass().getName();
        final ClassProperties props = new ClassProperties(className, typeNum, true);
        MarshallingRuntime.classToClassProperties.put(className, props);
    }
    
    protected static boolean checkTypeReferences() {
        boolean someMissing = false;
        final Map<JavaClass, LinkedList<JavaClass>> missingTypes = new HashMap<JavaClass, LinkedList<JavaClass>>();
        for (final Map.Entry<String, ClassProperties> entry : MarshallingRuntime.classToClassProperties.entrySet()) {
            final String className = entry.getKey();
            final ClassProperties props = entry.getValue();
            if (props.builtin) {
                continue;
            }
            final JavaClass c = javaClassOrNull(className);
            if (c == null) {
                Log.error("Could not find registered class '" + className + "'");
                someMissing = true;
            }
            else {
                if (!c.isPublic()) {
                    Log.error("Class '" + className + "' is not a public class");
                    someMissing = true;
                }
                final Short n = props.typeNum;
                if (!marshalledTypeNum(n)) {
                    continue;
                }
                if (!c.isClass()) {
                    Log.error("Class '" + className + "' is an interface, not an instantiable class");
                    someMissing = true;
                }
                else if (!hasNoArgConstructor(c)) {
                    Log.error("Class '" + className + "' does not have a public, no-args constructor");
                    someMissing = true;
                }
                else {
                    final JavaClass superclass = InjectionGenerator.getValidSuperclass(c);
                    if (superclass != null) {
                        checkClassPresent(c, superclass, missingTypes);
                    }
                    final LinkedList<Field> fields = InjectionGenerator.getValidClassFields(c);
                    for (final Field f : fields) {
                        final Type fieldType = f.getType();
                        if (fieldType.getType() == 13) {
                            Log.error("For class '" + className + "', field '" + f.getName() + "' is an array, and arrays are not supported");
                            someMissing = true;
                        }
                        final String rawName = InjectionGenerator.nonPrimitiveObjectTypeName(fieldType);
                        if (rawName != null) {
                            final String name = translateFieldTypeName(rawName);
                            if (name.equals("java.lang.Object")) {
                                continue;
                            }
                            final JavaClass fieldClass = javaClassOrNull(name);
                            if (fieldClass.isEnum()) {
                                Log.error("For class '" + className + "', field '" + f.getName() + "' is an enum, and enums are not supported");
                                someMissing = true;
                            }
                            if (fieldClass == null) {
                                Log.error("For class '" + className + "', could not find field '" + f.getName() + "' class '" + name + "'");
                                someMissing = true;
                            }
                            else {
                                checkClassPresent(c, fieldClass, missingTypes);
                            }
                        }
                    }
                }
            }
        }
        if (missingTypes.size() > 0) {
            for (final Map.Entry<JavaClass, LinkedList<JavaClass>> entry2 : missingTypes.entrySet()) {
                final JavaClass c2 = entry2.getKey();
                final LinkedList<JavaClass> refs = entry2.getValue();
                String s = "";
                for (final JavaClass ref : refs) {
                    if (s != "") {
                        s += ", ";
                    }
                    s = s + "'" + getSimpleClassName(ref) + "'";
                }
                someMissing = true;
                Log.error("Missing type '" + getSimpleClassName(c2) + "' is referred to by type(s) " + s);
            }
        }
        if (someMissing) {
            Log.error("Aborting code generation due to missing or incorrect types");
        }
        return someMissing;
    }
    
    protected static boolean hasNoArgConstructor(final JavaClass c) {
        final Method[] methods = c.getMethods();
        boolean sawConstructor = false;
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            final String methodName = method.getName();
            if (methodName.equals("<init>")) {
                sawConstructor = true;
                if (method.getArgumentTypes().length == 0 && method.isPublic() && !method.isStatic()) {
                    return true;
                }
            }
        }
        return !sawConstructor;
    }
    
    protected static void checkClassPresent(final JavaClass referringClass, final JavaClass referredClass, final Map<JavaClass, LinkedList<JavaClass>> missingTypes) {
        final Short s = getTypeNumFromJavaClass(referredClass);
        if (s == null) {
            LinkedList<JavaClass> references = missingTypes.get(referredClass);
            if (references == null) {
                references = new LinkedList<JavaClass>();
                missingTypes.put(referredClass, references);
            }
            if (!references.contains(referringClass)) {
                references.add(referringClass);
            }
        }
    }
    
    protected static Short getTypeNumFromJavaClass(final JavaClass c) {
        final String className = c.getClassName();
        final Short typeNum = getTypeNumForClassName(className);
        return typeNum;
    }
    
    protected static String getSimpleClassName(final JavaClass c) {
        final String name = c.getClassName();
        final int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }
    
    protected static String translateFieldTypeName(final String s) {
        if (InjectionGenerator.interfaceClass(s)) {
            return "java.lang.Object";
        }
        if (builtinType(s)) {
            return s;
        }
        if (s.equals("java.util.List")) {
            return "java.util.LinkedList";
        }
        if (s.equals("java.util.Map")) {
            return "java.util.HashMap";
        }
        if (s.equals("java.util.Set")) {
            return "java.util.HashSet";
        }
        if (s.equals("java.io.Serializable")) {
            return "java.lang.Object";
        }
        return s;
    }
    
    protected static JavaClass javaClassOrNull(final String className) {
        try {
            return Repository.lookupClass(className);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static void installPredefinedTypes() {
        if (MarshallingRuntime.predefinedTypesInstalled) {
            return;
        }
        MarshallingRuntime.marshallers = new Class[256];
        final Boolean booleanVal = true;
        final Byte byteVal = 3;
        final Short shortVal = 3;
        final Integer integerVal = 3;
        final Long longVal = 3L;
        final Float floatVal = 3.0f;
        final Double doubleVal = 3.0;
        final String stringVal = "3";
        final LinkedList listVal = new LinkedList();
        final ArrayList arrayListVal = new ArrayList();
        final HashMap hashMapVal = new HashMap();
        final LinkedHashMap linkedHashMapVal = new LinkedHashMap();
        final TreeMap treeMapVal = new TreeMap();
        final HashSet hashSetVal = new HashSet();
        final LinkedHashSet linkedHashSetVal = new LinkedHashSet();
        final TreeSet treeSetVal = new TreeSet();
        final byte[] byteArrayVal = new byte[2];
        final OID oidVal = OID.fromLong(3L);
        addPrimitiveToTypeMap(booleanVal, (short)1);
        addPrimitiveToTypeMap(byteVal, (short)2);
        addPrimitiveToTypeMap(doubleVal, (short)3);
        addPrimitiveToTypeMap(floatVal, (short)4);
        addPrimitiveToTypeMap(integerVal, (short)5);
        addPrimitiveToTypeMap(longVal, (short)6);
        addPrimitiveToTypeMap(shortVal, (short)7);
        addPrimitiveToTypeMap(stringVal, (short)8);
        addPrimitiveToTypeMap(listVal, (short)9);
        addPrimitiveToTypeMap(arrayListVal, (short)10);
        addPrimitiveToTypeMap(hashMapVal, (short)11);
        addPrimitiveToTypeMap(linkedHashMapVal, (short)12);
        addPrimitiveToTypeMap(treeMapVal, (short)13);
        addPrimitiveToTypeMap(hashSetVal, (short)14);
        addPrimitiveToTypeMap(linkedHashSetVal, (short)15);
        addPrimitiveToTypeMap(treeSetVal, (short)16);
        addPrimitiveToTypeMap(byteArrayVal, (short)17);
        MarshallingRuntime.predefinedTypesInstalled = true;
    }
    
    protected static void processMarshallers(final String marshallerFile) {
        try {
            if (Log.loggingDebug) {
                Log.debug("Processing marshaller file '" + marshallerFile + "'");
            }
            final File f = new File(marshallerFile);
            if (f.exists()) {
                final FileReader fReader = new FileReader(f);
                final BufferedReader in = new BufferedReader(fReader);
                String originalLine = null;
                while ((originalLine = in.readLine()) != null) {
                    String line = originalLine.trim();
                    final int pos = line.indexOf("#");
                    if (pos >= 0) {
                        line = line.substring(0, pos).trim();
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.indexOf(" ") > 0) {
                        Log.error("In marshallers file '" + marshallerFile + "', illegal line '" + originalLine + "'");
                    }
                    else {
                        final String[] args = line.split(",", 2);
                        if (args.length == 2) {
                            Short typeNum;
                            try {
                                typeNum = Short.decode(args[1]);
                            }
                            catch (NumberFormatException e2) {
                                Log.error("In marshallers file '" + marshallerFile + "', illegal type number format in line '" + originalLine + "'");
                                continue;
                            }
                            registerMarshallingClass(args[0], typeNum);
                        }
                        else {
                            registerMarshallingClass(args[0]);
                        }
                    }
                }
                in.close();
                Log.debug("Processing of marshallers file '" + marshallerFile + "' completed");
            }
            else {
                Log.warn("Didn't find marshallers file '" + marshallerFile + "'");
            }
        }
        catch (Exception e) {
            Log.exception("MarshallingRuntime.processMarshallers", e);
            System.exit(1);
        }
    }
    
    protected static void registerMarshallingClasses(final LinkedList<String> scripts) {
        for (final String script : scripts) {
            processMarshallers(script);
        }
    }
    
    public static boolean initialize(final String[] argv) {
        if (MarshallingRuntime.initialized) {
            Log.dumpStack("MarshallingRuntime.initialize() called twice!");
        }
        Log.info("Entered MarshallingRuntime.initialize()");
        final LinkedList<String> scripts = new LinkedList<String>();
        boolean generateClassFiles = false;
        String outputDir = "";
        String typeNumFileName = "";
        boolean listGeneratedCode = false;
        for (int i = 0; i < argv.length; ++i) {
            final String flag = argv[i];
            if (flag.equals("-m")) {
                ++i;
                final String arg = argv[i];
                scripts.add(arg);
            }
            else if (flag.equals("-r")) {
                generateClassFiles = true;
            }
            else if (flag.equals("-o")) {
                ++i;
                outputDir = argv[i];
            }
            else if (flag.equals("-t")) {
                ++i;
                typeNumFileName = argv[i];
            }
            else if (flag.equals("-g")) {
                listGeneratedCode = true;
            }
        }
        Log.debug("MarshallingRuntime.initialize: Installing primitive types");
        installPredefinedTypes();
        Log.debug("MarshallingRuntime.initialize: Initializing InjectionGenerator");
        InjectionGenerator.initialize(generateClassFiles, outputDir, listGeneratedCode);
        Log.debug("MarshallingRuntime.initialize: Registering Marshalling Classes");
        registerMarshallingClasses(scripts);
        final int countRegistered = MarshallingRuntime.classToClassProperties.size() - 17;
        Log.info("MarshallingRuntime.initialize: Registered " + countRegistered + " marshalling classes");
        final boolean broken = checkTypeReferences();
        Log.debug("MarshallingRuntime.initialize: Finished checking type references");
        if (!broken) {
            injectAllClasses(outputDir, typeNumFileName);
        }
        Repository.clearCache();
        MarshallingRuntime.initialized = true;
        return broken;
    }
    
    public static void initializeBatch(final String typeNumFileName) {
        Log.info("Entered MarshallingRuntime.initializeBatch: reading type nums from '" + typeNumFileName + "'");
        installPredefinedTypes();
        final File typeNumFile = new File(typeNumFileName);
        if (!typeNumFile.exists()) {
            Log.error("MarshallingRuntime.initializeBatch: type num file '" + typeNumFileName + "' does not exist!");
            return;
        }
        try {
            final FileReader reader = new FileReader(typeNumFile);
            final BufferedReader in = new BufferedReader(reader);
            int i = 0;
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                final String[] fields = line.split(",");
                final String className = fields[0];
                final short typeNum = (short)Integer.parseInt(fields[1]);
                final ClassProperties props = new ClassProperties(className, typeNum, false);
                MarshallingRuntime.classToClassProperties.put(className, props);
                final Class c = Class.forName(className);
                addMarshaller(c, typeNum);
                ++i;
            }
            Log.info("Entered MarshallingRuntime.initializeBatch: Registered " + i + " classes");
            in.close();
        }
        catch (Exception e) {
            Log.exception("MarshallingRuntime.initializeBatch: Exception reading type num file", e);
        }
    }
    
    public static void injectAllClasses(final String outputDir, final String typeNumFileName) {
        BufferedWriter out = null;
        if (outputDir != "") {
            try {
                final File typeNumFile = new File(typeNumFileName);
                Log.info("Writing type num files to '" + typeNumFile.getName() + "'");
                final FileWriter writer = new FileWriter(typeNumFile);
                out = new BufferedWriter(writer);
            }
            catch (Exception e) {
                Log.exception("MarshallingRuntime.injectAllClasses: Exception opening typenum file", e);
            }
        }
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date();
        final String dateString = dateFormat.format(date);
        try {
            out.write("# This is a generated file - - do not edit!  Written at " + dateString + "\n");
        }
        catch (IOException e2) {
            Log.exception("MarshallingRuntime.injectAllClasses: Exception writing typenum file", e2);
        }
        for (final Map.Entry<String, ClassProperties> entry : MarshallingRuntime.classToClassProperties.entrySet()) {
            final String className = entry.getKey();
            final ClassProperties props = entry.getValue();
            final Short typeNum = props.typeNum;
            if (props.builtin) {
                continue;
            }
            if (outputDir != "") {
                if (!InjectionGenerator.handlesMarshallable(className)) {
                    try {
                        maybeInjectMarshalling(className);
                    }
                    catch (ClassNotFoundException e4) {
                        Log.error("MarshallingRuntime.injectAllClasses: Could not load class '" + className + "'");
                    }
                }
                try {
                    out.write(className + "," + typeNum + "\n");
                }
                catch (IOException e3) {
                    Log.exception("MarshallingRuntime.injectAllClasses: Exception writing typenum file", e3);
                }
            }
            else {
                final Class c = getClassForClassName(className);
                if (InjectionGenerator.handlesMarshallable(className)) {
                    addMarshaller(c, typeNum);
                    Log.debug("Recorded by-hand marshaller '" + className + "', typeNum " + typeNum + "/0x" + Integer.toHexString(typeNum));
                }
                else {
                    if (c != null) {
                        continue;
                    }
                    Log.error("MarshallingRuntime.injectAllClasses: Class.forName('" + className + "' did not return the Class object");
                }
            }
        }
        if (outputDir != "") {
            try {
                out.close();
            }
            catch (IOException e2) {
                Log.exception("MarshallingRuntime.injectAllClasses: Exception closing typenum file", e2);
            }
        }
    }
    
    public static String getHexString(final byte[] raw) {
        final byte[] hex = new byte[2 * raw.length];
        int index = 0;
        for (final byte b : raw) {
            final int v = b & 0xFF;
            hex[index++] = MarshallingRuntime.HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = MarshallingRuntime.HEX_CHAR_TABLE[v & 0xF];
        }
        try {
            return new String(hex, "ASCII");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    static {
        MarshallingRuntime.classToClassProperties = new HashMap<String, ClassProperties>();
        MarshallingRuntime.predefinedTypesInstalled = false;
        MarshallingRuntime.initialized = false;
        HEX_CHAR_TABLE = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
        MarshallingRuntime.firstGeneratedValueType = 27;
        MarshallingRuntime.nextGeneratedValueType = MarshallingRuntime.firstGeneratedValueType;
    }
    
    public static class ClassNameAndTypeNumber
    {
        public String className;
        public Short typeNum;
        
        public ClassNameAndTypeNumber(final String className, final Short typeNum) {
            this.className = className;
            this.typeNum = typeNum;
        }
    }
    
    public static class ClassProperties
    {
        String className;
        Short typeNum;
        boolean builtin;
        boolean injected;
        
        public ClassProperties(final String className, final Short typeNum, final boolean builtin) {
            this.className = className;
            this.typeNum = typeNum;
            this.builtin = builtin;
            this.injected = false;
        }
    }
}
