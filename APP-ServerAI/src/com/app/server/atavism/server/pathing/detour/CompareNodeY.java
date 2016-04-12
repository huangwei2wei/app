// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

import java.util.Comparator;

public class CompareNodeY implements Comparator<Object>
{
    @Override
    public int compare(final Object va, final Object vb) {
        final BVNode a = (BVNode)va;
        final BVNode b = (BVNode)vb;
        if (a != null && b != null) {
            if (a.BMin[1] < b.BMin[1]) {
                return -1;
            }
            if (a.BMin[1] > b.BMin[1]) {
                return 1;
            }
        }
        return 0;
    }
}
