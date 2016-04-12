// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import java.util.HashMap;
import atavism.server.objects.ObjectTypes;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class BuildingResourceAcquireHook implements AcquireHook
{
    protected int resourceID;
    private static final long serialVersionUID = 1L;
    
    public BuildingResourceAcquireHook() {
    }
    
    public BuildingResourceAcquireHook(final int resourceID) {
        this.setResourceID(resourceID);
    }
    
    public void setResourceID(final int resourceID) {
        if (resourceID == -1) {
            throw new RuntimeException("BuildingResourceAcquireHook.setResource: bad resource");
        }
        this.resourceID = resourceID;
    }
    
    public int getResourceID() {
        return this.resourceID;
    }
    
    @Override
    public boolean acquired(final OID activatorOid, final AgisItem item) {
        if (Log.loggingDebug) {
            Log.debug("BuildingResourceAcquireHook.activate: activator=" + activatorOid + " item=" + item + " resource=" + this.resourceID);
        }
        if (WorldManagerClient.getObjectInfo(activatorOid).objType != ObjectTypes.player) {
            return false;
        }
        int resourceAmount = item.getStackSize();
        final String resourceKey = new StringBuilder().append(this.resourceID).toString();
        HashMap<String, Integer> buildingResources = (HashMap<String, Integer>)EnginePlugin.getObjectProperty(activatorOid, WorldManagerClient.NAMESPACE, "buildingResources");
        if (buildingResources == null) {
            buildingResources = new HashMap<String, Integer>();
        }
        if (buildingResources.containsKey(resourceKey)) {
            resourceAmount += buildingResources.get(resourceKey);
        }
        buildingResources.put(resourceKey, resourceAmount);
        EnginePlugin.setObjectProperty(activatorOid, WorldManagerClient.NAMESPACE, "buildingResources", (Serializable)buildingResources);
        sendBuildingResources(activatorOid, buildingResources);
        return true;
    }
    
    public static void sendBuildingResources(final OID oid, final HashMap<String, Integer> resources) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "buildingResources");
        int numResources = 0;
        for (final String resourceID : resources.keySet()) {
            Log.debug("RESOURCE: got currency to send: " + resourceID);
            props.put("resource" + numResources + "ID", resourceID);
            props.put("resource" + numResources + "Count", resources.get(resourceID));
            ++numResources;
        }
        props.put("numResources", numResources);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("RESOURCES: sending down building resources message to: " + oid + " with props: " + props);
    }
    
    @Override
    public String toString() {
        return "BuildingResourceAcquireHook=" + this.resourceID;
    }
}
