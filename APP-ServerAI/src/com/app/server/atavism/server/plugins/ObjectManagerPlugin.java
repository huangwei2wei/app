// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.app.server.atavism.agis.plugins.AgisWorldManagerPlugin;
import com.app.server.atavism.server.engine.EnginePlugin;
import com.app.server.atavism.server.engine.Manager;
import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.objects.AOObject;
import com.app.server.atavism.server.objects.Entity;
import com.app.server.atavism.server.objects.EntityManager;
import com.app.server.atavism.server.objects.ObjectType;
import com.app.server.atavism.server.objects.Template;

public class ObjectManagerPlugin {
	private static ObjectManagerPlugin objectManagerPlugin = new ObjectManagerPlugin();
	private static final int INSTANCE_OK = 0;
	private static final int INSTANCE_LOADING = 1;
	private static final int INSTANCE_UNLOADING = 2;
	private static final int INSTANCE_DELETING = 3;
	public static int nextFreeTemplateID = -2;
	public static String ITEM_TEMPLATE = "item";;
	public static String MOB_TEMPLATE = "mob";
	protected static final Logger log = Logger.getLogger("navmesh");
	protected HashMap<String, Manager<Template>> templateManager;
	private Map<OID, InstanceState> instanceContent;
	private AgisWorldManagerPlugin agisWorldManagerPlugin;

	public static ObjectManagerPlugin getObjectManagerPlugin() {
		return objectManagerPlugin;
	}

	public ObjectManagerPlugin() {
		this.templateManager = new HashMap<String, Manager<Template>>();
		this.instanceContent = new HashMap<OID, InstanceState>();
		this.registerTemplate(new Template("BaseTemplate", -1, "BaseTemplate"));
	}

	private void addInstance(final MasterObject instance) {
		final InstanceState instanceState = new InstanceState(instance);
		synchronized (this.instanceContent) {
			final InstanceState previous = this.instanceContent.put(instance.getOid(), instanceState);
			if (previous != null) {
				log.error("addInstance: duplicate instance [OLD " + previous + "] [NEW " + instanceState + "]");
			}
		}
		log.debug("addInstance: added instanceOid=" + instance.getOid());
	}

	private void removeInstance(final MasterObject instance) {
		synchronized (this.instanceContent) {
			final InstanceState instanceState = this.instanceContent.get(instance.getOid());
			if (instanceState == null) {
				log.error("removeInstance: unknown instanceOid=" + instance.getOid());
				return;
			}
			if (instanceState.entities.size() > 0) {
				log.warn("removeInstance: wrong state: " + instanceState);
			}
			this.instanceContent.remove(instance.getOid());
		}
		log.debug("removeInstance: removed instanceOid=" + instance.getOid());
	}

	private void addInstanceContent(final OID instanceOid, final MasterObject entity) {
		log.debug("addInstanceContent: instanceOid=" + instanceOid + " oid=" + entity.getOid());
		synchronized (this.instanceContent) {
			final InstanceState instanceState = this.instanceContent.get(instanceOid);
			if (instanceState == null) {
				log.error("addInstanceContent: unknown instanceOid=" + instanceOid + " for " + entity);
				return;
			}
			instanceState.entities.add(entity);
		}
	}

	private void removeInstanceContent(final OID instanceOid, final MasterObject entity) {
		synchronized (this.instanceContent) {
			final InstanceState instanceState = this.instanceContent.get(instanceOid);
			if (instanceState == null) {
				log.error("removeInstanceContent: unknown instanceOid=" + instanceOid);
				return;
			}
			log.debug("removeInstanceContent: instanceOid=" + instanceOid + " oid=" + entity.getOid() + " count=" + instanceState.entities.size());
			instanceState.entities.remove(entity);
		}
	}

	private boolean isInstanceOk(final OID instanceOid, final int newStatus) {
		synchronized (this.instanceContent) {
			final InstanceState instanceState = this.instanceContent.get(instanceOid);
			if (instanceState != null) {
				final boolean result = instanceState.status == 0;
				if (result && newStatus != -1) {
					instanceState.status = newStatus;
				}
				return result;
			}
			return false;
		}
	}

	private boolean isInstanceLoading(final OID instanceOid) {
		synchronized (this.instanceContent) {
			final InstanceState instanceState = this.instanceContent.get(instanceOid);
			return instanceState != null && (instanceState.status == 0 || instanceState.status == 1);
		}
	}

	private void setInstanceStatus(final OID instanceOid, final int newStatus) {
		synchronized (this.instanceContent) {
			final InstanceState instanceState = this.instanceContent.get(instanceOid);
			instanceState.status = newStatus;
		}
	}

	private OID getInstanceNamedObject(final OID instanceOid, final String name, final ObjectType objectType) {
		synchronized (this.instanceContent) {
			final InstanceState instanceState = this.instanceContent.get(instanceOid);
			if (instanceState == null) {
				return null;
			}
			if (objectType != null) {
				for (final MasterObject entity : instanceState.entities) {
					final String entityName = entity.getName();
					if (entity.getType() == objectType && entityName != null && entityName.equals(name)) {
						return entity.getOid();
					}
				}
			} else {
				for (final MasterObject entity : instanceState.entities) {
					final String entityName = entity.getName();
					if (entityName != null && entityName.equals(name)) {
						return entity.getOid();
					}
				}
			}
			return null;
		}
	}

	private OID getNamedObject(final String name, final ObjectType objectType) {
		final Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.OBJECT_MANAGER);
		if (objectType != null) {
			for (final Entity entity : entities) {
				final String entityName = entity.getName();
				if (entity.getType() == objectType && entityName != null && entityName.equals(name)) {
					return entity.getOid();
				}
			}
		} else {
			for (final Entity entity : entities) {
				final String entityName = entity.getName();
				if (entityName != null && entityName.equals(name)) {
					return entity.getOid();
				}
			}
		}
		return null;
	}

	private List<OID> getMatchingObjects(final OID oid, final String name, final ObjectType objectType, final Map<Namespace, Map<String, Serializable>> filters) {
		final List<OID> rv = new LinkedList<OID>();
		final Collection<Entity> entities = new HashSet<Entity>();
		if (oid == null) {
			final Entity[] arr$;
			final Entity[] entityArray = arr$ = EntityManager.getAllEntitiesByNamespace(Namespace.OBJECT_MANAGER);
			for (final Entity entity : arr$) {
				entities.add(entity);
			}
		} else {
			final InstanceState instanceState = this.instanceContent.get(oid);
			if (instanceState == null) {
				return rv;
			}
			for (final Entity entity2 : instanceState.entities) {
				entities.add(entity2);
			}
		}
		for (final Entity entity3 : entities) {
			if (name != null) {
				final String entityName = entity3.getName();
				if (entityName == null) {
					continue;
				}
				if (!entityName.equals(name)) {
					continue;
				}
			}
			if (objectType != null && !entity3.getType().isA(objectType)) {
				continue;
			}
			boolean possibleMatch = true;
			if (filters != null) {
				for (final Map.Entry<Namespace, Map<String, Serializable>> namespaceEntry : filters.entrySet()) {
					final Namespace namespace = namespaceEntry.getKey();
					final Map<String, Serializable> namespaceFilters = namespaceEntry.getValue();
					final List<String> keys = new LinkedList<String>();
					final List<Serializable> filterValues = new LinkedList<Serializable>();
					for (final Map.Entry<String, Serializable> filter : namespaceFilters.entrySet()) {
						keys.add(filter.getKey());
						filterValues.add(filter.getValue());
					}
					final List<Serializable> objectValues = EnginePlugin.getObjectProperties(entity3.getOid(), namespace, keys);
					if (objectValues == null) {
						possibleMatch = false;
						break;
					}
					for (int i = 0; i < keys.size(); ++i) {
						final String key = keys.get(i);
						final Serializable filterVal = filterValues.get(i);
						final Serializable val = objectValues.get(i);
						if (filterVal != null || val != null) {
							if ((filterVal != null && val == null) || (filterVal == null && val != null) || !filterVal.equals(val)) {
								possibleMatch = false;
								break;
							}
						}
					}
					if (!possibleMatch) {
						break;
					}
				}
			}
			if (!possibleMatch) {
				continue;
			}
			rv.add(entity3.getOid());
		}
		return rv;
	}

	/**
	 * 模板注册
	 * 
	 * @param tmpl
	 * @return
	 */
	public boolean registerTemplate(final Template tmpl) {
		final String templateType = tmpl.getTemplateType();
		if (!this.templateManager.containsKey(templateType)) {
			this.templateManager.put(templateType, new Manager<Template>(templateType + "Manager"));
		}
		return this.templateManager.get(templateType).register(tmpl.getTemplateID(), tmpl);
	}

	public static int getNextFreeTemplateID() {
		return ObjectManagerPlugin.nextFreeTemplateID--;
	}

	/**
	 * 生成对象(怪物，人物
	 * 
	 * @param templateID
	 * @param templateType
	 * @param overrideTemplate
	 * @return
	 */
	public OID generateObject(int templateID, String templateType, Template overrideTemplate) {
		log.debug("ANDREW: getting template: " + templateID + " of templateType: " + templateType);
		final Template template = ObjectManagerPlugin.this.templateManager.get(templateType).get(templateID);
		if (template == null) {
			log.error("template not found: " + templateType + ":" + templateID);
			return null;
		}
		final Set<Namespace> namespaces22 = template.getNamespaces();
		Template finalTemplate;
		if (overrideTemplate != null) {
			finalTemplate = template.merge(overrideTemplate);
		} else {
			finalTemplate = template;
		}
		final Set<Namespace> namespaces33 = finalTemplate.getNamespaces();
		Boolean persistent = (Boolean) finalTemplate.get(Namespace.OBJECT_MANAGER, ":persistent");
		if (persistent == null) {
			persistent = false;
		}
		ObjectManagerPlugin.log.debug("GenerateObjectHook: generating entity: " + finalTemplate.getName() + ", template=" + finalTemplate);
		System.out.println(finalTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME));
		String entityName = (String) finalTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME);
		if (entityName == null) {
			entityName = finalTemplate.getName();
		}
		final OID instanceOid = (OID) finalTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE);
		final ObjectType objectType = (ObjectType) finalTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE);
		final MasterObject masterObj = new MasterObject(entityName);
		masterObj.setPersistenceFlag(persistent);
		masterObj.setInstanceOid(instanceOid);
		if (objectType != null) {
			masterObj.setType(objectType);
		}
		final Map<String, Serializable> objMgrProps = finalTemplate.getSubMap(Namespace.OBJECT_MANAGER);
		if (objMgrProps != null) {
			for (final Map.Entry<String, Serializable> entry : objMgrProps.entrySet()) {
				if (!entry.getKey().startsWith(":")) {
					masterObj.setProperty(entry.getKey(), entry.getValue());
				}
			}
		}
		EntityManager.registerEntityByNamespace(masterObj, Namespace.OBJECT_MANAGER);
		ObjectManagerPlugin.log.debug("GenerateObjectHook: created master obj: " + masterObj);
		final Set<Namespace> namespaces = finalTemplate.getNamespaces();
		namespaces.remove(Namespace.OBJECT_MANAGER);
		masterObj.setSubObjectNamespaces(namespaces);
		for (final Namespace namespace : namespaces) {
			final Template subTemplate = finalTemplate.restrict(namespace);
			subTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", persistent);
			ObjectManagerPlugin.log.debug("GenerateObjectHook: creating subobj for ns=" + namespace + ", subTemplate=" + subTemplate);
			AOObject subObjData = AgisWorldManagerPlugin.getAgisWorldManagerPlugin().generateSubObject(subTemplate, namespace, masterObj.getOid()); // 生成子对象

			// final GenericResponseMessage respMsg = ObjectManagerPlugin.this.generateSubObject(masterObj.getOid(), namespace, subTemplate);
			masterObj.addLoadedNamespace(namespace);
			// final List<Namespace> depNamespaces = (List<Namespace>) subObjData.namespaces;
			ObjectManagerPlugin.log.debug("GenerateObjectHook: created subobj for ns=" + namespace);
			// if (depNamespaces != null) {
			// if (depNamespaces.isEmpty()) {
			// continue;
			// }
			// this.depTable.put(masterObj.getOid(), namespace, depNamespaces);
			// }
		}
		return masterObj.getOid();
	}

	public static class MasterObject extends Entity {
		private transient int loadedNamespaces;
		private transient OID instanceOid;
		private static final long serialVersionUID = 1L;

		public MasterObject() {
			this.loadedNamespaces = 0;
		}

		public MasterObject(final String name) {
			super(name);
			this.loadedNamespaces = 0;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setInstanceOid(final OID instanceOid2) {
			this.instanceOid = instanceOid2;
		}

		public int getLoadedNamespaces() {
			return this.loadedNamespaces;
		}

		public void setLoadedNamespaces(final int namespaceBits) {
			this.loadedNamespaces = namespaceBits;
		}

		public void addLoadedNamespace(final Namespace namespace) {
			this.loadedNamespaces |= 1 << namespace.getNumber();
		}

		public void removeLoadedNamespace(final Namespace namespace) {
			this.loadedNamespaces &= ~(1 << namespace.getNumber());
		}

		public boolean isNamespaceLoaded(final Namespace namespace) {
			return (this.loadedNamespaces & 1 << namespace.getNumber()) != 0x0;
		}

		public boolean loadComplete() {
			return this.getSubObjectNamespacesInt() == this.loadedNamespaces;
		}
	}

	private static class InstanceState {
		public MasterObject instance;
		public int status;
		public Set<MasterObject> entities;

		public InstanceState(final MasterObject instance) {
			this.entities = new HashSet<MasterObject>();
			this.instance = instance;
			this.status = 0;
		}

		@Override
		public String toString() {
			return "instanceOid=" + this.instance.getOid() + " status=" + statusToString(this.status) + " entityCount=" + this.entities.size();
		}

		public static String statusToString(final int status) {
			if (status == 0) {
				return "OK";
			}
			if (status == 1) {
				return "LOADING";
			}
			if (status == 2) {
				return "UNLOADING";
			}
			if (status == 3) {
				return "DELETING";
			}
			return "" + status + " (unknown)";
		}
	}
}
