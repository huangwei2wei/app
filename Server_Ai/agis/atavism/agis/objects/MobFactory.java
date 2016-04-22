// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;
import java.util.LinkedList;

import atavism.server.engine.Behavior;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import atavism.server.objects.ObjectFactory;
import atavism.server.objects.ObjectStub;
import atavism.server.objects.SpawnData;
import atavism.server.util.Log;

public class MobFactory extends ObjectFactory implements Serializable
{
    private LinkedList<Behavior> behavs;
    private static final long serialVersionUID = 1L;
    
    public MobFactory(final int templateID) {
        super(templateID);
        this.behavs = new LinkedList<Behavior>();
    }
    
    public ObjectStub makeObject(final SpawnData spawnData, final OID instanceOid, final Point loc) {
        final ObjectStub obj = super.makeObject(spawnData, instanceOid, loc);
        Log.debug("MOBFACTORY: makeObject; adding behavs: " + this.behavs);
        for (final Behavior behav : this.behavs) {
            if (!obj.getBehaviors().contains(behav)) {
                obj.addBehavior(behav);
                Log.debug("MOBFACTORY: makeObject; adding behav: " + behav);
            }
        }
        return obj;
    }
    
    public void addBehav(final Behavior behav) {
        this.behavs.add(behav);
    }
    
    public void setBehavs(final LinkedList<Behavior> behavs) {
        this.behavs = behavs;
    }
    
    public LinkedList<Behavior> getBehavs() {
        return this.behavs;
    }
}
