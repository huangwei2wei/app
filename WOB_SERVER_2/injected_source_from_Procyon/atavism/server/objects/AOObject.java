// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.AOByteBuffer;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import atavism.server.util.LockFactory;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import atavism.server.engine.QuadTreeElement;
import atavism.server.util.Log;
import atavism.server.engine.WMWorldNode;
import atavism.server.engine.MobilePerceiver;
import atavism.server.math.AOVector;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.engine.BasicWorldNode;
import atavism.server.engine.WorldNode;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.Event;
import java.io.Serializable;
import atavism.server.engine.OID;
import atavism.server.engine.Namespace;
import java.util.concurrent.locks.Lock;
import atavism.server.marshalling.Marshallable;

public class AOObject extends Entity implements Marshallable
{
    public static final String stateMapKey = "aoobj.statemap";
    public static final String wnodeKey = "aoobj.wnode";
    public static final String perceiverKey = "aoobj.perceiver";
    public static final String aoidKey = "aoobj.aoid";
    public static final String dcKey = "aoobj.dc";
    private String scaleKey;
    private String permCBKey;
    private static AOObjectCreateHook createHook;
    public static Lock transferLock;
    private static final long serialVersionUID = 1L;
    
    public AOObject() {
        this.scaleKey = "aoobj.scale";
        this.permCBKey = "aoobj.permCB";
        this.setNamespace(Namespace.WORLD_MANAGER);
        this.init();
    }
    
    public AOObject(final String name) {
        super(name);
        this.scaleKey = "aoobj.scale";
        this.permCBKey = "aoobj.permCB";
        this.setNamespace(Namespace.WORLD_MANAGER);
        this.init();
    }
    
    public AOObject(final OID oid) {
        super(oid);
        this.scaleKey = "aoobj.scale";
        this.permCBKey = "aoobj.permCB";
        this.setNamespace(Namespace.WORLD_MANAGER);
        this.init();
    }
    
    private void init() {
        final AOObjectCreateHook hook = getObjCreateHook();
        if (hook != null) {
            hook.objectCreateHook(this);
        }
    }
    
    public OID getMasterOid() {
        return this.getOid();
    }
    
    public boolean isMob() {
        return this.getType().isMob();
    }
    
    public boolean isItem() {
        return this.getType() == ObjectTypes.item;
    }
    
    public boolean isLight() {
        return this.getType() == ObjectTypes.light;
    }
    
    public boolean isUser() {
        return this.getType().isPlayer();
    }
    
    public boolean isStructure() {
        return this.getType().isStructure();
    }
    
    @Override
    public String toString() {
        return "[AOObject: " + this.getName() + ":" + this.getOid() + ", type=" + this.getType() + "]";
    }
    
    public ObjState setState(final String state, final ObjState obj) {
        this.lock.lock();
        try {
            final AOObject.StateMap stateMap = this.getStateMap();
            return stateMap.setState(state, obj);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public ObjState getState(final String s) {
        this.lock.lock();
        try {
            final AOObject.StateMap stateMap = this.getStateMap();
            return stateMap.getState(s);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private AOObject.StateMap getStateMap() {
        this.lock.lock();
        try {
            AOObject.StateMap stateMap = (AOObject.StateMap)this.getProperty("aoobj.statemap");
            if (stateMap == null) {
                stateMap = new AOObject.StateMap();
                this.setProperty("aoobj.statemap", (Serializable)stateMap);
            }
            return stateMap;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void sendEvent(final Event event) {
        throw new AORuntimeException("legacy code");
    }
    
    public WorldNode worldNode() {
        return (WorldNode)this.getProperty("aoobj.wnode");
    }
    
    public void worldNode(final WorldNode worldNode) {
        this.setProperty("aoobj.wnode", (Serializable)worldNode);
    }
    
    public BasicWorldNode baseWorldNode() {
        return new BasicWorldNode((InterpolatedWorldNode)this.getProperty("aoobj.wnode"));
    }
    
    public Point getLoc() {
        final WorldNode node = this.worldNode();
        return (node == null) ? null : node.getLoc();
    }
    
    public Point getCurrentLoc() {
        final WorldNode node = this.worldNode();
        return (node == null) ? null : node.getCurrentLoc();
    }
    
    public Quaternion getOrientation() {
        final WorldNode node = this.worldNode();
        return (node == null) ? null : node.getOrientation();
    }
    
    public AOVector getDirection() {
        final InterpolatedWorldNode iwn = (InterpolatedWorldNode)this.getProperty("aoobj.wnode");
        return iwn.getDir();
    }
    
    public InterpolatedWorldNode.InterpolatedDirLocOrientTime getDirLocOrientTime() {
        final InterpolatedWorldNode iwn = (InterpolatedWorldNode)this.getProperty("aoobj.wnode");
        return iwn.getDirLocOrientTime();
    }
    
    public MobilePerceiver<WMWorldNode> perceiver() {
        this.lock.lock();
        try {
            return (MobilePerceiver<WMWorldNode>)this.getProperty("aoobj.perceiver");
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void perceiver(final MobilePerceiver<WMWorldNode> p) {
        this.lock.lock();
        try {
            final MobilePerceiver<WMWorldNode> perceiver = this.perceiver();
            if (perceiver == p) {
                Log.warn("AOObject.setPerceiver: new/cur perceiver same");
            }
            if (perceiver != null) {
                perceiver.setElement((QuadTreeElement)null);
                Log.warn("AOObject.setPerceiver: perceiv is already not null");
            }
            if (Log.loggingDebug) {
                Log.debug("AOObject.setPerceiver: obj oid=" + this.getOid() + ", perceiver=" + p);
            }
            this.setProperty("aoobj.perceiver", (Serializable)p);
            if (p != null) {
                p.setElement((QuadTreeElement)this.worldNode());
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public OID atavismID() {
        return (OID)this.getProperty("aoobj.aoid");
    }
    
    public void atavismID(final OID id) {
        this.setProperty("aoobj.aoid", (Serializable)id);
    }
    
    public void displayContext(final DisplayContext dc) {
        DisplayContext dcCopy = null;
        if (dc != null) {
            dcCopy = (DisplayContext)dc.clone();
            dcCopy.setObjRef(this.getOid());
        }
        this.setProperty("aoobj.dc", dcCopy);
    }
    
    public DisplayContext displayContext() {
        final DisplayContext dc = (DisplayContext)this.getProperty("aoobj.dc");
        return dc;
    }
    
    public void scale(final float scale) {
        this.scale(new AOVector(scale, scale, scale));
    }
    
    public void scale(final AOVector scale) {
        this.setProperty(this.scaleKey, (Serializable)scale.clone());
    }
    
    public AOVector scale() {
        return (AOVector)this.getProperty(this.scaleKey);
    }
    
    public static void registerObjCreateHook(final AOObjectCreateHook hook) {
        AOObject.createHook = hook;
    }
    
    public static AOObjectCreateHook getObjCreateHook() {
        return AOObject.createHook;
    }
    
    public void permissionCallback(final PermissionCallback cb) {
        this.setProperty(this.permCBKey, cb);
    }
    
    public PermissionCallback permissionCallback() {
        return (PermissionCallback)this.getProperty(this.permCBKey);
    }
    
    public static void writeObject(final ObjectOutput out, final Object obj) throws IOException {
        out.writeBoolean(obj == null);
        if (obj != null) {
            out.writeObject(obj);
        }
    }
    
    public static Object readObject(final ObjectInput in) throws IOException, ClassNotFoundException {
        final boolean isNull = in.readBoolean();
        if (!isNull) {
            return in.readObject();
        }
        return null;
    }
    
    public static void writeString(final ObjectOutput out, final String string) throws IOException {
        if (string == null) {
            out.writeUTF("");
        }
        else {
            out.writeUTF(string);
        }
    }
    
    public static Collection<AOObject> getAllObjects() {
        final Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.WORLD_MANAGER);
        final Set<AOObject> objSet = new HashSet<AOObject>();
        for (final Entity e : entities) {
            if (e instanceof AOObject) {
                objSet.add((AOObject)e);
            }
        }
        return objSet;
    }
    
    public static AOObject getObject(final OID oid) {
        return (AOObject)EntityManager.getEntityByNamespace(oid, Namespace.WORLD_MANAGER);
    }
    
    static {
        AOObject.createHook = null;
        AOObject.transferLock = LockFactory.makeLock("objXferLock");
        try {
            final BeanInfo info = Introspector.getBeanInfo(AOObject.class);
            final PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; ++i) {}
        }
        catch (Exception e) {
            Log.error("failed aoobject beans initalization");
        }
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.scaleKey != null && this.scaleKey != "") {
            flag_bits = 1;
        }
        if (this.permCBKey != null && this.permCBKey != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.scaleKey != null && this.scaleKey != "") {
            buf.putString(this.scaleKey);
        }
        if (this.permCBKey != null && this.permCBKey != "") {
            buf.putString(this.permCBKey);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.scaleKey = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.permCBKey = buf.getString();
        }
        return this;
    }
}
