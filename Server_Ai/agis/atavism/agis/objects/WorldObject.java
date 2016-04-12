// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.objects.DisplayContext;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import java.io.Serializable;

public class WorldObject implements Serializable, Runnable
{
    int id;
    Point loc;
    Quaternion orientation;
    String model;
    OID instanceOID;
    OID objectOID;
    HashMap<String, Serializable> props;
    boolean active;
    private static final long serialVersionUID = 1L;
    
    public WorldObject() {
    }
    
    public WorldObject(final int id, final Point loc, final OID instanceOID, final String model, final HashMap<String, Serializable> props) {
        this(id, loc, new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), instanceOID, model, props);
    }
    
    public WorldObject(final int id, final Point loc, final Quaternion orientation, final OID instanceOID, final String model, final HashMap<String, Serializable> props) {
        this.id = id;
        this.loc = loc;
        this.orientation = orientation;
        this.instanceOID = instanceOID;
        this.props = props;
        this.spawn();
    }
    
    public void respawn(final int time) {
        Engine.getExecutor().schedule(this, time, TimeUnit.SECONDS);
    }
    
    @Override
    public void run() {
        this.spawn();
    }
    
    protected void spawn() {
        final Template markerTemplate = new Template();
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)("WorldObject" + this.id));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)this.instanceOID);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)this.loc);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, (Serializable)this.orientation);
        final DisplayContext dc = new DisplayContext(this.model, true);
        dc.addSubmesh(new DisplayContext.Submesh("", ""));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        if (this.props != null) {
            for (final String propName : this.props.keySet()) {
                markerTemplate.put(Namespace.WORLD_MANAGER, propName, (Serializable)this.props.get(propName));
            }
        }
        this.objectOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", markerTemplate);
        if (this.objectOID != null) {
            WorldManagerClient.spawn(this.objectOID);
            this.active = true;
        }
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public Point getLoc() {
        return this.loc;
    }
    
    public void setLoc(final Point loc) {
        this.loc = loc;
    }
    
    public Quaternion getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final Quaternion orientation) {
        this.orientation = orientation;
    }
    
    public HashMap<String, Serializable> getProps() {
        return this.props;
    }
    
    public void setProps(final HashMap<String, Serializable> props) {
        this.props = props;
    }
    
    public OID getInstanceOID() {
        return this.instanceOID;
    }
    
    public void setInstanceOID(final OID instanceOID) {
        this.instanceOID = instanceOID;
    }
    
    public OID getObjectOID() {
        return this.objectOID;
    }
    
    public void setObjectOID(final OID objectOID) {
        this.objectOID = objectOID;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public void setModel(final String model) {
        this.model = model;
    }
    
    public boolean getActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
}
