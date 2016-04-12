// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

public class NavMeshCreateParams
{
    public int[] Verts;
    public int VertCount;
    public int[] Polys;
    public int[] PolyFlags;
    public short[] PolyAreas;
    public int PolyCount;
    public int Nvp;
    public long[] DetailMeshes;
    public float[] DetailVerts;
    public int DetailVertsCount;
    public short[] DetailTris;
    public int DetailTriCount;
    public float[] OffMeshConVerts;
    public float[] OffMeshConRad;
    public int[] OffMeshConFlags;
    public int[] OffMeshConAreas;
    public int[] OffMeshConDir;
    public long[] OffMeshConUserId;
    public int OffMeshConCount;
    public long UserId;
    public int TileX;
    public int TileY;
    public int TileLayer;
    public float[] BMin;
    public float[] BMax;
    public float WalkableHeight;
    public float WalkableRadius;
    public float WalkableClimb;
    public float Cs;
    public float Ch;
    public Boolean BuildBvTree;
}
