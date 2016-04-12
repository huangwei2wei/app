// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import org.python.core.PyException;
import atavism.server.objects.EntityManager;
import atavism.server.util.ServerVersion;
import java.text.DecimalFormat;
import atavism.server.util.LockFactory;
import javax.management.JMException;
import javax.management.ObjectName;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.concurrent.Executors;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.Message;
import java.util.HashMap;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import atavism.msgsys.MessageCatalog;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import atavism.server.objects.World;
import atavism.msgsys.MessageType;
import atavism.server.util.AORuntimeException;
import atavism.server.util.InitLogAndPid;
import java.util.concurrent.ThreadFactory;
import atavism.server.util.NamedThreadFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.LinkedList;
import java.lang.management.RuntimeMXBean;
import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.lang.management.ManagementFactory;
import atavism.server.util.Log;
import javax.management.MBeanServer;
import atavism.msgsys.MessageAgent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.Map;
import java.util.Properties;
import atavism.server.util.Logger;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.Set;
import java.util.concurrent.Executor;

public class Engine
{
    private static ThreadLocal<EnginePlugin> currentPlugin;
    private static Executor defaultMessageExecutor;
    private static boolean runCPUTimeThread;
    private static int cpuTimeSamplingInterval;
    private static Object mxbean;
    private static Thread cpuTimeThread;
    private static Thread statusReportingThread;
    private static Set<EnginePlugin> statusReportingPlugins;
    private static int statusReportingInterval;
    private static Lock statusReportingLock;
    protected static Method cpuMethod;
    protected static int processorCount;
    static final Logger log;
    public static Properties properties;
    public static int ExecutorThreadPoolSize;
    private static ScriptManager scriptManager;
    public static int MAX_NETWORK_BUF_SIZE;
    public static String dumpStacksAndExitIfFileExists;
    private static Integer defaultWorldmgrport;
    private static String defaultMsgSvrHostname;
    private static String engineHostName;
    private static Integer defaultMsgSvrPort;
    private static Engine instance;
    private static final int DEFAULT_PERSISTENT_OBJ_SAVE_INTERVAL = 600000;
    public long PersistentObjectSaveIntervalMS;
    private static Lock pluginMapLock;
    private static Map<String, EnginePlugin> pluginMap;
    private static Lock oidManagerLock;
    private static OIDManager oidManager;
    private static EventServer eventServer;
    private static ScheduledThreadPoolExecutor executor;
    private static Interpolator interpolator;
    static PersistenceManager persistenceMgr;
    private static Database db;
    private static MessageAgent agent;
    private static MBeanServer mbeanServer;
    
    public Engine() {
        this.PersistentObjectSaveIntervalMS = 600000L;
        Engine.engineHostName = determineHostName();
        Engine.log.info("My local host name is '" + Engine.engineHostName + "'");
        if (isManagementEnabled()) {
            Log.debug("Enabling JMX management agent");
            this.createManagementAgent();
        }
        Engine.mxbean = ManagementFactory.getOperatingSystemMXBean();
        Engine.cpuMethod = getCPUMethod();
        Engine.processorCount = Runtime.getRuntime().availableProcessors();
        String cpuIntervalString = Engine.properties.getProperty("atavism.cputime_logging_interval");
        int cpuInterval = 0;
        int logLevel = 1;
        if (cpuIntervalString != null) {
            final int p = cpuIntervalString.indexOf(44);
            if (p > 0) {
                final String logLevelString = cpuIntervalString.substring(p + 1);
                cpuIntervalString = cpuIntervalString.substring(0, p);
                try {
                    final int maybeLogLevel = Integer.parseInt(logLevelString.trim());
                    if (maybeLogLevel >= 0 && maybeLogLevel <= 4) {
                        logLevel = maybeLogLevel;
                    }
                }
                catch (Exception ex) {}
            }
            try {
                cpuInterval = Integer.parseInt(cpuIntervalString.trim());
            }
            catch (Exception ex2) {}
        }
        if (cpuInterval > 0) {
            Engine.runCPUTimeThread = true;
            Engine.cpuTimeSamplingInterval = cpuInterval;
            Engine.log.debug("atavism.cputime_logging_interval set to " + cpuInterval + " ms");
        }
        else {
            Engine.runCPUTimeThread = false;
            Engine.cpuTimeSamplingInterval = cpuInterval;
            Engine.log.debug("atavism.cputime_logging_interval disabled");
        }
        if (Engine.runCPUTimeThread) {
            (Engine.cpuTimeThread = new Thread(new CPUTimeThread(logLevel), "CPUTime")).start();
        }
    }
    
    private static String determineHostName() {
        String hostName = System.getProperty("atavism.hostname");
        if (hostName == null) {
            hostName = reverseLocalHostLookup();
        }
        if (hostName == null) {
            Engine.log.warn("Could not determine host name from reverse lookup or atavism.hostname, using 'localhost'");
            hostName = "localhost";
        }
        return hostName;
    }
    
    private static String reverseLocalHostLookup() {
        InetAddress localMachine = null;
        try {
            localMachine = InetAddress.getLocalHost();
            return localMachine.getHostName();
        }
        catch (UnknownHostException e) {
            Engine.log.warn("Could not get host name from local IP address " + localMachine);
            return null;
        }
    }
    
    public static Engine getInstance() {
        return Engine.instance;
    }
    
    private static void saveProcessID(final String svrName, final String runDir) {
        final RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        final String pid = rt.getName();
        if (Log.loggingDebug) {
            Engine.log.info("PROCESS ID IS " + pid);
            Engine.log.info("server name is " + svrName);
        }
        try {
            if (runDir != null) {
                final File outFile = new File(runDir + "\\" + svrName + ".bat");
                final PrintWriter out = new PrintWriter(new FileWriter(outFile));
                out.println("set pid=" + pid.substring(0, pid.indexOf("@")));
                out.close();
            }
        }
        catch (IOException e) {
            Log.exception("Engine.saveProcessID caught exception", e);
        }
    }
    
    public static void dumpAllThreadStacks() {
        final StringBuilder traceStr = new StringBuilder(1000);
        dumpAllThreadStacks(traceStr, false);
        Log.error(traceStr.toString());
    }
    
    public static void dumpAllThreadStacks(final StringBuilder stringBuilder, final boolean sortThreads) {
        final Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        stringBuilder.append("Dumping the thread stack for every thread in the process");
        Collection<Thread> keySet = traces.keySet();
        if (sortThreads) {
            final LinkedList<Thread> threads = new LinkedList<Thread>();
            threads.addAll(keySet);
            Collections.sort(threads, new Comparator<Thread>() {
                @Override
                public int compare(final Thread t1, final Thread t2) {
                    return t1.getName().compareTo(t2.getName());
                }
            });
            keySet = threads;
        }
        for (final Thread thread : keySet) {
            stringBuilder.append("\n\nDumping stack for thread " + thread.getName());
            printStack(stringBuilder, traces.get(thread));
        }
    }
    
    public static void printStack(final StringBuilder stringBuilder, final StackTraceElement[] elements) {
        for (final StackTraceElement elem : elements) {
            stringBuilder.append("\n       at ");
            stringBuilder.append(elem.toString());
        }
    }
    
    public static EventServer getEventServer() {
        if (Engine.eventServer == null) {
            Log.warn("Engine.getEventServer: creating eventserver (was null)");
            Engine.eventServer = new EventServer();
        }
        return Engine.eventServer;
    }
    
    public static ScheduledThreadPoolExecutor getExecutor() {
        if (Engine.executor == null) {
            Engine.executor = new ScheduledThreadPoolExecutor(Engine.ExecutorThreadPoolSize, new NamedThreadFactory("Scheduled"));
        }
        return Engine.executor;
    }
    
    public static Interpolator<?> getInterpolator() {
        return (Interpolator<?>)Engine.interpolator;
    }
    
    public static void setInterpolator(final Interpolator<?> interpolator) {
        Engine.interpolator = interpolator;
    }
    
    public static void setBasicInterpolatorInterval(final Integer interval) {
        Engine.interpolator = new BasicInterpolator(interval);
    }
    
    public static ScriptManager getScriptManager() {
        return Engine.scriptManager;
    }
    
    public static Database getDatabase() {
        if (Engine.db == null) {
            Log.warn("Engine.getDatabase: returning null database object");
        }
        return Engine.db;
    }
    
    public static void setDatabase(final Database db) {
        Engine.db = db;
    }
    
    public static OIDManager getOIDManager() {
        Engine.oidManagerLock.lock();
        try {
            if (Engine.oidManager == null) {
                Engine.oidManager = new OIDManager();
            }
            return Engine.oidManager;
        }
        finally {
            Engine.oidManagerLock.unlock();
        }
    }
    
    public static void setOIDManager(final OIDManager o) {
        Engine.oidManagerLock.lock();
        try {
            if (Engine.oidManager != null) {
                throw new RuntimeException("Engine.setOIDManager: oid manager is not null");
            }
            Engine.oidManager = o;
        }
        finally {
            Engine.oidManagerLock.unlock();
        }
    }
    
    public static PersistenceManager getPersistenceManager() {
        return Engine.persistenceMgr;
    }
    
    public static void main(final String[] args) {
        if (args.length < 1) {
            System.err.println("java Engine [-i pre-script ...] [post-script ...]");
            System.exit(1);
        }
        final String worldName = System.getProperty("atavism.worldname");
        final String hostName = determineHostName();
        Engine.properties = InitLogAndPid.initLogAndPid(args, worldName, hostName);
        Engine.instance = new Engine();
        final String svrName = System.getProperty("atavism.loggername");
        final String agentType = System.getProperty("atavism.agenttype", svrName);
        final String runDir = System.getProperty("atavism.rundir");
        if (System.getProperty("os.name").contains("Windows") && svrName != null && runDir != null) {
            saveProcessID(svrName, runDir);
        }
        final List<String> postScripts = processPreScripts(args);
        try {
            Engine.db = new Database(getDBDriver());
            if (Log.loggingDebug) {
                Engine.log.debug("connecting to " + getDBHostname() + "user = " + getDBUser() + " passwd=" + getDBPassword());
            }
            Engine.db.connect(getDBUrl(), getDBUser(), getDBPassword());
        }
        catch (AORuntimeException e) {
            Log.exception("Engine.main: error connecting to the database", e);
            System.exit(1);
        }
        Log.info("connected to database");
        Namespace.encacheNamespaceMapping();
        Log.info("encached the mapping of namespace strings to ints");
        (Engine.agent = new MessageAgent(svrName)).setDefaultSubscriptionFlags(1);
        Engine.agent.setAdvertisementFileName(agentType + "-ads.txt");
        final List<MessageType> types = readAdvertisements(agentType);
        types.add(EnginePlugin.MSG_TYPE_PLUGIN_STATE);
        Engine.agent.addAdvertisements(types);
        Engine.agent.addNoProducersExpected(MessageType.intern("ao.SEARCH"));
        Engine.agent.addNoProducersExpected(MessageType.intern("ao.GET_PLUGIN_STATUS"));
        final String message_agent_stats = Engine.properties.getProperty("atavism.message_agent_stats");
        if (message_agent_stats != null && message_agent_stats.equals("true")) {
            Engine.agent.startStatsThread();
        }
        try {
            Engine.agent.openListener();
            Engine.agent.connectToDomain(getMessageServerHostname(), getMessageServerPort());
            Engine.agent.waitForRemoteAgents();
        }
        catch (Exception ex) {
            Log.exception("Engine.main: domain server " + getMessageServerHostname() + ":" + getMessageServerPort() + " failed", ex);
            System.exit(1);
        }
        Engine.executor = new ScheduledThreadPoolExecutor(Engine.ExecutorThreadPoolSize, new NamedThreadFactory("Scheduled"));
        if (World.getGeometry() == null) {
            Log.warn("engine: world geometry is not set");
        }
        setOIDManager(new OIDManager(Engine.db));
        processPostScripts(postScripts);
        try {
            while (true) {
                if (Engine.dumpStacksAndExitIfFileExists.length() > 0) {
                    final File dumpFile = new File(Engine.dumpStacksAndExitIfFileExists);
                    if (dumpFile.exists()) {
                        final StringBuilder sb = new StringBuilder();
                        dumpAllThreadStacks(sb, true);
                        Log.info(sb.toString());
                        System.exit(0);
                    }
                    Thread.sleep(1000L);
                }
                else {
                    Thread.sleep(10000L);
                }
            }
        }
        catch (Exception e2) {
            Log.exception("Engine.main: error in Thread.sleep", e2);
        }
    }
    
    private static List<MessageType> readAdvertisements(final String agentName) {
        final List<MessageType> result = new LinkedList<MessageType>();
        final String home = System.getenv("AO_HOME");
        final String worldConfigDir = System.getenv("AO_WORLD_CONFIG");
        final String commonFileName = home + "/config/common/" + agentName + "-ads.txt";
        final File commonFile = new File(commonFileName);
        final String worldFileName = worldConfigDir + "/" + agentName + "-ads.txt";
        final File worldFile = new File(worldFileName);
        if (!commonFile.exists() && !worldFile.exists()) {
            Log.warn("Missing advertisements file for agent " + agentName + " for world " + getWorldName() + " in either " + commonFileName + " or " + worldFileName);
            return result;
        }
        if (commonFile.exists()) {
            addAdvertisements(commonFileName, result);
        }
        if (worldFile.exists()) {
            addAdvertisements(worldFileName, result);
        }
        return result;
    }
    
    static void addAdvertisements(final String fileName, final List<MessageType> result) {
        try {
            final File file = new File(fileName);
            final BufferedReader in = new BufferedReader(new FileReader(file));
            String originalLine = null;
            int count = 0;
            while ((originalLine = in.readLine()) != null) {
                ++count;
                String line = originalLine.trim();
                final int pos = line.indexOf("#");
                if (pos >= 0) {
                    line = line.substring(0, pos).trim();
                }
                if (line.length() == 0) {
                    continue;
                }
                if (line.indexOf(" ") > 0 || line.indexOf(",") > 0) {
                    Log.error("File '" + fileName + "', line " + count + ": unexpected character");
                }
                else {
                    final MessageType type = MessageCatalog.getMessageType(line);
                    if (type == null) {
                        Log.error("File '" + fileName + "', line " + count + ": unknown message type " + line);
                    }
                    else {
                        if (result.contains(type)) {
                            continue;
                        }
                        result.add(type);
                    }
                }
            }
            in.close();
        }
        catch (IOException ex) {
            Log.exception(fileName, ex);
        }
    }
    
    public static List<String> processPreScripts(final String[] args) {
        final List<String> preScripts = new LinkedList<String>();
        final List<String> postScripts = new LinkedList<String>();
        populateScriptList(args, preScripts, postScripts);
        Engine.scriptManager = new ScriptManager();
        String scriptName = null;
        try {
            Engine.scriptManager.init();
            final Iterator i$ = preScripts.iterator();
            while (i$.hasNext()) {
                final String initScriptFile = scriptName = i$.next();
                if (Log.loggingDebug) {
                    Engine.log.debug("Engine: reading in script: " + initScriptFile);
                }
                final File f = new File(initScriptFile);
                if (f.exists()) {
                    if (Log.loggingDebug) {
                        Engine.log.debug("Executing init script file: " + initScriptFile);
                    }
                    Engine.scriptManager.runFile(initScriptFile);
                    Engine.log.debug("script completed");
                }
                else {
                    Log.warn("didnt find local script file, skipping: " + initScriptFile);
                }
            }
        }
        catch (Exception e) {
            Log.exception("Engine.processPreScripts: got exception running script '" + scriptName + "'", e);
            System.exit(1);
        }
        return postScripts;
    }
    
    public static void processPostScripts(final List<String> postScripts) {
        Engine.scriptManager = new ScriptManager();
        String scriptName = null;
        try {
            Engine.scriptManager.init();
            final Iterator i$ = postScripts.iterator();
            while (i$.hasNext()) {
                final String scriptFilename = scriptName = i$.next();
                if (Log.loggingDebug) {
                    Engine.log.debug("Executing script file: " + scriptFilename);
                }
                Engine.scriptManager.runFile(scriptFilename);
                Engine.log.debug("script completed");
            }
        }
        catch (Exception e) {
            Log.exception("Engine.processPostScripts: got exception running script '" + scriptName + "'", e);
            System.exit(1);
        }
    }
    
    static void populateScriptList(final String[] args, final List<String> preScripts, final List<String> postScripts) {
        final LongOpt[] longopts = { new LongOpt("pid", 1, (StringBuffer)null, 2) };
        final Getopt g = new Getopt("Engine", args, "i:w:m:t:rgP:", longopts);
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 105: {
                    final String arg = g.getOptarg();
                    if (Log.loggingDebug) {
                        Engine.log.debug("populateScriptList: option i: " + arg);
                    }
                    preScripts.add(arg);
                    continue;
                }
                case 63: {
                    continue;
                }
                case 2:
                case 109:
                case 116: {
                    final String arg = g.getOptarg();
                    continue;
                }
                case 80:
                case 103:
                case 114: {
                    continue;
                }
                default: {
                    System.out.print("getopt() returned " + c + "\n");
                    continue;
                }
            }
        }
        for (int i = g.getOptind(); i < args.length; ++i) {
            if (Log.loggingDebug) {
                Engine.log.debug("populateScriptList: nonoption args element: " + args[i]);
            }
            postScripts.add(args[i]);
        }
    }
    
    public long getPersistentObjectSaveIntervalMS() {
        return this.PersistentObjectSaveIntervalMS;
    }
    
    public void setPersistentObjectSaveIntervalMS(final long interval) {
        this.PersistentObjectSaveIntervalMS = interval;
    }
    
    public static EnginePlugin registerPlugin(final String className) {
        try {
            if (Log.loggingDebug) {
                Engine.log.debug("Engine.registerPlugin: loading class " + className);
            }
            final Class enginePluginClass = Class.forName(className);
            final EnginePlugin enginePlugin = enginePluginClass.newInstance();
            registerPlugin(enginePlugin);
            return enginePlugin;
        }
        catch (Exception e) {
            Engine.log.error("EnginePlugin.registerPugin failed: stack trace follows");
            e.printStackTrace();
            throw new RuntimeException("could not load and/or activate class: " + e, e);
        }
    }
    
    public static void registerPlugin(final EnginePlugin plugin) {
        if (Log.loggingDebug) {
            Engine.log.debug("Engine.registerPlugin: registering " + plugin.getName());
        }
        setCurrentPlugin(plugin);
        plugin.activate();
        setCurrentPlugin(null);
        Engine.pluginMapLock.lock();
        try {
            Engine.pluginMap.put(plugin.getName(), plugin);
        }
        finally {
            Engine.pluginMapLock.unlock();
        }
    }
    
    public static EnginePlugin getPlugin(final String name) {
        Engine.pluginMapLock.lock();
        try {
            return Engine.pluginMap.get(name);
        }
        finally {
            Engine.pluginMapLock.unlock();
        }
    }
    
    public static EnginePlugin getCurrentPlugin() {
        return Engine.currentPlugin.get();
    }
    
    public static void setCurrentPlugin(final EnginePlugin plugin) {
        Engine.currentPlugin.set(plugin);
    }
    
    public static String getEngineHostName() {
        return Engine.engineHostName;
    }
    
    public static String getDBDriver() {
        String driver = Engine.properties.getProperty("atavism.db_driver");
        if (driver == null) {
            driver = "com.mysql.jdbc.Driver";
        }
        return driver;
    }
    
    public static void setDBDriver(final String driver) {
        Engine.properties.setProperty("atavism.db_driver", driver);
    }
    
    public static String getDBType() {
        final String dbtype = Engine.properties.getProperty("atavism.db_type");
        if (dbtype == null) {
            return "mysql";
        }
        return dbtype;
    }
    
    public static void setDBType(final String dbtype) {
        Engine.properties.setProperty("atavism.db_type", dbtype);
    }
    
    public static void setDBUrl(final String url) {
        Engine.properties.setProperty("atavism.db_url", url);
    }
    
    public static String getDBUrl() {
        String url = Engine.properties.getProperty("atavism.db_url");
        if (url == null) {
            url = "jdbc:" + getDBType() + "://" + getDBHostname() + "/" + getDBName();
        }
        return url;
    }
    
    public static String getDBUser() {
        return Engine.properties.getProperty("atavism.db_user");
    }
    
    public static void setDBUser(final String username) {
        Engine.properties.setProperty("atavism.db_user", username);
    }
    
    public static String getDBPassword() {
        return Engine.properties.getProperty("atavism.db_password");
    }
    
    public static void setDBPassword(final String password) {
        Engine.properties.setProperty("atavism.db_password", password);
    }
    
    public static String getDBHostname() {
        return Engine.properties.getProperty("atavism.db_hostname");
    }
    
    public static void setDBHostname(final String hostname) {
        Engine.properties.setProperty("atavism.db_hostname", hostname);
    }
    
    public static String getDBName() {
        final String dbname = Engine.properties.getProperty("atavism.db_name");
        if (dbname == null) {
            return "atavism";
        }
        return dbname;
    }
    
    public static void setDBName(final String name) {
        Engine.properties.setProperty("atavism.db_name", name);
    }
    
    public static String getMessageServerHostname() {
        String msgSvrHostname = Engine.defaultMsgSvrHostname;
        msgSvrHostname = Engine.properties.getProperty("atavism.msgsvr_hostname");
        if (msgSvrHostname == null) {
            msgSvrHostname = Engine.defaultMsgSvrHostname;
        }
        return msgSvrHostname;
    }
    
    public static void setMessageServerHostname(final String host) {
        Engine.properties.setProperty("atavism.msgsvr_port", host);
    }
    
    public static Integer getMessageServerPort() {
        final String sMsgSvrPort = Engine.properties.getProperty("atavism.msgsvr_port");
        int msgSvrPort;
        if (sMsgSvrPort == null) {
            msgSvrPort = Engine.defaultMsgSvrPort;
        }
        else {
            msgSvrPort = Integer.parseInt(sMsgSvrPort.trim());
        }
        return msgSvrPort;
    }
    
    public static void setMessageServerPort(final Integer port) {
        Engine.properties.setProperty("atavism.msgsvr_port", Integer.toString(port));
    }
    
    public static void setWorldMgrPort(final Integer port) {
        Engine.properties.setProperty("atavism.worldmgrport", Integer.toString(port));
    }
    
    public static Integer getWorldMgrPort() {
        final String sWorldMgrPort = Engine.properties.getProperty("atavism.worldmgrport");
        int port;
        if (sWorldMgrPort == null) {
            port = Engine.defaultWorldmgrport;
        }
        else {
            port = Integer.parseInt(sWorldMgrPort.trim());
        }
        return port;
    }
    
    public static int getStatusReportingInterval() {
        return Engine.statusReportingInterval;
    }
    
    public static void setStatusReportingInterval(final int statusReportingInterval) {
        Engine.statusReportingInterval = statusReportingInterval;
    }
    
    public static Properties getProperties() {
        return Engine.properties;
    }
    
    public static String getProperty(final String propName) {
        return Engine.properties.getProperty(propName);
    }
    
    public static void setProperty(final String propName, final String propValue) {
        Engine.properties.setProperty(propName, propValue);
    }
    
    public static Integer getIntProperty(final String propName) {
        final String intString = Engine.properties.getProperty(propName);
        if (intString == null) {
            return null;
        }
        try {
            return Integer.valueOf(intString.trim());
        }
        catch (NumberFormatException e) {
            Log.error("Property '" + propName + "' value '" + intString.trim() + "' is not an integer.");
            return null;
        }
    }
    
    public static String getWorldName() {
        return System.getProperty("atavism.worldname");
    }
    
    public static void setWorldName(final String worldName) {
        Engine.properties.setProperty("atavism.worldname", worldName);
    }
    
    public static String getLogLevel() {
        String logLevelString = getProperty("atavism.log_level");
        if (logLevelString == null) {
            logLevelString = "1";
        }
        return logLevelString;
    }
    
    public static void setLogLevel(final String level) {
        Engine.properties.setProperty("atavism.log_level", level);
    }
    
    public static boolean isManagementEnabled() {
        final String mgmt = Engine.properties.getProperty("com.sun.management.jmxremote");
        return mgmt != null;
    }
    
    public static MessageAgent getAgent() {
        return Engine.agent;
    }
    
    public static Map<String, String> makeMapOfString(final String str) {
        final Map<String, String> propMap = new HashMap<String, String>();
        final String[] arr$;
        final String[] keysAndValues = arr$ = str.split(",");
        for (final String keyAndValue : arr$) {
            final String[] ss = keyAndValue.split("=");
            if (ss.length == 2) {
                propMap.put(ss[0], ss[1]);
            }
            else {
                Log.error("Engine.makeMapOfString: Could not parse name/value string '" + str + "' at '" + keyAndValue + "'");
            }
        }
        return propMap;
    }
    
    public static String makeStringFromMap(final Map<String, String> propMap) {
        String s = "";
        if (propMap == null) {
            return s;
        }
        for (final Map.Entry<String, String> pair : propMap.entrySet()) {
            if (s != "") {
                s += ",";
            }
            s = s + pair.getKey() + "=" + pair.getValue();
        }
        return s;
    }
    
    public static void defaultDispatchMessage(final Message message, final int flags, final MessageCallback callback) {
        if (Log.loggingDebug) {
            Log.debug("defaultDispatchMessage " + message.getSenderName() + "," + message.getMsgId() + " " + message.getMsgType());
        }
        if (Engine.defaultMessageExecutor == null) {
            Engine.defaultMessageExecutor = Executors.newFixedThreadPool(10, new NamedThreadFactory("EngineDispatch"));
        }
        Engine.defaultMessageExecutor.execute(new QueuedMessage(message, flags, callback));
    }
    
    public static void registerStatusReportingPlugin(final EnginePlugin plugin) {
        Engine.statusReportingLock.lock();
        try {
            if (Log.loggingDebug) {
                Log.debug("Engine.registerStatusReportingPlugin: Registering plugin " + plugin.getName() + " of type " + plugin.getPluginType());
            }
            if (Engine.statusReportingPlugins == null) {
                Engine.statusReportingPlugins = new HashSet<EnginePlugin>();
                if (Engine.mxbean == null) {
                    Engine.mxbean = ManagementFactory.getOperatingSystemMXBean();
                }
                (Engine.statusReportingThread = new Thread(new StatusReportingThread(), "StatusReporting")).start();
            }
            if (!getDatabase().registerStatusReportingPlugin(plugin, getAgent().getDomainStartTime())) {
                Engine.log.error("Engine.registerStatusReportingPlugin: Registration of plugin '" + plugin.getName() + "' failed!");
            }
            else {
                Engine.statusReportingPlugins.add(plugin);
            }
        }
        finally {
            Engine.statusReportingLock.unlock();
        }
    }
    
    protected static Method getCPUMethod() {
        try {
            Class operatingSystemMXBean = null;
            operatingSystemMXBean = getParentInterface(Engine.mxbean.getClass(), "com.sun.management.OperatingSystemMXBean");
            if (operatingSystemMXBean == null) {
                throw new ClassNotFoundException("OperatingSystemMXBean is not a super-class of the management bean");
            }
            return operatingSystemMXBean.getMethod("getProcessCpuTime", (Class[])new Class[0]);
        }
        catch (NoSuchMethodException ex) {
            Log.exception("CPU time will not be reported", ex);
        }
        catch (ClassNotFoundException ex2) {
            Log.exception("CPU time will not be reported", ex2);
        }
        return null;
    }
    
    protected static long getProcessCpuTime(final Method cpuMethod, final Object mxbean) {
        if (cpuMethod == null) {
            return 0L;
        }
        try {
            return (long)cpuMethod.invoke(mxbean, new Object[0]);
        }
        catch (IllegalAccessException ex) {
            Log.exception("Failed getting CPU time", ex);
        }
        catch (InvocationTargetException ex2) {
            Log.exception("Failed getting CPU time", ex2);
        }
        return 0L;
    }
    
    protected static Class getParentInterface(final Class cl, final String name) {
        if (cl.getName().equals(name)) {
            return cl;
        }
        final Class[] interfaces = cl.getInterfaces();
        for (int ii = 0; ii < interfaces.length; ++ii) {
            final Class match = getParentInterface(interfaces[ii], name);
            if (match != null) {
                return match;
            }
        }
        return null;
    }
    
    private void createManagementAgent() {
        Engine.mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            final ObjectName name = new ObjectName("net.atavism:type=Engine");
            Engine.mbeanServer.registerMBean(this.createMBeanInstance(), name);
            Log.debug("Registered Engine with JMX management agent");
        }
        catch (JMException ex) {
            Log.exception("Engine.createManagementAgent: exception in registerMBean", ex);
        }
    }
    
    protected Object createMBeanInstance() {
        return new EngineJMX();
    }
    
    public static MBeanServer getManagementAgent() {
        return Engine.mbeanServer;
    }
    
    static {
        Engine.currentPlugin = new ThreadLocal<EnginePlugin>();
        Engine.runCPUTimeThread = false;
        Engine.cpuTimeSamplingInterval = 250;
        Engine.mxbean = null;
        Engine.cpuTimeThread = null;
        Engine.statusReportingThread = null;
        Engine.statusReportingPlugins = null;
        Engine.statusReportingInterval = 5000;
        Engine.statusReportingLock = LockFactory.makeLock("statusReportingLock");
        log = new Logger("Engine");
        Engine.properties = new Properties();
        Engine.ExecutorThreadPoolSize = 10;
        Engine.scriptManager = null;
        Engine.MAX_NETWORK_BUF_SIZE = 1000;
        Engine.dumpStacksAndExitIfFileExists = "";
        Engine.defaultWorldmgrport = 5040;
        Engine.defaultMsgSvrHostname = "localhost";
        Engine.engineHostName = "localhost";
        Engine.defaultMsgSvrPort = 20374;
        Engine.instance = null;
        Engine.pluginMapLock = LockFactory.makeLock("pluginMapLock");
        Engine.pluginMap = new HashMap<String, EnginePlugin>();
        Engine.oidManagerLock = LockFactory.makeLock("oidManagerLock");
        Engine.oidManager = null;
        Engine.eventServer = null;
        Engine.executor = null;
        Engine.interpolator = null;
        Engine.persistenceMgr = new PersistenceManager();
        Engine.db = null;
        Engine.agent = null;
        Engine.mbeanServer = null;
    }
    
    static class QueuedMessage implements Runnable
    {
        Message message;
        int flags;
        MessageCallback callback;
        
        QueuedMessage(final Message message, final int flags, final MessageCallback callback) {
            this.message = message;
            this.flags = flags;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            try {
                this.callback.handleMessage(this.message, this.flags);
            }
            catch (Exception ex) {
                Log.exception("Engine message handler: " + this.message.getMsgType(), ex);
            }
        }
    }
    
    static class CPUTimeThread implements Runnable
    {
        int logLevel;
        
        public CPUTimeThread(final int logLevel) {
            this.logLevel = logLevel;
        }
        
        @Override
        public void run() {
            float lastCPUTime = Engine.getProcessCpuTime(Engine.cpuMethod, Engine.mxbean) / 1000000L / 1000.0f;
            long lastTime = System.currentTimeMillis();
            final DecimalFormat timeFormatter = new DecimalFormat("####.000");
            while (true) {
                try {
                    Thread.sleep(Engine.cpuTimeSamplingInterval);
                }
                catch (Exception e) {
                    Engine.log.exception("CPUTimeThread.run exception", e);
                }
                final long currentTime = System.currentTimeMillis();
                final float currentCPUTime = Engine.getProcessCpuTime(Engine.cpuMethod, Engine.mxbean) / 1000000L / 1000.0f;
                final float diff = currentCPUTime - lastCPUTime;
                final long msDiff = (currentTime - lastTime) * Engine.processorCount;
                final float secsDiff = msDiff / 1000.0f;
                final int percentDiff = (int)(diff * 100.0f / secsDiff);
                if (Log.getLogLevel() <= this.logLevel) {
                    Log.logAtLevel(this.logLevel, "Process CPU time: " + timeFormatter.format(currentCPUTime) + ", CPU time since last " + timeFormatter.format(diff) + ", " + percentDiff + "% CPU");
                }
                lastTime = currentTime;
                lastCPUTime = currentCPUTime;
            }
        }
    }
    
    static class StatusReportingThread implements Runnable
    {
        float lastCPUTime;
        long lastTime;
        
        @Override
        public void run() {
            this.lastCPUTime = Engine.getProcessCpuTime(Engine.cpuMethod, Engine.mxbean) / 1000000L / 1000.0f;
            this.lastTime = System.currentTimeMillis();
            while (true) {
                try {
                    Thread.sleep(Engine.statusReportingInterval);
                }
                catch (InterruptedException e) {
                    Engine.log.exception("StatusReportingThread.run exception", e);
                }
                try {
                    this.updateStatus();
                }
                catch (Exception e2) {
                    Engine.log.exception("StatusReportingThread.run", e2);
                }
            }
        }
        
        private void updateStatus() {
            if (Log.loggingDebug) {
                Log.debug("Engine.StatusReportingThread.run: count of status reporting plugins is " + Engine.statusReportingPlugins.size());
            }
            final long currentTime = System.currentTimeMillis();
            final float currentCPUTime = Engine.getProcessCpuTime(Engine.cpuMethod, Engine.mxbean) / 1000000L / 1000.0f;
            final float diff = currentCPUTime - this.lastCPUTime;
            final long msDiff = (currentTime - this.lastTime) * Engine.processorCount;
            final float secsDiff = msDiff / 1000.0f;
            final int percentDiff = (int)(diff * 100.0f / secsDiff);
            if (Log.loggingDebug) {
                Log.debug("Engine.StatusReportingThread: " + Engine.statusReportingPlugins.size() + " plugins, " + percentDiff + "% CPU");
            }
            for (final EnginePlugin plugin : Engine.statusReportingPlugins) {
                plugin.setPercentCPULoad(percentDiff);
                Engine.getDatabase().updatePluginStatus(plugin, System.currentTimeMillis() + Engine.statusReportingInterval);
            }
            this.lastTime = currentTime;
            this.lastCPUTime = currentCPUTime;
        }
    }
    
    protected static class EngineJMX implements EngineJMXMBean
    {
        protected static String defaultPythonImports;
        protected static ScriptManager mbeanScriptManager;
        
        @Override
        public String getVersion() {
            return "2.5.0";
        }
        
        @Override
        public String getFullVersion() {
            return ServerVersion.getVersionString();
        }
        
        @Override
        public String getBuildNumber() {
            return ServerVersion.getBuildNumber();
        }
        
        @Override
        public String getBuildDate() {
            return ServerVersion.getBuildDate();
        }
        
        @Override
        public String getBuildString() {
            return ServerVersion.getBuildString();
        }
        
        @Override
        public String getAgentName() {
            return Engine.agent.getName();
        }
        
        @Override
        public String getWorldName() {
            return Engine.getWorldName();
        }
        
        @Override
        public String getPlugins() {
            String plugins = "";
            for (final String name : Engine.pluginMap.keySet()) {
                if (!plugins.equals("")) {
                    plugins += ",";
                }
                plugins += name;
            }
            return plugins;
        }
        
        @Override
        public int getLogLevel() {
            return Log.getLogLevel();
        }
        
        @Override
        public String getLogLevelString() {
            final int level = Log.getLogLevel();
            if (level == 0) {
                return "TRACE";
            }
            if (level == 1) {
                return "DEBUG";
            }
            if (level == 2) {
                return "INFO";
            }
            if (level == 3) {
                return "WARN";
            }
            if (level == 4) {
                return "ERROR";
            }
            return "unknown";
        }
        
        @Override
        public void setLogLevel(final int level) {
            Log.setLogLevel(level);
        }
        
        @Override
        public long getPersistentObjectSaveIntervalMS() {
            return Engine.getInstance().PersistentObjectSaveIntervalMS;
        }
        
        @Override
        public void setPersistentObjectSaveIntervalMS(final long interval) {
            Engine.getInstance().PersistentObjectSaveIntervalMS = interval;
        }
        
        @Override
        public boolean getCPUTimeMonitor() {
            return Engine.runCPUTimeThread;
        }
        
        @Override
        public int getCPUTimeMonitorIntervalMS() {
            return Engine.cpuTimeSamplingInterval;
        }
        
        @Override
        public void setCPUTimeMonitorIntervalMS(final int milliSeconds) {
            if (milliSeconds > 0) {
                Engine.cpuTimeSamplingInterval = milliSeconds;
            }
        }
        
        @Override
        public int getEntities() {
            return EntityManager.getEntityCount();
        }
        
        @Override
        public String runPythonScript(final String script) {
            initScriptManager();
            try {
                final ScriptManager.ScriptOutput output = EngineJMX.mbeanScriptManager.runPYScript(script);
                if (output.stderr == null || output.stderr.equals("")) {
                    return output.stdout;
                }
                return "OUT: " + output.stdout + "\nERR: " + output.stderr;
            }
            catch (PyException e) {
                return e.toString();
            }
        }
        
        @Override
        public String evalPythonScript(final String script) {
            initScriptManager();
            try {
                return EngineJMX.mbeanScriptManager.evalPYScriptAsString(script);
            }
            catch (PyException e) {
                return e.toString();
            }
        }
        
        protected static void initScriptManager() {
            if (EngineJMX.mbeanScriptManager != null) {
                return;
            }
            (EngineJMX.mbeanScriptManager = new ScriptManager()).initLocal();
            try {
                EngineJMX.mbeanScriptManager.runPYScript(EngineJMX.defaultPythonImports);
            }
            catch (PyException e) {
                Log.exception("EngineJMX.initScriptManager", (Exception)e);
            }
        }
        
        static {
            EngineJMX.defaultPythonImports = "import sys\nfrom atavism.agis import *\nfrom atavism.agis.objects import *\nfrom atavism.agis.core import *\nfrom atavism.agis.events import *\nfrom atavism.agis.util import *\nfrom atavism.agis.plugins import *\nfrom atavism.msgsys import *\nfrom atavism.server.plugins import *\nfrom atavism.server.math import *\nfrom atavism.server.events import *\nfrom atavism.server.objects import *\nfrom atavism.server.worldmgr import *\nfrom atavism.server.engine import *";
        }
    }
    
    public interface EngineJMXMBean
    {
        String getVersion();
        
        String getFullVersion();
        
        String getBuildNumber();
        
        String getBuildDate();
        
        String getBuildString();
        
        String getAgentName();
        
        String getWorldName();
        
        String getPlugins();
        
        int getLogLevel();
        
        String getLogLevelString();
        
        void setLogLevel(final int p0);
        
        long getPersistentObjectSaveIntervalMS();
        
        void setPersistentObjectSaveIntervalMS(final long p0);
        
        boolean getCPUTimeMonitor();
        
        int getCPUTimeMonitorIntervalMS();
        
        void setCPUTimeMonitorIntervalMS(final int p0);
        
        int getEntities();
        
        String runPythonScript(final String p0);
        
        String evalPythonScript(final String p0);
    }
}
