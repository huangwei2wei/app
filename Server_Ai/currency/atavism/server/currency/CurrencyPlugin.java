// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import java.util.List;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.Message;
import java.util.Iterator;
import java.util.Map;
import atavism.server.objects.Template;
import java.util.Collection;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.engine.Namespace;
import java.util.HashSet;
import java.io.Serializable;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.server.engine.OID;
import atavism.server.engine.Hook;
import atavism.server.util.AORuntimeException;
import atavism.server.util.Log;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.Logger;
import atavism.server.engine.Manager;
import atavism.server.engine.EnginePlugin;

public class CurrencyPlugin extends EnginePlugin
{
    public static final String PLUGIN_NAME = "Currency";
    public static Manager<CurrencySystem> CurrencySystemManager;
    private static final Logger log;
    
    public CurrencyPlugin() {
        super("Currency");
        this.setPluginType("Currency");
    }
    
    public static void registerCurrencySystem(final CurrencySystem currencySystem) {
        CurrencyPlugin.CurrencySystemManager.set(currencySystem.getID(), (Object)currencySystem);
    }
    
    public void onActivate() {
        try {
            super.onActivate();
            this.registerHooks();
            final MessageTypeFilter filter = new MessageTypeFilter();
            filter.addType(CurrencyClient.MSG_TYPE_UPDATE_BALANCE);
            Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
            final MessageTypeFilter filterRPC = new MessageTypeFilter();
            filterRPC.addType(CurrencyClient.MSG_TYPE_GET_BALANCE);
            filterRPC.addType(CurrencyClient.MSG_TYPE_LIST_CURRENCIES);
            Engine.getAgent().createSubscription((IFilter)filterRPC, (MessageCallback)this, 8);
            this.registerPluginNamespace(CurrencyClient.NAMESPACE, (EnginePlugin.GenerateSubObjectHook)new CurrencyPluginGenerateSubObjectHook());
            this.registerLoadHook(CurrencyClient.NAMESPACE, (EnginePlugin.LoadHook)new CurrencyPluginLoadHook());
            if (Log.loggingDebug) {
                CurrencyPlugin.log.debug("CurrencyPlugin activated.");
            }
        }
        catch (Exception e) {
            throw new AORuntimeException("onActivate failed", (Throwable)e);
        }
    }
    
    public void registerHooks() {
        this.getHookManager().addHook(CurrencyClient.MSG_TYPE_GET_BALANCE, (Hook)new GetBalanceHook());
        this.getHookManager().addHook(CurrencyClient.MSG_TYPE_UPDATE_BALANCE, (Hook)new UpdateBalanceHook());
        this.getHookManager().addHook(CurrencyClient.MSG_TYPE_LIST_CURRENCIES, (Hook)new ListCurrenciesHook());
    }
    
    public static Purse getPurse(final OID oid) {
        Purse purse = (Purse)EntityManager.getEntityByNamespace(oid, CurrencyClient.NAMESPACE);
        if (purse == null) {
            purse = new Purse(oid, "Purse");
            EntityManager.registerEntityByNamespace((Entity)purse, CurrencyClient.NAMESPACE);
            EnginePlugin.setObjectProperty(oid, CurrencyClient.NAMESPACE, "version", (Serializable)"1.0");
            purse.setPersistenceFlag(true);
            Engine.getPersistenceManager().persistEntity((Entity)purse);
            Engine.getPersistenceManager().setDirty((Entity)purse);
            final HashSet<Namespace> namespaces = new HashSet<Namespace>();
            namespaces.add(CurrencyClient.NAMESPACE);
            final Boolean rv = ObjectManagerClient.addSubObjectNamespace(oid, (Collection)namespaces);
        }
        return purse;
    }
    
    public Float getBalance(final Purse purse, final int currency) {
        final CurrencySystem currencySystem = (CurrencySystem)CurrencyPlugin.CurrencySystemManager.get(currency);
        if (currencySystem != null) {
            return currencySystem.getBalance(purse);
        }
        return null;
    }
    
    public Float getBalance(final OID subjectOid, final int currency) {
        Float balance = 0.0f;
        final Purse purse = getPurse(subjectOid);
        if (purse != null) {
            balance = this.getBalance(purse, currency);
        }
        return balance;
    }
    
    public Float updateBalance(final Purse purse, final Float delta, final int currency, final String reason) {
        final CurrencySystem currencySystem = (CurrencySystem)CurrencyPlugin.CurrencySystemManager.get(currency);
        if (currencySystem != null) {
            return currencySystem.updateBalance(purse, delta, reason);
        }
        return null;
    }
    
    static {
        CurrencyPlugin.CurrencySystemManager = (Manager<CurrencySystem>)new Manager("CurrencySystemManager");
        log = new Logger("CurrencyPlugin");
    }
    
    public class CurrencyPluginLoadHook implements EnginePlugin.LoadHook
    {
        public void onLoad(final Entity entity) {
        }
    }
    
    public class CurrencyPluginGenerateSubObjectHook extends EnginePlugin.GenerateSubObjectHook
    {
        public CurrencyPluginGenerateSubObjectHook() {
            super((EnginePlugin)CurrencyPlugin.this);
        }
        
        public EnginePlugin.SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
            if (Log.loggingDebug) {
                CurrencyPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            if (masterOid == null) {
                return null;
            }
            final Purse purse = CurrencyPlugin.getPurse(masterOid);
            final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(CurrencyClient.NAMESPACE);
            if (props != null) {
                for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                    final String key = entry.getKey();
                    final String value = entry.getValue();
                    if (!key.startsWith(":")) {
                        final Float amount = Float.parseFloat(value);
                        purse.setProperty(key, (Serializable)amount);
                    }
                }
            }
            if (Log.loggingDebug) {
                CurrencyPlugin.log.debug("GenerateSubObjectHook: created entity " + purse);
            }
            return new EnginePlugin.SubObjData();
        }
    }
    
    public class GetBalanceHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage extmsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID subjectOid = extmsg.getSubject();
            final int currency = (int)extmsg.getProperty("currency");
            final Float balance = CurrencyPlugin.this.getBalance(subjectOid, currency);
            Engine.getAgent().sendObjectResponse(msg, (Object)balance);
            return true;
        }
    }
    
    public class UpdateBalanceHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage extmsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID subjectOid = extmsg.getSubject();
            final int currency = (int)extmsg.getProperty("currency");
            final Float delta = (Float)extmsg.getProperty("delta");
            final String reason = (String)extmsg.getProperty("reason");
            final Purse purse = CurrencyPlugin.getPurse(subjectOid);
            Float balance = null;
            if (purse != null) {
                balance = CurrencyPlugin.this.updateBalance(purse, delta, currency, reason);
            }
            return true;
        }
    }
    
    public class ListCurrenciesHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final List<Integer> currencyList = (List<Integer>)CurrencyPlugin.CurrencySystemManager.keyList();
            Engine.getAgent().sendObjectResponse(msg, (Object)currencyList);
            return true;
        }
    }
}
