// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.RollingFileAppender;
import java.util.Enumeration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import java.io.IOException;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Log
{
    private static int logLevel;
    static Logger logger;
    public static boolean loggingWarn;
    public static boolean loggingInfo;
    public static boolean loggingDebug;
    public static boolean loggingNet;
    public static boolean loggingTrace;
    
    public static void init() {
        if (Log.logger == null) {
            initLogging();
        }
    }
    
    public static void init(final Properties properties) {
        if (Log.logger != null) {
            Log.logger.removeAppender("AODefaultConsoleAppender");
        }
        PropertyConfigurator.configure(properties);
        if (Log.logger == null) {
            initLogging();
        }
        else {
            syncWithOtherLevel();
            makeFailsafeAppender(true);
        }
        final boolean rotate = Boolean.parseBoolean(properties.getProperty("atavism.rotate_logs_on_startup", "false"));
        if (rotate) {
            rotateLogs();
        }
    }
    
    private Log() {
        init();
    }
    
    public static void net(final String s) {
        Log.logger.trace((Object)s);
    }
    
    public static void trace(final String s) {
        Log.logger.trace((Object)s);
    }
    
    public static void debug(final String s) {
        Log.logger.debug((Object)s);
    }
    
    public static void info(final String s) {
        Log.logger.info((Object)s);
    }
    
    public static void warn(final String s) {
        Log.logger.warn((Object)s);
    }
    
    public static void error(final String s) {
        Log.logger.error((Object)s);
    }
    
    public static void logAtLevel(final int level, final String s) {
        if (Log.logLevel <= level) {
            switch (level) {
                case 0: {
                    trace(s);
                    break;
                }
                case 1: {
                    debug(s);
                    break;
                }
                case 2: {
                    info(s);
                    break;
                }
                case 3: {
                    warn(s);
                    break;
                }
                case 4: {
                    error(s);
                    break;
                }
            }
        }
    }
    
    public static void dumpStack() {
        dumpStack("");
    }
    
    public static void dumpStack(final String context) {
        Log.logger.error((Object)buildStackDump(context, Thread.currentThread(), 5).toString());
    }
    
    public static void dumpStack(final String context, final Thread thread) {
        Log.logger.error((Object)buildStackDump(context, thread, 5).toString());
    }
    
    public static void warnAndDumpStack(final String context) {
        Log.logger.warn((Object)buildStackDump(context, Thread.currentThread(), 5).toString());
    }
    
    public static void warnAndDumpStack(final String context, final Thread thread) {
        Log.logger.warn((Object)buildStackDump(context, thread, 5).toString());
    }
    
    protected static StringBuilder buildStackDump(final String context, final Thread thread, final int framesToSkip) {
        final StringBuilder traceStr = new StringBuilder(1000);
        traceStr.append(((context == null || context.length() == 0) ? "Dumping stack for thread " : (context + ", dumping stack for thread ")) + thread.getName());
        int cnt = 0;
        for (final StackTraceElement elem : thread.getStackTrace()) {
            if (++cnt >= framesToSkip) {
                traceStr.append("\n       at ");
                traceStr.append(elem.toString());
            }
        }
        return traceStr;
    }
    
    public static String exceptionToString(final Exception e) {
        Throwable throwable = e;
        final StringBuilder traceStr = new StringBuilder(1000);
        do {
            traceStr.append(throwable.toString());
            for (final StackTraceElement elem : throwable.getStackTrace()) {
                traceStr.append("\n       at ");
                traceStr.append(elem.toString());
            }
            throwable = throwable.getCause();
            if (throwable != null) {
                traceStr.append("\nCaused by: ");
            }
        } while (throwable != null);
        return traceStr.toString();
    }
    
    public static void exception(final String context, final Exception e) {
        Log.logger.error((Object)(((context == null || context.length() == 0) ? "Exception: " : (context + " ")) + exceptionToString(e)));
    }
    
    public static void exception(final Exception e) {
        Log.logger.error((Object)("Exception: " + e + exceptionToString(e)));
    }
    
    public static void addFile(final String fileName) throws IOException {
        final FileAppender appender = new FileAppender((Layout)new PatternLayout("%-5p [%d{ISO8601}] %-10t %m%n"), fileName);
        appender.setName(fileName);
        Log.logger.addAppender((Appender)appender);
    }
    
    public static void removeFile(final String fileName) {
        Log.logger.removeAppender(fileName);
    }
    
    private static void initLogging() {
        final String disableLogs = System.getProperty("atavism.disable_logs", null);
        if (disableLogs != null) {
            (Log.logger = Logger.getRootLogger()).addAppender((Appender)new NullAppender());
        }
        else {
            final String loggerName = System.getProperty("atavism.loggername", "AO");
            Log.logger = Logger.getLogger(loggerName);
            syncWithOtherLevel();
            makeFailsafeAppender(false);
        }
    }
    
    private static void syncWithOtherLevel() {
        final Level log4jLevel = Log.logger.getEffectiveLevel();
        if (log4jLevel == Level.TRACE) {
            Log.logLevel = 0;
        }
        else if (log4jLevel == Level.DEBUG) {
            Log.logLevel = 1;
        }
        else if (log4jLevel == Level.INFO) {
            Log.logLevel = 2;
        }
        else if (log4jLevel == Level.WARN) {
            Log.logLevel = 3;
        }
        else if (log4jLevel == Level.ERROR) {
            Log.logLevel = 4;
        }
        setLogLevel(Log.logLevel);
    }
    
    private static void makeFailsafeAppender(final boolean complain) {
        final Logger rootLogger = Logger.getRootLogger();
        final Enumeration rootAppenders = rootLogger.getAllAppenders();
        final Enumeration ourAppenders = Log.logger.getAllAppenders();
        final boolean rootEmpty = !rootAppenders.hasMoreElements();
        final boolean ourEmpty = !ourAppenders.hasMoreElements();
        if (rootEmpty && ourEmpty) {
            if (complain) {
                System.out.println("Missing log config file, logging to console");
            }
            final ConsoleAppender appender = new ConsoleAppender((Layout)new PatternLayout("%-5p [%d{ISO8601}] %-10t %m%n"), "System.err");
            appender.setName("AODefaultConsoleAppender");
            Log.logger.addAppender((Appender)appender);
        }
    }
    
    private static void rotateLogs() {
        final Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            final Appender a = appenders.nextElement();
            if (a instanceof RollingFileAppender && !a.getName().equals("ErrorLog")) {
                ((RollingFileAppender)a).rollOver();
            }
        }
    }
    
    public static void setLogLevel(final int level) {
        if (level != Log.logLevel) {
            if (level == 0) {
                Log.logger.setLevel(Level.TRACE);
            }
            else if (level == 1) {
                Log.logger.setLevel(Level.DEBUG);
            }
            else if (level == 2) {
                Log.logger.setLevel(Level.INFO);
            }
            else if (level == 3) {
                Log.logger.setLevel(Level.WARN);
            }
            else {
                if (level != 4) {
                    return;
                }
                Log.logger.setLevel(Level.ERROR);
            }
        }
        Log.logLevel = level;
        Log.loggingWarn = (Log.logLevel <= 3);
        Log.loggingInfo = (Log.logLevel <= 2);
        Log.loggingDebug = (Log.logLevel <= 1);
        Log.loggingNet = (Log.logLevel <= 0);
        Log.loggingTrace = Log.loggingNet;
    }
    
    public static int getLogLevel() {
        return Log.logLevel;
    }
    
    static {
        Log.logLevel = 1;
        Log.loggingWarn = false;
        Log.loggingInfo = false;
        Log.loggingDebug = false;
        Log.loggingNet = false;
        Log.loggingTrace = false;
    }
    
    private static class NullAppender extends AppenderSkeleton
    {
        protected void append(final LoggingEvent event) {
        }
        
        public void close() {
        }
        
        public boolean requiresLayout() {
            return false;
        }
    }
}
