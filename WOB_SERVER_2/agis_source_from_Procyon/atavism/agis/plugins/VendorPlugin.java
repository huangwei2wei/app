// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.msgsys.Message;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.plugins.InventoryClient;
import atavism.server.objects.Template;
import atavism.server.engine.Namespace;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import java.util.Iterator;
import atavism.server.plugins.BillingClient;
import java.io.Serializable;
import java.util.LinkedList;
import atavism.server.engine.OID;
import atavism.server.engine.Hook;
import atavism.server.util.Log;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.Logger;
import java.util.HashMap;
import atavism.server.engine.EnginePlugin;

public class VendorPlugin extends EnginePlugin
{
    private static HashMap<String, Integer> abilityList;
    private static final Logger log;
    private static Float tokenLowMark;
    private static Boolean virtualCurrency;
    private static String vendorUrl;
    
    static {
        VendorPlugin.abilityList = new HashMap<String, Integer>();
        log = new Logger("VendorPlugin");
        VendorPlugin.tokenLowMark = 0.0f;
        VendorPlugin.virtualCurrency = Boolean.FALSE;
        VendorPlugin.vendorUrl = null;
    }
    
    public VendorPlugin() {
        super("Vendor");
        this.setPluginType("Vendor");
    }
    
    public void onActivate() {
        super.onActivate();
        this.registerHooks();
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(VendorClient.MSG_TYPE_ITEM_PURCHASE);
        filter.addType(VendorClient.MSG_TYPE_VENDOR_GET_BALANCE);
        filter.addType(VendorClient.MSG_TYPE_VENDOR_INFO_REQ);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        this.registerPluginNamespace(VendorClient.NAMESPACE, (EnginePlugin.GenerateSubObjectHook)new VendorSubObjectHook());
        if (Log.loggingDebug) {
            VendorPlugin.log.debug("VendorPlugin activated");
        }
    }
    
    public void registerHooks() {
        this.getHookManager().addHook(VendorClient.MSG_TYPE_ITEM_PURCHASE, (Hook)new HandleItemPurchase());
        this.getHookManager().addHook(VendorClient.MSG_TYPE_VENDOR_GET_BALANCE, (Hook)new HandleVendorBalanceRequest());
        this.getHookManager().addHook(VendorClient.MSG_TYPE_VENDOR_INFO_REQ, (Hook)new HandleVendorInfoRequest());
    }
    
    public static LinkedList<HashMap<String, Serializable>> getVendorItems(final OID vendorOid) {
        final LinkedList<HashMap<String, Serializable>> returnList = new LinkedList<HashMap<String, Serializable>>();
        return returnList;
    }
    
    public void purchaseAbility(final OID playerOid, final String itemName) {
        synchronized (VendorPlugin.abilityList) {
            final int cost = VendorPlugin.abilityList.get(itemName);
            final Float balance = BillingClient.getTokenBalance(playerOid);
            if (balance >= cost) {
                CombatClient.addAbility(itemName, playerOid);
            }
        }
        // monitorexit(VendorPlugin.abilityList)
    }
    
    public HashMap<String, HashMap<String, Serializable>> getAbilities() {
        final HashMap<String, HashMap<String, Serializable>> returnList = new HashMap<String, HashMap<String, Serializable>>();
        for (final String abilityName : VendorPlugin.abilityList.keySet()) {
            Log.debug("VendorPlugin.GetAbilities - processing ability " + abilityName);
            final HashMap<String, String> ability = CombatClient.getAbilityInfo(abilityName);
            if (ability == null) {
                continue;
            }
            final HashMap<String, Serializable> abilityInfo = new HashMap<String, Serializable>();
            abilityInfo.put("name", ability.get("name"));
            abilityInfo.put("icon", ability.get("icon"));
            abilityInfo.put("description", ability.get("name"));
            abilityInfo.put("cost", VendorPlugin.abilityList.get(abilityName));
            returnList.put(ability.get("name"), abilityInfo);
        }
        Log.debug("VendorPlugin.GetAbilities - returning ability list " + returnList.toString());
        return returnList;
    }
    
    public static boolean purchaseItems(final OID playerOid, final OID vendorOid, final int itemID, final Integer itemCount) {
        final Template itemTemplate = ObjectManagerClient.getTemplate(itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
        if (itemTemplate == null) {
            VendorPlugin.log.error(String.valueOf(itemID) + " is not a valid item template id.");
            return false;
        }
        final Map<String, Serializable> props = (Map<String, Serializable>)itemTemplate.getSubMap(Namespace.AGISITEM);
        final Float itemValue = props.get("item_value");
        Float balance = 0.0f;
        balance = BillingClient.getTokenBalance(playerOid);
        Boolean rv = false;
        if (Log.loggingInfo) {
            Log.info("purchaseItems: " + itemID + " : " + itemCount + " : " + itemValue + " : " + balance);
        }
        OID itemOid = null;
        if (itemValue * itemCount <= balance) {
            for (int i = 0; i < itemCount; ++i) {
                if (Log.loggingDebug) {
                    Log.debug("CreateItemSubObjCommand: templ=" + itemID + ", generating object");
                }
                final Template overrideTemplate = new Template();
                overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)new Boolean(true));
                itemOid = ObjectManagerClient.generateObject(itemID, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
                rv = InventoryClient.addItem(playerOid, playerOid, playerOid, itemOid);
                if (Log.loggingDebug) {
                    Log.debug("CreateItemSubObjCommand: createitem: oid=" + itemOid + ", adding to inventory");
                }
                rv = InventoryClient.addItem(playerOid, playerOid, playerOid, itemOid);
                if (Log.loggingDebug) {
                    Log.debug("CommandPlugin: createitem: oid=" + itemOid + ", added, rv=" + rv);
                }
                WorldManagerClient.sendObjChatMsg(playerOid, 0, "Added item to inventory: " + itemID);
            }
            final Float newBalance = 0.0f;
            VendorClient.sendBalanceUpdate(playerOid, newBalance);
        }
        else {
            rv = false;
        }
        return rv;
    }
    
    public static EnginePlugin.SubObjData createVendorSubObject(final Template template, final Namespace namespace, final OID masterOid) {
        if (Log.loggingDebug) {
            VendorPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
        }
        if (masterOid == null) {
            VendorPlugin.log.error("GenerateSubObjectHook: no master oid");
            return null;
        }
        if (Log.loggingDebug) {
            VendorPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
        }
        final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(VendorClient.NAMESPACE);
        if (props == null) {
            Log.warn("GenerateSubObjectHook: no props in ns " + VendorClient.NAMESPACE);
            return null;
        }
        return new EnginePlugin.SubObjData();
    }
    
    public static void setLowTokenBalanceValue(final Float lowMark) {
        VendorPlugin.tokenLowMark = lowMark;
    }
    
    public static Float getLowTokenBalanceValue() {
        return VendorPlugin.tokenLowMark;
    }
    
    public static void allowVirtualCurrency(final Boolean allowed) {
        VendorPlugin.virtualCurrency = allowed;
    }
    
    public static Boolean useVirtualCurrency() {
        return VendorPlugin.virtualCurrency;
    }
    
    public static void setVendorUrl(final String url) {
        VendorPlugin.vendorUrl = url;
    }
    
    public static String getVendorUrl() {
        return VendorPlugin.vendorUrl;
    }
    
    public static void registerAbility(final String abilityName, final int tokenCost) {
        VendorPlugin.abilityList.put(abilityName, tokenCost);
    }
    
    public class VendorSubObjectHook extends EnginePlugin.GenerateSubObjectHook
    {
        public VendorSubObjectHook() {
            super((EnginePlugin)VendorPlugin.this);
        }
        
        public EnginePlugin.SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
            return VendorPlugin.createVendorSubObject(template, namespace, masterOid);
        }
    }
    
    public class HandleVendorBalanceRequest implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage message = (WorldManagerClient.ExtensionMessage)msg;
            VendorClient.sendBalanceUpdate(message.getSubject(), BillingClient.getTokenBalance(message.getSubject()));
            return true;
        }
    }
    
    public class HandleItemPurchase implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage purchaseMsg = (WorldManagerClient.ExtensionMessage)msg;
            final String itemName = (String)purchaseMsg.getProperty("itemName");
            final String itemType = purchaseMsg.getProperty("itemType").toString();
            Integer itemCount = 0;
            if (purchaseMsg.getProperty("itemCount") instanceof Integer) {
                itemCount = (Integer)purchaseMsg.getProperty("itemCount");
            }
            else {
                itemCount = new Integer((String)purchaseMsg.getProperty("itemCount"));
            }
            if (itemType.equals("ability")) {
                VendorPlugin.this.purchaseAbility(purchaseMsg.getSubject(), itemName);
            }
            return true;
        }
    }
    
    public class HandleVendorInfoRequest implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage infoMsg = (WorldManagerClient.ExtensionMessage)msg;
            return !infoMsg.getProperty("itemType").equals("ability") || VendorClient.sendVendorInfoMessage(infoMsg.getSubject(), VendorPlugin.this.getAbilities());
        }
    }
}
