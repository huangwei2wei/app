// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import atavism.server.util.Log;

public class PropertyFileReader
{
    public static String propFile;
    public static boolean usePropFile;
    
    public PropertyFileReader() {
        try {
            if (PropertyFileReader.propFile == null) {
                PropertyFileReader.propFile = System.getProperty("atavism.propertyfile");
            }
            if (PropertyFileReader.propFile == null) {
                Log.debug("No property file specified.  Will use command-line properties.");
                PropertyFileReader.usePropFile = false;
            }
            else {
                final File f = new File(PropertyFileReader.propFile);
                if (f.exists()) {
                    PropertyFileReader.usePropFile = true;
                }
                else {
                    if (Log.loggingDebug) {
                        Log.debug("Specified property file " + PropertyFileReader.propFile + " does not exist! Defaulting to command-line properties.");
                    }
                    PropertyFileReader.usePropFile = false;
                }
            }
        }
        catch (Exception e) {
            Log.exception("PropertyFileReader caught exception finding Properties file", e);
        }
    }
    
    public Properties readPropFile() {
        final File f = new File(PropertyFileReader.propFile);
        final Properties properties = new Properties(System.getProperties());
        if (f.exists()) {
            try {
                properties.load(new FileInputStream(PropertyFileReader.propFile));
            }
            catch (IOException e) {
                Log.exception("PropertyFileReader.readPropFile caught exception finding Properties file", e);
            }
        }
        else {
            Log.error("Properties file " + PropertyFileReader.propFile + " does not exist.");
        }
        return properties;
    }
    
    static {
        PropertyFileReader.propFile = null;
        PropertyFileReader.usePropFile = false;
    }
}
