// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.marshalling;

import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.Repository;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.IF_ACMPEQ;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ConstantPoolGen;
import java.io.File;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.Type;
import java.util.Iterator;
import atavism.server.util.Log;
import org.apache.bcel.classfile.Field;
import java.util.LinkedList;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ObjectType;
import java.util.HashSet;

public class InjectionGenerator
{
    int currentStack;
    int maxStack;
    HashSet<MarshallingRuntime.ClassNameAndTypeNumber> classesToBeMarshalled;
    static String marshallingRuntimeClassName;
    static String marshallableClassName;
    static String aoByteBufferClassName;
    ObjectType aoByteBufferType;
    ObjectType OBJBOOLEAN;
    ObjectType OBJBYTE;
    ObjectType OBJCHAR;
    ObjectType OBJDOUBLE;
    ObjectType OBJFLOAT;
    ObjectType OBJINT;
    ObjectType OBJLONG;
    ObjectType OBJSHORT;
    protected boolean generateClassFiles;
    protected String outputDir;
    protected boolean listGeneratedCode;
    protected static PrimitiveTypeInfo[] primitiveTypes;
    protected static InjectionGenerator instance;
    
    public InjectionGenerator(final boolean generateClassFiles, final String outputDir, final boolean listGeneratedCode) {
        this.generateClassFiles = false;
        this.outputDir = "";
        this.listGeneratedCode = false;
        this.generateClassFiles = generateClassFiles;
        this.outputDir = outputDir;
        this.listGeneratedCode = listGeneratedCode;
    }
    
    public static InjectionGenerator getInstance() {
        return InjectionGenerator.instance;
    }
    
    public JavaClass maybeInjectMarshalling(JavaClass clazz, final Short typeNum) {
        final String className = clazz.getClassName();
        int flagBitCount = 0;
        final LinkedList<Field> fields = getValidClassFields(clazz);
        final LinkedList<Integer> nullTestedFields = new LinkedList<Integer>();
        int index = -1;
        for (final Field f : fields) {
            ++index;
            final Type fieldType = f.getType();
            if (!isPrimitiveType(fieldType)) {
                nullTestedFields.add(index);
                ++flagBitCount;
            }
        }
        final JavaClass superclass = getValidSuperclass(clazz);
        try {
            clazz = this.injectMarshallingMethods(clazz, superclass, fields, nullTestedFields, flagBitCount);
            MarshallingRuntime.markInjected(className);
            Log.debug("Generated marshalling for '" + className + "', typeNum " + typeNum + "/0x" + Integer.toHexString(typeNum));
        }
        catch (Exception e) {
            Log.error("Injection into class '" + className + "' terminated due to exception: " + e.getMessage());
        }
        return clazz;
    }
    
    protected JavaClass injectMarshallingMethods(final JavaClass clazz, final JavaClass superclass, final LinkedList<Field> fields, final LinkedList<Integer> nullTestedFields, final int flagBitCount) {
        final ClassGen classGen = new ClassGen(clazz);
        final JavaClass updatedClass = classGen.getJavaClass();
        final String className = updatedClass.getClassName();
        final ConstantPoolGen cp = classGen.getConstantPool();
        final InstructionFactory factory = new InstructionFactory(cp);
        this.initStack();
        final MethodGen mmg = this.createMarshallingMethod(clazz, superclass, cp, factory, fields, nullTestedFields, flagBitCount);
        mmg.setMaxStack(this.getFinalStack());
        this.initStack();
        final MethodGen umg = this.createUnmarshallingMethod(clazz, superclass, cp, factory, fields, nullTestedFields, flagBitCount);
        umg.setMaxStack(this.getFinalStack());
        classGen.addInterface(InjectionGenerator.marshallableClassName);
        classGen.addMethod(mmg.getMethod());
        classGen.addMethod(umg.getMethod());
        if (this.listGeneratedCode) {
            Log.debug("ConstantPool:\n" + cp.toString() + "\n");
            this.logGeneratedMethod(classGen, mmg);
            this.logGeneratedMethod(classGen, umg);
        }
        mmg.getInstructionList().dispose();
        umg.getInstructionList().dispose();
        if (this.generateClassFiles) {
            String pathname;
            if (this.outputDir != "") {
                pathname = this.outputDir + className.replace(".", File.separator) + ".class";
            }
            else {
                pathname = "." + File.separator + "build" + File.separator + className.replace(".", File.separator) + ".class";
            }
            try {
                Log.debug("Replacing class file '" + className + "'");
                classGen.getJavaClass().dump(pathname);
            }
            catch (Exception e) {
                Log.error("Exception raised when writing class '" + updatedClass.getClassName() + "': " + e.getMessage());
            }
        }
        return classGen.getJavaClass();
    }
    
    protected void logGeneratedMethod(final ClassGen classGen, final MethodGen method) {
        Log.debug("Method details:\n" + method.toString() + "\n" + "Method instructions:\n" + method.getInstructionList().toString());
    }
    
    protected MethodGen createMarshallingMethod(final JavaClass clazz, final JavaClass superclass, final ConstantPoolGen cp, final InstructionFactory factory, final LinkedList<Field> fields, final LinkedList<Integer> nullTestedFields, final int flagBitCount) {
        final String className = clazz.getClassName();
        final InstructionList il = new InstructionList();
        final MethodGen mg = new MethodGen(1, (Type)Type.VOID, new Type[] { this.aoByteBufferType }, new String[] { "buf" }, "marshalObject", className, il, cp);
        if (superclass != null) {
            il.append((Instruction)new ALOAD(0));
            il.append((Instruction)new ALOAD(1));
            this.addStack(2);
            il.append((Instruction)factory.createInvoke(superclass.getClassName(), "marshalObject", (Type)Type.VOID, new Type[] { this.aoByteBufferType }, (short)183));
            this.addStack(-2);
        }
        LocalVariableGen flagVar = null;
        int flagVarIndex = 0;
        if (flagBitCount > 0) {
            flagVar = mg.addLocalVariable("flag_bits", (Type)Type.BYTE, (InstructionHandle)null, (InstructionHandle)null);
            flagVarIndex = flagVar.getIndex();
        }
        for (int batches = (flagBitCount + 7) / 8, i = 0; i < batches; ++i) {
            il.append(factory.createConstant((Object)0));
            this.addStack(1);
            il.append((Instruction)InstructionFactory.createStore((Type)Type.BYTE, flagVarIndex));
            this.addStack(-1);
            int j;
            for (int limit = Math.min(flagBitCount, (i + 1) * 8), start = j = i * 8; j < limit; ++j) {
                final boolean firstInBatch = (j & 0x7) == 0x0;
                final Field f = fields.get(nullTestedFields.get(j));
                final BranchFixup[] branchFixups = this.makeOmittedTest(clazz, f, factory, il);
                if (!firstInBatch) {
                    il.append((Instruction)InstructionFactory.createLoad((Type)Type.BYTE, flagVarIndex));
                    this.addStack(1);
                }
                il.append(factory.createConstant((Object)(1 << j - start)));
                this.addStack(1);
                if (!firstInBatch) {
                    il.append((Instruction)InstructionFactory.createBinaryOperation("|", (Type)Type.BYTE));
                    il.append(factory.createCast((Type)Type.INT, (Type)Type.BYTE));
                    this.addStack(-1);
                }
                il.append((Instruction)InstructionFactory.createStore((Type)Type.BYTE, flagVarIndex));
                this.addStack(-1);
                this.noteBranchTargets(branchFixups, il);
            }
            il.append((Instruction)new ALOAD(1));
            il.append((Instruction)InstructionFactory.createLoad((Type)Type.BYTE, flagVarIndex));
            this.addStack(2);
            il.append((Instruction)factory.createInvoke(InjectionGenerator.aoByteBufferClassName, "putByte", (Type)this.aoByteBufferType, new Type[] { Type.BYTE }, (short)182));
            il.append((Instruction)InstructionFactory.createPop(1));
            this.addStack(-2);
        }
        int index = -1;
        for (final Field f2 : fields) {
            ++index;
            final boolean tested = nullTestedFields.contains(index);
            BranchFixup[] branchFixups2 = null;
            if (tested) {
                branchFixups2 = this.makeOmittedTest(clazz, f2, factory, il);
            }
            this.addMarshallingForField(clazz, f2, factory, il);
            if (tested) {
                this.noteBranchTargets(branchFixups2, il);
            }
        }
        il.append((Instruction)InstructionFactory.createReturn((Type)Type.VOID));
        BranchFixup.fixAllFixups(il);
        return mg;
    }
    
    protected void addMarshallingForField(final JavaClass clazz, final Field f, final InstructionFactory factory, final InstructionList il) {
        final Type fieldType = f.getType();
        final PrimitiveTypeInfo info = getPrimitiveTypeInfo(fieldType);
        if (info != null || this.isStringType(fieldType)) {
            this.addAOByteBufferFieldPut(clazz, f, fieldType, info, factory, il);
        }
        else if (fieldType instanceof ObjectType) {
            final ObjectType fieldObjectType = (ObjectType)fieldType;
            if (this.marshalledByMarshallingRuntimeMarshalObject(fieldObjectType)) {
                il.append((Instruction)new ALOAD(1));
                this.addStack(1);
                this.addFieldFetch(clazz, factory, f, il);
                il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "marshalObject", (Type)Type.VOID, new Type[] { this.aoByteBufferType, Type.OBJECT }, (short)184));
                this.addStack(-(1 + fieldType.getSize()));
            }
            else {
                final Short aggregateTypeNum = this.getAggregateTypeNum(fieldObjectType);
                if (aggregateTypeNum != null) {
                    final String s = this.aggregateTypeString(fieldObjectType);
                    il.append((Instruction)new ALOAD(1));
                    this.addStack(1);
                    this.addFieldFetch(clazz, factory, f, il);
                    il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "marshal" + s, (Type)Type.VOID, new Type[] { this.aoByteBufferType, Type.OBJECT }, (short)184));
                    this.addStack(-(1 + fieldType.getSize()));
                }
                else {
                    il.append((Instruction)new ALOAD(1));
                    this.addStack(1);
                    this.addFieldFetch(clazz, factory, f, il);
                    il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "marshalSerializable", (Type)Type.VOID, new Type[] { this.aoByteBufferType, fieldType }, (short)184));
                    this.addStack(-(1 + fieldType.getSize()));
                }
            }
        }
        else if (fieldType instanceof ArrayType) {
            il.append((Instruction)new ALOAD(1));
            this.addStack(1);
            this.addFieldFetch(clazz, factory, f, il);
            il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "marshalArray", (Type)Type.VOID, new Type[] { this.aoByteBufferType, fieldType }, (short)184));
            this.addStack(-2);
        }
        else {
            throwError("In addtoBytesForField, unknown type '" + fieldType + "'");
        }
    }
    
    protected void addAOByteBufferFieldPut(final JavaClass clazz, final Field f, final Type fieldType, final PrimitiveTypeInfo info, final InstructionFactory factory, final InstructionList il) {
        il.append((Instruction)new ALOAD(1));
        this.addStack(1);
        this.addFieldFetch(clazz, factory, f, il);
        if (this.isStringType(fieldType)) {
            il.append((Instruction)factory.createInvoke(InjectionGenerator.aoByteBufferClassName, "putString", (Type)this.aoByteBufferType, new Type[] { Type.STRING }, (short)182));
            il.append((Instruction)InstructionFactory.createPop(1));
            this.addStack(-(1 + fieldType.getSize()));
            return;
        }
        if (fieldType instanceof ObjectType) {
            final String className = info.objectType.getClassName();
            il.append((Instruction)factory.createInvoke(className, info.valueString + "Value", info.type, new Type[0], (short)182));
            this.addStack(-1 + info.type.getSize());
        }
        if (info.type == Type.BOOLEAN) {
            final BranchHandle branch1 = il.append(InstructionFactory.createBranchInstruction((short)153, il.getStart()));
            this.addStack(-1);
            final BranchFixup branchFixup1 = new BranchFixup(branch1, il);
            il.append(factory.createConstant((Object)1));
            this.addStack(1);
            final BranchHandle branch2 = il.append(InstructionFactory.createBranchInstruction((short)167, il.getStart()));
            final BranchFixup branchFixup2 = new BranchFixup(branch2, il);
            branchFixup1.atTarget(il);
            il.append(factory.createConstant((Object)0));
            branchFixup2.atTarget(il);
            il.append(factory.createCast((Type)Type.INT, (Type)Type.BYTE));
        }
        il.append((Instruction)factory.createInvoke(InjectionGenerator.aoByteBufferClassName, "put" + info.aoByteBufferSuffix, (Type)this.aoByteBufferType, new Type[] { this.storageType(info.type) }, (short)182));
        il.append((Instruction)InstructionFactory.createPop(1));
        this.addStack(-(1 + info.type.getSize()));
    }
    
    protected BranchFixup[] makeOmittedTest(final JavaClass clazz, final Field f, final InstructionFactory factory, final InstructionList il) {
        this.addFieldFetch(clazz, factory, f, il);
        final BranchHandle branch1 = il.append(InstructionFactory.createBranchInstruction((short)198, il.getStart()));
        this.addStack(-1);
        final BranchFixup branchFixup1 = new BranchFixup(branch1, il);
        BranchFixup branchFixup2 = null;
        if (this.isStringType(f.getType())) {
            this.addFieldFetch(clazz, factory, f, il);
            il.append(factory.createConstant((Object)""));
            this.addStack(1);
            final BranchHandle branch2 = il.append((BranchInstruction)new IF_ACMPEQ(il.getStart()));
            this.addStack(-2);
            branchFixup2 = new BranchFixup(branch2, il);
        }
        if (branchFixup2 != null) {
            return new BranchFixup[] { branchFixup1, branchFixup2 };
        }
        return new BranchFixup[] { branchFixup1 };
    }
    
    protected MethodGen createUnmarshallingMethod(final JavaClass clazz, final JavaClass superclass, final ConstantPoolGen cp, final InstructionFactory factory, final LinkedList<Field> fields, final LinkedList<Integer> nullTestedFields, final int flagBitCount) {
        final String className = clazz.getClassName();
        final InstructionList il = new InstructionList();
        final MethodGen mg = new MethodGen(1, (Type)Type.OBJECT, new Type[] { this.aoByteBufferType }, new String[] { "buf" }, "unmarshalObject", className, il, cp);
        if (superclass != null) {
            il.append((Instruction)new ALOAD(0));
            il.append((Instruction)new ALOAD(1));
            this.addStack(2);
            il.append((Instruction)factory.createInvoke(superclass.getClassName(), "unmarshalObject", (Type)Type.OBJECT, new Type[] { this.aoByteBufferType }, (short)183));
            il.append((Instruction)InstructionFactory.createPop(1));
            this.addStack(-2);
        }
        final int batches = (flagBitCount + 7) / 8;
        LocalVariableGen[] flagVars = null;
        Integer[] flagVarIndices = null;
        if (flagBitCount > 0) {
            flagVars = new LocalVariableGen[batches];
            flagVarIndices = new Integer[batches];
            for (int i = 0; i < batches; ++i) {
                flagVars[i] = mg.addLocalVariable("flag_bits" + i, (Type)Type.BYTE, (InstructionHandle)null, (InstructionHandle)null);
                flagVarIndices[i] = flagVars[i].getIndex();
                il.append((Instruction)new ALOAD(1));
                this.addStack(1);
                il.append((Instruction)factory.createInvoke(InjectionGenerator.aoByteBufferClassName, "getByte", (Type)Type.BYTE, new Type[0], (short)182));
                il.append((Instruction)InstructionFactory.createStore((Type)Type.BYTE, (int)flagVarIndices[i]));
                this.addStack(-1);
            }
        }
        int flagBitIndex = -1;
        int fieldIndex = -1;
        for (final Field f : fields) {
            ++fieldIndex;
            final Type fieldType = f.getType();
            final boolean tested = nullTestedFields.contains(fieldIndex);
            BranchHandle branch = null;
            BranchFixup branchFixup = null;
            if (tested) {
                final int flagVarNumber = ++flagBitIndex >> 3;
                final int flagBitNumber = flagBitIndex & 0x7;
                il.append((Instruction)InstructionFactory.createLoad((Type)Type.BYTE, flagVars[flagVarNumber].getIndex()));
                il.append(factory.createConstant((Object)(1 << flagBitNumber)));
                this.addStack(2);
                il.append((Instruction)InstructionFactory.createBinaryOperation("&", (Type)Type.BYTE));
                this.addStack(-1);
                branch = il.append(InstructionFactory.createBranchInstruction((short)153, (InstructionHandle)null));
                this.addStack(-1);
                branchFixup = new BranchFixup(branch, il);
            }
            this.addUnmarshallingForField(clazz, f, factory, cp, il);
            il.append((Instruction)factory.createPutField(clazz.getClassName(), f.getName(), fieldType));
            this.addStack(-(1 + fieldType.getSize()));
            if (branch != null) {
                branchFixup.atTarget(il);
            }
        }
        il.append((Instruction)new ALOAD(0));
        this.addStack(1);
        il.append((Instruction)InstructionFactory.createReturn((Type)Type.OBJECT));
        this.addStack(-1);
        BranchFixup.fixAllFixups(il);
        return mg;
    }
    
    protected void addUnmarshallingForField(final JavaClass clazz, final Field f, final InstructionFactory factory, final ConstantPoolGen cp, final InstructionList il) {
        final Type fieldType = f.getType();
        final PrimitiveTypeInfo info = getPrimitiveTypeInfo(fieldType);
        il.append((Instruction)new ALOAD(0));
        this.addStack(1);
        if (info != null || this.isStringType(fieldType)) {
            this.addAOByteBufferFieldGet(clazz, f, fieldType, info, factory, il);
        }
        else if (fieldType instanceof ObjectType) {
            final ObjectType fieldObjectType = (ObjectType)fieldType;
            il.append((Instruction)new ALOAD(1));
            this.addStack(1);
            if (this.marshalledByMarshallingRuntimeMarshalObject(fieldObjectType)) {
                il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "unmarshalObject", (Type)Type.OBJECT, new Type[] { this.aoByteBufferType }, (short)184));
            }
            else {
                final Short aggregateTypeNum = this.getAggregateTypeNum(fieldObjectType);
                if (aggregateTypeNum != null) {
                    final String s = this.aggregateTypeString(fieldObjectType);
                    il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "unmarshal" + s, (Type)Type.OBJECT, new Type[] { this.aoByteBufferType }, (short)184));
                }
                else {
                    il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "unmarshalSerializable", (Type)Type.OBJECT, new Type[] { this.aoByteBufferType }, (short)184));
                }
            }
            if (!fieldObjectType.getClassName().equals("java.lang.Object")) {
                il.append((Instruction)new CHECKCAST(cp.addClass(fieldObjectType.getClassName())));
            }
        }
        else if (fieldType instanceof ArrayType) {
            il.append((Instruction)factory.createInvoke(InjectionGenerator.marshallingRuntimeClassName, "unmarshalArray", (Type)Type.OBJECT, new Type[] { this.aoByteBufferType }, (short)184));
        }
        else {
            throwError("In addtoBytesForField, unknown type '" + fieldType + "'");
        }
    }
    
    protected void addAOByteBufferFieldGet(final JavaClass clazz, final Field f, final Type fieldType, final PrimitiveTypeInfo info, final InstructionFactory factory, final InstructionList il) {
        il.append((Instruction)new ALOAD(1));
        this.addStack(1);
        if (this.isStringType(fieldType)) {
            il.append((Instruction)factory.createInvoke(InjectionGenerator.aoByteBufferClassName, "getString", (Type)Type.STRING, new Type[0], (short)182));
            return;
        }
        il.append((Instruction)factory.createInvoke(InjectionGenerator.aoByteBufferClassName, "get" + info.aoByteBufferSuffix, this.storageType(info.type), new Type[0], (short)182));
        this.addStack(-1 + info.type.getSize());
        if (info.type == Type.BOOLEAN) {
            final BranchHandle branch1 = il.append(InstructionFactory.createBranchInstruction((short)153, il.getStart()));
            this.addStack(-1);
            final BranchFixup branchFixup1 = new BranchFixup(branch1, il);
            il.append(factory.createConstant((Object)1));
            this.addStack(1);
            final BranchHandle branch2 = il.append(InstructionFactory.createBranchInstruction((short)167, il.getStart()));
            final BranchFixup branchFixup2 = new BranchFixup(branch2, il);
            branchFixup1.atTarget(il);
            il.append(factory.createConstant((Object)0));
            branchFixup2.atTarget(il);
        }
        if (fieldType instanceof ObjectType) {
            final String className = info.objectType.getClassName();
            il.append((Instruction)factory.createInvoke(className, "valueOf", (Type)info.objectType, new Type[] { info.type }, (short)184));
            this.addStack(1 - info.type.getSize());
        }
    }
    
    protected void addFieldFetch(final JavaClass clazz, final InstructionFactory factory, final Field f, final InstructionList il) {
        il.append((Instruction)new ALOAD(0));
        this.addStack(1);
        final Type fieldType = f.getType();
        il.append((Instruction)factory.createGetField(clazz.getClassName(), f.getName(), fieldType));
        final int size = fieldType.getSize();
        this.addStack(-1 + size);
    }
    
    protected static JavaClass lookupClass(final String className) {
        try {
            return Repository.lookupClass(className);
        }
        catch (Exception e) {
            throwError("Could not find class '" + className + "'");
            return null;
        }
    }
    
    protected static PrimitiveTypeInfo getPrimitiveTypeInfo(final Type type) {
        if (type instanceof ObjectType) {
            final ObjectType ot = (ObjectType)type;
            final String otName = ot.getClassName();
            for (final PrimitiveTypeInfo info : InjectionGenerator.primitiveTypes) {
                if (otName.equals(info.objectType.getClassName())) {
                    return info;
                }
            }
        }
        else {
            for (final PrimitiveTypeInfo info2 : InjectionGenerator.primitiveTypes) {
                if (type == info2.type) {
                    return info2;
                }
            }
        }
        return null;
    }
    
    protected boolean isStringType(final Type type) {
        if (!(type instanceof ObjectType)) {
            return false;
        }
        final ObjectType objectType = (ObjectType)type;
        return objectType.getClassName().equals("java.lang.String");
    }
    
    protected static boolean isPrimitiveObjectType(final Type type) {
        if (type instanceof ObjectType) {
            final ObjectType ot = (ObjectType)type;
            final String otName = ot.getClassName();
            for (final PrimitiveTypeInfo info : InjectionGenerator.primitiveTypes) {
                if (otName.equals(info.objectType.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected static boolean isPrimitiveType(final Type type) {
        for (final PrimitiveTypeInfo info : InjectionGenerator.primitiveTypes) {
            if (type == info.type) {
                return true;
            }
        }
        return false;
    }
    
    protected static String nonPrimitiveObjectTypeName(final Type type) {
        if (!isPrimitiveType(type) && type instanceof ObjectType) {
            final ObjectType ot = (ObjectType)type;
            final String typeName = ot.getClassName();
            return typeName;
        }
        return null;
    }
    
    protected Type underlyingPrimitiveType(final Type type) {
        if (type instanceof ObjectType) {
            final ObjectType ot = (ObjectType)type;
            final String typeName = ot.getClassName();
            for (final PrimitiveTypeInfo info : InjectionGenerator.primitiveTypes) {
                if (typeName.equals(info.objectType.getClassName())) {
                    return info.type;
                }
            }
        }
        else {
            for (final PrimitiveTypeInfo info2 : InjectionGenerator.primitiveTypes) {
                if (type == info2.type) {
                    return type;
                }
            }
        }
        return null;
    }
    
    protected Type storageType(final Type type) {
        final Type underlying = this.underlyingPrimitiveType(type);
        if (underlying == null) {
            throwError("In storageType, unknown type '" + type + "'");
            return null;
        }
        if (underlying == Type.BOOLEAN) {
            return (Type)Type.BYTE;
        }
        return underlying;
    }
    
    protected Short getAggregateTypeNum(final ObjectType fieldObjectType) {
        final String s = fieldObjectType.getClassName();
        final Short typeNum = MarshallingRuntime.builtinAggregateTypeNum(s);
        if (typeNum != null) {
            return typeNum;
        }
        if (s.equals("java.util.List")) {
            return 9;
        }
        if (s.equals("java.util.Map")) {
            return 11;
        }
        if (s.equals("java.util.Set")) {
            return 14;
        }
        return null;
    }
    
    protected String aggregateTypeString(final ObjectType fieldObjectType) {
        final String s = fieldObjectType.getClassName();
        if (MarshallingRuntime.builtinAggregateTypeNum(s) != null) {
            final int indexOfDot = s.lastIndexOf(46);
            if (indexOfDot >= 0) {
                return s.substring(indexOfDot + 1);
            }
        }
        throwError("InjectionGenerator:addMarshallingForField: unrecognized aggregate type " + s);
        return "";
    }
    
    protected boolean marshalledByMarshallingRuntimeMarshalObject(final ObjectType fieldObjectType) {
        return this.doesOrWillHandleMarshallable(fieldObjectType) || this.referencesInterface(fieldObjectType) || fieldObjectType.getClassName().equals("java.lang.Object");
    }
    
    protected boolean referencesInterface(final ObjectType type) {
        try {
            return type.referencesInterfaceExact();
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    protected boolean doesOrWillHandleMarshallable(final ObjectType type) {
        final String className = type.getClassName();
        if (MarshallingRuntime.hasMarshallingProperties(className)) {
            return MarshallingRuntime.injectedClass(className);
        }
        return this.handlesMarshallable(type);
    }
    
    protected boolean handlesMarshallable(final ObjectType type) {
        final String typeName = type.getClassName();
        return handlesMarshallable(typeName);
    }
    
    public static boolean handlesMarshallable(final String typeName) {
        final JavaClass clazz = lookupClass(typeName);
        if (clazz == null) {
            throwError("InjectionGenerator.handlesMarshallable: Could not find class '" + typeName + "'");
        }
        return handlesMarshallable(clazz);
    }
    
    public static boolean handlesMarshallable(final JavaClass clazz) {
        final String[] arr$;
        final String[] names = arr$ = clazz.getInterfaceNames();
        for (final String name : arr$) {
            if (InjectionGenerator.marshallableClassName.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean objectTypeIsInterface(final ObjectType type) {
        return interfaceClass(type.getClassName());
    }
    
    public static boolean interfaceClass(final String s) {
        try {
            final JavaClass jc = Repository.lookupClass(s);
            return !jc.isClass();
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    protected static LinkedList<Field> getValidClassFields(final JavaClass c) {
        final LinkedList<Field> validFields = new LinkedList<Field>();
        final Field[] arr$;
        final Field[] fields = arr$ = c.getFields();
        for (final Field f : arr$) {
            if (!f.isStatic() && !f.isTransient()) {
                validFields.add(f);
            }
        }
        return validFields;
    }
    
    public static JavaClass getValidSuperclass(final JavaClass c) {
        final String superclassName = c.getSuperclassName();
        if (superclassName == null) {
            return null;
        }
        final JavaClass superclass = lookupClass(superclassName);
        if (superclass != null && !superclass.getClassName().equals("java.lang.Object") && !superclass.isInterface()) {
            return superclass;
        }
        return null;
    }
    
    protected void noteBranchTargets(final BranchFixup[] branchFixups, final InstructionList il) {
        for (final BranchFixup branchFixup : branchFixups) {
            branchFixup.atTarget(il);
        }
    }
    
    protected void initStack() {
        this.currentStack = 0;
        this.maxStack = 0;
    }
    
    protected void addStack(final int count) {
        final int newCurrent = this.currentStack + count;
        if (newCurrent < 0) {
            throwError("InjectionGenerator.addStack: Stack depth below zero!");
        }
        if ((this.currentStack = newCurrent) > this.maxStack) {
            this.maxStack = newCurrent;
        }
    }
    
    protected int getFinalStack() {
        if (this.currentStack != 0) {
            throwError("InjectionGenerator.getFinalStack: Final stack depth should be zero, but is " + this.currentStack);
        }
        return this.maxStack;
    }
    
    protected static void logInvoke(final String s, final InvokeInstruction iv, final ConstantPoolGen cp) {
        Log.debug(s + " signature is '" + iv.getSignature(cp) + "', return type is " + iv.getReturnType(cp));
    }
    
    protected static void throwError(final String msg) {
        Log.error(msg);
        throw new RuntimeException(msg);
    }
    
    protected void initializeGlobals() {
        InjectionGenerator.marshallingRuntimeClassName = "atavism.server.marshalling.MarshallingRuntime";
        InjectionGenerator.marshallableClassName = "atavism.server.marshalling.Marshallable";
        InjectionGenerator.aoByteBufferClassName = "atavism.server.network.AOByteBuffer";
        this.aoByteBufferType = new ObjectType(InjectionGenerator.aoByteBufferClassName);
        this.OBJBOOLEAN = new ObjectType("java.lang.Boolean");
        this.OBJBYTE = new ObjectType("java.lang.Byte");
        this.OBJCHAR = new ObjectType("java.lang.Character");
        this.OBJDOUBLE = new ObjectType("java.lang.Double");
        this.OBJFLOAT = new ObjectType("java.lang.Float");
        this.OBJINT = new ObjectType("java.lang.Integer");
        this.OBJLONG = new ObjectType("java.lang.Long");
        this.OBJSHORT = new ObjectType("java.lang.Short");
        InjectionGenerator.primitiveTypes = new PrimitiveTypeInfo[] { new PrimitiveTypeInfo((Type)Type.BOOLEAN, this.OBJBOOLEAN, "Byte", "boolean"), new PrimitiveTypeInfo((Type)Type.BYTE, this.OBJBYTE, "Byte", "byte"), new PrimitiveTypeInfo((Type)Type.CHAR, this.OBJCHAR, "Char", "char"), new PrimitiveTypeInfo((Type)Type.DOUBLE, this.OBJDOUBLE, "Double", "double"), new PrimitiveTypeInfo((Type)Type.FLOAT, this.OBJFLOAT, "Float", "float"), new PrimitiveTypeInfo((Type)Type.INT, this.OBJINT, "Int", "int"), new PrimitiveTypeInfo((Type)Type.LONG, this.OBJLONG, "Long", "long"), new PrimitiveTypeInfo((Type)Type.SHORT, this.OBJSHORT, "Short", "short") };
    }
    
    public static void initialize(final boolean generateClassFiles, final String outputDir, final boolean listGeneratedCode) {
        (InjectionGenerator.instance = new InjectionGenerator(generateClassFiles, outputDir, listGeneratedCode)).initializeGlobals();
    }
    
    static {
        InjectionGenerator.instance = null;
    }
    
    public static class BranchFixup
    {
        public BranchHandle branch;
        public int lengthAtBranch;
        public int lengthAtTarget;
        public static LinkedList<BranchFixup> allBranchFixups;
        
        public BranchFixup(final BranchHandle branch, final InstructionList il) {
            this.branch = branch;
            this.lengthAtBranch = il.getLength();
            BranchFixup.allBranchFixups.add(this);
        }
        
        public void atTarget(final InstructionList il) {
            this.lengthAtTarget = il.getLength();
        }
        
        public static void fixAllFixups(final InstructionList il) {
            for (final BranchFixup branchFixup : BranchFixup.allBranchFixups) {
                final int delta = branchFixup.lengthAtTarget - branchFixup.lengthAtBranch + 1;
                InstructionHandle handle;
                final BranchHandle branch = (BranchHandle)(handle = (InstructionHandle)branchFixup.branch);
                if (delta > 0) {
                    for (int i = 0; i < delta; ++i) {
                        handle = handle.getNext();
                    }
                }
                else {
                    for (int i = 0; i < -delta; ++i) {
                        handle = handle.getPrev();
                    }
                }
                branch.setTarget(handle);
            }
            BranchFixup.allBranchFixups.clear();
        }
        
        static {
            BranchFixup.allBranchFixups = new LinkedList<BranchFixup>();
        }
    }
    
    public static class PrimitiveTypeInfo
    {
        public Type type;
        public ObjectType objectType;
        public String aoByteBufferSuffix;
        public String valueString;
        
        public PrimitiveTypeInfo(final Type type, final ObjectType objectType, final String aoByteBufferSuffix, final String valueString) {
            this.type = type;
            this.objectType = objectType;
            this.aoByteBufferSuffix = aoByteBufferSuffix;
            this.valueString = valueString;
        }
    }
}
