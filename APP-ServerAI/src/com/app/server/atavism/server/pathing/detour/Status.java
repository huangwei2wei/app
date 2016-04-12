// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

import java.util.Collection;
import java.util.EnumSet;

public enum Status
{
    Failure(Integer.MIN_VALUE), 
    Success(1073741824), 
    InProgress(536870912), 
    WrongMagic(1), 
    WrongVersion(2), 
    OutOfMemory(4), 
    InvalidParam(8), 
    BufferTooSmall(16), 
    OutOfNodes(32), 
    PartialResult(64), 
    DetailMask(16777215);
    
    private final int id;
    
    private Status(final int id) {
        this.id = id;
    }
    
    public int getValue() {
        return this.id;
    }
    
    public static EnumSet<Status> MaskDetails(final EnumSet<Status> status) {
        final EnumSet<Status> details = EnumSet.allOf(Status.class);
        details.remove(Status.Failure);
        details.remove(Status.Success);
        details.remove(Status.InProgress);
        status.retainAll(details);
        return status;
    }
}
