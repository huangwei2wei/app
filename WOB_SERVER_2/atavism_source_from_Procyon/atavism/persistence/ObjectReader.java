// 
// Decompiled by Procyon v0.5.30
// 

package atavism.persistence;

import atavism.server.engine.Namespace;
import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.reflect.Array;
import java.util.Collection;
import java.lang.reflect.Field;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.HashMap;
import java.io.Reader;
import java.util.Set;
import java.util.Map;

public class ObjectReader
{
    private String input;
    private static Map<String, Class> typeMap;
    private static Set<Class> primitiveBoxTypes;
    private Reader reader;
    private PackageAliases aliases;
    
    public ObjectReader(final String input) {
        (ObjectReader.typeMap = new HashMap<String, Class>()).put("long", Long.TYPE);
        ObjectReader.typeMap.put("int", Integer.TYPE);
        ObjectReader.typeMap.put("boolean", Boolean.TYPE);
        ObjectReader.typeMap.put("short", Short.TYPE);
        ObjectReader.typeMap.put("byte", Byte.TYPE);
        ObjectReader.typeMap.put("char", Character.TYPE);
        ObjectReader.typeMap.put("double", Double.TYPE);
        ObjectReader.typeMap.put("float", Float.TYPE);
        ObjectReader.typeMap.put("String", String.class);
        ObjectReader.typeMap.put("null", NullClass.class);
        ObjectReader.typeMap.put("Long", Long.class);
        ObjectReader.typeMap.put("Integer", Integer.class);
        ObjectReader.typeMap.put("Boolean", Boolean.class);
        ObjectReader.typeMap.put("Short", Short.class);
        ObjectReader.typeMap.put("Byte", Byte.class);
        ObjectReader.typeMap.put("Character", Character.class);
        ObjectReader.typeMap.put("Double", Double.class);
        ObjectReader.typeMap.put("Float", Float.class);
        (ObjectReader.primitiveBoxTypes = new HashSet<Class>()).add(Long.class);
        ObjectReader.primitiveBoxTypes.add(Integer.class);
        ObjectReader.primitiveBoxTypes.add(Boolean.class);
        ObjectReader.primitiveBoxTypes.add(Short.class);
        ObjectReader.primitiveBoxTypes.add(Byte.class);
        ObjectReader.primitiveBoxTypes.add(Character.class);
        ObjectReader.primitiveBoxTypes.add(Double.class);
        ObjectReader.primitiveBoxTypes.add(Float.class);
        this.input = input;
        this.reader = new StringReader(input);
        this.aliases = new PackageAliases();
    }
    
    public ObjectReader(final String input, final PackageAliases aliases) {
        (ObjectReader.typeMap = new HashMap<String, Class>()).put("long", Long.TYPE);
        ObjectReader.typeMap.put("int", Integer.TYPE);
        ObjectReader.typeMap.put("boolean", Boolean.TYPE);
        ObjectReader.typeMap.put("short", Short.TYPE);
        ObjectReader.typeMap.put("byte", Byte.TYPE);
        ObjectReader.typeMap.put("char", Character.TYPE);
        ObjectReader.typeMap.put("double", Double.TYPE);
        ObjectReader.typeMap.put("float", Float.TYPE);
        ObjectReader.typeMap.put("String", String.class);
        ObjectReader.typeMap.put("null", NullClass.class);
        ObjectReader.typeMap.put("Long", Long.class);
        ObjectReader.typeMap.put("Integer", Integer.class);
        ObjectReader.typeMap.put("Boolean", Boolean.class);
        ObjectReader.typeMap.put("Short", Short.class);
        ObjectReader.typeMap.put("Byte", Byte.class);
        ObjectReader.typeMap.put("Character", Character.class);
        ObjectReader.typeMap.put("Double", Double.class);
        ObjectReader.typeMap.put("Float", Float.class);
        (ObjectReader.primitiveBoxTypes = new HashSet<Class>()).add(Long.class);
        ObjectReader.primitiveBoxTypes.add(Integer.class);
        ObjectReader.primitiveBoxTypes.add(Boolean.class);
        ObjectReader.primitiveBoxTypes.add(Short.class);
        ObjectReader.primitiveBoxTypes.add(Byte.class);
        ObjectReader.primitiveBoxTypes.add(Character.class);
        ObjectReader.primitiveBoxTypes.add(Double.class);
        ObjectReader.primitiveBoxTypes.add(Float.class);
        this.input = input;
        this.reader = new StringReader(input);
        this.aliases = aliases;
    }
    
    public Object readObject() throws IOException, ClassNotFoundException {
        final TypedObject typedObject = this.readTypedObject();
        if (typedObject == null) {
            return null;
        }
        return typedObject.value;
    }
    
    public TypedObject readTypedObject() throws IOException, ClassNotFoundException {
        final Class c = this.readType();
        if (c == null) {
            return null;
        }
        final int size = this.readSize();
        this.reader.mark(Integer.MAX_VALUE);
        final char ch = (char)this.reader.read();
        System.out.println(ch);
        if (ch == '=') {
            return new TypedObject(c, this.readValue(c, size));
        }
        this.reader.reset();
        return new TypedObject(c, null);
    }
    
    private Class readType() throws IOException, ClassNotFoundException {
        final String primitive = this.readIdentifier();
        Class c = ObjectReader.typeMap.get(primitive);
        if (c != null) {
            return c;
        }
        if (primitive.equals("array")) {
            this.readWhiteSpace();
            c = this.readClass();
            if (c.isPrimitive()) {
                return this.primitiveArrayType(c);
            }
            return Class.forName("[L" + c.getName() + ";");
        }
        else {
            if (primitive.equals("map") || primitive.equals("list") || primitive.equals("set") || primitive.equals("object")) {
                this.readWhiteSpace();
                return this.readClass();
            }
            return null;
        }
    }
    
    private Class readClass() throws IOException, ClassNotFoundException {
        final String className = this.readFullIdentifier();
        System.out.println(className);
        final Class c = ObjectReader.typeMap.get(className);
        if (c != null) {
            return c;
        }
        final int dot = className.lastIndexOf(46);
        if (dot != -1) {
            final String alias = this.aliases.getPackage(className.substring(0, dot));
            if (alias != null) {
                return Class.forName(alias + className.substring(dot));
            }
        }
        return Class.forName(className);
    }
    
    private String readIdentifier() throws IOException {
        final StringBuilder buf = new StringBuilder();
        while (true) {
            this.reader.mark(Integer.MAX_VALUE);
            final char ch = (char)this.reader.read();
            if (!Character.isLetter(ch) && !Character.isDigit(ch) && ch != '_') {
                break;
            }
            buf.append(ch);
        }
        this.reader.reset();
        return new String(buf);
    }
    
    private String readFullIdentifier() throws IOException {
        final StringBuilder buf = new StringBuilder();
        while (true) {
            this.reader.mark(Integer.MAX_VALUE);
            final char ch = (char)this.reader.read();
            if (!Character.isLetter(ch) && !Character.isDigit(ch) && ch != '_' && ch != '.' && ch != '$') {
                break;
            }
            buf.append(ch);
        }
        this.reader.reset();
        return new String(buf);
    }
    
    private int readSize() throws IOException {
        this.reader.mark(Integer.MAX_VALUE);
        char ch = (char)this.reader.read();
        if (ch == ' ') {
            ch = (char)this.reader.read();
            if (ch == '[') {
                final String digits = this.readDigits();
                ch = (char)this.reader.read();
                if (ch == ']') {
                    return Integer.parseInt(digits);
                }
                throw new RuntimeException("unexpected '" + ch + "' when parsing object size");
            }
        }
        else {
            this.reader.reset();
        }
        return -1;
    }
    
    private Object readValue(final Class c, final int size) throws IOException, ClassNotFoundException {
        if (c.isPrimitive()) {
            return this.readPrimitiveValue(c);
        }
        if (ObjectReader.primitiveBoxTypes.contains(c)) {
            return this.readPrimitiveValue(c);
        }
        if (c == String.class) {
            return this.readString();
        }
        if (c.isArray()) {
            return this.readArray(c.getComponentType(), size);
        }
        if (List.class.isAssignableFrom(c)) {
            return this.readCollection(c, size);
        }
        if (Map.class.isAssignableFrom(c)) {
            return this.readMap(c, size);
        }
        if (Set.class.isAssignableFrom(c)) {
            return this.readCollection(c, size);
        }
        return this.readObjectValue(c);
    }
    
    private Object readObjectValue(final Class c) throws IOException, ClassNotFoundException {
        char ch = (char)this.reader.read();
        if (ch != '{') {
            throw new RuntimeException("Unexpected '" + ch + "' when expecting '{'");
        }
        this.readWhiteSpace();
        System.out.println("readObjectValue " + c);
        Object object;
        try {
            object = c.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(c.getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(c.getName(), e2);
        }
        while (true) {
            this.readWhiteSpace();
            final String fieldName = this.readIdentifier();
            if (fieldName.length() == 0) {
                ch = (char)this.reader.read();
                if (ch == '}') {
                    return object;
                }
                throw new RuntimeException("Unexpected '" + ch + "' when expecting '}'");
            }
            else {
                System.out.println("fieldName " + fieldName);
                ch = (char)this.reader.read();
                if (ch != ':') {
                    throw new RuntimeException("Unexpected '" + ch + "' when expecting ':'");
                }
                final Object value = this.readObject();
                Field field = null;
                Class chain = c;
                do {
                    try {
                        field = chain.getDeclaredField(fieldName);
                    }
                    catch (NoSuchFieldException e4) {
                        chain = chain.getSuperclass();
                    }
                } while (field == null && chain != Object.class);
                if (field == null) {
                    throw new RuntimeException("Unknown field '" + fieldName + "' in class " + c);
                }
                field.setAccessible(true);
                final Class fieldType = field.getType();
                if (value != null && !fieldType.isPrimitive() && !fieldType.isAssignableFrom(value.getClass())) {
                    throw new RuntimeException("Field type mismatch: expecting " + fieldType.getName() + " got " + value.getClass().getName() + ": class=" + c.getName() + " field=" + fieldName);
                }
                try {
                    field.set(object, value);
                }
                catch (IllegalAccessException e3) {
                    throw new RuntimeException(c + " field " + fieldName, e3);
                }
            }
        }
    }
    
    private Collection readCollection(final Class c, final int size) throws IOException, ClassNotFoundException {
        char ch = (char)this.reader.read();
        if (ch != '{') {
            throw new RuntimeException("Unexpected '" + ch + "' when expecting '{'");
        }
        Collection collection;
        try {
            collection = c.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(c.getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(c.getName(), e2);
        }
        while (true) {
            final TypedObject typedObject = this.readTypedObject();
            if (typedObject != null) {
                collection.add(typedObject.value);
            }
            ch = (char)this.reader.read();
            if (ch == '}') {
                return collection;
            }
            if (ch != ',') {
                throw new RuntimeException("Unexpected '" + ch + "' when expecting ','");
            }
        }
    }
    
    private Map readMap(final Class c, final int size) throws IOException, ClassNotFoundException {
        char ch = (char)this.reader.read();
        if (ch != '{') {
            throw new RuntimeException("Unexpected '" + ch + "' when expecting '{'");
        }
        Map map;
        try {
            map = c.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(c.getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(c.getName(), e2);
        }
        while (true) {
            ch = (char)this.reader.read();
            if (ch == '}') {
                return map;
            }
            if (ch != '{') {
                throw new RuntimeException("Unexpected '" + ch + "' when expecting '{'");
            }
            final TypedObject key = this.readTypedObject();
            ch = (char)this.reader.read();
            if (ch != ',') {
                throw new RuntimeException("Unexpected '" + ch + "' when expecting ','");
            }
            final TypedObject value = this.readTypedObject();
            System.out.println("key " + key + " value " + value);
            map.put(key.value, value.value);
            ch = (char)this.reader.read();
            if (ch != '}') {
                throw new RuntimeException("Unexpected '" + ch + "' when expecting '}'");
            }
        }
    }
    
    private Object readArray(final Class c, final int size) throws IOException, ClassNotFoundException {
        char ch = (char)this.reader.read();
        if (ch != '{') {
            throw new RuntimeException("Unexpected '" + ch + "' when expecting '{'");
        }
        final Object array = Array.newInstance(c, size);
        if (c.isPrimitive()) {
            return this.readPrimitiveArray(array, c);
        }
        int ii = 0;
        while (true) {
            final TypedObject typedObject = this.readTypedObject();
            if (typedObject != null) {
                Array.set(array, ii++, typedObject.value);
            }
            ch = (char)this.reader.read();
            if (ch == '}') {
                return array;
            }
            if (ch != ',') {
                throw new RuntimeException("Unexpected '" + ch + "' when expecting ','");
            }
        }
    }
    
    private Object readPrimitiveArray(final Object array, final Class c) throws IOException, ClassNotFoundException {
        int ii = 0;
        while (true) {
            this.reader.mark(Integer.MAX_VALUE);
            char ch = (char)this.reader.read();
            if (ch == '}') {
                break;
            }
            this.reader.reset();
            final Object value = this.readPrimitiveValue(c);
            Array.set(array, ii++, value);
            ch = (char)this.reader.read();
            if (ch == '}') {
                break;
            }
            if (ch != ',') {
                throw new RuntimeException("Unexpected '" + ch + "' when expecting ','");
            }
        }
        return array;
    }
    
    private void readWhiteSpace() throws IOException {
        char ch;
        do {
            this.reader.mark(Integer.MAX_VALUE);
            ch = (char)this.reader.read();
        } while (Character.isWhitespace(ch));
        this.reader.reset();
    }
    
    private Object readPrimitiveValue(final Class c) throws IOException {
        if (c == Integer.class || c == Integer.TYPE) {
            final String digits = this.readDigits();
            return new Integer(digits);
        }
        if (c == Long.class || c == Long.TYPE) {
            final String digits = this.readDigits();
            return new Long(digits);
        }
        if (c == Short.class || c == Short.TYPE) {
            final String digits = this.readDigits();
            return new Short(digits);
        }
        if (c == Byte.class || c == Byte.TYPE) {
            final String digits = this.readDigits();
            return new Byte(digits);
        }
        if (c == Boolean.class || c == Boolean.TYPE) {
            final String valueStr = this.readIdentifier();
            if (valueStr.equals("true")) {
                return Boolean.TRUE;
            }
            if (valueStr.equals("false")) {
                return Boolean.FALSE;
            }
            throw new RuntimeException("unexpected '" + valueStr + "' for boolean value");
        }
        else {
            if (c == Float.class || c == Float.TYPE) {
                final String digits = this.readFloatDigits();
                return new Float(digits);
            }
            if (c == Double.class || c == Double.TYPE) {
                final String digits = this.readFloatDigits();
                return new Double(digits);
            }
            if (c == Character.class || c == Character.TYPE) {
                final String string = this.readString();
                return new Character(string.charAt(0));
            }
            return null;
        }
    }
    
    private String readString() throws IOException {
        final StringBuilder buf = new StringBuilder();
        while (true) {
            this.reader.mark(Integer.MAX_VALUE);
            final char ch = (char)this.reader.read();
            if (ch == ',' || ch == '\n' || ch == '}') {
                break;
            }
            buf.append(ch);
        }
        this.reader.reset();
        return Coding.stringDecode(new String(buf));
    }
    
    private String readDigits() throws IOException {
        final StringBuilder buf = new StringBuilder();
        while (true) {
            this.reader.mark(Integer.MAX_VALUE);
            final char ch = (char)this.reader.read();
            if (!Character.isDigit(ch) && ch != '-') {
                break;
            }
            buf.append(ch);
        }
        this.reader.reset();
        return new String(buf);
    }
    
    private String readFloatDigits() throws IOException {
        final StringBuilder buf = new StringBuilder();
        while (true) {
            this.reader.mark(Integer.MAX_VALUE);
            final char ch = (char)this.reader.read();
            if (!Character.isDigit(ch) && ch != '-' && ch != 'E' && ch != '.') {
                break;
            }
            buf.append(ch);
        }
        this.reader.reset();
        return new String(buf);
    }
    
    private Class primitiveArrayType(final Class componentType) throws ClassNotFoundException {
        if (componentType == Long.TYPE) {
            return long[].class;
        }
        if (componentType == Integer.TYPE) {
            return int[].class;
        }
        if (componentType == Boolean.TYPE) {
            return boolean[].class;
        }
        if (componentType == Short.TYPE) {
            return short[].class;
        }
        if (componentType == Byte.TYPE) {
            return byte[].class;
        }
        if (componentType == Character.TYPE) {
            return char[].class;
        }
        if (componentType == Double.TYPE) {
            return double[].class;
        }
        if (componentType == Float.TYPE) {
            return float[].class;
        }
        throw new ClassNotFoundException("[" + componentType);
    }
    
    private static Object test(final String string) {
        return test(string, new PackageAliases());
    }
    
    private static Object test(final String string, final PackageAliases aliases) {
        System.out.println("TEST '" + string + "'");
        final ObjectReader reader = new ObjectReader(string, aliases);
        Object object = null;
        try {
            object = reader.readObject();
            System.out.println(object);
            System.out.println(object.getClass());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        return object;
    }
    
    private static void testCoding(final String s) {
        System.out.println(s + ": '" + Coding.stringEncode(s) + "'");
        if (s.equals(Coding.stringDecode(Coding.stringEncode(s)))) {
            System.out.println("PASS");
        }
        else {
            System.out.println("FAIL: " + Coding.stringDecode(Coding.stringEncode(s)));
        }
    }
    
    public static void main(final String[] args) {
        testCoding("");
        testCoding("abc");
        testCoding("1,2,3");
        testCoding("yum yum");
        testCoding("percent %");
        testCoding("hash #");
        testCoding("PQ\u8c81");
        test("Long=123");
        test("Boolean=true");
        test("Boolean=false");
        test("long=123");
        final c1 object = new c1();
        object.i1 = 1;
        object.i2 = 2;
        object.i3 = 3;
        object.i4 = 4;
        object.s1 = "string1";
        object.b1 = true;
        object.l1 = new LinkedList();
        (object.l2 = new ArrayList()).add(1);
        object.l2.add(2);
        object.l2.add("th,ree");
        (object.l3 = new LinkedList<String>()).add("one");
        object.l3.add("two");
        object.l3.add(null);
        object.m1 = new HashMap();
        (object.m2 = new HashMap()).put(null, "null key");
        object.m2.put("null value", null);
        object.m2.put(33, "number key");
        object.ia1 = new int[] { 1, 2, 3 };
        object.la1 = new long[] { 1L, 2L, 3L };
        object.sa1 = new String[] { "one", "two", "three" };
        final ObjectWriter writer = new ObjectWriter();
        writer.writeObject(object);
        final c1 copy = (c1)test(writer.getString());
        System.out.println("copy.i1 " + copy.i1);
        System.out.println("copy.i2 " + copy.i2);
        System.out.println("copy.i3 " + copy.i3);
        System.out.println("copy.i4 " + copy.i4);
        System.out.println("copy.s1 " + copy.s1);
        System.out.println("copy.b1 " + copy.b1);
        System.out.println("copy.l1 " + copy.l1);
        System.out.println("copy.l2 " + copy.l2);
        System.out.println("copy.l3 " + copy.l3);
        System.out.println("copy.m1 " + copy.m1);
        System.out.println("copy.m2 " + copy.m2);
        System.out.println("copy.ia1 " + copy.ia1);
        System.out.println("copy.la1 " + copy.la1);
        System.out.println("copy.sa1 " + copy.sa1);
        final ObjectWriter writer2 = new ObjectWriter();
        writer2.writeObject(copy);
        System.out.println("roundtrip " + writer2.getString().equals(writer.getString()));
        final PackageAliases aliases = new PackageAliases();
        aliases.addAlias("j.u", "java.util");
        aliases.addAlias("m.s.o", "atavism.server.objects");
        aliases.addAlias("m.s.e", "atavism.server.engine");
        aliases.addAlias("m.m.o", "atavism.agis.objects");
        Namespace.COMBAT = new Namespace("NS.combat", 1);
    }
    
    private static class TypedObject
    {
        public Class type;
        public Object value;
        
        public TypedObject(final Class type, final Object value) {
            this.type = type;
            this.value = value;
        }
    }
    
    private static final class NullClass
    {
    }
    
    private static class c1
    {
        String s;
        public int i1;
        private int i2;
        protected int i3;
        int i4;
        String s1;
        Boolean bNull;
        boolean b1;
        List lNull;
        List l1;
        ArrayList l2;
        List<String> l3;
        Map mNull;
        Map m1;
        Map m2;
        int[] ia0;
        int[] ia1;
        long[] la1;
        String[] sa1;
        byte byte1;
        byte byte2;
        float f1;
        float f2;
        char c0;
        char c1;
        char cspace;
        char cpercent;
        char chash;
        char ca;
        char cx;
        char cc;
        char cn;
        char ct;
        char cu;
        
        public c1() {
            this.byte1 = 1;
            this.byte2 = -128;
            this.f1 = 9.9870035E11f;
            this.f2 = 1.0E-11f;
            this.c1 = '\u0001';
            this.cspace = ' ';
            this.cpercent = '%';
            this.chash = '#';
            this.ca = 'a';
            this.cx = '}';
            this.cc = ',';
            this.cn = '\n';
            this.ct = '\t';
            this.cu = '\u1234';
        }
    }
}
