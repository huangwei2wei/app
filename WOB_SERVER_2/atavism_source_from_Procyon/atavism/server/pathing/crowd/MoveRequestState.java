// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

public enum MoveRequestState
{
    TargetNone, 
    TargetFailed, 
    TargetValid, 
    TargetRequesting, 
    TargetWaitingForQueue, 
    TargetWaitingForPath, 
    TargetVelocity;
}
