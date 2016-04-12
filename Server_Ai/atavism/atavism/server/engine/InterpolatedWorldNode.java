// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import atavism.server.util.Log;
import java.beans.Introspector;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.pathing.PathLocAndDir;
import java.util.Collection;
import java.util.HashSet;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.util.LockFactory;
import atavism.server.plugins.WorldManagerClient;
import java.util.concurrent.locks.Lock;
import java.util.Set;
import atavism.server.pathing.PathInterpolator;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import atavism.server.objects.EntityHandle;

public class InterpolatedWorldNode implements WorldNode, BasicInterpolatable
{
    protected EntityHandle objHandle;
    protected Boolean followsTerrain;
    protected boolean spawned;
    protected OID instanceOid;
    protected Point rawLoc;
    protected Point interpLoc;
    protected AOVector dir;
    protected Quaternion orient;
    protected transient PathInterpolator pathInterpolator;
    protected long lastUpdate;
    protected long lastInterp;
    protected WorldNode parent;
    protected Set<WorldNode> children;
    public transient Lock lock;
    public transient Lock treeLock;
    private static final long serialVersionUID = 1L;
    
    public InterpolatedWorldNode() {
        this.objHandle = null;
        this.followsTerrain = true;
        this.spawned = false;
        this.rawLoc = null;
        this.interpLoc = null;
        this.dir = new AOVector(0.0f, 0.0f, 0.0f);
        this.orient = null;
        this.pathInterpolator = null;
        this.lastUpdate = -1L;
        this.lastInterp = -1L;
        this.parent = null;
        this.children = null;
        this.lock = null;
        this.treeLock = null;
        this.setupTransient();
    }
    
    public InterpolatedWorldNode(final BasicWorldNode bnode) {
        this.objHandle = null;
        this.followsTerrain = true;
        this.spawned = false;
        this.rawLoc = null;
        this.interpLoc = null;
        this.dir = new AOVector(0.0f, 0.0f, 0.0f);
        this.orient = null;
        this.pathInterpolator = null;
        this.lastUpdate = -1L;
        this.lastInterp = -1L;
        this.parent = null;
        this.children = null;
        this.lock = null;
        this.treeLock = null;
        this.setupTransient();
        this.instanceOid = bnode.getInstanceOid();
        this.rawLoc = bnode.getLoc();
        this.interpLoc = this.rawLoc;
        this.dir = bnode.getDir();
        this.orient = bnode.getOrientation();
        this.lastInterp = System.currentTimeMillis();
    }
    
    public InterpolatedWorldNode(final WorldManagerClient.ObjectInfo info) {
        this.objHandle = null;
        this.followsTerrain = true;
        this.spawned = false;
        this.rawLoc = null;
        this.interpLoc = null;
        this.dir = new AOVector(0.0f, 0.0f, 0.0f);
        this.orient = null;
        this.pathInterpolator = null;
        this.lastUpdate = -1L;
        this.lastInterp = -1L;
        this.parent = null;
        this.children = null;
        this.lock = null;
        this.treeLock = null;
        this.setupTransient();
        this.rawLoc = info.loc;
        this.interpLoc = this.rawLoc;
        this.dir = info.dir;
        this.orient = info.orient;
        this.lastInterp = System.currentTimeMillis();
        this.instanceOid = info.instanceOid;
    }
    
    void setupTransient() {
        this.lock = LockFactory.makeLock("InterpolatedWorldNodeLock");
    }
    
    @Override
    public String toString() {
        return "[InterpolatedWorldNode: objHandle=" + this.objHandle + ", instanceOid=" + this.getInstanceOid() + ", rawLoc=" + this.getRawLoc() + ", interpLoc=" + this.getInterpLoc() + ", dir=" + this.getDir() + ", orient=" + this.getOrientation() + "]";
    }
    
    @Override
    public OID getInstanceOid() {
        return this.instanceOid;
    }
    
    public void setInstanceOid(final OID oid) {
        this.instanceOid = oid;
    }
    
    @Override
    public Point getLoc() {
        final Lock myTreeLock = this.treeLock;
        if (myTreeLock != null) {
            myTreeLock.lock();
        }
        this.lock.lock();
        try {
            final BasicInterpolator interp = (BasicInterpolator)Engine.getInterpolator();
            if (interp != null) {
                interp.interpolate((BasicInterpolatable)this);
            }
            return (this.interpLoc == null) ? null : ((Point)this.interpLoc.clone());
        }
        finally {
            this.lock.unlock();
            if (myTreeLock != null) {
                myTreeLock.unlock();
            }
        }
    }
    
    @Override
    public void setLoc(final Point p) {
        final Lock myTreeLock = this.treeLock;
        if (myTreeLock != null) {
            myTreeLock.lock();
        }
        this.lock.lock();
        try {
            final long time = System.currentTimeMillis();
            this.setRawLoc(p);
            this.setLastUpdate(time);
            this.setInterpLoc(p);
            this.setLastInterp(time);
        }
        finally {
            this.lock.unlock();
            if (myTreeLock != null) {
                myTreeLock.unlock();
            }
        }
    }
    
    @Override
    public long getLastUpdate() {
        this.lock.lock();
        try {
            return this.lastUpdate;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setLastUpdate(final long time) {
        this.lock.lock();
        try {
            this.lastUpdate = time;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public AOObject getObject() {
        this.lock.lock();
        try {
            return (this.objHandle == null) ? null : ((AOObject)this.objHandle.getEntity(Namespace.WORLD_MANAGER));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setObject(final AOObject obj) {
        this.lock.lock();
        try {
            this.objHandle = ((obj == null) ? null : new EntityHandle(obj));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setObjectOID(final OID oid) {
        this.objHandle = new EntityHandle(oid);
    }
    
    public OID getObjectOID() {
        if (this.objHandle == null) {
            return null;
        }
        return this.objHandle.getOid();
    }
    
    @Override
    public WorldNode getParent() {
        this.lock.lock();
        try {
            return this.parent;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setParent(final WorldNode node) {
        this.lock.lock();
        try {
            this.parent = node;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Quaternion getOrientation() {
        this.lock.lock();
        try {
            return (this.orient == null) ? null : ((Quaternion)this.orient.clone());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setOrientation(final Quaternion orient) {
        this.lock.lock();
        try {
            this.orient = ((orient == null) ? null : ((Quaternion)orient.clone()));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setDirLocOrient(final BasicWorldNode bnode) {
        final Lock myTreeLock = this.treeLock;
        if (myTreeLock != null) {
            myTreeLock.lock();
        }
        this.lock.lock();
        try {
            this.setLastInterp(System.currentTimeMillis());
            this.setRawLoc(bnode.getLoc());
            this.setDir(bnode.getDir());
            this.setInterpLoc(bnode.getLoc());
            this.setOrientation(bnode.getOrientation());
        }
        finally {
            this.lock.unlock();
            if (myTreeLock != null) {
                myTreeLock.unlock();
            }
        }
    }
    
    public InterpolatedDirLocOrientTime getDirLocOrientTime() {
        final InterpolatedDirLocOrientTime val = new InterpolatedDirLocOrientTime();
        this.lock.lock();
        try {
            val.interpLoc = ((this.interpLoc == null) ? null : ((Point)this.interpLoc.clone()));
            val.dir = ((this.dir == null) ? null : ((AOVector)this.dir.clone()));
            val.orient = ((this.orient == null) ? null : ((Quaternion)this.orient.clone()));
            val.lastInterp = this.lastInterp;
            return val;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Set<WorldNode> getChildren() {
        this.lock.lock();
        try {
            if (this.children != null) {
                return new HashSet<WorldNode>(this.children);
            }
            return new HashSet<WorldNode>();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setChildren(final Set<WorldNode> children) {
        this.lock.lock();
        try {
            this.children = new HashSet<WorldNode>(children);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void addChild(final WorldNode child) {
        this.lock.lock();
        try {
            if (this.children == null) {
                this.children = new HashSet<WorldNode>();
            }
            this.children.add(child);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void removeChild(final WorldNode child) {
        this.lock.lock();
        try {
            this.children.remove(child);
            if (this.children.size() == 0) {
                this.children = null;
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean isSpawned() {
        return this.spawned;
    }
    
    public void isSpawned(final boolean spawned) {
        if (!(this.spawned = spawned)) {
            final BasicInterpolator interp = (BasicInterpolator)Engine.getInterpolator();
            if (interp != null) {
                interp.unregister((BasicInterpolatable)this);
            }
        }
    }
    
    @Override
    public PathInterpolator getPathInterpolator() {
        return this.pathInterpolator;
    }
    
    public void setPathInterpolator(final PathInterpolator pathInterpolator) {
        this.lock.lock();
        try {
            this.pathInterpolator = pathInterpolator;
            if (pathInterpolator == null) {
                this.changeDir(new AOVector(0.0f, 0.0f, 0.0f), false);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public PathLocAndDir interpolate(final float t) {
        this.lock.lock();
        try {
            if (this.pathInterpolator == null) {
                return null;
            }
            final PathLocAndDir locAndDir = this.pathInterpolator.interpolate(t);
            if (locAndDir == null) {
                this.pathInterpolator = null;
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public AOVector getDir() {
        this.lock.lock();
        try {
            return (this.dir == null) ? null : ((AOVector)this.dir.clone());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setDir(final AOVector dir) {
        final Lock myTreeLock = this.treeLock;
        if (myTreeLock != null) {
            myTreeLock.lock();
        }
        this.lock.lock();
        try {
            this.changeDir(dir, true);
        }
        finally {
            this.lock.unlock();
            if (myTreeLock != null) {
                myTreeLock.unlock();
            }
        }
    }
    
    protected void changeDir(final AOVector dir, final boolean performDirInterpolation) {
        final BasicInterpolator interp = (BasicInterpolator)Engine.getInterpolator();
        if (interp != null && performDirInterpolation) {
            interp.interpolate((BasicInterpolatable)this);
            if (!this.dir.isZero() && dir.isZero()) {
                interp.unregister((BasicInterpolatable)this);
            }
            else if (this.dir.isZero() && !dir.isZero()) {
                interp.register((BasicInterpolatable)this);
            }
        }
        this.dir = ((dir == null) ? null : ((AOVector)dir.clone()));
    }
    
    @Override
    public Point getRawLoc() {
        this.lock.lock();
        try {
            return (this.rawLoc == null) ? null : ((Point)this.rawLoc.clone());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setRawLoc(final Point p) {
        this.lock.lock();
        try {
            this.rawLoc = ((p == null) ? null : ((Point)p.clone()));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public long getLastInterp() {
        this.lock.lock();
        try {
            return this.lastInterp;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setLastInterp(final long time) {
        this.lock.lock();
        try {
            this.lastInterp = time;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Point getInterpLoc() {
        this.lock.lock();
        try {
            return (this.interpLoc == null) ? null : ((Point)this.interpLoc.clone());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Point getCurrentLoc() {
        return this.getInterpLoc();
    }
    
    @Override
    public void setInterpLoc(final Point p) {
        this.lock.lock();
        try {
            this.interpLoc = ((p == null) ? null : ((Point)p.clone()));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void setPathInterpolatorValues(final long time, final AOVector newDir, final Point newLoc, final Quaternion orientation) {
        final Lock myTreeLock = this.treeLock;
        if (myTreeLock != null) {
            myTreeLock.lock();
        }
        this.lock.lock();
        try {
            this.lastInterp = time;
            this.dir = newDir;
            this.orient = ((orientation == null) ? null : ((Quaternion)orientation.clone()));
            this.setInterpLoc(newLoc);
        }
        finally {
            this.lock.unlock();
            if (myTreeLock != null) {
                myTreeLock.unlock();
            }
        }
    }
    
    public Boolean getFollowsTerrain() {
        return this.followsTerrain;
    }
    
    public void setFollowsTerrain(final Boolean flag) {
        this.followsTerrain = flag;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    static {
        try {
            final BeanInfo info = Introspector.getBeanInfo(InterpolatedWorldNode.class);
            final PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; ++i) {
                final PropertyDescriptor pd = propertyDescriptors[i];
                if (pd.getName().equals("children")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
                if (pd.getName().equals("object")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
                if (pd.getName().equals("loc")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
                if (pd.getName().equals("lastUpdate")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
                if (pd.getName().equals("parent")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
                if (pd.getName().equals("dir")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }
        }
        catch (Exception e) {
            Log.error("failed beans initalization");
        }
    }
    
    public class InterpolatedDirLocOrientTime
    {
        public AOVector dir;
        public Point interpLoc;
        public Quaternion orient;
        public long lastInterp;
    }
}
