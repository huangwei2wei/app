// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import java.util.Random;
import atavism.server.math.Quaternion;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import atavism.server.objects.DisplayContext;

public class ArenaWeaponObject extends ArenaObject
{
    protected DisplayContext meleeDC;
    protected String meleeObjectType;
    protected DisplayContext rangedDC;
    protected String rangedObjectType;
    private static final long serialVersionUID = 1L;
    
    public ArenaWeaponObject() {
        this.meleeObjectType = "Melee Weapon";
        this.rangedObjectType = "Ranged Weapon";
    }
    
    public ArenaWeaponObject(final int id, final Point loc, final OID instanceOID, final DisplayContext meleeDC, final DisplayContext rangedDC, final HashMap<String, Serializable> props) {
        this(id, loc, new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), instanceOID, meleeDC, rangedDC, props);
    }
    
    public ArenaWeaponObject(final int id, final Point loc, final Quaternion orientation, final OID instanceOID, final DisplayContext meleeDC, final DisplayContext rangedDC, final HashMap<String, Serializable> props) {
        this.meleeObjectType = "Melee Weapon";
        this.rangedObjectType = "Ranged Weapon";
        this.id = id;
        this.loc = loc;
        this.orientation = orientation;
        this.instanceOID = instanceOID;
        this.props = props;
        this.teamToReactTo = -1;
        this.meleeDC = meleeDC;
        this.rangedDC = rangedDC;
        this.spawn();
    }
    
    @Override
    protected void spawn() {
        final Random rand = new Random();
        if (rand.nextBoolean()) {
            this.dc = this.meleeDC;
            this.objectType = this.meleeObjectType;
        }
        else {
            this.dc = this.rangedDC;
            this.objectType = this.rangedObjectType;
        }
        final Template markerTemplate = new Template();
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)(String.valueOf(this.objectType) + this.id));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)this.instanceOID);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)this.loc);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, (Serializable)this.orientation);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)this.dc);
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
}
