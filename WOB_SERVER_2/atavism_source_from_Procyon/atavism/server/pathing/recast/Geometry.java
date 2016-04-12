// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Geometry
{
    public static short WalkableArea;
    public ArrayList<RecastVertex> Vertexes;
    public int NumVertexes;
    public ArrayList<Integer> Triangles;
    public int NumTriangles;
    public RecastVertex MaxBounds;
    public RecastVertex MinBounds;
    private int _walkableAreas;
    public ChunkyTriMesh ChunkyTriMesh;
    public ArrayList<Float> OffMeshConnectionVerts;
    public ArrayList<Float> OffMeshConnectionRadii;
    public ArrayList<Integer> OffMeshConnectionDirections;
    public ArrayList<Integer> OffMeshConnectionAreas;
    public ArrayList<Integer> OffMeshConnectionFlags;
    public ArrayList<Long> OffMeshConnectionIds;
    public long OffMeshConnectionCount;
    
    public int WalkableAreas() {
        return this._walkableAreas;
    }
    
    public Geometry() {
        this.Vertexes = new ArrayList<RecastVertex>();
        this.Triangles = new ArrayList<Integer>();
        this.OffMeshConnectionVerts = new ArrayList<Float>();
        this.OffMeshConnectionRadii = new ArrayList<Float>();
        this.OffMeshConnectionDirections = new ArrayList<Integer>();
        this.OffMeshConnectionAreas = new ArrayList<Integer>();
        this.OffMeshConnectionFlags = new ArrayList<Integer>();
        this.OffMeshConnectionIds = new ArrayList<Long>();
    }
    
    public Geometry(final String filename) {
        this.Vertexes = new ArrayList<RecastVertex>();
        this.Triangles = new ArrayList<Integer>();
        try {
            final BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.charAt(0) == '#') {
                    continue;
                }
                if (line.charAt(0) != 'v' || line.charAt(1) == 'n' || line.charAt(1) != 't') {}
                if (line.charAt(0) == 'f') {
                    continue;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.CalculateBounds();
    }
    
    public void CalculateBounds() {
        if (this.Vertexes.size() != 0) {
            this.MinBounds = this.Vertexes.get(0);
            this.MaxBounds = this.Vertexes.get(0);
            for (final RecastVertex recastVertex : this.Vertexes) {
                this.MinBounds = RecastVertex.Min(this.MinBounds, recastVertex);
                this.MaxBounds = RecastVertex.Max(this.MaxBounds, recastVertex);
            }
        }
    }
    
    public void MarkWalkableTriangles(final float walkableSlopeAngle, final short[] areas) {
        this.MarkWalkableTriangles(walkableSlopeAngle, this.Triangles, this.NumTriangles, areas);
    }
    
    public void MarkWalkableTriangles(final float walkableSlopeAngle, final List<Integer> triangles, final int numTriangles, final short[] areas) {
        final float walkableThr = (float)Math.cos(walkableSlopeAngle / 180.0f * 3.141592653589793);
        final float[] norm = new float[3];
        this._walkableAreas = 0;
        for (int i = 0; i < numTriangles; ++i) {
            final int tri = i * 3;
            this.CalcTriNormal(this.Vertexes.get(triangles.get(tri + 0)), this.Vertexes.get(triangles.get(tri + 1)), this.Vertexes.get(triangles.get(tri + 2)), norm);
            if (norm[1] - walkableThr > 0.0f) {
                areas[i] = Geometry.WalkableArea;
            }
        }
    }
    
    private void CalcTriNormal(final RecastVertex v0, final RecastVertex v1, final RecastVertex v2, float[] norm) {
        final RecastVertex e0 = RecastVertex.Sub(v1, v0);
        final RecastVertex e2 = RecastVertex.Sub(v2, v0);
        final RecastVertex n = RecastVertex.Cross(e0, e2);
        n.Normalize();
        norm = n.ToArray();
    }
    
    public void CreateChunkyTriMesh() {
        this.ChunkyTriMesh = new ChunkyTriMesh((RecastVertex[])this.Vertexes.toArray(), (Integer[])this.Triangles.toArray(), this.NumTriangles, 256);
    }
    
    public void AddOffMeshConnection(final RecastVertex start, final RecastVertex end, final float radius, final Boolean biDirectional, final short area, final int flags) {
        this.OffMeshConnectionVerts.add(start.X);
        this.OffMeshConnectionVerts.add(start.Y);
        this.OffMeshConnectionVerts.add(start.Z);
        this.OffMeshConnectionVerts.add(end.X);
        this.OffMeshConnectionVerts.add(end.Y);
        this.OffMeshConnectionVerts.add(end.Z);
        this.OffMeshConnectionRadii.add(radius);
        this.OffMeshConnectionDirections.add(((boolean)biDirectional) ? 1 : 0);
        this.OffMeshConnectionAreas.add((int)area);
        this.OffMeshConnectionFlags.add(flags);
        this.OffMeshConnectionIds.add(1000L + this.OffMeshConnectionCount++);
    }
    
    static {
        Geometry.WalkableArea = 63;
    }
}
