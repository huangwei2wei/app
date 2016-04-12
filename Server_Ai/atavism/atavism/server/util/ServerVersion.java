// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.lang.reflect.Field;

public class ServerVersion
{
    public static final String ServerMajorVersion = "2.5.0";
    public static final int VERSION_LESSER = -1;
    public static final int VERSION_EQUAL = 0;
    public static final int VERSION_GREATER = 1;
    public static final int VERSION_FORMAT_ERROR = -9;
    private static String buildString;
    private static String buildDate;
    private static String buildNumber;
    
    public static String getVersionString() {
        return "2.5.0 " + getBuildNumber() + " (" + getBuildString() + " " + getBuildDate() + ")";
    }
    
    public static String getBuildString() {
        if (ServerVersion.buildString != null) {
            return ServerVersion.buildString;
        }
        return getFieldValue("buildString", "-");
    }
    
    public static String getBuildDate() {
        if (ServerVersion.buildDate != null) {
            return ServerVersion.buildDate;
        }
        return getFieldValue("buildDate", "-");
    }
    
    public static String getBuildNumber() {
        if (ServerVersion.buildNumber != null) {
            return ServerVersion.buildNumber;
        }
        return getFieldValue("buildNumber", "0");
    }
    
    public static int compareVersionStrings(final String leftVersion, final String rightVersion) {
        final float left = extractVersion(leftVersion);
        final float right = extractVersion(rightVersion);
        if (left == 0.0 || right == 0.0) {
            return -9;
        }
        if (left == right) {
            return 0;
        }
        if (left < right) {
            return -1;
        }
        if (left > right) {
            return 1;
        }
        return -9;
    }
    
    public static float extractVersion(final String versionString) {
        int ii;
        for (ii = 0; ii < versionString.length(); ++ii) {
            final char c = versionString.charAt(ii);
            if (!Character.isDigit(c)) {
                break;
            }
        }
        if (ii == 0) {
            return 0.0f;
        }
        if (ii == versionString.length()) {
            return 0.0f;
        }
        if (versionString.charAt(ii) != '.') {
            return 0.0f;
        }
        ++ii;
        while (ii < versionString.length()) {
            final char c = versionString.charAt(ii);
            if (!Character.isDigit(c)) {
                break;
            }
            ++ii;
        }
        final String versionNumber = versionString.substring(0, ii);
        final float num = Float.parseFloat(versionNumber);
        return num;
    }
    
    private static String getFieldValue(final String fieldName, final String defaultValue) {
        try {
            final Class buildInfo = Class.forName("atavism.server.util.BuildInfo");
            final Field stringField = buildInfo.getField(fieldName);
            return (String)stringField.get(null);
        }
        catch (IllegalAccessException ex) {}
        catch (NoSuchFieldException ex2) {}
        catch (ClassNotFoundException ex3) {}
        return defaultValue;
    }
    
    static {
        ServerVersion.buildString = null;
        ServerVersion.buildDate = null;
        ServerVersion.buildNumber = null;
    }
}
