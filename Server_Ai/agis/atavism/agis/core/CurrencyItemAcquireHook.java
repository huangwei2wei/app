// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.objects.ObjectTypes;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class CurrencyItemAcquireHook implements AcquireHook
{
    protected int currencyID;
    private static final long serialVersionUID = 1L;
    
    public CurrencyItemAcquireHook() {
    }
    
    public CurrencyItemAcquireHook(final int currencyID) {
        this.setCurrencyID(currencyID);
    }
    
    public void setCurrencyID(final int currencyID) {
        if (currencyID < 1) {
            throw new RuntimeException("CurrencyItemAcquireHook.setCurrencyID: bad currency");
        }
        this.currencyID = currencyID;
    }
    
    public int getCurrencyID() {
        return this.currencyID;
    }
    
    @Override
    public boolean acquired(final OID activatorOid, final AgisItem item) {
        if (Log.loggingDebug) {
            Log.debug("CurrencyItemAcquireHook.activate: activator=" + activatorOid + " item=" + item + " resource=" + this.currencyID);
        }
        if (WorldManagerClient.getObjectInfo(activatorOid).objType != ObjectTypes.player) {
            return false;
        }
        final int currencyAmount = item.getStackSize();
        AgisInventoryClient.alterCurrency(activatorOid, this.currencyID, currencyAmount);
        return true;
    }
    
    @Override
    public String toString() {
        return "CurrencyItemAcquireHook=" + this.currencyID;
    }
}
