// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.objects;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.app.server.atavism.server.engine.Behavior;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.objects.ObjectFactory;
import com.app.server.atavism.server.objects.ObjectStub;
import com.app.server.atavism.server.objects.SpawnData;
/**
 * mob对象工厂
 * 
 * @author doter
 * 
 */
public class MobFactory extends ObjectFactory implements Serializable {
	private Logger log = Logger.getLogger("navmesh");
	private LinkedList<Behavior> behavs;
	private static final long serialVersionUID = 1L;

	public MobFactory(final int templateID) {
		super(templateID);
		this.behavs = new LinkedList<Behavior>();
	}

	public ObjectStub makeObject(final SpawnData spawnData, final OID instanceOid, final Point loc) {
		final ObjectStub obj = super.makeObject(spawnData, instanceOid, loc);
		log.debug("MOBFACTORY: makeObject; adding behavs: " + this.behavs);
		for (final Behavior behav : this.behavs) {
			if (!obj.getBehaviors().contains(behav)) {
				obj.addBehavior(behav);
				log.debug("MOBFACTORY: makeObject; adding behav: " + behav);
			}
		}
		return obj;
	}

	public void addBehav(final Behavior behav) {
		this.behavs.add(behav);
	}

	public void setBehavs(final LinkedList<Behavior> behavs) {
		this.behavs = behavs;
	}

	public LinkedList<Behavior> getBehavs() {
		return this.behavs;
	}
}
