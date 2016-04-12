// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;

import java.util.Random;
import atavism.server.math.IntVector2;
import atavism.server.objects.Vector2;
import java.util.EnumSet;
import atavism.server.util.Log;
import atavism.server.pathing.recast.Helper;

public class NavMeshQuery {
	public NodePool NodePool;
	public NavMesh NavMesh;
	public NodePool _tinyNodePool;
	public NodeQueue _openList;
	public QueryData _query;
	public static float HScale;
	public static short StraightPathStart;
	public static short StraightPathEnd;
	public static short StraightPathOffMeshConnection;
	public static short StraightPathAreaCrossings;
	public static short StraightPathAllCrossings;

	public Status Init(final NavMesh navMesh, final int maxNodes) {
		this.NavMesh = navMesh;
		if (this.NodePool == null || this.NodePool.MaxNodes < maxNodes) {
			this.NodePool = new NodePool(maxNodes, (int) Helper.NextPow2(maxNodes / 4));
		} else {
			this.NodePool.Clear();
		}
		if (this._tinyNodePool == null) {
			this._tinyNodePool = new NodePool(64, 32);
		} else {
			this._tinyNodePool.Clear();
		}
		if (this._openList == null || this._openList.Capacity < maxNodes) {
			this._openList = new NodeQueue(maxNodes);
		} else {
			this._openList.Clear();
		}
		return Status.Success;
	}

	public DetourStatusReturn FindPath(final long startRef, final long endRef, final float[] startPos, final float[] endPos, final QueryFilter filter, final long[] path, final int pathCount,
			final int maxPath) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
			if (this.NodePool == null) {
				throw new Exception("NodePool is not initialized");
			}
			if (this._openList == null) {
				throw new Exception("OpenList is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		if (startRef <= 0L || endRef <= 0L || maxPath <= 0) {
			statusReturn.status.add(Status.Failure);
			statusReturn.status.add(Status.InvalidParam);
			return statusReturn;
		}
		if (!this.NavMesh.IsValidPolyRef(startRef) || !this.NavMesh.IsValidPolyRef(endRef)) {
			statusReturn.status.add(Status.Failure);
			statusReturn.status.add(Status.InvalidParam);
			return statusReturn;
		}
		if (startRef == endRef) {
			path[0] = startRef;
			statusReturn.intValue = 1;
			statusReturn.status.add(Status.Success);
			return statusReturn;
		}
		this.NodePool.Clear();
		this._openList.Clear();
		final Node startNode = this.NodePool.GetNode(startRef);
		System.arraycopy(startPos, 0, startNode.Pos, 0, 3);
		startNode.PIdx = 0L;
		startNode.Cost = 0.0f;
		startNode.Total = Helper.VDist(startPos[0], startPos[1], startPos[2], endPos[0], endPos[1], endPos[2]) * NavMeshQuery.HScale;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeOpen;
		this._openList.Push(startNode);
		Node lastBestNode = startNode;
		float lastBestNodeCost = startNode.Total;
		statusReturn.status.add(Status.Success);
		while (!this._openList.Empty()) {
			final Node pop;
			final Node bestNode = pop = this._openList.Pop();
			pop.Flags &= ~Node.NodeOpen;
			final Node node2 = bestNode;
			node2.Flags |= Node.NodeClosed;
			if (bestNode.Id == endRef) {
				lastBestNode = bestNode;
				break;
			}
			final long bestRef = bestNode.Id;
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(bestRef);
			final MeshTile bestTile = tileAndPoly.tile;
			final Poly bestPoly = tileAndPoly.poly;
			long parentRef = 0L;
			MeshTile parentTile = null;
			Poly parentPoly = null;
			if (bestNode.PIdx > 0L) {
				parentRef = this.NodePool.GetNodeAtIdx(bestNode.PIdx).Id;
			}
			if (parentRef > 0L) {
				final DetourMeshTileAndPoly parentTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(parentRef);
				parentTile = parentTileAndPoly.tile;
				parentPoly = parentTileAndPoly.poly;
			}
			long i = bestPoly.FirstLink;
			while (true) {
				final long n2 = i;
				final NavMesh navMesh = this.NavMesh;
				if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final long neighborRef = bestTile.Links[(int) i].Ref;
				if (neighborRef > 0L) {
					if (neighborRef != parentRef) {
						final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(neighborRef);
						final MeshTile neighbourTile = neiTileAndPoly.tile;
						final Poly neighbourPoly = neiTileAndPoly.poly;
						if (filter.PassFilter(neighborRef, neighbourTile, neighbourPoly)) {
							final Node neighbourNode = this.NodePool.GetNode(neighborRef);
							if (neighbourNode == null) {
								statusReturn.status.add(Status.OutOfNodes);
							} else {
								if (neighbourNode.Flags == 0L) {
									final float[] pos = new float[3];
									this.GetEdgeMidPoint(bestRef, bestPoly, bestTile, neighborRef, neighbourPoly, neighbourTile, pos);
									System.arraycopy(pos, 0, neighbourNode.Pos, 0, 3);
								}
								float cost = 0.0f;
								float heuristic = 0.0f;
								if (neighborRef == endRef) {
									final float curCost = filter.GetCost(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighbourNode.Pos[0], neighbourNode.Pos[1], neighbourNode.Pos[2],
											parentRef, parentTile, parentPoly, bestRef, bestTile, bestPoly, neighborRef, neighbourTile, neighbourPoly);
									final float endCost = filter.GetCost(neighbourNode.Pos[0], neighbourNode.Pos[1], neighbourNode.Pos[2], endPos[0], endPos[1], endPos[2], bestRef, bestTile,
											bestPoly, neighborRef, neighbourTile, neighbourPoly, 0L, null, null);
									cost = bestNode.Cost + curCost + endCost;
									heuristic = 0.0f;
								} else {
									final float curCost = filter.GetCost(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighbourNode.Pos[0], neighbourNode.Pos[1], neighbourNode.Pos[2],
											parentRef, parentTile, parentPoly, bestRef, bestTile, bestPoly, neighborRef, neighbourTile, neighbourPoly);
									cost = bestNode.Cost + curCost;
									heuristic = Helper.VDist(neighbourNode.Pos[0], neighbourNode.Pos[1], neighbourNode.Pos[2], endPos[0], endPos[1], endPos[2]) * NavMeshQuery.HScale;
								}
								final float total = cost + heuristic;
								if ((neighbourNode.Flags & Node.NodeOpen) == 0x0L || total < neighbourNode.Total) {
									if ((neighbourNode.Flags & Node.NodeClosed) == 0x0L || total < neighbourNode.Total) {
										neighbourNode.PIdx = this.NodePool.GetNodeIdx(bestNode);
										neighbourNode.Id = neighborRef;
										neighbourNode.Flags &= ~Node.NodeClosed;
										neighbourNode.Cost = cost;
										neighbourNode.Total = total;
										if ((neighbourNode.Flags & Node.NodeOpen) != 0x0L) {
											this._openList.Modify(neighbourNode);
										} else {
											final Node node3 = neighbourNode;
											node3.Flags |= Node.NodeOpen;
											this._openList.Push(neighbourNode);
										}
										if (heuristic < lastBestNodeCost) {
											lastBestNodeCost = heuristic;
											lastBestNode = neighbourNode;
										}
									}
								}
							}
						}
					}
				}
				i = bestTile.Links[(int) i].Next;
			}
		}
		if (lastBestNode.Id != endRef) {
			statusReturn.status.add(Status.PartialResult);
		}
		Node prev = null;
		Node node = lastBestNode;
		do {
			final Node next = this.NodePool.GetNodeAtIdx(node.PIdx);
			node.PIdx = this.NodePool.GetNodeIdx(prev);
			prev = node;
			node = next;
		} while (node != null);
		node = prev;
		int n = 0;
		do {
			path[n++] = node.Id;
			if (n >= maxPath) {
				statusReturn.status.add(Status.BufferTooSmall);
				break;
			}
			node = this.NodePool.GetNodeAtIdx(node.PIdx);
		} while (node != null);
		statusReturn.intValue = n;
		return statusReturn;
	}

	public DetourStatusReturn FindStraightPath(final float[] startPos, final float[] endPos, final long[] path, final int pathSize, final float[] straightPath, final short[] straightPathFlags,
			final long[] straightPathRefs, final int maxStraightPath) {
		return this.FindStraightPath(startPos, endPos, path, pathSize, straightPath, straightPathFlags, straightPathRefs, maxStraightPath, 0);
	}

	public DetourStatusReturn FindStraightPath(final float[] startPos, final float[] endPos, final long[] path, final int pathSize, final float[] straightPath, final short[] straightPathFlags,
			final long[] straightPathRefs, final int maxStraightPath, final int options) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = 0;
		if (maxStraightPath <= 0) {
			statusReturn.status.add(Status.Failure);
			statusReturn.status.add(Status.InvalidParam);
			return statusReturn;
		}
		if (path[0] <= 0L) {
			statusReturn.status.add(Status.Failure);
			statusReturn.status.add(Status.InvalidParam);
			return statusReturn;
		}
		final float[] closestStartPos = new float[3];
		if (this.ClosestPointOnPolyBoundary(path[0], startPos, closestStartPos).contains(Status.Failure)) {
			statusReturn.status.add(Status.Failure);
			statusReturn.status.add(Status.InvalidParam);
			return statusReturn;
		}
		final float[] closestEndPos = new float[3];
		if (this.ClosestPointOnPolyBoundary(path[pathSize - 1], endPos, closestEndPos).contains(Status.Failure)) {
			statusReturn.status.add(Status.Failure);
			statusReturn.status.add(Status.InvalidParam);
			return statusReturn;
		}
		DetourStatusReturn sr = this.AppendVertex(closestStartPos, NavMeshQuery.StraightPathStart, path[0], straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath);
		if (!sr.status.contains(Status.InProgress)) {
			return sr;
		}
		statusReturn.intValue = sr.intValue;
		if (pathSize > 1) {
			final float[] portalApex = new float[3];
			final float[] portalLeft = new float[3];
			final float[] portalRight = new float[3];
			System.arraycopy(closestStartPos, 0, portalApex, 0, 3);
			System.arraycopy(portalApex, 0, portalLeft, 0, 3);
			System.arraycopy(portalApex, 0, portalRight, 0, 3);
			Log.debug("CORRIDOR: portalRight 1: " + portalRight[0] + "," + portalRight[1] + "," + portalRight[2]);
			int apexIndex = 0;
			int leftIndex = 0;
			int rightIndex = 0;
			short leftPolyType = 0;
			short rightPolyType = 0;
			long leftPolyRef = path[0];
			long rightPolyRef = path[0];
			for (int i = 0; i < pathSize; ++i) {
				final float[] left = new float[3];
				final float[] right = new float[3];
				short fromType = 0;
				short toType = 0;
				if (i + 1 < pathSize) {
					final DetourNumericReturn ppReturn = this.GetPortalPoints(path[i], path[i + 1], left, right);
					Log.debug("CORRIDOR: gotPortalPoints right 1: " + right[0] + "," + right[1] + "," + right[2] + " left 1: " + left[0] + "," + left[1] + "," + left[2]);
					fromType = (short) ppReturn.intValue;
					toType = (short) ppReturn.longValue;
					if (sr.status.contains(Status.Failure)) {
						if (this.ClosestPointOnPolyBoundary(path[i], endPos, closestEndPos).contains(Status.Failure)) {
							statusReturn.status.add(Status.Failure);
							statusReturn.status.add(Status.InvalidParam);
							return statusReturn;
						}
						if ((options & (NavMeshQuery.StraightPathAreaCrossings | NavMeshQuery.StraightPathAllCrossings)) != 0x0) {
							sr = this.AppendPortals(apexIndex, i, closestEndPos, path, straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath, options);
						}
						sr = this.AppendVertex(closestEndPos, (short) 0, path[i], straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath);
						statusReturn.intValue = sr.intValue;
						statusReturn.status.add(Status.Success);
						statusReturn.status.add(Status.PartialResult);
						if (statusReturn.intValue >= maxStraightPath) {
							statusReturn.status.add(Status.BufferTooSmall);
						}
						return statusReturn;
					} else if (i == 0 && Helper.DistancePtSegSqr2D(portalApex[0], portalApex[1], portalApex[2], left[0], left[1], left[2], right[0], right[1], right[2]).x < 1.0000001111620804E-6) {
						continue;
					}
				} else {
					System.arraycopy(closestEndPos, 0, left, 0, 3);
					System.arraycopy(closestEndPos, 0, right, 0, 3);
					Log.debug("CORRIDOR: right 2: " + right[0] + "," + right[1] + "," + right[2]);
					toType = (fromType = NavMeshBuilder.PolyTypeGround);
				}
				if (Helper.TriArea2D(portalApex, portalRight, right) <= 0.0f) {
					if (Helper.VEqual(portalApex[0], portalApex[1], portalApex[2], portalRight[0], portalRight[1], portalRight[2]) || Helper.TriArea2D(portalApex, portalLeft, right) > 0.0f) {
						System.arraycopy(right, 0, portalRight, 0, 3);
						rightPolyRef = ((i + 1 < pathSize) ? path[i + 1] : 0L);
						rightPolyType = toType;
						rightIndex = i;
					} else {
						if ((options & (NavMeshQuery.StraightPathAreaCrossings | NavMeshQuery.StraightPathAllCrossings)) != 0x0) {
							sr = this.AppendPortals(apexIndex, leftIndex, portalLeft, path, straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath, options);
							if (sr.status.contains(Status.InProgress)) {
								return sr;
							}
						}
						System.arraycopy(portalLeft, 0, portalApex, 0, 3);
						apexIndex = leftIndex;
						short flags = 0;
						if (leftPolyRef <= 0L) {
							flags = NavMeshQuery.StraightPathEnd;
						} else if (leftPolyType == NavMeshBuilder.PolyTypeOffMeshConnection) {
							flags = NavMeshQuery.StraightPathOffMeshConnection;
						}
						final long refId = leftPolyRef;
						Log.debug("CORRIDOR: apex: " + portalApex[0] + "," + portalApex[1] + "," + portalApex[2]);
						sr = this.AppendVertex(portalApex, flags, refId, straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath);
						if (sr.status.contains(Status.InProgress)) {
							return sr;
						}
						System.arraycopy(portalApex, 0, portalLeft, 0, 3);
						System.arraycopy(portalApex, 0, portalRight, 0, 3);
						Log.debug("CORRIDOR: portalRight 3: " + portalRight[0] + "," + portalRight[1] + "," + portalRight[2]);
						leftIndex = apexIndex;
						rightIndex = apexIndex;
						i = apexIndex;
						continue;
					}
				}
				if (Helper.TriArea2D(portalApex, portalLeft, left) >= 0.0f) {
					if (Helper.VEqual(portalApex[0], portalApex[1], portalApex[2], portalLeft[0], portalLeft[1], portalLeft[2]) || Helper.TriArea2D(portalApex, portalRight, left) < 0.0f) {
						System.arraycopy(left, 0, portalLeft, 0, 3);
						leftPolyRef = ((i + 1 < pathSize) ? path[i + 1] : 0L);
						leftPolyType = toType;
						leftIndex = i;
					} else {
						if ((options & (NavMeshQuery.StraightPathAreaCrossings | NavMeshQuery.StraightPathAllCrossings)) != 0x0) {
							sr = this.AppendPortals(apexIndex, rightIndex, portalRight, path, straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath, options);
							if (sr.status.contains(Status.InProgress)) {
								return sr;
							}
						}
						Log.debug("CORRIDOR: portalRight 4: " + portalRight[0] + "," + portalRight[1] + "," + portalRight[2]);
						System.arraycopy(portalRight, 0, portalApex, 0, 3);
						apexIndex = rightIndex;
						short flags = 0;
						if (rightPolyRef <= 0L) {
							flags = NavMeshQuery.StraightPathEnd;
						} else if (rightPolyType == NavMeshBuilder.PolyTypeOffMeshConnection) {
							flags = NavMeshQuery.StraightPathOffMeshConnection;
						}
						final long refId = rightPolyRef;
						sr = this.AppendVertex(portalApex, flags, refId, straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath);
						if (sr.status.contains(Status.InProgress)) {
							return sr;
						}
						System.arraycopy(portalApex, 0, portalLeft, 0, 3);
						System.arraycopy(portalApex, 0, portalRight, 0, 3);
						leftIndex = apexIndex;
						rightIndex = apexIndex;
						i = apexIndex;
					}
				}
			}
			if ((options & (NavMeshQuery.StraightPathAreaCrossings | NavMeshQuery.StraightPathAllCrossings)) != 0x0) {
				sr = this.AppendPortals(apexIndex, pathSize - 1, closestEndPos, path, straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath, options);
				if (sr.status.contains(Status.InProgress)) {
					return sr;
				}
			}
		}
		sr = this.AppendVertex(closestEndPos, NavMeshQuery.StraightPathEnd, 0L, straightPath, straightPathFlags, straightPathRefs, statusReturn.intValue, maxStraightPath);
		statusReturn.intValue = sr.intValue;
		statusReturn.status.add(Status.Success);
		if (statusReturn.intValue >= maxStraightPath) {
			statusReturn.status.add(Status.BufferTooSmall);
		}
		return statusReturn;
	}

	public EnumSet<Status> InitSlicedFindPath(final long startRef, final long endRef, final float[] startPos, final float[] endPos, final QueryFilter filter) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
			if (this.NodePool == null) {
				throw new Exception("NodePool is not initialized");
			}
			if (this._openList == null) {
				throw new Exception("OpenList is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this._query = new QueryData();
		this._query.Status = EnumSet.of(Status.Failure);
		this._query.StartRef = startRef;
		this._query.EndRef = endRef;
		System.arraycopy(startPos, 0, this._query.StartPos, 0, 3);
		System.arraycopy(endPos, 0, this._query.EndPos, 0, 3);
		this._query.Filter = filter;
		if (startRef <= 0L || endRef <= 0L) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		if (!this.NavMesh.IsValidPolyRef(startRef) || !this.NavMesh.IsValidPolyRef(endRef)) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		if (startRef == endRef) {
			this._query.Status = EnumSet.of(Status.Success);
			return EnumSet.of(Status.Success);
		}
		this.NodePool.Clear();
		this._openList.Clear();
		final Node startNode = this.NodePool.GetNode(startRef);
		System.arraycopy(startPos, 0, startNode.Pos, 0, 3);
		startNode.PIdx = 0L;
		startNode.Cost = 0.0f;
		startNode.Total = Helper.VDist(startPos[0], startPos[1], startPos[2], endPos[0], endPos[1], endPos[2]) * NavMeshQuery.HScale;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeOpen;
		this._openList.Push(startNode);
		this._query.Status = EnumSet.of(Status.InProgress);
		this._query.LastBestNode = startNode;
		this._query.LastBestNodeCost = startNode.Total;
		return this._query.Status;
	}

	public DetourStatusReturn UpdateSlicedFindPath(final int maxIter) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = 0;
		if (!this._query.Status.contains(Status.InProgress)) {
			statusReturn.status = this._query.Status;
			return statusReturn;
		}
		if (!this.NavMesh.IsValidPolyRef(this._query.StartRef) || !this.NavMesh.IsValidPolyRef(this._query.EndRef)) {
			this._query.Status = EnumSet.of(Status.Failure);
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		int iter = 0;
		while (iter < maxIter && !this._openList.Empty()) {
			++iter;
			final Node pop;
			final Node bestNode = pop = this._openList.Pop();
			pop.Flags &= ~Node.NodeOpen;
			final Node node = bestNode;
			node.Flags |= Node.NodeClosed;
			if (bestNode.Id == this._query.EndRef) {
				this._query.LastBestNode = bestNode;
				final EnumSet<Status> details = Status.MaskDetails(this._query.Status);
				details.add(Status.Success);
				this._query.Status = details;
				statusReturn.intValue = iter;
				statusReturn.status = this._query.Status;
				return statusReturn;
			}
			final long bestRef = bestNode.Id;
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(bestRef);
			if (tileAndPoly.status.contains(Status.Failure)) {
				this._query.Status = EnumSet.of(Status.Failure);
				statusReturn.intValue = iter;
				statusReturn.status = this._query.Status;
				return statusReturn;
			}
			final MeshTile bestTile = tileAndPoly.tile;
			final Poly bestPoly = tileAndPoly.poly;
			long parentRef = 0L;
			MeshTile parentTile = null;
			Poly parentPoly = null;
			if (bestNode.PIdx > 0L) {
				parentRef = this.NodePool.GetNodeAtIdx(bestNode.PIdx).Id;
			}
			if (parentRef > 0L) {
				final DetourMeshTileAndPoly parentTileAndPoly = this.NavMesh.GetTileAndPolyByRef(parentRef);
				if (parentTileAndPoly.status.contains(Status.Failure)) {
					this._query.Status = EnumSet.of(Status.Failure);
					statusReturn.intValue = iter;
					statusReturn.status = this._query.Status;
					return statusReturn;
				}
				parentTile = parentTileAndPoly.tile;
				parentPoly = parentTileAndPoly.poly;
			}
			long i = bestPoly.FirstLink;
			while (true) {
				final long n = i;
				final NavMesh navMesh = this.NavMesh;
				if (n == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final long neighborRef = bestTile.Links[(int) i].Ref;
				if (neighborRef > 0L) {
					if (neighborRef != parentRef) {
						final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(neighborRef);
						final MeshTile neighborTile = neiTileAndPoly.tile;
						final Poly neighborPoly = neiTileAndPoly.poly;
						if (this._query.Filter.PassFilter(neighborRef, neighborTile, neighborPoly)) {
							final Node neighborNode = this.NodePool.GetNode(neighborRef);
							if (neighborNode == null) {
								this._query.Status.add(Status.OutOfNodes);
							} else {
								if (neighborNode.Flags == 0L) {
									final float[] tempPos = new float[3];
									this.GetEdgeMidPoint(bestRef, bestPoly, bestTile, neighborRef, neighborPoly, neighborTile, tempPos);
									System.arraycopy(tempPos, 0, neighborNode.Pos, 0, 3);
								}
								float cost = 0.0f;
								float heuristic = 0.0f;
								if (neighborRef == this._query.EndRef) {
									final float curCost = this._query.Filter.GetCost(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2],
											parentRef, parentTile, parentPoly, bestRef, bestTile, bestPoly, neighborRef, neighborTile, neighborPoly);
									final float endCost = this._query.Filter.GetCost(neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2], this._query.EndPos[0], this._query.EndPos[1],
											this._query.EndPos[2], bestRef, bestTile, bestPoly, neighborRef, neighborTile, neighborPoly, 0L, null, null);
									cost = bestNode.Cost + curCost + endCost;
									heuristic = 0.0f;
								} else {
									final float curCost = this._query.Filter.GetCost(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2],
											parentRef, parentTile, parentPoly, bestRef, bestTile, bestPoly, neighborRef, neighborTile, neighborPoly);
									cost = bestNode.Cost + curCost;
									heuristic = Helper.VDist(neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2], this._query.EndPos[0], this._query.EndPos[1], this._query.EndPos[2])
											* NavMeshQuery.HScale;
								}
								final float total = cost + heuristic;
								if ((neighborNode.Flags & Node.NodeOpen) == 0x0L || total < neighborNode.Total) {
									if ((neighborNode.Flags & Node.NodeClosed) == 0x0L || total < neighborNode.Total) {
										neighborNode.PIdx = this.NodePool.GetNodeIdx(bestNode);
										neighborNode.Id = neighborRef;
										neighborNode.Flags &= ~Node.NodeClosed;
										neighborNode.Cost = cost;
										neighborNode.Total = total;
										if ((neighborNode.Flags & Node.NodeOpen) != 0x0L) {
											this._openList.Modify(neighborNode);
										} else {
											final Node node2 = neighborNode;
											node2.Flags |= Node.NodeOpen;
											this._openList.Push(neighborNode);
										}
										if (heuristic < this._query.LastBestNodeCost) {
											this._query.LastBestNodeCost = heuristic;
											this._query.LastBestNode = neighborNode;
										}
									}
								}
							}
						}
					}
				}
				i = bestTile.Links[(int) i].Next;
			}
		}
		if (this._openList.Empty()) {
			final EnumSet<Status> details2 = Status.MaskDetails(this._query.Status);
			details2.add(Status.Success);
			this._query.Status = details2;
		}
		statusReturn.intValue = iter;
		statusReturn.status = this._query.Status;
		return statusReturn;
	}

	public DetourStatusReturn FinalizeSlicedFindPath(final long[] path, final int maxPath) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = 0;
		if (this._query.Status.contains(Status.Failure)) {
			this._query = new QueryData();
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		int n = 0;
		if (this._query.StartRef == this._query.EndRef) {
			path[n++] = this._query.StartRef;
		} else {
			if (this._query.LastBestNode == null) {
				try {
					throw new Exception("Query has no last best node");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (this._query.LastBestNode.Id != this._query.EndRef) {
				this._query.Status.add(Status.PartialResult);
			}
			Node prev = null;
			Node node = this._query.LastBestNode;
			do {
				final Node next = this.NodePool.GetNodeAtIdx(node.PIdx);
				node.PIdx = this.NodePool.GetNodeIdx(prev);
				prev = node;
				node = next;
			} while (node != null);
			node = prev;
			do {
				path[n++] = node.Id;
				if (n >= maxPath) {
					this._query.Status.add(Status.BufferTooSmall);
					break;
				}
				node = this.NodePool.GetNodeAtIdx(node.PIdx);
			} while (node != null);
		}
		final EnumSet<Status> details = Status.MaskDetails(this._query.Status);
		this._query = new QueryData();
		statusReturn.intValue = n;
		details.add(Status.Success);
		statusReturn.status = details;
		return statusReturn;
	}
	/**
	 * 完成切片查找路径偏
	 * 
	 * @param existing
	 * @param existingSize
	 * @param path
	 * @param maxPath
	 * @return
	 */
	public DetourStatusReturn FinalizeSlicedFindPathPartial(final long[] existing, final int existingSize, final long[] path, final int maxPath) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = 0;
		if (existingSize == 0) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		if (this._query.Status.contains(Status.Failure)) {
			this._query = new QueryData();
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		int n = 0;
		if (this._query.StartRef == this._query.EndRef) {
			path[n++] = this._query.StartRef;
		} else {
			Node prev = null;
			Node node = null;
			for (int i = existingSize - 1; i >= 0; --i) {
				node = this.NodePool.FindNode(existing[i]);
				if (node != null) {
					break;
				}
			}
			if (node == null) {
				this._query.Status.add(Status.PartialResult);
				if (this._query.LastBestNode == null) {
					try {
						throw new Exception("Query Last Best Node is not initialized");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				node = this._query.LastBestNode;
			}
			do {
				final Node next = this.NodePool.GetNodeAtIdx(node.PIdx);
				node.PIdx = this.NodePool.GetNodeIdx(prev);
				prev = node;
				node = next;
			} while (node != null);
			node = prev;
			do {
				path[n++] = node.Id;
				if (n >= maxPath) {
					this._query.Status.add(Status.BufferTooSmall);
					break;
				}
				node = this.NodePool.GetNodeAtIdx(node.PIdx);
			} while (node != null);
		}
		final EnumSet details = Status.MaskDetails(this._query.Status);
		this._query = new QueryData();
		statusReturn.intValue = n;
		details.add(Status.Success);
		statusReturn.status = (EnumSet<Status>) details;
		return statusReturn;
	}

	public DetourStatusReturn FindPolysAroundCircle(final long startRef, final float[] centerPos, final float radius, final QueryFilter filter, final long[] resultRef, final long[] resultParent,
			final float[] resultCost, final int maxResult) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
			if (this.NodePool == null) {
				throw new Exception("NodePool is not initialized");
			}
			if (this._openList == null) {
				throw new Exception("OpenList is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		statusReturn.intValue = 0;
		if (startRef <= 0L || !this.NavMesh.IsValidPolyRef(startRef)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		this.NodePool.Clear();
		this._openList.Clear();
		final Node startNode = this.NodePool.GetNode(startRef);
		System.arraycopy(centerPos, 0, startNode.Pos, 0, 3);
		startNode.PIdx = 0L;
		startNode.Cost = 0.0f;
		startNode.Total = 0.0f;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeOpen;
		this._openList.Push(startNode);
		statusReturn.status = EnumSet.of(Status.Success);
		int n = 0;
		if (n < maxResult) {
			resultRef[n] = startNode.Id;
			resultParent[n] = 0L;
			resultCost[n] = 0.0f;
			++n;
		} else {
			statusReturn.status.add(Status.BufferTooSmall);
		}
		final float radiusSqr = radius * radius;
		while (!this._openList.Empty()) {
			final Node pop;
			final Node bestNode = pop = this._openList.Pop();
			pop.Flags &= ~Node.NodeOpen;
			final Node node = bestNode;
			node.Flags |= Node.NodeClosed;
			final long bestRef = bestNode.Id;
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(bestRef);
			final MeshTile bestTile = tileAndPoly.tile;
			final Poly bestPoly = tileAndPoly.poly;
			long parentRef = 0L;
			MeshTile parentTile = null;
			Poly parentPoly = null;
			if (bestNode.PIdx != 0L) {
				parentRef = this.NodePool.GetNodeAtIdx(bestNode.PIdx).Id;
			}
			if (parentRef > 0L) {
				final DetourMeshTileAndPoly parentTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(parentRef);
				parentTile = parentTileAndPoly.tile;
				parentPoly = parentTileAndPoly.poly;
			}
			long i = bestPoly.FirstLink;
			while (true) {
				final long n2 = i;
				final NavMesh navMesh = this.NavMesh;
				if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final Link link = bestTile.Links[(int) i];
				final long neighborRef = link.Ref;
				if (neighborRef > 0L) {
					if (neighborRef != parentRef) {
						final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(neighborRef);
						final MeshTile neighborTile = neiTileAndPoly.tile;
						final Poly neighborPoly = neiTileAndPoly.poly;
						if (filter.PassFilter(neighborRef, neighborTile, neighborPoly)) {
							final float[] va = new float[3];
							final float[] vb = new float[3];
							if (this.GetPortalPoints(bestRef, bestPoly, bestTile, neighborRef, neighborPoly, neighborTile, va, vb).contains(Status.Success)) {
								final float distSqr = (float) Helper.DistancePtSegSqr2D(centerPos[0], centerPos[1], centerPos[2], va[0], va[1], va[2], vb[0], vb[1], vb[2]).x;
								if (distSqr <= radiusSqr) {
									final Node neighborNode = this.NodePool.GetNode(neighborRef);
									if (neighborNode == null) {
										statusReturn.status.add(Status.OutOfNodes);
									} else if ((neighborNode.Flags & Node.NodeClosed) == 0x0L) {
										if (neighborNode.Flags == 0L) {
											float[] temp = new float[3];
											temp = Helper.VLerp(temp, va[0], va[1], va[2], vb[0], vb[1], vb[2], 0.5f);
											System.arraycopy(temp, 0, neighborNode.Pos, 0, 3);
										}
										final float total = bestNode.Total
												+ Helper.VDist(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2]);
										if ((neighborNode.Flags & Node.NodeOpen) == 0x0L || total < neighborNode.Total) {
											neighborNode.Id = neighborRef;
											neighborNode.Flags &= ~Node.NodeClosed;
											neighborNode.PIdx = this.NodePool.GetNodeIdx(bestNode);
											neighborNode.Total = total;
											if ((neighborNode.Flags & Node.NodeOpen) != 0x0L) {
												this._openList.Modify(neighborNode);
											} else {
												if (n < maxResult) {
													resultRef[n] = neighborNode.Id;
													resultParent[n] = this.NodePool.GetNodeAtIdx(neighborNode.PIdx).Id;
													resultCost[n] = neighborNode.Total;
													++n;
												} else {
													statusReturn.status.add(Status.BufferTooSmall);
												}
												neighborNode.Flags = Node.NodeOpen;
												this._openList.Push(neighborNode);
											}
										}
									}
								}
							}
						}
					}
				}
				i = bestTile.Links[(int) i].Next;
			}
		}
		statusReturn.intValue = n;
		return statusReturn;
	}

	public DetourStatusReturn FindPolysAroundShape(final long startRef, final float[] verts, final int nverts, final QueryFilter filter, final long[] resultRef, final long[] resultParent,
			final float[] resultCost, final int maxResult) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
			if (this.NodePool == null) {
				throw new Exception("NodePool is not initialized");
			}
			if (this._openList == null) {
				throw new Exception("OpenList is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		statusReturn.intValue = 0;
		if (startRef <= 0L || !this.NavMesh.IsValidPolyRef(startRef)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		this.NodePool.Clear();
		this._openList.Clear();
		float[] centerPos = {0.0f, 0.0f, 0.0f};
		for (int i = 0; i < nverts; ++i) {
			centerPos = Helper.VAdd(centerPos[0], centerPos[1], centerPos[2], verts[i * 3 + 0], verts[i * 3 + 1], verts[i * 3 + 2]);
		}
		centerPos = Helper.VScale(centerPos[0], centerPos[1], centerPos[2], 1.0f / nverts);
		final Node startNode = this.NodePool.GetNode(startRef);
		System.arraycopy(centerPos, 0, startNode.Pos, 0, 3);
		startNode.PIdx = 0L;
		startNode.Cost = 0.0f;
		startNode.Total = 0.0f;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeOpen;
		this._openList.Push(startNode);
		int n = 0;
		if (n < maxResult) {
			resultRef[n] = startNode.Id;
			resultParent[n] = 0L;
			resultCost[n] = 0.0f;
			++n;
		} else {
			statusReturn.status.add(Status.BufferTooSmall);
		}
		while (!this._openList.Empty()) {
			final Node pop;
			final Node bestNode = pop = this._openList.Pop();
			pop.Flags &= Node.NodeOpen;
			final Node node = bestNode;
			node.Flags |= Node.NodeClosed;
			final long bestRef = bestNode.Id;
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(bestRef);
			final MeshTile bestTile = tileAndPoly.tile;
			final Poly bestPoly = tileAndPoly.poly;
			long parentRef = 0L;
			MeshTile parentTile = null;
			Poly parentPoly = null;
			if (bestNode.PIdx > 0L) {
				parentRef = this.NodePool.GetNodeAtIdx(bestNode.PIdx).Id;
			}
			if (parentRef > 0L) {
				final DetourMeshTileAndPoly parentTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(parentRef);
				parentTile = parentTileAndPoly.tile;
				parentPoly = parentTileAndPoly.poly;
			}
			long j = bestPoly.FirstLink;
			while (true) {
				final long n2 = j;
				final NavMesh navMesh = this.NavMesh;
				if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final Link link = bestTile.Links[(int) j];
				final long neighborRef = link.Ref;
				if (neighborRef > 0L) {
					if (neighborRef != parentRef) {
						final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(neighborRef);
						final MeshTile neighborTile = neiTileAndPoly.tile;
						final Poly neighborPoly = neiTileAndPoly.poly;
						final float[] va = new float[3];
						final float[] vb = new float[3];
						if (this.GetPortalPoints(bestRef, bestPoly, bestTile, neighborRef, neighborPoly, neighborTile, va, vb).contains(Status.Success)) {
							final Vector2 tMinMax = new Vector2();
							final IntVector2 segMinMax = new IntVector2();
							if (Helper.IntersectSegmentPoly2D(va, vb, verts, nverts, tMinMax, segMinMax)) {
								if (tMinMax.x <= 1.0) {
									if (tMinMax.y >= 0.0) {
										final Node neighborNode = this.NodePool.GetNode(neighborRef);
										if (neighborNode == null) {
											statusReturn.status.add(Status.OutOfNodes);
										} else if ((neighborNode.Flags & Node.NodeClosed) == 0x0L) {
											if (neighborNode.Flags == 0L) {
												float[] temp = new float[3];
												temp = Helper.VLerp(temp, va[0], va[1], va[2], vb[0], vb[1], vb[2], 0.5f);
												System.arraycopy(temp, 0, neighborNode.Pos, 0, 3);
											}
											final float total = bestNode.Total
													+ Helper.VDist(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2]);
											if ((neighborNode.Flags & Node.NodeOpen) == 0x0L || total < neighborNode.Total) {
												neighborNode.Id = neighborRef;
												neighborNode.Flags &= ~Node.NodeClosed;
												neighborNode.PIdx = this.NodePool.GetNodeIdx(bestNode);
												neighborNode.Total = total;
												if ((neighborNode.Flags & Node.NodeOpen) != 0x0L) {
													this._openList.Modify(neighborNode);
												} else {
													if (n < maxResult) {
														resultRef[n] = neighborNode.Id;
														resultParent[n] = this.NodePool.GetNodeAtIdx(neighborNode.PIdx).Id;
														resultCost[n] = neighborNode.Total;
														++n;
													} else {
														statusReturn.status.add(Status.BufferTooSmall);
													}
													neighborNode.Flags = Node.NodeOpen;
													this._openList.Push(neighborNode);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				j = bestTile.Links[(int) j].Next;
			}
		}
		statusReturn.intValue = n;
		return statusReturn;
	}

	public DetourNumericReturn FindNearestPoly(final float[] center, final float[] extents, final QueryFilter filter, final float[] nearestPt) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourNumericReturn statusReturn = new DetourNumericReturn();
		final long[] polys = new long[128];
		final DetourStatusReturn sr = this.QueryPolygons(center, extents, filter, polys, 128);
		if (sr.status.contains(Status.Failure)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final int polyCount = sr.intValue;
		long nearest = 0L;
		float nearestDistanceSqr = Float.MAX_VALUE;
		for (final long refId : polys) {
			final float[] closestPtPoly = new float[3];
			this.ClosestPointOnPoly(refId, center, closestPtPoly);
			final float d = Helper.VDistSqr(center[0], center[1], center[2], closestPtPoly[0], closestPtPoly[1], closestPtPoly[2]);
			if (d < nearestDistanceSqr) {
				if (nearestPt != null) {
					System.arraycopy(closestPtPoly, 0, nearestPt, 0, 3);
				}
				nearestDistanceSqr = d;
				nearest = refId;
			}
		}
		statusReturn.longValue = nearest;
		statusReturn.status = EnumSet.of(Status.Success);
		return statusReturn;
	}

	public DetourStatusReturn QueryPolygons(final float[] center, final float[] extents, final QueryFilter filter, final long[] polys, final int maxPolys) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		final float[] bmin = Helper.VSub(center[0], center[1], center[2], extents[0], extents[1], extents[2]);
		final float[] bmax = Helper.VAdd(center[0], center[1], center[2], extents[0], extents[1], extents[2]);
		final IntVector2 min = this.NavMesh.CalcTileLoc(bmin[0], bmin[1], bmin[2]);
		final IntVector2 max = this.NavMesh.CalcTileLoc(bmax[0], bmax[1], bmax[2]);
		final int minx = min.x;
		final int miny = min.y;
		final int maxx = max.x;
		final int maxy = max.y;
		final int MaxNeis = 32;
		final MeshTile[] neis = new MeshTile[MaxNeis];
		int n = 0;
		for (int y = miny; y <= maxy; ++y) {
			for (int x = minx; x <= maxx; ++x) {
				for (int nneis = this.NavMesh.GetTilesAt(x, y, neis, MaxNeis), j = 0; j < nneis; ++j) {
					final long[] tempPolys = new long[maxPolys - n];
					final int tempn = this.QueryPolygonsInTile(neis[j], bmin, bmax, filter, tempPolys, maxPolys - n);
					for (int i = 0; i < tempn; ++i) {
						polys[n + i] = tempPolys[i];
					}
					n += tempn;
					if (n >= maxPolys) {
						statusReturn.intValue = n;
						statusReturn.status = EnumSet.of(Status.Success, Status.BufferTooSmall);
						return statusReturn;
					}
				}
			}
		}
		statusReturn.intValue = n;
		return statusReturn;
	}

	public DetourStatusReturn FindLocalNeighbourhood(final long startRef, final float[] centerPos, final float radius, final QueryFilter filter, final long[] resultRef, final long[] resultParent,
			final int maxResult) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
			if (this._tinyNodePool == null) {
				throw new Exception("tinyNodePool is not initialized");
			}
			if (filter == null) {
				throw new Exception("QueryFilter cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = 0;
		if (startRef <= 0L || !this.NavMesh.IsValidPolyRef(startRef)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final int MaxStack = 48;
		final Node[] stack = new Node[MaxStack];
		int nstack = 0;
		this._tinyNodePool.Clear();
		final Node startNode = this._tinyNodePool.GetNode(startRef);
		startNode.PIdx = 0L;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeClosed;
		stack[nstack++] = startNode;
		final float radiusSqr = radius * radius;
		final float[] pa = new float[NavMeshBuilder.VertsPerPoly * 3];
		final float[] pb = new float[NavMeshBuilder.VertsPerPoly * 3];
		statusReturn.status = EnumSet.of(Status.Success);
		int n = 0;
		if (n < maxResult) {
			resultRef[n] = startNode.Id;
			if (resultParent != null) {
				resultParent[n] = 0L;
			}
			++n;
		} else {
			statusReturn.status.add(Status.BufferTooSmall);
		}
		while (nstack > 0) {
			final Node curNode = stack[0];
			for (int i = 0; i < nstack - 1; ++i) {
				stack[i] = stack[i + 1];
			}
			--nstack;
			final long curRef = curNode.Id;
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(curRef);
			final MeshTile curTile = tileAndPoly.tile;
			final Poly curPoly = tileAndPoly.poly;
			long j = curPoly.FirstLink;
			while (true) {
				final long n2 = j;
				final NavMesh navMesh = this.NavMesh;
				if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final Link link = curTile.Links[(int) j];
				final long neighborRef = link.Ref;
				if (neighborRef > 0L) {
					final Node neighborNode = this._tinyNodePool.GetNode(neighborRef);
					if (neighborNode != null) {
						if ((neighborNode.Flags & Node.NodeClosed) == 0x0L) {
							final DetourMeshTileAndPoly neitileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(neighborRef);
							final MeshTile neighborTile = neitileAndPoly.tile;
							final Poly neighborPoly = neitileAndPoly.poly;
							if (neighborPoly.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
								if (filter.PassFilter(neighborRef, neighborTile, neighborPoly)) {
									final float[] va = new float[3];
									final float[] vb = new float[3];
									if (this.GetPortalPoints(curRef, curPoly, curTile, neighborRef, neighborPoly, neighborTile, va, vb).contains(Status.Success)) {
										final float distSqr = (float) Helper.DistancePtSegSqr2D(centerPos[0], centerPos[1], centerPos[2], va[0], va[1], va[2], vb[0], vb[1], vb[2]).x;
										if (distSqr <= radiusSqr) {
											final Node node = neighborNode;
											node.Flags |= Node.NodeClosed;
											neighborNode.PIdx = this._tinyNodePool.GetNodeIdx(curNode);
											final int npa = neighborPoly.VertCount;
											for (int k = 0; k < npa; ++k) {
												System.arraycopy(neighborTile.Verts, neighborPoly.Verts[k] * 3, pa, k * 3, 3);
											}
											Boolean overlap = false;
											for (final long pastRef : resultRef) {
												Boolean connected = false;
												long m = curPoly.FirstLink;
												while (true) {
													final long n3 = m;
													final NavMesh navMesh2 = this.NavMesh;
													if (n3 == atavism.server.pathing.detour.NavMesh.NullLink) {
														break;
													}
													if (curTile.Links[(int) m].Ref == pastRef) {
														connected = true;
														break;
													}
													m = curTile.Links[(int) m].Next;
												}
												if (!connected) {
													final DetourMeshTileAndPoly pastTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(pastRef);
													final MeshTile pastTile = pastTileAndPoly.tile;
													final Poly pastPoly = pastTileAndPoly.poly;
													Label_0807 : {
														if (pastTile != null) {
															if (pastPoly != null) {
																break Label_0807;
															}
														}
														try {
															throw new Exception("past is null");
														} catch (Exception e2) {
															e2.printStackTrace();
														}
													}
													final int npb = pastPoly.VertCount;
													for (int k2 = 0; k2 < npb; ++k2) {
														System.arraycopy(pastTile.Verts, pastPoly.Verts[k2] * 3, pb, k2 * 3, 3);
													}
													if (Helper.OverlapPolyPoly2D(pa, npa, pb, npb)) {
														overlap = true;
														break;
													}
												}
											}
											if (!overlap) {
												if (n < maxResult) {
													resultRef[n] = neighborRef;
													if (resultParent != null) {
														resultParent[n] = curRef;
													}
													++n;
												} else {
													statusReturn.status.add(Status.BufferTooSmall);
												}
												if (nstack < MaxStack) {
													stack[nstack++] = neighborNode;
												}
											}
										}
									}
								}
							}
						}
					}
				}
				j = curTile.Links[(int) j].Next;
			}
		}
		statusReturn.intValue = n;
		return statusReturn;
	}

	public DetourStatusReturn MoveAlongSurface(final long startRef, final float[] startPos, final float[] endPos, final QueryFilter filter, final float[] resultPos, final long[] visited,
			final int maxVisitedSize) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
			if (this._tinyNodePool == null) {
				throw new Exception("tinyNodePool is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = 0;
		if (startRef <= 0L) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		if (!this.NavMesh.IsValidPolyRef(startRef)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final Status status = Status.Success;
		final int MaxStack = 48;
		final Node[] stack = new Node[MaxStack];
		int nstack = 0;
		this._tinyNodePool.Clear();
		final Node startNode = this._tinyNodePool.GetNode(startRef);
		startNode.PIdx = 0L;
		startNode.Cost = 0.0f;
		startNode.Total = 0.0f;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeClosed;
		stack[nstack++] = startNode;
		float[] bestPos = new float[3];
		float bestDist = Float.MAX_VALUE;
		Node bestNode = null;
		System.arraycopy(startPos, 0, bestPos, 0, 3);
		float[] searchPos = new float[3];
		searchPos = Helper.VLerp(searchPos, startPos[0], startPos[1], startPos[2], endPos[0], endPos[1], endPos[2], 0.5f);
		float searchRadSqr = Helper.VDist(startPos[0], startPos[1], startPos[2], endPos[0], endPos[1], endPos[2]) / 2.0f + 0.001f;
		searchRadSqr *= searchRadSqr;
		final float[] verts = new float[NavMeshBuilder.VertsPerPoly * 3];
		while (nstack > 0) {
			final Node curNode = stack[0];
			for (int i = 0; i < nstack - 1; ++i) {
				stack[i] = stack[i + 1];
			}
			--nstack;
			final long curRef = curNode.Id;
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(curRef);
			final MeshTile curTile = tileAndPoly.tile;
			final Poly curPoly = tileAndPoly.poly;
			final int nverts = curPoly.VertCount;
			for (int j = 0; j < nverts; ++j) {
				System.arraycopy(curTile.Verts, curPoly.Verts[j] * 3, verts, j * 3, 3);
			}
			if (Helper.PointInPolygon(endPos[0], endPos[1], endPos[2], verts, nverts)) {
				bestNode = curNode;
				System.arraycopy(endPos, 0, bestPos, 0, 3);
				break;
			}
			int j = 0;
			int k = curPoly.VertCount - 1;
			while (j < curPoly.VertCount) {
				final int MaxNeis = 8;
				int nneis = 0;
				final long[] neis = new long[MaxNeis];
				if ((curPoly.Neis[k] & NavMeshBuilder.ExtLink) != 0x0) {
					long l = curPoly.FirstLink;
					while (true) {
						final long n2 = l;
						final NavMesh navMesh = this.NavMesh;
						if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
							break;
						}
						final Link link = curTile.Links[(int) l];
						if (link.Edge == k && link.Ref != 0L) {
							final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(link.Ref);
							final MeshTile neiTile = neiTileAndPoly.tile;
							final Poly neiPoly = neiTileAndPoly.poly;
							if (filter.PassFilter(link.Ref, neiTile, neiPoly) && nneis < MaxNeis) {
								neis[nneis++] = link.Ref;
							}
						}
						l = curTile.Links[(int) l].Next;
					}
				} else if (curPoly.Neis[k] > 0) {
					final long idx = curPoly.Neis[k] - 1;
					final long refId = this.NavMesh.GetPolyRefBase(curTile) | idx;
					if (filter.PassFilter(refId, curTile, curTile.Polys[(int) idx])) {
						neis[nneis++] = refId;
					}
				}
				if (nneis == 0) {
					final int vj = k * 3;
					final int vi = j * 3;
					final Vector2 distVect = Helper.DistancePtSegSqr2D(endPos[0], endPos[1], endPos[2], verts[vj + 0], verts[vj + 1], verts[vj + 2], verts[vi + 0], verts[vi + 1], verts[vi + 2]);
					final float distSqr = (float) distVect.x;
					final float tseg = (float) distVect.y;
					if (distSqr < bestDist) {
						bestPos = Helper.VLerp(bestPos, verts[vj + 0], verts[vj + 1], verts[vj + 2], verts[vi + 0], verts[vi + 1], verts[vi + 2], tseg);
						bestDist = distSqr;
						bestNode = curNode;
					}
				} else {
					for (int m = 0; m < nneis; ++m) {
						final Node neighborNode = this._tinyNodePool.GetNode(neis[m]);
						if (neighborNode != null) {
							if ((neighborNode.Flags & Node.NodeClosed) == 0x0L) {
								final int vj2 = k * 3;
								final int vi2 = j * 3;
								final Vector2 distSqr2 = Helper.DistancePtSegSqr2D(searchPos[0], searchPos[1], searchPos[2], verts[vj2 + 0], verts[vj2 + 1], verts[vj2 + 2], verts[vi2 + 0],
										verts[vi2 + 1], verts[vi2 + 2]);
								if (distSqr2.x <= searchRadSqr) {
									if (nstack < MaxStack) {
										neighborNode.PIdx = this._tinyNodePool.GetNodeIdx(curNode);
										final Node node2 = neighborNode;
										node2.Flags |= Node.NodeClosed;
										stack[nstack++] = neighborNode;
									}
								}
							}
						}
					}
				}
				k = j++;
			}
		}
		int n = 0;
		if (bestNode != null) {
			Node prev = null;
			Node node = bestNode;
			do {
				final Node next = this._tinyNodePool.GetNodeAtIdx(node.PIdx);
				node.PIdx = this._tinyNodePool.GetNodeIdx(prev);
				prev = node;
				node = next;
			} while (node != null);
			node = prev;
			do {
				visited[n++] = node.Id;
				if (n >= maxVisitedSize) {
					statusReturn.status.add(Status.BufferTooSmall);
				}
				node = this._tinyNodePool.GetNodeAtIdx(node.PIdx);
			} while (node != null);
		}
		System.arraycopy(bestPos, 0, resultPos, 0, 3);
		statusReturn.intValue = n;
		return statusReturn;
	}

	public DetourRaycastHit Raycast(final long startRef, final float[] startPos, final float[] endPos, final QueryFilter filter, float[] hitNormal, final long[] path, final int maxPath) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourRaycastHit statusReturn = new DetourRaycastHit();
		statusReturn.t = 0.0f;
		statusReturn.pathCount = 0;
		if (startRef <= 0L || !this.NavMesh.IsValidPolyRef(startRef)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		long curRef = startRef;
		final float[] verts = new float[3 * NavMeshBuilder.VertsPerPoly];
		int n = 0;
		hitNormal[0] = 0.0f;
		hitNormal[2] = (hitNormal[1] = 0.0f);
		statusReturn.status = EnumSet.of(Status.Success);
		while (curRef > 0L) {
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(curRef);
			final MeshTile tile = tileAndPoly.tile;
			final Poly poly = tileAndPoly.poly;
			int nv = 0;
			for (int i = 0; i < poly.VertCount; ++i) {
				System.arraycopy(tile.Verts, poly.Verts[i] * 3, verts, nv * 3, 3);
				++nv;
			}
			final Vector2 tMinMax = new Vector2();
			final IntVector2 segMinMax = new IntVector2();
			if (!Helper.IntersectSegmentPoly2D(startPos, endPos, verts, nv, tMinMax, segMinMax)) {
				statusReturn.pathCount = n;
				return statusReturn;
			}
			if (tMinMax.y > statusReturn.t) {
				statusReturn.t = (float) tMinMax.y;
			}
			if (n < maxPath) {
				path[n++] = curRef;
			} else {
				statusReturn.status.add(Status.BufferTooSmall);
			}
			if (segMinMax.y == -1) {
				statusReturn.t = Float.MAX_VALUE;
				statusReturn.pathCount = n;
				return statusReturn;
			}
			long nextRef = 0L;
			long j = poly.FirstLink;
			while (true) {
				final long n2 = j;
				final NavMesh navMesh = this.NavMesh;
				if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final Link link = tile.Links[(int) j];
				if (link.Edge == segMinMax.y) {
					final DetourMeshTileAndPoly nextTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(link.Ref);
					final MeshTile nextTile = nextTileAndPoly.tile;
					final Poly nextPoly = nextTileAndPoly.poly;
					if (nextPoly.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
						if (filter.PassFilter(link.Ref, nextTile, nextPoly)) {
							if (link.Side == 255) {
								nextRef = link.Ref;
								break;
							}
							if (link.BMin == 0 && link.BMax == 255) {
								nextRef = link.Ref;
								break;
							}
							final int left = poly.Verts[link.Edge] * 3;
							final int right = poly.Verts[(link.Edge + 1) % poly.VertCount] * 3;
							if (link.Side == 0 || link.Side == 4) {
								final float s = 0.003921569f;
								float lmin = tile.Verts[left + 2] + (tile.Verts[right + 2] - tile.Verts[left + 2]) * (link.BMin * s);
								float lmax = tile.Verts[left + 2] + (tile.Verts[right + 2] - tile.Verts[left + 2]) * (link.BMax * s);
								if (lmin > lmax) {
									final float temp = lmin;
									lmin = lmax;
									lmax = temp;
								}
								final float z = (float) (startPos[2] + (endPos[2] - startPos[2]) * tMinMax.y);
								if (z >= lmin && z <= lmax) {
									nextRef = link.Ref;
									break;
								}
							} else if (link.Side == 2 || link.Side == 6) {
								final float s = 0.003921569f;
								float lmin = tile.Verts[left + 0] + (tile.Verts[right + 0] - tile.Verts[left + 0]) * (link.BMin * s);
								float lmax = tile.Verts[left + 0] + (tile.Verts[right + 0] - tile.Verts[left + 0]) * (link.BMax * s);
								if (lmin > lmax) {
									final float temp = lmin;
									lmin = lmax;
									lmax = temp;
								}
								final float z = (float) (startPos[0] + (endPos[0] - startPos[0]) * tMinMax.y);
								if (z >= lmin && z <= lmax) {
									nextRef = link.Ref;
									break;
								}
							}
						}
					}
				}
				j = tile.Links[(int) j].Next;
			}
			if (nextRef == 0L) {
				final int a = segMinMax.y;
				final int b = (segMinMax.y + 1 < nv) ? (segMinMax.y + 1) : 0;
				final float dx = verts[b * 3 + 0] - verts[a * 3 + 0];
				final float dz = verts[b * 3 + 2] - verts[a * 3 + 2];
				hitNormal[0] = dz;
				hitNormal[1] = 0.0f;
				hitNormal[2] = -dx;
				hitNormal = Helper.VNormalize(hitNormal);
				statusReturn.pathCount = n;
				return statusReturn;
			}
			curRef = nextRef;
		}
		statusReturn.pathCount = n;
		return statusReturn;
	}

	public DetourNumericReturn FindDistanceToWall(final long startRef, final float[] centerPos, final float maxRadius, final QueryFilter filter, final float hitDist, final float[] hitPos,
			float[] hitNormal) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
			if (this.NodePool == null) {
				throw new Exception("NodePool is not initialized");
			}
			if (this._openList == null) {
				throw new Exception("OpenList is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourNumericReturn statusReturn = new DetourNumericReturn();
		if (startRef <= 0L || !this.NavMesh.IsValidPolyRef(startRef)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		this.NodePool.Clear();
		this._openList.Clear();
		final Node startNode = this.NodePool.GetNode(startRef);
		System.arraycopy(centerPos, 0, startNode.Pos, 0, 3);
		startNode.PIdx = 0L;
		startNode.Cost = 0.0f;
		startNode.Total = 0.0f;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeOpen;
		this._openList.Push(startNode);
		float radiusSqr = maxRadius * maxRadius;
		statusReturn.status = EnumSet.of(Status.Success);
		while (!this._openList.Empty()) {
			final Node pop;
			final Node bestNode = pop = this._openList.Pop();
			pop.Flags &= ~Node.NodeOpen;
			final Node node = bestNode;
			node.Flags |= Node.NodeClosed;
			final long bestRef = bestNode.Id;
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(bestRef);
			final MeshTile bestTile = tileAndPoly.tile;
			final Poly bestPoly = tileAndPoly.poly;
			long parentRef = 0L;
			MeshTile parentTile = null;
			Poly parentPoly = null;
			if (bestNode.PIdx > 0L) {
				parentRef = this.NodePool.GetNodeAtIdx(bestNode.PIdx).Id;
			}
			if (parentRef > 0L) {
				final DetourMeshTileAndPoly parentTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(parentRef);
				parentTile = parentTileAndPoly.tile;
				parentPoly = parentTileAndPoly.poly;
			}
			int i = 0;
			int j = bestPoly.VertCount - 1;
			while (i < bestPoly.VertCount) {
				Label_0849 : {
					if ((bestPoly.Neis[j] & NavMeshBuilder.ExtLink) != 0x0) {
						Boolean solid = true;
						long k = bestPoly.FirstLink;
						while (true) {
							final long n = k;
							final NavMesh navMesh = this.NavMesh;
							if (n >= atavism.server.pathing.detour.NavMesh.NullLink) {
								break;
							}
							final Link link = bestTile.Links[(int) k];
							if (link.Edge == j) {
								if (link.Ref != 0L) {
									final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(link.Ref);
									final MeshTile neiTile = neiTileAndPoly.tile;
									final Poly neiPoly = neiTileAndPoly.poly;
									if (filter.PassFilter(link.Ref, neiTile, neiPoly)) {
										solid = false;
									}
									break;
								}
								break;
							} else {
								k = bestTile.Links[(int) k].Next;
							}
						}
						if (!solid) {
							break Label_0849;
						}
					} else if (bestPoly.Neis[j] > 0) {
						final long idx = bestPoly.Neis[j] - 1;
						final long refId = this.NavMesh.GetPolyRefBase(bestTile) | idx;
						if (filter.PassFilter(refId, bestTile, bestTile.Polys[(int) idx])) {
							break Label_0849;
						}
					}
					final int vj = bestPoly.Verts[j] * 3;
					final int vi = bestPoly.Verts[i] * 3;
					final Vector2 distVec = Helper.DistancePtSegSqr2D(centerPos[0], centerPos[1], centerPos[2], bestTile.Verts[vj + 0], bestTile.Verts[vj + 1], bestTile.Verts[vj + 2],
							bestTile.Verts[vi + 0], bestTile.Verts[vi + 1], bestTile.Verts[vi + 2]);
					final float distSqr = (float) distVec.x;
					final float tseg = (float) distVec.y;
					if (distSqr <= radiusSqr) {
						radiusSqr = distSqr;
						hitPos[0] = bestTile.Verts[vj + 0] + (bestTile.Verts[vi + 0] - bestTile.Verts[vj + 0]) * tseg;
						hitPos[0] = bestTile.Verts[vj + 1] + (bestTile.Verts[vi + 1] - bestTile.Verts[vj + 1]) * tseg;
						hitPos[0] = bestTile.Verts[vj + 2] + (bestTile.Verts[vi + 2] - bestTile.Verts[vj + 2]) * tseg;
					}
				}
				j = i++;
			}
			long l = bestPoly.FirstLink;
			while (true) {
				final long n2 = l;
				final NavMesh navMesh2 = this.NavMesh;
				if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final Link link2 = bestTile.Links[(int) l];
				final long neighborRef = link2.Ref;
				if (neighborRef > 0L) {
					if (neighborRef != parentRef) {
						final DetourMeshTileAndPoly neiTileAndPoly2 = this.NavMesh.GetTileAndPolyByRefUnsafe(neighborRef);
						final MeshTile neighborTile = neiTileAndPoly2.tile;
						final Poly neighborPoly = neiTileAndPoly2.poly;
						if (neighborPoly.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
							final int va = bestPoly.Verts[link2.Edge] * 3;
							final int vb = bestPoly.Verts[(link2.Edge + 1) % bestPoly.VertCount] * 3;
							final Vector2 distSqr2 = Helper.DistancePtSegSqr2D(centerPos[0], centerPos[1], centerPos[2], bestTile.Verts[va + 0], bestTile.Verts[va + 1], bestTile.Verts[va + 2],
									bestTile.Verts[vb + 0], bestTile.Verts[vb + 1], bestTile.Verts[vb + 2]);
							if (distSqr2.x <= radiusSqr) {
								if (filter.PassFilter(neighborRef, neighborTile, neighborPoly)) {
									final Node neighborNode = this.NodePool.GetNode(neighborRef);
									if (neighborNode == null) {
										statusReturn.status.add(Status.OutOfNodes);
									} else if ((neighborNode.Flags & Node.NodeClosed) == 0x0L) {
										if (neighborNode.Flags == 0L) {
											final float[] temp = new float[3];
											this.GetEdgeMidPoint(bestRef, bestPoly, bestTile, neighborRef, neighborPoly, neighborTile, temp);
											System.arraycopy(temp, 0, neighborNode.Pos, 0, 3);
										}
										final float total = bestNode.Total
												+ Helper.VDist(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2]);
										if ((neighborNode.Flags & Node.NodeOpen) == 0x0L || total < neighborNode.Total) {
											neighborNode.Id = neighborRef;
											neighborNode.Flags &= ~Node.NodeClosed;
											neighborNode.PIdx = this.NodePool.GetNodeIdx(bestNode);
											neighborNode.Total = total;
											if ((neighborNode.Flags & Node.NodeOpen) != 0x0L) {
												this._openList.Modify(neighborNode);
											} else {
												final Node node2 = neighborNode;
												node2.Flags |= Node.NodeOpen;
												this._openList.Push(neighborNode);
											}
										}
									}
								}
							}
						}
					}
				}
				++l;
			}
		}
		hitNormal = Helper.VSub(centerPos[0], centerPos[1], centerPos[2], hitPos[0], hitPos[1], hitPos[2]);
		hitNormal = Helper.VNormalize(hitNormal);
		statusReturn.floatValue = (float) Math.sqrt(radiusSqr);
		return statusReturn;
	}

	public DetourStatusReturn GetPolyWallSegments(final long refId, final QueryFilter filter, final float[] segmentVerts, final long[] segmentRefs, final int maxSegments) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = 0;
		final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(refId);
		if (tileAndPoly.status.contains(Status.Failure)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final MeshTile tile = tileAndPoly.tile;
		final Poly poly = tileAndPoly.poly;
		int n = 0;
		final int MaxInterval = 16;
		final SegInterval[] ints = new SegInterval[MaxInterval];
		for (int i = 0; i < MaxInterval; ++i) {
			ints[i] = new SegInterval();
		}
		final Boolean storePortals = segmentRefs != null;
		statusReturn.status = EnumSet.of(Status.Success);
		int j = 0;
		int k = poly.VertCount - 1;
		while (j < poly.VertCount) {
			final int nints = 0;
			if ((poly.Neis[k] & NavMeshBuilder.ExtLink) != 0x0) {
				long l = poly.FirstLink;
				while (true) {
					final long n2 = l;
					final NavMesh navMesh = this.NavMesh;
					if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
						break;
					}
					final Link link = tile.Links[(int) l];
					if (link.Edge == k && link.Ref != 0L) {
						final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(link.Ref);
						final MeshTile neiTile = neiTileAndPoly.tile;
						final Poly neiPoly = neiTileAndPoly.poly;
						if (filter.PassFilter(link.Ref, neiTile, neiPoly)) {
							InsertInterval(ints, nints, MaxInterval, link.BMin, link.BMax, link.Ref);
						}
					}
					l = tile.Links[(int) l].Next;
				}
				InsertInterval(ints, nints, MaxInterval, (short) (-1), (short) 0, 0L);
				InsertInterval(ints, nints, MaxInterval, (short) 255, (short) 256, 0L);
				final int vj = poly.Verts[k] * 3;
				final int vi = poly.Verts[j] * 3;
				for (int m = 1; m < nints; ++m) {
					if (storePortals && ints[m].RefId > 0L) {
						final float tmin = ints[m].TMin / 255.0f;
						final float tmax = ints[m].TMax / 255.0f;
						if (n < maxSegments) {
							float[] temp = new float[3];
							temp = Helper.VLerp(temp, tile.Verts[vj + 0], tile.Verts[vj + 1], tile.Verts[vj + 2], tile.Verts[vi + 0], tile.Verts[vi + 1], tile.Verts[vi + 2], tmin);
							System.arraycopy(temp, 0, segmentVerts, n * 6, 3);
							temp = Helper.VLerp(temp, tile.Verts[vj + 0], tile.Verts[vj + 1], tile.Verts[vj + 2], tile.Verts[vi + 0], tile.Verts[vi + 1], tile.Verts[vi + 2], tmax);
							System.arraycopy(temp, 0, segmentVerts, n * 6 + 3, 3);
							if (segmentRefs != null) {
								segmentRefs[n] = ints[m].RefId;
							}
							++n;
						} else {
							statusReturn.status.add(Status.BufferTooSmall);
						}
					}
					final int imin = ints[m - 1].TMax;
					final int imax = ints[m].TMin;
					if (imin != imax) {
						final float tmin2 = imin / 255.0f;
						final float tmax2 = imax / 255.0f;
						if (n < maxSegments) {
							float[] temp2 = new float[3];
							temp2 = Helper.VLerp(temp2, tile.Verts[vj + 0], tile.Verts[vj + 1], tile.Verts[vj + 2], tile.Verts[vi + 0], tile.Verts[vi + 1], tile.Verts[vi + 2], tmin2);
							System.arraycopy(temp2, 0, segmentVerts, n * 6, 3);
							temp2 = Helper.VLerp(temp2, tile.Verts[vj + 0], tile.Verts[vj + 1], tile.Verts[vj + 2], tile.Verts[vi + 0], tile.Verts[vi + 1], tile.Verts[vi + 2], tmax2);
							System.arraycopy(temp2, 0, segmentVerts, n * 6 + 3, 3);
							if (segmentRefs != null) {
								segmentRefs[n] = 0L;
							}
							++n;
						} else {
							statusReturn.status.add(Status.BufferTooSmall);
						}
					}
				}
			} else {
				long neiRef = 0L;
				if (poly.Neis[k] > 0) {
					final long idx = poly.Neis[k] - 1;
					neiRef = (this.NavMesh.GetPolyRefBase(tile) | idx);
					if (!filter.PassFilter(neiRef, tile, tile.Polys[(int) idx])) {
						neiRef = 0L;
					}
				}
				if (neiRef == 0L || storePortals) {
					if (n < maxSegments) {
						final int vj2 = poly.Verts[k] * 3;
						final int vi2 = poly.Verts[j] * 3;
						System.arraycopy(tile.Verts, vj2, segmentVerts, n * 6, 3);
						System.arraycopy(tile.Verts, vi2, segmentVerts, n * 6 + 3, 3);
						if (segmentRefs != null) {
							segmentRefs[n] = neiRef;
						}
						++n;
					} else {
						statusReturn.status.add(Status.BufferTooSmall);
					}
				}
			}
			k = j++;
		}
		statusReturn.intValue = n;
		return statusReturn;
	}

	public static int InsertInterval(final SegInterval[] ints, int nints, final int maxInts, final short tmin, final short tmax, final long refId) {
		if (nints + 1 > maxInts) {
			return nints;
		}
		int idx;
		for (idx = 0; idx < nints && tmax > ints[idx].TMin; ++idx) {
		}
		if (nints - idx > 0) {
			System.arraycopy(ints, idx, ints, idx + 1, nints - idx);
		}
		ints[idx].RefId = refId;
		ints[idx].TMin = tmin;
		ints[idx].TMax = tmax;
		return ++nints;
	}

	public DetourNumericReturn FindRandomPoint(final QueryFilter filter, final Random func, final long randomRef, final float[] randomPt) {
		final DetourNumericReturn statusReturn = new DetourNumericReturn();
		if (this.NavMesh == null) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		MeshTile tile = null;
		float tsum = 0.0f;
		for (int i = 0; i < this.NavMesh.GetMaxTiles(); ++i) {
			final MeshTile temp = this.NavMesh.GetTile(i);
			if (temp != null) {
				if (temp.Header != null) {
					final float area = 1.0f;
					tsum += area;
					final float u = func.nextFloat();
					if (u * tsum <= area) {
						tile = temp;
					}
				}
			}
		}
		if (tile == null) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		Poly poly = null;
		long polyRef = 0L;
		final long baseRef = (int) this.NavMesh.GetPolyRefBase(tile);
		float areaSum = 0.0f;
		for (int j = 0; j < tile.Header.PolyCount; ++j) {
			final Poly p = tile.Polys[j];
			if (p.getType() == NavMeshBuilder.PolyTypeGround) {
				final long refId = baseRef | j;
				if (filter.PassFilter(refId, tile, p)) {
					float polyArea = 0.0f;
					for (int k = 2; k < p.VertCount; ++k) {
						final float[] va = new float[3];
						final float[] vb = new float[3];
						final float[] vc = new float[3];
						System.arraycopy(tile.Verts, p.Verts[0] * 3, va, 0, 3);
						System.arraycopy(tile.Verts, p.Verts[k - 1] * 3, vb, 0, 3);
						System.arraycopy(tile.Verts, p.Verts[k] * 3, vc, 0, 3);
						polyArea += Helper.TriArea2D(va, vb, vc);
					}
					areaSum += polyArea;
					final float u2 = func.nextFloat();
					if (u2 * areaSum <= polyArea) {
						poly = p;
						polyRef = refId;
					}
				}
			}
		}
		if (poly == null) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		int v = poly.Verts[0] * 3;
		final float[] verts = new float[3 * NavMeshBuilder.VertsPerPoly];
		final float[] areas = new float[NavMeshBuilder.VertsPerPoly];
		System.arraycopy(tile.Verts, v, verts, 0, 3);
		for (int l = 1; l < poly.VertCount; ++l) {
			v = poly.Verts[l] * 3;
			System.arraycopy(tile.Verts, v, verts, l * 3, 3);
		}
		final float s = func.nextFloat();
		final float t = func.nextFloat();
		final float[] pt = new float[3];
		Helper.RandomPointInConvexPoly(verts, poly.VertCount, areas, s, t, pt);
		final float h = 0.0f;
		final DetourNumericReturn status = this.GetPolyHeight(polyRef, pt, h);
		if (status.status.contains(Status.Failure)) {
			return status;
		}
		pt[1] = status.floatValue;
		System.arraycopy(pt, 0, randomPt, 0, 3);
		statusReturn.longValue = polyRef;
		statusReturn.status = EnumSet.of(Status.Success);
		return statusReturn;
	}

	public DetourNumericReturn FindRandomPointAroundCircle(final long startRef, final float[] centerPos, final float radius, final QueryFilter filter, final Random frand, final long randomRef,
			final float[] randomPt) {
		final DetourNumericReturn statusReturn = new DetourNumericReturn();
		if (this.NavMesh == null || this.NodePool == null || this._openList == null) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		if (startRef <= 0L || !this.NavMesh.IsValidPolyRef(startRef)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(startRef);
		final MeshTile startTile = tileAndPoly.tile;
		final Poly startPoly = tileAndPoly.poly;
		if (!filter.PassFilter(startRef, startTile, startPoly)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		this.NodePool.Clear();
		this._openList.Clear();
		final Node startNode = this.NodePool.GetNode(startRef);
		System.arraycopy(centerPos, 0, startNode.Pos, 0, 3);
		startNode.PIdx = 0L;
		startNode.Cost = 0.0f;
		startNode.Total = 0.0f;
		startNode.Id = startRef;
		startNode.Flags = Node.NodeOpen;
		this._openList.Push(startNode);
		statusReturn.status = EnumSet.of(Status.Success);
		final float radiusSqr = radius * radius;
		float areaSum = 0.0f;
		MeshTile randomTile = null;
		Poly randomPoly = null;
		long randomPolyRef = 0L;
		while (!this._openList.Empty()) {
			final Node pop;
			final Node bestNode = pop = this._openList.Pop();
			pop.Flags &= Node.NodeOpen;
			final Node node = bestNode;
			node.Flags |= Node.NodeClosed;
			final long bestRef = bestNode.Id;
			final DetourMeshTileAndPoly bestTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(bestRef);
			final MeshTile bestTile = bestTileAndPoly.tile;
			final Poly bestPoly = bestTileAndPoly.poly;
			if (bestPoly.getType() == NavMeshBuilder.PolyTypeGround) {
				float polyArea = 0.0f;
				for (int j = 2; j < bestPoly.VertCount; ++j) {
					final float[] va = new float[3];
					final float[] vb = new float[3];
					final float[] vc = new float[3];
					System.arraycopy(bestTile.Verts, bestPoly.Verts[0] * 3, va, 0, 3);
					System.arraycopy(bestTile.Verts, bestPoly.Verts[j - 1] * 3, vb, 0, 3);
					System.arraycopy(bestTile.Verts, bestPoly.Verts[j] * 3, vc, 0, 3);
					polyArea += Helper.TriArea2D(va, vb, vc);
				}
				areaSum += polyArea;
				final float u = frand.nextFloat();
				if (u * areaSum <= polyArea) {
					randomTile = bestTile;
					randomPoly = bestPoly;
					randomPolyRef = bestRef;
				}
			}
			long parentRef = 0L;
			MeshTile parentTile = null;
			Poly parentPoly = null;
			if (bestNode.PIdx > 0L) {
				parentRef = this.NodePool.GetNodeAtIdx(bestNode.PIdx).Id;
			}
			if (parentRef > 0L) {
				final DetourMeshTileAndPoly parentTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(parentRef);
				parentTile = parentTileAndPoly.tile;
				parentPoly = parentTileAndPoly.poly;
			}
			long i = bestPoly.FirstLink;
			while (true) {
				final long n = i;
				final NavMesh navMesh = this.NavMesh;
				if (n == atavism.server.pathing.detour.NavMesh.NullLink) {
					break;
				}
				final Link link = bestTile.Links[(int) i];
				final long neighborRef = link.Ref;
				if (neighborRef > 0L) {
					if (neighborRef != parentRef) {
						final DetourMeshTileAndPoly neiTileAndPoly = this.NavMesh.GetTileAndPolyByRefUnsafe(neighborRef);
						final MeshTile neighborTile = neiTileAndPoly.tile;
						final Poly neighborPoly = neiTileAndPoly.poly;
						if (filter.PassFilter(neighborRef, neighborTile, neighborPoly)) {
							final float[] va2 = new float[3];
							final float[] vb2 = new float[3];
							if (!this.GetPortalPoints(bestRef, bestPoly, bestTile, neighborRef, neighborPoly, neighborTile, va2, vb2).contains(Status.Failure)) {
								final Vector2 distSqr = Helper.DistancePtSegSqr2D(centerPos[0], centerPos[1], centerPos[2], va2[0], va2[1], va2[2], vb2[0], vb2[1], vb2[2]);
								if (distSqr.x <= radiusSqr) {
									final Node neighborNode = this.NodePool.GetNode(neighborRef);
									if (neighborNode == null) {
										statusReturn.status.add(Status.OutOfNodes);
									} else if ((neighborNode.Flags & Node.NodeClosed) == 0x0L) {
										if (neighborNode.Flags == 0L) {
											float[] pos = new float[3];
											pos = Helper.VLerp(pos, va2[0], va2[1], va2[2], vb2[0], vb2[1], vb2[2], 0.5f);
											System.arraycopy(pos, 0, neighborNode.Pos, 0, 3);
										}
										final float total = bestNode.Total
												+ Helper.VDist(bestNode.Pos[0], bestNode.Pos[1], bestNode.Pos[2], neighborNode.Pos[0], neighborNode.Pos[1], neighborNode.Pos[2]);
										if ((neighborNode.Flags & Node.NodeOpen) == 0x0L || total < neighborNode.Total) {
											neighborNode.Id = neighborRef;
											neighborNode.Flags &= ~Node.NodeClosed;
											neighborNode.PIdx = this.NodePool.GetNodeIdx(bestNode);
											neighborNode.Total = total;
											if ((neighborNode.Flags & Node.NodeOpen) != 0x0L) {
												this._openList.Modify(neighborNode);
											} else {
												neighborNode.Flags = Node.NodeOpen;
												this._openList.Push(neighborNode);
											}
										}
									}
								}
							}
						}
					}
				}
				i = bestTile.Links[(int) i].Next;
			}
		}
		if (randomPoly == null) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		int v = randomPoly.Verts[0] * 3;
		final float[] verts = new float[3 * NavMeshBuilder.VertsPerPoly];
		final float[] areas = new float[NavMeshBuilder.VertsPerPoly];
		System.arraycopy(randomTile.Verts, v, verts, 0, 3);
		for (int k = 1; k < randomPoly.VertCount; ++k) {
			v = randomPoly.Verts[k] * 3;
			System.arraycopy(randomTile.Verts, v, verts, k * 3, 3);
		}
		final float s = frand.nextFloat();
		final float t = frand.nextFloat();
		final float[] pt = new float[3];
		Helper.RandomPointInConvexPoly(verts, randomPoly.VertCount, areas, s, t, pt);
		final float h = 0.0f;
		final DetourNumericReturn stat = this.GetPolyHeight(randomPolyRef, pt, h);
		if (stat.status.contains(Status.Failure)) {
			return stat;
		}
		pt[1] = stat.floatValue;
		System.arraycopy(pt, 0, randomPt, 0, 3);
		statusReturn.longValue = randomPolyRef;
		statusReturn.status = EnumSet.of(Status.Success);
		return statusReturn;
	}

	public EnumSet<Status> ClosestPointOnPoly(final long refId, final float[] pos, final float[] closest) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(refId);
		if (tileAndPoly.status.contains(Status.Failure)) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final MeshTile tile = tileAndPoly.tile;
		final Poly poly = tileAndPoly.poly;
		if (tile == null) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		this.ClosestPointOnPolyInTile(tile, poly, pos, closest);
		return EnumSet.of(Status.Success);
	}

	public EnumSet<Status> ClosestPointOnPolyBoundary(final long refId, final float[] pos, float[] closest) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(refId);
		if (tileAndPoly.status.contains(Status.Failure)) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final MeshTile tile = tileAndPoly.tile;
		final Poly poly = tileAndPoly.poly;
		final float[] verts = new float[3 * NavMeshBuilder.VertsPerPoly];
		final float[] edged = new float[NavMeshBuilder.VertsPerPoly];
		final float[] edget = new float[NavMeshBuilder.VertsPerPoly];
		int nv = 0;
		for (int i = 0; i < poly.VertCount; ++i) {
			System.arraycopy(tile.Verts, poly.Verts[i] * 3, verts, nv * 3, 3);
			++nv;
		}
		final Boolean inside = Helper.DistancePtPolyEdgesSqr(pos[0], pos[1], pos[2], verts, nv, edged, edget);
		if (inside) {
			System.arraycopy(pos, 0, closest, 0, 3);
		} else {
			float dmin = Float.MAX_VALUE;
			int imin = -1;
			for (int j = 0; j < nv; ++j) {
				if (edged[j] < dmin) {
					dmin = edged[j];
					imin = j;
				}
			}
			final int va = imin * 3;
			final int vb = (imin + 1) % nv * 3;
			closest = Helper.VLerp(closest, verts[va + 0], verts[va + 1], verts[va + 2], verts[vb + 0], verts[vb + 1], verts[vb + 2], edget[imin]);
		}
		return EnumSet.of(Status.Success);
	}

	public DetourNumericReturn GetPolyHeight(final long refId, final float[] pos, final float height) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourNumericReturn statusReturn = new DetourNumericReturn();
		final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(refId);
		if (tileAndPoly.status.contains(Status.Failure)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final MeshTile tile = tileAndPoly.tile;
		final Poly poly = tileAndPoly.poly;
		if (poly.getType() == NavMeshBuilder.PolyTypeOffMeshConnection) {
			final int v0 = poly.Verts[0] * 3;
			final int v2 = poly.Verts[1] * 3;
			final float d0 = Helper.VDist(pos[0], pos[1], pos[2], tile.Verts[v0 + 0], tile.Verts[v0 + 1], tile.Verts[v0 + 2]);
			final float d2 = Helper.VDist(pos[0], pos[1], pos[2], tile.Verts[v2 + 0], tile.Verts[v2 + 1], tile.Verts[v2 + 2]);
			final float u = d0 / (d0 + d2);
			statusReturn.floatValue = tile.Verts[v0 + 1] + (tile.Verts[v2 + 1] - tile.Verts[v0 + 1]) * u;
			statusReturn.status = EnumSet.of(Status.Success);
			return statusReturn;
		}
		long ip = 0L;
		for (int i = 0; i < tile.Polys.length; ++i) {
			if (tile.Polys[i] == poly) {
				ip = i;
				break;
			}
		}
		final PolyDetail pd = tile.DetailMeshes[(int) ip];
		for (int j = 0; j < pd.TriCount; ++j) {
			final int t = ((int) pd.TriBase + j) * 4;
			final float[] v3 = new float[9];
			for (int k = 0; k < 3; ++k) {
				if (tile.DetailTris[t + k] < poly.VertCount) {
					System.arraycopy(tile.Verts, poly.Verts[tile.DetailTris[t + k]] * 3, v3, k * 3, 3);
				} else {
					System.arraycopy(tile.DetailVerts, (int) ((pd.VertBase + (tile.DetailTris[t + k] - poly.VertCount)) * 3L), v3, k * 3, 3);
				}
			}
			final DetourNumericReturn closestDist = Helper.ClosestHeightPointTriangle(pos[0], pos[1], pos[2], v3[0], v3[1], v3[2], v3[3], v3[4], v3[5], v3[6], v3[7], v3[8]);
			if (closestDist.boolValue) {
				statusReturn.floatValue = closestDist.floatValue;
				statusReturn.status = EnumSet.of(Status.Success);
				return statusReturn;
			}
		}
		statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
		return statusReturn;
	}
	/**
	 * 判断是否有效
	 */
	public Boolean IsValidPolyRef(final long refId, final QueryFilter filter) {
		final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(refId);
		if (tileAndPoly.status.contains(Status.Failure)) {
			return false;
		}
		final MeshTile tile = tileAndPoly.tile;
		final Poly poly = tileAndPoly.poly;
		if (!filter.PassFilter(refId, tile, poly)) {
			return false;
		}
		return true;
	}

	public Boolean IsInClosedList(final long refId) {
		if (this.NodePool == null) {
			return false;
		}
		final Node node = this.NodePool.FindNode(refId);
		return node != null && (node.Flags & Node.NodeClosed) != 0x0L;
	}

	public Boolean IsInOpenList(final long refId) {
		if (this.NodePool == null) {
			return false;
		}
		final Node node = this.NodePool.FindNode(refId);
		return node != null && (node.Flags & Node.NodeOpen) != 0x0L;
	}

	private int QueryPolygonsInTile(final MeshTile tile, final float[] qmin, final float[] qmax, final QueryFilter filter, final long[] polys, final int maxPolys) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (tile.BVTree != null) {
			int node = 0;
			final int end = tile.Header.BVNodeCount;
			final float[] tbmin = tile.Header.BMin;
			final float[] tbmax = tile.Header.BMax;
			final float qfac = tile.Header.BVQuantFactor;
			final int[] bmin = new int[3];
			final int[] bmax = new int[3];
			final float minx = Helper.Clamp(qmin[0], tbmin[0], tbmax[0]) - tbmin[0];
			final float miny = Helper.Clamp(qmin[1], tbmin[1], tbmax[1]) - tbmin[1];
			final float minz = Helper.Clamp(qmin[2], tbmin[2], tbmax[2]) - tbmin[2];
			final float maxx = Helper.Clamp(qmax[0], tbmin[0], tbmax[0]) - tbmin[0];
			final float maxy = Helper.Clamp(qmax[1], tbmin[1], tbmax[1]) - tbmin[1];
			final float maxz = Helper.Clamp(qmax[2], tbmin[2], tbmax[2]) - tbmin[2];
			bmin[0] = ((int) (qfac * minx) & 0xFFFE);
			bmin[1] = ((int) (qfac * miny) & 0xFFFE);
			bmin[2] = ((int) (qfac * minz) & 0xFFFE);
			bmax[0] = ((int) (qfac * maxx + 1.0f) | 0x1);
			bmax[1] = ((int) (qfac * maxy + 1.0f) | 0x1);
			bmax[2] = ((int) (qfac * maxz + 1.0f) | 0x1);
			final long baseRef = this.NavMesh.GetPolyRefBase(tile);
			int n = 0;
			while (node < end) {
				final Boolean overlap = Helper.OverlapQuantBounds(bmin, bmax, tile.BVTree[node].BMin, tile.BVTree[node].BMax);
				final Boolean isLeafNode = tile.BVTree[node].I >= 0;
				if (isLeafNode && overlap) {
					final long refId = baseRef | tile.BVTree[node].I;
					if (filter.PassFilter(refId, tile, tile.Polys[tile.BVTree[node].I]) && n < maxPolys) {
						polys[n++] = refId;
					}
				}
				if (overlap || isLeafNode) {
					++node;
				} else {
					final int escapeIndex = -tile.BVTree[node].I;
					node += escapeIndex;
				}
			}
			return n;
		}
		float[] bmin2 = new float[3];
		float[] bmax2 = new float[3];
		int n2 = 0;
		final long baseRef2 = this.NavMesh.GetPolyRefBase(tile);
		for (int i = 0; i < tile.Header.PolyCount; ++i) {
			final Poly p = tile.Polys[i];
			if (p.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
				final long refId2 = baseRef2 | i;
				if (filter.PassFilter(refId2, tile, p)) {
					int v = p.Verts[0] * 3;
					System.arraycopy(tile.Verts, v, bmin2, 0, 3);
					System.arraycopy(tile.Verts, v, bmax2, 0, 3);
					for (int j = 1; j < p.VertCount; ++j) {
						v = p.Verts[j] * 3;
						bmin2 = Helper.VMin(bmin2, tile.Verts[v + 0], tile.Verts[v + 1], tile.Verts[v + 2]);
						bmax2 = Helper.VMax(bmax2, tile.Verts[v + 0], tile.Verts[v + 1], tile.Verts[v + 2]);
					}
					if (Helper.OverlapBounds(qmin[0], qmin[1], qmin[2], qmax[0], qmax[1], qmax[2], bmin2[0], bmin2[1], bmin2[2], bmax2[0], bmax2[1], bmax2[2]) && n2 < maxPolys) {
						polys[n2++] = refId2;
					}
				}
			}
		}
		return n2;
	}

	private long FindNearestPolyInTile(final MeshTile tile, final float[] center, final float[] extents, final QueryFilter filter, final float[] nearestPt) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final float[] bmin = Helper.VSub(center[0], center[1], center[2], extents[0], extents[1], extents[2]);
		final float[] bmax = Helper.VAdd(center[0], center[1], center[2], extents[0], extents[1], extents[2]);
		final long[] polys = new long[128];
		final int polyCount = this.QueryPolygonsInTile(tile, bmin, bmax, filter, polys, 128);
		long nearest = 0L;
		float nearestDistanceSqr = Float.MAX_VALUE;
		for (final long refId : polys) {
			final Poly poly = tile.Polys[(int) this.NavMesh.DecodePolyIdPoly(refId)];
			final float[] closestPtPoly = new float[3];
			this.ClosestPointOnPolyInTile(tile, poly, center, closestPtPoly);
			final float d = Helper.VDistSqr(center[0], center[1], center[2], closestPtPoly[0], closestPtPoly[1], closestPtPoly[2]);
			if (d < nearestDistanceSqr) {
				System.arraycopy(closestPtPoly, 0, nearestPt, 0, 3);
				nearestDistanceSqr = d;
				nearest = refId;
			}
		}
		return nearest;
	}

	private void ClosestPointOnPolyInTile(final MeshTile tile, final Poly poly, final float[] pos, float[] closest) {
		if (poly.getType() == NavMeshBuilder.PolyTypeOffMeshConnection) {
			final int v0 = poly.Verts[0] * 3;
			final int v2 = poly.Verts[1] * 3;
			final float d0 = Helper.VDist(pos[0], pos[1], pos[2], tile.Verts[v0 + 0], tile.Verts[v0 + 1], tile.Verts[v0 + 2]);
			final float d2 = Helper.VDist(pos[0], pos[1], pos[2], tile.Verts[v2 + 0], tile.Verts[v2 + 1], tile.Verts[v2 + 2]);
			final float u = d0 / (d0 + d2);
			closest = Helper.VLerp(closest, tile.Verts[v0 + 0], tile.Verts[v0 + 1], tile.Verts[v0 + 2], tile.Verts[v2 + 0], tile.Verts[v2 + 1], tile.Verts[v2 + 2], u);
			return;
		}
		long ip = 0L;
		for (int i = 0; i < tile.Polys.length; ++i) {
			if (tile.Polys[i] == poly) {
				ip = i;
				break;
			}
		}
		final PolyDetail pd = tile.DetailMeshes[(int) ip];
		final float[] verts = new float[3 * NavMeshBuilder.VertsPerPoly];
		final float[] edged = new float[NavMeshBuilder.VertsPerPoly];
		final float[] edget = new float[NavMeshBuilder.VertsPerPoly];
		final int nv = poly.VertCount;
		if (poly.VertCount == 0) {
			Log.warn("NAVMESH: got 0 vert count for poly: " + poly.getArea() + " from tile: " + tile.Header.X + "/" + tile.Header.Y);
		}
		for (int j = 0; j < nv; ++j) {
			System.arraycopy(tile.Verts, poly.Verts[j] * 3, verts, j * 3, 3);
		}
		System.arraycopy(pos, 0, closest, 0, 3);
		if (!Helper.DistancePtPolyEdgesSqr(pos[0], pos[1], pos[2], verts, nv, edged, edget)) {
			float dmin = Float.MAX_VALUE;
			int imin = -1;
			for (int k = 0; k < nv; ++k) {
				if (edged[k] < dmin) {
					dmin = edged[k];
					imin = k;
				}
			}
			final int va = imin * 3;
			final int vb = (imin + 1) % nv * 3;
			closest = Helper.VLerp(closest, verts[va + 0], verts[va + 1], verts[va + 2], verts[vb + 0], verts[vb + 1], verts[vb + 2], edget[imin]);
		}
		for (int l = 0; l < pd.TriCount; ++l) {
			final int t = ((int) pd.TriBase + l) * 4;
			final float[] v3 = new float[9];
			for (int m = 0; m < 3; ++m) {
				if (tile.DetailTris[t + m] < poly.VertCount) {
					System.arraycopy(tile.Verts, poly.Verts[tile.DetailTris[t + m]] * 3, v3, m * 3, 3);
				} else {
					System.arraycopy(tile.DetailVerts, (int) (pd.VertBase + (tile.DetailTris[t + m] - poly.VertCount)) * 3, v3, m * 3, 3);
				}
			}
			final DetourNumericReturn closestDist = Helper.ClosestHeightPointTriangle(pos[0], pos[1], pos[2], v3[0], v3[1], v3[2], v3[3], v3[4], v3[5], v3[6], v3[7], v3[8]);
			if (closestDist.boolValue) {
				closest[1] = closestDist.floatValue;
				break;
			}
		}
	}

	private DetourNumericReturn GetPortalPoints(final long from, final long to, final float[] left, final float[] right) {
		try {
			if (this.NavMesh == null) {
				throw new Exception("NavMesh is not initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final DetourNumericReturn statusReturn = new DetourNumericReturn();
		final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(from);
		if (tileAndPoly.status.contains(Status.Failure)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final MeshTile fromTile = tileAndPoly.tile;
		final Poly fromPoly = tileAndPoly.poly;
		statusReturn.intValue = fromPoly.getType();
		final DetourMeshTileAndPoly toTileAndPoly = this.NavMesh.GetTileAndPolyByRef(to);
		if (toTileAndPoly.status.contains(Status.Failure)) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final MeshTile toTile = toTileAndPoly.tile;
		final Poly toPoly = toTileAndPoly.poly;
		statusReturn.longValue = toPoly.getType();
		statusReturn.status = this.GetPortalPoints(from, fromPoly, fromTile, to, toPoly, toTile, left, right);
		return statusReturn;
	}

	private EnumSet<Status> GetPortalPoints(final long from, final Poly fromPoly, final MeshTile fromTile, final long to, final Poly toPoly, final MeshTile toTile, float[] left, float[] right) {
		Link link = null;
		long i = fromPoly.FirstLink;
		while (true) {
			final long n = i;
			final NavMesh navMesh = this.NavMesh;
			if (n == atavism.server.pathing.detour.NavMesh.NullLink) {
				break;
			}
			if (fromTile.Links[(int) i].Ref == to) {
				link = fromTile.Links[(int) i];
				break;
			}
			i = fromTile.Links[(int) i].Next;
		}
		if (link == null) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		if (fromPoly.getType() == NavMeshBuilder.PolyTypeOffMeshConnection) {
			i = fromPoly.FirstLink;
			while (true) {
				final long n2 = i;
				final NavMesh navMesh2 = this.NavMesh;
				if (n2 == atavism.server.pathing.detour.NavMesh.NullLink) {
					return EnumSet.of(Status.Failure, Status.InvalidParam);
				}
				if (fromTile.Links[(int) i].Ref == to) {
					final int v = fromTile.Links[(int) i].Edge;
					System.arraycopy(fromTile.Verts, fromPoly.Verts[v] * 3, left, 0, 3);
					System.arraycopy(fromTile.Verts, fromPoly.Verts[v] * 3, right, 0, 3);
					return EnumSet.of(Status.Success);
				}
				i = fromTile.Links[(int) i].Next;
			}
		} else {
			if (toPoly.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
				final int v2 = fromPoly.Verts[link.Edge];
				final int v3 = fromPoly.Verts[(link.Edge + 1) % fromPoly.VertCount];
				System.arraycopy(fromTile.Verts, v2 * 3, left, 0, 3);
				System.arraycopy(fromTile.Verts, v3 * 3, right, 0, 3);
				if (link.Side != 255 && (link.BMin != 0 || link.BMax != 255)) {
					final float s = 0.003921569f;
					final float tmin = link.BMin * s;
					final float tmax = link.BMax * s;
					left = Helper.VLerp(left, fromTile.Verts[v2 * 3 + 0], fromTile.Verts[v2 * 3 + 1], fromTile.Verts[v2 * 3 + 2], fromTile.Verts[v3 + 3 + 0], fromTile.Verts[v3 + 3 + 1],
							fromTile.Verts[v3 + 3 + 2], tmin);
					right = Helper.VLerp(right, fromTile.Verts[v2 * 3 + 0], fromTile.Verts[v2 * 3 + 1], fromTile.Verts[v2 * 3 + 2], fromTile.Verts[v3 + 3 + 0], fromTile.Verts[v3 + 3 + 1],
							fromTile.Verts[v3 + 3 + 2], tmax);
				}
				return EnumSet.of(Status.Success);
			}
			i = toPoly.FirstLink;
			while (true) {
				final long n3 = i;
				final NavMesh navMesh3 = this.NavMesh;
				if (n3 == atavism.server.pathing.detour.NavMesh.NullLink) {
					return EnumSet.of(Status.Failure, Status.InvalidParam);
				}
				if (toTile.Links[(int) i].Ref == from) {
					final int v = toTile.Links[(int) i].Edge;
					System.arraycopy(toTile.Verts, toPoly.Verts[v] * 3, left, 0, 3);
					System.arraycopy(toTile.Verts, toPoly.Verts[v] * 3, right, 0, 3);
					return EnumSet.of(Status.Failure, Status.InvalidParam);
				}
				i = toTile.Links[(int) i].Next;
			}
		}
	}

	private EnumSet<Status> GetEdgeMidPoint(final long from, final long to, final float[] mid) {
		final float[] left = new float[3];
		final float[] right = new float[3];
		final DetourNumericReturn statusReturn = this.GetPortalPoints(from, to, left, right);
		if (statusReturn.status.contains(Status.Failure)) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		mid[0] = (left[0] + right[0]) * 0.5f;
		mid[1] = (left[1] + right[1]) * 0.5f;
		mid[2] = (left[2] + right[2]) * 0.5f;
		return EnumSet.of(Status.Success);
	}

	private EnumSet<Status> GetEdgeMidPoint(final long from, final Poly fromPoly, final MeshTile fromTile, final long to, final Poly toPoly, final MeshTile toTile, final float[] mid) {
		final float[] left = new float[3];
		final float[] right = new float[3];
		if (this.GetPortalPoints(from, fromPoly, fromTile, to, toPoly, toTile, left, right).contains(Status.Failure)) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		mid[0] = (left[0] + right[0]) * 0.5f;
		mid[1] = (left[1] + right[1]) * 0.5f;
		mid[2] = (left[2] + right[2]) * 0.5f;
		return EnumSet.of(Status.Success);
	}

	private DetourStatusReturn AppendVertex(final float[] pos, final short flags, final long refId, final float[] straightPath, final short[] straightPathFlags, final long[] straightPathRefs,
			int straightPathCount, final int maxStraightPath) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		if (straightPathCount > 0
				&& Helper.VEqual(straightPath[(straightPathCount - 1) * 3 + 0], straightPath[(straightPathCount - 1) * 3 + 1], straightPath[(straightPathCount - 1) * 3 + 2], pos[0], pos[1], pos[2])) {
			straightPathFlags[straightPathCount - 1] = flags;
			straightPathRefs[straightPathCount - 1] = refId;
		} else {
			System.arraycopy(pos, 0, straightPath, straightPathCount * 3, 3);
			straightPathFlags[straightPathCount] = flags;
			straightPathRefs[straightPathCount] = refId;
			++straightPathCount;
			if (flags == NavMeshQuery.StraightPathEnd || straightPathCount >= maxStraightPath) {
				statusReturn.intValue = straightPathCount;
				statusReturn.status = EnumSet.of(Status.Success);
				if (straightPathCount >= maxStraightPath) {
					statusReturn.status.add(Status.BufferTooSmall);
				}
				return statusReturn;
			}
		}
		statusReturn.intValue = straightPathCount;
		statusReturn.status = EnumSet.of(Status.InProgress);
		return statusReturn;
	}

	private DetourStatusReturn AppendPortals(final int startIdx, final int endIdx, final float[] endPos, final long[] path, final float[] straightPath, final short[] straightPathFlags,
			final long[] straightPathRefs, final int straightPathCount, final int maxStraightPath, final int options) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		statusReturn.intValue = straightPathCount;
		final int startPos = (straightPathCount - 1) * 3;
		for (int i = startIdx; i < endIdx; ++i) {
			final long from = path[i];
			final DetourMeshTileAndPoly tileAndPoly = this.NavMesh.GetTileAndPolyByRef(from);
			if (tileAndPoly.status.contains(Status.Failure)) {
				statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
				return statusReturn;
			}
			final MeshTile fromTile = tileAndPoly.tile;
			final Poly fromPoly = tileAndPoly.poly;
			final long to = path[i + 1];
			final DetourMeshTileAndPoly toTileAndPoly = this.NavMesh.GetTileAndPolyByRef(to);
			if (toTileAndPoly.status.contains(Status.Failure)) {
				statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
				return statusReturn;
			}
			final MeshTile toTile = toTileAndPoly.tile;
			final Poly toPoly = toTileAndPoly.poly;
			final float[] left = new float[3];
			final float[] right = new float[3];
			if (this.GetPortalPoints(from, fromPoly, fromTile, to, toPoly, toTile, left, right).contains(Status.Failure)) {
				break;
			}
			if ((options & NavMeshQuery.StraightPathAreaCrossings) == 0x0 || fromPoly.getArea() != toPoly.getArea()) {
				final DetourNumericReturn segIntersected = Helper.IntersectSegSeg2D(straightPath[startPos + 0], straightPath[startPos + 1], straightPath[startPos + 2], endPos[0], endPos[1],
						endPos[2], left, right);
				if (segIntersected.boolValue) {
					float[] pt = new float[3];
					pt = Helper.VLerp(pt, left[0], left[1], left[2], right[0], right[1], right[2], (float) segIntersected.vector2Value.y);
					final DetourStatusReturn stat = this.AppendVertex(pt, (short) 0, path[i + 1], straightPath, straightPathFlags, straightPathRefs, straightPathCount, maxStraightPath);
					if (stat.status.contains(Status.InProgress)) {
						return stat;
					}
				}
			}
		}
		statusReturn.status = EnumSet.of(Status.InProgress);
		return statusReturn;
	}

	static {
		NavMeshQuery.HScale = 0.999f;
		NavMeshQuery.StraightPathStart = 1;
		NavMeshQuery.StraightPathEnd = 2;
		NavMeshQuery.StraightPathOffMeshConnection = 4;
		NavMeshQuery.StraightPathAreaCrossings = 1;
		NavMeshQuery.StraightPathAllCrossings = 2;
	}
}
