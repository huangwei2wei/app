// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.recast;

public class CompactSpan
{
    public int Y;
    public int Reg;
    public long Con;
    public int H;
    
    public void SetCon(final int dir, final int i) {
        final int shift = dir * 6;
        final long con = this.Con;
        this.Con = ((con & ~(63 << shift)) | (i & 0x3F) << shift);
    }
    
    public int GetCon(final int dir) {
        final int shift = dir * 6;
        return (int)(this.Con >> shift & 0x3FL);
    }
}
