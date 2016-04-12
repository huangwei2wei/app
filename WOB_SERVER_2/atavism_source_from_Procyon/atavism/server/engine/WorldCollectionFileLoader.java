// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.util.AORuntimeException;
import org.w3c.dom.NamedNodeMap;
import java.util.HashMap;
import atavism.server.objects.SpawnData;
import atavism.msgsys.Message;
import atavism.server.objects.FogRegionConfig;
import atavism.server.objects.SoundRegionConfig;
import atavism.server.objects.Region;
import atavism.server.objects.Boundary;
import atavism.server.objects.SoundData;
import atavism.server.pathing.PathArc;
import java.util.Map;
import atavism.server.pathing.PathData;
import atavism.server.pathing.PathPolygon;
import atavism.server.pathing.PathObject;
import java.util.LinkedList;
import atavism.server.objects.DisplayContext;
import atavism.server.math.Quaternion;
import atavism.server.objects.Color;
import atavism.server.objects.Road;
import java.util.Iterator;
import java.util.List;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.objects.Template;
import atavism.server.objects.TerrainDecalData;
import atavism.server.objects.RegionConfig;
import atavism.server.objects.LightData;
import atavism.server.util.Log;
import org.w3c.dom.Node;
import atavism.server.util.XMLHelper;
import atavism.server.objects.Instance;

public class WorldCollectionFileLoader extends WorldFileLoader implements WorldCollectionLoader
{
    private static int DEFAULT_SOUND_PERCEPTION_RADIUS;
    
    public WorldCollectionFileLoader(final String worldCollectionFileName, final WorldLoaderOverride override) {
        super(worldCollectionFileName, override);
    }
    
    @Override
    public boolean generate(final Instance instance) {
        final Node worldObjColNode = XMLHelper.getMatchingChild(this.worldDoc, "WorldObjectCollection");
        if (worldObjColNode == null) {
            Log.error("No <WorldObjectCollection>");
            return false;
        }
        final String version = XMLHelper.getAttribute(worldObjColNode, "Version");
        if (version == null || (!version.equals("2") && !version.equals("2.0"))) {
            Log.error("Unsupported version number in file " + this.worldFileName + ": " + version);
            return false;
        }
        Log.debug("World collection '" + this.worldFileName + "' version: " + version);
        final List<Node> pointLightNodes = XMLHelper.getMatchingChildren(worldObjColNode, "PointLight");
        for (final Node pointLightNode : pointLightNodes) {
            this.processPointLight(instance, pointLightNode);
        }
        final List<Node> boundaryNodes = XMLHelper.getMatchingChildren(worldObjColNode, "Boundary");
        for (final Node boundaryNode : boundaryNodes) {
            this.processBoundary(instance, boundaryNode);
        }
        final List<Node> roadNodes = XMLHelper.getMatchingChildren(worldObjColNode, "Road");
        if (Log.loggingDebug) {
            Log.debug("Road count=" + roadNodes.size());
        }
        for (final Node roadNode : roadNodes) {
            final Road road = processRoad(roadNode);
            instance.getRoadConfig().addRoad(road);
            if (Log.loggingDebug) {
                Log.debug("Road: " + road + ", config=" + instance.getRoadConfig());
            }
        }
        final LightData globalDirLight = instance.getGlobalDirectionalLight();
        if (Log.loggingDebug) {
            Log.debug("Global dir light: " + globalDirLight);
        }
        if (globalDirLight != null) {
            final RegionConfig lightRegionConfig = new RegionConfig(LightData.DirLightRegionType);
            lightRegionConfig.setProperty("orient", globalDirLight.getOrientation());
            lightRegionConfig.setProperty("specular", globalDirLight.getSpecular());
            lightRegionConfig.setProperty("diffuse", globalDirLight.getDiffuse());
            lightRegionConfig.setProperty("name", "dirLight_GLOBAL");
            instance.getGlobalRegion().addConfig(lightRegionConfig);
        }
        final Color globalAmbientLight = instance.getGlobalAmbientLight();
        if (Log.loggingDebug) {
            Log.debug("Global ambient light: " + globalAmbientLight);
        }
        if (globalAmbientLight != null) {
            final RegionConfig ambientConfig = new RegionConfig(LightData.AmbientLightRegionType);
            ambientConfig.setProperty("color", globalAmbientLight);
            instance.getGlobalRegion().addConfig(ambientConfig);
        }
        final List<Node> markerNodes = XMLHelper.getMatchingChildren(worldObjColNode, "Waypoint");
        for (final Node markerNode : markerNodes) {
            this.processMarker(instance, markerNode);
        }
        final List<Node> terrainDecals = XMLHelper.getMatchingChildren(worldObjColNode, "TerrainDecal");
        for (final Node terrainDecal : terrainDecals) {
            final String decalName = XMLHelper.getAttribute(terrainDecal, "Name");
            final String imageName = XMLHelper.getAttribute(terrainDecal, "ImageName");
            final int posX = (int)Math.round(Double.parseDouble(XMLHelper.getAttribute(terrainDecal, "PositionX")));
            final int posZ = (int)Math.round(Double.parseDouble(XMLHelper.getAttribute(terrainDecal, "PositionZ")));
            final float sizeX = Float.parseFloat(XMLHelper.getAttribute(terrainDecal, "SizeX"));
            final float sizeZ = Float.parseFloat(XMLHelper.getAttribute(terrainDecal, "SizeZ"));
            final float rotation = Float.parseFloat(XMLHelper.getAttribute(terrainDecal, "Rotation"));
            final int priority = Integer.parseInt(XMLHelper.getAttribute(terrainDecal, "Priority"));
            int perceptionRadius = 0;
            final String radiusStr = XMLHelper.getAttribute(terrainDecal, "PerceptionRadius");
            if (radiusStr != null) {
                perceptionRadius = (int)Float.parseFloat(radiusStr);
            }
            final TerrainDecalData data = new TerrainDecalData(imageName, posX, posZ, sizeX, sizeZ, rotation, priority);
            final Template overrideTemplate = new Template();
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, WorldManagerClient.TEMPL_OBJECT_TYPE_TERRAIN_DECAL);
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, decalName);
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instance.getOid());
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, new Point(posX, 0.0f, posZ));
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SCALE, new AOVector(1.0f, 1.0f, 1.0f));
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, perceptionRadius);
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_TERRAIN_DECAL_DATA, data);
            final OID objOid = ObjectManagerClient.generateObject(-1, "BaseTemplate", overrideTemplate);
            if (objOid != null) {
                WorldManagerClient.spawn(objOid);
            }
            else {
                Log.error("Could not create decal=" + decalName + " imageName=" + imageName);
            }
        }
        this.processWorldCollections(instance, worldObjColNode);
        return true;
    }
    
    private void processPointLight(final Instance instance, final Node pointLightNode) {
        final String lightName = XMLHelper.getAttribute(pointLightNode, "Name");
        final String attenuationRange = XMLHelper.getAttribute(pointLightNode, "AttenuationRange");
        final String attenuationConstant = XMLHelper.getAttribute(pointLightNode, "AttenuationConstant");
        final String attenuationLinear = XMLHelper.getAttribute(pointLightNode, "AttenuationLinear");
        final String attenuationQuadratic = XMLHelper.getAttribute(pointLightNode, "AttenuationQuadratic");
        final Point lightLoc = WorldFileLoader.getPoint(XMLHelper.getMatchingChild(pointLightNode, "Position"));
        final Color specular = WorldFileLoader.getColor(XMLHelper.getMatchingChild(pointLightNode, "Specular"));
        final Color diffuse = WorldFileLoader.getColor(XMLHelper.getMatchingChild(pointLightNode, "Diffuse"));
        final LightData lightData = new LightData();
        lightData.setName(lightName);
        lightData.setAttenuationRange(Float.parseFloat(attenuationRange));
        lightData.setAttenuationConstant(Float.parseFloat(attenuationConstant));
        lightData.setAttenuationLinear(Float.parseFloat(attenuationLinear));
        lightData.setAttenuationQuadradic(Float.parseFloat(attenuationQuadratic));
        lightData.setSpecular(specular);
        lightData.setDiffuse(diffuse);
        lightData.setInitLoc(lightLoc);
        if (Log.loggingDebug) {
            Log.debug("LightData=" + lightData);
        }
        if (this.worldLoaderOverride.adjustLightData(this.worldFileName, lightName, lightData)) {
            final OID lightOid = ObjectManagerClient.generateLight(instance.getOid(), lightData);
            if (Log.loggingDebug) {
                Log.debug("Generated light, oid=" + lightOid);
            }
            final boolean rv = WorldManagerClient.spawn(lightOid) >= 0;
            if (Log.loggingDebug) {
                Log.debug("Light spawn rv=" + rv);
            }
        }
    }
    
    private boolean processStaticObject(final Instance instance, final Node objNode) {
        final String name = XMLHelper.getAttribute(objNode, "Name");
        final String mesh = XMLHelper.getAttribute(objNode, "Mesh");
        if (Log.loggingDebug) {
            Log.debug("StaticObject " + name + " mesh '" + mesh + "'");
        }
        final Node posNode = XMLHelper.getMatchingChild(objNode, "Position");
        if (posNode == null) {
            Log.error("No <Position> node, name=" + name);
            return false;
        }
        final Point loc = WorldFileLoader.getPoint(posNode);
        int perceptionRadius = 0;
        final String radiusStr = XMLHelper.getAttribute(objNode, "PerceptionRadius");
        if (radiusStr != null) {
            perceptionRadius = (int)Float.parseFloat(radiusStr);
        }
        final Node scaleNode = XMLHelper.getMatchingChild(objNode, "Scale");
        final AOVector scale = WorldFileLoader.getVector(scaleNode);
        final Node rotNode = XMLHelper.getMatchingChild(objNode, "Rotation");
        Quaternion orient = null;
        if (rotNode != null) {
            final float rotation = WorldFileLoader.getPoint(rotNode).getY();
            orient = Quaternion.fromAngleAxisDegrees(rotation, new AOVector(0.0f, 1.0f, 0.0f));
        }
        else {
            final Node orientNode = XMLHelper.getMatchingChild(objNode, "Orientation");
            orient = WorldFileLoader.getQuaternion(orientNode);
        }
        boolean castShadow = false;
        boolean receiveShadow = false;
        String shadowStr = XMLHelper.getAttribute(objNode, "CastShadows");
        if (shadowStr != null) {
            castShadow = shadowStr.equals("True");
        }
        shadowStr = XMLHelper.getAttribute(objNode, "ReceiveShadows");
        if (shadowStr != null) {
            receiveShadow = shadowStr.equals("True");
        }
        final DisplayContext dc = new DisplayContext(mesh);
        dc.setCastShadow(castShadow);
        dc.setReceiveShadow(receiveShadow);
        final Node subMeshNode = XMLHelper.getMatchingChild(objNode, "SubMeshes");
        if (subMeshNode != null) {
            final List<Node> subMeshInfoList = XMLHelper.getMatchingChildren(subMeshNode, "SubMeshInfo");
            for (final Node subMeshInfo : subMeshInfoList) {
                final String subMeshInfoName = XMLHelper.getAttribute(subMeshInfo, "Name");
                final String subMeshInfoMaterial = XMLHelper.getAttribute(subMeshInfo, "MaterialName");
                if (Log.loggingDebug) {
                    Log.debug("Submesh name=" + subMeshInfoName + ", material=" + subMeshInfoMaterial);
                }
                if (!XMLHelper.getAttribute(subMeshInfo, "Show").equals("True")) {
                    Log.warn("SubMesh is not visible - skipping, name=" + subMeshInfoName);
                }
                else {
                    dc.addSubmesh(new DisplayContext.Submesh(subMeshInfoName, subMeshInfoMaterial));
                }
            }
        }
        final Node pathObjectsNode = XMLHelper.getMatchingChild(objNode, "PathData");
        PathData pathData = null;
        if (pathObjectsNode != null) {
            final int pathVersion = (int)Float.parseFloat(XMLHelper.getAttribute(pathObjectsNode, "version"));
            final List<PathObject> pathObjects = new LinkedList<PathObject>();
            final List<Node> pathObjectNodes = XMLHelper.getMatchingChildren(pathObjectsNode, "PathObject");
            for (final Node pathObjectNode : pathObjectNodes) {
                final String modelName = XMLHelper.getAttribute(pathObjectNode, "modelName");
                final String type = XMLHelper.getAttribute(pathObjectNode, "type");
                final int firstTerrainIndex = (int)Float.parseFloat(XMLHelper.getAttribute(pathObjectNode, "firstTerrainIndex"));
                final List<PathPolygon> boundingPolygons = WorldFileLoader.processPathPolygons("BoundingPolygon", pathObjectNode);
                assert boundingPolygons.size() == 1;
                final List<PathPolygon> polygons = WorldFileLoader.processPathPolygons("PathPolygons", pathObjectNode);
                final List<PathArc> portals = WorldFileLoader.processPathArcs("PathPortals", pathObjectNode);
                final List<PathArc> arcs = WorldFileLoader.processPathArcs("PathArcs", pathObjectNode);
                final PathPolygon boundingPolygon = boundingPolygons.get(0);
                pathObjects.add(new PathObject(modelName, type, firstTerrainIndex, boundingPolygon, polygons, portals, arcs));
                if (!Log.loggingDebug) {
                    continue;
                }
                Log.debug("Path object model name =" + modelName + ", bounding polygon = " + boundingPolygon + ", polygon count = " + polygons.size() + ", portals count = " + portals.size() + ", arcs count = " + arcs.size());
            }
            if (pathObjects.size() > 0) {
                pathData = new PathData(pathVersion, pathObjects);
            }
            if (Log.loggingDebug) {
                Log.debug("Read PathData for model object " + name);
            }
        }
        if (Log.loggingDebug) {
            Log.debug("StaticObject name=" + name + " mesh=" + mesh + " loc=" + loc + " scale=" + scale + " orient=" + orient + " mesh=" + dc.getMeshFile() + " percRadius=" + perceptionRadius);
        }
        final List<Node> sounds = XMLHelper.getMatchingChildren(objNode, "Sound");
        final List<SoundData> soundData = this.getSoundDataList(sounds);
        final Node nameValuePairsNode = XMLHelper.getMatchingChild(objNode, "NameValuePairs");
        String anim = null;
        final Template overrideTemplate = new Template();
        if (nameValuePairsNode != null) {
            final Map<String, Serializable> props = XMLHelper.nameValuePairsHelper(nameValuePairsNode);
            if (props.containsKey("animation")) {
                anim = props.get("animation");
            }
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                overrideTemplate.put(Namespace.WORLD_MANAGER, entry.getKey(), entry.getValue());
            }
        }
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, name);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, dc);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instance.getOid());
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, loc);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, orient);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SCALE, scale);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, perceptionRadius);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_FOLLOWS_TERRAIN, Boolean.FALSE);
        if (anim != null) {
            overrideTemplate.put(Namespace.WORLD_MANAGER, ":tmpl.anim", anim);
        }
        if (soundData.size() > 0) {
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SOUND_DATA_LIST, (Serializable)soundData);
        }
        final List<Node> partEffectNodes = XMLHelper.getMatchingChildren(objNode, "ParticleEffect");
        final LinkedList<LinkedList<Object>> particles = new LinkedList<LinkedList<Object>>();
        for (final Node partEffectNode : partEffectNodes) {
            final LinkedList<Object> particleData = new LinkedList<Object>();
            final String peName = XMLHelper.getAttribute(partEffectNode, "ParticleEffectName");
            final String velScale = XMLHelper.getAttribute(partEffectNode, "VelocityScale");
            final String particleScale = XMLHelper.getAttribute(partEffectNode, "ParticleScale");
            final String attachName = XMLHelper.getAttribute(partEffectNode, "AttachmentPoint");
            particleData.add(peName);
            particleData.add(attachName);
            particleData.add(Float.parseFloat(velScale));
            particleData.add(Float.parseFloat(particleScale));
            particles.add(particleData);
        }
        if (!particles.isEmpty()) {
            overrideTemplate.put(Namespace.WORLD_MANAGER, "StaticParticles", particles);
        }
        if (this.worldLoaderOverride.adjustObjectTemplate(this.worldFileName, name, overrideTemplate)) {
            final OID objOid = ObjectManagerClient.generateObject(-1, "BaseTemplate", overrideTemplate);
            if (objOid != null) {
                WorldManagerClient.spawn(objOid);
            }
            else {
                Log.error("Could not create static object=" + name + " mesh=" + mesh + " loc=" + loc);
            }
            if (pathData != null) {
                instance.getPathInfo().getPathDictionary().put(name, pathData);
            }
        }
        return true;
    }
    
    private void processBoundary(final Instance instance, final Node boundaryNode) {
        final String name = XMLHelper.getAttribute(boundaryNode, "Name");
        final Boundary boundary = new Boundary(name);
        final String priS = XMLHelper.getAttribute(boundaryNode, "Priority");
        final Integer pri = (priS == null) ? null : Integer.parseInt(priS);
        final Node pointsNode = XMLHelper.getMatchingChild(boundaryNode, "PointCollection");
        final List<Node> points = XMLHelper.getMatchingChildren(pointsNode, "Point");
        if (points == null) {
            Log.warn("No points for boundary, ignoring");
            return;
        }
        for (final Node pointNode : points) {
            final Point p = WorldFileLoader.getPoint(pointNode);
            boundary.addPoint(p);
        }
        final Region region = new Region();
        region.setName(name);
        region.setPriority(pri);
        region.setBoundary(boundary);
        if (Log.loggingDebug) {
            Log.debug("processBoundary: new region=" + region);
        }
        final Node nameValuePairsNode = XMLHelper.getMatchingChild(boundaryNode, "NameValuePairs");
        if (nameValuePairsNode != null) {
            region.setProperties(XMLHelper.nameValuePairsHelper(nameValuePairsNode));
        }
        if (!this.worldLoaderOverride.adjustRegion(this.worldFileName, name, region)) {
            return;
        }
        final List<Node> sounds = XMLHelper.getMatchingChildren(boundaryNode, "Sound");
        final List<SoundData> soundData = this.getSoundDataList(sounds);
        if (soundData.size() > 0) {
            final SoundRegionConfig soundConfig = new SoundRegionConfig();
            soundConfig.setSoundData(soundData);
            if (this.worldLoaderOverride.adjustRegionConfig(this.worldFileName, name, region, soundConfig)) {
                region.addConfig(soundConfig);
            }
        }
        final Node fogNode = getFogNode(boundaryNode);
        if (fogNode != null) {
            final String nearS = XMLHelper.getAttribute(fogNode, "Near");
            final String farS = XMLHelper.getAttribute(fogNode, "Far");
            final Node colorNode = XMLHelper.getMatchingChild(fogNode, "Color");
            final String redS = XMLHelper.getAttribute(colorNode, "R");
            final String greenS = XMLHelper.getAttribute(colorNode, "G");
            final String blueS = XMLHelper.getAttribute(colorNode, "B");
            final int red = (int)(Float.parseFloat(redS) * 255.0f);
            final int green = (int)(Float.parseFloat(greenS) * 255.0f);
            final int blue = (int)(Float.parseFloat(blueS) * 255.0f);
            final int near = (int)Float.parseFloat(nearS);
            final int far = (int)Float.parseFloat(farS);
            final FogRegionConfig fogConfig = new FogRegionConfig();
            fogConfig.setColor(new Color(red, green, blue));
            fogConfig.setNear(near);
            fogConfig.setFar(far);
            if (Log.loggingDebug) {
                Log.debug("Fog region: " + fogConfig);
            }
            if (this.worldLoaderOverride.adjustRegionConfig(this.worldFileName, name, region, fogConfig)) {
                region.addConfig(fogConfig);
            }
        }
        final Node dirLightNode = XMLHelper.getMatchingChild(boundaryNode, "DirectionalLight");
        if (dirLightNode != null) {
            final AOVector dir = WorldFileLoader.getVector(XMLHelper.getMatchingChild(dirLightNode, "Direction"));
            final Color diffuse = WorldFileLoader.getColor(XMLHelper.getMatchingChild(dirLightNode, "Diffuse"));
            final Color specular = WorldFileLoader.getColor(XMLHelper.getMatchingChild(dirLightNode, "Specular"));
            final RegionConfig regionConfig = new RegionConfig(LightData.DirLightRegionType);
            Quaternion orient = AOVector.UnitZ.getRotationTo(dir);
            if (orient == null) {
                if (Log.loggingDebug) {
                    Log.debug("Region light is near inverse, dir=" + dir);
                }
                orient = new Quaternion(0.0f, 1.0f, 0.0f, 0.0f);
            }
            regionConfig.setProperty("orient", orient);
            regionConfig.setProperty("specular", specular);
            regionConfig.setProperty("diffuse", diffuse);
            final String boundaryName = XMLHelper.getAttribute(boundaryNode, "Name");
            regionConfig.setProperty("name", "dirLight_" + boundaryName);
            if (this.worldLoaderOverride.adjustRegionConfig(this.worldFileName, name, region, regionConfig)) {
                region.addConfig(regionConfig);
                if (Log.loggingDebug) {
                    Log.debug("Added dir light region: specular=" + specular + " diffuse=" + diffuse + " dir=" + dir + " orient=" + orient);
                }
            }
        }
        final Node ambientNode = XMLHelper.getMatchingChild(boundaryNode, "AmbientLight");
        if (ambientNode != null) {
            final Color ambientColor = WorldFileLoader.getColor(XMLHelper.getMatchingChild(ambientNode, "Color"));
            final RegionConfig regionConfig2 = new RegionConfig(LightData.AmbientLightRegionType);
            regionConfig2.setProperty("color", ambientColor);
            if (this.worldLoaderOverride.adjustRegionConfig(this.worldFileName, name, region, regionConfig2)) {
                region.addConfig(regionConfig2);
                Log.debug("Added ambient light region: color=" + ambientColor);
            }
        }
        final Node waterNode = getWaterNode(boundaryNode);
        if (waterNode != null) {
            final float height = Float.parseFloat(XMLHelper.getAttribute(waterNode, "Height"));
            String regionConfig3 = "<boundaries><boundary><name>" + name + "_WATER</name>";
            regionConfig3 += "<points>";
            for (final Point point : boundary.getPoints()) {
                regionConfig3 = regionConfig3 + "<point x=\"" + point.getX() + "\" y=\"" + point.getZ() + "\" />";
            }
            regionConfig3 += "</points>";
            regionConfig3 += "<boundarySemantic type=\"WaterPlane\">";
            regionConfig3 = regionConfig3 + "<height>" + height + "</height>";
            regionConfig3 = regionConfig3 + "<name>" + name + "_WATERNAME</name>";
            regionConfig3 += "</boundarySemantic>";
            regionConfig3 += "</boundary></boundaries>";
            if (Log.loggingDebug) {
                Log.debug("processBoundary: waterRegion: " + regionConfig3);
            }
            instance.addRegionConfig(regionConfig3);
        }
        final List<Node> forestsNode = XMLHelper.getMatchingChildren(boundaryNode, "Forest");
        if (forestsNode != null) {
            for (final Node forestNode : forestsNode) {
                final String forestXML = this.processTreeBoundary(boundary.getPoints(), forestNode);
                if (Log.loggingDebug) {
                    Log.debug("processBoundary: Tree boundary: xml=" + forestXML);
                }
                instance.addRegionConfig(forestXML);
            }
        }
        final Node grassNode = XMLHelper.getMatchingChild(boundaryNode, "Grass");
        if (grassNode != null) {
            final String grassXML = this.processGrassBoundary(boundary.getPoints(), grassNode);
            if (Log.loggingDebug) {
                Log.debug("processBoundary: Grass boundary: xml=" + grassXML);
            }
            instance.addRegionConfig(grassXML);
        }
        final Message msg = new WorldManagerClient.NewRegionMessage(instance.getOid(), region);
        Engine.getAgent().sendBroadcast(msg);
        instance.addRegion(region);
    }
    
    private void processMarker(final Instance instance, final Node markerNode) {
        final String name = XMLHelper.getAttribute(markerNode, "Name");
        final Node posNode = XMLHelper.getMatchingChild(markerNode, "Position");
        final Point loc = WorldFileLoader.getPoint(posNode);
        final Node orientNode = XMLHelper.getMatchingChild(markerNode, "Orientation");
        final Quaternion orient = WorldFileLoader.getQuaternion(orientNode);
        if (Log.loggingDebug) {
            Log.debug("Marker " + name + ", loc=" + loc);
        }
        final Template overrideTemplate = new Template();
        final Node nameValuePairsNode = XMLHelper.getMatchingChild(markerNode, "NameValuePairs");
        if (nameValuePairsNode != null) {
            final Map<String, Serializable> props = XMLHelper.nameValuePairsHelper(nameValuePairsNode);
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                overrideTemplate.put(Namespace.WORLD_MANAGER, entry.getKey(), entry.getValue());
            }
        }
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, name);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, WorldManagerClient.TEMPL_OBJECT_TYPE_MARKER);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instance.getOid());
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, loc);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, orient);
        if (this.worldLoaderOverride.adjustObjectTemplate(this.worldFileName, name, overrideTemplate)) {
            final OID objOid = ObjectManagerClient.generateObject(-1, "BaseTemplate", overrideTemplate);
            if (objOid != null) {
                WorldManagerClient.spawn(objOid);
            }
            else {
                Log.error("Could not create marker=" + name + " worldFileName=" + this.worldFileName);
            }
        }
        final List<Node> partEffectNodes = XMLHelper.getMatchingChildren(markerNode, "ParticleEffect");
        for (final Node partEffectNode : partEffectNodes) {
            this.processParticleEffect(instance, name, loc, orient, partEffectNode);
        }
        final List<Node> spawnGenNodes = XMLHelper.getMatchingChildren(markerNode, "SpawnGen");
        for (final Node spawnGenNode : spawnGenNodes) {
            this.processSpawnGen(instance, name, loc, orient, nameValuePairsNode, spawnGenNode);
        }
        final List<Node> sounds = XMLHelper.getMatchingChildren(markerNode, "Sound");
        for (final Node soundNode : sounds) {
            this.processSound(instance, name, loc, soundNode);
        }
    }
    
    private void processParticleEffect(final Instance instance, final String markerName, final Point loc, final Quaternion orient, final Node partEffectNode) {
        final String peName = XMLHelper.getAttribute(partEffectNode, "ParticleEffectName");
        final String velScale = XMLHelper.getAttribute(partEffectNode, "VelocityScale");
        final String particleScale = XMLHelper.getAttribute(partEffectNode, "ParticleScale");
        final String objName = markerName + "-" + peName;
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, objName);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, new DisplayContext("tiny_cube.mesh"));
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instance.getOid());
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, loc);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, orient);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_FOLLOWS_TERRAIN, Boolean.FALSE);
        final LinkedList<LinkedList<Object>> particles = new LinkedList<LinkedList<Object>>();
        final LinkedList<Object> particleData = new LinkedList<Object>();
        particleData.add(peName);
        particleData.add("base");
        particleData.add(Float.parseFloat(velScale));
        particleData.add(Float.parseFloat(particleScale));
        particles.add(particleData);
        overrideTemplate.put(Namespace.WORLD_MANAGER, "StaticParticles", particles);
        if (this.worldLoaderOverride.adjustObjectTemplate(this.worldFileName, objName, overrideTemplate)) {
            final OID fakeOid = ObjectManagerClient.generateObject(-1, "BaseTemplate", overrideTemplate);
            if (fakeOid != null) {
                WorldManagerClient.spawn(fakeOid);
            }
            else {
                Log.error("Could not create object for particle system=" + markerName + " particle=" + peName + " loc=" + loc);
            }
        }
    }
    
    private void processSpawnGen(final Instance instance, final String markerName, final Point loc, final Quaternion orient, final Node nameValuePairsNode, final Node spawnGenNode) {
        final String templateName = XMLHelper.getAttribute(spawnGenNode, "TemplateName");
        final String spawnRadius = XMLHelper.getAttribute(spawnGenNode, "SpawnRadius");
        final String numSpawns = XMLHelper.getAttribute(spawnGenNode, "NumSpawns");
        final String respawnTime = XMLHelper.getAttribute(spawnGenNode, "RespawnTime");
        Integer spawnRadiusVal = new Integer(0);
        Integer numSpawnsVal = new Integer(1);
        Integer respawnTimeVal = new Integer(0);
        if (spawnRadius != null) {
            spawnRadiusVal = Integer.parseInt(spawnRadius);
        }
        if (numSpawns != null) {
            numSpawnsVal = Integer.parseInt(numSpawns);
        }
        if (respawnTime != null) {
            respawnTimeVal = Integer.parseInt(respawnTime);
        }
        final SpawnData spawnData = new SpawnData(markerName, templateName, 1, "WEObjFactory", instance.getOid(), loc, orient, spawnRadiusVal, numSpawnsVal, respawnTimeVal);
        if (nameValuePairsNode != null) {
            spawnData.setPropertyMap(XMLHelper.nameValuePairsHelper(nameValuePairsNode));
        }
        if (this.worldLoaderOverride.adjustSpawnData(this.worldFileName, markerName, spawnData)) {
            instance.addSpawnData(spawnData);
        }
    }
    
    private void processSound(final Instance instance, final String markerName, final Point markerLoc, final Node soundNode) {
        final String fileName = XMLHelper.getAttribute(soundNode, "Filename");
        final String typeStr = XMLHelper.getAttribute(soundNode, "Type");
        final NamedNodeMap attrMap = soundNode.getAttributes();
        final Map<String, String> propertyMap = new HashMap<String, String>();
        for (int ii = 0; ii < attrMap.getLength(); ++ii) {
            final Node attr = attrMap.item(ii);
            final String attrName = attr.getNodeName();
            if (!attrName.equals("Filename") && !attrName.equals("Type")) {
                propertyMap.put(attrName, attr.getNodeValue());
            }
        }
        final Template overrideTemplate = new Template();
        int perceptionRadius = WorldCollectionFileLoader.DEFAULT_SOUND_PERCEPTION_RADIUS;
        final String maxAtten = propertyMap.get("MaxAttenuationDistance");
        if (maxAtten != null) {
            perceptionRadius = (int)Float.parseFloat(maxAtten);
        }
        final String objName = markerName + "-" + fileName;
        final DisplayContext fakeDC = new DisplayContext("tiny_cube.mesh");
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, WorldManagerClient.TEMPL_OBJECT_TYPE_POINT_SOUND);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, objName);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, fakeDC);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instance.getOid());
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, markerLoc);
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SCALE, new AOVector(1.0f, 1.0f, 1.0f));
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, perceptionRadius);
        final List<SoundData> soundList = new LinkedList<SoundData>();
        soundList.add(new SoundData(fileName, typeStr, propertyMap));
        overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SOUND_DATA_LIST, (Serializable)soundList);
        if (this.worldLoaderOverride.adjustObjectTemplate(this.worldFileName, objName, overrideTemplate)) {
            final OID objOid = ObjectManagerClient.generateObject(-1, "BaseTemplate", overrideTemplate);
            if (objOid != null) {
                WorldManagerClient.spawn(objOid);
            }
            else {
                Log.error("Could not create marker=" + markerName + " soundFileName=" + fileName);
            }
        }
    }
    
    private String processTreeBoundary(final List<Point> points, final Node forestNode) {
        String xml = "<boundaries><boundary>";
        final String name = XMLHelper.getAttribute(forestNode, "Name");
        xml = xml + "<name>" + name + "_FOREST</name>";
        if (Log.loggingDebug) {
            Log.debug("processTreeBoundary: name=" + name);
        }
        xml += "<points>";
        for (final Point p : points) {
            xml = xml + "<point x=\"" + p.getX() + "\" y=\"" + p.getZ() + "\" />";
        }
        xml += "</points>";
        xml += "<boundarySemantic type=\"SpeedTreeForest\">";
        final String seed = XMLHelper.getAttribute(forestNode, "Seed");
        xml = xml + "<seed>" + seed + "</seed>";
        if (Log.loggingDebug) {
            Log.debug("processTreeBoundary: seed=" + seed);
        }
        xml = xml + "<name>" + name + "</name>";
        final String windFile = XMLHelper.getAttribute(forestNode, "Filename");
        xml = xml + "<windFilename>" + windFile + "</windFilename>";
        if (Log.loggingDebug) {
            Log.debug("processTreeBoundary: windFile=" + windFile);
        }
        final String windStr = XMLHelper.getAttribute(forestNode, "WindSpeed");
        xml = xml + "<windStrength>" + windStr + "</windStrength>";
        if (Log.loggingDebug) {
            Log.debug("processTreeBoundary: windStrength=" + windStr);
        }
        final Node windDir = XMLHelper.getMatchingChild(forestNode, "WindDirection");
        final String dirX = XMLHelper.getAttribute(windDir, "x");
        final String dirY = XMLHelper.getAttribute(windDir, "y");
        final String dirZ = XMLHelper.getAttribute(windDir, "z");
        xml = xml + "<windDirection x=\"" + dirX + "\" y=\"" + dirY + "\" z=\"" + dirZ + "\" />";
        if (Log.loggingDebug) {
            Log.debug("processTreeBoundary: windDir: x=" + dirX + ",y=" + dirY + ",z=" + dirZ);
        }
        final List<Node> treeTypes = XMLHelper.getMatchingChildren(forestNode, "Tree");
        if (treeTypes.isEmpty()) {
            Log.warn("processTreeBoundary: no trees in forest");
            return null;
        }
        for (final Node treeType : treeTypes) {
            final String fileName = XMLHelper.getAttribute(treeType, "Filename");
            xml += "<treeType";
            xml = xml + " filename=\"" + fileName + "\"";
            if (Log.loggingDebug) {
                Log.debug("processTreeBoundary: TreeType Filename=" + fileName);
            }
            final String scale = XMLHelper.getAttribute(treeType, "Scale");
            xml = xml + " size=\"" + scale + "\"";
            if (Log.loggingDebug) {
                Log.debug("processTreeBoundary: scale size=" + scale);
            }
            final String scaleVariance = XMLHelper.getAttribute(treeType, "ScaleVariance");
            xml = xml + " sizeVariance=\"" + scaleVariance + "\"";
            if (Log.loggingDebug) {
                Log.debug("processTreeBoundary: sizeVariance=" + scaleVariance);
            }
            final String instances = XMLHelper.getAttribute(treeType, "Instances");
            xml = xml + " numInstances=\"" + instances + "\" />";
            if (Log.loggingDebug) {
                Log.debug("processTreeBoundary: instances=" + instances);
            }
        }
        xml += "</boundarySemantic></boundary></boundaries>";
        return xml;
    }
    
    protected String processGrassBoundary(final List<Point> points, final Node grassNode) {
        String xml = "<boundaries><boundary>";
        final String name = XMLHelper.getAttribute(grassNode, "Name");
        xml = xml + "<name>" + name + "_GRASS</name>";
        if (Log.loggingDebug) {
            Log.debug("processTreeBoundary: name=" + name);
        }
        xml += "<points>";
        for (final Point p : points) {
            xml = xml + "<point x=\"" + p.getX() + "\" y=\"" + p.getZ() + "\" />";
        }
        xml += "</points>";
        xml += "<boundarySemantic type=\"Vegetation\">";
        xml = xml + "<name>" + name + "</name>";
        final List<Node> plantTypes = XMLHelper.getMatchingChildren(grassNode, "PlantType");
        if (plantTypes == null) {
            throw new AORuntimeException("no plant types in a grass boundary");
        }
        for (final Node plantTypeNode : plantTypes) {
            final String numInstances = XMLHelper.getAttribute(plantTypeNode, "Instances");
            final String imageName = XMLHelper.getAttribute(plantTypeNode, "ImageName");
            final String colorMultLow = XMLHelper.getAttribute(plantTypeNode, "ColorMultLow");
            final String colorMultHi = XMLHelper.getAttribute(plantTypeNode, "ColorMultHi");
            final String scaleWidthLow = XMLHelper.getAttribute(plantTypeNode, "ScaleWidthLow");
            final String scaleWidthHi = XMLHelper.getAttribute(plantTypeNode, "ScaleWidthHi");
            final String scaleHeightLow = XMLHelper.getAttribute(plantTypeNode, "ScaleHeightLow");
            final String scaleHeightHi = XMLHelper.getAttribute(plantTypeNode, "ScaleHeightHi");
            final String windMagnitude = XMLHelper.getAttribute(plantTypeNode, "WindMagnitude");
            final String red = XMLHelper.getAttribute(plantTypeNode, "R");
            final String green = XMLHelper.getAttribute(plantTypeNode, "G");
            final String blue = XMLHelper.getAttribute(plantTypeNode, "B");
            xml = xml + "<PlantType numInstances=\"" + numInstances + "\" imageName=\"" + imageName + "\" atlasStartX=\"0\" atlasStartY=\"0\" atlasEndX=\"1\" atlasEndY=\"1\"" + " scaleWidthLow=\"" + scaleWidthLow + "\" scaleWidthHi=\"" + scaleWidthHi + "\" scaleHeightLow=\"" + scaleHeightLow + "\" scaleHeightHi=\"" + scaleHeightHi + "\" colorMultLow=\"" + colorMultLow + "\" colorMultHi=\"" + colorMultHi + "\" windMagnitude=\"" + windMagnitude + "\"><color r=\"" + red + "\" g=\"" + green + "\" b=\"" + blue + "\"/>" + "</PlantType>";
        }
        xml += "</boundarySemantic></boundary></boundaries>";
        return xml;
    }
    
    private List<SoundData> getSoundDataList(final List<Node> sounds) {
        final List<SoundData> soundData = new LinkedList<SoundData>();
        for (final Node sound : sounds) {
            final String fileName = XMLHelper.getAttribute(sound, "Filename");
            final String typeStr = XMLHelper.getAttribute(sound, "Type");
            final NamedNodeMap attrMap = sound.getAttributes();
            final Map<String, String> propertyMap = new HashMap<String, String>();
            for (int ii = 0; ii < attrMap.getLength(); ++ii) {
                final Node attr = attrMap.item(ii);
                final String attrName = attr.getNodeName();
                if (!attrName.equals("Filename") && !attrName.equals("Type")) {
                    propertyMap.put(attrName, attr.getNodeValue());
                }
            }
            soundData.add(new SoundData(fileName, typeStr, propertyMap));
        }
        return soundData;
    }
    
    private static Road processRoad(final Node roadNode) {
        final String name = XMLHelper.getAttribute(roadNode, "Name");
        final Integer halfWidth = Integer.parseInt(XMLHelper.getAttribute(roadNode, "HalfWidth"));
        final Road road = new Road(name);
        road.setHalfWidth(halfWidth);
        final Node pointsNode = XMLHelper.getMatchingChild(roadNode, "PointCollection");
        final List<Node> points = XMLHelper.getMatchingChildren(pointsNode, "Point");
        for (final Node pointNode : points) {
            final String x = XMLHelper.getAttribute(pointNode, "x");
            final String y = XMLHelper.getAttribute(pointNode, "y");
            final String z = XMLHelper.getAttribute(pointNode, "z");
            road.addPoint(new Point((int)Math.round(Double.parseDouble(x)), (int)Math.round(Double.parseDouble(y)), (int)Math.round(Double.parseDouble(z))));
        }
        return road;
    }
    
    static Node getFogNode(final Node boundaryNode) {
        final Node fogNode = XMLHelper.getMatchingChild(boundaryNode, "Fog");
        return fogNode;
    }
    
    static Node getWaterNode(final Node boundaryNode) {
        return XMLHelper.getMatchingChild(boundaryNode, "Water");
    }
    
    static {
        WorldCollectionFileLoader.DEFAULT_SOUND_PERCEPTION_RADIUS = 25;
    }
}
