// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

public class Config
{
    public float CellSize;
    public float CellHeight;
    public float WalkableSlopeAngle;
    public int WalkableHeight;
    public int WalkableClimb;
    public int WalkableRadius;
    public int MaxEdgeLength;
    public float MaxSimplificationError;
    public int MinRegionArea;
    public int MergeRegionArea;
    public int MaxVertexesPerPoly;
    public float DetailSampleDistance;
    public float DetailSampleMaxError;
    public RecastVertex MinBounds;
    public RecastVertex MaxBounds;
    public int Width;
    public int Height;
    public int BorderSize;
    public int TileSize;
    
    public void CalculateGridSize(final Geometry geom) {
        this.Width = (int)((geom.MaxBounds.X - geom.MinBounds.X) / this.CellSize + 0.5f);
        this.Height = (int)((geom.MaxBounds.Z - geom.MinBounds.Z) / this.CellSize + 0.5f);
        this.MaxBounds = geom.MaxBounds;
        this.MinBounds = geom.MinBounds;
    }
}
