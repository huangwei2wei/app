// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.HashMap;
import java.io.Serializable;

public class BuildObjectStage implements Serializable
{
    protected HashMap<Integer, Integer> itemReqs;
    protected String gameObject;
    protected float buildTimeReq;
    protected int health;
    protected int nextStageID;
    private static final long serialVersionUID = 1L;
    
    public BuildObjectStage(final String gameObject, final float buildTimeReq, final HashMap<Integer, Integer> itemReqs, final int health, final int nextStageID) {
        this.buildTimeReq = 1.0f;
        this.health = 0;
        this.nextStageID = -1;
        this.gameObject = gameObject;
        this.buildTimeReq = buildTimeReq;
        this.itemReqs = itemReqs;
        this.health = health;
        this.nextStageID = nextStageID;
    }
    
    public String getGameObject() {
        return this.gameObject;
    }
    
    public void setGameObject(final String gameObject) {
        this.gameObject = gameObject;
    }
    
    public HashMap<Integer, Integer> getItemReqs() {
        return this.itemReqs;
    }
    
    public void setItemReqs(final HashMap<Integer, Integer> itemReqs) {
        this.itemReqs = itemReqs;
    }
    
    public float getBuildTimeReq() {
        return this.buildTimeReq;
    }
    
    public void setBuildTimeReq(final float buildTimeReq) {
        this.buildTimeReq = buildTimeReq;
    }
    
    public int getHealth() {
        return this.health;
    }
    
    public void setHealth(final int health) {
        this.health = health;
    }
    
    public int getNextStageID() {
        return this.nextStageID;
    }
    
    public void setNextStageID(final int nextStageID) {
        this.nextStageID = nextStageID;
    }
}
