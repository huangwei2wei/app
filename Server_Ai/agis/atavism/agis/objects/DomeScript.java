// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.HashMap;
import atavism.server.objects.SpawnData;
import java.util.Random;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.engine.EnginePlugin;
import java.util.concurrent.TimeUnit;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import java.util.Iterator;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.math.AOVector;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.DisplayContext;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.agis.plugins.AgisMobClient;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import java.util.ArrayList;
import atavism.server.objects.Template;
import atavism.msgsys.MessageDispatch;
import atavism.msgsys.MessageCallback;

public class DomeScript implements MessageCallback, MessageDispatch
{
    int scriptID;
    int domeID;
    Template statueObject;
    ArrayList<Template> fountainObjects;
    OID instanceOID;
    OID statueOID;
    ArrayList<OID> fountainOIDs;
    Point loc;
    ArrayList<Point> fountainLocs;
    Quaternion orient;
    int activationItem;
    boolean active;
    protected Long sub;
    
    public DomeScript(final int scriptID, final int domeID, final OID instanceOID) {
        this.scriptID = scriptID;
        this.domeID = domeID;
        this.instanceOID = instanceOID;
        this.active = false;
        this.loc = new Point(437516.0f, 42875.0f, -204685.0f);
        this.orient = new Quaternion(0.0f, 4.0E-8f, 0.0f, 1.0f);
        (this.fountainLocs = new ArrayList<Point>()).add(new Point(432911.0f, 42156.0f, -197056.0f));
        this.fountainLocs.add(new Point(429751.0f, 42156.0f, -209212.0f));
        this.fountainLocs.add(new Point(444838.0f, 42156.0f, -200449.0f));
        this.fountainLocs.add(new Point(441784.0f, 42156.0f, -212458.0f));
        this.fountainObjects = new ArrayList<Template>();
        this.fountainOIDs = new ArrayList<OID>();
        this.activationItem = 19;
        this.initialize();
        this.createStatueTemplate();
        this.spawnStatue();
    }
    
    public void initialize() {
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(AgisMobClient.MSG_TYPE_MOB_KILLED);
        filter.addType(AgisMobClient.MSG_TYPE_OBJECT_ACTIVATED);
        this.sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    protected void createStatueTemplate() {
        final DisplayContext dc = new DisplayContext("prop_knightfountain.mesh", true);
        dc.setDisplayID(85);
        (this.statueObject = new Template()).put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)"Dome Viking Statue");
        this.statueObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        this.statueObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)this.instanceOID);
        this.statueObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)this.loc);
        this.statueObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, (Serializable)this.orient);
        this.statueObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        final float scaleVal = 4.0f;
        final AOVector v = new AOVector(scaleVal, scaleVal, scaleVal);
        this.statueObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SCALE, (Serializable)v);
        this.statueOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", this.statueObject);
        final DisplayContext dc2 = new DisplayContext("prop_knightfountain.mesh", true);
        dc2.setDisplayID(84);
        for (final Point loc : this.fountainLocs) {
            final Template fountainObject = new Template();
            fountainObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)"Dome Statue Fountain");
            fountainObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
            fountainObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)this.instanceOID);
            fountainObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)loc);
            fountainObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, (Serializable)this.orient);
            fountainObject.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc2);
            fountainObject.put(Namespace.WORLD_MANAGER, "Usable", (Serializable)true);
            fountainObject.put(Namespace.WORLD_MANAGER, "targetable", (Serializable)false);
            fountainObject.put(Namespace.WORLD_MANAGER, "activated", (Serializable)false);
            final OID fountainOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", fountainObject);
            this.fountainOIDs.add(fountainOID);
            this.fountainObjects.add(fountainObject);
        }
    }
    
    public void dispatchMessage(final Message message, final int flags, final MessageCallback callback) {
        Engine.defaultDispatchMessage(message, flags, callback);
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg.getMsgType() == AgisMobClient.MSG_TYPE_MOB_KILLED) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final Integer scriptID = (Integer)eMsg.getProperty("scriptID");
            Log.error("Got Mob killed with scriptID: " + scriptID);
            if (scriptID != null && scriptID != -1) {
                final RespawnTimer teleportTimer = new RespawnTimer();
                Engine.getExecutor().schedule(teleportTimer, 5L, TimeUnit.MINUTES);
                this.spawnCoins();
            }
        }
        else if (msg.getMsgType() == AgisMobClient.MSG_TYPE_OBJECT_ACTIVATED) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID subject = eMsg.getSubject();
            final OID objectOid = OID.fromLong((long)eMsg.getProperty("object"));
            Log.error("ACTIVATE: got activate message with subject: " + subject + " and object: " + objectOid);
            this.activateFountain(subject, objectOid);
        }
    }
    
    protected void activateFountain(final OID subject, final OID object) {
        if (!this.fountainOIDs.contains(object)) {
            return;
        }
        final Integer domeID = (Integer)EnginePlugin.getObjectProperty(subject, WorldManagerClient.NAMESPACE, "domeID");
        if (domeID == null || domeID != this.domeID) {
            return;
        }
        Log.error("FOUNTAIN: activating fountain: " + object + "; finding item: " + this.activationItem);
        final int itemCount = AgisInventoryClient.getAccountItemCount(subject, this.activationItem);
        Log.error("FOUNTAIN: activating fountain with item count: " + itemCount);
        if (itemCount > 0) {
            final boolean activated = (boolean)EnginePlugin.getObjectProperty(object, WorldManagerClient.NAMESPACE, "activated");
            if (!activated) {
                WorldManagerClient.sendObjChatMsg(subject, 2, "Activating fountain");
                EnginePlugin.setObjectProperty(object, WorldManagerClient.NAMESPACE, "", (Serializable)true);
                final WorldManagerClient.ExtensionMessage itemMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT, (String)null, subject);
                itemMsg.setProperty("itemID", (Serializable)this.activationItem);
                itemMsg.setProperty("count", (Serializable)(-1));
                Engine.getAgent().sendBroadcast((Message)itemMsg);
                EnginePlugin.setObjectProperty(object, WorldManagerClient.NAMESPACE, "activated", (Serializable)true);
            }
        }
        else {
            WorldManagerClient.sendObjChatMsg(subject, 2, "Missing reagent to activate fountain");
        }
        boolean allActivated = true;
        for (final OID fountainOid : this.fountainOIDs) {
            final boolean activated2 = (boolean)EnginePlugin.getObjectProperty(fountainOid, WorldManagerClient.NAMESPACE, "activated");
            if (!activated2) {
                allActivated = false;
            }
        }
        if (allActivated) {
            this.spawnMob();
        }
    }
    
    protected void spawnStatue() {
        Log.error("Spawning statue");
        if (this.statueOID != null && !this.active) {
            WorldManagerClient.spawn(this.statueOID);
            this.active = true;
            Log.error("Spawned statue");
            for (final OID fountainOid : this.fountainOIDs) {
                WorldManagerClient.spawn(fountainOid);
            }
        }
    }
    
    protected void spawnCoins() {
        final ArrayList<Point> coinLocs = new ArrayList<Point>();
        final Random random = new Random();
        int xIncrement = 0;
        int zIncrement = 0;
        Log.error("Getting coin locs");
        for (int i = 0; i < 16; ++i) {
            final Point p = new Point(this.loc.getX(), this.loc.getY(), this.loc.getZ());
            final int direction = random.nextInt(4);
            if (direction == 0) {
                xIncrement += random.nextInt(1000);
                p.add(xIncrement, 0, zIncrement);
            }
            else if (direction == 1) {
                zIncrement += random.nextInt(1000);
                xIncrement += random.nextInt(1000);
                p.add(-xIncrement, 0, -zIncrement);
            }
            else if (direction == 2) {
                zIncrement += random.nextInt(1000);
                p.add(xIncrement, 0, -zIncrement);
            }
            else {
                zIncrement += random.nextInt(1000);
                xIncrement += random.nextInt(1000);
                p.add(-xIncrement, 0, zIncrement);
            }
            coinLocs.add(p);
        }
        Log.error("Sending coin spawns");
        int coinID = 1;
        final Iterator<Point> iterator = coinLocs.iterator();
        while (iterator.hasNext()) {
            final Point p = iterator.next();
            final SpawnData spawnData = new SpawnData();
            spawnData.setProperty("id", (Serializable)coinID);
            spawnData.setTemplateID(4);
            spawnData.setInstanceOid(this.instanceOID);
            spawnData.setLoc(p);
            spawnData.setNumSpawns(1);
            spawnData.setSpawnRadius(0);
            spawnData.setRespawnTime(-1);
            spawnData.setCorpseDespawnTime(500);
            final HashMap<String, Serializable> spawnProps = new HashMap<String, Serializable>();
            spawnProps.put("domeID", this.domeID);
            spawnProps.put("duration", 30000);
            spawnData.setProperty("props", (Serializable)spawnProps);
            spawnData.setProperty("itemID", (Serializable)15);
            final WorldManagerClient.ExtensionMessage spawnMsg = new WorldManagerClient.ExtensionMessage();
            spawnMsg.setMsgType(AgisMobClient.MSG_TYPE_SPAWN_DOME_MOB);
            spawnMsg.setProperty("spawnData", (Serializable)spawnData);
            spawnMsg.setProperty("spawnType", (Serializable)(-4));
            spawnMsg.setProperty("roamRadius", (Serializable)0);
            Engine.getAgent().sendBroadcast((Message)spawnMsg);
            Log.error("Sending spawn coin at loc: " + p);
            ++coinID;
        }
        Log.error("Sent coin spawns");
    }
    
    public boolean checkLocation(final Point loc) {
        return Point.distanceTo(loc, this.loc) < 10000.0f;
    }
    
    public void spawnMob() {
        if (!this.active) {
            return;
        }
        Log.error("SCRIPT: spawning statue mob");
        this.active = false;
        WorldManagerClient.despawn(this.statueOID);
        for (final OID fountainOID : this.fountainOIDs) {
            EnginePlugin.setObjectProperty(fountainOID, WorldManagerClient.NAMESPACE, "activated", (Serializable)false);
            WorldManagerClient.despawn(fountainOID);
        }
        final SpawnData spawnData = new SpawnData();
        spawnData.setProperty("id", (Serializable)1);
        spawnData.setTemplateID(12);
        spawnData.setInstanceOid(this.instanceOID);
        spawnData.setLoc(this.loc);
        spawnData.setOrientation(this.orient);
        spawnData.setNumSpawns(1);
        spawnData.setSpawnRadius(0);
        spawnData.setRespawnTime(-1);
        spawnData.setCorpseDespawnTime(5000);
        final HashMap<String, Serializable> spawnProps = new HashMap<String, Serializable>();
        spawnProps.put("domeID", this.domeID);
        spawnProps.put("scriptID", this.scriptID);
        spawnProps.put("primaryItem", 188);
        spawnData.setProperty("props", (Serializable)spawnProps);
        final WorldManagerClient.ExtensionMessage spawnMsg = new WorldManagerClient.ExtensionMessage();
        spawnMsg.setMsgType(AgisMobClient.MSG_TYPE_SPAWN_DOME_MOB);
        spawnMsg.setProperty("spawnData", (Serializable)spawnData);
        spawnMsg.setProperty("spawnType", (Serializable)1);
        spawnMsg.setProperty("roamRadius", (Serializable)0);
        Engine.getAgent().sendBroadcast((Message)spawnMsg);
    }
    
    public class RespawnTimer implements Runnable
    {
        @Override
        public void run() {
            DomeScript.this.spawnStatue();
        }
    }
}
