// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

import java.util.Iterator;
import atavism.server.math.Quaternion;
import atavism.agis.objects.Claim;
import atavism.agis.objects.BuildObjectStage;
import atavism.agis.objects.BuildObjectTemplate;
import atavism.server.math.AOVector;
import atavism.agis.objects.ResourceNode;
import atavism.server.engine.OID;
import java.util.ArrayList;
import atavism.server.util.Log;
import atavism.server.math.Point;
import atavism.agis.objects.CharacterTemplate;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import atavism.agis.util.HelperFunctions;

public class ContentDatabase
{
    protected static Queries queries;
    
    public ContentDatabase(final boolean keepAlive) {
        if (ContentDatabase.queries == null) {
            ContentDatabase.queries = new Queries(keepAlive);
        }
    }
    
    public String loadGameSetting(final String settingName) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `game_setting` where name = '" + settingName + "'  AND isactive = 1");
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null && rs.next()) {
                return HelperFunctions.readEncodedString(rs.getBytes("value"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public HashMap<String, CharacterTemplate> loadCharacterFactoryTemplates() {
        final HashMap<String, CharacterTemplate> templates = new HashMap<String, CharacterTemplate>();
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `character_create_template` where isactive = 1");
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int templateID = rs.getInt("id");
                    final String race = HelperFunctions.readEncodedString(rs.getBytes("race"));
                    final String aspect = HelperFunctions.readEncodedString(rs.getBytes("aspect"));
                    final int faction = rs.getInt("faction");
                    final String instanceName = HelperFunctions.readEncodedString(rs.getBytes("instanceName"));
                    final float pos_x = rs.getFloat("pos_x");
                    final float pos_y = rs.getFloat("pos_y");
                    final float pos_z = rs.getFloat("pos_z");
                    final int autoAttackID = rs.getInt("autoAttack");
                    final CharacterTemplate tmpl = new CharacterTemplate();
                    tmpl.setRace(race);
                    tmpl.setAspect(aspect);
                    tmpl.setFaction(faction);
                    tmpl.setInstanceName(instanceName);
                    tmpl.setSpawnPoint(new Point(pos_x, pos_y, pos_z));
                    tmpl.setAutoAttack(autoAttackID);
                    this.loadCharacterFactoryStats(tmpl, templateID);
                    tmpl.setStartingSkills(this.loadCharacterFactorySkills(templateID));
                    this.loadCharacterStartingItems(tmpl, templateID);
                    templates.put(String.valueOf(race) + aspect, tmpl);
                    Log.debug("CHARTMPL: added character template: " + race + aspect);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return templates;
    }
    
    public void loadCharacterFactoryStats(final CharacterTemplate tmpl, final int id) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `character_create_stats` where character_create_id = " + id + " AND isactive = 1");
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String stat = HelperFunctions.readEncodedString(rs.getBytes("stat"));
                    final int baseValue = rs.getInt("value");
                    final float levelIncrease = rs.getFloat("levelIncrease");
                    final float levelPercentIncrease = rs.getFloat("levelPercentIncrease");
                    Log.debug("CHARTMPL: added stat " + stat + " with value: " + baseValue + " to character template");
                    tmpl.AddStatProgression(stat, baseValue, levelIncrease, levelPercentIncrease);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Integer> loadCharacterFactorySkills(final int id) {
        final ArrayList<Integer> skills = new ArrayList<Integer>();
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `character_create_skills` where character_create_id = " + id + " AND isactive = 1");
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int skill = rs.getInt("skill");
                    Log.debug("CHARTMPL: added skill " + skill);
                    skills.add(skill);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return skills;
    }
    
    public void loadCharacterStartingItems(final CharacterTemplate tmpl, final int id) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `character_create_items` where character_create_id = " + id + " AND isactive = 1");
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int item = rs.getInt("item_id");
                    final int count = rs.getInt("count");
                    final boolean equipped = rs.getBoolean("equipped");
                    Log.debug("CHARTMPL: added item " + item);
                    tmpl.addStartingItem(item, count, equipped);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public HashMap<Integer, ResourceNode> loadResourceNodes(final String instance, final OID instanceOid) {
        final HashMap<Integer, ResourceNode> nodes = new HashMap<Integer, ResourceNode>();
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `resource_node_template` where instance=?");
            ps.setString(1, instance);
            Log.debug("GRID: " + ps.toString());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    final AOVector p = new AOVector();
                    p.setX(rs.getFloat("locX"));
                    p.setY(rs.getFloat("locY"));
                    p.setZ(rs.getFloat("locZ"));
                    final ResourceNode resourceNode = new ResourceNode(id, p, instanceOid);
                    resourceNode.setName(HelperFunctions.readEncodedString(rs.getBytes("name")));
                    resourceNode.setGameObject(HelperFunctions.readEncodedString(rs.getBytes("gameObject")));
                    resourceNode.setCoordEffect(HelperFunctions.readEncodedString(rs.getBytes("coordEffect")));
                    resourceNode.setSkill(rs.getInt("skill"));
                    resourceNode.setSkillLevelReq(rs.getInt("skillLevel"));
                    resourceNode.setSkillLevelMax(rs.getInt("skillLevelMax"));
                    resourceNode.setWeaponReq(HelperFunctions.readEncodedString(rs.getBytes("weaponReq")));
                    resourceNode.setEquippedReq(rs.getBoolean("equipped"));
                    resourceNode.setRespawnTime(rs.getInt("respawnTime"));
                    resourceNode.setHarvestCount(rs.getInt("harvestCount"));
                    resourceNode.setHarvestTimeReq(rs.getFloat("harvestTimeReq"));
                    this.loadResourceDrops(resourceNode);
                    nodes.put(resourceNode.getID(), resourceNode);
                    Log.debug("RESOURCE: added node " + resourceNode.getID() + " to map");
                    resourceNode.spawn();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return nodes;
    }
    
    public void loadResourceDrops(final ResourceNode resourceNode) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `resource_drop` where resource_template = " + resourceNode.getID());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int item = rs.getInt("item");
                    final int min = rs.getInt("min");
                    int max = rs.getInt("max");
                    final int chance = rs.getInt("chance");
                    if (max < min) {
                        max = min;
                    }
                    if (min > 0) {
                        resourceNode.AddResourceDrop(item, min, max, chance);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public HashMap<Integer, BuildObjectTemplate> loadBuildObjectTemplates() {
        final HashMap<Integer, BuildObjectStage> stages = this.loadBuildObjectStages();
        final HashMap<Integer, BuildObjectTemplate> grids = new HashMap<Integer, BuildObjectTemplate>();
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `build_object_template` where isactive = 1");
            Log.debug("BUILD: " + ps.toString());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final BuildObjectTemplate bg = new BuildObjectTemplate(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")), rs.getInt("skill"), rs.getInt("skillLevelReq"), HelperFunctions.readEncodedString(rs.getBytes("weaponReq")), rs.getFloat("distanceReq"));
                    int stageID = rs.getInt("firstStageID");
                    while (stageID > 0) {
                        if (stages.containsKey(stageID)) {
                            bg.addStage(stages.get(stageID));
                            stageID = stages.get(stageID).getNextStageID();
                        }
                        else {
                            stageID = -1;
                        }
                    }
                    int health = 0;
                    for (int i = 1; i < bg.getStages().size(); ++i) {
                        health += bg.getStage(i).getHealth();
                    }
                    bg.setMaxHealth(health);
                    grids.put(bg.getId(), bg);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return grids;
    }
    
    public HashMap<Integer, BuildObjectStage> loadBuildObjectStages() {
        final HashMap<Integer, BuildObjectStage> stages = new HashMap<Integer, BuildObjectStage>();
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `build_object_stage` where isactive = 1");
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    int health = 0;
                    final HashMap<Integer, Integer> itemReqs = new HashMap<Integer, Integer>();
                    for (int i = 1; i <= 6; ++i) {
                        final Integer itemID = rs.getInt("itemReq" + i);
                        final Integer itemCount = rs.getInt("itemReq" + i + "Count");
                        if (itemID != null && itemID > 0 && itemCount != null && itemCount > 0) {
                            itemReqs.put(itemID, itemCount);
                            health += this.getItemHealthValue(itemID) * itemCount;
                        }
                    }
                    final BuildObjectStage bg = new BuildObjectStage(HelperFunctions.readEncodedString(rs.getBytes("gameObject")), rs.getFloat("buildTimeReq"), itemReqs, health, rs.getInt("nextStage"));
                    stages.put(rs.getInt("id"), bg);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return stages;
    }
    
    public int getItemHealthValue(final int itemID) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM item_templates where id = " + itemID);
            Log.debug("BUILD: " + ps.toString());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    for (int i = 1; i <= 12; ++i) {
                        final String itemEffectType = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "type"));
                        if (itemEffectType != null && itemEffectType.equals("BuildingMaterial")) {
                            final String itemEffectValue = HelperFunctions.readEncodedString(rs.getBytes("effect" + i + "value"));
                            if (itemEffectValue != null && !itemEffectValue.equals("")) {
                                return Integer.parseInt(itemEffectValue);
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public HashMap<Integer, Claim> loadClaims(final String instance) {
        final HashMap<Integer, Claim> grids = new HashMap<Integer, Claim>();
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `claim` where instance=?");
            ps.setString(1, instance);
            Log.debug("GRID: " + ps.toString());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final Claim bg = new Claim();
                    bg.setID(rs.getInt("id"));
                    bg.setName(HelperFunctions.readEncodedString(rs.getBytes("name")));
                    final AOVector p = new AOVector();
                    p.setX(rs.getFloat("locX"));
                    p.setY(rs.getFloat("locY"));
                    p.setZ(rs.getFloat("locZ"));
                    bg.setLoc(p);
                    bg.setSize(rs.getInt("size"));
                    final Long oid = rs.getLong("owner");
                    if (oid == null || oid == -1L) {
                        bg.setOwner(null);
                    }
                    else {
                        bg.setOwner(OID.fromLong((long)oid));
                    }
                    bg.setForSale(rs.getBoolean("forSale"));
                    bg.setCost(rs.getInt("cost"));
                    bg.setCurrency(rs.getInt("currency"));
                    bg.setSellerName(rs.getString("sellerName"));
                    bg.setClaimItemTemplate(rs.getInt("claimItemTemplate"));
                    this.loadClaimActions(bg);
                    this.loadClaimObjects(bg);
                    this.loadClaimPermissions(bg);
                    bg.setContentDatabase(this);
                    grids.put(bg.getID(), bg);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return grids;
    }
    
    public void loadClaimActions(final Claim claim) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `claim_action` where claimID = " + claim.getID());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    final String action = HelperFunctions.readEncodedString(rs.getBytes("action"));
                    final String brushType = HelperFunctions.readEncodedString(rs.getBytes("brushType"));
                    final float locX = rs.getFloat("locX");
                    final float locY = rs.getFloat("locY");
                    final float locZ = rs.getFloat("locZ");
                    final float normalX = rs.getFloat("normalX");
                    final float normalY = rs.getFloat("normalY");
                    final float normalZ = rs.getFloat("normalZ");
                    final int material = rs.getShort("material");
                    final int sizeX = rs.getInt("sizeX");
                    final int sizeY = rs.getInt("sizeY");
                    final int sizeZ = rs.getInt("sizeZ");
                    claim.AddActionData(id, action, brushType, new AOVector((float)sizeX, (float)sizeY, (float)sizeZ), new AOVector(locX, locY, locZ), new AOVector(normalX, normalY, normalZ), material);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void loadClaimObjects(final Claim claim) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `claim_object` where claimID = " + claim.getID());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    final int templateId = rs.getInt("template");
                    final int stage = rs.getInt("stage");
                    final String gameObject = HelperFunctions.readEncodedString(rs.getBytes("gameObject"));
                    final float locX = rs.getFloat("locX");
                    final float locY = rs.getFloat("locY");
                    final float locZ = rs.getFloat("locZ");
                    final float orientX = rs.getFloat("orientX");
                    final float orientY = rs.getFloat("orientY");
                    final float orientZ = rs.getFloat("orientZ");
                    final float orientW = rs.getFloat("orientW");
                    final int itemID = rs.getShort("itemID");
                    final String state = HelperFunctions.readEncodedString(rs.getBytes("objectState"));
                    final boolean complete = rs.getBoolean("complete");
                    final int health = rs.getInt("health");
                    final int maxHealth = rs.getInt("maxHealth");
                    final HashMap<Integer, Integer> itemCounts = new HashMap<Integer, Integer>();
                    for (int i = 1; i <= 6; ++i) {
                        final Integer itemId = rs.getInt("item" + i);
                        final Integer itemCount = rs.getInt("item" + i + "Count");
                        if (itemId != null && itemId > 0 && itemCount != null && itemCount > 0) {
                            itemCounts.put(itemId, itemCount);
                        }
                    }
                    claim.AddClaimObject(id, templateId, stage, complete, gameObject, new AOVector(locX, locY, locZ), new Quaternion(orientX, orientY, orientZ, orientW), itemID, state, health, maxHealth, itemCounts);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void loadClaimResources(final Claim claim) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `claim_resource` where claimID = " + claim.getID());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    final int itemID = rs.getInt("itemID");
                    final int count = rs.getInt("count");
                    claim.AddClaimResource(id, itemID, count);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void loadClaimPermissions(final Claim claim) {
        try {
            final PreparedStatement ps = ContentDatabase.queries.prepare("SELECT * FROM `claim_permission` where claimID = " + claim.getID());
            final ResultSet rs = ContentDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final OID playerOid = OID.fromLong(rs.getLong("playerOid"));
                    final String playerName = HelperFunctions.readEncodedString(rs.getBytes("playerName"));
                    final int permissionLevel = rs.getInt("permissionLevel");
                    claim.AddClaimPermission(playerOid, playerName, permissionLevel);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public int writeClaim(final Claim claim, final String instance) {
        Log.debug("Writing claim data to database");
        int inserted = -1;
        final String tableName = "claim";
        final String columnNames = "instance,locX,locY,locZ,owner,size,forSale,cost,currency,sellerName,name,claimItemTemplate,priority";
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, instance);
            stmt.setFloat(2, claim.getLoc().getX());
            stmt.setFloat(3, claim.getLoc().getY());
            stmt.setFloat(4, claim.getLoc().getZ());
            if (claim.getOwner() != null) {
                stmt.setLong(5, claim.getOwner().toLong());
            }
            else {
                stmt.setLong(5, 0L);
            }
            stmt.setInt(6, claim.getSize());
            stmt.setBoolean(7, claim.getForSale());
            stmt.setInt(8, claim.getCost());
            stmt.setInt(9, claim.getCurrency());
            stmt.setString(10, claim.getSellerName());
            stmt.setString(11, claim.getName());
            stmt.setInt(12, claim.getClaimItemTemplate());
            stmt.setInt(13, claim.getPriority());
            inserted = ContentDatabase.queries.executeInsert(stmt);
            claim.setID(inserted);
        }
        catch (SQLException ex) {}
        Log.debug("Wrote claim data to database");
        return inserted;
    }
    
    public int updateClaim(final Claim claim) {
        Log.debug("Updating claim data to database");
        final String tableName = "claim";
        int updated;
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("UPDATE " + tableName + " set name=?, owner=?, forSale=?, " + "cost=?, currency=?, sellerName=? where id=?");
            stmt.setString(1, claim.getName());
            stmt.setLong(2, claim.getOwner().toLong());
            stmt.setBoolean(3, claim.getForSale());
            stmt.setInt(4, claim.getCost());
            stmt.setInt(5, claim.getCurrency());
            stmt.setString(6, claim.getSellerName());
            stmt.setInt(7, claim.getID());
            Log.debug("CONTENTDB: updating claim with statement: " + stmt.toString());
            updated = ContentDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote claim data to database");
        return updated;
    }
    
    public int deleteClaim(final int claimID) {
        String tableName = "claim";
        String deleteString = "DELETE FROM `" + tableName + "` WHERE id = " + claimID;
        int deleted = ContentDatabase.queries.executeUpdate(deleteString);
        tableName = "claim_action";
        deleteString = "DELETE FROM `" + tableName + "` WHERE claimID = " + claimID;
        deleted = ContentDatabase.queries.executeUpdate(deleteString);
        tableName = "claim_object";
        deleteString = "DELETE FROM `" + tableName + "` WHERE claimID = " + claimID;
        deleted = ContentDatabase.queries.executeUpdate(deleteString);
        return deleted;
    }
    
    public int writeClaimAction(final int claimID, final String action, final String brushType, final AOVector size, final AOVector loc, final AOVector normal, final int material) {
        Log.debug("Writing claim data to database");
        int inserted = -1;
        final String tableName = "claim_action";
        final String columnNames = "claimID,action,brushType,locX,locY,locZ,material,normalX,normalY,normalZ,sizeX,sizeY,sizeZ";
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, claimID);
            stmt.setString(2, action);
            stmt.setString(3, brushType);
            stmt.setFloat(4, loc.getX());
            stmt.setFloat(5, loc.getY());
            stmt.setFloat(6, loc.getZ());
            stmt.setInt(7, material);
            stmt.setFloat(8, normal.getX());
            stmt.setFloat(9, normal.getY());
            stmt.setFloat(10, normal.getZ());
            stmt.setInt(11, (int)size.getX());
            stmt.setInt(12, (int)size.getY());
            stmt.setInt(13, (int)size.getZ());
            inserted = ContentDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException ex) {}
        Log.debug("Wrote claim data to database");
        return inserted;
    }
    
    public void deleteClaimAction(final int spawnID) {
        final String tableName = "claim_action";
        final String deleteString = "DELETE FROM `" + tableName + "` WHERE id = " + spawnID;
        ContentDatabase.queries.executeUpdate(deleteString);
    }
    
    public int writeClaimObject(final int claimID, final int templateId, final int stage, final boolean complete, final String gameObject, final AOVector loc, final Quaternion orient, final int itemID, final String state, final int health, final int maxHealth, final HashMap<Integer, Integer> itemCounts) {
        Log.debug("Writing claim data to database");
        int inserted = -1;
        final String tableName = "claim_object";
        final String columnNames = "claimID,template,stage,complete,gameObject,locX,locY,locZ,orientX,orientY,orientZ,orientW,itemID,objectState,health,maxHealth,item1,item1Count,item2,item2Count,item3,item3Count,item4,item4Count,item5,item5Count,item6,item6Count";
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, claimID);
            stmt.setInt(2, templateId);
            stmt.setInt(3, stage);
            stmt.setBoolean(4, complete);
            stmt.setString(5, gameObject);
            stmt.setFloat(6, loc.getX());
            stmt.setFloat(7, loc.getY());
            stmt.setFloat(8, loc.getZ());
            stmt.setFloat(9, orient.getX());
            stmt.setFloat(10, orient.getY());
            stmt.setFloat(11, orient.getZ());
            stmt.setFloat(12, orient.getW());
            stmt.setInt(13, itemID);
            stmt.setString(14, state);
            stmt.setInt(15, health);
            stmt.setInt(16, maxHealth);
            int parameterIndex = 17;
            for (final int item : itemCounts.keySet()) {
                stmt.setInt(parameterIndex, item);
                ++parameterIndex;
                stmt.setInt(parameterIndex, itemCounts.get(item));
                ++parameterIndex;
            }
            for (int i = itemCounts.size(); i < 6; ++i) {
                stmt.setInt(parameterIndex, -1);
                ++parameterIndex;
                stmt.setInt(parameterIndex, -1);
                ++parameterIndex;
            }
            inserted = ContentDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException ex) {}
        Log.debug("Wrote claim data to database");
        return inserted;
    }
    
    public int updateClaimObjectPosition(final int id, final AOVector loc, final Quaternion orient) {
        Log.debug("Updating claim object in database");
        final String tableName = "claim_object";
        int updated;
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("UPDATE " + tableName + " set locX=?,locY=?,locZ=?,orientX=?,orientY=?,orientZ=?,orientW=? where id=?");
            stmt.setFloat(1, loc.getX());
            stmt.setFloat(2, loc.getY());
            stmt.setFloat(3, loc.getZ());
            stmt.setFloat(4, orient.getX());
            stmt.setFloat(5, orient.getY());
            stmt.setFloat(6, orient.getZ());
            stmt.setFloat(7, orient.getW());
            stmt.setInt(8, id);
            Log.debug("CONTENTDB: updating claim resource with statement: " + stmt.toString());
            updated = ContentDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote claim data to database");
        return updated;
    }
    
    public int updateClaimObjectState(final int id, final int templateID, final int stage, final boolean complete, final String state, final String gameObject, final int health, final int maxHealth, final HashMap<Integer, Integer> itemCounts) {
        Log.debug("Updating claim object state in database");
        final String tableName = "claim_object";
        int updated;
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("UPDATE " + tableName + " set template=?, stage=?, complete=?, objectState=?, " + "gameObject=?, health=?, maxHealth=?, item1=?, item1Count=?, item2=?, item2Count=?, item3=?, item3Count=?, " + "item4=?, item4Count=?, item5=?, item5Count=?, item6=?, item6Count=? where id=?");
            stmt.setInt(1, templateID);
            stmt.setInt(2, stage);
            stmt.setBoolean(3, complete);
            stmt.setString(4, state);
            stmt.setString(5, gameObject);
            stmt.setInt(6, health);
            stmt.setInt(7, maxHealth);
            int parameterIndex = 8;
            for (final int item : itemCounts.keySet()) {
                stmt.setInt(parameterIndex, item);
                ++parameterIndex;
                stmt.setInt(parameterIndex, itemCounts.get(item));
                ++parameterIndex;
            }
            for (int i = itemCounts.size(); i < 6; ++i) {
                stmt.setInt(parameterIndex, -1);
                ++parameterIndex;
                stmt.setInt(parameterIndex, -1);
                ++parameterIndex;
            }
            stmt.setInt(20, id);
            Log.debug("CONTENTDB: updating claim object with statement: " + stmt.toString());
            updated = ContentDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote claim data to database");
        return updated;
    }
    
    public void deleteClaimObject(final int objectID) {
        final String tableName = "claim_object";
        final String deleteString = "DELETE FROM `" + tableName + "` WHERE id = " + objectID;
        ContentDatabase.queries.executeUpdate(deleteString);
    }
    
    public int writeClaimResource(final int claimID, final int itemID, final int count) {
        Log.debug("Writing claim data to database");
        int inserted = -1;
        final String tableName = "claim_resource";
        final String columnNames = "claimID,itemID,count";
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?)");
            stmt.setInt(1, claimID);
            stmt.setInt(2, itemID);
            stmt.setInt(3, count);
            inserted = ContentDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException ex) {}
        Log.debug("Wrote claim data to database");
        return inserted;
    }
    
    public int updateClaimResource(final int id, final int itemID, final int count) {
        Log.debug("Writing claim data to database");
        final String tableName = "claim_resource";
        int updated;
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("UPDATE " + tableName + " set count=? where id=?");
            stmt.setInt(1, count);
            stmt.setInt(2, id);
            Log.debug("CONTENTDB: updating claim resource with statement: " + stmt.toString());
            updated = ContentDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote claim data to database");
        return updated;
    }
    
    public int writeClaimPermission(final int claimID, final OID playerOid, final String playerName, final int permissionLevel) {
        Log.debug("Writing claim data to database");
        int inserted = -1;
        final String tableName = "claim_permission";
        final String columnNames = "claimID,playerOid,playerName,permissionLevel";
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?)");
            stmt.setInt(1, claimID);
            stmt.setLong(2, playerOid.toLong());
            stmt.setString(3, playerName);
            stmt.setInt(4, permissionLevel);
            inserted = ContentDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException ex) {}
        Log.debug("Wrote claim data to database");
        return inserted;
    }
    
    public int updateClaimPermission(final int claimID, final OID playerOid, final int permissionLevel) {
        Log.debug("Writing claim data to database");
        final String tableName = "claim_permission";
        int updated;
        try {
            final PreparedStatement stmt = ContentDatabase.queries.prepare("UPDATE " + tableName + " set permissionLevel=? where claimID=? AND playerOid=?");
            stmt.setInt(1, permissionLevel);
            stmt.setInt(2, claimID);
            stmt.setLong(3, playerOid.toLong());
            Log.debug("CONTENTDB: updating claim resource with statement: " + stmt.toString());
            updated = ContentDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote claim data to database");
        return updated;
    }
    
    public void deleteClaimPermission(final int claimID, final OID playerOid) {
        final String tableName = "claim_permission";
        final String deleteString = "DELETE FROM `" + tableName + "` WHERE claimID = " + claimID + " AND playerOid = " + playerOid.toLong();
        ContentDatabase.queries.executeUpdate(deleteString);
    }
    
    public void close() {
        ContentDatabase.queries.close();
    }
}
