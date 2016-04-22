// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import atavism.server.messages.OIDNamespaceMessage;
import com.app.server.atavism.server.objects.Template;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutionException;
import java.util.HashSet;
import java.util.concurrent.Future;
import java.util.Set;
import atavism.msgsys.ResponseMessage;
import atavism.msgsys.ExceptionResponseMessage;
import javax.management.JMException;
import javax.management.ObjectName;
import atavism.server.messages.PropertyMessage;
import com.app.server.atavism.server.objects.EntityManager;
import atavism.server.messages.INamespaceMessage;
import com.app.server.atavism.server.objects.Entity;
import java.io.Serializable;
import atavism.msgsys.Filter;
import com.app.server.atavism.server.plugins.ObjectManagerClient;
import atavism.server.messages.NamespaceFilter;
import java.util.ArrayList;
import java.util.List;
import atavism.msgsys.Message;
import java.util.Iterator;
import atavism.msgsys.IFilter;
import atavism.msgsys.MessageTypeFilter;
import com.app.server.atavism.server.util.AORuntimeException;
import com.app.server.atavism.server.util.LockFactory;
import java.util.Collections;
import java.util.HashMap;
import atavism.server.messages.INamespaceFilter;
import atavism.msgsys.MessageType;
import atavism.server.util.ObjectLockManager;
import java.util.concurrent.locks.Lock;
import atavism.server.util.AOMeter;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedList;
import atavism.msgsys.MessageCallback;

public class EnginePlugin implements MessageCallback, StatusMapCallback {
	private LinkedList<PluginActivateHook> activateHookList;
	String pluginState;
	private Map<String, String> pluginStateMap;
	Collection<Namespace> localNamespaces;
	AOMeter engPluginMeter;
	private Long subObjectDepReadySub;
	private Lock depLock;
	Map<OID, Map<Namespace, Hook>> depsOutstanding;
	private MessageCallback messageHandler;
	private HookManager hookManager;
	private Map<Namespace, LoadHook> loadHookMap;
	private Map<Namespace, UnloadHook> unloadHookMap;
	private Map<Namespace, DeleteHook> deleteHookMap;
	private String name;
	private String pluginType;
	private String pluginInfo;
	private int percentCPULoad;
	private boolean pluginAvailable;
	protected Lock lock;
	private ObjectLockManager objLockManager;
	public static MessageType MSG_TYPE_PLUGIN_STATE;
	public static MessageType MSG_TYPE_GET_PROPERTY;
	public static MessageType MSG_TYPE_GET_PROPERTY_NAMES;
	public static MessageType MSG_TYPE_SET_PROPERTY;
	public static MessageType MSG_TYPE_SET_PROPERTY_NONBLOCK;
	protected static Lock dumpAllThreadSubscriptionLock;
	protected static Long dumpAllThreadSubscription;
	protected static Long pluginStateSubscription;
	protected static Long subObjectSubscription;
	protected static Long selectionSubscription;
	protected static INamespaceFilter selectionFilter;
	protected static Long saveSubObjectSubscription;
	protected static Long loadSubObjectSubscription;
	protected static Long unloadSubObjectSubscription;
	protected static Long deleteSubObjectSubscription;
	protected static Long setSubObjectPersistenceSubscription;
	protected static Long propertySubscription;
	public static MessageType MSG_TYPE_DUMP_ALL_THREAD_STACKS;
	public static final MessageType MSG_TYPE_TRANSFER_OBJECT;

	public EnginePlugin() {
		this.activateHookList = new LinkedList<PluginActivateHook>();
		this.pluginState = "Unknown";
		this.pluginStateMap = Collections.synchronizedMap(new HashMap<String, String>());
		this.localNamespaces = null;
		this.engPluginMeter = new AOMeter("EnginePluginOnMessageMeter");
		this.subObjectDepReadySub = null;
		this.depLock = LockFactory.makeLock("subObjectDepReadySub");
		this.depsOutstanding = Collections.synchronizedMap(new HashMap<OID, Map<Namespace, Hook>>());
		this.messageHandler = new DebugPoolMessageHandler();
		this.hookManager = new HookManager();
		this.loadHookMap = Collections.synchronizedMap(new HashMap<Namespace, LoadHook>());
		this.unloadHookMap = Collections.synchronizedMap(new HashMap<Namespace, UnloadHook>());
		this.deleteHookMap = Collections.synchronizedMap(new HashMap<Namespace, DeleteHook>());
		this.name = null;
		this.percentCPULoad = 0;
		this.pluginAvailable = false;
		this.lock = LockFactory.makeLock("EnginePluginLock");
		this.objLockManager = new ObjectLockManager();
	}

	public EnginePlugin(final String name) {
		this.activateHookList = new LinkedList<PluginActivateHook>();
		this.pluginState = "Unknown";
		this.pluginStateMap = Collections.synchronizedMap(new HashMap<String, String>());
		this.localNamespaces = null;
		this.engPluginMeter = new AOMeter("EnginePluginOnMessageMeter");
		this.subObjectDepReadySub = null;
		this.depLock = LockFactory.makeLock("subObjectDepReadySub");
		this.depsOutstanding = Collections.synchronizedMap(new HashMap<OID, Map<Namespace, Hook>>());
		this.messageHandler = new DebugPoolMessageHandler();
		this.hookManager = new HookManager();
		this.loadHookMap = Collections.synchronizedMap(new HashMap<Namespace, LoadHook>());
		this.unloadHookMap = Collections.synchronizedMap(new HashMap<Namespace, UnloadHook>());
		this.deleteHookMap = Collections.synchronizedMap(new HashMap<Namespace, DeleteHook>());
		this.name = null;
		this.percentCPULoad = 0;
		this.pluginAvailable = false;
		this.lock = LockFactory.makeLock("EnginePluginLock");
		this.objLockManager = new ObjectLockManager();
		this.setName(name);
	}

	public EnginePlugin(final String name, final PluginActivateHook activateHook) {
		this(name);
		try {
			this.registerActivateHook(activateHook);
		} catch (AORuntimeException e) {
			throw new RuntimeException("registerActivateHook failed", e);
		}
	}

	public String getName() {
		return this.name;
	}

	protected void setName(final String name) {
		this.name = name;
	}

	public void setPluginType(final String pluginType) {
		this.pluginType = pluginType;
	}

	public String getPluginType() {
		return this.pluginType;
	}

	@Override
	public Map<String, String> getStatusMap() {
		return null;
	}

	public String getPluginStatus() {
		return Engine.makeStringFromMap(this.getStatusMap());
	}

	public void setPluginInfo(final String pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public String getPluginInfo() {
		return this.pluginInfo;
	}

	public void setPercentCPULoad(final int percentCPULoad) {
		this.percentCPULoad = percentCPULoad;
	}

	public int getPercentCPULoad() {
		return this.percentCPULoad;
	}

	public void registerActivateHook(final PluginActivateHook hook) {
		this.lock.lock();
		try {
			this.activateHookList.add(hook);
		} finally {
			this.lock.unlock();
		}
	}

	public void activate() {
		if (Log.loggingDebug) {
			Log.debug("EnginePlugin.activate: plugin=" + this.getName());
		}
		this.pluginState = "Starting";
		EnginePlugin.dumpAllThreadSubscriptionLock.lock();
		try {
			if (EnginePlugin.dumpAllThreadSubscription == null) {
				final MessageTypeFilter filter = new MessageTypeFilter();
				filter.addType(EnginePlugin.MSG_TYPE_DUMP_ALL_THREAD_STACKS);
				EnginePlugin.dumpAllThreadSubscription = Engine.getAgent().createSubscription(filter, this);
				if (Log.loggingDebug) {
					Log.debug("EnginePlugin.activate: plugin=" + this.getName() + ", created createSubscription for dumpAllStacks");
				}
				this.getHookManager().addHook(EnginePlugin.MSG_TYPE_DUMP_ALL_THREAD_STACKS, new DumpAllStacksMessageHook());
				if (Log.loggingDebug) {
					Log.debug("EnginePlugin.activate: registered DumpAllStacksMessageHook");
				}
			}
		} finally {
			EnginePlugin.dumpAllThreadSubscriptionLock.unlock();
		}
		Engine.getAgent().getDomainClient().awaitPluginDependents(this.getPluginType(), this.getName());
		this.pluginAvailable = true;
		this.onActivate();
		if (Log.loggingDebug) {
			Log.debug("EnginePlugin.activate: plugin=" + this.getName() + ", onActivate complete, calling activateHooks");
		}
		this.createManagementObject();
		this.lock.lock();
		try {
			for (final PluginActivateHook activateHook : this.activateHookList) {
				activateHook.activate();
			}
		} finally {
			this.lock.unlock();
		}
		if (Log.loggingDebug) {
			Log.debug("EnginePlugin.activate: plugin=" + this.getName() + ", activate hooks called");
		}
		if (this.pluginAvailable) {
			Engine.getAgent().getDomainClient().pluginAvailable(this.getPluginType(), this.getName());
		}
	}

	public boolean getPluginAvailable() {
		return this.pluginAvailable;
	}

	public void setPluginAvailable(final boolean avail) {
		if (!this.pluginAvailable && avail) {
			Engine.getAgent().getDomainClient().pluginAvailable(this.getPluginType(), this.getName());
		}
		this.pluginAvailable = avail;
	}

	public void onActivate() {
	}

	public String getPluginState(final String pluginName) {
		return this.pluginStateMap.get(pluginName);
	}

	protected void setMessageHandler(final MessageCallback handler) {
		this.messageHandler = handler;
	}

	protected MessageCallback getMessageHandler() {
		return this.messageHandler;
	}

	public void createSubscription(final Hook hook, final MessageType msgType, final int flags) {
		this.getHookManager().addHook(msgType, hook);
		final MessageTypeFilter filter = new MessageTypeFilter(msgType);
		Engine.getAgent().createSubscription(filter, this, flags);
	}

	@Override
	public void handleMessage(final Message msg, final int flags) {
		if (this.messageHandler != null) {
			this.messageHandler.handleMessage(msg, flags);
		} else {
			this.handleMessageImpl(msg, flags);
		}
	}

	protected void handleMessageImpl(final Message msg, final int flags) {
		final MessageType msgType = msg.getMsgType();
		final List<Hook> hooks = this.hookManager.getHooks(msgType);
		if (Log.loggingDebug) {
			Log.debug("EnginePlugin.handleMessage: got msg id " + msg.getMsgId() + ", matching " + hooks.size() + " hooks for msgtype " + msgType);
		}
		long timer = 0L;
		if (Log.loggingDebug) {
			timer = System.currentTimeMillis();
		}
		for (final Hook hook : hooks) {
			if (!hook.processMessage(msg, 0)) {
				break;
			}
		}
		if (Log.loggingDebug) {
			final long elapsed = System.currentTimeMillis() - timer;
			Log.debug("EnginePlugin.handleMessage: processed msg " + msg.getMsgId() + ", type=" + msgType + ", time in ms=" + elapsed);
			this.engPluginMeter.add(elapsed);
		}
	}

	public Collection<Namespace> getPluginNamespaces() {
		this.lock.lock();
		try {
			return new ArrayList<Namespace>(this.localNamespaces);
		} finally {
			this.lock.unlock();
		}
	}

	public void registerPluginNamespace(final Namespace namespace, final GenerateSubObjectHook genSubObjHook) {
		final List<Namespace> namespaces = new LinkedList<Namespace>();
		namespaces.add(namespace);
		this.registerPluginNamespaces(namespaces, genSubObjHook, null, null, null, null, null, null);
	}

	public void registerPluginNamespaces(final Collection<Namespace> namespaces, final GenerateSubObjectHook genSubObjHook) {
		this.registerPluginNamespaces(namespaces, genSubObjHook, null, null, null, null, null, null);
	}

	public void registerPluginNamespaces(final Collection<Namespace> namespaces, final GenerateSubObjectHook genSubObjHook, final INamespaceFilter selectionFilter,
			final INamespaceFilter subObjectFilter) {
		this.registerPluginNamespaces(namespaces, genSubObjHook, null, null, null, null, selectionFilter, subObjectFilter);
	}

	public void registerPluginNamespaces(final Collection<Namespace> namespaces, final GenerateSubObjectHook genSubObjHook, Hook loadSubObjHook, Hook saveSubObjHook, Hook unloadSubObjHook,
			Hook deleteSubObjHook, INamespaceFilter selectionFilter, INamespaceFilter subObjectFilter) {
		if (Log.loggingDebug) {
			String s = "";
			for (final Namespace namespace : namespaces) {
				if (s != "") {
					s += ",";
				}
				s += namespace;
			}
			Log.debug("EnginePlugin.registerPluginNamespaces: namespaces " + s);
		}
		this.lock.lock();
		try {
			this.localNamespaces = new ArrayList<Namespace>(namespaces);
			if (saveSubObjHook == null) {
				saveSubObjHook = new SaveSubObjHook(this);
			}
			if (loadSubObjHook == null) {
				loadSubObjHook = new LoadSubObjHook(this);
			}
			if (unloadSubObjHook == null) {
				unloadSubObjHook = new UnloadSubObjHook(this);
			}
			if (deleteSubObjHook == null) {
				deleteSubObjHook = new DeleteSubObjHook(this);
			}
			if (selectionFilter == null) {
				selectionFilter = new NamespaceFilter();
			}
			if (subObjectFilter == null) {
				subObjectFilter = new NamespaceFilter();
			}
			this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_GENERATE_SUB_OBJECT, genSubObjHook);
			this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_SET_SUBPERSISTENCE, new SubPersistenceHook());
			this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_SAVE_SUBOBJECT, saveSubObjHook);
			this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_LOAD_SUBOBJECT, loadSubObjHook);
			this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_UNLOAD_SUBOBJECT, unloadSubObjHook);
			this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_DELETE_SUBOBJECT, deleteSubObjHook);
			this.getHookManager().addHook(EnginePlugin.MSG_TYPE_GET_PROPERTY, new GetPropertyHook());
			this.getHookManager().addHook(EnginePlugin.MSG_TYPE_GET_PROPERTY_NAMES, new GetPropertyNamesHook());
			this.getHookManager().addHook(EnginePlugin.MSG_TYPE_SET_PROPERTY, new SetPropertyHook());
			this.getHookManager().addHook(EnginePlugin.MSG_TYPE_SET_PROPERTY_NONBLOCK, new SetPropertyHook());
			selectionFilter.addType(ObjectManagerClient.MSG_TYPE_GENERATE_SUB_OBJECT);
			selectionFilter.addType(ObjectManagerClient.MSG_TYPE_LOAD_SUBOBJECT);
			selectionFilter.setNamespaces(namespaces);
			EnginePlugin.selectionFilter = selectionFilter;
			EnginePlugin.selectionSubscription = Engine.getAgent().createSubscription(selectionFilter, this, 8);
			subObjectFilter.addType(ObjectManagerClient.MSG_TYPE_SAVE_SUBOBJECT);
			subObjectFilter.addType(ObjectManagerClient.MSG_TYPE_UNLOAD_SUBOBJECT);
			subObjectFilter.addType(ObjectManagerClient.MSG_TYPE_DELETE_SUBOBJECT);
			subObjectFilter.addType(ObjectManagerClient.MSG_TYPE_SET_SUBPERSISTENCE);
			subObjectFilter.addType(EnginePlugin.MSG_TYPE_GET_PROPERTY);
			subObjectFilter.addType(EnginePlugin.MSG_TYPE_GET_PROPERTY_NAMES);
			subObjectFilter.addType(EnginePlugin.MSG_TYPE_SET_PROPERTY);
			subObjectFilter.setNamespaces(namespaces);
			EnginePlugin.subObjectSubscription = Engine.getAgent().createSubscription(subObjectFilter, this, 8);
			final NamespaceFilter propertyNonblockFilter = new NamespaceFilter(namespaces);
			propertyNonblockFilter.addType(EnginePlugin.MSG_TYPE_SET_PROPERTY_NONBLOCK);
			EnginePlugin.propertySubscription = Engine.getAgent().createSubscription(propertyNonblockFilter, this);
		} finally {
			this.lock.unlock();
		}
	}

	public void registerSaveHook(final Namespace namespace, final SaveHook saveHook) {
		Engine.getPersistenceManager().registerSaveHook(namespace, saveHook);
	}

	public void registerLoadHook(final Namespace namespace, final LoadHook loadHook) {
		this.loadHookMap.put(namespace, loadHook);
	}

	public void registerUnloadHook(final Namespace namespace, final UnloadHook unloadHook) {
		this.unloadHookMap.put(namespace, unloadHook);
	}

	public void registerDeleteHook(final Namespace namespace, final DeleteHook deleteHook) {
		this.deleteHookMap.put(namespace, deleteHook);
	}

	public void registerTransferHook(final Filter filter, final Hook hook) {
		this.getHookManager().addHook(EnginePlugin.MSG_TYPE_TRANSFER_OBJECT, hook);
		Engine.getAgent().createSubscription(filter, this);
	}

	public boolean transferObject(final HashMap<String, Serializable> propMap, final Entity entity) {
		final TransferObjectMessage transferMessage = new TransferObjectMessage(propMap, entity);
		return Engine.getAgent().sendRPCReturnBoolean(transferMessage);
	}

	public HookManager getHookManager() {
		return this.hookManager;
	}

	protected ObjectLockManager getObjectLockManager() {
		return this.objLockManager;
	}

	private void addToDepsOutstanding(final OID oid, final Namespace namespace, final Hook callback) {
		Map<Namespace, Hook> deps = this.depsOutstanding.get(oid);
		if (deps == null) {
			deps = new HashMap<Namespace, Hook>();
			this.depsOutstanding.put(oid, deps);
		}
		final Hook previousHook = deps.get(namespace);
		if (previousHook != null) {
			Log.error("EnginePlugin.addToNamespaceDeps: for oid " + oid + " and namespace " + namespace + ", hook already exists");
		} else {
			deps.put(namespace, callback);
			this.logDepsOutstanding("addToDepsOutstanding", oid, namespace);
		}
	}

	private Hook removeFromDepsOutstanding(final OID oid, final Namespace namespace) {
		this.logDepsOutstanding("removeFromDepsOutstanding", oid, namespace);
		final Map<Namespace, Hook> deps = this.depsOutstanding.get(oid);
		if (deps == null) {
			Log.error("EnginePlugin.removeFromDepsOutstanding: Map<Namespace, Hook> for oid " + oid + " not found");
		} else {
			final Hook hook = deps.remove(namespace);
			if (hook != null) {
				if (deps.size() == 0) {
					deps.remove(oid);
				}
				return hook;
			}
			Log.error("EnginePlugin.removeFromDepsOutstanding: for oid " + oid + ", namespace " + namespace + ", deps do not contain Hook");
		}
		return null;
	}

	public void sendSubObjectResponse(final Message origMsg, final OID oid, final Namespace oidNamespace) {
		if (Log.loggingDebug) {
			Log.debug("EnginePlugin.sendSubObjectResponse: origMsg=" + origMsg + ", oid=" + oid + ", namespace " + oidNamespace);
		}
		this.sendSubObjectResponse(origMsg, oid, oidNamespace, (LinkedList<Namespace>) null, null);
	}

	public void sendSubObjectResponse(final Message origMsg, final OID oid, final Namespace oidNamespace, final Namespace depNamespace, final Hook callback) {
		if (Log.loggingDebug) {
			Log.debug("EnginePlugin.sendSubObjectResponse: origMsg=" + origMsg + ", oid=" + oid + ", oidNamespace " + oidNamespace + ", depNs=" + depNamespace);
		}
		final LinkedList<Namespace> deps = new LinkedList<Namespace>();
		deps.add(depNamespace);
		this.sendSubObjectResponse(origMsg, oid, oidNamespace, deps, callback);
	}

	public void sendSubObjectResponse(final Message msg, final OID oid, final Namespace oidNamespace, final LinkedList<Namespace> depNamespaces, final Hook callback) {
		final INamespaceMessage origMsg = (INamespaceMessage) msg;
		if (Log.loggingDebug) {
			Log.debug("EnginePlugin.sendSubObjectResponse: origMsg=" + origMsg + ", oid=" + oid + ", depNs=" + depNamespaces + ", hook=" + callback);
		}
		this.depLock.lock();
		try {
			if (Log.loggingDebug) {
				Log.debug("sendSubObjectResponse: oid=" + oid);
			}
			if (depNamespaces != null && !depNamespaces.isEmpty()) {
				if (Log.loggingDebug) {
					Log.debug("sendSubObjectResponse: oid=" + oid + ", depNamespaces=" + depNamespaces.toString());
				}
				if (this.subObjectDepReadySub == null) {
					if (Log.loggingDebug) {
						Log.debug("sendSubObjectResponse: oid=" + oid + ", need to create deps ready sub");
					}
					this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_SUB_OBJECT_DEPS_READY, new SubObjectDepsReadyHook());
					final Filter namespaceFilter = new NamespaceFilter(ObjectManagerClient.MSG_TYPE_SUB_OBJECT_DEPS_READY, this.getPluginNamespaces());
					this.subObjectDepReadySub = Engine.getAgent().createSubscription(namespaceFilter, this, 8);
					Log.debug("sendSubObjectResponse: created depsReady sub");
				} else {
					Log.debug("sendSubObjectResponse: depsReady sub already in place");
				}
				if (callback == null) {
					Log.error("EnginePlugin.sendSubObjectResponse: callback is null");
				}
				this.addToDepsOutstanding(oid, oidNamespace, callback);
			}
			final Namespace namespace = origMsg.getNamespace();
			if (namespace == null) {
				Log.error("EnginePlugin.sendSubObjectResponse: namespace is null");
			}
			final Entity subObj = EntityManager.getEntityByNamespace(oid, namespace);
			if (subObj == null) {
				Log.error("EnginePlugin.sendSubObjectResponse: subObj is null for oid " + oid);
			}
			if (Log.loggingDebug) {
				Log.debug("EnginePlugin.sendSubObjectResponse: set entity " + subObj.getOid() + " namespace to " + namespace);
			}
		} finally {
			this.depLock.unlock();
		}
		Engine.getAgent().sendObjectResponse(msg, depNamespaces);
	}

	protected void logDepsOutstanding(final String prefix, final OID oid, final Namespace ns) {
		if (Log.loggingDebug) {
			String s = "";
			final Map<Namespace, Hook> deps = this.depsOutstanding.get(oid);
			if (deps == null) {
				s = "None";
			} else {
				for (final Namespace dep : deps.keySet()) {
					if (s != "") {
						s += ",";
					}
					s += dep.getName();
				}
			}
			Log.debug(prefix + ": masterOid " + oid + ", namespace " + ns + ", deps " + s);
		}
	}

	protected boolean getPropertyImpl(final GetPropertyMessage msg) {
		final Namespace ns = msg.getNamespace();
		final OID oid = msg.getSubject();
		final List<Serializable> vals = new LinkedList<Serializable>();
		final Entity e = EntityManager.getEntityByNamespace(oid, ns);
		if (e == null) {
			Log.error("EnginePlugin.GetPropertyHook: could not find subobj for oid " + oid + " in namespace " + ns);
			Engine.getAgent().sendObjectResponse(msg, null);
			return false;
		}
		final List<String> keys = msg.getKeys();
		for (final String key : keys) {
			vals.add(e.getProperty(key));
		}
		Engine.getAgent().sendObjectResponse(msg, vals);
		if (Log.loggingDebug) {
			String s = "";
			for (int i = 0; i < keys.size(); ++i) {
				final String key2 = keys.get(i);
				final Serializable val = vals.get(i);
				if (s != "") {
					s += ",";
				}
				s = s + key2 + "=" + val;
			}
			Log.debug("EnginePlugin.GetPropertyHook: sent response, oid=" + oid + ", " + s);
		}
		return true;
	}

	protected boolean getPropertyNamesImpl(final GetPropertyNamesMessage msg) {
		final Namespace ns = msg.getNamespace();
		final OID oid = msg.getSubject();
		final List<Serializable> vals = new LinkedList<Serializable>();
		final Entity e = EntityManager.getEntityByNamespace(oid, ns);
		if (e == null) {
			Log.error("EnginePlugin.GetPropertyHook: could not find subobj for oid " + oid);
			Engine.getAgent().sendObjectResponse(msg, null);
			return false;
		}
		final Map<String, Serializable> propMap = e.getPropertyMap();
		vals.addAll(propMap.keySet());
		Engine.getAgent().sendObjectResponse(msg, vals);
		if (Log.loggingDebug) {
			String s = "";
			for (int i = 0; i < vals.size(); ++i) {
				final Serializable val = vals.get(i);
				if (s != "") {
					s += ",";
				}
				s += val;
			}
			Log.debug("EnginePlugin.GetPropertyHook: sent response, oid=" + oid + ", " + s);
		}
		return true;
	}

	protected boolean setPropertyImpl(final SetPropertyMessage msg) {
		final OID oid = msg.getSubject();
		final Namespace ns = msg.getNamespace();
		String s = "";
		if (Log.loggingDebug) {
			for (final Map.Entry<String, Serializable> entry : msg.getPropMap().entrySet()) {
				final String key = entry.getKey();
				final Serializable val = entry.getValue();
				if (s != "") {
					s += ",";
				}
				s = s + key + "=" + val;
			}
			if (Log.loggingDebug) {
				Log.debug("EnginePlugin.setPropertyImpl: oid=" + oid + " props " + s);
			}
		}
		final boolean reqResp = msg.getRequestResponse();
		List<Serializable> previousVals = null;
		if (reqResp) {
			previousVals = new LinkedList<Serializable>();
		}
		final Entity entity = EntityManager.getEntityByNamespace(oid, ns);
		if (entity == null) {
			for (final Map.Entry<String, Serializable> entry2 : msg.getPropMap().entrySet()) {
				final String key2 = entry2.getKey();
				Log.error("EnginePlugin.setPropertyImpl: could not find obj / subobj for oid " + oid + ", namespace " + ns + ", key=" + key2);
			}
			if (reqResp) {
				Engine.getAgent().sendObjectResponse(msg, previousVals);
			}
			return false;
		}
		final PropertyMessage propMsg = new PropertyMessage(oid);
		for (final Map.Entry<String, Serializable> entry3 : msg.getPropMap().entrySet()) {
			final String key3 = entry3.getKey();
			final Serializable newValue = entry3.getValue();
			final Serializable previousValue = entity.setProperty(key3, newValue);
			propMsg.setProperty(key3, newValue);
			if (reqResp) {
				previousVals.add(previousValue);
			}
		}
		final Iterator i$3 = msg.getRemovedProperties().iterator();
		while (i$3.hasNext()) {
			final String key2 = i$3.next();
			final Serializable previousValue2 = entity.removeProperty(key2);
			propMsg.removeProperty(key2);
			if (reqResp) {
				previousVals.add(previousValue2);
			}
		}
		if (reqResp) {
			Engine.getAgent().sendObjectResponse(msg, previousVals);
		}
		Engine.getAgent().sendBroadcast(propMsg);
		Engine.getPersistenceManager().setDirty(entity);
		return true;
	}

	public static Serializable getObjectProperty(final OID oid, final Namespace namespace, final String key) {
		final GetPropertyMessage msg = new GetPropertyMessage(oid, namespace, key);
		final List<Serializable> vals = (List<Serializable>) Engine.getAgent().sendRPCReturnObject(msg);
		if (vals == null) {
			return null;
		}
		return vals.get(0);
	}

	public static List<Serializable> getObjectProperties(final OID oid, final Namespace namespace, final List<String> keys) {
		final GetPropertyMessage msg = new GetPropertyMessage(oid, namespace, keys);
		return (List<Serializable>) Engine.getAgent().sendRPCReturnObject(msg);
	}

	public static List<Serializable> getObjectProperties(final OID oid, final Namespace namespace, final String... keys) {
		final List<String> list = new LinkedList<String>();
		for (final String s : keys) {
			list.add(s);
		}
		return getObjectProperties(oid, namespace, list);
	}

	public static List<String> getObjectPropertyNames(final OID oid, final Namespace namespace) {
		final GetPropertyNamesMessage msg = new GetPropertyNamesMessage(oid, namespace);
		return (List<String>) Engine.getAgent().sendRPCReturnObject(msg);
	}

	public static Serializable setObjectProperty(final OID oid, final Namespace namespace, final String key, final Serializable value) {
		final SetPropertyMessage msg = new SetPropertyMessage(oid, namespace, key, value, true);
		return (Serializable) Engine.getAgent().sendRPCReturnObject(msg);
	}

	public static void setObjectPropertyNoResponse(final OID oid, final Namespace namespace, final String key, final Serializable value) {
		final SetPropertyMessage msg = new SetPropertyMessage(oid, namespace, key, value, false);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static List<Serializable> setObjectProperties(final OID oid, final Namespace namespace, final Map<String, Serializable> propMap) {
		return setObjectProperties(oid, namespace, propMap, new LinkedList<String>());
	}

	public static List<Serializable> setObjectProperties(final OID oid, final Namespace namespace, final Map<String, Serializable> propMap, final Collection<String> removedProps) {
		final SetPropertyMessage msg = new SetPropertyMessage(oid, namespace, propMap, removedProps, true);
		return (List<Serializable>) Engine.getAgent().sendRPCReturnObject(msg);
	}

	public static List<Serializable> setObjectProperties(final OID oid, final Namespace namespace, final Serializable... keysAndValues) {
		final Map<String, Serializable> propMap = processKeysAndValues("setObjectProperties", keysAndValues);
		if (propMap == null) {
			return new LinkedList<Serializable>();
		}
		return setObjectProperties(oid, namespace, propMap, new LinkedList<String>());
	}

	public static void setObjectPropertiesNoResponse(final OID oid, final Namespace namespace, final Map<String, Serializable> propMap) {
		setObjectPropertiesNoResponse(oid, namespace, propMap, new LinkedList<String>());
	}

	public static void setObjectPropertiesNoResponse(final OID oid, final Namespace namespace, final Map<String, Serializable> propMap, final Collection<String> removedProps) {
		final SetPropertyMessage msg = new SetPropertyMessage(oid, namespace, propMap, removedProps, false);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void setObjectPropertiesNoResponse(final OID oid, final Namespace namespace, final Serializable... keysAndValues) {
		final Map<String, Serializable> propMap = processKeysAndValues("setObjectPropertiesNoResponse", keysAndValues);
		if (propMap == null) {
			return;
		}
		setObjectPropertiesNoResponse(oid, namespace, propMap, new LinkedList<String>());
	}

	protected static Map<String, Serializable> processKeysAndValues(final String what, final Serializable[] keysAndValues) {
		final int len = keysAndValues.length;
		if ((len & 0x1) != 0x0) {
			Log.dumpStack("Odd number of args to " + what);
			return null;
		}
		if (len == 0) {
			return null;
		}
		final Map<String, Serializable> propMap = new HashMap<String, Serializable>();
		for (int i = 0; i < len; i += 2) {
			propMap.put((String) keysAndValues[i], keysAndValues[i + 1]);
		}
		return propMap;
	}

	private void createManagementObject() {
		if (Engine.getManagementAgent() == null) {
			Log.debug("Engine.getManagementAgent returned null");
			return;
		}
		final Object mbean = this.createMBeanInstance();
		if (mbean == null) {
			Log.debug("EnginePlugin.createMBeanInstance returned null");
			return;
		}
		try {
			final ObjectName name = new ObjectName("net.atavism:plugin=" + this.getName());
			Engine.getManagementAgent().registerMBean(mbean, name);
			Log.debug("Registered " + this.getName() + " with JMX management agent");
		} catch (JMException ex) {
			Log.exception("createManagementObject: exception in registerMBean", ex);
		}
	}

	protected Object createMBeanInstance() {
		return null;
	}

	static {
		EnginePlugin.MSG_TYPE_PLUGIN_STATE = MessageType.intern("ao.PLUGIN_STATE");
		EnginePlugin.MSG_TYPE_GET_PROPERTY = MessageType.intern("ao.GET_PROPERTY");
		EnginePlugin.MSG_TYPE_GET_PROPERTY_NAMES = MessageType.intern("ao.GET_PROPERTY_NAMES");
		EnginePlugin.MSG_TYPE_SET_PROPERTY = MessageType.intern("ao.SET_PROPERTY");
		EnginePlugin.MSG_TYPE_SET_PROPERTY_NONBLOCK = MessageType.intern("ao.SET_PROPERTY_NONBLOCK");
		EnginePlugin.dumpAllThreadSubscriptionLock = LockFactory.makeLock("DumpAllThreadsLock");
		EnginePlugin.dumpAllThreadSubscription = null;
		EnginePlugin.pluginStateSubscription = null;
		EnginePlugin.subObjectSubscription = null;
		EnginePlugin.selectionSubscription = null;
		EnginePlugin.selectionFilter = null;
		EnginePlugin.saveSubObjectSubscription = null;
		EnginePlugin.loadSubObjectSubscription = null;
		EnginePlugin.unloadSubObjectSubscription = null;
		EnginePlugin.deleteSubObjectSubscription = null;
		EnginePlugin.setSubObjectPersistenceSubscription = null;
		EnginePlugin.propertySubscription = null;
		EnginePlugin.MSG_TYPE_DUMP_ALL_THREAD_STACKS = MessageType.intern("ao.DUMP_ALL_THREAD_STACKS");
		MSG_TYPE_TRANSFER_OBJECT = MessageType.intern("ao.TRANSFER_OBJECT");
	}

	class PluginStateMessageHook implements Hook {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean processMessage(final Message m, final int flags) {
			final PluginStateMessage msg = (PluginStateMessage) m;
			if (Log.loggingDebug) {
				Log.debug("PluginStateHook: got plugin status message for plugin: " + msg.getPluginName() + ", state=" + msg.getState() + ", msg=" + m);
			}
			EnginePlugin.this.pluginStateMap.put(msg.getPluginName(), msg.getState());
			if (msg.getTargetSession() == null) {
				if (EnginePlugin.this.pluginState == "Available") {
					Log.debug("PluginStateHook: not a response msg, sending out a response");
					final PluginStateMessage respMsg = new PluginStateMessage(EnginePlugin.this.getName(), EnginePlugin.this.pluginState);
					respMsg.setTargetSession(msg.getSenderName());
					respMsg.setTargetPluginName(EnginePlugin.this.name);
					Engine.getAgent().sendBroadcast(respMsg);
				}
			} else {
				Log.debug("PluginStatusHook: is response message");
			}
			return true;
		}
	}

	class DumpAllStacksMessageHook implements Hook {
		@Override
		public boolean processMessage(final Message m, final int flags) {
			if (Log.loggingDebug) {
				Log.debug("DumpAllStacksMessageHook: received MSG_TYPE_DUMP_ALL_STACKS");
			}
			final StringBuilder sb = new StringBuilder();
			Engine.dumpAllThreadStacks(sb, true);
			Log.info(sb.toString());
			return true;
		}
	}

	class QueuedMessage implements Runnable {
		Message message;
		int flags;

		QueuedMessage(final Message message, final int flags) {
			this.message = message;
			this.flags = flags;
		}

		@Override
		public void run() {
			try {
				EnginePlugin.this.handleMessageImpl(this.message, this.flags);
			} catch (Exception ex) {
				Log.exception("handleMessageImpl", ex);
				if ((this.flags & 0x1) != 0x0) {
					Engine.getAgent().sendResponse(new ExceptionResponseMessage(this.message, ex));
				}
			}
		}
	}

	class DebugPoolMessageHandler extends PoolMessageHandler {
		private Set<Future<?>> workers;
		private long lastThreadDump;
		private final long THREAD_DUMP_TIMER = 30000L;

		DebugPoolMessageHandler() {
			this.workers = new HashSet<Future<?>>();
			this.lastThreadDump = 0L;
		}

		@Override
		public void handleMessage(final Message msg, final int flags) {
			if (this.pruneWorkers()) {
				Thread.yield();
			}
			if (this.pruneWorkers()) {
				final long now = System.currentTimeMillis();
				if (this.lastThreadDump == 0L || now - this.lastThreadDump > 30000L) {
					this.lastThreadDump = now;
					Log.info("Exhausted thread worker pool for PoolMessageHandler");
					final StringBuilder sb = new StringBuilder();
					Engine.dumpAllThreadStacks(sb, true);
					Log.info(sb.toString());
				}
			}
			synchronized (this.workers) {
				final Future<?> future = this.executor.submit(new QueuedMessage(msg, flags));
				this.workers.add(future);
			}
		}

		private boolean pruneWorkers() {
			synchronized (this.workers) {
				final Iterator<Future<?>> iterator = this.workers.iterator();
				while (iterator.hasNext()) {
					final Future<?> worker = iterator.next();
					if (worker.isDone()) {
						try {
							worker.get();
						} catch (InterruptedException e) {
							Log.warn("Encountered InterruptedException on worker that was done: " + e);
						} catch (ExecutionException e2) {
							Log.error("Encountered ExecutionException on worker that was done: " + e2);
						}
						iterator.remove();
					}
				}
				return this.workers.size() >= 10;
			}
		}
	}

	class PoolMessageHandler implements MessageCallback, ThreadFactory {
		protected final int MAX_THREADS = 10;
		protected ExecutorService executor;
		private int threadCount;

		PoolMessageHandler() {
			this.threadCount = 1;
			this.executor = Executors.newFixedThreadPool(10, this);
		}

		@Override
		public void handleMessage(final Message msg, final int flags) {
			this.executor.execute(new QueuedMessage(msg, flags));
		}

		@Override
		public Thread newThread(final Runnable runnable) {
			return new Thread(runnable, EnginePlugin.this.getName() + "-" + this.threadCount++);
		}
	}

	public abstract static class TransferFilter extends MessageTypeFilter {
		public TransferFilter() {
			this.addType(EnginePlugin.MSG_TYPE_TRANSFER_OBJECT);
		}

		@Override
		public boolean matchRemaining(final Message msg) {
			if (msg instanceof TransferObjectMessage) {
				final TransferObjectMessage transferMsg = (TransferObjectMessage) msg;
				final Map propMap = transferMsg.getPropMap();
				return this.matchesMap(propMap, msg);
			}
			return false;
		}

		public abstract boolean matchesMap(final Map p0, final Message p1);
	}

	class SubPersistenceHook implements Hook {
		@Override
		public boolean processMessage(final Message m, final int flags) {
			final ObjectManagerClient.SetPersistenceMessage msg = (ObjectManagerClient.SetPersistenceMessage) m;
			final OID masterOid = msg.getSubject();
			final Namespace namespace = msg.getNamespace();
			final Boolean persistVal = msg.getPersistVal();
			if (Log.loggingDebug) {
				Log.debug("SubPersistenceHook: masterOid=" + masterOid + ", namespace=" + namespace);
			}
			final Entity subObj = EntityManager.getEntityByNamespace(masterOid, namespace);
			if (subObj == null) {
				Log.error("SubPersistenceHook: could not find sub object");
				Engine.getAgent().sendBooleanResponse(m, false);
			}
			if (subObj.getPersistenceFlag() == persistVal) {
				Engine.getAgent().sendBooleanResponse(m, true);
				return true;
			}
			subObj.setPersistenceFlag(persistVal);
			if (Log.loggingDebug) {
				Log.debug("SubPersistenceHook: masterOid=" + masterOid + ", set persist flag on subOid" + subObj.getOid() + ", to val=" + persistVal);
			}
			if (persistVal) {
				Engine.getPersistenceManager().setDirty(subObj);
				Log.debug("SubPersistenceHook: set subobject dirty");
			}
			if (!persistVal) {
				Engine.getDatabase().deleteObjectData(masterOid, namespace);
			}
			Engine.getAgent().sendBooleanResponse(m, true);
			return true;
		}
	}

	public abstract static class GenerateSubObjectHook implements Hook {
		public EnginePlugin plugin;

		public GenerateSubObjectHook(final EnginePlugin plugin) {
			this.plugin = null;
			this.plugin = plugin;
		}

		@Override
		public boolean processMessage(final Message m, final int flags) {
			final ObjectManagerClient.GenerateSubObjectMessage msg = (ObjectManagerClient.GenerateSubObjectMessage) m;
			final Template template = msg.getTemplate();
			final Namespace namespace = msg.getNamespace();
			final OID masterOid = msg.getSubject();
			if (masterOid == null) {
				Log.error("GenerateSubObjectHook: no master oid");
				return false;
			}
			final SubObjData subObjData = this.generateSubObject(template, namespace, masterOid);
			final Entity newObj = EntityManager.getEntityByNamespace(masterOid, namespace);
			if (newObj == null) {
				throw new RuntimeException("could not find newly created subobject, oid=" + masterOid + ", namespace " + namespace);
			}
			this.plugin.sendSubObjectResponse(m, masterOid, namespace, subObjData.namespaces, subObjData.dependencyHook);
			return true;
		}

		public abstract SubObjData generateSubObject(final Template p0, final Namespace p1, final OID p2);
	}

	public static class SubObjData {
		public Hook dependencyHook;
		LinkedList<Namespace> namespaces;

		public SubObjData() {
			this.dependencyHook = null;
			this.namespaces = null;
		}

		public SubObjData(final Namespace namespace, final Hook dependencyHook) {
			this.dependencyHook = null;
			this.namespaces = null;
			this.namespaces = new LinkedList<Namespace>();
			if (namespace != null) {
				this.namespaces.add(namespace);
			}
			this.dependencyHook = dependencyHook;
		}

		public SubObjData(final LinkedList<Namespace> namespaces, final Hook dependencyHook) {
			this.dependencyHook = null;
			this.namespaces = null;
			if (namespaces != null) {
				this.namespaces = new LinkedList<Namespace>(namespaces);
			} else {
				this.namespaces = new LinkedList<Namespace>();
			}
			this.dependencyHook = dependencyHook;
		}
	}

	public static class SaveSubObjHook implements Hook {
		EnginePlugin pluginRef;

		public SaveSubObjHook(final EnginePlugin plugin) {
			this.pluginRef = null;
			this.pluginRef = plugin;
		}

		@Override
		public boolean processMessage(final Message m, final int flags) {
			final OIDNamespaceMessage msg = (OIDNamespaceMessage) m;
			final OID masterOid = msg.getSubject();
			final Namespace namespace = msg.getNamespace();
			final Entity subObj = EntityManager.getEntityByNamespace(masterOid, namespace);
			if (subObj == null) {
				Log.error("SaveSubObjHook: could not find sub object for masterOid " + masterOid);
				Engine.getAgent().sendBooleanResponse(m, false);
				return true;
			}
			if (!subObj.getPersistenceFlag()) {
				Log.error("SaveSubObjHook: ignoring save of non-persistent sub-object oid=" + masterOid + " namespace=" + namespace);
				Engine.getAgent().sendBooleanResponse(m, false);
				return true;
			}
			if (Log.loggingDebug) {
				Log.debug("SaveSubObjHook: saving object, subOid=" + subObj.getOid());
			}
			Engine.getPersistenceManager().persistEntity(subObj);
			if (Log.loggingDebug) {
				Log.debug("SaveSubObjHook: saved object subOid=" + subObj.getOid());
			}
			Engine.getAgent().sendBooleanResponse(m, true);
			return true;
		}
	}

	public static class LoadSubObjHook implements Hook {
		EnginePlugin pluginRef;

		public LoadSubObjHook(final EnginePlugin plugin) {
			this.pluginRef = null;
			this.pluginRef = plugin;
		}

		@Override
		public boolean processMessage(final Message m, final int flags) {
			final ObjectManagerClient.LoadSubObjectMessage msg = (ObjectManagerClient.LoadSubObjectMessage) m;
			final OID masterOid = msg.getSubject();
			final Namespace namespace = msg.getNamespace();
			Entity entity = null;
			try {
				entity = Engine.getDatabase().loadEntity(masterOid, namespace);
			} catch (AORuntimeException e) {
				Engine.getAgent().sendBooleanResponse(msg, false);
				return false;
			}
			if (entity == null) {
				Log.error("LoadSubObjHook: no such entity with oid " + masterOid + " and namespace " + namespace);
				Engine.getAgent().sendBooleanResponse(msg, false);
				return false;
			}
			EntityManager.registerEntityByNamespace(entity, namespace);
			final LoadHook loadHook = this.pluginRef.loadHookMap.get(namespace);
			if (loadHook != null) {
				loadHook.onLoad(entity);
			}
			if (Log.loggingDebug) {
				Log.debug("LoadSubObjHook: called loadhook, loaded object oid " + masterOid + " and namespace " + namespace);
			}
			Engine.getAgent().sendBooleanResponse(m, true);
			return true;
		}
	}

	public static class UnloadSubObjHook implements Hook {
		EnginePlugin plugin;

		public UnloadSubObjHook(final EnginePlugin plugin) {
			this.plugin = plugin;
		}

		@Override
		public boolean processMessage(final Message m, final int flags) {
			final ObjectManagerClient.UnloadSubObjectMessage msg = (ObjectManagerClient.UnloadSubObjectMessage) m;
			final OID oid = msg.getSubject();
			final Namespace namespace = msg.getNamespace();
			final Entity entity = EntityManager.getEntityByNamespace(oid, namespace);
			if (entity == null) {
				Log.error("UnloadSubObjectMessage: no such entity oid=" + oid + " ns=" + namespace);
				Engine.getAgent().sendBooleanResponse(msg, false);
				return false;
			}
			final boolean rc = EntityManager.removeEntityByNamespace(oid, namespace);
			if (rc) {
				final UnloadHook unloadHook = this.plugin.unloadHookMap.get(namespace);
				if (unloadHook != null) {
					try {
						unloadHook.onUnload(entity);
					} catch (Exception e) {
						Log.exception("UnloadHook.onUnload oid=" + oid + " " + unloadHook.getClass().getName() + "", e);
					}
				}
			}
			if (Log.loggingDebug) {
				Log.debug("UnloadSubObjectMessage: unloaded oid=" + oid + " ns=" + namespace + " result=" + rc);
			}
			if (entity.getPersistenceFlag() && Engine.getPersistenceManager().isDirty(entity)) {
				Engine.getPersistenceManager().persistEntity(entity);
			}
			Engine.getAgent().sendBooleanResponse(msg, rc);
			return true;
		}
	}

	public static class DeleteSubObjHook implements Hook {
		EnginePlugin plugin;

		public DeleteSubObjHook(final EnginePlugin plugin) {
			this.plugin = plugin;
		}

		@Override
		public boolean processMessage(final Message m, final int flags) {
			final ObjectManagerClient.DeleteSubObjectMessage msg = (ObjectManagerClient.DeleteSubObjectMessage) m;
			final OID oid = msg.getSubject();
			final Namespace namespace = msg.getNamespace();
			final DeleteHook deleteHook = this.plugin.deleteHookMap.get(namespace);
			final Entity entity = EntityManager.getEntityByNamespace(oid, namespace);
			if (entity == null) {
				Log.error("DeleteSubObjectMessage: no such entity oid=" + oid + " ns=" + namespace);
				if (deleteHook != null) {
					try {
						deleteHook.onDelete(oid, namespace);
					} catch (Exception e) {
						Log.exception("DeleteHook.onDelete oid=" + oid + " ns=" + namespace + " " + deleteHook.getClass().getName(), e);
					}
				}
				Engine.getAgent().sendBooleanResponse(msg, false);
				return false;
			}
			entity.getLock().lock();
			try {
				if (entity.isDeleted()) {
					Log.debug("DeleteSubObjectMessage: already deleted oid=" + oid);
					return true;
				}
				entity.setDeleted();
			} finally {
				entity.getLock().unlock();
			}
			final boolean rc = EntityManager.removeEntityByNamespace(oid, namespace);
			if (rc && deleteHook != null) {
				try {
					deleteHook.onDelete(entity);
				} catch (Exception e2) {
					Log.exception("DeleteHook.onDelete oid=" + oid + " " + deleteHook.getClass().getName(), e2);
				}
			}
			if (Log.loggingDebug) {
				Log.debug("DeleteSubObjectMessage: deleted oid=" + oid + " ns=" + namespace + " result=" + rc);
			}
			Engine.getAgent().sendBooleanResponse(msg, rc);
			return true;
		}
	}

	class SubObjectDepsReadyHook implements Hook {
		@Override
		public boolean processMessage(final Message m, final int flags) {
			final ObjectManagerClient.SubObjectDepsReadyMessage msg = (ObjectManagerClient.SubObjectDepsReadyMessage) m;
			final OID masterOid = msg.getSubject();
			final Namespace ns = msg.getNamespace();
			EnginePlugin.this.logDepsOutstanding("SubObjectDepsReadyHook.processMessage", masterOid, ns);
			final Hook cb = EnginePlugin.this.removeFromDepsOutstanding(masterOid, ns);
			if (cb == null) {
				Log.error("SubObjectDepsReadyHook: the sub oid was not in the wait list: " + masterOid + ", namespace " + ns);
				return false;
			}
			cb.processMessage(msg, 0);
			Engine.getAgent().sendBooleanResponse(m, Boolean.TRUE);
			return true;
		}
	}

	public static class GetPropertyMessage extends OIDNamespaceMessage {
		List<String> keys;
		private static final long serialVersionUID = 1L;

		public GetPropertyMessage() {
			this.keys = new LinkedList<String>();
		}

		public GetPropertyMessage(final OID oid, final Namespace namespace, final String key) {
			super(EnginePlugin.MSG_TYPE_GET_PROPERTY, oid, namespace);
			this.keys = new LinkedList<String>();
			this.addKey(key);
		}

		public GetPropertyMessage(final OID oid, final Namespace namespace, final List<String> keys) {
			super(EnginePlugin.MSG_TYPE_GET_PROPERTY, oid, namespace);
			this.keys = new LinkedList<String>();
			this.keys = keys;
		}

		@Override
		public String toString() {
			String s = "";
			for (final Serializable key : this.keys) {
				if (s != "") {
					s += ",";
				}
				s += key;
			}
			return "[GetPropertyMessage oid=" + this.getSubject() + ", keys=" + s + ", super=" + super.toString() + "]";
		}

		public void addKey(final String key) {
			if (!this.keys.contains(key)) {
				this.keys.add(key);
			}
		}

		public Serializable removeKey(final Serializable key) {
			return this.keys.remove(key);
		}

		public List<String> getKeys() {
			return this.keys;
		}
	}

	public static class GetPropertyNamesMessage extends OIDNamespaceMessage {
		private static final long serialVersionUID = 1L;

		public GetPropertyNamesMessage() {
		}

		public GetPropertyNamesMessage(final OID oid, final Namespace namespace) {
			super(EnginePlugin.MSG_TYPE_GET_PROPERTY_NAMES, oid, namespace);
		}

		@Override
		public String toString() {
			return "[GetPropertyNamesMessage oid=" + this.getSubject() + ", super=" + super.toString() + "]";
		}
	}

	public static class SetPropertyMessage extends OIDNamespaceMessage {
		private boolean reqResponse;
		Map<String, Serializable> propMap;
		Collection<String> removedProperties;
		private static final long serialVersionUID = 1L;

		public SetPropertyMessage() {
			this.reqResponse = false;
			this.propMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
		}

		public SetPropertyMessage(final OID oid, final Namespace namespace, final String key, final Serializable val, final boolean reqResponse) {
			super(EnginePlugin.MSG_TYPE_SET_PROPERTY, oid, namespace);
			this.reqResponse = false;
			this.propMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
			if (!reqResponse) {
				this.setMsgType(EnginePlugin.MSG_TYPE_SET_PROPERTY_NONBLOCK);
			}
			this.propMap.put(key, val);
			this.setRequestResponse(reqResponse);
		}

		public SetPropertyMessage(final OID oid, final Namespace namespace, final Map<String, Serializable> propMap, final Collection<String> removedProps, final boolean reqResponse) {
			super(EnginePlugin.MSG_TYPE_SET_PROPERTY, oid, namespace);
			this.reqResponse = false;
			this.propMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
			if (!reqResponse) {
				this.setMsgType(EnginePlugin.MSG_TYPE_SET_PROPERTY_NONBLOCK);
			}
			this.propMap = propMap;
			this.removedProperties = removedProps;
			this.setRequestResponse(reqResponse);
		}

		public Serializable get(final String key) {
			return this.getProperty(key);
		}

		public Serializable getProperty(final String key) {
			return this.propMap.get(key);
		}

		public boolean containsKey(final String key) {
			return this.propMap.containsKey(key);
		}

		public void put(final String key, final Serializable val) {
			this.setProperty(key, val);
		}

		public Serializable setProperty(final String key, final Serializable val) {
			final Serializable rv = this.propMap.put(key, val);
			this.removedProperties.remove(key);
			return rv;
		}

		public void removeProperty(final String key) {
			this.propMap.remove(key);
			this.removedProperties.add(key);
		}

		public Map<String, Serializable> getPropMap() {
			return this.propMap;
		}

		public Collection<String> getRemovedProperties() {
			return this.removedProperties;
		}

		public void setRequestResponse(final boolean val) {
			this.reqResponse = val;
			if (this.reqResponse) {
				this.setMsgType(EnginePlugin.MSG_TYPE_SET_PROPERTY);
			} else {
				this.setMsgType(EnginePlugin.MSG_TYPE_SET_PROPERTY_NONBLOCK);
			}
		}

		public boolean getRequestResponse() {
			return this.reqResponse;
		}
	}

	class GetPropertyHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final GetPropertyMessage rMsg = (GetPropertyMessage) msg;
			return EnginePlugin.this.getPropertyImpl(rMsg);
		}
	}

	class GetPropertyNamesHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final GetPropertyNamesMessage rMsg = (GetPropertyNamesMessage) msg;
			return EnginePlugin.this.getPropertyNamesImpl(rMsg);
		}
	}

	class SetPropertyHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final SetPropertyMessage rMsg = (SetPropertyMessage) msg;
			return EnginePlugin.this.setPropertyImpl(rMsg);
		}
	}

	public static class PluginStateMessage extends Message {
		private String pluginName;
		private String state;
		private String targetPluginName;
		private String sessionId;
		public static final String BuiltInStateAvailable = "Available";
		public static final String BuiltInStateUnknown = "Unknown";
		public static final String BuiltInStateStarting = "Starting";
		private static final long serialVersionUID = 1L;

		public PluginStateMessage() {
			this.setMsgType(EnginePlugin.MSG_TYPE_PLUGIN_STATE);
		}

		public PluginStateMessage(final String pluginName, final String state) {
			this.setMsgType(EnginePlugin.MSG_TYPE_PLUGIN_STATE);
			this.setPluginName(pluginName);
			this.setState(state);
		}

		public void setPluginName(final String pluginName) {
			this.pluginName = pluginName;
		}

		public String getPluginName() {
			return this.pluginName;
		}

		public void setState(final String state) {
			this.state = state;
		}

		public String getState() {
			return this.state;
		}

		public void setTargetSession(final String sessionId) {
			this.sessionId = sessionId;
		}

		public String getTargetSession() {
			return this.sessionId;
		}

		public void setTargetPluginName(final String pluginName) {
			this.targetPluginName = pluginName;
		}

		public String getTargetPluginName() {
			return this.targetPluginName;
		}
	}

	public static class TransferObjectMessage extends Message {
		private HashMap<String, Serializable> propMap;
		private Entity entity;
		private static final long serialVersionUID = 1L;

		public TransferObjectMessage() {
			super(EnginePlugin.MSG_TYPE_TRANSFER_OBJECT);
		}

		public TransferObjectMessage(final HashMap<String, Serializable> propMap, final Entity entity) {
			super(EnginePlugin.MSG_TYPE_TRANSFER_OBJECT);
			this.setPropMap(propMap);
			this.setEntity(entity);
		}

		public HashMap<String, Serializable> getPropMap() {
			return this.propMap;
		}

		public void setPropMap(final HashMap<String, Serializable> propMap) {
			this.propMap = propMap;
		}

		public Entity getEntity() {
			return this.entity;
		}

		public void setEntity(final Entity entity) {
			this.entity = entity;
		}
	}

	public interface DeleteHook {
		void onDelete(final Entity p0);

		void onDelete(final OID p0, final Namespace p1);
	}

	public interface UnloadHook {
		void onUnload(final Entity p0);
	}

	public interface LoadHook {
		void onLoad(final Entity p0);
	}

	public interface SaveHook {
		void onSave(final Entity p0, final Namespace p1);
	}

	public interface PluginActivateHook {
		void activate();
	}
}
