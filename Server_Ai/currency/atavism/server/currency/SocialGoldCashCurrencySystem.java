// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import java.io.Serializable;
import atavism.server.util.Log;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;

public class SocialGoldCashCurrencySystem extends CurrencySystem
{
    private static final Long NEW_ACCOUNT_BALANCE;
    private final SocialGoldService sgService;
    
    public SocialGoldCashCurrencySystem(final int id, final String name, final String offerId, final String secretKey) {
        super(id, name);
        this.sgService = new SocialGoldService(offerId, secretKey);
    }
    
    public SocialGoldCashCurrencySystem(final int id, final String name, final String offerId, final String secretKey, final boolean live) {
        super(id, name);
        this.sgService = new SocialGoldService(offerId, secretKey, live);
    }
    
    private String getUserId(final OID playerOid) {
        String accountId = null;
        final Serializable accountIdObj = EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "AccountId");
        if (accountIdObj instanceof Integer) {
            accountId = String.format("%d", (int)accountIdObj);
        }
        else if (accountIdObj instanceof Long) {
            accountId = String.format("%d", (long)accountIdObj);
        }
        else if (accountIdObj instanceof String) {
            accountId = (String)accountIdObj;
        }
        else if (accountIdObj instanceof OID) {
            accountId = String.format("%d", ((OID)accountIdObj).toLong());
        }
        else {
            Log.error("Unknown type for 'AccountId'");
        }
        return accountId;
    }
    
    @Override
    public Float getBalance(final Purse purse) {
        purse.getLock().lock();
        try {
            Long balance = null;
            final String userId = this.getUserId(purse.getOid());
            balance = this.sgService.getBalance(userId);
            if (balance == null && this.sgService.createUser(userId, SocialGoldCashCurrencySystem.NEW_ACCOUNT_BALANCE)) {
                balance = this.sgService.getBalance(userId);
            }
            return (balance != null) ? purse.balance(balance / 100.0f, this.getName()) : null;
        }
        finally {
            purse.getLock().unlock();
        }
    }
    
    @Override
    public Float updateBalance(final Purse purse, Float delta, final String reason) {
        purse.getLock().lock();
        try {
            Long balance = null;
            final String userId = this.getUserId(purse.getOid());
            balance = this.sgService.getBalance(userId);
            if (balance == null) {
                return null;
            }
            delta *= 100.0f;
            if (delta > 0.0f) {
                if (this.getMaxBalance() == 0.0f || balance + delta <= this.getMaxBalance()) {
                    balance = this.sgService.credit(userId, (long)(Object)delta, reason);
                }
                else {
                    balance = null;
                }
            }
            else if (balance + delta >= this.getMinBalance()) {
                balance = this.sgService.debit(userId, -(long)(Object)delta, reason);
            }
            else {
                balance = null;
            }
            if (balance != null && this.getLogTransactions()) {
                this.logTransaction(purse, delta, reason);
            }
            return (balance != null) ? purse.balance(balance / 100.0f, this.getName()) : null;
        }
        finally {
            purse.getLock().unlock();
        }
    }
    
    @Override
    public String amountToString(final Float amount) {
        final String amountString = "" + (long)(Object)amount + " " + this.getName();
        return amountString;
    }
    
    static {
        NEW_ACCOUNT_BALANCE = 0L;
    }
}
