// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.AORuntimeException;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import java.io.Serializable;
import java.util.Map;
import atavism.server.engine.Event;

public class ExtensionMessageEvent extends Event
{
    private Map<String, Serializable> propertyMap;
    private OID targetOid;
    private Boolean clientTargeted;
    
    public ExtensionMessageEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.propertyMap = null;
        this.targetOid = null;
        this.clientTargeted = null;
    }
    
    public ExtensionMessageEvent() {
        this.propertyMap = null;
        this.targetOid = null;
        this.clientTargeted = null;
    }
    
    public ExtensionMessageEvent(final OID objOid) {
        super(objOid);
        this.propertyMap = null;
        this.targetOid = null;
        this.clientTargeted = null;
    }
    
    @Override
    public String getName() {
        return "ExtensionMessageEvent";
    }
    
    @Override
    public String toString() {
        return "[ExtensionMessageEvent: subType=" + this.getExtensionType() + ", oid=" + this.getObjectOid() + ", targetOid=" + this.getTargetOid() + ", clientTargeted=" + this.getClientTargeted() + "]";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        throw new AORuntimeException("ExtensionMessageEvent.toBytes not implemented");
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID oid = buf.getOID();
        this.setObjectOid(oid);
        buf.getInt();
        final byte flags = buf.getByte();
        if ((flags & 0x1) != 0x0) {
            this.targetOid = buf.getOID();
        }
        this.clientTargeted = ((flags & 0x2) != 0x0);
        this.propertyMap = buf.getPropertyMap();
    }
    
    public void setExtensionType(final String type) {
        this.propertyMap.put("ext_msg_subtype", type);
    }
    
    public String getExtensionType() {
        return this.propertyMap.get("ext_msg_subtype");
    }
    
    public void setPropertyMap(final Map<String, Serializable> v) {
        this.propertyMap = v;
    }
    
    public Map<String, Serializable> getPropertyMap() {
        return this.propertyMap;
    }
    
    public void setTargetOid(final OID v) {
        this.targetOid = v;
    }
    
    public OID getTargetOid() {
        return this.targetOid;
    }
    
    public void setClientTargeted(final Boolean v) {
        this.clientTargeted = v;
    }
    
    public Boolean getClientTargeted() {
        return this.clientTargeted;
    }
}
