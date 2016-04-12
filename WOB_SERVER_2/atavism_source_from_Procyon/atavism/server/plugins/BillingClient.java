// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.HashSet;
import java.io.Serializable;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;

public class BillingClient
{
    public static Namespace NAMESPACE;
    public static final MessageType MSG_TYPE_DECREMENT_TOKEN_BALANCE;
    public static final MessageType MSG_TYPE_GET_TOKEN_BALANCE;
    public static final MessageType MSG_TYPE_BILLING_BALANCE;
    public static final MessageType MSG_GET_PLAYER;
    
    public static Float decrementTokenBalance(final OID playerOid, final Float amount) {
        if (amount > 0.0) {
            Log.error("BillingClient.DecrementTokenBalance - Amount must be nonpositive.");
            return null;
        }
        final WorldManagerClient.ExtensionMessage updateRequest = new WorldManagerClient.ExtensionMessage(BillingClient.MSG_TYPE_DECREMENT_TOKEN_BALANCE, "ao.DECREMENT_TOKEN_BALANCE", playerOid);
        updateRequest.setProperty("amount", amount);
        final HashSet<Serializable> purchasedItems = new HashSet<Serializable>();
        updateRequest.setProperty("purchasedItems", purchasedItems);
        final Float updatedBalance = (Float)Engine.getAgent().sendRPCReturnObject(updateRequest);
        return updatedBalance;
    }
    
    public static Float getTokenBalance(final OID playerOid) {
        final WorldManagerClient.ExtensionMessage request = new WorldManagerClient.ExtensionMessage(BillingClient.MSG_TYPE_GET_TOKEN_BALANCE, "ao.GET_TOKEN_BALANCE", playerOid);
        final Float balance = (Float)Engine.getAgent().sendRPCReturnObject(request);
        return balance;
    }
    
    static {
        BillingClient.NAMESPACE = null;
        MSG_TYPE_DECREMENT_TOKEN_BALANCE = MessageType.intern("ao.DECREMENT_TOKEN_BALANCE");
        MSG_TYPE_GET_TOKEN_BALANCE = MessageType.intern("ao.GET_TOKEN_BALANCE");
        MSG_TYPE_BILLING_BALANCE = MessageType.intern("ao.BILLING_BALANCE");
        MSG_GET_PLAYER = MessageType.intern("ao.GET_PLAYER");
    }
}
