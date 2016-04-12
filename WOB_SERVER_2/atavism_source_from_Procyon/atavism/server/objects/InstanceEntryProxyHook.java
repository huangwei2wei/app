// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.util.Iterator;
import atavism.server.math.AOVector;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.server.engine.BasicWorldNode;
import atavism.server.plugins.InstanceClient;
import java.io.Serializable;
import java.util.Map;
import atavism.server.util.Log;
import atavism.server.plugins.ProxyPlugin;
import atavism.server.events.ExtensionMessageEvent;

public class InstanceEntryProxyHook implements ProxyExtensionHook
{
    @Override
    public void processExtensionEvent(final ExtensionMessageEvent event, final Player player, final ProxyPlugin proxy) {
        final Map<String, Serializable> props = event.getPropertyMap();
        int flags = 0;
        OID instanceOid = null;
        if (Log.loggingDebug) {
            String propStr = "";
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                propStr = propStr + entry.getKey() + "=" + entry.getValue() + " ";
            }
            Log.debug("processInstanceEntryEvent: " + propStr);
        }
        final String flagStr = props.get("flags");
        if (flagStr != null) {
            if (flagStr.equals("push")) {
                flags |= 0x1;
            }
            else if (flagStr.equals("pop")) {
                flags |= 0x2;
            }
        }
        if ((flags & 0x2) != 0x0) {
            InstanceClient.objectInstanceEntry(player.getOid(), null, flags);
            return;
        }
        final String instanceName = props.get("instanceName");
        if (instanceName == null) {
            instanceOid = props.get("instanceOid");
            if (instanceOid == null) {
                Log.error("Instance entry event: missing instanceName and instanceOid");
                return;
            }
        }
        else {
            instanceOid = InstanceClient.getInstanceOid(instanceName);
            if (instanceOid == null) {
                Log.error("Instance entry event: unknown instanceName=" + instanceName);
                return;
            }
        }
        Marker marker = null;
        final String markerName = props.get("locMarker");
        if (markerName == null) {
            marker = new Marker();
            marker.setPoint(props.get("locPoint"));
            if (marker.getPoint() == null) {
                Log.error("Instance entry event: missing locMarker and locPoint");
                return;
            }
        }
        else {
            marker = InstanceClient.getMarker(instanceOid, markerName);
            if (marker == null) {
                Log.error("Instance entry event: unknown marker=" + markerName);
                return;
            }
        }
        final Quaternion orient = props.get("orientation");
        if (orient != null && marker != null) {
            marker.setOrientation(orient);
        }
        Marker restoreMarker = null;
        OID currentInstanceOid = null;
        if ((flags & 0x1) != 0x0) {
            final String restoreMarkerName = props.get("restoreMarker");
            if (restoreMarkerName == null) {
                restoreMarker = new Marker();
                restoreMarker.setPoint(props.get("restorePoint"));
                if (restoreMarker.getPoint() == null) {
                    restoreMarker = null;
                }
                else {
                    final BasicWorldNode currentLoc = WorldManagerClient.getWorldNode(player.getOid());
                    currentInstanceOid = currentLoc.getInstanceOid();
                }
            }
            else {
                final BasicWorldNode currentLoc = WorldManagerClient.getWorldNode(player.getOid());
                currentInstanceOid = currentLoc.getInstanceOid();
                restoreMarker = InstanceClient.getMarker(currentInstanceOid, restoreMarkerName);
                if (restoreMarker == null) {
                    Log.error("Instance entry event: unknown restore marker=" + restoreMarkerName);
                    return;
                }
            }
        }
        else if (props.get("restoreMarker") != null || props.get("restorePoint") != null) {
            Log.warn("processInstanceEntryEvent: ignoring restore marker because flag push is not set");
        }
        final Quaternion restoreOrient = props.get("restoreOrientation");
        if (restoreOrient != null && restoreMarker != null) {
            restoreMarker.setOrientation(restoreOrient);
        }
        BasicWorldNode wnode = null;
        if (marker != null) {
            wnode = new BasicWorldNode();
            wnode.setInstanceOid(instanceOid);
            wnode.setLoc(marker.getPoint());
            wnode.setDir(new AOVector(0.0f, 0.0f, 0.0f));
            if (marker.getOrientation() != null) {
                wnode.setOrientation(marker.getOrientation());
            }
        }
        BasicWorldNode restoreWnode = null;
        if (restoreMarker != null) {
            restoreWnode = new BasicWorldNode();
            restoreWnode.setInstanceOid(currentInstanceOid);
            restoreWnode.setLoc(restoreMarker.getPoint());
            restoreWnode.setDir(new AOVector(0.0f, 0.0f, 0.0f));
            if (restoreMarker.getOrientation() != null) {
                restoreWnode.setOrientation(restoreMarker.getOrientation());
            }
        }
        InstanceClient.objectInstanceEntry(player.getOid(), wnode, flags, restoreWnode);
    }
}
