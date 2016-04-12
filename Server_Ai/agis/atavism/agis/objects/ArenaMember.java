// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.objects.Marker;
import atavism.server.math.AOVector;
import atavism.server.plugins.InstanceClient;
import atavism.server.engine.BasicWorldNode;
import java.util.concurrent.TimeUnit;
import atavism.server.objects.Template;
import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.server.util.Log;
import atavism.msgsys.IFilter;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.SubjectFilter;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.plugins.CombatClient;
import java.util.Map;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.msgsys.MessageCallback;

public class ArenaMember implements MessageCallback
{
    protected OID oid;
    protected String name;
    protected OID instanceOid;
    protected int team;
    protected int score;
    protected int kills;
    protected int deaths;
    protected int damageDealt;
    protected int damageTaken;
    protected ArenaStats stats;
    protected HashMap<String, Serializable> properties;
    protected boolean useWeapons;
    protected boolean useHealth;
    protected int[] abilities;
    protected ArenaWeapon mainHandWeapon;
    protected ArenaWeapon offHandWeapon;
    protected Long sub;
    protected boolean active;
    protected int base_speed;
    public static final int NUM_ABILITIES = 3;
    public static final int NUM_ABILITIES_WITH_WEAPONS = 5;
    public static final int primaryWeaponAbilitySlot = 0;
    public static final int secondaryWeaponAbilitySlot = 4;
    
    public ArenaMember(final OID oid, final String name, final int team, final int base_speed, final boolean useWeapons, final boolean useHealth) {
        this.oid = oid;
        this.name = name;
        this.team = team;
        this.properties = new HashMap<String, Serializable>();
        this.active = true;
        this.useWeapons = useWeapons;
        if (useWeapons) {
            this.abilities = new int[5];
            final HashMap<String, Serializable> propMap = new HashMap<String, Serializable>();
            propMap.put("primaryItem", -1);
            propMap.put("secondaryItem", -1);
            propMap.put("playerAppearance", 0);
            EnginePlugin.setObjectProperties(oid, WorldManagerClient.NAMESPACE, (Map)propMap);
        }
        else {
            this.abilities = new int[3];
        }
        this.clearAbilities();
        this.useHealth = useHealth;
        this.base_speed = base_speed;
        if (useHealth) {
            EnginePlugin.setObjectProperty(oid, CombatClient.NAMESPACE, "attackable", (Serializable)true);
            final WorldManagerClient.ExtensionMessage healthMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_UPDATE_HEALTH_PROPS, (String)null, oid);
            Engine.getAgent().sendBroadcast((Message)healthMsg);
        }
        this.initialize();
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.oid);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter.addType(CombatClient.MSG_TYPE_DECREMENT_WEAPON_USES);
        this.sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void deactivate() {
        this.active = false;
        Log.error("ARENA: deactivating player: " + this.oid);
        if (this.sub != null) {
            Engine.getAgent().removeSubscription((long)this.sub);
            this.sub = null;
        }
        EnginePlugin.setObjectProperty(this.oid, CombatClient.NAMESPACE, "deadstate", (Serializable)false);
        EnginePlugin.setObjectProperty(this.oid, CombatClient.NAMESPACE, "attackable", (Serializable)false);
        if (this.useWeapons) {
            final HashMap<String, Serializable> propMap = new HashMap<String, Serializable>();
            final Integer weaponID = (Integer)EnginePlugin.getObjectProperty(this.oid, WorldManagerClient.NAMESPACE, "equippedItem");
            final Integer offhandID = (Integer)EnginePlugin.getObjectProperty(this.oid, WorldManagerClient.NAMESPACE, "offHand");
            if (weaponID != -1) {
                final Template itemTemplate = ObjectManagerClient.getTemplate((int)weaponID, ObjectManagerPlugin.ITEM_TEMPLATE);
                final int displayVal = (int)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "displayVal");
                propMap.put("primaryItem", displayVal);
            }
            if (offhandID != -1) {
                final Template itemTemplate = ObjectManagerClient.getTemplate((int)offhandID, ObjectManagerPlugin.ITEM_TEMPLATE);
                final int displayVal = (int)itemTemplate.get(InventoryClient.ITEM_NAMESPACE, "displayVal");
                propMap.put("secondaryItem", displayVal);
            }
            propMap.put("playerAppearance", 0);
            EnginePlugin.setObjectProperties(this.oid, WorldManagerClient.NAMESPACE, (Map)propMap);
        }
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (!this.active) {
            return;
        }
        if (msg instanceof PropertyMessage) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
            if (dead != null && dead && propMsg.getSubject().equals((Object)this.oid)) {
                this.playerDied();
                Log.error("ARENAMEMBER: player " + this.oid + " died");
            }
        }
        else if (msg.getMsgType() == CombatClient.MSG_TYPE_DECREMENT_WEAPON_USES) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final int abilityID = (int)eMsg.getProperty("abilityID");
            this.weaponUsed(abilityID);
        }
    }
    
    protected void weaponUsed(final int abilityID) {
        if (this.mainHandWeapon.getAbilityID() == abilityID) {
            if (this.mainHandWeapon.weaponUsed() == 0) {
                this.setAbility(0, 3);
            }
            else {
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("slot", 0);
                props.put("uses", this.mainHandWeapon.getUses());
                this.sendMessage("ability_uses", props);
            }
        }
        else if (this.offHandWeapon.getAbilityID() == abilityID) {
            if (this.offHandWeapon.weaponUsed() == 0) {
                this.setAbility(4, -1);
            }
            else {
                final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
                props.put("slot", 4);
                props.put("uses", this.offHandWeapon.getUses());
                this.sendMessage("ability_uses", props);
            }
        }
    }
    
    public void setProperty(final String propName, final Serializable value) {
        this.properties.put(propName, value);
    }
    
    public Serializable getProperty(final String propName) {
        return this.properties.get(propName);
    }
    
    public void updateScore(final int delta) {
        this.score += delta;
    }
    
    public void addKill() {
        ++this.kills;
    }
    
    public void addDeath() {
        ++this.deaths;
    }
    
    public void addDamageDealt(final int damageDealt) {
        this.damageDealt += damageDealt;
    }
    
    public void addDamageTaken(final int damageTaken) {
        this.damageTaken += damageTaken;
    }
    
    public void weaponPickedUp(final int weaponID, final String weaponType) {
        final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("slot", 0);
        if (weaponType.equals("Melee Weapon")) {
            this.mainHandWeapon = new ArenaWeapon(1, weaponType, 188);
            this.setPlayerProperty("primaryItem", 188);
            this.setPlayerProperty("playerAppearance", -1);
            this.setAbility(0, 1);
            props.put("uses", this.mainHandWeapon.getUses());
        }
        else {
            this.mainHandWeapon = new ArenaWeapon(2, "Ranged", 195);
            this.setPlayerProperty("primaryItem", 195);
            this.setPlayerProperty("playerAppearance", -1);
            this.setAbility(0, 2);
            props.put("uses", this.mainHandWeapon.getUses());
        }
        this.sendMessage("arena_abilities", null);
        this.sendMessage("ability_uses", props);
    }
    
    public void setAbility(final int slot, final int abilityID) {
        this.abilities[slot] = abilityID;
    }
    
    public void clearAbilities() {
        for (int i = 0; i < this.abilities.length; ++i) {
            if (this.useWeapons) {
                if (i == 0) {
                    continue;
                }
                if (i == 4) {
                    continue;
                }
            }
            this.abilities[i] = -1;
        }
    }
    
    public void playerDied() {
        this.active = false;
        this.clearAbilities();
        this.playDeathAnimation();
        final DeathTeleport teleportTimer = new DeathTeleport();
        Engine.getExecutor().schedule(teleportTimer, 4L, TimeUnit.SECONDS);
    }
    
    public void playDeathAnimation() {
        final CoordinatedEffect cE = new CoordinatedEffect("DeathEffect");
        cE.sendSourceOid(true);
        cE.invoke(this.oid, this.oid);
    }
    
    public void playVictoryAnimation() {
        final CoordinatedEffect cE = new CoordinatedEffect("VictoryEffect");
        cE.sendSourceOid(true);
        cE.invoke(this.oid, this.oid);
    }
    
    public void queueReactivation(final int seconds) {
        final Reactivate reactivateTimer = new Reactivate();
        Engine.getExecutor().schedule(reactivateTimer, seconds, TimeUnit.SECONDS);
    }
    
    public void setPlayerProperty(final String prop, final Serializable value) {
        final PropertyMessage propMsg = new PropertyMessage(this.oid, this.oid);
        propMsg.setProperty(prop, value);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public boolean sendMessage(final String msgType, final Serializable data) {
        boolean handled = false;
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType.equals("message_text")) {
            final String value = (String)data;
            props.put("message", value);
            handled = true;
        }
        else if (msgType.equals("arena_abilities")) {
            props.put("numAbilities", this.abilities.length);
            for (int i = 0; i < this.abilities.length; ++i) {
                final int abilityID = this.abilities[i];
                props.put("ability" + i + "ID", abilityID);
            }
        }
        else if (msgType.equals("ability_uses")) {
            final HashMap<String, Serializable> inProps = (HashMap<String, Serializable>)data;
            props.put("slot", inProps.get("slot"));
            props.put("uses", inProps.get("uses"));
            handled = true;
        }
        else if (msgType == "attack_cooldown") {
            final HashMap<String, Serializable> map = (HashMap<String, Serializable>)data;
            props.put("cooldown_length", map.get("length"));
            props.put("ability_id", map.get("abilityID"));
            handled = true;
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, this.oid, this.oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
        return handled;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public void setInstanceOid(final OID instanceOid) {
        this.instanceOid = instanceOid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getTeam() {
        return this.team;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public int getDeaths() {
        return this.deaths;
    }
    
    public int getDamageDealt() {
        return this.damageDealt;
    }
    
    public int getDamageTaken() {
        return this.damageTaken;
    }
    
    public ArenaStats getStats() {
        return this.stats;
    }
    
    public HashMap<String, Serializable> getProperties() {
        return this.properties;
    }
    
    public int[] getAbilities() {
        return this.abilities;
    }
    
    public void setSub(final Long sub) {
        this.sub = sub;
    }
    
    public Long getSub() {
        return this.sub;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public boolean getActive() {
        return this.active;
    }
    
    public class DeathTeleport implements Runnable
    {
        @Override
        public void run() {
            ArenaMember.this.active = true;
            Log.error("ARENA: teleporting dead player: " + ArenaMember.this.oid);
            final HashMap<String, Serializable> propMap = new HashMap<String, Serializable>();
            propMap.put("world.nomove", false);
            propMap.put("world.noturn", false);
            EnginePlugin.setObjectProperties(ArenaMember.this.oid, WorldManagerClient.NAMESPACE, (Map)propMap);
            final BasicWorldNode tnode = new BasicWorldNode();
            final String markerName = "team" + ArenaMember.this.team + "Spawn";
            if (ArenaMember.this.instanceOid == null) {
                Log.error("ARENA: instance Oid is null");
                ArenaMember.this.instanceOid = WorldManagerClient.getObjectInfo(ArenaMember.this.oid).instanceOid;
            }
            Log.error("ARENA: teleporting dead player to instance: " + ArenaMember.this.instanceOid);
            final Marker spawn = InstanceClient.getMarker(ArenaMember.this.instanceOid, markerName);
            Log.error("ARENA: got marker: " + spawn);
            tnode.setLoc(spawn.getPoint());
            tnode.setDir(new AOVector());
            Log.error("ARENA: set respawn loc");
            WorldManagerClient.updateWorldNode(ArenaMember.this.oid, tnode, true);
            WorldManagerClient.refreshWNode(ArenaMember.this.oid);
            Log.error("ARENA: updated world node");
            EnginePlugin.setObjectProperty(ArenaMember.this.oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)ArenaMember.this.base_speed);
            if (ArenaMember.this.useHealth) {
                Log.error("ARENA: setting dead state to false and resetting health props");
                EnginePlugin.setObjectProperty(ArenaMember.this.oid, CombatClient.NAMESPACE, "deadstate", (Serializable)false);
                final WorldManagerClient.ExtensionMessage healthMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_UPDATE_HEALTH_PROPS, (String)null, ArenaMember.this.oid);
                Engine.getAgent().sendBroadcast((Message)healthMsg);
            }
        }
    }
    
    public class Reactivate implements Runnable
    {
        @Override
        public void run() {
            ArenaMember.this.active = true;
        }
    }
}
