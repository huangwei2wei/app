// 
// Decompiled by Procyon v0.5.30
// 

package atavism.management;

import atavism.server.util.Log;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import atavism.server.util.InitLogAndPid;
import java.util.Iterator;
import java.util.Map;
import atavism.msgsys.GenericResponseMessage;
import atavism.msgsys.ResponseMessage;
import atavism.msgsys.Message;
import java.io.IOException;
import java.util.LinkedList;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import atavism.msgsys.ResponseCallback;

public class PluginStatusCheck extends TransientAgent implements ResponseCallback
{
    public static final int TIMEOUT = 5000;
    int responders;
    int expectedResponders;
    List<String> pluginStatus;
    LinkedHashMap<String, Serializable> rollup;
    
    public PluginStatusCheck(final String agentName, final String domainServer, final int domainPort) {
        super(agentName, domainServer, domainPort);
        this.responders = 0;
        this.pluginStatus = new LinkedList<String>();
        this.rollup = new LinkedHashMap<String, Serializable>();
        this.agent().addAdvertisement(Management.MSG_TYPE_GET_PLUGIN_STATUS);
    }
    
    @Override
    public void connect() throws IOException {
        super.connect();
        this.agent().getDomainClient().awaitPluginDependents("Domain", "PluginStatusCheck");
    }
    
    public List<String> getPluginStatus() {
        final Message request = new Message(Management.MSG_TYPE_GET_PLUGIN_STATUS);
        this.expectedResponders = this.agent().sendBroadcastRPC(request, this);
        synchronized (this) {
            this.responders += this.expectedResponders;
            final long startTime = System.currentTimeMillis();
            while (this.responders != 0) {
                try {
                    this.wait(5000L);
                    if (System.currentTimeMillis() - startTime > 5000L) {
                        break;
                    }
                    continue;
                }
                catch (InterruptedException e) {}
            }
        }
        return this.pluginStatus;
    }
    
    @Override
    public synchronized void handleResponse(final ResponseMessage rr) {
        --this.responders;
        final GenericResponseMessage response = (GenericResponseMessage)rr;
        final LinkedHashMap<String, Serializable> status = (LinkedHashMap<String, Serializable>)response.getData();
        String statusString = "";
        String pluginName = status.get("plugin");
        if (pluginName == null) {
            pluginName = response.getSenderName();
        }
        for (final Map.Entry<String, Serializable> ss : status.entrySet()) {
            if (ss.getKey().equals("plugin")) {
                continue;
            }
            statusString = statusString + pluginName + "." + ss.getKey() + "=" + ss.getValue() + " ";
            this.rollupValue(ss.getKey(), ss.getValue());
        }
        this.pluginStatus.add(statusString);
        if (this.responders == 0) {
            this.notify();
        }
    }
    
    private void rollupValue(final String key, final Serializable value) {
        Serializable current = this.rollup.get(key);
        if (value instanceof Integer) {
            if (current == null) {
                current = new Integer(0);
            }
            current = (int)current + (int)value;
            this.rollup.put(key, current);
        }
        else if (value instanceof Long) {
            if (current == null) {
                current = new Long(0L);
            }
            current = (long)current + (long)value;
            this.rollup.put(key, current);
        }
        else if (value instanceof Float) {
            if (current == null) {
                current = new Float(0.0f);
            }
            current = (float)current + (float)value;
            this.rollup.put(key, current);
        }
        else if (value instanceof Double) {
            if (current == null) {
                current = new Double(0.0);
            }
            current = (double)current + (double)value;
            this.rollup.put(key, current);
        }
    }
    
    public int getMissingResponders() {
        return this.responders;
    }
    
    public int getExpectedResponders() {
        return this.expectedResponders;
    }
    
    public Map<String, Serializable> getRollup() {
        return this.rollup;
    }
    
    public static void main(final String[] args) throws IOException {
        InitLogAndPid.initLogAndPid(args);
        final LongOpt[] longopts = { new LongOpt("port", 1, (StringBuffer)null, 2), new LongOpt("keys", 1, (StringBuffer)null, 3), new LongOpt("host", 1, (StringBuffer)null, 4) };
        final Getopt g = new Getopt("PluginStatusCheck", args, "s:a:m:t:", longopts);
        String agentName = "PluginStatusCheck";
        String domainServer = "localhost";
        int domainPort = 20374;
        final List<String> scripts = new LinkedList<String>();
        String[] keys = null;
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 115: {
                    scripts.add(g.getOptarg());
                    continue;
                }
                case 97: {
                    agentName = g.getOptarg();
                }
                case 109:
                case 116: {
                    continue;
                }
                case 2: {
                    domainPort = Integer.parseInt(g.getOptarg());
                    continue;
                }
                case 3: {
                    keys = g.getOptarg().split(",");
                    continue;
                }
                case 4: {
                    domainServer = g.getOptarg();
                    continue;
                }
            }
        }
        Log.init();
        final PluginStatusCheck tagent = new PluginStatusCheck(agentName, domainServer, domainPort);
        for (final String scriptFileName : scripts) {
            tagent.runScript(scriptFileName);
        }
        tagent.agent().setDomainConnectRetries(0);
        tagent.connect();
        final List<String> status = tagent.getPluginStatus();
        String statusText;
        int exitCode;
        if (tagent.getMissingResponders() == 0) {
            statusText = "OK " + tagent.getExpectedResponders() + " plugins responding";
            exitCode = 0;
        }
        else {
            statusText = "WARN missing " + tagent.getMissingResponders() + " out of " + tagent.getExpectedResponders() + " plugins";
            exitCode = 1;
        }
        String perfData = "|";
        if (keys != null) {
            for (final String key : keys) {
                final Object value = tagent.getRollup().get(key);
                if (value != null) {
                    perfData = perfData + key + "=" + value + " ";
                }
                else {
                    perfData = perfData + key + "=U ";
                }
            }
        }
        else {
            for (final Map.Entry<String, Serializable> ss : tagent.getRollup().entrySet()) {
                perfData = perfData + ss.getKey() + "=" + ss.getValue() + " ";
            }
        }
        if (keys == null) {
            for (final String ss2 : status) {
                perfData += ss2;
            }
        }
        System.out.println(statusText + perfData);
        System.exit(exitCode);
    }
}
