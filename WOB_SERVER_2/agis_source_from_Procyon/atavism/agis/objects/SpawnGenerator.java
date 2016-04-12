// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.msgsys.IFilter;
import atavism.msgsys.SubjectFilter;
import atavism.server.engine.EnginePlugin;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Points;
import java.util.concurrent.TimeUnit;
import atavism.server.objects.EntityManager;
import atavism.server.engine.Namespace;
import atavism.server.messages.PropertyMessage;
import atavism.server.engine.Engine;
import atavism.msgsys.Message;
import java.util.Iterator;
import atavism.server.util.Log;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.util.AORuntimeException;
import java.util.ArrayList;
import java.util.HashMap;
import atavism.server.objects.ObjectStub;
import java.util.List;
import java.util.Map;
import atavism.server.objects.ObjectFactory;
import atavism.server.objects.SpawnData;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.msgsys.MessageDispatch;
import atavism.msgsys.MessageCallback;

public class SpawnGenerator implements MessageCallback, MessageDispatch, Runnable
{
    protected int spawnID;
    protected OID instanceOid;
    protected String name;
    protected Point loc;
    protected Quaternion orient;
    protected int spawnRadius;
    protected int respawnTime;
    protected int numSpawns;
    protected int corpseDespawnTime;
    protected SpawnData spawnData;
    protected ObjectFactory factory;
    protected Map<OID, Long> deathWatchMap;
    protected List<ObjectStub> spawns;
    private static Map<OID, HashMap<Integer, SpawnGenerator>> instanceContent;
    private static final long serialVersionUID = 1L;
    
    static {
        SpawnGenerator.instanceContent = new HashMap<OID, HashMap<Integer, SpawnGenerator>>();
    }
    
    public SpawnGenerator() {
        this.spawnID = -1;
        this.instanceOid = null;
        this.name = null;
        this.loc = null;
        this.orient = null;
        this.spawnRadius = 0;
        this.respawnTime = 0;
        this.numSpawns = 3;
        this.corpseDespawnTime = -1;
        this.spawnData = null;
        this.factory = null;
        this.deathWatchMap = new HashMap<OID, Long>();
    }
    
    public SpawnGenerator(final String name) {
        this.spawnID = -1;
        this.instanceOid = null;
        this.name = null;
        this.loc = null;
        this.orient = null;
        this.spawnRadius = 0;
        this.respawnTime = 0;
        this.numSpawns = 3;
        this.corpseDespawnTime = -1;
        this.spawnData = null;
        this.factory = null;
        this.deathWatchMap = new HashMap<OID, Long>();
        this.setName(name);
    }
    
    public SpawnGenerator(final SpawnData data) {
        this.spawnID = -1;
        this.instanceOid = null;
        this.name = null;
        this.loc = null;
        this.orient = null;
        this.spawnRadius = 0;
        this.respawnTime = 0;
        this.numSpawns = 3;
        this.corpseDespawnTime = -1;
        this.spawnData = null;
        this.factory = null;
        this.deathWatchMap = new HashMap<OID, Long>();
        this.initialize(data);
    }
    
    public void initialize(final SpawnData data) {
        this.setSpawnData(data);
        this.setName(data.getName());
        this.setSpawnID((int)data.getProperty("id"));
        this.setInstanceOid(data.getInstanceOid());
        this.setLoc(data.getLoc());
        this.setOrientation(data.getOrientation());
        this.setSpawnRadius(data.getSpawnRadius());
        this.setNumSpawns(data.getNumSpawns());
        this.setRespawnTime(data.getRespawnTime());
        if (data.getCorpseDespawnTime() != null) {
            this.setCorpseDespawnTime(data.getCorpseDespawnTime());
        }
    }
    
    public void activate() {
        try {
            this.spawns = new ArrayList<ObjectStub>(this.numSpawns);
            for (int i = 0; i < this.numSpawns; ++i) {
                this.spawnObject();
            }
        }
        catch (Exception e) {
            throw new AORuntimeException("activate failed", (Throwable)e);
        }
    }
    
    public void deactivate() {
        if (this.spawns == null) {
            return;
        }
        final List<ObjectStub> cleanupSpawns = this.spawns;
        for (final ObjectStub obj : cleanupSpawns) {
            try {
                obj.despawn();
                ObjectManagerClient.unloadObject(obj.getOid());
                this.removeDeathWatch(obj.getOid());
            }
            catch (Exception e) {
                Log.exception("SpawnGenerator.deactivate()", e);
            }
        }
        this.spawns = null;
    }
    
    public void dispatchMessage(final Message message, final int flags, final MessageCallback callback) {
        Engine.defaultDispatchMessage(message, flags, callback);
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof PropertyMessage) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final OID oid = propMsg.getSubject();
            final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
            if (dead != null && dead) {
                this.removeDeathWatch(oid);
                final ObjectStub obj = (ObjectStub)EntityManager.getEntityByNamespace(oid, Namespace.MOB);
                if (obj != null && this.corpseDespawnTime != -1) {
                    Log.debug("DESPAWN: scheduling despawn in " + this.corpseDespawnTime);
                    Engine.getExecutor().schedule(new CorpseDespawner(obj), this.corpseDespawnTime, TimeUnit.MILLISECONDS);
                }
                if (this.respawnTime != -1) {
                    Engine.getExecutor().schedule(this, this.respawnTime, TimeUnit.MILLISECONDS);
                }
                return;
            }
            final Boolean empty = (Boolean)propMsg.getProperty("objectEmpty");
            if (empty != null && empty) {
                Log.debug("SPAWNGEN: mob is empty, despawning");
                this.removeDeathWatch(oid);
                final ObjectStub obj2 = (ObjectStub)EntityManager.getEntityByNamespace(oid, Namespace.MOB);
                if (obj2 != null) {
                    Engine.getExecutor().schedule(new CorpseDespawner(obj2), 500L, TimeUnit.MILLISECONDS);
                }
                if (this.respawnTime != -1) {
                    Engine.getExecutor().schedule(this, this.respawnTime, TimeUnit.MILLISECONDS);
                }
                return;
            }
            final Boolean tamed = (Boolean)propMsg.getProperty("tamed");
            if (tamed != null && tamed) {
                Log.debug("SPAWNGEN: mob is tamed, despawning");
                this.removeDeathWatch(oid);
                final ObjectStub obj3 = (ObjectStub)EntityManager.getEntityByNamespace(oid, Namespace.MOB);
                if (obj3 != null && this.corpseDespawnTime != -1) {
                    Engine.getExecutor().schedule(new CorpseDespawner(obj3), this.corpseDespawnTime, TimeUnit.MILLISECONDS);
                }
                if (this.respawnTime != -1) {
                    Engine.getExecutor().schedule(this, this.respawnTime, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    protected void spawnObject() {
        if (this.spawns == null) {
            return;
        }
        final Point loc = Points.findNearby(this.getLoc(), this.spawnRadius);
        ObjectStub obj = null;
        obj = this.factory.makeObject(this.spawnData, this.instanceOid, loc);
        if (obj == null) {
            Log.error("SpawnGenerator: Factory.makeObject failed, returned null, factory=" + this.factory);
            return;
        }
        if (Log.loggingDebug) {
            Log.debug("SpawnGenerator.spawnObject: name=" + this.getName() + ", created object " + obj + " at loc=" + loc);
        }
        this.addDeathWatch(obj.getOid());
        obj.spawn();
        this.spawns.add(obj);
        this.updateObjectProperties(obj);
        if (Log.loggingDebug) {
            Log.debug("SpawnGenerator.spawnObject: name=" + this.getName() + ", spawned obj " + obj);
        }
    }
    
    protected void updateObjectProperties(final ObjectStub obj) {
        final HashMap<String, Serializable> props = (HashMap<String, Serializable>)this.spawnData.getProperty("props");
        if (props != null) {
            for (final String prop : props.keySet()) {
                EnginePlugin.setObjectProperty(obj.getOid(), WorldManagerClient.NAMESPACE, prop, (Serializable)props.get(prop));
            }
        }
        final String baseAction = (String)this.spawnData.getProperty("baseAction");
        if (baseAction != null) {
            EnginePlugin.setObjectProperty(obj.getOid(), WorldManagerClient.NAMESPACE, "currentAction", (Serializable)baseAction);
        }
        final Integer merchantTable = (Integer)this.spawnData.getProperty("merchantTable");
        if (merchantTable != null) {
            EnginePlugin.setObjectProperty(obj.getOid(), WorldManagerClient.NAMESPACE, "merchantTable", (Serializable)merchantTable);
        }
    }
    
    protected void spawnObject(final int millis) {
        if (this.spawns == null) {
            return;
        }
        Log.debug("SpawnGenerator: adding spawn timer");
        Engine.getExecutor().schedule(this, millis, TimeUnit.MILLISECONDS);
    }
    
    public void run() {
        try {
            this.spawnObject();
        }
        catch (AORuntimeException e) {
            Log.exception("SpawnGenerator.run caught exception: ", (Exception)e);
        }
    }
    
    protected void addDeathWatch(final OID oid) {
        if (Log.loggingDebug) {
            Log.debug("SpawnGenerator.addDeathWatch: oid=" + oid);
        }
        final SubjectFilter filter = new SubjectFilter(oid);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        final Long sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        this.deathWatchMap.put(oid, sub);
    }
    
    protected void removeDeathWatch(final OID oid) {
        final Long sub = this.deathWatchMap.remove(oid);
        if (sub != null) {
            if (Log.loggingDebug) {
                Log.debug("SpawnGenerator.removeDeathWatch: oid=" + oid);
            }
            Engine.getAgent().removeSubscription(sub);
        }
    }
    
    public int getSpawnId() {
        return this.spawnID;
    }
    
    public void setSpawnID(final int spawnID) {
        this.spawnID = spawnID;
    }
    
    public OID getInstanceOid() {
        return this.instanceOid;
    }
    
    public void setInstanceOid(final OID oid) {
        if (this.instanceOid == null) {
            this.instanceOid = oid;
            addInstanceContent(this);
            return;
        }
        throw new AORuntimeException("Cannot change SpawnGenerator instanceOid, from=" + this.instanceOid + " to=" + oid);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setLoc(final Point p) {
        this.loc = p;
    }
    
    public Point getLoc() {
        return this.loc;
    }
    
    public void setOrientation(final Quaternion o) {
        this.orient = o;
    }
    
    public Quaternion getOrientation() {
        return this.orient;
    }
    
    public int getSpawnRadius() {
        return this.spawnRadius;
    }
    
    public void setSpawnRadius(final int radius) {
        this.spawnRadius = radius;
    }
    
    public int getRespawnTime() {
        return this.respawnTime;
    }
    
    public void setRespawnTime(final int milliseconds) {
        this.respawnTime = milliseconds;
    }
    
    public int getNumSpawns() {
        return this.numSpawns;
    }
    
    public void setNumSpawns(final int num) {
        this.numSpawns = num;
    }
    
    public int getCorpseDespawnTime() {
        return this.corpseDespawnTime;
    }
    
    public void setCorpseDespawnTime(final int time) {
        this.corpseDespawnTime = time;
    }
    
    public ObjectFactory getObjectFactory() {
        return this.factory;
    }
    
    public void setObjectFactory(final ObjectFactory factory) {
        this.factory = factory;
    }
    
    public SpawnData getSpawnData() {
        return this.spawnData;
    }
    
    public void setSpawnData(final SpawnData spawnData) {
        this.spawnData = spawnData;
    }
    
    private static void addInstanceContent(final SpawnGenerator spawnGen) {
        synchronized (SpawnGenerator.instanceContent) {
            HashMap<Integer, SpawnGenerator> spawnGenList = SpawnGenerator.instanceContent.get(spawnGen.getInstanceOid());
            if (spawnGenList == null) {
                spawnGenList = new HashMap<Integer, SpawnGenerator>();
                SpawnGenerator.instanceContent.put(spawnGen.getInstanceOid(), spawnGenList);
            }
            spawnGenList.put(spawnGen.getSpawnId(), spawnGen);
        }
        // monitorexit(SpawnGenerator.instanceContent)
    }
    
    public static void cleanupInstance(final OID instanceOid2) {
        final HashMap<Integer, SpawnGenerator> spawnGenList;
        synchronized (SpawnGenerator.instanceContent) {
            spawnGenList = SpawnGenerator.instanceContent.remove(instanceOid2);
        }
        // monitorexit(SpawnGenerator.instanceContent)
        if (spawnGenList != null) {
            for (final SpawnGenerator spawnGen : spawnGenList.values()) {
                spawnGen.deactivate();
            }
        }
    }
    
    public static void removeSpawnGenerator(final OID instanceOid2, final int spawnID) {
        final HashMap<Integer, SpawnGenerator> spawnGenList;
        synchronized (SpawnGenerator.instanceContent) {
            spawnGenList = SpawnGenerator.instanceContent.get(instanceOid2);
            Log.debug("AJ: instanceContent - " + SpawnGenerator.instanceContent);
            Log.debug("AJ: spawnGenList - " + spawnGenList);
        }
        // monitorexit(SpawnGenerator.instanceContent)
        if (spawnGenList != null) {
            final SpawnGenerator sg = spawnGenList.remove(spawnID);
            sg.deactivate();
        }
    }
    
    public static void respawnMatchingMobs(final OID instanceOid2, final int mobID) {
        final HashMap<Integer, SpawnGenerator> spawnGenList;
        synchronized (SpawnGenerator.instanceContent) {
            spawnGenList = SpawnGenerator.instanceContent.get(instanceOid2);
            Log.debug("AJ: instanceContent - " + SpawnGenerator.instanceContent);
            Log.debug("AJ: spawnGenList - " + spawnGenList);
        }
        // monitorexit(SpawnGenerator.instanceContent)
        if (spawnGenList != null) {
            for (final SpawnGenerator sg : spawnGenList.values()) {
                if (sg.getObjectFactory().getTemplateID() == mobID) {
                    sg.deactivate();
                    sg.activate();
                }
            }
        }
    }
    
    protected class CorpseDespawner implements Runnable
    {
        protected ObjectStub obj;
        
        public CorpseDespawner(final ObjectStub obj) {
            this.obj = obj;
        }
        
        @Override
        public void run() {
            Log.debug("DESPAWN: running CorpseDespawner");
            if (SpawnGenerator.this.spawns == null) {
                return;
            }
            Log.debug("DESPAWN: running CorpseDespawner 2");
            SpawnGenerator.this.spawns.remove(this.obj);
            Log.debug("DESPAWN: running CorpseDespawner 3");
            try {
                Log.debug("DESPAWN: running CorpseDespawner 4 with obj: " + this.obj);
                this.obj.despawn();
                Log.debug("DESPAWN: running CorpseDespawner 5");
                ObjectManagerClient.unloadObject(this.obj.getOid());
                Log.debug("DESPAWN: running CorpseDespawner 6");
            }
            catch (AORuntimeException e) {
                Log.exception("SpawnGenerator.CorpseDespawner: exception: ", (Exception)e);
            }
        }
    }
}
