// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

import java.util.List;
import atavism.agis.objects.CraftingComponent;
import atavism.agis.plugins.CraftingPlugin;
import atavism.agis.objects.CraftingRecipe;
import atavism.agis.objects.MerchantTable;
import atavism.agis.objects.AgisEquipSlot;
import java.util.LinkedList;
import atavism.agis.objects.AgisEquipInfo;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import atavism.agis.core.SpawnMobAcquireHook;
import atavism.agis.core.RecipeItemActivateHook;
import atavism.agis.core.CurrencyItemActivateHook;
import atavism.agis.core.CurrencyItemAcquireHook;
import atavism.agis.objects.AgisItem;
import atavism.agis.core.CreateClaimActivateHook;
import atavism.agis.core.AbilityActivateHook;
import java.util.HashMap;
import atavism.agis.core.EquipActivateHook;
import java.io.Serializable;
import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.agis.util.HelperFunctions;
import atavism.server.util.Log;
import atavism.server.objects.Template;
import java.util.ArrayList;

public class ItemDatabase
{
    protected static Queries queries;
    String[] statNames;
    String[] resistanceNames;
    String[] physicalResistNames;
    String[] magicalResistNames;
    public static final String ITEM_TABLE = "item_templates";
    
    public ItemDatabase(final boolean keepAlive) {
        this.statNames = new String[] { "slash_resist", "pierce_resist", "crush_resist", "fire_resist", "poison_resist", "wound_resist", "mental_resist", "spirit_resist", "balance_resist", "strength", "dexterity", "quickness", "potential", "intelligence", "channelling", "perception", "endurance", "willpower" };
        this.resistanceNames = new String[] { "slash_resist", "pierce_resist", "crush_resist", "fire_resist", "poison_resist", "wound_resist", "mental_resist", "spirit_resist", "balance_resist" };
        this.physicalResistNames = new String[] { "slash_resist", "pierce_resist", "crush_resist" };
        this.magicalResistNames = new String[] { "fire_resist", "poison_resist", "wound_resist", "mental_resist", "spirit_resist", "balance_resist" };
        if (ItemDatabase.queries == null) {
            ItemDatabase.queries = new Queries(keepAlive);
        }
    }
    
    public ArrayList<Template> loadItemTemplates() {
        Log.debug("ITEM: loading item templates");
        final ArrayList<Template> list = new ArrayList<Template>();
        try {
            final PreparedStatement ps = ItemDatabase.queries.prepare("SELECT * FROM item_templates where isactive = 1");
            final ResultSet rs = ItemDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final Template tmpl = new Template(HelperFunctions.readEncodedString(rs.getBytes("name")), rs.getInt("id"), ObjectManagerPlugin.ITEM_TEMPLATE);
                    Log.debug("ITEM: loading item template " + rs.getInt("id"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "baseName", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("name")));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "category", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("category")));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "subcategory", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("subcategory")));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "itemID", (Serializable)rs.getInt("id"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "item_icon", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("icon")));
                    final String itemType = HelperFunctions.readEncodedString(rs.getBytes("itemType"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "itemType", (Serializable)itemType);
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "subType", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("subType")));
                    if (itemType.equals("Weapon") || itemType.equals("Armor")) {
                        final AgisEquipInfo eqInfo = this.getEquipInfo(HelperFunctions.readEncodedString(rs.getBytes("slot")));
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "slot", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("slot")));
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "item_equipInfo", (Serializable)eqInfo);
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "item_activateHook", (Serializable)new EquipActivateHook());
                        final String displayID = HelperFunctions.readEncodedString(rs.getBytes("display"));
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "displayVal", (Serializable)displayID);
                    }
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "itemGrade", (Serializable)rs.getInt("itemQuality"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "binding", (Serializable)rs.getInt("binding"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "isUnique", (Serializable)rs.getBoolean("isUnique"));
                    if (itemType.equals("Bag") || itemType.equals("Container")) {
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "numSlots", (Serializable)rs.getInt("stackLimit"));
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "stackLimit", (Serializable)1);
                    }
                    else {
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "stackLimit", (Serializable)rs.getInt("stackLimit"));
                    }
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "purchaseCurrency", (Serializable)rs.getInt("purchaseCurrency"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "purchaseCost", (Serializable)rs.getInt("purchaseCost"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "sellable", (Serializable)rs.getBoolean("sellable"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "levelReq", (Serializable)rs.getInt("levelReq"));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "aspectReq", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("aspectReq")));
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "raceReq", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("raceReq")));
                    final HashMap<String, Integer> itemStats = new HashMap<String, Integer>();
                    for (int i = 1; i <= 12; ++i) {
                        final String effectType = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "type"));
                        if (effectType == null) {
                            break;
                        }
                        if (effectType.equals("")) {
                            break;
                        }
                        if (effectType.equals("Stat")) {
                            if (itemType.equals("Weapon") || itemType.equals("Armor")) {
                                final String stat = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "name"));
                                final String value = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                                if (stat != null && !stat.isEmpty()) {
                                    itemStats.put(stat, Integer.parseInt(value));
                                }
                            }
                        }
                        else if (effectType.equals("UseAbility")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int abilityID = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, "abilityID", (Serializable)abilityID);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, "item_activateHook", (Serializable)new AbilityActivateHook(abilityID));
                            final LinkedList<String> cooldowns = this.getAbilityCooldowns(abilityID);
                            for (int j = 0; j < cooldowns.size(); ++j) {
                                tmpl.put(InventoryClient.ITEM_NAMESPACE, "cooldown_" + j, (Serializable)cooldowns.get(j));
                            }
                        }
                        else if (effectType.equals("AutoAttack")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int abilityID = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, "autoAttack", (Serializable)abilityID);
                        }
                        else if (effectType.equals("CreateClaim")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int size = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, "item_activateHook", (Serializable)new CreateClaimActivateHook(size));
                        }
                        else if (effectType.equals("Currency")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int currencyID = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, AgisItem.TEMPL_ACQUIRE_HOOK, (Serializable)new CurrencyItemAcquireHook(currencyID));
                        }
                        else if (effectType.equals("CurrencyItem")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int currencyID = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, "item_activateHook", (Serializable)new CurrencyItemActivateHook(currencyID));
                        }
                        else if (effectType.equals("BuildingMaterial")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int buildHealthValue = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, "buildHealthValue", (Serializable)buildHealthValue);
                        }
                        else if (effectType.equals("Blueprint")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int recipeID = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, "item_activateHook", (Serializable)new RecipeItemActivateHook(recipeID));
                        }
                        else if (effectType.equals("SpawnMob")) {
                            final String value2 = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            final int mobTemplateID = Integer.parseInt(value2);
                            tmpl.put(InventoryClient.ITEM_NAMESPACE, AgisItem.TEMPL_ACQUIRE_HOOK, (Serializable)new SpawnMobAcquireHook(mobTemplateID));
                        }
                    }
                    tmpl.put(InventoryClient.ITEM_NAMESPACE, "bonusStats", (Serializable)itemStats);
                    if (itemType.equals("Weapon")) {
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "damage", (Serializable)rs.getInt("damage"));
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "damageType", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("damageType")));
                        final float delay = rs.getFloat("delay") * 1000.0f;
                        tmpl.put(InventoryClient.ITEM_NAMESPACE, "delay", (Serializable)(int)delay);
                    }
                    list.add(tmpl);
                    Log.debug("ITEM: loaded item template " + rs.getInt("id") + " with name: " + HelperFunctions.readEncodedString(rs.getBytes("name")));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private LinkedList<String> getAbilityCooldowns(final int abilityID) {
        final LinkedList<String> list = new LinkedList<String>();
        try {
            final PreparedStatement ps = ItemDatabase.queries.prepare("SELECT cooldown1Type FROM abilities where id=" + abilityID);
            final ResultSet rs = ItemDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    Log.debug("COOLDOWN: Reading in cooldown data for ability: " + abilityID);
                    final String cooldown = HelperFunctions.readEncodedString(rs.getBytes("cooldown1Type"));
                    if (cooldown != null && !cooldown.equals("")) {
                        list.add(cooldown);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        Log.debug("COOLDOWN: " + abilityID + " has cooldowns: " + list);
        return list;
    }
    
    private AgisEquipInfo getEquipInfo(final String slot) {
        final AgisEquipInfo eqInfo = new AgisEquipInfo();
        if (slot.equals("Main Hand")) {
            eqInfo.setName("weapon");
            eqInfo.addEquipSlot(AgisEquipSlot.PRIMARYWEAPON);
        }
        else if (slot.equals("Off Hand")) {
            eqInfo.setName("weapon");
            eqInfo.addEquipSlot(AgisEquipSlot.SECONDARYWEAPON);
        }
        else if (slot.equals("Any Hand")) {
            eqInfo.setName("weapon");
            eqInfo.addEquipSlot(AgisEquipSlot.PRIMARYWEAPON);
            eqInfo.addEquipSlot(AgisEquipSlot.SECONDARYWEAPON);
        }
        else if (slot.equals("Head")) {
            eqInfo.setName("head");
            eqInfo.addEquipSlot(AgisEquipSlot.HEAD);
        }
        else if (slot.equals("Shoulder")) {
            eqInfo.setName("shoulder");
            eqInfo.addEquipSlot(AgisEquipSlot.SHOULDER);
        }
        else if (slot.equals("Chest")) {
            eqInfo.setName("chest");
            eqInfo.addEquipSlot(AgisEquipSlot.CHEST);
        }
        else if (slot.equals("Hands")) {
            eqInfo.setName("hands");
            eqInfo.addEquipSlot(AgisEquipSlot.HANDS);
        }
        else if (slot.equals("Waist")) {
            eqInfo.setName("belt");
            eqInfo.addEquipSlot(AgisEquipSlot.BELT);
        }
        else if (slot.equals("Legs")) {
            eqInfo.setName("legs");
            eqInfo.addEquipSlot(AgisEquipSlot.LEGS);
        }
        else if (slot.equals("Feet")) {
            eqInfo.setName("feet");
            eqInfo.addEquipSlot(AgisEquipSlot.FEET);
        }
        else if (slot.equals("Back")) {
            eqInfo.setName("back");
            eqInfo.addEquipSlot(AgisEquipSlot.BACK);
        }
        else if (slot.equals("Neck")) {
            eqInfo.setName("neck");
            eqInfo.addEquipSlot(AgisEquipSlot.NECK);
        }
        else if (slot.equals("Ring")) {
            eqInfo.setName("ring");
            eqInfo.addEquipSlot(AgisEquipSlot.PRIMARYRING);
            eqInfo.addEquipSlot(AgisEquipSlot.SECONDARYRING);
        }
        else if (slot.equals("Shirt")) {
            eqInfo.setName("shirt");
            eqInfo.addEquipSlot(AgisEquipSlot.SHIRT);
        }
        return eqInfo;
    }
    
    public HashMap<Integer, MerchantTable> loadMerchantTables() {
        final HashMap<Integer, MerchantTable> map = new HashMap<Integer, MerchantTable>();
        try {
            final PreparedStatement ps = ItemDatabase.queries.prepare("SELECT * FROM merchant_tables where isactive = 1");
            final ResultSet rs = ItemDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int tableID = rs.getInt("id");
                    final MerchantTable m = new MerchantTable(tableID, HelperFunctions.readEncodedString(rs.getBytes("name")));
                    this.loadMerchantItems(m);
                    map.put(tableID, m);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
    
    public void loadMerchantItems(final MerchantTable mTbl) {
        final ArrayList<Integer> items = new ArrayList<Integer>();
        final ArrayList<Integer> counts = new ArrayList<Integer>();
        final ArrayList<Integer> refreshTimes = new ArrayList<Integer>();
        try {
            final PreparedStatement ps = ItemDatabase.queries.prepare("SELECT * FROM merchant_item where tableID=" + mTbl.getID() + " AND isactive = 1");
            final ResultSet rs = ItemDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    items.add(rs.getInt("itemID"));
                    counts.add(rs.getInt("count"));
                    refreshTimes.add(rs.getInt("refreshTime"));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        mTbl.setItems(items);
        mTbl.setItemCounts(counts);
        mTbl.setItemRespawns(refreshTimes);
    }
    
    public HashMap<Integer, CraftingRecipe> loadCraftingRecipes() {
        final HashMap<Integer, CraftingRecipe> list = new HashMap<Integer, CraftingRecipe>();
        try {
            final PreparedStatement ps = ItemDatabase.queries.prepare("SELECT * FROM crafting_recipes where isactive = 1");
            final ResultSet rs = ItemDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    Log.debug("CRAFTING: loading recipe:" + rs.getInt("id"));
                    final CraftingRecipe recipe = new CraftingRecipe(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                    recipe.setResultItemId(rs.getInt("resultItemID"));
                    recipe.setResultItemCount(rs.getInt("resultItemCount"));
                    recipe.setSkillID(rs.getInt("skillID"));
                    recipe.setRequiredSkillLevel(rs.getInt("skillLevelReq"));
                    recipe.setRecipeItemId(rs.getInt("recipeItemID"));
                    recipe.setQualityChangeable(rs.getBoolean("qualityChangeable"));
                    recipe.setAllowDyes(rs.getBoolean("allowDyes"));
                    recipe.setAllowEssences(rs.getBoolean("allowEssences"));
                    recipe.setStationReq(HelperFunctions.readEncodedString(rs.getBytes("stationReq")));
                    recipe.setMustMatchLayout(rs.getBoolean("layoutReq"));
                    for (int i = 0; i < CraftingPlugin.GRID_SIZE; ++i) {
                        final LinkedList<CraftingComponent> componentRow = new LinkedList<CraftingComponent>();
                        for (int j = 0; j < CraftingPlugin.GRID_SIZE; ++j) {
                            final CraftingComponent component = new CraftingComponent("", rs.getInt("component" + (i * CraftingPlugin.GRID_SIZE + j + 1) + "Count"), rs.getInt("component" + (i * CraftingPlugin.GRID_SIZE + j + 1)));
                            componentRow.add(component);
                            Log.debug("CRAFTING: adding item: " + component.getItemId() + " to row: " + i + " in column: " + j);
                        }
                        recipe.addCraftingComponentRow(componentRow);
                    }
                    list.put(recipe.getID(), recipe);
                    Log.debug("CRAFTING: put recipe:" + recipe.getID());
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<Long> getCharacterOids() {
        final List<Long> list = new ArrayList<Long>();
        final PreparedStatement ps = ItemDatabase.queries.prepare("SELECT DISTINCT obj_id FROM objstore WHERE type = 'PLAYER'");
        final ResultSet rs = ItemDatabase.queries.executeSelect(ps);
        try {
            while (rs.next()) {
                list.add(rs.getLong("obj_id"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void close() {
        ItemDatabase.queries.close();
    }
}
