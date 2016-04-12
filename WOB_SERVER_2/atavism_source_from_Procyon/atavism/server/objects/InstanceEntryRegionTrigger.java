// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.msgsys.BooleanResponseMessage;
import atavism.msgsys.ResponseMessage;
import atavism.server.engine.OID;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.math.AOVector;
import atavism.server.engine.BasicWorldNode;
import atavism.server.plugins.InstanceClient;
import atavism.server.util.Log;
import atavism.msgsys.ResponseCallback;

public class InstanceEntryRegionTrigger implements RegionTrigger, ResponseCallback
{
    @Override
    public void enter(final AOObject obj, final Region region) {
        if (!obj.getType().isPlayer()) {
            return;
        }
        final String instanceName = (String)region.getProperty("instanceName");
        if (instanceName == null) {
            Log.error("InstanceEntryRegionTrigger: missing instanceName property on region " + region);
            return;
        }
        final String markerName = (String)region.getProperty("locMarker");
        if (markerName == null) {
            Log.error("InstanceEntryRegionTrigger: missing locMarker property on region " + region);
            return;
        }
        final OID instanceOid = InstanceClient.getInstanceOid(instanceName);
        if (instanceOid == null) {
            Log.error("InstanceEntryRegionTrigger: unknown instanceName=" + instanceName);
            return;
        }
        final Marker marker = InstanceClient.getMarker(instanceOid, markerName);
        if (marker == null) {
            Log.error("Instance entry event: unknown locMarker=" + markerName);
            return;
        }
        final BasicWorldNode wnode = new BasicWorldNode();
        wnode.setInstanceOid(instanceOid);
        wnode.setLoc(marker.getPoint());
        wnode.setDir(new AOVector(0.0f, 0.0f, 0.0f));
        if (marker.getOrientation() != null) {
            wnode.setOrientation(marker.getOrientation());
        }
        final InstanceClient.InstanceEntryReqMessage message = new InstanceClient.InstanceEntryReqMessage(obj.getOid(), wnode);
        Engine.getAgent().sendRPC(message, this);
    }
    
    @Override
    public void leave(final AOObject obj, final Region region) {
    }
    
    @Override
    public void handleResponse(final ResponseMessage response) {
        if (Log.loggingDebug) {
            Log.debug("InstanceEntryRegionTrigger: instance entry result=" + ((BooleanResponseMessage)response).getBooleanVal());
        }
    }
}
