// 
// Decompiled by Procyon v0.5.30
// 

package atavism.persistence;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.engine.Namespace;
import java.net.URISyntaxException;
import java.net.URI;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.Set;
import java.util.List;
import java.util.Map;

public class ObjectWriter
{
    private static final int INITIAL_CAPACITY = 4096;
    private static Map<Class, String> simpleClassNames;
    private StringBuilder output;
    private int indent;
    private PackageAliases aliases;
    
    public ObjectWriter() {
        ObjectWriter.simpleClassNames.put(Long.class, "Long");
        ObjectWriter.simpleClassNames.put(Integer.class, "Integer");
        ObjectWriter.simpleClassNames.put(Boolean.class, "Boolean");
        ObjectWriter.simpleClassNames.put(Short.class, "Short");
        ObjectWriter.simpleClassNames.put(Byte.class, "Byte");
        ObjectWriter.simpleClassNames.put(Character.class, "Character");
        ObjectWriter.simpleClassNames.put(Double.class, "Double");
        ObjectWriter.simpleClassNames.put(Float.class, "Float");
        ObjectWriter.simpleClassNames.put(String.class, "String");
        this.output = new StringBuilder(4096);
        this.aliases = new PackageAliases();
    }
    
    public ObjectWriter(final PackageAliases aliases) {
        ObjectWriter.simpleClassNames.put(Long.class, "Long");
        ObjectWriter.simpleClassNames.put(Integer.class, "Integer");
        ObjectWriter.simpleClassNames.put(Boolean.class, "Boolean");
        ObjectWriter.simpleClassNames.put(Short.class, "Short");
        ObjectWriter.simpleClassNames.put(Byte.class, "Byte");
        ObjectWriter.simpleClassNames.put(Character.class, "Character");
        ObjectWriter.simpleClassNames.put(Double.class, "Double");
        ObjectWriter.simpleClassNames.put(Float.class, "Float");
        ObjectWriter.simpleClassNames.put(String.class, "String");
        this.output = new StringBuilder(4096);
        this.aliases = aliases;
    }
    
    public void writeObject(final Object object) {
        try {
            if (object == null) {
                this.output.append("null");
                return;
            }
            final Class c = object.getClass();
            this.writeType(c);
            this.writeSize(object, c);
            this.output.append('=');
            this.writeValue(object);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("writeObject", e);
        }
    }
    
    public PackageAliases getAliases() {
        return this.aliases;
    }
    
    public void setAliases(final PackageAliases aliases) {
        this.aliases = aliases;
    }
    
    private void writeValue(final Object value) throws IllegalAccessException {
        final Class c = value.getClass();
        if (c.isPrimitive()) {
            this.output.append("PRIMITIVE");
        }
        else if (c == Long.class) {
            this.output.append(value);
        }
        else if (c == Boolean.class) {
            this.output.append(value);
        }
        else if (c == String.class) {
            this.output.append(Coding.stringEncode((String)value));
        }
        else if (c == Integer.class) {
            this.output.append(value);
        }
        else if (c == Float.class) {
            this.output.append(value);
        }
        else if (c == Double.class) {
            this.output.append(value);
        }
        else if (c == Byte.class) {
            this.output.append(value);
        }
        else if (c == Character.class) {
            this.output.append(value);
        }
        else if (c.isArray()) {
            this.writeArray(value);
        }
        else if (List.class.isAssignableFrom(c)) {
            this.writeList((List)value);
        }
        else if (Map.class.isAssignableFrom(c)) {
            this.writeMap((Map)value);
        }
        else if (Set.class.isAssignableFrom(c)) {
            this.writeSet((Set)value);
        }
        else {
            this.writeObjectValue(value);
        }
    }
    
    private void writeArray(final Object array) throws IllegalAccessException {
        if (array.getClass().getComponentType().isPrimitive()) {
            this.writePrimitiveArray(array);
            return;
        }
        this.output.append('{');
        final int length = Array.getLength(array);
        for (int ii = 0; ii < length; ++ii) {
            final Object value = Array.get(array, ii);
            if (value == null) {
                this.output.append("null,");
            }
            else {
                this.writeType(value.getClass());
                this.writeSize(value, value.getClass());
                this.output.append('=');
                this.writeValue(value);
                this.output.append(',');
            }
        }
        if (length > 0) {
            this.output.setLength(this.output.length() - 1);
        }
        this.output.append('}');
    }
    
    private void writePrimitiveArray(final Object array) throws IllegalAccessException {
        final Class type = array.getClass().getComponentType();
        this.output.append('{');
        final int length = Array.getLength(array);
        for (int ii = 0; ii < length; ++ii) {
            if (type == Integer.TYPE) {
                this.output.append(Array.getInt(array, ii));
            }
            else if (type == Long.TYPE) {
                this.output.append(Array.getLong(array, ii));
            }
            else if (type == Short.TYPE) {
                this.output.append(Array.getShort(array, ii));
            }
            else if (type == Byte.TYPE) {
                this.output.append(Array.getByte(array, ii));
            }
            else if (type == Boolean.TYPE) {
                this.output.append(Array.getBoolean(array, ii));
            }
            else if (type == Character.TYPE) {
                this.output.append(Coding.stringEncode("" + Array.getChar(array, ii)));
            }
            else if (type == Double.TYPE) {
                this.output.append(Array.getDouble(array, ii));
            }
            else if (type == Float.TYPE) {
                this.output.append(Array.getFloat(array, ii));
            }
            this.output.append(',');
        }
        if (length > 0) {
            this.output.setLength(this.output.length() - 1);
        }
        this.output.append('}');
    }
    
    private void writeList(final List list) throws IllegalAccessException {
        this.output.append('{');
        for (final Object value : list) {
            if (value == null) {
                this.output.append("null,");
            }
            else {
                this.writeType(value.getClass());
                this.writeSize(value, value.getClass());
                this.output.append('=');
                this.writeValue(value);
                this.output.append(',');
            }
        }
        if (list.size() > 0) {
            this.output.setLength(this.output.length() - 1);
        }
        this.output.append('}');
    }
    
    private void writeMap(final Map map) throws IllegalAccessException {
        this.output.append('{');
        for (final Map.Entry entry : map.entrySet()) {
            this.output.append('{');
            this.writeObject(entry.getKey());
            this.output.append(',');
            this.writeObject(entry.getValue());
            this.output.append('}');
        }
        this.output.append('}');
    }
    
    private void writeSet(final Set set) throws IllegalAccessException {
        this.output.append('{');
        for (final Object value : set) {
            this.writeType(value.getClass());
            this.writeSize(value, value.getClass());
            this.output.append('=');
            this.writeValue(value);
            this.output.append(',');
        }
        if (set.size() > 0) {
            this.output.setLength(this.output.length() - 1);
        }
        this.output.append('}');
    }
    
    private void writeObjectValue(final Object value) throws IllegalAccessException {
        this.output.append('{');
        this.output.append('\n');
        ++this.indent;
        this.writeObject(value, value.getClass());
        --this.indent;
        this.writeIndent();
        this.output.append('}');
    }
    
    private void writeTypedObject(final Object object, final Class c) {
        try {
            if (object != null) {
                this.writeType(object.getClass());
                this.writeSize(object, c);
                this.output.append('=');
                this.writeValue(object);
            }
            else {
                this.writeType(c);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("writeObject", e);
        }
    }
    
    private void writeSize(final Object object, final Class c) {
        if (List.class.isAssignableFrom(c) || Set.class.isAssignableFrom(c)) {
            this.output.append(" [");
            this.output.append(((Collection)object).size());
            this.output.append(']');
        }
        else if (Map.class.isAssignableFrom(c)) {
            this.output.append(" [");
            this.output.append(((Map)object).size());
            this.output.append(']');
        }
        else if (c.isArray()) {
            this.output.append(" [");
            this.output.append(Array.getLength(object));
            this.output.append(']');
        }
    }
    
    public void writeObject(final Object object, final Class c) throws IllegalAccessException {
        final Class sc = c.getSuperclass();
        System.out.println(c.getName() + " extends " + sc.getName());
        if (sc != null && sc != Object.class) {
            this.writeObject(object, sc);
        }
        final Field[] arr$;
        final Field[] fields = arr$ = c.getDeclaredFields();
        for (final Field field : arr$) {
            final int modifiers = field.getModifiers();
            if ((modifiers & 0x10) == 0x0 && (modifiers & 0x8) == 0x0) {
                if ((modifiers & 0x80) == 0x0) {
                    System.out.println(field.getName());
                    System.out.println(field.toString());
                    System.out.println(field.toGenericString());
                    field.setAccessible(true);
                    final Class fieldType = field.getType();
                    this.writeIndent();
                    this.output.append(field.getName());
                    this.output.append(':');
                    if (fieldType.isPrimitive()) {
                        this.writePrimitive(object, field);
                    }
                    else {
                        this.writeTypedObject(field.get(object), fieldType);
                    }
                    this.output.append('\n');
                }
            }
        }
    }
    
    public StringBuilder getStringBuilder() {
        return this.output;
    }
    
    public String getString() {
        return this.output.toString();
    }
    
    private void writePrimitive(final Object object, final Field field) throws IllegalAccessException {
        final Class fieldType = field.getType();
        if (fieldType == Long.TYPE) {
            this.output.append("long=");
            this.output.append(field.getLong(object));
        }
        else if (fieldType == Boolean.TYPE) {
            this.output.append("boolean=");
            this.output.append(field.getBoolean(object));
        }
        else if (fieldType == Integer.TYPE) {
            this.output.append("int=");
            this.output.append(field.getInt(object));
        }
        else if (fieldType == Float.TYPE) {
            this.output.append("float=");
            this.output.append(field.getFloat(object));
        }
        else if (fieldType == Double.TYPE) {
            this.output.append("double=");
            this.output.append(field.getDouble(object));
        }
        else if (fieldType == Byte.TYPE) {
            this.output.append("byte=");
            this.output.append(field.getByte(object));
        }
        else if (fieldType == Short.TYPE) {
            this.output.append("short=");
            this.output.append(field.getShort(object));
        }
        else if (fieldType == Character.TYPE) {
            this.output.append("char=");
            this.output.append(Coding.stringEncode("" + field.getChar(object)));
        }
    }
    
    private void writeType(final Class c) {
        final String shortName = ObjectWriter.simpleClassNames.get(c);
        if (shortName != null) {
            this.output.append(shortName);
        }
        else if (c.isPrimitive()) {
            this.output.append(c.getName());
        }
        else if (c.isArray()) {
            this.output.append("array ");
            this.writeType(c.getComponentType());
        }
        else if (List.class.isAssignableFrom(c)) {
            this.output.append("list ");
            this.writeClassName(c.getName());
        }
        else if (Map.class.isAssignableFrom(c)) {
            this.output.append("map ");
            this.writeClassName(c.getName());
        }
        else if (Set.class.isAssignableFrom(c)) {
            this.output.append("set ");
            this.writeClassName(c.getName());
        }
        else {
            this.output.append("object ");
            this.writeClassName(c.getName());
        }
    }
    
    private void writeClassName(final String className) {
        final int dot = className.lastIndexOf(46);
        if (dot == -1) {
            this.output.append(className);
            return;
        }
        final String alias = this.aliases.getAlias(className.substring(0, dot));
        if (alias != null) {
            this.output.append(alias);
            this.output.append(className.substring(dot));
        }
        else {
            this.output.append(className);
        }
    }
    
    private void writeIndent() {
        for (int ii = this.indent * 2; ii > 0; --ii) {
            this.output.append(' ');
        }
    }
    
    public static void encode() {
        try {
            final URI uri = new URI(null, null, "abc_123space dash-less<greater>newline\n");
            System.out.println("ASCII");
            System.out.println(uri.toASCIIString());
            System.out.println("toString");
            System.out.println(uri.toString());
            final String str = uri.toASCIIString().substring(1);
            final URI uri2 = new URI("#" + str);
            System.out.println(uri2.getFragment());
        }
        catch (URISyntaxException e) {
            System.out.println("EXCEPTION " + e);
        }
    }
    
    public static void main(final String[] args) {
        try {
            System.out.println(args.getClass());
            System.out.println(new int[4].getClass());
            final Class c = Class.forName("[Ljava.lang.Long;");
            System.out.println(c);
            System.exit(1);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        final PackageAliases aliases = new PackageAliases();
        aliases.addAlias("j.u", "java.util");
        aliases.addAlias("m.s.o", "atavism.server.objects");
        aliases.addAlias("m.s.e", "atavism.server.engine");
        aliases.addAlias("m.m.o", "atavism.agis.objects");
        Namespace.COMBAT = new Namespace("NS.combat", 1);
        final test1 t1 = new test1();
        t1.arrayTest1[0] = new test1();
        final ObjectWriter writer2 = new ObjectWriter(aliases);
        writer2.writeObject(t1);
        System.out.println(writer2.getString());
    }
    
    static {
        ObjectWriter.simpleClassNames = new HashMap<Class, String>();
    }
    
    private static class test1
    {
        public int[] arrayInt;
        public byte[] arrayByte;
        public String[] arrayString;
        public int[] arrayNull;
        public test1[] arrayTest1;
        public test1[] arrayTestNull;
        
        private test1() {
            this.arrayInt = new int[] { 1, 2, 3 };
            this.arrayByte = new byte[] { 1, 2, 3, 127, -1, -128 };
            this.arrayString = new String[] { "one", "two", "three", null };
            this.arrayNull = null;
            this.arrayTest1 = new test1[] { null };
            this.arrayTestNull = null;
        }
    }
}
