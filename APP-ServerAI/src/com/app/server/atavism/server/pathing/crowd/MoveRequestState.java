// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.crowd;

public enum MoveRequestState
{
    TargetNone, //无目标
    TargetFailed, 
    TargetValid, 
    TargetRequesting, 
    TargetWaitingForQueue, 
    TargetWaitingForPath, 
    TargetVelocity;
}
