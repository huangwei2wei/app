// 
// Decompiled by Procyon v0.5.30
// 

package atavism.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;

public class PropertyGetter
{
    static String propFile;
    static Properties properties;
    static String propName;
    static String win_env_var;
    
    public static void main(final String[] args) {
        if (PropertyGetter.propFile == null) {
            System.err.println("ERROR: Property file must be specified with -D.");
            System.exit(1);
        }
        if (args.length < 1) {
            System.err.println("ERROR: Specify property name!");
            System.exit(1);
        }
        PropertyGetter.propName = args[0];
        String defaultValue = null;
        if (args.length > 1) {
            defaultValue = args[1];
        }
        final File f = new File(PropertyGetter.propFile);
        if (f.exists()) {
            try {
                PropertyGetter.properties.load(new FileInputStream(PropertyGetter.propFile));
            }
            catch (IOException e) {
                System.out.println("Error finding Properties file - " + f.getAbsoluteFile());
            }
            final String propValue = PropertyGetter.properties.getProperty(PropertyGetter.propName, defaultValue);
            if (PropertyGetter.win_env_var == null) {
                System.out.print(propValue);
            }
            else {
                System.out.println("set " + PropertyGetter.win_env_var + "=" + propValue);
            }
        }
        else {
            System.out.println("Properties file " + PropertyGetter.propFile + " does not exist.");
        }
    }
    
    public String getWorldName() {
        return PropertyGetter.properties.getProperty("atavism.worldname");
    }
    
    public String getWorldFileName() {
        return PropertyGetter.properties.getProperty("atavism.aowfile");
    }
    
    static {
        PropertyGetter.propFile = System.getProperty("atavism.propertyfile");
        PropertyGetter.properties = new Properties();
        PropertyGetter.win_env_var = System.getProperty("win_env_var");
    }
}
