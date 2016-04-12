// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.msgsys.MessageType;
import atavism.server.objects.EntityManager;
import atavism.server.engine.Namespace;
import atavism.server.engine.BasicWorldNode;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.BaseBehavior;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Behavior;
import atavism.msgsys.SubjectFilter;
import atavism.server.pathing.crowd.UpdateFlags;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.objects.EntityHandle;
import atavism.server.pathing.crowd.CrowdAgentParams;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import atavism.server.objects.AOObject;
import atavism.server.engine.OID;
import atavism.msgsys.MessageCallback;

public class DetourActor implements MessageCallback
{
    OID oid;
    AOObject obj;
    AOObject target;
    Point lastTargetLoc;
    Point targetLoc;
    Point lastLoc;
    AOVector lastDir;
    Quaternion lastOrient;
    float speed;
    CrowdAgentParams params;
    InstanceNavMeshManager navMeshManager;
    private int agentId;
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
        final InterpolatedWorldNode node = (InterpolatedWorldNode)obj.worldNode();
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
    
    public void activate() {
        final SubjectFilter filter = new SubjectFilter(this.oid);
        filter.addType(Behavior.MSG_TYPE_COMMAND);
        filter.addType(WorldManagerClient.MSG_TYPE_MOB_PATH_CORRECTION);
        this.commandSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        Engine.getAgent().sendBroadcast((Message)new BaseBehavior.ArrivedEventMessage(this.oid));
        Engine.getAgent().sendBroadcast((Message)new BaseBehavior.DisableCommandMessage(this.oid));
    }
    
    public void deactivate() {
        if (this.commandSub != null) {
            Engine.getAgent().removeSubscription(this.commandSub);
            this.commandSub = null;
        }
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg.getMsgType() == Behavior.MSG_TYPE_COMMAND) {
            final Behavior.CommandMessage cmdMsg = (Behavior.CommandMessage)msg;
            final String command = cmdMsg.getCmd();
            if (Log.loggingDebug) {
                Log.debug("DetourActor.onMessage: command = " + command + "; oid = " + this.oid);
            }
            if (command.equals("goto")) {
                final BaseBehavior.GotoCommandMessage gotoMsg = (BaseBehavior.GotoCommandMessage)msg;
                final Point destination = gotoMsg.getDestination();
                this.mode = "goto";
                this.roamingBehavior = true;
                this.navMeshManager.setActorTarget(this.oid, destination);
                this.navMeshManager.setActorSpeed(this.oid, gotoMsg.getSpeed());
                this.targetLoc = new Point(destination.getX(), destination.getY(), destination.getZ());
            }
            else {
                if (command.equals("stop")) {
                    this.followTarget = null;
                    final InterpolatedWorldNode node = (InterpolatedWorldNode)this.obj.worldNode();
                    node.setDir(new AOVector(0.0f, 0.0f, 0.0f));
                    WorldManagerClient.updateWorldNode(this.oid, new BasicWorldNode(node));
                    this.mode = "stop";
                    if (!this.roamingBehavior) {
                        return;
                    }
                    try {
                        Engine.getAgent().sendBroadcast((Message)new BaseBehavior.ArrivedEventMessage(this.oid));
                        return;
                    }
                    catch (Exception e) {
                        Log.error("BaseBehavior.onMessage: Error sending ArrivedEventMessage, error was '" + e.getMessage() + "'");
                        throw new RuntimeException(e);
                    }
                }
                if (command.equals("follow")) {
                    final BaseBehavior.FollowCommandMessage followMsg = (BaseBehavior.FollowCommandMessage)msg;
                    this.mode = "follow";
                    this.target = (AOObject)EntityManager.getEntityByNamespace(followMsg.getTarget().getOid(), Namespace.WORLD_MANAGER);
                    this.speed = followMsg.getSpeed();
                    this.distanceToFollowAt = followMsg.getDistanceToFollowAt();
                    this.setupFollow(this.lastLoc);
                }
            }
        }
        else {
            msg.getMsgType();
            final MessageType msg_TYPE_MOB_PATH_CORRECTION = WorldManagerClient.MSG_TYPE_MOB_PATH_CORRECTION;
        }
    }
    
    void setupFollow(final Point pos) {
        Log.debug("DETOUR: setupFollow hit");
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
        Log.debug("DETOUR: targetLoc: " + this.targetLoc + " from followLoc: " + this.lastTargetLoc + " and currentPos: " + pos);
    }
    
    public void updateDirLoc(final AOVector dir, final Point pos) {
        if (this.lastLoc == null || pos == null || this.lastDir == null) {
            Log.debug("DETOUR: hit null pos or dir");
        }
        float distanceSquared = Point.distanceToSquared(this.lastLoc, pos);
        final AOVector dirDiff = AOVector.sub(dir, this.lastDir);
        if (Math.abs(dirDiff.getX()) > 0.1 || Math.abs(dirDiff.getY()) > 0.1 || Math.abs(dirDiff.getZ()) > 0.1 || distanceSquared > 36.0f) {
            Log.debug("DETOUR: direction vector or position has changed for: " + this.oid + " new Dir: " + dir + ", new loc: " + pos);
            this.lastDir = dir;
            this.lastLoc = pos;
            final BasicWorldNode wnode = new BasicWorldNode();
            wnode.setInstanceOid(this.navMeshManager.getInstanceOid());
            if (this.targetLoc != null) {
                distanceSquared = Point.distanceToSquared(pos, this.targetLoc);
                Log.debug("DETOUR: distanceSquared = " + distanceSquared);
                if (distanceSquared < 0.5f) {
                    Engine.getAgent().sendBroadcast((Message)new BaseBehavior.ArrivedEventMessage(this.oid));
                    this.navMeshManager.resetActorTarget(this.oid);
                    this.lastDir = AOVector.Zero;
                    this.navMeshManager.setActorSpeed(this.oid, 0.0f);
                    if (!this.mode.equals("follow")) {
                        Log.debug("DETOUR: setting behaviour to stop");
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
            }
            else if (this.lastOrient != null) {
                wnode.setOrientation(this.lastOrient);
            }
            wnode.setLoc(pos);
            WorldManagerClient.updateWorldNode(this.oid, wnode);
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
