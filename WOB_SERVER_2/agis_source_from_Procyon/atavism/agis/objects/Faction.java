// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.server.util.Log;
import java.util.HashMap;
import java.io.Serializable;

public class Faction implements Serializable
{
    protected int id;
    protected String name;
    protected String group;
    protected int category;
    protected boolean isPublic;
    protected int defaultStance;
    HashMap<Integer, Integer> defaultStances;
    private static final long serialVersionUID = 1L;
    
    public Faction(final int id, final String name, final String group, final int category) {
        this.id = -1;
        this.name = null;
        this.group = null;
        this.category = 0;
        this.isPublic = false;
        this.defaultStances = new HashMap<Integer, Integer>();
        this.setID(id);
        this.setName(name);
        this.setGroup(group);
        this.setCategory(category);
        Log.debug("FACTION: creating faction: " + name);
    }
    
    public int getDefaultReputation(final int factionID) {
        int stance = 0;
        if (this.defaultStances.containsKey(factionID)) {
            stance = this.defaultStances.get(factionID);
        }
        else {
            stance = this.defaultStance;
        }
        if (stance == -2) {
            return -3000;
        }
        if (stance == -1) {
            return -1500;
        }
        if (stance == 0) {
            return -500;
        }
        if (stance == 1) {
            return 500;
        }
        if (stance == 2) {
            return 1500;
        }
        if (stance == 3) {
            return 3000;
        }
        return -500;
    }
    
    public static PlayerFactionData addFactionToPlayer(final OID targetOid, final OID oid, final Faction faction, final int playerFaction) {
        final HashMap<Integer, PlayerFactionData> pfdMap = (HashMap<Integer, PlayerFactionData>)EnginePlugin.getObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "factionData");
        if (pfdMap == null) {
            Log.error("FACTION: pfdMap is null in addFactionToPlayer with player " + targetOid);
            return null;
        }
        if (faction == null) {
            return null;
        }
        if (pfdMap.containsKey(faction.getName())) {
            Log.error("FACTION: tried adding faction " + faction.getName() + " to player " + targetOid + " but player already has it");
            return pfdMap.get(faction.getName());
        }
        final int reputation = faction.getDefaultReputation(playerFaction);
        final PlayerFactionData newFactionData = new PlayerFactionData(faction.getID(), faction.getName(), reputation, faction.getGroup(), faction.getCategory());
        pfdMap.put(faction.getID(), newFactionData);
        EnginePlugin.setObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "factionData", (Serializable)pfdMap);
        return newFactionData;
    }
    
    public static void sendFactionData(final OID oid) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "reputations");
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public int getCategory() {
        return this.category;
    }
    
    public void setCategory(final int category) {
        this.category = category;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public void setGroup(final String group) {
        this.group = group;
    }
    
    public boolean getIsPublic() {
        return this.isPublic;
    }
    
    public void setIsPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public int getDefaultStance() {
        return this.defaultStance;
    }
    
    public void setDefaultStance(final int defaultStance) {
        this.defaultStance = defaultStance;
    }
    
    public HashMap<Integer, Integer> getDefaultStances() {
        return this.defaultStances;
    }
    
    public void setDefaultStances(final HashMap<Integer, Integer> defaultStances) {
        this.defaultStances = defaultStances;
    }
}
