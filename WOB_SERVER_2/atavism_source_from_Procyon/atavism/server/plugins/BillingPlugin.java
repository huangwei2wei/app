// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.msgsys.ResponseMessage;
import atavism.msgsys.Message;
import java.util.HashSet;
import java.util.Iterator;
import atavism.server.objects.EntityManager;
import java.io.Serializable;
import java.util.Map;
import atavism.server.objects.Entity;
import atavism.server.engine.OID;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.engine.Hook;
import atavism.server.util.Log;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.messages.LoginMessage;
import atavism.server.messages.LogoutMessage;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.LockFactory;
import atavism.server.util.Logger;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.EnginePlugin;

public class BillingPlugin extends EnginePlugin
{
    protected Lock lock;
    private static final Logger log;
    
    public BillingPlugin() {
        super("Billing");
        this.lock = LockFactory.makeLock("BillingPlugin");
        this.setPluginType("Billing");
    }
    
    @Override
    public void onActivate() {
        super.onActivate();
        this.registerHooks();
        final MessageTypeFilter responderFilter = new MessageTypeFilter();
        responderFilter.addType(BillingClient.MSG_TYPE_GET_TOKEN_BALANCE);
        responderFilter.addType(BillingClient.MSG_TYPE_DECREMENT_TOKEN_BALANCE);
        responderFilter.addType(LogoutMessage.MSG_TYPE_LOGOUT);
        responderFilter.addType(LoginMessage.MSG_TYPE_LOGIN);
        Engine.getAgent().createSubscription(responderFilter, this, 8);
        this.registerPluginNamespace(BillingClient.NAMESPACE, new BillingSubObjectHook());
        if (Log.loggingDebug) {
            BillingPlugin.log.debug("BillingPlugin activated");
        }
    }
    
    public void registerHooks() {
        this.getHookManager().addHook(BillingClient.MSG_TYPE_GET_TOKEN_BALANCE, new GetTokenBalanceHook());
        this.getHookManager().addHook(BillingClient.MSG_TYPE_DECREMENT_TOKEN_BALANCE, new UpdateTokenBalanceHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, new LogOutHook());
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, new LoginHook());
    }
    
    public static SubObjData createBillingSubObject(final Template template, final Namespace namespace, final OID masterOid) {
        if (Log.loggingDebug) {
            BillingPlugin.log.debug("createBillingSubObject: masterOid=" + masterOid + ", template=" + template);
        }
        if (masterOid == null) {
            BillingPlugin.log.error("createBillingSubObject: no master oid");
            return null;
        }
        if (Log.loggingDebug) {
            BillingPlugin.log.debug("createBillingSubObject: masterOid=" + masterOid + ", template=" + template);
        }
        final Map<String, Serializable> props = template.getSubMap(BillingClient.NAMESPACE);
        if (props == null) {
            BillingPlugin.log.warn("createBillingSubObject: no props in ns " + BillingClient.NAMESPACE);
            return null;
        }
        final Entity tinfo = new Entity(masterOid);
        tinfo.setName(template.getName());
        for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
            final String key = entry.getKey();
            final Serializable value = entry.getValue();
            if (!key.startsWith(":")) {
                tinfo.setProperty(key, value);
            }
        }
        if (Log.loggingDebug) {
            BillingPlugin.log.debug("createBillingSubObject: created entity " + tinfo);
        }
        EntityManager.registerEntityByNamespace(tinfo, BillingClient.NAMESPACE);
        return new SubObjData();
    }
    
    public static void runEchoTest() {
    }
    
    private Float getTokenBalance(final Integer accountId, final String worldId) {
        this.lock.lock();
        try {
            return 100.0f;
        }
        catch (Exception e) {
            BillingPlugin.log.exception("getTokenBalance - " + e.toString(), e);
            return new Float(0.0f);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private Float updateTokenBalance(final Integer accountId, final String worldId, final Float amount, final HashSet<Serializable> purchasedItems, final OID playerOid) {
        this.lock.lock();
        try {
            if (amount > 0.0) {
                BillingPlugin.log.error("updateTokenBalance - Amount must be nonpositive.");
                return new Float(0.0f);
            }
            return 100.0f + amount;
        }
        catch (Exception e) {
            BillingPlugin.log.exception("updateTokenBalance - " + e.toString(), e);
            return new Float(0.0f);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private void handleLoginLogOutMessage(final String type, final Integer accountId, final OID playerOid) {
        this.lock.lock();
        this.lock.unlock();
    }
    
    static {
        log = new Logger("BillingPlugin");
    }
    
    public class BillingSubObjectHook extends GenerateSubObjectHook
    {
        public BillingSubObjectHook() {
            super(BillingPlugin.this);
        }
        
        @Override
        public SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
            return BillingPlugin.createBillingSubObject(template, namespace, masterOid);
        }
    }
    
    class GetTokenBalanceHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage message = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = message.getSubject();
            final Integer accountId = (Integer)EnginePlugin.getObjectProperty(playerOid, Namespace.WORLD_MANAGER, "AccountId");
            final String worldId = Engine.getWorldName();
            BillingPlugin.log.info("GET TOKEN BALANCE VARIABLES: " + accountId + " : " + worldId);
            Float balance = 0.0f;
            final String endPoint = Engine.properties.getProperty("atavism.service_endpoint");
            if (endPoint != null && endPoint.length() > 0) {
                balance = BillingPlugin.this.getTokenBalance(accountId, worldId);
            }
            Engine.getAgent().sendObjectResponse(msg, balance);
            return true;
        }
    }
    
    class UpdateTokenBalanceHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage message = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = message.getSubject();
            final Integer accountId = (Integer)EnginePlugin.getObjectProperty(playerOid, Namespace.WORLD_MANAGER, "AccountId");
            final String worldId = Engine.getWorldName();
            Float balance = 0.0f;
            final String endPoint = Engine.properties.getProperty("atavism.service_endpoint");
            if (endPoint != null && endPoint.length() > 0) {
                balance = BillingPlugin.this.updateTokenBalance(accountId, worldId, (Float)message.getProperty("amount"), (HashSet)message.getProperty("purchasedItems"), playerOid);
            }
            Engine.getAgent().sendObjectResponse(msg, balance);
            return true;
        }
    }
    
    class LogOutHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage logoutMsg = (LogoutMessage)msg;
            final OID playerOid = logoutMsg.getSubject();
            final Integer accountId = (Integer)EnginePlugin.getObjectProperty(playerOid, Namespace.WORLD_MANAGER, "AccountId");
            final String endPoint = Engine.properties.getProperty("atavism.service_endpoint");
            if (endPoint != null && endPoint.length() > 0) {
                BillingPlugin.this.handleLoginLogOutMessage("out", accountId, playerOid);
            }
            Engine.getAgent().sendResponse(new ResponseMessage(logoutMsg));
            return true;
        }
    }
    
    class LoginHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage loginMsg = (LoginMessage)msg;
            final OID playerOid = loginMsg.getSubject();
            final Integer accountId = (Integer)EnginePlugin.getObjectProperty(playerOid, Namespace.WORLD_MANAGER, "AccountId");
            final String endPoint = Engine.properties.getProperty("atavism.service_endpoint");
            if (endPoint != null && endPoint.length() > 0) {
                BillingPlugin.this.handleLoginLogOutMessage("in", accountId, playerOid);
            }
            Engine.getAgent().sendResponse(new ResponseMessage(loginMsg));
            return true;
        }
    }
}
