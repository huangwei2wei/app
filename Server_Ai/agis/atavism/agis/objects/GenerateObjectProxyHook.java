// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.engine.OID;
import atavism.server.objects.Marker;
import java.util.Iterator;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.math.AOVector;
import atavism.server.engine.BasicWorldNode;
import atavism.server.plugins.InstanceClient;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.Map;
import atavism.server.util.Log;
import atavism.server.plugins.ProxyPlugin;
import atavism.server.objects.Player;
import atavism.server.events.ExtensionMessageEvent;
import atavism.server.objects.ProxyExtensionHook;

public class GenerateObjectProxyHook implements ProxyExtensionHook
{
    public void processExtensionEvent(final ExtensionMessageEvent event, final Player player, final ProxyPlugin proxy) {
        final Map<String, Serializable> props = (Map<String, Serializable>)event.getPropertyMap();
        if (Log.loggingDebug) {
            String propStr = "";
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                propStr = String.valueOf(propStr) + entry.getKey() + "=" + entry.getValue() + " ";
            }
            Log.debug("GenerateObjectProxyHook: " + player + " " + propStr);
        }
        final int templateID = props.get("template");
        final BasicWorldNode playerLoc = WorldManagerClient.getWorldNode(player.getOid());
        final String markerName = props.get("marker");
        BasicWorldNode objectLoc;
        if (markerName != null) {
            final Marker marker = InstanceClient.getMarker(playerLoc.getInstanceOid(), markerName);
            if (marker == null) {
                Log.error("GenerateObjectProxyHook: unknown marker=" + markerName);
                return;
            }
            objectLoc = new BasicWorldNode();
            objectLoc.setInstanceOid(playerLoc.getInstanceOid());
            objectLoc.setLoc(marker.getPoint());
            objectLoc.setOrientation(marker.getOrientation());
            objectLoc.setDir(new AOVector(0.0f, 0.0f, 0.0f));
        }
        else {
            objectLoc = playerLoc;
            objectLoc.setDir(new AOVector(0.0f, 0.0f, 0.0f));
        }
        boolean persistent = false;
        if (props.get("persistent") != null) {
            final Integer persInt = props.get("persistent");
            if (persInt != 0) {
                persistent = true;
            }
        }
        final Template override = new Template();
        override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_INSTANCE, (Serializable)objectLoc.getInstanceOid());
        override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_LOC, (Serializable)objectLoc.getLoc());
        override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ORIENT, (Serializable)objectLoc.getOrientation());
        if (persistent) {
            override.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)persistent);
        }
        final OID oid = ObjectManagerClient.generateObject(templateID, ObjectManagerPlugin.MOB_TEMPLATE, override);
        if (oid == null) {
            Log.error("GenerateObjectProxyHook: generateObject failed templateID=" + templateID);
            return;
        }
        if (Log.loggingDebug) {
            Log.debug("GenerateObjectProxyHook: generateObject success templateID=" + templateID + " oid=" + oid);
        }
        final Integer result = WorldManagerClient.spawn(oid);
        if (result < 0) {
            Log.error("GenerateObjectProxyHook: spawn failed result=" + result + " oid=" + oid);
        }
    }
}
