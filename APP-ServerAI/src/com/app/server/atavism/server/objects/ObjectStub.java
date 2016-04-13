// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.util.Collection;
import java.util.Iterator;
import com.app.server.atavism.server.engine.EnginePlugin;
import com.app.server.atavism.server.plugins.MobManagerPlugin;
import com.app.server.atavism.server.plugins.WorldManagerClient;
import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.Namespace;
import java.util.ArrayList;
import com.app.server.atavism.server.engine.Behavior;
import java.util.List;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;

public class ObjectStub extends Entity implements EntityWithWorldNode {
	InterpolatedWorldNode node;
	int templateID;
	protected boolean spawned;
	protected List<Behavior> behaviors;
	private static final long serialVersionUID = 1L;

	public ObjectStub() {
		this.spawned = false;
		this.behaviors = new ArrayList<Behavior>();
		this.setNamespace(Namespace.MOB);
	}

	public ObjectStub(final OID objId, final InterpolatedWorldNode node, final int template) {
		this.spawned = false;
		this.behaviors = new ArrayList<Behavior>();
		this.setOid(objId);
		this.setWorldNode(node);
		this.setTemplateID(template);
	}

	@Override
	public String toString() {
		return "[ObjectStub: oid=" + this.getOid() + " node=" + this.node + "]";
	}

	@Override
	public Entity getEntity() {
		return this;
	}

	public OID getInstanceOid() {
		return this.node.getInstanceOid();
	}

	@Override
	public InterpolatedWorldNode getWorldNode() {
		return this.node;
	}

	@Override
	public void setWorldNode(final InterpolatedWorldNode node) {
		this.node = node;
	}

	@Override
	public void setDirLocOrient(final BasicWorldNode bnode) {
		if (this.node != null) {
			this.node.setDirLocOrient(bnode);
		}
	}

	public int getTemplateID() {
		return this.templateID;
	}

	public void setTemplateID(final int template) {
		this.templateID = template;
	}

	public void updateWorldNode() {
		WorldManagerClient.updateWorldNode(this.getOid(), new BasicWorldNode(this.node));
	}

	public void spawn() {
		final OID oid = this.getOid();
		MobManagerPlugin.getTracker(this.getInstanceOid()).addLocalObject(oid, (Integer) EnginePlugin.getObjectProperty(oid, Namespace.WORLD_MANAGER, "reactionRadius"));
		WorldManagerClient.spawn(oid);
		for (final Behavior behav : this.behaviors) {
			behav.activate();
		}
	}

	public void despawn() {
		this.unload();
		WorldManagerClient.despawn(this.getOid());
	}

	public void unload() {
		for (final Behavior behav : this.behaviors) {
			behav.deactivate();
		}
		final OID oid = this.getOid();
		if (MobManagerPlugin.getTracker(this.getInstanceOid()) != null) {
			MobManagerPlugin.getTracker(this.getInstanceOid()).removeLocalObject(oid);
		}
		EntityManager.removeEntityByNamespace(oid, Namespace.MOB);
	}

	public void addBehavior(final Behavior behav) {
		behav.setObjectStub(this);
		this.behaviors.add(behav);
		behav.initialize();
	}

	public void removeBehavior(final Behavior behav) {
		this.behaviors.remove(behav);
	}

	public List<Behavior> getBehaviors() {
		return new ArrayList<Behavior>(this.behaviors);
	}

	public void setBehaviors(final List<Behavior> behavs) {
		this.behaviors = new ArrayList<Behavior>(behavs);
	}
}
