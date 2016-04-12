// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.DisplayContext;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class AttachEvent extends Event
{
    private OID objToAttachID;
    private String socketName;
    private DisplayContext displayContext;
    
    public AttachEvent() {
        this.objToAttachID = null;
        this.socketName = null;
        this.displayContext = null;
    }
    
    public AttachEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.objToAttachID = null;
        this.socketName = null;
        this.displayContext = null;
    }
    
    public AttachEvent(final AOObject attacher, final AOObject objToAttach, final String socketName) {
        super(attacher);
        this.objToAttachID = null;
        this.socketName = null;
        this.displayContext = null;
        this.setObjToAttachID(objToAttach.getOid());
        this.setSocketName(socketName);
        this.setDisplayContext(objToAttach.displayContext());
    }
    
    public AttachEvent(final OID attacherOid, final OID attacheeOid, final String socketName, final DisplayContext dc) {
        super(attacherOid);
        this.objToAttachID = null;
        this.socketName = null;
        this.displayContext = null;
        this.setObjToAttachID(attacheeOid);
        this.setSocketName(socketName);
        this.setDisplayContext(dc);
    }
    
    @Override
    public String getName() {
        return "AttachEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getAttacherOid());
        buf.putInt(msgId);
        buf.putOID(this.getObjToAttachID());
        buf.putString(this.socketName);
        buf.putString(this.displayContext.getMeshFile());
        final Set<DisplayContext.Submesh> submeshes = this.displayContext.getSubmeshes();
        buf.putInt(submeshes.size());
        for (final DisplayContext.Submesh submesh : submeshes) {
            buf.putString(submesh.name);
            buf.putString(submesh.material);
        }
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setAttacherOid(buf.getOID());
        buf.getInt();
        this.setObjToAttachID(buf.getOID());
        this.setSocketName(buf.getString());
        final DisplayContext dc = new DisplayContext();
        dc.setMeshFile(buf.getString());
        final Set<DisplayContext.Submesh> submeshes = new HashSet<DisplayContext.Submesh>();
        for (int numSubmeshes = buf.getInt(); numSubmeshes > 0; --numSubmeshes) {
            final String name = buf.getString();
            final String material = buf.getString();
            final DisplayContext.Submesh submesh = new DisplayContext.Submesh(name, material);
            submeshes.add(submesh);
        }
        dc.setSubmeshes(submeshes);
        this.setDisplayContext(dc);
    }
    
    public void setAttacherOid(final OID oid) {
        this.setObjectOid(oid);
    }
    
    public OID getAttacherOid() {
        return this.getObjectOid();
    }
    
    public void setObjToAttachID(final OID objID) {
        this.objToAttachID = objID;
    }
    
    public OID getObjToAttachID() {
        return this.objToAttachID;
    }
    
    public void setSocketName(final String socketName) {
        this.socketName = socketName;
    }
    
    public String getSocketName() {
        return this.socketName;
    }
    
    public void setDisplayContext(final DisplayContext dc) {
        this.displayContext = dc;
    }
    
    public DisplayContext getDisplayContext() {
        return this.displayContext;
    }
}
