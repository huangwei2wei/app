// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.marshalling;

import java.util.Set;
import java.util.List;
import atavism.server.math.Point;
import java.util.HashSet;
import java.util.Comparator;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import atavism.server.util.Log;
import java.io.FileWriter;
import java.util.HashMap;

public class MarshallingGenerator extends MarshallingRuntime
{
    private static String indentString;
    protected static String generatedMarshallingClass;
    protected static HashMap<Short, String> readOps;
    protected static HashMap<Short, String> writeOps;
    protected static Class linkedListClass;
    protected static Class hashMapClass;
    protected static Class hashSetClass;
    
    public static void setGeneratedMarshallingClass(final String className) {
        MarshallingGenerator.generatedMarshallingClass = className;
    }
    
    public static void generateMarshalling(final String codeFile) {
        installPredefinedTypes();
        initializeCodeGenerator();
        if (!checkTypeReferences()) {
            return;
        }
        FileWriter str = null;
        try {
            str = new FileWriter(codeFile);
        }
        catch (Exception e) {
            Log.error("Exception opening generated file: " + e.getMessage());
        }
        int indent = 0;
        writeLine(str, indent, "package atavism.server.marshalling;");
        writeLine(str, 0, "");
        writeLine(str, indent, "import java.io.*;");
        writeLine(str, indent, "import java.util.*;");
        writeLine(str, indent, "import atavism.server.util.*;");
        writeLine(str, indent, "import atavism.server.network.*;");
        writeLine(str, indent, "import atavism.msgsys.*;");
        writeLine(str, indent, "import atavism.server.plugins.*;");
        writeLine(str, indent, "import atavism.server.objects.*;");
        writeLine(str, indent, "import atavism.server.math.*;");
        writeLine(str, indent, "import atavism.server.plugins.WorldManagerClient.ObjectInfo;");
        writeLine(str, 0, "");
        writeLine(str, indent, "public class " + MarshallingGenerator.generatedMarshallingClass + " extends MarshallingRuntime {");
        ++indent;
        final ArrayList<MarshallingPair> sortedList = new ArrayList<MarshallingPair>();
        for (final Map.Entry<String, ClassProperties> entry : MarshallingGenerator.classToClassProperties.entrySet()) {
            final String className = entry.getKey();
            final Short n = entry.getValue().typeNum;
            if (n <= 26) {
                continue;
            }
            if (supportsMarshallable(className)) {
                continue;
            }
            sortedList.add(new MarshallingPair(className, n));
        }
        for (final MarshallingPair entry2 : sortedList) {
            final String name = entry2.getClassKey();
            final Class c = lookupClass(name);
            final Short n2 = entry2.getTypeNum();
            int flagBitCount = 0;
            final LinkedList<Field> fields = getValidClassFields(c);
            final LinkedList<Integer> nullTestedFields = new LinkedList<Integer>();
            int index = -1;
            for (final Field f : fields) {
                ++index;
                final Class fieldType = getFieldType(f);
                if (typeIsPrimitive(fieldType)) {
                    continue;
                }
                final String fieldName = f.getName();
                final Short fieldTypeNum = MarshallingRuntime.getTypeNumForClass(fieldType);
                if (fieldTypeNum == null) {
                    Log.error("Field " + fieldName + " of type " + c + " has a type for which there is no encode/decode support");
                }
                else {
                    if (fieldTypeNum >= -9 && fieldTypeNum <= -3) {
                        continue;
                    }
                    nullTestedFields.add(index);
                    ++flagBitCount;
                }
            }
            final String className2 = getSimpleClassName(c);
            writeLine(str, indent, "public static class " + className2 + "Marshaller implements Marshallable {");
            ++indent;
            generateToBytesMarshalling(c, n2, str, indent, fields, nullTestedFields, flagBitCount);
            generateParseBytesMarshalling(c, n2, str, indent);
            generateAssignBytesMarshalling(c, n2, str, indent, fields, nullTestedFields, flagBitCount);
            --indent;
            writeLine(str, indent, "}");
            writeLine(str, 0, "");
            try {
                str.flush();
            }
            catch (Exception e2) {
                Log.info("Could not flush output file!");
            }
        }
        writeLine(str, indent, "public static void initialize() {");
        ++indent;
        for (final MarshallingPair entry2 : sortedList) {
            final String className = entry2.getClassKey();
            final Short n = entry2.getTypeNum();
            writeLine(str, indent, "addMarshaller((short)" + n + ", new " + className + "Marshaller());");
        }
        --indent;
        writeLine(str, indent, "}");
        --indent;
        writeLine(str, indent, "}");
        try {
            str.close();
        }
        catch (Exception e3) {
            Log.info("Could not close output file!");
        }
    }
    
    protected static boolean checkTypeReferences() {
        final Map<Class, LinkedList<Class>> missingTypes = new HashMap<Class, LinkedList<Class>>();
        for (final Map.Entry<String, ClassProperties> entry : MarshallingGenerator.classToClassProperties.entrySet()) {
            final String className = entry.getKey();
            final Class c = lookupClass(className);
            final Short n = entry.getValue().typeNum;
            if (MarshallingRuntime.marshalledTypeNum(n)) {
                final Class superclass = getValidSuperclass(c);
                if (superclass != null) {
                    checkClassPresent(c, superclass, missingTypes);
                }
                final LinkedList<Field> fields = getValidClassFields(c);
                for (final Field f : fields) {
                    final Class fieldType = getFieldType(f);
                    checkClassPresent(c, fieldType, missingTypes);
                }
            }
        }
        if (missingTypes.size() > 0) {
            for (final Map.Entry<Class, LinkedList<Class>> entry2 : missingTypes.entrySet()) {
                final Class c2 = entry2.getKey();
                final LinkedList<Class> refs = entry2.getValue();
                String s = "";
                for (final Class ref : refs) {
                    if (s != "") {
                        s += ", ";
                    }
                    s = s + "'" + getSimpleClassName(ref) + "'";
                }
                Log.error("Missing type '" + getSimpleClassName(c2) + "' is referred to by type(s) " + s);
            }
            Log.error("Aborting code generation due to missing types");
            return false;
        }
        return true;
    }
    
    protected static boolean supportsMarshallable(final String className) {
        final Class c = lookupClass(className);
        for (final Class iface : c.getInterfaces()) {
            if (iface.getSimpleName().equals("Marshallable")) {
                return true;
            }
        }
        return false;
    }
    
    protected static void generateToBytesMarshalling(final Class c, final int n, final FileWriter str, int indent, final LinkedList<Field> fields, final LinkedList<Integer> nullTestedFields, final int flagBitCount) {
        final String className = getSimpleClassName(c);
        writeLine(str, indent, "public void toBytes(AOByteBuffer buf, Object object) {");
        ++indent;
        writeLine(str, indent, className + " me = (" + className + ")object;");
        final Class superclass = getValidSuperclass(c);
        if (superclass != null) {
            final Short typeNum = MarshallingRuntime.getTypeNumForClass(superclass);
            writeLine(str, indent, "MarshallingRuntime.marshallers[" + typeNum + "].toBytes(buf, object);");
        }
        for (int batches = (flagBitCount + 7) / 8, i = 0; i < batches; ++i) {
            final int limit = Math.min(flagBitCount, (i + 1) * 8);
            String s = "buf.writeByte(";
            int j;
            for (int start = j = i * 8; j < limit; ++j) {
                final int index = j - i * 8;
                final Field f = fields.get(nullTestedFields.get(j));
                final String test = "(" + makeOmittedTest(f) + " ? " + (1 << index) + " : 0)";
                s += test;
                if (j < limit - 1) {
                    s += " |";
                    if (j == start) {
                        writeLine(str, indent, s);
                    }
                    else {
                        writeLine(str, indent + 2, s);
                    }
                }
                else {
                    s += ");";
                    writeLine(str, (j == start) ? indent : (indent + 2), s);
                }
                s = "";
            }
        }
        int index2 = -1;
        for (final Field f2 : fields) {
            ++index2;
            final String fieldName = f2.getName();
            final Class fieldType = getFieldType(f2);
            final Short fieldTypeNum = MarshallingRuntime.getTypeNumForClass(fieldType);
            final boolean tested = nullTestedFields.contains(index2);
            if (tested) {
                writeLine(str, indent, "if " + makeOmittedTest(f2));
                ++indent;
            }
            writeLine(str, indent, createWriteOp(fieldType, "me." + fieldName, fieldTypeNum) + ";");
            if (tested) {
                --indent;
            }
        }
        --indent;
        writeLine(str, indent, "}");
        writeLine(str, 0, "");
    }
    
    protected static void generateParseBytesMarshalling(final Class c, final int n, final FileWriter str, int indent) {
        final String className = getSimpleClassName(c);
        writeLine(str, indent, "public Object parseBytes(AOByteBuffer buf) {");
        ++indent;
        writeLine(str, indent, className + " me = new " + className + "();");
        writeLine(str, indent, "assignBytes(buf, me);");
        writeLine(str, indent, "return me;");
        --indent;
        writeLine(str, indent, "}");
        writeLine(str, 0, "");
    }
    
    protected static void generateAssignBytesMarshalling(final Class c, final int n, final FileWriter str, int indent, final LinkedList<Field> fields, final LinkedList<Integer> nullTestedFields, final int flagBitCount) {
        final String className = getSimpleClassName(c);
        writeLine(str, indent, "public void assignBytes(AOByteBuffer buf, Object object) {");
        ++indent;
        writeLine(str, indent, className + " me = (" + className + ")object;");
        final Class superclass = getValidSuperclass(c);
        if (superclass != null) {
            final Short typeNum = MarshallingRuntime.getTypeNumForClass(superclass);
            writeLine(str, indent, "MarshallingRuntime.marshallers[" + typeNum + "].Marshaller.assignBytes(buf, object);");
        }
        if (flagBitCount > 0) {
            final int batches = (flagBitCount + 7) / 8;
            if (batches > 1) {
                for (int i = 0; i < batches; ++i) {
                    writeLine(str, indent, "byte flags" + i + " = buf.getByte();");
                }
            }
            else {
                writeLine(str, indent, "byte flags = buf.getByte();");
            }
        }
        int index = -1;
        int testIndex = -1;
        for (final Field f : fields) {
            ++index;
            final String fieldName = f.getName();
            final Class fieldType = getFieldType(f);
            final Short fieldTypeNum = MarshallingRuntime.getTypeNumForClass(fieldType);
            final boolean tested = nullTestedFields.contains(index);
            if (tested) {
                ++testIndex;
                writeLine(str, indent, "if " + formatFlagBitReference(testIndex, flagBitCount));
                ++indent;
            }
            final String op = createReadOp(fieldType, fieldName, fieldTypeNum);
            writeLine(str, indent, op + ";");
            if (tested) {
                --indent;
            }
        }
        --indent;
        writeLine(str, indent, "}");
    }
    
    protected static String makeOmittedTest(final Field f) {
        final Class fieldType = getFieldType(f);
        String test = "(me." + f.getName() + " != null)";
        if (isStringType(fieldType)) {
            test = "(" + test + " && !(me." + f.getName() + ".equals(\"\")))";
        }
        return test;
    }
    
    protected static String createWriteOp(final Class c, final String getter, final Short fieldTypeNum) {
        if (fieldTypeNum < -9 || fieldTypeNum > 8) {
            return "MarshallingRuntime.marshallers[" + fieldTypeNum + "].toBytes(buf, " + getter + ")";
        }
        final String s = MarshallingGenerator.writeOps.get(fieldTypeNum);
        if (s == null) {
            Log.error("Could not find the writeOp for fieldTypeNum " + fieldTypeNum);
            return "<Didn't get writeOp for typeNum" + fieldTypeNum + ">";
        }
        return s.replaceAll("\\#", getter);
    }
    
    protected static String createReadOp(final Class c, final String fieldName, final short fieldTypeNum) {
        if (fieldTypeNum < -9 || fieldTypeNum > 8) {
            return "me." + fieldName + " = MarshallingRuntime.marshallers[" + fieldTypeNum + "].parseBytes(buf)";
        }
        final String s = MarshallingGenerator.readOps.get(fieldTypeNum);
        if (s == null) {
            Log.error("Could not find the readOp for fieldTypeNum " + fieldTypeNum);
            return "";
        }
        return "me." + fieldName + " = " + s;
    }
    
    protected static void checkClassPresent(final Class referringClass, final Class referredClass, final Map<Class, LinkedList<Class>> missingTypes) {
        final Short s = MarshallingRuntime.getTypeNumForClass(referredClass);
        if (s == null) {
            LinkedList<Class> references = missingTypes.get(referredClass);
            if (references == null) {
                references = new LinkedList<Class>();
                missingTypes.put(referredClass, references);
            }
            if (!references.contains(referringClass)) {
                references.add(referringClass);
            }
        }
    }
    
    protected static String formatFlagBitReference(final int index, final int flagBitCount) {
        if (flagBitCount > 8) {
            return "((flags" + (index >> 3) + " & " + (1 << (index & 0x7)) + ") != 0)";
        }
        return "((flags & " + (1 << index) + ") != 0)";
    }
    
    protected static String formatTitle(final String n) {
        final String[] parts = n.split("\\.");
        return parts[parts.length - 1];
    }
    
    protected static void writeLine(final FileWriter str, final int indent, final String s) {
        try {
            str.write(MarshallingGenerator.indentString.substring(0, indent * 4) + s + "\r\n");
        }
        catch (Exception e) {
            Log.error("Error writing generated file: " + e.getMessage());
        }
    }
    
    protected static boolean isStaticOrTransient(final Field f) {
        return (f.getModifiers() & 0x88) != 0x0;
    }
    
    protected static Class getValidSuperclass(final Class c) {
        final Class superclass = c.getSuperclass();
        if (superclass != null && (superclass.getModifiers() & 0x200) == 0x0 && !getSimpleClassName(superclass).equals("Object")) {
            return superclass;
        }
        return null;
    }
    
    protected static LinkedList<Field> getValidClassFields(final Class c) {
        final LinkedList<Field> validFields = new LinkedList<Field>();
        final Field[] arr$;
        final Field[] fields = arr$ = c.getDeclaredFields();
        for (final Field f : arr$) {
            if (!isStaticOrTransient(f)) {
                validFields.add(f);
            }
        }
        return validFields;
    }
    
    protected static Class getFieldType(final Field f) {
        return canonicalType(f.getType());
    }
    
    protected static Class canonicalType(final Class c) {
        final String s = c.getSimpleName();
        if (s.equals("List")) {
            return MarshallingGenerator.linkedListClass;
        }
        if (s.equals("Map")) {
            return MarshallingGenerator.hashMapClass;
        }
        if (s.equals("Set")) {
            return MarshallingGenerator.hashSetClass;
        }
        return c;
    }
    
    protected static boolean typeIsPrimitive(final Class c) {
        return c.isPrimitive();
    }
    
    protected static boolean isStringType(final Class c) {
        return c.getSimpleName().equals("String");
    }
    
    protected static String getSimpleClassName(final Class c) {
        return c.getSimpleName();
    }
    
    protected static Class lookupClass(final String className) {
        try {
            return Class.forName(className);
        }
        catch (Exception e) {
            Log.error("MarshallingGenerator.lookupClass: could not find class '" + className + "'");
            return null;
        }
    }
    
    protected static void initializeCodeGenerator() {
        MarshallingGenerator.linkedListClass = lookupClass(MarshallingRuntime.getClassForTypeNum((short)9));
        MarshallingGenerator.hashMapClass = lookupClass(MarshallingRuntime.getClassForTypeNum((short)11));
        MarshallingGenerator.hashSetClass = lookupClass(MarshallingRuntime.getClassForTypeNum((short)14));
        MarshallingGenerator.readOps = new HashMap<Short, String>();
        MarshallingGenerator.writeOps = new HashMap<Short, String>();
        defineRWCode((short)(-9), (short)1, "buf.getByte() != 0", "buf.putByte(# ? 1 : 0)");
        defineRWCode((short)(-8), (short)2, "buf.getByte()", "buf.putByte(#)");
        defineRWCode((short)(-3), (short)7, "buf.getShort()", "buf.putShort(#)");
        defineRWCode((short)(-5), (short)5, "buf.getInt()", "buf.putInt(#)");
        defineRWCode((short)(-4), (short)6, "buf.getLong()", "buf.putLong(#)");
        defineRWCode((short)(-6), (short)4, "buf.getFloat()", "buf.putFloat(#)");
        defineRWCode((short)(-7), (short)3, "buf.getDouble()", "buf.putDouble(#)");
        defineRWCode((short)8, "buf.getString()", "buf.putString(#)");
    }
    
    protected static void defineRWCode(final Short typeNumPrimitive, final Short typeNumNonPrimitive, final String readOp, final String writeOp) {
        defineRWCode(typeNumPrimitive, readOp, writeOp);
        defineRWCode(typeNumNonPrimitive, readOp, writeOp);
    }
    
    protected static void defineRWCode(final Short typeNum, final String readOp, final String writeOp) {
        MarshallingGenerator.readOps.put(typeNum, readOp);
        MarshallingGenerator.writeOps.put(typeNum, writeOp);
    }
    
    protected static void logGenericClassInfo(final Object object, final String what) {
        final Class c = object.getClass();
        final Type genSuper = c.getGenericSuperclass();
        final Type[] interfaces = c.getGenericInterfaces();
        String s = "";
        for (final Type iface : interfaces) {
            if (s != "") {
                s += ", ";
            }
            s += iface;
        }
        Log.info("logGenericClassInfo " + what + " Class " + c + ", genSuper " + genSuper + ", interfaces " + s);
    }
    
    public static void main(final String[] args) {
        Log.init();
        MarshallingRuntime.registerMarshallingClass("atavism.server.math.Point");
        MarshallingRuntime.registerMarshallingClass("atavism.server.math.AOVector");
        MarshallingRuntime.registerMarshallingClass("atavism.server.math.Quaternion");
        MarshallingRuntime.registerMarshallingClass("atavism.server.objects.DisplayContext");
        MarshallingRuntime.registerMarshallingClass("atavism.server.objects.SoundData");
        MarshallingRuntime.registerMarshallingClass("atavism.server.plugins.WorldManagerClient.ObjectInfo");
        MarshallingRuntime.registerMarshallingClass("atavism.msgsys.MessageTypeFilter");
        MarshallingRuntime.registerMarshallingClass("atavism.server.objects.LightData");
        MarshallingRuntime.registerMarshallingClass("atavism.server.objects.Color");
        MarshallingRuntime.registerMarshallingClass("atavism.server.marshalling.MarshalTestClass1");
        MarshallingRuntime.registerMarshallingClass("atavism.server.marshalling.MarshalTestClass2");
    }
    
    static {
        MarshallingGenerator.indentString = "                                                                ";
        MarshallingGenerator.generatedMarshallingClass = "AOMarshalling";
        MarshallingGenerator.readOps = null;
        MarshallingGenerator.writeOps = null;
        MarshallingGenerator.linkedListClass = null;
        MarshallingGenerator.hashMapClass = null;
        MarshallingGenerator.hashSetClass = null;
    }
    
    public static class MarshallingPair implements Comparator
    {
        String className;
        Short typeNum;
        
        public MarshallingPair(final String className, final Short typeNum) {
            this.className = className;
            this.typeNum = typeNum;
        }
        
        @Override
        public int compare(final Object o1, final Object o2) {
            final MarshallingPair p1 = (MarshallingPair)o1;
            final MarshallingPair p2 = (MarshallingPair)o2;
            if (p1.typeNum < p2.typeNum) {
                return -1;
            }
            if (p1.typeNum > p2.typeNum) {
                return 1;
            }
            return 0;
        }
        
        public String getClassKey() {
            return this.className;
        }
        
        public Short getTypeNum() {
            return this.typeNum;
        }
    }
    
    public static class MarshalTestClass1
    {
        public Boolean BooleanVal;
        public Byte ByteVal;
        public Short ShortVal;
        public Integer IntegerVal;
        public Long LongVal;
        public Float FloatVal;
        public Double DoubleVal;
        public boolean booleanVal;
        public byte byteVal;
        public short shortVal;
        public int integerVal;
        public long longVal;
        public float floatVal;
        public double doubleVal;
        public String stringVal;
        public LinkedList<String> stringList;
        public HashMap<String, Object> stringMap;
        public HashSet<Point> points;
        public List gstringList;
        public Map gstringMap;
        public Set gpoints;
        
        public MarshalTestClass1() {
            this.BooleanVal = true;
            this.ByteVal = 3;
            this.ShortVal = 3;
            this.IntegerVal = 3;
            this.LongVal = 3L;
            this.FloatVal = 3.0f;
            this.DoubleVal = 3.0;
            this.booleanVal = true;
            this.byteVal = 3;
            this.shortVal = 3;
            this.integerVal = 3;
            this.longVal = 3L;
            this.floatVal = 3.0f;
            this.doubleVal = 3.0;
            this.stringVal = "3";
            this.stringList = new LinkedList<String>();
            this.stringMap = new HashMap<String, Object>();
            this.points = new HashSet<Point>();
            this.gstringList = new LinkedList();
            this.gstringMap = new HashMap();
            this.gpoints = new HashSet();
        }
    }
    
    public static class MarshalTestClass2 extends MarshalTestClass1
    {
        public MarshalTestClass1 myFirstClass1;
        public MarshalTestClass1 mySecondClass1;
    }
}
