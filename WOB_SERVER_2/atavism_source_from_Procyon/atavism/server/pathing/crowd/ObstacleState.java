// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

public final class ObstacleState
{
    public static final ObstacleState DT_OBSTACLE_EMPTY;
    public static final ObstacleState DT_OBSTACLE_PROCESSING;
    public static final ObstacleState DT_OBSTACLE_PROCESSED;
    public static final ObstacleState DT_OBSTACLE_REMOVING;
    private static ObstacleState[] swigValues;
    private static int swigNext;
    private final int swigValue;
    private final String swigName;
    
    public final int swigValue() {
        return this.swigValue;
    }
    
    @Override
    public String toString() {
        return this.swigName;
    }
    
    public static ObstacleState swigToEnum(final int swigValue) {
        if (swigValue < ObstacleState.swigValues.length && swigValue >= 0 && ObstacleState.swigValues[swigValue].swigValue == swigValue) {
            return ObstacleState.swigValues[swigValue];
        }
        for (int i = 0; i < ObstacleState.swigValues.length; ++i) {
            if (ObstacleState.swigValues[i].swigValue == swigValue) {
                return ObstacleState.swigValues[i];
            }
        }
        throw new IllegalArgumentException("No enum " + ObstacleState.class + " with value " + swigValue);
    }
    
    private ObstacleState(final String swigName) {
        this.swigName = swigName;
        this.swigValue = ObstacleState.swigNext++;
    }
    
    private ObstacleState(final String swigName, final int swigValue) {
        this.swigName = swigName;
        this.swigValue = swigValue;
        ObstacleState.swigNext = swigValue + 1;
    }
    
    private ObstacleState(final String swigName, final ObstacleState swigEnum) {
        this.swigName = swigName;
        this.swigValue = swigEnum.swigValue;
        ObstacleState.swigNext = this.swigValue + 1;
    }
    
    static {
        DT_OBSTACLE_EMPTY = new ObstacleState("DT_OBSTACLE_EMPTY");
        DT_OBSTACLE_PROCESSING = new ObstacleState("DT_OBSTACLE_PROCESSING");
        DT_OBSTACLE_PROCESSED = new ObstacleState("DT_OBSTACLE_PROCESSED");
        DT_OBSTACLE_REMOVING = new ObstacleState("DT_OBSTACLE_REMOVING");
        ObstacleState.swigValues = new ObstacleState[] { ObstacleState.DT_OBSTACLE_EMPTY, ObstacleState.DT_OBSTACLE_PROCESSING, ObstacleState.DT_OBSTACLE_PROCESSED, ObstacleState.DT_OBSTACLE_REMOVING };
        ObstacleState.swigNext = 0;
    }
}
