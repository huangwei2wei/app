// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.objects.DisplayContext;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import java.io.Serializable;

public class ResourceGrid implements Serializable
{
    int id;
    String instance;
    Point position;
    float layerHeight;
    String resourceType;
    int count;
    float rotation;
    OID resourceOID;
    private static final long serialVersionUID = 1L;
    
    public ResourceGrid() {
    }
    
    public ResourceGrid(final int id, final Point position, final int resourceCount, final String resourceType, final float rotation) {
        this.id = id;
        this.position = position;
        this.count = resourceCount;
        this.resourceType = resourceType;
        this.rotation = rotation;
    }
    
    public void harvestResource() {
        --this.count;
        if (this.count < 1) {
            WorldManagerClient.despawn(this.resourceOID);
        }
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getInstance() {
        return this.instance;
    }
    
    public void setInstance(final String instance) {
        this.instance = instance;
    }
    
    public Point getPosition() {
        return this.position;
    }
    
    public void setPosition(final Point position) {
        this.position = position;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void setCount(final int count) {
        this.count = count;
    }
    
    public float getLayerHeight() {
        return this.layerHeight;
    }
    
    public void setLayerHeight(final float layerHeight) {
        this.layerHeight = layerHeight;
    }
    
    public String getResourceType() {
        return this.resourceType;
    }
    
    public void setResourceType(final String resourceType) {
        this.resourceType = resourceType;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }
    
    public OID getOID() {
        return this.resourceOID;
    }
    
    public void spawnResource(final OID instanceOID) {
        if (this.count == 0) {
            return;
        }
        final Template markerTemplate = new Template();
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)("Resource" + this.id));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)instanceOID);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)this.position);
        final Quaternion orientation = Quaternion.fromAngleAxisDegrees((double)this.rotation, new AOVector(0.0f, 1.0f, 0.0f));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, (Serializable)orientation);
        final DisplayContext dc = new DisplayContext("RockDungeonSpawns", true);
        dc.addSubmesh(new DisplayContext.Submesh("", ""));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        markerTemplate.put(Namespace.WORLD_MANAGER, "resourceID", (Serializable)this.id);
        markerTemplate.put(Namespace.WORLD_MANAGER, "resourceMesh", (Serializable)"rockgravel1");
        markerTemplate.put(Namespace.WORLD_MANAGER, "resourceCount", (Serializable)this.count);
        final OID objectOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", markerTemplate);
        if (objectOID != null) {
            WorldManagerClient.spawn(objectOID);
            this.resourceOID = objectOID;
            Log.debug("GRID: spawned resource: " + this.resourceType + " at loc: " + this.position + " in instance: " + instanceOID);
        }
    }
}
