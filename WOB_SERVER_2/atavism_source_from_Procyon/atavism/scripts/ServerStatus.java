// 
// Decompiled by Procyon v0.5.30
// 

package atavism.scripts;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class ServerStatus
{
    public static String RUNNING;
    public static String NOT_RUNNING;
    public static String PATH;
    public static String STATUSFILE;
    
    public static void main(final String[] args) {
        boolean running = false;
        if (args.length < 2) {
            System.err.println("Error - specify server name and world name on cmd line.");
            System.exit(1);
        }
        else {
            final String worldName = args[0];
            final String serverName = getServerName(args[1]);
            final String sFile = ServerStatus.PATH + File.separator + worldName + File.separator + ServerStatus.STATUSFILE;
            final File f = new File(sFile);
            if (!f.exists()) {
                System.err.println("Error - Staus file " + ServerStatus.STATUSFILE + " does not exist");
                System.exit(1);
            }
            try {
                final BufferedReader inputStream = new BufferedReader(new FileReader(sFile));
                String line;
                while ((line = inputStream.readLine()) != null) {
                    if (line.length() > 0 && line.contains("java.exe")) {
                        running = true;
                    }
                }
            }
            catch (Exception e) {
                System.err.println("File input error");
            }
            if (running) {
                System.out.println(serverName + ": running");
            }
            else {
                System.out.println(serverName + ": not running");
            }
        }
    }
    
    public static String getServerName(final String sName) {
        String s = "default";
        if (sName.equals("anim")) {
            s = "Animation server";
        }
        else if (sName.equals("combat")) {
            s = "Combat server";
        }
        else if (sName.equals("domain")) {
            s = "Message domain server";
        }
        else if (sName.equals("objmgr")) {
            s = "Object server";
        }
        else if (sName.equals("wmgr_1")) {
            s = "World manager";
        }
        else if (sName.equals("login_manager")) {
            s = "Login server";
        }
        else if (sName.equals("mobserver")) {
            s = "Mob server";
        }
        else if (sName.equals("proxy_1")) {
            s = "Proxy server";
        }
        else if (sName.equals("startup")) {
            s = "Startup monitor";
        }
        else if (sName.equals("instance")) {
            s = "Instance server";
        }
        else if (sName.equals("voiceserver")) {
            s = "Voice server";
        }
        return s;
    }
    
    static {
        ServerStatus.RUNNING = "java.exe";
        ServerStatus.NOT_RUNNING = "No tasks";
        ServerStatus.PATH = "run";
        ServerStatus.STATUSFILE = "status.txt";
    }
}
