// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.pathing.PathEdge;
import atavism.server.pathing.PathArc;
import java.util.LinkedList;
import atavism.server.pathing.PathPolygon;
import atavism.server.math.Point;
import atavism.server.objects.Boundary;
import atavism.msgsys.Message;
import java.util.Iterator;
import java.util.List;
import atavism.server.objects.Color;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.objects.OceanData;
import atavism.server.pathing.PathObjectType;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.objects.LightData;
import atavism.server.objects.Fog;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import java.io.IOException;
import atavism.server.util.Log;
import java.io.File;
import atavism.server.util.XMLHelper;
import atavism.server.objects.Instance;
import org.w3c.dom.Document;

public class WorldFileLoader
{
    protected String worldFileName;
    protected String worldFileBasePath;
    protected WorldLoaderOverride worldLoaderOverride;
    protected Document worldDoc;
    
    public WorldFileLoader(final String worldFileName, final WorldLoaderOverride override) {
        this.worldFileName = worldFileName;
        this.worldLoaderOverride = override;
    }
    
    public void setWorldLoaderOverride(final WorldLoaderOverride override) {
        this.worldLoaderOverride = override;
    }
    
    public WorldLoaderOverride getWorldLoaderOverride() {
        return this.worldLoaderOverride;
    }
    
    public boolean load(final Instance instance) {
        return this.parse() && this.generate(instance);
    }
    
    public boolean parse() {
        try {
            final DocumentBuilder builder = XMLHelper.makeDocBuilder();
            final File xmlFile = new File(this.worldFileName);
            this.worldFileBasePath = xmlFile.getParent();
            this.worldDoc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            Log.exception("WorldFileLoader.parse(" + this.worldFileName + ")", e);
            return false;
        }
        catch (SAXException e2) {
            Log.exception("WorldFileLoader.parse(" + this.worldFileName + ")", e2);
            return false;
        }
        return true;
    }
    
    public boolean generate(final Instance instance) {
        final Node worldNode = XMLHelper.getMatchingChild(this.worldDoc, "World");
        if (worldNode == null) {
            Log.error("No <World> node in file " + this.worldFileName);
            return false;
        }
        final String worldName = XMLHelper.getAttribute(worldNode, "Name");
        if (worldName == null) {
            Log.error("No world name in file " + this.worldFileName);
            return false;
        }
        if (Log.loggingDebug) {
            Log.debug("world name=" + worldName + " (file " + this.worldFileName + ")");
        }
        final String fileVersion = XMLHelper.getAttribute(worldNode, "Version");
        if (fileVersion == null) {
            Log.error("No world file version");
            return false;
        }
        if (Log.loggingDebug) {
            Log.debug("world file version=" + fileVersion);
        }
        if (!fileVersion.equals("2") && !fileVersion.equals("2.0")) {
            Log.error("Unsupported world file version in file " + this.worldFileName);
            return false;
        }
        final Node skyboxNode = XMLHelper.getMatchingChild(worldNode, "Skybox");
        if (skyboxNode == null) {
            Log.debug("No <Skybox> node in file " + this.worldFileName);
        }
        else {
            final String skybox = XMLHelper.getAttribute(skyboxNode, "Name");
            if (Log.loggingDebug) {
                Log.debug("Global skybox=" + skybox);
            }
            instance.setGlobalSkybox(skybox);
        }
        final Node globalFogNode = XMLHelper.getMatchingChild(worldNode, "GlobalFog");
        if (globalFogNode != null) {
            final String near = XMLHelper.getAttribute(globalFogNode, "Near");
            final String far = XMLHelper.getAttribute(globalFogNode, "Far");
            final Color fogColor = getColor(XMLHelper.getMatchingChild(globalFogNode, "Color"));
            final Fog fog = new Fog("global fog");
            fog.setStart((int)Float.parseFloat(near));
            fog.setEnd((int)Float.parseFloat(far));
            fog.setColor(fogColor);
            instance.setGlobalFog(fog);
            if (Log.loggingDebug) {
                Log.debug("Global fog: " + fog);
            }
        }
        final Node globalAmbientLightNode = XMLHelper.getMatchingChild(worldNode, "GlobalAmbientLight");
        if (globalAmbientLightNode != null) {
            final Color lightColor = getColor(XMLHelper.getMatchingChild(globalAmbientLightNode, "Color"));
            instance.setGlobalAmbientLight(lightColor);
            if (Log.loggingDebug) {
                Log.debug("Global ambient light: " + lightColor);
            }
        }
        final Node globalDirectionalLightNode = XMLHelper.getMatchingChild(worldNode, "GlobalDirectionalLight");
        if (globalDirectionalLightNode != null) {
            final Color diffuseColor = getColor(XMLHelper.getMatchingChild(globalDirectionalLightNode, "Diffuse"));
            final Color specularColor = getColor(XMLHelper.getMatchingChild(globalDirectionalLightNode, "Specular"));
            final AOVector lightDir = getVector(XMLHelper.getMatchingChild(globalDirectionalLightNode, "Direction"));
            final LightData lightData = new LightData();
            lightData.setName("globalDirLight");
            lightData.setDiffuse(diffuseColor);
            lightData.setSpecular(specularColor);
            lightData.setAttenuationRange(1000.0f);
            lightData.setAttenuationConstant(1.0f);
            Quaternion q = AOVector.UnitZ.getRotationTo(lightDir);
            if (q == null) {
                if (Log.loggingDebug) {
                    Log.debug("global light orient is near inverse, dir=" + lightDir);
                }
                q = new Quaternion(0.0f, 1.0f, 0.0f, 0.0f);
            }
            lightData.setOrientation(q);
            instance.setGlobalDirectionalLight(lightData);
            if (Log.loggingDebug) {
                Log.debug("Global directional light: " + lightData);
            }
        }
        final Node pathObjectTypesNode = XMLHelper.getMatchingChild(worldNode, "PathObjectTypes");
        if (pathObjectTypesNode != null) {
            final List<Node> pathObjectTypeNodes = XMLHelper.getMatchingChildren(pathObjectTypesNode, "PathObjectType");
            for (final Node pathObjectTypeNode : pathObjectTypeNodes) {
                final String potName = XMLHelper.getAttribute(pathObjectTypeNode, "name");
                final float potHeight = Float.parseFloat(XMLHelper.getAttribute(pathObjectTypeNode, "height"));
                final float potWidth = Float.parseFloat(XMLHelper.getAttribute(pathObjectTypeNode, "width"));
                final float potMaxClimbSlope = Float.parseFloat(XMLHelper.getAttribute(pathObjectTypeNode, "maxClimbSlope"));
                instance.getPathInfo().getTypeDictionary().put(potName, new PathObjectType(potName, potHeight, potWidth, potMaxClimbSlope));
                if (Log.loggingDebug) {
                    Log.debug("Path object type name=" + potName);
                }
            }
        }
        final Node oceanNode = XMLHelper.getMatchingChild(worldNode, "Ocean");
        if (oceanNode != null) {
            final OceanData oceanData = new OceanData();
            final String displayOcean = XMLHelper.getAttribute(oceanNode, "DisplayOcean");
            oceanData.displayOcean = (displayOcean.equals("True") ? Boolean.TRUE : Boolean.FALSE);
            final String useParams = XMLHelper.getAttribute(oceanNode, "UseParams");
            if (useParams != null) {
                oceanData.useParams = (useParams.equals("True") ? Boolean.TRUE : Boolean.FALSE);
            }
            final String waveHeight = XMLHelper.getAttribute(oceanNode, "WaveHeight");
            if (waveHeight != null) {
                oceanData.waveHeight = Float.parseFloat(waveHeight);
            }
            final String seaLevel = XMLHelper.getAttribute(oceanNode, "SeaLevel");
            if (seaLevel != null) {
                oceanData.seaLevel = Float.parseFloat(seaLevel);
            }
            final String bumpScale = XMLHelper.getAttribute(oceanNode, "BumpScale");
            if (bumpScale != null) {
                oceanData.bumpScale = Float.parseFloat(bumpScale);
            }
            final String bumpSpeedX = XMLHelper.getAttribute(oceanNode, "BumpSpeedX");
            if (bumpSpeedX != null) {
                oceanData.bumpSpeedX = Float.parseFloat(bumpSpeedX);
            }
            final String bumpSpeedZ = XMLHelper.getAttribute(oceanNode, "BumpSpeedZ");
            if (bumpSpeedZ != null) {
                oceanData.bumpSpeedZ = Float.parseFloat(bumpSpeedZ);
            }
            final String textureScaleX = XMLHelper.getAttribute(oceanNode, "TextureScaleX");
            if (textureScaleX != null) {
                oceanData.textureScaleX = Float.parseFloat(textureScaleX);
            }
            final String textureScaleZ = XMLHelper.getAttribute(oceanNode, "TextureScaleZ");
            if (textureScaleZ != null) {
                oceanData.textureScaleZ = Float.parseFloat(textureScaleZ);
            }
            final Node deepColorNode = XMLHelper.getMatchingChild(oceanNode, "DeepColor");
            if (deepColorNode != null) {
                oceanData.deepColor = getColor(deepColorNode);
            }
            final Node shallowColorNode = XMLHelper.getMatchingChild(oceanNode, "ShallowColor");
            if (shallowColorNode != null) {
                oceanData.shallowColor = getColor(shallowColorNode);
            }
            instance.setOceanData(oceanData);
            if (Log.loggingDebug) {
                Log.debug("Ocean: " + oceanData);
            }
        }
        TerrainConfig terrainConfig = instance.getTerrainConfig();
        if (terrainConfig != null && Log.loggingDebug) {
            Log.debug("Terrain: " + terrainConfig);
        }
        if (terrainConfig == null) {
            final Node terrainNode = XMLHelper.getMatchingChild(worldNode, "Terrain");
            if (terrainNode != null) {
                String terrainXML = XMLHelper.toXML(terrainNode);
                if (Log.loggingDebug) {
                    Log.debug("Terrain: xmlsize=" + terrainXML.length());
                }
                final Node terrainDisplay = XMLHelper.getMatchingChild(worldNode, "TerrainDisplay");
                if (terrainDisplay != null) {
                    final String terrainDisplayXML = XMLHelper.toXML(terrainDisplay);
                    if (Log.loggingDebug) {
                        Log.debug("TerrainDisplay: " + terrainDisplayXML);
                    }
                    terrainXML += terrainDisplayXML;
                }
                terrainConfig = new TerrainConfig();
                terrainConfig.setConfigType("xmlstring");
                terrainConfig.setConfigData(terrainXML);
                instance.setTerrainConfig(terrainConfig);
                if (Log.loggingDebug) {
                    Log.debug("terrain has been set:" + terrainConfig);
                }
            }
            else {
                Log.debug("No terrain in file");
            }
        }
        this.setupGlobalRegion(instance);
        if (!this.processWorldCollections(instance, worldNode)) {
            return false;
        }
        final Message msg = new WorldManagerClient.NewRegionMessage(instance.getOid(), instance.getGlobalRegion());
        Engine.getAgent().sendBroadcast(msg);
        return true;
    }
    
    protected void setupGlobalRegion(final Instance instance) {
        final Boundary globalBoundary = new Boundary();
        globalBoundary.addPoint(new Point(-2000000.0f, 0.0f, 2000000.0f));
        globalBoundary.addPoint(new Point(2000000.0f, 0.0f, 2000000.0f));
        globalBoundary.addPoint(new Point(2000000.0f, 0.0f, -2000000.0f));
        globalBoundary.addPoint(new Point(-2000000.0f, 0.0f, -2000000.0f));
        instance.getGlobalRegion().setBoundary(globalBoundary);
    }
    
    protected boolean processWorldCollections(final Instance instance, final Node node) {
        final List<Node> worldCollections = XMLHelper.getMatchingChildren(node, "WorldCollection");
        for (final Node worldCollectionNode : worldCollections) {
            final String colFilename = XMLHelper.getAttribute(worldCollectionNode, "Filename");
            if (colFilename != null) {
                final String fullFile = this.worldFileBasePath + File.separator + colFilename;
                if (Log.loggingDebug) {
                    Log.debug("Loading world collection " + fullFile);
                }
                final WorldCollectionFileLoader collectionLoader = new WorldCollectionFileLoader(fullFile, this.worldLoaderOverride);
                if (!collectionLoader.load(instance)) {
                    return false;
                }
                continue;
            }
            else {
                final String collectionName = XMLHelper.getAttribute(worldCollectionNode, "Name");
                if (Log.loggingDebug) {
                    Log.debug("Loading world collection from database entry " + collectionName);
                }
                final WorldCollectionDatabaseLoader collectionLoader2 = new WorldCollectionDatabaseLoader(collectionName, this.worldLoaderOverride);
                if (!collectionLoader2.load(instance)) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    protected static List<PathPolygon> processPathPolygons(final String introducer, final Node parentNode) {
        final Node polyContainerNode = XMLHelper.getMatchingChild(parentNode, introducer);
        final List<Node> polyNodes = XMLHelper.getMatchingChildren(polyContainerNode, "PathPolygon");
        final LinkedList<PathPolygon> polys = new LinkedList<PathPolygon>();
        if (polyNodes == null) {
            return polys;
        }
        for (final Node polyNode : polyNodes) {
            final int index = (int)Float.parseFloat(XMLHelper.getAttribute(polyNode, "index"));
            final String stringKind = XMLHelper.getAttribute(polyNode, "kind");
            final byte polygonKind = PathPolygon.parsePolygonKind(stringKind);
            final List<Node> cornerNodes = XMLHelper.getMatchingChildren(polyNode, "Corner");
            assert cornerNodes.size() >= 3;
            final LinkedList<AOVector> corners = new LinkedList<AOVector>();
            for (final Node corner : cornerNodes) {
                corners.add(new AOVector(getPoint(corner)));
            }
            polys.add(new PathPolygon(index, polygonKind, corners));
        }
        return polys;
    }
    
    protected static List<PathArc> processPathArcs(final String introducer, final Node parentNode) {
        final Node arcContainerNode = XMLHelper.getMatchingChild(parentNode, introducer);
        final List<Node> arcNodes = XMLHelper.getMatchingChildren(arcContainerNode, "PathArc");
        final LinkedList<PathArc> arcs = new LinkedList<PathArc>();
        if (arcNodes == null) {
            return arcs;
        }
        for (final Node arcNode : arcNodes) {
            final byte arcKind = PathArc.parseArcKind(XMLHelper.getAttribute(arcNode, "kind"));
            final int poly1Index = (int)Float.parseFloat(XMLHelper.getAttribute(arcNode, "poly1Index"));
            final int poly2Index = (int)Float.parseFloat(XMLHelper.getAttribute(arcNode, "poly2Index"));
            final PathEdge edge = processPathEdge(arcNode);
            arcs.add(new PathArc(arcKind, poly1Index, poly2Index, edge));
        }
        return arcs;
    }
    
    protected static PathEdge processPathEdge(final Node parentNode) {
        final Node edgeNode = XMLHelper.getMatchingChild(parentNode, "PathEdge");
        return new PathEdge(new AOVector(getPoint(XMLHelper.getMatchingChild(edgeNode, "Start"))), new AOVector(getPoint(XMLHelper.getMatchingChild(edgeNode, "End"))));
    }
    
    public static Color getColor(final Node colorNode) {
        final String redS = XMLHelper.getAttribute(colorNode, "R");
        final String greenS = XMLHelper.getAttribute(colorNode, "G");
        final String blueS = XMLHelper.getAttribute(colorNode, "B");
        final Color color = new Color();
        color.setRed((int)(Float.parseFloat(redS) * 255.0f));
        color.setGreen((int)(Float.parseFloat(greenS) * 255.0f));
        color.setBlue((int)(Float.parseFloat(blueS) * 255.0f));
        return color;
    }
    
    public static AOVector getVector(final Node xyzNode) {
        final String posX = XMLHelper.getAttribute(xyzNode, "x");
        final String posY = XMLHelper.getAttribute(xyzNode, "y");
        final String posZ = XMLHelper.getAttribute(xyzNode, "z");
        final float x = Float.parseFloat(posX);
        final float y = Float.parseFloat(posY);
        final float z = Float.parseFloat(posZ);
        return new AOVector(x, y, z);
    }
    
    public static Point getPoint(final Node xyzNode) {
        final String posX = XMLHelper.getAttribute(xyzNode, "x");
        final String posY = XMLHelper.getAttribute(xyzNode, "y");
        final String posZ = XMLHelper.getAttribute(xyzNode, "z");
        final int x = (int)Math.round(Double.parseDouble(posX));
        final int y = (int)Math.round(Double.parseDouble(posY));
        final int z = (int)Math.round(Double.parseDouble(posZ));
        return new Point(x, y, z);
    }
    
    public static Quaternion getQuaternion(final Node quatNode) {
        final String x = XMLHelper.getAttribute(quatNode, "x");
        final String y = XMLHelper.getAttribute(quatNode, "y");
        final String z = XMLHelper.getAttribute(quatNode, "z");
        final String w = XMLHelper.getAttribute(quatNode, "w");
        return new Quaternion(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z), Float.parseFloat(w));
    }
}
