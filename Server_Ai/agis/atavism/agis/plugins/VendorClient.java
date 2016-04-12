// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.HashMap;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;

public class VendorClient
{
    public static Namespace NAMESPACE;
    public static final MessageType MSG_TYPE_ITEM_PURCHASE;
    public static final MessageType MSG_TYPE_VENDOR_INFO_REQ;
    public static final MessageType MSG_TYPE_VENDOR_INFO;
    public static final MessageType MSG_TYPE_VENDOR_BALANCE;
    public static final MessageType MSG_TYPE_VENDOR_GET_BALANCE;
    public static final String EXTMSG_TYPE_ITEM_PURCHASE = "ao.ITEM_PURCHASE";
    public static final String EXTMSG_TYPE_VENDOR_INFO = "ao.VENDOR_INFO";
    public static final String EXTMSG_TYPE_VENDOR_BALANCE = "ao.VENDOR_BALANCE";
    public static final String TMPL_IS_VENDOR = "isVendor";
    public static final String TMPL_VENDOR_ITEMS = "items";
    
    static {
        VendorClient.NAMESPACE = null;
        MSG_TYPE_ITEM_PURCHASE = MessageType.intern("ao.ITEM_PURCHASE");
        MSG_TYPE_VENDOR_INFO_REQ = MessageType.intern("ao.VENDOR_INFO_REQ");
        MSG_TYPE_VENDOR_INFO = MessageType.intern("ao.VENDOR_INFO");
        MSG_TYPE_VENDOR_BALANCE = MessageType.intern("ao.VENDOR_BALANCE");
        MSG_TYPE_VENDOR_GET_BALANCE = MessageType.intern("ao.VENDOR_GET_BALANCE");
    }
    
    public static boolean sendBalanceUpdate(final OID playerOid, Float balance) {
        final WorldManagerClient.TargetedExtensionMessage clientBalanceMsg = new WorldManagerClient.TargetedExtensionMessage();
        clientBalanceMsg.setExtensionType("ao.VENDOR_BALANCE");
        clientBalanceMsg.setSubject(playerOid);
        clientBalanceMsg.setTarget(playerOid);
        if (balance == null) {
            balance = 0.0f;
        }
        clientBalanceMsg.setProperty("balance", (Serializable)balance);
        if (VendorPlugin.useVirtualCurrency() && balance < VendorPlugin.getLowTokenBalanceValue()) {
            clientBalanceMsg.setProperty("lowbalance", (Serializable)Boolean.TRUE);
        }
        else {
            clientBalanceMsg.setProperty("lowbalance", (Serializable)Boolean.FALSE);
        }
        final int subs = Engine.getAgent().sendBroadcast((Message)clientBalanceMsg);
        Log.debug("Sending balance message to " + subs + " number of subscribers...");
        return true;
    }
    
    public static boolean sendVendorInfoMessage(final OID playerOid, final HashMap<String, HashMap<String, Serializable>> itemList) {
        final WorldManagerClient.TargetedExtensionMessage returnMsg = new WorldManagerClient.TargetedExtensionMessage();
        returnMsg.setExtensionType("ao.VENDOR_INFO");
        returnMsg.setSubject(playerOid);
        returnMsg.setTarget(playerOid);
        returnMsg.setProperty("itemList", (Serializable)itemList);
        returnMsg.setProperty("vendorurl", (Serializable)VendorPlugin.getVendorUrl());
        returnMsg.setProperty("usevcurrency", (Serializable)VendorPlugin.useVirtualCurrency());
        Engine.getAgent().sendBroadcast((Message)returnMsg);
        return true;
    }
}
