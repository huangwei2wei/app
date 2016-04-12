// 
// Decompiled by Procyon v0.5.30
// 

package atavism.scripts;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import atavism.server.engine.PropertyFileReader;
import java.util.Properties;

public class Launcher
{
    public static Properties properties;
    public String propFile;
    
    Launcher() {
        this.propFile = System.getProperty("atavism.propertyfile");
        System.out.println("Using property file " + this.propFile);
        final PropertyFileReader pfr = new PropertyFileReader();
        Launcher.properties = pfr.readPropFile();
    }
    
    public int exit() {
        System.runFinalization();
        ManagementFactory.getPlatformMBeanServer();
        try {
            final ObjectName name = new ObjectName("atavism.server.engine.Launcher:type=Launcher");
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(name);
            System.out.println("Unregistered Launcher with JMX mgmt agent");
            System.exit(0);
        }
        catch (Exception ex) {
            System.out.println("Message Server: caught exception: " + ex);
            ex.printStackTrace();
        }
        System.exit(0);
        return 0;
    }
    
    public void startAllServers() {
        final String servers = Launcher.properties.getProperty("atavism.servers");
        try {
            if (servers != null) {
                final String[] serverArray = servers.split(",");
                for (int i = 0; i < serverArray.length; ++i) {
                    if (serverArray[i] != null) {
                        System.out.println(">>>Starting server #" + i);
                        this.startServer(serverArray[i]);
                        Thread.sleep(5000L);
                    }
                    else {
                        System.out.println("ERROR - server " + i + " is null");
                    }
                }
            }
            else {
                System.out.println("server list is null!");
            }
        }
        catch (Exception ex) {
            System.out.println("Error starting all servers: caught exception: " + ex);
            ex.printStackTrace();
        }
    }
    
    public void printElements(final Vector<String> v) {
        System.out.println("ELEMENTS OF COMMAND VECTOR");
        final Iterator it = v.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
    
    public Process startServer(final String svrName) {
        final Vector<String> cmds = new Vector<String>();
        Process p = null;
        System.out.println("Starting " + svrName);
        cmds.addElement("java");
        cmds.addElement("-Datavism.propertyfile=" + this.propFile);
        cmds.addElement("-Dcom.sun.management.jmxremote");
        cmds.addElement("-Datavism.servername=" + svrName);
        if (svrName == "messageServer") {
            cmds.addElement("atavism.msgsvr.MessageServer");
        }
        else {
            cmds.addElement("atavism.server.engine.Engine");
        }
        try {
            final String scriptlist = Launcher.properties.getProperty(String.valueOf(svrName) + ".scripts");
            if (scriptlist != null) {
                final String[] scripts = scriptlist.split(",");
                System.out.print("scripts: ");
                for (int i = 0; i < scripts.length; ++i) {
                    System.out.print(String.valueOf(scripts[i]) + ",  ");
                    cmds.addElement(scripts[i]);
                }
                System.out.println("\n---------");
            }
            else {
                System.out.println("No scripts specified for " + svrName);
            }
            final List<String> lCmds = cmds;
            final ProcessBuilder pb = new ProcessBuilder(lCmds);
            if (pb != null) {
                final String cp = System.getProperty("java.class.path");
                final Map<String, String> env = pb.environment();
                env.put("CLASSPATH", cp);
                p = pb.start();
            }
            else {
                System.out.println("pb is null!");
            }
        }
        catch (Exception e) {
            System.out.println("Exception in Launcher ");
            e.printStackTrace();
        }
        return p;
    }
    
    public static void main(final String[] args) {
        final Launcher launcher = new Launcher();
        String command = "all";
        command = args[0];
        if (command == null) {
            command = "all";
        }
        try {
            launcher.startAllServers();
        }
        catch (Exception e) {
            System.out.println("Exception in Launcher ");
            e.printStackTrace();
        }
    }
    
    static {
        Launcher.properties = new Properties();
    }
}
