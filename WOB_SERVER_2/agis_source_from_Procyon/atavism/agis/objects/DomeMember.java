// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.math.AOVector;
import atavism.server.engine.BasicWorldNode;
import java.util.Map;
import atavism.server.objects.Template;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.agis.plugins.AgisMobClient;
import atavism.msgsys.IFilter;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.SubjectFilter;
import atavism.msgsys.Message;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.EnginePlugin;
import atavism.agis.plugins.CombatClient;
import atavism.server.util.LockFactory;
import atavism.server.util.Log;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ScheduledFuture;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.msgsys.MessageCallback;

public class DomeMember implements MessageCallback
{
    protected OID oid;
    protected String name;
    protected OID instanceOid;
    protected int domeID;
    protected int timeRemaining;
    protected int permitID;
    protected ArenaWeapon mainHandWeapon;
    protected ArenaWeapon offHandWeapon;
    protected int hearts;
    protected int maxHearts;
    protected boolean regenerating;
    protected Point respawnLocation;
    protected int team;
    protected int score;
    protected int kills;
    protected int deaths;
    protected int damageDealt;
    protected int damageTaken;
    protected ArenaStats stats;
    protected HashMap<String, Serializable> properties;
    protected int[] abilities;
    protected Long cooldown;
    protected Long sub;
    protected boolean active;
    protected int base_speed;
    protected ScheduledFuture<?> schedule;
    protected transient Lock lock;
    public static final int numAbilities = 5;
    public static final int primaryWeaponAbilitySlot = 0;
    public static final int secondaryWeaponAbilitySlot = 4;
    
    public DomeMember(final OID oid, final String name, final int team, final int domeID, final int permitID, final int permitCount, final Point respawnLocation) {
        this.lock = null;
        this.oid = oid;
        this.name = name;
        this.team = team;
        this.domeID = domeID;
        this.permitID = permitID;
        this.timeRemaining = permitCount;
        this.respawnLocation = respawnLocation;
        this.properties = new HashMap<String, Serializable>();
        this.active = true;
        this.abilities = new int[5];
        this.cooldown = System.currentTimeMillis();
        this.base_speed = 7;
        this.clearAbilities();
        this.sendMessage("dome_joined", null);
        final DecrementTimeRemaining decrementTime = new DecrementTimeRemaining();
        this.schedule = Engine.getExecutor().scheduleAtFixedRate(decrementTime, 1L, 1L, TimeUnit.MINUTES);
        Log.error("DOME: created player: " + oid);
        this.lock = LockFactory.makeLock("DomeMemberLock");
        this.regenerating = false;
        EnginePlugin.setObjectProperty(oid, CombatClient.NAMESPACE, "attackable", (Serializable)true);
        final WorldManagerClient.ExtensionMessage healthMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_UPDATE_HEALTH_PROPS, (String)null, oid);
        Engine.getAgent().sendBroadcast((Message)healthMsg);
        this.setWeapons();
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "domeID", (Serializable)domeID);
        this.initialize();
        final RegenerateHealth healthRegen = new RegenerateHealth();
        Engine.getExecutor().schedule(healthRegen, 5L, TimeUnit.SECONDS);
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.oid);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter.addType(CombatClient.MSG_TYPE_DECREMENT_WEAPON_USES);
        this.sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void deactivate(final boolean stillOnline) {
        this.active = false;
        Log.error("DOME: deactivating player: " + this.oid);
        this.schedule.cancel(true);
        this.schedule = null;
        if (stillOnline) {
            this.sendMessage("dome_left", null);
            EnginePlugin.setObjectProperty(this.oid, WorldManagerClient.NAMESPACE, "domeID", (Serializable)(-1));
            EnginePlugin.setObjectProperty(this.oid, CombatClient.NAMESPACE, "attackable", (Serializable)false);
        }
        if (this.sub != null) {
            Engine.getAgent().removeSubscription(this.sub);
            this.sub = null;
        }
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (!this.active) {
            return;
        }
        this.lock.lock();
        try {
            if (msg.getMsgType() == AgisMobClient.MSG_TYPE_ACTIVATE_DOME_ABILITY) {
                final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
                final int slot = (int)eMsg.getProperty("slot");
                final OID targetOid = OID.fromLong((long)eMsg.getProperty("targetOid"));
                this.activateAbility(slot, targetOid);
            }
            else if (msg.getMsgType() == CombatClient.MSG_TYPE_DECREMENT_WEAPON_USES) {
                final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
                final int abilityID = (int)eMsg.getProperty("abilityID");
                this.weaponUsed(abilityID);
            }
            else if (msg instanceof PropertyMessage) {
                final PropertyMessage propMsg = (PropertyMessage)msg;
                Integer equippedItem = (Integer)propMsg.getProperty("equippedItem");
                if (equippedItem != null) {
                    Log.error("DOME: got new equippedItem: " + equippedItem);
                    final SetWeapon setWeap = new SetWeapon();
                    Engine.getExecutor().schedule(setWeap, 100L, TimeUnit.MILLISECONDS);
                }
                else {
                    equippedItem = (Integer)propMsg.getProperty("offHand");
                    if (equippedItem != null) {
                        Log.error("DOME: got new equippedItem: " + equippedItem);
                        final SetWeapon setWeap = new SetWeapon();
                        Engine.getExecutor().schedule(setWeap, 100L, TimeUnit.MILLISECONDS);
                    }
                }
                final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
                if (dead != null && dead && propMsg.getSubject().equals((Object)this.oid)) {
                    this.playerDied();
                    Log.error("DOMEMEMBER: player " + this.oid + " died");
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    protected void setWeapons() {
        final Integer weaponID = (Integer)EnginePlugin.getObjectProperty(this.oid, WorldManagerClient.NAMESPACE, "equippedItem");
        final Integer offhandID = (Integer)EnginePlugin.getObjectProperty(this.oid, WorldManagerClient.NAMESPACE, "offHand");
        Log.error("DOME: weaponID: " + weaponID + " offhandID: " + offhandID);
        if (weaponID == null || weaponID == -1) {
            this.mainHandWeapon = new ArenaWeapon(3, "Unarmed", -1);
            this.setAbility(0, 3);
            Log.error("DOME: got no main hand item");
        }
        else {
            final Template itemTmpl = ObjectManagerClient.getTemplate((int)weaponID, ObjectManagerPlugin.ITEM_TEMPLATE);
            final String itemSubType = (String)itemTmpl.get(InventoryClient.ITEM_NAMESPACE, "subType");
            Log.error("DOME: itemSubType: " + itemSubType);
            final int displayVal = (int)itemTmpl.get(InventoryClient.ITEM_NAMESPACE, "displayVal");
            final int weaponAbility = (int)itemTmpl.get(InventoryClient.ITEM_NAMESPACE, "abilityID");
            if (itemSubType.equals("Ranged Weapon")) {
                this.mainHandWeapon = new ArenaWeapon(weaponAbility, "Ranged", displayVal);
            }
            else if (itemSubType.equals("Melee Weapon")) {
                this.mainHandWeapon = new ArenaWeapon(weaponAbility, "Melee", displayVal);
            }
            else {
                this.mainHandWeapon = new ArenaWeapon(3, "Unarmed", -1);
                this.setAbility(0, 3);
            }
            this.mainHandWeapon.setWeaponID(weaponID);
            final int uses = AgisInventoryClient.getAccountItemCount(this.oid, weaponID);
            this.mainHandWeapon.setUses(uses);
            this.setAbility(0, weaponAbility);
            final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("slot", 0);
            props.put("uses", this.mainHandWeapon.getUses());
            this.sendMessage("ability_uses", props);
        }
        if (offhandID == null || offhandID == -1) {
            this.offHandWeapon = new ArenaWeapon(3, "Unarmed", -1);
            this.setAbility(4, -1);
            Log.error("DOME: got no off hand item");
        }
        else {
            final Template itemTmpl = ObjectManagerClient.getTemplate((int)offhandID, ObjectManagerPlugin.ITEM_TEMPLATE);
            final String itemSubType = (String)itemTmpl.get(InventoryClient.ITEM_NAMESPACE, "subType");
            Log.error("DOME: itemSubType: " + itemSubType);
            final int displayVal = (int)itemTmpl.get(InventoryClient.ITEM_NAMESPACE, "displayVal");
            final int weaponAbility = (int)itemTmpl.get(InventoryClient.ITEM_NAMESPACE, "abilityID");
            if (itemSubType.equals("Ranged Weapon")) {
                this.offHandWeapon = new ArenaWeapon(weaponAbility, "Ranged", displayVal);
            }
            else if (itemSubType.equals("Melee Weapon")) {
                this.offHandWeapon = new ArenaWeapon(weaponAbility, "Melee", displayVal);
            }
            else if (itemSubType.equals("Shield")) {
                this.offHandWeapon = new ArenaWeapon(weaponAbility, "Shield", displayVal);
            }
            else {
                this.offHandWeapon = new ArenaWeapon(3, "Unarmed", -1);
                this.setAbility(4, -1);
            }
            this.offHandWeapon.setWeaponID(offhandID);
            final int uses = AgisInventoryClient.getAccountItemCount(this.oid, offhandID);
            this.offHandWeapon.setUses(uses);
            this.setAbility(4, weaponAbility);
            final HashMap<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("slot", 4);
            props.put("uses", this.offHandWeapon.getUses());
            this.sendMessage("ability_uses", props);
        }
    }
    
    public void activateAbility(final int slot, final OID targetOid) {
        if (!this.active) {
            return;
        }
        final int abilityID = this.abilities[slot];
        Log.error("DOME: got activate ability message with slot: " + slot + " which gives abilityID: " + abilityID);
        if (abilityID != -1) {
            CombatClient.startAbility(abilityID, this.oid, targetOid, null);
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
            if (this.mainHandWeapon.getWeaponID() != -1) {
                final WorldManagerClient.ExtensionMessage itemMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT, (String)null, this.oid);
                itemMsg.setProperty("itemID", (Serializable)this.mainHandWeapon.getWeaponID());
                itemMsg.setProperty("count", (Serializable)(-1));
                Engine.getAgent().sendBroadcast((Message)itemMsg);
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
            if (this.offHandWeapon.getWeaponID() != -1) {
                final WorldManagerClient.ExtensionMessage itemMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT, (String)null, this.oid);
                itemMsg.setProperty("itemID", (Serializable)this.offHandWeapon.getWeaponID());
                itemMsg.setProperty("count", (Serializable)(-1));
                Engine.getAgent().sendBroadcast((Message)itemMsg);
            }
        }
    }
    
    public boolean alterHearts(final int change, final OID caster) {
        if (!this.active) {
            return true;
        }
        this.hearts += change;
        if (this.hearts > this.maxHearts) {
            this.hearts = this.maxHearts;
        }
        EnginePlugin.setObjectProperty(this.oid, WorldManagerClient.NAMESPACE, "hearts", (Serializable)this.hearts);
        if (this.hearts <= 0) {
            this.playerDied();
        }
        else if (!this.regenerating) {
            final RegenerateHealth healthRegen = new RegenerateHealth();
            Engine.getExecutor().schedule(healthRegen, 5L, TimeUnit.SECONDS);
            this.regenerating = true;
        }
        return false;
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
    
    public void setAbility(final int slot, final int abilityID) {
        this.abilities[slot] = abilityID;
        this.sendMessage("arena_abilities", null);
    }
    
    public void clearAbilities() {
        for (int i = 0; i < this.abilities.length; ++i) {
            if (i != 0 && i != 4) {
                this.abilities[i] = -1;
            }
        }
        this.sendMessage("arena_abilities", null);
    }
    
    public void playerDied() {
        this.active = false;
        this.regenerating = false;
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
        if (msgType.equals("dome_joined")) {
            props.put("timeRemaining", this.timeRemaining);
            props.put("domeID", this.domeID);
            handled = true;
        }
        else if (msgType.equals("dome_left")) {
            handled = true;
        }
        else if (msgType.equals("dome_time_remaining")) {
            props.put("timeRemaining", this.timeRemaining);
            handled = true;
        }
        else if (msgType.equals("message_text")) {
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
    
    public class SetWeapon implements Runnable
    {
        @Override
        public void run() {
            DomeMember.this.setWeapons();
        }
    }
    
    public class RegenerateHealth implements Runnable
    {
        @Override
        public void run() {
            if (!DomeMember.this.active) {
                return;
            }
            final WorldManagerClient.ExtensionMessage regenMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_REGEN_HEALTH_MANA, (String)null, DomeMember.this.oid);
            regenMsg.setProperty("amount", (Serializable)1);
            Engine.getAgent().sendBroadcast((Message)regenMsg);
            Engine.getExecutor().schedule(this, 5L, TimeUnit.SECONDS);
        }
    }
    
    public class DecrementTimeRemaining implements Runnable
    {
        @Override
        public void run() {
            final DomeMember this$0 = DomeMember.this;
            --this$0.timeRemaining;
            if (DomeMember.this.timeRemaining == 0) {
                final WorldManagerClient.ExtensionMessage itemMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT, (String)null, DomeMember.this.oid);
                itemMsg.setProperty("itemID", (Serializable)DomeMember.this.permitID);
                itemMsg.setProperty("count", (Serializable)(-1));
                Engine.getAgent().sendBroadcast((Message)itemMsg);
                final WorldManagerClient.ExtensionMessage leaveMsg = new WorldManagerClient.ExtensionMessage(AgisMobClient.MSG_TYPE_DOME_LEAVE_REQUEST, (String)null, DomeMember.this.oid);
                leaveMsg.setProperty("domeID", (Serializable)DomeMember.this.domeID);
                Engine.getAgent().sendBroadcast((Message)leaveMsg);
            }
            else {
                final WorldManagerClient.ExtensionMessage itemMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT, (String)null, DomeMember.this.oid);
                itemMsg.setProperty("itemID", (Serializable)DomeMember.this.permitID);
                itemMsg.setProperty("count", (Serializable)(-1));
                Engine.getAgent().sendBroadcast((Message)itemMsg);
                DomeMember.this.sendMessage("dome_time_remaining", null);
            }
            Log.error("DOME: decrementing time for  player: " + DomeMember.this.oid + " with time remaining: " + DomeMember.this.timeRemaining);
        }
    }
    
    public class DeathTeleport implements Runnable
    {
        @Override
        public void run() {
            Log.debug("ARENA: cleaning up the Arena");
            final BasicWorldNode tnode = new BasicWorldNode();
            tnode.setLoc(DomeMember.this.respawnLocation);
            tnode.setDir(new AOVector());
            WorldManagerClient.updateWorldNode(DomeMember.this.oid, tnode, true);
            WorldManagerClient.refreshWNode(DomeMember.this.oid);
            EnginePlugin.setObjectProperty(DomeMember.this.oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)DomeMember.this.base_speed);
            EnginePlugin.setObjectProperty(DomeMember.this.oid, CombatClient.NAMESPACE, "deadstate", (Serializable)false);
            EnginePlugin.setObjectProperty(DomeMember.this.oid, WorldManagerClient.NAMESPACE, "world.nomove", (Serializable)false);
            EnginePlugin.setObjectProperty(DomeMember.this.oid, WorldManagerClient.NAMESPACE, "world.noturn", (Serializable)false);
            final WorldManagerClient.ExtensionMessage healthMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_UPDATE_HEALTH_PROPS, (String)null, DomeMember.this.oid);
            Engine.getAgent().sendBroadcast((Message)healthMsg);
            DomeMember.this.active = true;
            EnginePlugin.setObjectProperty(DomeMember.this.oid, WorldManagerClient.NAMESPACE, "movement_speed", (Serializable)DomeMember.this.base_speed);
            final RegenerateHealth healthRegen = new RegenerateHealth();
            Engine.getExecutor().schedule(healthRegen, 5L, TimeUnit.SECONDS);
        }
    }
    
    public class Reactivate implements Runnable
    {
        @Override
        public void run() {
            DomeMember.this.active = true;
        }
    }
}
