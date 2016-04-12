// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.Serializable;

public class TerrainDecalData implements Serializable
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
}
