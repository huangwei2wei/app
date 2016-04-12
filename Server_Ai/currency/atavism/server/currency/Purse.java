// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import atavism.server.util.LockFactory;
import atavism.server.objects.EntityManager;
import atavism.server.util.Log;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.engine.OID;
import java.util.concurrent.locks.Lock;
import atavism.server.objects.Entity;

public class Purse extends Entity
{
    private static final long serialVersionUID = 1L;
    private static String PRIMARY;
    public static Lock GlobalLock;
    protected transient Purse accountPurse;
    
    public Purse() {
        this.accountPurse = null;
        this.setNamespace(CurrencyClient.NAMESPACE);
    }
    
    public Purse(final String name) {
        super(name);
        this.accountPurse = null;
        this.setNamespace(CurrencyClient.NAMESPACE);
    }
    
    public Purse(final OID objectOid) {
        super(objectOid);
        this.accountPurse = null;
        this.setNamespace(CurrencyClient.NAMESPACE);
    }
    
    public Purse(final OID objectOid, final String name) {
        super(objectOid);
        this.accountPurse = null;
        this.setName(name);
        this.setNamespace(CurrencyClient.NAMESPACE);
    }
    
    public Float balance(final String currency) {
        this.getLock().lock();
        try {
            final Float bal = (Float)this.getProperty(currency);
            return (bal == null) ? new Float(0.0f) : bal;
        }
        finally {
            this.getLock().unlock();
        }
    }
    
    public Float balance(final Float balance, final String currency) {
        this.getLock().lock();
        try {
            this.setProperty(currency, (Serializable)balance);
            Engine.getPersistenceManager().setDirty((Entity)this);
            return balance;
        }
        finally {
            this.getLock().unlock();
        }
    }
    
    public Float updateBalance(final Float delta, final String currency) {
        this.getLock().lock();
        try {
            final Float bal = this.balance(currency);
            return (bal != null) ? this.balance(bal + delta, currency) : bal;
        }
        finally {
            this.getLock().unlock();
        }
    }
    
    public Purse getAccountPurse() {
        if (this.accountPurse == null) {
            final OID playerOid = this.getOid();
            final Serializable accountIdObj = EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "AccountId");
            OID accountId = null;
            if (accountIdObj instanceof Integer) {
                accountId = OID.fromLong((long)accountIdObj);
            }
            else if (accountIdObj instanceof Long) {
                accountId = OID.fromLong((long)accountIdObj);
            }
            else if (accountIdObj instanceof String) {
                accountId = OID.fromLong(Long.parseLong((String)accountIdObj));
            }
            else {
                if (!(accountIdObj instanceof OID)) {
                    Log.error("Unknown type for 'AccountId'");
                    return null;
                }
                accountId = (OID)accountIdObj;
            }
            Purse.GlobalLock.lock();
            try {
                final String accountPurseName = "AccountPurse-" + accountId.toLong();
                OID accountPurseOid = Engine.getDatabase().getOidByName(accountPurseName, CurrencyClient.NAMESPACE);
                if (accountPurseOid != null) {
                    this.accountPurse = (Purse)Engine.getDatabase().loadEntity(accountPurseOid, CurrencyClient.NAMESPACE);
                }
                if (this.accountPurse == null) {
                    this.accountPurse = new Purse(accountPurseName);
                    accountPurseOid = this.accountPurse.getOid();
                    EntityManager.registerEntityByNamespace((Entity)this.accountPurse, CurrencyClient.NAMESPACE);
                    this.accountPurse.setPersistenceFlag(true);
                    Engine.getPersistenceManager().persistEntity((Entity)this.accountPurse);
                    Engine.getPersistenceManager().setDirty((Entity)this.accountPurse);
                }
            }
            finally {
                Purse.GlobalLock.unlock();
            }
        }
        return this.accountPurse;
    }
    
    static {
        Purse.PRIMARY = "primary";
        Purse.GlobalLock = LockFactory.makeLock("PurseStaticGlobalLock");
    }
}
