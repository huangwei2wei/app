// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import atavism.msgsys.TargetMessage;
import atavism.server.engine.TerrainConfig;
import atavism.server.objects.OceanData;
import com.app.server.atavism.server.objects.LightData;
import com.app.server.atavism.server.objects.Color;
import atavism.server.objects.Fog;
import atavism.server.objects.Region;
import atavism.server.engine.BasicWorldNode;
import atavism.server.util.Log;
import java.util.Map;
import atavism.server.objects.Marker;
import atavism.server.math.Point;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import atavism.msgsys.GenericMessage;
import atavism.msgsys.SubjectMessage;
import atavism.server.engine.OID;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.objects.Template;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;

public class InstanceClient
{
    public static final int FLAG_OID = 1;
    public static final int FLAG_NAME = 2;
    public static final int FLAG_TEMPLATE_NAME = 4;
    public static final int FLAG_SKYBOX = 8;
    public static final int FLAG_FOG = 16;
    public static final int FLAG_AMBIENT_LIGHT = 32;
    public static final int FLAG_DIR_LIGHT = 64;
    public static final int FLAG_OCEAN = 128;
    public static final int FLAG_WORLDFILE = 256;
    public static final int FLAG_TERRAIN = 512;
    public static final int FLAG_REGION_CONFIG = 1024;
    public static final int FLAG_PLAYER_POPULATION = 2048;
    public static final int FLAG_POPULATION_LIMIT = 4096;
    public static final int FLAG_MULTIPLE = 8192;
    public static final int FLAG_ALL_INFO = -8193;
    public static final long REGION_BOUNDARY = 1L;
    public static final long REGION_PROPERTIES = 2L;
    public static final long REGION_ALL = 3L;
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR_UNKNOWN_OBJECT = -1;
    public static final int RESULT_ERROR_INTERNAL = -2;
    public static final int RESULT_ERROR_NO_WORLD_MANAGER = -3;
    public static final int RESULT_ERROR_RETRY = -4;
    public static Namespace NAMESPACE;
    public static final String TEMPL_INSTANCE_TEMPLATE_NAME = "templateName";
    public static final String TEMPL_WORLD_FILE_NAME = "worldFileName";
    public static final String TEMPL_INIT_SCRIPT_FILE_NAME = "initScriptFileName";
    public static final String TEMPL_LOAD_SCRIPT_FILE_NAME = "loadScriptFileName";
    public static final String TEMPL_TERRAIN_CONFIG_FILE = "terrainConfigFile";
    public static final String TEMPL_INSTANCE_NAME = "name";
    public static final String TEMPL_LOADER_OVERRIDE_NAME = "loaderOverrideName";
    public static final String TEMPL_UNIQUE_NAME = "uniqueName";
    public static final String TEMPL_COLLISION_POINTS = "collisionPoints";
    public static final String TEMPL_ASSET_INFO = "assetInfo";
    public static final String TEMPL_WORLD_COLLECTION_FILES = "worldCollectionFiles";
    public static final String TEMPL_WORLD_COLLECTION_DATABASE_KEYS = "worldCollectionDatabaseKeys";
    public static final String TEMPL_POPULATION_LIMIT = "populationLimit";
    public static final String COLLECTION_STATIC_OBJECTS = "static_objects";
    public static final String COLLECTION_MARKER_OBJECTS = "marker_objects";
    public static final MessageType MSG_TYPE_REGISTER_INSTANCE_TEMPLATE;
    public static final MessageType MSG_TYPE_CREATE_INSTANCE;
    public static final MessageType MSG_TYPE_GET_INSTANCE_INFO;
    public static final MessageType MSG_TYPE_GET_MARKER;
    public static final MessageType MSG_TYPE_GET_REGION;
    public static final MessageType MSG_TYPE_LOAD_INSTANCE;
    public static final MessageType MSG_TYPE_UNLOAD_INSTANCE;
    public static final MessageType MSG_TYPE_DELETE_INSTANCE;
    public static final MessageType MSG_TYPE_LOAD_INSTANCE_CONTENT;
    public static final MessageType MSG_TYPE_INSTANCE_UNLOADED;
    public static final MessageType MSG_TYPE_INSTANCE_DELETED;
    public static final MessageType MSG_TYPE_INSTANCE_LOADED;
    public static MessageType MSG_TYPE_INSTANCE_ENTRY_REQ;
    public static MessageType MSG_TYPE_GET_ENTITY_OIDS;
    public static MessageType MSG_TYPE_LOAD_INSTANCE_BY_NAME;
    public static MessageType MSG_TYPE_GET_NAVMESH_PATH;
    
    public static boolean registerInstanceTemplate(final Template template) {
        final RegisterInstanceTemplateMessage message = new RegisterInstanceTemplateMessage(template);
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    public static OID createInstance(final String templateName, final Template override) {
        final CreateInstanceMessage message = new CreateInstanceMessage(templateName, override);
        return Engine.getAgent().sendRPCReturnOID(message);
    }
    
    public static int loadInstance(final OID instanceOid) {
        final SubjectMessage message = new SubjectMessage(InstanceClient.MSG_TYPE_LOAD_INSTANCE, instanceOid);
        return Engine.getAgent().sendRPCReturnInt(message);
    }
    
    public static boolean unloadInstance(final OID instanceOid) {
        final SubjectMessage message = new SubjectMessage(InstanceClient.MSG_TYPE_UNLOAD_INSTANCE, instanceOid);
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    public static boolean deleteInstance(final OID instanceOid) {
        final SubjectMessage message = new SubjectMessage(InstanceClient.MSG_TYPE_DELETE_INSTANCE, instanceOid);
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    public static OID loadInstance(final String instanceName) {
        final GenericMessage message = new GenericMessage(InstanceClient.MSG_TYPE_LOAD_INSTANCE_BY_NAME);
        message.setProperty("instanceName", instanceName);
        final OID instanceOid = (OID)Engine.getAgent().sendRPCReturnObject(message);
        return instanceOid;
    }
    
    public static OID getInstanceOid(final String instanceName) {
        final GetInstanceInfoMessage message = new GetInstanceInfoMessage(instanceName, 1);
        final InstanceInfo info = (InstanceInfo)Engine.getAgent().sendRPCReturnObject(message);
        if (info != null) {
            return info.oid;
        }
        return null;
    }
    
    public static List<OID> getInstanceOids(final String instanceName) {
        final GetInstanceInfoMessage message = new GetInstanceInfoMessage(instanceName, 8193);
        final List<InstanceInfo> info = (List<InstanceInfo>)Engine.getAgent().sendRPCReturnObject(message);
        if (info != null) {
            final List<OID> oids = new ArrayList<OID>(info.size());
            for (final InstanceInfo ii : info) {
                oids.add(ii.oid);
            }
            return oids;
        }
        return null;
    }
    
    public static List<OID> getMatchingEntityOids(final String entityName) {
        final GetMatchingEntityOidsMessage message = new GetMatchingEntityOidsMessage(entityName);
        final List<OID> oids = (List<OID>)Engine.getAgent().sendRPCReturnObject(message);
        return oids;
    }
    
    public static List<Point> getNavMeshPath(final OID mobOid, final Point startLoc, final Point endLoc) {
        final GetNavMeshPathMessage message = new GetNavMeshPathMessage(mobOid, startLoc, endLoc);
        final List<Point> points = (List<Point>)Engine.getAgent().sendRPCReturnObject(message);
        return points;
    }
    
    public static InstanceInfo getInstanceInfo(final OID instanceOid, final int flags) {
        final GetInstanceInfoMessage message = new GetInstanceInfoMessage(instanceOid, flags);
        return (InstanceInfo)Engine.getAgent().sendRPCReturnObject(message);
    }
    
    public static List<InstanceInfo> getInstanceInfoByName(final String instanceName, final int flags) {
        final GetInstanceInfoMessage message = new GetInstanceInfoMessage(instanceName, flags | 0x2000);
        return (List<InstanceInfo>)Engine.getAgent().sendRPCReturnObject(message);
    }
    
    public static Marker getMarker(final OID defaultInstanceOid, final String markerName) {
        final List<OID> oidList = ObjectManagerClient.getMatchingObjects(defaultInstanceOid, markerName, Marker.OBJECT_TYPE, null);
        if (oidList.size() == 1) {
            final OID oid = oidList.get(0);
            final BasicWorldNode wNode = WorldManagerClient.getWorldNode(oid);
            return new Marker(wNode.getLoc(), wNode.getOrientation());
        }
        if (oidList.size() == 0) {
            Log.warn("No matching markers found for marker name: " + markerName);
        }
        else {
            Log.warn("Multiple (" + oidList.size() + ") matching markers found for marker name: " + markerName);
        }
        return null;
    }
    
    public static Point getMarkerPoint(final OID instanceOid, final String markerName) {
        final Marker marker = getMarker(instanceOid, markerName);
        if (marker != null) {
            return marker.getPoint();
        }
        return null;
    }
    
    public static Region getRegion(final OID instanceOid, final String regionName, final long flags) {
        final GetRegionMessage message = new GetRegionMessage(instanceOid, regionName, flags);
        return (Region)Engine.getAgent().sendRPCReturnObject(message);
    }
    
    public static boolean objectInstanceEntry(final OID oid, final BasicWorldNode instanceLoc, final int flags, final BasicWorldNode restoreWnode) {
        final InstanceEntryReqMessage message = new InstanceEntryReqMessage(oid, instanceLoc, flags, restoreWnode);
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    public static boolean objectInstanceEntry(final OID oid, final BasicWorldNode instanceLoc, final int flags) {
        return objectInstanceEntry(oid, instanceLoc, flags, null);
    }
    
    public static boolean objectInstanceEntry(final OID oid, final String instanceName, final BasicWorldNode instanceLoc, final int flags) {
        final OID instanceOid = getInstanceOid(instanceName);
        if (instanceOid == null) {
            Log.error("objectInstanceEntry: unknown instance name=" + instanceName + " for oid=" + oid);
            return false;
        }
        instanceLoc.setInstanceOid(instanceOid);
        final InstanceEntryReqMessage message = new InstanceEntryReqMessage(oid, instanceLoc, flags);
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    static {
        InstanceClient.NAMESPACE = null;
        MSG_TYPE_REGISTER_INSTANCE_TEMPLATE = MessageType.intern("ao.REGISTER_INSTANCE_TEMPLATE");
        MSG_TYPE_CREATE_INSTANCE = MessageType.intern("ao.CREATE_INSTANCE");
        MSG_TYPE_GET_INSTANCE_INFO = MessageType.intern("ao.GET_INSTANCE_INFO");
        MSG_TYPE_GET_MARKER = MessageType.intern("ao.GET_MARKER");
        MSG_TYPE_GET_REGION = MessageType.intern("ao.GET_REGION");
        MSG_TYPE_LOAD_INSTANCE = MessageType.intern("ao.LOAD_INSTANCE");
        MSG_TYPE_UNLOAD_INSTANCE = MessageType.intern("ao.UNLOAD_INSTANCE");
        MSG_TYPE_DELETE_INSTANCE = MessageType.intern("ao.DELETE_INSTANCE");
        MSG_TYPE_LOAD_INSTANCE_CONTENT = MessageType.intern("ao.LOAD_INSTANCE_CONTENT");
        MSG_TYPE_INSTANCE_UNLOADED = MessageType.intern("ao.INSTANCE_UNLOADED");
        MSG_TYPE_INSTANCE_DELETED = MessageType.intern("ao.INSTANCE_DELETED");
        MSG_TYPE_INSTANCE_LOADED = MessageType.intern("ao.INSTANCE_LOADED");
        InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ = MessageType.intern("ao.INSTANCE_ENTRY_REQ");
        InstanceClient.MSG_TYPE_GET_ENTITY_OIDS = MessageType.intern("ao.GET_ENTITY_OIDS");
        InstanceClient.MSG_TYPE_LOAD_INSTANCE_BY_NAME = MessageType.intern("ao.LOAD_INSTANCE_BY_NAME");
        InstanceClient.MSG_TYPE_GET_NAVMESH_PATH = MessageType.intern("ao.GET_NAVMESH_PATH");
    }
    
    public static class RegisterInstanceTemplateMessage extends Message
    {
        Template template;
        private static final long serialVersionUID = 1L;
        
        public RegisterInstanceTemplateMessage() {
            super(InstanceClient.MSG_TYPE_REGISTER_INSTANCE_TEMPLATE);
        }
        
        public RegisterInstanceTemplateMessage(final Template template) {
            super(InstanceClient.MSG_TYPE_REGISTER_INSTANCE_TEMPLATE);
            this.template = template;
        }
        
        public Template getTemplate() {
            return this.template;
        }
        
        public void setTemplate(final Template template) {
            this.template = template;
        }
    }
    
    public static class CreateInstanceMessage extends Message
    {
        private String templateName;
        private Template overrideTemplate;
        private static final long serialVersionUID = 1L;
        
        public CreateInstanceMessage() {
            super(InstanceClient.MSG_TYPE_CREATE_INSTANCE);
        }
        
        public CreateInstanceMessage(final String templateName, final Template override) {
            super(InstanceClient.MSG_TYPE_CREATE_INSTANCE);
            this.setTemplateName(templateName);
            this.setOverrideTemplate(override);
        }
        
        public String getTemplateName() {
            return this.templateName;
        }
        
        public void setTemplateName(final String templateName) {
            this.templateName = templateName;
        }
        
        public Template getOverrideTemplate() {
            return this.overrideTemplate;
        }
        
        public void setOverrideTemplate(final Template override) {
            this.overrideTemplate = override;
        }
    }
    
    public static class GetInstanceInfoMessage extends Message
    {
        private OID instanceOid;
        private String instanceName;
        private int flags;
        private static final long serialVersionUID = 1L;
        
        public GetInstanceInfoMessage() {
            super(InstanceClient.MSG_TYPE_GET_INSTANCE_INFO);
        }
        
        public GetInstanceInfoMessage(final String instanceName, final int flags) {
            super(InstanceClient.MSG_TYPE_GET_INSTANCE_INFO);
            this.setInstanceName(instanceName);
            this.setFlags(flags);
        }
        
        public GetInstanceInfoMessage(final OID instanceOid, final int flags) {
            super(InstanceClient.MSG_TYPE_GET_INSTANCE_INFO);
            this.setInstanceOid(instanceOid);
            this.setFlags(flags);
        }
        
        public OID getInstanceOid() {
            return this.instanceOid;
        }
        
        public void setInstanceOid(final OID oid) {
            this.instanceOid = oid;
        }
        
        public String getInstanceName() {
            return this.instanceName;
        }
        
        public void setInstanceName(final String name) {
            this.instanceName = name;
        }
        
        public int getFlags() {
            return this.flags;
        }
        
        public void setFlags(final int flags) {
            this.flags = flags;
        }
    }
    
    public static class InstanceInfo
    {
        public OID oid;
        public boolean loaded;
        public String name;
        public String templateName;
        public String skybox;
        public Fog fog;
        public Color ambientLight;
        public LightData dirLight;
        public OceanData ocean;
        public String worldFile;
        public TerrainConfig terrainConfig;
        public List<String> regionConfig;
        public int playerPopulation;
        public int populationLimit;
    }
    
    public static class GetRegionMessage extends Message
    {
        private OID instanceOid;
        private String regionName;
        private long flags;
        private static final long serialVersionUID = 1L;
        
        public GetRegionMessage() {
            this.setMsgType(InstanceClient.MSG_TYPE_GET_REGION);
        }
        
        public GetRegionMessage(final OID instanceOid, final String name, final long flags) {
            this.setMsgType(InstanceClient.MSG_TYPE_GET_REGION);
            this.setInstanceOid(instanceOid);
            this.setRegionName(name);
            this.setFlags(flags);
        }
        
        public OID getInstanceOid() {
            return this.instanceOid;
        }
        
        public void setInstanceOid(final OID oid) {
            this.instanceOid = oid;
        }
        
        public void setRegionName(final String name) {
            this.regionName = name;
        }
        
        public String getRegionName() {
            return this.regionName;
        }
        
        public long getFlags() {
            return this.flags;
        }
        
        public void setFlags(final long flags) {
            this.flags = flags;
        }
    }
    
    public static class InstanceEntryReqMessage extends TargetMessage
    {
        public static final int FLAG_NONE = 0;
        public static final int FLAG_PUSH = 1;
        public static final int FLAG_POP = 2;
        private BasicWorldNode instanceLoc;
        private int flags;
        private BasicWorldNode restoreLoc;
        private transient Object processingState;
        private boolean manifestChecked;
        private static final long serialVersionUID = 1L;
        
        public InstanceEntryReqMessage() {
            this.manifestChecked = false;
        }
        
        public InstanceEntryReqMessage(final OID oid) {
            super(InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ, oid);
            this.manifestChecked = false;
        }
        
        public InstanceEntryReqMessage(final OID oid, final BasicWorldNode instanceLoc) {
            super(InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ, oid);
            this.manifestChecked = false;
            this.setWorldNode(instanceLoc);
        }
        
        public InstanceEntryReqMessage(final OID oid, final BasicWorldNode instanceLoc, final int flags) {
            super(InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ, oid);
            this.manifestChecked = false;
            this.setWorldNode(instanceLoc);
            this.setFlags(flags);
        }
        
        public InstanceEntryReqMessage(final OID oid, final BasicWorldNode instanceLoc, final int flags, final BasicWorldNode restoreLoc) {
            super(InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ, oid);
            this.manifestChecked = false;
            this.setWorldNode(instanceLoc);
            this.setFlags(flags);
            this.setRestoreNode(restoreLoc);
        }
        
        public InstanceEntryReqMessage(final OID oid, final int flags) {
            super(InstanceClient.MSG_TYPE_INSTANCE_ENTRY_REQ, oid);
            this.manifestChecked = false;
            this.setFlags(flags);
        }
        
        public BasicWorldNode getWorldNode() {
            return this.instanceLoc;
        }
        
        public void setWorldNode(final BasicWorldNode instanceLoc) {
            this.instanceLoc = instanceLoc;
        }
        
        public int getFlags() {
            return this.flags;
        }
        
        public void setFlags(final int flags) {
            this.flags = flags;
        }
        
        public BasicWorldNode getRestoreNode() {
            return this.restoreLoc;
        }
        
        public void setRestoreNode(final BasicWorldNode restoreLoc) {
            this.restoreLoc = restoreLoc;
        }
        
        public Object getProcessingState() {
            return this.processingState;
        }
        
        public void setProcessingState(final Object state) {
            this.processingState = state;
        }
        
        public void setManifestChecked(final boolean checked) {
            this.manifestChecked = checked;
        }
        
        public boolean getManifestChecked() {
            return this.manifestChecked;
        }
    }
    
    public static class GetMatchingEntityOidsMessage extends Message
    {
        private String entityName;
        private static final long serialVersionUID = 1L;
        
        public GetMatchingEntityOidsMessage(final String name) {
            super(InstanceClient.MSG_TYPE_GET_ENTITY_OIDS);
            this.entityName = null;
            this.entityName = name;
        }
        
        public String getEntityName() {
            return this.entityName;
        }
    }
    
    public static class GetNavMeshPathMessage extends Message
    {
        Point startLoc;
        Point endLoc;
        OID mobOid;
        private static final long serialVersionUID = 1L;
        
        public GetNavMeshPathMessage() {
            super(InstanceClient.MSG_TYPE_GET_NAVMESH_PATH);
        }
        
        public GetNavMeshPathMessage(final OID mobOid, final Point startLoc, final Point endLoc) {
            super(InstanceClient.MSG_TYPE_GET_NAVMESH_PATH);
            this.startLoc = startLoc;
            this.endLoc = endLoc;
            this.mobOid = mobOid;
        }
        
        public Point getStartLoc() {
            return this.startLoc;
        }
        
        public void setStartLoc(final Point startLoc) {
            this.startLoc = startLoc;
        }
        
        public Point getEndLoc() {
            return this.endLoc;
        }
        
        public void setEndLoc(final Point endLoc) {
            this.endLoc = endLoc;
        }
        
        public OID getMobOid() {
            return this.mobOid;
        }
        
        public void setMobOid(final OID mobOid) {
            this.mobOid = mobOid;
        }
    }
}
