// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

public final class dtAllocHint
{
    public static final dtAllocHint DT_ALLOC_PERM;
    public static final dtAllocHint DT_ALLOC_TEMP;
    private static dtAllocHint[] swigValues;
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
    
    public static dtAllocHint swigToEnum(final int swigValue) {
        if (swigValue < dtAllocHint.swigValues.length && swigValue >= 0 && dtAllocHint.swigValues[swigValue].swigValue == swigValue) {
            return dtAllocHint.swigValues[swigValue];
        }
        for (int i = 0; i < dtAllocHint.swigValues.length; ++i) {
            if (dtAllocHint.swigValues[i].swigValue == swigValue) {
                return dtAllocHint.swigValues[i];
            }
        }
        throw new IllegalArgumentException("No enum " + dtAllocHint.class + " with value " + swigValue);
    }
    
    private dtAllocHint(final String swigName) {
        this.swigName = swigName;
        this.swigValue = dtAllocHint.swigNext++;
    }
    
    private dtAllocHint(final String swigName, final int swigValue) {
        this.swigName = swigName;
        this.swigValue = swigValue;
        dtAllocHint.swigNext = swigValue + 1;
    }
    
    private dtAllocHint(final String swigName, final dtAllocHint swigEnum) {
        this.swigName = swigName;
        this.swigValue = swigEnum.swigValue;
        dtAllocHint.swigNext = this.swigValue + 1;
    }
    
    static {
        DT_ALLOC_PERM = new dtAllocHint("DT_ALLOC_PERM");
        DT_ALLOC_TEMP = new dtAllocHint("DT_ALLOC_TEMP");
        dtAllocHint.swigValues = new dtAllocHint[] { dtAllocHint.DT_ALLOC_PERM, dtAllocHint.DT_ALLOC_TEMP };
        dtAllocHint.swigNext = 0;
    }
}
