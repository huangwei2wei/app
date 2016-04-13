// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.objects;

//import atavism.msgsys.MessageType;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.ai.ArrivedEventMessage;
import com.app.empire.protocol.data.ai.CommandMessage;
import com.app.empire.protocol.data.ai.FollowCommandMessage;
import com.app.empire.protocol.data.ai.GotoCommandMessage;
import com.app.protocol.data.AbstractData;
import com.app.server.atavism.server.objects.EntityManager;
import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.Engine;
import com.app.server.atavism.server.plugins.WorldManagerClient;
import com.app.server.atavism.server.pathing.crowd.UpdateFlags;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.objects.EntityHandle;

import com.app.server.atavism.server.pathing.crowd.CrowdAgentParams;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.objects.AOObject;
import com.app.server.atavism.server.engine.OID;

/**
 * 绕行演员
 * 
 * @author doter
 * 
 */
public class DetourActor {
	private Logger log = Logger.getLogger("navmesh");
	OID oid;
	AOObject obj;
	AOObject target;
	Point lastTargetLoc;
	Point targetLoc;// 目标
	Point lastLoc;
	AOVector lastDir;
	Quaternion lastOrient;
	float speed;
	CrowdAgentParams params;
	InstanceNavMeshManager navMeshManager;
	private int agentId;// 唯一id
	EntityHandle followTarget;
	float distanceToFollowAt;
	protected String mode;
	protected boolean roamingBehavior;
	Long commandSub;

	public DetourActor(final OID oid, final AOObject obj) {
		this.distanceToFollowAt = 0.0f;
		this.mode = "stop";
		this.roamingBehavior = false;
		this.commandSub = null;
		this.oid = oid;
		this.obj = obj;
		final InterpolatedWorldNode node = (InterpolatedWorldNode) obj.worldNode();
		this.lastDir = node.getDir();
		this.lastLoc = node.getCurrentLoc();
		this.params = new CrowdAgentParams();
		this.params.CollisionQueryRange = 6.0f;
		this.params.Height = 2.0f;
		this.params.MaxAcceleration = 50.0f;
		this.params.MaxSpeed = 50.0f;
		this.params.ObstacleAvoidanceType = 3;
		this.params.PathOptimizationRange = 18.0f;
		this.params.Radius = 0.4f;
		this.params.SeparationWeight = 2.0f;
		this.params.UpdateFlags = UpdateFlags.None;
	}
	// 主要是广播
	public void activate() {
		// final SubjectFilter filter = new SubjectFilter(this.oid);
		// filter.addType(Behavior.MSG_TYPE_COMMAND);
		// filter.addType(WorldManagerClient.MSG_TYPE_MOB_PATH_CORRECTION);
		// this.commandSub = Engine.getAgent().createSubscription((IFilter) filter, (MessageCallback) this);
		// Engine.getAgent().sendBroadcast((Message) new BaseBehavior.ArrivedEventMessage(this.oid));// 发生广播
		// Engine.getAgent().sendBroadcast((Message) new BaseBehavior.DisableCommandMessage(this.oid));
	}

	public void deactivate() {
		// if (this.commandSub != null) {
		// Engine.getAgent().removeSubscription((long) this.commandSub);
		// this.commandSub = null;
		// }
	}

	// 处理
	public void handleMessage(AbstractData msg, final int flags) {
		CommandMessage cmdMsg = (CommandMessage) msg;
		String command = cmdMsg.getCmd();
		log.debug("DetourActor.onMessage: command = " + command + "; oid = " + this.oid);
		if (command.equals("goto")) {
			GotoCommandMessage gotoMsg = (GotoCommandMessage) msg;
			Point destination = new Point(gotoMsg.getX(), gotoMsg.getY(), gotoMsg.getZ());// gotoMsg.getDestination();
			this.mode = "goto";
			this.roamingBehavior = true;
			this.navMeshManager.setActorTarget(this.oid, destination);
			this.navMeshManager.setActorSpeed(this.oid, gotoMsg.getSpeed());
			this.targetLoc = new Point(destination.getX(), destination.getY(), destination.getZ());
		} else if (command.equals("stop")) {
			this.followTarget = null;
			InterpolatedWorldNode node = (InterpolatedWorldNode) this.obj.worldNode();
			node.setDir(new AOVector(0.0F, 0.0F, 0.0F));
			WorldManagerClient.updateWorldNode(this.oid, new BasicWorldNode(node));
			this.mode = "stop";
			if (this.roamingBehavior) {
				try {
					ArrivedEventMessage arrivedEventMessage = new ArrivedEventMessage();
					arrivedEventMessage.setOid(this.oid.getData());
					// Engine.getAgent().sendBroadcast(arrivedEventMessage);// 广播

				} catch (Exception e) {
					log.error("BaseBehavior.onMessage: Error sending ArrivedEventMessage, error was '" + e.getMessage() + "'");
					throw new RuntimeException(e);
				}
			}
		} else if (command.equals("follow")) {
			FollowCommandMessage followMsg = (FollowCommandMessage) msg;
			this.mode = "follow";
			this.target = ((AOObject) EntityManager.getEntityByNamespace(OID.fromLong(followMsg.getOid()), Namespace.WORLD_MANAGER));
			this.speed = followMsg.getSpeed().intValue();
			this.distanceToFollowAt = followMsg.getDistanceToFollowAt().floatValue();
			setupFollow(this.lastLoc);
		}

	}
	/**
	 * 建立跟踪
	 * 
	 * @param pos
	 */
	void setupFollow(final Point pos) {
		log.debug("DETOUR: setupFollow hit");
		this.lastTargetLoc = this.target.getCurrentLoc();
		float len = Point.distanceTo(this.lastTargetLoc, pos);
		if (len < this.distanceToFollowAt) {
			this.navMeshManager.resetActorTarget(this.oid);
			this.lastDir = AOVector.Zero;
			this.navMeshManager.setActorSpeed(this.oid, 0.0f);
			return;
		}
		len -= this.distanceToFollowAt;
		final AOVector newp2 = new AOVector(this.lastTargetLoc);
		newp2.sub(pos);
		newp2.normalize();
		newp2.multiply(len);
		newp2.add(pos);
		this.navMeshManager.setActorTarget(this.oid, new Point(newp2.getX(), this.lastTargetLoc.getY(), newp2.getZ()));
		this.navMeshManager.setActorSpeed(this.oid, this.speed);
		this.targetLoc = new Point(newp2.getX(), this.lastTargetLoc.getY(), newp2.getZ());
		log.debug("DETOUR: targetLoc: " + this.targetLoc + " from followLoc: " + this.lastTargetLoc + " and currentPos: " + pos);
	}
	/**
	 * 更新地址
	 * 
	 * @param dir
	 * @param pos
	 */
	public void updateDirLoc(final AOVector dir, final Point pos) {
		if (this.lastLoc == null || pos == null || this.lastDir == null) {
			log.debug("DETOUR: hit null pos or dir");
		}
		float distanceSquared = Point.distanceToSquared(this.lastLoc, pos);
		final AOVector dirDiff = AOVector.sub(dir, this.lastDir);
		if (Math.abs(dirDiff.getX()) > 0.1 || Math.abs(dirDiff.getY()) > 0.1 || Math.abs(dirDiff.getZ()) > 0.1 || distanceSquared > 36.0f) {
			log.debug("DETOUR: direction vector or position has changed for: " + this.oid + " new Dir: " + dir + ", new loc: " + pos);
			this.lastDir = dir;
			this.lastLoc = pos;
			final BasicWorldNode wnode = new BasicWorldNode();
			wnode.setInstanceOid(this.navMeshManager.getInstanceOid());
			if (this.targetLoc != null) {
				distanceSquared = Point.distanceToSquared(pos, this.targetLoc);
				log.debug("DETOUR: distanceSquared = " + distanceSquared);
				if (distanceSquared < 0.5f) {
					ArrivedEventMessage arrivedEventMessage = new ArrivedEventMessage();
					arrivedEventMessage.setOid(this.oid.getData());
					// Engine.getAgent().sendBroadcast((Message) new BaseBehavior.ArrivedEventMessage(this.oid));// 到达事件消息

					this.navMeshManager.resetActorTarget(this.oid);
					this.lastDir = AOVector.Zero;
					this.navMeshManager.setActorSpeed(this.oid, 0.0f);
					if (!this.mode.equals("follow")) {
						log.debug("DETOUR: setting behaviour to stop");
						this.targetLoc = null;
						this.mode = "stop";
					}
				}
			}
			wnode.setDir(this.lastDir);
			if (this.targetLoc != null && !this.lastDir.isZero()) {
				final float yaw = AOVector.getLookAtYaw(this.lastDir);
				(this.lastOrient = new Quaternion()).setEulerAngles(0.0f, yaw, 0.0f);
				wnode.setOrientation(this.lastOrient);
			} else if (this.lastOrient != null) {
				wnode.setOrientation(this.lastOrient);
			}
			wnode.setLoc(pos);
			WorldManagerClient.updateWorldNode(this.oid, wnode);// 更新世界节点
		}
		if (this.mode != null && this.mode.equals("follow")) {
			final Point followLoc = this.target.getCurrentLoc();
			if (followLoc != null && this.lastTargetLoc != null && Point.distanceToSquared(followLoc, this.lastTargetLoc) > 1.0f) {
				this.setupFollow(pos);
			}
		}
	}

	public CrowdAgentParams getParams() {
		return this.params;
	}

	public void addToNavMeshManager(final InstanceNavMeshManager navMeshManager, final int id) {
		this.navMeshManager = navMeshManager;
		this.agentId = id;
	}

	public OID getOid() {
		return this.oid;
	}

	public int getAgentId() {
		return this.agentId;
	}

	public void setAgentId(final int id) {
		this.agentId = id;
	}
}
