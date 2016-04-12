// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import java.util.LinkedList;
import java.util.List;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;

public class CurrencyClient
{
    public static Namespace NAMESPACE;
    public static final String EXT_MSG_TYPE_GET_BALANCE = "ao.CURRENCY_GET_BALANCE";
    public static final String EXT_MSG_TYPE_UPDATE_BALANCE = "ao.CURRENCY_UPDATE_BALANCE";
    public static final String EXT_MSG_TYPE_LIST_CURRENCIES = "ao.CURRENCY_LIST_CURRENCIES";
    public static final MessageType MSG_TYPE_GET_BALANCE;
    public static final MessageType MSG_TYPE_UPDATE_BALANCE;
    public static final MessageType MSG_TYPE_LIST_CURRENCIES;
    public static final String DEFAULT_CURRENCY = "default";
    public static final String REASON_LOOT = "loot";
    public static final String REASON_SELL = "sell";
    public static final String REASON_BUY = "buy";
    public static final String REASON_TRADE = "trade";
    public static final String REASON_ADMIN = "admin";
    public static final String REASON_NEW = "new";
    public static final String REASON_LOAD = "load";
    public static final String REASON_DEPENDENCY_WITHDRAWAL = "dependency withdrawal";
    public static final String REASON_DEPENDENCY_DEPOSIT = "dependency deposit";
    
    public static Float getBalance(final OID playerOid, final String currency) {
        try {
            final WorldManagerClient.ExtensionMessage balanceMessage = new WorldManagerClient.ExtensionMessage(CurrencyClient.MSG_TYPE_GET_BALANCE, "ao.CURRENCY_GET_BALANCE", playerOid);
            balanceMessage.setProperty("currency", (Serializable)currency);
            return (Float)Engine.getAgent().sendRPCReturnObject((Message)balanceMessage);
        }
        catch (Exception e) {
            Log.exception(e);
            return 0.0f;
        }
    }
    
    public static void updateBalance(final OID playerOid, final Float delta, final String currency, final String reason) {
        try {
            final WorldManagerClient.ExtensionMessage updateMessage = new WorldManagerClient.ExtensionMessage(CurrencyClient.MSG_TYPE_UPDATE_BALANCE, "ao.CURRENCY_UPDATE_BALANCE", playerOid);
            updateMessage.setProperty("currency", (Serializable)currency);
            updateMessage.setProperty("delta", (Serializable)delta);
            updateMessage.setProperty("reason", (Serializable)reason);
            Engine.getAgent().sendBroadcast((Message)updateMessage);
        }
        catch (Exception e) {
            Log.exception(e);
        }
    }
    
    public static List<String> listCurrencies(final OID playerOid) {
        try {
            final WorldManagerClient.ExtensionMessage listMessage = new WorldManagerClient.ExtensionMessage(CurrencyClient.MSG_TYPE_LIST_CURRENCIES, "ao.CURRENCY_LIST_CURRENCIES", playerOid);
            return (List<String>)Engine.getAgent().sendRPCReturnObject((Message)listMessage);
        }
        catch (Exception e) {
            Log.exception(e);
            return new LinkedList<String>();
        }
    }
    
    static {
        CurrencyClient.NAMESPACE = null;
        MSG_TYPE_GET_BALANCE = MessageType.intern("ao.CURRENCY_GET_BALANCE");
        MSG_TYPE_UPDATE_BALANCE = MessageType.intern("ao.CURRENCY_UPDATE_BALANCE");
        MSG_TYPE_LIST_CURRENCIES = MessageType.intern("ao.CURRENCY_LIST_CURRENCIES");
    }
}
