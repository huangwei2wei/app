// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

public class FileUtil
{
    public static String expandFileName(String fileName) {
        fileName = fileName.replace("$AO_HOME", System.getenv("AO_HOME"));
        fileName = fileName.replace("$WORLD_NAME", System.getProperty("atavism.worldname"));
        fileName = fileName.replace("$WORLD_DIR", System.getenv("AO_WORLD_CONFIG"));
        fileName = fileName.replace("$AO_LOGS", System.getProperty("atavism.logs"));
        return fileName;
    }
}
