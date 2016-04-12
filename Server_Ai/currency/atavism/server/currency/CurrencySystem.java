// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import java.util.Iterator;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class CurrencySystem
{
    private int id;
    private final String name;
    private Boolean primaryOnly;
    private boolean sharedByAccount;
    private boolean logTransactions;
    private Float minBalance;
    private Float maxBalance;
    private LinkedList<CurrencySystem> dependsOn;
    
    public CurrencySystem(final int id, final String name) {
        this.primaryOnly = true;
        this.sharedByAccount = false;
        this.logTransactions = false;
        this.minBalance = 0.0f;
        this.maxBalance = 0.0f;
        this.dependsOn = new LinkedList<CurrencySystem>();
        this.id = id;
        this.name = name;
    }
    
    public void setSharedByAccount(final boolean sharedByAccount) {
        this.sharedByAccount = sharedByAccount;
    }
    
    public boolean getSharedByAccount() {
        return this.sharedByAccount;
    }
    
    public void setLogTransactions(final boolean logTransactions) {
        this.logTransactions = logTransactions;
    }
    
    public boolean getLogTransactions() {
        return this.logTransactions;
    }
    
    public void addDependsOn(final CurrencySystem dependsOnCurrencySystem) {
        this.dependsOn.add(dependsOnCurrencySystem);
    }
    
    public LinkedList<CurrencySystem> getDependsOn() {
        return this.dependsOn;
    }
    
    private void setMinBalance(final Float min) {
        this.minBalance = min;
    }
    
    public Float getMinBalance() {
        return this.minBalance;
    }
    
    private void setMaxBalance(final Float max) {
        if (this.maxBalance >= 0.0f) {
            this.maxBalance = max;
        }
    }
    
    public Float getMaxBalance() {
        return this.maxBalance;
    }
    
    public int getID() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void logTransaction(final Purse purse, final Float delta, final String reason) {
        final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("name", this.getName());
        map.put("reason", reason);
        map.put("value", (int)(Object)delta);
    }
    
    public Float getBalance(final Purse playerPurse) {
        final Purse purse = this.getSharedByAccount() ? playerPurse.getAccountPurse() : playerPurse;
        purse.getLock().lock();
        playerPurse.getLock().lock();
        try {
            Float balance = purse.balance(this.getName());
            for (final CurrencySystem dependsOnCurrencySystem : this.dependsOn) {
                final Float delta = dependsOnCurrencySystem.getBalance(playerPurse);
                if (delta > 0.0f && balance + delta >= this.getMinBalance() && (this.getMaxBalance() == 0.0f || balance + delta <= this.getMaxBalance())) {
                    final Float dependencyBalance = dependsOnCurrencySystem.updateBalance(playerPurse, -delta, "dependency withdrawal");
                    if (dependencyBalance == null) {
                        continue;
                    }
                    if (!dependsOnCurrencySystem.getLogTransactions()) {
                        dependsOnCurrencySystem.logTransaction(playerPurse, -delta, "dependency withdrawal");
                    }
                    balance = purse.updateBalance(delta, this.getName());
                    if (balance == null) {
                        continue;
                    }
                    this.logTransaction(playerPurse, delta, "dependency deposit");
                }
            }
            return balance;
        }
        finally {
            playerPurse.getLock().unlock();
            purse.getLock().unlock();
        }
    }
    
    public Float updateBalance(final Purse playerPurse, final Float delta, final String reason) {
        final Purse purse = this.getSharedByAccount() ? playerPurse.getAccountPurse() : playerPurse;
        purse.getLock().lock();
        playerPurse.getLock().lock();
        try {
            final Float balance = this.getBalance(playerPurse);
            Float returnBalance = null;
            if (balance + delta >= this.getMinBalance() && (this.getMaxBalance() == 0.0f || balance + delta <= this.getMaxBalance())) {
                returnBalance = purse.updateBalance(delta, this.getName());
            }
            if (returnBalance != null && this.getLogTransactions()) {
                this.logTransaction(playerPurse, delta, reason);
            }
            return returnBalance;
        }
        finally {
            playerPurse.getLock().unlock();
            purse.getLock().unlock();
        }
    }
    
    public String amountToString(final Float amount) {
        final String amountString = "" + (long)(Object)amount + " " + this.getName();
        return amountString;
    }
}
