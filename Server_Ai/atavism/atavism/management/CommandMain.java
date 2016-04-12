// 
// Decompiled by Procyon v0.5.30
// 

package atavism.management;

import javax.management.JMException;
import sun.jvmstat.monitor.MonitorException;
import java.net.URISyntaxException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.HostIdentifier;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import sun.management.ConnectorAddressLink;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import gnu.getopt.Getopt;
import java.util.Set;

public class CommandMain
{
    static Set activeVms;
    
    public static void main(final String[] args) {
        final Getopt g = new Getopt("CommandMain", args, "p:f:s:d:q");
        final List<String> scripts = new LinkedList<String>();
        final List<String> scriptNames = new LinkedList<String>();
        List<String> processes = new LinkedList<String>();
        String pidDir = null;
        boolean quiet = false;
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 112: {
                    processes.add(g.getOptarg());
                    continue;
                }
                case 102: {
                    final String fileName = g.getOptarg();
                    scriptNames.add(fileName);
                    final String script = readFile(fileName);
                    if (script == null) {
                        System.exit(1);
                    }
                    scripts.add(script);
                    continue;
                }
                case 115: {
                    final String script = g.getOptarg();
                    scripts.add(script);
                    scriptNames.add(script);
                    continue;
                }
                case 100: {
                    pidDir = g.getOptarg();
                    continue;
                }
                case 113: {
                    quiet = true;
                    continue;
                }
            }
        }
        if (processes.size() == 0 || scripts.size() == 0) {
            System.err.println("Usage: aom [-q] [-d <pid-dir>] -p <pid|agent-name> -s <script> -f <script-file>");
            System.exit(1);
        }
        if (pidDir != null) {
            processes = resolveProcesses(pidDir, processes);
        }
        String argvString = "argv = [\"<string>\"";
        int arg = 0;
        for (int ii = g.getOptind(); ii < args.length; ++ii) {
            argvString = argvString + ",\"" + args[ii] + "\"";
            ++arg;
        }
        argvString += "]\n";
        boolean ok = true;
        for (final String process : processes) {
            int ss = 0;
            for (final String script2 : scripts) {
                if (!quiet) {
                    System.out.println("Process " + process + ": " + scriptNames.get(ss));
                }
                ++ss;
                ok = (ok && execScript(process, argvString + script2));
            }
        }
        if (ok) {
            System.exit(0);
        }
        else {
            System.exit(1);
        }
    }
    
    static String readFile(final String fileName) {
        final File scriptFile = new File(fileName);
        if (!scriptFile.exists()) {
            System.err.println(fileName + ": file does not exist");
            return null;
        }
        final char[] data = new char[(int)scriptFile.length()];
        try {
            final FileReader reader = new FileReader(scriptFile);
            reader.read(data, 0, (int)scriptFile.length());
            reader.close();
        }
        catch (IOException e) {
            System.err.println(fileName + ": " + e);
            return null;
        }
        return new String(data);
    }
    
    static boolean execScript(final String process, final String script) {
        int vmid = -1;
        try {
            vmid = Integer.parseInt(process);
        }
        catch (NumberFormatException e4) {
            vmid = findVmid(process);
        }
        if (vmid == -1) {
            System.err.println(process + ": Could not find process");
            return false;
        }
        try {
            final String address = ConnectorAddressLink.importFrom(vmid);
            System.out.println("vmid: " + vmid + "; address: " + address);
            final JMXServiceURL jmxUrl = new JMXServiceURL(address);
            final JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl);
            final MBeanServerConnection server = jmxc.getMBeanServerConnection();
            final Object[] parameters = { script };
            final String[] signature = { "java.lang.String" };
            final Object result = server.invoke(new ObjectName("net.atavism:type=Engine"), "runPythonScript", parameters, signature);
            System.out.println(result.toString());
            jmxc.close();
        }
        catch (IOException e) {
            System.err.println("Unable to attach to " + vmid + ": " + e.getMessage());
            return false;
        }
        catch (MalformedObjectNameException e2) {
            System.err.println("Internal error: " + e2.getMessage());
            return false;
        }
        catch (InstanceNotFoundException e5) {
            System.err.println("Process " + vmid + " is not a Atavism engine");
            return false;
        }
        catch (Exception e3) {
            System.err.println("Error: " + e3);
            return false;
        }
        return true;
    }
    
    static int findVmid(final String agentName) {
        if (CommandMain.activeVms == null) {
            try {
                final MonitoredHost host = MonitoredHost.getMonitoredHost(new HostIdentifier((String)null));
                CommandMain.activeVms = host.activeVms();
            }
            catch (URISyntaxException e) {
                throw new InternalError(e.getMessage());
            }
            catch (MonitorException e2) {
                throw new InternalError(e2.getMessage());
            }
        }
        for (final Object vm : CommandMain.activeVms) {
            try {
                final String address = ConnectorAddressLink.importFrom((int)vm);
                if (address == null) {
                    continue;
                }
                final JMXServiceURL jmxUrl = new JMXServiceURL(address);
                final JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl);
                final MBeanServerConnection server = jmxc.getMBeanServerConnection();
                final Object result = server.getAttribute(new ObjectName("net.atavism:type=Engine"), "AgentName");
                jmxc.close();
                if (result != null && result.toString().equals(agentName)) {
                    return (int)vm;
                }
                continue;
            }
            catch (IOException e3) {
                System.err.println("Unable to attach to " + vm + ": " + e3.getMessage());
            }
            catch (InstanceNotFoundException e5) {}
            catch (JMException e4) {
                System.err.println("Unable to attach to " + vm + ": " + e4);
            }
        }
        return -1;
    }
    
    static List<String> resolveProcesses(final String pidDir, final List<String> processes) {
        final List<String> processIds = new LinkedList<String>();
        for (final String processName : processes) {
            String pidFile = null;
            final File scriptFile = new File(pidDir + File.separator + processName + ".winpid");
            if (scriptFile.exists()) {
                pidFile = pidDir + File.separator + processName + ".winpid";
            }
            else {
                pidFile = pidDir + File.separator + processName + ".pid";
            }
            String fileContents = readFile(pidFile);
            fileContents = fileContents.trim();
            processIds.add(fileContents);
        }
        return processIds;
    }
    
    static {
        CommandMain.activeVms = null;
    }
}
