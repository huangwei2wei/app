// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.HashSet;
import atavism.server.util.AORuntimeException;
import java.util.Iterator;
import java.util.Set;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Logger;
import atavism.server.objects.DisplayContext;
import atavism.server.engine.Event;

public class ModelInfoEvent extends Event
{
    protected DisplayContext dc;
    protected boolean forceInstantLoad;
    protected static final Logger log;
    
    public ModelInfoEvent() {
        this.dc = null;
        this.forceInstantLoad = false;
    }
    
    public ModelInfoEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.dc = null;
        this.forceInstantLoad = false;
    }
    
    public ModelInfoEvent(final AOObject obj) {
        super(obj);
        this.dc = null;
        this.forceInstantLoad = false;
        this.setDisplayContext((DisplayContext)obj.displayContext().clone());
    }
    
    public ModelInfoEvent(final OID objOid) {
        super(objOid);
        this.dc = null;
        this.forceInstantLoad = false;
    }
    
    @Override
    public String getName() {
        return "ModelInfoEvent";
    }
    
    public void setDisplayContext(final DisplayContext dc) {
        this.dc = dc;
    }
    
    public DisplayContext getDisplayContext() {
        return this.dc;
    }
    
    public void setForceInstantLoad(final boolean forceInstantLoad) {
        this.forceInstantLoad = forceInstantLoad;
    }
    
    public boolean getForceInstantLoad() {
        return this.forceInstantLoad;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(400);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putInt(1);
        final DisplayContext dc = this.getDisplayContext();
        buf.putString(dc.getMeshFile());
        if (Log.loggingDebug) {
            ModelInfoEvent.log.debug("ModelInfoEvent.toBytes: meshfile=" + dc.getMeshFile());
        }
        final Set<DisplayContext.Submesh> submeshes = dc.getSubmeshes();
        final int submeshLen = submeshes.size();
        buf.putInt(submeshLen);
        if (Log.loggingDebug) {
            ModelInfoEvent.log.debug("ModelInfoEvent.toBytes: submeshLen=" + submeshLen);
        }
        final int castShadow = dc.getCastShadow() ? 1 : 0;
        final int receiveShadow = dc.getReceiveShadow() ? 1 : 0;
        for (final DisplayContext.Submesh submesh : submeshes) {
            buf.putString(submesh.name);
            buf.putString(submesh.material);
            buf.putInt(castShadow);
            buf.putInt(receiveShadow);
            if (Log.loggingDebug) {
                ModelInfoEvent.log.debug("ModelInfoEvent.toBytes: submeshName=" + submesh.name + ", material=" + submesh.material + ", castShadow=" + castShadow + ", receiveShadow=" + receiveShadow);
            }
        }
        buf.putInt(dc.getDisplayID());
        Log.debug("DISPLAY: set display ID to: " + dc.getDisplayID());
        buf.putBoolean(this.forceInstantLoad);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        final int meshFiles = buf.getInt();
        if (meshFiles != 1) {
            throw new AORuntimeException("more than 1 meshfile is not supported");
        }
        final DisplayContext dc = new DisplayContext();
        dc.setMeshFile(buf.getString());
        if (Log.loggingDebug) {
            ModelInfoEvent.log.debug("parseBytes: objOid=" + this.getObjectOid() + ", meshfile=" + dc.getMeshFile());
        }
        final Set<DisplayContext.Submesh> submeshes = new HashSet<DisplayContext.Submesh>();
        int numSubmeshes = buf.getInt();
        while (numSubmeshes > 0) {
            final String name = buf.getString();
            final String material = buf.getString();
            final DisplayContext.Submesh submesh = new DisplayContext.Submesh(name, material);
            submeshes.add(submesh);
            --numSubmeshes;
            final int castShadow = buf.getInt();
            final int receiveShadow = buf.getInt();
            dc.setCastShadow(castShadow == 1);
            dc.setReceiveShadow(receiveShadow == 1);
        }
        dc.setSubmeshes(submeshes);
        dc.setDisplayID(buf.getInt());
        this.forceInstantLoad = buf.getBoolean();
    }
    
    static {
        log = new Logger("ModelInfoEvent");
    }
}
