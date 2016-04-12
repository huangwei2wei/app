// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.marshalling;

import java.util.Hashtable;
import java.io.IOException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import atavism.server.engine.PropertyFileReader;
import java.lang.management.ManagementFactory;
import atavism.server.util.Log;
import java.util.Properties;

public class Trampoline
{
    private static Properties properties;
    
    protected static Class getClassForClassName(final String className) {
        try {
            final Class c = Class.forName(className);
            return c;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static void main(final String[] argv) throws Throwable {
        if (argv.length < 1) {
            System.out.println("Usage: java atavism.server.marshalling.Trampoline <main_class> [args]");
            return;
        }
        final String mainClassName = argv[0];
        final String[] new_argv = new String[argv.length - 1];
        System.arraycopy(argv, 1, new_argv, 0, new_argv.length);
        final boolean disableLogs = System.getProperty("atavism.disable_logs", "false").equals("true");
        Log.init();
        String pid = "?";
        final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        pid = runtimeBean.getName();
        final PropertyFileReader pfr = new PropertyFileReader();
        if (PropertyFileReader.usePropFile) {
            Trampoline.properties = pfr.readPropFile();
        }
        Trampoline.properties = parsePropertyArgs(argv, Trampoline.properties);
        final String logLevelString = Trampoline.properties.getProperty("atavism.log_level");
        Integer logLevel = null;
        if (logLevelString != null) {
            try {
                logLevel = Integer.parseInt(logLevelString.trim());
            }
            catch (Exception ex3) {}
        }
        if (!disableLogs) {
            Log.init(Trampoline.properties);
        }
        else if (logLevel != null) {
            Log.setLogLevel(logLevel);
        }
        Log.info("pid " + pid);
        writePidFile(argv, pid);
        Log.debug("Using property file " + PropertyFileReader.propFile);
        Log.debug("Properties are:");
        final Enumeration en = Trampoline.properties.propertyNames();
        while (en.hasMoreElements()) {
            final String sKey = en.nextElement();
            Log.debug("    " + sKey + " = " + Trampoline.properties.getProperty(sKey));
        }
        if (logLevel != null) {
            Log.setLogLevel(logLevel);
        }
        Log.info("The log level is " + Log.getLogLevel());
        final String build = Trampoline.properties.getProperty("atavism.build");
        if (build != null) {
            Log.info("Atavism Server Build " + build);
        }
        final String[] mr_argv = new String[new_argv.length];
        System.arraycopy(new_argv, 0, mr_argv, 0, mr_argv.length);
        if (MarshallingRuntime.initialize(mr_argv)) {
            System.out.println("Exiting because MarshallingRuntime.initialize() found missing or incorrect classes");
            System.exit(1);
        }
        final Class cl = getClassForClassName(mainClassName);
        if (cl == null) {
            System.out.println("Loading of class '" + mainClassName + "' returned null!");
            return;
        }
        Method method = null;
        try {
            method = cl.getMethod("main", argv.getClass());
            final int m = method.getModifiers();
            final Class r = method.getReturnType();
            if (!Modifier.isPublic(m) || !Modifier.isStatic(m) || Modifier.isAbstract(m) || r != Void.TYPE) {
                throw new NoSuchMethodException();
            }
        }
        catch (NoSuchMethodException no) {
            System.out.println("In class " + mainClassName + ": public static void main(String[] argv) is not defined");
            return;
        }
        try {
            method.invoke(null, new_argv);
        }
        catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
        catch (Exception ex2) {
            throw ex2;
        }
    }
    
    public static Properties getProperties() {
        return Trampoline.properties;
    }
    
    private static Properties parsePropertyArgs(final String[] args, final Properties defaults) {
        final Properties props = new Properties(defaults);
        for (int ii = 0; ii < args.length; ++ii) {
            if (args[ii].startsWith("-P") && args[ii].indexOf(61) != -1) {
                final int equal = args[ii].indexOf(61);
                final String key = args[ii].substring(2, equal);
                final String value = args[ii].substring(equal + 1);
                ((Hashtable<String, String>)props).put(key, value);
            }
        }
        return props;
    }
    
    private static void writePidFile(final String[] argv, final String pid) {
        String pidFileName = null;
        for (int ii = 0; ii < argv.length; ++ii) {
            if (argv[ii].equals("--pid")) {
                pidFileName = argv[ii + 1];
                break;
            }
        }
        if (pidFileName == null) {
            return;
        }
        int nDigits = 0;
        for (int ii2 = 0; ii2 < pid.length() && Character.isDigit(pid.charAt(ii2)); ++ii2) {
            ++nDigits;
        }
        if (nDigits == 0) {
            return;
        }
        try {
            final FileOutputStream pidFile = new FileOutputStream(pidFileName);
            pidFile.write((pid.substring(0, nDigits) + "\n").getBytes());
            pidFile.close();
        }
        catch (IOException e) {
            Log.exception(pidFileName, e);
        }
    }
    
    static {
        Trampoline.properties = null;
    }
}
