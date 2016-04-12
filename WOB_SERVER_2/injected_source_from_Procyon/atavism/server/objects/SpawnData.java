// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Namespace;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;

public class SpawnData extends Entity implements Marshallable
{
    private int templateID;
    private String templateName;
    private int category;
    private String factoryName;
    private String className;
    private OID instanceOid;
    private Point loc;
    private Quaternion orient;
    private Integer spawnRadius;
    private Integer numSpawns;
    private Integer respawnTime;
    private Integer corpseDespawnTime;
    private static final long serialVersionUID = 1L;
    
    public SpawnData() {
        this.setNamespace(Namespace.TRANSIENT);
    }
    
    public SpawnData(final String name, final String templateName, final int category, final String factoryName, final OID instanceOid, final Point loc, final Quaternion orient, final Integer spawnRadius, final Integer numSpawns, final Integer respawnTime) {
        super(name);
        this.setNamespace(Namespace.TRANSIENT);
        this.setTemplateName(templateName);
        this.setCategory(category);
        this.setFactoryName(factoryName);
        this.setInstanceOid(instanceOid);
        this.setLoc(loc);
        this.setOrientation(orient);
        this.setSpawnRadius(spawnRadius);
        this.setNumSpawns(numSpawns);
        this.setRespawnTime(respawnTime);
    }
    
    @Override
    public String toString() {
        return "[SpawnData: oid=" + this.getOid() + ", name=" + this.getName() + ", templateName=" + this.getTemplateName() + ", factoryName=" + this.getFactoryName() + ", instanceOid=" + this.getInstanceOid() + ", loc=" + this.getLoc() + ", orient=" + this.getOrientation() + ", numSpawns=" + this.getNumSpawns() + ", respawnTime=" + this.getRespawnTime() + ", corpseDespawnTime=" + this.getCorpseDespawnTime() + "]";
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setTemplateID(final int templateID) {
        this.templateID = templateID;
    }
    
    public int getTemplateID() {
        return this.templateID;
    }
    
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }
    
    public String getTemplateName() {
        return this.templateName;
    }
    
    public void setCategory(final int category) {
        this.category = category;
    }
    
    public int getCategory() {
        return this.category;
    }
    
    public void setFactoryName(final String factoryName) {
        this.factoryName = factoryName;
    }
    
    public String getFactoryName() {
        return this.factoryName;
    }
    
    public OID getInstanceOid() {
        return this.instanceOid;
    }
    
    public void setInstanceOid(final OID oid) {
        this.instanceOid = oid;
    }
    
    public void setLoc(final Point loc) {
        this.loc = loc;
    }
    
    public Point getLoc() {
        return this.loc;
    }
    
    public void setOrientation(final Quaternion orient) {
        this.orient = orient;
    }
    
    public Quaternion getOrientation() {
        return this.orient;
    }
    
    public void setSpawnRadius(final Integer spawnRadius) {
        this.spawnRadius = spawnRadius;
    }
    
    public Integer getSpawnRadius() {
        return this.spawnRadius;
    }
    
    public void setNumSpawns(final Integer numSpawns) {
        this.numSpawns = numSpawns;
    }
    
    public Integer getNumSpawns() {
        return this.numSpawns;
    }
    
    public void setRespawnTime(final Integer respawnTime) {
        this.respawnTime = respawnTime;
    }
    
    public Integer getRespawnTime() {
        return this.respawnTime;
    }
    
    public void setCorpseDespawnTime(final Integer time) {
        this.corpseDespawnTime = time;
    }
    
    public Integer getCorpseDespawnTime() {
        return this.corpseDespawnTime;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.templateName != null && this.templateName != "") {
            flag_bits = 1;
        }
        if (this.factoryName != null && this.factoryName != "") {
            flag_bits |= 0x2;
        }
        if (this.className != null && this.className != "") {
            flag_bits |= 0x4;
        }
        if (this.instanceOid != null) {
            flag_bits |= 0x8;
        }
        if (this.loc != null) {
            flag_bits |= 0x10;
        }
        if (this.orient != null) {
            flag_bits |= 0x20;
        }
        if (this.spawnRadius != null) {
            flag_bits |= 0x40;
        }
        if (this.numSpawns != null) {
            flag_bits |= (byte)128;
        }
        buf.putByte(flag_bits);
        flag_bits = 0;
        if (this.respawnTime != null) {
            flag_bits = 1;
        }
        if (this.corpseDespawnTime != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        buf.putInt(this.templateID);
        if (this.templateName != null && this.templateName != "") {
            buf.putString(this.templateName);
        }
        buf.putInt(this.category);
        if (this.factoryName != null && this.factoryName != "") {
            buf.putString(this.factoryName);
        }
        if (this.className != null && this.className != "") {
            buf.putString(this.className);
        }
        if (this.instanceOid != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.instanceOid);
        }
        if (this.loc != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.loc);
        }
        if (this.orient != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.orient);
        }
        if (this.spawnRadius != null) {
            buf.putInt((int)this.spawnRadius);
        }
        if (this.numSpawns != null) {
            buf.putInt((int)this.numSpawns);
        }
        if (this.respawnTime != null) {
            buf.putInt((int)this.respawnTime);
        }
        if (this.corpseDespawnTime != null) {
            buf.putInt((int)this.corpseDespawnTime);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        final byte flag_bits2 = buf.getByte();
        this.templateID = buf.getInt();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.templateName = buf.getString();
        }
        this.category = buf.getInt();
        if ((flag_bits0 & 0x2) != 0x0) {
            this.factoryName = buf.getString();
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.className = buf.getString();
        }
        if ((flag_bits0 & 0x8) != 0x0) {
            this.instanceOid = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x10) != 0x0) {
            this.loc = (Point)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x20) != 0x0) {
            this.orient = (Quaternion)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x40) != 0x0) {
            this.spawnRadius = buf.getInt();
        }
        if ((flag_bits0 & 0x80) != 0x0) {
            this.numSpawns = buf.getInt();
        }
        if ((flag_bits2 & 0x1) != 0x0) {
            this.respawnTime = buf.getInt();
        }
        if ((flag_bits2 & 0x2) != 0x0) {
            this.corpseDespawnTime = buf.getInt();
        }
        return this;
    }
}
