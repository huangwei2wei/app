// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.Collection;
import atavism.msgsys.ResponseMessage;
import atavism.agis.objects.CoordinatedEffect;
import atavism.server.objects.ObjectTypes;
import java.util.concurrent.locks.Lock;
import atavism.server.util.ObjectLockManager;
import atavism.server.objects.PermissionCallback;
import atavism.agis.objects.AgisEquipSlot;
import atavism.server.messages.PropertyMessage;
import atavism.server.plugins.BillingClient;
import atavism.agis.objects.Mail;
import atavism.agis.util.EquipHelper;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import atavism.server.plugins.InventoryClient;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.msgsys.Message;
import atavism.msgsys.SubjectFilter;
import atavism.server.objects.AOObject;
import atavism.server.plugins.ObjectManagerPlugin;
import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import atavism.server.objects.Bag;
import atavism.agis.objects.InventoryInfo;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.server.engine.Namespace;
import atavism.agis.objects.AgisItem;
import atavism.agis.objects.Currency;
import atavism.server.util.Log;
import atavism.agis.core.Agis;
import atavism.agis.objects.LootTable;
import java.util.Iterator;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.objects.Template;
import atavism.agis.database.MobDatabase;
import atavism.agis.database.ItemDatabase;
import atavism.agis.database.ContentDatabase;
import atavism.server.util.AORuntimeException;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.messages.LogoutMessage;
import atavism.server.messages.LoginMessage;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Hook;
import atavism.server.util.Logger;
import atavism.agis.database.AccountDatabase;
import java.util.Random;
import atavism.agis.objects.ItemWeight;
import java.util.ArrayList;
import java.util.HashMap;
import atavism.server.objects.DisplayContext;
import atavism.agis.core.TradeSession;
import atavism.server.engine.OID;
import java.util.Map;
import atavism.msgsys.MessageCallback;
import atavism.server.plugins.InventoryPlugin;

public class AgisInventoryPlugin extends InventoryPlugin implements MessageCallback
{
    Map<OID, TradeSession> tradeSessionMap;
    Map<OID, DisplayContext> baseDCMap;
    public static final String EQUIP_MAP_PROP = "equipMap";
    public static HashMap<String, Float> equipmentSlots;
    public static ArrayList<ItemWeight> itemPrefixes;
    public static ArrayList<ItemWeight> itemSuffixes;
    public static Random random;
    public static final float sellFactor = 4.0f;
    public int INVENTORY_BAG_COUNT;
    public int INVENTORY_FIRST_BAG_SIZE;
    public int INVENTORY_OTHER_BAG_SIZE;
    public static int MAIL_ATTACHMENT_COUNT;
    public static int MAIL_LIFE_DAYS;
    public static int MAIL_COD_LIFE_DAYS;
    public static AccountDatabase aDB;
    static final Logger log;
    
    static {
        AgisInventoryPlugin.equipmentSlots = new HashMap<String, Float>();
        AgisInventoryPlugin.itemPrefixes = new ArrayList<ItemWeight>();
        AgisInventoryPlugin.itemSuffixes = new ArrayList<ItemWeight>();
        AgisInventoryPlugin.random = new Random();
        AgisInventoryPlugin.MAIL_ATTACHMENT_COUNT = 10;
        AgisInventoryPlugin.MAIL_LIFE_DAYS = 30;
        AgisInventoryPlugin.MAIL_COD_LIFE_DAYS = 3;
        log = new Logger("AgisInventoryPlugin");
    }
    
    public AgisInventoryPlugin() {
        this.tradeSessionMap = new HashMap<OID, TradeSession>();
        this.baseDCMap = new HashMap<OID, DisplayContext>();
        this.INVENTORY_BAG_COUNT = 4;
        this.INVENTORY_FIRST_BAG_SIZE = 16;
        this.INVENTORY_OTHER_BAG_SIZE = 0;
    }
    
    public void onActivate() {
        super.onActivate();
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_AGIS_INV_FIND, (Hook)new AgisFindItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_TRADE_START_REQ, (Hook)new TradeStartReqHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_TRADE_OFFER_REQ, (Hook)new TradeOfferReqHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, (Hook)new DespawnedHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_REMOVE_GENERIC_ITEM, (Hook)new RemoveGenericItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_REMOVE_SPECIFIC_ITEM, (Hook)new RemoveSpecificItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_GET_SPECIFIC_ITEM_DATA, (Hook)new GetSpecificItemDataHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_GET_GENERIC_ITEM_DATA, (Hook)new GetGenericItemDataHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_GENERATE_ITEM, (Hook)new GenerateItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_PLACE_BAG, (Hook)new PlaceBagHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_MOVE_BAG, (Hook)new MoveBagHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_REMOVE_BAG, (Hook)new RemoveBagHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_MOVE_ITEM, (Hook)new MoveItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_LOOT_ITEM, (Hook)new LootItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_LOOT_ALL, (Hook)new LootAllHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_GENERATE_LOOT, (Hook)new GenerateLootHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_GET_LOOT_LIST, (Hook)new GetLootListHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_GET_MERCHANT_LIST, (Hook)new GetMerchantListHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_PURCHASE_ITEM, (Hook)new PurchaseItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_SELL_ITEM, (Hook)new SellItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_PICKUP_ITEM, (Hook)new PickupItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_SEND_INV_UPDATE, (Hook)new SendInventoryUpdateHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_GET_MAIL, (Hook)new GetMailHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_MAIL_READ, (Hook)new MailReadHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_MAIL_TAKE_ITEM, (Hook)new TakeMailItemHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_RETURN_MAIL, (Hook)new ReturnMailHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_DELETE_MAIL, (Hook)new DeleteMailHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_SEND_MAIL, (Hook)new SendMailHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_SEND_PURCHASE_MAIL, (Hook)new SendPurchaseMailHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_CHECK_CURRENCY, (Hook)new CheckCurrencyHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_ALTER_CURRENCY, (Hook)new AlterCurrencyHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT, (Hook)new AlterItemCountHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_USE_ACCOUNT_ITEM, (Hook)new UseAccountItemHook());
        this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CATEGORY_UPDATED, (Hook)new CategoryUpdatedHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_RELOAD_ITEMS, (Hook)new ReloadItemsHook());
        this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_RETURNBOOLEAN_CHECK_COMPONENTS, (Hook)new CheckComponentHook());
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook)new LoginHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook)new LogoutHook());
        try {
            final MessageTypeFilter filterNeedsResponse = new MessageTypeFilter();
            filterNeedsResponse.addType(AgisInventoryClient.MSG_TYPE_AGIS_INV_FIND);
            filterNeedsResponse.addType(AgisInventoryClient.MSG_TYPE_GET_SKINS);
            filterNeedsResponse.addType(AgisInventoryClient.MSG_TYPE_GET_ACCOUNT_ITEM_COUNT);
            filterNeedsResponse.addType(AgisInventoryClient.MSG_TYPE_GET_GENERIC_ITEM_DATA);
            filterNeedsResponse.addType(AgisInventoryClient.MSG_TYPE_RETURNBOOLEAN_CHECK_COMPONENTS);
            filterNeedsResponse.addType(AgisInventoryClient.MSG_TYPE_CHECK_CURRENCY);
            filterNeedsResponse.addType(AgisInventoryClient.MSG_TYPE_PURCHASE_ITEM);
            Engine.getAgent().createSubscription((IFilter)filterNeedsResponse, (MessageCallback)this, 8);
            final MessageTypeFilter filterNoResponse = new MessageTypeFilter();
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_TRADE_START_REQ);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_TRADE_OFFER_REQ);
            filterNoResponse.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_REMOVE_GENERIC_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_REMOVE_SPECIFIC_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_GET_SPECIFIC_ITEM_DATA);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_GENERATE_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_PLACE_BAG);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_MOVE_BAG);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_REMOVE_BAG);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_MOVE_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_LOOT_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_LOOT_ALL);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_GENERATE_LOOT);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_GET_LOOT_LIST);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_GET_MERCHANT_LIST);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_SELL_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_PICKUP_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_SEND_INV_UPDATE);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_GET_MAIL);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_MAIL_READ);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_MAIL_TAKE_ITEM);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_RETURN_MAIL);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_DELETE_MAIL);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_SEND_MAIL);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_SEND_PURCHASE_MAIL);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_ALTER_CURRENCY);
            filterNoResponse.addType(AgisMobClient.MSG_TYPE_CATEGORY_UPDATED);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_RELOAD_ITEMS);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_PURCHASE_SKIN);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_SET_WEAPON);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_SET_SKIN_COLOUR);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT);
            filterNoResponse.addType(AgisInventoryClient.MSG_TYPE_USE_ACCOUNT_ITEM);
            Engine.getAgent().createSubscription((IFilter)filterNoResponse, (MessageCallback)this);
            final MessageTypeFilter filter2 = new MessageTypeFilter();
            filter2.addType(LoginMessage.MSG_TYPE_LOGIN);
            filter2.addType(LogoutMessage.MSG_TYPE_LOGOUT);
            Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this, 8);
        }
        catch (Exception e) {
            throw new AORuntimeException("activate failed", (Throwable)e);
        }
        final ContentDatabase cDB = new ContentDatabase(false);
        final String bagCount = cDB.loadGameSetting("PLAYER_BAG_COUNT");
        if (bagCount != null) {
            this.INVENTORY_BAG_COUNT = Integer.parseInt(bagCount);
        }
        final String firstBagSize = cDB.loadGameSetting("PLAYER_DEFAULT_BAG_SIZE");
        if (firstBagSize != null) {
            this.INVENTORY_FIRST_BAG_SIZE = Integer.parseInt(firstBagSize);
        }
        final ItemDatabase iDB = new ItemDatabase(false);
        this.loadItemsFromDatabase(iDB);
        iDB.close();
        final MobDatabase mDB = new MobDatabase(false);
        this.loadLootTables(mDB);
        this.loadCurrencies(mDB);
        mDB.close();
        AgisInventoryPlugin.aDB = new AccountDatabase();
    }
    
    public void loadItemsFromDatabase(final ItemDatabase iDB) {
        final ArrayList<Template> items = iDB.loadItemTemplates();
        for (final Template tmpl : items) {
            ObjectManagerClient.registerTemplate(tmpl);
        }
    }
    
    private void loadLootTables(final MobDatabase mDB) {
        final HashMap<Integer, LootTable> lootTables = mDB.loadLootTables(-1);
        for (final LootTable lTbl : lootTables.values()) {
            Agis.LootTableManager.register(lTbl.getID(), (Object)lTbl);
            Log.debug("LOOT: loaded loot Table: [" + lTbl.getName() + "]");
        }
    }
    
    private void loadCurrencies(final MobDatabase mDB) {
        final ArrayList<Currency> currencies = mDB.loadCurrencies(-1);
        for (final Currency currency : currencies) {
            Agis.CurrencyManager.register(currency.getCurrencyID(), (Object)currency);
            Log.debug("CURRENCY: currency Table: [" + currency.getCurrencyID() + ":" + currency.getCurrencyName() + "]");
        }
    }
    
    public static AgisItem getAgisItem(final OID oid) {
        return (AgisItem)EntityManager.getEntityByNamespace(oid, Namespace.AGISITEM);
    }
    
    public static void registerAgisItem(final AgisItem item) {
        EntityManager.registerEntityByNamespace((Entity)item, Namespace.AGISITEM);
    }
    
    public static InventoryInfo getInventoryInfo(final OID oid) {
        return (InventoryInfo)EntityManager.getEntityByNamespace(oid, Namespace.BAG);
    }
    
    public static void registerInventoryInfo(final InventoryInfo iInfo) {
        EntityManager.registerEntityByNamespace((Entity)iInfo, Namespace.BAG);
    }
    
    public static Bag getBag(final OID activatorOid) {
        return (Bag)EntityManager.getEntityByNamespace(activatorOid, Namespace.BAG);
    }
    
    public static void registerBag(final Bag bag) {
        EntityManager.registerEntityByNamespace((Entity)bag, Namespace.BAG);
    }
    
    public EnginePlugin.SubObjData createInvSubObj(final OID mobOid, final Template template) {
        final InventoryInfo iInfo = new InventoryInfo(mobOid);
        iInfo.setName(template.getName());
        iInfo.setCurrentCategory(1);
        final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(Namespace.BAG);
        if (props == null) {
            Log.warn("createInvSubObj: no props in ns " + Namespace.BAG);
            return null;
        }
        Boolean persistent = (Boolean)template.get(Namespace.OBJECT_MANAGER, ":persistent");
        if (persistent == null) {
            persistent = false;
        }
        iInfo.setPersistenceFlag((boolean)persistent);
        for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
            final String key = entry.getKey();
            final Serializable value = entry.getValue();
            if (!key.startsWith(":")) {
                iInfo.setProperty(key, value);
            }
        }
        registerInventoryInfo(iInfo);
        this.createBags(iInfo, mobOid);
        for (final int currencyID : Agis.CurrencyManager.keyList()) {
            if (!iInfo.getCurrentCurrencies().containsKey(currencyID)) {
                iInfo.addCurrency(currencyID, 0);
            }
        }
        if (persistent) {
            Engine.getPersistenceManager().persistEntity((Entity)iInfo);
        }
        final String invItems = props.get(":inv_items");
        return new EnginePlugin.SubObjData(Namespace.WORLD_MANAGER, (Hook)new CreateInventoryHook(mobOid, invItems));
    }
    
    protected void createBags(final InventoryInfo iInfo, final OID mobOid) {
        final OID[] bags = new OID[this.INVENTORY_BAG_COUNT + 1];
        Bag subBag = this.createSubBag(iInfo.getOid(), 0, this.INVENTORY_FIRST_BAG_SIZE);
        if (subBag == null) {
            return;
        }
        bags[0] = subBag.getOid();
        for (int subBagNum = 1; subBagNum < this.INVENTORY_BAG_COUNT; ++subBagNum) {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("createInvSubObj: creating sub bag, moboid=" + mobOid + ", bag pos=" + subBagNum);
            }
            subBag = this.createSubBag(iInfo.getOid(), subBagNum, this.INVENTORY_OTHER_BAG_SIZE);
            if (subBag == null) {
                return;
            }
            bags[subBagNum] = subBag.getOid();
        }
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("createInvSubObj: creating sub bag, moboid=" + mobOid + ", bag pos=" + this.INVENTORY_BAG_COUNT);
        }
        subBag = this.createSubBag(iInfo.getOid(), this.INVENTORY_BAG_COUNT, 15);
        if (subBag == null) {
            return;
        }
        bags[this.INVENTORY_BAG_COUNT] = subBag.getOid();
        iInfo.setBags(bags);
    }
    
    protected void createInventoryItems(final OID masterOid, final InventoryInfo iInfo, final String invItems) {
        String[] split;
        for (int length = (split = invItems.split(";")).length, i = 0; i < length; ++i) {
            String itemName = split[i];
            boolean equip = false;
            itemName = itemName.trim();
            if (!itemName.isEmpty()) {
                if (itemName.startsWith("*")) {
                    itemName = itemName.substring(1);
                    equip = true;
                }
                final int itemID = Integer.parseInt(itemName);
                if (Log.loggingDebug) {
                    AgisInventoryPlugin.log.debug("CreateInventoryHook.processMessage: creating item=" + itemName + " equip=" + equip);
                }
                final Template itemTemplate = new Template();
                itemTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)iInfo.getPersistenceFlag());
                if (Log.loggingDebug) {
                    AgisInventoryPlugin.log.debug("CreateInventoryHook.processMessage: generating item=" + itemID);
                }
                final OID itemOid = ObjectManagerClient.generateObject(itemID, ObjectManagerPlugin.ITEM_TEMPLATE, itemTemplate);
                if (Log.loggingDebug) {
                    AgisInventoryPlugin.log.debug("CreateInventoryHook.processMessage: created item=" + itemOid);
                }
                this.addItem(masterOid, iInfo.getOid(), itemOid);
                if (Log.loggingDebug) {
                    AgisInventoryPlugin.log.debug("CreateInventoryHook.processMessage: added item to inv=" + itemOid);
                }
                if (equip) {
                    final AgisItem item = getAgisItem(itemOid);
                    this.equipItem(item, masterOid, false);
                }
            }
        }
    }
    
    private Bag createSubBag(final OID ownerOid, final int parentBagSlotNum, final int numSlots) {
        final Bag bag = new Bag();
        bag.setOid(Engine.getOIDManager().getNextOid());
        bag.setNumSlots(numSlots);
        bag.setProperty("inv.backref", (Serializable)ownerOid);
        registerBag(bag);
        final SubjectFilter wmFilter = new SubjectFilter(bag.getOid());
        wmFilter.addType(EnginePlugin.MSG_TYPE_SET_PROPERTY);
        wmFilter.addType(EnginePlugin.MSG_TYPE_GET_PROPERTY);
        return bag;
    }
    
    protected void sendInvUpdate(final OID mobOid) {
        this.sendEquippedInvUpdate(mobOid);
        this.sendBagInvUpdate(mobOid);
    }
    
    protected void sendBagInvUpdate(final OID mobOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "BagInventoryUpdate");
        final HashMap<Integer, Integer> itemCounts = new HashMap<Integer, Integer>();
        int numItems = 0;
        final InventoryInfo iInfo = getInventoryInfo(mobOid);
        if (iInfo == null) {
            return;
        }
        final OID[] items = iInfo.getBags();
        props.put("numBags", this.INVENTORY_BAG_COUNT);
        for (int bagPos = 0; bagPos < this.INVENTORY_BAG_COUNT; ++bagPos) {
            final OID subBagOid = items[bagPos];
            if (subBagOid == null) {
                AgisInventoryPlugin.log.error("sendInvUpdate: sub bag oid is null");
            }
            else {
                final Bag subBag = getBag(subBagOid);
                if (subBag == null) {
                    AgisInventoryPlugin.log.error("sendInvUpdate: sub bag obj is null");
                    props.put("bag_" + bagPos + "ID", 0);
                    props.put("bag_" + bagPos + "Name", "");
                    props.put("bag_" + bagPos + "NumSlots", 0);
                }
                else {
                    props.put("bag_" + bagPos + "ID", subBag.getID());
                    props.put("bag_" + bagPos + "Name", subBag.getName());
                    props.put("bag_" + bagPos + "NumSlots", subBag.getNumSlots());
                    final OID[] itemsInBag = subBag.getItemsList();
                    for (int itemPos = 0; itemPos < itemsInBag.length; ++itemPos) {
                        boolean itemExists = true;
                        final OID oid = itemsInBag[itemPos];
                        if (oid == null) {
                            itemExists = false;
                        }
                        final AgisItem item = getAgisItem(oid);
                        if (item == null) {
                            Log.warn("sendInvUpdateHelper: item is null, oid=" + oid);
                            itemExists = false;
                        }
                        if (itemExists) {
                            if (Log.loggingDebug) {
                                AgisInventoryPlugin.log.debug("sendInvUpdateHelper: adding bagNum=" + bagPos + ", bagPos=" + itemPos + ", itemOid=" + oid + ", itemName=" + item.getName() + ",icon=" + item.getIcon());
                            }
                            props.put("item_" + numItems + "Name", item.getName());
                            props.put("item_" + numItems + "BaseName", item.getProperty("baseName"));
                            props.put("item_" + numItems + "Id", (Serializable)item.getOid());
                            props.put("item_" + numItems + "Count", item.getStackSize());
                            props.put("item_" + numItems + "BagNum", bagPos);
                            props.put("item_" + numItems + "SlotNum", itemPos);
                            if (item.getProperty("energyCost") != null) {
                                props.put("item_" + numItems + "EnergyCost", item.getProperty("energyCost"));
                            }
                            else {
                                props.put("item_" + numItems + "EnergyCost", 0);
                            }
                            if (item.getProperty("resistanceStats") != null) {
                                int numResist = 0;
                                final HashMap<String, Integer> resistances = (HashMap<String, Integer>)item.getProperty("resistanceStats");
                                for (final String resistance : resistances.keySet()) {
                                    props.put("item_" + numItems + "Resist_" + numResist + "Name", resistance);
                                    props.put("item_" + numItems + "Resist_" + numResist + "Value", resistances.get(resistance));
                                    ++numResist;
                                }
                                props.put("item_" + numItems + "NumResistances", numResist);
                            }
                            else {
                                props.put("item_" + numItems + "NumResistances", 0);
                            }
                            if (item.getProperty("bonusStats") != null) {
                                int numStats = 0;
                                final HashMap<String, Integer> stats = (HashMap<String, Integer>)item.getProperty("bonusStats");
                                for (final String statName : stats.keySet()) {
                                    props.put("item_" + numItems + "Stat_" + numStats + "Name", statName);
                                    props.put("item_" + numItems + "Stat_" + numStats + "Value", stats.get(statName));
                                    ++numStats;
                                }
                                props.put("item_" + numItems + "NumStats", numStats);
                            }
                            else {
                                props.put("item_" + numItems + "NumStats", 0);
                            }
                            if (item.getItemType().equals("Weapon")) {
                                props.put("item_" + numItems + "Delay", item.getProperty("delay"));
                                props.put("item_" + numItems + "DamageType", item.getProperty("attackType"));
                                props.put("item_" + numItems + "DamageValue", item.getProperty("damage"));
                            }
                            ++numItems;
                            if (itemCounts.containsKey(item.getTemplateID())) {
                                itemCounts.put(item.getTemplateID(), itemCounts.get(item.getTemplateID()) + item.getStackSize());
                            }
                            else {
                                itemCounts.put(item.getTemplateID(), item.getStackSize());
                            }
                        }
                    }
                }
            }
        }
        props.put("numItems", numItems);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, mobOid, mobOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
        final AgisInventoryClient.QuestItemsListMessage invUpdateMsg = new AgisInventoryClient.QuestItemsListMessage(mobOid, itemCounts);
        Engine.getAgent().sendBroadcast((Message)invUpdateMsg);
        ExtendedCombatMessages.sendCurrencies(mobOid, iInfo.getCurrentCurrencies());
    }
    
    protected void sendEquippedInvUpdate(final OID mobOid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "EquippedInventoryUpdate");
        final InventoryInfo iInfo = getInventoryInfo(mobOid);
        if (iInfo == null) {
            return;
        }
        final OID[] bags = iInfo.getBags();
        final OID subBagOid = bags[this.INVENTORY_BAG_COUNT];
        if (subBagOid == null) {
            AgisInventoryPlugin.log.error("sendInvUpdate: sub bag oid is null");
        }
        final Bag subBag = getBag(subBagOid);
        if (subBag == null) {
            AgisInventoryPlugin.log.error("sendInvUpdate: sub bag obj is null");
        }
        props.put("numSlots", subBag.getNumSlots());
        for (int pos = 0; pos < subBag.getNumSlots(); ++pos) {
            final OID oid = subBag.getItem(pos);
            if (oid == null) {
                props.put("item_" + pos + "Name", "");
            }
            else {
                final AgisItem item = getAgisItem(oid);
                if (item == null) {
                    Log.warn("sendInvUpdateHelper: item is null, oid=" + oid);
                    props.put("item_" + pos + "Name", "");
                }
                else {
                    if (Log.loggingDebug) {
                        AgisInventoryPlugin.log.debug("sendEquippedUpdateHelper: , itemOid=" + oid + ", itemName=" + item.getName() + ",icon=" + item.getIcon());
                    }
                    props.put("item_" + pos + "Name", item.getName());
                    props.put("item_" + pos + "BaseName", item.getProperty("baseName"));
                    props.put("item_" + pos + "Id", (Serializable)item.getOid());
                    props.put("item_" + pos + "Count", item.getStackSize());
                    if (item.getProperty("energyCost") != null) {
                        props.put("item_" + pos + "EnergyCost", item.getProperty("energyCost"));
                    }
                    else {
                        props.put("item_" + pos + "EnergyCost", 0);
                    }
                    if (item.getProperty("resistanceStats") != null) {
                        int numResist = 0;
                        final HashMap<String, Integer> resistances = (HashMap<String, Integer>)item.getProperty("resistanceStats");
                        for (final String resistance : resistances.keySet()) {
                            props.put("item_" + pos + "Resist_" + numResist + "Name", resistance);
                            props.put("item_" + pos + "Resist_" + numResist + "Value", resistances.get(resistance));
                            ++numResist;
                        }
                        props.put("item_" + pos + "NumResistances", numResist);
                    }
                    else {
                        props.put("item_" + pos + "NumResistances", 0);
                    }
                    if (item.getProperty("bonusStats") != null) {
                        int numStats = 0;
                        final HashMap<String, Integer> stats = (HashMap<String, Integer>)item.getProperty("bonusStats");
                        for (final String statName : stats.keySet()) {
                            props.put("item_" + pos + "Stat_" + numStats + "Name", statName);
                            props.put("item_" + pos + "Stat_" + numStats + "Value", stats.get(statName));
                            ++numStats;
                        }
                        props.put("item_" + pos + "NumStats", numStats);
                    }
                    else {
                        props.put("item_" + pos + "NumStats", 0);
                    }
                    if (item.getItemType().equals("Weapon")) {
                        props.put("item_" + pos + "Speed", item.getProperty("speed"));
                        props.put("item_" + pos + "DamageType", item.getProperty("attackType"));
                        props.put("item_" + pos + "DamageValue", item.getProperty("damage"));
                    }
                }
            }
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, mobOid, mobOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    private int changeBagInSlot(final AgisItem item, final OID mobOid, final int parentBagSlotNum) {
        if (parentBagSlotNum == 0) {
            ExtendedCombatMessages.sendErrorMessage(mobOid, "You cannot swap out your Backpack.");
            return -1;
        }
        Log.debug("BAG: changing bag in slot: " + parentBagSlotNum + " to item with type: " + item.getType());
        if (!item.getItemType().equals("Bag")) {
            return -1;
        }
        final InventoryInfo iInfo = getInventoryInfo(mobOid);
        if (iInfo == null) {
            return -1;
        }
        final OID[] subBags = iInfo.getBags();
        final OID subBagOid = subBags[parentBagSlotNum];
        final Bag subBag = getBag(subBagOid);
        Log.debug("BAG: bag has num slots: " + item.getProperty("numSlots"));
        final int newNumSlots = (int)item.getProperty("numSlots");
        final OID[] itemsInBag = subBag.getItemsList();
        final Boolean result = this.removeItemFromBag(mobOid, item.getOid());
        if (!result) {
            return -1;
        }
        if (newNumSlots < subBag.getNumSlots()) {
            for (int i = 0; i < itemsInBag.length; ++i) {
                Log.debug("BAG: checking items in bag for swap. Item: " + itemsInBag[i]);
                if (itemsInBag[i] != null) {
                    ExtendedCombatMessages.sendErrorMessage(mobOid, "This bag cannot hold all of the items in the bag you are currently using.");
                    return -1;
                }
            }
            subBag.setNumSlots(newNumSlots);
        }
        else {
            subBag.setNumSlots(newNumSlots);
            final OID[] newItems = new OID[newNumSlots];
            for (int j = 0; j < itemsInBag.length; ++j) {
                newItems[j] = itemsInBag[j];
            }
            subBag.setItemsList(newItems);
        }
        final int oldBagID = subBag.getID();
        subBag.setName(item.getName());
        subBag.setID(item.getTemplateID());
        Engine.getPersistenceManager().setDirty((Entity)iInfo);
        return oldBagID;
    }
    
    private OID generateItem(final int templateID, final String itemName) {
        final Template itemTemplate = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
        if (itemTemplate == null) {
            Log.error("ITEM: generating item: " + itemName + " failed");
            return null;
        }
        String templateName = itemTemplate.getName();
        final Boolean randomisedStats = (Boolean)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "randomisedStats");
        if (randomisedStats != null && randomisedStats) {
            Log.debug("ITEM: generating new item: " + templateName + " which has randomised stats.");
            ItemWeight prefix = null;
            ItemWeight suffix = null;
            int roll = AgisInventoryPlugin.random.nextInt(100);
            if (roll < 40) {
                roll = AgisInventoryPlugin.random.nextInt(AgisInventoryPlugin.itemPrefixes.size());
                prefix = AgisInventoryPlugin.itemPrefixes.get(roll);
                Log.debug("ITEM: prefix name: " + prefix.getItemWeightName());
                templateName = String.valueOf(prefix.getItemWeightName()) + " " + templateName;
            }
            else if (roll < 80) {
                roll = AgisInventoryPlugin.random.nextInt(AgisInventoryPlugin.itemSuffixes.size());
                suffix = AgisInventoryPlugin.itemSuffixes.get(roll);
                Log.debug("ITEM: suffix name: " + suffix.getItemWeightName());
                templateName = String.valueOf(templateName) + " " + suffix.getItemWeightName();
            }
            else {
                roll = AgisInventoryPlugin.random.nextInt(AgisInventoryPlugin.itemPrefixes.size());
                prefix = AgisInventoryPlugin.itemPrefixes.get(roll);
                roll = AgisInventoryPlugin.random.nextInt(AgisInventoryPlugin.itemSuffixes.size());
                suffix = AgisInventoryPlugin.itemSuffixes.get(roll);
                Log.debug("ITEM: prefix name: " + prefix.getItemWeightName() + "; suffix name: " + suffix.getItemWeightName());
                templateName = String.valueOf(prefix.getItemWeightName()) + " " + templateName + " " + suffix.getItemWeightName();
            }
            Log.debug("ITEM: randomised template: " + templateName + " does not yet exist.");
            this.createNewItemTemplate(itemTemplate, templateName, prefix, suffix);
        }
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
        final OID itemOid = ObjectManagerClient.generateObject(templateID, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
        final AgisItem item = getAgisItem(itemOid);
        item.setName(itemName);
        return itemOid;
    }
    
    private Template createNewItemTemplate(final Template itemTemplate, final String newTemplateName, final ItemWeight prefix, final ItemWeight suffix) {
        Log.debug("ITEM: creating new item template with name: " + newTemplateName);
        itemTemplate.setName(newTemplateName);
        itemTemplate.setTemplateID(itemTemplate.getTemplateID() * -1);
        float totalStatWeight = 0.0f;
        double energyCost = 0.0;
        final HashMap<String, Integer> statWeights = new HashMap<String, Integer>();
        if (prefix != null) {
            totalStatWeight += prefix.getTotalStatWeight();
            if (prefix.getStat1() != null && prefix.getWeight1() > 0) {
                statWeights.put(prefix.getStat1(), prefix.getWeight1());
            }
            if (prefix.getStat2() != null && prefix.getWeight2() > 0) {
                statWeights.put(prefix.getStat2(), prefix.getWeight2());
            }
            if (prefix.getStat3() != null && prefix.getWeight3() > 0) {
                statWeights.put(prefix.getStat3(), prefix.getWeight3());
            }
        }
        if (suffix != null) {
            totalStatWeight += suffix.getTotalStatWeight();
            if (suffix.getStat1() != null && suffix.getWeight1() > 0) {
                if (statWeights.containsKey(suffix.getStat1())) {
                    statWeights.put(suffix.getStat1(), suffix.getWeight1() + statWeights.get(suffix.getStat1()));
                }
                else {
                    statWeights.put(suffix.getStat1(), suffix.getWeight1());
                }
            }
            if (suffix.getStat2() != null && suffix.getWeight2() > 0) {
                if (statWeights.containsKey(suffix.getStat2())) {
                    statWeights.put(suffix.getStat2(), suffix.getWeight2() + statWeights.get(suffix.getStat2()));
                }
                else {
                    statWeights.put(suffix.getStat2(), suffix.getWeight2());
                }
            }
            if (suffix.getStat3() != null && suffix.getWeight3() > 0) {
                if (statWeights.containsKey(suffix.getStat3())) {
                    statWeights.put(suffix.getStat3(), suffix.getWeight3() + statWeights.get(suffix.getStat3()));
                }
                else {
                    statWeights.put(suffix.getStat3(), suffix.getWeight3());
                }
            }
        }
        final float itemQuality = (int)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "itemQuality");
        final String slot = (String)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "slot");
        final float slotModifier = getSlotModifier(slot);
        final int itemGrade = (int)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "itemGrade");
        final float gradeModifier = getGradeModifier(itemGrade);
        final HashMap<String, Integer> itemStats = new HashMap<String, Integer>();
        final double totalStatsValue = Math.pow(itemQuality * slotModifier * gradeModifier, 1.7);
        Log.debug("CALC: totalStatsValue: " + totalStatsValue);
        for (final String statName : statWeights.keySet()) {
            final float statMod = getStatModifier(statName);
            Log.debug("CALC: itemQuality: " + itemQuality + "; slotMod: " + slotModifier + "; gradeModifier: " + gradeModifier + "; totalStatWeight: " + totalStatWeight + "; statWeight: " + (float)statWeights.get(statName));
            final double statCalc1 = totalStatsValue / totalStatWeight * statWeights.get(statName);
            Log.debug("CALC: statCalc1: " + statCalc1 + "; statMod: " + statMod);
            final double statValue = Math.floor(Math.pow(statCalc1, 0.5882352941176471) / statMod);
            Log.debug("CALC: final stat value for " + statName + " is: " + statValue);
            itemStats.put(statName, (int)statValue);
            energyCost += Math.pow(statValue * statMod, 1.7);
        }
        energyCost = Math.ceil(energyCost);
        itemTemplate.put(InventoryClient.ITEM_NAMESPACE, "bonusStats", (Serializable)itemStats);
        itemTemplate.put(InventoryClient.ITEM_NAMESPACE, "energyCost", (Serializable)(int)energyCost);
        ObjectManagerClient.registerTemplate(itemTemplate);
        return itemTemplate;
    }
    
    private Template createPetItemTemplate(final Template itemTemplate, final String newTemplateName, final String petRef) {
        Log.debug("ITEM: creating new item template with name: " + newTemplateName);
        itemTemplate.setName(newTemplateName);
        itemTemplate.put(InventoryClient.ITEM_NAMESPACE, "petRef", (Serializable)petRef);
        ObjectManagerClient.registerTemplate(itemTemplate);
        return itemTemplate;
    }
    
    public int getMobCurrency(final OID mobOid, final int currencyID) {
        Log.debug("CURRENCY: getting mob currency: " + currencyID + " for mob: " + mobOid);
        final Currency c = (Currency)Agis.CurrencyManager.get(currencyID);
        if (c.getExternal()) {
            final OID accountID = (OID)EnginePlugin.getObjectProperty(mobOid, WorldManagerClient.NAMESPACE, "accountId");
            return AgisInventoryPlugin.aDB.getCharacterCoinAmount(accountID);
        }
        final InventoryInfo iInfo = getInventoryInfo(mobOid);
        return iInfo.getCurrencyAmount(currencyID, true);
    }
    
    public void alterMobCurrency(final OID mobOid, final int currencyID, final int delta) {
        final Currency c = (Currency)Agis.CurrencyManager.get(currencyID);
        Log.debug("CURRENCY: getting currency: " + currencyID);
        if (c.getExternal()) {
            final OID accountID = (OID)EnginePlugin.getObjectProperty(mobOid, WorldManagerClient.NAMESPACE, "accountId");
            AgisInventoryPlugin.aDB.alterCharacterCoinAmount(accountID, delta);
        }
        else {
            final InventoryInfo iInfo = getInventoryInfo(mobOid);
            iInfo.alterCurrencyAmount(currencyID, delta);
            Engine.getPersistenceManager().setDirty((Entity)iInfo);
        }
        final InventoryInfo iInfo = getInventoryInfo(mobOid);
        ExtendedCombatMessages.sendCurrencies(mobOid, iInfo.getCurrentCurrencies());
    }
    
    protected ItemAcquireResult accountItemAcquired(final OID oid, final int templateID, int count) {
        Log.error("PICKUP: got pickup message with player: " + oid + " and item: " + templateID);
        final Template tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
        if (tmpl == null) {
            Log.error("PICKUP: item ID: " + templateID + " does not exist");
            return ItemAcquireResult.ITEM_DOESNT_EXIST;
        }
        final String eventTrigger = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerEvent");
        if (eventTrigger.equals("ITEM_OBTAINED")) {
            final String eventAction = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Type");
            final int stackLimit = (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "stackLimit");
            if (eventAction.equals("addWeapon")) {
                final String eventActionData = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Data");
                final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
                final int uses = 10 + 15 * (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "itemGrade");
                final HashMap<String, Serializable> loggerProps = new HashMap<String, Serializable>();
                loggerProps.put("weapon", eventActionData);
                DataLoggerClient.logData("WEAPON_ADDED", oid, null, accountID, loggerProps);
                this.sendBagInvUpdate(oid);
                if (count > 1) {
                    ExtendedCombatMessages.sendCombatText(oid, "+" + count + " " + tmpl.getName() + "s", 9);
                }
                else if (count == 1) {
                    ExtendedCombatMessages.sendCombatText(oid, "+" + count + " " + tmpl.getName(), 9);
                }
                WorldManagerClient.sendObjChatMsg(oid, 2, "Received: " + tmpl.getName() + " (x" + count + ")");
                return ItemAcquireResult.SUCCESS;
            }
            if (eventAction.equals("addItem")) {
                final String eventActionData = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Data");
                final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
                if (!AgisInventoryPlugin.aDB.addItem(accountID, templateID, count)) {
                    AgisInventoryPlugin.aDB.alterItemAmount(accountID, templateID, count);
                }
                final HashMap<String, Serializable> loggerProps2 = new HashMap<String, Serializable>();
                loggerProps2.put("item", eventActionData);
                DataLoggerClient.logData("ITEM_ADDED", oid, null, accountID, loggerProps2);
                this.sendBagInvUpdate(oid);
                if (count > 1) {
                    ExtendedCombatMessages.sendCombatText(oid, "+" + count + " " + tmpl.getName() + "s", 9);
                }
                else if (count == 1) {
                    ExtendedCombatMessages.sendCombatText(oid, "+" + count + " " + tmpl.getName(), 9);
                }
                WorldManagerClient.sendObjChatMsg(oid, 2, "Received: " + tmpl.getName() + " (x" + count + ")");
                return ItemAcquireResult.SUCCESS;
            }
            if (eventAction.contains("addCurrency")) {
                final int currency = Integer.parseInt(eventAction.substring(eventAction.length() - 1));
                final String eventActionData2 = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Data");
                count *= Integer.parseInt(eventActionData2);
                final OID accountID2 = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
                final HashMap<String, Serializable> loggerProps = new HashMap<String, Serializable>();
                loggerProps.put("currency", currency);
                DataLoggerClient.logData("CURRENCY_ADDED", oid, null, accountID2, loggerProps);
                this.alterMobCurrency(oid, currency, count);
                this.sendBagInvUpdate(oid);
                if (count > 1) {
                    ExtendedCombatMessages.sendCombatText(oid, "+" + count + " Tokens", 9);
                }
                else if (count == 1) {
                    ExtendedCombatMessages.sendCombatText(oid, "+" + count + " Token", 9);
                }
                WorldManagerClient.sendObjChatMsg(oid, 2, "Received: " + tmpl.getName() + " (x" + count + ")");
                return ItemAcquireResult.SUCCESS;
            }
        }
        if (!this.hasSpace(oid, templateID, count, 0)) {
            ExtendedCombatMessages.sendErrorMessage(oid, "You do not have enough space in your inventory to receive that item.");
            return ItemAcquireResult.STACK_LIMIT_REACHED;
        }
        Log.debug("ITEM: finished generation and adding of item: " + templateID);
        if (this.addItemFromTemplate(oid, templateID, count, null)) {
            return ItemAcquireResult.SUCCESS;
        }
        return ItemAcquireResult.UNKNOWN_FAILURE;
    }
    
    protected static void sendTradeComplete(final OID trader1, final OID trader2, final byte status) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.TRADE_COMPLETE");
        props.put("status", status);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(AgisInventoryClient.MSG_TYPE_TRADE_COMPLETE, trader1, trader2, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected static void sendTradeStart(final OID trader1, final OID trader2) {
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(AgisInventoryClient.MSG_TYPE_TRADE_START, "ao.TRADE_START", trader1, trader2);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public boolean validateTradeOffer(final OID trader, final List<OID> offer) {
        final Set<OID> itemSet = new HashSet<OID>();
        for (final OID itemOid : offer) {
            if (itemOid == null) {
                continue;
            }
            if (!itemSet.add(itemOid)) {
                return false;
            }
        }
        final InventoryInfo iInfo = getInventoryInfo(trader);
        if (iInfo == null) {
            return false;
        }
        final OID[] subBags = iInfo.getBags();
        OID[] array;
        for (int length = (array = subBags).length, i = 0; i < length; ++i) {
            final OID subBagOid = array[i];
            if (subBagOid != null) {
                final Bag subBag = getBag(subBagOid);
                OID[] itemsList;
                for (int length2 = (itemsList = subBag.getItemsList()).length, j = 0; j < length2; ++j) {
                    final OID itemOid2 = itemsList[j];
                    itemSet.remove(itemOid2);
                }
            }
        }
        return itemSet.isEmpty();
    }
    
    public static void sendTradeOfferUpdate(final OID trader1, final OID trader2, final TradeSession tradeSession) {
        final Boolean accepted1 = tradeSession.getAccepted(trader1);
        final Boolean accepted2 = tradeSession.getAccepted(trader2);
        final LinkedList<LinkedList> offer1 = sendTradeOfferUpdateHelper(trader1, tradeSession);
        final LinkedList<LinkedList> offer2 = sendTradeOfferUpdateHelper(trader2, tradeSession);
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.TRADE_OFFER_UPDATE");
        props.put("accepted1", accepted1);
        props.put("accepted2", accepted2);
        props.put("offer1", offer1);
        props.put("offer2", offer2);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(AgisInventoryClient.MSG_TYPE_TRADE_OFFER_UPDATE, trader1, trader2, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    protected static LinkedList<LinkedList> sendTradeOfferUpdateHelper(final OID traderOid, final TradeSession tradeSession) {
        final LinkedList<LinkedList> offer = new LinkedList<LinkedList>();
        for (final OID itemOid : tradeSession.getOffer(traderOid)) {
            final LinkedList<Object> info = new LinkedList<Object>();
            if (itemOid == null) {
                info.add(itemOid);
                info.add("");
                info.add("");
            }
            else {
                final AgisItem item = getAgisItem(itemOid);
                info.add(itemOid);
                info.add(item.getName());
                info.add(item.getIcon());
            }
            offer.add(info);
        }
        return offer;
    }
    
    public void completeTrade(final TradeSession tradeSession) {
        final OID trader1Oid = tradeSession.getTrader1();
        final OID trader2Oid = tradeSession.getTrader2();
        final InventoryInfo iInfo1 = getInventoryInfo(trader1Oid);
        final InventoryInfo iInfo2 = getInventoryInfo(trader2Oid);
        final List<OID> offer1 = tradeSession.getOffer(trader1Oid);
        final List<OID> offer2 = tradeSession.getOffer(trader2Oid);
        for (final OID itemOid : offer1) {
            this.removeItem(trader1Oid, itemOid, true);
        }
        for (final OID itemOid : offer2) {
            this.removeItem(trader2Oid, itemOid, true);
        }
        for (final OID itemOid : offer1) {
            this.addItem(trader2Oid, iInfo2.getOid(), itemOid);
        }
        for (final OID itemOid : offer2) {
            this.addItem(trader1Oid, iInfo1.getOid(), itemOid);
        }
        this.sendInvUpdate(trader1Oid);
        this.sendInvUpdate(trader2Oid);
    }
    
    protected EnginePlugin.SubObjData createItemSubObj(final OID masterOid, final Template template) {
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("createItemSubObj: creating item=" + template.getName() + " masterOid=" + masterOid);
        }
        final AgisItem item = new AgisItem(masterOid);
        item.setName(template.getName());
        item.setTemplateID(template.getTemplateID());
        final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(Namespace.AGISITEM);
        if (props == null) {
            Log.warn("createItemSubObj: no props in ns " + Namespace.AGISITEM);
            return null;
        }
        Boolean persistent = (Boolean)template.get(Namespace.OBJECT_MANAGER, ":persistent");
        if (persistent == null) {
            persistent = false;
        }
        item.setPersistenceFlag((boolean)persistent);
        for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
            final String key = entry.getKey();
            final Serializable value = entry.getValue();
            if (!key.startsWith(":")) {
                item.setProperty(key, value);
            }
        }
        registerAgisItem(item);
        return new EnginePlugin.SubObjData();
    }
    
    protected void loadInventory(final Entity e) {
        final InventoryInfo iInfo = (InventoryInfo)e;
        final OID ownerOid = iInfo.getOid();
        boolean dirty = false;
        OID[] bags;
        for (int length = (bags = iInfo.getBags()).length, i = 0; i < length; ++i) {
            final OID subBagOid = bags[i];
            if (subBagOid != null && this.loadSubBag(subBagOid, iInfo)) {
                dirty = true;
            }
        }
        if (dirty) {
            Engine.getPersistenceManager().setDirty((Entity)iInfo);
        }
        for (final int currencyID : Agis.CurrencyManager.keyList()) {
            if (!iInfo.getCurrentCurrencies().containsKey(currencyID)) {
                iInfo.addCurrency(currencyID, 0);
            }
        }
        ExtendedCombatMessages.sendCurrencies(ownerOid, iInfo.getCurrencies(iInfo.getCurrentCategory()));
        this.sendInvUpdate(ownerOid);
        final OID[] subBags = iInfo.getBags();
        final OID subBagOid2 = subBags[this.INVENTORY_BAG_COUNT];
        final Bag subBag = getBag(subBagOid2);
        if (subBag != null) {
            OID[] itemsList;
            for (int length2 = (itemsList = subBag.getItemsList()).length, j = 0; j < length2; ++j) {
                final OID itemOid = itemsList[j];
                if (itemOid != null) {
                    final AgisItem item = getAgisItem(itemOid);
                    final String displayVal = (String)item.getProperty("displayVal");
                    EquipHelper.updateDisplay(ownerOid, displayVal, item.getPrimarySlot());
                    AgisInventoryClient.itemEquipStatusChanged(ownerOid, item, true, item.getPrimarySlot().toString());
                }
            }
        }
    }
    
    protected boolean loadSubBag(final OID subBagOid, final Entity rootBag) {
        final Bag subBag = (Bag)Engine.getDatabase().loadEntity(subBagOid, Namespace.BAG);
        registerBag(subBag);
        boolean dirty = false;
        OID[] itemsList;
        for (int length = (itemsList = subBag.getItemsList()).length, i = 0; i < length; ++i) {
            final OID itemOid = itemsList[i];
            if (itemOid != null && ObjectManagerClient.loadObject(itemOid) == null) {
                Log.warn("loadSubBag: item " + itemOid + " does not exist, removing from bag " + subBagOid);
                final boolean rv = subBag.removeItem(itemOid);
                if (rv) {
                    dirty = true;
                }
            }
        }
        return dirty;
    }
    
    protected void loadMailItems(final OID oid) {
        final InventoryInfo iInfo = getInventoryInfo(oid);
        for (final Mail m : iInfo.getMail()) {
            if (m.getItems() != null) {
                for (int i = 0; i < m.getItems().size(); ++i) {
                    if (m.getItems().get(i) != null) {
                        ObjectManagerClient.loadObject((OID)m.getItems().get(i));
                    }
                }
            }
        }
    }
    
    protected void unloadInventory(final Entity e) {
        final InventoryInfo iInfo = (InventoryInfo)e;
        final OID ownerOid = iInfo.getOid();
        if (e.isDeleted()) {
            return;
        }
        if (Engine.getPersistenceManager().isDirty(e)) {
            Engine.getPersistenceManager().clearDirty(e);
            this.saveInventory(e, null);
        }
        Log.debug("unloadInventory: oid=" + e.getOid() + " owner=" + ownerOid);
        OID[] bags;
        for (int length = (bags = iInfo.getBags()).length, i = 0; i < length; ++i) {
            final OID subBagOid = bags[i];
            if (subBagOid != null) {
                final Bag subBag = getBag(subBagOid);
                if (subBag != null) {
                    OID[] itemsList;
                    for (int length2 = (itemsList = subBag.getItemsList()).length, j = 0; j < length2; ++j) {
                        final OID itemOid = itemsList[j];
                        if (itemOid != null) {
                            if (Log.loggingDebug) {
                                Log.debug("unloadInventory: bag oid=" + e.getOid() + " subbag=" + subBagOid + " item=" + itemOid);
                            }
                            ObjectManagerClient.unloadObject(itemOid);
                        }
                    }
                    EntityManager.removeEntityByNamespace(subBagOid, Namespace.BAG);
                }
            }
        }
    }
    
    protected void deleteInventory(final Entity e) {
        final InventoryInfo iInfo = (InventoryInfo)e;
        final OID ownerOid = iInfo.getOid();
        Log.debug("deleteInventory: oid=" + e.getOid() + " owner=" + ownerOid);
        OID[] bags;
        for (int length = (bags = iInfo.getBags()).length, i = 0; i < length; ++i) {
            final OID subBagOid = bags[i];
            if (subBagOid != null) {
                final Bag subBag = getBag(subBagOid);
                if (subBag != null) {
                    OID[] itemsList;
                    for (int length2 = (itemsList = subBag.getItemsList()).length, j = 0; j < length2; ++j) {
                        final OID itemOid = itemsList[j];
                        if (itemOid != null) {
                            if (Log.loggingDebug) {
                                Log.debug("deleteInventory: bag oid=" + e.getOid() + " subbag=" + subBagOid + " item=" + itemOid);
                            }
                            ObjectManagerClient.deleteObject(itemOid);
                        }
                    }
                    subBag.setDeleted();
                    EntityManager.removeEntityByNamespace(subBagOid, Namespace.BAG);
                    Engine.getDatabase().deleteObjectData(subBagOid);
                }
            }
        }
    }
    
    protected void loadItem(final Entity e) {
    }
    
    protected void unloadItem(final Entity e) {
    }
    
    protected void deleteItem(final Entity item) {
        if (Log.loggingDebug) {
            Log.debug("deleteItem: oid=" + item.getOid());
        }
        final OID subBagOid = (OID)item.getProperty("inv.backref");
        if (subBagOid != null && this.removeItemFromBagHelper(subBagOid, item)) {
            final Bag subBag = getBag(subBagOid);
            final OID ownerOid = (OID)subBag.getProperty("inv.backref");
            final AgisItem aItem = (AgisItem)item;
            aItem.unacquired(ownerOid);
            final InventoryInfo iInfo = getInventoryInfo(ownerOid);
            if (iInfo != null) {
                Engine.getPersistenceManager().setDirty((Entity)iInfo);
                this.sendInvUpdate(ownerOid);
            }
        }
        Engine.getDatabase().deleteObjectData(item.getOid());
    }
    
    protected void saveInventory(final Entity e, final Namespace namespace) {
        final InventoryInfo iInfo = (InventoryInfo)e;
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("saveInventory: rootBag=" + iInfo.getOid());
        }
        OID[] bags;
        for (int length = (bags = iInfo.getBags()).length, j = 0; j < length; ++j) {
            final OID subBagOid = bags[j];
            if (subBagOid != null) {
                final Bag subBag = getBag(subBagOid);
                if (subBag == null) {
                    AgisInventoryPlugin.log.error("saveInventory: subBag not found oid=" + subBagOid);
                }
                else {
                    if (Log.loggingDebug) {
                        AgisInventoryPlugin.log.debug("saveInventory: subBag oid=" + subBag.getOid());
                    }
                    Engine.getDatabase().saveObject((Entity)subBag, Namespace.BAG);
                    OID[] itemsList;
                    for (int length2 = (itemsList = subBag.getItemsList()).length, k = 0; k < length2; ++k) {
                        final OID itemOid = itemsList[k];
                        if (itemOid != null) {
                            if (Log.loggingDebug) {
                                AgisInventoryPlugin.log.debug("saveInventory: saving itemOid=" + itemOid);
                            }
                            ObjectManagerClient.saveObject(itemOid);
                            if (Log.loggingDebug) {
                                AgisInventoryPlugin.log.debug("saveInventory: done saving itemOid=" + itemOid);
                            }
                        }
                    }
                }
            }
        }
        for (final Mail m : iInfo.getMail()) {
            if (m.getItems() != null) {
                for (int i = 0; i < m.getItems().size(); ++i) {
                    if (m.getItems().get(i) != null) {
                        ObjectManagerClient.saveObject((OID)m.getItems().get(i));
                    }
                }
            }
        }
    }
    
    protected void saveItem(final Entity e, final Namespace namespace) {
    }
    
    private boolean placeItem(final OID mobOid, final OID rootBagOid, final OID itemOid, final int containerNum, final int slotNum) {
        final InventoryInfo iInfo = getInventoryInfo(rootBagOid);
        final OID[] subBagOids = iInfo.getBags();
        final OID subBagOid = subBagOids[containerNum];
        final Bag subBag = getBag(subBagOid);
        if (subBag == null) {
            Log.warn("placeItem: did not find sub bag: " + subBagOid + " for bagoid=" + subBagOid);
            return false;
        }
        final Entity item = (Entity)getAgisItem(itemOid);
        if (item == null) {
            return false;
        }
        final boolean rv = subBag.putItem(slotNum, itemOid);
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("placeItem: adding to bag, rv=" + rv);
        }
        if (rv) {
            item.setProperty("inv.backref", (Serializable)subBagOid);
        }
        Engine.getPersistenceManager().setDirty(item);
        Engine.getPersistenceManager().setDirty((Entity)iInfo);
        this.sendBagInvUpdate(mobOid);
        return rv;
    }
    
    private boolean addItemFromTemplate(final OID mobOid, final int itemID, int count, final HashMap<String, Serializable> itemProps) {
        this.lock.lock();
        try {
            final InventoryInfo iInfo = getInventoryInfo(mobOid);
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("addItem: found bag object: " + iInfo);
            }
            final Template tmpl = ObjectManagerClient.getTemplate(itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
            final String itemName = tmpl.getName();
            final int stackLimit = (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "stackLimit");
            final ArrayList<OID> existingOids = this.findItemStacks(mobOid, itemID);
            if (existingOids.size() > 0) {
                Log.debug("ITEM: user already has item " + itemID + ", see if we can add it to one of the stacks.");
                for (final OID existingOid : existingOids) {
                    final AgisItem tempItem = getAgisItem(existingOid);
                    Log.debug("ITEM: stackSize " + tempItem.getStackSize() + ", stackLimit: " + tempItem.getStackLimit() + "for item: " + existingOid);
                    if (tempItem.getStackSize() + count <= tempItem.getStackLimit()) {
                        Log.debug("ITEM: increasing stack size for item: " + existingOid);
                        tempItem.setStackSize(tempItem.getStackSize() + count);
                        Engine.getPersistenceManager().setDirty((Entity)tempItem);
                        Engine.getPersistenceManager().setDirty((Entity)iInfo);
                        return true;
                    }
                    Log.debug("ITEM: increasing stack size to max for item: " + existingOid);
                    final int stackSpace = tempItem.getStackLimit() - tempItem.getStackSize();
                    tempItem.setStackSize(tempItem.getStackLimit());
                    Engine.getPersistenceManager().setDirty((Entity)tempItem);
                    Engine.getPersistenceManager().setDirty((Entity)iInfo);
                    count -= stackSpace;
                }
            }
            final OID[] subBags = iInfo.getBags();
            final int stacksNeeded = (count - 1) / stackLimit + 1;
            Log.debug("ITEM: there is no stacks to add to for item: " + itemID + " so going to create a new item. Stacks needed: " + stacksNeeded);
            for (int i = 0; i < stacksNeeded; ++i) {
                final OID itemOid = this.generateItem(itemID, itemName);
                if (itemProps != null) {
                    final AgisItem item = getAgisItem(itemOid);
                    for (final String itemProp : itemProps.keySet()) {
                        item.setProperty(itemProp, (Serializable)itemProps.get(itemProp));
                    }
                }
                final AgisItem item = getAgisItem(itemOid);
                if (count > 1) {
                    if (count > stackLimit) {
                        item.setStackSize(stackLimit);
                        count -= stackLimit;
                    }
                    else {
                        item.setStackSize(count);
                        count = 0;
                    }
                }
                else {
                    count = 0;
                }
                boolean stackAdded = false;
                for (int pos = 0; pos < subBags.length - 1; ++pos) {
                    final OID subBag = subBags[pos];
                    if (this.addItemHelper(mobOid, subBag, pos, item)) {
                        Engine.getPersistenceManager().setDirty((Entity)iInfo);
                        stackAdded = true;
                        break;
                    }
                }
                if (!stackAdded) {
                    Log.error("ITEM: space for item: " + itemName + " was not found.");
                    return false;
                }
            }
            if (count > 0) {
                return false;
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        return true;
    }
    
    protected boolean addItem(final OID mobOid, final OID rootBagOid, final OID itemOid) {
        this.lock.lock();
        try {
            final InventoryInfo iInfo = getInventoryInfo(mobOid);
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("addItem: found bag object: " + iInfo);
            }
            final AgisItem item = getAgisItem(itemOid);
            if (item == null) {
                Log.warn("addItem: item is null: oid=" + itemOid);
                return false;
            }
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("addItem: found item: " + item);
            }
            final int stackSizeToAdd = item.getStackSize();
            final ArrayList<OID> existingOids = this.findItemStacks(mobOid, item.getTemplateID());
            if (existingOids.size() > 0) {
                Log.debug("ITEM: user already has item " + item.getName() + ", see if we can add it to one of the stacks.");
                for (final OID existingOid : existingOids) {
                    final AgisItem tempItem = getAgisItem(existingOid);
                    Log.debug("ITEM: stackSize " + tempItem.getStackSize() + ", stackLimit: " + tempItem.getStackLimit() + "for item: " + existingOid);
                    if (tempItem.getStackSize() + stackSizeToAdd <= tempItem.getStackLimit()) {
                        Log.debug("ITEM: increasing stack size for item: " + existingOid);
                        tempItem.setStackSize(tempItem.getStackSize() + stackSizeToAdd);
                        Engine.getPersistenceManager().setDirty((Entity)tempItem);
                        Engine.getPersistenceManager().setDirty((Entity)iInfo);
                        return true;
                    }
                }
            }
            Log.debug("ITEM: there is no stacks to add to for item: " + item.getName() + " so going to create a new item");
            final OID[] subBags = iInfo.getBags();
            for (int pos = 0; pos < subBags.length - 1; ++pos) {
                final OID subBag = subBags[pos];
                if (this.addItemHelper(mobOid, subBag, pos, item)) {
                    Engine.getPersistenceManager().setDirty((Entity)iInfo);
                    return true;
                }
            }
            ExtendedCombatMessages.sendErrorMessage(mobOid, "There is no space in your bags.");
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected boolean addItemHelper(final OID ownerOid, final OID subBagOid, final int slotNum, final AgisItem item) {
        if (item.acquired(ownerOid)) {
            return true;
        }
        final Bag subBag = getBag(subBagOid);
        if (subBag == null) {
            Log.warn("addItemHelper: did not find sub bag: " + subBagOid + " for bagoid=" + subBagOid);
            return false;
        }
        if (item.getProperty("inv.backref") != null) {
            Log.warn("addItem: item is already in a container, itemOid=" + item.getOid());
            return false;
        }
        final boolean rv = subBag.addItem(item.getOid());
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("addItem: adding to bag=" + subBag + " with slots=" + subBag.getNumSlots() + ", rv=" + rv);
        }
        if (rv) {
            item.setProperty("inv.backref", (Serializable)subBagOid);
        }
        Engine.getPersistenceManager().setDirty((Entity)item);
        return rv;
    }
    
    protected boolean removeItemFromBag(final OID rootBagOid, final OID itemOid) {
        this.lock.lock();
        try {
            final InventoryInfo iInfo = getInventoryInfo(rootBagOid);
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItemFromBag: found root bag object: " + iInfo);
            }
            final AgisItem item = getAgisItem(itemOid);
            if (item == null) {
                Log.warn("removeItemFromBag: item is null: oid=" + itemOid);
                return false;
            }
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItemFromBag: found item: " + item);
            }
            final OID[] subBags = iInfo.getBags();
            for (int pos = 0; pos < subBags.length; ++pos) {
                final OID subBag = subBags[pos];
                if (this.removeItemFromBagHelper(subBag, (Entity)item)) {
                    Engine.getPersistenceManager().setDirty((Entity)iInfo);
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected boolean removeItemFromBagHelper(final OID subBagOid, final Entity item) {
        final Bag subBag = getBag(subBagOid);
        if (subBag == null) {
            Log.warn("removeItemFromBagHelper: did not find sub bag: " + subBagOid);
            return false;
        }
        final Integer slotNum = subBag.findItem(item.getOid());
        if (slotNum == null) {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItemFromBagHelper: item not in bag itemOid=" + item.getOid() + " bagOid=" + subBagOid);
            }
            return false;
        }
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("removeItemFromBagHelper: found - slot=" + slotNum + ", itemOid=" + item.getOid());
        }
        final boolean rv = subBag.removeItem(item.getOid());
        if (!rv) {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItemFromBagHelper: remove item failed");
            }
            return false;
        }
        item.setProperty("inv.backref", (Serializable)null);
        Engine.getPersistenceManager().setDirty(item);
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("removeItemFromBagHelper: remove from bag, rv=" + rv);
        }
        return rv;
    }
    
    public void updateObject(final OID mobOid, final OID target) {
        if (!mobOid.equals((Object)target)) {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("updateObject: obj is not a player, ignoring: " + mobOid);
            }
            return;
        }
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("updateObject: obj is a player: " + mobOid);
        }
        final InventoryInfo iInfo = getInventoryInfo(mobOid);
        if (iInfo != null) {
            Log.debug("AgisInventoryPlugin - sending inventory update");
            this.sendInvUpdate(mobOid);
        }
        else {
            AgisInventoryPlugin.log.debug("updateObject: could not find entity in " + Namespace.BAG + " for mobOid " + mobOid);
        }
        if (VendorPlugin.useVirtualCurrency()) {
            Log.debug("AgisInventoryPlugin - sending token balance");
            VendorClient.sendBalanceUpdate(mobOid, BillingClient.getTokenBalance(mobOid));
        }
    }
    
    public void sendLootList(final OID oid, final OID mobOid) {
        final LinkedList<OID> itemOids = (LinkedList<OID>)EnginePlugin.getObjectProperty(mobOid, InventoryClient.NAMESPACE, "loot");
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "LootList");
        props.put("lootTarget", (Serializable)mobOid);
        Log.debug("LOOT: list to send down to client: " + itemOids);
        int numItems = 0;
        final List<Integer> questItemReqs = QuestClient.getQuestItemReqs(oid);
        for (int itemPos = 0; itemPos < itemOids.size(); ++itemPos) {
            boolean itemExists = true;
            final OID itemOid = itemOids.get(itemPos);
            if (oid == null) {
                itemExists = false;
            }
            final AgisItem item = getAgisItem(itemOid);
            if (item == null) {
                Log.warn("sendInvUpdateHelper: item is null, oid=" + itemOid);
                itemExists = false;
            }
            boolean itemNeeded = true;
            if (item.getItemType().equals("Quest")) {
                itemNeeded = false;
                Log.debug("ANDREW: found quest item " + item.getName() + ". does the player need it?");
                for (int i = 0; i < questItemReqs.size(); ++i) {
                    Log.debug("ANDREW: checking questItem req: " + questItemReqs.get(i) + " against " + item.getTemplateID());
                    if (questItemReqs.get(i) == item.getTemplateID()) {
                        itemNeeded = true;
                        Log.debug("ANDREW: found quest item - it was needed");
                    }
                }
            }
            if (itemExists && itemOids.contains(item.getOid()) && itemNeeded) {
                if (Log.loggingDebug) {
                    AgisInventoryPlugin.log.debug("sendInvUpdateHelper: adding bagPos=" + itemPos + ", itemOid=" + oid + ", itemName=" + item.getName() + ",icon=" + item.getIcon());
                }
                props.put("item_" + numItems + "Name", item.getName());
                props.put("item_" + numItems + "BaseName", item.getProperty("baseName"));
                props.put("item_" + numItems + "Id", (Serializable)item.getOid());
                props.put("item_" + numItems + "Count", item.getStackSize());
                props.put("item_" + numItems + "SlotNum", itemPos);
                if (item.getProperty("energyCost") != null) {
                    props.put("item_" + numItems + "EnergyCost", item.getProperty("energyCost"));
                }
                else {
                    props.put("item_" + numItems + "EnergyCost", 0);
                }
                if (item.getProperty("resistanceStats") != null) {
                    int numResist = 0;
                    final HashMap<String, Integer> resistances = (HashMap<String, Integer>)item.getProperty("resistanceStats");
                    for (final String resistance : resistances.keySet()) {
                        props.put("item_" + numItems + "Resist_" + numResist + "Name", resistance);
                        props.put("item_" + numItems + "Resist_" + numResist + "Value", resistances.get(resistance));
                        ++numResist;
                    }
                    props.put("item_" + numItems + "NumResistances", numResist);
                }
                else {
                    props.put("item_" + numItems + "NumResistances", 0);
                }
                if (item.getProperty("bonusStats") != null) {
                    int numStats = 0;
                    final HashMap<String, Integer> stats = (HashMap<String, Integer>)item.getProperty("bonusStats");
                    for (final String statName : stats.keySet()) {
                        props.put("item_" + numItems + "Stat_" + numStats + "Name", statName);
                        props.put("item_" + numItems + "Stat_" + numStats + "Value", stats.get(statName));
                        ++numStats;
                    }
                    props.put("item_" + numItems + "NumStats", numStats);
                }
                else {
                    props.put("item_" + numItems + "NumStats", 0);
                }
                if (item.getItemType().equals("Weapon")) {
                    props.put("item_" + numItems + "Speed", item.getProperty("speed"));
                    props.put("item_" + numItems + "DamageType", item.getProperty("attackType"));
                    props.put("item_" + numItems + "DamageValue", item.getProperty("damage"));
                }
                ++numItems;
            }
        }
        if (numItems == 0) {
            final PropertyMessage propMsg2 = new PropertyMessage(mobOid, mobOid);
            propMsg2.setProperty("lootable", (Serializable)false);
            Engine.getAgent().sendBroadcast((Message)propMsg2);
            Log.debug("LOOT: sending lootable = false");
        }
        props.put("numItems", numItems);
        final WorldManagerClient.TargetedExtensionMessage TEmsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)TEmsg);
    }
    
    protected boolean lootAll(final OID looterOid, final OID mobOid) {
        AgisInventoryPlugin.log.debug("lootAll: looterOid=" + looterOid + ", mobOid=" + mobOid);
        this.lock.lock();
        this.lock.unlock();
        EnginePlugin.setObjectPropertyNoResponse(mobOid, Namespace.WORLD_MANAGER, "lootable", (Serializable)Boolean.FALSE);
        this.sendInvUpdate(looterOid);
        return true;
    }
    
    protected boolean lootAllHelper(final OID looterOid, final Bag looterRootBag, final OID mobOid, final Bag mobRootBag, final Bag mobSubBag) {
        final List<Integer> questItemReqs = QuestClient.getQuestItemReqs(looterOid);
        Log.debug("ANDREW - got questItemReqs response: " + questItemReqs.toString());
        int numItems = 2;
        int equippedItems = 0;
        for (int slotNum = 0; slotNum < mobSubBag.getNumSlots(); ++slotNum) {
            final EquipMap emap = this.getEquipMap(mobOid);
            final OID itemOid = mobSubBag.getItem(slotNum);
            if (itemOid == null) {
                AgisInventoryPlugin.log.debug("lootAllHelper: slotNum " + slotNum + " is empty");
            }
            else if (emap.containsValue(itemOid)) {
                ++equippedItems;
            }
            else {
                ++numItems;
            }
        }
        final Random rand = new Random();
        int roll = rand.nextInt(numItems);
        roll += equippedItems;
        Log.debug("ANDREW: roll is: " + roll + "; num items: " + numItems + "; equipped items: " + equippedItems);
        for (int slotNum2 = 0; slotNum2 < mobSubBag.getNumSlots(); ++slotNum2) {
            final EquipMap emap2 = this.getEquipMap(mobOid);
            final OID itemOid2 = mobSubBag.getItem(slotNum2);
            if (itemOid2 == null || emap2.containsValue(itemOid2)) {
                AgisInventoryPlugin.log.debug("lootAllHelper: slotNum " + slotNum2 + " is empty");
            }
            else {
                boolean questItem = false;
                AgisInventoryPlugin.log.debug("lootAllHelper: processing sub bags item slot=" + slotNum2 + " oid=" + itemOid2);
                boolean rv = this.removeItemFromBag(mobRootBag.getOid(), itemOid2);
                AgisInventoryPlugin.log.debug("lootAllHelper: removed oid=" + itemOid2 + ", rv=" + rv);
                if (rv) {
                    final AgisItem item = getAgisItem(itemOid2);
                    if (item.getItemType().equals("Quest")) {
                        Log.debug("ANDREW - found quest item " + item.getName() + ". does the player need it?");
                        int needed = 0;
                        for (int i = 0; i < questItemReqs.size(); ++i) {
                            if (questItemReqs.get(i) == item.getTemplateID()) {
                                needed = 1;
                            }
                        }
                        if (needed == 0) {
                            continue;
                        }
                        Log.debug("ANDREW - found quest item - it was needed");
                        questItem = true;
                    }
                    if (item != null) {
                        ObjectManagerClient.setPersistenceFlag(itemOid2, true);
                        if (slotNum2 == roll) {
                            rv = this.addItem(looterOid, looterRootBag.getOid(), itemOid2);
                            AgisInventoryPlugin.log.debug("lootAllHelper: addItem to looter, oid=" + itemOid2 + ", rv=" + rv);
                            if (rv) {
                                WorldManagerClient.sendObjChatMsg(looterOid, 2, "You have recieved: " + item.getName());
                            }
                        }
                    }
                }
            }
        }
        AgisInventoryPlugin.log.debug("lootAllHelper: done processing subbag " + mobSubBag);
        return true;
    }
    
    protected boolean containsItem(final OID mobOid, final OID itemOid) {
        this.lock.lock();
        try {
            final AgisItem item = getAgisItem(itemOid);
            if (item == null) {
                return false;
            }
            if (item.isDeleted()) {
                return false;
            }
            final OID subBagOid = (OID)item.getProperty("inv.backref");
            if (subBagOid == null) {
                return false;
            }
            final Bag subBag = getBag(subBagOid);
            if (subBag == null) {
                return false;
            }
            final OID rootBagOid = (OID)subBag.getProperty("inv.backref");
            return rootBagOid != null && mobOid.equals((Object)rootBagOid);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected OID findItem(final OID mobOid, final int templateID) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("findItem: mob=" + mobOid + " template=" + templateID);
            }
            if (mobOid == null) {
                AgisInventoryPlugin.log.debug("findItem: cant find rootBagOid");
                return null;
            }
            final InventoryInfo iInfo = getInventoryInfo(mobOid);
            if (iInfo == null) {
                AgisInventoryPlugin.log.debug("findItem: could not find root bag");
                return null;
            }
            final ArrayList<OID> resultList = new ArrayList<OID>();
            this.findItemHelper(mobOid, iInfo, templateID, resultList);
            return resultList.get(0);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected ArrayList<OID> findItemStacks(final OID mobOid, final int templateID) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("findItem: mob=" + mobOid + " template=" + templateID);
            }
            if (mobOid == null) {
                AgisInventoryPlugin.log.debug("findItem: cant find rootBagOid");
                return null;
            }
            final InventoryInfo iInfo = getInventoryInfo(mobOid);
            if (iInfo == null) {
                AgisInventoryPlugin.log.debug("findItem: could not find root bag");
                return null;
            }
            final ArrayList<OID> resultList = new ArrayList<OID>();
            this.findItemStacksHelper(mobOid, iInfo, templateID, resultList);
            return resultList;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected void findItemStacksHelper(final OID mobOid, final InventoryInfo iInfo, final int templateID, final ArrayList<OID> resultList) {
        final OID[] subBags = iInfo.getBags();
        for (int i = 0; i < subBags.length - 1; ++i) {
            final OID subBagOid = subBags[i];
            if (subBagOid != null) {
                final Bag subBag = getBag(subBagOid);
                OID[] itemsList;
                for (int length = (itemsList = subBag.getItemsList()).length, j = 0; j < length; ++j) {
                    final OID itemOid = itemsList[j];
                    if (itemOid != null) {
                        final AgisItem item = getAgisItem(itemOid);
                        if (templateID == item.getTemplateID()) {
                            if (!resultList.contains(itemOid)) {
                                if (Log.loggingDebug) {
                                    AgisInventoryPlugin.log.debug("findItemHelper: adding item to resultList=" + itemOid);
                                }
                                resultList.add(itemOid);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected ArrayList<OID> findItems(final OID mobOid, final ArrayList<Integer> templateList) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("findItem: mob=" + mobOid + " templateList=" + templateList);
            }
            if (mobOid == null) {
                AgisInventoryPlugin.log.debug("findItem: cant find rootBagOid");
                return null;
            }
            final InventoryInfo iInfo = getInventoryInfo(mobOid);
            if (iInfo == null) {
                AgisInventoryPlugin.log.debug("findItem: could not find root bag");
                return null;
            }
            final ArrayList<OID> resultList = new ArrayList<OID>();
            for (final int template : templateList) {
                this.findItemHelper(mobOid, iInfo, template, resultList);
            }
            return resultList;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected boolean findItemHelper(final OID mobOid, final InventoryInfo iInfo, final int templateID, final ArrayList<OID> resultList) {
        final OID[] subBags = iInfo.getBags();
        for (int i = 0; i < subBags.length - 1; ++i) {
            final OID subBagOid = subBags[i];
            if (subBagOid != null) {
                final Bag subBag = getBag(subBagOid);
                OID[] itemsList;
                for (int length = (itemsList = subBag.getItemsList()).length, j = 0; j < length; ++j) {
                    final OID itemOid = itemsList[j];
                    if (itemOid != null) {
                        final AgisItem item = getAgisItem(itemOid);
                        if (templateID == item.getTemplateID()) {
                            if (!resultList.contains(itemOid)) {
                                if (Log.loggingDebug) {
                                    AgisInventoryPlugin.log.debug("findItemHelper: adding item to resultList=" + itemOid);
                                }
                                resultList.add(itemOid);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        resultList.add(null);
        return false;
    }
    
    protected OID findItem(final OID mobOid, final AgisEquipSlot slot) {
        this.lock.lock();
        try {
            final EquipMap equipMap = this.getEquipMap(mobOid);
            final OID itemOid = equipMap.get(slot);
            if (itemOid != null) {
                return equipMap.get(slot);
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected OID removeItem(final OID mobOid, final OID itemOid, final boolean removeFromPlayer) {
        this.lock.lock();
        try {
            final AgisItem item = getAgisItem(itemOid);
            if (item == null) {
                return null;
            }
            this.unequipItem(item, mobOid, false);
            if (mobOid == null) {
                AgisInventoryPlugin.log.debug("removeItem: cant find rootBagOid");
                return null;
            }
            final Boolean result = this.removeItemFromBag(mobOid, itemOid);
            if (result) {
                if (removeFromPlayer) {
                    item.unacquired(mobOid);
                }
                return itemOid;
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected OID removeItem(final OID mobOid, final int template, final boolean removeFromPlayer) {
        this.lock.lock();
        try {
            final OID itemOid = this.findItem(mobOid, template);
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItem: mobOid=" + mobOid + " template=" + template + " ItemOid=" + itemOid);
            }
            return this.removeItem(mobOid, itemOid, removeFromPlayer);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected ArrayList<OID> removeItems(final OID mobOid, final ArrayList<Integer> templateList, final boolean removeFromPlayer) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItems: mobOid=" + mobOid + " templateList=" + templateList);
            }
            final ArrayList<OID> itemList = this.findItems(mobOid, templateList);
            for (final OID itemOid : itemList) {
                if (itemOid != null) {
                    this.removeItem(mobOid, itemOid, removeFromPlayer);
                }
            }
            for (int i = itemList.size() - 1; i >= 0; --i) {
                if (itemList.get(i) == null) {
                    itemList.remove(i);
                }
            }
            return itemList;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    protected boolean activateObject(final OID objOid, final OID activatorOid, final OID targetOid) {
        final AgisItem item = getAgisItem(objOid);
        if (item == null) {
            Log.warn("ActivateHook: item is null, oid=" + objOid);
            return false;
        }
        return item.activate(activatorOid, targetOid);
    }
    
    public boolean isItemEquipped(final AOObject itemObj, final OID activatorOid) {
        final AgisItem item = AgisItem.convert(itemObj);
        final AgisEquipSlot slot = item.getPrimarySlot();
        if (slot == null) {
            return false;
        }
        final EquipMap equipMap = this.getEquipMap(activatorOid);
        final OID oItemOid = equipMap.get(slot);
        if (oItemOid != null && oItemOid.equals((Object)item.getOid())) {
            Log.debug("EQUIP: got isEquipped item: " + oItemOid);
            return true;
        }
        return false;
    }
    
    public boolean equipItem(final AOObject itemObj, final OID activatorOid, final boolean replace) {
        AgisInventoryPlugin.log.debug("AgisInventoryPlugin.equipItem: item=" + itemObj + ", activatorOid=" + activatorOid);
        final AgisItem item = AgisItem.convert(itemObj);
        final PermissionCallback cb = item.permissionCallback();
        if (cb != null && !cb.use(activatorOid)) {
            AgisInventoryPlugin.log.warn("permission callback failed");
            return false;
        }
        final AgisEquipSlot slot = item.getPrimarySlot();
        if (slot == null) {
            Log.warn("AgisInventoryPlugin: slot is null for item: " + item);
            return false;
        }
        this.lock.lock();
        try {
            final EquipMap equipMap = this.getEquipMap(activatorOid);
            final OID oItemOid = equipMap.get(slot);
            if (oItemOid != null) {
                final AgisItem oItemObj = getAgisItem(oItemOid);
                AgisInventoryPlugin.log.debug("EQUIPITEM: item = " + oItemObj);
                if (Log.loggingDebug) {
                    AgisInventoryPlugin.log.debug("AgisInventoryPlugin: slot occupied");
                }
                if (!replace) {
                    return false;
                }
                this.unequipItem(oItemObj, activatorOid, true);
            }
            final OID itemOid = this.removeItem(activatorOid, item.getOid(), false);
            this.placeEquippedItem(activatorOid, itemOid, slot);
            equipMap.put(slot, item.getMasterOid());
            this.setDirty(activatorOid);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        AgisInventoryClient.itemEquipStatusChanged(activatorOid, item, true, slot.toString());
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("AgisInventoryPlugin: calling addDC, activatorOid=" + activatorOid + ", item=" + item);
        }
        final String displayVal = (String)item.getProperty("displayVal");
        EquipHelper.updateDisplay(activatorOid, displayVal, item.getPrimarySlot());
        this.sendInvUpdate(activatorOid);
        return true;
    }
    
    public boolean unequipItem(final AOObject itemObj, final OID activatorOid, final boolean isReplaced) {
        AgisInventoryPlugin.log.debug("AgisInventoryPlugin.unequipItem: item=" + itemObj + ", mobOid=" + activatorOid);
        final AgisItem item = AgisItem.convert(itemObj);
        final PermissionCallback cb = item.permissionCallback();
        if (cb != null && !cb.use(activatorOid)) {
            AgisInventoryPlugin.log.warn("callback failed");
            return false;
        }
        this.lock.lock();
        try {
            final EquipMap equipMap = this.getEquipMap(activatorOid);
            final AgisEquipSlot slot = equipMap.getSlot(item.getMasterOid());
            if (slot == null) {
                Log.warn("AgisInventoryPlugin.unequipItem: item not equipped: item=" + item);
                return false;
            }
            final InventoryInfo iInfo = getInventoryInfo(activatorOid);
            final boolean hasSpace = this.hasSpace(activatorOid, item.getTemplateID(), 1, 0);
            if (hasSpace) {
                equipMap.remove(slot);
                this.removeEquippedItem(activatorOid, item.getOid(), slot);
                this.addItem(activatorOid, iInfo.getOid(), item.getOid());
                this.setDirty(activatorOid);
                AgisInventoryClient.itemEquipStatusChanged(activatorOid, item, false, slot.toString());
                if (!isReplaced) {
                    EquipHelper.updateDisplay(activatorOid, null, item.getPrimarySlot());
                }
                if (Log.loggingDebug) {
                    AgisInventoryPlugin.log.debug("AgisInventoryPlugin.unequipItem: removed DC for item:" + item);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        this.sendInvUpdate(activatorOid);
        return true;
    }
    
    public boolean hasSpace(final OID looterOid, final int itemID, int count, final int invType) {
        this.lock.lock();
        int spaceNeeded = 0;
        int freeSpaces = 0;
        try {
            if (looterOid == null) {
                AgisInventoryPlugin.log.debug("lootAll: cant find rootLooterBagOid");
                return false;
            }
            final InventoryInfo iInfo = getInventoryInfo(looterOid);
            if (invType == 0) {
                final Template tmpl = ObjectManagerClient.getTemplate(itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
                final int stackLimit = (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "stackLimit");
                final ArrayList<OID> existingOids = this.findItemStacks(looterOid, itemID);
                if (existingOids.size() > 0) {
                    Log.debug("ITEM: user already has item " + itemID + ", see if we can add it to one of the stacks.");
                    for (final OID existingOid : existingOids) {
                        final AgisItem tempItem = getAgisItem(existingOid);
                        Log.debug("ITEM: stackSize " + tempItem.getStackSize() + ", stackLimit: " + tempItem.getStackLimit() + "for item: " + existingOid);
                        if (tempItem.getStackSize() < tempItem.getStackLimit()) {
                            Log.debug("ITEM: reducing count in hasSpace for item: " + itemID);
                            count -= tempItem.getStackLimit() - tempItem.getStackSize();
                            if (count <= 0) {
                                Log.debug("ITEM: hasSpace check has been fulfilled before creating new stacks for item: " + itemID);
                                count = 0;
                                break;
                            }
                            continue;
                        }
                    }
                }
                if (count > 0) {
                    spaceNeeded = (count - 1) / stackLimit + 1;
                    Log.debug("ITEM: item " + itemID + " needs " + spaceNeeded + " with a count of " + count + " and stackLimit of " + stackLimit);
                    final OID[] subBags = iInfo.getBags();
                    for (int pos = 0; pos < subBags.length - 1; ++pos) {
                        final OID subBagOid = subBags[pos];
                        final Bag subBag = getBag(subBagOid);
                        if (subBag == null) {
                            Log.warn("hasSpace: did not find sub bag: " + subBagOid + " for bagoid=" + subBagOid);
                        }
                        else {
                            for (int numSlots = subBag.getNumSlots(), i = 0; i < numSlots; ++i) {
                                if (subBag.getItem(i) == null) {
                                    Log.debug("hasSpace: bag has free space at spot: " + i);
                                    ++freeSpaces;
                                }
                            }
                        }
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        Log.debug("hasSpace: freeSpaces = " + freeSpaces + "; spaceNeeded = " + spaceNeeded);
        return freeSpaces >= spaceNeeded;
    }
    
    public boolean placeEquippedItem(final OID activatorOid, final OID itemOid, final AgisEquipSlot slot) {
        Log.debug("placeEquippedItem: slot = " + slot);
        final InventoryInfo iInfo = getInventoryInfo(activatorOid);
        final OID[] subBags = iInfo.getBags();
        final OID subBagOid = subBags[this.INVENTORY_BAG_COUNT];
        final Bag subBag = getBag(subBagOid);
        final Entity item = (Entity)getAgisItem(itemOid);
        int slotNum = -1;
        if (slot.equals(AgisEquipSlot.PRIMARYWEAPON)) {
            slotNum = 0;
        }
        else if (slot.equals(AgisEquipSlot.SECONDARYWEAPON)) {
            slotNum = 1;
        }
        if (slot.equals(AgisEquipSlot.HEAD)) {
            slotNum = 2;
        }
        else if (slot.equals(AgisEquipSlot.BACK) || slot.equals(AgisEquipSlot.CAPE)) {
            slotNum = 3;
        }
        else if (slot.equals(AgisEquipSlot.SHIRT)) {
            slotNum = 4;
        }
        else if (slot.equals(AgisEquipSlot.SHOULDER)) {
            slotNum = 5;
        }
        else if (slot.equals(AgisEquipSlot.CHEST)) {
            slotNum = 6;
        }
        else if (slot.equals(AgisEquipSlot.HANDS)) {
            slotNum = 7;
        }
        else if (slot.equals(AgisEquipSlot.LEGS)) {
            slotNum = 8;
        }
        else if (slot.equals(AgisEquipSlot.FEET)) {
            slotNum = 9;
        }
        else if (slot.equals(AgisEquipSlot.NECK)) {
            slotNum = 10;
        }
        else if (slot.equals(AgisEquipSlot.BELT)) {
            slotNum = 11;
        }
        else if (slot.equals(AgisEquipSlot.PRIMARYRING)) {
            slotNum = 12;
        }
        else if (slot.equals(AgisEquipSlot.SECONDARYRING)) {
            slotNum = 13;
        }
        if (slotNum == -1) {
            Log.debug("placeEquippedItem: slot is -1");
            return false;
        }
        final boolean rv = subBag.putItem(slotNum, itemOid);
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("placeEquippedItem: adding to bag, rv=" + rv);
        }
        if (rv) {
            item.setProperty("inv.backref", (Serializable)subBagOid);
        }
        Engine.getPersistenceManager().setDirty(item);
        Engine.getPersistenceManager().setDirty((Entity)iInfo);
        return rv;
    }
    
    public boolean removeEquippedItem(final OID activatorOid, final OID itemOid, final AgisEquipSlot slot) {
        final InventoryInfo iInfo = getInventoryInfo(activatorOid);
        final OID[] subBags = iInfo.getBags();
        final OID subBagOid = subBags[this.INVENTORY_BAG_COUNT];
        final Bag subBag = getBag(subBagOid);
        final Entity item = (Entity)getAgisItem(itemOid);
        final Integer slotNum = subBag.findItem(item.getOid());
        if (slotNum == null) {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItemFromBagHelper: item not in bag itemOid=" + item.getOid() + " bagOid=" + subBagOid);
            }
            return false;
        }
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("removeItemFromBagHelper: found - slot=" + slotNum + ", itemOid=" + item.getOid());
        }
        final boolean rv = subBag.removeItem(item.getOid());
        if (!rv) {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("removeItemFromBagHelper: remove item failed");
            }
            return false;
        }
        item.setProperty("inv.backref", (Serializable)null);
        Engine.getPersistenceManager().setDirty(item);
        Engine.getPersistenceManager().setDirty((Entity)iInfo);
        if (Log.loggingDebug) {
            AgisInventoryPlugin.log.debug("removeItemFromBagHelper: remove from bag, rv=" + rv);
        }
        return rv;
    }
    
    private boolean createAndSendMail(final OID senderOid, final OID recipientOid, final String recipientName, final String subject, final String message, final ArrayList<OID> items, final int currencyType, final int currencyAmount, final boolean CoD) {
        final String senderName = WorldManagerClient.getObjectInfo(senderOid).name;
        if (currencyAmount > 0 && !CoD) {
            final Currency c = (Currency)Agis.CurrencyManager.get(currencyType);
            if (c == null) {
                ExtendedCombatMessages.sendErrorMessage(senderOid, "Invalid Currency");
                return false;
            }
            if (this.getMobCurrency(senderOid, currencyType) < currencyAmount) {
                ExtendedCombatMessages.sendErrorMessage(senderOid, "You cannot send more " + c.getCurrencyName() + " than you have");
                return false;
            }
        }
        Log.debug("MAIL: creating mail");
        final Mail m = new Mail(-1, recipientOid, recipientName, senderOid, senderName, subject, message, currencyType, currencyAmount, items, 1, CoD);
        AgisInventoryPlugin.aDB.addNewMail(m);
        for (final OID itemOid : items) {
            if (itemOid != null) {
                this.removeItem(senderOid, itemOid, true);
            }
        }
        if (currencyAmount > 0 && !CoD) {
            this.alterMobCurrency(senderOid, currencyType, -currencyAmount);
        }
        Log.debug("MAIL: created mail object");
        final InventoryInfo iInfo = getInventoryInfo(recipientOid);
        if (iInfo != null) {
            iInfo.addMail(m);
            WorldManagerClient.sendObjChatMsg(recipientOid, 2, "You have recieved new mail from " + senderName);
        }
        this.sendBagInvUpdate(senderOid);
        return true;
    }
    
    private Mail getMailByID(final ArrayList<Mail> mailList, final int mailID) {
        for (final Mail m : mailList) {
            if (m.getID() == mailID) {
                return m;
            }
        }
        return null;
    }
    
    private void sendMailList(final OID playerOid) {
        final InventoryInfo iInfo = getInventoryInfo(playerOid);
        final ArrayList<Mail> mailList = iInfo.getMail();
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "MailList");
        props.put("numMail", mailList.size());
        for (int pos = 0; pos < mailList.size(); ++pos) {
            final Mail m = mailList.get(pos);
            props.put("mail_" + pos + "ID", m.getID());
            props.put("mail_" + pos + "SenderOid", (Serializable)m.getSenderOID());
            props.put("mail_" + pos + "SenderName", m.getSenderName());
            props.put("mail_" + pos + "Subject", m.getSubject());
            props.put("mail_" + pos + "Message", m.getMessage());
            props.put("mail_" + pos + "Read", m.getMailRead());
            for (int i = 0; i < m.getItems().size(); ++i) {
                if (m.getItems().get(i) == null) {
                    props.put("mail_" + pos + "ItemTemplate" + i, -1);
                    props.put("mail_" + pos + "ItemName" + i, "");
                    props.put("mail_" + pos + "ItemCount" + i, 0);
                }
                else {
                    final AgisItem item = getAgisItem(m.getItems().get(i));
                    props.put("mail_" + pos + "ItemTemplate" + i, item.getTemplateID());
                    props.put("mail_" + pos + "ItemName" + i, item.getName());
                    props.put("mail_" + pos + "ItemCount" + i, item.getStackSize());
                }
            }
            props.put("mail_" + pos + "NumItems", m.getItems().size());
            props.put("mail_" + pos + "CurrencyType", m.getCurrencyType());
            props.put("mail_" + pos + "CurrencyAmount", m.getCurrencyAmount());
            props.put("mail_" + pos + "CoD", m.getCoD());
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public EquipMap getEquipMap(final OID activatorOid) {
        this.lock.lock();
        try {
            final InventoryInfo subObj = getInventoryInfo(activatorOid);
            EquipMap map = (EquipMap)subObj.getProperty("equipMap");
            if (map == null) {
                map = new EquipMap();
                subObj.setProperty("equipMap", (Serializable)map);
                Engine.getPersistenceManager().setDirty((Entity)subObj);
            }
            return map;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setDirty(final OID mobOid) {
        final InventoryInfo subObj = getInventoryInfo(mobOid);
        Engine.getPersistenceManager().setDirty((Entity)subObj);
    }
    
    public static float getSlotModifier(final String slot) {
        if (slot.equals("Head")) {
            return 0.28f;
        }
        if (slot.equals("Shoulder")) {
            return 0.14f;
        }
        if (slot.equals("Chest")) {
            return 0.28f;
        }
        if (slot.equals("Hands")) {
            return 0.14f;
        }
        if (slot.equals("Waist")) {
            return 0.16f;
        }
        if (slot.equals("Legs")) {
            return 0.27f;
        }
        if (slot.equals("Feet")) {
            return 0.14f;
        }
        if (slot.equals("Back")) {
            return 0.15f;
        }
        if (slot.equals("Neck")) {
            return 0.16f;
        }
        if (slot.equals("Ring")) {
            return 0.14f;
        }
        if (slot.equals("Main Hand")) {
            return 0.25f;
        }
        if (slot.equals("Off Hand")) {
            return 0.25f;
        }
        if (slot.equals("Any Hand")) {
            return 0.25f;
        }
        if (slot.equals("Two Hand")) {
            return 0.5f;
        }
        return 0.0f;
    }
    
    public static float getGradeModifier(final int grade) {
        if (grade == 1) {
            return 0.667f;
        }
        if (grade == 2) {
            return 0.8f;
        }
        if (grade == 3) {
            return 1.0f;
        }
        if (grade == 4) {
            return 1.25f;
        }
        if (grade == 5) {
            return 1.55f;
        }
        if (grade == 6) {
            return 1.95f;
        }
        return 0.5f;
    }
    
    public static float getStatModifier(final String statName) {
        return 1.0f;
    }
    
    public static List<Float> getArmourTypeModifier(final String armourType) {
        final List<Float> armourModifiers = new LinkedList<Float>();
        if (armourType.equals("Silk")) {
            armourModifiers.add(0.1f);
            armourModifiers.add(0.9f);
        }
        else if (armourType.equals("Wool")) {
            armourModifiers.add(0.2f);
            armourModifiers.add(0.8f);
        }
        else if (armourType.equals("Soft Leather")) {
            armourModifiers.add(0.3f);
            armourModifiers.add(0.7f);
        }
        else if (armourType.equals("Hard Leather")) {
            armourModifiers.add(0.4f);
            armourModifiers.add(0.6f);
        }
        else if (armourType.equals("Chain")) {
            armourModifiers.add(0.5f);
            armourModifiers.add(0.5f);
        }
        else if (armourType.equals("Scale")) {
            armourModifiers.add(0.6f);
            armourModifiers.add(0.4f);
        }
        else if (armourType.equals("Branded")) {
            armourModifiers.add(0.7f);
            armourModifiers.add(0.3f);
        }
        else if (armourType.equals("Plate")) {
            armourModifiers.add(0.8f);
            armourModifiers.add(0.2f);
        }
        else {
            armourModifiers.add(0.5f);
            armourModifiers.add(0.5f);
        }
        return armourModifiers;
    }
    
    public static List<Float> getArmourSlotModifier(final String armourSlot) {
        final List<Float> armourModifiers = new LinkedList<Float>();
        if (armourSlot.equals("Head")) {
            armourModifiers.add(0.12f);
            armourModifiers.add(0.11f);
        }
        else if (armourSlot.equals("Chest")) {
            armourModifiers.add(0.22f);
            armourModifiers.add(0.06f);
        }
        else if (armourSlot.equals("Legs")) {
            armourModifiers.add(0.21f);
            armourModifiers.add(0.06f);
        }
        else if (armourSlot.equals("Hands")) {
            armourModifiers.add(0.11f);
            armourModifiers.add(0.03f);
        }
        else if (armourSlot.equals("Feet")) {
            armourModifiers.add(0.11f);
            armourModifiers.add(0.03f);
        }
        else if (armourSlot.equals("Shoulder")) {
            armourModifiers.add(0.11f);
            armourModifiers.add(0.03f);
        }
        else if (armourSlot.equals("Waist")) {
            armourModifiers.add(0.02f);
            armourModifiers.add(0.14f);
        }
        else if (armourSlot.equals("Back")) {
            armourModifiers.add(0.05f);
            armourModifiers.add(0.1f);
        }
        else if (armourSlot.equals("Neck")) {
            armourModifiers.add(0.0f);
            armourModifiers.add(0.16f);
        }
        else if (armourSlot.equals("Ring")) {
            armourModifiers.add(0.0f);
            armourModifiers.add(0.14f);
        }
        else {
            armourModifiers.add(0.05f);
            armourModifiers.add(0.05f);
        }
        return armourModifiers;
    }
    
    static /* synthetic */ ObjectLockManager access$0(final AgisInventoryPlugin agisInventoryPlugin) {
        return agisInventoryPlugin.getObjectLockManager();
    }
    
    public enum ItemAcquireResult
    {
        SUCCESS("SUCCESS", 0), 
        INSUFFICIENT_CURRENCY("INSUFFICIENT_CURRENCY", 1), 
        STACK_LIMIT_REACHED("STACK_LIMIT_REACHED", 2), 
        ITEM_DOESNT_EXIST("ITEM_DOESNT_EXIST", 3), 
        UNKNOWN_FAILURE("UNKNOWN_FAILURE", 4);
        
        private ItemAcquireResult(final String s, final int n) {
        }
    }
    
    class ReloadItemsHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            Log.debug("RELOAD: reloading items from database");
            final ItemDatabase iDB = new ItemDatabase(false);
            AgisInventoryPlugin.this.loadItemsFromDatabase(iDB);
            iDB.close();
            final OID objOid = eMsg.getSubject();
            WorldManagerClient.sendObjChatMsg(objOid, 2, "Reloading items from the database.");
            return true;
        }
    }
    
    protected class CreateInventoryHook implements Hook
    {
        protected OID masterOid;
        protected String invItems;
        
        public CreateInventoryHook(final OID masterOid, final String invItems) {
            this.masterOid = masterOid;
            this.invItems = invItems;
        }
        
        public boolean processMessage(final Message msg, final int flags) {
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("CreateInventoryHook.processMessage: masterOid=" + this.masterOid + " invItems=" + this.invItems);
            }
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(this.masterOid);
            if (this.invItems == null) {
                return true;
            }
            if (this.invItems.equals("")) {
                return true;
            }
            AgisInventoryPlugin.this.createInventoryItems(this.masterOid, iInfo, this.invItems);
            return true;
        }
    }
    
    public class CategoryUpdatedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisMobClient.categoryUpdatedMessage pMsg = (AgisMobClient.categoryUpdatedMessage)msg;
            final OID playerOid = pMsg.getSubject();
            final int category = (int)pMsg.getProperty("category");
            Log.debug("CATEGORY: updating category for player " + playerOid + " and category: " + category);
            final Lock lock = AgisInventoryPlugin.access$0(AgisInventoryPlugin.this).getLock(playerOid);
            lock.lock();
            try {
                final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(playerOid);
                this.unequipOldItems(iInfo);
                AgisInventoryPlugin.this.saveInventory(iInfo, Namespace.BAG);
                AgisInventoryPlugin.this.unloadInventory(iInfo);
                if (iInfo.categoryUpdated(category)) {
                    AgisInventoryPlugin.this.createBags(iInfo, playerOid);
                    if (WorldManagerClient.getObjectInfo(playerOid).objType == ObjectTypes.player) {
                        AgisInventoryPlugin.this.createInventoryItems(playerOid, iInfo, "");
                    }
                }
                else {
                    AgisInventoryPlugin.this.loadInventory(iInfo);
                    OID[] bags;
                    for (int length = (bags = iInfo.getBags()).length, i = 0; i < length; ++i) {
                        final OID bagOid = bags[i];
                        final Bag bag = AgisInventoryPlugin.getBag(bagOid);
                        Log.debug("BAG: bag slots: " + bag.getNumSlots());
                    }
                }
                this.equipNewItems(iInfo);
                Log.debug("CATEGORY: updating category for player " + playerOid + " complete");
            }
            finally {
                lock.unlock();
            }
            lock.unlock();
            AgisInventoryPlugin.this.sendBagInvUpdate(playerOid);
            return true;
        }
        
        private void unequipOldItems(final InventoryInfo iInfo) {
            final OID bagOid = iInfo.getBags()[4];
            final Bag equipBag = AgisInventoryPlugin.getBag(bagOid);
            OID[] itemsList;
            for (int length = (itemsList = equipBag.getItemsList()).length, i = 0; i < length; ++i) {
                final OID itemOID = itemsList[i];
                if (itemOID != null) {
                    final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOID);
                    AgisInventoryPlugin.this.lock.lock();
                    try {
                        final EquipMap equipMap = AgisInventoryPlugin.this.getEquipMap(iInfo.getOid());
                        final AgisEquipSlot slot = equipMap.getSlot(item.getMasterOid());
                        if (slot == null) {
                            Log.warn("AgisInventoryPlugin.unequipItem: item not equipped: item=" + item);
                            continue;
                        }
                        equipMap.remove(slot);
                        if (Log.loggingDebug) {
                            AgisInventoryPlugin.log.debug("AgisInventoryPlugin.unequipItem: removed DC for item:" + item);
                        }
                    }
                    finally {
                        AgisInventoryPlugin.this.lock.unlock();
                    }
                    AgisInventoryPlugin.this.lock.unlock();
                    AgisInventoryClient.itemEquipStatusChanged(iInfo.getOid(), item, false, item.getPrimarySlot().toString());
                    EquipHelper.updateDisplay(iInfo.getOid(), null, item.getPrimarySlot());
                }
            }
        }
        
        private void equipNewItems(final InventoryInfo iInfo) {
            final OID bagOid = iInfo.getBags()[4];
            final Bag equipBag = AgisInventoryPlugin.getBag(bagOid);
            OID[] itemsList;
            for (int length = (itemsList = equipBag.getItemsList()).length, i = 0; i < length; ++i) {
                final OID itemOID = itemsList[i];
                if (itemOID != null) {
                    final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOID);
                    final AgisEquipSlot slot = item.getPrimarySlot();
                    if (slot == null) {
                        Log.warn("AgisInventoryPlugin: slot is null for item: " + item);
                    }
                    else {
                        AgisInventoryPlugin.this.lock.lock();
                        try {
                            final EquipMap equipMap = AgisInventoryPlugin.this.getEquipMap(iInfo.getOid());
                            equipMap.put(slot, item.getMasterOid());
                        }
                        finally {
                            AgisInventoryPlugin.this.lock.unlock();
                        }
                        AgisInventoryPlugin.this.lock.unlock();
                        AgisInventoryClient.itemEquipStatusChanged(iInfo.getOid(), item, true, item.getPrimarySlot().toString());
                        final String displayVal = (String)item.getProperty("displayVal");
                        EquipHelper.updateDisplay(iInfo.getOid(), displayVal, item.getPrimarySlot());
                    }
                }
            }
        }
    }
    
    class AgisFindItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final InventoryClient.RemoveOrFindItemMessage findMsg = (InventoryClient.RemoveOrFindItemMessage)msg;
            final OID mobOid = findMsg.getSubject();
            final String method = findMsg.getMethod();
            AgisInventoryPlugin.log.debug("AgisFindItemHook: got message");
            if (method.equals("slot")) {
                final AgisEquipSlot slot = (AgisEquipSlot)findMsg.getPayload();
                final OID resultOid = AgisInventoryPlugin.this.findItem(mobOid, slot);
                Engine.getAgent().sendOIDResponse((Message)findMsg, resultOid);
            }
            else {
                Log.error("AgisFindItemHook: unknown method=" + method);
            }
            return true;
        }
    }
    
    class SendInventoryUpdateHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.SendInventoryUpdateMessage findMsg = (AgisInventoryClient.SendInventoryUpdateMessage)msg;
            final OID mobOid = findMsg.getSubject();
            AgisInventoryPlugin.this.sendBagInvUpdate(mobOid);
            return true;
        }
    }
    
    class GetGenericItemDataHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            Template itemTmpl = null;
            final int itemID = (int)getMsg.getProperty("itemID");
            itemTmpl = ObjectManagerClient.getTemplate(itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
            Engine.getAgent().sendObjectResponse((Message)getMsg, (Object)itemTmpl);
            return true;
        }
    }
    
    class GetSpecificItemDataHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.getSpecificItemDataMessage getMsg = (AgisInventoryClient.getSpecificItemDataMessage)msg;
            final OID oid = getMsg.getSubject();
            final OID targetOid = (OID)getMsg.getProperty("targetOid");
            final ArrayList<Long> itemOids = (ArrayList<Long>)getMsg.getProperty("itemOids");
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "TargetItemData");
            int numItems = 0;
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(targetOid);
            if (iInfo == null) {
                return true;
            }
            final OID[] items = iInfo.getBags();
            for (int bagPos = 0; bagPos < items.length; ++bagPos) {
                final OID subBagOid = items[bagPos];
                if (subBagOid == null) {
                    AgisInventoryPlugin.log.error("sendInvUpdate: sub bag oid is null");
                }
                else {
                    final Bag subBag = AgisInventoryPlugin.getBag(subBagOid);
                    if (subBag == null) {
                        AgisInventoryPlugin.log.error("sendInvUpdate: sub bag obj is null");
                    }
                    else {
                        final OID[] itemsInBag = subBag.getItemsList();
                        for (int itemPos = 0; itemPos < itemsInBag.length; ++itemPos) {
                            boolean itemExists = true;
                            final OID itemOid = itemsInBag[itemPos];
                            if (oid == null) {
                                itemExists = false;
                            }
                            final AgisItem item = AgisInventoryPlugin.getAgisItem(oid);
                            if (item == null) {
                                Log.warn("sendInvUpdateHelper: item is null, oid=" + oid);
                                itemExists = false;
                            }
                            if (itemExists && !props.containsValue(item.getName()) && itemOids.contains(item.getOid())) {
                                if (Log.loggingDebug) {
                                    AgisInventoryPlugin.log.debug("sendInvUpdateHelper: adding bagNum=" + bagPos + ", bagPos=" + itemPos + ", itemOid=" + oid + ", itemName=" + item.getName() + ",icon=" + item.getIcon());
                                }
                                props.put("item_" + numItems + "Name", item.getName());
                                props.put("item_" + numItems + "Id", (Serializable)item.getOid());
                                props.put("item_" + numItems + "Count", item.getStackSize());
                                props.put("item_" + numItems + "BagNum", bagPos);
                                props.put("item_" + numItems + "SlotNum", itemPos);
                                if (item.getProperty("energyCost") != null) {
                                    props.put("item_" + numItems + "EnergyCost", item.getProperty("energyCost"));
                                }
                                if (item.getProperty("resistanceStats") != null) {
                                    int numResist = 0;
                                    final HashMap<String, Integer> resistances = (HashMap<String, Integer>)item.getProperty("resistanceStats");
                                    for (final String resistance : resistances.keySet()) {
                                        props.put("item_" + numItems + "Resist_" + numResist + "Name", resistance);
                                        props.put("item_" + numItems + "Resist_" + numResist + "Value", resistances.get(resistance));
                                        ++numResist;
                                    }
                                    props.put("item_" + numItems + "NumResistances", numResist);
                                }
                                else {
                                    props.put("item_" + numItems + "NumResistances", 0);
                                }
                                if (item.getProperty("bonusStats") != null) {
                                    int numStats = 0;
                                    final HashMap<String, Integer> stats = (HashMap<String, Integer>)item.getProperty("bonusStats");
                                    for (final String statName : stats.keySet()) {
                                        props.put("item_" + numItems + "Stat_" + numStats + "Name", statName);
                                        props.put("item_" + numItems + "Stat_" + numStats + "Value", stats.get(statName));
                                        ++numStats;
                                    }
                                    props.put("item_" + numItems + "NumStats", numStats);
                                }
                                else {
                                    props.put("item_" + numItems + "NumStats", 0);
                                }
                                if (item.getItemType().equals("Weapon")) {
                                    props.put("item_" + numItems + "Speed", item.getProperty("speed"));
                                    props.put("item_" + numItems + "DamageType", item.getProperty("attackType"));
                                    props.put("item_" + numItems + "DamageValue", item.getProperty("damage"));
                                }
                                ++numItems;
                            }
                        }
                    }
                }
            }
            props.put("numItems", numItems);
            final WorldManagerClient.TargetedExtensionMessage TEmsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)TEmsg);
            return true;
        }
    }
    
    class PlaceBagHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.placeBagMessage removeMsg = (AgisInventoryClient.placeBagMessage)msg;
            final OID oid = removeMsg.getSubject();
            final OID itemOid = (OID)removeMsg.getProperty("itemOid");
            final int bagSpotNum = (int)removeMsg.getProperty("bagSpotNum");
            AgisInventoryPlugin.this.lock.lock();
            try {
                final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOid);
                if (item == null) {
                    return true;
                }
                final OID rootBagOid = oid;
                if (rootBagOid == null) {
                    AgisInventoryPlugin.log.debug("placeBag: cant find rootBagOid");
                    return true;
                }
                final int oldBagID = AgisInventoryPlugin.this.changeBagInSlot(item, oid, bagSpotNum);
                if (oldBagID > 0) {
                    AgisInventoryClient.generateItem(oid, oldBagID, item.getName(), 1, null);
                }
            }
            finally {
                AgisInventoryPlugin.this.lock.unlock();
            }
            AgisInventoryPlugin.this.lock.unlock();
            AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            return true;
        }
    }
    
    class MoveBagHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.moveBagMessage genMsg = (AgisInventoryClient.moveBagMessage)msg;
            final OID mobOid = genMsg.getSubject();
            final int parentBagSlotNum = (int)genMsg.getProperty("bagSpotNum");
            final int newSlotNum = (int)genMsg.getProperty("newSpotNum");
            if (parentBagSlotNum == 0 || newSlotNum == 0) {
                ExtendedCombatMessages.sendErrorMessage(mobOid, "You cannot swap out your Backpack.");
                return true;
            }
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(mobOid);
            if (iInfo == null) {
                return true;
            }
            final OID[] subBags = iInfo.getBags();
            final OID subBagOid1 = subBags[parentBagSlotNum];
            final Bag subBag1 = AgisInventoryPlugin.getBag(subBagOid1);
            final OID[] itemsInBag1 = subBag1.getItemsList();
            final String bagName1 = subBag1.getName();
            final int bagNumSlots1 = subBag1.getNumSlots();
            final OID subBagOid2 = subBags[newSlotNum];
            final Bag subBag2 = AgisInventoryPlugin.getBag(subBagOid2);
            final OID[] itemsInBag2 = subBag2.getItemsList();
            final String bagName2 = subBag2.getName();
            final int bagNumSlots2 = subBag2.getNumSlots();
            subBag1.setName(bagName2);
            subBag1.setNumSlots(bagNumSlots2);
            subBag1.setItemsList(itemsInBag2);
            subBag2.setName(bagName1);
            subBag2.setNumSlots(bagNumSlots1);
            subBag2.setItemsList(itemsInBag1);
            Engine.getPersistenceManager().setDirty((Entity)iInfo);
            AgisInventoryPlugin.this.sendBagInvUpdate(mobOid);
            return true;
        }
    }
    
    class RemoveBagHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.removeBagMessage genMsg = (AgisInventoryClient.removeBagMessage)msg;
            final OID mobOid = genMsg.getSubject();
            final int parentBagSlotNum = (int)genMsg.getProperty("bagSpotNum");
            final int containerId = (int)genMsg.getProperty("containerId");
            final int slotId = (int)genMsg.getProperty("slotId");
            if (parentBagSlotNum == 0) {
                ExtendedCombatMessages.sendErrorMessage(mobOid, "You cannot swap out your Backpack.");
                return true;
            }
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(mobOid);
            if (iInfo == null) {
                return true;
            }
            final OID[] subBags = iInfo.getBags();
            final OID subBagOid1 = subBags[parentBagSlotNum];
            final Bag subBag1 = AgisInventoryPlugin.getBag(subBagOid1);
            final OID[] itemsInBag1 = subBag1.getItemsList();
            for (int i = 0; i < itemsInBag1.length; ++i) {
                Log.debug("BAG: checking items in bag for removal. Item: " + itemsInBag1[i]);
                if (itemsInBag1[i] != null) {
                    ExtendedCombatMessages.sendErrorMessage(mobOid, "You cannot remove a bag that has items inside it.");
                    return true;
                }
            }
            final int bagID1 = subBag1.getID();
            final String bagName1 = subBag1.getName();
            if (containerId != -1) {
                final OID subBagOid2 = subBags[containerId];
                final Bag subBag2 = AgisInventoryPlugin.getBag(subBagOid2);
                final OID[] itemsInBag2 = subBag2.getItemsList();
                if (itemsInBag2[slotId] != null) {
                    ExtendedCombatMessages.sendErrorMessage(mobOid, "You must place the bag in an empty slot.");
                    return true;
                }
                final OID itemOid = AgisInventoryPlugin.this.generateItem(bagID1, bagName1);
                if (!AgisInventoryPlugin.this.placeItem(mobOid, mobOid, itemOid, containerId, slotId)) {
                    ExtendedCombatMessages.sendErrorMessage(mobOid, "You must place the bag in an empty slot.");
                    return true;
                }
            }
            subBag1.setNumSlots(0);
            subBag1.setName("");
            subBag1.setID(-1);
            Engine.getPersistenceManager().setDirty((Entity)iInfo);
            AgisInventoryPlugin.this.sendBagInvUpdate(mobOid);
            return true;
        }
    }
    
    class MoveItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage moveItemMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID mobOid = moveItemMsg.getSubject();
            final int containerId = (int)moveItemMsg.getProperty("bagNum");
            final int slotId = (int)moveItemMsg.getProperty("slotNum");
            final OID itemOid = (OID)moveItemMsg.getProperty("itemOid");
            Log.debug("ITEM: got move item message with containerId: " + containerId + " and slot: " + slotId + " for item: " + itemOid);
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(mobOid);
            if (iInfo == null) {
                return true;
            }
            final OID[] subBags = iInfo.getBags();
            if (containerId != -1) {
                final OID subBagOid = subBags[containerId];
                final Bag subBag = AgisInventoryPlugin.getBag(subBagOid);
                final OID[] itemsInBag = subBag.getItemsList();
                final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOid);
                final OID oldBagID = (OID)item.getProperty("inv.backref");
                Log.debug("ITEM: old bag id: " + oldBagID);
                Bag oldSubBag = subBag;
                if (!oldBagID.equals((Object)subBagOid)) {
                    oldSubBag = AgisInventoryPlugin.getBag(oldBagID);
                    Log.debug("ITEM: old bag is different to new bag: " + oldSubBag);
                }
                if (itemsInBag[slotId] != null) {
                    final AgisItem oldItem = AgisInventoryPlugin.getAgisItem(itemsInBag[slotId]);
                    Log.debug("ITEM: old item: " + oldItem.getOid() + " new item: " + itemOid);
                    if (oldItem.getName().equals(item.getName()) && !itemOid.equals((Object)oldItem.getOid())) {
                        final int stackSizeToAdd = item.getStackSize();
                        if (oldItem.getStackSize() + stackSizeToAdd <= oldItem.getStackLimit()) {
                            oldItem.setStackSize(oldItem.getStackSize() + stackSizeToAdd);
                            Engine.getPersistenceManager().setDirty((Entity)oldItem);
                            oldSubBag.removeItem(itemOid);
                            Engine.getPersistenceManager().setDirty((Entity)iInfo);
                            AgisInventoryPlugin.this.sendBagInvUpdate(mobOid);
                            return true;
                        }
                    }
                    if (!itemOid.equals((Object)oldItem.getOid())) {
                        ExtendedCombatMessages.sendErrorMessage(mobOid, "You must place the item in an empty slot.");
                    }
                    return true;
                }
                if (AgisInventoryPlugin.this.isItemEquipped(item, mobOid)) {
                    AgisInventoryPlugin.this.unequipItem(item, mobOid, false);
                    return true;
                }
                oldSubBag.removeItem(itemOid);
                if (!AgisInventoryPlugin.this.placeItem(mobOid, mobOid, itemOid, containerId, slotId)) {
                    ExtendedCombatMessages.sendErrorMessage(mobOid, "Could not place item into destination slot");
                    return true;
                }
            }
            AgisInventoryPlugin.this.sendBagInvUpdate(mobOid);
            return true;
        }
    }
    
    class GenerateItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.generateItemMessage genMsg = (AgisInventoryClient.generateItemMessage)msg;
            final OID oid = genMsg.getSubject();
            final int templateID = (int)genMsg.getProperty("itemID");
            String itemName = (String)genMsg.getProperty("itemName");
            final int count = (int)genMsg.getProperty("count");
            final HashMap<String, Serializable> itemProps = (HashMap<String, Serializable>)genMsg.getProperty("itemProps");
            if (itemName == null || itemName.equals("")) {
                final Template tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
                itemName = tmpl.getName();
            }
            AgisInventoryPlugin.this.addItemFromTemplate(oid, templateID, count, itemProps);
            Log.debug("ITEM: finished generation and adding of item: " + templateID);
            AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            return true;
        }
    }
    
    class CreatePetItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.generateItemMessage genMsg = (AgisInventoryClient.generateItemMessage)msg;
            final OID oid = genMsg.getSubject();
            final String templateName = (String)genMsg.getProperty("itemName");
            Log.debug("ITEM: finished generation and adding of item: " + templateName);
            return true;
        }
    }
    
    class CheckCurrencyHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.checkCurrencyMessage purMsg = (AgisInventoryClient.checkCurrencyMessage)msg;
            final OID oid = purMsg.getSubject();
            final int currencyID = purMsg.getCurrencyID();
            final int cost = purMsg.getCount();
            final int currencyAmount = AgisInventoryPlugin.this.getMobCurrency(oid, currencyID);
            if (currencyAmount < cost) {
                Engine.getAgent().sendBooleanResponse((Message)purMsg, false);
                Log.debug("CURRENCY: not enough money");
                return true;
            }
            Log.debug("CURRENCY: has enough money");
            Engine.getAgent().sendBooleanResponse((Message)purMsg, true);
            return true;
        }
    }
    
    class GetMerchantListHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = getMsg.getSubject();
            final int merchantTable = (int)getMsg.getProperty("merchantTable");
            return true;
        }
    }
    
    class PurchaseItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.purchaseItemMessage purMsg = (AgisInventoryClient.purchaseItemMessage)msg;
            final OID oid = purMsg.getSubject();
            final int templateID = purMsg.getItemID();
            int count = purMsg.getCount();
            Log.debug("PURCHASE: attempting to purchase " + count + " of item " + templateID);
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("ext_msg_subtype", "item_purchase_result");
            final Template itemTemplate = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
            props.put("itemName", itemTemplate.getName());
            final WorldManagerClient.TargetedExtensionMessage resultMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
            final Template tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
            final int purchaseCurrency = (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "purchaseCurrency");
            final int cost = (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "purchaseCost");
            final int currencyAmount = AgisInventoryPlugin.this.getMobCurrency(oid, purchaseCurrency);
            final Currency c = (Currency)Agis.CurrencyManager.get(purchaseCurrency);
            if (currencyAmount < cost * count) {
                props.put("result", "insufficient_funds");
                props.put("currency", c.getCurrencyName());
                Engine.getAgent().sendBroadcast((Message)resultMsg);
                Log.debug("PURCHASE: not enough funds: " + currencyAmount + " for cost: " + cost * count);
                Engine.getAgent().sendBooleanResponse((Message)purMsg, false);
                return true;
            }
            Log.debug("PURCHASE: has enough money");
            final String eventTrigger = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerEvent");
            if (eventTrigger.equals("ITEM_PURCHASED") || eventTrigger.equals("ITEM_OBTAINED")) {
                final String eventAction = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Type");
                if (eventAction.equals("addWeapon")) {
                    final String eventActionData = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Data");
                    final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
                    final int uses = 10 + 15 * (int)tmpl.get(InventoryClient.ITEM_NAMESPACE, "itemGrade");
                    final HashMap<String, Serializable> loggerProps = new HashMap<String, Serializable>();
                    loggerProps.put("weapon", eventActionData);
                    DataLoggerClient.logData("WEAPON_ADDED", oid, null, accountID, loggerProps);
                    final int delta = -cost * count;
                    AgisInventoryPlugin.this.alterMobCurrency(oid, purchaseCurrency, delta);
                    AgisInventoryPlugin.this.sendBagInvUpdate(oid);
                    props.put("result", "equipment_success");
                    Engine.getAgent().sendBroadcast((Message)resultMsg);
                    Engine.getAgent().sendBooleanResponse((Message)purMsg, true);
                    return true;
                }
                if (eventAction.equals("addItem")) {
                    final String eventActionData = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Data");
                    final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
                    int stackSize = count;
                    try {
                        stackSize = Integer.parseInt(eventActionData);
                    }
                    catch (NumberFormatException ex) {}
                    if (!AgisInventoryPlugin.aDB.addItem(accountID, templateID, stackSize)) {
                        AgisInventoryPlugin.aDB.alterItemAmount(accountID, templateID, stackSize);
                    }
                    final HashMap<String, Serializable> loggerProps = new HashMap<String, Serializable>();
                    loggerProps.put("item", eventActionData);
                    DataLoggerClient.logData("ITEM_ADDED", oid, null, accountID, loggerProps);
                    final int delta = -cost * count;
                    AgisInventoryPlugin.this.alterMobCurrency(oid, purchaseCurrency, delta);
                    AgisInventoryPlugin.this.sendBagInvUpdate(oid);
                    props.put("result", "success");
                    Engine.getAgent().sendBroadcast((Message)resultMsg);
                    Engine.getAgent().sendBooleanResponse((Message)purMsg, true);
                    return true;
                }
                if (eventAction.contains("addCurrency")) {
                    final int delta2 = -cost * count;
                    final int currency = Integer.parseInt(eventAction.substring(eventAction.length() - 1));
                    final String eventActionData2 = (String)tmpl.get(InventoryClient.ITEM_NAMESPACE, "triggerAction1Data");
                    count *= Integer.parseInt(eventActionData2);
                    final OID accountID2 = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
                    final HashMap<String, Serializable> loggerProps2 = new HashMap<String, Serializable>();
                    loggerProps2.put("currency", currency);
                    DataLoggerClient.logData("CURRENCY_ADDED", oid, null, accountID2, loggerProps2);
                    AgisInventoryPlugin.this.alterMobCurrency(oid, currency, count);
                    AgisInventoryPlugin.this.alterMobCurrency(oid, purchaseCurrency, delta2);
                    AgisInventoryPlugin.this.sendBagInvUpdate(oid);
                    if (count > 1) {
                        ExtendedCombatMessages.sendCombatText(oid, "+" + count + " Tokens", 9);
                    }
                    else if (count == 1) {
                        ExtendedCombatMessages.sendCombatText(oid, "+" + count + " Token", 9);
                    }
                    Engine.getAgent().sendBooleanResponse((Message)purMsg, true);
                    return true;
                }
            }
            if (!AgisInventoryPlugin.this.hasSpace(oid, templateID, count, 0)) {
                props.put("result", "insufficient_space");
                Engine.getAgent().sendBroadcast((Message)resultMsg);
                ExtendedCombatMessages.sendErrorMessage(oid, "You do not have enough space in your inventory to purchase that item.");
                Log.debug("PURCHASE: not enough space: ");
                Engine.getAgent().sendBooleanResponse((Message)purMsg, false);
                return true;
            }
            final boolean itemAdded = AgisInventoryPlugin.this.addItemFromTemplate(oid, templateID, count, null);
            if (itemAdded) {
                final int delta2 = -cost * count;
                AgisInventoryPlugin.this.alterMobCurrency(oid, purchaseCurrency, delta2);
                AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            }
            Log.debug("ITEM: finished generation and adding of item: " + templateID);
            props.put("result", "success");
            Engine.getAgent().sendBroadcast((Message)resultMsg);
            Engine.getAgent().sendBooleanResponse((Message)purMsg, true);
            return true;
        }
    }
    
    class SellItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage removeMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = removeMsg.getSubject();
            final String sellType = (String)removeMsg.getProperty("sellType");
            if (sellType.equals("Account")) {
                final int itemID = (int)removeMsg.getProperty("itemID");
                Log.debug("SELL: got account sell for item: " + itemID);
            }
            final OID itemOid = (OID)removeMsg.getProperty("itemOid");
            Log.debug("SELL: got sell for item: " + itemOid);
            AgisInventoryPlugin.this.lock.lock();
            try {
                final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOid);
                if (item == null) {
                    return true;
                }
                AgisInventoryPlugin.this.unequipItem(item, oid, false);
                final OID rootBagOid = oid;
                if (rootBagOid == null) {
                    AgisInventoryPlugin.log.debug("removeItem: cant find rootBagOid");
                    return true;
                }
                Log.debug("SELL: got root bag");
                if (item.getPurchaseCurrency() < 1) {
                    ExtendedCombatMessages.sendErrorMessage(oid, "You cannot sell an item that has no value.");
                    return true;
                }
                final boolean sellable = item.getBooleanProperty("sellable");
                if (!sellable) {
                    ExtendedCombatMessages.sendErrorMessage(oid, "You cannot sell that item.");
                    return true;
                }
                final boolean removed = AgisInventoryPlugin.this.removeItemFromBag(rootBagOid, itemOid);
                if (removed) {
                    Log.debug("SELL: removed Item");
                    int delta = (int)(item.getPurchaseCost() / 4.0f) * item.getStackSize();
                    if (delta == 0 && item.getPurchaseCost() > 0) {
                        delta = 1;
                    }
                    AgisInventoryPlugin.this.alterMobCurrency(oid, item.getPurchaseCurrency(), delta);
                    item.unacquired(rootBagOid);
                }
                else {
                    Log.debug("SELL: remove failed");
                }
            }
            finally {
                AgisInventoryPlugin.this.lock.unlock();
            }
            AgisInventoryPlugin.this.lock.unlock();
            AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            return true;
        }
    }
    
    class AlterItemCountHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage purMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = purMsg.getSubject();
            Log.debug("ALTER: got item alter message");
            final int templateID = (int)purMsg.getProperty("itemID");
            final int count = (int)purMsg.getProperty("count");
            final Template tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.ITEM_TEMPLATE);
            if (tmpl == null) {
                Log.error("PICKUP: item ID: " + templateID + " does not exist");
                return true;
            }
            AgisInventoryPlugin.this.addItemFromTemplate(playerOid, templateID, count, null);
            WorldManagerClient.sendObjChatMsg(playerOid, 2, "Received " + tmpl.getName() + " x" + count);
            AgisInventoryPlugin.this.sendBagInvUpdate(playerOid);
            return true;
        }
    }
    
    class PickupItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage purMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = purMsg.getSubject();
            int templateID = (int)purMsg.getProperty("itemID");
            int count = (int)purMsg.getProperty("count");
            int itemsAcquired = 0;
            if (templateID == -1) {
                final HashMap<Integer, Integer> lootTables = (HashMap<Integer, Integer>)purMsg.getProperty("lootTables");
                Log.error("LOOT: num loot tables: " + lootTables.size());
                for (final int lootTable : lootTables.keySet()) {
                    final int tableChance = lootTables.get(lootTable);
                    final int roll = AgisInventoryPlugin.random.nextInt(100);
                    Log.error("LOOT: roll for loot table: " + lootTable + " is: " + roll + " with tablechance: " + tableChance);
                    if (roll < tableChance) {
                        Log.debug("LOOT: lootManager has: " + Agis.LootTableManager.getMap());
                        final LootTable lt = (LootTable)Agis.LootTableManager.get(lootTable);
                        final int itemNum = lt.getRandomItemNum();
                        Log.debug("LOOT: randomised item number: " + itemNum);
                        templateID = lt.getItems().get(itemNum);
                        count = lt.getItemCounts().get(itemNum);
                        if (AgisInventoryPlugin.this.accountItemAcquired(oid, templateID, count) != ItemAcquireResult.SUCCESS) {
                            continue;
                        }
                        ++itemsAcquired;
                    }
                }
            }
            else if (AgisInventoryPlugin.this.accountItemAcquired(oid, templateID, count) == ItemAcquireResult.SUCCESS) {
                ++itemsAcquired;
            }
            AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            if (itemsAcquired == 0) {
                ExtendedCombatMessages.sendCombatText(oid, "No items found", 9);
            }
            return true;
        }
    }
    
    class AlterCurrencyHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.alterCurrencyMessage alterMsg = (AgisInventoryClient.alterCurrencyMessage)msg;
            final OID oid = alterMsg.getSubject();
            final int currencyType = alterMsg.getCurrencyType();
            final int delta = alterMsg.getDelta();
            AgisInventoryPlugin.this.alterMobCurrency(oid, currencyType, delta);
            Engine.getPersistenceManager().setDirty((Entity)AgisInventoryPlugin.getInventoryInfo(oid));
            return true;
        }
    }
    
    class TradeStartReqHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage startMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID trader1Oid = (OID)startMsg.getProperty("requesterOid");
            final OID trader2Oid = (OID)startMsg.getProperty("partnerOid");
            Log.debug("TradeStartReqHook: trader1=" + trader1Oid + " trader2=" + trader2Oid);
            if (AgisInventoryPlugin.this.tradeSessionMap.containsKey(trader1Oid) || AgisInventoryPlugin.this.tradeSessionMap.containsKey(trader2Oid)) {
                AgisInventoryPlugin.sendTradeComplete(trader1Oid, trader2Oid, (byte)4);
                return true;
            }
            final TradeSession tradeSession = new TradeSession(trader1Oid, trader2Oid);
            AgisInventoryPlugin.this.tradeSessionMap.put(trader1Oid, tradeSession);
            AgisInventoryPlugin.this.tradeSessionMap.put(trader2Oid, tradeSession);
            AgisInventoryPlugin.sendTradeStart(trader1Oid, trader2Oid);
            AgisInventoryPlugin.sendTradeStart(trader2Oid, trader1Oid);
            return true;
        }
    }
    
    class TradeOfferReqHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage tradeMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID trader1 = (OID)tradeMsg.getProperty("requesterOid");
            final OID trader2 = (OID)tradeMsg.getProperty("partnerOid");
            Log.debug("TradeOfferReqHook: trader1=" + trader1 + " trader2=" + trader2);
            final TradeSession tradeSession = AgisInventoryPlugin.this.tradeSessionMap.get(trader1);
            if (tradeSession == null || !tradeSession.isTrader(trader2)) {
                AgisInventoryPlugin.sendTradeComplete(trader1, trader2, (byte)3);
                if (tradeSession != null) {
                    AgisInventoryPlugin.this.tradeSessionMap.remove(trader1);
                    final OID partner = tradeSession.getPartnerOid(trader1);
                    AgisInventoryPlugin.this.tradeSessionMap.remove(partner);
                    AgisInventoryPlugin.sendTradeComplete(partner, trader1, (byte)3);
                }
                return true;
            }
            final List<OID> offer = (List<OID>)tradeMsg.getProperty("offerItems");
            final boolean cancelled = (boolean)tradeMsg.getProperty("cancelled");
            if (cancelled || !AgisInventoryPlugin.this.validateTradeOffer(trader1, offer)) {
                byte status = 3;
                if (cancelled) {
                    status = 2;
                }
                AgisInventoryPlugin.this.tradeSessionMap.remove(trader1);
                AgisInventoryPlugin.this.tradeSessionMap.remove(trader2);
                AgisInventoryPlugin.sendTradeComplete(trader1, trader2, status);
                AgisInventoryPlugin.sendTradeComplete(trader2, trader1, status);
            }
            final boolean accepted = (boolean)tradeMsg.getProperty("accepted");
            tradeSession.updateOffer(trader1, offer, accepted);
            if (tradeSession.isComplete()) {
                AgisInventoryPlugin.this.tradeSessionMap.remove(trader1);
                AgisInventoryPlugin.this.tradeSessionMap.remove(trader2);
                AgisInventoryPlugin.sendTradeComplete(trader1, trader2, (byte)1);
                AgisInventoryPlugin.sendTradeComplete(trader2, trader1, (byte)1);
                AgisInventoryPlugin.this.completeTrade(tradeSession);
                return true;
            }
            AgisInventoryPlugin.sendTradeOfferUpdate(trader1, trader2, tradeSession);
            AgisInventoryPlugin.sendTradeOfferUpdate(trader2, trader1, tradeSession);
            return true;
        }
    }
    
    class DespawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.DespawnedMessage despawnedMsg = (WorldManagerClient.DespawnedMessage)msg;
            final OID oid = despawnedMsg.getSubject();
            final TradeSession tradeSession = AgisInventoryPlugin.this.tradeSessionMap.get(oid);
            if (tradeSession != null) {
                final OID trader1 = tradeSession.getTrader1();
                final OID trader2 = tradeSession.getTrader2();
                AgisInventoryPlugin.this.tradeSessionMap.remove(trader1);
                AgisInventoryPlugin.this.tradeSessionMap.remove(trader2);
                AgisInventoryPlugin.sendTradeComplete(trader1, trader2, (byte)3);
                AgisInventoryPlugin.sendTradeComplete(trader2, trader1, (byte)3);
            }
            return true;
        }
    }
    
    class GenerateLootHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.generateLootMessage lootMsg = (AgisInventoryClient.generateLootMessage)msg;
            final OID mobOid = lootMsg.getSubject();
            Log.debug("LOOT: Generating Loot for mob: " + mobOid);
            final HashMap<Integer, Integer> itemsToAdd = new HashMap<Integer, Integer>();
            final LinkedList<OID> itemsToLoot = new LinkedList<OID>();
            final HashMap<Integer, Integer> mobLootTables = (HashMap<Integer, Integer>)EnginePlugin.getObjectProperty(mobOid, InventoryClient.NAMESPACE, "lootTables");
            for (final int lootTable : mobLootTables.keySet()) {
                final int tableChance = mobLootTables.get(lootTable);
                final int roll = AgisInventoryPlugin.random.nextInt(100);
                Log.debug("LOOT: roll for loot table: " + lootTable + " is: " + roll + " with tablechance: " + tableChance);
                if (roll < tableChance) {
                    Log.debug("LOOT: lootManager has: " + Agis.LootTableManager.getMap());
                    final LootTable lt = (LootTable)Agis.LootTableManager.get(lootTable);
                    final int itemNum = lt.getRandomItemNum();
                    Log.debug("LOOT: randomised item number: " + itemNum);
                    final int templateID = lt.getItems().get(itemNum);
                    if (templateID <= -1) {
                        continue;
                    }
                    final int count = lt.getItemCounts().get(itemNum);
                    if (itemsToAdd.containsKey(templateID)) {
                        itemsToAdd.put(templateID, itemsToAdd.get(templateID) + count);
                    }
                    else {
                        itemsToAdd.put(templateID, count);
                    }
                }
            }
            for (final int templateID2 : itemsToAdd.keySet()) {
                final Template tmpl = ObjectManagerClient.getTemplate(templateID2, ObjectManagerPlugin.ITEM_TEMPLATE);
                final OID itemOid = AgisInventoryPlugin.this.generateItem(templateID2, tmpl.getName());
                final boolean itemAdded = InventoryClient.addItem(mobOid, mobOid, mobOid, itemOid);
                for (int i = 1; i < itemsToAdd.get(templateID2); ++i) {
                    final OID iOid = AgisInventoryPlugin.this.generateItem(templateID2, tmpl.getName());
                    final boolean itemAdded2 = InventoryClient.addItem(mobOid, mobOid, mobOid, iOid);
                    Log.debug("LOOT: adding item2: " + tmpl.getName() + " to mobs loot with result: " + itemAdded2);
                }
                Log.debug("LOOT: adding item: " + tmpl.getName() + " to mobs loot with result: " + itemAdded);
                if (itemAdded) {
                    itemsToLoot.add(itemOid);
                }
            }
            EnginePlugin.setObjectProperty(mobOid, InventoryClient.NAMESPACE, "loot", (Serializable)itemsToLoot);
            Log.debug("Mob: " + mobOid + " now has loot: " + itemsToLoot);
            return true;
        }
    }
    
    class GetLootListHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.getLootListMessage getMsg = (AgisInventoryClient.getLootListMessage)msg;
            final OID oid = getMsg.getSubject();
            final OID mobOid = (OID)getMsg.getProperty("mobOid");
            AgisInventoryPlugin.this.sendLootList(oid, mobOid);
            final CoordinatedEffect cE = new CoordinatedEffect("LootEffect");
            cE.sendSourceOid(true);
            cE.sendTargetOid(true);
            cE.invoke(oid, oid);
            return true;
        }
    }
    
    class LootAllHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.lootAllMessage lootMsg = (AgisInventoryClient.lootAllMessage)msg;
            final OID looterOid = lootMsg.getSubject();
            final OID mobOid = (OID)lootMsg.getProperty("mobOid");
            final OID accountId = (OID)EnginePlugin.getObjectProperty(looterOid, WorldManagerClient.NAMESPACE, "accountId");
            final LinkedList<OID> loot = (LinkedList<OID>)EnginePlugin.getObjectProperty(mobOid, InventoryClient.NAMESPACE, "loot");
            final LinkedList<OID> leftOverLoot = new LinkedList<OID>();
            for (int i = 0; i < loot.size(); ++i) {
                final OID itemOid = loot.get(i);
                boolean rv = AgisInventoryPlugin.this.removeItemFromBag(mobOid, itemOid);
                AgisInventoryPlugin.log.debug("LOOT: removed oid=" + itemOid + ", rv=" + rv);
                if (rv) {
                    final Entity item = (Entity)AgisInventoryPlugin.getAgisItem(itemOid);
                    if (item != null) {
                        ObjectManagerClient.setPersistenceFlag(itemOid, true);
                        final HashMap<String, Serializable> logData = new HashMap<String, Serializable>();
                        logData.put("item", "Item attempt to loot : " + item.getName() + " : OID " + itemOid + " : ");
                        logData.put("playerOid", (Serializable)looterOid);
                        DataLoggerClient.logData("ITEM_LOOTED_EVENT", looterOid, mobOid, accountId, logData);
                        rv = AgisInventoryPlugin.this.addItem(looterOid, looterOid, itemOid);
                        AgisInventoryPlugin.log.debug("LOOT: addItem to looter, oid=" + itemOid + ", rv=" + rv);
                        if (!rv) {
                            DataLoggerClient.logData("ITEM_LOOTED_FAILED_EVENT", looterOid, itemOid, accountId, logData);
                            AgisInventoryPlugin.this.addItem(mobOid, mobOid, itemOid);
                            leftOverLoot.add(itemOid);
                        }
                        else {
                            final String lootedItem = item.getName();
                            if (lootedItem != null) {
                                WorldManagerClient.sendObjChatMsg(looterOid, 2, "You have looted: " + lootedItem);
                            }
                        }
                    }
                    else {
                        leftOverLoot.add(itemOid);
                    }
                }
            }
            EnginePlugin.setObjectProperty(mobOid, InventoryClient.NAMESPACE, "loot", (Serializable)leftOverLoot);
            AgisInventoryPlugin.this.sendBagInvUpdate(looterOid);
            AgisInventoryPlugin.this.sendLootList(looterOid, mobOid);
            return true;
        }
    }
    
    class LootItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.lootItemMessage lootMsg = (AgisInventoryClient.lootItemMessage)msg;
            final OID looterOid = lootMsg.getSubject();
            final OID mobOid = (OID)lootMsg.getProperty("mobOid");
            final OID itemOid = (OID)lootMsg.getProperty("itemOid");
            final OID accountId = (OID)EnginePlugin.getObjectProperty(looterOid, WorldManagerClient.NAMESPACE, "accountId");
            final LinkedList<OID> loot = (LinkedList<OID>)EnginePlugin.getObjectProperty(mobOid, InventoryClient.NAMESPACE, "loot");
            boolean rv = AgisInventoryPlugin.this.removeItemFromBag(mobOid, itemOid);
            AgisInventoryPlugin.log.debug("LOOT: removed oid=" + itemOid + ", rv=" + rv);
            if (!rv) {
                return true;
            }
            final Entity item = (Entity)AgisInventoryPlugin.getAgisItem(itemOid);
            if (item == null) {
                return true;
            }
            ObjectManagerClient.setPersistenceFlag(itemOid, true);
            final HashMap<String, Serializable> logData = new HashMap<String, Serializable>();
            logData.put("item", "Item attempt to loot : " + item.getName() + " : OID " + itemOid + " : ");
            logData.put("playerOid", (Serializable)looterOid);
            DataLoggerClient.logData("ITEM_LOOTED_EVENT", looterOid, mobOid, accountId, logData);
            rv = AgisInventoryPlugin.this.addItem(looterOid, looterOid, itemOid);
            AgisInventoryPlugin.log.debug("LOOT: addItem to looter, oid=" + itemOid + ", rv=" + rv);
            if (!rv) {
                DataLoggerClient.logData("ITEM_LOOTED_FAILED_EVENT", looterOid, itemOid, accountId, logData);
                AgisInventoryPlugin.this.addItem(mobOid, mobOid, itemOid);
                return true;
            }
            loot.remove(itemOid);
            EnginePlugin.setObjectProperty(mobOid, InventoryClient.NAMESPACE, "loot", (Serializable)loot);
            final String lootedItem = item.getName();
            if (lootedItem != null) {
                WorldManagerClient.sendObjChatMsg(looterOid, 2, "You have looted: " + lootedItem);
            }
            AgisInventoryPlugin.this.sendBagInvUpdate(looterOid);
            AgisInventoryPlugin.this.sendLootList(looterOid, mobOid);
            return true;
        }
    }
    
    class RemoveSpecificItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.removeSpecificItemMessage removeMsg = (AgisInventoryClient.removeSpecificItemMessage)msg;
            final OID oid = removeMsg.getSubject();
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(oid);
            final OID itemOid = (OID)removeMsg.getProperty("itemOid");
            final boolean removeStack = (boolean)removeMsg.getProperty("removeStack");
            final int numToRemove = (int)removeMsg.getProperty("numToRemove");
            AgisInventoryPlugin.this.lock.lock();
            try {
                final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOid);
                if (item == null) {
                    return true;
                }
                if (!removeStack) {
                    item.setStackSize(item.getStackSize() - numToRemove);
                    Engine.getPersistenceManager().setDirty((Entity)item);
                    Engine.getPersistenceManager().setDirty((Entity)iInfo);
                    Log.debug("ITEM: reduced stack: " + itemOid + " of item type: " + item.getName() + " to size: " + item.getStackSize());
                }
                if (item.getStackSize() < 1 || removeStack) {
                    AgisInventoryPlugin.this.unequipItem(item, oid, false);
                    final OID rootBagOid = oid;
                    if (rootBagOid == null) {
                        AgisInventoryPlugin.log.debug("removeItem: cant find rootBagOid");
                        return true;
                    }
                    final Boolean result = AgisInventoryPlugin.this.removeItemFromBag(rootBagOid, itemOid);
                    if (result) {
                        item.unacquired(rootBagOid);
                    }
                }
            }
            finally {
                AgisInventoryPlugin.this.lock.unlock();
            }
            AgisInventoryPlugin.this.lock.unlock();
            AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            return true;
        }
    }
    
    class RemoveGenericItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.removeGenericItemMessage removeMsg = (AgisInventoryClient.removeGenericItemMessage)msg;
            final OID oid = removeMsg.getSubject();
            final int itemID = (int)removeMsg.getProperty("itemID");
            final boolean removeStack = (boolean)removeMsg.getProperty("removeStack");
            int numToRemove = (int)removeMsg.getProperty("numToRemove");
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(oid);
            AgisInventoryPlugin.this.lock.lock();
            try {
                final ArrayList<OID> itemOids = AgisInventoryPlugin.this.findItemStacks(oid, itemID);
                for (final OID itemOid : itemOids) {
                    final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOid);
                    Log.debug("ITEM: found stack: " + itemOid + " of item type: " + item.getName() + " to remove from.");
                    if (item == null) {
                        continue;
                    }
                    if (!removeStack) {
                        final int stackSize = item.getStackSize();
                        item.setStackSize(stackSize - numToRemove);
                        numToRemove -= stackSize;
                        Engine.getPersistenceManager().setDirty((Entity)item);
                        Engine.getPersistenceManager().setDirty((Entity)iInfo);
                        Log.debug("ITEM: reduced stack: " + itemOid + " of item type: " + item.getName() + " to size: " + item.getStackSize());
                    }
                    if (item.getStackSize() < 1 || removeStack) {
                        AgisInventoryPlugin.this.unequipItem(item, oid, false);
                        final OID rootBagOid = oid;
                        if (rootBagOid == null) {
                            AgisInventoryPlugin.log.debug("removeItem: cant find rootBagOid");
                            continue;
                        }
                        final Boolean result = AgisInventoryPlugin.this.removeItemFromBag(rootBagOid, itemOid);
                        if (result) {
                            item.unacquired(rootBagOid);
                        }
                        Log.debug("ITEM: removing from stack: " + itemOid + " of item type: " + item.getName() + " had result: " + result);
                    }
                    if (numToRemove < 1) {
                        break;
                    }
                }
            }
            finally {
                AgisInventoryPlugin.this.lock.unlock();
            }
            AgisInventoryPlugin.this.lock.unlock();
            AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            return true;
        }
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            final OID instanceOid = message.getInstanceOid();
            Log.debug("LoginHook: playerOid=" + playerOid + " instanceOid=" + instanceOid);
            AgisInventoryPlugin.aDB.checkCharacterPurchases(playerOid);
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(playerOid);
            iInfo.setMail(AgisInventoryPlugin.aDB.retrieveMail(playerOid));
            Log.debug("MAIL: loaded " + iInfo.getMail().size() + " pieces of mail for player: " + playerOid);
            AgisInventoryPlugin.this.loadMailItems(playerOid);
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            return true;
        }
    }
    
    class LogoutHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage message = (LogoutMessage)msg;
            final OID playerOid = message.getSubject();
            Log.debug("LOGOUT: inventory logout started for: " + playerOid);
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            Log.debug("LOGOUT: inventory logout finished for: " + playerOid);
            return true;
        }
    }
    
    class GetMailHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = getMsg.getSubject();
            AgisInventoryPlugin.this.sendMailList(oid);
            return true;
        }
    }
    
    class MailReadHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = getMsg.getSubject();
            final int mailID = (int)getMsg.getProperty("mailID");
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(oid);
            final ArrayList<Mail> mailList = iInfo.getMail();
            final Mail m = AgisInventoryPlugin.this.getMailByID(mailList, mailID);
            Log.debug("Setting mail read with mailID: " + mailID);
            m.setMailRead(true);
            iInfo.setMail(mailList);
            AgisInventoryPlugin.aDB.readMail(mailID);
            AgisInventoryPlugin.this.sendMailList(oid);
            return true;
        }
    }
    
    class TakeMailItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = getMsg.getSubject();
            final int mailID = (int)getMsg.getProperty("mailID");
            final int itemPos = (int)getMsg.getProperty("itemPos");
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(oid);
            Log.debug("MAIL: got take mail item with mailID: " + mailID);
            final ArrayList<Mail> mailList = iInfo.getMail();
            final Mail m = AgisInventoryPlugin.this.getMailByID(mailList, mailID);
            Log.debug("MAIL: got take mail item with mailID: " + mailID);
            if (itemPos == -1) {
                AgisInventoryPlugin.this.alterMobCurrency(oid, m.getCurrencyType(), m.getCurrencyAmount());
                m.setCurrencyAmount(0);
                iInfo.setMail(mailList);
                AgisInventoryPlugin.aDB.takeMailCurrency(mailID);
                AgisInventoryPlugin.this.sendMailList(oid);
                AgisInventoryPlugin.this.sendBagInvUpdate(oid);
                return true;
            }
            if (m.getCoD()) {
                final Currency c = (Currency)Agis.CurrencyManager.get(m.getCurrencyType());
                if (c == null) {
                    ExtendedCombatMessages.sendErrorMessage(oid, "Invalid Currency");
                    return true;
                }
                if (AgisInventoryPlugin.this.getMobCurrency(oid, m.getCurrencyType()) < m.getCurrencyAmount()) {
                    ExtendedCombatMessages.sendErrorMessage(oid, "You do not have enough " + c.getCurrencyName() + " to pay the CoD");
                    return true;
                }
            }
            final OID itemOID = m.getItems().get(itemPos);
            final AgisItem item = AgisInventoryPlugin.getAgisItem(itemOID);
            Log.debug("MAIL: adding item: " + item);
            if (item == null) {
                Log.error("Mail item is null from mail: " + mailID);
                return true;
            }
            final boolean itemAdded = AgisInventoryPlugin.this.addItem(oid, oid, item.getOid());
            if (itemAdded) {
                Log.debug("MAIL: taken item: " + item);
                m.itemTaken(itemPos);
                iInfo.setMail(mailList);
                AgisInventoryPlugin.aDB.takeMailItem(mailID, itemPos, m.getCoD());
                if (m.getCoD()) {
                    final String message = String.valueOf(m.getRecipientName()) + " has accepted your CoD request. Your payment is attached.";
                    AgisInventoryPlugin.this.createAndSendMail(m.getRecipientOID(), m.getSenderOID(), m.getSenderName(), "CoD Payment", message, new ArrayList(), m.getCurrencyType(), m.getCurrencyAmount(), false);
                    m.setCoD(false);
                    m.setCurrencyAmount(0);
                }
            }
            AgisInventoryPlugin.this.sendMailList(oid);
            AgisInventoryPlugin.this.sendBagInvUpdate(oid);
            return true;
        }
    }
    
    class ReturnMailHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = getMsg.getSubject();
            final int mailID = (int)getMsg.getProperty("mailID");
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(oid);
            final ArrayList<Mail> mailList = iInfo.getMail();
            final Mail m = AgisInventoryPlugin.this.getMailByID(mailList, mailID);
            Log.debug("Setting mail deleted with mail: " + mailID);
            AgisInventoryPlugin.aDB.returnMail(m.getID(), m.getSenderOID(), m.getSenderName(), m.getRecipientOID(), m.getRecipientName(), m.getSubject(), m.getCoD());
            mailList.remove(m);
            iInfo.setMail(mailList);
            AgisInventoryPlugin.this.sendMailList(oid);
            return true;
        }
    }
    
    class DeleteMailHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = getMsg.getSubject();
            final int mailID = (int)getMsg.getProperty("mailID");
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(oid);
            final ArrayList<Mail> mailList = iInfo.getMail();
            final Mail m = AgisInventoryPlugin.this.getMailByID(mailList, mailID);
            Log.debug("Setting mail deleted with mail: " + mailID);
            AgisInventoryPlugin.aDB.deleteMail(mailID);
            mailList.remove(m);
            iInfo.setMail(mailList);
            AgisInventoryPlugin.this.sendMailList(oid);
            return true;
        }
    }
    
    class SendMailHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage sendMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID senderOid = sendMsg.getSubject();
            Log.debug("MAIL: got send mail");
            final String recipientName = (String)sendMsg.getProperty("recipient");
            OID recipientOid = null;
            recipientOid = Engine.getDatabase().getOidByName(recipientName, WorldManagerClient.NAMESPACE);
            if (recipientOid == null || recipientOid.equals((Object)0L)) {
                ExtendedCombatMessages.sendErrorMessage(senderOid, "No character called '" + recipientName + "' exists.");
                return true;
            }
            Log.debug("MAIL: got valid recipient");
            final String subject = (String)sendMsg.getProperty("subject");
            final String message = (String)sendMsg.getProperty("message");
            final int numItems = (int)sendMsg.getProperty("numItems");
            final ArrayList<OID> items = new ArrayList<OID>();
            for (int i = 0; i < numItems; ++i) {
                items.add((OID)sendMsg.getProperty("item" + i));
            }
            Log.debug("MAIL: handled items");
            final int currencyType = (int)sendMsg.getProperty("currencyType");
            final int currencyAmount = (int)sendMsg.getProperty("currencyAmount");
            final boolean CoD = (boolean)sendMsg.getProperty("CoD");
            Log.debug("MAIL: handled currency");
            final boolean sent = AgisInventoryPlugin.this.createAndSendMail(senderOid, recipientOid, recipientName, subject, message, items, currencyType, currencyAmount, CoD);
            if (sent) {
                Log.debug("MAIL: added mail, sending success message");
                final Map<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("ext_msg_subtype", "MailSent");
                final WorldManagerClient.TargetedExtensionMessage successMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, senderOid, senderOid, false, (Map)props);
                Engine.getAgent().sendBroadcast((Message)successMsg);
                Log.debug("MAIL: mail sent");
            }
            else {
                Log.warn("MAIL: mail was not sent to player: " + recipientOid);
            }
            return true;
        }
    }
    
    class SendPurchaseMailHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final AgisInventoryClient.sendPurchaseMailMessage getMsg = (AgisInventoryClient.sendPurchaseMailMessage)msg;
            final OID recipientOID = getMsg.getSubject();
            final String subject = "Item Shop Purchase";
            final String message = "Thank you for shopping at the Item Shop. Your purchase has been included in this mail.";
            final int itemID = (int)getMsg.getProperty("itemID");
            final Template tmpl = ObjectManagerClient.getTemplate(itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
            final String itemName = tmpl.getName();
            final OID itemOID = AgisInventoryPlugin.this.generateItem(itemID, itemName);
            final String senderName = "The Item Shop";
            final int currencyType = 0;
            final int currencyAmount = 0;
            final ArrayList<OID> items = new ArrayList<OID>();
            if (!itemOID.equals((Object)0L)) {
                items.add(itemOID);
            }
            final boolean CoD = false;
            final Mail m = new Mail(-1, recipientOID, "", null, senderName, subject, message, currencyType, currencyAmount, items, 1, CoD);
            final InventoryInfo iInfo = AgisInventoryPlugin.getInventoryInfo(recipientOID);
            iInfo.addMail(m);
            AgisInventoryPlugin.this.sendMailList(recipientOID);
            WorldManagerClient.sendObjChatMsg(recipientOID, 2, "You have recieved new mail from " + senderName);
            return true;
        }
    }
    
    class CheckComponentHook implements Hook
    {
        public boolean processMessage(final Message arg0, final int arg1) {
            final AgisInventoryClient.CheckComponentMessage msg = (AgisInventoryClient.CheckComponentMessage)arg0;
            if (msg.gridSystem) {
                final HashMap<Integer, Integer> requirements = new HashMap<Integer, Integer>();
                final HashMap<Integer, Integer> itemTotals = new HashMap<Integer, Integer>();
                for (int i = 0; i < msg._reqComponents.size(); ++i) {
                    final int reqComponent = msg._reqComponents.get(i);
                    int reqCount = msg._reqStackSizes.get(i);
                    if (!requirements.containsKey(reqComponent)) {
                        requirements.put(reqComponent, reqCount);
                        Log.debug("CHECK: set required itemID " + reqComponent + " to count " + reqCount);
                    }
                    else {
                        reqCount += requirements.get(reqComponent);
                        requirements.put(reqComponent, reqCount);
                        Log.debug("CHECK: set required itemID " + reqComponent + " to count " + reqCount);
                    }
                    itemTotals.put(reqComponent, 0);
                }
                final HashMap<Integer, Integer> provided = new HashMap<Integer, Integer>();
                final HashMap<Long, Integer> providedIds = new HashMap<Long, Integer>();
                for (int j = 0; j < msg._components.size(); ++j) {
                    final OID component = OID.fromLong((long)msg._components.get(j));
                    final int providedItemID = AgisInventoryPlugin.getAgisItem(component).getTemplateID();
                    int count = msg._componentCounts.get(j);
                    if (!provided.containsKey(providedItemID)) {
                        provided.put(providedItemID, count);
                        providedIds.put(msg._components.get(j), count);
                        Log.debug("CHECK: set provided itemID " + providedItemID + " to count " + count);
                    }
                    else {
                        count += provided.get(providedItemID);
                        provided.put(providedItemID, count);
                        providedIds.put(msg._components.get(j), count);
                        Log.debug("CHECK: set provided itemID " + providedItemID + " to count " + count);
                    }
                }
                for (final Long providedId : providedIds.keySet()) {
                    final OID component2 = OID.fromLong((long)providedId);
                    final AgisItem tempItem = AgisInventoryPlugin.getAgisItem(component2);
                    itemTotals.put(tempItem.getTemplateID(), itemTotals.get(tempItem.getTemplateID()) + tempItem.getStackSize());
                }
                for (final Integer itemID : requirements.keySet()) {
                    if (!provided.containsKey(itemID)) {
                        Log.debug("CHECK: itemID " + itemID + " was not found in the provided list");
                        Engine.getAgent().sendObjectResponse((Message)msg, (Object)false);
                        return true;
                    }
                    if (provided.get(itemID) < requirements.get(itemID)) {
                        Log.debug("CHECK: itemID " + itemID + " required " + requirements.get(itemID) + " but player only provided " + provided.get(itemID));
                        Engine.getAgent().sendObjectResponse((Message)msg, (Object)false);
                        return true;
                    }
                    if (itemTotals.get(itemID) < requirements.get(itemID)) {
                        Log.debug("CHECK: itemID " + itemID + " required " + requirements.get(itemID) + " but player only had " + itemTotals.get(itemID));
                        Engine.getAgent().sendObjectResponse((Message)msg, (Object)false);
                        return true;
                    }
                }
            }
            else {
                final ArrayList<Integer> test = new ArrayList<Integer>();
                test.addAll(msg._reqComponents);
                final HashMap<Integer, Integer> requirements2 = new HashMap<Integer, Integer>();
                for (int i = 0; i < msg._reqComponents.size(); ++i) {
                    final int reqComponent = msg._reqComponents.get(i);
                    int reqCount = msg._reqStackSizes.get(i);
                    if (!requirements2.containsKey(reqComponent)) {
                        requirements2.put(reqComponent, reqCount);
                        Log.debug("CHECK: set required itemID " + reqComponent + " to count " + reqCount);
                    }
                    else {
                        reqCount += requirements2.get(reqComponent);
                        requirements2.put(reqComponent, reqCount);
                        Log.debug("CHECK: set required itemID " + reqComponent + " to count " + reqCount);
                    }
                }
                for (final int itemID2 : requirements2.keySet()) {
                    final int count2 = requirements2.get(itemID2);
                    int amountFound = 0;
                    final ArrayList<OID> existingOids = AgisInventoryPlugin.this.findItemStacks(msg._subject, itemID2);
                    if (existingOids.size() > 0) {
                        Log.debug("CHECK: user has item " + itemID2);
                        for (final OID existingOid : existingOids) {
                            final AgisItem tempItem2 = AgisInventoryPlugin.getAgisItem(existingOid);
                            Log.debug("CHECK: increasing amountFound for item: " + existingOid);
                            amountFound += tempItem2.getStackSize();
                        }
                    }
                    if (amountFound < count2) {
                        Log.debug("CHECK: did not find enough of the required item");
                        Engine.getAgent().sendObjectResponse((Message)msg, (Object)false);
                        return true;
                    }
                }
            }
            Log.debug("CHECK: passed item check");
            Engine.getAgent().sendObjectResponse((Message)msg, (Object)true);
            return true;
        }
    }
    
    class UseAccountItemHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = eMsg.getSubject();
            final int itemID = (int)eMsg.getProperty("itemID");
            final Template itemTemplate = ObjectManagerClient.getTemplate(itemID, ObjectManagerPlugin.ITEM_TEMPLATE);
            return true;
        }
    }
    
    public static class EquipMap implements Serializable
    {
        HashMap<AgisEquipSlot, OID> map;
        private static final long serialVersionUID = 1L;
        
        public EquipMap() {
            this.map = new HashMap<AgisEquipSlot, OID>();
        }
        
        public AgisEquipSlot getSlot(final OID itemOid) {
            for (final Map.Entry<AgisEquipSlot, OID> entry : this.map.entrySet()) {
                final OID oItemOid = entry.getValue();
                if (oItemOid.equals((Object)itemOid)) {
                    if (Log.loggingDebug) {
                        AgisInventoryPlugin.log.debug("EquipMap.getSlot: found item=" + itemOid + " slot=" + entry.getKey());
                    }
                    return entry.getKey();
                }
            }
            if (Log.loggingDebug) {
                AgisInventoryPlugin.log.debug("EquipMap.getSlot: item=" + itemOid + " slot=null");
            }
            return null;
        }
        
        public OID get(final AgisEquipSlot slot) {
            return this.map.get(slot);
        }
        
        public void put(final AgisEquipSlot slot, final OID longVal) {
            this.map.put(slot, longVal);
        }
        
        public void remove(final AgisEquipSlot slot) {
            this.map.remove(slot);
        }
        
        public boolean containsValue(final OID itemOid) {
            return this.map.containsValue(itemOid);
        }
        
        public HashMap<AgisEquipSlot, OID> getEquipMap() {
            return this.map;
        }
        
        public void setEquipMap(final HashMap<AgisEquipSlot, OID> map) {
            this.map = map;
        }
    }
}
