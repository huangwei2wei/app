// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.crowd;

import java.util.EnumSet;
import com.app.server.atavism.server.pathing.recast.Helper;
import com.app.server.atavism.server.pathing.detour.QueryFilter;
import com.app.server.atavism.server.pathing.detour.DetourStatusReturn;
import com.app.server.atavism.server.pathing.detour.Status;
import com.app.server.atavism.server.pathing.detour.NavMesh;
import com.app.server.atavism.server.pathing.detour.NavMeshQuery;

public class PathQueue
{
    public static final long PathQInvalid = 0L;
    private final int MaxQueue = 8;
    private PathQuery[] _queue;
    private long _nextHandle;
    private int _maxPathSize;
    private int _queueHead;
    private NavMeshQuery _navQuery;
    
    public PathQueue() {
        this._nextHandle = 1L;
        this._maxPathSize = 0;
        this._queueHead = 0;
        this._navQuery = null;
        this._queue = new PathQuery[8];
        for (int i = 0; i < 8; ++i) {
            this._queue[i] = new PathQuery();
            this._queue[i].path = null;
        }
    }
    
    private void Purge() {
        this._navQuery = null;
        for (int i = 0; i < 8; ++i) {
            this._queue[i].path = null;
        }
    }
    
    public Boolean Init(final int maxPathSize, final int maxSearchNodeCount, final NavMesh nav) {
        this.Purge();
        this._navQuery = new NavMeshQuery();
        if ((this._navQuery.Init(nav, maxSearchNodeCount).getValue() & Status.Failure.getValue()) != 0x0) {
            return false;
        }
        this._maxPathSize = maxPathSize;
        for (int i = 0; i < 8; ++i) {
            this._queue[i].refId = 0L;
            this._queue[i].path = new long[this._maxPathSize];
        }
        this._queueHead = 0;
        return true;
    }
    
    public void Update(final int maxIters) {
        final int MaxKeepAlive = 2;
        int iterCount = maxIters;
        for (int i = 0; i < 8; ++i) {
            final PathQuery q = this._queue[this._queueHead % 8];
            if (q.refId == 0L) {
                ++this._queueHead;
            }
            else if (q.status != null && (q.status.contains(Status.Success) || q.status.contains(Status.Failure))) {
                final PathQuery pathQuery = q;
                ++pathQuery.keepAlive;
                if (q.keepAlive > MaxKeepAlive) {
                    q.refId = 0L;
                    q.status = null;
                }
                ++this._queueHead;
            }
            else {
                if (q.status == null) {
                    q.status = this._navQuery.InitSlicedFindPath(q.startRef, q.endRef, q.startPos, q.endPos, q.filter);
                }
                if (q.status.contains(Status.InProgress)) {
                    final DetourStatusReturn statusReturn = this._navQuery.UpdateSlicedFindPath(iterCount);
                    q.status = statusReturn.status;
                    iterCount -= statusReturn.intValue;
                }
                if (q.status.contains(Status.Success)) {
                    q.status = this._navQuery.FinalizeSlicedFindPath(q.path, this._maxPathSize).status;
                }
                if (iterCount <= 0) {
                    break;
                }
                ++this._queueHead;
            }
        }
    }
    
    public long Request(final long startRef, final long endRef, final float[] startPos, final float[] endPos, final QueryFilter filter) {
        int slot = -1;
        for (int i = 0; i < 8; ++i) {
            if (this._queue[i].refId == 0L) {
                slot = i;
                break;
            }
        }
        if (slot == -1) {
            return 0L;
        }
        final long refId = this._nextHandle++;
        if (this._nextHandle == 0L) {
            ++this._nextHandle;
        }
        final PathQuery q = this._queue[slot];
        q.refId = refId;
        Helper.VCopy(q.startPos, startPos);
        q.startRef = startRef;
        Helper.VCopy(q.endPos, endPos);
        q.endRef = endRef;
        q.status = null;
        q.npath = 0;
        q.filter = filter;
        q.keepAlive = 0;
        return refId;
    }
    
    public EnumSet<Status> GetRequestStatus(final long refId) {
        for (int i = 0; i < 8; ++i) {
            if (this._queue[i].refId == refId) {
                return this._queue[i].status;
            }
        }
        return EnumSet.of(Status.Failure);
    }
    
    public DetourStatusReturn GetPathResult(final long refId, final long[] path, final int pathSize, final int maxPath) {
        final DetourStatusReturn statusReturn = new DetourStatusReturn();
        for (int i = 0; i < 8; ++i) {
            if (this._queue[i].refId == refId) {
                final PathQuery q = this._queue[i];
                q.refId = 0L;
                q.status = null;
                final int n = Math.min(q.npath, maxPath);
                System.arraycopy(q.path, 0, path, 0, n);
                statusReturn.intValue = n;
                statusReturn.status = EnumSet.of(Status.Success);
                return statusReturn;
            }
        }
        statusReturn.status = EnumSet.of(Status.Failure);
        return statusReturn;
    }
    
    public NavMeshQuery NavQuery() {
        return this._navQuery;
    }
    
    private class PathQuery
    {
        public long refId;
        public float[] startPos;
        public float[] endPos;
        public long startRef;
        public long endRef;
        public long[] path;
        public int npath;
        public EnumSet<Status> status;
        public int keepAlive;
        public QueryFilter filter;
        
        private PathQuery() {
            this.startPos = new float[3];
            this.endPos = new float[3];
        }
    }
}
