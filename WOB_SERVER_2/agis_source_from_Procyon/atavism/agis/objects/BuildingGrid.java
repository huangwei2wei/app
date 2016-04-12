// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.plugins.ObjectManagerClient;
import atavism.server.objects.DisplayContext;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import java.util.ArrayList;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import java.io.Serializable;

public class BuildingGrid implements Serializable
{
    int id;
    String instance;
    Point position;
    int type;
    OID owner;
    int layerCount;
    float layerHeight;
    ArrayList<String> buildings;
    ArrayList<Float> rotations;
    ArrayList<Integer> blueprints;
    ArrayList<OID> buildingOIDs;
    private static final long serialVersionUID = 1L;
    
    public BuildingGrid() {
        this.buildings = new ArrayList<String>();
        this.rotations = new ArrayList<Float>();
        this.blueprints = new ArrayList<Integer>();
        this.buildingOIDs = new ArrayList<OID>();
    }
    
    public BuildingGrid(final int id, final Point position, final int type, final OID owner, final int layerCount, final ArrayList<String> buildings, final ArrayList<Float> rotations) {
        this.buildings = new ArrayList<String>();
        this.rotations = new ArrayList<Float>();
        this.blueprints = new ArrayList<Integer>();
        this.buildingOIDs = new ArrayList<OID>();
        this.id = id;
        this.position = position;
        this.type = type;
        this.owner = owner;
        this.layerCount = layerCount;
        this.buildings = buildings;
        this.rotations = rotations;
    }
    
    public void updateBuilding(int layer, final String building, final int blueprint, final float rotation) {
        --layer;
        if (building == null || building.isEmpty()) {
            Log.debug("GRID: removing building at layer: " + layer + " with current layers: " + this.buildings.size());
            while (this.buildings.size() > layer) {
                Log.debug("GRID: removing layer: " + layer);
                this.buildings.remove(layer);
                this.blueprints.remove(layer);
                this.rotations.remove(layer);
                Log.debug("GRID: despawning: " + this.buildingOIDs.get(layer));
                WorldManagerClient.despawn((OID)this.buildingOIDs.get(layer));
                this.buildingOIDs.remove(layer);
            }
        }
        else {
            Log.debug("GRID: adding building at layer: " + layer + " with current layers: " + this.buildings.size());
            if (layer >= this.buildings.size()) {
                layer = this.buildings.size();
                this.buildings.add(layer, building);
                this.blueprints.add(layer, blueprint);
                this.rotations.add(layer, rotation);
            }
            else {
                this.buildings.set(layer, building);
                this.blueprints.set(layer, blueprint);
                this.rotations.set(layer, rotation);
            }
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
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public OID getOwner() {
        return this.owner;
    }
    
    public void setOwner(final OID owner) {
        this.owner = owner;
    }
    
    public int getLayerCount() {
        return this.layerCount;
    }
    
    public void setLayerCount(final int layerCount) {
        this.layerCount = layerCount;
    }
    
    public float getLayerHeight() {
        return this.layerHeight;
    }
    
    public void setLayerHeight(final float layerHeight) {
        this.layerHeight = layerHeight;
    }
    
    public ArrayList<String> getBuildings() {
        return this.buildings;
    }
    
    public void setBuildings(final ArrayList<String> buildings) {
        this.buildings = buildings;
    }
    
    public ArrayList<Integer> getBlueprints() {
        return this.blueprints;
    }
    
    public void setBlueprints(final ArrayList<Integer> blueprints) {
        this.blueprints = blueprints;
    }
    
    public ArrayList<Float> getRotations() {
        return this.rotations;
    }
    
    public void setRotations(final ArrayList<Float> rotations) {
        this.rotations = rotations;
    }
    
    public ArrayList<OID> getOIDs() {
        return this.buildingOIDs;
    }
    
    public void spawnBuildings(final OID instanceOID) {
        if (this.buildings == null) {
            return;
        }
        for (int i = 1; i <= this.buildings.size(); ++i) {
            this.spawnBuilding(i, instanceOID);
        }
    }
    
    public void spawnBuilding(int layer, final OID instanceOID) {
        --layer;
        if (this.buildingOIDs.size() > layer && this.buildingOIDs.get(layer) != null) {
            WorldManagerClient.despawn((OID)this.buildingOIDs.get(layer));
        }
        if (this.buildings.size() <= layer || this.buildings.get(layer) == null || this.buildings.get(layer).equals("")) {
            return;
        }
        final Template markerTemplate = new Template();
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)("Building" + this.id));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)instanceOID);
        final Point loc = new Point();
        loc.add(this.position);
        loc.setY(loc.getY() + this.layerHeight * layer);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)loc);
        final Quaternion orientation = Quaternion.fromAngleAxisDegrees((double)this.rotations.get(layer), new AOVector(0.0f, 1.0f, 0.0f));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, (Serializable)orientation);
        final DisplayContext dc = new DisplayContext((String)this.buildings.get(layer), true);
        dc.addSubmesh(new DisplayContext.Submesh("", ""));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        markerTemplate.put(Namespace.WORLD_MANAGER, "blueprint", (Serializable)this.blueprints.get(layer));
        markerTemplate.put(Namespace.WORLD_MANAGER, "buildingID", (Serializable)this.id);
        final OID objectOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", markerTemplate);
        if (objectOID != null) {
            WorldManagerClient.spawn(objectOID);
            this.buildingOIDs.add(layer, objectOID);
            Log.debug("GRID: spawned building: " + this.buildings.get(layer) + " at loc: " + this.position + " in instance: " + instanceOID + " in layer: " + layer);
        }
    }
}
