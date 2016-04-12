// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.crowd;

//import atavism.server.util.Log;
import com.app.server.atavism.server.pathing.detour.NavMesh;
import com.app.server.atavism.server.pathing.detour.DetourStatusReturn;
import java.util.EnumSet;

import org.apache.log4j.Logger;

import com.app.server.atavism.server.pathing.recast.Helper;
import com.app.server.atavism.server.pathing.detour.Status;
import com.app.server.atavism.server.pathing.detour.NavMeshQuery;
import com.app.server.atavism.server.pathing.detour.QueryFilter;
/**
 * 人群
 * 
 * @author doter
 * 
 */
public class Crowd {
	private Logger log = Logger.getLogger("navmesh");
	public static int CrowdMaxObstAvoidanceParams;
	private int _maxAgents;
	private CrowdAgent[] _agents;// 代理商
	private CrowdAgent[] _activeAgents;
	private CrowdAgentAnimation[] _agentAnims;
	private PathQueue _pathq;
	private ObstacleAvoidanceParams[] _obstacleQueryParams;
	private ObstacleAvoidanceQuery _obstacleQuery;
	private ProximityGrid _grid;
	private long[] _pathResult;
	private int _maxPathResult;
	private float[] _ext;
	private QueryFilter _filter;
	private float _maxAgentRadius;
	private int _velocitySampleCount;
	private NavMeshQuery _navQuery;
	public static int MaxPathQueueNodes;
	public static int MaxCommonNodes;
	public static int MaxItersPerUpdate;

	public Crowd() {
		this._obstacleQueryParams = new ObstacleAvoidanceParams[Crowd.CrowdMaxObstAvoidanceParams];
		this._ext = new float[3];
		this._maxAgents = 0;
		this._agents = null;
		this._activeAgents = null;
		this._agentAnims = null;
		this._obstacleQuery = null;
		this._grid = null;
		this._pathResult = null;
		this._maxPathResult = 0;
		this._maxAgentRadius = 0.0f;
		this._velocitySampleCount = 0;
		this._navQuery = null;
		this._filter = new QueryFilter();
	}
	/**
	 * 更新移动请求
	 * 
	 * @param dt
	 */
	private void UpdateMoveRequest(final float dt) {
		final int PathMaxAgents = 8;
		final CrowdAgent[] queue = new CrowdAgent[PathMaxAgents];
		int nqueue = 0;
		EnumSet<Status> status = null;
		for (int i = 0; i < this._maxAgents; ++i) {
			final CrowdAgent ag = this._agents[i];
			if (ag.Active) {
				if (ag.State != CrowdAgentState.Invalid) {
					if (ag.TargetState != MoveRequestState.TargetNone) {
						if (ag.TargetState != MoveRequestState.TargetVelocity) {
							if (ag.TargetState == MoveRequestState.TargetRequesting) {
								final long[] path = ag.Corridor.GetPath();
								final int npath = ag.Corridor.PathCount();
								final int MaxRes = 32;
								final float[] reqPos = new float[3];
								final long[] reqPath = new long[MaxRes];
								int reqPathCount = 0;
								final int MaxIter = 20;
								this._navQuery.InitSlicedFindPath(path[0], ag.TargetRef, ag.npos, ag.TargetPos, this._filter);
								final int doneIters = 0;
								this._navQuery.UpdateSlicedFindPath(MaxIter);
								if (ag.TargetReplan) {
									final DetourStatusReturn statusReturn = this._navQuery.FinalizeSlicedFindPathPartial(path, npath, reqPath, MaxRes);
									status = statusReturn.status;
									reqPathCount = statusReturn.intValue;
								} else {
									final DetourStatusReturn statusReturn = this._navQuery.FinalizeSlicedFindPath(reqPath, MaxRes);
									status = statusReturn.status;
									reqPathCount = statusReturn.intValue;
								}
								if (!status.contains(Status.Failure) && reqPathCount > 0) {
									if (reqPath[reqPathCount - 1] != ag.TargetRef) {
										status = this._navQuery.ClosestPointOnPoly(reqPath[reqPathCount - 1], ag.TargetPos, reqPos);
										if (status.contains(Status.Failure)) {
											reqPathCount = 0;
										}
									} else {
										Helper.VCopy(reqPos, ag.TargetPos);
									}
								} else {
									reqPathCount = 0;
								}
								if (reqPathCount <= 0) {
									Helper.VCopy(reqPos, ag.npos);
									reqPos[0] = path[0];
									reqPathCount = 1;
								}
								ag.Corridor.SetCorridor(reqPos, reqPath, reqPathCount);
								ag.Boundary.Reset();
								if (reqPath[reqPathCount - 1] == ag.TargetRef) {
									ag.TargetState = MoveRequestState.TargetValid;
									ag.TargetReplanTime = 0.0f;
								} else {
									ag.TargetState = MoveRequestState.TargetWaitingForQueue;
								}
							}
							if (ag.TargetState == MoveRequestState.TargetWaitingForQueue) {
								nqueue = AddToPlanQueue(ag, queue, nqueue, PathMaxAgents);
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < nqueue; ++i) {
			final CrowdAgent ag = queue[i];
			ag.TargetPathQRef = this._pathq.Request(ag.Corridor.LastPoly(), ag.TargetRef, ag.Corridor.Target(), ag.TargetPos, this._filter);
			if (ag.TargetPathQRef != 0L) {
				ag.TargetState = MoveRequestState.TargetWaitingForPath;
			}
		}
		this._pathq.Update(Crowd.MaxItersPerUpdate);
		for (int i = 0; i < this._maxAgents; ++i) {
			final CrowdAgent ag = this._agents[i];
			if (ag.Active) {
				if (ag.TargetState != MoveRequestState.TargetNone) {
					if (ag.TargetState != MoveRequestState.TargetVelocity) {
						if (ag.TargetState == MoveRequestState.TargetWaitingForPath) {
							status = this._pathq.GetRequestStatus(ag.TargetPathQRef);
							if (status.contains(Status.Failure)) {
								ag.TargetPathQRef = 0L;
								if (ag.TargetRef > 0L) {
									ag.TargetState = MoveRequestState.TargetRequesting;
								} else {
									ag.TargetState = MoveRequestState.TargetFailed;
								}
								ag.TargetReplanTime = 0.0f;
							} else if (status.contains(Status.Success)) {
								final long[] path = ag.Corridor.GetPath();
								final int npath = ag.Corridor.PathCount();
								final float[] targetPos = new float[3];
								Helper.VCopy(targetPos, ag.TargetPos);
								final long[] res = this._pathResult;
								Boolean valid = true;
								int nres = 0;
								final DetourStatusReturn statusReturn2 = this._pathq.GetPathResult(ag.TargetPathQRef, res, nres, this._maxPathResult);
								nres = statusReturn2.intValue;
								status = statusReturn2.status;
								if (status.contains(Status.Failure) || nres <= 0) {
									valid = false;
								}
								if (valid) {
									if (npath > 1) {
										if (npath - 1 + nres > this._maxPathResult) {
											nres = this._maxPathResult - (npath - 1);
										}
										System.arraycopy(res, 0, res, npath - 1, nres);
										System.arraycopy(path, 0, res, 0, npath - 1);
										nres += npath - 1;
										for (int j = 0; j < nres; ++j) {
											if (j - 1 >= 0 && j + 1 < nres && res[j - 1] == res[j + 1]) {
												System.arraycopy(res, j + 1, res, j - 1, nres - (j + 1));
												nres -= 2;
												j -= 2;
											}
										}
									}
									if (res[nres - 1] != ag.TargetRef) {
										final float[] nearest = new float[3];
										status = this._navQuery.ClosestPointOnPoly(res[nres - 1], targetPos, nearest);
										if (status.contains(Status.Success)) {
											Helper.VCopy(targetPos, nearest);
										} else {
											valid = false;
										}
									}
								}
								if (valid) {
									ag.Corridor.SetCorridor(targetPos, res, nres);
									ag.Boundary.Reset();
									ag.TargetState = MoveRequestState.TargetValid;
								} else {
									ag.TargetState = MoveRequestState.TargetFailed;
								}
								ag.TargetReplanTime = 0.0f;
							}
						}
					}
				}
			}
		}
	}
	/**
	 * 更新拓扑优化
	 * 
	 * @param agents
	 * @param nagents
	 * @param dt
	 */
	private void UpdateTopologyOptimization(final CrowdAgent[] agents, final int nagents, final float dt) {
		if (nagents <= 0) {
			return;
		}
		final float OptTimeThr = 0.5f;
		final int OptMaxAgents = 1;
		final CrowdAgent[] queue = new CrowdAgent[OptMaxAgents];
		queue[0] = new CrowdAgent();
		int nqueue = 0;
		for (final CrowdAgent ag : agents) {
			if (ag.State == CrowdAgentState.Walking) {
				if (ag.TargetState != MoveRequestState.TargetNone) {
					if (ag.TargetState != MoveRequestState.TargetVelocity) {
						if ((ag.Param.UpdateFlags.getValue() & UpdateFlags.OptimizeTopology.getValue()) != 0x0) {
							final CrowdAgent crowdAgent = ag;
							crowdAgent.TopologyOptTime += dt;
							if (ag.TopologyOptTime >= OptTimeThr) {
								nqueue = AddToOptQueue(ag, queue, nqueue, OptMaxAgents);
							}
						}
					}
				}
			}
		}
		for (final CrowdAgent ag : queue) {
			ag.Corridor.OptimizePathTopology(this._navQuery, this._filter);
			ag.TopologyOptTime = 0.0f;
		}
	}
	/**
	 * 检查有效性的路径
	 * 
	 * @param agents
	 * @param nagents
	 * @param dt
	 */
	private void CheckPathValitidy(final CrowdAgent[] agents, final int nagents, final float dt) {
		final int CheckLookAhead = 10;
		final float TargetReplanDelay = 1.0f;
		for (final CrowdAgent ag : agents) {
			Label_0495 : {
				if (ag.State == CrowdAgentState.Walking) {
					if (ag.TargetState != MoveRequestState.TargetNone) {
						if (ag.TargetState != MoveRequestState.TargetVelocity) {
							final CrowdAgent crowdAgent = ag;
							crowdAgent.TargetReplanTime += dt;
							Boolean replan = false;
							final int idx = this.AgentIndex(ag);
							final float[] agentPos = new float[3];
							long agentRef = ag.Corridor.FirstPoly();
							Helper.VCopy(agentPos, ag.npos);
							if (!this._navQuery.IsValidPolyRef(agentRef, this._filter)) {
								final float[] nearest = new float[3];
								agentRef = 0L;
								agentRef = this._navQuery.FindNearestPoly(ag.npos, this._ext, this._filter, nearest).longValue;
								Helper.VCopy(agentPos, nearest);
								if (agentRef <= 0L) {
									ag.Corridor.Reset(0L, agentPos);
									ag.Boundary.Reset();
									ag.State = CrowdAgentState.Invalid;
									break Label_0495;
								}
								ag.Corridor.FixPathStart(agentRef, agentPos);
								ag.Boundary.Reset();
								Helper.VCopy(ag.npos, agentPos);
								replan = true;
							}
							if (ag.TargetState != MoveRequestState.TargetNone && ag.TargetState != MoveRequestState.TargetFailed) {
								if (!this._navQuery.IsValidPolyRef(ag.TargetRef, this._filter)) {
									final float[] nearest = new float[3];
									ag.TargetRef = this._navQuery.FindNearestPoly(ag.TargetPos, this._ext, this._filter, nearest).longValue;
									Helper.VCopy(ag.TargetPos, nearest);
									replan = true;
								}
								if (ag.TargetRef <= 0L) {
									ag.Corridor.Reset(agentRef, agentPos);
									ag.TargetState = MoveRequestState.TargetNone;
								}
							}
							if (!ag.Corridor.IsValid(CheckLookAhead, this._navQuery, this._filter)) {
								replan = true;
							}
							if (ag.TargetState == MoveRequestState.TargetValid && ag.TargetReplanTime > TargetReplanDelay && ag.Corridor.PathCount() < CheckLookAhead
									&& ag.Corridor.LastPoly() != ag.TargetRef) {
								replan = true;
							}
							if (replan && ag.TargetState != MoveRequestState.TargetNone) {
								this.RequestMoveTargetReplan(idx, ag.TargetRef, ag.TargetPos);
							}
						}
					}
				}
			}
		}
	}

	private int AgentIndex(final CrowdAgent agent) {
		for (int i = 0; i < this._agents.length; ++i) {
			if (this._agents[i] == agent) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 请求重新移动目标
	 * 
	 * @param idx
	 * @param refId
	 * @param pos
	 * @return
	 */
	private Boolean RequestMoveTargetReplan(final int idx, final long refId, final float[] pos) {
		if (idx < 0 || idx > this._maxAgents) {
			return false;
		}
		final CrowdAgent ag = this._agents[idx];
		ag.TargetRef = refId;
		Helper.VCopy(ag.TargetPos, pos);
		ag.TargetPathQRef = 0L;
		ag.TargetReplan = true;
		if (ag.TargetRef > 0L) {
			ag.TargetState = MoveRequestState.TargetRequesting;
		} else {
			ag.TargetState = MoveRequestState.TargetFailed;
		}
		return true;
	}
	/**
	 * 清除
	 */
	private void Purge() {
		this._agents = null;
		this._maxAgents = 0;
		this._activeAgents = null;
		this._agentAnims = null;
		this._pathResult = null;
		this._grid = null;
		this._obstacleQuery = null;
		this._navQuery = null;
	}

	public Boolean Init(final int maxAgents, final float maxAgentRadius, final NavMesh nav) {
		this.Purge();
		this._maxAgents = maxAgents;
		this._maxAgentRadius = maxAgentRadius;
		Helper.VSet(this._ext, this._maxAgentRadius * 2.0f, this._maxAgentRadius * 1.5f, this._maxAgentRadius * 2.0f);
		(this._grid = new ProximityGrid()).Init(this._maxAgents * 4, maxAgentRadius * 3.0f);
		(this._obstacleQuery = new ObstacleAvoidanceQuery()).Init(6, 8);
		for (int i = 0; i < this._obstacleQueryParams.length; ++i) {
			this._obstacleQueryParams[i] = new ObstacleAvoidanceParams(0.4f, 2.0f, 0.75f, 0.75f, 2.5f, 2.5f, (short) 33, (short) 7, (short) 2, (short) 5);
		}
		this._maxPathResult = 256;
		this._pathResult = new long[this._maxPathResult];
		(this._pathq = new PathQueue()).Init(this._maxPathResult, Crowd.MaxPathQueueNodes, nav);
		this._agents = new CrowdAgent[this._maxAgents];
		this._activeAgents = new CrowdAgent[this._maxAgents];
		this._agentAnims = new CrowdAgentAnimation[this._maxAgents];
		for (int i = 0; i < this._maxAgents; ++i) {
			this._agents[i] = new CrowdAgent();
			this._agents[i].Active = false;
			this._agents[i].Corridor.Init(this._maxPathResult);
		}
		for (int i = 0; i < this._maxAgents; ++i) {
			this._agentAnims[i] = new CrowdAgentAnimation();
			this._agentAnims[i].Active = false;
		}
		(this._navQuery = new NavMeshQuery()).Init(nav, Crowd.MaxCommonNodes);
		return true;
	}
	/**
	 * 设置避障
	 * 
	 * @param idx
	 * @param param
	 */
	public void SetObstacleAvoidanceParams(final int idx, final ObstacleAvoidanceParams param) {
		if (idx >= 0 && idx < Crowd.CrowdMaxObstAvoidanceParams) {
			this._obstacleQueryParams[idx] = param;
		}
	}
	/**
	 * 避障
	 * 
	 * @param idx
	 * @return
	 */
	public ObstacleAvoidanceParams GetObstacleAvoidanceParams(final int idx) {
		if (idx >= 0 && idx < Crowd.CrowdMaxObstAvoidanceParams) {
			return this._obstacleQueryParams[idx];
		}
		return null;
	}

	public CrowdAgent GetAgent(final int idx) {
		return this._agents[idx];
	}

	public int AgentCount() {
		return this._maxAgents;
	}
	/**
	 * 添加媒介
	 * 
	 * @param pos
	 * @param param
	 * @return
	 */
	public int AddAgent(final float[] pos, final CrowdAgentParams param) {
		int idx = -1;
		for (int i = 0; i < this._maxAgents; ++i) {
			if (!this._agents[i].Active) {
				idx = i;
				break;
			}
		}
		if (idx == -1) {
			return -1;
		}
		final CrowdAgent ag = this._agents[idx];
		final float[] nearest = new float[3];
		final long refId = this._navQuery.FindNearestPoly(pos, this._ext, this._filter, nearest).longValue;
		log.debug("DETOUR: added agent to refId: " + refId);
		ag.Corridor.Reset(refId, nearest);
		ag.Boundary.Reset();
		this.UpdateAgentParameters(idx, param);
		ag.TopologyOptTime = 0.0f;
		ag.TargetReplanTime = 0.0f;
		ag.NNeis = 0;
		Helper.VSet(ag.dvel, 0.0f, 0.0f, 0.0f);
		Helper.VSet(ag.nvel, 0.0f, 0.0f, 0.0f);
		Helper.VSet(ag.vel, 0.0f, 0.0f, 0.0f);
		Helper.VCopy(ag.npos, nearest);
		ag.DesiredSpeed = 0.0f;
		if (refId > 0L) {
			ag.State = CrowdAgentState.Walking;
		} else {
			ag.State = CrowdAgentState.Invalid;
		}
		ag.TargetState = MoveRequestState.TargetNone;
		ag.Active = true;
		return idx;
	}
	/**
	 * 更新媒介参数
	 * 
	 * @param idx
	 * @param param
	 */
	public void UpdateAgentParameters(final int idx, final CrowdAgentParams param) {
		if (idx < 0 || idx > this._maxAgents) {
			return;
		}
		this._agents[idx].Param = param;
	}
	/**
	 * 移除媒介
	 * 
	 * @param idx
	 */
	public void RemoveAgent(final int idx) {
		if (idx >= 0 || idx < this._maxAgents) {
			this._agents[idx].Active = false;
		}
	}
	/**
	 * 请求移动目标
	 * 
	 * @param idx
	 * @param refId
	 * @param pos
	 * @return
	 */
	public Boolean RequestMoveTarget(final int idx, final long refId, final float[] pos) {
		if (idx < 0 || idx > this._maxAgents) {
			return false;
		}
		if (refId <= 0L) {
			return false;
		}
		final CrowdAgent ag = this._agents[idx];
		ag.TargetRef = refId;
		Helper.VCopy(ag.TargetPos, pos);
		ag.TargetPathQRef = 0L;
		ag.TargetReplan = false;
		if (ag.TargetRef > 0L) {
			ag.TargetState = MoveRequestState.TargetRequesting;
		} else {
			ag.TargetState = MoveRequestState.TargetFailed;
		}
		log.debug("DETOUR: set target to pos: " + pos[0] + "," + pos[1] + "," + pos[2] + " with currentPos: " + ag.npos[0] + "," + ag.npos[1] + "," + ag.npos[2] + " and EndRef: " + refId);
		return true;
	}
	/**
	 * 请求移动速度
	 * 
	 * @param idx
	 * @param vel
	 * @return
	 */
	public Boolean RequestMoveVelocity(final int idx, final float[] vel) {
		if (idx < 0 || idx > this._maxAgents) {
			return false;
		}
		final CrowdAgent ag = this._agents[idx];
		ag.TargetRef = 0L;
		Helper.VCopy(ag.TargetPos, vel);
		ag.TargetPathQRef = 0L;
		ag.TargetReplan = false;
		ag.TargetState = MoveRequestState.TargetVelocity;
		return true;
	}
	/**
	 * 复位移动目标
	 * 
	 * @param idx
	 * @return
	 */
	public Boolean ResetMoveTarget(final int idx) {
		if (idx < 0 || idx > this._maxAgents) {
			return false;
		}
		final CrowdAgent ag = this._agents[idx];
		ag.TargetRef = 0L;
		Helper.VSet(ag.TargetPos, 0.0f, 0.0f, 0.0f);
		ag.TargetPathQRef = 0L;// 目标路径
		ag.TargetReplan = false;// 目标计划
		ag.TargetState = MoveRequestState.TargetNone;
		return true;
	}
	/**
	 * 获取活动的媒介
	 * 
	 * @param agents
	 * @param maxAgents
	 * @return
	 */
	public int GetActiveAgents(final CrowdAgent[] agents, final int maxAgents) {
		int n = 0;
		for (int i = 0; i < this._maxAgents; ++i) {
			if (this._agents[i].Active) {
				if (n < maxAgents) {
					agents[n++] = this._agents[i];
				}
			}
		}
		return n;
	}
	/**
	 * 人群媒介调试信息
	 * 
	 * @param dt
	 * @param debug
	 * @return
	 */
	public CrowdAgentDebugInfo Update(final float dt, final CrowdAgentDebugInfo debug) {
		log.debug("CROWD: starting update with time: " + dt);
		this._velocitySampleCount = 0;
		final int debugIdx = (debug != null) ? debug.Idx : -1;
		final CrowdAgent[] agents = this._activeAgents;
		final int nagents = this.GetActiveAgents(agents, this._maxAgents);
		this.CheckPathValitidy(agents, nagents, dt);
		this.UpdateMoveRequest(dt);
		this.UpdateTopologyOptimization(agents, nagents, dt);
		this._grid.Clear();
		for (int i = 0; i < nagents; ++i) {
			final CrowdAgent ag = agents[i];
			final float[] p = ag.npos;
			final float r = ag.Param.Radius;
			this._grid.AddItem(i, p[0] - r, p[2] - r, p[0] + r, p[2] + r);
		}
		for (int i = 0; i < nagents; ++i) {
			final CrowdAgent ag = agents[i];
			if (ag.State == CrowdAgentState.Walking) {
				final float updateThr = ag.Param.CollisionQueryRange * 0.25f;
				final float dist = Helper.VDist2DSqr(ag.npos[0], ag.npos[1], ag.npos[2], ag.Boundary.getCenter()[0], ag.Boundary.getCenter()[1], ag.Boundary.getCenter()[2]);
				if (dist > updateThr * updateThr || !ag.Boundary.IsValid(this._navQuery, this._filter)) {
					ag.Boundary.Update(ag.Corridor.FirstPoly(), ag.npos, ag.Param.CollisionQueryRange, this._navQuery, this._filter);
				}
				ag.NNeis = GetNeighbors(ag.npos, ag.Param.Height, ag.Param.CollisionQueryRange, ag, ag.Neis, CrowdAgent.CrowdAgentMaxNeighbors, agents, nagents, this._grid);
				for (int j = 0; j < ag.NNeis; ++j) {
					ag.Neis[j].Idx = this.AgentIndex(agents[ag.Neis[j].Idx]);
				}
			}
		}
		for (int i = 0; i < nagents; ++i) {
			final CrowdAgent ag = agents[i];
			if (ag.State == CrowdAgentState.Walking) {
				if (ag.TargetState != MoveRequestState.TargetNone) {
					if (ag.TargetState != MoveRequestState.TargetVelocity) {
						ag.NCorners = ag.Corridor.FindCorners(ag.CornerVerts, ag.CornerFlags, ag.CornerPolys, CrowdAgent.CrowdAgentMaxCorners, this._navQuery, this._filter);
						if ((ag.Param.UpdateFlags.getValue() & UpdateFlags.OptimizeVisibility.getValue()) != 0x0 && ag.NCorners > 0) {
							final float[] target = new float[3];
							System.arraycopy(ag.CornerVerts, Math.min(1, ag.NCorners - 1) * 3, target, 0, 3);
							ag.Corridor.OptimizePathVisibility(target, ag.Param.PathOptimizationRange, this._navQuery, this._filter);
							if (debugIdx == i) {
								Helper.VCopy(debug.OptStart, ag.Corridor.Pos());
								Helper.VCopy(debug.OptEnd, target);
							}
						} else if (debugIdx == i) {
							Helper.VSet(debug.OptStart, 0.0f, 0.0f, 0.0f);
							Helper.VSet(debug.OptEnd, 0.0f, 0.0f, 0.0f);
						}
					}
				}
			}
		}
		for (int i = 0; i < nagents; ++i) {
			final CrowdAgent ag = agents[i];
			if (ag.State == CrowdAgentState.Walking) {
				if (ag.TargetState != MoveRequestState.TargetNone) {
					if (ag.TargetState != MoveRequestState.TargetVelocity) {
						final float triggerRadius = ag.Param.Radius * 2.25f;
						if (ag.OverOffMeshConnection(triggerRadius)) {
							final int idx = this.AgentIndex(ag);
							final CrowdAgentAnimation anim = this._agentAnims[idx];
							final long[] refs = new long[2];
							if (ag.Corridor.MoveOverOffmeshConnection(ag.CornerPolys[ag.NCorners - 1], refs, anim.StartPos, anim.EndPos, this._navQuery)) {
								Helper.VCopy(anim.InitPos, ag.npos);
								anim.PolyRef = refs[1];
								anim.Active = true;
								anim.T = 0.0f;
								anim.TMax = Helper.VDist2D(anim.StartPos, anim.EndPos) / ag.Param.MaxSpeed * 0.5f;
								ag.State = CrowdAgentState.OffMesh;
								ag.NCorners = 0;
								ag.NNeis = 0;
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < nagents; ++i) {
			final CrowdAgent ag = agents[i];
			if (ag.State == CrowdAgentState.Walking) {
				if (ag.TargetState == MoveRequestState.TargetNone) {
					Helper.VSet(ag.dvel, 0.0f, 0.0f, 0.0f);
				} else {
					float[] dvel = {0.0f, 0.0f, 0.0f};
					if (ag.TargetState == MoveRequestState.TargetVelocity) {
						Helper.VCopy(dvel, ag.TargetPos);
						ag.DesiredSpeed = Helper.VLen(ag.TargetPos);
					} else {
						if ((ag.Param.UpdateFlags.getValue() & UpdateFlags.AnticipateTurns.getValue()) != 0x0) {
							dvel = ag.CalcSmoothSteerDirection(dvel);
						} else {
							dvel = ag.CalcStraightSteerDirection(dvel);
						}
						final float slowDownRadius = ag.Param.Radius * 2.0f;
						final float speedScale = ag.GetDistanceToGoal(slowDownRadius) / slowDownRadius;
						dvel = Helper.VScale(dvel[0], dvel[1], dvel[2], ag.DesiredSpeed * speedScale);
					}
					if ((ag.Param.UpdateFlags.getValue() & UpdateFlags.Separation.getValue()) != 0x0) {
						final float separationDist = ag.Param.CollisionQueryRange;
						final float invSeparationDist = 1.0f / separationDist;
						final float separationWeight = ag.Param.SeparationWeight;
						float w = 0.0f;
						float[] disp = {0.0f, 0.0f, 0.0f};
						for (int k = 0; k < ag.NNeis; ++k) {
							final CrowdAgent nei = this._agents[ag.Neis[k].Idx];
							final float[] diff = Helper.VSub(ag.npos[0], ag.npos[1], ag.npos[2], nei.npos[0], nei.npos[1], nei.npos[2]);
							diff[1] = 0.0f;
							final float distSqr = Helper.VLenSqr(diff);
							if (distSqr >= 1.0E-5f) {
								if (distSqr <= separationDist * separationDist) {
									final float dist2 = (float) Math.sqrt(distSqr);
									final float weight = separationWeight * (1.0f - dist2 * invSeparationDist * dist2 * invSeparationDist);
									disp = Helper.VMad(disp, disp, diff, weight / dist2);
									++w;
								}
							}
						}
						if (w > 1.0E-4f) {
							dvel = Helper.VMad(dvel, dvel, disp, 1.0f / w);
							final float speedSqr = Helper.VLenSqr(dvel);
							final float desiredSpeed = ag.DesiredSpeed * ag.DesiredSpeed;
							if (speedSqr > desiredSpeed) {
								dvel = Helper.VScale(dvel[0], dvel[1], dvel[2], desiredSpeed / speedSqr);
							}
						}
					}
					Helper.VCopy(ag.dvel, dvel);
				}
			}
		}
		for (int i = 0; i < nagents; ++i) {
			final CrowdAgent ag = agents[i];
			if (ag.State == CrowdAgentState.Walking) {
				if ((ag.Param.UpdateFlags.getValue() & UpdateFlags.ObstacleAvoidance.getValue()) != 0x0) {
					this._obstacleQuery.Reset();
					for (int l = 0; l < ag.NNeis; ++l) {
						final CrowdAgent nei2 = this._agents[ag.Neis[l].Idx];
						this._obstacleQuery.AddCircle(nei2.npos, nei2.Param.Radius, nei2.vel, nei2.dvel);
					}
					for (int l = 0; l < ag.Boundary.SegmentCount(); ++l) {
						log.debug("CROWD: getting segment: " + l);
						final float[] s0 = new float[3];
						final float[] s2 = new float[3];
						System.arraycopy(ag.Boundary.GetSegment(l), 0, s0, 0, 3);
						System.arraycopy(ag.Boundary.GetSegment(l), 3, s2, 0, 3);
						if (Helper.TriArea2D(ag.npos, s0, s2) >= 0.0f) {
							this._obstacleQuery.AddSegment(s0, s2);
						}
					}
					ObstacleAvoidanceDebugData vod = null;
					if (debugIdx == i) {
						vod = debug.Vod;
					}
					final Boolean adaptive = true;
					int ns = 0;
					final ObstacleAvoidanceParams param = this._obstacleQueryParams[ag.Param.ObstacleAvoidanceType];
					if (adaptive) {
						ns = this._obstacleQuery.SampleVelocityAdaptive(ag.npos, ag.Param.Radius, ag.DesiredSpeed, ag.vel, ag.dvel, ag.nvel, param, vod);
					} else {
						ns = this._obstacleQuery.SampleVelocityGrid(ag.npos, ag.Param.Radius, ag.DesiredSpeed, ag.vel, ag.dvel, ag.nvel, param, vod);
					}
					this._velocitySampleCount += ns;
				} else {
					Helper.VCopy(ag.nvel, ag.dvel);
				}
			}
		}
		for (int i = 0; i < nagents; ++i) {
			final CrowdAgent ag = agents[i];
			if (ag.State == CrowdAgentState.Walking) {
				ag.Integrate(dt);
			}
		}
		final float CollisionResolveFactor = 0.7f;
		for (int iter = 0; iter < 4; ++iter) {
			for (final CrowdAgent ag2 : agents) {
				final int idx2 = this.AgentIndex(ag2);
				if (ag2.State == CrowdAgentState.Walking) {
					Helper.VSet(ag2.disp, 0.0f, 0.0f, 0.0f);
					float w2 = 0.0f;
					for (int j2 = 0; j2 < ag2.NNeis; ++j2) {
						final CrowdAgent nei3 = this._agents[ag2.Neis[j2].Idx];
						final int idx3 = this.AgentIndex(nei3);
						final float[] diff2 = Helper.VSub(ag2.npos[0], ag2.npos[1], ag2.npos[2], nei3.npos[0], nei3.npos[1], nei3.npos[2]);
						diff2[1] = 0.0f;
						float dist3 = Helper.VLenSqr(diff2);
						if (dist3 <= (ag2.Param.Radius + nei3.Param.Radius) * (ag2.Param.Radius + nei3.Param.Radius)) {
							dist3 = (float) Math.sqrt(dist3);
							float pen = ag2.Param.Radius + nei3.Param.Radius - dist3;
							if (dist3 < 1.0E-4f) {
								if (idx2 > idx3) {
									Helper.VSet(diff2, -ag2.dvel[2], 0.0f, ag2.dvel[0]);
								} else {
									Helper.VSet(diff2, ag2.dvel[2], 0.0f, -ag2.vel[0]);
								}
								pen = 0.01f;
							} else {
								pen = 1.0f / dist3 * (pen * 0.5f) * CollisionResolveFactor;
							}
							ag2.disp = Helper.VMad(ag2.disp, ag2.disp, diff2, pen);
							++w2;
						}
					}
					if (w2 > 1.0E-4f) {
						final float iw = 1.0f / w2;
						ag2.disp = Helper.VScale(ag2.disp[0], ag2.disp[1], ag2.disp[2], iw);
					}
				}
			}
			for (final CrowdAgent ag2 : agents) {
				if (ag2.State == CrowdAgentState.Walking) {
					ag2.npos = Helper.VAdd(ag2.npos[0], ag2.npos[1], ag2.npos[2], ag2.disp[0], ag2.disp[1], ag2.disp[2]);
				}
			}
		}
		for (final CrowdAgent ag3 : agents) {
			if (ag3.State == CrowdAgentState.Walking) {
				ag3.Corridor.MovePosition(ag3.npos, this._navQuery, this._filter);
				Helper.VCopy(ag3.npos, ag3.Corridor.Pos());
				if (ag3.TargetState == MoveRequestState.TargetNone || ag3.TargetState == MoveRequestState.TargetVelocity) {
					ag3.Corridor.Reset(ag3.Corridor.FirstPoly(), ag3.npos);
				}
			}
		}
		for (int i2 = 0; i2 < this._maxAgents; ++i2) {
			final CrowdAgentAnimation anim2 = this._agentAnims[i2];
			if (anim2.Active) {
				final CrowdAgent ag2 = agents[i2];
				final CrowdAgentAnimation crowdAgentAnimation = anim2;
				crowdAgentAnimation.T += dt;
				if (anim2.T > anim2.TMax) {
					anim2.Active = false;
					ag2.State = CrowdAgentState.Walking;
				} else {
					final float ta = anim2.TMax * 0.15f;
					final float tb = anim2.TMax;
					if (anim2.T < ta) {
						final float u = this.Tween(anim2.T, 0.0f, ta);
						ag2.npos = Helper.VLerp(ag2.npos, anim2.InitPos[0], anim2.InitPos[1], anim2.InitPos[2], anim2.StartPos[0], anim2.StartPos[1], anim2.StartPos[2], u);
					} else {
						final float u = this.Tween(anim2.T, ta, tb);
						ag2.npos = Helper.VLerp(ag2.npos, anim2.StartPos[0], anim2.StartPos[1], anim2.StartPos[2], anim2.EndPos[0], anim2.EndPos[1], anim2.EndPos[2], u);
					}
					Helper.VSet(ag2.vel, 0.0f, 0.0f, 0.0f);
					Helper.VSet(ag2.dvel, 0.0f, 0.0f, 0.0f);
				}
			}
		}
		return debug;
	}

	public QueryFilter getFilter() {
		return this._filter;
	}

	public void setFilter(final QueryFilter value) {
		this._filter = value;
	}

	public float[] QueryExtents() {
		return this._ext;
	}

	public int VelocitySample() {
		return this._velocitySampleCount;
	}

	public ProximityGrid Grid() {
		return this._grid;
	}

	public NavMeshQuery NavMeshQuery() {
		return this._navQuery;
	}
	/**
	 * 添加邻居
	 * 
	 * @param idx
	 * @param dist
	 * @param neis
	 * @param nneis
	 * @param maxNeis
	 * @return
	 */
	public static int AddNeighbor(final int idx, final float dist, final CrowdNeighbour[] neis, final int nneis, final int maxNeis) {
		CrowdNeighbour nei = null;
		if (neis != null || neis.length > 0) {
			nei = neis[nneis];
		} else if (dist >= neis[nneis - 1].Dist) {
			if (nneis >= maxNeis) {
				return nneis;
			}
			nei = neis[nneis];
		} else {
			int i;
			for (i = 0; i < nneis && dist > neis[i].Dist; ++i) {
			}
			final int tgt = i + 1;
			final int n = Math.min(nneis - i, maxNeis - tgt);
			if (n > 0) {
				System.arraycopy(neis, i, neis, tgt, n);
			}
			nei = neis[i];
		}
		nei = new CrowdNeighbour();
		nei.Idx = idx;
		nei.Dist = dist;
		return Math.min(nneis + 1, maxNeis);
	}
	/**
	 * 获取邻居
	 * 
	 * @param pos
	 * @param height
	 * @param range
	 * @param skip
	 * @param result
	 * @param maxResult
	 * @param agents
	 * @param nagents
	 * @param grid
	 * @return
	 */
	public static int GetNeighbors(final float[] pos, final float height, final float range, final CrowdAgent skip, final CrowdNeighbour[] result, final int maxResult, final CrowdAgent[] agents,
			final int nagents, final ProximityGrid grid) {
		int n = 0;
		final int MaxNeis = 32;
		final int[] ids = new int[MaxNeis];
		for (int nids = grid.QueryItems(pos[0] - range, pos[2] - range, pos[0] + range, pos[2] + range, ids, MaxNeis), i = 0; i < nids; ++i) {
			final CrowdAgent ag = agents[ids[i]];
			if (ag != skip) {
				final float[] diff = Helper.VSub(pos[0], pos[1], pos[2], ag.npos[0], ag.npos[1], ag.npos[2]);
				if (Math.abs(diff[1]) < (height + ag.Param.Height) / 2.0f) {
					diff[1] = 0.0f;
					final float distSqr = Helper.VLenSqr(diff);
					if (distSqr <= range * range) {
						n = AddNeighbor(ids[i], distSqr, result, n, maxResult);
					}
				}
			}
		}
		return n;
	}
	/**
	 * 加入选择列队
	 * 
	 * @param newag
	 * @param agents
	 * @param nagents
	 * @param maxAgents
	 * @return
	 */
	public static int AddToOptQueue(final CrowdAgent newag, final CrowdAgent[] agents, final int nagents, final int maxAgents) {
		int slot = 0;
		if (nagents <= 0) {
			slot = 0;
		} else if (newag.TopologyOptTime <= agents[nagents - 1].TopologyOptTime) {
			if (nagents >= maxAgents) {
				return nagents;
			}
			slot = nagents;
		} else {
			int i;
			for (i = 0; i < nagents && newag.TopologyOptTime < agents[i].TopologyOptTime; ++i) {
			}
			final int tgt = i + 1;
			final int n = Math.min(nagents - i, maxAgents - tgt);
			if (n > 0) {
				System.arraycopy(agents, i, agents, tgt, n);
			}
			slot = i;
		}
		agents[slot] = newag;
		return Math.min(nagents + 1, maxAgents);
	}
	/**
	 * 加入计划列队
	 * 
	 * @param newag
	 * @param agents
	 * @param nagents
	 * @param maxAgents
	 * @return
	 */
	public static int AddToPlanQueue(final CrowdAgent newag, final CrowdAgent[] agents, final int nagents, final int maxAgents) {
		int slot = 0;
		if (nagents <= 0) {
			slot = 0;
		} else if (newag.TargetReplanTime <= agents[nagents - 1].TargetReplanTime) {
			if (nagents >= maxAgents) {
				return nagents;
			}
			slot = nagents;
		} else {
			int i;
			for (i = 0; i < nagents && newag.TargetReplanTime < agents[i].TargetReplanTime; ++i) {
			}
			final int tgt = i + 1;
			final int n = Math.min(nagents - i, maxAgents - tgt);
			if (n > 0) {
				System.arraycopy(agents, i, agents, tgt, n);
			}
			slot = i;
		}
		agents[slot] = newag;
		return Math.min(nagents + 1, maxAgents);
	}

	public float Tween(final float t, final float t0, final float t1) {
		return Math.max(0.0f, Math.min(1.0f, (t - t0) / (t1 - t0)));
	}

	static {
		Crowd.CrowdMaxObstAvoidanceParams = 8;
		Crowd.MaxPathQueueNodes = 4096;
		Crowd.MaxCommonNodes = 512;
		Crowd.MaxItersPerUpdate = 100;
	}
}
