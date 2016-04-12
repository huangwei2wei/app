// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.marshalling;

import java.lang.reflect.Method;
import org.apache.bcel.util.ClassPath;
import java.util.HashMap;

public class MarshallingClassLoader extends ClassLoader
{
    public static String[] ignoredPackages;
    private HashMap<String, Class> loadedClasses;
    private Class marshallingRuntimeClass;
    private ClassPath classPath;
    private boolean injecting;
    private Method maybeInjectMarshallingMethod;
    private Method addMarshallingClassMethod;
    
    public MarshallingClassLoader(final ClassLoader parent) {
        super(parent);
        this.loadedClasses = new HashMap<String, Class>();
        this.marshallingRuntimeClass = null;
        this.classPath = null;
        this.injecting = false;
        this.maybeInjectMarshallingMethod = null;
        this.addMarshallingClassMethod = null;
        this.classPath = ClassPath.SYSTEM_CLASS_PATH;
    }
    
    @Override
    protected synchronized Class<?> loadClass(final String className, final boolean resolve) throws ClassNotFoundException {
        Class cl = this.loadedClasses.get(className);
        if (cl != null) {
            return (Class<?>)cl;
        }
        for (int i = 0; i < MarshallingClassLoader.ignoredPackages.length; ++i) {
            if (className.startsWith(MarshallingClassLoader.ignoredPackages[i])) {
                cl = this.getParent().loadClass(className);
                return (Class<?>)cl;
            }
        }
        if (!this.injecting && this.marshallingRuntimeClass == null) {
            this.marshallingRuntimeClass = this.loadedClasses.get("atavism.server.marshalling.MarshallingRuntime");
            if (this.marshallingRuntimeClass != null) {
                try {
                    this.maybeInjectMarshallingMethod = this.marshallingRuntimeClass.getMethod("maybeInjectMarshalling", className.getClass());
                    this.addMarshallingClassMethod = this.marshallingRuntimeClass.getMethod("addMarshallingClass", className.getClass(), this.getClass().getClass());
                    this.injecting = true;
                }
                catch (Exception e) {
                    throw new RuntimeException("MarshallingClassLoader.loadClass: Could not find MarshallingRuntime.maybeInjectMarshalling method");
                }
            }
        }
        boolean classInjected = false;
        if (this.injecting) {
            try {
                final byte[] bytes = (byte[])this.maybeInjectMarshallingMethod.invoke(null, className);
                if (bytes != null) {
                    cl = this.defineClass(className, bytes, 0, bytes.length);
                    classInjected = true;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        final Class existingClass = this.loadedClasses.get(className);
        if (existingClass != null) {
            return (Class<?>)existingClass;
        }
        if (cl == null) {
            cl = this.loadClassWithoutInjection(className);
        }
        if (resolve) {
            this.resolveClass(cl);
        }
        this.loadedClasses.put(className, cl);
        if (classInjected) {
            try {
                this.addMarshallingClassMethod.invoke(null, className, cl);
            }
            catch (Exception ex2) {
                System.out.println("Exception while loading class " + className + ": " + ex2);
                ex2.printStackTrace();
                return null;
            }
        }
        return (Class<?>)cl;
    }
    
    protected Class loadClassWithoutInjection(final String className) throws ClassNotFoundException {
        try {
            final byte[] bytes = this.classPath.getBytes(className);
            final Class cl = this.defineClass(className, bytes, 0, bytes.length);
            return cl;
        }
        catch (Exception e) {
            throw new ClassNotFoundException("loadClassWithoutInjection: exception loading class '" + className + "': " + e.toString());
        }
    }
    
    static {
        MarshallingClassLoader.ignoredPackages = new String[] { "java.", "javax.", "sun.", "apache.", "org.", "com.sun." };
    }
}
