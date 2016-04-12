// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.msgsys.SubjectMessage;
import atavism.server.messages.PropertyMessage;
import atavism.server.objects.Template;
import java.util.ArrayList;
import atavism.agis.objects.AgisItem;
import java.util.LinkedList;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import java.util.HashMap;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.plugins.InventoryClient;
import atavism.agis.objects.AgisEquipSlot;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;

public class AgisInventoryClient
{
    public static final MessageType MSG_TYPE_AGIS_INV_FIND;
    public static final MessageType MSG_TYPE_TRADE_START_REQ;
    public static final MessageType MSG_TYPE_TRADE_START;
    public static final MessageType MSG_TYPE_TRADE_COMPLETE;
    public static final MessageType MSG_TYPE_TRADE_OFFER_REQ;
    public static final MessageType MSG_TYPE_TRADE_OFFER_UPDATE;
    public static final MessageType MSG_TYPE_ITEM_ACQUIRE_STATUS_CHANGE;
    public static final MessageType MSG_TYPE_ITEM_EQUIP_STATUS_CHANGE;
    public static final MessageType MSG_TYPE_REQ_OPEN_MOB;
    public static final MessageType MSG_TYPE_REMOVE_SPECIFIC_ITEM;
    public static final MessageType MSG_TYPE_REMOVE_GENERIC_ITEM;
    public static final MessageType MSG_TYPE_GET_SPECIFIC_ITEM_DATA;
    public static final MessageType MSG_TYPE_GET_GENERIC_ITEM_DATA;
    public static final MessageType MSG_TYPE_GENERATE_ITEM;
    public static final MessageType MSG_TYPE_PLACE_BAG;
    public static final MessageType MSG_TYPE_MOVE_BAG;
    public static final MessageType MSG_TYPE_REMOVE_BAG;
    public static final MessageType MSG_TYPE_MOVE_ITEM;
    public static final MessageType MSG_TYPE_LOOT_ITEM;
    public static final MessageType MSG_TYPE_LOOT_ALL;
    public static final MessageType MSG_TYPE_GENERATE_LOOT;
    public static final MessageType MSG_TYPE_GET_LOOT_LIST;
    public static final MessageType MSG_TYPE_GET_MERCHANT_LIST;
    public static final MessageType MSG_TYPE_PURCHASE_ITEM_FROM_MERCHANT;
    public static final MessageType MSG_TYPE_PURCHASE_ITEM;
    public static final MessageType MSG_TYPE_SELL_ITEM;
    public static final MessageType MSG_TYPE_PICKUP_ITEM;
    public static final MessageType MSG_TYPE_QUEST_ITEMS_LIST;
    public static final MessageType MSG_TYPE_SEND_INV_UPDATE;
    public static final MessageType MSG_TYPE_CHECK_CURRENCY;
    public static final MessageType MSG_TYPE_ALTER_CURRENCY;
    public static final MessageType MSG_TYPE_GET_SKINS;
    public static final MessageType MSG_TYPE_PURCHASE_SKIN;
    public static final MessageType MSG_TYPE_SET_WEAPON;
    public static final MessageType MSG_TYPE_SET_SKIN_COLOUR;
    public static final MessageType MSG_TYPE_GET_ACCOUNT_ITEM_COUNT;
    public static final MessageType MSG_TYPE_ALTER_ITEM_COUNT;
    public static final MessageType MSG_TYPE_USE_ACCOUNT_ITEM;
    public static final MessageType MSG_TYPE_ITEM_ACTIVATED;
    public static final MessageType MSG_TYPE_RETURNBOOLEAN_CHECK_COMPONENTS;
    public static final MessageType MSG_TYPE_GET_MAIL;
    public static final MessageType MSG_TYPE_MAIL_READ;
    public static final MessageType MSG_TYPE_MAIL_TAKE_ITEM;
    public static final MessageType MSG_TYPE_RETURN_MAIL;
    public static final MessageType MSG_TYPE_DELETE_MAIL;
    public static final MessageType MSG_TYPE_SEND_MAIL;
    public static final MessageType MSG_TYPE_SEND_PURCHASE_MAIL;
    public static final MessageType MSG_TYPE_RELOAD_ITEMS;
    public static final String INV_METHOD_SLOT = "slot";
    public static final String MSG_INV_SLOT = "inv_slot";
    public static final byte tradeSuccess = 1;
    public static final byte tradeCancelled = 2;
    public static final byte tradeFailed = 3;
    public static final byte tradeBusy = 4;
    
    static {
        MSG_TYPE_AGIS_INV_FIND = MessageType.intern("ao.AGIS_INV_FIND");
        MSG_TYPE_TRADE_START_REQ = MessageType.intern("ao.TRADE_START_REQ");
        MSG_TYPE_TRADE_START = MessageType.intern("ao.TRADE_START");
        MSG_TYPE_TRADE_COMPLETE = MessageType.intern("ao.TRADE_COMPLETE");
        MSG_TYPE_TRADE_OFFER_REQ = MessageType.intern("ao.TRADE_OFFER_REQ");
        MSG_TYPE_TRADE_OFFER_UPDATE = MessageType.intern("ao.TRADE_OFFER_UPDATE");
        MSG_TYPE_ITEM_ACQUIRE_STATUS_CHANGE = MessageType.intern("ao.ITEM_ACQUIRE_STATUS_CHANGE");
        MSG_TYPE_ITEM_EQUIP_STATUS_CHANGE = MessageType.intern("ao.ITEM_EQUIP_STATUS_CHANGE");
        MSG_TYPE_REQ_OPEN_MOB = MessageType.intern("inventory.REQ_OPEN_MOB");
        MSG_TYPE_REMOVE_SPECIFIC_ITEM = MessageType.intern("inventory.REMOVE_SPECIFIC_ITEM");
        MSG_TYPE_REMOVE_GENERIC_ITEM = MessageType.intern("inventory.REMOVE_GENERIC_ITEM");
        MSG_TYPE_GET_SPECIFIC_ITEM_DATA = MessageType.intern("inventory.GET_SPECIFIC_ITEM_DATA");
        MSG_TYPE_GET_GENERIC_ITEM_DATA = MessageType.intern("inventory.GET_GENERIC_ITEM_DATA");
        MSG_TYPE_GENERATE_ITEM = MessageType.intern("inventory.GENERATE_ITEM");
        MSG_TYPE_PLACE_BAG = MessageType.intern("inventory.PLACE_BAG");
        MSG_TYPE_MOVE_BAG = MessageType.intern("inventory.MOVE_BAG");
        MSG_TYPE_REMOVE_BAG = MessageType.intern("inventory.REMOVE_BAG");
        MSG_TYPE_MOVE_ITEM = MessageType.intern("inventory.MOVE_ITEM");
        MSG_TYPE_LOOT_ITEM = MessageType.intern("inventory.LOOT_ITEM");
        MSG_TYPE_LOOT_ALL = MessageType.intern("inventory.LOOT_ALL");
        MSG_TYPE_GENERATE_LOOT = MessageType.intern("inventory.GENERATE_LOOT");
        MSG_TYPE_GET_LOOT_LIST = MessageType.intern("inventory.GET_LOOT_LIST");
        MSG_TYPE_GET_MERCHANT_LIST = MessageType.intern("inventory.GET_MERCHANT_LIST");
        MSG_TYPE_PURCHASE_ITEM_FROM_MERCHANT = MessageType.intern("inventory.MSG_TYPE_PURCHASE_ITEM_FROM_MERCHANT");
        MSG_TYPE_PURCHASE_ITEM = MessageType.intern("inventory.PURCHASE_ITEM");
        MSG_TYPE_SELL_ITEM = MessageType.intern("inventory.SELL_ITEM");
        MSG_TYPE_PICKUP_ITEM = MessageType.intern("inventory.PICKUP_ITEM");
        MSG_TYPE_QUEST_ITEMS_LIST = MessageType.intern("inventory.QUEST_ITEMS_LIST");
        MSG_TYPE_SEND_INV_UPDATE = MessageType.intern("inventory.SEND_INV_UPDATE");
        MSG_TYPE_CHECK_CURRENCY = MessageType.intern("inventory.CHECK_CURRENCY");
        MSG_TYPE_ALTER_CURRENCY = MessageType.intern("inventory.ALTER_CURRENCY");
        MSG_TYPE_GET_SKINS = MessageType.intern("ao.GET_SKINS");
        MSG_TYPE_PURCHASE_SKIN = MessageType.intern("ao.PURCHASE_SKIN");
        MSG_TYPE_SET_WEAPON = MessageType.intern("ao.SET_WEAPON");
        MSG_TYPE_SET_SKIN_COLOUR = MessageType.intern("ao.SET_SKIN_COLOUR");
        MSG_TYPE_GET_ACCOUNT_ITEM_COUNT = MessageType.intern("ao.GET_ACCOUNT_ITEM_COUNT");
        MSG_TYPE_ALTER_ITEM_COUNT = MessageType.intern("ao.ALTER_ITEM_COUNT");
        MSG_TYPE_USE_ACCOUNT_ITEM = MessageType.intern("ao.USE_ACCOUNT_ITEM");
        MSG_TYPE_ITEM_ACTIVATED = MessageType.intern("ao.ITEM_ACTIVATED");
        MSG_TYPE_RETURNBOOLEAN_CHECK_COMPONENTS = MessageType.intern("ao.CHECK_COMPONENT");
        MSG_TYPE_GET_MAIL = MessageType.intern("inventory.GET_MAIL");
        MSG_TYPE_MAIL_READ = MessageType.intern("inventory.MAIL_READ");
        MSG_TYPE_MAIL_TAKE_ITEM = MessageType.intern("inventory.MAIL_TAKE_ITEM");
        MSG_TYPE_RETURN_MAIL = MessageType.intern("inventory.RETURN_MAIL");
        MSG_TYPE_DELETE_MAIL = MessageType.intern("inventory.DELETE_MAIL");
        MSG_TYPE_SEND_MAIL = MessageType.intern("inventory.SEND_MAIL");
        MSG_TYPE_SEND_PURCHASE_MAIL = MessageType.intern("inventory.SEND_PURCHASE_MAIL");
        MSG_TYPE_RELOAD_ITEMS = MessageType.intern("ao.RELOAD_ITEMS");
    }
    
    public static OID findItem(final OID mobOid, final AgisEquipSlot slot) {
        final InventoryClient.RemoveOrFindItemMessage msg = new InventoryClient.RemoveOrFindItemMessage(AgisInventoryClient.MSG_TYPE_AGIS_INV_FIND, mobOid, "slot", (Serializable)slot);
        final OID oid = Engine.getAgent().sendRPCReturnOID((Message)msg);
        Log.debug("findItem: got response");
        return oid;
    }
    
    public static void tradeStart(final OID requesterOid, final Long partnerOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("requesterOid", (Serializable)requesterOid);
        props.put("partnerOid", partnerOid);
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_TRADE_START_REQ, requesterOid, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void tradeUpdate(final OID requesterOid, final OID partnerOid, final LinkedList<OID> offerItems, final boolean accepted, final boolean cancelled) {
        Log.debug("AgisInventoryClient.tradeUpdate: requesterOid=" + requesterOid + " partnerOid=" + partnerOid + " offer=" + offerItems + " accepted=" + accepted + " cancelled=" + cancelled);
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("requesterOid", (Serializable)requesterOid);
        props.put("partnerOid", (Serializable)partnerOid);
        props.put("offerItems", offerItems);
        props.put("accepted", accepted);
        props.put("cancelled", cancelled);
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_TRADE_OFFER_REQ, requesterOid, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void itemAcquiredStatusChange(final OID oid, final AgisItem item, final boolean acquried) {
        final ItemAcquiredStatusMessage msg = new ItemAcquiredStatusMessage(oid, item, acquried);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("INV: ItemAcquiredStatusMessage sent");
    }
    
    public static void itemEquipStatusChanged(final OID oid, final AgisItem item, final boolean equipping, final String slot) {
        final ItemEquipStatusMessage msg = new ItemEquipStatusMessage(oid, item, equipping, slot);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("INV: ItemEquipStatusMessage sent");
    }
    
    public static void requestOpenMob(final OID mobOid, final OID playerOid) {
        final RequestOpenMobMessage msg = new RequestOpenMobMessage(mobOid, playerOid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void removeSpecificItem(final OID oid, final OID itemOid, final boolean removeStack, final int numToRemove) {
        final removeSpecificItemMessage msg = new removeSpecificItemMessage(oid, itemOid, removeStack, numToRemove);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client removeSpecificItemMessage hit 1");
    }
    
    public static void removeGenericItem(final OID oid, final int itemID, final boolean removeStack, final int numToRemove) {
        final removeGenericItemMessage msg = new removeGenericItemMessage(oid, itemID, removeStack, numToRemove);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client removeGenericItemMessage hit 1");
    }
    
    public static void getSpecificItemData(final OID oid, final OID targetOid, final ArrayList<Long> itemOids) {
        final getSpecificItemDataMessage msg = new getSpecificItemDataMessage(oid, targetOid, itemOids);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client getSpecificItemData hit 1");
    }
    
    public static Template getGenericItemData(final OID oid, final int itemID) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_GET_GENERIC_ITEM_DATA, (String)null, oid);
        msg.setProperty("itemID", (Serializable)itemID);
        msg.setProperty("dataType", (Serializable)"id");
        Log.debug("ITEM: client getGenericItemData hit 1");
        return (Template)Engine.getAgent().sendRPCReturnObject((Message)msg);
    }
    
    public static void generateItem(final OID oid, final int templateID, final String itemName, final int count, final HashMap<String, Serializable> itemProps) {
        final generateItemMessage msg = new generateItemMessage(oid, templateID, itemName, count, itemProps);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client generateItem hit 1");
    }
    
    public static void placeBag(final OID oid, final OID itemOid, final int bagSpotNum) {
        final placeBagMessage msg = new placeBagMessage(oid, itemOid, bagSpotNum);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client placeBag hit 1");
    }
    
    public static void moveBag(final OID oid, final int bagSpotNum, final int newSpotNum) {
        final moveBagMessage msg = new moveBagMessage(oid, bagSpotNum, newSpotNum);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client moveBagMessage hit 1");
    }
    
    public static void removeBag(final OID oid, final int bagSpotNum, final int containerId, final int slotId) {
        final removeBagMessage msg = new removeBagMessage(oid, bagSpotNum, containerId, slotId);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client removeBagMessage hit 1");
    }
    
    public static void lootItem(final OID oid, final OID itemOid, final OID mobOid) {
        final lootItemMessage msg = new lootItemMessage(oid, itemOid, mobOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client lootItemMessage hit 1");
    }
    
    public static void lootAll(final OID oid, final OID mobOid) {
        final lootAllMessage msg = new lootAllMessage(oid, mobOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client lootAllMessage hit 1");
    }
    
    public static void generateLoot(final OID oid) {
        final generateLootMessage msg = new generateLootMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client generateLootMessage hit 1");
    }
    
    public static void getLootList(final OID oid, final OID mobOid) {
        final getLootListMessage msg = new getLootListMessage(oid, mobOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client getLootListMessage hit 1");
    }
    
    public static void getMerchantList(final OID oid, final OID mobOid) {
        final getMerchantListMessage msg = new getMerchantListMessage(oid, mobOid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client getMerchantListMessage hit 1");
    }
    
    public static void purchaseItemFromMerchant(final OID oid, final OID mobOid, final int itemID, final int count) {
        final purchaseItemFromMerchantMessage msg = new purchaseItemFromMerchantMessage(oid, mobOid, itemID, count);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: purchaseItemFromMerchantMessage hit 1");
    }
    
    public static Boolean purchaseItem(final OID playerOid, final int itemID, final int count) {
        final purchaseItemMessage msg = new purchaseItemMessage(playerOid, itemID, count);
        final Boolean purchaseSuccessful = Engine.getAgent().sendRPCReturnBoolean((Message)msg);
        return purchaseSuccessful;
    }
    
    public static void getMail(final OID oid) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(oid);
        msg.setMsgType(AgisInventoryClient.MSG_TYPE_GET_MAIL);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client getMailMessage hit 1");
    }
    
    public static void mailRead(final OID oid, final OID mailID) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(oid);
        msg.setMsgType(AgisInventoryClient.MSG_TYPE_MAIL_READ);
        msg.setProperty("mailID", (Serializable)mailID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client mailReadMessage hit 1");
    }
    
    public static void takeMailItem(final OID oid, final OID mailID, final int itemPos) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(oid);
        msg.setMsgType(AgisInventoryClient.MSG_TYPE_MAIL_TAKE_ITEM);
        msg.setProperty("mailID", (Serializable)mailID);
        msg.setProperty("itemPos", (Serializable)itemPos);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client takeMailItemMessage hit 1");
    }
    
    public static void deleteMail(final OID oid, final OID mailID) {
        final WorldManagerClient.ExtensionMessage msg = new WorldManagerClient.ExtensionMessage(oid);
        msg.setMsgType(AgisInventoryClient.MSG_TYPE_DELETE_MAIL);
        msg.setProperty("mailID", (Serializable)mailID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client deleteMailMessage hit 1");
    }
    
    public static void sendMail(final OID oid, final String recipient, final String subject, final String message, final int currencyType, final int currencyAmount, final boolean CoD) {
        Log.debug("MAIL: client sendMailMessage hit 1");
        final WorldManagerClient.ExtensionMessage sendMailMessage = new WorldManagerClient.ExtensionMessage();
        sendMailMessage.setMsgType(AgisInventoryClient.MSG_TYPE_SEND_MAIL);
        sendMailMessage.setSubject(oid);
        sendMailMessage.setProperty("recipient", (Serializable)recipient);
        sendMailMessage.setProperty("subject", (Serializable)"Claim sold");
        sendMailMessage.setProperty("message", (Serializable)message);
        sendMailMessage.setProperty("numItems", (Serializable)0);
        sendMailMessage.setProperty("currencyType", (Serializable)currencyType);
        sendMailMessage.setProperty("currencyAmount", (Serializable)currencyAmount);
        sendMailMessage.setProperty("CoD", (Serializable)CoD);
        Engine.getAgent().sendBroadcast((Message)sendMailMessage);
    }
    
    public static void sendPurchaseMail(final OID oid, final int itemID) {
        final sendPurchaseMailMessage msg = new sendPurchaseMailMessage(oid, itemID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEM: client sendPurchaseMailMessage hit 1");
    }
    
    public static Boolean checkCurrency(final OID playerOid, final int currencyID, final int cost) {
        final checkCurrencyMessage msg = new checkCurrencyMessage(playerOid, currencyID, cost);
        final Boolean hasEnoughCurrency = Engine.getAgent().sendRPCReturnBoolean((Message)msg);
        return hasEnoughCurrency;
    }
    
    public static void alterCurrency(final OID oid, final int currencyType, final int delta) {
        final alterCurrencyMessage msg = new alterCurrencyMessage(oid, currencyType, delta);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("ITEMCLIENT: alterCurrencyMessage hit 1");
    }
    
    public static ArrayList<String> getAccountSkins(final OID oid) {
        final WorldManagerClient.ExtensionMessage eMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_GET_SKINS, (String)null, oid);
        return (ArrayList<String>)Engine.getAgent().sendRPCReturnObject((Message)eMsg);
    }
    
    public static int getAccountItemCount(final OID oid, final int itemID) {
        final WorldManagerClient.ExtensionMessage eMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_GET_ACCOUNT_ITEM_COUNT, (String)null, oid);
        eMsg.setProperty("itemID", (Serializable)itemID);
        return Engine.getAgent().sendRPCReturnInt((Message)eMsg);
    }
    
    public static Boolean checkComponents(final OID id, final LinkedList<Integer> components, final LinkedList<Integer> componentCounts) {
        final CheckComponentMessage msg = new CheckComponentMessage(id, components, componentCounts);
        final Boolean hasRequiredComponents = (Boolean)Engine.getAgent().sendRPCReturnObject((Message)msg);
        return hasRequiredComponents;
    }
    
    public static Boolean checkSpecificComponents(final OID id, final LinkedList<Integer> reqComponentIDs, final LinkedList<Integer> reqStackSizes, final LinkedList<Long> components, final LinkedList<Integer> componentCounts) {
        final CheckComponentMessage msg = new CheckComponentMessage(id, reqComponentIDs, reqStackSizes, components, componentCounts);
        final Boolean hasRequiredComponents = (Boolean)Engine.getAgent().sendRPCReturnObject((Message)msg);
        return hasRequiredComponents;
    }
    
    public static class ItemAcquiredStatusMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        private AgisItem item;
        private boolean acquired;
        
        public ItemAcquiredStatusMessage() {
        }
        
        public ItemAcquiredStatusMessage(final OID oid, final AgisItem item, final boolean acquried) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_ITEM_ACQUIRE_STATUS_CHANGE);
            this.setItem(item);
            this.setAcquired(acquried);
        }
        
        public AgisItem getItem() {
            return this.item;
        }
        
        public void setItem(final AgisItem item) {
            this.item = item;
        }
        
        public boolean getAcquired() {
            return this.acquired;
        }
        
        public void setAcquired(final boolean acquired) {
            this.acquired = acquired;
        }
    }
    
    public static class ItemEquipStatusMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        private AgisItem item;
        private boolean equipped;
        private String slot;
        
        public ItemEquipStatusMessage() {
        }
        
        public ItemEquipStatusMessage(final OID oid, final AgisItem item, final boolean equipping, final String slot) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_ITEM_EQUIP_STATUS_CHANGE);
            this.setItem(item);
            this.setEquipped(equipping);
            this.setSlot(slot);
        }
        
        public AgisItem getItem() {
            return this.item;
        }
        
        public void setItem(final AgisItem item) {
            this.item = item;
        }
        
        public boolean getEquipped() {
            return this.equipped;
        }
        
        public void setEquipped(final boolean equipped) {
            this.equipped = equipped;
        }
        
        public String getSlot() {
            return this.slot;
        }
        
        public void setSlot(final String slot) {
            this.slot = slot;
        }
    }
    
    public static class RequestOpenMobMessage extends SubjectMessage
    {
        OID playerOid;
        private static final long serialVersionUID = 1L;
        
        public RequestOpenMobMessage() {
            super(AgisInventoryClient.MSG_TYPE_REQ_OPEN_MOB);
            this.playerOid = null;
        }
        
        RequestOpenMobMessage(final OID npcOid, final OID playerOid) {
            super(AgisInventoryClient.MSG_TYPE_REQ_OPEN_MOB, npcOid);
            this.playerOid = null;
            this.setPlayerOid(playerOid);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
    }
    
    public static class removeSpecificItemMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public removeSpecificItemMessage() {
        }
        
        public removeSpecificItemMessage(final OID oid, final OID itemOid, final boolean removeStack, final int numToRemove) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_REMOVE_SPECIFIC_ITEM);
            this.setProperty("itemOid", (Serializable)itemOid);
            this.setProperty("removeStack", (Serializable)removeStack);
            this.setProperty("numToRemove", (Serializable)numToRemove);
            Log.debug("ITEM: client removeSpecificItemMessage hit 2");
        }
    }
    
    public static class removeGenericItemMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public removeGenericItemMessage() {
        }
        
        public removeGenericItemMessage(final OID oid, final int itemID, final boolean removeStack, final int numToRemove) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_REMOVE_GENERIC_ITEM);
            this.setProperty("itemID", (Serializable)itemID);
            this.setProperty("removeStack", (Serializable)removeStack);
            this.setProperty("numToRemove", (Serializable)numToRemove);
            Log.debug("ITEM: client removeGenericItemMessage hit 2");
        }
    }
    
    public static class getSpecificItemDataMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public getSpecificItemDataMessage() {
        }
        
        public getSpecificItemDataMessage(final OID oid, final OID targetOid, final ArrayList<Long> itemOids) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_GET_SPECIFIC_ITEM_DATA);
            this.setProperty("itemOids", (Serializable)itemOids);
            this.setProperty("targetOid", (Serializable)targetOid);
            Log.debug("ITEM: client getSpecificItemDataMessage hit 2");
        }
    }
    
    public static class getGenericItemDataMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public getGenericItemDataMessage() {
        }
        
        public getGenericItemDataMessage(final OID oid, final String itemName) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_GET_GENERIC_ITEM_DATA);
            this.setProperty("itemName", (Serializable)itemName);
            Log.debug("ITEM: client getGenericItemDataMessage hit 2");
        }
    }
    
    public static class generateItemMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public generateItemMessage() {
        }
        
        public generateItemMessage(final OID oid, final int templateID, final String itemName, final int count, final HashMap<String, Serializable> itemProps) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_GENERATE_ITEM);
            this.setProperty("itemID", (Serializable)templateID);
            this.setProperty("itemName", (Serializable)itemName);
            this.setProperty("count", (Serializable)count);
            this.setProperty("itemProps", (Serializable)itemProps);
            Log.debug("ITEM: client generateItemMessage hit 2");
        }
    }
    
    public static class placeBagMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public placeBagMessage() {
        }
        
        public placeBagMessage(final OID oid, final OID itemOid, final int bagSpotNum) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_PLACE_BAG);
            this.setProperty("itemOid", (Serializable)itemOid);
            this.setProperty("bagSpotNum", (Serializable)bagSpotNum);
            Log.debug("ITEM: client placeBagMessage hit 2");
        }
    }
    
    public static class moveBagMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public moveBagMessage() {
        }
        
        public moveBagMessage(final OID oid, final int bagSpotNum, final int newSpotNum) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_MOVE_BAG);
            this.setProperty("bagSpotNum", (Serializable)bagSpotNum);
            this.setProperty("newSpotNum", (Serializable)newSpotNum);
            Log.debug("ITEM: client moveBagMessage hit 2");
        }
    }
    
    public static class removeBagMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public removeBagMessage() {
        }
        
        public removeBagMessage(final OID oid, final int bagSpotNum, final int containerId, final int slotId) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_REMOVE_BAG);
            this.setProperty("bagSpotNum", (Serializable)bagSpotNum);
            this.setProperty("containerId", (Serializable)containerId);
            this.setProperty("slotId", (Serializable)slotId);
            Log.debug("ITEM: client removeBagMessage hit 2");
        }
    }
    
    public static class lootItemMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public lootItemMessage() {
        }
        
        public lootItemMessage(final OID oid, final OID itemOid, final OID mobOid) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_LOOT_ITEM);
            this.setProperty("mobOid", (Serializable)mobOid);
            this.setProperty("itemOid", (Serializable)itemOid);
            Log.debug("ITEM: client lootItemMessage hit 2");
        }
    }
    
    public static class lootAllMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public lootAllMessage() {
        }
        
        public lootAllMessage(final OID oid, final OID mobOid) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_LOOT_ALL);
            this.setProperty("mobOid", (Serializable)mobOid);
            Log.debug("ITEM: client lootAllMessage hit 2");
        }
    }
    
    public static class generateLootMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public generateLootMessage() {
        }
        
        public generateLootMessage(final OID oid) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_GENERATE_LOOT);
            Log.debug("ITEM: client generateLootMessage hit 2");
        }
    }
    
    public static class getLootListMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public getLootListMessage() {
        }
        
        public getLootListMessage(final OID oid, final OID mobOid) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_GET_LOOT_LIST);
            this.setProperty("mobOid", (Serializable)mobOid);
            Log.debug("ITEM: client getLootListMessage hit 2");
        }
    }
    
    public static class getMerchantListMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        OID playerOid;
        
        public getMerchantListMessage() {
            this.playerOid = null;
        }
        
        public getMerchantListMessage(final OID oid, final OID mobOid) {
            super(AgisInventoryClient.MSG_TYPE_GET_MERCHANT_LIST, mobOid);
            this.playerOid = null;
            this.setPlayerOid(oid);
            Log.debug("ITEM: client getMerchantListMessage hit 2");
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
    }
    
    public static class purchaseItemFromMerchantMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        OID playerOid;
        int itemID;
        int count;
        
        public purchaseItemFromMerchantMessage() {
            this.playerOid = null;
            this.itemID = -1;
            this.count = 1;
        }
        
        public purchaseItemFromMerchantMessage(final OID oid, final OID mobOid, final int itemID, final int count) {
            super(AgisInventoryClient.MSG_TYPE_PURCHASE_ITEM_FROM_MERCHANT, mobOid);
            this.playerOid = null;
            this.itemID = -1;
            this.count = 1;
            this.setPlayerOid(oid);
            this.setItemID(itemID);
            this.setCount(count);
            Log.debug("ITEM: purchaseItemFromMerchantMessage hit 2");
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID playerOid) {
            this.playerOid = playerOid;
        }
        
        public int getItemID() {
            return this.itemID;
        }
        
        public void setItemID(final int itemID) {
            this.itemID = itemID;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public void setCount(final int count) {
            this.count = count;
        }
    }
    
    public static class purchaseItemMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        int itemID;
        int count;
        
        public purchaseItemMessage() {
            this.itemID = -1;
            this.count = 1;
        }
        
        public purchaseItemMessage(final OID oid, final int itemID, final int count) {
            super(oid);
            this.itemID = -1;
            this.count = 1;
            this.setMsgType(AgisInventoryClient.MSG_TYPE_PURCHASE_ITEM);
            this.setItemID(itemID);
            this.setCount(count);
            Log.debug("ITEM: client getLootListMessage hit 2");
        }
        
        public int getItemID() {
            return this.itemID;
        }
        
        public void setItemID(final int itemID) {
            this.itemID = itemID;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public void setCount(final int count) {
            this.count = count;
        }
    }
    
    public static class QuestItemsListMessage extends SubjectMessage
    {
        HashMap<Integer, Integer> itemList;
        private static final long serialVersionUID = 1L;
        
        public QuestItemsListMessage() {
            super(AgisInventoryClient.MSG_TYPE_QUEST_ITEMS_LIST);
        }
        
        public QuestItemsListMessage(final OID playerOid, final HashMap<Integer, Integer> itemList) {
            super(AgisInventoryClient.MSG_TYPE_QUEST_ITEMS_LIST, playerOid);
            this.setItemList(itemList);
        }
        
        public HashMap<Integer, Integer> getItemList() {
            return this.itemList;
        }
        
        public void setItemList(final HashMap<Integer, Integer> itemList) {
            this.itemList = itemList;
        }
    }
    
    public static class SendInventoryUpdateMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public SendInventoryUpdateMessage() {
        }
        
        public SendInventoryUpdateMessage(final OID oid) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_SEND_INV_UPDATE);
            Log.debug("ITEM: client SendInventoryUpdateMessage hit 2");
        }
    }
    
    public static class sendPurchaseMailMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public sendPurchaseMailMessage() {
        }
        
        public sendPurchaseMailMessage(final OID oid, final int itemID) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_SEND_PURCHASE_MAIL);
            this.setProperty("itemID", (Serializable)itemID);
            Log.debug("ITEM: client sendPurchaseMailMessage hit 2");
        }
    }
    
    public static class checkCurrencyMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        int currencyID;
        int count;
        
        public checkCurrencyMessage() {
            this.currencyID = -1;
            this.count = 1;
        }
        
        public checkCurrencyMessage(final OID oid, final int currencyID, final int count) {
            super(oid);
            this.currencyID = -1;
            this.count = 1;
            this.setMsgType(AgisInventoryClient.MSG_TYPE_CHECK_CURRENCY);
            this.setCurrencyID(currencyID);
            this.setCount(count);
            Log.debug("ITEM: client checkCurrencyMessage hit 2");
        }
        
        public int getCurrencyID() {
            return this.currencyID;
        }
        
        public void setCurrencyID(final int currencyID) {
            this.currencyID = currencyID;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public void setCount(final int count) {
            this.count = count;
        }
    }
    
    public static class alterCurrencyMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        protected int currencyType;
        protected int delta;
        
        public alterCurrencyMessage() {
        }
        
        public alterCurrencyMessage(final OID oid, final int currencyType, final int delta) {
            super(oid);
            this.setMsgType(AgisInventoryClient.MSG_TYPE_ALTER_CURRENCY);
            this.setCurrencyType(currencyType);
            this.setDelta(delta);
            Log.debug("ITEMCLIENT: alterCurrencyMessage hit 2");
        }
        
        public int getCurrencyType() {
            return this.currencyType;
        }
        
        public void setCurrencyType(final int currencyType) {
            this.currencyType = currencyType;
        }
        
        public int getDelta() {
            return this.delta;
        }
        
        public void setDelta(final int delta) {
            this.delta = delta;
        }
    }
    
    public static class CheckComponentMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        public LinkedList<Integer> _reqComponents;
        public LinkedList<Integer> _reqStackSizes;
        public LinkedList<Long> _components;
        public LinkedList<Integer> _componentCounts;
        public OID _subject;
        public boolean gridSystem;
        
        public CheckComponentMessage() {
            super(AgisInventoryClient.MSG_TYPE_RETURNBOOLEAN_CHECK_COMPONENTS);
            this.gridSystem = false;
        }
        
        public CheckComponentMessage(final OID subject, final LinkedList<Integer> componentIDs, final LinkedList<Integer> stackSize, final LinkedList<Long> components, final LinkedList<Integer> componentCounts) {
            super(AgisInventoryClient.MSG_TYPE_RETURNBOOLEAN_CHECK_COMPONENTS, subject);
            this.gridSystem = false;
            this._reqComponents = componentIDs;
            this._reqStackSizes = stackSize;
            this._components = components;
            this._componentCounts = componentCounts;
            this._subject = subject;
            this.gridSystem = true;
        }
        
        public CheckComponentMessage(final OID subject, final LinkedList<Integer> components, final LinkedList<Integer> componentCounts) {
            super(AgisInventoryClient.MSG_TYPE_RETURNBOOLEAN_CHECK_COMPONENTS, subject);
            this.gridSystem = false;
            this._reqComponents = components;
            this._reqStackSizes = componentCounts;
            this._subject = subject;
            this.gridSystem = false;
        }
    }
}
