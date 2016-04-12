// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.math.AOVector;
import atavism.server.objects.DisplayContext;
import atavism.server.objects.ObjectTypes;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.objects.Marker;
import atavism.server.objects.ObjectType;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Node;
import atavism.server.util.XMLHelper;
import atavism.server.objects.Template;
import java.util.Iterator;
import atavism.server.objects.Entity;
import atavism.server.plugins.ObjectManagerClient;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import java.util.Collection;
import java.util.List;
import atavism.server.objects.PersistableTemplate;
import java.util.LinkedList;
import atavism.server.objects.Instance;
import atavism.server.util.Logger;

public class WorldCollectionDatabaseLoader implements WorldCollectionLoader
{
    protected static final Logger log;
    protected String collectionName;
    protected WorldLoaderOverride worldLoaderOverride;
    
    public WorldCollectionDatabaseLoader(final String collectionName, final WorldLoaderOverride override) {
        this.collectionName = collectionName;
        this.worldLoaderOverride = override;
    }
    
    @Override
    public boolean load(final Instance instance) {
        WorldCollectionDatabaseLoader.log.debug("load: loading collection name: " + this.collectionName);
        final OID persistOid = Engine.getDatabase().getOidByName(this.collectionName, Namespace.INSTANCE);
        if (persistOid == null) {
            WorldCollectionDatabaseLoader.log.error("Failed to load from persistence key: " + this.collectionName);
            return false;
        }
        final Entity persistEntity = Engine.getDatabase().loadEntity(persistOid, Namespace.INSTANCE);
        if (persistEntity == null) {
            WorldCollectionDatabaseLoader.log.error("Failed to load from persistence key: " + this.collectionName);
            return false;
        }
        final List<PersistableTemplate> persistableTemplates = new LinkedList<PersistableTemplate>();
        List<PersistableTemplate> list = (List<PersistableTemplate>)persistEntity.getProperty("static_objects");
        if (list != null) {
            persistableTemplates.addAll(list);
        }
        list = (List<PersistableTemplate>)persistEntity.getProperty("marker_objects");
        if (list != null) {
            persistableTemplates.addAll(list);
        }
        for (final PersistableTemplate persistableTemplate : persistableTemplates) {
            Template overrideTemplate = null;
            overrideTemplate = persistableTemplate.toTemplate();
            final String name = (String)overrideTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME);
            overrideTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instance.getOid());
            if (this.worldLoaderOverride.adjustObjectTemplate(this.collectionName, name, overrideTemplate)) {
                final OID objOid = ObjectManagerClient.generateObject(-1, "BaseTemplate", overrideTemplate);
                if (objOid != null) {
                    WorldManagerClient.spawn(objOid);
                    WorldCollectionDatabaseLoader.log.debug("WorldCollectionDatabaseLoader: generated and spawned oid " + objOid);
                    WorldCollectionDatabaseLoader.log.debug("WorldCollectionDatabaseLoader: obj name=" + name + ", map=" + persistableTemplate.getPropMap().get("NS.level_editor"));
                }
                else {
                    WorldCollectionDatabaseLoader.log.error("Could not create object=" + name);
                }
            }
        }
        return true;
    }
    
    public static String generateWorldCollectionFile(final String collectionName) {
        final DocumentBuilder docBuilder = XMLHelper.makeDocBuilder();
        final Document doc = docBuilder.newDocument();
        final Element worldObjectCollection = doc.createElement("WorldObjectCollection");
        worldObjectCollection.setAttribute("Version", "2");
        buildWorldCollectionXml(collectionName, worldObjectCollection);
        doc.appendChild(worldObjectCollection);
        final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + XMLHelper.toXML(worldObjectCollection);
        WorldCollectionDatabaseLoader.log.info("XML for " + collectionName + ":\n" + xml);
        return xml;
    }
    
    private static boolean buildWorldCollectionXml(final String collectionName, final Node node) {
        final OID persistOid = Engine.getDatabase().getOidByName(collectionName, Namespace.INSTANCE);
        if (persistOid == null) {
            WorldCollectionDatabaseLoader.log.error("Failed to load from persistence key: " + collectionName);
            return false;
        }
        final Entity persistEntity = Engine.getDatabase().loadEntity(persistOid, Namespace.INSTANCE);
        if (persistEntity == null) {
            WorldCollectionDatabaseLoader.log.error("Failed to load from persistence key: " + collectionName);
            return false;
        }
        final List<PersistableTemplate> persistableTemplates = new LinkedList<PersistableTemplate>();
        List<PersistableTemplate> list = (List<PersistableTemplate>)persistEntity.getProperty("static_objects");
        if (list != null) {
            persistableTemplates.addAll(list);
        }
        list = (List<PersistableTemplate>)persistEntity.getProperty("marker_objects");
        if (list != null) {
            persistableTemplates.addAll(list);
        }
        final Document doc = node.getOwnerDocument();
        for (final PersistableTemplate persistableTemplate : persistableTemplates) {
            Template template = null;
            template = persistableTemplate.toTemplate();
            final String name = (String)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME);
            final ObjectType objType = (ObjectType)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE);
            if (objType.isA(Marker.OBJECT_TYPE)) {
                final Element childNode = doc.createElement("Waypoint");
                childNode.setAttribute("Name", name);
                final Point pos = (Point)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC);
                final Element posNode = doc.createElement("Position");
                posNode.setAttribute("x", Float.toString(pos.getX()));
                posNode.setAttribute("y", Float.toString(pos.getY()));
                posNode.setAttribute("z", Float.toString(pos.getZ()));
                childNode.appendChild(posNode);
                final Quaternion orient = (Quaternion)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT);
                final Element orientNode = doc.createElement("Orientation");
                orientNode.setAttribute("x", Float.toString(orient.getX()));
                orientNode.setAttribute("y", Float.toString(orient.getY()));
                orientNode.setAttribute("z", Float.toString(orient.getZ()));
                orientNode.setAttribute("w", Float.toString(orient.getW()));
                childNode.appendChild(orientNode);
                node.appendChild(childNode);
            }
            else {
                if (!objType.isA(ObjectTypes.structure)) {
                    continue;
                }
                final Element childNode = doc.createElement("StaticObject");
                childNode.setAttribute("Name", name);
                final DisplayContext dispContext = (DisplayContext)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT);
                childNode.setAttribute("Mesh", dispContext.getMeshFile());
                if (dispContext.getSubmeshes().size() > 0) {
                    final Element submeshesNode = doc.createElement("SubMeshes");
                    for (final DisplayContext.Submesh submesh : dispContext.getSubmeshes()) {
                        final Element submeshInfoNode = doc.createElement("SubMeshInfo");
                        submeshInfoNode.setAttribute("Name", submesh.name);
                        submeshInfoNode.setAttribute("MaterialName", submesh.material);
                        submeshInfoNode.setAttribute("Show", "True");
                        submeshesNode.appendChild(submeshInfoNode);
                    }
                    childNode.appendChild(submeshesNode);
                }
                final Point pos2 = (Point)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC);
                final Element posNode2 = doc.createElement("Position");
                posNode2.setAttribute("x", Float.toString(pos2.getX()));
                posNode2.setAttribute("y", Float.toString(pos2.getY()));
                posNode2.setAttribute("z", Float.toString(pos2.getZ()));
                childNode.appendChild(posNode2);
                final Quaternion orient2 = (Quaternion)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT);
                final Element orientNode2 = doc.createElement("Orientation");
                orientNode2.setAttribute("x", Float.toString(orient2.getX()));
                orientNode2.setAttribute("y", Float.toString(orient2.getY()));
                orientNode2.setAttribute("z", Float.toString(orient2.getZ()));
                orientNode2.setAttribute("w", Float.toString(orient2.getW()));
                childNode.appendChild(orientNode2);
                final AOVector scale = (AOVector)template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_SCALE);
                final Element scaleNode = doc.createElement("Scale");
                scaleNode.setAttribute("x", Float.toString(scale.getX()));
                scaleNode.setAttribute("y", Float.toString(scale.getY()));
                scaleNode.setAttribute("z", Float.toString(scale.getZ()));
                childNode.appendChild(scaleNode);
                node.appendChild(childNode);
            }
        }
        return true;
    }
    
    static {
        log = new Logger("WorldCollectionDatabaseLoader");
    }
}
