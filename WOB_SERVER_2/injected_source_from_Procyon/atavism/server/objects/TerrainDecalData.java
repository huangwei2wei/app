// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class TerrainDecalData implements Serializable, Marshallable
{
    private String imageName;
    private int posX;
    private int posZ;
    private float sizeX;
    private float sizeZ;
    private float rotation;
    private int priority;
    private static final long serialVersionUID = 1L;
    
    public TerrainDecalData() {
        this.imageName = null;
        this.posX = 0;
        this.posZ = 0;
        this.sizeX = 0.0f;
        this.sizeZ = 0.0f;
        this.rotation = 0.0f;
        this.priority = 0;
    }
    
    public TerrainDecalData(final String imageName, final int posX, final int posZ, final float sizeX, final float sizeZ, final float rotation, final int priority) {
        this.imageName = null;
        this.posX = 0;
        this.posZ = 0;
        this.sizeX = 0.0f;
        this.sizeZ = 0.0f;
        this.rotation = 0.0f;
        this.priority = 0;
        this.setImageName(imageName);
        this.setPosX(posX);
        this.setPosZ(posZ);
        this.setSizeX(sizeX);
        this.setSizeZ(sizeZ);
        this.setRotation(rotation);
        this.setPriority(priority);
    }
    
    @Override
    public String toString() {
        return "[TerrainDecalData: ImageName=" + this.getImageName() + ", PosX=" + this.getPosX() + ", PosZ=" + this.getPosZ() + ", SizeX=" + this.getSizeX() + ", SizeZ=" + this.getSizeZ() + ", Rotation=" + this.getRotation() + ", Priority=" + this.getPriority() + "]";
    }
    
    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }
    
    public String getImageName() {
        return this.imageName;
    }
    
    public void setPosX(final int val) {
        this.posX = val;
    }
    
    public int getPosX() {
        return this.posX;
    }
    
    public void setPosZ(final int val) {
        this.posZ = val;
    }
    
    public int getPosZ() {
        return this.posZ;
    }
    
    public void setSizeX(final float val) {
        this.sizeX = val;
    }
    
    public float getSizeX() {
        return this.sizeX;
    }
    
    public void setSizeZ(final float val) {
        this.sizeZ = val;
    }
    
    public float getSizeZ() {
        return this.sizeZ;
    }
    
    public void setRotation(final float val) {
        this.rotation = val;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setPriority(final int val) {
        this.priority = val;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.imageName != null && this.imageName != "") {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.imageName != null && this.imageName != "") {
            buf.putString(this.imageName);
        }
        buf.putInt(this.posX);
        buf.putInt(this.posZ);
        buf.putFloat(this.sizeX);
        buf.putFloat(this.sizeZ);
        buf.putFloat(this.rotation);
        buf.putInt(this.priority);
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.imageName = buf.getString();
        }
        this.posX = buf.getInt();
        this.posZ = buf.getInt();
        this.sizeX = buf.getFloat();
        this.sizeZ = buf.getFloat();
        this.rotation = buf.getFloat();
        this.priority = buf.getInt();
        return this;
    }
}
