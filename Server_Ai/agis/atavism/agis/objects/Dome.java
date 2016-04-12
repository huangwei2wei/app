// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.engine.BasicWorldNode;
import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.plugins.AgisMobClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.math.Quaternion;
import atavism.server.objects.SpawnData;
import atavism.server.util.Log;
import java.util.concurrent.ScheduledFuture;
import atavism.server.math.Point;
import java.io.Serializable;
import java.util.ArrayList;
import atavism.server.engine.OID;
import java.util.HashMap;

public class Dome
{
    protected HashMap<OID, DomeMember> domeMembers;
    ArrayList<HashMap<String, Serializable>> spawns;
    protected int id;
    protected OID instanceOid;
    protected int permitID;
    protected Point respawnLocation;
    protected DomeScript script;
    protected ScheduledFuture<?> scheduledExecutioner;
    public static final int MOBTYPE_NORMAL = 0;
    public static final int MOBTYPE_BOSS = 1;
    public static final int MOBTYPE_WARDEN = 3;
    public static final int MOBTYPE_LOOT = -4;
    public static final int MOBTYPE_HEART = -5;
    public static final int MOBTYPE_ABILITY = -6;
    
    public Dome(final int id, final OID instanceOid, final int permitID, final ArrayList<HashMap<String, Serializable>> spawns) {
        this.domeMembers = new HashMap<OID, DomeMember>();
        Log.error("DOME: loading dome with id: " + id + " instanceOID: " + instanceOid + " and numSpawns: " + spawns.size());
        this.id = id;
        this.instanceOid = instanceOid;
        this.spawns = spawns;
        this.permitID = permitID;
        this.respawnLocation = new Point(181800.0f, 41500.0f, 24700.0f);
        this.spawnMobs();
        this.script = new DomeScript(1, id, instanceOid);
    }
    
    protected void spawnMobs() {
        int spawnNum = 0;
        for (final HashMap<String, Serializable> props : this.spawns) {
            final SpawnData spawnData = new SpawnData();
            spawnData.setProperty("id", (Serializable)spawnNum);
            spawnData.setTemplateID((int)props.get("mobTemplate"));
            spawnData.setInstanceOid(this.instanceOid);
            spawnData.setLoc((Point)props.get("loc"));
            spawnData.setOrientation((Quaternion)props.get("orient"));
            spawnData.setNumSpawns(1);
            spawnData.setSpawnRadius(0);
            spawnData.setRespawnTime(60000);
            spawnData.setCorpseDespawnTime(5000);
            final HashMap<String, Serializable> spawnProps = new HashMap<String, Serializable>();
            final int spawnType = props.get("spawnType");
            if (spawnType == 0) {
                spawnProps.put("domeID", this.id);
                spawnProps.put("hearts", 5);
            }
            else if (spawnType == 3) {
                spawnProps.put("DomeWarden", this.id);
            }
            spawnProps.put("domeID", this.id);
            spawnData.setProperty("props", (Serializable)spawnProps);
            final WorldManagerClient.ExtensionMessage spawnMsg = new WorldManagerClient.ExtensionMessage();
            spawnMsg.setMsgType(AgisMobClient.MSG_TYPE_SPAWN_DOME_MOB);
            spawnMsg.setProperty("spawnData", (Serializable)spawnData);
            spawnMsg.setProperty("spawnType", (Serializable)spawnType);
            spawnMsg.setProperty("roamRadius", (Serializable)props.get("roamRadius"));
            Engine.getAgent().sendBroadcast((Message)spawnMsg);
            ++spawnNum;
        }
    }
    
    public void addPlayer(final OID player, final int permitCount) {
        final String name = WorldManagerClient.getObjectInfo(player).name;
        final DomeMember member = new DomeMember(player, name, 0, this.id, this.permitID, permitCount, this.respawnLocation);
        this.domeMembers.put(player, member);
    }
    
    public void removePlayer(final OID player, final boolean stillOnline) {
        DomeMember member = this.domeMembers.remove(player);
        if (member != null) {
            member.deactivate(stillOnline);
            member = null;
        }
    }
    
    public void activateAbility(final OID player, final int slot, final OID targetOid) {
        if (this.domeMembers.containsKey(player)) {
            this.domeMembers.get(player).activateAbility(slot, targetOid);
        }
    }
    
    public void alterPlayerHearts(final OID player, final int change, final OID caster) {
        if (this.domeMembers.containsKey(player)) {
            this.domeMembers.get(player).alterHearts(change, caster);
        }
    }
    
    public int getPermitID() {
        return this.permitID;
    }
    
    public class LocationCheck implements Runnable
    {
        @Override
        public void run() {
            for (final OID oid : Dome.this.domeMembers.keySet()) {
                WorldManagerClient.refreshWNode(oid);
                final BasicWorldNode node = WorldManagerClient.getWorldNode(oid);
                final Point loc = node.getLoc();
                if (Dome.this.script.checkLocation(loc)) {
                    Dome.this.script.spawnMob();
                }
            }
        }
    }
}
