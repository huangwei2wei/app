// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.util.Log;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class CurrencyItemActivateHook implements ActivateHook
{
    protected int currencyID;
    private static final long serialVersionUID = 1L;
    
    public CurrencyItemActivateHook() {
    }
    
    public CurrencyItemActivateHook(final int currencyID) {
        this.setCurrencyID(currencyID);
    }
    
    public void setCurrencyID(final int currencyID) {
        if (currencyID < 1) {
            throw new RuntimeException("CurrencyItemActivateHook.setCurrencyID: bad currency");
        }
        this.currencyID = currencyID;
    }
    
    public int getCurrencyID() {
        return this.currencyID;
    }
    
    @Override
    public boolean activate(final OID activatorOid, final AgisItem item, final OID targetOid) {
        if (Log.loggingDebug) {
            Log.debug("CurrencyItemActivateHook.activate: activator=" + activatorOid + " item=" + item + " ability=" + this.currencyID + " target=" + targetOid);
        }
        final int currencyAmount = item.getStackSize();
        AgisInventoryClient.alterCurrency(activatorOid, this.currencyID, currencyAmount);
        AgisInventoryClient.removeSpecificItem(activatorOid, item.getOid(), true, 1);
        return true;
    }
    
    @Override
    public String toString() {
        return "CurrencyItemActivateHook.currency=" + this.currencyID;
    }
}
