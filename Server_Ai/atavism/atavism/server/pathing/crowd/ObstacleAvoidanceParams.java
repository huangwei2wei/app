// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

public class ObstacleAvoidanceParams
{
    public float velBias;
    public float weightDesVel;
    public float weightCurVel;
    public float weightSide;
    public float weightToi;
    public float horizTime;
    public short gridSize;
    public short adaptiveDivs;
    public short adaptiveRings;
    public short adaptiveDepth;
    
    public ObstacleAvoidanceParams() {
    }
    
    public ObstacleAvoidanceParams(final float velBias, final float weightDesVel, final float weightCurVel, final float weightSide, final float weightToi, final float horizTime, final short gridSize, final short adaptiveDivs, final short adaptiveRings, final short adaptiveDepth) {
        this.velBias = velBias;
        this.weightDesVel = weightDesVel;
        this.weightCurVel = weightCurVel;
        this.weightSide = weightSide;
        this.weightToi = weightToi;
        this.horizTime = horizTime;
        this.gridSize = gridSize;
        this.adaptiveDivs = adaptiveDivs;
        this.adaptiveRings = adaptiveRings;
        this.adaptiveDepth = adaptiveDepth;
    }
    
    public ObstacleAvoidanceParams(final ObstacleAvoidanceParams param) {
        this.velBias = param.velBias;
        this.weightDesVel = param.weightDesVel;
        this.weightCurVel = param.weightCurVel;
        this.weightSide = param.weightSide;
        this.weightToi = param.weightToi;
        this.horizTime = param.horizTime;
        this.gridSize = param.gridSize;
        this.adaptiveDivs = param.adaptiveDivs;
        this.adaptiveRings = param.adaptiveRings;
        this.adaptiveDepth = param.adaptiveDepth;
    }
}
