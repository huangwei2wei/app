// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import com.app.server.atavism.server.pathing.PathLocAndDir;
import com.app.server.atavism.server.objects.EntityWithWorldNode;
import com.app.server.atavism.server.plugins.MobManagerPlugin;
import java.util.concurrent.TimeUnit;
import com.app.server.atavism.server.objects.ObjectStub;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.plugins.WorldManagerClient;
import com.app.server.atavism.server.util.LockFactory;
import com.app.server.atavism.server.objects.SpawnData;
import java.util.concurrent.locks.Lock;
import com.app.server.atavism.server.objects.EntityHandle;
import com.app.server.atavism.server.pathing.PathState;
import com.app.server.atavism.server.math.Point;

public class BaseBehavior extends Behavior implements Runnable {
	String pathObjectTypeName;
	Long commandSub;
	Point destLoc;
	long arriveTime;
	PathState pathState;
	EntityHandle followTarget;
	float distanceToFollowAt;
	float mobSpeed;
	boolean interpolatingPath;
	protected transient Lock lock;
	protected String mode;
	protected boolean roamingBehavior;
	protected boolean activated;
	public static final String MSG_CMD_TYPE_GOTO = "goto";
	public static final String MSG_CMD_TYPE_FOLLOW = "follow";
	public static final String MSG_CMD_TYPE_STOP = "stop";
	public static final String MSG_CMD_TYPE_DISABLE = "disable";
	public static final String MSG_EVENT_TYPE_ARRIVED = "arrived";
	private static final long serialVersionUID = 1L;

	public BaseBehavior() {
		this.pathObjectTypeName = "Generic";
		this.commandSub = null;
		this.destLoc = null;
		this.arriveTime = 0L;
		this.pathState = null;
		this.followTarget = null;
		this.distanceToFollowAt = 0.0f;
		this.mobSpeed = 0.0f;
		this.interpolatingPath = false;
		this.lock = null;
		this.mode = "stop";
		this.roamingBehavior = false;
		this.activated = false;
	}

	public BaseBehavior(final SpawnData data) {
		super(data);
		this.pathObjectTypeName = "Generic";
		this.commandSub = null;
		this.destLoc = null;
		this.arriveTime = 0L;
		this.pathState = null;
		this.followTarget = null;
		this.distanceToFollowAt = 0.0f;
		this.mobSpeed = 0.0f;
		this.interpolatingPath = false;
		this.lock = null;
		this.mode = "stop";
		this.roamingBehavior = false;
		this.activated = false;
	}

	@Override
	public void initialize() {
		this.lock = LockFactory.makeLock("BaseBehaviorLock");
		final OID oid = this.obj.getOid();
		final SubjectFilter filter = new SubjectFilter(oid);
		filter.addType(Behavior.MSG_TYPE_COMMAND);
		filter.addType(WorldManagerClient.MSG_TYPE_MOB_PATH_CORRECTION);
		this.pathState = new PathState(oid, this.pathObjectTypeName, true);
		this.commandSub = Engine.getAgent().createSubscription(filter, this);
	}

	@Override
	public void activate() {
		this.activated = true;
	}

	@Override
	public void deactivate() {
		this.lock.lock();
		try {
			this.activated = false;
			if (this.commandSub != null) {
				Engine.getExecutor().remove(this);
				Engine.getAgent().removeSubscription(this.commandSub);
				this.commandSub = null;
			}
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void handleMessage(final Message msg, final int flags) {
		try {
			this.lock.lock();
			if (!this.activated) {
				return;
			}
			if (msg.getMsgType() == Behavior.MSG_TYPE_COMMAND) {
				final CommandMessage cmdMsg = (CommandMessage) msg;
				final String command = cmdMsg.getCmd();
				Engine.getExecutor().remove(this);
				if (Log.loggingDebug) {
					Log.debug("BaseBehavior.onMessage: command = " + command + "; oid = " + this.obj.getOid() + "; name " + this.obj.getName());
				}
				if (command.equals("goto")) {
					final GotoCommandMessage gotoMsg = (GotoCommandMessage) msg;
					final Point destination = gotoMsg.getDestination();
					this.mode = "goto";
					this.roamingBehavior = true;
					this.gotoSetup(destination, gotoMsg.getSpeed());
				} else {
					if (command.equals("stop")) {
						this.followTarget = null;
						this.pathState.clear();
						this.obj.getWorldNode().setDir(new AOVector(0.0f, 0.0f, 0.0f));
						this.obj.updateWorldNode();
						this.mode = "stop";
						if (!this.roamingBehavior) {
							return;
						}
						try {
							Engine.getAgent().sendBroadcast(new ArrivedEventMessage(this.obj));
							return;
						} catch (Exception e) {
							Log.error("BaseBehavior.onMessage: Error sending ArrivedEventMessage, error was '" + e.getMessage() + "'");
							throw new RuntimeException(e);
						}
					}
					if (command.equals("follow")) {
						final FollowCommandMessage followMsg = (FollowCommandMessage) msg;
						this.mode = "follow";
						this.followSetup(followMsg.getTarget(), followMsg.getSpeed(), followMsg.getDistanceToFollowAt());
					} else if (command.equals("disable")) {
						this.deactivate();
					}
				}
			} else if (msg.getMsgType() == WorldManagerClient.MSG_TYPE_MOB_PATH_CORRECTION) {
				Engine.getExecutor().remove(this);
				this.interpolatePath();
				this.interpolatingPath = false;
			}
		} finally {
			this.lock.unlock();
		}
	}

	public void gotoSetup(final Point dest, final float speed) {
		this.destLoc = dest;
		this.mobSpeed = speed;
		final Point myLoc = this.obj.getWorldNode().getLoc();
		final OID oid = this.obj.getOid();
		if (Log.loggingDebug) {
			Log.debug("BaseBehavior.gotoSetup: oid = " + oid + "; myLoc = " + myLoc + "; dest = " + dest);
		}
		this.scheduleMe(this.setupPathInterpolator(oid, myLoc, dest, false, 0.0f, this.obj.getWorldNode().getFollowsTerrain()));
	}

	public void gotoUpdate() {
		final Point myLoc = this.obj.getWorldNode().getLoc();
		final OID oid = this.obj.getOid();
		if (this.interpolatingPath) {
			this.interpolatePath();
			if (!this.interpolatingPath) {
				Engine.getAgent().sendBroadcast(new ArrivedEventMessage(this.obj));
				if (Log.loggingDebug) {
					Log.debug("BaseBehavior.gotoUpdate sending ArrivedEventMessage: oid = " + oid + "; myLoc = " + myLoc + "; destLoc = " + this.destLoc);
				}
				this.mode = "stop";
			}
		}
		if (this.interpolatingPath) {
			this.scheduleMe(this.pathState.pathTimeRemaining());
		}
	}

	public void followSetup(final EntityHandle target, final int speed, final float distance) {
		this.followTarget = target;
		this.distanceToFollowAt = distance;
		this.mobSpeed = speed;
		final InterpolatedWorldNode node = this.obj.getWorldNode();
		final Point myLoc = node.getLoc();
		final OID oid = this.obj.getOid();
		final ObjectStub followObj = (ObjectStub) this.followTarget.getEntity(Namespace.MOB);
		final Point followLoc = followObj.getWorldNode().getLoc();
		this.destLoc = followLoc;
		this.scheduleMe(this.setupPathInterpolator(oid, myLoc, followLoc, true, this.distanceToFollowAt, node.getFollowsTerrain()));
	}

	protected void scheduleMe(final long timeToDest) {
		final long ms = Math.min(500L, timeToDest);
		Engine.getExecutor().schedule(this, ms, TimeUnit.MILLISECONDS);
	}

	public void followUpdate() {
		final ObjectStub followObj = (ObjectStub) this.followTarget.getEntity(Namespace.MOB);
		final Point followLoc = followObj.getWorldNode().getLoc();
		final InterpolatedWorldNode node = this.obj.getWorldNode();
		final Point myLoc = node.getLoc();
		final OID oid = this.obj.getOid();
		final float fdist = Point.distanceTo(followLoc, this.destLoc);
		final float dist = Point.distanceTo(followLoc, myLoc);
		if (Log.loggingDebug) {
			Log.debug("BaseBehavior.followUpdate: oid = " + oid + "; myLoc = " + myLoc + "; followLoc = " + followLoc + "; fdist = " + fdist + "; dist = " + dist);
		}
		long msToSleep = 500L;
		if (fdist > 1.0f) {
			final long msToDest = this.setupPathInterpolator(oid, myLoc, followLoc, true, this.distanceToFollowAt, node.getFollowsTerrain());
			this.destLoc = followLoc;
			msToSleep = ((msToDest == 0L) ? 500L : Math.min(500L, msToDest));
		} else if (this.interpolatingPath) {
			this.interpolatePath();
			if (Log.loggingDebug) {
				Log.debug("baseBehavior.followUpdate: oid = " + oid + "; interpolated myLoc = " + this.obj.getWorldNode().getLoc());
			}
		}
		this.scheduleMe(this.interpolatingPath ? msToSleep : this.pathState.pathTimeRemaining());
	}

	protected long setupPathInterpolator(final OID oid, final Point myLoc, final Point dest, final boolean follow, final float distanceToFollowAt, final boolean followsTerrain) {
		final long timeNow = System.currentTimeMillis();
		final WorldManagerClient.MobPathReqMessage reqMsg = this.pathState.setupPathInterpolator(timeNow, myLoc, dest, this.mobSpeed, follow, distanceToFollowAt, followsTerrain);
		if (reqMsg != null) {
			try {
				Engine.getAgent().sendBroadcast(reqMsg);
				if (Log.loggingDebug) {
					Log.debug("BaseBehavior.setupPathInterpolator: send MobPathReqMessage " + reqMsg);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			this.interpolatingPath = true;
			return this.pathState.pathTimeRemaining();
		}
		this.interpolatingPath = false;
		return 0L;
	}

	protected void cancelPathInterpolator(final OID oid) {
		final WorldManagerClient.MobPathReqMessage cancelMsg = new WorldManagerClient.MobPathReqMessage(oid);
		try {
			Engine.getAgent().sendBroadcast(cancelMsg);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean interpolatePath() {
		final long timeNow = System.currentTimeMillis();
		final PathLocAndDir locAndDir = this.pathState.interpolatePath(timeNow);
		final OID oid = this.obj.getOid();
		if (locAndDir == null) {
			if (this.interpolatingPath) {
				if (Log.loggingDebug) {
					Log.debug("BaseBehavior.interpolatePath: cancelling path: oid = " + oid + "; myLoc = " + this.obj.getWorldNode().getLoc());
				}
				this.cancelPathInterpolator(oid);
				this.interpolatingPath = false;
			}
			this.obj.getWorldNode().setDir(new AOVector(0.0f, 0.0f, 0.0f));
		} else {
			this.obj.getWorldNode().setPathInterpolatorValues(timeNow, locAndDir.getDir(), locAndDir.getLoc(), locAndDir.getOrientation());
			MobManagerPlugin.getTracker(this.obj.getInstanceOid()).updateEntity(this.obj);
		}
		return this.interpolatingPath;
	}

	@Override
	public void run() {
		try {
			this.lock.lock();
			if (!this.activated) {
				return;
			}
			try {
				if (this.mode == "goto") {
					this.gotoUpdate();
				} else if (this.mode == "follow") {
					this.followUpdate();
				} else if (this.mode != "stop") {
					Log.error("BaseBehavior.run: invalid mode");
				}
			} catch (Exception e) {
				Log.exception("BaseBehavior.run caught exception raised during run for mode = " + this.mode, e);
				throw new RuntimeException(e);
			}
		} finally {
			this.lock.unlock();
		}
	}

	protected String getPathObjectTypeName() {
		return this.pathObjectTypeName;
	}

	public static class GotoCommandMessage extends CommandMessage {
		private Point dest;
		private float speed;
		private static final long serialVersionUID = 1L;

		public GotoCommandMessage() {
			super("goto");
		}

		public GotoCommandMessage(final ObjectStub obj, final Point dest, final float speed) {
			super(obj, "goto");
			this.setDestination(dest);
			this.setSpeed(speed);
		}

		public Point getDestination() {
			return this.dest;
		}

		public void setDestination(final Point dest) {
			this.dest = dest;
		}

		public float getSpeed() {
			return this.speed;
		}

		public void setSpeed(final float speed) {
			this.speed = speed;
		}
	}

	public static class FollowCommandMessage extends CommandMessage {
		private EntityHandle target;
		private Integer speed;
		private Float distanceToFollowAt;
		private static final long serialVersionUID = 1L;

		public FollowCommandMessage() {
			super("follow");
		}

		public FollowCommandMessage(final ObjectStub obj, final EntityHandle target, final Integer speed, final Float distanceToFollowAt) {
			super(obj);
			this.setTarget(target);
			this.setSpeed(speed);
			this.setDistanceToFollowAt(distanceToFollowAt);
		}

		public EntityHandle getTarget() {
			return this.target;
		}

		public void setTarget(final EntityHandle target) {
			this.target = target;
		}

		public Integer getSpeed() {
			return this.speed;
		}

		public void setSpeed(final Integer speed) {
			this.speed = speed;
		}

		public Float getDistanceToFollowAt() {
			return this.distanceToFollowAt;
		}

		public void setDistanceToFollowAt(final Float distanceToFollowAt) {
			this.distanceToFollowAt = distanceToFollowAt;
		}
	}

	public static class StopCommandMessage extends CommandMessage {
		private static final long serialVersionUID = 1L;

		public StopCommandMessage() {
			super("stop");
		}

		public StopCommandMessage(final OID objOid) {
			super(objOid, "stop");
		}

		public StopCommandMessage(final ObjectStub obj) {
			super(obj, "stop");
		}
	}

	public static class DisableCommandMessage extends CommandMessage {
		private static final long serialVersionUID = 1L;

		public DisableCommandMessage() {
			super("disable");
		}

		public DisableCommandMessage(final OID objOid) {
			super(objOid, "disable");
		}

		public DisableCommandMessage(final ObjectStub obj) {
			super(obj, "disable");
		}
	}

	public static class ArrivedEventMessage extends EventMessage {
		private static final long serialVersionUID = 1L;

		public ArrivedEventMessage() {
			this.setEvent("arrived");
		}

		public ArrivedEventMessage(final OID objOid) {
			super(objOid);
			this.setEvent("arrived");
		}

		public ArrivedEventMessage(final ObjectStub obj) {
			super(obj);
			this.setEvent("arrived");
		}
	}
}
