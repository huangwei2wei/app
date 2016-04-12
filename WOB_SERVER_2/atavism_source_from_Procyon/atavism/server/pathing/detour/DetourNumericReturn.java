// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;

import java.util.EnumSet;
import atavism.server.objects.Vector2;

public class DetourNumericReturn
{
    public int intValue;
    public float floatValue;
    public long longValue;
    public Boolean boolValue;
    public Vector2 vector2Value;
    public EnumSet<Status> status;
    
    public DetourNumericReturn() {
        this.status = EnumSet.noneOf(Status.class);
    }
}
