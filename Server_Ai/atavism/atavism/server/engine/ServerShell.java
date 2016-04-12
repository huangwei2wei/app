// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.msgsys.MessageTrigger;
import atavism.msgsys.Filter;
import atavism.server.messages.PerceptionTrigger;
import atavism.msgsys.ResponseCallback;
import atavism.msgsys.ResponseMessage;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.objects.ObjectTypes;
import atavism.server.messages.PerceptionMessage;
import atavism.msgsys.TargetMessage;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.FilterUpdate;
import atavism.msgsys.IFilter;
import atavism.server.messages.PerceptionFilter;
import java.util.HashMap;
import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.msgsys.MessageCatalog;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import gnu.getopt.Getopt;
import atavism.server.util.Log;
import atavism.msgsys.MessageAgent;
import atavism.msgsys.MessageType;
import atavism.msgsys.MessageCallback;

public class ServerShell implements MessageCallback
{
    public static ServerShell shell;
    public static MessageType MSG_TYPE_TEST0;
    public static MessageType MSG_TYPE_TEST1;
    public static MessageType MSG_TYPE_TEST2;
    public static MessageType MSG_TYPE_TEST3;
    public static MessageType MSG_TYPE_TEST4;
    public static MessageType MSG_TYPE_TEST5;
    public MessageAgent agent;
    public long lastMessageCount;
    public long startTime;
    
    public ServerShell() {
        this.lastMessageCount = 0L;
        this.startTime = 0L;
    }
    
    public static void main(final String[] args) {
        Log.init();
        final ServerShell shell = ServerShell.shell = new ServerShell();
        configureMessageCatalog();
        final String agentName = "mtest" + args[0];
        final Getopt g = new Getopt("ServerShell", args, "t:m:");
        final List<String> tests = new LinkedList<String>();
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 116: {
                    final String testName = g.getOptarg();
                    tests.add(testName);
                }
                case 109: {
                    continue;
                }
                case 63: {
                    Log.info("Exiting ServerShell because of unrecognized option '" + c + "'");
                    System.exit(1);
                    continue;
                }
            }
        }
        final MessageAgent agent = new MessageAgent(agentName);
        shell.agent = agent;
        shell.lastMessageCount = 0L;
        try {
            agent.openListener();
            final List<MessageType> adverts = new LinkedList<MessageType>();
            adverts.add(ServerShell.MSG_TYPE_TEST0);
            adverts.add(ServerShell.MSG_TYPE_TEST1);
            adverts.add(ServerShell.MSG_TYPE_TEST2);
            adverts.add(ServerShell.MSG_TYPE_TEST3);
            adverts.add(ServerShell.MSG_TYPE_TEST4);
            adverts.add(ServerShell.MSG_TYPE_TEST5);
            agent.setAdvertisements(adverts);
            agent.connectToDomain("localhost", 20374);
        }
        catch (Exception e) {
            System.err.println("connectToDomain: " + e);
            e.printStackTrace();
            System.exit(1);
        }
        shell.runTests(tests);
    }
    
    static void configureMessageCatalog() {
        final MessageCatalog messageCatalog = MessageCatalog.addMsgCatalog("test", 10000, 100);
        messageCatalog.addMsgTypeTranslation(ServerShell.MSG_TYPE_TEST0);
        messageCatalog.addMsgTypeTranslation(ServerShell.MSG_TYPE_TEST1);
        messageCatalog.addMsgTypeTranslation(ServerShell.MSG_TYPE_TEST2);
        messageCatalog.addMsgTypeTranslation(ServerShell.MSG_TYPE_TEST3);
        messageCatalog.addMsgTypeTranslation(ServerShell.MSG_TYPE_TEST4);
        messageCatalog.addMsgTypeTranslation(ServerShell.MSG_TYPE_TEST5);
    }
    
    @Override
    public void handleMessage(final Message message, final int flags) {
        System.out.println("** Got message id " + message.getMsgId());
    }
    
    public void handleMessage2(final Message message, final int flags) {
        if (this.startTime == 0L) {
            this.startTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - this.startTime >= 1000L) {
            System.out.println("** Got message id " + message.getMsgId());
            System.out.println("Message count " + (this.agent.getAppMessageCount() - this.lastMessageCount));
            this.lastMessageCount = this.agent.getAppMessageCount();
            this.startTime = System.currentTimeMillis();
        }
    }
    
    public void runTests(final List<String> tests) {
        for (final String testName : tests) {
            try {
                if (testName.equals("test1")) {
                    new test1();
                }
                else if (testName.startsWith("pub")) {
                    final HashMap<String, String> args = parseArgs(testName);
                    new pub(args);
                }
                else if (testName.startsWith("respond")) {
                    final HashMap<String, String> args = parseArgs(testName);
                    new respond(args);
                }
                else if (testName.startsWith("rpc")) {
                    final HashMap<String, String> args = parseArgs(testName);
                    new rpc(args);
                }
                else if (testName.startsWith("sub")) {
                    final HashMap<String, String> args = parseArgs(testName);
                    new sub(args);
                }
                else if (testName.startsWith("sleep")) {
                    final HashMap<String, String> args = parseArgs(testName);
                    int seconds = 1;
                    final String str = args.get("s");
                    if (str != null) {
                        try {
                            seconds = Integer.parseInt(str);
                        }
                        catch (Exception ex2) {}
                    }
                    System.out.println("Sleeping " + seconds + " seconds");
                    try {
                        Thread.sleep(seconds * 1000);
                    }
                    catch (Exception ex3) {}
                }
                else {
                    System.err.println("Unknown test " + testName);
                }
            }
            catch (Exception ex) {
                System.out.println("ERROR " + ex);
            }
        }
    }
    
    public static HashMap<String, String> parseArgs(String arg) {
        final int colon = arg.indexOf(58);
        final HashMap<String, String> args = new HashMap<String, String>();
        if (colon == -1) {
            return args;
        }
        arg = arg.substring(colon + 1);
        final String[] strings = arg.split(",");
        for (int ii = 0; ii < strings.length; ++ii) {
            final String[] kv = strings[ii].split("=", 2);
            if (kv.length == 1) {
                args.put(kv[0], null);
            }
            else {
                args.put(kv[0], kv[1]);
            }
        }
        return args;
    }
    
    static {
        ServerShell.shell = null;
        ServerShell.MSG_TYPE_TEST0 = MessageType.intern("test0");
        ServerShell.MSG_TYPE_TEST1 = MessageType.intern("test1");
        ServerShell.MSG_TYPE_TEST2 = MessageType.intern("test2");
        ServerShell.MSG_TYPE_TEST3 = MessageType.intern("test3");
        ServerShell.MSG_TYPE_TEST4 = MessageType.intern("test4");
        ServerShell.MSG_TYPE_TEST5 = MessageType.intern("test5");
    }
    
    public class test1 implements Runnable
    {
        ServerShell shell;
        MessageAgent agent;
        
        public test1() {
            this.shell = ServerShell.shell;
            this.agent = this.shell.agent;
            new Thread(this).start();
        }
        
        @Override
        public void run() {
            final List<MessageType> messageTypes = new LinkedList<MessageType>();
            messageTypes.add(ServerShell.MSG_TYPE_TEST2);
            messageTypes.add(ServerShell.MSG_TYPE_TEST3);
            final PerceptionFilter filter = new PerceptionFilter(messageTypes);
            final long subId = this.agent.createSubscription(filter, this.shell);
            try {
                Thread.sleep(2000L);
            }
            catch (InterruptedException ex) {}
            if (filter.addTarget(OID.fromLong(1001L))) {
                final FilterUpdate update = new FilterUpdate(1);
                update.addFieldValue(1, new Long(1001L));
                this.agent.applyFilterUpdate(subId, update);
            }
        }
    }
    
    public class pub implements Runnable
    {
        ServerShell shell;
        MessageAgent agent;
        HashMap<String, String> args;
        
        public pub(final HashMap<String, String> args) {
            this.args = args;
            this.shell = ServerShell.shell;
            this.agent = this.shell.agent;
            new Thread(this).start();
        }
        
        @Override
        public void run() {
            MessageType msgType = ServerShell.MSG_TYPE_TEST0;
            Class msgClass = Message.class;
            OID oid = null;
            OID noid = null;
            int interval = 0;
            int count = 1;
            try {
                String arg = this.args.get("t");
                if (arg != null) {
                    msgType = MessageCatalog.getMessageType(arg);
                }
                arg = this.args.get("class");
                if (arg != null) {
                    if (arg.equals("subj")) {
                        msgClass = SubjectMessage.class;
                    }
                    else if (arg.equals("targ")) {
                        msgClass = TargetMessage.class;
                    }
                    else if (arg.equals("multi")) {
                        msgClass = PerceptionMessage.class;
                    }
                }
                arg = this.args.get("oid");
                if (arg != null) {
                    oid = OID.parseLong(arg);
                }
                arg = this.args.get("noid");
                if (arg != null) {
                    noid = OID.parseLong(arg);
                }
                arg = this.args.get("interval");
                if (arg != null) {
                    interval = Integer.parseInt(arg) * 1000;
                }
                arg = this.args.get("intervalms");
                if (arg != null) {
                    interval = Integer.parseInt(arg);
                }
                arg = this.args.get("count");
                if (arg != null) {
                    count = Integer.parseInt(arg);
                }
            }
            catch (Exception ex2) {}
            while (count > 0) {
                Message message = null;
                try {
                    message = msgClass.newInstance();
                }
                catch (Exception ex) {
                    System.out.println("msgClass " + ex);
                }
                message.setMsgType(msgType);
                if (msgClass == SubjectMessage.class) {
                    ((SubjectMessage)message).setSubject(oid);
                }
                if (msgClass == TargetMessage.class) {
                    ((TargetMessage)message).setTarget(noid);
                }
                if (msgClass == PerceptionMessage.class) {
                    ((PerceptionMessage)message).gainObject(noid, oid, ObjectTypes.unknown);
                }
                this.agent.sendBroadcast(message);
                try {
                    Thread.sleep(interval);
                }
                catch (Exception ex3) {}
                --count;
            }
        }
    }
    
    public class respond implements Runnable, MessageCallback
    {
        ServerShell shell;
        MessageAgent agent;
        HashMap<String, String> args;
        
        public respond(final HashMap<String, String> args) {
            this.args = args;
            this.shell = ServerShell.shell;
            this.agent = this.shell.agent;
            new Thread(this).start();
        }
        
        @Override
        public void run() {
            final MessageType requestType = ServerShell.MSG_TYPE_TEST4;
            try {
                String arg = this.args.get("t");
                arg = this.args.get("class");
                if (arg != null) {}
            }
            catch (Exception ex) {}
            final List<MessageType> messageTypes = new LinkedList<MessageType>();
            messageTypes.add(requestType);
            final MessageTypeFilter filter = new MessageTypeFilter(messageTypes);
            this.agent.createSubscription(filter, this, 8);
        }
        
        @Override
        public void handleMessage(final Message message, final int flags) {
            final ResponseMessage response = new ResponseMessage(message);
            this.agent.sendResponse(response);
        }
    }
    
    public class rpc implements Runnable, ResponseCallback
    {
        ServerShell shell;
        MessageAgent agent;
        HashMap<String, String> args;
        
        public rpc(final HashMap<String, String> args) {
            this.args = args;
            this.shell = ServerShell.shell;
            this.agent = this.shell.agent;
            new Thread(this).start();
        }
        
        @Override
        public void run() {
            MessageType msgType = ServerShell.MSG_TYPE_TEST4;
            Class msgClass = Message.class;
            OID oid = null;
            OID noid = null;
            boolean broadcast = false;
            int count = 1;
            try {
                String arg = this.args.get("t");
                if (arg != null) {
                    msgType = MessageCatalog.getMessageType(arg);
                }
                arg = this.args.get("class");
                if (arg != null) {
                    if (arg.equals("subj")) {
                        msgClass = SubjectMessage.class;
                    }
                    else if (arg.equals("targ")) {
                        msgClass = TargetMessage.class;
                    }
                }
                arg = this.args.get("oid");
                if (arg != null) {
                    oid = OID.parseLong(arg);
                }
                arg = this.args.get("noid");
                if (arg != null) {
                    noid = OID.parseLong(arg);
                }
                arg = this.args.get("broadcast");
                if (arg != null) {
                    broadcast = true;
                }
                arg = this.args.get("count");
                if (arg != null) {
                    count = Integer.parseInt(arg);
                }
            }
            catch (Exception ex2) {}
            final long startTime = System.currentTimeMillis();
            for (int rpcCount = count; rpcCount > 0; --rpcCount) {
                Message message = null;
                try {
                    message = msgClass.newInstance();
                }
                catch (Exception ex) {
                    System.out.println("msgClass " + ex);
                }
                message.setMsgType(msgType);
                if (msgClass == SubjectMessage.class) {
                    ((SubjectMessage)message).setSubject(oid);
                }
                if (msgClass == TargetMessage.class) {
                    ((TargetMessage)message).setTarget(noid);
                }
                if (broadcast) {
                    this.agent.sendBroadcastRPC(message, this);
                }
                else {
                    this.agent.sendRPC(message);
                }
            }
            System.out.println(count + " rpc in " + (System.currentTimeMillis() - startTime) + " ms");
        }
        
        @Override
        public void handleResponse(final ResponseMessage response) {
            System.out.println("** handleResponse got message id " + response.getMsgId() + " from " + response.getSenderName());
        }
    }
    
    public class sub implements Runnable, MessageCallback
    {
        ServerShell shell;
        MessageAgent agent;
        HashMap<String, String> args;
        
        public sub(final HashMap<String, String> args) {
            this.args = args;
            this.shell = ServerShell.shell;
            this.agent = this.shell.agent;
            new Thread(this).start();
        }
        
        @Override
        public void run() {
            MessageType msgType = ServerShell.MSG_TYPE_TEST5;
            MessageType triggerMsgType = ServerShell.MSG_TYPE_TEST0;
            Filter filter = new MessageTypeFilter();
            MessageTrigger trigger = null;
            OID noid = null;
            short flags = 0;
            try {
                String arg = this.args.get("t");
                if (arg != null) {
                    msgType = MessageCatalog.getMessageType(arg);
                    if (msgType == null) {
                        throw new RuntimeException("Unknown message type " + arg);
                    }
                }
                arg = this.args.get("filter");
                if (arg != null && arg.equals("multi")) {
                    filter = new PerceptionFilter();
                }
                arg = this.args.get("trigger");
                if (arg != null && arg.equals("multi")) {
                    trigger = new PerceptionTrigger();
                }
                arg = this.args.get("trigger-type");
                if (arg != null) {
                    triggerMsgType = MessageCatalog.getMessageType(arg);
                    if (msgType == null) {
                        throw new RuntimeException("Unknown message type " + arg);
                    }
                }
                arg = this.args.get("noid");
                if (arg != null) {
                    noid = OID.parseLong(arg);
                }
                arg = this.args.get("blocking");
                if (arg != null) {
                    flags |= 0x1;
                }
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex2) {
                System.out.println("error " + ex2);
            }
            final List<MessageType> messageTypes = new LinkedList<MessageType>();
            System.out.println("sub msgType " + msgType);
            messageTypes.add(msgType);
            if (filter instanceof MessageTypeFilter) {
                ((MessageTypeFilter)filter).setTypes(messageTypes);
            }
            if (filter instanceof PerceptionFilter) {
                ((PerceptionFilter)filter).setTypes(messageTypes);
                if (noid != null) {
                    ((PerceptionFilter)filter).addTarget(noid);
                }
            }
            if (trigger instanceof PerceptionTrigger) {
                final List<MessageType> types = new LinkedList<MessageType>();
                types.add(triggerMsgType);
                ((PerceptionTrigger)trigger).setTriggeringTypes(types);
            }
            final long startTime = System.currentTimeMillis();
            this.agent.createSubscription(filter, this, flags, trigger);
            System.out.println("createSubscription " + (System.currentTimeMillis() - startTime) + " ms");
        }
        
        @Override
        public void handleMessage(final Message message, final int flags) {
            System.out.println("** Subscriber got message id=" + message.getMsgId() + " class=" + message.getClass().getName());
        }
    }
}
