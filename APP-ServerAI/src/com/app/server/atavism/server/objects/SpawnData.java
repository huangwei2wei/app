// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.engine.OID;

public class SpawnData extends Entity
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
}
