// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.math.AOVector;
import java.util.ArrayList;
import atavism.server.engine.OID;
import java.io.Serializable;

public class Terravol implements Serializable
{
    String mapName;
    String instance;
    OID instanceOid;
    ArrayList<TerravolAction> changes;
    ArrayList<OID> subscribers;
    private static final long serialVersionUID = 1L;
    
    public Terravol() {
        this.changes = new ArrayList<TerravolAction>();
        this.subscribers = new ArrayList<OID>();
    }
    
    public Terravol(final String mapName, final String instance, final OID instanceOid) {
        this.changes = new ArrayList<TerravolAction>();
        this.subscribers = new ArrayList<OID>();
        this.mapName = mapName;
        this.instance = instance;
        this.instanceOid = instanceOid;
    }
    
    public TerravolAction addChange(final int id, final AOVector position, final AOVector size, final int actionDataType, final int brush, final ArrayList<AOVector> affectedVirtualChunks, final String blockName, final float isoValueToAdd, final boolean force) {
        final TerravolAction change = new TerravolAction(id, position, size, actionDataType, brush, affectedVirtualChunks, blockName, isoValueToAdd, force);
        this.changes.add(change);
        return change;
    }
    
    public void sendAllChangesToPlayer(final OID playerOid) {
        for (final TerravolAction action : this.changes) {
            final WorldManagerClient.TargetedExtensionMessage actionMessage = new WorldManagerClient.TargetedExtensionMessage(playerOid, playerOid);
            actionMessage.setExtensionType("terravol_action");
            actionMessage.setProperty("map_name", (Serializable)this.mapName);
            actionMessage.setProperty("position", (Serializable)action.position);
            actionMessage.setProperty("size", (Serializable)action.size);
            actionMessage.setProperty("actionDataType", (Serializable)action.actionDataType);
            actionMessage.setProperty("brush", (Serializable)action.brush);
            actionMessage.setProperty("blockName", (Serializable)action.blockName);
            actionMessage.setProperty("isoValueToAdd", (Serializable)action.isoValueToAdd);
            actionMessage.setProperty("force", (Serializable)action.force);
            Engine.getAgent().sendBroadcast((Message)actionMessage);
            Log.debug("TERRA: sent change message to: " + playerOid);
        }
    }
    
    public void sendBlockChangeToSubscribers(final TerravolAction action) {
        for (final OID oid : this.subscribers) {
            final WorldManagerClient.TargetedExtensionMessage actionMessage = new WorldManagerClient.TargetedExtensionMessage(oid, oid);
            actionMessage.setExtensionType("terravol_action");
            actionMessage.setProperty("map_name", (Serializable)this.mapName);
            actionMessage.setProperty("position", (Serializable)action.position);
            actionMessage.setProperty("size", (Serializable)action.size);
            actionMessage.setProperty("actionDataType", (Serializable)action.actionDataType);
            actionMessage.setProperty("brush", (Serializable)action.brush);
            actionMessage.setProperty("blockName", (Serializable)action.blockName);
            actionMessage.setProperty("isoValueToAdd", (Serializable)action.isoValueToAdd);
            actionMessage.setProperty("force", (Serializable)action.force);
            Engine.getAgent().sendBroadcast((Message)actionMessage);
            Log.debug("TERRA: sent change message to: " + oid);
        }
    }
    
    public void addSubscriber(final OID subscriber) {
        this.subscribers.add(subscriber);
    }
    
    public void removeSubscriber(final OID subscriber) {
        this.subscribers.remove(subscriber);
    }
    
    public String getMapName() {
        return this.mapName;
    }
    
    public void setMapName(final String mapName) {
        this.mapName = mapName;
    }
    
    public String getInstance() {
        return this.instance;
    }
    
    public void setInstance(final String instance) {
        this.instance = instance;
    }
    
    public OID getOwner() {
        return this.instanceOid;
    }
    
    public void setOwner(final OID instanceOid) {
        this.instanceOid = instanceOid;
    }
    
    public ArrayList<TerravolAction> getChanges() {
        return this.changes;
    }
    
    public void setChanges(final ArrayList<TerravolAction> changes) {
        this.changes = changes;
    }
    
    public ArrayList<OID> getSubscribers() {
        return this.subscribers;
    }
    
    public class TerravolAction
    {
        public int id;
        public AOVector position;
        public AOVector size;
        public int actionDataType;
        public int brush;
        public ArrayList<AOVector> affectedVirtualChunks;
        public String blockName;
        public float isoValueToAdd;
        public boolean force;
        
        public TerravolAction(final int id, final AOVector position, final AOVector size, final int actionDataType, final int brush, final ArrayList<AOVector> affectedVirtualChunks, final String blockName, final float isoValueToAdd, final boolean force) {
            this.id = id;
            this.position = position;
            this.size = size;
            this.actionDataType = actionDataType;
            this.brush = brush;
            this.affectedVirtualChunks = affectedVirtualChunks;
            this.blockName = blockName;
            this.isoValueToAdd = isoValueToAdd;
            this.force = force;
        }
    }
}
