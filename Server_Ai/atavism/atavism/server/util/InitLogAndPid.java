// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Hashtable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Enumeration;
import java.lang.management.RuntimeMXBean;
import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.engine.PropertyFileReader;
import java.lang.management.ManagementFactory;
import java.util.Properties;

public class InitLogAndPid
{
    public static Properties initLogAndPid(final String[] args) {
        return initLogAndPid(args, null, null);
    }
    
    public static Properties initLogAndPid(final String[] args, final String worldName, final String hostName) {
        final boolean disableLogs = System.getProperty("atavism.disable_logs", "false").equals("true");
        Log.init();
        String pid = "?";
        final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        pid = runtimeBean.getName();
        final Properties properties = readAndParseProperties(args, worldName, hostName);
        final String logLevelString = properties.getProperty("atavism.log_level");
        Integer logLevel = null;
        if (logLevelString != null) {
            try {
                logLevel = Integer.parseInt(logLevelString.trim());
            }
            catch (Exception ex) {}
        }
        if (!disableLogs) {
            Log.init(properties);
        }
        else if (logLevel != null) {
            Log.setLogLevel(logLevel);
        }
        Log.info("pid " + pid);
        writePidFile(args, pid);
        Log.debug("Using property file " + PropertyFileReader.propFile);
        Log.debug("Properties are:");
        final Enumeration en = properties.propertyNames();
        while (en.hasMoreElements()) {
            final String sKey = en.nextElement();
            Log.debug("    " + sKey + " = " + properties.getProperty(sKey));
        }
        if (logLevel != null) {
            Log.setLogLevel(logLevel);
        }
        Log.info("The log level is " + Log.getLogLevel());
        final String build = properties.getProperty("atavism.build");
        if (build != null) {
            Log.info("Atavism Server Build " + build);
        }
        Log.info("Atavism server version " + ServerVersion.getVersionString());
        final String typeNumFileName = getTypeNumbersArg(args);
        if (typeNumFileName != "") {
            MarshallingRuntime.initializeBatch(typeNumFileName);
        }
        return properties;
    }
    
    public static String getTypeNumbersArg(final String[] args) {
        for (int i = 0; i < args.length - 1; ++i) {
            if (args[i].equals("-t")) {
                return args[i + 1];
            }
        }
        return "";
    }
    
    public static Properties readAndParseProperties(final String[] args, final String worldName, final String hostName) {
        final PropertyFileReader pfr = new PropertyFileReader();
        Properties properties = null;
        if (PropertyFileReader.usePropFile) {
            properties = pfr.readPropFile();
        }
        final String worldConfigDir = System.getenv("AO_WORLD_CONFIG");
        if (worldName != null && worldConfigDir != null) {
            properties = readPropertyFile(worldConfigDir + "/world.properties", properties);
        }
        if (hostName != null && worldConfigDir != null) {
            final String propfilename = worldConfigDir + "/" + hostName + ".properties";
            properties = readPropertyFile(propfilename, properties);
        }
        properties = parsePropertyArgs(args, properties);
        return properties;
    }
    
    private static Properties readPropertyFile(final String fileName, Properties properties) {
        final File propertyFile = new File(fileName);
        if (propertyFile.exists()) {
            final Properties overrideProperties = new Properties(properties);
            try {
                overrideProperties.load(new FileInputStream(propertyFile));
                properties = overrideProperties;
            }
            catch (IOException e) {
                Log.exception("Loading properties file " + fileName, e);
            }
        }
        return properties;
    }
    
    private static Properties parsePropertyArgs(final String[] args, final Properties defaults) {
        Properties properties = new Properties(defaults);
        for (int ii = 0; ii < args.length; ++ii) {
            if (args[ii].startsWith("-P") && args[ii].indexOf(61) != -1) {
                final int equal = args[ii].indexOf(61);
                final String key = args[ii].substring(2, equal);
                final String value = args[ii].substring(equal + 1);
                ((Hashtable<String, String>)properties).put(key, value);
            }
            else if (args[ii].equals("-p")) {
                if (++ii >= args.length) {
                    Log.error("Missing file name for -p");
                    break;
                }
                properties = readPropertyFile(args[ii], properties);
            }
        }
        return properties;
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
}
