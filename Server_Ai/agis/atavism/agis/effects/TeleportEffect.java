// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.server.objects.Marker;
import atavism.server.engine.OID;
import atavism.server.engine.BasicWorldNode;
import atavism.agis.objects.CombatInfo;
import atavism.agis.plugins.ArenaClient;
import atavism.server.util.Log;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.InstanceClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.math.Point;
import atavism.agis.core.AgisEffect;

public class TeleportEffect extends AgisEffect
{
    protected Point location;
    protected String markerName;
    protected String instanceName;
    private static final long serialVersionUID = 1L;
    
    public TeleportEffect(final int id, final String name) {
        super(id, name);
        this.location = null;
        this.markerName = null;
        this.instanceName = null;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo target = state.getTarget();
        final BasicWorldNode wnode = WorldManagerClient.getWorldNode(target.getOid());
        OID instanceOid;
        if (this.instanceName.equals("")) {
            instanceOid = wnode.getInstanceOid();
        }
        else {
            instanceOid = InstanceClient.getInstanceOid(this.instanceName);
        }
        if (this.markerName.equals("hearth")) {
            final Point hearthLoc = (Point)EnginePlugin.getObjectProperty(target.getOid(), WorldManagerClient.NAMESPACE, "hearthLoc");
            wnode.setLoc(hearthLoc);
            final String hearthInstance = (String)EnginePlugin.getObjectProperty(target.getOid(), WorldManagerClient.NAMESPACE, "hearthInstance");
            instanceOid = InstanceClient.getInstanceOid(hearthInstance);
            Log.debug("TELEPORT: hearthInstance: " + hearthInstance + " gives instanceOid: " + instanceOid);
        }
        else if (this.markerName.equals("")) {
            wnode.setLoc(this.location);
        }
        else {
            final Marker m = InstanceClient.getMarker(instanceOid, this.markerName);
            final Point loc = m.getPoint();
            wnode.setLoc(loc);
        }
        if (instanceOid.equals((Object)wnode.getInstanceOid())) {
            wnode.setInstanceOid(instanceOid);
            WorldManagerClient.updateWorldNode(target.getOid(), wnode, true);
        }
        else {
            wnode.setInstanceOid(instanceOid);
            InstanceClient.objectInstanceEntry(target.getOid(), wnode, 0);
            WorldManagerClient.updateWorldNode(target.getOid(), wnode, true);
        }
        ArenaClient.removePlayer(target.getOid());
    }
    
    public Point getTeleportLocation() {
        return this.location;
    }
    
    public void setTeleportLocation(final Point loc) {
        this.location = loc;
    }
    
    public String getMarkerName() {
        return this.markerName;
    }
    
    public void setMarkerName(final String markerName) {
        this.markerName = markerName;
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }
}
